using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Linq;
using Windows.Storage;
using WinStore_FamilyAccountingCore.Exceptions;
using WinStore_FamilyAccountingCore.Exceptions.FormatExceptions;
using WinStore_FamilyAccountingCore.MasterData;
using WinStore_FamilyAccountingCore.Utilities;

namespace WinStore_FamilyAccountingCore.DocumentEntries
{
    public class EntryTemplatesManagement : AbstractManagement
    {
        public static readonly string FILE_NAME = "templates.xml";

        public static readonly String XML_ROOT = "root";

        public static readonly String XML_TEMPLATE = "template";

        private readonly Dictionary<int, EntryTemplate> _list;

        private readonly MasterDataManagement _mdMgmt;

        /// <summary>
        /// constructor
        /// </summary>
        /// <param name="coreDriver"></param>
        /// <param name="mdMgmt"></param>
        public EntryTemplatesManagement(CoreDriver coreDriver, MasterDataManagement mdMgmt)
            : base(coreDriver)
        {
            _list = new Dictionary<int, EntryTemplate>();
            _mdMgmt = mdMgmt;
        }

        /// <summary>
        /// initialize
        /// </summary>
        /// <returns></returns>
        public override async Task InitializeAsync()
        {
            String filePath = String.Format("{0}/{1}", _coreDriver.RootFolder.Path,
                    FILE_NAME);
            StorageFile file;
            try
            {
                file = await _coreDriver.RootFolder.GetFileAsync(FILE_NAME);
            }
            catch (FileNotFoundException)
            {
                return;
            }

            try
            {
                String str = await FileIO.ReadTextAsync(file);
                XDocument xdoc = XDocument.Parse(str);
                XElement rootElem = xdoc.Element(XML_ROOT);

                // no root element
                if (rootElem == null)
                {
                    _coreDriver.logDebugInfo(this.GetType(), 112,
                            "No root element", MessageType.ERRO);
                    throw new TemplateFormatException();
                }

                foreach (XElement elem in rootElem.Elements(XML_TEMPLATE))
                {
                    // parse master data entity
                    EntryTemplate template = EntryTemplate.Parse(
                            _coreDriver, _mdMgmt, elem);
                    this.addTemplate(template);
                }

            }
            catch (DuplicatedTemplateException e)
            {
                _coreDriver.logDebugInfo(this.GetType(), 72,
                        String.Format(e.Message, filePath), MessageType.ERRO);
                throw new TemplateFormatException();
            }
        }

