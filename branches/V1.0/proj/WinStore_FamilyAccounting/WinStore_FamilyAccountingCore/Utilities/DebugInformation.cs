using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WinStore_FamilyAccountingCore.Utilities
{
    public sealed class DebugInformation
    {
        private readonly Type _cl;
        public Type CL { get { return _cl; } }

        private readonly int _lineNum;
        public int LineNumber { get { return _lineNum; } }

        private readonly String _info;
        public string Info { get { return _info; } }

        private readonly MessageType _type;
        public MessageType MessageType { get { return _type; } }

        public DebugInformation(Type cl, int lineNum, String info,
                MessageType type)
        {
            _cl = cl;
            _lineNum = lineNum;
            _info = info;
            _type = type;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <returns></returns>
        public override String ToString()
        {
            return String.Format("%s: %s, %s\t----%s(%d)",
                    DateTime.Today.ToString("yyyy-MM-dd HH:mm:ss"), _type, _info,
                    _cl.Name, _lineNum);
        }
    
    }
}
