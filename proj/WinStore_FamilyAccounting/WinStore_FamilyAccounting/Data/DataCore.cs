using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WinStore_FamilyAccountingCore;

namespace WinStore_FamilyAccounting.Data
{
    public class DataCore
    {
        private static DataCore _instance;
        public static DataCore GetInstance()
        {
            if (_instance == null)
            {
                _instance = new DataCore();
            }
            return _instance;
        }

        /// <summary>
        /// constructor
        /// </summary>
        private DataCore()
        {
            _monthAdapter = new MonthAdapter(this);
            _reportAdatper = new ReportTypesAdapter(this);
            _costAdapter = new CostReportsAdapter(this);
            _coreDriver = new CoreDriver();
        }

        #region Properties
        /// <summary>
        /// month adapter
        /// </summary>
        private readonly MonthAdapter _monthAdapter;
        public MonthAdapter MonthsAdp { get { return _monthAdapter; } }

        /// <summary>
        /// report types adapter
        /// </summary>
        private readonly ReportTypesAdapter _reportAdatper;
        public ReportTypesAdapter ReportsTypeAdp { get { return _reportAdatper; } }

        private readonly CostReportsAdapter _costAdapter;
        public CostReportsAdapter CostAdp { get { return _costAdapter; } }

        private readonly CoreDriver _coreDriver;
        public CoreDriver BackendCoreDriver { get { return _coreDriver; } }
        #endregion

        public async Task InitializeAsync()
        {
            await _coreDriver.RestartAsync();
        }
    }
}
