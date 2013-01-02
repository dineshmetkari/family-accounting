using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WinStore_FamilyAccountingCore.Transaction;

namespace WinStore_FamilyAccounting.Data
{
    public class CostDocumentAdapterItem : AbstractAdapterItem
    {
        private readonly HeadEntity _head;
        internal CostDocumentAdapterItem(HeadEntity head, string name, 
            DocumentsAdapter parent)
            : base(head.DocIdentity, name, parent)
        {
            _head = head;
        }

        /// <summary>
        /// compare
        /// </summary>
        /// <param name="other"></param>
        /// <returns></returns>
        public override int CompareTo(AbstractAdapterItem other)
        {
            CostDocumentAdapterItem item = other as CostDocumentAdapterItem;
            if (item == null)
            {
                return 0;
            }
            return _head.PstDate.CompareTo(item._head.PstDate);
        }
    }

    /// <summary>
    /// cost document
    /// </summary>
    public class CostDocumentAdapter : AbstractAdapter
    {
        private ObservableCollection<AbstractAdapterItem> _item;
        public CostDocumentAdapter(DataCore dataCore)
            : base(dataCore)
        {
        }

        
    }
}
