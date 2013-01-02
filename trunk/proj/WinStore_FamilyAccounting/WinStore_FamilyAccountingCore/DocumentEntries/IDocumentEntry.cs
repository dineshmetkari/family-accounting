using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WinStore_FamilyAccountingCore.MasterData;
using WinStore_FamilyAccountingCore.Transaction;

namespace WinStore_FamilyAccountingCore.DocumentEntries
{
    public interface IDocumentEntry
    {
        /// <summary>
        /// set value
        /// </summary>
        /// <param name="fieldName"></param>
        /// <param name="value"></param>
        /// <exception cref="NoFieldNameException">No such field name</exception>
        /// <exception cref="NotInValueRangeException">The value is not supported</exception>
        void SetValue(String fieldName, Object value);

        /// <summary>
        /// 
        /// </summary>
        /// <param name="fieldName"></param>
        /// <returns></returns>
        /// <exception cref="NoFieldNameException"> no such field name</exception>
        Object GetValue(String fieldName);

        /// <summary>
        /// 
        /// </summary>
        /// <param name="fieldName"></param>
        /// <returns></returns>
        /// <exception cref="NoFieldNameException"> no such field name</exception>
        Object GetDefaultValue(String fieldName)
            ;

        /// <summary>
        /// 
        /// </summary>
        /// <param name="fieldName"></param>
        /// <returns></returns>
        /// <exception cref="NoFieldNameException"> no such field name</exception>
        List<MasterDataBase> GetValueSet(String fieldName);

        /// <summary>
        /// Check the document entry before saving
        /// </summary>
        /// <exception cref="MandatoryFieldIsMissing"></exception>
        void CheckBeforeSave();

        /// <summary>
        /// 
        /// </summary>
        /// <param name="saved"></param>
        /// <exception cref="MandatoryFieldIsMissing"></exception>
        /// <exception cref="SaveClosedLedgerException"></exception>
        Task SaveAsync(bool saved);

        /// <summary>
        /// is saved
        /// </summary>
        /// <returns></returns>
        bool isSaved();

        /// <summary>
        /// 
        /// </summary>
        /// <returns></returns>
        HeadEntity getDocument();
    }

}
