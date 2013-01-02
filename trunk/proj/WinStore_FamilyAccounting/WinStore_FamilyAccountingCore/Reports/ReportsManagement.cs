using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Storage;
using WinStore_FamilyAccountingCore.MasterData;

namespace WinStore_FamilyAccountingCore.Reports
{
    public class ReportsManagement : AbstractManagement
    {
        protected readonly DocumentIndex[] indexes;
        protected MasterDataManagement _mdMgmt;

        public ReportsManagement(CoreDriver coreDriver, MasterDataManagement mdMgmt)
            : base(coreDriver)
        {
            _mdMgmt = mdMgmt;
            indexes = new DocumentIndex[DocumentIndex.INDEX_COUNT];
            indexes[DocumentIndex.ACCOUNT_INDEX] = new DocumentAccountIndex(
                    _coreDriver, mdMgmt);
            indexes[DocumentIndex.BUSINESS_INDEX] = new DocumentBusinessIndex(
                    _coreDriver, mdMgmt);
        }

        /// <summary>
        /// get document index keys
        /// </summary>
        /// <param name="indexType"></param>
        /// <returns></returns>
        public List<MasterDataIdentity> getDocumentAccountIndexKeys(
                int indexType)
        {
            DocumentIndex index = indexes[indexType];
            return index.Keys;
        }

        /// <summary>
        /// get document index
        /// </summary>
        /// <param name="indexType"></param>
        /// <returns></returns>
        public DocumentIndex getDocumentIndex(int indexType)
        {
            DocumentIndex index = indexes[indexType];
            return index;
        }

        /// <summary>
        /// clear
        /// </summary>
        public override void Clear()
        {
            foreach (DocumentIndex index in indexes)
            {
                index.Clear();
            }
        }

        public override Task InitializeAsync()
        {
            throw new NotImplementedException();
        }

        public override Task EstablishFilesAsync()
        {
            throw new NotImplementedException();
        }

        public override bool NeedInit
        {
            get { return false; }
        }

        public override bool NeedEstablishFile
        {
            get { return false; }
        }
    }
}
