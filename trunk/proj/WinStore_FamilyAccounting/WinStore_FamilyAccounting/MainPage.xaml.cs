using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using Windows.Foundation;
using Windows.Foundation.Collections;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Controls.Primitives;
using Windows.UI.Xaml.Data;
using Windows.UI.Xaml.Input;
using Windows.UI.Xaml.Media;
using Windows.UI.Xaml.Navigation;
using WinStore_FamilyAccounting.Data;
using WinStore_FamilyAccounting.ReportPages;
using WinStore_FamilyAccountingCore;
using WinStore_FamilyAccountingCore.Transaction;
using WinStore_FamilyAccountingCore.Utilities;

// The Basic Page item template is documented at http://go.microsoft.com/fwlink/?LinkId=234237

namespace WinStore_FamilyAccounting
{
    /// <summary>
    /// A basic page that provides characteristics common to most applications.
    /// </summary>
    public sealed partial class MainPage : WinStore_FamilyAccounting.Common.LayoutAwarePage
    {
        private Frame _reportFrame;
        public MainPage()
        {
            this.InitializeComponent();

            _reportFrame = ReportFrame;
        }

        /// <summary>
        /// Populates the page with content passed during navigation.  Any saved state is also
        /// provided when recreating a page from a prior session.
        /// </summary>
        /// <param name="navigationParameter">The parameter value passed to
        /// <see cref="Frame.Navigate(Type, Object)"/> when this page was initially requested.
        /// </param>
        /// <param name="pageState">A dictionary of state preserved by this page during an earlier
        /// session.  This will be null the first time a page is visited.</param>
        protected override void LoadState(Object navigationParameter, Dictionary<String, Object> pageState)
        {
            // set month selection resource
            this.DefaultViewModel["MonthSelectionItems"] = DataCore.GetInstance().MonthsAdp.Items;
            // set reports selection resource
            this.DefaultViewModel["ReportsSelectionItems"] = DataCore.GetInstance().ReportsTypeAdp.Items;            

            // load balance data
            this.setBalanceData();
        }

        /// <summary>
        /// Preserves state associated with this page in case the application is suspended or the
        /// page is discarded from the navigation cache.  Values must conform to the serialization
        /// requirements of <see cref="SuspensionManager.SessionState"/>.
        /// </summary>
        /// <param name="pageState">An empty dictionary to be populated with serializable state.</param>
        protected override void SaveState(Dictionary<String, Object> pageState)
        {
        }

        /// <summary>
        /// month selection changed, reload data
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Month_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            setBalanceData();// set data
            setReport();
        }
        /// <summary>
        /// 
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ReportType_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            setReport();
        }
        /// <summary>
        /// set balance data
        /// </summary>
        private void setBalanceData()
        {
            // int index = MonthSelection.SelectedIndex;
            MonthItem monthItem = MonthSelection.SelectedItem as MonthItem;
            if (monthItem == null)
            {
                return;
            }
            CoreDriver coreDriver = DataCore.GetInstance().BackendCoreDriver;
            if (coreDriver.IsInitialize == false)
            {
                return;
            }

            TransactionDataManagement transMgmt = coreDriver.TransMgmt;
            GLAccountBalanceCollection balCol = transMgmt.AccountBalanceCol;          

            #region revenue
            CurrencyAmount revenue = new CurrencyAmount();
            foreach (GLAccountGroupENUM group in GLAccountGroup.REVENUE_GROUP) {
            CurrencyAmount cur = balCol
                    .GetGroupBalance(group, monthItem.MonthId, monthItem.MonthId);
                cur.Negate();
                revenue.AddTo(cur);
            }
            // set value
            incomingAmount.Text = revenue.ToString();
            #endregion

            #region cost
            CurrencyAmount cost = new CurrencyAmount();
            foreach (GLAccountGroupENUM group in GLAccountGroup.COST_GROUP)
            {
                CurrencyAmount cur = balCol
                        .GetGroupBalance(group, monthItem.MonthId, monthItem.MonthId);
                cost.AddTo(cur);
            }
            // set value
            this.outgoingAmount.Text = cost.ToString();
            #endregion

            #region balance
            CurrencyAmount balance = new CurrencyAmount();
            foreach (GLAccountGroupENUM group in GLAccountGroup.BALANCE_GROUP)
            {
                CurrencyAmount cur = balCol
                        .GetGroupBalance(group);
                balance.AddTo(cur);
            }
            // set value
            this.balanceAmount.Text = balance.ToString();
            #endregion

            #region liquidity
            CurrencyAmount liquidity = new CurrencyAmount();
            foreach (GLAccountGroupENUM group in GLAccountGroup.Liquidity_GROUP)
            {
                CurrencyAmount cur = balCol
                        .GetGroupBalance(group);
                liquidity.AddTo(cur);
            }
            // set value
            this.liquidityAmount.Text = liquidity.ToString();
            #endregion
        }

        /// <summary>
        /// set report
        /// </summary>
        private void setReport()
        {
            ReportTypeItem item = this.ReportSelection.SelectedItem as ReportTypeItem;
            if (item == null)
            {
                return;
            }
            switch (item.TypeId)
            {
                case ReportTypesEnum.ALL_DOCUMENTS:
                    break;
                case ReportTypesEnum.COST_REPORTS:
                    this._reportFrame.Navigate(typeof(CostDetailPage), MonthSelection.SelectedItem);
                    break;
                case ReportTypesEnum.BALANCE_REPORTS:
                    break;
                case ReportTypesEnum.LIQUIDITY_REPORTS:
                    break;
            }
        }
    }
}
