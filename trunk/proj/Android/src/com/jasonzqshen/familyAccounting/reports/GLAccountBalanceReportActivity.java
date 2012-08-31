package com.jasonzqshen.familyAccounting.reports;

import java.util.ArrayList;

import com.jasonzqshen.familyAccounting.EntriesDialogBuilder;
import com.jasonzqshen.familyAccounting.R;
import com.jasonzqshen.familyAccounting.data.DataCore;
import com.jasonzqshen.familyAccounting.widgets.AccountReportAdapterItem;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.transaction.MonthIdentity;
import com.jasonzqshen.familyaccounting.core.utils.GLAccountGroup;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

public class GLAccountBalanceReportActivity extends BalanceReportActivityBase {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImageButton newBtn = (ImageButton) this.findViewById(R.id.new_icon);
        newBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(R.id.dialog_entries);
            }
        });
        _dataCore = DataCore.getInstance();
    }

    @Override
    public void onResume() {
        super.onResume();

        refersh();
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
    protected void onListItemClick(ListView l, View v, int position, long id) {
        CoreDriver coreDriver = _dataCore.getCoreDriver();
        if (coreDriver.isInitialized() == false) {
            return;
        }

        MonthIdentity monthId = coreDriver.getCurMonthId();

        AccountReportAdapterItem item = (AccountReportAdapterItem) _adapter
                .getItem(position);
        if (item.Type != AccountReportAdapterItem.ITEM_VIEW) {
            return;
        }

        ArrayList<Object> list = new ArrayList<Object>();
        list.add(item.Account.getIdentity());

        // add parameters
        ArrayList<DocListParamItem> items = new ArrayList<DocListParamItem>();
        items.add(new DocListParamItem(list, DocListParam.ACCOUNT_CATEGORY));

        DocListParam param = new DocListParam(monthId, items);
        Intent docListIntent = new Intent(this, DocumentsListActivity.class);
        docListIntent.putExtra(DocListParam.PARAM_NAME, param);
        this.startActivity(docListIntent);
    }

    @Override
    protected GLAccountGroup[] getAccountGroup() {
        return new GLAccountGroup[] { GLAccountGroup.CASH,
                GLAccountGroup.BANK_ACCOUNT, GLAccountGroup.ASSETS,
                GLAccountGroup.PREPAID, GLAccountGroup.INVESTMENT,
                GLAccountGroup.SHORT_LIABILITIES,
                GLAccountGroup.LONG_LIABILITIES, GLAccountGroup.EQUITY };
    }

    @Override
    protected int getViewId() {
        return R.layout.account_balance_report;
    }
}
