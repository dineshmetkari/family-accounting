package com.jasonzqshen.familyaccounting.core.reports;

import java.util.ArrayList;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.ManagementBase;
import com.jasonzqshen.familyaccounting.core.exception.format.FormatException;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;

public class ReportsManagement extends ManagementBase {
    protected final DocumentIndex[] indexes;

    protected final CoreDriver _coreDriver;

    public ReportsManagement(CoreDriver coreDriver, MasterDataManagement mdMgmt) {
        super(coreDriver);

        _coreDriver = coreDriver;

        indexes = new DocumentIndex[DocumentIndex.INDEX_COUNT];
        indexes[DocumentIndex.ACCOUNT_INDEX] = new DocumentAccountIndex(
                _coreDriver, mdMgmt);
        indexes[DocumentIndex.BUSINESS_INDEX] = new DocumentBusinessIndex(
                _coreDriver, mdMgmt);
    }

    /**
     * index type
     * 
     * @param indexType
     * @return
     */
    public ArrayList<MasterDataIdentity> getDocumentAccountIndexKeys(
            int indexType) {
        DocumentIndex index = indexes[indexType];
        return index.getKeys();
    }

    /**
     * get index
     * 
     * @param indexType
     * @return
     */
    public DocumentIndex getDocumentIndex(int indexType) {
        DocumentIndex index = indexes[indexType];
        return index;
    }

    @Override
    public void initialize() throws FormatException {
        // nothing
    }

    @Override
    public void clear() {
        for (DocumentIndex index : indexes) {
            index.clear();
        }
    }

    @Override
    public void establishFiles() {
        // nothing
    }
}
