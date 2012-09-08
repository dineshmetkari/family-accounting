package com.jasonzqshen.familyAccounting;

import java.util.ArrayList;
import java.util.Collections;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import com.jasonzqshen.familyAccounting.data.DataCore;
import com.jasonzqshen.familyAccounting.entries.CheckBalanceActivity;
import com.jasonzqshen.familyAccounting.exceptions.CoreDriverInitException;
import com.jasonzqshen.familyAccounting.exceptions.ExternalStorageException;
import com.jasonzqshen.familyAccounting.reports.DocumentsListNavigation;
import com.jasonzqshen.familyAccounting.utils.ActivityAction;
import com.jasonzqshen.familyAccounting.utils.ChartUtil;
import com.jasonzqshen.familyAccounting.widgets.AccountReportAdapter;
import com.jasonzqshen.familyAccounting.widgets.AccountReportAdapterItem;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.masterdata.GLAccountMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;
import com.jasonzqshen.familyaccounting.core.transaction.GLAccountBalanceCollection;
import com.jasonzqshen.familyaccounting.core.transaction.GLAccountBalanceItem;
import com.jasonzqshen.familyaccounting.core.transaction.MonthIdentity;
import com.jasonzqshen.familyaccounting.core.transaction.TransactionDataManagement;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;
import com.jasonzqshen.familyaccounting.core.utils.GLAccountGroup;

import android.os.Bundle;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity {
    public static final String TAG = "MAIN";

    private MainActivityHandler _handler = new MainActivityHandler(this);

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
     * cost row click
     */
    private final OnClickListener COST_ROW_CLICK = new OnClickListener() {
        @Override
        public void onClick(View v) {
            DocumentsListNavigation.navigate2CostDetails(MainActivity.this);
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

    private AccountReportAdapter _costAccountAdapter;

    private final DataCore _dataCore;

    /**
     * constructor, initialize the data
     */
    public MainActivity() {
        // initialize data component
        _dataCore = DataCore.getInstance();

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

    private TableRow _costRow = null;

    private TextView _revenueValue = null;

    private ImageButton _newButton = null;

    private ImageButton _reportButton = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

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
        _costRow = (TableRow) this.findViewById(R.id.cost_row);
        _costRow.setOnClickListener(COST_ROW_CLICK);

        _revenueValue = (TextView) this.findViewById(R.id.revenue_value);

        _newButton = (ImageButton) this.findViewById(R.id.new_icon);
        _newButton.setOnClickListener(MENU_BTN_CLICK);

        _reportButton = (ImageButton) this.findViewById(R.id.report_icon);
        _reportButton.setOnClickListener(REPORT_BTN_CLICK);
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
        case R.id.menu_check_balance:
            ActivityAction action = new ActivityAction(
                    CheckBalanceActivity.class, this);
            action.execute();
            break;
        case R.id.menu_close_ledger:
            showDialog(R.id.dialog_close_ledger_confirm);
            break;
        }

        return true;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
        case R.id.dialog_entries:
            AlertDialog entryDialog = (AlertDialog) dialog;
            EntriesDialogBuilder.setDataEntriesDialog(entryDialog, this);
            return;
        }

        super.onPrepareDialog(id, dialog);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case R.id.dialog_entries:
            return EntriesDialogBuilder.buildEntriesDialog(this);
        case R.id.dialog_close_ledger_confirm:
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(this.getString(R.string.message_close_confirm))
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int id) {
                                    closeLedger();
                                    dialog.dismiss();
                                }
                            }).setNegativeButton(R.string.cancel, null);
            return builder.create();
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

                // get color
                int colorID = ChartUtil.COLORS[count % ChartUtil.COLORS.length];
                renderer.setColor(this.getResources().getColor(colorID));
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
            cur.negate();
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
        DocumentsListNavigation.navigate2DocList(this, monthId,
                item.Account.getIdentity());
    }

    /**
     * close ledger
     */
    private void closeLedger() {
        CoreDriver coreDriver = this._dataCore.getCoreDriver();
        TransactionDataManagement transMgmt = coreDriver
                .getTransDataManagement();

        transMgmt.monthEndClose();
    }
}
