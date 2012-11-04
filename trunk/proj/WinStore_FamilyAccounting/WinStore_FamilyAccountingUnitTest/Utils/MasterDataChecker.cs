using Microsoft.VisualStudio.TestPlatform.UnitTestFramework;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WinStore_FamilyAccountingCore;
using WinStore_FamilyAccountingCore.MasterData;
using WinStore_FamilyAccountingCore.Utilities;

namespace WinStore_FamilyAccountingUnitTest.Utils
{
    public class MasterDataChecker
    {
        public static void CheckMasterData(CoreDriver _coreDriver)
        {
            Assert.AreEqual(true, _coreDriver.IsInitialize);

            MasterDataManagement masterDataManagement = _coreDriver.MdMgmt;

            List<MasterDataBase> datas;

            // check vendor data
            VendorMasterDataFactory vendorFactory = (VendorMasterDataFactory)masterDataManagement
                    .GetMasterDataFactory(MasterDataType.VENDOR);
            Assert.AreEqual(2, vendorFactory.MasterDataCount);
            datas = vendorFactory.AllEntities;
            Assert.AreEqual(TestData.VENDOR_BUS, datas[0].Identity.ToString());
            Assert.AreEqual(TestData.VENDOR_BUS_DESCP, datas[0].Descp);
            Assert.AreEqual(TestData.VENDOR_SUBWAY, datas[1].Identity.ToString());
            Assert.AreEqual(TestData.VENDOR_SUBWAY_DESCP, datas[1].Descp);

            // check customer data
            CustomerMasterDataFactory customerFactory = (CustomerMasterDataFactory)masterDataManagement
                    .GetMasterDataFactory(MasterDataType.CUSTOMER);
            Assert.AreEqual(2, customerFactory.MasterDataCount);
            datas = customerFactory.AllEntities;
            Assert.AreEqual(TestData.CUSTOMER1, datas[0].Identity.ToString());
            Assert.AreEqual(TestData.CUSTOMER1_DESCP, datas[0].Descp);
            Assert.AreEqual(TestData.CUSTOMER2, datas[1].Identity.ToString());
            Assert.AreEqual(TestData.CUSTOMER2_DESCP, datas[1].Descp);

            // check business area
            BusinessAreaMasterDataFactory businessAreaFactory = (BusinessAreaMasterDataFactory)masterDataManagement
                    .GetMasterDataFactory(MasterDataType.BUSINESS_AREA);
            Assert.AreEqual(3, businessAreaFactory.MasterDataCount);
            datas = businessAreaFactory.AllEntities;
            Assert.AreEqual(TestData.BUSINESS_AREA_WORK, datas[0].Identity
                    .ToString());
            Assert.AreEqual(TestData.BUSINESS_AREA_WORK_DESCP, datas[0].Descp);
            Assert.AreEqual(CriticalLevel.HIGH,
                    ((BusinessAreaMasterData)datas[0]).CriLevel);
            Assert.AreEqual(TestData.BUSINESS_AREA_SNACKS, datas[1].Identity
                    .ToString());
            Assert.AreEqual(TestData.BUSINESS_AREA_SNACKS_DESCP, datas[1].Descp);
            Assert.AreEqual(CriticalLevel.LOW,
                    ((BusinessAreaMasterData)datas[1]).CriLevel);
            Assert.AreEqual(TestData.BUSINESS_AREA_ENTERTAIN, datas[2].Identity
                    .ToString());
            Assert.AreEqual(TestData.BUSINESS_AREA_ENTERTAIN_DESCP,
                    datas[2].Descp);
            Assert.AreEqual(CriticalLevel.MEDIUM,
                    ((BusinessAreaMasterData)datas[2]).CriLevel);

            // check bank key
            BankKeyMasterDataFactory bankKeyFactory = (BankKeyMasterDataFactory)masterDataManagement
                    .GetMasterDataFactory(MasterDataType.BANK_KEY);
            Assert.AreEqual(1, bankKeyFactory.MasterDataCount);
            datas = bankKeyFactory.AllEntities;
            Assert.AreEqual(TestData.BANK_KEY, datas[0].Identity.ToString());
            Assert.AreEqual(TestData.BANK_KEY_DESCP, datas[0].Descp);

            // check bank account
            BankAccountMasterDataFactory bankAccountFactory = (BankAccountMasterDataFactory)masterDataManagement
                    .GetMasterDataFactory(MasterDataType.BANK_ACCOUNT);
            Assert.AreEqual(2, bankAccountFactory.MasterDataCount);
            datas = bankAccountFactory.AllEntities;
            Assert.AreEqual(TestData.BANK_ACCOUNT_CMB_6235, datas[0].Identity
                    .ToString());
            Assert.AreEqual(TestData.BANK_ACCOUNT_CMB_6235_DESCP, datas[0].Descp);
            Assert.AreEqual(TestData.BANK_ACCOUNT_CMB_6235_ACC,
                    ((BankAccountMasterData)datas[0]).AccountNumber
                            .ToString());
            Assert.AreEqual(BankAccountType.SAVING_ACCOUNT,
                    ((BankAccountMasterData)datas[0]).BankAccType);
            Assert.AreEqual(TestData.BANK_ACCOUNT_CMB_6620, datas[1].Identity
                    .ToString());
            Assert.AreEqual(TestData.BANK_ACCOUNT_CMB_6620_DESCP, datas[1].Descp);
            Assert.AreEqual(TestData.BANK_ACCOUNT_CMB_6620_ACC,
                    ((BankAccountMasterData)datas[1]).AccountNumber
                            .ToString());
            Assert.AreEqual(BankAccountType.CREDIT_CARD,
                    ((BankAccountMasterData)datas[1]).BankAccType);

            // check G/L account
            GLAccountMasterDataFactory accountFactory = (GLAccountMasterDataFactory)masterDataManagement
                    .GetMasterDataFactory(MasterDataType.GL_ACCOUNT);
            Assert.AreEqual(5, accountFactory.MasterDataCount);
            datas = accountFactory.AllEntities;
            Assert.AreEqual(TestData.GL_ACCOUNT_CASH, datas[0].Identity
                    .ToString());
            Assert.AreEqual(TestData.GL_ACCOUNT_CASH_DESCP, datas[0].Descp);
            Assert.AreEqual(TestData.AMOUNT_VENDOR, ((GLAccountMasterData)datas[0]).InitAmount);

            Assert.AreEqual(TestData.GL_ACCOUNT_BANK, datas[1].Identity
                    .ToString());
            Assert.AreEqual(TestData.GL_ACCOUNT_BANK_DESCP, datas[1].Descp);
            Assert.AreEqual(TestData.BANK_ACCOUNT_CMB_6235,
                    ((GLAccountMasterData)datas[1]).BankAccount.ToString());
            Assert.AreEqual(new CurrencyAmount(), ((GLAccountMasterData)datas[1]).InitAmount);

            Assert.AreEqual(TestData.GL_ACCOUNT_EQUITY, datas[2].Identity
                    .ToString());
            Assert.AreEqual(TestData.GL_ACCOUNT_EQUITY_DESCP, datas[2].Descp);
            Assert.AreEqual(new CurrencyAmount(), ((GLAccountMasterData)datas[2]).InitAmount);

            Assert.AreEqual(TestData.GL_ACCOUNT_REV, datas[3].Identity
                    .ToString());
            Assert.AreEqual(TestData.GL_ACCOUNT_REV_DESCP, datas[3].Descp);
            Assert.AreEqual(new CurrencyAmount(), ((GLAccountMasterData)datas[3]).InitAmount);

            Assert.AreEqual(TestData.GL_ACCOUNT_COST, datas[4].Identity
                    .ToString());
            Assert.AreEqual(TestData.GL_ACCOUNT_COST_DESCP, datas[4].Descp);
            Assert.AreEqual(new CurrencyAmount(), ((GLAccountMasterData)datas[4]).InitAmount);
        }
    }
}
