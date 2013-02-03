using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WinStore_FamilyAccountingCore.MasterData;

namespace WinStore_FamilyAccountingCore.Exceptions
{
    public class NoMasterDataFactoryClass : Exception
    {
        public readonly MasterDataType MdType;
        public NoMasterDataFactoryClass(MasterDataType type)
            : base(String.Format("Master data type {0} is not registered",
                type.ToString()))
        {
            MdType = type;
        }
    }
}
