package com.jasonzqshen.familyAccounting.settings;

import com.jasonzqshen.familyAccounting.R;

public class CostAccountEditActivity extends AbstractAccountEditActivity {

    @Override
    protected String getStrTitle() {
        String str = this
                .getString(R.string.masterdata_edit_cost_account_title);
        return String.format("%s%s", str, _glAccountId.toString());
    }
}
