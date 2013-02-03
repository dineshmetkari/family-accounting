using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WinStore_FamilyAccountingCore.Exceptions
{
    public class NoGLAccountGroupException : Exception
    {
        public NoGLAccountGroupException(String groupID)
            : base(String
                .Format("The G/L account group %s does not exist.", groupID))
        {
        }
    }
}
