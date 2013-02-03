package com.jasonzqshen.familyAccountingBackendTest.utils;

import static org.junit.Assert.assertEquals;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.masterdata.BankAccountMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.BankAccountMasterDataFactory;
import com.jasonzqshen.familyaccounting.core.masterdata.BankKeyMasterDataFactory;
import com.jasonzqshen.familyaccounting.core.masterdata.BusinessAreaMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.BusinessAreaMasterDataFactory;
import com.jasonzqshen.familyaccounting.core.masterdata.CustomerMasterDataFactory;
import com.jasonzqshen.familyaccounting.core.masterdata.GLAccountMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.GLAccountMasterDataFactory;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataBase;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;
import com.jasonzqshen.familyaccounting.core.masterdata.VendorMasterDataFactory;
import com.jasonzqshen.familyaccounting.core.utils.BankAccountType;
import com.jasonzqshen.familyaccounting.core.utils.CriticalLevel;

public class MasterDataCheckerCN {
    /**
     * check the master data information
     * 
     * @param coreDriver
     */
    public static void checkMasterData(CoreDriver coreDriver) {

        MasterDataManagement masterDataManagement = coreDriver
                .getMasterDataManagement();

        MasterDataBase[] datas;

        // check vendor data
        VendorMasterDataFactory vendorFactory = (VendorMasterDataFactory) masterDataManagement
                .getMasterDataFactory(MasterDataType.VENDOR);
        assertEquals(2, vendorFactory.getMasterDataCount());
        datas = vendorFactory.getAllEntities();
        assertEquals(TestData.VENDOR_BUS, datas[0].getIdentity().toString());
        assertEquals(TestData.VENDOR_BUS_DESCP_CN, datas[0].getDescp());
        assertEquals(TestData.VENDOR_SUBWAY, datas[1].getIdentity().toString());
        assertEquals(TestData.VENDOR_SUBWAY_DESCP_CN, datas[1].getDescp());

        // check customer data
        CustomerMasterDataFactory customerFactory = (CustomerMasterDataFactory) masterDataManagement
                .getMasterDataFactory(MasterDataType.CUSTOMER);
        assertEquals(2, customerFactory.getMasterDataCount());
        datas = customerFactory.getAllEntities();
        assertEquals(TestData.CUSTOMER1, datas[0].getIdentity().toString());
        assertEquals(TestData.CUSTOMER1_DESCP_CN, datas[0].getDescp());
        assertEquals(TestData.CUSTOMER2, datas[1].getIdentity().toString());
        assertEquals(TestData.CUSTOMER2_DESCP_CN, datas[1].getDescp());

        // check business area
        BusinessAreaMasterDataFactory businessAreaFactory = (BusinessAreaMasterDataFactory) masterDataManagement
                .getMasterDataFactory(MasterDataType.BUSINESS_AREA);
        assertEquals(3, businessAreaFactory.getMasterDataCount());
        datas = businessAreaFactory.getAllEntities();
        assertEquals(TestData.BUSINESS_AREA_WORK, datas[0].getIdentity()
                .toString());
        assertEquals(TestData.BUSINESS_AREA_WORK_DESCP_CN, datas[0].getDescp());
        assertEquals(CriticalLevel.HIGH,
                ((BusinessAreaMasterData) datas[0]).getCriticalLevel());
        assertEquals(TestData.BUSINESS_AREA_SNACKS, datas[1].getIdentity()
                .toString());
        assertEquals(TestData.BUSINESS_AREA_SNACKS_DESCP_CN, datas[1].getDescp());
        assertEquals(CriticalLevel.LOW,
                ((BusinessAreaMasterData) datas[1]).getCriticalLevel());
        assertEquals(TestData.BUSINESS_AREA_ENTERTAIN, datas[2].getIdentity()
                .toString());
        assertEquals(TestData.BUSINESS_AREA_ENTERTAIN_DESCP_CN,
                datas[2].getDescp());
        assertEquals(CriticalLevel.MEDIUM,
                ((BusinessAreaMasterData) datas[2]).getCriticalLevel());

        // check bank key
        BankKeyMasterDataFactory bankKeyFactory = (BankKeyMasterDataFactory) masterDataManagement
                .getMasterDataFactory(MasterDataType.BANK_KEY);
        assertEquals(1, bankKeyFactory.getMasterDataCount());
        datas = bankKeyFactory.getAllEntities();
        assertEquals(TestData.BANK_KEY, datas[0].getIdentity().toString());
        assertEquals(TestData.BANK_KEY_DESCP_CN, datas[0].getDescp());

        // check bank account
        BankAccountMasterDataFactory bankAccountFactory = (BankAccountMasterDataFactory) masterDataManagement
                .getMasterDataFactory(MasterDataType.BANK_ACCOUNT);
        assertEquals(2, bankAccountFactory.getMasterDataCount());
        datas = bankAccountFactory.getAllEntities();
        assertEquals(TestData.BANK_ACCOUNT_CMB_6235, datas[0].getIdentity()
                .toString());
        assertEquals(TestData.BANK_ACCOUNT_CMB_6235_DESCP, datas[0].getDescp());
        assertEquals(TestData.BANK_ACCOUNT_CMB_6235_ACC,
                ((BankAccountMasterData) datas[0]).getBankAccountNumber()
                        .toString());
        assertEquals(BankAccountType.SAVING_ACCOUNT,
                ((BankAccountMasterData) datas[0]).getBankAccType());
        assertEquals(TestData.BANK_ACCOUNT_CMB_6620, datas[1].getIdentity()
                .toString());
        assertEquals(TestData.BANK_ACCOUNT_CMB_6620_DESCP, datas[1].getDescp());
        assertEquals(TestData.BANK_ACCOUNT_CMB_6620_ACC,
                ((BankAccountMasterData) datas[1]).getBankAccountNumber()
                        .toString());
        assertEquals(BankAccountType.CREDIT_CARD,
                ((BankAccountMasterData) datas[1]).getBankAccType());

        // check G/L account
        GLAccountMasterDataFactory accountFactory = (GLAccountMasterDataFactory) masterDataManagement
                .getMasterDataFactory(MasterDataType.GL_ACCOUNT);
        assertEquals(5, accountFactory.getMasterDataCount());
        datas = accountFactory.getAllEntities();
        assertEquals(TestData.GL_ACCOUNT_CASH, datas[0].getIdentity()
                .toString());
        assertEquals(TestData.GL_ACCOUNT_CASH_DESCP_CN, datas[0].getDescp());
        assertEquals(TestData.GL_ACCOUNT_BANK, datas[1].getIdentity()
                .toString());
        assertEquals(TestData.GL_ACCOUNT_BANK_DESCP_CN, datas[1].getDescp());
        assertEquals(TestData.BANK_ACCOUNT_CMB_6235,
                ((GLAccountMasterData) datas[1]).getBankAccount().toString());
        assertEquals(TestData.GL_ACCOUNT_EQUITY, datas[2].getIdentity()
                .toString());
        assertEquals(TestData.GL_ACCOUNT_EQUITY_DESCP_CN, datas[2].getDescp());
        assertEquals(TestData.GL_ACCOUNT_REV, datas[3].getIdentity()
                .toString());
        assertEquals(TestData.GL_ACCOUNT_REV_DESCP_CN, datas[3].getDescp());
        assertEquals(TestData.GL_ACCOUNT_COST, datas[4].getIdentity()
                .toString());
        assertEquals(TestData.GL_ACCOUNT_COST_DESCP_CN, datas[4].getDescp());

    }

}
