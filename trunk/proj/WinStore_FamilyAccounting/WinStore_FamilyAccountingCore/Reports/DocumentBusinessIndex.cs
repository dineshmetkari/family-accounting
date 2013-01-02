using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WinStore_FamilyAccountingCore.MasterData;
using WinStore_FamilyAccountingCore.Transaction;
using WinStore_FamilyAccountingCore.Utilities;

namespace WinStore_FamilyAccountingCore.Reports
{
    public class DocumentBusinessIndex : DocumentIndex
    {
        internal DocumentBusinessIndex(CoreDriver coreDriver,
                MasterDataManagement mdMgmt)
            : base(coreDriver, mdMgmt)
        {
        }

        protected override void newDoc(HeadEntity head)
        {
            List<ItemEntity> items = head.Items;
            foreach (ItemEntity itemEntity in items)
            {
                MasterDataIdentity id = itemEntity.GLAccount;
                if (id != null)
                {
                    DocumentIndexItem item;
                    if (!_list.TryGetValue(id, out item))
                    {
                        item = new DocumentIndexItemWithBalance(_coreDriver, id,
                                MasterDataType.BUSINESS_AREA);
                        _list.Add(id, item);
                    }
                    // add document
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
                MasterDataIdentity id = itemEntity.BusinessArea;
                if (id == null)
                {
                    continue;
                }
                DocumentIndexItem item;
                if (!_list.TryGetValue(id, out item))
                {
                    continue;
                }
                // remove document
                item.removeDoc(head);
                // reverse amount
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
