package com.jasonzqshen.familyaccounting.core.document_entries;

import java.util.Date;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.BalanceNotZero;
import com.jasonzqshen.familyaccounting.core.exception.MandatoryFieldIsMissing;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityNotDefined;
import com.jasonzqshen.familyaccounting.core.exception.NoFieldNameException;
import com.jasonzqshen.familyaccounting.core.exception.NotInValueRangeException;
import com.jasonzqshen.familyaccounting.core.exception.NullValueNotAcceptable;
import com.jasonzqshen.familyaccounting.core.exception.SaveOpenLedgerException;
import com.jasonzqshen.familyaccounting.core.exception.StorageException;
import com.jasonzqshen.familyaccounting.core.exception.format.CurrencyAmountFormatException;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.masterdata.GLAccountMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataBase;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataFactoryBase;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;
import com.jasonzqshen.familyaccounting.core.transaction.ItemEntity;
import com.jasonzqshen.familyaccounting.core.utils.AccountType;
import com.jasonzqshen.familyaccounting.core.utils.CreditDebitIndicator;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;
import com.jasonzqshen.familyaccounting.core.utils.DocumentType;
import com.jasonzqshen.familyaccounting.core.utils.MessageType;

public class CustomerEntry implements IDocumentEntry {
	public final static String CUSTOMER = "CUSTOMER";

	public final static String REC_ACC = "REC_ACC";

	public final static String GL_ACCOUNT = "GL_ACCOUNT";

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
	private void setCustomer(MasterDataIdentity customer)
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
	private void setGLAccount(MasterDataIdentity_GLAccount glAccount)
			throws NotInValueRangeException, NoFieldNameException {
		if (glAccount == null) {
			throw new NotInValueRangeException(GL_ACCOUNT, "");
		}

		Object[] valueSet = this.getValueSet(GL_ACCOUNT);
		for (Object obj : valueSet) {
			GLAccountMasterData glAcc = (GLAccountMasterData) obj;
			if (glAcc.getIdentity().equals(glAccount)) {
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
	private void setRecAccount(MasterDataIdentity_GLAccount recAcc)
			throws NotInValueRangeException, NoFieldNameException {
		if (recAcc == null) {
			throw new NotInValueRangeException(REC_ACC, "");
		}

		Object[] valueSet = this.getValueSet(REC_ACC);
		for (Object obj : valueSet) {
			GLAccountMasterData glAccount = (GLAccountMasterData) obj;
			if (glAccount.getIdentity().equals(recAcc)) {
				_recAcc = recAcc;
				return;
			}
		}

		throw new NotInValueRangeException(REC_ACC, recAcc);
	}

	public void setValue(String fieldName, Object value)
			throws NoFieldNameException, NotInValueRangeException {
		if (_isSaved) {
			return;
		}

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
			if (_amount == null || _amount.isZero()) {
				return null;
			}
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

	public void save(boolean store) throws MandatoryFieldIsMissing,
			SaveOpenLedgerException {
		if (_isSaved) {
			return;
		}
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

			try {
				doc.save(store);
			} catch (StorageException e) {
				_coreDriver.logDebugInfo(this.getClass(), 256,
						"Dirty data is not in file system.",
						MessageType.WARNING);
			}

			_isSaved = true;
			_doc = doc;

		} catch (NullValueNotAcceptable e) {
			_coreDriver.logDebugInfo(this.getClass(), 267,
					"Null value not acceptable.", MessageType.ERROR);
			throw new SystemException(e);
		} catch (MasterDataIdentityNotDefined e) {
			_coreDriver.logDebugInfo(this.getClass(), 267,
					"Master data identity is not defined.", MessageType.ERROR);
			throw new SystemException(e);
		} catch (BalanceNotZero e) {
			_coreDriver.logDebugInfo(this.getClass(), 267,
					"Balance is not zero.", MessageType.ERROR);
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

	public MasterDataBase[] getValueSet(String fieldName)
			throws NoFieldNameException {
		if (fieldName.equals(GL_ACCOUNT)) {
			MasterDataManagement manage = _coreDriver.getMasterDataManagement();
			return manage.getRevenueAccounts();
		} else if (fieldName.equals(REC_ACC)) {
			MasterDataManagement manage = _coreDriver.getMasterDataManagement();
			return manage.getLiquidityAccounts();
		} else if (fieldName.equals(CUSTOMER)) {
			MasterDataManagement manage = _coreDriver.getMasterDataManagement();
			MasterDataFactoryBase factory = manage
					.getMasterDataFactory(MasterDataType.CUSTOMER);
			return factory.getAllEntities();
		}
		throw new NoFieldNameException(fieldName);
	}

	/**
	 * pasrse document to customer entry
	 * 
	 * @param head
	 * @return return null if cannot parse to customer entry.
	 */
	public static CustomerEntry parse(HeadEntity head) {
		// check
		if (head.getDocumentType() != DocumentType.CUSTOMER_INVOICE) {
			return null;
		}
		ItemEntity[] items = head.getItems();
		if (items.length != 2) {
			return null;
		}
		// credit item
		ItemEntity creditItem = items[0];
		if (creditItem.getAccountType() != AccountType.GL_ACCOUNT) {
			return null;
		}
		ItemEntity debitItem = items[1];
		if (debitItem.getAccountType() != AccountType.CUSTOMER) {
			return null;
		}

		CustomerEntry entry = new CustomerEntry(head._coreDriver);
		entry._glAccount = creditItem.getGLAccount();
		entry._recAcc = debitItem.getGLAccount();
		entry._customer = debitItem.getCustomer();
		entry._date = head.getPostingDate();
		entry._amount = creditItem.getAmount();
		if (creditItem.getCDIndicator() == CreditDebitIndicator.DEBIT) {
			// reverse
			entry._amount.negate();
		}
		entry._text = head.getDocText();
		entry._isSaved = true;
		entry._doc = head;

		return entry;
	}
}
