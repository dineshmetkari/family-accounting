package com.jasonzqshen.familyaccounting.core.document_entries;

import java.util.Date;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.MandatoryFieldIsMissing;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityNotDefined;
import com.jasonzqshen.familyaccounting.core.exception.NoFieldNameException;
import com.jasonzqshen.familyaccounting.core.exception.NotInValueRangeException;
import com.jasonzqshen.familyaccounting.core.exception.NullValueNotAcceptable;
import com.jasonzqshen.familyaccounting.core.exception.format.CurrencyAmountFormatException;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.masterdata.GLAccountMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;
import com.jasonzqshen.familyaccounting.core.transaction.ItemEntity;
import com.jasonzqshen.familyaccounting.core.utils.CreditDebitIndicator;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;
import com.jasonzqshen.familyaccounting.core.utils.DocumentType;

public class VendorEntry implements IDocumentEntry {
	public final static String VENDOR = "VENDOR";
	public final static String REC_ACC = "REC_ACC";
	public final static String GL_ACCOUNT = "GL_ACCOUNT";
	public final static String POSTING_DATE = "POSTING_DATE";
	public final static String AMOUNT = "AMOUNT";
	public final static String TEXT = "TEXT";
	public final static String BUSINESS_AREA = "BUSINESS_AREA";

	private final CoreDriver _coreDriver;

	private MasterDataIdentity_GLAccount _recAcc;
	private MasterDataIdentity_GLAccount _glAccount;
	private MasterDataIdentity _vendor;
	private Date _date;
	private CurrencyAmount _amount;
	private String _text;
	private MasterDataIdentity _businessArea;
	private boolean _isSaved;
	private HeadEntity _doc;

	public VendorEntry(CoreDriver coreDriver) {
		_coreDriver = coreDriver;
		_text = "";

		_recAcc = null;
		_glAccount = null;
		_vendor = null;
		_date = null;
		_amount = new CurrencyAmount();
		_businessArea = null;

		_doc = null;
	}

	/**
	 * set vendor
	 * 
	 * @param vendor
	 * @throws NotInValueRangeException
	 */
	public void setVendor(MasterDataIdentity vendor)
			throws NotInValueRangeException {
		if (vendor == null) {
			throw new NotInValueRangeException(VENDOR, "");
		}

		MasterDataManagement master = _coreDriver.getMasterDataManagement();
		if (!master.containsMasterData(vendor, MasterDataType.VENDOR)) {
			throw new NotInValueRangeException(VENDOR, vendor);
		}

		_vendor = vendor;
	}

	/**
	 * set G/L account
	 * 
	 * @param glAccount
	 * @throws NoFieldNameException
	 */
	public void setGLAccount(MasterDataIdentity_GLAccount glAccount)
			throws NotInValueRangeException, NoFieldNameException {
		if (glAccount == null) {
			throw new NotInValueRangeException(GL_ACCOUNT, "");
		}

		Object[] valueSet = this.getValueSet(GL_ACCOUNT);
		for (Object obj : valueSet) {
			GLAccountMasterData glAcc = (GLAccountMasterData) obj;
			if (glAcc.getGLIdentity().equals(glAccount)) {
				_glAccount = glAccount;
				return;
			}
		}

		throw new NotInValueRangeException(GL_ACCOUNT, glAccount);
	}

	/**
	 * set business area
	 * 
	 * @param businessArea
	 */
	public void setBusinessArea(MasterDataIdentity businessArea)
			throws NotInValueRangeException {
		if (businessArea == null) {
			throw new NotInValueRangeException(BUSINESS_AREA, "");
		}

		MasterDataManagement master = _coreDriver.getMasterDataManagement();
		if (!master.containsMasterData(businessArea,
				MasterDataType.BUSINESS_AREA)) {
			throw new NotInValueRangeException(BUSINESS_AREA, businessArea);
		}

		_businessArea = businessArea;
	}

	/**
	 * set reconciliation account
	 * 
	 * @param recAcc
	 * @throws NotInValueRangeException
	 * @throws NoFieldNameException
	 */
	public void setRecAccount(MasterDataIdentity_GLAccount recAcc)
			throws NotInValueRangeException, NoFieldNameException {
		if (recAcc == null) {
			throw new NotInValueRangeException(REC_ACC, "");
		}

		Object[] valueSet = this.getValueSet(REC_ACC);
		for (Object obj : valueSet) {
			GLAccountMasterData glAccount = (GLAccountMasterData) obj;
			if (glAccount.getGLIdentity().equals(recAcc)) {
				_recAcc = recAcc;
				return;
			}
		}

		throw new NotInValueRangeException(REC_ACC, recAcc);
	}

