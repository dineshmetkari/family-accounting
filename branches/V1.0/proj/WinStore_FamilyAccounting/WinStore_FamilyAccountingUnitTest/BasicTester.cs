using Microsoft.VisualStudio.TestPlatform.UnitTestFramework;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WinStore_FamilyAccountingCore.Exceptions;
using WinStore_FamilyAccountingCore.MasterData;
using WinStore_FamilyAccountingCore.Transaction;
using WinStore_FamilyAccountingCore.Utilities;
using WinStore_FamilyAccountingUnitTest.Utils;

namespace WinStore_FamilyAccountingUnitTest
{
    [TestClass]
    public class BasicTester
    {
        /// <summary>
        /// Test bank account number
        /// </summary>
        [TestMethod]
        public void TestBankAccountNumber()
        {
            // test length
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i <= 16; ++i)
            {
                builder.Append('0');
                try
                {
                    char[] testCase = builder.ToString().ToCharArray();
                    BankAccountNumber test = new BankAccountNumber(testCase);
                    test.ToString();
                }
                catch (Exception e)
                {
                    Assert.Fail(e.Message);
                }
            }

            // test number
            try
            {
                char[] testCase = "123456789".ToCharArray();
                BankAccountNumber test = new BankAccountNumber(testCase);
                Assert.AreEqual(test.ToString(), "0000000123456789");
            }
            catch (Exception e)
            {
                Assert.Fail(e.Message);
            }

            // test number
            try
            {
                char[] testCase = "0000000000000000".ToCharArray();
                BankAccountNumber test = new BankAccountNumber(testCase);
                Assert.AreEqual(test.ToString(), "0000000000000000");
            }
            catch (Exception e)
            {
                Assert.Fail(e.Message);
            }

            // test AreEquals
            try
            {
                char[] testCase = "123".ToCharArray();
                BankAccountNumber test1 = new BankAccountNumber(testCase);
                BankAccountNumber test2 = new BankAccountNumber(testCase);
                Assert.AreEqual(test1, test2);
                Assert.AreEqual(test1.GetHashCode(), test2.GetHashCode());
            }
            catch (Exception e)
            {
                Assert.Fail(e.Message);
            }

            // zero length
            try
            {
                char[] zeroTest = "".ToCharArray();
                BankAccountNumber test = new BankAccountNumber(zeroTest);
                test.ToString();
                Assert.Fail("IdentityNoData should occur when length is zero");
            }
            catch (IdentityNoData)
            {
                // pass
            }
            catch (Exception)
            {
                Assert.Fail("IdentityNoData should occur when length is zero, but it is other exception");
            }

            // format error
            try
            {
                char[] testCase = "a?".ToCharArray();
                BankAccountNumber test = new BankAccountNumber(testCase);
                test.ToString();
                Assert.Fail("IdentityNoData should occur when there is no data");
            }
            catch (IdentityInvalidChar)
            {
                // pass
            }
            catch (Exception)
            {
                Assert.Fail(
                    "IdentityInvalidChar should occur when length is zero, but it is other exception");
            }

            // too long
            try
            {
                char[] testCase = "12345678901234567".ToCharArray();
                BankAccountNumber test = new BankAccountNumber(testCase);
                test.ToString();
                Assert.Fail("IdentityNoData should occur when there is no data");
            }
            catch (IdentityTooLong)
            {
                // pass
            }
            catch (Exception)
            {
                Assert.Fail(
                    "IdentityInvalidChar should occur when length is zero, but it is other exception");
            }

        }

        [TestMethod]
        public void TestCurrencyAmount()
        {
            // test to string
            CurrencyAmount amount = new CurrencyAmount(123.45);
            Assert.AreEqual("123.45", amount.ToString());

            // test parse
            amount = CurrencyAmount.Parse("123.45");
            Assert.AreEqual("123.45", amount.ToString());

            // test add
            CurrencyAmount amount2 = new CurrencyAmount(543.21);
            amount = new CurrencyAmount(123.45);
            Assert.AreEqual("666.66", CurrencyAmount.Add(amount, amount2).ToString());

            amount.AddTo(amount2);
            Assert.AreEqual("666.66", amount.ToString());

            // test minus
            amount2 = new CurrencyAmount(543.21);
            amount = new CurrencyAmount(123.45);
            Assert.AreEqual("-419.76", CurrencyAmount.Minus(amount, amount2).ToString());
            Assert.AreEqual("419.76", CurrencyAmount.Minus(amount2, amount).ToString());
            Assert.AreEqual("0.00", CurrencyAmount.Minus(amount2, amount2).ToString());

            // is zero
            Assert.AreEqual(true, CurrencyAmount.Minus(amount2, amount2).IsZero());
        }

