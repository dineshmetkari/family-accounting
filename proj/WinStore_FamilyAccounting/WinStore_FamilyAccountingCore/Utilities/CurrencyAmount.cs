using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WinStore_FamilyAccountingCore.Exceptions.FormatExceptions;

namespace WinStore_FamilyAccountingCore.Utilities
{
    /// <summary>
    /// currency amount ~.2
    /// </summary>
    public class CurrencyAmount : IComparable<CurrencyAmount>
    {
        private int _value;

        /// <summary>
        /// Constructor with ZERO
        /// </summary>
        public CurrencyAmount()
        {
            _value = 0;
        }

        /// <summary>
        /// Construct with input value
        /// </summary>
        /// <param name="value"></param>
        public CurrencyAmount(Double value)
        {
            Set(value);
        }

        /// <summary>
        /// Construct with other amount
        /// </summary>
        /// <param name="amount"></param>
        public CurrencyAmount(CurrencyAmount amount)
        {
            if (amount == null)
            {
                _value = 0;
                return;
            }

            _value = amount._value;
        }

        /// <summary>
        /// this += amount
        /// </summary>
        /// <param name="amount"></param>
        public void AddTo(CurrencyAmount amount)
        {
            _value += amount._value;
        }

        /// <summary>
        /// this -= amount
        /// </summary>
        /// <param name="amount"></param>
        public void MinusTo(CurrencyAmount amount)
        {
            _value -= amount._value;
        }

        /// <summary>
        /// set amount
        /// </summary>
        /// <param name="amount"></param>
        public void Set(CurrencyAmount amount)
        {
            if (amount == null)
            {
                return;
            }

            _value = amount._value;
        }

        /// <summary>
        /// set amount
        /// </summary>
        /// <param name="value"></param>
        public void Set(double value)
        {
            double v = value * 100;
            if (v >= 0)
            {
                _value = (int)(v + 0.5);
            }
            else
            {
                _value = (int)(v - 0.5);
            }
        }

        /// <summary>
        /// Compare to
        /// </summary>
        /// <param name="arg0"></param>
        /// <returns></returns>
        public int CompareTo(CurrencyAmount arg0)
        {
            return this._value - arg0._value;
        }

        /// <summary>
        /// is zero
        /// </summary>
        /// <returns></returns>
        public bool IsZero()
        {
            return this._value == 0;
        }

        /// <summary>
        /// is negative
        /// </summary>
        /// <returns></returns>
        public bool IsNegative()
        {
            return this._value < 0;
        }

        /// <summary>
        /// this = -this
        /// </summary>
        public void Negate()
        {
            this._value = 0 - this._value;
        }

        /// <summary>
        /// ret = oper1 + oper2
        /// </summary>
        /// <param name="oper1"></param>
        /// <param name="oper2"></param>
        /// <returns></returns>
        public static CurrencyAmount Add(CurrencyAmount oper1, CurrencyAmount oper2)
        {
            CurrencyAmount cur = new CurrencyAmount();
            cur.AddTo(oper1);
            cur.AddTo(oper2);

            return cur;
        }

        /// <summary>
        /// ret = oper1 - oper2
        /// </summary>
        /// <param name="oper1"></param>
        /// <param name="oper2"></param>
        /// <returns></returns>
        public static CurrencyAmount Minus(CurrencyAmount oper1,
                CurrencyAmount oper2)
        {
            CurrencyAmount cur = new CurrencyAmount();
            cur.AddTo(oper1);
            cur.MinusTo(oper2);

            return cur;
        }

        /// <summary>
        /// Equals
        /// </summary>
        /// <param name="obj"></param>
        /// <returns></returns>
        public override bool Equals(Object obj)
        {
            CurrencyAmount amount = obj as CurrencyAmount;
            if (amount == null)
            {
                return false;
            }
            return this._value == amount._value;
        }

        /// <summary>
        /// get hash code
        /// </summary>
        /// <returns></returns>
        public override int GetHashCode()
        {
            return this._value;
        }

        /// <summary>
        /// To string
        /// </summary>
        /// <returns></returns>
        public override String ToString()
        {
            double value = this.ToNumber();
            return String.Format("{0:0.00}", value);
        }

        /// <summary>
        /// to number
        /// </summary>
        /// <returns></returns>
        public double ToNumber()
        {
            return _value / 100.0;
        }

        /// <summary>
        /// parse string to currency amount
        /// </summary>
        /// <param name="value"></param>
        /// <returns></returns>
        /// <exception cref="CurrencyAmountFormatException"></exception>
        public static CurrencyAmount Parse(String value)
        {
            double v;
            bool flag = Double.TryParse(value, out v);
            if (flag == false)
            {
                throw new CurrencyAmountFormatException(value);
            }

            return new CurrencyAmount(v);
        }
    }
}