	public void setValue(String fieldName, Object value)
			throws NoFieldNameException, NotInValueRangeException {
		if (value == null) {
			throw new NotInValueRangeException(fieldName, "");
		}

		if (fieldName.equals(VENDOR)) {
			if (!(value instanceof MasterDataIdentity)) {
				throw new NotInValueRangeException(fieldName, value);
			}
			MasterDataIdentity vendor = (MasterDataIdentity) value;
			setVendor(vendor);
		} else if (fieldName.equals(GL_ACCOUNT)) {
			if (!(value instanceof MasterDataIdentity_GLAccount)) {
				throw new NotInValueRangeException(fieldName, value);
			}
			MasterDataIdentity_GLAccount glAccount = (MasterDataIdentity_GLAccount) value;
			setGLAccount(glAccount);
		} else if (fieldName.equals(REC_ACC)) {
			if (!(value instanceof MasterDataIdentity_GLAccount)) {
				throw new NotInValueRangeException(fieldName, value);
			}
			MasterDataIdentity_GLAccount recAcc = (MasterDataIdentity_GLAccount) value;
			setRecAccount(recAcc);
		} else if (fieldName.equals(POSTING_DATE)) {
			if (!(value instanceof Date)) {
				throw new NotInValueRangeException(fieldName, value);
			}
			Date date = (Date) value;
			_date = date;
		} else if (fieldName.equals(AMOUNT)) {
			try {
				CurrencyAmount amount = CurrencyAmount.parse(value.toString());
				if (amount.isZero() || amount.isNegative()) {
					throw new NotInValueRangeException(fieldName, value);
				}
				_amount = amount;
			} catch (CurrencyAmountFormatException e) {
				throw new NotInValueRangeException(fieldName, value);
			}
		} else if (fieldName.equals(TEXT)) {
			_text = value.toString();
		} else if (fieldName.equals(BUSINESS_AREA)) {
			if (!(value instanceof MasterDataIdentity)) {
				throw new NotInValueRangeException(fieldName, value);
			}
			MasterDataIdentity businessArea = (MasterDataIdentity) value;
			setBusinessArea(businessArea);
		} else {
			throw new NoFieldNameException(fieldName);
		}
	}

	public Object getValue(String fieldName) throws NoFieldNameException {
		if (fieldName.equals(VENDOR)) {
			return _vendor;
		} else if (fieldName.equals(GL_ACCOUNT)) {
			return _glAccount;
		} else if (fieldName.equals(REC_ACC)) {
			return _recAcc;
		} else if (fieldName.equals(POSTING_DATE)) {
			return _date;
		} else if (fieldName.equals(AMOUNT)) {
			return new CurrencyAmount(_amount);
		} else if (fieldName.equals(TEXT)) {
			return _text;
		} else if (fieldName.equals(BUSINESS_AREA)) {
			return _businessArea;
		}
		throw new NoFieldNameException(fieldName);
	}

	public Object getDefaultValue(String fieldName) throws NoFieldNameException {
		// TODO Auto-generated method stub
		return null;
	}

	public void checkBeforeSave() throws MandatoryFieldIsMissing {
		if (_recAcc == null) {
			throw new MandatoryFieldIsMissing(REC_ACC);
		}

		if (_glAccount == null) {
			throw new MandatoryFieldIsMissing(GL_ACCOUNT);
		}

		if (_vendor == null) {
			throw new MandatoryFieldIsMissing(VENDOR);
		}

		if (_date == null) {
			throw new MandatoryFieldIsMissing(POSTING_DATE);
		}

		if (_amount.isZero() || _amount.isNegative()) {
			throw new MandatoryFieldIsMissing(AMOUNT);
		}

		if (_businessArea == null) {
			throw new MandatoryFieldIsMissing(BUSINESS_AREA);
		}
	}

	public void save(boolean store) throws MandatoryFieldIsMissing {
		// check before save
		checkBeforeSave();

		try {
			HeadEntity doc = new HeadEntity(_coreDriver,
					_coreDriver.getMasterDataManagement());
			doc.setDocText(_text);
			doc.setDocumentType(DocumentType.VENDOR_INVOICE);
			doc.setPostingDate(_date);

			// credit item
			ItemEntity creditItem = doc.createEntity();
			creditItem.setAmount(CreditDebitIndicator.CREDIT, _amount);
			creditItem.setVendor(_vendor, _recAcc);

			// debit item
			ItemEntity debitItem = doc.createEntity();
			debitItem.setAmount(CreditDebitIndicator.DEBIT, _amount);
			debitItem.setGLAccount(_glAccount);
			debitItem.setBusinessArea(_businessArea);

			boolean ret = doc.save(store);
			if (ret) {
				_isSaved = true;
				_doc = doc;
			}
		} catch (NullValueNotAcceptable e) {
			throw new SystemException(e);
		} catch (MasterDataIdentityNotDefined e) {
			throw new SystemException(e);
		}
	}

	public boolean isSaved() {
		return _isSaved;
	}

	public HeadEntity getDocument() {
		if (_isSaved) {
			return _doc;
		}

		return null;
	}

	public Object[] getValueSet(String fieldName) throws NoFieldNameException {
		if (fieldName.equals(GL_ACCOUNT)) {
			MasterDataManagement manage = _coreDriver.getMasterDataManagement();
			return manage.getCostAccounts();
		} else if (fieldName.equals(REC_ACC)) {
			MasterDataManagement manage = _coreDriver.getMasterDataManagement();
			return manage.getBalanceAccounts();
		}
		throw new NoFieldNameException(fieldName);
	}

}
