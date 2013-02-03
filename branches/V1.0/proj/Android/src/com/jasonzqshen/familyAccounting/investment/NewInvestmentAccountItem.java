package com.jasonzqshen.familyAccounting.investment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.jasonzqshen.familyAccounting.R;
import com.jasonzqshen.familyAccounting.data.DataCore;
import com.jasonzqshen.familyAccounting.settings.MasterDataSettingActivity;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityNotDefined;
import com.jasonzqshen.familyaccounting.core.exception.format.CurrencyAmountFormatException;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.investment.InvestmentAccount;
import com.jasonzqshen.familyaccounting.core.investment.InvestmentManagement;
import com.jasonzqshen.familyaccounting.core.masterdata.GLAccountMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

public class NewInvestmentAccountItem extends Activity {
    /**
     * start date picker click
     */
    private DatePickerDialog.OnDateSetListener _START_DATE_CLICK = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                int dayOfMonth) {
            _startCalendar.set(Calendar.YEAR, year);
            _startCalendar.set(Calendar.MONTH, monthOfYear);
            _startCalendar.set(Calendar.DATE, dayOfMonth);

            setDate();
        }
    };

    /**
     * on click start date row
     */
    private View.OnClickListener _START_DATE_ROW_CLICK = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int year = _startCalendar.get(Calendar.YEAR);
            int month = _startCalendar.get(Calendar.MONTH);
            int date = _startCalendar.get(Calendar.DATE);

            new DatePickerDialog(NewInvestmentAccountItem.this,
                    _START_DATE_CLICK, year, month, date).show();
        }
    };

    /**
     * start due picker click
     */
    private DatePickerDialog.OnDateSetListener _DUE_DATE_CLICK = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                int dayOfMonth) {
            _dueCalendar.set(Calendar.YEAR, year);
            _dueCalendar.set(Calendar.MONTH, monthOfYear);
            _dueCalendar.set(Calendar.DATE, dayOfMonth);

            setDate();
        }
    };

    /**
     * on click start date row
     */
    private View.OnClickListener _DUE_DATE_ROW_CLICK = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int year = _dueCalendar.get(Calendar.YEAR);
            int month = _dueCalendar.get(Calendar.MONTH);
            int date = _dueCalendar.get(Calendar.DATE);

            new DatePickerDialog(NewInvestmentAccountItem.this,
                    _DUE_DATE_CLICK, year, month, date).show();
        }
    };

    /**
     * save click
     */
    private View.OnClickListener _SAVE_CLICK = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            save();
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

    /**
     * start date calendar
     */
    private Calendar _startCalendar;

    /**
     * due date calendar
     */
    private Calendar _dueCalendar;

    /**
     * start date row
     */
    private TableRow _startDateRow;

    /**
     * due date row
     */
    private TableRow _dueDateRow;

    /**
     * start date field
     */
    private TextView _startDateField;

    /**
     * due date field
     */
    private TextView _dueDateField;

    /**
     * source field
     */
    private Spinner _srcField;

    /**
     * save button
     */
    private ImageButton _saveBtn;

    /**
     * accounts
     */
    private GLAccountMasterData[] _accounts;

    private InvestmentAccount _investAccount;

    private TextView _amountField;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.investment_item_entry);

        // start date
        _startDateRow = (TableRow) this.findViewById(R.id.startDateRow);
        _startDateRow.setOnClickListener(_START_DATE_ROW_CLICK);
        _startDateField = (TextView) this.findViewById(R.id.startDateValue);

        _startCalendar = Calendar.getInstance();

        // due date
        _dueDateRow = (TableRow) this.findViewById(R.id.dueDateRow);
        _dueDateRow.setOnClickListener(_DUE_DATE_ROW_CLICK);
        _dueDateField = (TextView) this.findViewById(R.id.dueDateValue);

        _dueCalendar = Calendar.getInstance();

        // amount
        _amountField = (TextView) this.findViewById(R.id.amountValue);

        // set source account
        DataCore dataCore = DataCore.getInstance();
        CoreDriver coreDriver = dataCore.getCoreDriver();
        MasterDataManagement mdMgmt = coreDriver.getMasterDataManagement();
        _accounts = mdMgmt.getLiquidityAccounts();

        _srcField = (Spinner) this.findViewById(R.id.srcValue);
        ArrayList<String> strArray = new ArrayList<String>();
        for (GLAccountMasterData glaccount : _accounts) {
            strArray.add(glaccount.getDescp());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, strArray);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        _srcField.setAdapter(adapter);

        _saveBtn = (ImageButton) this.findViewById(R.id.save_icon);
        _saveBtn.setOnClickListener(_SAVE_CLICK);

        // get input investment management and investment account
        MasterDataIdentity_GLAccount identity = (MasterDataIdentity_GLAccount) this
                .getIntent().getSerializableExtra(
                        MasterDataSettingActivity.PARAM_MASTERDATA_ID);
        InvestmentManagement investMgmt = dataCore.getInvestMgmt();
        _investAccount = investMgmt.getInvestmentAccount(identity);

        // set default date
        setDate();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // set source accounts
        DataCore dataCore = DataCore.getInstance();
        CoreDriver coreDriver = dataCore.getCoreDriver();
        MasterDataManagement mdMgmt = coreDriver.getMasterDataManagement();
        _accounts = mdMgmt.getLiquidityAccounts();

        ArrayList<String> strArray = new ArrayList<String>();
        for (GLAccountMasterData glaccount : _accounts) {
            strArray.add(glaccount.getDescp());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, strArray);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        _srcField.setAdapter(adapter);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
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

    /**
     * set posting date
     */
    private void setDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");

        String dateStr = format.format(_startCalendar.getTime());
        _startDateField.setText(dateStr);

        dateStr = format.format(_dueCalendar.getTime());
        _dueDateField.setText(dateStr);
    }

    /**
     * save
     */
    private void save() {
        try {
            int position = this._srcField.getSelectedItemPosition();
            CurrencyAmount amount = CurrencyAmount.parse(_amountField.getText()
                    .toString());
            _investAccount.createInvestment(_startCalendar.getTime(),
                    _dueCalendar.getTime(), _accounts[position].getIdentity(),
                    amount);
        } catch (CurrencyAmountFormatException e) {
            showDialog(R.id.dialog_amount_format_error);
            return;
        } catch (MasterDataIdentityNotDefined e) {
            throw new SystemException(e);
        }

        showDialog(R.id.dialog_document_saved);
    }
}
