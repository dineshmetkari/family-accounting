using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WinStore_FamilyAccountingCore.MasterData;
using WinStore_FamilyAccountingCore.Utilities;

namespace WinStore_FamilyAccountingUnitTest.Utils
{
    public class TestUtilities
    {

	public static readonly String TEST_BANK_KEY = "CMB";

	public static readonly String TEST_ACCOUNT_NUMBER = "1234123412341234";

	public static readonly BankAccountType TEST_BANK_ACCOUNT_TYPE = BankAccountType.SAVING_ACCOUNT;

	public static readonly CriticalLevel TEST_CRITICAL_LEVEL = CriticalLevel.LOW;

	public static readonly String TEST_BANK_KEY_ID = "CMB_6620";

	public static readonly String TEST_GL_ACCOUNT_GROUP = "1010";

	public static readonly String TEST_DOC_NUM = "1000000000";

	public static readonly String TEST_DOC_ID = "1000000000_2012_07";

	public static readonly String TEST_AMOUNT1 = "123.45";

	public static readonly String TEST_AMOUNT2 = "543.21";

	public static readonly String TEST_AMOUNT3 = "419.76";

	public static readonly String TEST_DESCP = "test";

	public static readonly String[] GL_IDS = { "1000100001", "1010100001",
			"3010100001", "4000100001", "5000100001" };

	public static readonly String[] VENDOR_IDS = { "SUBWAY", "BUS" };

	public static readonly String[] CUSTOMER_IDS = { "C1", "C2" };

	public static readonly String[] BUSINESS_IDS = { "WORK", "ENTERTAIN",
			"FAMILY", "TEAM_MATES", "FRIENDS", "SNACKS", "HEALTH",
			"DAILY_LIFE", "LUX_LIFE" };

	public static readonly String[] BANK_KEY_IDS = { "CMB", "SPDB", "ICBC" };

	public static readonly String[] BANK_ACCOUNT_IDS = { "CMB_6620", "CMB_6235" };

	public static readonly String[] DOCUMNET_NUMS = { "1000000001", "1000000002" };

	public static readonly String GL_ACCOUNT_CASH = "1000100001";

	public static readonly String GL_ACCOUNT_BANK = "1010100001";

	public static readonly String GL_ACCOUNT_COST = "5000100001";

	public static readonly String GL_ACCOUNT_ENQUITY = "3010100001";

	public static readonly String GL_ACCOUNT_PROFIT = "4010100001";

	public static readonly String VENDOR = "0000000BUS";

	public static readonly String CUSTOMER = "00000000C1";

	public static readonly String BUSINESS_AREA = "000000WORK";
    }
}
