package com.jasonzqshen.familyaccounting.core.document_entries;

import java.util.Date;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.MandatoryFieldIsMissing;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityNotDefined;
import com.jasonzqshen.familyaccounting.core.exception.NoFieldNameException;
import com.jasonzqshen.familyaccounting.core.exception.NotInValueRangeException;
import com.jasonzqshen.familyaccounting.core.exception.NullValueNotAcceptable;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.masterdata.GLAccountMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;
import com.jasonzqshen.familyaccounting.core.transaction.ItemEntity;
import com.jasonzqshen.familyaccounting.core.utils.CreditDebitIndicator;
import com.jasonzqshen.familyaccounting.core.utils.DocumentType;
import com.jasonzqshen.familyaccounting.core.utils.MessageType;

public class GLAccountEntry implements IDocumentEntry {
	public static final String SRC_ACCOUNT = "SOURCE_ACCOUNT";
	public static final String DST_ACCOUNT = "DESTINATION_ACCOUNT";
	public static final String AMOUNT = "AMOUNT";
	public static final String TEXT = "TEXT";
	public static final String POSTING_DATE = "DATE";

	private final CoreDriver _coreDriver;
	private HeadEntity _doc = null;
	private MasterDataIdentity_GLAccount _srcAccount; // source account
	private MasterDataIdentity_GLAccount _dstAccount; // destination account
	private Date _pstDate; // posting date
	private double _amount;// amount
	private String _text; // document text
	private boolean _isSaved;

	/**
	 * constructor
	 */
	public GLAccountEntry(CoreDriver coreDriver) {
		_coreDriver = coreDriver;
		_isSaved = false;
	}

	/**
	 * set source G/L account
	 * 
	 * @param srcAccount
	 * @throws NoFieldNameException
	 */
	public void setSourceAccount(MasterDataIdentity_GLAccount srcAccount)
			throws NotInValueRangeException, NoFieldNameException {
		if (srcAccount == null) {
			throw new NotInValueRangeException(SRC_ACCOUNT, "");
		}

		Object[] valueSet = this.getValueSet(SRC_ACCOUNT);
		for (Object obj : valueSet) {
			GLAccountMasterData glAccount = (GLAccountMasterData) obj;
			if (glAccount.getGLIdentity().equals(srcAccount)) {
				_srcAccount = srcAccount;
				return;
			}
		}

		throw new NotInValueRangeException(SRC_ACCOUNT, srcAccount);
	}

	/**
	 * set source G/L account
	 * 
	 * @param srcAccount
	 * @throws NoFieldNameException
	 */
	public void setDstAccount(MasterDataIdentity_GLAccount dstAccount)
			throws NotInValueRangeException, NoFieldNameException {
		if (dstAccount == null) {
			throw new NotInValueRangeException(DST_ACCOUNT, "");
		}

		Object[] valueSet = this.getValueSet(DST_ACCOUNT);
		for (Object obj : valueSet) {
			GLAccountMasterData glAccount = (GLAccountMasterData) obj;
			if (glAccount.getGLIdentity().equals(dstAccount)) {
				_dstAccount = dstAccount;
				return;
			}
		}
		throw new NotInValueRangeException(SRC_ACCOUNT, dstAccount);
	}

	public void checkBeforeSave() throws MandatoryFieldIsMissing {
		if (_srcAccount == null) {
			throw new MandatoryFieldIsMissing(SRC_ACCOUNT);
		}

		if (_dstAccount == null) {
			throw new MandatoryFieldIsMissing(DST_ACCOUNT);
		}

		if (_amount <= 0) {
			throw new MandatoryFieldIsMissing(AMOUNT);
		}

		if (_pstDate == null) {
			throw new MandatoryFieldIsMissing(POSTING_DATE);
		}
	}

