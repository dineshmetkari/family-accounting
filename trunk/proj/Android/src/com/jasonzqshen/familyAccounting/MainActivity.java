package com.jasonzqshen.familyAccounting;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import com.jasonzqshen.familyAccounting.data.DataCore;
import com.jasonzqshen.familyAccounting.exceptions.CoreDriverInitException;
import com.jasonzqshen.familyAccounting.exceptions.ExternalStorageException;
import com.jasonzqshen.familyAccounting.utils.ChartUtil;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataBase;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;
import com.jasonzqshen.familyaccounting.core.transaction.GLAccountBalanceCollection;
import com.jasonzqshen.familyaccounting.core.transaction.GLAccountBalanceItem;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;
import com.jasonzqshen.familyaccounting.core.utils.GLAccountGroup;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	public static final String TAG = "MAIN";

	private MainActivityHandler _handler = new MainActivityHandler(this);
	private final OnCheckedChangeListener _dimenChanged = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {

		}
	};

	private final OnClickListener MENU_BTN_CLICK = new OnClickListener() {
		@Override
		public void onClick(View v) {
			_handler.navigate2MainMenu();
		}
	};

	private DataCore _dataCore = null;
	private DefaultRenderer _renderer = new DefaultRenderer();
	private CategorySeries _series = new CategorySeries("");
	private GraphicalView _chartView;

	private TextView _costValue = null;
	private TextView _revenueValue = null;
	private RadioGroup _radioGroup = null;
	private ImageButton _menuButton = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// initialize data component
		_dataCore = DataCore.getInstance();
		try {
			_dataCore.initialize();
		} catch (ExternalStorageException e) {
			Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
		} catch (CoreDriverInitException e) {
			Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
		}

		// initialize render
		buildPieChartRenderer();

		// get text view
		_costValue = (TextView) this.findViewById(R.id.cost_value);
		_revenueValue = (TextView) this.findViewById(R.id.revenue_value);
		_radioGroup = (RadioGroup) this.findViewById(R.id.dimenSelection);
		_radioGroup.setOnCheckedChangeListener(_dimenChanged);

		_menuButton = (ImageButton) this.findViewById(R.id.menu_icon);
		_menuButton.setOnClickListener(MENU_BTN_CLICK);
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

		// set data
		int count = 0;
		for (int i = 0; i < GLAccountGroup.COST_GROUP.length; i++) {
			GLAccountGroup group = GLAccountGroup.COST_GROUP[i];
			MasterDataIdentity_GLAccount[] ids = mdMgmt
					.getGLAccountsBasedGroup(group);
			for (int j = 0; j < ids.length; ++j) {
				MasterDataBase masterData = mdMgmt.getMasterData(ids[j],
						MasterDataType.GL_ACCOUNT);
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

	}
}
