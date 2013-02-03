package com.jasonzqshen.familyAccounting.reports;

import java.util.ArrayList;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import com.jasonzqshen.familyAccounting.CostItemsDialogBuilder;
import com.jasonzqshen.familyAccounting.R;
import com.jasonzqshen.familyAccounting.data.DataCore;
import com.jasonzqshen.familyAccounting.exceptions.CoreDriverInitException;
import com.jasonzqshen.familyAccounting.exceptions.ExternalStorageException;
import com.jasonzqshen.familyAccounting.utils.ChartUtil;
import com.jasonzqshen.familyAccounting.widgets.AccountReportAdapter;
import com.jasonzqshen.familyAccounting.widgets.AccountReportAdapterItem;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.masterdata.GLAccountMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataBase;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;
import com.jasonzqshen.familyaccounting.core.reports.DocumentBusinessIndex;
import com.jasonzqshen.familyaccounting.core.reports.DocumentIndex;
import com.jasonzqshen.familyaccounting.core.reports.DocumentIndexItem;
import com.jasonzqshen.familyaccounting.core.reports.ReportsManagement;
import com.jasonzqshen.familyaccounting.core.transaction.GLAccountBalanceCollection;
import com.jasonzqshen.familyaccounting.core.transaction.GLAccountBalanceItem;
import com.jasonzqshen.familyaccounting.core.transaction.MonthIdentity;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;
import com.jasonzqshen.familyaccounting.core.utils.GLAccountGroup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * abstract cost details
 * 
 * @author jasonzqshen@gmail.com
 * 
 */
public abstract class AbstractCostDetailsActivity extends Activity {
	protected DataCore _dataCore = DataCore.getInstance();

	/**
	 * cost adapter
	 */
	private AccountReportAdapter _costAdapter;

	/**
	 * chart
	 */
	private DefaultRenderer _renderer = new DefaultRenderer();

	private CategorySeries _series = new CategorySeries("");

	private GraphicalView _chartView;

	/**
	 * UI controller
	 */
	protected TextView _costValue = null;

	protected LinearLayout _costRow = null;

	protected Spinner _groupSpinner = null;

	/**
	 * cost row click
	 */
	private final OnClickListener COST_ROW_CLICK = new OnClickListener() {
		@Override
		public void onClick(View v) {
			DocumentsListNavigation
					.navigate2CostDetails(AbstractCostDetailsActivity.this);
		}
	};

