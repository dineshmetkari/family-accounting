package com.jasonzqshen.familyAccounting.reports;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer.Orientation;

import com.jasonzqshen.familyAccounting.R;
import com.jasonzqshen.familyAccounting.data.DataCore;
import com.jasonzqshen.familyAccounting.utils.ChartUtil;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.transaction.GLAccountBalanceCollection;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;
import com.jasonzqshen.familyaccounting.core.utils.GLAccountGroup;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AssetReportsActivity extends Activity {
	public static final String TAG = "AssetReport";

	private DataCore _dataCore = null;

	private CheckBox _cashCheckBox = null;
	private CheckBox _fixedAsstCheckBox = null;
	private CheckBox _bankAccountCheckBox = null;
	private CheckBox _investCheckBox = null;
	private CheckBox _prepaidCheckBox = null;

	private TextView _sumField = null;

	private XYMultipleSeriesRenderer _renderer = new XYMultipleSeriesRenderer();
	private GraphicalView _chartView = null;
	private XYMultipleSeriesDataset _dataset = new XYMultipleSeriesDataset();

	private final OnCheckedChangeListener _checkedChangedListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// log
			Log.i(TAG, "checked change listener.");

			setData();
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.asset_report);

		_dataCore = DataCore.getInstance();

		// set check box
		_cashCheckBox = (CheckBox) this.findViewById(R.id.cashCheckBox);
		_cashCheckBox.setOnCheckedChangeListener(_checkedChangedListener);
		_fixedAsstCheckBox = (CheckBox) this
				.findViewById(R.id.fixedAssetCheckBox);
		_fixedAsstCheckBox.setOnCheckedChangeListener(_checkedChangedListener);
		_bankAccountCheckBox = (CheckBox) this
				.findViewById(R.id.bankAccountCheckBox);
		_bankAccountCheckBox
				.setOnCheckedChangeListener(_checkedChangedListener);
		_investCheckBox = (CheckBox) this.findViewById(R.id.investCheckBox);
		_investCheckBox.setOnCheckedChangeListener(_checkedChangedListener);
		_prepaidCheckBox = (CheckBox) this.findViewById(R.id.prepaidCheckBox);
		_prepaidCheckBox.setOnCheckedChangeListener(_checkedChangedListener);

		_sumField = (TextView) this.findViewById(R.id.sumValue);

		// initialize render
		buildBarRenderer();
	}

	protected void onResume() {
		super.onResume();

		drawChart();
		setData();
	}

	/**
	 * Builds a bar multiple series renderer to use the provided colors.
	 * 
	 */
	private void buildBarRenderer() {
		_renderer.setAxisTitleTextSize(16);
		_renderer.setChartTitleTextSize(20);
		_renderer.setLabelsTextSize(15);
		_renderer.setLegendTextSize(15);

		_renderer.setOrientation(Orientation.HORIZONTAL);

		_renderer.setChartTitle(this.getString(R.string.asset_report_title));
		_renderer.setXTitle(this.getString(R.string.asset_report_xtitle));
		_renderer.setYTitle(this.getString(R.string.asset_report_ytitle));
		_renderer.setAxesColor(Color.GRAY);
		_renderer.setLabelsColor(Color.LTGRAY);
		_renderer.setXLabelsColor(Color.LTGRAY);

		_renderer.setZoomEnabled(false);
		_renderer.setPanEnabled(false);
		_renderer.setBarSpacing(0.2);
	}

	/**
	 * set chart data
	 */
	private void drawChart() {

		if (_chartView != null) {
			_chartView.repaint();
			return;
		}

		_renderer.setXAxisMin(0);
		_renderer.setXAxisMax(5);
		_renderer.setYAxisMin(0);
		_renderer.setYAxisMax(500);

		// create bar chart view
		_chartView = ChartFactory.getBarChartView(this, _dataset, _renderer,
				Type.DEFAULT);

		LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
		layout.addView(_chartView, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
	}

	/**
	 * set data
	 */
	private void setData() {
		CoreDriver coreDriver = this._dataCore.getCoreDriver();
		if (!coreDriver.isInitialized()) {
			return;
		}
		// clear
		int count = _dataset.getSeriesCount();
		for (int i = count - 1; i >= 0; --i) {
			_dataset.removeSeries(i);
		}
		// clear
		_renderer.setXLabels(0);
		GLAccountBalanceCollection balCol = coreDriver.getTransDataManagement()
				.getAccBalCol();
		CurrencyAmount maxAmount = new CurrencyAmount();
		CurrencyAmount sumAmount = new CurrencyAmount();
		count = 0;
		// set data
		XYSeries series = new XYSeries(
				this.getString(R.string.asset_report_ytitle));
		for (int i = 0; i < GLAccountGroup.BALANCE_GROUP.length; i++) {
			GLAccountGroup group = GLAccountGroup.BALANCE_GROUP[i];
			if (!isVisible(group)) {
				continue;
			}
			count++;
			// set color
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(ChartUtil.COLORS[i % ChartUtil.COLORS.length]);
			_renderer.addSeriesRenderer(r);

			CurrencyAmount amount = balCol.getGroupBalance(group);
			series.add(count, amount.toNumber());
			_renderer.addXTextLabel(count, GLAccountGroup.getDescp(group));
			count++;

			// calculate value range
			if (amount.compareTo(maxAmount) > 0) {
				maxAmount = amount;
			}
			sumAmount.addTo(amount);
		}

		// set data set
		_dataset.addSeries(series);

		// set range

		_renderer.setXAxisMin(0);
		_renderer.setXAxisMax(count);
		_renderer.setYAxisMin(0);
		_renderer.setYAxisMax(maxAmount.toNumber() * ChartUtil.MAX_BASE);

		if (_chartView != null) {
			_chartView.repaint();
		}

		// set sum
		_sumField.setText(sumAmount.toString());
	}

	/**
	 * check whether the account group is checked.
	 * 
	 * @param group
	 * @return
	 */
	private boolean isVisible(GLAccountGroup group) {
		if (group == GLAccountGroup.ASSETS) {
			return _fixedAsstCheckBox.isChecked();
		} else if (group == GLAccountGroup.BANK_ACCOUNT) {
			return _bankAccountCheckBox.isChecked();
		} else if (group == GLAccountGroup.PREPAID) {
			return _prepaidCheckBox.isChecked();
		} else if (group == GLAccountGroup.CASH) {
			return _cashCheckBox.isChecked();
		} else if (group == GLAccountGroup.INVESTMENT) {
			return _investCheckBox.isChecked();
		}

		return false;
	}

}
