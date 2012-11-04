using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WinStore_FamilyAccountingCore.Exceptions;
using WinStore_FamilyAccountingCore.MasterData;
using WinStore_FamilyAccountingCore.Utilities;

namespace WinStore_FamilyAccountingCore.Transaction
{
    public class GLAccountBalanceCollection
    {
        private readonly CoreDriver _coreDriver;

        private readonly MasterDataManagement _mdMgmt;
        public MasterDataManagement MdMgmt { get { return _mdMgmt; } }

        private readonly Dictionary<MasterDataIdentity_GLAccount, GLAccountBalanceItem> _items;

        internal GLAccountBalanceCollection(CoreDriver coreDriver,
                MasterDataManagement mdMgmt)
        {
            _coreDriver = coreDriver;
            _mdMgmt = mdMgmt;

            _items = new Dictionary<MasterDataIdentity_GLAccount, GLAccountBalanceItem>();

            _coreDriver.ListenerMgmt.DocumentSavedHandler += documentSaved;
            _coreDriver.ListenerMgmt.DocumentLoadHandler += documentLoad;
            _coreDriver.ListenerMgmt.MasterDataLoadHandler += masterDataLoad;
            _coreDriver.ListenerMgmt.MasterDataCreatedHandler += masterDataCreated;
            _coreDriver.ListenerMgmt.DocumentReversedHandler += documentReversed;
            _coreDriver.ListenerMgmt.GLAccountInitAmountChangedHandler += glAccountInitAmountChanged;
        }

        #region Events handler
        private void documentLoad(Object source, HeadEntity document)
        {
            newDoc(document);
        }

        private void documentReversed(HeadEntity doc)
        {
            reverseDoc(doc);
        }

        private void documentSaved(HeadEntity document)
        {
            newDoc(document);
        }
        private void masterDataLoad(Object source, MasterDataBase masterData)
        {
            newMasterdata(masterData);
        }

        private void masterDataCreated(MasterDataFactoryBase factory, MasterDataBase master)
        {
            newMasterdata(master);
        }

        private void glAccountInitAmountChanged(MasterDataIdentity_GLAccount glAccountId
            , CurrencyAmount orgAmount, CurrencyAmount newAmount)
        {
            GLAccountBalanceItem item;
            if (!_items.TryGetValue(glAccountId, out item))
            {
                return;
            }

            // ret += newAmount - orgAmount
            item.AddAmount(_coreDriver.StartMonthId
                , CurrencyAmount.Minus(newAmount, orgAmount));
        }
        #endregion


        /// <summary>
        /// reverse document
        /// </summary>
        /// <param name="doc"></param>
        private void reverseDoc(HeadEntity doc)
        {
            foreach (ItemEntity item in doc.DocItems)
            {
                CurrencyAmount amount = item.Amount;
                if (item.CdIndicator == CreditDebitIndicator.DEBIT)
                {
                    amount.Negate();
                }
                GLAccountBalanceItem balItem;
                if (!_items.TryGetValue(item.GLAccount, out balItem))
                {
                    continue;
                }

                balItem.AddAmount(doc.MonthID, amount);
            }
        }
        /// <summary>
        /// set report when new doc
        /// </summary>
        /// <param name="head"></param>
        private void newDoc(HeadEntity head)
        {
            foreach (ItemEntity item in head.DocItems)
            {
                CurrencyAmount amount = item.Amount;
                if (item.CdIndicator == CreditDebitIndicator.CREDIT)
                {
                    amount.Negate();
                }
                GLAccountBalanceItem balItem;
                if (!_items.TryGetValue(item.GLAccount, out balItem))
                {
                    continue;
                }

                balItem.AddAmount(head.MonthID, amount);
            }
        }

        /// <summary>
        /// set report when new master data
        /// </summary>
        /// <param name="data"></param>
        private void newMasterdata(MasterDataBase data)
        {
            GLAccountMasterData glAcc = data as GLAccountMasterData;
            if (glAcc == null)
            {
                return;
            }
            if (!_items.ContainsKey(glAcc.GLIdentity))
            {
                GLAccountBalanceItem item = new GLAccountBalanceItem(glAcc.GLIdentity);
                _items.Add(glAcc.GLIdentity, item);
                item.AddAmount(_coreDriver.StartMonthId, glAcc.InitAmount);
            }
        }

        /// <summary>
        /// get balance item
        /// </summary>
        /// <param name="glAccount"></param>
        /// <returns></returns>
        public GLAccountBalanceItem GetBalanceItem(
                MasterDataIdentity_GLAccount glAccount)
        {
            GLAccountBalanceItem item;
            if (!_items.TryGetValue(glAccount, out item))
            {
                return null;
            }
            return item;
        }

        /// <summary>
        /// Get group balance amount
        /// </summary>
        /// <param name="accountGroup"></param>
        /// <returns></returns>
        /// <exception cref="SystemException"></exception>
        public CurrencyAmount GetGroupBalance(GLAccountGroupENUM accountGroup)
        {
            CurrencyAmount ret = new CurrencyAmount();
            MasterDataManagement mdMgmt = this._mdMgmt;
            foreach (var item in _items)
            {
                GLAccountMasterData glAccount;
                try
                {
                    glAccount = (GLAccountMasterData)mdMgmt
                           .GetMasterData(item.Key, MasterDataType.GL_ACCOUNT);
                }
                catch (Exception e)
                {
                    _coreDriver.logDebugInfo(this.GetType(), 183, e.Message, MessageType.ERRO);
                    throw new SystemException(e);
                }
                if (glAccount.Group.Identity == accountGroup)
                {
                    ret.AddTo(item.Value.Sum);
                }
            }

            return ret;
        }

        /// <summary>
        /// Get group balance from start month to end month
        /// </summary>
        /// <param name="accountGroup"></param>
        /// <param name="startMonthId"></param>
        /// <param name="endMonthId"></param>
        /// <returns></returns>
        /// <exception cref="SystemException"></exception>
        public CurrencyAmount GetGroupBalance(GLAccountGroupENUM accountGroup,
            MonthIdentity startMonthId, MonthIdentity endMonthId)
        {
            CurrencyAmount ret = new CurrencyAmount();
            MasterDataManagement mdMgmt = this._mdMgmt;
            foreach (var item in _items)
            {
                GLAccountMasterData glAccount;
                try
                {
                    glAccount = (GLAccountMasterData)mdMgmt
                           .GetMasterData(item.Key, MasterDataType.GL_ACCOUNT);
                }
                catch (Exception e)
                {
                    _coreDriver.logDebugInfo(this.GetType(), 183, e.Message, MessageType.ERRO);
                    throw new SystemException(e);
                }
                if (glAccount.Group.Identity == accountGroup)
                {
                    ret.AddTo(item.Value.GetSumAmount(startMonthId, endMonthId));
                }
            }

            return ret;
        }

    }
}
