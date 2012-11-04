using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WinStore_FamilyAccountingCore.Exceptions;
using WinStore_FamilyAccountingCore.Exceptions.FormatExceptions;

namespace WinStore_FamilyAccountingCore.Transaction
{
    /// <summary>
    /// Document identity
    /// </summary>
    public class DocumentIdentity
    {
        public readonly DocumentNumber _docNumber;
        public readonly MonthIdentity _monthIdentity;

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="docNum"></param>
        /// <param name="fiscalYear"></param>
        /// <param name="fiscalMonth"></param>
        /// <exception cref="FiscalYearRangeException"></exception>
        /// <exception cref="FiscalMonthRangeException"></exception>
        public DocumentIdentity(DocumentNumber docNum, int fiscalYear,
                int fiscalMonth)
        {
            _docNumber = docNum;
            _monthIdentity = new MonthIdentity(fiscalYear, fiscalMonth);
        }

        /// <summary>
        /// constructor
        /// </summary>
        /// <param name="docNum"></param>
        /// <param name="monthIdentity"></param>
        public DocumentIdentity(DocumentNumber docNum, MonthIdentity monthIdentity)
        {
            _docNumber = docNum;
            _monthIdentity = monthIdentity;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="obj"></param>
        /// <returns></returns>
        public override bool Equals(Object obj)
        {
            DocumentIdentity id = obj as DocumentIdentity;
            if (id == null)
            {
                return false;
            }
            return id._docNumber.Equals(_docNumber)
                    && id._monthIdentity.Equals(_monthIdentity);
        }
        /// <summary>
        /// Get hash code
        /// </summary>
        /// <returns></returns>
        public override int GetHashCode()
        {
            String str = this.ToString();
            int count = 0;
            for (int i = 0; i < str.Length; ++i)
            {
                count += str[i];
            }

            return count;
        }

        /// <summary>
        /// to string
        /// </summary>
        /// <returns></returns>
        public override String ToString()
        {
            return String.Format("{0}_{1}", _docNumber, _monthIdentity);
        }

        /// <summary>
        /// Parse string to Document Identity
        /// </summary>
        /// <param name="docIdStr"></param>
        /// <returns></returns>
        /// <exception cref="DocumentIdentityFormatException">Document Identity Exception</exception>
        public static DocumentIdentity Parse(String docIdStr)
        {
            try
            {
                String docNumStr = docIdStr.Substring(0, 10);
                String monthIdStr = docIdStr.Substring(11, 7);

                DocumentNumber id = new DocumentNumber(docNumStr.ToCharArray());
                MonthIdentity monthId = MonthIdentity.Parse(monthIdStr);

                return new DocumentIdentity(id, monthId);
            }
            catch (ArgumentOutOfRangeException)
            {
                throw new DocumentIdentityFormatException(docIdStr);
            }
            catch (IdentityTooLong)
            {
                throw new DocumentIdentityFormatException(docIdStr);
            }
            catch (IdentityNoData)
            {
                throw new DocumentIdentityFormatException(docIdStr);
            }
            catch (IdentityInvalidChar)
            {
                throw new DocumentIdentityFormatException(docIdStr);
            }
            catch (MonthIdentityFormatException)
            {
                throw new DocumentIdentityFormatException(docIdStr);
            }
        }
    }
}
