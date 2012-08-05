package com.jasonzqshen.familyaccounting.core.transaction;

import org.w3c.dom.Element;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.IdentityInvalidChar;
import com.jasonzqshen.familyaccounting.core.exception.IdentityNoData;
import com.jasonzqshen.familyaccounting.core.exception.IdentityTooLong;
import com.jasonzqshen.familyaccounting.core.exception.MandatoryFieldIsMissing;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityNotDefined;
import com.jasonzqshen.familyaccounting.core.exception.NullValueNotAcceptable;
import com.jasonzqshen.familyaccounting.core.exception.SystemException;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataBase;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;
import com.jasonzqshen.familyaccounting.core.utils.AccountType;
import com.jasonzqshen.familyaccounting.core.utils.CreditDebitIndicator;
import com.jasonzqshen.familyaccounting.core.utils.StringUtility;
import com.jasonzqshen.familyaccounting.core.utils.XMLTransfer;

public class ItemEntity implements Comparable<ItemEntity> {
	public final CoreDriver _coreDriver;
	private final HeadEntity _head;
	private final int _lineNum;
	private MasterDataIdentity_GLAccount _glAccount;
	private MasterDataIdentity _customer;
	private MasterDataIdentity _vendor;
	private double _amount;
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
	ItemEntity(CoreDriver coreDriver, HeadEntity head, int lineNum) {
		_coreDriver = coreDriver;

		_head = head;
		_lineNum = lineNum;

		_type = null;
		_glAccount = null;
		_customer = null;
		_vendor = null;
		_amount = 0;
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
		MasterDataManagement management = _coreDriver.getMasterDataManagement();
		MasterDataBase accountId = management.getMasterData(glAccount,
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
		MasterDataManagement management = _coreDriver.getMasterDataManagement();
		MasterDataBase customerId = management.getMasterData(customer,
				MasterDataType.CUSTOMER);
		if (customerId == null) {
			throw new MasterDataIdentityNotDefined(customer,
					MasterDataType.CUSTOMER);
		}

		// check G/L account
		if (glAccount == null) {
			throw new NullValueNotAcceptable("G/L account");
		}
		MasterDataIdentity accountId = management.getMasterData(glAccount,
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
		MasterDataManagement management = _coreDriver.getMasterDataManagement();
		MasterDataBase vendorId = management.getMasterData(vendor,
				MasterDataType.VENDOR);
		if (vendorId == null) {
			throw new MasterDataIdentityNotDefined(vendor,
					MasterDataType.VENDOR);
		}

		// check G/L account
		if (glAccount == null) {
			throw new NullValueNotAcceptable("G/L account");
		}
		MasterDataIdentity accountId = management.getMasterData(glAccount,
				MasterDataType.GL_ACCOUNT).getIdentity();
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
	public boolean setAmount(CreditDebitIndicator indicator, double amount) {
		if (_isSaved) {
			return false;
		}

		if (amount < 0) {
			return false;
		}
		_cdIndicator = indicator;
		_amount = amount;
		return true;
	}

	/**
	 * 
	 * @return
	 */
	public double getAmount() {
		return _amount;
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

		MasterDataManagement management = _coreDriver.getMasterDataManagement();
		MasterDataBase accountId = management.getMasterData(businessArea,
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
	public boolean checkMandatory() throws MandatoryFieldIsMissing {

		// check account
		if (_type == null) {
			throw new MandatoryFieldIsMissing("Account Type");
		}

		if (_type == AccountType.GL_ACCOUNT) {
			if (!(_glAccount != null && _customer == null && _vendor == null)) {
				throw new MandatoryFieldIsMissing("G/L Account");
			}
		} else if (_type == AccountType.CUSTOMER) {
			if (!(_glAccount != null && _customer != null && _vendor == null)) {
				throw new MandatoryFieldIsMissing("Customer");
			}
		} else {
			if (!(_glAccount != null && _customer == null && _vendor != null)) {
				throw new MandatoryFieldIsMissing("Vendor");
			}
		}

		// check amount
		if (_cdIndicator == null) {
			throw new MandatoryFieldIsMissing("Credit/Debit Indicator");
		}
		int amount = (int) (_amount * 100);
		if (amount * 100 == 0) {
			throw new MandatoryFieldIsMissing("Amount");
		}

		return true;
	}

	/**
	 * Parse XML to item
	 * 
	 * @return
	 */
	public static ItemEntity parse(CoreDriver coreDriver, HeadEntity head,
			Element elem) throws MandatoryFieldIsMissing, SystemException {
		String lineNumStr = elem.getAttribute(TransDataUtils.XML_LINE_NUM);
		String typeStr = elem.getAttribute(TransDataUtils.XML_ACCOUNT_TYPE);
		String glAccountStr = elem.getAttribute(TransDataUtils.XML_GL_ACCOUNT);
		String vendorStr = elem.getAttribute(TransDataUtils.XML_VENDOR);
		String customerStr = elem.getAttribute(TransDataUtils.XML_CUSTOMER);
		String amountStr = elem.getAttribute(TransDataUtils.XML_AMOUNT);
		String cdIndStr = elem.getAttribute(TransDataUtils.XML_CD_INDICATOR);
		String businessAreaStr = elem
				.getAttribute(TransDataUtils.XML_BUSINESS_AREA);
		// check mandatory
		if (StringUtility.isNullOrEmpty(lineNumStr)) {
			throw new MandatoryFieldIsMissing("Line Number");
		}
		if (StringUtility.isNullOrEmpty(typeStr)) {
			throw new MandatoryFieldIsMissing("Account Type");
		}
		if (StringUtility.isNullOrEmpty(glAccountStr)) {
			throw new MandatoryFieldIsMissing("G/L account");
		}
		if (StringUtility.isNullOrEmpty(amountStr)) {
			throw new MandatoryFieldIsMissing("Amount");
		}
		if (StringUtility.isNullOrEmpty(cdIndStr)) {
			throw new MandatoryFieldIsMissing("Credit/Debit Indicator");
		}

		try {
			int lineNum = Integer.parseInt(lineNumStr);
			ItemEntity newItem = new ItemEntity(coreDriver, head, lineNum);

			AccountType type = AccountType.parse(typeStr.charAt(0));
			MasterDataIdentity_GLAccount glAccount = new MasterDataIdentity_GLAccount(
					glAccountStr.toCharArray());
			if (type == AccountType.GL_ACCOUNT) {
				newItem.setGLAccount(glAccount);
			} else if (type == AccountType.VENDOR) {
				if (StringUtility.isNullOrEmpty(vendorStr)) {
					throw new MandatoryFieldIsMissing("Vendor");
				}
				MasterDataIdentity vendorId = new MasterDataIdentity(
						vendorStr.toCharArray());
				newItem.setVendor(vendorId, glAccount);
			} else if (type == AccountType.CUSTOMER) {
				if (StringUtility.isNullOrEmpty(customerStr)) {
					throw new MandatoryFieldIsMissing("Customer");
				}
				MasterDataIdentity customerId = new MasterDataIdentity(
						customerStr.toCharArray());
				newItem.setCustomer(customerId, glAccount);
			}

			CreditDebitIndicator indicator = CreditDebitIndicator
					.parse(cdIndStr.charAt(0));
			double amount = Double.parseDouble(amountStr);
			newItem.setAmount(indicator, amount);

			if (StringUtility.isNullOrEmpty(businessAreaStr) == false) {
				newItem.setBusinessArea(new MasterDataIdentity(businessAreaStr
						.toCharArray()));
			}

			return newItem;

		} catch (NumberFormatException e) {
			throw new SystemException(e);
		} catch (IdentityTooLong e) {
			throw new SystemException(e);
		} catch (IdentityNoData e) {
			throw new SystemException(e);
		} catch (IdentityInvalidChar e) {
			throw new SystemException(e);
		} catch (NullValueNotAcceptable e) {
			throw new SystemException(e);
		} catch (MasterDataIdentityNotDefined e) {
			throw new SystemException(e);
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
		strBuilder.append(String.format("%s%s ", XMLTransfer.SINGLE_TAG_LEFT,
				TransDataUtils.XML_ITEM));

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
		strBuilder.append(String.format("%s=\"%f\" ",
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

		strBuilder.append(XMLTransfer.SINGLE_TAG_RIGHT);
		return strBuilder.toString();
	}
}