	/**
	 * group spinner click
	 */
	private final AdapterView.OnItemSelectedListener GROUP_SPINNER_CLICK = new AdapterView.OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> adapter, View view,
				int position, long id) {
			setData();
		}

		@Override
		public void onNothingSelected(AdapterView<?> adapter) {
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getContentView());

		try {
			_dataCore.initialize(this);
		} catch (ExternalStorageException e) {
			Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
		} catch (CoreDriverInitException e) {
			Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
		}
		// initialize render
		buildPieChartRenderer(_renderer,
				this.getString(R.string.main_chart_title));

		// get text view
		_costValue = (TextView) this.findViewById(R.id.cost_value);
		_costRow = (LinearLayout) this.findViewById(R.id.cost_row);
		_costRow.setOnClickListener(COST_ROW_CLICK);

		// group spinner
		_groupSpinner = (Spinner) this.findViewById(R.id.group_spinner);
		_groupSpinner.setOnItemSelectedListener(GROUP_SPINNER_CLICK);

	}

	@Override
	protected void onResume() {
		super.onResume();

		drawChart();
		setData();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case R.id.dialog_cost_details:
			return CostItemsDialogBuilder.buildCostItemsDialog(this,
					_costAdapter);
		}

		return super.onCreateDialog(id);
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case R.id.dialog_cost_details:
			AlertDialog detailsDialog = (AlertDialog) dialog;
			CostItemsDialogBuilder.setCostDetailsDialog(detailsDialog,
					_costAdapter);
			return;
		}

		super.onPrepareDialog(id, dialog);
	}

	/**
	 * Builds a bar render
	 * 
	 */
	public static void buildPieChartRenderer(DefaultRenderer renderer,
			String title) {
		// initialize render
		renderer.setApplyBackgroundColor(false);
		// _renderer.setBackgroundColor(Color.argb(100, 50, 50, 50));
		renderer.setChartTitleTextSize(ChartUtil.PIE_CHART_TITLE_SIZE);
		renderer.setLabelsTextSize(ChartUtil.PIE_CHART_LABEL_SIZE);
		renderer.setLegendTextSize(ChartUtil.PIE_CHART_LABEL_SIZE);
		renderer.setLabelsColor(Color.BLACK);
		renderer.setMargins(new int[] { 20, 30, 15, 0 });
		renderer.setZoomEnabled(false);
		renderer.setPanEnabled(false);
		renderer.setStartAngle(90);
		renderer.setChartTitle(title);
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
		_chartView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(R.id.dialog_cost_details);
			}
		});

		LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
		layout.addView(_chartView, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
	}

	/**
	 * set data
	 */
	protected void setData() {
		// set the data in bar chart
		CoreDriver coreDriver = _dataCore.getCoreDriver();
		if (coreDriver.isInitialized() == false) {
			return;
		}
		// clear
		this._series.clear();
		SimpleSeriesRenderer[] oldRenderers = _renderer.getSeriesRenderers();
		for (SimpleSeriesRenderer r : oldRenderers) {
			_renderer.removeSeriesRenderer(r);
		}

		GLAccountBalanceCollection balCol = coreDriver.getTransDataManagement()
				.getAccBalCol();

		// for cost detail
		ArrayList<AccountReportAdapterItem> list = null;
		// generate data

		int selected = _groupSpinner.getSelectedItemPosition();
		if (selected == 0) {
			list = getGroupByAccount(coreDriver, getMonthIdentity());
		} else {
			list = getGroupByBusinessArea(coreDriver, getMonthIdentity());
		}
		_costAdapter = new AccountReportAdapter(this, list);

		// generate pie graphic
		int colorIndex = 0; // index just for calculate the color
		for (AccountReportAdapterItem item : list) {
			// get color
			int colorID = ChartUtil.COLORS[colorIndex % ChartUtil.COLORS.length];
			colorIndex++;
			if (item.Amount.isZero()) {
				continue;
			}

			_series.add(item.Descp, item.Amount.toNumber());
			SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();

			renderer.setColor(this.getResources().getColor(colorID));
			renderer.setChartValuesTextSize(ChartUtil.PIE_CHART_VALUE_TEXT_SIZE);
			renderer.setChartValuesSpacing(ChartUtil.PIE_CHART_VALUE_SPACING);
			renderer.setDisplayChartValues(true);

			_renderer.addSeriesRenderer(renderer);

		}

		if (_chartView != null) {
			_chartView.repaint();
		}

		// cost value in header
		CurrencyAmount costAmount = new CurrencyAmount();
		for (GLAccountGroup group : GLAccountGroup.COST_GROUP) {
			CurrencyAmount cur = balCol.getGroupBalance(group,
					getMonthIdentity(), getMonthIdentity());
			costAmount.addTo(cur);
		}
		this._costValue.setText(costAmount.toString());
	}

	/**
	 * get data group by business area
	 * 
	 * @param coreDriver
	 * @return
	 */
	private static ArrayList<AccountReportAdapterItem> getGroupByBusinessArea(
			CoreDriver coreDriver, MonthIdentity monthId) {
		ArrayList<AccountReportAdapterItem> retList = new ArrayList<AccountReportAdapterItem>();
		DataCore dataCore = DataCore.getInstance();
		ReportsManagement reportsMgmt = dataCore.getReportsManagement();
		DocumentBusinessIndex index = (DocumentBusinessIndex) reportsMgmt
				.getDocumentIndex(DocumentIndex.BUSINESS_INDEX);
		MasterDataManagement mdMgmt = coreDriver.getMasterDataManagement();

		ArrayList<MasterDataIdentity> businessIds = index.getKeys();
		for (MasterDataIdentity id : businessIds) {
			MasterDataBase masterData = mdMgmt.getMasterData(id,
					MasterDataType.BUSINESS_AREA);
			DocumentIndexItem item = index.getIndexItem(id);

			CurrencyAmount amount = item.getAmount(monthId);

			retList.add(new AccountReportAdapterItem(masterData.getDescp(),
					amount, AccountReportAdapterItem.ITEM_VIEW, masterData));
		}

		return retList;
	}

	/**
	 * get data group by account
	 * 
	 * @param coreDriver
	 * @return
	 */
	private static ArrayList<AccountReportAdapterItem> getGroupByAccount(
			CoreDriver coreDriver, MonthIdentity monthId) {
		GLAccountBalanceCollection balCol = coreDriver.getTransDataManagement()
				.getAccBalCol();
		MasterDataManagement mdMgmt = coreDriver.getMasterDataManagement();
		ArrayList<AccountReportAdapterItem> retList = new ArrayList<AccountReportAdapterItem>();

		// for each cost group
		for (int i = 0; i < GLAccountGroup.COST_GROUP.length; i++) {
			GLAccountGroup group = GLAccountGroup.COST_GROUP[i];
			MasterDataIdentity_GLAccount[] ids = mdMgmt
					.getGLAccountsBasedGroup(group);
			// for g/l account in each group
			for (int j = 0; j < ids.length; ++j) {
				// get master data
				GLAccountMasterData masterData = (GLAccountMasterData) mdMgmt
						.getMasterData(ids[j], MasterDataType.GL_ACCOUNT);
				// get balance item

				GLAccountBalanceItem item = balCol.getBalanceItem(ids[j]);
				retList.add(new AccountReportAdapterItem(masterData.getDescp(),
						item.getAmount(monthId),
						AccountReportAdapterItem.ITEM_VIEW, masterData));
			}
		}

		return retList;
	}

	protected abstract int getContentView();

	protected abstract MonthIdentity getMonthIdentity();
}
