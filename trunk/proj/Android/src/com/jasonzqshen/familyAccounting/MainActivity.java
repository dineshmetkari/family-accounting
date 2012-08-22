package com.jasonzqshen.familyAccounting;

import java.util.ArrayList;
import java.util.Collections;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import com.jasonzqshen.familyAccounting.data.DataCore;
import com.jasonzqshen.familyAccounting.entries.VendorEntryActivity;
import com.jasonzqshen.familyAccounting.exceptions.CoreDriverInitException;
import com.jasonzqshen.familyAccounting.exceptions.ExternalStorageException;
import com.jasonzqshen.familyAccounting.utils.ActivityAction;
import com.jasonzqshen.familyAccounting.utils.ChartUtil;
import com.jasonzqshen.familyAccounting.widgets.AccountReportAdapter;
import com.jasonzqshen.familyAccounting.widgets.AccountReportAdapterItem;
import com.jasonzqshen.familyAccounting.widgets.MenuAdapter;
import com.jasonzqshen.familyAccounting.widgets.MenuAdapterItem;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.masterdata.GLAccountMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;
import com.jasonzqshen.familyaccounting.core.transaction.GLAccountBalanceCollection;
import com.jasonzqshen.familyaccounting.core.transaction.GLAccountBalanceItem;
import com.jasonzqshen.familyaccounting.core.transaction.MonthIdentity;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;
import com.jasonzqshen.familyaccounting.core.utils.GLAccountGroup;

