package com.jasonzqshen.familyAccounting.entries;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;

import com.jasonzqshen.familyAccounting.R;
import com.jasonzqshen.familyAccounting.reports.BalanceReportActivityBase;
import com.jasonzqshen.familyAccounting.widgets.AccountReportAdapter;
import com.jasonzqshen.familyAccounting.widgets.AccountReportAdapterItem;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityNotDefined;
import com.jasonzqshen.familyaccounting.core.exception.NullValueNotAcceptable;
import com.jasonzqshen.familyaccounting.core.exception.format.CurrencyAmountFormatException;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.masterdata.GLAccountMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;
import com.jasonzqshen.familyaccounting.core.transaction.ItemEntity;
import com.jasonzqshen.familyaccounting.core.utils.CreditDebitIndicator;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;
import com.jasonzqshen.familyaccounting.core.utils.DocumentType;
import com.jasonzqshen.familyaccounting.core.utils.GLAccountGroup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;

public class CheckBalanceActivity extends BalanceReportActivityBase {
    public static final String TAG = "CheckBalanceActivity";

    private EditText _newAmountField;

    private AccountReportAdapterItem _selectedItem;

    private Hashtable<GLAccountMasterData, AmountPair> _newValueList;

    private Spinner _costSpinner;

    private GLAccountMasterData[] _costAccounts;

    private final DialogInterface.OnClickListener _OK_CLICK_LISTENER = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            String numStr = _newAmountField.getText().toString();
            try {
                CurrencyAmount amount = CurrencyAmount.parse(numStr);
                if (_newValueList.containsKey(_selectedItem.Account)) {
                    AmountPair amountPair = _newValueList
                            .get(_selectedItem.Account);
                    AmountPair newAmountPair = new AmountPair(
                            _selectedItem.Account, amountPair.OrgAmount, amount);
                    _newValueList.put(_selectedItem.Account, newAmountPair);
                } else {
                    AmountPair newAmountPair = new AmountPair(
                            _selectedItem.Account, _selectedItem.Amount, amount);
                    _newValueList.put(_selectedItem.Account, newAmountPair);
                }

                setListAdapter(_adapter);
            } catch (CurrencyAmountFormatException e) {
                return;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CoreDriver coreDriver = _dataCore.getCoreDriver();
        if (coreDriver.isInitialized() == false) {
            return;
        }

        _newAmountField = new EditText(this);
        _newAmountField.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL
                | InputType.TYPE_CLASS_NUMBER
                | InputType.TYPE_NUMBER_FLAG_SIGNED);
        CurrencyAmount amount = new CurrencyAmount();
        _newAmountField.setHint(amount.toString());
        _newAmountField.setGravity(Gravity.RIGHT);

        // construct new value list
        _newValueList = new Hashtable<GLAccountMasterData, AmountPair>();

        // set the cost accounts selection
        _costAccounts = coreDriver.getMasterDataManagement().getCostAccounts();
        _costSpinner = (Spinner) this.findViewById(R.id.costSpinner);
        ArrayAdapter<GLAccountMasterData> adapter = new ArrayAdapter<GLAccountMasterData>(
                this, android.R.layout.simple_spinner_item, _costAccounts);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _costSpinner.setAdapter(adapter);

