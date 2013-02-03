using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Linq;
using WinStore_FamilyAccountingCore.Exceptions;
using WinStore_FamilyAccountingCore.Exceptions.FormatExceptions;
using WinStore_FamilyAccountingCore.MasterData;
using WinStore_FamilyAccountingCore.Utilities;

namespace WinStore_FamilyAccountingCore.DocumentEntries
{

    public enum EntryType
    {
        GLEntry = 1, VendorEntry = 2, CustomerEntry = 3
    }
    /// <summary>
    /// the entry template is based on the document entries
    /// </summary>
    public class EntryTemplate : IComparable<EntryTemplate>
    {
        public readonly static String POSTING_DATE = "POSTING_DATE";

        public readonly static String AMOUNT = "AMOUNT";

        public readonly static String TEXT = "TEXT";

        public readonly static String XML_ID = "id";

        public readonly static String XML_TYPE = "type";

        public readonly static String XML_NAME = "name";

        public readonly static String XML_VALUE = "value";

        public readonly static String XML_VENDOR = "VENDOR";

        public readonly static String XML_CUSTOMER = "CUSTOMER";

        public readonly static String XML_GL = "GL";

        public readonly static String XML_FIELD = "field";


        private readonly CoreDriver _coreDriver;
        private readonly MasterDataManagement _mdMgmt;

        private readonly Dictionary<String, Object> _defaultValues;

        private readonly int _id;
        public int Identity { get { return _id; } }

        private readonly EntryType _entryType;
        public EntryType EntryT { get { return _entryType; } }
        /// <summary>
        /// get name of template
        /// </summary>
        /// <returns></returns>
        private readonly String _name;
        public String TempName { get { return _name; } }

        /// <summary>
        /// Construcotr
        /// </summary>
        /// <param name="coreDriver"></param>
        /// <param name="entryType"></param>
        /// <param name="id"></param>
        /// <param name="name"></param>
        internal EntryTemplate(CoreDriver coreDriver, MasterDataManagement mdMgmt
            , EntryType entryType, int id, String name)
        {
            _coreDriver = coreDriver;
            _mdMgmt = mdMgmt;
            _defaultValues = new Dictionary<String, Object>();
            _entryType = entryType;
            _id = id;
            _name = name;
        }

        /// <summary>
        /// name of field, cannot set default value on date
        /// </summary>
        /// <param name="name"></param>
        /// <param name="value"></param>
        /// <exception cref="NotInValueRangeException">The value enter in is not in supported value range</exception>
        /// <exception cref="NoFieldNameException">No such field name</exception>
        public void AddDefaultValue(String name, Object value)
        {
            if (value == null)
            {
                return;
            }

            if (name.Equals(AMOUNT))
            {
                CurrencyAmount amount = value as CurrencyAmount;
                if (value != null)
                {
                    if (amount.IsNegative())
                    {
                        throw new NotInValueRangeException(name, value);
                    }

                    _defaultValues.Add(name, value);
                    return;
                }

                throw new NotInValueRangeException(name, value);
            }
            else if (name.Equals(TEXT))
            {
                if (value is String)
                {
                    _defaultValues.Add(name, value);
                    return;
                }

                throw new NotInValueRangeException(name, value);
            }

            switch (_entryType)
            {
                case EntryType.GLEntry:
                    checkValueGLEntry(name, value);
                    _defaultValues.Add(name, value);
                    break;
                case EntryType.VendorEntry:
                    checkValueVendorEntry(name, value);
                    _defaultValues.Add(name, value);
                    break;
                case EntryType.CustomerEntry:
                    checkValueCustomerEntry(name, value);
                    _defaultValues.Add(name, value);
                    break;
            }
        }

        /// <summary>
        /// get default value
        /// </summary>
        /// <param name="name"></param>
        /// <returns></returns>
        public Object getDefaultValue(String name)
        {
            Object value;
            if (!_defaultValues.TryGetValue(name, out value))
            {
                return null;
            }

            return value;
        }


