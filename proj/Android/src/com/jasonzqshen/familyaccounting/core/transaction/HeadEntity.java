package com.jasonzqshen.familyaccounting.core.transaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.DocumentIdentityFormatException;
import com.jasonzqshen.familyaccounting.core.exception.FiscalMonthRangeException;
import com.jasonzqshen.familyaccounting.core.exception.FiscalYearRangeException;
import com.jasonzqshen.familyaccounting.core.exception.IdentityInvalidChar;
import com.jasonzqshen.familyaccounting.core.exception.IdentityNoData;
import com.jasonzqshen.familyaccounting.core.exception.IdentityTooLong;
import com.jasonzqshen.familyaccounting.core.exception.MandatoryFieldIsMissing;
import com.jasonzqshen.familyaccounting.core.exception.SystemException;
import com.jasonzqshen.familyaccounting.core.utils.DocumentType;
import com.jasonzqshen.familyaccounting.core.utils.StringUtility;

public class HeadEntity {

	/**
	 * generate when document save.
	 */
	private DocumentNumber _docNumber;
	private MonthIdentity _monthId;
	private Date _postingDate;
	private String _docText;
	private DocumentType _type;
	private boolean _isReversed;
	private DocumentIdentity _ref;
	public final CoreDriver _coreDriver;
	private final ArrayList<ItemEntity> _items;

	public HeadEntity(CoreDriver coreDriver) {
		_coreDriver = coreDriver;
		_items = new ArrayList<ItemEntity>();
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
	 * set the posting date
	 * 
	 * @param postingDate
	 */
	public void setPostingDate(Date postingDate) {
		_postingDate = postingDate;
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
	public void setDocumentType(DocumentType type) {
		_type = type;
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
	 * 
	 * @return
	 */
	public ItemEntity createEntity() {
		return null;
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
}
