package com.jasonzqshen.familyAccountingBackendTest.investment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.jasonzqshen.familyAccountingBackendTest.utils.TestUtilities;
import com.jasonzqshen.familyAccountingBackendTest.utils.TesterBase;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.investment.InvestmentAccount;
import com.jasonzqshen.familyaccounting.core.investment.InvestmentItem;
import com.jasonzqshen.familyaccounting.core.investment.InvestmentManagement;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;
import com.jasonzqshen.familyaccounting.core.transaction.ItemEntity;
import com.jasonzqshen.familyaccounting.core.transaction.MonthIdentity;
import com.jasonzqshen.familyaccounting.core.transaction.MonthLedger;
import com.jasonzqshen.familyaccounting.core.transaction.TransactionDataManagement;
import com.jasonzqshen.familyaccounting.core.utils.AccountType;
import com.jasonzqshen.familyaccounting.core.utils.CreditDebitIndicator;
import com.jasonzqshen.familyaccounting.core.utils.DocumentType;

public class InvesetmentLoadingTester extends TesterBase {
    private InvestmentManagement _investMgmt;

    @Override
    protected void doTest(CoreDriver coreDriver) throws Exception {
        _investMgmt = new InvestmentManagement(coreDriver);
        coreDriver.setRootPath(TestUtilities.TEST_LOAD_INVEST_DATA);
        _investMgmt.initialize();
    }

    @Override
    protected void check(CoreDriver coreDriver) throws Exception {
        checkInvestment(coreDriver, _investMgmt);
    }

    /**
     * check investment
     * 
     * @throws Exception
     */
    public static void checkInvestment(CoreDriver coreDriver,
            InvestmentManagement investMgmt) throws Exception {
        // check the transaction
        assertEquals(true, coreDriver.isInitialized());

        TransactionDataManagement transMgmt = coreDriver
                .getTransDataManagement();
        MonthIdentity[] monthIds = transMgmt.getAllMonthIds();
        //assertEquals(2, monthIds.length);

        MonthIdentity month07 = monthIds[0];
        assertEquals(2012, month07._fiscalYear);
        assertEquals(7, month07._fiscalMonth);

        MonthLedger ledger = transMgmt.getLedger(month07);
        assertEquals(3, ledger.getCount());

        HeadEntity[] docs = ledger.getEntities();
        checkInvestmentDoc1(docs[0]);
        checkInvestmentDoc2(docs[1]);
        checkInvestmentDoc3(docs[2]);

        // check the investment
        assertEquals(1, investMgmt.getAccountsCount());
        ArrayList<InvestmentAccount> investAccounts = investMgmt
                .getInvestmentAccounts();
        InvestmentAccount investAccount = investAccounts.get(0);
        assertEquals("deposite account in ICBC", investAccount.getName());
        assertEquals("1010100001", investAccount.getAccount().toString());
        assertEquals("4000100001", investAccount.getRevAccount().toString());
        assertEquals("1030.00", investAccount.getTotalAmount().toString());
        assertEquals("30.00", investAccount.getRevAmount().toString());

        assertEquals(2, investAccount.getItemCount());
        ArrayList<InvestmentItem> items = investAccount.getItems();

        // investment item 1
        InvestmentItem investItem1 = items.get(0);
        checkInvestmentItem1(docs[0], docs[1], investItem1);
        checkInvestmentItem2(docs[2], items.get(1));

    }

    private static void checkInvestmentItem1(HeadEntity startDoc,
            HeadEntity endDoc, InvestmentItem item) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(item.getStartDate());
        assertEquals(2012, calendar.get(Calendar.YEAR));
        assertEquals(6, calendar.get(Calendar.MONTH));
        assertEquals(2, calendar.get(Calendar.DATE));

        calendar.setTime(item.getDueDate());
        assertEquals(2012, calendar.get(Calendar.YEAR));
        assertEquals(6, calendar.get(Calendar.MONTH));
        assertEquals(30, calendar.get(Calendar.DATE));

        calendar.setTime(item.getEndDate());
        assertEquals(2012, calendar.get(Calendar.YEAR));
        assertEquals(6, calendar.get(Calendar.MONTH));
        assertEquals(30, calendar.get(Calendar.DATE));

        assertEquals(true, item.isClosed());
        assertEquals("1000.00", item.getAmount().toString());
        assertEquals("30.00", item.getRevAmount().toString());

