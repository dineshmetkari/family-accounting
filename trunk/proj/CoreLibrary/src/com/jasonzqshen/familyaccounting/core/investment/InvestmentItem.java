package com.jasonzqshen.familyaccounting.core.investment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.w3c.dom.Element;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.BalanceNotZero;
import com.jasonzqshen.familyaccounting.core.exception.MandatoryFieldIsMissing;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityNotDefined;
import com.jasonzqshen.familyaccounting.core.exception.NullValueNotAcceptable;
import com.jasonzqshen.familyaccounting.core.exception.SaveOpenLedgerException;
import com.jasonzqshen.familyaccounting.core.exception.StorageException;
import com.jasonzqshen.familyaccounting.core.exception.format.DocumentIdentityFormatException;
import com.jasonzqshen.familyaccounting.core.exception.format.InvestmentFileFormatException;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.transaction.DocumentIdentity;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;
import com.jasonzqshen.familyaccounting.core.transaction.ItemEntity;
import com.jasonzqshen.familyaccounting.core.transaction.MonthIdentity;
import com.jasonzqshen.familyaccounting.core.transaction.TransactionDataManagement;
import com.jasonzqshen.familyaccounting.core.utils.CreditDebitIndicator;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;
import com.jasonzqshen.familyaccounting.core.utils.DocumentType;
import com.jasonzqshen.familyaccounting.core.utils.MessageType;
import com.jasonzqshen.familyaccounting.core.utils.XMLTransfer;

public class InvestmentItem implements Comparable<InvestmentItem> {
	public static final String XML_START_DATE = "start_date";

	public static final String XML_DUE_DATE = "due_date";

	public static final String XML_END_DATE = "end_date";

	public static final String XML_IS_CLOSED = "is_closed";

	public static final String XML_START_DOC = "start_doc";

	public static final String XML_END_DOC = "end_doc";

	private final CoreDriver _coreDriver;

	private final InvestmentAccount _investAcc;

	private final Date _startDate;

	private final Date _dueDate;

	private Date _endDate;

	private boolean _isClosed;

	private final DocumentIdentity _startDoc;

	private DocumentIdentity _endDoc;

	/**
	 * investment item
	 * 
	 * @param startDate
	 */
	InvestmentItem(CoreDriver coreDriver, InvestmentAccount account,
			Date startDate, Date dueDate, DocumentIdentity startDoc) {
		_coreDriver = coreDriver;
		_investAcc = account;
		_startDate = startDate;
		_dueDate = dueDate;
		_startDoc = startDoc;

		_isClosed = false;
	}

	/**
	 * get start date
	 * 
	 * @return
	 */
	public Date getStartDate() {
		return _startDate;
	}

	/**
	 * get due date
	 * 
	 * @return
	 */
	public Date getDueDate() {
		return _dueDate;
	}

	/**
	 * get end date
	 * 
	 * @return
	 */
	public Date getEndDate() {
		return _endDate;
	}

	/**
	 * get start document
	 * 
	 * @return
	 */
	public HeadEntity getStartDoc() {
		TransactionDataManagement transMgmt = _coreDriver
				.getTransDataManagement();
		return transMgmt.getEntity(_startDoc);
	}

	/**
	 * get end document
	 * 
	 * @return
	 */
	public HeadEntity getEndDoc() {
		TransactionDataManagement transMgmt = _coreDriver
				.getTransDataManagement();
		return transMgmt.getEntity(_endDoc);
	}

	/**
	 * get amount
	 * 
	 * @return
	 */
	public CurrencyAmount getAmount() {
		TransactionDataManagement transMgmt = _coreDriver
				.getTransDataManagement();
		HeadEntity head = transMgmt.getEntity(_startDoc);
		ItemEntity[] items = head.getItems();
		CurrencyAmount sum = new CurrencyAmount();
		for (ItemEntity item : items) {
			if (item.getGLAccount().equals(_investAcc.getAccount())) {
				if (item.getCDIndicator() == CreditDebitIndicator.DEBIT) {
					sum.addTo(item.getAmount());
				} else {
					sum.minusTo(item.getAmount());
				}
			}
		}
		return sum;
	}

