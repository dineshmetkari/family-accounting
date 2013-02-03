using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WinStore_FamilyAccountingCore;
using WinStore_FamilyAccountingCore.MasterData;
using WinStore_FamilyAccountingCore.Transaction;
using WinStore_FamilyAccountingCore.Utilities;

namespace WinStore_FamilyAccounting.Data
{
    public class CostReportItem : AbstractAdapterItem
    {
        protected readonly CurrencyAmount _amount;
        public CostReportItem(MasterDataIdentity_GLAccount id
            , String name, CurrencyAmount amount, CostReportsAdapter parent)
            : base(id, name, parent)
        {
            _amount = amount;
        }

        public String Amount { get { return _amount.ToString(); } }

        /// <summary>
        /// Comparation
        /// </summary>
        /// <param name="other"></param>
        /// <returns></returns>
        public override int CompareTo(AbstractAdapterItem other)
        {
            CostReportItem item = other as CostReportItem;
            if (item == null)
            {
                return -1;
            }
            return - this._amount.CompareTo(item._amount);
        }
    }

    /// <summary>
    /// 
    /// </summary>
    public class CostReportsAdapter : AbstractAdapter
    {
        /// <summary>
        /// currency month
        /// </summary>
        private MonthIdentity _curMonthId;
        /// <summary>
        /// items
        /// </summary>
        private readonly ObservableCollection<AbstractAdapterItem> _items;
        /// <summary>
        /// 
        /// </summary>
        /// <param name="dataCore"></param>
        internal CostReportsAdapter(DataCore dataCore)
            : base(dataCore)
        {
            _items = new ObservableCollection<AbstractAdapterItem>();
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="monthId"></param>
        /// <returns></returns>
        public ObservableCollection<AbstractAdapterItem> GetItems(MonthIdentity monthId)
        {
            CoreDriver coreDriver = _dataCore.BackendCoreDriver;
            if (coreDriver.IsInitialize == false)
            {
                return _items;
            }

            if (_curMonthId == null
                || _curMonthId.Equals(monthId) == false)
            {
                // clear
                _items.Clear();

                // change month
                _curMonthId = monthId;
                GLAccountBalanceCollection balCol = coreDriver.TransMgmt.AccountBalanceCol;
                MasterDataManagement mdMgmt = coreDriver.MdMgmt;

                // for each cost group
                List<CostReportItem> costList = new List<CostReportItem>();
                foreach (GLAccountGroupENUM group in GLAccountGroup.COST_GROUP)
                {
                    List<MasterDataIdentity_GLAccount> ids = mdMgmt
                            .GetGLAccountsBasedGroup(group);
                    // for g/l account in each group
                    foreach (MasterDataIdentity_GLAccount id in ids)
                    {
                        // get master data
                        GLAccountMasterData masterData = (GLAccountMasterData)mdMgmt
                                .GetMasterData(id, MasterDataType.GL_ACCOUNT);
                        // get balance item
                        GLAccountBalanceItem item = balCol.GetBalanceItem(id);
                        costList.Add(new CostReportItem(id, masterData.Descp
                            , item.GetAmount(_curMonthId), this));
                    }
                }
                // sort 
                costList.Sort();
                foreach(CostReportItem costItem in costList)
                {
                    _items.Add(costItem);
                }                
            }

            return _items;
        }
    }
}
