using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using WinStore_FamilyAccountingCore.Utilities;
using WinStore_FamilyAccountingCore.MasterData;
using WinStore_FamilyAccountingCore.Transaction;

namespace WinStore_FamilyAccountingCore.Reports
{
    /// <summary>
    /// Account index
    /// </summary>
    public class DocumentAccountIndex : DocumentIndex
    {

        internal DocumentAccountIndex(CoreDriver coreDriver, MasterDataManagement mdMgmt)
            : base(coreDriver, mdMgmt)
        {
        }

        /// <summary>
        /// new document
        /// </summary>
        /// <param name="head"></param>
        protected override void newDoc(HeadEntity head)
        {
            List<ItemEntity> items = head.Items;

            foreach (ItemEntity itemEntity in items)
            {
                MasterDataIdentity id = itemEntity.GLAccount;
                DocumentIndexItem item;

                if (!_list.TryGetValue(id, out item))
                {
                    item = new DocumentIndexItem(_coreDriver, id);
                    _list.Add(id, item);
                }
                item.addDoc(head);
                // add amount
                CurrencyAmount amount = itemEntity.Amount;
                if (itemEntity.CdIndicator == CreditDebitIndicator.CREDIT)
                {
                    amount.Negate();
                }
                item.addAmount(head.MonthID, amount);
            }
        }
        /// <summary>
        /// reverse document
        /// </summary>
        /// <param name="head"></param>
        protected override void reverseDoc(HeadEntity head)
        {
            List<ItemEntity> items = head.Items;

            foreach (ItemEntity itemEntity in items)
            {
                MasterDataIdentity id = itemEntity.GLAccount;
                if (!_list.ContainsKey(id))
                {
                    continue;
                }
                DocumentIndexItem item;
                if (_list.TryGetValue(id, out item) == false)
                {
                    continue;
                }

                // remove document
                item.removeDoc(head);
                // remove amount
                CurrencyAmount amount = itemEntity.Amount;
                if (itemEntity.CdIndicator == CreditDebitIndicator.DEBIT)
                {
                    amount.Negate();
                }
                item.addAmount(head.MonthID, amount);
            }
        }
    }

}
