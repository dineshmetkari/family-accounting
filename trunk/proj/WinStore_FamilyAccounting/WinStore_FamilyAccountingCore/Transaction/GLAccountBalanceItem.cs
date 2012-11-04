using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WinStore_FamilyAccountingCore.MasterData;
using WinStore_FamilyAccountingCore.Utilities;

namespace WinStore_FamilyAccountingCore.Transaction
{
    public class GLAccountBalanceItem
    {
        private readonly Dictionary<MonthIdentity, CurrencyAmount> _list;
        private readonly MasterDataIdentity_GLAccount _glAccount;
        public MasterDataIdentity_GLAccount GLAccount { get { return _glAccount; } }

        private CurrencyAmount _sum;
        public CurrencyAmount Sum { get { return new CurrencyAmount(_sum); } }


        internal GLAccountBalanceItem(MasterDataIdentity_GLAccount glAccount)
        {
            _glAccount = glAccount;
            _list = new Dictionary<MonthIdentity, CurrencyAmount>();
            _sum = new CurrencyAmount();

        }

        /// <summary>
        /// Add amount 
        /// </summary>
        /// <param name="monthId"></param>
        /// <param name="amount"></param>
        internal void AddAmount(MonthIdentity monthId, CurrencyAmount amount)
        {
            _sum.AddTo(amount);

            if (_list.ContainsKey(monthId))
            {
                CurrencyAmount sum;
                _list.TryGetValue(monthId, out sum);
                sum.AddTo(amount);
            }
            else
            {
                _list.Add(monthId, new CurrencyAmount(amount));
            }
        }

        /// <summary>
        /// Get sum of amount from start month to end month
        /// </summary>
        /// <param name="startId">id of start month</param>
        /// <param name="endId">id of end month</param>
        /// <returns></returns>
        public CurrencyAmount GetSumAmount(MonthIdentity startId,
                MonthIdentity endId)
        {
            CurrencyAmount sum = new CurrencyAmount();
            foreach (var item in _list)
            {
                if (item.Key.CompareTo(startId) >= 0 && item.Key.CompareTo(endId) <= 0)
                {
                    sum.AddTo(item.Value);
                }
            }

            return sum;
        }

        /// <summary>
        /// Get amount
        /// </summary>
        /// <param name="id"></param>
        /// <returns></returns>
        public CurrencyAmount GetAmount(MonthIdentity id)
        {
            if (_list.ContainsKey(id))
            {
                CurrencyAmount amount;
                _list.TryGetValue(id, out amount);
                return new CurrencyAmount(amount);
            }
            return new CurrencyAmount();
        }
    }

}
