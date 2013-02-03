package com.jasonzqshen.familyAccounting.settings;

import com.jasonzqshen.familyAccounting.R;
import com.jasonzqshen.familyaccounting.core.utils.GLAccountGroup;

public class RevAccountNewActivity extends AbstractAccountNewActivity {

    @Override
    protected GLAccountGroupItem[] getGLAccountGroupSet() {
        return new GLAccountGroupItem[] {
                new GLAccountGroupItem(this, GLAccountGroup.SALARY),
                new GLAccountGroupItem(this, GLAccountGroup.INVEST_REVENUE) };
    }

    @Override
    protected int getStrTitle() {
        return R.string.masterdata_new_rev_account_title;
    }

}
