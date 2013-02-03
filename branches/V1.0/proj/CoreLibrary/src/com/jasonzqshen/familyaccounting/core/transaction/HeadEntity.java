package com.jasonzqshen.familyaccounting.core.transaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.BalanceNotZero;
import com.jasonzqshen.familyaccounting.core.exception.FiscalMonthRangeException;
import com.jasonzqshen.familyaccounting.core.exception.FiscalYearRangeException;
import com.jasonzqshen.familyaccounting.core.exception.IdentityInvalidChar;
import com.jasonzqshen.familyaccounting.core.exception.IdentityNoData;
import com.jasonzqshen.familyaccounting.core.exception.IdentityTooLong;
import com.jasonzqshen.familyaccounting.core.exception.MandatoryFieldIsMissing;
import com.jasonzqshen.familyaccounting.core.exception.SaveClosedLedgerException;
import com.jasonzqshen.familyaccounting.core.exception.StorageException;
import com.jasonzqshen.familyaccounting.core.exception.format.TransactionDataFileFormatException;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.utils.CreditDebitIndicator;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;
import com.jasonzqshen.familyaccounting.core.utils.DocumentType;
import com.jasonzqshen.familyaccounting.core.utils.MessageType;
import com.jasonzqshen.familyaccounting.core.utils.StringUtility;
import com.jasonzqshen.familyaccounting.core.utils.XMLTransfer;

public class HeadEntity implements Comparable<HeadEntity> {

	/**
	 * generate when document save.
	 */
	DocumentNumber _docNumber;

	private MonthIdentity _monthId;

	private Date _postingDate;

	private String _docText;

	private DocumentType _type;

	boolean _isReversed;

	public final CoreDriver _coreDriver;

	public final MasterDataManagement _management;

	private final ArrayList<ItemEntity> _items;

	private boolean _isSaved;

	/**
	 * header fields
	 */
	private Hashtable<String, String> _fields;

	public HeadEntity(CoreDriver coreDriver, MasterDataManagement management) {
		_coreDriver = coreDriver;
		_management = management;
		_items = new ArrayList<ItemEntity>();
		_fields = new Hashtable<String, String>();

		_postingDate = null;
		_docText = "";
		_type = null;

		_isReversed = false;
		_isSaved = false;
	}

	/**
	 * get document identity
	 * 
	 * @return
	 */
	public DocumentIdentity getDocIdentity() {
		if (_docNumber == null || _monthId == null) {
			return null;
		}
		return new DocumentIdentity(_docNumber, _monthId);
	}

	/**
	 * get document number
	 * 
	 * @return document number
	 */
	public DocumentNumber getDocumentNumber() {
		return _docNumber;
	}

	/**
	 * get fiscal year
	 * 
	 * @return fiscal year
	 */
	public int getFiscalYear() {
		return _monthId._fiscalYear;
	}

	/**
	 * get fiscal month
	 * 
	 * @return
	 */
	public int getFiscalMonth() {
		return _monthId._fiscalMonth;
	}

	/**
	 * get month identity
	 * 
	 * @return
	 */
	public MonthIdentity getMonthId() {
		return _monthId;
	}

