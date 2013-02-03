package com.jasonzqshen.familyaccounting.core.investment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.BalanceNotZero;
import com.jasonzqshen.familyaccounting.core.exception.IdentityInvalidChar;
import com.jasonzqshen.familyaccounting.core.exception.IdentityNoData;
import com.jasonzqshen.familyaccounting.core.exception.IdentityTooLong;
import com.jasonzqshen.familyaccounting.core.exception.MandatoryFieldIsMissing;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityNotDefined;
import com.jasonzqshen.familyaccounting.core.exception.NullValueNotAcceptable;
import com.jasonzqshen.familyaccounting.core.exception.SaveClosedLedgerException;
import com.jasonzqshen.familyaccounting.core.exception.StorageException;
import com.jasonzqshen.familyaccounting.core.exception.format.InvestmentFileFormatException;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;
import com.jasonzqshen.familyaccounting.core.transaction.ItemEntity;
import com.jasonzqshen.familyaccounting.core.utils.CreditDebitIndicator;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;
import com.jasonzqshen.familyaccounting.core.utils.DocumentType;
import com.jasonzqshen.familyaccounting.core.utils.Language;
import com.jasonzqshen.familyaccounting.core.utils.MessageType;
import com.jasonzqshen.familyaccounting.core.utils.StringUtility;
import com.jasonzqshen.familyaccounting.core.utils.XMLTransfer;

public class InvestmentAccount implements Comparable<InvestmentAccount> {
	public static final String XML_ACCOUNT = "account";

	public static final String XML_REV_ACCOUNT = "revenue_account";

	public static final String XML_ROOT = "root";

	public static final String XML_NAME = "name";

	public static final String XML_ITEM = "investement";

	private final CoreDriver _coreDriver;

	private final MasterDataIdentity_GLAccount _account;

	public static final String START_DOC_DESCP = "start of investment, ";

	public static final String END_DOC_DESCP = "end of investment, ";

	/**
	 * revenue account
	 */
	private final MasterDataIdentity_GLAccount _revAccount;

	private final String _name;

	private final ArrayList<InvestmentItem> _items;

	private final InvestmentManagement _investMgmt;

	/**
	 * investment account
	 * 
	 * @param account
	 * @param revAccount
	 * @param name
	 */
	InvestmentAccount(CoreDriver coreDriver, InvestmentManagement investMgmt,
			MasterDataIdentity_GLAccount account,
			MasterDataIdentity_GLAccount revAccount, String name) {
		_coreDriver = coreDriver;
		_account = account;
		_revAccount = revAccount;
		_name = name;
		_investMgmt = investMgmt;

		_items = new ArrayList<InvestmentItem>();
	}

	/**
	 * get count of investments item
	 * 
	 * @return
	 */
	public int getItemCount() {
		return _items.size();
	}

	/**
	 * get investment items
	 * 
	 * @return
	 */
	public ArrayList<InvestmentItem> getItems() {
		ArrayList<InvestmentItem> ret = new ArrayList<InvestmentItem>(_items);
		Collections.sort(ret);

		return ret;
	}

	/**
	 * get investment account
	 * 
	 * @return
	 */
	public MasterDataIdentity_GLAccount getAccount() {
		return _account;
	}

	/**
	 * get revenue account
	 * 
	 * @return
	 */
	public MasterDataIdentity_GLAccount getRevAccount() {
		return _revAccount;
	}

	/**
	 * get the name of investment account
	 * 
	 * @return
	 */
	public String getName() {
		return _name;
	}

	/**
	 * get total amount of the account
	 * 
	 * @return
	 */
	public CurrencyAmount getTotalAmount() {
		CurrencyAmount amount = new CurrencyAmount();
		for (InvestmentItem item : _items) {
			if (item.isClosed()) {
				continue;
			}

			amount.addTo(item.getAmount());
		}
		return amount;
	}