        /// <summary>
        /// generate document entry with this template
        /// </summary>
        /// <returns></returns>
        /// <exception cref="SystemException"></exception>
        public IDocumentEntry GenerateEntry()
        {
            IDocumentEntry entry;
            switch (_entryType)
            {
                case EntryType.GLEntry:
                    entry = new GLAccountEntry(_coreDriver, _mdMgmt);
                    break;
                case EntryType.VendorEntry:
                    entry = new VendorEntry(_coreDriver, _mdMgmt);
                    break;
                case EntryType.CustomerEntry:
                    entry = new CustomerEntry(_coreDriver, _mdMgmt);
                    break;
                default:
                    return null;
            }
            foreach (var item in _defaultValues)
            {
                try
                {
                    entry.SetValue(item.Key, item.Value);
                }
                catch (NoFieldNameException e)
                {
                    _coreDriver.logDebugInfo(this.GetType(), 72, e.Message,
                            MessageType.ERRO);
                    throw new SystemException(e);
                }
                catch (NotInValueRangeException e)
                {
                    _coreDriver.logDebugInfo(this.GetType(), 72, e.Message,
                            MessageType.ERRO);
                    throw new SystemException(e);
                }
            }
            return entry;
        }

        /// <summary>
        /// check value
        /// </summary>
        /// <param name="name"></param>
        /// <param name="obj"></param>
        /// <exception cref="NoFieldNameException"></exception>
        /// <exception cref="NotInValueRangeException"></exception>
        private void checkValueVendorEntry(String name, Object obj)
        {
            VendorEntry entry = new VendorEntry(_coreDriver, _mdMgmt);
            List<MasterDataBase> datas = entry.GetValueSet(name);
            foreach (MasterDataBase data in datas)
            {
                if (data.Identity.Equals(obj))
                {
                    return;
                }
            }

            throw new NotInValueRangeException(name, obj);
        }

        /// <summary>
        /// check value
        /// </summary>
        /// <param name="name"></param>
        /// <param name="obj"></param>
        /// <exception cref="NoFieldNameException"></exception>
        /// <exception cref="NotInValueRangeException"></exception>
        private void checkValueCustomerEntry(String name, Object obj)
        {
            CustomerEntry entry = new CustomerEntry(_coreDriver, _mdMgmt);
            List<MasterDataBase> datas = entry.GetValueSet(name);
            foreach (MasterDataBase data in datas)
            {
                if (data.Identity.Equals(obj))
                {
                    return;
                }
            }

            throw new NotInValueRangeException(name, obj);
        }

        /// <summary>
        /// check value
        /// </summary>
        /// <param name="name"></param>
        /// <param name="obj"></param>
        /// <exception cref="NoFieldNameException"></exception>
        /// <exception cref="NotInValueRangeException"></exception>
        private void checkValueGLEntry(String name, Object obj)
        {
            GLAccountEntry entry = new GLAccountEntry(_coreDriver, _mdMgmt);
            List<MasterDataBase> datas = entry.GetValueSet(name);
            foreach (MasterDataBase data in datas)
            {
                if (data.Identity.Equals(obj))
                {
                    return;
                }
            }

            throw new NotInValueRangeException(name, obj);
        }

        public int CompareTo(EntryTemplate template)
        {
            return _id - template._id;
        }



        /// <summary>
        /// To XML
        /// </summary>
        /// <returns></returns>
        public XElement ToXML()
        {
            StringBuilder strBuilder = new StringBuilder();


            String typeName;
            switch (_entryType)
            {
                case EntryType.GLEntry:
                    typeName = XML_GL;
                    break;
                case EntryType.VendorEntry:
                    typeName = XML_VENDOR;
                    break;
                case EntryType.CustomerEntry:
                    typeName = XML_CUSTOMER;
                    break;
                default:
                    throw new SystemException(null);
            }
            XElement xelem = new XElement(EntryTemplatesManagement.XML_TEMPLATE);
            xelem.Add(new XAttribute(XML_ID, _id));
            xelem.Add(new XAttribute(XML_TYPE, typeName));
            xelem.Add(new XAttribute(XML_NAME, _name));


            // fields
            foreach (var item in _defaultValues)
            {
                XElement child = new XElement(XML_FIELD);
                child.Add(new XAttribute(XML_NAME, item.Key));
                child.Add(new XAttribute(XML_VALUE, item.Value.ToString()));
            }
            return xelem;
        }

