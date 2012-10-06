package com.jasonzqshen.familyAccounting.settings;

import com.jasonzqshen.familyAccounting.R;
import com.jasonzqshen.familyaccounting.core.utils.GLAccountGroup;

public class AccountNewActivity extends AbstractAccountNewActivity {

    @Override
    protected GLAccountGroupItem[] getGLAccountGroupSet() {
        return new GLAccountGroupItem[] {
                new GLAccountGroupItem(this, GLAccountGroup.CASH),
                new GLAccountGroupItem(this, GLAccountGroup.BANK_ACCOUNT),
                new GLAccountGroupItem(this, GLAccountGroup.INVESTMENT),
                new GLAccountGroupItem(this, GLAccountGroup.PREPAID),
                new GLAccountGroupItem(this, GLAccountGroup.ASSETS),
                new GLAccountGroupItem(this, GLAccountGroup.SHORT_LIABILITIES),
                new GLAccountGroupItem(this, GLAccountGroup.LONG_LIABILITIES) };
    }

    @Override
    protected int getStrTitle() {
        return R.string.masterdata_new_account_title;
    }

}
