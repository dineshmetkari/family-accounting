package com.jasonzqshen.familyAccounting.entries;

import com.jasonzqshen.familyAccounting.R;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.document_entries.CustomerEntry;
import com.jasonzqshen.familyaccounting.core.document_entries.IDocumentEntry;

public class CustomerEntryActivity extends EntryActivityBase {

    @Override
    protected int getContentViewId() {
        return R.layout.incoming_entry;
    }

    @Override
    protected EntryField[] getEntryFields() {

        return new EntryField[] {
                new EntryField(R.id.incomingRow, R.id.incomingValue,
                        R.id.dialog_incoming_selection, CustomerEntry.REC_ACC),
                new EntryField(R.id.customerRow, R.id.customerValue,
                        R.id.dialog_customer_selection, CustomerEntry.CUSTOMER),
                new EntryField(R.id.profitRow, R.id.profitValue,
                        R.id.dialog_profit_selection, CustomerEntry.GL_ACCOUNT) };
    }

    @Override
    protected IDocumentEntry constructDocEntry(CoreDriver coreDriver) {
        return new CustomerEntry(coreDriver);
    }

}
