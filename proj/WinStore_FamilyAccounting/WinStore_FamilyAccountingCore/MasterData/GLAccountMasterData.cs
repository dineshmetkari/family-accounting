using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Linq;
using WinStore_FamilyAccountingCore.Exceptions;
using WinStore_FamilyAccountingCore.Exceptions.FormatExceptions;
using WinStore_FamilyAccountingCore.Utilities;

namespace WinStore_FamilyAccountingCore.MasterData
{
    public class GLAccountMasterData : MasterDataBase
    {
        public static readonly String FILE_NAME = "gl_account.xml";
        private MasterDataIdentity _bankAccount;
        public MasterDataIdentity BankAccount { get { return _bankAccount; } }
        private readonly GLAccountGroup _group;
        public GLAccountGroup Group { get { return _group; } }
        private CurrencyAmount _initAmount;
        public CurrencyAmount InitAmount { get { return new CurrencyAmount(_initAmount); } }




        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="coreDriver"></param>
        /// <param name="management"></param>
        /// <param name="id"></param>
        /// <param name="descp"></param>
        /// <exception cref="ArgumentNullException">argument is null</exception>
        public GLAccountMasterData(CoreDriver coreDriver,
                MasterDataManagement management, MasterDataIdentity_GLAccount id,
                String descp)
            : this(coreDriver, management, id, descp, null)
        {
        }


        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="coreDriver"></param>
        /// <param name="management"></param>
        /// <param name="id"></param>
        /// <param name="descp"></param>
        /// <param name="bankAccount"></param>
        /// <exception cref="ArgumentNullException">Argument is null.</exception>
        /// <exception cref="NoGLAccountGroupException">No such G\L account group exception</exception>
        public GLAccountMasterData(CoreDriver coreDriver,
                MasterDataManagement management, MasterDataIdentity_GLAccount id,
                String descp, MasterDataIdentity bankAccount)
            : base(coreDriver, management, id, descp)
        {
            // check id and get group
            String groupId = id.ToString().Substring(0, 4);
            try
            {
                _group = GLAccountGroup.Parse(groupId);
            }
            catch (GLAccountGroupFormatException)
            {
                throw new NoGLAccountGroupException(groupId);
            }

            if (bankAccount == null)
            {
                _bankAccount = null;
            }
            else
            {
                MasterDataBase bankAccountId = _management.GetMasterData(
                        bankAccount, MasterDataType.BANK_ACCOUNT);
                if (bankAccountId == null)
                {
                    throw new MasterDataIdentityNotDefined(bankAccount,
                            MasterDataType.BANK_ACCOUNT);
                }
                _bankAccount = bankAccountId.GetIdentity();
            }

            // init amount
            _initAmount = new CurrencyAmount();
        }

        /// <summary>
        /// 
        /// </summary>
        /// <returns></returns>
        public MasterDataIdentity_GLAccount GLIdentity
        {
            get
            {
                return (MasterDataIdentity_GLAccount)_identity;
            }
        }

        /// <summary>
        /// set init amount
        /// </summary>
        /// <param name="amount"></param>
        /// <returns></returns>
        public bool SetInitAmount(CurrencyAmount amount)
        {
            bool ret = setInitAmountInternal(amount);
            if(ret)
                this.SetDirtyData();
            return ret;
        }

        /// <summary>
        /// set initialize amount, cost & revenue account cannot set init amount
        /// </summary>
        /// <param name="amount"></param>
        /// <returns></returns>
        internal bool setInitAmountInternal(CurrencyAmount amount)
        {
            // check group
            foreach(GLAccountGroupENUM group in GLAccountGroup.COST_GROUP)
            {
                if (this.Group.Identity == group)
                {
                    return false;
                }
            }
            foreach (GLAccountGroupENUM group in GLAccountGroup.REVENUE_GROUP)
            {
                if (this.Group.Identity == group)
                {
                    return false;
                }
            }

            CurrencyAmount orgAmount = _initAmount;
            _initAmount = new CurrencyAmount(amount);

            _coreDriver.ListenerMgmt.GLAccountInitAmountChanged(this, this.GLIdentity, orgAmount, amount);
            //this.SetDirtyData();
            return true;
        }



        /// <summary>
        /// Set bank account 
        /// </summary>
        /// <param name="bankAccount"></param>
        /// <exception cref="MasterDataIdentityNotDefined"></exception>
        public void SetBankAccount(MasterDataIdentity bankAccount)
        {
            if (bankAccount == null)
            {
                // clear
                _bankAccount = null;
                return;
            }
            MasterDataBase bankAccountId = this._management.GetMasterData(bankAccount,
                    MasterDataType.BANK_ACCOUNT);
            if (bankAccountId == null)
            {
                throw new MasterDataIdentityNotDefined(bankAccount,
                        MasterDataType.BANK_ACCOUNT);
            }
            this.SetDirtyData();
            _bankAccount = bankAccountId.Identity;
        }

        /// <summary>
        /// Parse memory to xml
        /// </summary>
        /// <returns></returns>
        public override XElement ToXML()
        {
            XElement xelem = base.ToXML();
            if (_bankAccount != null)
            {
                xelem.Add(new XAttribute(MasterDataUtils.XML_BANK_ACCOUNT
                    , _bankAccount.ToString()));
            }
            xelem.Add(new XAttribute(MasterDataUtils.XML_INIT_AMOUNT
                , _initAmount.ToString()));

            return xelem;
        }
    }
}