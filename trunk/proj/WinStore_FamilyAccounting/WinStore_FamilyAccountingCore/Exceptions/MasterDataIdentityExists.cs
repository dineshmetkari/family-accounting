﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WinStore_FamilyAccountingCore.Exceptions
{
    public class MasterDataIdentityExists : Exception
    {
        public MasterDataIdentityExists()
            : base("Master data identity exists in the factory")
        {
        }
    }
}
