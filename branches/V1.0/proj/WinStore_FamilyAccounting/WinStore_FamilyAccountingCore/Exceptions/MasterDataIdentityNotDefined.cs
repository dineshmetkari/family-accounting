using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WinStore_FamilyAccountingCore.MasterData;

namespace WinStore_FamilyAccountingCore.Exceptions
{
    public class MasterDataIdentityNotDefined : Exception
    {
        public MasterDataIdentity MdId { get; protected set; }
        public MasterDataType MdType { get; protected set; }
        public MasterDataIdentityNotDefined(MasterDataIdentity id,
            MasterDataType type)
            : base(String.Format("Bank Key %s is not defined %s", id.ToString(),
                type.ToString()))
        {
            MdId = id;
            MdType = type;
        }
    }
}
