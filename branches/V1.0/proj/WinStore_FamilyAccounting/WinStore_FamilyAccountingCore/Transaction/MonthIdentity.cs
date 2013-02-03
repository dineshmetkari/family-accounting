using WinStore_FamilyAccountingCore.Exceptions;
using WinStore_FamilyAccountingCore.Exceptions.FormatExceptions;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WinStore_FamilyAccountingCore.Transaction
{
    public class MonthIdentity : IComparable<MonthIdentity>
    {

        private readonly int _fiscalYear;
        public int FiscalYear { get { return _fiscalYear; } }

        private readonly int _fiscalMonth;
        public int FiscalMonth { get { return _fiscalMonth; } }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="fiscalYear"></param>
        /// <param name="fiscalMonth"></param>
        /// <exception cref="FiscalYearRangeException">Value of fiscal year</exception>
        /// <exception cref="FiscalMonthRangeException">Value of fiscal month</exception>
        public MonthIdentity(int fiscalYear, int fiscalMonth)
        {
            if (fiscalYear < 1000 || fiscalYear > 9999)
            {
                throw new FiscalYearRangeException(fiscalYear);
            }
            if (fiscalMonth <= 0 || fiscalMonth > 13)
            {
                throw new FiscalMonthRangeException(fiscalMonth);
            }
            _fiscalYear = fiscalYear;
            _fiscalMonth = fiscalMonth;
        }

        /// <summary>
        /// to string
        /// </summary>
        /// <returns></returns>
        public override String ToString()
        {
            if (_fiscalMonth < 10)
            {
                return String.Format("{0}_0{1}", _fiscalYear, _fiscalMonth);
            }
            return String.Format("{0}_{1}", _fiscalYear, _fiscalMonth);
        }

        /// <summary>
        /// equals
        /// </summary>
        /// <param name="obj"></param>
        /// <returns></returns>
        public override bool Equals(Object obj)
        {
            MonthIdentity monthId = obj as MonthIdentity;
            if (monthId == null)
            {// not match type MonthIdentity
                return false;
            }
            return monthId._fiscalMonth == this._fiscalMonth
                    && monthId._fiscalYear == this._fiscalYear;
        }

        /// <summary>
        /// Get hash code
        /// </summary>
        /// <returns></returns>
        public override int GetHashCode()
        {
            return _fiscalYear * 100 + _fiscalMonth;
        }


        /// <summary>
        /// parse string to month identity
        /// </summary>
        /// <param name="str"></param>
        /// <returns></returns>
        /// <exception cref="MonthIdentityFormatException">input string with error month identity format</exception>
        public static MonthIdentity Parse(String str)
        {
            try
            {
                string yearStr = str.Substring(0, 4);
                string monthStr = str.Substring(5, 2);
                int year = Int32.Parse(yearStr);
                int month = Int32.Parse(monthStr);

                MonthIdentity id = new MonthIdentity(year, month);

                return id;
            }
            catch (ArgumentOutOfRangeException e)
            {
                throw new MonthIdentityFormatException(e.Message);
            }
            catch (FiscalYearRangeException e)
            {
                throw new MonthIdentityFormatException(e.Message);
            }
            catch (FiscalMonthRangeException e)
            {
                throw new MonthIdentityFormatException(e.Message);
            }
            catch (ArgumentNullException e)
            {
                throw new MonthIdentityFormatException(e.Message);
            }
            catch (FormatException e)
            {
                throw new MonthIdentityFormatException(e.Message);
            }
            catch (OverflowException e)
            {
                throw new MonthIdentityFormatException(e.Message);
            }
        }


        /// <summary>
        /// Add month, for example, current month identity is 2012_08, return is 2012_09
        /// </summary>
        /// <returns></returns>
        public MonthIdentity AddMonth()
        {
            int month = _fiscalMonth + 1;
            int year = _fiscalYear;
            if (month > 12)
            {
                month = 1;
                year++;
            }

            MonthIdentity newId = null;
            try
            {
                newId = new MonthIdentity(year, month);
            }
            catch (FiscalYearRangeException e)
            {
                throw new SystemException(e); // bug
            }
            catch (FiscalMonthRangeException e)
            {
                throw new SystemException(e); // bug
            }

            return newId;
        }

        /// <summary>
        /// Comparable
        /// </summary>
        /// <param name="other"></param>
        /// <returns></returns>
        public int CompareTo(MonthIdentity other)
        {
            int ret = (this._fiscalYear - other._fiscalYear) * 12
                    + this._fiscalMonth - other._fiscalMonth;
            return ret;
        }
    }

}
