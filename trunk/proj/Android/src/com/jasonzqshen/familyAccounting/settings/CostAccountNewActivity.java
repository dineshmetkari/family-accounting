package com.jasonzqshen.familyAccounting.settings;

import com.jasonzqshen.familyAccounting.R;
import com.jasonzqshen.familyaccounting.core.utils.GLAccountGroup;

public class CostAccountNewActivity extends AbstractAccountNewActivity {

    @Override
    protected GLAccountGroupItem[] getGLAccountGroupSet() {
        return new GLAccountGroupItem[] {
                new GLAccountGroupItem(this, GLAccountGroup.COST_PURE),
                new GLAccountGroupItem(this, GLAccountGroup.COST_ACCI) };
    }

    @Override
    protected int getStrTitle() {
        return R.string.masterdata_new_cost_account_title;
    }

}