	/**
	 * get revenue amount
	 * 
	 * @return
	 */
	public CurrencyAmount getRevAmount() {
		CurrencyAmount amount = new CurrencyAmount();
		for (InvestmentItem item : _items) {
			if (item.isClosed()) {
				amount.addTo(item.getRevAmount());
			}
		}
		return amount;
	}

	/**
	 * 
	 * @param startDate
	 * @param dueDate
	 * @param srcAccount
	 * @param srcAmount
	 * @throws MasterDataIdentityNotDefined
	 */
	public InvestmentItem createInvestment(Date startDate, Date dueDate,
			MasterDataIdentity_GLAccount srcAccount, CurrencyAmount srcAmount)
			throws MasterDataIdentityNotDefined {
		// check source amount
		if (srcAmount.isNegative() || srcAmount.isZero()) {
			return null;
		}

		// create head entity
		HeadEntity headEntity = new HeadEntity(_coreDriver,
				_coreDriver.getMasterDataManagement());
		headEntity.setPostingDate(startDate);
		headEntity.setDocText(START_DOC_DESCP + _name);
		headEntity.setDocumentType(DocumentType.GL);
		try {
			// create the source item
			ItemEntity srcItem = headEntity.createEntity();
			srcItem.setAmount(CreditDebitIndicator.CREDIT, srcAmount);
			srcItem.setGLAccount(srcAccount);

			ItemEntity dstItem = headEntity.createEntity();
			dstItem.setAmount(CreditDebitIndicator.DEBIT, srcAmount);
			dstItem.setGLAccount(_account);
		} catch (NullValueNotAcceptable e) {
			_coreDriver.logDebugInfo(this.getClass(), 164, e.toString(),
					MessageType.ERROR);
			throw new SystemException(e);
		}

		try {
			headEntity.save(true);
		} catch (SaveClosedLedgerException e) {
			_coreDriver.logDebugInfo(this.getClass(), 255,
					"Save in closed ledger.", MessageType.ERROR);
			throw new SystemException(e);
		} catch (MandatoryFieldIsMissing e) {
			_coreDriver.logDebugInfo(this.getClass(), 259,
					"Mandatory field is missing", MessageType.ERROR);
			throw new SystemException(e);
		} catch (BalanceNotZero e) {
			_coreDriver.logDebugInfo(this.getClass(), 261,
					"Balance is not zero", MessageType.ERROR);
			throw new SystemException(e);
		} catch (StorageException e) {
			_coreDriver.logDebugInfo(this.getClass(), 265,
					"Dirty data is not in file system", MessageType.WARNING);
		}

		// create investment item
		InvestmentItem investItem = new InvestmentItem(_coreDriver, this,
				startDate, dueDate, headEntity.getDocIdentity());
		this.addItem(investItem);

		this.store();

		return investItem;
	}

	@Override
	public int compareTo(InvestmentAccount another) {
		return _account.compareTo(another._account);
	}

	/**
	 * parse investment account to XML
	 * 
	 * @return
	 */
	public String toXML() {
		StringBuilder strBuilder = new StringBuilder();
		// append root
		strBuilder.append(String.format("%s%s ", XMLTransfer.BEGIN_TAG_LEFT,
				XML_ROOT));
		strBuilder.append(String.format("%s=\"%s\" %s=\"%s\" %s=\"%s\" ",
				XML_ACCOUNT, _account, XML_REV_ACCOUNT, _revAccount, XML_NAME,
				_name));
		strBuilder.append(String.format("%s", XMLTransfer.BEGIN_TAG_RIGHT));
		// append items
		for (InvestmentItem item : _items) {
			strBuilder.append(item.toXML());
		}

		// append the end tag
		strBuilder.append(String.format("%s%s %s", XMLTransfer.END_TAG_LEFT,
				XML_ROOT, XMLTransfer.END_TAG_RIGHT));
		return strBuilder.toString();
	}

