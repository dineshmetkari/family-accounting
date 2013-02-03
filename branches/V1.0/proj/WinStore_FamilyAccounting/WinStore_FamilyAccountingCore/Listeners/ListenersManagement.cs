using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WinStore_FamilyAccountingCore.MasterData;
using WinStore_FamilyAccountingCore.Transaction;
using WinStore_FamilyAccountingCore.Utilities;

namespace WinStore_FamilyAccountingCore.Listeners
{
    public delegate void DocumentLoad(Object source, HeadEntity document);
    public delegate void MasterDataLoad(Object source, MasterDataBase masterData);
    public delegate void DocumentSaved(HeadEntity document);
    public delegate void MasterDataCreated(MasterDataFactoryBase factory, MasterDataBase master);
    public delegate void DocumentReversed(HeadEntity doc);
    public delegate void GLAccountInitAmountChanged(MasterDataIdentity_GLAccount glAccountId
        , CurrencyAmount orgAmount, CurrencyAmount newAmount);

    /// <summary>
    /// List listener management
    /// </summary>
    public class ListenersManagement
    {
        public event DocumentLoad DocumentLoadHandler;
        public event MasterDataLoad MasterDataLoadHandler;
        public event DocumentSaved DocumentSavedHandler;
        public event MasterDataCreated MasterDataCreatedHandler;
        public event DocumentReversed DocumentReversedHandler;
        public event GLAccountInitAmountChanged GLAccountInitAmountChangedHandler;

        /// <summary>
        /// Construct
        /// </summary>
        public ListenersManagement()
        {

        }

        /// <summary>
        /// Raise save document event
        /// </summary>
        /// <param name="doc"></param>
        public void SaveDoc(HeadEntity doc)
        {
            foreach (DocumentSaved d in DocumentSavedHandler.GetInvocationList())
            {
                d(doc);
            }
        }

        /// <summary>
        /// Raise reverse document event
        /// </summary>
        /// <param name="doc"></param>
        public void ReverseDoc(HeadEntity doc)
        {
            foreach (DocumentReversed d in DocumentReversedHandler.GetInvocationList())
            {
                d(doc);
            }
        }

        /// <summary>
        /// Raise load document event
        /// </summary>
        /// <param name="source"></param>
        /// <param name="doc"></param>
        public void LoadDoc(Object source, HeadEntity doc)
        {
            foreach (DocumentLoad d in DocumentLoadHandler.GetInvocationList())
            {
                d(source, doc);
            }
        }

        /// <summary>
        /// Raise master data created event
        /// </summary>
        /// <param name="factory"></param>
        /// <param name="masterData"></param>
        public void CreateMasterData(MasterDataFactoryBase factory,
                MasterDataBase masterData)
        {
            foreach (MasterDataCreated d in MasterDataCreatedHandler.GetInvocationList())
            {
                d(factory, masterData);
            }
        }

        /// <summary>
        /// Master data load
        /// </summary>
        /// <param name="source"></param>
        /// <param name="masterData"></param>
        public void LoadMasterData(Object source, MasterDataBase masterData)
        {
            foreach (MasterDataLoad d in MasterDataLoadHandler.GetInvocationList())
            {
                d(source, masterData);
            }
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="source"></param>
        /// <param name="glAccountId"></param>
        /// <param name="orgAmount"></param>
        /// <param name="newAmount"></param>
        public void GLAccountInitAmountChanged(Object source, MasterDataIdentity_GLAccount glAccountId,
            CurrencyAmount orgAmount, CurrencyAmount newAmount)
        {
            foreach (GLAccountInitAmountChanged d in 
                GLAccountInitAmountChangedHandler.GetInvocationList())
            {
                d(glAccountId, orgAmount, newAmount);
            }
        }
    }
}
