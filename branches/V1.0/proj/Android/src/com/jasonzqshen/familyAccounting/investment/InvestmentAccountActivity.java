package com.jasonzqshen.familyAccounting.investment;

import com.jasonzqshen.familyAccounting.R;
import com.jasonzqshen.familyAccounting.data.DataCore;
import com.jasonzqshen.familyAccounting.settings.MasterDataSettingActivity;
import com.jasonzqshen.familyAccounting.utils.ActivityAction;
import com.jasonzqshen.familyAccounting.widgets.InvestAccItemAdapter;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.format.CurrencyAmountFormatException;
import com.jasonzqshen.familyaccounting.core.investment.InvestmentAccount;
import com.jasonzqshen.familyaccounting.core.investment.InvestmentItem;
import com.jasonzqshen.familyaccounting.core.investment.InvestmentManagement;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataBase;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

public class InvestmentAccountActivity extends ListActivity {

    public static final String TAG = "InvestmentAccountActivity";

    private InvestAccItemAdapter _adapter;

    private InvestmentAccount _investAccount;

    private int _selectedId;

    private View _view;

    private TextView _amountField;

    /**
     * set long click listener
     */
    private final AdapterView.OnItemLongClickListener _ITEM_LONG_CLICK_LISTENER = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapter, View view,
                int position, long id) {
            _selectedId = position;

            showDialog(R.id.dialog_investment_item_commit);
            return true;
        }
    };

    /**
     * 
     */
    private final DialogInterface.OnClickListener _COMMIT_CLICK_LISTENER = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            try {
                CurrencyAmount amount = CurrencyAmount.parse(_amountField
                        .getText().toString());
                InvestmentItem item = _investAccount.getItems()
                        .get(_selectedId);
                item.commit(amount);
            } catch (CurrencyAmountFormatException e) {
                showDialog(R.id.dialog_amount_format_error);
            }
        }
    };

    /**
     * on alert dialog OK click
     */
    private DialogInterface.OnClickListener _SAVED_ALERT_DIALOG_OK_CLICK = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            finish();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.investmentaccount_main);

        // get input investment management and investment account
        MasterDataIdentity_GLAccount identity = (MasterDataIdentity_GLAccount) this
                .getIntent().getSerializableExtra(
                        MasterDataSettingActivity.PARAM_MASTERDATA_ID);
        DataCore dataCore = DataCore.getInstance();
        InvestmentManagement investMgmt = dataCore.getInvestMgmt();
        _investAccount = investMgmt.getInvestmentAccount(identity);

        CoreDriver coreDriver = dataCore.getCoreDriver();
        MasterDataManagement mdMgmt = coreDriver.getMasterDataManagement();
        MasterDataBase masterData = mdMgmt.getMasterData(
                _investAccount.getAccount(), MasterDataType.GL_ACCOUNT);
        this.setTitle(masterData.getDescp());

        // commit view
        LayoutInflater factory = LayoutInflater
                .from(InvestmentAccountActivity.this);
        _view = factory.inflate(R.layout.investment_item_commit_entry, null);
        _amountField = (TextView) _view.findViewById(R.id.amountValue);

        // set long click listener
        this.getListView()
                .setOnItemLongClickListener(_ITEM_LONG_CLICK_LISTENER);
    }

    @Override
    protected void onResume() {
        super.onResume();

        _adapter = new InvestAccItemAdapter(this, _investAccount.getItems());
        this.setListAdapter(_adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.investment_acccount_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_new:
            ActivityAction action = new ActivityAction(
                    NewInvestmentAccountItem.class, this);
            action.ParamName = MasterDataSettingActivity.PARAM_MASTERDATA_ID;
            action.ParamValue = _investAccount.getAccount();

            action.execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Dialog onCreateDialog(int id) {
        switch (id) {
        case R.id.dialog_investment_item_commit:
            return new AlertDialog.Builder(this)
                    .setTitle(R.string.entry_investment_commit).setView(_view)
                    .setPositiveButton(R.string.ok, _COMMIT_CLICK_LISTENER)
                    .create();
        case R.id.dialog_amount_format_error:
            return new AlertDialog.Builder(this).setTitle(R.string.error)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(R.string.message_currency_format_error)
                    .setPositiveButton(R.string.ok, null).create();
        case R.id.dialog_document_saved:
            return new AlertDialog.Builder(this)
                    .setTitle(R.string.entry_saved)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setMessage(R.string.message_investitem_saved)
                    .setPositiveButton(R.string.ok,
                            _SAVED_ALERT_DIALOG_OK_CLICK).create();
        }

        return super.onCreateDialog(id);
    }
}
