using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WinStore_FamilyAccountingCore.Exceptions.FormatExceptions;

namespace WinStore_FamilyAccountingCore.Utilities
{
    public enum GLAccountGroupENUM
    {
        CASH = 0, BANK_ACCOUNT, INVESTMENT, PREPAID, ASSETS
            , SHORT_LIABILITIES, LONG_LIABILITIES, EQUITY
                , SALARY, INVEST_REVENUE, COST_PURE, COST_ACCI
    }


    public class GLAccountGroup
    {
        public readonly static string[] GROUP_VALUE = {
	("1000"), ("1010"), ("1060"), ("1430"), (
			"1500"), ("2000"), ("2700"), (
			"3010"), ("4000"), ("4010"), ("5000"), (
			"5010")};

        /// <summary>
        /// Group of account group
        /// </summary>
        public static readonly GLAccountGroupENUM[] BALANCE_GROUP = { GLAccountGroupENUM.CASH,
			GLAccountGroupENUM.BANK_ACCOUNT, GLAccountGroupENUM.INVESTMENT,
			GLAccountGroupENUM.ASSETS, GLAccountGroupENUM.PREPAID };
        public static readonly GLAccountGroupENUM[] Liquidity_GROUP = {
			GLAccountGroupENUM.CASH, GLAccountGroupENUM.BANK_ACCOUNT,
			GLAccountGroupENUM.SHORT_LIABILITIES, GLAccountGroupENUM.PREPAID };
        public static readonly GLAccountGroupENUM[] LIABILITIES_GROUP = {
			GLAccountGroupENUM.LONG_LIABILITIES, GLAccountGroupENUM.SHORT_LIABILITIES };
        public static readonly GLAccountGroupENUM[] REVENUE_GROUP = {
			GLAccountGroupENUM.SALARY, GLAccountGroupENUM.INVEST_REVENUE };
        public static readonly GLAccountGroupENUM[] COST_GROUP = {
			GLAccountGroupENUM.COST_ACCI, GLAccountGroupENUM.COST_PURE };

        private readonly GLAccountGroupENUM _id;
        public GLAccountGroupENUM Identity { get { return _id; } }

        /// <summary>
        /// constructor
        /// </summary>
        /// <param name="str"></param>
        private GLAccountGroup(GLAccountGroupENUM str)
        {
            _id = str;
        }

        /// <summary>
        /// to String
        /// </summary>
        /// <returns></returns>
        public override String ToString()
        {
            return GROUP_VALUE[(int)_id];
        }

        /// <summary>
        /// equals
        /// </summary>
        /// <param name="obj"></param>
        /// <returns></returns>
        public override bool Equals(object obj)
        {
            GLAccountGroup group = obj as GLAccountGroup;
            if (group == null)
            {
                return false;
            }
            return this.Identity == group.Identity;
        }

        /// <summary>
        /// get hash code
        /// </summary>
        /// <returns></returns>
        public override int GetHashCode()
        {
            return (int)this.Identity;
        }
        /// <summary>
        /// parse identity to GLAccountGroupENUM
        /// </summary>
        /// <param name="id"></param>
        /// <returns></returns>
        /// <exception cref="GLAccountGroupFormatException">GL account group format exception</exception>
        /// <exception cref="ArgumentNullException">Id is null</exception>
        public static GLAccountGroupENUM ParseEnum(String id)
        {
            if (id == null)
            {
                throw new ArgumentNullException();
            }

            int i = 0;
            foreach (string str in GROUP_VALUE)
            {
                if (str.Equals(id))
                {
                    return (GLAccountGroupENUM)i;
                }

                i++;
            }
            throw new GLAccountGroupFormatException(id);
        }

        /// <summary>
        /// parse string to GLAccountGroup
        /// </summary>
        /// <param name="id"></param>
        /// <returns></returns>
        public static GLAccountGroup Parse(String id)
        {
            if (id == null)
            {
                return null;
            }
            GLAccountGroupENUM e = ParseEnum(id);
            return new GLAccountGroup(e);
        }
    }
}