import android.os.Bundle;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity {
	public static final String TAG = "MAIN";

	private MainActivityHandler _handler = new MainActivityHandler(this);
	private final OnCheckedChangeListener _dimenChanged = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {

		}
	};

	/**
	 * menu button click
	 */
	private final OnClickListener MENU_BTN_CLICK = new OnClickListener() {
		@Override
		public void onClick(View v) {
			showDialog(R.id.dialog_entries);
		}
	};

	/**
	 * menu button click
	 */
	private final OnClickListener REPORT_BTN_CLICK = new OnClickListener() {
		@Override
		public void onClick(View v) {
			_handler.navigate2BalanceReport();
		}
	};

	private final DialogInterface.OnClickListener ENTRY_CLICK = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			MenuAdapterItem menuItem = (MenuAdapterItem) _entryListAdapter
					.getItem(which);
			Log.d(TAG, "menu dialog click on " + which);

			if (menuItem.ItemType == MenuAdapterItem.HEAD_TYPE) {
				// showDialog(R.id.dialog_entries);
			} else {
				if (menuItem.Action != null) {
					menuItem.Action.execute();
				}
			}
		}

	};

	private AccountReportAdapter _costAccountAdapter;
	private MenuAdapter _entryListAdapter;

	private final DataCore _dataCore;

	/**
	 * constructor, initialize the data
	 */
	public MainActivity() {
		// initialize data component
		_dataCore = DataCore.getInstance();

		try {
			_dataCore.initialize();
		} catch (ExternalStorageException e) {
			Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
		} catch (CoreDriverInitException e) {
			Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * chart
	 */
	private DefaultRenderer _renderer = new DefaultRenderer();
	private CategorySeries _series = new CategorySeries("");
	private GraphicalView _chartView;

	/**
	 * UI controller
	 */
	private TextView _costValue = null;
	private TextView _revenueValue = null;
	private RadioGroup _radioGroup = null;
	private ImageButton _newButton = null;
	private ImageButton _reportButton = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// initialize render
		buildPieChartRenderer();

		// get text view
		_costValue = (TextView) this.findViewById(R.id.cost_value);
		_revenueValue = (TextView) this.findViewById(R.id.revenue_value);
		_radioGroup = (RadioGroup) this.findViewById(R.id.dimenSelection);
		_radioGroup.setOnCheckedChangeListener(_dimenChanged);

		_newButton = (ImageButton) this.findViewById(R.id.new_icon);
		_newButton.setOnClickListener(MENU_BTN_CLICK);

		_reportButton = (ImageButton) this.findViewById(R.id.report_icon);
		_reportButton.setOnClickListener(REPORT_BTN_CLICK);

		contructEntryAdatper();
	}

	/**
	 * Builds a bar render
	 * 
	 */
	private void buildPieChartRenderer() {
		// initialize render
		_renderer.setApplyBackgroundColor(false);
		// _renderer.setBackgroundColor(Color.argb(100, 50, 50, 50));
		_renderer.setChartTitleTextSize(ChartUtil.PIE_CHART_TITLE_SIZE);
		_renderer.setLabelsTextSize(ChartUtil.PIE_CHART_LABEL_SIZE);
		_renderer.setLegendTextSize(ChartUtil.PIE_CHART_LABEL_SIZE);
		_renderer.setLabelsColor(Color.BLACK);
		_renderer.setMargins(new int[] { 20, 30, 15, 0 });
		_renderer.setZoomEnabled(false);
		_renderer.setPanEnabled(false);
		_renderer.setStartAngle(90);
		_renderer.setChartTitle(this.getString(R.string.main_chart_title));
	}

	protected void onResume() {
		super.onResume();

		drawChart();
		setData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_main_menu:
			_handler.navigate2MainMenu();
			break;
		}

		return true;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case R.id.dialog_entries:

			return new AlertDialog.Builder(this)
					.setTitle(R.string.main_entries)
					.setAdapter(_entryListAdapter, ENTRY_CLICK).create();
		}

		return null;
	}

	/**
	 * draw chart
	 */
	private void drawChart() {
		if (_chartView != null) {
			_chartView.repaint();
			return;
		}

		_chartView = ChartFactory.getPieChartView(this, _series, _renderer);

		LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
		layout.addView(_chartView, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
	}

	/**
	 * set data
	 */
	private void setData() {
		// set the data in bar chart
		CoreDriver coreDriver = _dataCore.getCoreDriver();
		if (coreDriver.isInitialized() == false) {
			return;
		}
		// clear
		this._series.clear();

		MasterDataManagement mdMgmt = coreDriver.getMasterDataManagement();
		GLAccountBalanceCollection balCol = coreDriver.getTransDataManagement()
				.getAccBalCol();

		// for cost detail
		ArrayList<AccountReportAdapterItem> list = new ArrayList<AccountReportAdapterItem>();
		// generate data
		int count = 0;
		for (int i = 0; i < GLAccountGroup.COST_GROUP.length; i++) {
			GLAccountGroup group = GLAccountGroup.COST_GROUP[i];
			MasterDataIdentity_GLAccount[] ids = mdMgmt
					.getGLAccountsBasedGroup(group);
			for (int j = 0; j < ids.length; ++j) {
				GLAccountMasterData masterData = (GLAccountMasterData) mdMgmt
						.getMasterData(ids[j], MasterDataType.GL_ACCOUNT);
				GLAccountBalanceItem item = balCol.getBalanceItem(ids[j]);

				_series.add(masterData.getDescp(), item.getSumAmount()
						.toNumber());
				SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
				renderer.setColor(ChartUtil.COLORS[count
						% ChartUtil.COLORS.length]);
				renderer.setChartValuesTextSize(ChartUtil.PIE_CHART_VALUE_TEXT_SIZE);
				renderer.setChartValuesSpacing(ChartUtil.PIE_CHART_VALUE_SPACING);
				renderer.setDisplayChartValues(true);

				_renderer.addSeriesRenderer(renderer);

				list.add(new AccountReportAdapterItem(masterData.getDescp(),
						item.getSumAmount(),
						AccountReportAdapterItem.ITEM_VIEW, masterData));
				count++;
			}

		}
		if (_chartView != null) {
			_chartView.repaint();
		}
		// revenue
		CurrencyAmount revenueAmount = new CurrencyAmount();
		for (GLAccountGroup group : GLAccountGroup.REVENUE_GROUP) {
			CurrencyAmount cur = balCol.getGroupBalance(group);
			revenueAmount.addTo(cur);
		}
		this._revenueValue.setText(revenueAmount.toString());

		// cost
		CurrencyAmount costAmount = new CurrencyAmount();
		for (GLAccountGroup group : GLAccountGroup.COST_GROUP) {
			CurrencyAmount cur = balCol.getGroupBalance(group);
			costAmount.addTo(cur);
		}
		this._costValue.setText(costAmount.toString());

		// set adapter
		Collections.sort(list);
		_costAccountAdapter = new AccountReportAdapter(this, list);
		this.setListAdapter(_costAccountAdapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// click on cost account list
		// negative to show the details of cost
		AccountReportAdapterItem item = (AccountReportAdapterItem) _costAccountAdapter
				.getItem(position);
		CoreDriver coreDriver = _dataCore.getCoreDriver();
		if (coreDriver.isInitialized() == false) {
			return;
		}

		MonthIdentity monthId = coreDriver.getCurMonthId();
		_handler.navigate2CostDetails(monthId, item.Account.getGLIdentity());
	}

	/**
	 * construct entry adapter
	 */
	private void contructEntryAdatper() {
		// set entries list
		ArrayList<MenuAdapterItem> list = new ArrayList<MenuAdapterItem>();

		list.add(new MenuAdapterItem(MenuAdapter.HEAD_TYPE, 0,
				R.string.menu_customizing_entry, null));
		list.add(new MenuAdapterItem(MenuAdapter.ITEM_TYPE,
				R.drawable.settings, R.string.menu_customized_entry, null));
		list.add(new MenuAdapterItem(MenuAdapter.HEAD_TYPE, 0,
				R.string.menu_entry, null));
		list.add(new MenuAdapterItem(MenuAdapter.ITEM_TYPE,
				R.drawable.new_entry, R.string.menu_vendor_entry,
				new ActivityAction(VendorEntryActivity.class, this)));
		list.add(new MenuAdapterItem(MenuAdapter.ITEM_TYPE,
				R.drawable.new_entry, R.string.menu_customer_entry, null));
		list.add(new MenuAdapterItem(MenuAdapter.ITEM_TYPE,
				R.drawable.new_entry, R.string.menu_gl_entry, null));

		_entryListAdapter = new MenuAdapter(this, list);
	}
}