	/**
	 * set the posting date
	 * 
	 * @param postingDate
	 */
	public boolean setPostingDate(Date postingDate) {
		if (_isSaved) {
			return false;
		}
		_postingDate = postingDate;
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(postingDate);
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH) + 1;
			_monthId = new MonthIdentity(year, month);
		} catch (FiscalYearRangeException e) {
			throw new SystemException(e);
		} catch (FiscalMonthRangeException e) {
			throw new SystemException(e);
		}

		return true;
	}

	/**
	 * get posting date
	 * 
	 * @return
	 */
	public Date getPostingDate() {
		return _postingDate;
	}

	/**
	 * set document text
	 * 
	 * @param text
	 */
	public void setDocText(String text) {
		_docText = text;
	}

	/**
	 * get document text
	 * 
	 * @return document text
	 */
	public String getDocText() {
		return _docText;
	}

	/**
	 * document type
	 * 
	 * @param type
	 */
	public boolean setDocumentType(DocumentType type) {
		if (_isSaved) {
			return false;
		}
		_type = type;
		return true;
	}

	/**
	 * return document type
	 * 
	 * @return
	 */
	public DocumentType getDocumentType() {
		return _type;
	}

	/**
	 * is reversed
	 * 
	 * @return
	 */
	public boolean IsReversed() {
		return _isReversed;
	}

	/**
	 * get document items
	 * 
	 * @return
	 */
	public ItemEntity[] getItems() {
		Collections.sort(_items);

		ItemEntity[] ret = new ItemEntity[_items.size()];
		for (int i = 0; i < _items.size(); ++i) {
			ret[i] = _items.get(i);
		}

		return ret;
	}

	/**
	 * get count of items
	 * 
	 * @return count of items
	 */
	public int getItemCount() {
		return _items.size();
	}

	/**
	 * set field key and value
	 * 
	 * @param key
	 * @param value
	 */
	public void addField(String key, String value) {
		_fields.put(key, value);
	}

	/**
	 * get field value
	 * 
	 * @param key
	 * @return
	 */
	public String getField(String key) {
		if (TransDataUtils.XML_DOC_NUM.equals(key)) {
			// document number
			return _docNumber.toString();
		} else if (TransDataUtils.XML_YEAR.equals(key)) {
			// fiscal year
			return String.valueOf(_monthId._fiscalYear);
		} else if (TransDataUtils.XML_MONTH.equals(key)) {
			// fiscal month
			return String.valueOf(_monthId._fiscalMonth);
		} else if (TransDataUtils.XML_DATE.equals(key)) {
			// posting date
			SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
			return format.format(_postingDate);
		} else if (TransDataUtils.XML_TEXT.equals(key)) {
			// doc text
			return XMLTransfer.toSafeString(_docText);
		} else if (TransDataUtils.XML_DOC_TYPE.equals(key)) {
			// doc type
			return _type.toString();
		} else if (TransDataUtils.XML_IS_REVERSED.equals(key)) {
			// is reversed
			return String.valueOf(_isReversed);
		} 

		if (_fields.containsKey(key)) {
			return null;
		}
		return _fields.get(key);
	}

	/**
	 * get fields
	 * 
	 * @return
	 */
	public ArrayList<String> getFields() {
		ArrayList<String> fields = new ArrayList<String>();

		for (String str : TransDataUtils.HEAD_XML_TAGS) {
			fields.add(str);
		}

		fields.addAll(_fields.keySet());
		return fields;
	}

	/**
	 * Indicator whether the document is saved
	 * 
	 * @return
	 */
	public boolean isSaved() {
		return _isSaved;
	}

	/**
	 * create item entity
	 * 
	 * @return item entity
	 */
	public ItemEntity createEntity() {
		if (_isSaved) {
			return null;
		}

		int lineNum = _items.size();
		ItemEntity item = new ItemEntity(_coreDriver, _management, this,
				lineNum);
		_items.add(item);

		return item;
	}

	@Override
	public String toString() {
		return getDocIdentity().toString();
	}

	/**
	 * parse XML element to memory
	 * 
	 * @param elem
	 * @return
	 * @throws TransactionDataFileFormatException
	 * @throws SystemException
	 */
	public static HeadEntity parse(CoreDriver coreDriver,
			MasterDataManagement management, Element elem)
			throws TransactionDataFileFormatException {
		HeadEntity head = new HeadEntity(coreDriver, management);
		head._isSaved = true;

		// document number
		String docNumStr = elem.getAttribute(TransDataUtils.XML_DOC_NUM);
		if (StringUtility.isNullOrEmpty(docNumStr)) {
			coreDriver.logDebugInfo(HeadEntity.class, 271, String.format(
					"Field %s is missing in.", TransDataUtils.XML_DOC_NUM),
					MessageType.ERROR);
			throw new TransactionDataFileFormatException("");
		}

		// fiscal year
		String yearStr = elem.getAttribute(TransDataUtils.XML_YEAR);
		if (StringUtility.isNullOrEmpty(yearStr)) {
			coreDriver.logDebugInfo(HeadEntity.class, 283, String.format(
					"Field %s is missing in.", TransDataUtils.XML_YEAR),
					MessageType.ERROR);
			throw new TransactionDataFileFormatException("");
		}

		// fiscal month
		String monthStr = elem.getAttribute(TransDataUtils.XML_MONTH);
		if (StringUtility.isNullOrEmpty(monthStr)) {
			coreDriver.logDebugInfo(HeadEntity.class, 295, String.format(
					"Field %s is missing in.", TransDataUtils.XML_MONTH),
					MessageType.ERROR);
			throw new TransactionDataFileFormatException("");
		}

		// posting date
		String dateStr = elem.getAttribute(TransDataUtils.XML_DATE);
		if (StringUtility.isNullOrEmpty(dateStr)) {
			coreDriver.logDebugInfo(HeadEntity.class, 307, String.format(
					"Field %s is missing in.", TransDataUtils.XML_DATE),
					MessageType.ERROR);
			throw new TransactionDataFileFormatException("");
		}

		// text
		String text = XMLTransfer.parseFromSafeString(elem
				.getAttribute(TransDataUtils.XML_TEXT));

		// document type
		String docTypeStr = elem.getAttribute(TransDataUtils.XML_DOC_TYPE);
		if (StringUtility.isNullOrEmpty(docTypeStr)) {
			coreDriver.logDebugInfo(HeadEntity.class, 325, String.format(
					"Field %s is missing in.", TransDataUtils.XML_DOC_TYPE),
					MessageType.ERROR);
			throw new TransactionDataFileFormatException("");
		}

		// is reversed
		String isReversedStr = elem
				.getAttribute(TransDataUtils.XML_IS_REVERSED);
		if (StringUtility.isNullOrEmpty(isReversedStr)) {
			coreDriver.logDebugInfo(HeadEntity.class, 338, String.format(
					"Field %s is missing in.", TransDataUtils.XML_IS_REVERSED),
					MessageType.ERROR);
			throw new TransactionDataFileFormatException("");
		}

		try {
			head._docNumber = new DocumentNumber(docNumStr.toCharArray());
			int year = Integer.parseInt(yearStr);
			int month = Integer.parseInt(monthStr);
			head._monthId = new MonthIdentity(year, month);
			SimpleDateFormat dateForm = new SimpleDateFormat("yyyy.MM.dd");
			head._postingDate = dateForm.parse(dateStr);
			head._docText = text;
			head._type = DocumentType.parse(docTypeStr.charAt(0));
			head._isReversed = Boolean.parseBoolean(isReversedStr);

			// parse item
			NodeList nodeList = elem.getChildNodes();
			nodeList = elem.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); ++i) {
				Node child = nodeList.item(i);
				if (child instanceof Element) {
					Element itemElem = (Element) child;
					if (itemElem.getNodeName().equals(TransDataUtils.XML_ITEM)) {
						ItemEntity item = ItemEntity.parse(coreDriver,
								management, head, itemElem);
						item._isSaved = true;

						coreDriver
								.logDebugInfo(
										HeadEntity.class,
										377,
										String.format(
												"Line Item %d appended during parsing document.",
												item.getLineNum()),
										MessageType.INFO);
						head._items.add(item);
					}
				} else if (child instanceof Attr) {
					// additional attribute
					Attr attr = (Attr) child;
					head._fields.put(attr.getName(), attr.getValue());
				}
			}

			// remove fields is not additional fields
			for (String str : TransDataUtils.HEAD_XML_TAGS) {
				head._fields.remove(str);
			}

			// check balance
			CurrencyAmount sum = new CurrencyAmount();
			for (ItemEntity item : head._items) {
				if (item.getCDIndicator() == CreditDebitIndicator.DEBIT) {
					sum.addTo(item.getAmount());
				} else {
					sum.minusTo(item.getAmount());
				}
			}

			if (sum.isZero() == false) {
				throw new TransactionDataFileFormatException("No Balance");
			}

			StringBuilder strBuilder = new StringBuilder(
					String.format(
							"Parse document %s with posting date %s, text %s, type %s, is_reversed %s",
							head.getDocIdentity(), head.getPostingDate(),
							head.getDocText(), head.getDocumentType(),
							head.IsReversed()));

			coreDriver.logDebugInfo(HeadEntity.class, 377,
					strBuilder.toString(), MessageType.INFO);
			return head;
		} catch (IdentityTooLong e) {
			coreDriver.logDebugInfo(HeadEntity.class, 382, e.toString(),
					MessageType.ERROR);
			throw new TransactionDataFileFormatException("");
		} catch (IdentityNoData e) {
			coreDriver.logDebugInfo(HeadEntity.class, 386, e.toString(),
					MessageType.ERROR);
			throw new TransactionDataFileFormatException("");
		} catch (IdentityInvalidChar e) {
			coreDriver.logDebugInfo(HeadEntity.class, 389, e.toString(),
					MessageType.ERROR);
			throw new TransactionDataFileFormatException("");
		} catch (NumberFormatException e) {
			coreDriver.logDebugInfo(HeadEntity.class, 393, e.toString(),
					MessageType.ERROR);
			throw new TransactionDataFileFormatException("");
		} catch (FiscalYearRangeException e) {
			coreDriver.logDebugInfo(HeadEntity.class, 398, e.toString(),
					MessageType.ERROR);
			throw new TransactionDataFileFormatException("");
		} catch (FiscalMonthRangeException e) {
			coreDriver.logDebugInfo(HeadEntity.class, 402, e.toString(),
					MessageType.ERROR);
			throw new TransactionDataFileFormatException("");
		}  catch (ParseException e) {
			coreDriver.logDebugInfo(HeadEntity.class, 410, e.toString(),
					MessageType.ERROR);
			throw new TransactionDataFileFormatException("");
		}

	}

	public int compareTo(HeadEntity head) {
		int ret = this._docNumber.compareTo(head._docNumber);
		return ret;
	}

	/**
	 * save document
	 * 
	 * @param messages
	 *            message generated during the saving process
	 * @param needStore
	 *            the flag whether to store the memory to disk
	 * @return
	 * @throws SaveClosedLedgerException
	 * @throws BalanceNotZero
	 * @throws MandatoryFieldIsMissing
	 * @throws StorageException
	 */
	public void save(boolean needStore) throws SaveClosedLedgerException,
			MandatoryFieldIsMissing, BalanceNotZero, StorageException {
		_coreDriver.logDebugInfo(this.getClass(), 427,
				"Starting to save document...", MessageType.INFO);

		// check before save
		checkBeforeSave();

		TransactionDataManagement transaction = _coreDriver
				.getTransDataManagement();

		_coreDriver
				.logDebugInfo(
						this.getClass(),
						450,
						"Check is OK. Then get the transaction management and then all the transaction to save the document.",
						MessageType.INFO);

		// store
		transaction.saveDocument(this, needStore);

		String info = String.format("Document %s in %s saved successfully.",
				_docNumber, _monthId);
		_coreDriver.logDebugInfo(this.getClass(), 459, info, MessageType.INFO);
		_isSaved = true;

		// raise saved document
		_coreDriver.getListenersManagement().saveDoc(this);
	}

	/**
	 * check before save
	 * 
	 * @return
	 * @throws MandatoryFieldIsMissing
	 * @throws BalanceNotZero
	 */
	public void checkBeforeSave() throws MandatoryFieldIsMissing,
			BalanceNotZero {
		// check posting date
		if (_postingDate == null) {
			_coreDriver.logDebugInfo(this.getClass(), 478,
					"Check document before save, posting date is null",
					MessageType.ERROR);
			throw new MandatoryFieldIsMissing("Posting Date");
		}

		// check document type
		if (_type == null) {
			_coreDriver.logDebugInfo(this.getClass(), 478,
					"Check document before save, document type is null",
					MessageType.ERROR);
			throw new MandatoryFieldIsMissing("Document Type");
		}

		CurrencyAmount sum = new CurrencyAmount();
		for (ItemEntity item : _items) {
			item.checkMandatory();

			if (item.getCDIndicator() == CreditDebitIndicator.CREDIT) {
				sum.minusTo(item.getAmount());
			} else if (item.getCDIndicator() == CreditDebitIndicator.DEBIT) {
				sum.addTo(item.getAmount());
			}
		}

		// check balance
		if (!sum.isZero()) {
			_coreDriver.logDebugInfo(this.getClass(), 478,
					"Check document before save, balance is not zero",
					MessageType.ERROR);
			throw new BalanceNotZero();
		}

		_coreDriver.logDebugInfo(this.getClass(), 319,
				"Check document before save successfully", MessageType.INFO);
	}

	/**
	 * 
	 * @return
	 */
	public String toXml() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(String.format("%s%s ", XMLTransfer.BEGIN_TAG_LEFT,
				TransDataUtils.XML_DOCUMENT));
		// add fields XML name and value
		for (String str : TransDataUtils.HEAD_XML_TAGS) {
			String value = this.getField(str);
			if (value != null) {
				strBuilder.append(String.format("%s=\"%s\" ", str, value));
			}
		}

		strBuilder.append(XMLTransfer.BEGIN_TAG_RIGHT);

		// items
		for (ItemEntity item : _items) {
			strBuilder.append(String.format("%s%s ",
					XMLTransfer.SINGLE_TAG_LEFT, TransDataUtils.XML_ITEM));
			strBuilder.append(item.toXML());
			strBuilder.append(XMLTransfer.SINGLE_TAG_RIGHT);
		}

		strBuilder.append(String.format("%s%s %s", XMLTransfer.END_TAG_LEFT,
				TransDataUtils.XML_DOCUMENT, XMLTransfer.END_TAG_RIGHT));
		return strBuilder.toString();
	}
}
