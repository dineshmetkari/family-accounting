package com.jasonzqshen.familyAccountingBackendTest.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.FiscalMonthRangeException;
import com.jasonzqshen.familyaccounting.core.exception.FiscalYearRangeException;
import com.jasonzqshen.familyaccounting.core.exception.IdentityInvalidChar;
import com.jasonzqshen.familyaccounting.core.exception.IdentityNoData;
import com.jasonzqshen.familyaccounting.core.exception.IdentityTooLong;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityExists;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityNotDefined;
import com.jasonzqshen.familyaccounting.core.exception.NoMasterDataFileException;
import com.jasonzqshen.familyaccounting.core.exception.ParametersException;
import com.jasonzqshen.familyaccounting.core.exception.RootFolderNotExsits;
import com.jasonzqshen.familyaccounting.core.exception.format.MasterDataFileFormatException;
import com.jasonzqshen.familyaccounting.core.exception.runtime.NoMasterDataFactoryClass;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.masterdata.BankAccountMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.BankAccountMasterDataFactory;
import com.jasonzqshen.familyaccounting.core.masterdata.BankAccountNumber;
import com.jasonzqshen.familyaccounting.core.masterdata.BankKeyMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.BankKeyMasterDataFactory;
import com.jasonzqshen.familyaccounting.core.masterdata.BusinessAreaMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.BusinessAreaMasterDataFactory;
import com.jasonzqshen.familyaccounting.core.masterdata.CustomerMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.CustomerMasterDataFactory;
import com.jasonzqshen.familyaccounting.core.masterdata.GLAccountMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.GLAccountMasterDataFactory;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;
import com.jasonzqshen.familyaccounting.core.masterdata.VendorMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.VendorMasterDataFactory;
import com.jasonzqshen.familyaccounting.core.utils.BankAccountType;
import com.jasonzqshen.familyaccounting.core.utils.CriticalLevel;

/**
 * Unit test case utility. 
 * Create Master data with Chinese description
 * @author jasonzqshen@gmail.com
 *
 */
