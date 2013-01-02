using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WinStore_FamilyAccountingCore.MasterData;
using WinStore_FamilyAccountingCore.Transaction;

namespace WinStore_FamilyAccountingCore.Reports
{
    public abstract class DocumentIndex
    {
        /// <summary>
        /// compararion
        /// </summary>
        public readonly static Comparison<HeadEntity> COMPARATOR_DATE = new Comparison<HeadEntity>(
            delegate(HeadEntity head1, HeadEntity head2)
            {
                return head1.PstDate.CompareTo(head2.PstDate);
            });

        public readonly static int ACCOUNT_INDEX = 0;

        public readonly static int BUSINESS_INDEX = 1;

        public readonly static int INDEX_COUNT = 2;

        protected readonly Dictionary<MasterDataIdentity, DocumentIndexItem> _list;

        protected readonly CoreDriver _coreDriver;

        protected readonly MasterDataManagement _mdMgmt;


        // load document listener
        private void documentLoad(Object source, HeadEntity document)
        {
            newDoc(document);
        }

        // save document listener
        private void documentSaved(HeadEntity document)
        {
            newDoc(document);
        }


        // reveser document listener

        private void documentReversed(HeadEntity doc)
        {
            reverseDoc(doc);
        }

        /// <summary>
        /// document index
        /// </summary>
        /// <param name="coreDriver"></param>
        /// <param name="mdMgmt"></param>
        protected DocumentIndex(CoreDriver coreDriver, MasterDataManagement mdMgmt)
        {
            _coreDriver = coreDriver;
            _mdMgmt = mdMgmt;

            _list = new Dictionary<MasterDataIdentity, DocumentIndexItem>();

            _coreDriver.ListenerMgmt.DocumentSavedHandler += documentSaved;
            _coreDriver.ListenerMgmt.DocumentLoadHandler += documentLoad;
            _coreDriver.ListenerMgmt.DocumentReversedHandler += documentReversed;
        }

        /// <summary>
        /// keys
        /// </summary>
        public List<MasterDataIdentity> Keys
        {
            get
            {
                List<MasterDataIdentity> ret = new List<MasterDataIdentity>(
                        _list.Keys);
                ret.Sort();

                return ret;
            }
        }

        /// <summary>
        /// get index item
        /// </summary>
        /// <param name="key"></param>
        /// <returns></returns>
        public DocumentIndexItem getIndexItem(MasterDataIdentity key)
        {
            DocumentIndexItem item;
            if (_list.TryGetValue(key, out item))
            {
                return item;
            } 
            return null;
        }

        /// <summary>
        /// clear
        /// </summary>
        public void Clear()
        {
            _list.Clear();
        }

        /// <summary>
        /// set report when new document
        /// </summary>
        /// <param name="head"></param>
        protected abstract void newDoc(HeadEntity head);

        protected abstract void reverseDoc(HeadEntity head);
    }
}
