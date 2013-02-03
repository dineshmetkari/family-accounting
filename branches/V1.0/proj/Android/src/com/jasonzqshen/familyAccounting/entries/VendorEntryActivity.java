package com.jasonzqshen.familyAccounting.entries;

import com.jasonzqshen.familyAccounting.R;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.document_entries.IDocumentEntry;
import com.jasonzqshen.familyaccounting.core.document_entries.VendorEntry;

public class VendorEntryActivity extends EntryActivityBase {

    @Override
    protected int getContentViewId() {
        return R.layout.outgoing_entry;
    }

    @Override
    protected EntryField[] getEntryFields() {
        return new EntryField[] {
                new EntryField(R.id.outgoingRow, R.id.outgoingValue,
                        R.id.dialog_outgoing_selection, VendorEntry.REC_ACC),
                new EntryField(R.id.vendorRow, R.id.vendorValue,
                        R.id.dialog_vendor_selection, VendorEntry.VENDOR),
                new EntryField(R.id.costRow, R.id.costValue,
                        R.id.dialog_cost_selection, VendorEntry.GL_ACCOUNT),
                new EntryField(R.id.businessAreaRow, R.id.businessAreaValue,
                        R.id.dialog_business_selection,
                        VendorEntry.BUSINESS_AREA) };
    }

    @Override
    protected IDocumentEntry constructDocEntry(CoreDriver coreDriver) {
        return new VendorEntry(coreDriver);
    }

}
