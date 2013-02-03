package com.jasonzqshen.familyaccounting.core.transaction;

import org.w3c.dom.Element;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.IdentityInvalidChar;
import com.jasonzqshen.familyaccounting.core.exception.IdentityNoData;
import com.jasonzqshen.familyaccounting.core.exception.IdentityTooLong;
import com.jasonzqshen.familyaccounting.core.exception.MandatoryFieldIsMissing;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityNotDefined;
import com.jasonzqshen.familyaccounting.core.exception.NullValueNotAcceptable;
import com.jasonzqshen.familyaccounting.core.exception.format.CurrencyAmountFormatException;
import com.jasonzqshen.familyaccounting.core.exception.format.TransactionDataFileFormatException;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataBase;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;
import com.jasonzqshen.familyaccounting.core.utils.AccountType;
import com.jasonzqshen.familyaccounting.core.utils.CreditDebitIndicator;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;
import com.jasonzqshen.familyaccounting.core.utils.MessageType;
import com.jasonzqshen.familyaccounting.core.utils.StringUtility;

public class ItemEntity implements Comparable<ItemEntity> {
	public final CoreDriver _coreDriver;
	public final MasterDataManagement _management;

	private final HeadEntity _head;
	private final int _lineNum;
	private MasterDataIdentity_GLAccount _glAccount;
	private MasterDataIdentity _customer;
	private MasterDataIdentity _vendor;
	private CurrencyAmount _amount;
	private CreditDebitIndicator _cdIndicator;
	private MasterDataIdentity _businessArea;
	boolean _isSaved = false;

	/**
	 * the value is based on the G/L account, customer and vendor
	 */
	private AccountType _type;

	/**
	 * constructor, inner invoked
	 * 
	 * @param head
	 * @param lineNum
	 */
	ItemEntity(CoreDriver coreDriver, MasterDataManagement management,
			HeadEntity head, int lineNum) {
		_coreDriver = coreDriver;
		_management = management;

		_head = head;
		_lineNum = lineNum;

		_type = null;
		_glAccount = null;
		_customer = null;
		_vendor = null;
		_amount = new CurrencyAmount();
		_businessArea = null;
		_cdIndicator = null;
	}

	/**
	 * get head
	 * 
	 * @return
	 */
	public HeadEntity getHead() {
		return _head;
	}

	/**
	 * get line number
	 * 
	 * @return
	 */
	public int getLineNum() {
		return _lineNum;
	}

	/**
	 * set G/L account
	 * 
	 * @param glAccount
	 * @throws NullValueNotAcceptable
	 *             GL Account value cannot be null.
	 * @throws MasterDataIdentityNotDefined
	 *             GL account must be defined
	 */
	public boolean setGLAccount(MasterDataIdentity_GLAccount glAccount)
			throws NullValueNotAcceptable, MasterDataIdentityNotDefined {
		if (_isSaved) {
			return false;
		}

		// check G/L account
		if (glAccount == null) {
			throw new NullValueNotAcceptable("G/L account");
		}
		MasterDataBase accountId = _management.getMasterData(glAccount,
				MasterDataType.GL_ACCOUNT);
		if (accountId == null) {
			throw new MasterDataIdentityNotDefined(glAccount,
					MasterDataType.GL_ACCOUNT);
		}

		_type = AccountType.GL_ACCOUNT;
		_glAccount = glAccount;
		_vendor = null;
		_customer = null;

		return true;
	}

	/**
	 * 
	 * @return
	 */
	public MasterDataIdentity_GLAccount getGLAccount() {
		return _glAccount;
	}

	/**
	 * 
	 * @param customer
	 * @param glAccount
	 * @throws NullValueNotAcceptable
	 * @throws MasterDataIdentityNotDefined
	 */
	public boolean setCustomer(MasterDataIdentity customer,
			MasterDataIdentity_GLAccount glAccount)
			throws NullValueNotAcceptable, MasterDataIdentityNotDefined {
		if (_isSaved) {
			return false;
		}

		// check customer
		if (customer == null) {
			throw new NullValueNotAcceptable("Customer");
		}
		MasterDataBase customerId = _management.getMasterData(customer,
				MasterDataType.CUSTOMER);
		if (customerId == null) {
			throw new MasterDataIdentityNotDefined(customer,
					MasterDataType.CUSTOMER);
		}

		// check G/L account
		if (glAccount == null) {
			throw new NullValueNotAcceptable("G/L account");
		}
		MasterDataIdentity accountId = _management.getMasterData(glAccount,
				MasterDataType.GL_ACCOUNT).getIdentity();
		if (accountId == null) {
			throw new MasterDataIdentityNotDefined(glAccount,
					MasterDataType.GL_ACCOUNT);
		}

		_type = AccountType.CUSTOMER;
		_glAccount = glAccount;
		_customer = customer;
		_vendor = null;

		return true;
	}

