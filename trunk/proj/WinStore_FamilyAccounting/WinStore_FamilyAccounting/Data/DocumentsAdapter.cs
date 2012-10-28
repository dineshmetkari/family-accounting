using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WinStore_FamilyAccounting.Data
{
    public class DocumentItem : AbstractAdapterItem
    {
        internal DocumentItem(Object docId, string name, 
            DocumentsAdapter parent)
            : base(docId, name, parent)
        {

        }

        public override string Name
        {
            get
            {
                return "这是一个测试记录";
            }
        }

        public string Amount
        {
            get { return "100.00"; }
        }

        public string Account
        {
            get { return "支出项目"; }
        }

        public DateTime DocumentDate
        {
            get { return DateTime.Today; }
        }
    }


    public class DocumentsAdapter : AbstractAdapter
    {
        public DocumentsAdapter()
        {
            for (int i = 0; i < 20; ++i)
            {
                _items.Add(new DocumentItem(string.Empty, string.Empty, null));
            }
        }
    }
}
