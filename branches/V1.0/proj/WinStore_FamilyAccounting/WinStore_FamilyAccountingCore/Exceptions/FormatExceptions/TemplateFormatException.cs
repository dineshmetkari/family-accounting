using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WinStore_FamilyAccountingCore.Exceptions.FormatExceptions
{
    public class TemplateFormatException: Exception
    {
        public TemplateFormatException()
            : base("Template file format error.")
        {
        }
    }
}