	/**
	 * 
	 * @return
	 */
	public MasterDataIdentity getCustomer() {
		return _customer;
	}

	/**
	 * 
	 * @param vendor
	 * @param glAccount
	 * @throws NullValueNotAcceptable
	 * @throws MasterDataIdentityNotDefined
	 */
	public boolean setVendor(MasterDataIdentity vendor,
			MasterDataIdentity_GLAccount glAccount)
			throws NullValueNotAcceptable, MasterDataIdentityNotDefined {
		if (_isSaved) {
			return false;
		}

		// check customer
		if (vendor == null) {
			throw new NullValueNotAcceptable("Vendor");
		}
		MasterDataBase vendorId = _management.getMasterData(vendor,
				MasterDataType.VENDOR);
		if (vendorId == null) {
			throw new MasterDataIdentityNotDefined(vendor,
					MasterDataType.VENDOR);
		}

		// check G/L account
		if (glAccount == null) {
			throw new NullValueNotAcceptable("G/L account");
		}
		MasterDataBase data = _management.getMasterData(glAccount,
                MasterDataType.GL_ACCOUNT);
		if (data == null) {
            throw new MasterDataIdentityNotDefined(glAccount,
                    MasterDataType.GL_ACCOUNT);
        }
		
		MasterDataIdentity accountId = data.getIdentity();
		if (accountId == null) {
			throw new MasterDataIdentityNotDefined(glAccount,
					MasterDataType.GL_ACCOUNT);
		}

		_type = AccountType.VENDOR;
		_glAccount = glAccount;
		_vendor = vendor;
		_customer = null;

		return true;
	}

	/**
	 * 
	 * @return
	 */
	public MasterDataIdentity getVendor() {
		return _vendor;
	}

	/**
	 * get account type
	 * 
	 * @return
	 */
	public AccountType getAccountType() {
		return _type;
	}

	/**
	 * 
	 * @param amount
	 *            amount should positive number or zero
	 */
	public boolean setAmount(CreditDebitIndicator indicator,
			CurrencyAmount amount) {
		if (_isSaved) {
			return false;
		}

		if (indicator == null || amount == null) {
			return false;
		}

		if (amount.isNegative()) {
			return false;
		}
		_cdIndicator = indicator;
		_amount.set(amount);
		return true;
	}

	/**
	 * 
	 * @return
	 */
	public CurrencyAmount getAmount() {
		return new CurrencyAmount(this._amount);
	}

	/**
	 * 
	 * @return
	 */
	public CreditDebitIndicator getCDIndicator() {
		return _cdIndicator;
	}

	/**
	 * set business area
	 * 
	 * @param businessArea
	 * @throws MasterDataIdentityNotDefined
	 */
	public boolean setBusinessArea(MasterDataIdentity businessArea)
			throws MasterDataIdentityNotDefined {
		if (_isSaved) {
			return false;
		}

		if (businessArea == null) {
			_businessArea = null;
			return true;
		}

		MasterDataBase accountId = _management.getMasterData(businessArea,
				MasterDataType.BUSINESS_AREA);
		if (accountId == null) {
			throw new MasterDataIdentityNotDefined(businessArea,
					MasterDataType.BUSINESS_AREA);
		}

		_businessArea = businessArea;
		return true;
	}

	/**
	 * get business area
	 * 
	 * @return
	 */
	public MasterDataIdentity getBusinessArea() {
		return _businessArea;
	}