        assertTrue(startDoc == item.getStartDoc());
        assertTrue(endDoc == item.getEndDoc());

    }

    private static void checkInvestmentItem2(HeadEntity startDoc,
            InvestmentItem item) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(item.getStartDate());
        assertEquals(2012, calendar.get(Calendar.YEAR));
        assertEquals(6, calendar.get(Calendar.MONTH));
        assertEquals(30, calendar.get(Calendar.DATE));

        calendar.setTime(item.getDueDate());
        assertEquals(2012, calendar.get(Calendar.YEAR));
        assertEquals(7, calendar.get(Calendar.MONTH));
        assertEquals(30, calendar.get(Calendar.DATE));


        assertEquals(false, item.isClosed());
        assertEquals("1030.00", item.getAmount().toString());

        assertTrue(startDoc == item.getStartDoc());

    }

    /**
     * check start document 1
     * 
     * @param head
     */
    private static void checkInvestmentDoc1(HeadEntity head) {
        Date date = head.getPostingDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        assertEquals(2012, calendar.get(Calendar.YEAR));
        assertEquals(6, calendar.get(Calendar.MONTH));
        assertEquals("start of investment, deposite account in ICBC",
                head.getDocText());
        assertEquals(DocumentType.GL, head.getDocumentType());
        assertEquals(false, head.IsReversed());

        assertEquals(2, head.getItemCount());
        ItemEntity[] items = head.getItems();
        ItemEntity srcItem = items[0];
        assertEquals(AccountType.GL_ACCOUNT, srcItem.getAccountType());
        assertEquals("1000100001", srcItem.getGLAccount().toString());
        assertEquals("1000.00", srcItem.getAmount().toString());
        assertEquals(CreditDebitIndicator.CREDIT, srcItem.getCDIndicator());

        ItemEntity dstItem = items[1];
        assertEquals(AccountType.GL_ACCOUNT, dstItem.getAccountType());
        assertEquals("1010100001", dstItem.getGLAccount().toString());
        assertEquals("1000.00", dstItem.getAmount().toString());
        assertEquals(CreditDebitIndicator.DEBIT, dstItem.getCDIndicator());
    }

    /**
     * check revenue document 2
     * 
     * @param head
     */
    private static void checkInvestmentDoc2(HeadEntity head) {
        Date date = head.getPostingDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        assertEquals(2012, calendar.get(Calendar.YEAR));
        assertEquals(6, calendar.get(Calendar.MONTH));
        assertEquals("end of investment, deposite account in ICBC",
                head.getDocText());
        assertEquals(DocumentType.GL, head.getDocumentType());
        assertEquals(false, head.IsReversed());

        assertEquals(3, head.getItemCount());
        ItemEntity[] items = head.getItems();
        ItemEntity srcItem = items[0];
        assertEquals(AccountType.GL_ACCOUNT, srcItem.getAccountType());
        assertEquals("1010100001", srcItem.getGLAccount().toString());
        assertEquals("1000.00", srcItem.getAmount().toString());
        assertEquals(CreditDebitIndicator.CREDIT, srcItem.getCDIndicator());

        ItemEntity revenueItem = items[1];
        assertEquals(AccountType.GL_ACCOUNT, revenueItem.getAccountType());
        assertEquals("4000100001", revenueItem.getGLAccount().toString());
        assertEquals("30.00", revenueItem.getAmount().toString());
        assertEquals(CreditDebitIndicator.CREDIT, revenueItem.getCDIndicator());

        ItemEntity dstItem = items[2];
        assertEquals(AccountType.GL_ACCOUNT, dstItem.getAccountType());
        assertEquals("1000100001", dstItem.getGLAccount().toString());
        assertEquals("1030.00", dstItem.getAmount().toString());
        assertEquals(CreditDebitIndicator.DEBIT, dstItem.getCDIndicator());
    }

    /**
     * check start document 3
     * 
     * @param head
     */
    private static void checkInvestmentDoc3(HeadEntity head) {
        Date date = head.getPostingDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        assertEquals(2012, calendar.get(Calendar.YEAR));
        assertEquals(6, calendar.get(Calendar.MONTH));
        assertEquals("start of investment, deposite account in ICBC",
                head.getDocText());
        assertEquals(DocumentType.GL, head.getDocumentType());
        assertEquals(false, head.IsReversed());

        assertEquals(2, head.getItemCount());
        ItemEntity[] items = head.getItems();
        ItemEntity srcItem = items[0];
        assertEquals(AccountType.GL_ACCOUNT, srcItem.getAccountType());
        assertEquals("1000100001", srcItem.getGLAccount().toString());
        assertEquals("1030.00", srcItem.getAmount().toString());
        assertEquals(CreditDebitIndicator.CREDIT, srcItem.getCDIndicator());

        ItemEntity dstItem = items[1];
        assertEquals(AccountType.GL_ACCOUNT, dstItem.getAccountType());
        assertEquals("1010100001", dstItem.getGLAccount().toString());
        assertEquals("1030.00", dstItem.getAmount().toString());
        assertEquals(CreditDebitIndicator.DEBIT, dstItem.getCDIndicator());
    }
}
