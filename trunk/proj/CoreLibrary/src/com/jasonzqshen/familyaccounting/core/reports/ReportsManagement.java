package com.jasonzqshen.familyaccounting.core.reports;

import java.util.ArrayList;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;

public class ReportsManagement {
	protected final DocumentIndex[] indexes;
	protected final CoreDriver _coreDriver;

	public ReportsManagement(CoreDriver coreDriver) {
		_coreDriver = coreDriver;

		indexes = new DocumentIndex[DocumentIndex.INDEX_COUNT];
		indexes[DocumentIndex.ACCOUNT_INDEX] = new DocumentAccountIndex(
				_coreDriver);
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
	 * @param indexType
	 * @return
	 */
	public DocumentIndex getDocumentIndex(int indexType) {
		DocumentIndex index = indexes[indexType];
		return index;
	}
}
