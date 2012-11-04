using Microsoft.VisualStudio.TestPlatform.UnitTestFramework;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WinStore_FamilyAccountingCore;
using WinStore_FamilyAccountingCore.Exceptions;
using WinStore_FamilyAccountingCore.MasterData;
using WinStore_FamilyAccountingCore.Utilities;

namespace WinStore_FamilyAccountingUnitTest.Utils
{
    public class MasterDataCreater
    {
        public static void CreateMasterData(CoreDriver coreDriver)
        {

            ///
            /// check the factory is initialized, and the factory with no master data
            /// 
            MasterDataManagement masterDataManagement = coreDriver.MdMgmt;

            // vendor
            VendorMasterDataFactory vendorFactory = (VendorMasterDataFactory)masterDataManagement
                    .GetMasterDataFactory(MasterDataType.VENDOR);
            Assert.AreEqual(0, vendorFactory.MasterDataCount);
            // customer
            CustomerMasterDataFactory customerFactory = (CustomerMasterDataFactory)masterDataManagement
                    .GetMasterDataFactory(MasterDataType.CUSTOMER);
            Assert.AreEqual(0, customerFactory.MasterDataCount);
            // business area
            BusinessAreaMasterDataFactory businessFactory = (BusinessAreaMasterDataFactory)masterDataManagement
                    .GetMasterDataFactory(MasterDataType.BUSINESS_AREA);
            Assert.AreEqual(0, businessFactory.MasterDataCount);
            // bank key
            BankKeyMasterDataFactory bankKeyFactory = (BankKeyMasterDataFactory)masterDataManagement
                    .GetMasterDataFactory(MasterDataType.BANK_KEY);
            Assert.AreEqual(0, bankKeyFactory.MasterDataCount);
            // bank account
            BankAccountMasterDataFactory bankAccountFactory = (BankAccountMasterDataFactory)masterDataManagement
                    .GetMasterDataFactory(MasterDataType.BANK_ACCOUNT);
            Assert.AreEqual(0, bankAccountFactory.MasterDataCount);
            // GL account
            GLAccountMasterDataFactory accountFactory = (GLAccountMasterDataFactory)masterDataManagement
                    .GetMasterDataFactory(MasterDataType.GL_ACCOUNT);
            Assert.AreEqual(0, accountFactory.MasterDataCount);

            /** add master data entities */
            // vendor
            VendorMasterData vendor = (VendorMasterData)vendorFactory
                    .CreateNewMasterDataBase(new MasterDataIdentity(
                            TestData.VENDOR_BUS), TestData.VENDOR_BUS_DESCP);
            Assert.IsTrue(vendor != null);
            vendor = (VendorMasterData)vendorFactory.CreateNewMasterDataBase(
                    new MasterDataIdentity(TestData.VENDOR_SUBWAY),
                    TestData.VENDOR_SUBWAY_DESCP);
            Assert.IsTrue(vendor != null);

            // duplicate id
            try
            {
                vendorFactory.CreateNewMasterDataBase(new MasterDataIdentity(
                        TestData.VENDOR_SUBWAY), TestData.VENDOR_SUBWAY_DESCP);
                Assert.Fail("Duplicate Id");
            }
            catch (MasterDataIdentityExists)
            {

            }

            // customer
            CustomerMasterData customer = (CustomerMasterData)customerFactory
                    .CreateNewMasterDataBase(new MasterDataIdentity(
                            TestData.CUSTOMER1), TestData.CUSTOMER1_DESCP);
            Assert.IsTrue(customer != null);
            customer = (CustomerMasterData)customerFactory
                    .CreateNewMasterDataBase(new MasterDataIdentity(
                            TestData.CUSTOMER2), TestData.CUSTOMER2_DESCP);
            Assert.IsTrue(customer != null);

            // duplicate id
            try
            {
                customerFactory.CreateNewMasterDataBase(new MasterDataIdentity(
                        TestData.CUSTOMER2), TestData.CUSTOMER2_DESCP);

                Assert.Fail("Duplicate Id");
            }
            catch (MasterDataIdentityExists)
            {
            }

            // bank key
            BankKeyMasterData bankKey = (BankKeyMasterData)bankKeyFactory
                    .CreateNewMasterDataBase(new MasterDataIdentity(
                            TestData.BANK_KEY), TestData.BANK_KEY_DESCP);
            Assert.IsTrue(bankKey != null);

            // duplicate id
            try
            {
                bankKeyFactory.CreateNewMasterDataBase(new MasterDataIdentity(
                        TestData.BANK_KEY), TestData.BANK_KEY_DESCP);
                Assert.Fail("Duplicate Id");
            }
            catch (MasterDataIdentityExists)
            {
            }

            // bank account
            MasterDataIdentity bankKeyId = new MasterDataIdentity(TestData.BANK_KEY);
            BankAccountNumber accountNum = new BankAccountNumber(
                    TestData.BANK_ACCOUNT_CMB_6235_ACC);
            BankAccountMasterData bankAcc = (BankAccountMasterData)bankAccountFactory
                    .CreateNewMasterDataBase(new MasterDataIdentity(
                            TestData.BANK_ACCOUNT_CMB_6235),
                            TestData.BANK_ACCOUNT_CMB_6235_DESCP, accountNum,
                            bankKeyId, BankAccountType.SAVING_ACCOUNT);
            Assert.IsTrue(bankAcc != null);
            accountNum = new BankAccountNumber(TestData.BANK_ACCOUNT_CMB_6620_ACC);
            bankAcc = (BankAccountMasterData)bankAccountFactory
                    .CreateNewMasterDataBase(new MasterDataIdentity(
                            TestData.BANK_ACCOUNT_CMB_6620),
                            TestData.BANK_ACCOUNT_CMB_6620_DESCP, accountNum,
                            bankKeyId, BankAccountType.CREDIT_CARD);
            Assert.IsTrue(bankAcc != null);

            // duplicate id
            try
            {
                bankAccountFactory.CreateNewMasterDataBase(new MasterDataIdentity(
                        TestData.BANK_ACCOUNT_CMB_6620),
                        TestData.BANK_ACCOUNT_CMB_6620_DESCP, accountNum, bankKey,
                        BankAccountType.SAVING_ACCOUNT);
                Assert.Fail("Duplicate Id");
            }
            catch (MasterDataIdentityExists)
            {
            }

            // business area
            BusinessAreaMasterData businessArea = (BusinessAreaMasterData)businessFactory
                    .CreateNewMasterDataBase(new MasterDataIdentity(
                            TestData.BUSINESS_AREA_ENTERTAIN),
                            TestData.BUSINESS_AREA_ENTERTAIN_DESCP,
                            CriticalLevel.MEDIUM);
            Assert.IsTrue(businessArea != null);
            businessArea = (BusinessAreaMasterData)businessFactory
                    .CreateNewMasterDataBase(new MasterDataIdentity(
                            TestData.BUSINESS_AREA_WORK),
                            TestData.BUSINESS_AREA_WORK_DESCP, CriticalLevel.HIGH);
            Assert.IsTrue(businessArea != null);
            businessArea = (BusinessAreaMasterData)businessFactory
                    .CreateNewMasterDataBase(new MasterDataIdentity(
                            TestData.BUSINESS_AREA_SNACKS),
                            TestData.BUSINESS_AREA_SNACKS_DESCP, CriticalLevel.LOW);
            Assert.IsTrue(businessArea != null);

            // duplicate id
            try
            {
                businessArea = (BusinessAreaMasterData)businessFactory
                        .CreateNewMasterDataBase(new MasterDataIdentity(
                                TestData.BUSINESS_AREA_SNACKS),
                                TestData.BUSINESS_AREA_SNACKS_DESCP,
                                CriticalLevel.LOW);
                Assert.Fail("Duplicate Id");
            }
            catch (MasterDataIdentityExists)
            {
            }

            // G/L account
            MasterDataIdentity bankAccId = new MasterDataIdentity(
                    TestData.BANK_ACCOUNT_CMB_6235);
            GLAccountMasterData glAccount = (GLAccountMasterData)accountFactory
                    .CreateNewMasterDataBase(new MasterDataIdentity_GLAccount(
                            TestData.GL_ACCOUNT_BANK),
                            TestData.GL_ACCOUNT_BANK_DESCP, bankAccId);
            Assert.IsTrue(glAccount != null);

            glAccount = (GLAccountMasterData)accountFactory
                    .CreateNewMasterDataBase(new MasterDataIdentity_GLAccount(
                            TestData.GL_ACCOUNT_CASH),
                            TestData.GL_ACCOUNT_CASH_DESCP);
            Assert.IsTrue(glAccount != null);
            glAccount.SetInitAmount(TestData.AMOUNT_VENDOR);

            glAccount = (GLAccountMasterData)accountFactory
                    .CreateNewMasterDataBase(new MasterDataIdentity_GLAccount(
                            TestData.GL_ACCOUNT_COST),
                            TestData.GL_ACCOUNT_COST_DESCP);
            Assert.IsTrue(glAccount != null);
            glAccount = (GLAccountMasterData)accountFactory
                    .CreateNewMasterDataBase(new MasterDataIdentity_GLAccount(
                            TestData.GL_ACCOUNT_EQUITY),
                            TestData.GL_ACCOUNT_EQUITY_DESCP);
            Assert.IsTrue(glAccount != null);
            glAccount = (GLAccountMasterData)accountFactory
                    .CreateNewMasterDataBase(new MasterDataIdentity_GLAccount(
                            TestData.GL_ACCOUNT_REV), TestData.GL_ACCOUNT_REV_DESCP);
            Assert.IsTrue(glAccount != null);

            // duplicate id
            try
            {
                accountFactory.CreateNewMasterDataBase(
                        new MasterDataIdentity_GLAccount(TestData.GL_ACCOUNT_REV),
                        TestData.GL_ACCOUNT_REV_DESCP);
                Assert.Fail("Duplicate Id");
            }
            catch (MasterDataIdentityExists)
            {
            }
        }
    }
}
