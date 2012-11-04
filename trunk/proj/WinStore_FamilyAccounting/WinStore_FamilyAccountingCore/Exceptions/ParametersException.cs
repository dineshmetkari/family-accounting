using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WinStore_FamilyAccountingCore.Exceptions
{
    public class ParametersException : Exception
    {
        public ParametersException(int expLen, int actLen)
            : base(string.Format("Count of parameters should be {0}, but actual {1}", expLen, actLen))
        {
        }

        public ParametersException()
            : base("Type of parameters is error. ")
        {
        }
    }
}
