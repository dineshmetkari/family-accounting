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
    public class DocumentIndexItemWithBalance : DocumentIndexItem
    {
        private readonly MasterDataType _type;

        private readonly CurrencyAmount _sum;

        /// <summary>
        /// constructor
        /// </summary>
        /// <param name="coreDriver"></param>
        /// <param name="id"></param>
        /// <param name="type"></param>
        internal DocumentIndexItemWithBalance(CoreDriver coreDriver, MasterDataIdentity id,
                MasterDataType type)
            : base(coreDriver, id)
        {
            _type = type;
            _sum = new CurrencyAmount();
        }

        /// <summary>
        /// Add document
        /// </summary>
        /// <param name="head"></param>
        internal override void addDoc(HeadEntity head)
        {
            base.addDoc(head);

            // add the balance items
            List<ItemEntity> items = head.Items;
            foreach (ItemEntity item in items)
            {
                _sum.AddTo(getAmountFromItem(item));
            }
        }

        /// <summary>
        /// remove document
        /// </summary>
        /// <param name="head"></param>
        internal override void removeDoc(HeadEntity head)
        {
            base.removeDoc(head);

            // add the balance items
            List<ItemEntity> items = head.Items;
            foreach (ItemEntity item in items)
            {
                CurrencyAmount amount = getAmountFromItem(item);
                amount.Negate();
                _sum.AddTo(amount);
            }
        }

        /// <summary>
        /// get the amount from item
        /// </summary>
        /// <param name="item"></param>
        /// <returns></returns>
        private CurrencyAmount getAmountFromItem(ItemEntity item)
        {
            if (_type == MasterDataType.BUSINESS_AREA)
            {
                MasterDataIdentity business = item.BusinessArea;
                if (this._id.Equals(business))
                {
                    if (item.CdIndicator == CreditDebitIndicator.DEBIT)
                    {
                        return item.Amount;
                    }
                    else
                    {
                        CurrencyAmount amount = item.Amount;
                        amount.Negate();
                        return amount;
                    }
                }
            }
            else if (_type == MasterDataType.CUSTOMER)
            {
                MasterDataIdentity customer = item.Customer;
                if (this._id.Equals(customer))
                {
                    if (item.CdIndicator == CreditDebitIndicator.DEBIT)
                    {
                        return item.Amount;
                    }
                    else
                    {
                        CurrencyAmount amount = item.Amount;
                        amount.Negate();
                        return amount;
                    }
                }
            }
            else if (_type == MasterDataType.VENDOR)
            {
                MasterDataIdentity vendor = item.Vendor;
                if (this._id.Equals(vendor))
                {
                    if (item.CdIndicator == CreditDebitIndicator.DEBIT)
                    {
                        return item.Amount;
                    }
                    else
                    {
                        CurrencyAmount amount = item.Amount;
                        amount.Negate();
                        return amount;
                    }
                }
            }

            return new CurrencyAmount();
        }

        /// <summary>
        /// get amount sum
        /// </summary>
        /// <returns></returns>
        public CurrencyAmount getAmountSum()
        {
            return new CurrencyAmount(_sum);
        }

        /// <summary>
        /// get amount
        /// </summary>
        /// <param name="startId">start month identity</param>
        /// <param name="endId">end month identity</param>
        /// <returns></returns>
        public CurrencyAmount getAmount(MonthIdentity startId, MonthIdentity endId)
        {
            CurrencyAmount amount = new CurrencyAmount();
            List<HeadEntity> docs = this.getEntities(startId, endId);
            foreach (HeadEntity head in docs)
            {
                List<ItemEntity> items = head.Items;
                foreach (ItemEntity item in items)
                {
                    amount.AddTo(getAmountFromItem(item));
                }
            }

            return amount;
        }
    }
}