	/**
	 * get revenue amount
	 * 
	 * @return
	 */
	public CurrencyAmount getRevAmount() {
		if (_isClosed == false) {
			return new CurrencyAmount();
		}
		TransactionDataManagement transMgmt = _coreDriver
				.getTransDataManagement();
		HeadEntity head = transMgmt.getEntity(_endDoc);
		ItemEntity[] items = head.getItems();
		CurrencyAmount sum = new CurrencyAmount();
		for (ItemEntity item : items) {
			if (item.getGLAccount().equals(_investAcc.getRevAccount())) {
				if (item.getCDIndicator() == CreditDebitIndicator.DEBIT) {
					sum.addTo(item.getAmount());
				} else {
					sum.minusTo(item.getAmount());
				}
			}
		}

		sum.negate();
		return sum;
	}

	/**
	 * commit the investment
	 * 
	 * @param endDate
	 * @param dstAccount
	 * @param amount
	 */
	public boolean commit(CurrencyAmount totalAmount) {
		// check amount
		if (totalAmount.isNegative() || totalAmount.isZero()) {
			return false;
		}

		HeadEntity headEntity = new HeadEntity(_coreDriver,
				_coreDriver.getMasterDataManagement());

		// get due date
		Date endDate = _dueDate;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(endDate);
		MonthIdentity curMonthId = _coreDriver.getCurMonthId();
		if (curMonthId._fiscalMonth != calendar.get(Calendar.MONTH) - 1
				|| curMonthId._fiscalYear != calendar.get(Calendar.YEAR)) {
			calendar.set(Calendar.YEAR, curMonthId._fiscalYear);
			calendar.set(Calendar.MONTH, curMonthId._fiscalMonth - 1);
			endDate = calendar.getTime();
		}

		// get the destination account
		TransactionDataManagement transMgmt = _coreDriver
				.getTransDataManagement();
		HeadEntity entity = transMgmt.getEntity(_startDoc);
		MasterDataIdentity_GLAccount dstAccount = entity.getItems()[0]
				.getGLAccount();

		headEntity.setPostingDate(endDate);
		headEntity.setDocText(InvestmentAccount.END_DOC_DESCP
				+ _investAcc.getName());
		headEntity.setDocumentType(DocumentType.GL);

		CurrencyAmount srcAmount = this.getAmount();
		CurrencyAmount revAmount = CurrencyAmount.minus(totalAmount, srcAmount);
		try {
			// investment item
			ItemEntity investItem = headEntity.createEntity();
			investItem.setAmount(CreditDebitIndicator.CREDIT, srcAmount);
			investItem.setGLAccount(_investAcc.getAccount());

			// revenue item
			ItemEntity revItem = headEntity.createEntity();
			CreditDebitIndicator cdIndicator = CreditDebitIndicator.CREDIT;
			if (revAmount.isZero() == false) {
				if (revAmount.isNegative()) {
					cdIndicator = CreditDebitIndicator.DEBIT;
					revAmount.negate();
				}

				revItem.setAmount(cdIndicator, revAmount);
				revItem.setGLAccount(_investAcc.getRevAccount());
			}

			// destination item
			ItemEntity dstItem = headEntity.createEntity();
			dstItem.setAmount(CreditDebitIndicator.DEBIT, totalAmount);
			dstItem.setGLAccount(dstAccount);
		} catch (NullValueNotAcceptable e) {
			_coreDriver.logDebugInfo(this.getClass(), 164, e.toString(),
					MessageType.ERROR);
			throw new SystemException(e);
		} catch (MasterDataIdentityNotDefined e) {
			_coreDriver.logDebugInfo(this.getClass(), 208, e.toString(),
					MessageType.ERROR);
			return false;
		}

		try {
			headEntity.save(true);
		} catch (SaveOpenLedgerException e) {
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

		_isClosed = true;
		_endDate = endDate;
		_endDoc = headEntity.getDocIdentity();

		return true;
	}

	/**
	 * is closed
	 * 
	 * @return
	 */
	public boolean isClosed() {
		return _isClosed;
	}

	/**
	 * close the investment item
	 */
	public void close(Date endDate, CurrencyAmount amount) {
		_endDate = endDate;

	}

	@Override
	public int compareTo(InvestmentItem another) {
		Calendar calendar1 = Calendar.getInstance();
		if (this._isClosed) {
			calendar1.setTime(_endDate);
		} else {
			calendar1.setTime(_dueDate);
		}

		Calendar calendar2 = Calendar.getInstance();
		if (another.isClosed()) {
			calendar2.setTime(another._endDate);
		} else {
			calendar2.setTime(another._dueDate);
		}
		return calendar1.compareTo(calendar2);
	}

	/**
	 * to XML
	 * 
	 * @return
	 */
	public String toXML() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(String.format("%s%s ", XMLTransfer.SINGLE_TAG_LEFT,
				InvestmentAccount.XML_ITEM));
		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
		strBuilder.append(String.format("%s=\"%s\" %s=\"%s\" %s=\"%s\" ",
				XML_START_DATE, format.format(_startDate), XML_DUE_DATE,
				format.format(_dueDate), XML_START_DOC, _startDoc.toString()));

		if (_isClosed) {
			strBuilder.append(String.format("%s=\"true\" %s=\"%s\" %s=\"%s\" ",
					XML_IS_CLOSED, XML_END_DATE, format.format(_endDate),
					XML_END_DOC, _endDoc.toString()));
		} else {
			strBuilder.append(String.format("%s=\"false\" ", XML_IS_CLOSED));
		}

		strBuilder.append(XMLTransfer.SINGLE_TAG_RIGHT);

		return strBuilder.toString();
	}

