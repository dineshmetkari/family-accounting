package com.jasonzqshen.familyAccounting.settings;

import com.jasonzqshen.familyAccounting.R;

public class RevAccountEditActivity extends AbstractAccountEditActivity {

    @Override
    protected String getStrTitle() {
        String str = this
                .getString(R.string.masterdata_edit_rev_account_title);
        return String.format("%s%s", str, _glAccountId.toString());
    }
}