	/**
	 * 
	 * @return
	 * @throws MandatoryFieldIsMissing
	 */
	public void checkMandatory() throws MandatoryFieldIsMissing {

		// check account
		if (_type == null) {
			_coreDriver.logDebugInfo(this.getClass(), 319,
					"Check line item before save, account type is null.",
					MessageType.ERROR);
			throw new MandatoryFieldIsMissing("Account Type");
		}

		if (_type == AccountType.GL_ACCOUNT) {
			if (!(_glAccount != null && _customer == null && _vendor == null)) {
				_coreDriver
						.logDebugInfo(
								this.getClass(),
								319,
								"Check line item before save, account error when account type is G/L account.",
								MessageType.ERROR);
				throw new MandatoryFieldIsMissing("G/L Account");
			}
		} else if (_type == AccountType.CUSTOMER) {
			if (!(_glAccount != null && _customer != null && _vendor == null)) {
				_coreDriver
						.logDebugInfo(
								this.getClass(),
								319,
								"Check line item before save, account error when account type is customer.",
								MessageType.ERROR);
				throw new MandatoryFieldIsMissing("Customer");
			}
		} else {
			if (!(_glAccount != null && _customer == null && _vendor != null)) {
				_coreDriver
						.logDebugInfo(
								this.getClass(),
								319,
								"Check line item before save, account error when account type is vendor.",
								MessageType.ERROR);
				throw new MandatoryFieldIsMissing("Vendor");
			}
		}

		// check amount
		if (_cdIndicator == null) {
			_coreDriver
					.logDebugInfo(
							this.getClass(),
							319,
							"Check line item before save, credit/debit indicator is null.",
							MessageType.ERROR);
			throw new MandatoryFieldIsMissing("Credit/Debit Indicator");
		}

		if (_amount.isZero() || _amount.isNegative()) {
			_coreDriver.logDebugInfo(this.getClass(), 319,
					"Check line item before save, amount <= 0.",
					MessageType.ERROR);
			throw new MandatoryFieldIsMissing("Amount");
		}

		_coreDriver.logDebugInfo(this.getClass(), 319, String.format(
				"Check line item %d before save successfully", _lineNum),
				MessageType.INFO);
	}

	/**
	 * Parse XML to item
	 * 
	 * @return
	 * @throws TransactionDataFileFormatException
	 */
	public static ItemEntity parse(CoreDriver coreDriver,
			MasterDataManagement management, HeadEntity head, Element elem)
			throws TransactionDataFileFormatException {
		// line number
		String lineNumStr = elem.getAttribute(TransDataUtils.XML_LINE_NUM);
		if (StringUtility.isNullOrEmpty(lineNumStr)) {
			coreDriver.logDebugInfo(HeadEntity.class, 363, String.format(
					"Field %s is missing in.", TransDataUtils.XML_LINE_NUM),
					MessageType.ERROR);
			throw new TransactionDataFileFormatException("");
		}

		// account type
		String typeStr = elem.getAttribute(TransDataUtils.XML_ACCOUNT_TYPE);
		if (StringUtility.isNullOrEmpty(typeStr)) {
			coreDriver
					.logDebugInfo(HeadEntity.class, 375, String.format(
							"Field %s is missing in.",
							TransDataUtils.XML_ACCOUNT_TYPE), MessageType.ERROR);
			throw new TransactionDataFileFormatException("");
		}

		// amount
		String amountStr = elem.getAttribute(TransDataUtils.XML_AMOUNT);
		if (StringUtility.isNullOrEmpty(amountStr)) {
			coreDriver.logDebugInfo(HeadEntity.class, 375, String.format(
					"Field %s is missing in.", TransDataUtils.XML_AMOUNT),
					MessageType.ERROR);
			throw new TransactionDataFileFormatException("");
		}

		// credit debit indicator
		String cdIndStr = elem.getAttribute(TransDataUtils.XML_CD_INDICATOR);
		if (StringUtility.isNullOrEmpty(cdIndStr)) {
			coreDriver
					.logDebugInfo(HeadEntity.class, 375, String.format(
							"Field %s is missing in.",
							TransDataUtils.XML_CD_INDICATOR), MessageType.ERROR);
			throw new TransactionDataFileFormatException("");
		}

		// G/L account
		String glAccountStr = elem.getAttribute(TransDataUtils.XML_GL_ACCOUNT);
		if (StringUtility.isNullOrEmpty(glAccountStr)) {
			coreDriver.logDebugInfo(HeadEntity.class, 414, String.format(
					"Field %s is missing in.", TransDataUtils.XML_GL_ACCOUNT),
					MessageType.ERROR);
			throw new TransactionDataFileFormatException("");
		}

		// vendor
		String vendorStr = elem.getAttribute(TransDataUtils.XML_VENDOR);
		// customer
		String customerStr = elem.getAttribute(TransDataUtils.XML_CUSTOMER);

		String businessAreaStr = elem
				.getAttribute(TransDataUtils.XML_BUSINESS_AREA);

		try {
			int lineNum = Integer.parseInt(lineNumStr);
			ItemEntity newItem = new ItemEntity(coreDriver, management, head,
					lineNum);

			AccountType type = AccountType.parse(typeStr.charAt(0));
			MasterDataIdentity_GLAccount glAccount = new MasterDataIdentity_GLAccount(
					glAccountStr.toCharArray());
			if (type == AccountType.GL_ACCOUNT) {
				newItem.setGLAccount(glAccount);
			} else if (type == AccountType.VENDOR) {
				if (StringUtility.isNullOrEmpty(vendorStr)) {
					coreDriver.logDebugInfo(HeadEntity.class, 414, String
							.format("Field %s is missing in.",
									TransDataUtils.XML_VENDOR),
							MessageType.ERROR);
					throw new TransactionDataFileFormatException("");
				}
				MasterDataIdentity vendorId = new MasterDataIdentity(
						vendorStr.toCharArray());
				newItem.setVendor(vendorId, glAccount);
			} else if (type == AccountType.CUSTOMER) {
				if (StringUtility.isNullOrEmpty(customerStr)) {
					coreDriver.logDebugInfo(HeadEntity.class, 414, String
							.format("Field %s is missing in.",
									TransDataUtils.XML_CUSTOMER),
							MessageType.ERROR);
					throw new TransactionDataFileFormatException("");
				}
				MasterDataIdentity customerId = new MasterDataIdentity(
						customerStr.toCharArray());
				newItem.setCustomer(customerId, glAccount);
			}

			CreditDebitIndicator indicator = CreditDebitIndicator
					.parse(cdIndStr.charAt(0));
			CurrencyAmount amount = CurrencyAmount.parse(amountStr);
			newItem.setAmount(indicator, amount);

			if (StringUtility.isNullOrEmpty(businessAreaStr) == false) {
				newItem.setBusinessArea(new MasterDataIdentity(businessAreaStr
						.toCharArray()));
			}

			coreDriver.logDebugInfo(
					ItemEntity.class,
					455,
					String.format("Parse line Item %d (%s).",
							newItem.getLineNum(), newItem.toXML()),
					MessageType.INFO);
			return newItem;

		} catch (NumberFormatException e) {
			coreDriver.logDebugInfo(ItemEntity.class, 455, e.toString(),
					MessageType.ERROR);
			throw new TransactionDataFileFormatException("");
		} catch (IdentityTooLong e) {
			coreDriver.logDebugInfo(ItemEntity.class, 463, e.toString(),
					MessageType.ERROR);
			throw new TransactionDataFileFormatException("");
		} catch (IdentityNoData e) {
			coreDriver.logDebugInfo(ItemEntity.class, 463, e.toString(),
					MessageType.ERROR);
			throw new TransactionDataFileFormatException("");
		} catch (IdentityInvalidChar e) {
			coreDriver.logDebugInfo(ItemEntity.class, 463, e.toString(),
					MessageType.ERROR);
			throw new TransactionDataFileFormatException("");
		} catch (NullValueNotAcceptable e) {
			coreDriver.logDebugInfo(ItemEntity.class, 463, e.toString(),
					MessageType.ERROR);
			throw new SystemException(e);
		} catch (MasterDataIdentityNotDefined e) {
			coreDriver.logDebugInfo(ItemEntity.class, 463, e.toString(),
					MessageType.ERROR);
			throw new TransactionDataFileFormatException("");
		} catch (CurrencyAmountFormatException e) {
			throw new TransactionDataFileFormatException(e.toString());
		}

	}

