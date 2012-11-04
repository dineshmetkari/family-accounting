using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WinStore_FamilyAccountingCore.Utilities;

namespace WinStore_FamilyAccountingUnitTest.Utils
{
    public class TestData
    {
        // document text
        public static readonly String TEXT_VENDOR_DOC = "Traffic expense on work";
        public static readonly String TEXT_GL_DOC = "Get money from bank account";
        public static readonly String TEXT_CUSTOMER_DOC = "Salary";
        public static readonly String TEXT_VENDOR_DOC_CN = "工作交通花费";
        public static readonly String TEXT_GL_DOC_CN = "从银行取钱";
        public static readonly String TEXT_CUSTOMER_DOC_CN = "工资";

        // date & month
        public static readonly String DATE_2012_07 = "2012.07.02";
        public static readonly String DATE_2012_08 = "2012.08.02";
        public static readonly int YEAR = 2012;
        public static readonly int MONTH_07 = 7;
        public static readonly int MONTH_08 = 8;

        // document number
        public static readonly String DOC_NUM1 = "1000000001";
        public static readonly String DOC_NUM2 = "1000000002";
        public static readonly String DOC_NUM3 = "1000000003";
        public static readonly String DOC_NUM4 = "1000000004";

        // amount
        public static readonly CurrencyAmount AMOUNT_VENDOR = new CurrencyAmount(123.45);
        public static readonly CurrencyAmount AMOUNT_CUSTOMER = new CurrencyAmount(543.21);
        public static readonly CurrencyAmount AMOUNT_GL = new CurrencyAmount(100.0);
        public static readonly CurrencyAmount AMOUNT_EQUITY = new CurrencyAmount(419.76);

        public static readonly String VENDOR_BUS = "0000000BUS";
        public static readonly String VENDOR_SUBWAY = "0000SUBWAY";
        public static readonly String CUSTOMER1 = "00000000C1";
        public static readonly String CUSTOMER2 = "00000000C2";

        public static readonly String VENDOR_BUS_DESCP = "Bus";
        public static readonly String VENDOR_SUBWAY_DESCP = "Subway";
        public static readonly String VENDOR_BUS_DESCP_CN = "公交车";
        public static readonly String VENDOR_SUBWAY_DESCP_CN = "地铁";
        public static readonly String CUSTOMER1_DESCP = "Customer 1";
        public static readonly String CUSTOMER2_DESCP = "Customer 2";
        public static readonly String CUSTOMER1_DESCP_CN = "客户 1";
        public static readonly String CUSTOMER2_DESCP_CN = "客户 2";

        public static readonly String GL_ACCOUNT_CASH = "1000100001";
        public static readonly String GL_ACCOUNT_COST = "5000100001";
        public static readonly String GL_ACCOUNT_REV = "4000100001";
        public static readonly String GL_ACCOUNT_BANK = "1010100001";
        public static readonly String GL_ACCOUNT_EQUITY = "3010100001";

        public static readonly String GL_ACCOUNT_CASH_DESCP = "cash on hands 1";
        public static readonly String GL_ACCOUNT_COST_DESCP = "Traffic cost";
        public static readonly String GL_ACCOUNT_REV_DESCP = "salary incoming 1";
        public static readonly String GL_ACCOUNT_BANK_DESCP = "bank account 6235";
        public static readonly String GL_ACCOUNT_EQUITY_DESCP = "Equity";
        public static readonly String GL_ACCOUNT_CASH_DESCP_CN = "手头现金";
        public static readonly String GL_ACCOUNT_COST_DESCP_CN = "交通花费";
        public static readonly String GL_ACCOUNT_REV_DESCP_CN = "工资收入";
        public static readonly String GL_ACCOUNT_BANK_DESCP_CN = "银行账户6235";
        public static readonly String GL_ACCOUNT_EQUITY_DESCP_CN = "所有者权益";

        public static readonly String BUSINESS_AREA_WORK = "000000WORK";
        public static readonly String BUSINESS_AREA_ENTERTAIN = "0ENTERTAIN";
        public static readonly String BUSINESS_AREA_SNACKS = "0000SNACKS";

        public static readonly String BUSINESS_AREA_WORK_DESCP = "Work";
        public static readonly String BUSINESS_AREA_ENTERTAIN_DESCP = "Entertainment(or with Yanyan), like dinner, movie...";
        public static readonly String BUSINESS_AREA_SNACKS_DESCP = "Expense on snack";
        public static readonly String BUSINESS_AREA_WORK_DESCP_CN = "工作";
        public static readonly String BUSINESS_AREA_ENTERTAIN_DESCP_CN = "娱乐";
        public static readonly String BUSINESS_AREA_SNACKS_DESCP_CN = "零食";

        public static readonly String BANK_KEY = "0000000CMB";
        public static readonly String BANK_KEY_DESCP = "China Merchants Bank";
        public static readonly String BANK_KEY_DESCP_CN = "招商银行";

        public static readonly String BANK_ACCOUNT_CMB_6620 = "00CMB_6620";
        public static readonly String BANK_ACCOUNT_CMB_6235 = "00CMB_6235";
        public static readonly String BANK_ACCOUNT_CMB_6620_DESCP = "credit card";
        public static readonly String BANK_ACCOUNT_CMB_6235_DESCP = "bank account";

        public static readonly String BANK_ACCOUNT_CMB_6620_ACC = "0000000000006620";
        public static readonly String BANK_ACCOUNT_CMB_6235_ACC = "0000000000006235";

    }

}
