﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WinStore_FamilyAccountingCore.MasterData
{
    public class CustomerMasterData : MasterDataBase
    {
        public static readonly String FILE_NAME = "customer.xml";

        /// <summary>
        /// Construct
        /// </summary>
        /// <param name="coreDriver"></param>
        /// <param name="management"></param>
        /// <param name="id"></param>
        /// <param name="descp"></param>
        /// <exception cref="ArgumentNullException">argument is null</exception>
        public CustomerMasterData(CoreDriver coreDriver, MasterDataManagement management, MasterDataIdentity id,
                String descp)
            : base(coreDriver, management, id, descp)
        {
        }
    }
}