	public int compareTo(ItemEntity another) {
		return _lineNum - another._lineNum;
	}

	/**
	 * parse to XML
	 * 
	 * @return
	 */
	public String toXML() {
		StringBuilder strBuilder = new StringBuilder();

		// line number
		strBuilder.append(String.format("%s=\"%d\" ",
				TransDataUtils.XML_LINE_NUM, _lineNum));

		// account type
		strBuilder.append(String.format("%s=\"%s\" ",
				TransDataUtils.XML_ACCOUNT_TYPE, _type.toString()));

		// g/l account
		strBuilder.append(String.format("%s=\"%s\" ",
				TransDataUtils.XML_GL_ACCOUNT, _glAccount.toString()));

		// customer
		if (_customer != null) {
			strBuilder.append(String.format("%s=\"%s\" ",
					TransDataUtils.XML_CUSTOMER, _customer.toString()));
		}

		// vendor
		if (_vendor != null) {
			strBuilder.append(String.format("%s=\"%s\" ",
					TransDataUtils.XML_VENDOR, _vendor.toString()));
		}

		// amount
		strBuilder.append(String.format("%s=\"%s\" ",
				TransDataUtils.XML_AMOUNT, _amount));

		// credit debit indicator
		strBuilder.append(String.format("%s=\"%s\" ",
				TransDataUtils.XML_CD_INDICATOR, _cdIndicator.toString()));

		// business area
		if (_businessArea != null) {
			strBuilder
					.append(String.format("%s=\"%s\" ",
							TransDataUtils.XML_BUSINESS_AREA,
							_businessArea.toString()));

		}

		return strBuilder.toString();
	}
}
