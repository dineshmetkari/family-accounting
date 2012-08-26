package com.jasonzqshen.familyAccounting.entries;

import com.jasonzqshen.familyAccounting.R;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.document_entries.GLAccountEntry;
import com.jasonzqshen.familyaccounting.core.document_entries.IDocumentEntry;

public class GLEntryActivity extends EntryActivityBase {

    @Override
    protected int getContentViewId() {
        return R.layout.internal_transfer_entry;
    }

    @Override
    protected EntryField[] getEntryFields() {
        return new EntryField[] {
                new EntryField(R.id.srcRow, R.id.srcValue,
                        R.id.dialog_src_account_selection,
                        GLAccountEntry.SRC_ACCOUNT),
                new EntryField(R.id.dstRow, R.id.dstValue,
                        R.id.dialog_dst_account_selection,
                        GLAccountEntry.DST_ACCOUNT) };
    }

    @Override
    protected IDocumentEntry constructDocEntry(CoreDriver coreDriver) {
        return new GLAccountEntry(coreDriver);
    }

}
