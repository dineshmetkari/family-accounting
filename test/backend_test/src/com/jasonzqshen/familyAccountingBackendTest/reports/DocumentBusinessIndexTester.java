package com.jasonzqshen.familyAccountingBackendTest.reports;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import com.jasonzqshen.familyAccountingBackendTest.utils.TestUtilities;
import com.jasonzqshen.familyAccountingBackendTest.utils.TesterBase;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.reports.DocumentBusinessIndex;
import com.jasonzqshen.familyaccounting.core.reports.DocumentIndex;
import com.jasonzqshen.familyaccounting.core.reports.DocumentIndexItemWithBalance;
import com.jasonzqshen.familyaccounting.core.reports.ReportsManagement;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;

public class DocumentBusinessIndexTester extends TesterBase {

    @Override
    protected void doTest(CoreDriver coreDriver) throws Exception {

        // set root path
        coreDriver.setRootPath(TestUtilities.TEST_BUSI_INDEX);
    }

    @Override
    protected void check(CoreDriver coreDriver) throws Exception {
        ReportsManagement rpMgmt = coreDriver.getReportsManagement();
        DocumentBusinessIndex index = (DocumentBusinessIndex) rpMgmt
                .getDocumentIndex(DocumentIndex.BUSINESS_INDEX);
        assertEquals(1, index.getKeys().size());
        MasterDataIdentity id = new MasterDataIdentity(
                TestUtilities.BUSINESS_AREA);
        DocumentIndexItemWithBalance item = index.getIndexItem(id);
        ArrayList<HeadEntity> entities = item.getEntities();
        assertEquals(3, entities.size());

        entities = item.getEntities(coreDriver.getCurMonthId(),
                coreDriver.getCurMonthId());
        assertEquals(2, entities.size());

        CurrencyAmount amount1 = CurrencyAmount
                .parse(TestUtilities.TEST_AMOUNT1);
        assertEquals(amount1, item.getAmountSum());

        assertEquals(
                new CurrencyAmount(),
                item.getAmount(coreDriver.getCurMonthId(),
                        coreDriver.getCurMonthId()));
        assertEquals(
                amount1,
                item.getAmount(coreDriver.getStartMonthId(),
                        coreDriver.getStartMonthId()));
    }

}
