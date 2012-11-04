using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WinStore_FamilyAccountingCore.DocumentEntries
{
    public class EntryTemplatesManagement : AbstractManagement
    {
        public static readonly string FILE_NAME = "templates.xml";

        public EntryTemplatesManagement(CoreDriver coreDriver)
            : base(coreDriver)
        {
        }

        public override async Task InitializeAsync()
        {
        }

        public override void Clear()
        {
        }

        public override async Task EstablishFilesAsync()
        {
        }
    }
}