	/**
	 * parse XML to investment element
	 * 
	 * @param elem
	 * @return
	 * @throws InvestmentFileFormatException
	 */
	public static InvestmentItem parse(CoreDriver coreDriver,
			InvestmentAccount investAcc, Element elem)
			throws InvestmentFileFormatException {
		String startDateStr = elem.getAttribute(XML_START_DATE);
		String dueDateStr = elem.getAttribute(XML_DUE_DATE);
		String startDocStr = elem.getAttribute(XML_START_DOC);
		String isClosedStr = elem.getAttribute(XML_IS_CLOSED);

		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
		try {
			Date startDate = format.parse(startDateStr);
			Date dueDate = format.parse(dueDateStr);
			DocumentIdentity startDoc = DocumentIdentity.parse(startDocStr);

			InvestmentItem item = new InvestmentItem(coreDriver, investAcc,
					startDate, dueDate, startDoc);

			boolean isClosed;
			if (isClosedStr.toLowerCase().equals("true")) {
				isClosed = true;
			} else if (isClosedStr.toLowerCase().equals("false")) {
				isClosed = false;
			} else {
				throw new InvestmentFileFormatException(
						"IsClosed Value is invalid: " + isClosedStr);
			}

			if (isClosed == false) {
				// check the attributes
				if (elem.hasAttribute(XML_END_DATE)
						|| elem.hasAttribute(XML_END_DOC)) {
					throw new InvestmentFileFormatException(
							"Open investemnt item should not contain end doc or end date");
				}
			} else {
				String endDateStr = elem.getAttribute(XML_END_DATE);
				String endDocStr = elem.getAttribute(XML_END_DOC);

				item._endDate = format.parse(endDateStr);
				item._endDoc = DocumentIdentity.parse(endDocStr);
				item._isClosed = true;
			}

			return item;

		} catch (ParseException e) {
			coreDriver.logDebugInfo(InvestmentItem.class, 174, e.toString(),
					MessageType.ERROR);
			throw new InvestmentFileFormatException(e.toString());
		} catch (DocumentIdentityFormatException e) {
			coreDriver.logDebugInfo(InvestmentItem.class, 179, e.toString(),
					MessageType.ERROR);
			throw new InvestmentFileFormatException(e.toString());
		}
	}

}
