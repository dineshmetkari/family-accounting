package com.jasonzqshen.familyAccounting.reports;

import java.util.ArrayList;

import com.jasonzqshen.familyAccounting.EntriesDialogBuilder;
import com.jasonzqshen.familyAccounting.R;
import com.jasonzqshen.familyAccounting.data.DataCore;
import com.jasonzqshen.familyAccounting.widgets.AccountReportAdapter;
import com.jasonzqshen.familyAccounting.widgets.AccountReportAdapterItem;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.masterdata.GLAccountMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;
import com.jasonzqshen.familyaccounting.core.transaction.GLAccountBalanceCollection;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;
import com.jasonzqshen.familyaccounting.core.utils.GLAccountGroup;

import android.app.Dialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public abstract class BalanceReportActivityBase extends ListActivity {
    protected DataCore _dataCore = null;

    protected AccountReportAdapter _adapter = null;

    private GLAccountGroup[] _groupSet = getAccountGroup();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getViewId());
        _dataCore = DataCore.getInstance();
    }

    @Override
    public void onResume() {
        super.onResume();

        refersh();
    }

    /**
     * refresh
     */
    protected void refersh() {
        ArrayList<AccountReportAdapterItem> arrList = new ArrayList<AccountReportAdapterItem>();
        CoreDriver coreDriver = _dataCore.getCoreDriver();
        if (!coreDriver.isInitialized()) {
            return;
        }

        MasterDataManagement masterDataMgmt = coreDriver
                .getMasterDataManagement();
        GLAccountBalanceCollection balCol = coreDriver.getTransDataManagement()
                .getAccBalCol();

        for (int i = 0; i < _groupSet.length; ++i) {
            GLAccountGroup group = _groupSet[i];

            CurrencyAmount groupAmount = balCol.getGroupBalance(group);

            if (group == GLAccountGroup.SHORT_LIABILITIES
                    || group == GLAccountGroup.LONG_LIABILITIES) {
                arrList.add(new AccountReportAdapterItem(GLAccountGroup
                        .getDescp(group), groupAmount,
                        AccountReportAdapterItem.HEAD_VIEW_RED, null));
            } else {
                if (group == GLAccountGroup.EQUITY) {
                    groupAmount.negate();
                }
                arrList.add(new AccountReportAdapterItem(GLAccountGroup
                        .getDescp(group), groupAmount,
                        AccountReportAdapterItem.HEAD_VIEW, null));
            }
            MasterDataIdentity_GLAccount[] accountsID = masterDataMgmt
                    .getGLAccountsBasedGroup(group);
            for (MasterDataIdentity_GLAccount id : accountsID) {
                GLAccountMasterData account = (GLAccountMasterData) masterDataMgmt
                        .getMasterData(id, MasterDataType.GL_ACCOUNT);
                CurrencyAmount amount = balCol.getBalanceItem(id)
                        .getSumAmount();

                if (group == GLAccountGroup.EQUITY) {
                    amount.negate();
                }
                arrList.add(new AccountReportAdapterItem(account.getDescp(),
                        amount, AccountReportAdapterItem.ITEM_VIEW, account));
            }

        }

        _adapter = new AccountReportAdapter(this, arrList);
        setListAdapter(_adapter);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case R.id.dialog_entries:
            return EntriesDialogBuilder.BuildEntriesDialog(this);
        }

        return null;
    }

    @Override
    protected abstract void onListItemClick(ListView l, View v, int position,
            long id);

    protected abstract GLAccountGroup[] getAccountGroup();
    
    protected abstract int getViewId();
}
