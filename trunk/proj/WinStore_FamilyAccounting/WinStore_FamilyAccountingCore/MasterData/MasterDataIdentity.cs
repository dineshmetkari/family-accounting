using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WinStore_FamilyAccountingCore.Exceptions;

namespace WinStore_FamilyAccountingCore.MasterData
{
    /// <summary>
    ///  the length of the identity is 10. if the input identity is less than 10,
    /// right align. And then fill the leading '0'. For example, 00IDENTITY
    /// </summary>
    public class MasterDataIdentity : IComparable<MasterDataIdentity>
    {
        public readonly static int LENGTH = 10;
        private readonly char[] _identity = new char[LENGTH];


        /// <summary>
        /// Constructr
        /// </summary>
        /// <param name="id">string of id</param>
        /// <exception cref="IdentityTooLong">Length of input string is longer than the limitation
        /// </exception>
        /// <exception cref="IdentityNoData">No data contain in the input string, empty or null
        /// </exception>
        /// <exception cref="IdentityInvalidChar">Invalid character in the input string.
        /// </exception>
        /// <exception cref="NullReferenceException"></exception>
        public MasterDataIdentity(String id) :
            this(id.ToCharArray())
        {
        }

        /// <summary>
        /// Constructr
        /// </summary>
        /// <param name="id">char array of id</param>
        /// <exception cref="IdentityTooLong">Length of input string is longer than the limitation
        /// </exception>
        /// <exception cref="IdentityNoData">No data contain in the input string, empty or null
        /// </exception>
        /// <exception cref="IdentityInvalidChar">Invalid character in the input string.
        /// </exception>
        public MasterDataIdentity(char[] id)
        {
            bool containData = false;

            if (id == null || id.Length == 0)
            {
                throw new IdentityNoData();
            }
            if (id.Length > LENGTH)
            {
                throw new IdentityTooLong(id.Length, LENGTH);
            }

            int l = id.Length;
            for (int i = LENGTH - 1; i >= 0; i--)
            {
                if (l + i - LENGTH >= 0)
                {
                    // check character valid
                    bool flag = isValidChar(id[l + i - LENGTH]);
                    if (flag == false)
                    {
                        throw new IdentityInvalidChar(id[l + i - LENGTH]);
                    }

                    // check whether contains data
                    if (id[l + i - LENGTH] != '0')
                    {
                        containData = true;
                    }

                    _identity[i] = Char.ToUpper(id[l + i - LENGTH]);
                }
                else
                {
                    // leading zero
                    _identity[i] = '0';
                }
            }

            if (!containData)
            {
                throw new IdentityNoData();
            }
        }

        /// <summary>
        /// Equals
        /// </summary>
        /// <param name="obj"></param>
        /// <returns></returns>
        public override bool Equals(Object obj)
        {
            MasterDataIdentity id = obj as MasterDataIdentity;
            if (obj == null)
            {
                return false;
            }
            for (int i = 0; i < LENGTH; ++i)
            {
                if (id._identity[i] != _identity[i])
                {
                    return false;
                }
            }

            return true;
        }

        /// <summary>
        /// get hash code
        /// </summary>
        /// <returns></returns>
        public override int GetHashCode()
        {
            int sum = 0;
            for (int i = 0; i < LENGTH; ++i)
            {
                sum += _identity[i];
            }
            return sum;
        }

        /// <summary>
        /// to string
        /// </summary>
        /// <returns></returns>
        public override String ToString()
        {
            StringBuilder strBuilder = new StringBuilder();
            foreach (char ch in _identity)
            {
                strBuilder.Append(ch);
            }
            return strBuilder.ToString();
        }

        /// <summary>
        /// only number, char(a..z) and '_' is valid
        /// </summary>
        /// <param name="ch"></param>
        /// <returns></returns>
        protected virtual bool isValidChar(char ch)
        {
            if (('0' <= ch && ch <= '9') || ('a' <= ch && ch <= 'z')
                    || ('A' <= ch && ch <= 'Z') || ch == '_')
            {
                return true;
            }
            return false;
        }

        /// <summary>
        /// Compare
        /// </summary>
        /// <param name="other"></param>
        /// <returns></returns>
        public int CompareTo(MasterDataIdentity other)
        {
            if (other == null)
            {
                return 1;
            }
            return this.ToString().CompareTo(other.ToString());
        }
    }

}
