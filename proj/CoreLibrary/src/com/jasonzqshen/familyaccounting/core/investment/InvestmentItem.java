package com.jasonzqshen.familyaccounting.core.investment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.w3c.dom.Element;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.format.DocumentIdentityFormatException;
import com.jasonzqshen.familyaccounting.core.exception.format.InvestmentFileFormatException;
import com.jasonzqshen.familyaccounting.core.transaction.DocumentIdentity;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;
import com.jasonzqshen.familyaccounting.core.transaction.ItemEntity;
import com.jasonzqshen.familyaccounting.core.transaction.TransactionDataManagement;
import com.jasonzqshen.familyaccounting.core.utils.CreditDebitIndicator;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;
import com.jasonzqshen.familyaccounting.core.utils.MessageType;

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
        calendar1.setTime(_startDate);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(another._startDate);
        return calendar1.compareTo(calendar2);
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
