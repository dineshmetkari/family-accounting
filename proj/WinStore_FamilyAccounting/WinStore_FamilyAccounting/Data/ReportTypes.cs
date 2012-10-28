using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WinStore_FamilyAccounting.Common;

namespace WinStore_FamilyAccounting.Data
{

    public enum ReportTypesEnum
    {
        COST_REPORTS, BALANCE_REPORTS, LIQUIDITY_REPORTS, ALL_DOCUMENTS
    }

    /// <summary>
    /// report type item
    /// </summary>
    public class ReportTypeItem : AbstractAdapterItem
    {
        public ReportTypeItem(ReportTypesEnum typeId, string name, ReportTypesAdapter parent)
            : base(typeId, name, parent)
        {
        }
        public ReportTypesEnum TypeId { get { return (ReportTypesEnum)Identity; } }
    }
    /// <summary>
    /// Report Type
    /// </summary>
    public class ReportTypesAdapter: AbstractAdapter
    {
        /// <summary>
        /// 
        /// </summary>
        internal ReportTypesAdapter()
        {

            // add items
            _items.Add(new ReportTypeItem(ReportTypesEnum.COST_REPORTS, "支出详情", this));
            _items.Add(new ReportTypeItem(ReportTypesEnum.LIQUIDITY_REPORTS, "现金详情", this));
            _items.Add(new ReportTypeItem(ReportTypesEnum.BALANCE_REPORTS, "总资产详情", this));
            _items.Add(new ReportTypeItem(ReportTypesEnum.ALL_DOCUMENTS, "所有记录", this));
        }
    }
}
