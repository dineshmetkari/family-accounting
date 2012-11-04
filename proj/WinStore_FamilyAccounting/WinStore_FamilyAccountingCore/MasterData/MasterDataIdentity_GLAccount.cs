using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WinStore_FamilyAccountingCore.MasterData
{
    /// <summary>
    /// The identity only contains numbers
    /// </summary>
    public class MasterDataIdentity_GLAccount : MasterDataIdentity
    {
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
        public MasterDataIdentity_GLAccount(String id)
            : base(id)
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
        public MasterDataIdentity_GLAccount(char[] id)
            : base(id)
        {
        }

        /// <summary>
        /// Only number is valid.
        /// </summary>
        /// <param name="ch"></param>
        /// <returns></returns>
        protected override bool isValidChar(char ch)
        {
            if ('0' <= ch && ch <= '9')
            {
                return true;
            }
            return false;
        }
    }
}
