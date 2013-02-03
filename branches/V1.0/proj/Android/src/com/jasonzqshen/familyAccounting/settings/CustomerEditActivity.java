package com.jasonzqshen.familyAccounting.settings;

import com.jasonzqshen.familyAccounting.R;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;

public class CustomerEditActivity extends AbstractEditActivity {

    @Override
    protected String getStrTitle() {
        String string = this.getString(R.string.masterdata_edit_customer_title);
        return String.format("%s%s", string, _masterDataId.toString());
    }

    @Override
    protected MasterDataType getMasterDataType() {
        return MasterDataType.CUSTOMER;
    }

    @Override
    protected int getContentId() {
        return R.layout.masterdata_edit_vendor_customer;
    }

}