	/**
	 * store investment information
	 * 
	 * @return
	 */
	public boolean store() {
		String folderPath = _investMgmt.getInvestFolder();
		String filePath = String.format("%s/%s.xml", folderPath,
				_account.toString());

		File file = new File(filePath);
		try {
			FileWriter writer = new FileWriter(file);
			String header = null;
			Language lang = _coreDriver.getLanguage();
			if (lang == Language.Engilish) {
				header = Language.ENGLISH_XML_HEADER;
			} else if (lang == Language.SimpleChinese) {
				header = Language.SIMPLE_CHINESE_XML_HEADER;
			}
			writer.write(header, 0, header.length());
			writer.write(this.toXML());
			writer.close();
		} catch (IOException e) {
			_coreDriver.logDebugInfo(this.getClass(), 258, e.toString(),
					MessageType.ERROR);
			throw new SystemException(e);
		}
		return true;
	}

	/**
	 * add investment item
	 * 
	 * @param item
	 */
	void addItem(InvestmentItem item) {
		_items.add(item);
	}

	/**
	 * parse investment document to memory
	 * 
	 * @param doc
	 * @return
	 * @throws InvestmentFileFormatException
	 */
	public static InvestmentAccount parse(CoreDriver coreDriver,
			InvestmentManagement investMgmt, Document doc)
			throws InvestmentFileFormatException {
		// get root element
		NodeList nodeList = doc.getChildNodes();
		Element rootElem = null;

		// get root element
		for (int i = 0; i < nodeList.getLength(); ++i) {
			Node child = nodeList.item(i);
			if (child instanceof Element) {
				Element elem = (Element) child;
				String nodeName = elem.getNodeName();
				if (nodeName.equals(XML_ROOT)) {
					rootElem = elem;
					break;
				}
			}
		}
		// no root element
		if (rootElem == null) {
			coreDriver.logDebugInfo(InvestmentAccount.class, 155,
					"No root element", MessageType.ERROR);
			throw new InvestmentFileFormatException("No root element.");
		}

		String accStr = rootElem.getAttribute(XML_ACCOUNT);
		String revAccStr = rootElem.getAttribute(XML_REV_ACCOUNT);
		String name = rootElem.getAttribute(XML_NAME);

		try {
			MasterDataIdentity_GLAccount acc = new MasterDataIdentity_GLAccount(
					accStr);
			MasterDataIdentity_GLAccount revAcc = new MasterDataIdentity_GLAccount(
					revAccStr);

			if (StringUtility.isNullOrEmpty(name)) {
				coreDriver
						.logDebugInfo(InvestmentAccount.class, 171,
								"Name of investment cannot be empty",
								MessageType.ERROR);
				throw new InvestmentFileFormatException(
						"Name of investment cannot be empty");
			}

			InvestmentAccount investAccount = new InvestmentAccount(coreDriver,
					investMgmt, acc, revAcc, name);

			nodeList = rootElem.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); ++i) {
				Node child = nodeList.item(i);
				if (child instanceof Element) {
					Element elem = (Element) child;
					if (elem.getNodeName().equals(XML_ITEM)) {
						InvestmentItem item = InvestmentItem.parse(coreDriver,
								investAccount, elem);
						investAccount.addItem(item);
					}
				}
			}

			return investAccount;
		} catch (IdentityTooLong e) {
			coreDriver.logDebugInfo(InvestmentAccount.class, 171, e.toString(),
					MessageType.ERROR);
			throw new InvestmentFileFormatException(e.toString());
		} catch (IdentityNoData e) {
			coreDriver.logDebugInfo(InvestmentAccount.class, 175, e.toString(),
					MessageType.ERROR);
			throw new InvestmentFileFormatException(e.toString());
		} catch (IdentityInvalidChar e) {
			coreDriver.logDebugInfo(InvestmentAccount.class, 179, e.toString(),
					MessageType.ERROR);
			throw new InvestmentFileFormatException(e.toString());
		}

	}

}