        /// <summary>
        /// parse XML to template
        /// </summary>
        /// <param name="coreDriver"></param>
        /// <param name="elem"></param>
        /// <returns></returns>
        /// <exception cref="TemplateFormatException"></exception>
        public static EntryTemplate Parse(CoreDriver coreDriver, MasterDataManagement mdMgmt
            , XElement elem)
        {
            XAttribute idStr = elem.Attribute(XML_ID);
            XAttribute name = elem.Attribute(XML_NAME);
            XAttribute typeStr = elem.Attribute(XML_TYPE);

            if (name == null)
            {
                coreDriver.logDebugInfo(typeof(EntryTemplate), 228,
                        "No value in template name", MessageType.ERRO);
                throw new TemplateFormatException();
            }

            int id;
            if (int.TryParse(idStr.Value, out id) == false)
            {
                throw new TemplateFormatException();
            }

            EntryType type;
            if (typeStr.Value.Equals(XML_VENDOR))
            {
                type = EntryType.VendorEntry;
            }
            else if (typeStr.Value.Equals(XML_CUSTOMER))
            {
                type = EntryType.CustomerEntry;
            }
            else if (typeStr.Value.Equals(XML_GL))
            {
                type = EntryType.GLEntry;
            }
            else
            {
                coreDriver.logDebugInfo(typeof(EntryTemplate), 252,
                        "template type is not correct: " + typeStr.Value,
                        MessageType.ERRO);
                throw new TemplateFormatException();
            }

            EntryTemplate template = new EntryTemplate(coreDriver, mdMgmt
                    , type, id, name.Value);

            foreach (XElement fieldElem in elem.Elements(XML_FIELD))
            {
                XAttribute fieldName = fieldElem.Attribute(XML_NAME);
                XAttribute fieldValue = fieldElem.Attribute(XML_VALUE);
                if (fieldName.Value.Equals(EntryTemplate.AMOUNT))
                {
                    try
                    {
                        CurrencyAmount amount = CurrencyAmount
                                .Parse(fieldValue.Value);
                        template.AddDefaultValue(fieldName.Value, amount);
                    }
                    catch (CurrencyAmountFormatException e)
                    {
                        coreDriver.logDebugInfo(typeof(EntryTemplate), 287,
                                e.Message, MessageType.ERRO);
                        throw new TemplateFormatException();
                    }
                    catch (NotInValueRangeException e)
                    {
                        coreDriver.logDebugInfo(typeof(EntryTemplate), 287,
                                e.Message, MessageType.ERRO);
                        throw new TemplateFormatException();
                    }
                    catch (NoFieldNameException e)
                    {
                        coreDriver.logDebugInfo(typeof(EntryTemplate), 287,
                                e.Message, MessageType.ERRO);
                        throw new TemplateFormatException();
                    }
                }
                else if (fieldName.Value.Equals(EntryTemplate.TEXT))
                {
                    try
                    {
                        template.AddDefaultValue(fieldName.Value, fieldValue.Value);
                    }
                    catch (NotInValueRangeException e)
                    {
                        coreDriver.logDebugInfo(typeof(EntryTemplate), 287,
                                e.Message, MessageType.ERRO);
                        throw new TemplateFormatException();
                    }
                    catch (NoFieldNameException e)
                    {
                        coreDriver.logDebugInfo(typeof(EntryTemplate), 287,
                                e.Message, MessageType.ERRO);
                        throw new TemplateFormatException();
                    }
                }
                else
                {
                    try
                    {
                        switch (type)
                        {
                            case EntryType.VendorEntry:
                                if (fieldName.Value.Equals(VendorEntry.REC_ACC)
                                        || fieldName
                                                .Value.Equals(VendorEntry.GL_ACCOUNT))
                                {
                                    MasterDataIdentity_GLAccount accountId = new MasterDataIdentity_GLAccount(
                                            fieldValue.Value);
                                    template.AddDefaultValue(fieldName.Value,
                                            accountId);
                                }
                                else
                                {
                                    MasterDataIdentity dataId = new MasterDataIdentity(
                                            fieldValue.Value);
                                    template.AddDefaultValue(fieldName.Value, dataId);
                                }
                                break;
                            case EntryType.CustomerEntry:
                                if (fieldName.Value.Equals(CustomerEntry.REC_ACC)
                                        || fieldName.Value
                                                .Equals(CustomerEntry.GL_ACCOUNT))
                                {
                                    MasterDataIdentity_GLAccount accountId = new MasterDataIdentity_GLAccount(
                                            fieldValue.Value);
                                    template.AddDefaultValue(fieldName.Value,
                                            accountId);
                                }
                                else
                                {
                                    MasterDataIdentity dataId = new MasterDataIdentity(
                                            fieldValue.Value);
                                    template.AddDefaultValue(fieldName.Value, dataId);
                                }
                                break;
                            case EntryType.GLEntry:
                                MasterDataIdentity_GLAccount accId = new MasterDataIdentity_GLAccount(
                                        fieldValue.Value);
                                template.AddDefaultValue(fieldName.Value, accId);
                                break;
                        }
                    }
                    catch (Exception e)
                    {
                        coreDriver.logDebugInfo(typeof(EntryTemplate), 309,
                                e.Message, MessageType.ERRO);
                        throw new TemplateFormatException();
                    }

                }


            }

            return template;
        }
    }

}
