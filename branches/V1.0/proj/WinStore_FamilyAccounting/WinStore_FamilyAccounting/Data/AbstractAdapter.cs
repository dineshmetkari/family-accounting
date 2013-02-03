using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WinStore_FamilyAccounting.Common;

namespace WinStore_FamilyAccounting.Data
{
    public abstract class AbstractAdapterItem : BindableBase, IComparable<AbstractAdapterItem>
    {
        private readonly Object _id;
        public Object Identity { get { return _id; } }

        private readonly string _name;
        public virtual String Name { get { return _name; } }

        private readonly AbstractAdapter _parent;
        public AbstractAdapter Parent { get { return _parent; } }
        protected AbstractAdapterItem(Object id, string name, AbstractAdapter parent)
        {
            _id = id;
            _name = name;
            _parent = parent;
        }

        public abstract int CompareTo(AbstractAdapterItem other);
    }

    /// <summary>
    /// adapter
    /// </summary>
    public abstract class AbstractAdapter
    {
        protected readonly DataCore _dataCore;
        protected AbstractAdapter(DataCore dataCore)
        {
            _dataCore = dataCore;
        }
    }
}
