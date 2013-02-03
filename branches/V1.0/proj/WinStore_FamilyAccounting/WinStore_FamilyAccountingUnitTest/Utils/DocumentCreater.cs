using Microsoft.VisualStudio.TestPlatform.UnitTestFramework;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WinStore_FamilyAccountingCore;
using WinStore_FamilyAccountingCore.MasterData;
using WinStore_FamilyAccountingCore.Transaction;
using WinStore_FamilyAccountingCore.Utilities;

namespace WinStore_FamilyAccountingUnitTest.Utils
{
    public class DocumentCreater
    {
        public static async Task<HeadEntity> CreateVendorDoc(CoreDriver coreDriver, DateTime date)
        {
            HeadEntity headEntity = new HeadEntity(coreDriver,
                    coreDriver.MdMgmt);
            headEntity.setPostingDate(date);
            headEntity.SetDocumentType(DocumentType.VENDOR_INVOICE);
            headEntity.SetDocText(TestData.TEXT_VENDOR_DOC);

            ItemEntity item1 = headEntity.CreateEntity();
            MasterDataIdentity_GLAccount rec_account = new MasterDataIdentity_GLAccount(
                    TestData.GL_ACCOUNT_CASH);
            item1.SetVendor(new MasterDataIdentity(TestData.VENDOR_BUS),
                    rec_account);
            item1.SetAmount(CreditDebitIndicator.CREDIT, TestData.AMOUNT_VENDOR);

            ItemEntity item2 = headEntity.CreateEntity();
            item2.SetGLAccount(new MasterDataIdentity_GLAccount(
                    TestData.GL_ACCOUNT_COST));
            item2.SetAmount(CreditDebitIndicator.DEBIT, TestData.AMOUNT_VENDOR);
            item2.SetBusinessArea(new MasterDataIdentity(
                    TestData.BUSINESS_AREA_WORK));

            try
            {
                await headEntity.SaveAsync(true);
            }
            catch (Exception e)
            {
                Assert.Fail(e.ToString());
            }
            return headEntity;
        }

        public static async Task<HeadEntity> CreateCustomerDoc(CoreDriver coreDriver, DateTime date)
        {
            HeadEntity headEntity = new HeadEntity(coreDriver,
                    coreDriver.MdMgmt);
            headEntity.setPostingDate(date);
            headEntity.SetDocumentType(DocumentType.CUSTOMER_INVOICE);
            headEntity.SetDocText(TestData.TEXT_CUSTOMER_DOC);

            ItemEntity item1 = headEntity.CreateEntity();
            item1.SetGLAccount(new MasterDataIdentity_GLAccount(
                    TestData.GL_ACCOUNT_REV));
            item1.SetAmount(CreditDebitIndicator.CREDIT,
                    TestData.AMOUNT_CUSTOMER);

            ItemEntity item2 = headEntity.CreateEntity();
            MasterDataIdentity_GLAccount account2 = new MasterDataIdentity_GLAccount(
                    TestData.GL_ACCOUNT_BANK);
            item2.SetCustomer(new MasterDataIdentity(TestData.CUSTOMER1),
                    account2);
            item2.SetAmount(CreditDebitIndicator.DEBIT,
                    TestData.AMOUNT_CUSTOMER);

            try
            {
                await headEntity.SaveAsync(true);
            }
            catch (Exception e)
            {
                Assert.Fail(e.ToString());
            }

            return headEntity;
        }

        public static async Task<HeadEntity> CreateGLDoc(CoreDriver coreDriver, DateTime date)
        {
            HeadEntity headEntity = new HeadEntity(coreDriver,
                    coreDriver.MdMgmt);
            headEntity.setPostingDate(date);
            headEntity.SetDocumentType(DocumentType.GL);
            headEntity.SetDocText(TestData.TEXT_GL_DOC);

            ItemEntity item1 = headEntity.CreateEntity();
            item1.SetGLAccount(new MasterDataIdentity_GLAccount(
                    TestData.GL_ACCOUNT_BANK));
            item1.SetAmount(CreditDebitIndicator.CREDIT, TestData.AMOUNT_GL);

            ItemEntity item2 = headEntity.CreateEntity();
            item2.SetGLAccount(new MasterDataIdentity_GLAccount(
                    TestData.GL_ACCOUNT_CASH));
            item2.SetAmount(CreditDebitIndicator.DEBIT, TestData.AMOUNT_GL);
            try
            {
                await headEntity.SaveAsync(true);
            }
            catch (Exception e)
            {
                Assert.Fail(e.ToString());
            }
            return headEntity;
        }
    }

}
