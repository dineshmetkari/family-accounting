package com.jasonzqshen.familyAccounting.reports;

import com.jasonzqshen.familyAccounting.R;
import com.jasonzqshen.familyaccounting.core.utils.GLAccountGroup;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class GLAccountBalanceReportActivity extends BalanceReportActivityBase {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        refersh();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

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
