using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WinStore_FamilyAccounting.Data
{
    public class CostReportItem : AbstractAdapterItem
    {
        public CostReportItem(Object id, String name, CostReportsAdapter parent)
            : base(id, name, parent)
        {
        }

        public String Amount { get { return "100.00"; } }
    }

    /// <summary>
    /// 
    /// </summary>
    public class CostReportsAdapter : AbstractAdapter
    {
        internal CostReportsAdapter()
        {
        }

        public override ObservableCollection<AbstractAdapterItem> Items
        {
            get
            {
                ObservableCollection<AbstractAdapterItem> items 
                    = new ObservableCollection<AbstractAdapterItem>();
                for (int i = 0; i < 20; ++i)
                {
                    items.Add(new CostReportItem(String.Format("id{0}", i),
                        String.Format("支出项目 {0}", i), this));
                }

                return items;
            }
        }
    }
}
