using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WinStore_FamilyAccountingCore
{
    public abstract class AbstractManagement
    {
        protected readonly CoreDriver _coreDriver;
        public CoreDriver Driver { get { return _coreDriver; } } 

        internal AbstractManagement(CoreDriver coreDriver)
        {
            _coreDriver = coreDriver;
        }

        public abstract bool NeedInit{get;}
        public abstract bool NeedEstablishFile { get; }
        /// <summary>
        /// initialize the management
        /// </summary>
        public abstract Task InitializeAsync();

        /// <summary>
        /// Clear the data in the management
        /// </summary>
        public abstract void Clear();

        public abstract Task EstablishFilesAsync();
    }
}