        [TestMethod]
        public void TestDocumentIdentity()
        {
            DocumentNumber docNum = new DocumentNumber(
                TestUtilities.TEST_DOC_NUM.ToCharArray());
            DocumentIdentity id = new DocumentIdentity(docNum, 2012, 07);
            String idStr = id.ToString();
            Assert.AreEqual(TestUtilities.TEST_DOC_ID, idStr);
            DocumentIdentity newId = DocumentIdentity.Parse(idStr);
            Assert.AreEqual(newId, id);
        }

        [TestMethod]
        public void TestMasterDataIdentity()
        {
            MasterDataIdentity test;
            // test length
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i <= 10; ++i)
            {
                builder.Append('A');
                try
                {
                    test = new MasterDataIdentity(builder.ToString());
                    test.ToString();
                }
                catch (Exception e)
                {
                    Assert.Fail(e.Message);
                }
            }

            // test low case
            test = new MasterDataIdentity("abcdefg");
            Assert.AreEqual(test.ToString(), "000ABCDEFG");


            // test number
            test = new MasterDataIdentity("123456789");
            Assert.AreEqual(test.ToString(), "0123456789");

            // test '_'
            test = new MasterDataIdentity("_123456789");
            Assert.AreEqual(test.ToString(), "_123456789");


            // test equals
            MasterDataIdentity test1 = new MasterDataIdentity("123");
            MasterDataIdentity test2 = new MasterDataIdentity("123");
            Assert.AreEqual(test1, test2);
            Assert.AreEqual(test1.GetHashCode(), test2.GetHashCode());

            // zero length
            try
            {
                test = new MasterDataIdentity("");
                test.ToString();
                Assert.Fail("IdentityNoData should occur when length is zero");
            }
            catch (IdentityNoData)
            {
                // pass
            }
            catch (Exception)
            {
                Assert.Fail("IdentityNoData should occur when length is zero, but it is other exception");
            }

            // no data
            try
            {
                test = new MasterDataIdentity("00");
                test.ToString();
                Assert.Fail("IdentityNoData should occur when there is no data");
            }
            catch (IdentityNoData)
            {
                // pass
            }
            catch (Exception)
            {
                Assert.Fail("IdentityNoData should occur when length is zero, but it is other exception");
            }

            // format error
            try
            {
                test = new MasterDataIdentity("a?");
                test.ToString();
                Assert.Fail("IdentityNoData should occur when there is no data");
            }
            catch (IdentityInvalidChar)
            {
                // pass
            }
            catch (Exception)
            {
                Assert.Fail("IdentityInvalidChar should occur when length is zero, but it is other exception");
            }

            // too long
            try
            {
                test = new MasterDataIdentity("12345678901");
                test.ToString();
                Assert.Fail("IdentityNoData should occur when there is no data");
            }
            catch (IdentityTooLong)
            {
                // pass
            }
            catch (Exception)
            {
                Assert.Fail("IdentityInvalidChar should occur when length is zero, but it is other exception");
            }

            // identity for gl account
            MasterDataIdentity_GLAccount glAccountTest = new MasterDataIdentity_GLAccount(
                    "113100");
            Assert.AreEqual(glAccountTest.ToString(), "0000113100");


            // identity for gl account
            // format error
            try
            {
                glAccountTest = new MasterDataIdentity_GLAccount("abcd");
                test.ToString();
                Assert.Fail("IdentityNoData should occur when there is no data");
            }
            catch (IdentityInvalidChar)
            {
                // pass
            }
            catch (Exception)
            {
                Assert.Fail("IdentityInvalidChar should occur when length is zero, but it is other exception");
            }

        }
    }
}
