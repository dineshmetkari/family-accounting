package com.jasonzqshen.familyaccounting.core.transaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.BalanceNotZero;
import com.jasonzqshen.familyaccounting.core.exception.DocumentIdentityFormatException;
import com.jasonzqshen.familyaccounting.core.exception.FiscalMonthRangeException;
import com.jasonzqshen.familyaccounting.core.exception.FiscalYearRangeException;
import com.jasonzqshen.familyaccounting.core.exception.IdentityInvalidChar;
import com.jasonzqshen.familyaccounting.core.exception.IdentityNoData;
import com.jasonzqshen.familyaccounting.core.exception.IdentityTooLong;
import com.jasonzqshen.familyaccounting.core.exception.MandatoryFieldIsMissing;
import com.jasonzqshen.familyaccounting.core.exception.SystemException;
import com.jasonzqshen.familyaccounting.core.utils.CreditDebitIndicator;
import com.jasonzqshen.familyaccounting.core.utils.DocumentType;
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
	DocumentIdentity _ref;
	public final CoreDriver _coreDriver;
	private final ArrayList<ItemEntity> _items;
	private boolean _isSaved;

	public HeadEntity(CoreDriver coreDriver) {
		_coreDriver = coreDriver;
		_items = new ArrayList<ItemEntity>();

		_postingDate = null;
		_docText = "";
		_type = null;

		_isReversed = false;
		_isSaved = false;
	}

	/**
	 * get reference
	 * 
	 * @return
	 */
	public DocumentIdentity getReference() {
		return _ref;
	}

	/**
	 * get document identity
	 * 
	 * @return
	 */
	public DocumentIdentity getDocIdentity() {
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
			e.printStackTrace();
		} catch (FiscalMonthRangeException e) {
			e.printStackTrace();
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
		ItemEntity item = new ItemEntity(_coreDriver, this, lineNum);
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
	 * @throws MandatoryFieldIsMissing
	 * @throws SystemException
	 */
	public static HeadEntity parse(CoreDriver coreDriver, Element elem)
			throws MandatoryFieldIsMissing, SystemException {
		HeadEntity head = new HeadEntity(coreDriver);
		head._isSaved = true;

		String docNumStr = elem.getAttribute(TransDataUtils.XML_DOC_NUM);
		String yearStr = elem.getAttribute(TransDataUtils.XML_YEAR);
		String monthStr = elem.getAttribute(TransDataUtils.XML_MONTH);
		String dateStr = elem.getAttribute(TransDataUtils.XML_DATE);
		String text = elem.getAttribute(TransDataUtils.XML_TEXT);
		String docTypeStr = elem.getAttribute(TransDataUtils.XML_DOC_TYPE);
		String isReversedStr = elem
				.getAttribute(TransDataUtils.XML_IS_REVERSED);
		String refStr = elem.getAttribute(TransDataUtils.XML_REF);

		// check mandatory
		if (StringUtility.isNullOrEmpty(docNumStr)) {
			throw new MandatoryFieldIsMissing("Document Number");
		}
		if (StringUtility.isNullOrEmpty(yearStr)) {
			throw new MandatoryFieldIsMissing("Fiscal Year");
		}
		if (StringUtility.isNullOrEmpty(monthStr)) {
			throw new MandatoryFieldIsMissing("Fiscal Month");
		}
		if (StringUtility.isNullOrEmpty(dateStr)) {
			throw new MandatoryFieldIsMissing("Posting Date");
		}
		if (StringUtility.isNullOrEmpty(docTypeStr)) {
			throw new MandatoryFieldIsMissing("Document Type");
		}
		if (StringUtility.isNullOrEmpty(isReversedStr)) {
			throw new MandatoryFieldIsMissing("Is Reversed");
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

			if (!StringUtility.isNullOrEmpty(refStr)) {
				head._ref = DocumentIdentity.parse(refStr);
			}

			// parse item
			NodeList nodeList = elem.getChildNodes();
			nodeList = elem.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); ++i) {
				Node child = nodeList.item(i);
				if (child instanceof Element) {
					Element itemElem = (Element) child;
					if (itemElem.getNodeName().equals(TransDataUtils.XML_ITEM)) {
						ItemEntity item = ItemEntity.parse(coreDriver, head,
								itemElem);
						item._isSaved = true;
						head._items.add(item);
					}
				}
			}
			return head;
		} catch (IdentityTooLong e) {
			throw new SystemException(e);
		} catch (IdentityNoData e) {
			throw new SystemException(e);
		} catch (IdentityInvalidChar e) {
			throw new SystemException(e);
		} catch (NumberFormatException e) {
			throw new SystemException(e);
		} catch (FiscalYearRangeException e) {
			throw new SystemException(e);
		} catch (FiscalMonthRangeException e) {
			throw new SystemException(e);
		} catch (DocumentIdentityFormatException e) {
			throw new SystemException(e);
		} catch (ParseException e) {
			throw new SystemException(e);
		}

	}

	public int compareTo(HeadEntity head) {
		int ret = this._docNumber.compareTo(head._docNumber);
		return ret;
	}

	/**
	 * save document
	 * 
	 * @return
	 * @throws MandatoryFieldIsMissing
	 * @throws BalanceNotZero
	 */
	public boolean save() throws MandatoryFieldIsMissing, BalanceNotZero {
		if (checkBeforeSave() == false) {
			return false;
		}

		TransactionDataManagement transaction = _coreDriver
				.getTransDataManagement();
		if (transaction.saveDocument(this) == false) {
			return false;
		}

		_isSaved = true;
		return true;
	}

	/**
	 * check before save
	 * 
	 * @return
	 * @throws MandatoryFieldIsMissing
	 * @throws BalanceNotZero
	 */
	public boolean checkBeforeSave() throws MandatoryFieldIsMissing,
			BalanceNotZero {
		// check posting date
		if (_postingDate == null) {
			throw new MandatoryFieldIsMissing("Posting Date");
		}

		// check document type
		if (_type == null) {
			throw new MandatoryFieldIsMissing("Document Type");
		}

		int creditSum = 0;
		int debitSum = 0;
		for (ItemEntity item : _items) {
			item.checkMandatory();

			if (item.getCDIndicator() == CreditDebitIndicator.CREDIT) {
				creditSum += (int) (item.getAmount() * 100);
			} else if (item.getCDIndicator() == CreditDebitIndicator.DEBIT) {
				debitSum += (int) (item.getAmount() * 100);
			}
		}

		// check balance
		if (creditSum != debitSum) {
			throw new BalanceNotZero();
		}

		return true;
	}

	/**
	 * 
	 * @return
	 */
	public String toXml() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(String.format("%s%s ", XMLTransfer.BEGIN_TAG_LEFT,
				TransDataUtils.XML_DOCUMENT));
		// document number
		strBuilder.append(String.format("%s=\"%s\" ",
				TransDataUtils.XML_DOC_NUM, _docNumber.toString()));
		// fiscal year
		strBuilder.append(String.format("%s=\"%d\" ", TransDataUtils.XML_YEAR,
				_monthId._fiscalYear));
		// fiscal month
		strBuilder.append(String.format("%s=\"%d\" ", TransDataUtils.XML_MONTH,
				_monthId._fiscalMonth));
		// posting date
		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
		strBuilder.append(String.format("%s=\"%s\" ", TransDataUtils.XML_DATE,
				format.format(_postingDate)));
		// text
		strBuilder.append(String.format("%s=\"%s\" ", TransDataUtils.XML_TEXT,
				_docText));
		// document type
		strBuilder.append(String.format("%s=\"%s\" ",
				TransDataUtils.XML_DOC_TYPE, _type));
		// is reversed
		strBuilder.append(String.format("%s=\"%s\" ",
				TransDataUtils.XML_IS_REVERSED, _isReversed));
		// reference
		if (_ref != null) {
			strBuilder.append(String.format("%s=\"%s\" ",
					TransDataUtils.XML_REF, _ref.toString()));
		}

		strBuilder.append(XMLTransfer.BEGIN_TAG_RIGHT);

		// items
		for (ItemEntity item : _items) {
			strBuilder.append(item.toXML());
		}

		strBuilder.append(String.format("%s%s %s", XMLTransfer.END_TAG_LEFT,
				TransDataUtils.XML_DOCUMENT, XMLTransfer.END_TAG_RIGHT));
		return strBuilder.toString();
	}
}
