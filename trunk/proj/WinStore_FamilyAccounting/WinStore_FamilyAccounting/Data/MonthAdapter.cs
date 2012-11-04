using WinStore_FamilyAccountingCore.Transaction;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WinStore_FamilyAccounting.Common;
using System.Collections.ObjectModel;

namespace WinStore_FamilyAccounting.Data
{
    public class MonthItem : AbstractAdapterItem
    {
        public MonthIdentity MonthId { get { return (MonthIdentity)this.Identity; } }

        public override string Name { get { return Identity.ToString(); } }
        public MonthItem(MonthIdentity monthId, MonthAdapter parent)
            : base(monthId, String.Empty, parent)
        {
        }
    }

    /// <summary>
    /// 
    /// </summary>
    public class MonthAdapter: AbstractAdapter
    {
        internal MonthAdapter(): base()
        {
            
        }

        public override ObservableCollection<AbstractAdapterItem> Items
        {
            get {
                ObservableCollection<AbstractAdapterItem> items 
                    = new ObservableCollection<AbstractAdapterItem>();
                DataCore dataCore = DataCore.GetInstance();
                MonthIdentity[] months = dataCore.BackendCoreDriver.MonthIds;

                foreach (MonthIdentity month in months)
                {
                    items.Add(new MonthItem(month, this));
                }
                return items;
            }
        }
    }
}
