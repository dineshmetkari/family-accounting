using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WinStore_FamilyAccountingCore.Exceptions
{
    /// <summary>
    /// Application should not catch the exception.
    /// if the exception occurs, it is the system bug.
    /// </summary>
    public class SystemException : Exception
    {
        private readonly Exception _innerException;
        public Exception InternalException { get { return _innerException; } }
        public SystemException(Exception e): base(e.Message)
        {
            _innerException = e;
        }
    }
}
