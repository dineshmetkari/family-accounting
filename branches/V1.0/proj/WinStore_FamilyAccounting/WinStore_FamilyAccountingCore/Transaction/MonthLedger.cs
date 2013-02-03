using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Linq;

namespace WinStore_FamilyAccountingCore.Transaction
{
    /// <summary>
    /// Month Ledger
    /// </summary>
    public class MonthLedger
    {
        private readonly Dictionary<DocumentIdentity, HeadEntity> _list;
        private readonly MonthIdentity _monthId;
        public MonthIdentity MonthId { get { return _monthId; } }
        public int Count { get { return _list.Count; } }
        public List<HeadEntity> Entities
        {
            get
            {
                List<HeadEntity> headArray = new List<HeadEntity>(
                    _list.Values);
                headArray.Sort();
                return headArray;
            }
        }

        /// <summary>
        /// Month ledger
        /// </summary>
        /// <param name="monthId"></param>
        public MonthLedger(MonthIdentity monthId)
        {
            _monthId = monthId;
            _list = new Dictionary<DocumentIdentity, HeadEntity>();
        }

        /// <summary>
        /// Add new entity
        /// </summary>
        /// <param name="head"></param>
        internal void Add(HeadEntity head)
        {
            DocumentIdentity id = head.DocIdentity;
            _list.Add(id, head);
        }

        /// <summary>
        /// get head entity based on document identity
        /// </summary>
        /// <param name="docId"></param>
        /// <returns></returns>
        public HeadEntity GetEntity(DocumentIdentity docId)
        {
            if (!_list.ContainsKey(docId))
            {
                return null;
            }
            HeadEntity entity;
            _list.TryGetValue(docId, out entity);
            return entity;
        }

        /// <summary>
        /// Parse to XDoc
        /// </summary>
        /// <returns></returns>
        public XDocument toXML()
        {
            XElement xrootElem = new XElement(TransDataUtils.XML_ROOT);
            XDocument xdoc = new XDocument(xrootElem);
            foreach (var item in _list)
            {
                xrootElem.Add(item.Value.ToXml());
            }

            return xdoc;
        }
    }
}
