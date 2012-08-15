package com.jasonzqshen.familyAccounting;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import com.jasonzqshen.familyAccounting.data.DataCore;
import com.jasonzqshen.familyAccounting.exceptions.CoreDriverInitException;
import com.jasonzqshen.familyAccounting.exceptions.ExternalStorageException;
import com.jasonzqshen.familyAccounting.utils.ChartUtil;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.transaction.GLAccountBalanceCollection;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;
import com.jasonzqshen.familyaccounting.core.utils.GLAccountGroup;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private DataCore _dataCore = null;
	private DefaultRenderer _renderer = new DefaultRenderer();
	private GraphicalView _chartView;
	private CategorySeries _series = new CategorySeries("");

	private TextView _equityValue = null;
	private TextView _assetValue = null;
	private TextView _liabilitiesValue = null;
	private TextView _costValue = null;
	private TextView _revenueValue = null;
	private TextView _liquaityValue = null;
	private TextView _monthId = null;
	private TextView _task = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

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
		_renderer.setApplyBackgroundColor(true);
		_renderer.setBackgroundColor(Color.argb(100, 50, 50, 50));
		_renderer.setChartTitleTextSize(20);
		_renderer.setLabelsTextSize(15);
		_renderer.setLegendTextSize(15);
		_renderer.setMargins(new int[] { 20, 30, 15, 0 });
		_renderer.setZoomButtonsVisible(true);
		_renderer.setStartAngle(90);

		// get text view
		_equityValue = (TextView) this.findViewById(R.id.equityValue);
		_assetValue = (TextView) this.findViewById(R.id.assertValue);
		_liabilitiesValue = (TextView) this.findViewById(R.id.liabilitytValue);
		_costValue = (TextView) this.findViewById(R.id.costValue);
		_revenueValue = (TextView) this.findViewById(R.id.revenueValue);
		_liquaityValue = (TextView) this.findViewById(R.id.liquadityValue);
		_monthId = (TextView) this.findViewById(R.id.monthLabel);
		_task = (TextView) this.findViewById(R.id.taskLabel);
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
		if (_chartView == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
			_chartView = ChartFactory.getPieChartView(this, _series, _renderer);
			_renderer.setClickEnabled(true);
			_renderer.setSelectableBuffer(10);
			_chartView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					SeriesSelection seriesSelection = _chartView
							.getCurrentSeriesAndPoint();
					if (seriesSelection == null) {
						Toast.makeText(MainActivity.this,
								"No chart element was clicked",
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(
								MainActivity.this,
								"Chart element data point index "
										+ seriesSelection.getPointIndex()
										+ " was clicked" + " point value="
										+ seriesSelection.getValue(),
								Toast.LENGTH_SHORT).show();
					}
				}
			});
			_chartView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					SeriesSelection seriesSelection = _chartView
							.getCurrentSeriesAndPoint();
					if (seriesSelection == null) {
						Toast.makeText(MainActivity.this,
								"No chart element was long pressed",
								Toast.LENGTH_SHORT).show();
						return false; // no chart element was long pressed, so
										// let something
						// else handle the event
					} else {
						Toast.makeText(
								MainActivity.this,
								"Chart element data point index "
										+ seriesSelection.getPointIndex()
										+ " was long pressed",
								Toast.LENGTH_SHORT).show();
						return true; // the element was long pressed - the event
										// has been
						// handled
					}
				}
			});
			layout.addView(_chartView, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		} else {
			_chartView.repaint();
		}
	}

	/**
	 * set data
	 */
	private void setData() {
		// set the data in pie chart
		_series.clear();
		CoreDriver coreDriver = _dataCore.getCoreDriver();
		GLAccountBalanceCollection balCol = coreDriver.getTransDataManagement()
				.getAccBalCol();
		CurrencyAmount assetAmount = new CurrencyAmount();
		int index = 0;
		for (GLAccountGroup group : GLAccountGroup.BALANCE_GROUP) {
			CurrencyAmount cur = balCol.getGroupBalance(group);

			_series.add(GLAccountGroup.getDescp(group), cur.toNumber());
			SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
			renderer.setColor(ChartUtil.COLORS[index % ChartUtil.COLORS.length]);
			_renderer.addSeriesRenderer(renderer);

			if (_chartView != null) {
				_chartView.repaint();
			}

			assetAmount.addTo(cur);
			index++;
		}
		// set data for field
		this._assetValue.setText(assetAmount.toString());

		// equity
		CurrencyAmount equityAmount = balCol
				.getGroupBalance(GLAccountGroup.EQUITY);
		equityAmount.negate();
		this._equityValue.setText(equityAmount.toString());

		// liabilities
		CurrencyAmount liabilityAmount = new CurrencyAmount();
		for (GLAccountGroup group : GLAccountGroup.LIABILITIES_GROUP) {
			CurrencyAmount cur = balCol.getGroupBalance(group);
			liabilityAmount.addTo(cur);
		}
		liabilityAmount.negate();
		this._liabilitiesValue.setText(liabilityAmount.toString());

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

		// liquidity
		CurrencyAmount liquidityAmount = new CurrencyAmount();
		for (GLAccountGroup group : GLAccountGroup.Liquidity_GROUP) {
			CurrencyAmount cur = balCol.getGroupBalance(group);
			liquidityAmount.addTo(cur);
		}
		this._liquaityValue.setText(liquidityAmount.toString());

		// set month identity
		_monthId.setText(coreDriver.getTransDataManagement().getCurrentLedger()
				.getMonthID().toString());

		_task.setText("task(0)");
	}
}
