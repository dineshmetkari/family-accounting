package com.jasonzqshen.familyAccounting;

import java.text.DateFormatSymbols;

import com.jasonzqshen.familyAccounting.data.DataCore;
import com.jasonzqshen.familyAccounting.entries.CheckBalanceActivity;
import com.jasonzqshen.familyAccounting.investment.InvestmentMainActivity;
import com.jasonzqshen.familyAccounting.reports.AbstractCostDetailsActivity;

import com.jasonzqshen.familyAccounting.utils.ActivityAction;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.transaction.GLAccountBalanceCollection;
import com.jasonzqshen.familyaccounting.core.transaction.MonthIdentity;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;
import com.jasonzqshen.familyaccounting.core.utils.GLAccountGroup;

import android.os.Bundle;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AbstractCostDetailsActivity {
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
     * menu button click
     */
    private final OnClickListener REPORT_BTN_CLICK = new OnClickListener() {
        @Override
        public void onClick(View v) {
            _handler.navigate2BalanceReport();
        }
    };

    // private AccountReportAdapter _costAccountAdapter;

    private final DataCore _dataCore;

    /**
     * constructor, initialize the data
     */
    public MainActivity() {
        // initialize data component
        _dataCore = DataCore.getInstance();

    }

    /**
     * UI controller
     */
    private TextView _monthIdentity = null;

    private TextView _yearValue = null;

    private TextView _revenueValue = null;

    private ImageButton _newButton = null;

    private ImageButton _reportButton = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // get text view
        _monthIdentity = (TextView) this.findViewById(R.id.month_identity);
        _yearValue = (TextView) this.findViewById(R.id.year);

        _revenueValue = (TextView) this.findViewById(R.id.revenue_value);

        _newButton = (ImageButton) this.findViewById(R.id.new_icon);
        _newButton.setOnClickListener(MENU_BTN_CLICK);

        _reportButton = (ImageButton) this.findViewById(R.id.report_icon);
        _reportButton.setOnClickListener(REPORT_BTN_CLICK);

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
            return true;
        case R.id.menu_check_balance:
            ActivityAction action = new ActivityAction(
                    CheckBalanceActivity.class, this);
            action.execute();
            return true;
        case R.id.menu_investment:
            ActivityAction actionInvest = new ActivityAction(
                    InvestmentMainActivity.class, this);
            actionInvest.execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        }

        return super.onCreateDialog(id);
    }

    @Override
    protected void setData() {
        super.setData();

        // set the data in bar chart
        CoreDriver coreDriver = _dataCore.getCoreDriver();
        if (coreDriver.isInitialized() == false) {
            return;
        }

        GLAccountBalanceCollection balCol = coreDriver.getTransDataManagement()
                .getAccBalCol();

        // month identity in header
        MonthIdentity monthId = coreDriver.getCurCalendarMonthId();
        _yearValue.setText(String.valueOf(monthId._fiscalYear));
        String[] months = new DateFormatSymbols().getMonths();
        _monthIdentity.setText(months[monthId._fiscalMonth - 1]);

        // revenue value in header
        CurrencyAmount revenueAmount = new CurrencyAmount();
        for (GLAccountGroup group : GLAccountGroup.REVENUE_GROUP) {
            CurrencyAmount cur = balCol
                    .getGroupBalance(group, monthId, monthId);
            cur.negate();
            revenueAmount.addTo(cur);
        }
        this._revenueValue.setText(revenueAmount.toString());
    }

    /**
     * @Override protected void onListItemClick(ListView l, View v, int
     *           position, long id) { // click on cost account list // negative
     *           to show the details of cost AccountReportAdapterItem item =
     *           (AccountReportAdapterItem) _costAccountAdapter
     *           .getItem(position); CoreDriver coreDriver =
     *           _dataCore.getCoreDriver(); if (coreDriver.isInitialized() ==
     *           false) { return; }
     * 
     *           MonthIdentity monthId = coreDriver.getCurMonthId();
     *           DocumentsListNavigation.navigate2DocList(this, monthId,
     *           item.Account.getIdentity()); }
     */


    @Override
    protected int getContentView() {
        return R.layout.main;
    }

    @Override
    protected MonthIdentity getMonthIdentity() {
        CoreDriver coreDriver = this._dataCore.getCoreDriver();
        return coreDriver.getCurCalendarMonthId();
    }
}
