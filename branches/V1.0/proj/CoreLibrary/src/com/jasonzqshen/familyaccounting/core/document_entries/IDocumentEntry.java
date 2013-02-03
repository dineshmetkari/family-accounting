package com.jasonzqshen.familyaccounting.core.document_entries;

import com.jasonzqshen.familyaccounting.core.exception.MandatoryFieldIsMissing;
import com.jasonzqshen.familyaccounting.core.exception.NoFieldNameException;
import com.jasonzqshen.familyaccounting.core.exception.NotInValueRangeException;
import com.jasonzqshen.familyaccounting.core.exception.SaveClosedLedgerException;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataBase;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;

public interface IDocumentEntry {
	public final static String POSTING_DATE = "POSTING_DATE";

	public final static String AMOUNT = "AMOUNT";

	public final static String TEXT = "TEXT";

	void setValue(String fieldName, Object value) throws NoFieldNameException,
			NotInValueRangeException;

	Object getValue(String fieldName) throws NoFieldNameException;

	Object getDefaultValue(String fieldName) throws NoFieldNameException;

	MasterDataBase[] getValueSet(String fieldName) throws NoFieldNameException;

	/**
	 * check before save
	 * 
	 * @throws MandatoryFieldIsMissing
	 */
	void checkBeforeSave() throws MandatoryFieldIsMissing;

	/**
	 * save document
	 * 
	 * @throws MandatoryFieldIsMissing
	 */
	void save(boolean saved) throws MandatoryFieldIsMissing,
			SaveClosedLedgerException;

	/**
	 * is saved
	 * 
	 * @return
	 */
	boolean isSaved();

	/**
	 * get document, after save
	 * 
	 * @return
	 */
	HeadEntity getDocument();
}
