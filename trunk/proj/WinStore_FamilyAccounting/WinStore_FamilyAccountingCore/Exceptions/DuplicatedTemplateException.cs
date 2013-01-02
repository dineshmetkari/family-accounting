using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WinStore_FamilyAccountingCore.Exceptions
{
    public class DuplicatedTemplateException : Exception
    {
        public DuplicatedTemplateException(int id)
            : base("Duplicated template identity " + id)
        {
        }
    }
}
