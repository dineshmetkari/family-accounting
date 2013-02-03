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
    public class AccountReportBalanceChecker
    {
        public static void CheckAccountBalance(CoreDriver coreDriver)
        {
            TransactionDataManagement transMgmt = coreDriver.TransMgmt;
            GLAccountBalanceCollection balCol = transMgmt.AccountBalanceCol;

            MonthIdentity month07 = new MonthIdentity(2012, 7);
            MonthIdentity month08 = new MonthIdentity(2012, 8);

            MasterDataIdentity_GLAccount glAccount2 = new MasterDataIdentity_GLAccount(
                    TestData.GL_ACCOUNT_CASH);
            GLAccountBalanceItem balItem = balCol.GetBalanceItem(glAccount2);
            CurrencyAmount amount2 = new CurrencyAmount(123.45 - 23.45);
            Assert.AreEqual(amount2, balItem.Sum);
            Assert.AreEqual(amount2, balItem.GetAmount(month07));
            Assert.AreEqual(new CurrencyAmount(), balItem.GetAmount(month08));

            Assert.AreEqual(amount2, balCol.GetGroupBalance(GLAccountGroupENUM.CASH));
            Assert.AreEqual(amount2,
                    balCol.GetGroupBalance(GLAccountGroupENUM.CASH, month07, month07));
            Assert.AreEqual(new CurrencyAmount(),
                    balCol.GetGroupBalance(GLAccountGroupENUM.CASH, month08, month08));

            // cost
            Assert.AreEqual(new CurrencyAmount(123.45),
                    balCol.GetGroupBalance(GLAccountGroupENUM.COST_PURE));
            Assert.AreEqual(new CurrencyAmount(123.45), balCol.GetGroupBalance(
                    GLAccountGroupENUM.COST_PURE, month07, month07));
            Assert.AreEqual(new CurrencyAmount(), balCol.GetGroupBalance(
                    GLAccountGroupENUM.COST_PURE, month08, month08));

            // revenue
            Assert.AreEqual(new CurrencyAmount(-543.21),
                    balCol.GetGroupBalance(GLAccountGroupENUM.SALARY));
            Assert.AreEqual(new CurrencyAmount(-543.21),
                    balCol.GetGroupBalance(GLAccountGroupENUM.SALARY, month07, month07));
            Assert.AreEqual(new CurrencyAmount(),
                    balCol.GetGroupBalance(GLAccountGroupENUM.SALARY, month08, month08));
        }
    }
}