        // undo button
        ImageButton undoBtn = (ImageButton) this.findViewById(R.id.undo_icon);
        undoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _newValueList.clear();
                setListAdapter(_adapter);
            }
        });

        // save button
        ImageButton saveBtn = (ImageButton) this.findViewById(R.id.check_icon);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(R.id.dialog_save_comfirm);
            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        _selectedItem = (AccountReportAdapterItem) _adapter.getItem(position);
        if (_selectedItem.Type == AccountReportAdapterItem.ITEM_VIEW) {
            if (_newValueList.containsKey(_selectedItem.Account)) {
                AmountPair amountPair = _newValueList
                        .get(_selectedItem.Account);
                _newAmountField.setText(amountPair.CurAmount.toString());
            } else {
                _newAmountField.setText("");
            }

            showDialog(R.id.dialog_check_balance);
        }
    }

    @Override
    protected GLAccountGroup[] getAccountGroup() {
        return new GLAccountGroup[] { GLAccountGroup.CASH,
                GLAccountGroup.BANK_ACCOUNT, GLAccountGroup.PREPAID,
                GLAccountGroup.SHORT_LIABILITIES,
                GLAccountGroup.LONG_LIABILITIES };
    }

    @Override
    protected int getViewId() {
        return R.layout.check_balance;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case R.id.dialog_check_balance:
            return new AlertDialog.Builder(this)
                    .setTitle(R.string.entry_org_balance)
                    .setView(_newAmountField)
                    .setPositiveButton(R.string.ok, _OK_CLICK_LISTENER)
                    .setNegativeButton(R.string.cancel, null).show();
        case R.id.dialog_save_comfirm:
            return new AlertDialog.Builder(this)
                    .setMessage(R.string.message_save_confirm)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    save();
                                    dialog.dismiss();
                                }
                            }).setNegativeButton(R.string.cancel, null).show();
        }
        return super.onCreateDialog(id);
    }

    @Override
    public void setListAdapter(ListAdapter adapter) {
        ArrayList<AccountReportAdapterItem> items = new ArrayList<AccountReportAdapterItem>();
        for (int i = 0; i < _adapter.getCount(); ++i) {
            AccountReportAdapterItem adapterItem = (AccountReportAdapterItem) _adapter
                    .getItem(i);
            if (adapterItem.Type == AccountReportAdapterItem.ITEM_VIEW
                    || adapterItem.Type == AccountReportAdapterItem.ITEM_VIEW_FOCUS) {
                if (_newValueList.containsKey(adapterItem.Account)) {
                    AmountPair amountPair = _newValueList
                            .get(adapterItem.Account);
                    AccountReportAdapterItem newAdapterItem = new AccountReportAdapterItem(
                            amountPair.GLAccount.getDescp(),
                            amountPair.CurAmount,
                            AccountReportAdapterItem.ITEM_VIEW_FOCUS,
                            amountPair.GLAccount);

                    Log.d(TAG,
                            "Balance is changed: "
                                    + amountPair.GLAccount.getDescp());
                    items.add(newAdapterItem);
                    continue;
                }
            }
            items.add(adapterItem);

        }
        AccountReportAdapter newAdapter = new AccountReportAdapter(this, items);
        super.setListAdapter(newAdapter);
    }

    /**
     * save
     */
    private void save() {
        if (_newValueList.size() == 0) {
            finish();
            return;
        }

        CoreDriver coreDriver = this._dataCore.getCoreDriver();
        MasterDataManagement mdMgmt = coreDriver.getMasterDataManagement();

        HeadEntity doc = new HeadEntity(coreDriver, mdMgmt);
        doc.setPostingDate(Calendar.getInstance().getTime());
        doc.setDocumentType(DocumentType.GL);
        doc.setDocText("check balance");
        CurrencyAmount sum = new CurrencyAmount();
        for (AmountPair amountPair : _newValueList.values()) {
            CurrencyAmount curAmount = CurrencyAmount.minus(
                    amountPair.CurAmount, amountPair.OrgAmount);
            sum.addTo(curAmount);

            ItemEntity item = doc.createEntity();
            try {
                item.setGLAccount(amountPair.GLAccount.getIdentity());
                CreditDebitIndicator indicator = CreditDebitIndicator.DEBIT;
                if (curAmount.isNegative()) {
                    indicator = CreditDebitIndicator.CREDIT;
                    curAmount.negate();
                }

                item.setAmount(indicator, curAmount);
            } catch (NullValueNotAcceptable e) {
                throw new SystemException(e);
            } catch (MasterDataIdentityNotDefined e) {
                throw new SystemException(e);
            }
        }

        if (sum.isZero() == false) {
            CreditDebitIndicator indicator = CreditDebitIndicator.CREDIT;
            if (sum.isNegative()) {
                indicator = CreditDebitIndicator.DEBIT;
                sum.negate();
            }

            ItemEntity item = doc.createEntity();
            try {
                item.setGLAccount(_costAccounts[_costSpinner
                        .getSelectedItemPosition()].getIdentity());
                item.setAmount(indicator, sum);
            } catch (NullValueNotAcceptable e) {
                throw new SystemException(e);
            } catch (MasterDataIdentityNotDefined e) {
                throw new SystemException(e);
            }
        }

        doc.save(true);
        finish();
    }
}
