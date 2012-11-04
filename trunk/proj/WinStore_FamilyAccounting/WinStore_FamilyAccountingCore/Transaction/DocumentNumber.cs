using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WinStore_FamilyAccountingCore.Exceptions;
using WinStore_FamilyAccountingCore.MasterData;

namespace WinStore_FamilyAccountingCore.Transaction
{
    public class DocumentNumber : MasterDataIdentity_GLAccount
    {

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="id"></param>
        /// <exception cref="IdentityTooLong"></exception>
        /// <exception cref="IdentityNoData"></exception>
        /// <exception cref="IdentityInvalidChar"></exception>
        public DocumentNumber(char[] id)
            : base(id)
        {
        }

        /// <summary>
        /// Get the next document number of this one
        /// </summary>
        /// <returns></returns>
        /// <exception cref="SystemException">System Exception</exception>
        public DocumentNumber Next()
        {
            char[] newId = this.ToString().ToCharArray();

            for (int i = newId.Length - 1; i >= 0; --i)
            {
                newId[i]++;
                if (newId[i] > '9')
                {
                    newId[i] = '0';
                }
                else
                {
                    break;
                }
            }

            try
            {
                return new DocumentNumber(newId);
            }
            catch (IdentityTooLong e)
            {
                throw new SystemException(e);
            }
            catch (IdentityNoData e)
            {
                throw new SystemException(e);
            }
            catch (IdentityInvalidChar e)
            {
                throw new SystemException(e);
            }
        }
    }

}
