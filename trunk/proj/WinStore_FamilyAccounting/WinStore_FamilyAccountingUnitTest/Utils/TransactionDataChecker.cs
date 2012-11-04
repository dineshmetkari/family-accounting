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
    public class TransactionDataChecker
    {
        /// <summary>
        /// check transaction data
        /// </summary>
        /// <param name="coreDriver"></param>
        public static void CheckTransactionData(CoreDriver coreDriver)
        {
            TransactionDataManagement transManagement = coreDriver.TransMgmt;
            MonthIdentity[] monthIds = coreDriver.MonthIds;
            // Assert.AreEqual(2, monthIds.length);

            MonthLedger ledger07 = transManagement.GetLedger(monthIds[0]);
            MonthLedger ledger08 = transManagement.GetLedger(monthIds[1]);

            CheckLedger2012_07(ledger07.Entities);
            CheckLedger2012_08(ledger08.Entities);
        }

        /// <summary>
        /// check ledger
        /// </summary>
        /// <param name="docs"></param>
        public static void CheckLedger2012_07(List<HeadEntity> docs)
        {
            Assert.AreEqual(3, docs.Count);

            checkVendorDoc(docs[0]);
            checkCustomerDoc(docs[1]);
            checkGLDoc(docs[2]);
        }

        /// <summary>
        /// check vendor document
        /// </summary>
        /// <param name="vendorDoc"></param>
        private static void checkVendorDoc(HeadEntity vendorDoc)
        {
            // check document number
            Assert.AreEqual(TestData.DOC_NUM1, vendorDoc.DocNumber
                    .ToString());
            // check posting date
            Assert.AreEqual(TestData.DATE_2012_07, vendorDoc.PstDate.ToString(MasterDataUtils.DATE_FORMAT));
            MonthIdentity monthId = vendorDoc.MonthID;
            Assert.AreEqual(TestData.YEAR, monthId.FiscalYear);
            Assert.AreEqual(TestData.MONTH_07, monthId.FiscalMonth);
            // check document type
            Assert.AreEqual(DocumentType.VENDOR_INVOICE, vendorDoc.DocType);
            // check text
            Assert.AreEqual(TestData.TEXT_VENDOR_DOC, vendorDoc.DocText);
            // check is reversed
            Assert.AreEqual(false, vendorDoc.IsReversed);

            Assert.AreEqual(2, vendorDoc.ItemCount);
            List<ItemEntity> items = vendorDoc.Items;
            // check the vendor item
            Assert.AreEqual(0, items[0].LineNum);
            Assert.AreEqual(AccountType.VENDOR, items[0].AccType);
            Assert.AreEqual(CreditDebitIndicator.CREDIT, items[0].CdIndicator);
            Assert.AreEqual(TestData.AMOUNT_VENDOR, items[0].Amount);
            Assert.AreEqual(null, items[0].Customer);
            Assert.AreEqual(TestData.VENDOR_BUS, items[0].Vendor.ToString());
            Assert.AreEqual(TestData.GL_ACCOUNT_CASH, items[0].GLAccount
                    .ToString());
            Assert.AreEqual(null, items[0].BusinessArea);

            // check cost item
            Assert.AreEqual(1, items[1].LineNum);
            Assert.AreEqual(AccountType.GL_ACCOUNT, items[1].AccType);
            Assert.AreEqual(CreditDebitIndicator.DEBIT, items[1].CdIndicator);
            Assert.AreEqual(TestData.AMOUNT_VENDOR, items[1].Amount);
            Assert.AreEqual(null, items[1].Customer);
            Assert.AreEqual(null, items[1].Vendor);
            Assert.AreEqual(TestData.GL_ACCOUNT_COST, items[1].GLAccount
                    .ToString());
            Assert.AreEqual(TestData.BUSINESS_AREA_WORK, items[1].BusinessArea
                    .ToString());
        }

        /// <summary>
        /// check customer document
        /// </summary>
        /// <param name="customerDoc"></param>
        private static void checkCustomerDoc(HeadEntity customerDoc)
        {
            // check document number
            Assert.AreEqual(TestData.DOC_NUM2, customerDoc.DocNumber
                    .ToString());
            // check posting date
            Assert.AreEqual(TestData.DATE_2012_07, customerDoc.PstDate.ToString(
                MasterDataUtils.DATE_FORMAT));
            MonthIdentity monthId = customerDoc.MonthID;
            Assert.AreEqual(TestData.YEAR, monthId.FiscalYear);
            Assert.AreEqual(TestData.MONTH_07, monthId.FiscalMonth);
            // check document type
            Assert.AreEqual(DocumentType.CUSTOMER_INVOICE,
                    customerDoc.DocType);
            // check text
            Assert.AreEqual(TestData.TEXT_CUSTOMER_DOC, customerDoc.DocText);
            // check is reversed
            Assert.AreEqual(false, customerDoc.IsReversed);

            Assert.AreEqual(2, customerDoc.ItemCount);
            List<ItemEntity> items = customerDoc.Items;
            // check revenue item
            Assert.AreEqual(0, items[0].LineNum);
            Assert.AreEqual(AccountType.GL_ACCOUNT, items[0].AccType);
            Assert.AreEqual(CreditDebitIndicator.CREDIT, items[0].CdIndicator);
            Assert.AreEqual(TestData.AMOUNT_CUSTOMER, items[0].Amount);
            Assert.AreEqual(null, items[0].Customer);
            Assert.AreEqual(null, items[0].Vendor);
            Assert.AreEqual(TestData.GL_ACCOUNT_REV, items[0].GLAccount
                    .ToString());
            Assert.AreEqual(null, items[0].BusinessArea);

            // check customer item
            Assert.AreEqual(1, items[1].LineNum);
            Assert.AreEqual(AccountType.CUSTOMER, items[1].AccType);
            Assert.AreEqual(CreditDebitIndicator.DEBIT, items[1].CdIndicator);
            Assert.AreEqual(TestData.AMOUNT_CUSTOMER, items[1].Amount);
            Assert.AreEqual(TestData.CUSTOMER1, items[1].Customer.ToString());
            Assert.AreEqual(null, items[1].Vendor);
            Assert.AreEqual(TestData.GL_ACCOUNT_BANK, items[1].GLAccount
                    .ToString());
            Assert.AreEqual(null, items[1].BusinessArea);
        }

        /// <summary>
        /// check gl doc
        /// </summary>
        /// <param name="glDoc"></param>
        private static void checkGLDoc(HeadEntity glDoc)
        {
            // check document number
            Assert.AreEqual(TestData.DOC_NUM3, glDoc.DocNumber.ToString());
            // check posting date
            Assert.AreEqual(TestData.DATE_2012_07, glDoc.PstDate.ToString(MasterDataUtils.DATE_FORMAT));
            MonthIdentity monthId = glDoc.MonthID;
            Assert.AreEqual(TestData.YEAR, monthId.FiscalYear);
            Assert.AreEqual(TestData.MONTH_07, monthId.FiscalMonth);
            // check document type
            Assert.AreEqual(DocumentType.GL, glDoc.DocType);
            // check text
            Assert.AreEqual(TestData.TEXT_GL_DOC, glDoc.DocText);
            // check is reversed
            Assert.AreEqual(false, glDoc.IsReversed);

            Assert.AreEqual(2, glDoc.ItemCount);
            List<ItemEntity> items = glDoc.Items;
            // check the source item
            Assert.AreEqual(0, items[0].LineNum);
            Assert.AreEqual(AccountType.GL_ACCOUNT, items[0].AccType);
            Assert.AreEqual(CreditDebitIndicator.CREDIT, items[0].CdIndicator);
            Assert.AreEqual(TestData.AMOUNT_GL, items[0].Amount);
            Assert.AreEqual(null, items[0].Customer);
            Assert.AreEqual(null, items[0].Vendor);
            Assert.AreEqual(TestData.GL_ACCOUNT_BANK, items[0].GLAccount
                    .ToString());
            Assert.AreEqual(null, items[0].BusinessArea);

            // check destination item
            Assert.AreEqual(1, items[1].LineNum);
            Assert.AreEqual(AccountType.GL_ACCOUNT, items[1].AccType);
            Assert.AreEqual(CreditDebitIndicator.DEBIT, items[1].CdIndicator);
            Assert.AreEqual(TestData.AMOUNT_GL, items[1].Amount);
            Assert.AreEqual(null, items[1].Customer);
            Assert.AreEqual(null, items[1].Vendor);
            Assert.AreEqual(TestData.GL_ACCOUNT_CASH, items[1].GLAccount
                    .ToString());
            Assert.AreEqual(null, items[1].BusinessArea);
        }

        /// <summary>
        /// check ledger 2012 07
        /// </summary>
        /// <param name="docs"></param>
        public static void CheckLedger2012_08(List<HeadEntity> docs)
        {
            Assert.AreEqual(1, docs.Count);
            checkVendorDoc_08(docs[0]);
        }


        /// <summary>
        /// check vendor doc in 08
        /// </summary>
        /// <param name="vendorDoc"></param>
        private static void checkVendorDoc_08(HeadEntity vendorDoc)
        {
            // check document number
            Assert.AreEqual(TestData.DOC_NUM1, vendorDoc.DocNumber
                    .ToString());
            // check posting date
            Assert.AreEqual(TestData.DATE_2012_08, vendorDoc.PstDate.ToString(MasterDataUtils.DATE_FORMAT));
            MonthIdentity monthId = vendorDoc.MonthID;
            Assert.AreEqual(TestData.YEAR, monthId.FiscalYear);
            Assert.AreEqual(TestData.MONTH_08, monthId.FiscalMonth);
            // check document type
            Assert.AreEqual(DocumentType.VENDOR_INVOICE, vendorDoc.DocType);
            // check text
            Assert.AreEqual(TestData.TEXT_VENDOR_DOC, vendorDoc.DocText);
            // check is reversed
            Assert.AreEqual(true, vendorDoc.IsReversed);

            Assert.AreEqual(2, vendorDoc.ItemCount);
            List<ItemEntity> items = vendorDoc.Items;
            // check the vendor item
            Assert.AreEqual(0, items[0].LineNum);
            Assert.AreEqual(AccountType.VENDOR, items[0].AccType);
            Assert.AreEqual(CreditDebitIndicator.CREDIT, items[0].CdIndicator);
            Assert.AreEqual(TestData.AMOUNT_VENDOR, items[0].Amount);
            Assert.AreEqual(null, items[0].Customer);
            Assert.AreEqual(TestData.VENDOR_BUS, items[0].Vendor.ToString());
            Assert.AreEqual(TestData.GL_ACCOUNT_CASH, items[0].GLAccount
                    .ToString());
            Assert.AreEqual(null, items[0].BusinessArea);

            // check cost item
            Assert.AreEqual(1, items[1].LineNum);
            Assert.AreEqual(AccountType.GL_ACCOUNT, items[1].AccType);
            Assert.AreEqual(CreditDebitIndicator.DEBIT, items[1].CdIndicator);
            Assert.AreEqual(TestData.AMOUNT_VENDOR, items[1].Amount);
            Assert.AreEqual(null, items[1].Customer);
            Assert.AreEqual(null, items[1].Vendor);
            Assert.AreEqual(TestData.GL_ACCOUNT_COST, items[1].GLAccount
                    .ToString());
            Assert.AreEqual(TestData.BUSINESS_AREA_WORK, items[1].BusinessArea
                    .ToString());
        }
    }
}