public class MasterDataCreaterCN {
    public static CoreDriver createMasterData(CoreDriver coreDriver)
            throws NoMasterDataFactoryClass, SystemException,
            RootFolderNotExsits, IdentityTooLong, IdentityNoData,
            IdentityInvalidChar, ParametersException, MasterDataIdentityExists,
            MasterDataIdentityNotDefined, FiscalYearRangeException,
            FiscalMonthRangeException, NoMasterDataFileException,
            MasterDataFileFormatException {

        /**
         * check the factory is initialized, and the factory with no master data
         * entities
         */
        MasterDataManagement masterDataManagement = coreDriver
                .getMasterDataManagement();

        // vendor
        VendorMasterDataFactory vendorFactory = (VendorMasterDataFactory) masterDataManagement
                .getMasterDataFactory(MasterDataType.VENDOR);
        assertEquals(0, vendorFactory.getMasterDataCount());
        // customer
        CustomerMasterDataFactory customerFactory = (CustomerMasterDataFactory) masterDataManagement
                .getMasterDataFactory(MasterDataType.CUSTOMER);
        assertEquals(0, customerFactory.getMasterDataCount());
        // business area
        BusinessAreaMasterDataFactory businessFactory = (BusinessAreaMasterDataFactory) masterDataManagement
                .getMasterDataFactory(MasterDataType.BUSINESS_AREA);
        assertEquals(0, businessFactory.getMasterDataCount());
        // bank key
        BankKeyMasterDataFactory bankKeyFactory = (BankKeyMasterDataFactory) masterDataManagement
                .getMasterDataFactory(MasterDataType.BANK_KEY);
        assertEquals(0, bankKeyFactory.getMasterDataCount());
        // bank account
        BankAccountMasterDataFactory bankAccountFactory = (BankAccountMasterDataFactory) masterDataManagement
                .getMasterDataFactory(MasterDataType.BANK_ACCOUNT);
        assertEquals(0, bankAccountFactory.getMasterDataCount());
        // GL account
        GLAccountMasterDataFactory accountFactory = (GLAccountMasterDataFactory) masterDataManagement
                .getMasterDataFactory(MasterDataType.GL_ACCOUNT);
        assertEquals(0, accountFactory.getMasterDataCount());

        /** add master data entities */
        // vendor
        VendorMasterData vendor = (VendorMasterData) vendorFactory
                .createNewMasterDataBase(new MasterDataIdentity(
                        TestData.VENDOR_BUS), TestData.VENDOR_BUS_DESCP_CN);
        assertTrue(vendor != null);
        vendor = (VendorMasterData) vendorFactory.createNewMasterDataBase(
                new MasterDataIdentity(TestData.VENDOR_SUBWAY),
                TestData.VENDOR_SUBWAY_DESCP_CN);
        assertTrue(vendor != null);

        // duplicate id
        try {
            vendorFactory.createNewMasterDataBase(new MasterDataIdentity(
                    TestData.VENDOR_SUBWAY), TestData.VENDOR_SUBWAY_DESCP_CN);
            assertTrue(false);
        } catch (MasterDataIdentityExists e) {

        }

        // customer
        CustomerMasterData customer = (CustomerMasterData) customerFactory
                .createNewMasterDataBase(new MasterDataIdentity(
                        TestData.CUSTOMER1), TestData.CUSTOMER1_DESCP_CN);
        assertTrue(customer != null);
        customer = (CustomerMasterData) customerFactory
                .createNewMasterDataBase(new MasterDataIdentity(
                        TestData.CUSTOMER2), TestData.CUSTOMER2_DESCP_CN);
        assertTrue(customer != null);

        // duplicate id
        try {
            customerFactory.createNewMasterDataBase(new MasterDataIdentity(
                    TestData.CUSTOMER2), TestData.CUSTOMER2_DESCP_CN);

            assertTrue(false);
        } catch (MasterDataIdentityExists e) {
        }

        // bank key
        BankKeyMasterData bankKey = (BankKeyMasterData) bankKeyFactory
                .createNewMasterDataBase(new MasterDataIdentity(
                        TestData.BANK_KEY), TestData.BANK_KEY_DESCP_CN);
        assertTrue(bankKey != null);

        // duplicate id
        try {
            bankKeyFactory.createNewMasterDataBase(new MasterDataIdentity(
                    TestData.BANK_KEY), TestData.BANK_KEY_DESCP_CN);
            assertTrue(false);
        } catch (MasterDataIdentityExists e) {
        }

        // bank account
        MasterDataIdentity bankKeyId = new MasterDataIdentity(TestData.BANK_KEY);
        BankAccountNumber accountNum = new BankAccountNumber(
                TestData.BANK_ACCOUNT_CMB_6235_ACC);
        BankAccountMasterData bankAcc = (BankAccountMasterData) bankAccountFactory
                .createNewMasterDataBase(new MasterDataIdentity(
                        TestData.BANK_ACCOUNT_CMB_6235),
                        TestData.BANK_ACCOUNT_CMB_6235_DESCP, accountNum,
                        bankKeyId, BankAccountType.SAVING_ACCOUNT);
        assertTrue(bankAcc != null);
        accountNum = new BankAccountNumber(TestData.BANK_ACCOUNT_CMB_6620_ACC);
        bankAcc = (BankAccountMasterData) bankAccountFactory
                .createNewMasterDataBase(new MasterDataIdentity(
                        TestData.BANK_ACCOUNT_CMB_6620),
                        TestData.BANK_ACCOUNT_CMB_6620_DESCP, accountNum,
                        bankKeyId, BankAccountType.CREDIT_CARD);
        assertTrue(bankAcc != null);

        // duplicate id
        try {
            bankAccountFactory.createNewMasterDataBase(new MasterDataIdentity(
                    TestData.BANK_ACCOUNT_CMB_6620),
                    TestData.BANK_ACCOUNT_CMB_6620_DESCP, accountNum, bankKey,
                    BankAccountType.SAVING_ACCOUNT);
            assertTrue(false);
            assertEquals(null, vendor);
        } catch (MasterDataIdentityExists e) {
        }

        // business area
        BusinessAreaMasterData businessArea = (BusinessAreaMasterData) businessFactory
                .createNewMasterDataBase(new MasterDataIdentity(
                        TestData.BUSINESS_AREA_ENTERTAIN),
                        TestData.BUSINESS_AREA_ENTERTAIN_DESCP_CN,
                        CriticalLevel.MEDIUM);
        assertTrue(businessArea != null);
        businessArea = (BusinessAreaMasterData) businessFactory
                .createNewMasterDataBase(new MasterDataIdentity(
                        TestData.BUSINESS_AREA_WORK),
                        TestData.BUSINESS_AREA_WORK_DESCP_CN, CriticalLevel.HIGH);
        assertTrue(businessArea != null);
        businessArea = (BusinessAreaMasterData) businessFactory
                .createNewMasterDataBase(new MasterDataIdentity(
                        TestData.BUSINESS_AREA_SNACKS),
                        TestData.BUSINESS_AREA_SNACKS_DESCP_CN, CriticalLevel.LOW);
        assertTrue(businessArea != null);

        // duplicate id
        try {
            businessArea = (BusinessAreaMasterData) businessFactory
                    .createNewMasterDataBase(new MasterDataIdentity(
                            TestData.BUSINESS_AREA_SNACKS),
                            TestData.BUSINESS_AREA_SNACKS_DESCP_CN,
                            CriticalLevel.LOW);
            assertTrue(false);
        } catch (MasterDataIdentityExists e) {
        }

        // G/L account
        MasterDataIdentity bankAccId = new MasterDataIdentity(
                TestData.BANK_ACCOUNT_CMB_6235);
        GLAccountMasterData glAccount = (GLAccountMasterData) accountFactory
                .createNewMasterDataBase(new MasterDataIdentity_GLAccount(
                        TestData.GL_ACCOUNT_BANK),
                        TestData.GL_ACCOUNT_BANK_DESCP_CN, bankAccId);
        assertTrue(glAccount != null);
        glAccount = (GLAccountMasterData) accountFactory
                .createNewMasterDataBase(new MasterDataIdentity_GLAccount(
                        TestData.GL_ACCOUNT_CASH),
                        TestData.GL_ACCOUNT_CASH_DESCP_CN);
        assertTrue(glAccount != null);
        glAccount = (GLAccountMasterData) accountFactory
                .createNewMasterDataBase(new MasterDataIdentity_GLAccount(
                        TestData.GL_ACCOUNT_COST),
                        TestData.GL_ACCOUNT_COST_DESCP_CN);
        assertTrue(glAccount != null);
        glAccount = (GLAccountMasterData) accountFactory
                .createNewMasterDataBase(new MasterDataIdentity_GLAccount(
                        TestData.GL_ACCOUNT_EQUITY),
                        TestData.GL_ACCOUNT_EQUITY_DESCP_CN);
        assertTrue(glAccount != null);
        glAccount = (GLAccountMasterData) accountFactory
                .createNewMasterDataBase(new MasterDataIdentity_GLAccount(
                        TestData.GL_ACCOUNT_REV), TestData.GL_ACCOUNT_REV_DESCP_CN);
        assertTrue(glAccount != null);

        // duplicate id
        try {
            accountFactory.createNewMasterDataBase(
                    new MasterDataIdentity_GLAccount(TestData.GL_ACCOUNT_REV),
                    TestData.GL_ACCOUNT_REV_DESCP_CN);
            assertTrue(false);
            assertEquals(null, glAccount);
        } catch (MasterDataIdentityExists e) {
        }

        return coreDriver;
    }
}