        /// <summary>
        /// save document as template
        /// </summary>
        /// <param name="entry"></param>
        /// <param name="name">template name</param>
        /// <returns></returns>
        public async Task<EntryTemplate> SaveAsTemplate(IDocumentEntry entry, String name)
        {
            EntryTemplate template;

            // get the id
            int id = _list.Count + 1;

            try
            {
                CurrencyAmount amount = (CurrencyAmount)entry
                        .GetValue(EntryTemplate.AMOUNT);
                String text = (String)entry.GetValue(EntryTemplate.TEXT);
                if (entry is VendorEntry)
                {
                    template = new EntryTemplate(_coreDriver, _mdMgmt,
                            EntryType.VendorEntry, id, name);
                    template.AddDefaultValue(EntryTemplate.AMOUNT, amount);
                    template.AddDefaultValue(EntryTemplate.TEXT, text);

                    Object vendor = entry.GetValue(VendorEntry.VENDOR);
                    if (vendor != null)
                    {
                        template.AddDefaultValue(VendorEntry.VENDOR, vendor);
                    }
                    Object recAcc = entry.GetValue(VendorEntry.REC_ACC);
                    if (recAcc != null)
                    {
                        template.AddDefaultValue(VendorEntry.REC_ACC, recAcc);
                    }
                    Object costAcc = entry.GetValue(VendorEntry.GL_ACCOUNT);
                    if (costAcc != null)
                    {
                        template.AddDefaultValue(VendorEntry.GL_ACCOUNT, costAcc);
                    }
                    Object businessArea = entry.GetValue(VendorEntry.BUSINESS_AREA);
                    if (businessArea != null)
                    {
                        template.AddDefaultValue(VendorEntry.BUSINESS_AREA,
                                businessArea);
                    }

                    this.addTemplate(template);
                }
                else if (entry is GLAccountEntry)
                {
                    template = new EntryTemplate(_coreDriver, _mdMgmt,
                            EntryType.GLEntry, id, name);

                    template.AddDefaultValue(EntryTemplate.AMOUNT, amount);
                    template.AddDefaultValue(EntryTemplate.TEXT, text);

                    Object recAcc = entry.GetValue(GLAccountEntry.SRC_ACCOUNT);
                    if (recAcc != null)
                    {
                        template.AddDefaultValue(GLAccountEntry.SRC_ACCOUNT, recAcc);
                    }
                    Object costAcc = entry.GetValue(GLAccountEntry.DST_ACCOUNT);
                    if (costAcc != null)
                    {
                        template.AddDefaultValue(GLAccountEntry.DST_ACCOUNT,
                                costAcc);
                    }

                    this.addTemplate(template);
                }
                else if (entry is CustomerEntry)
                {
                    template = new EntryTemplate(_coreDriver, _mdMgmt,
                            EntryType.CustomerEntry, id, name);

                    template.AddDefaultValue(EntryTemplate.AMOUNT, amount);
                    template.AddDefaultValue(EntryTemplate.TEXT, text);

                    Object customer = entry.GetValue(CustomerEntry.CUSTOMER);
                    if (customer != null)
                    {
                        template.AddDefaultValue(CustomerEntry.CUSTOMER, customer);
                    }
                    Object recAcc = entry.GetValue(CustomerEntry.REC_ACC);
                    if (recAcc != null)
                    {
                        template.AddDefaultValue(CustomerEntry.REC_ACC, recAcc);
                    }
                    Object costAcc = entry.GetValue(CustomerEntry.GL_ACCOUNT);
                    if (costAcc != null)
                    {
                        template.AddDefaultValue(CustomerEntry.GL_ACCOUNT, costAcc);
                    }

                    this.addTemplate(template);
                }
                else
                {
                    return null;
                }
            }
            catch (Exception e)
            {
                _coreDriver.logDebugInfo(this.GetType(), 72, e.Message,
                        MessageType.ERRO);
                throw new SystemException(e);
            }

            // save
            await _coreDriver.MdMgmt.StoreAsync();
            String filePath = String.Format("%s/%s", _coreDriver.RootFolder.Path,
                    FILE_NAME);
            StorageFile file = await _coreDriver.RootFolder.CreateFileAsync(
                FILE_NAME, CreationCollisionOption.OpenIfExists);

            XDocument xdoc = this.ToXMLDoc();

            await FileIO.WriteTextAsync(file, xdoc.ToString());
            return template;
        }


        /// <summary>
        /// add template
        /// </summary>
        /// <param name="entry"></param>
        /// <exception cref="DuplicatedTemplateException"></exception>
        private void addTemplate(EntryTemplate entry)
        {
            if (_list.ContainsKey(entry.Identity))
            {
                throw new DuplicatedTemplateException(entry.Identity);
            }
            _coreDriver.logDebugInfo(this.GetType(), 72,
                    String.Format("Template: %s add", entry.TempName),
                    MessageType.INFO);
            _list.Add(entry.Identity, entry);
        }

        /// <summary>
        /// templates
        /// </summary>
        /// <returns></returns>
        public List<EntryTemplate> EntryTemplates
        {
            get
            {
                List<EntryTemplate> ret = new List<EntryTemplate>(
                        _list.Values);
                ret.Sort();
                return ret;
            }
        }

        /// <summary>
        /// get template
        /// </summary>
        /// <param name="tempId"></param>
        /// <returns></returns>
        public EntryTemplate GetEntryTemplate(int tempId)
        {
            EntryTemplate template;
            if (_list.TryGetValue(tempId, out template))
            {
                return template;
            }
            return null;
        }

        /// <summary>
        /// to XML document
        /// </summary>
        /// <returns></returns>
        public XDocument ToXMLDoc()
        {
            XElement rootElem = new XElement(XML_ROOT);
            XDocument xdoc = new XDocument(rootElem);
            StringBuilder strBuilder = new StringBuilder();

            List<EntryTemplate> templates = this.EntryTemplates;
            foreach (EntryTemplate template in templates)
            {
                rootElem.Add(template.ToXML());
            }
            return xdoc;
        }

        /// <summary>
        /// clear
        /// </summary>
        public override void Clear()
        {
            _list.Clear();
        }

        /// <summary>
        /// establish file 
        /// </summary>
        /// <returns></returns>
        public override Task EstablishFilesAsync()
        {
            // empty
            throw new NotImplementedException();
        }

        public override bool NeedInit
        {
            get { return true; }
        }

        public override bool NeedEstablishFile
        {
            get { return false; }
        }
    }
}
