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
    /// <summary>
    /// document index item is based on the each master data
    /// </summary>
    public class DocumentIndexItem
    {
        protected readonly List<HeadEntity> _list;

        protected readonly Dictionary<MonthIdentity, CurrencyAmount> _amountList;

        protected readonly MasterDataIdentity _id;

        protected readonly CoreDriver _coreDriver;

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="coreDriver"></param>
        /// <param name="id"></param>
        internal DocumentIndexItem(CoreDriver coreDriver, MasterDataIdentity id)
        {
            _coreDriver = coreDriver;
            _id = id;
            _list = new List<HeadEntity>();
            _amountList = new Dictionary<MonthIdentity, CurrencyAmount>();
        }

        /// <summary>
        /// get entities based on the comparator
        /// </summary>
        /// <param name="comparator"></param>
        /// <returns></returns>
        public List<HeadEntity> getEntities(Comparison<HeadEntity> comparator)
        {
            List<HeadEntity> ret = new List<HeadEntity>(_list);
            ret.Sort(comparator);
            return ret;
        }

        /// <summary>
        /// get entities with default order, sorted based on Date
        /// </summary>
        public List<HeadEntity> Entities
        {
            get
            {
                List<HeadEntity> ret = new List<HeadEntity>(_list);
                return ret;
            }
        }

        /// <summary>
        /// get month identity
        /// </summary>
        /// <param name="startId">start month identity</param>
        /// <param name="endId">end month identity</param>
        /// <returns></returns>
        public List<HeadEntity> getEntities(MonthIdentity startId,
                MonthIdentity endId)
        {
            if (startId == null)
            {
                startId = _coreDriver.StartMonthId;
            }
            if (endId == null)
            {
                endId = _coreDriver.CurCalendarMonthId;
            }
            List<HeadEntity> ret = new List<HeadEntity>();
            foreach (HeadEntity head in _list)
            {
                if (head.MonthID.CompareTo(endId) > 0)
                {
                    break;
                }

                if (head.MonthID.CompareTo(startId) >= 0)
                {
                    ret.Add(head);
                }
            }
            return ret;
        }

        /// <summary>
        /// Get month identity
        /// </summary>
        /// <param name="comparator"></param>
        /// <param name="startId"></param>
        /// <param name="endId"></param>
        /// <returns></returns>
        public List<HeadEntity> getEntities(Comparison<HeadEntity> comparator,
                MonthIdentity startId, MonthIdentity endId)
        {
            if (startId == null)
            {
                startId = _coreDriver.StartMonthId;
            }
            if (endId == null)
            {
                endId = _coreDriver.CurCalendarMonthId;
            }
            List<HeadEntity> ret = new List<HeadEntity>();
            foreach (HeadEntity head in _list)
            {
                if (head.MonthID.CompareTo(startId) >= 0)
                {
                    ret.Add(head);
                }
                if (head.MonthID.CompareTo(endId) > 0)
                {
                    break;
                }
            }

            ret.Sort(comparator);
            return ret;
        }

        /// <summary>
        /// get sum amount
        /// </summary>
        public CurrencyAmount Amount
        {
            get
            {
                CurrencyAmount sum = new CurrencyAmount();
                foreach (var item in _amountList)
                {
                    sum.AddTo(item.Value);
                }
                return sum;
            }
        }

        /// <summary>
        /// get amount
        /// </summary>
        /// <param name="monthID"></param>
        /// <returns></returns>
        public CurrencyAmount getAmount(MonthIdentity monthID)
        {
            CurrencyAmount sum = new CurrencyAmount();
            CurrencyAmount amount;
            if (_amountList.TryGetValue(monthID, out amount))
            {
                sum.AddTo(amount);
            }

            return sum;
        }

        /// <summary>
        /// Add document
        /// </summary>
        /// <param name="head"></param>
        internal virtual void addDoc(HeadEntity head)
        {
            _list.Add(head);
            _list.Sort(DocumentIndex.COMPARATOR_DATE);
        }

        /// <summary>
        /// remove document
        /// </summary>
        /// <param name="head"></param>
        internal virtual void removeDoc(HeadEntity head)
        {
            _list.Remove(head);
        }

        /// <summary>
        /// add currency amount
        /// </summary>
        /// <param name="monthId"></param>
        /// <param name="amount"></param>
        internal void addAmount(MonthIdentity monthId, CurrencyAmount amount)
        {
            CurrencyAmount v1;
            if (_amountList.TryGetValue(monthId, out v1))
            {
                v1.AddTo(amount);
                return;
            }
            // put
            _amountList.Add(monthId, amount);
        }
    }
}
