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
    public class GLAccountMasterDataFactory : MasterDataFactoryBase
    {

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="coreDriver"></param>
        /// <param name="management"></param>
        /// <exception cref="ArgumentNullException">Argument is null</exception>
        public GLAccountMasterDataFactory(CoreDriver coreDriver,
                MasterDataManagement management)
            : base(coreDriver, management)
        {
        }

        /// <summary>
        /// Create new master data 
        /// </summary>
        /// <param name="identity"></param>
        /// <param name="descp"></param>
        /// <param name="?"></param>
        /// <returns></returns>
        /// <exception cref="ParametersException">Parameters Exception</exception>
        /// <exception cref="MasterDataIdentityExists">Duplicated master data identity exists </exception>
        /// <exception cref="MasterDataIdentityNotDefined">Master data identity is not defined</exception>
        /// <exception cref="ArgumentNullException">Argument is null</exception>
        /// <exception cref="SystemException">Bug</exception>
        public override MasterDataBase CreateNewMasterDataBase(MasterDataIdentity identity,
                String descp, params Object[] objects)
        {
            // check id is G/L identity
            MasterDataIdentity_GLAccount identity_gl = identity as MasterDataIdentity_GLAccount;
            if (identity_gl == null)
            {
                throw new ParametersException();
            }
            // check duplicated
            if (_list.ContainsKey(identity))
            {
                throw new MasterDataIdentityExists();
            }

            MasterDataIdentity bankAccount = null;

            if (objects.Length == 0 || objects.Length == 1)
            {
            }
            else
            {
                throw new ParametersException(1, objects.Length);
            }

            if (objects.Length == 1)
            {
                // contain bank account
                bankAccount = objects[0] as MasterDataIdentity;
                if (bankAccount == null)
                {
                    throw new ParametersException();
                }
            }

            GLAccountMasterData glAccount;
            try
            {
                glAccount = new GLAccountMasterData(_coreDriver, _management,
                        identity_gl, descp, bankAccount);
            }
            catch (ArgumentNullException e)
            {
                throw new SystemException(e);
            }
            catch (NoGLAccountGroupException)
            {
                throw new ParametersException();
            }

            this._containDirtyData = true;
            this._list.Add(identity_gl, glAccount);

            // raise create master data
            _coreDriver.ListenerMgmt.CreateMasterData(this, glAccount);
            _coreDriver.logDebugInfo(this.GetType(), 84,
                    String.Format("Create G/L account ({0}).", glAccount.Identity.ToString()),
                    MessageType.INFO);
            return glAccount;
        }

        /// <summary>
        /// Parse master data from XML
        /// </summary>
        /// <param name="coreDriver"></param>
        /// <param name="elem"></param>
        /// <returns></returns>
        /// <exception cref="MasterDataFileFormatException">Master Data file exception</exception>
        /// <exception cref="ArgumentNullException">Argument is null</exception>
        /// <exception cref="SystemException">Bug</exception>
        public override MasterDataBase ParseMasterData(CoreDriver coreDriver, XElement elem)
        {
            XAttribute id = elem.Attribute(MasterDataUtils.XML_ID);
            XAttribute descp = elem.Attribute(MasterDataUtils.XML_DESCP);
            XAttribute bankAccStr = elem.Attribute(MasterDataUtils.XML_BANK_ACCOUNT);
            XAttribute initAmountAttr = elem.Attribute(MasterDataUtils.XML_INIT_AMOUNT);

            MasterDataIdentity_GLAccount identity;
            MasterDataIdentity bankAccId = null;
            try
            {
                identity = new MasterDataIdentity_GLAccount(
                        id.Value);

                // bank account
                if (bankAccStr != null)
                {
                    bankAccId = new MasterDataIdentity(
                            bankAccStr.Value);
                }

            }
            catch (Exception e)
            {
                _coreDriver.logDebugInfo(this.GetType(), 150, e.Message, MessageType.ERRO);
                throw new MasterDataFileFormatException(MasterDataType.GL_ACCOUNT);
            }

            CurrencyAmount initAmount = new CurrencyAmount();
            if (initAmountAttr != null)
            {
                try
                {
                    initAmount = CurrencyAmount.Parse(initAmountAttr.Value);
                }
                catch (CurrencyAmountFormatException e)
                {
                    _coreDriver.logDebugInfo(this.GetType(), 150, e.Message, MessageType.ERRO);
                    throw new MasterDataFileFormatException(MasterDataType.GL_ACCOUNT);
                }
            }
            try
            {
                GLAccountMasterData glAccount;
                if (bankAccId == null)
                {
                    glAccount = (GLAccountMasterData)this.CreateNewMasterDataBase(
                            identity, descp.Value);
                }
                else
                {
                    glAccount = (GLAccountMasterData)this.CreateNewMasterDataBase(
                                identity, descp.Value, bankAccId);
                }
                glAccount.setInitAmountInternal(initAmount);
                _coreDriver
                            .logDebugInfo(
                                    this.GetType(),
                                    167,
                                    String.Format("Parse G/L account ({0}).",
                                            glAccount.Identity.ToString()), MessageType.INFO);
                return glAccount;
            }
            catch (Exception e)
            {
                throw new SystemException(e);
            }

        }

        /// <summary>
        /// balance accounts
        /// </summary>
        /// <returns></returns>
        public List<GLAccountMasterData> BalanceAccounts
        {
            get
            {
                return GetAccounts(GLAccountGroup.BALANCE_GROUP);
            }
        }

        /// <summary>
        /// liquidity accounts
        /// </summary>
        public List<GLAccountMasterData> LiquidityAccounts
        {
            get
            {
                return GetAccounts(GLAccountGroup.Liquidity_GROUP);
            }
        }

        /// <summary>
        /// liability accounts
        /// </summary>
        public List<GLAccountMasterData> LiabilityAccounts
        {
            get
            {
                return GetAccounts(GLAccountGroup.LIABILITIES_GROUP);
            }
        }

        /// <summary>
        /// get revenue accounts
        /// </summary>
        public List<GLAccountMasterData> RevenueAccounts
        {
            get
            {
                return GetAccounts(GLAccountGroup.REVENUE_GROUP);
            }
        }

       /// <summary>
       /// get cost accounts
       /// </summary>
        public List<GLAccountMasterData> CostAccounts{ get { return GetAccounts(GLAccountGroup.COST_GROUP); } }

       /// <summary>
       /// get accounts based on the groups
       /// </summary>
       /// <param name="groups"></param>
       /// <returns></returns>
        public List<GLAccountMasterData> GetAccounts(GLAccountGroupENUM[] groups)
        {
            List<GLAccountMasterData> array = new List<GLAccountMasterData>();
            foreach (var item in this._list)
            {
                GLAccountMasterData glAccount = (GLAccountMasterData)item.Value;
                for (int i = 0; i < groups.Length; ++i)
                {
                    if (glAccount.Group.Identity == groups[i])
                    {
                        array.Add(glAccount);
                        break;
                    }
                }
            }

            array.Sort();

            return array;
        }
    }
}
