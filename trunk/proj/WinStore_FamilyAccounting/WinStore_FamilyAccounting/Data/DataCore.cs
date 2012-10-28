﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

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
            _monthAdapter = new MonthAdapter();
            _reportAdatper = new ReportTypesAdapter();
            _costAdapter = new CostReportsAdapter();
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
        #endregion
    }
}
