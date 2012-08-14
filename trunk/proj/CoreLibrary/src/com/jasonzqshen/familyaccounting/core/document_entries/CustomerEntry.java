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

public class CustomerEntry implements IDocumentEntry {
	public final static String CUSTOMER = "CUSTOMER";
	public final static String REC_ACC = "REC_ACC";
	public final static String GL_ACCOUNT = "GL_ACCOUNT";
	public final static String POSTING_DATE = "POSTING_DATE";
	public final static String AMOUNT = "AMOUNT";
	public final static String TEXT = "TEXT";

	private final CoreDriver _coreDriver;

	private MasterDataIdentity_GLAccount _recAcc;
	private MasterDataIdentity_GLAccount _glAccount;
	private MasterDataIdentity _customer;
	private Date _date;
	private CurrencyAmount _amount;
	private String _text;
	private boolean _isSaved;
	private HeadEntity _doc;

	public CustomerEntry(CoreDriver coreDriver) {
		_coreDriver = coreDriver;
	}

	/**
	 * set customer
	 * 
	 * @param customer
	 * @throws NotInValueRangeException
	 */
	public void setCustomer(MasterDataIdentity customer)
			throws NotInValueRangeException {
		if (customer == null) {
			throw new NotInValueRangeException(CUSTOMER, "");
		}

		MasterDataManagement master = _coreDriver.getMasterDataManagement();
		if (!master.containsMasterData(customer, MasterDataType.CUSTOMER)) {
			throw new NotInValueRangeException(CUSTOMER, customer);
		}

		_customer = customer;
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

		if (fieldName.equals(CUSTOMER)) {
			if (!(value instanceof MasterDataIdentity)) {
				throw new NotInValueRangeException(fieldName, value);
			}
			MasterDataIdentity customer = (MasterDataIdentity) value;
			setCustomer(customer);
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
		} else {
			throw new NoFieldNameException(fieldName);
		}

	}

	public Object getValue(String fieldName) throws NoFieldNameException {
		if (fieldName.equals(CUSTOMER)) {
			return _customer;
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

		if (_customer == null) {
			throw new MandatoryFieldIsMissing(CUSTOMER);
		}

		if (_date == null) {
			throw new MandatoryFieldIsMissing(POSTING_DATE);
		}

		if (_amount.isZero() || _amount.isNegative()) {
			throw new MandatoryFieldIsMissing(AMOUNT);
		}

	}

	public void save(boolean store) throws MandatoryFieldIsMissing {
		checkBeforeSave();

		try {
			HeadEntity doc = new HeadEntity(_coreDriver,
					_coreDriver.getMasterDataManagement());
			doc.setDocText(_text);
			doc.setDocumentType(DocumentType.CUSTOMER_INVOICE);
			doc.setPostingDate(_date);

			// credit item
			ItemEntity creditItem = doc.createEntity();
			creditItem.setAmount(CreditDebitIndicator.CREDIT, _amount);
			creditItem.setGLAccount(_glAccount);

			// debit item
			ItemEntity debitItem = doc.createEntity();
			debitItem.setAmount(CreditDebitIndicator.DEBIT, _amount);
			debitItem.setCustomer(_customer, _recAcc);

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
			return manage.getRevenueAccounts();
		} else if (fieldName.equals(REC_ACC)) {
			MasterDataManagement manage = _coreDriver.getMasterDataManagement();
			return manage.getLiquidityAccounts();
		}
		throw new NoFieldNameException(fieldName);
	}

}