	public void save(boolean store) throws MandatoryFieldIsMissing {
		checkBeforeSave();
		try {
			HeadEntity head = new HeadEntity(_coreDriver,
					_coreDriver.getMasterDataManagement());
			head.setPostingDate(_pstDate);
			head.setDocText(_text);
			head.setDocumentType(DocumentType.GL);

			ItemEntity srcItem = head.createEntity();
			srcItem.setAmount(CreditDebitIndicator.CREDIT, _amount);
			srcItem.setGLAccount(_srcAccount);

			ItemEntity dstItem = head.createEntity();
			dstItem.setAmount(CreditDebitIndicator.DEBIT, _amount);
			dstItem.setGLAccount(_dstAccount);

			boolean ret = head.save(store);
			if (ret) {
				_isSaved = true;
				_doc = head;
			}
		} catch (NullValueNotAcceptable e) {
			_coreDriver.logDebugInfo(this.getClass(), 123, e.toString(),
					MessageType.ERROR);
			throw new SystemException(e);
		} catch (MasterDataIdentityNotDefined e) {
			_coreDriver.logDebugInfo(this.getClass(), 123, e.toString(),
					MessageType.ERROR);
			throw new SystemException(e);
		}

	}

	public HeadEntity getDocument() {
		if (_isSaved) {
			return _doc;
		}
		return null;
	}

	public void setValue(String fieldName, Object value)
			throws NoFieldNameException, NotInValueRangeException {
		if (value == null) {
			throw new NotInValueRangeException(fieldName, "");
		}

		if (fieldName.equals(DST_ACCOUNT)) {
			if (!(value instanceof MasterDataIdentity_GLAccount)) {
				throw new NotInValueRangeException(fieldName, value);
			}
			MasterDataIdentity_GLAccount id = (MasterDataIdentity_GLAccount) value;
			setDstAccount(id);
		} else if (fieldName.equals(SRC_ACCOUNT)) {
			if (!(value instanceof MasterDataIdentity_GLAccount)) {
				throw new NotInValueRangeException(fieldName, value);
			}
			MasterDataIdentity_GLAccount id = (MasterDataIdentity_GLAccount) value;
			setSourceAccount(id);
		} else if (fieldName.equals(TEXT)) {
			_text = value.toString();
		} else if (fieldName.equals(AMOUNT)) {
			try {
				double amount = Double.parseDouble(value.toString());
				if (amount <= 0) {
					throw new NotInValueRangeException(fieldName, value);
				}
				_amount = amount;
			} catch (NumberFormatException e) {
				throw new NotInValueRangeException(fieldName, value);
			}
		} else if (fieldName.equals(POSTING_DATE)) {
			if (!(value instanceof Date)) {
				throw new NotInValueRangeException(fieldName, value);
			}
			Date pstDate = (Date) value;
			_pstDate = pstDate;
		} else {
			throw new NoFieldNameException(fieldName);
		}
	}

	public Object getValue(String fieldName) throws NoFieldNameException {
		if (fieldName.equals(DST_ACCOUNT)) {
			return _dstAccount;
		} else if (fieldName.equals(SRC_ACCOUNT)) {
			return _srcAccount;
		} else if (fieldName.equals(TEXT)) {
			return _text;
		} else if (fieldName.equals(AMOUNT)) {
			return _amount;
		}
		throw new NoFieldNameException(fieldName);
	}

	public Object getDefaultValue(String fieldName) throws NoFieldNameException {
		if (fieldName.equals(DST_ACCOUNT)) {
			return null;
		} else if (fieldName.equals(SRC_ACCOUNT)) {
			return null;
		} else if (fieldName.equals(TEXT)) {
			return "";
		} else if (fieldName.equals(AMOUNT)) {
			return 0;
		}
		throw new NoFieldNameException(fieldName);
	}

	public boolean isSaved() {
		return _isSaved;
	}

	@Override
	public Object[] getValueSet(String fieldName) throws NoFieldNameException {
		if (fieldName.equals(DST_ACCOUNT)) {
			MasterDataManagement manage = _coreDriver.getMasterDataManagement();
			return manage.getLiquidityAccounts();
		} else if (fieldName.equals(SRC_ACCOUNT)) {
			MasterDataManagement manage = _coreDriver.getMasterDataManagement();
			return manage.getLiquidityAccounts();
		}
		throw new NoFieldNameException(fieldName);
	}

}
