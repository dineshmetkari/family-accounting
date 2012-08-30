package com.jasonzqshen.familyaccounting.core.document_entries;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.ManagementBase;
import com.jasonzqshen.familyaccounting.core.exception.EntryTypeNotExistException;
import com.jasonzqshen.familyaccounting.core.exception.NoFieldNameException;
import com.jasonzqshen.familyaccounting.core.exception.NotInValueRangeException;
import com.jasonzqshen.familyaccounting.core.exception.format.DuplicatedTemplateException;
import com.jasonzqshen.familyaccounting.core.exception.format.TemplateFormatException;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;
import com.jasonzqshen.familyaccounting.core.utils.MessageType;
import com.jasonzqshen.familyaccounting.core.utils.XMLTransfer;

public class EntryTemplatesManagement extends ManagementBase {
    public static final String FILE_NAME = "templates.xml";

    public static final String XML_ROOT = "root";

    public static final String XML_TEMPLATE = "template";

    private final Hashtable<Integer, EntryTemplate> _list;

    private boolean _isInitialize;

    public EntryTemplatesManagement(CoreDriver coreDriver) {
        super(coreDriver);
        _list = new Hashtable<Integer, EntryTemplate>();
        _isInitialize = false;
    }

    /**
     * initialize
     * 
     * @throws TemplateFormatException
     */
    public void initialize() throws TemplateFormatException {
        if (_isInitialize) {
            return;
        }

        // check whether core driver is initialized.
        if (_coreDriver.isInitialized() == false) {
            _coreDriver.logDebugInfo(this.getClass(), 18,
                    "CoreDriver should be initialized before Entry Templets.",
                    MessageType.ERROR);
            return;
        }

        String filePath = String.format("%s/%s", _coreDriver.getRootPath(),
                FILE_NAME);
        File file = new File(filePath);
        if (!file.exists()) {
            _isInitialize = true;
            return;
        }

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder builder = docFactory.newDocumentBuilder();
            Document doc = builder.parse(file);

            NodeList nodeList = doc.getChildNodes();
            Element rootElem = null;

            // get root element
            for (int i = 0; i < nodeList.getLength(); ++i) {
                Node child = nodeList.item(i);
                if (child instanceof Element) {
                    Element elem = (Element) child;
                    String nodeName = elem.getNodeName();
                    if (nodeName.equals(XML_ROOT)) {
                        rootElem = elem;
                        break;
                    }
                }
            }
            // no root element
            if (rootElem == null) {
                _coreDriver.logDebugInfo(this.getClass(), 112,
                        "No root element", MessageType.ERROR);
                throw new TemplateFormatException();
            }

            nodeList = rootElem.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); ++i) {
                Node child = nodeList.item(i);
                if (child instanceof Element) {
                    Element elem = (Element) child;
                    if (elem.getNodeName().equals(XML_TEMPLATE)) {
                        // parse master data entity
                        EntryTemplate template = EntryTemplate.parse(
                                _coreDriver, elem);
                        this.addTemplate(template);
                    }
                }
            }

        } catch (ParserConfigurationException e) {
            _coreDriver.logDebugInfo(this.getClass(), 61, e.toString(),
                    MessageType.ERROR);
            throw new SystemException(e);
        } catch (SAXException e) {
            _coreDriver.logDebugInfo(this.getClass(), 67, String.format(
                    "Template file cannot be parsed to XML.", filePath),
                    MessageType.ERROR);
            throw new TemplateFormatException();
        } catch (IOException e) {
            _coreDriver.logDebugInfo(this.getClass(), 72,
                    String.format(e.toString(), filePath), MessageType.ERROR);
            throw new SystemException(e);
        } catch (DuplicatedTemplateException e) {
            _coreDriver.logDebugInfo(this.getClass(), 72,
                    String.format(e.toString(), filePath), MessageType.ERROR);
            throw new TemplateFormatException();
        }

        _isInitialize = true;
    }

    /**
     * save document entry as a template
     * 
     * @param entry
     */
    public EntryTemplate saveAsTemplate(IDocumentEntry entry, String name) {
        if (_isInitialize == false) {
            return null;
        }

        EntryTemplate template;

        // get the id
        int id = _list.size() + 1;

        try {
            CurrencyAmount amount = (CurrencyAmount) entry
                    .getValue(IDocumentEntry.AMOUNT);
            String text = (String) entry.getValue(IDocumentEntry.TEXT);
            if (entry instanceof VendorEntry) {
                template = new EntryTemplate(_coreDriver,
                        EntryTemplate.VENDOR_ENTRY_TYPE, id, name);
                template.addDefaultValue(VendorEntry.AMOUNT, amount);
                template.addDefaultValue(VendorEntry.TEXT, text);

                Object vendor = entry.getValue(VendorEntry.VENDOR);
                if (vendor != null) {
                    template.addDefaultValue(VendorEntry.VENDOR, vendor);
                }
                Object recAcc = entry.getValue(VendorEntry.REC_ACC);
                if (recAcc != null) {
                    template.addDefaultValue(VendorEntry.REC_ACC, recAcc);
                }
                Object costAcc = entry.getValue(VendorEntry.GL_ACCOUNT);
                if (costAcc != null) {
                    template.addDefaultValue(VendorEntry.GL_ACCOUNT, costAcc);
                }
                Object businessArea = entry.getValue(VendorEntry.BUSINESS_AREA);
                if (businessArea != null) {
                    template.addDefaultValue(VendorEntry.BUSINESS_AREA,
                            businessArea);
                }

                this.addTemplate(template);
            } else if (entry instanceof GLAccountEntry) {
                template = new EntryTemplate(_coreDriver,
                        EntryTemplate.GL_ENTRY_TYPE, id, name);

                template.addDefaultValue(GLAccountEntry.AMOUNT, amount);
                template.addDefaultValue(GLAccountEntry.TEXT, text);

                Object recAcc = entry.getValue(GLAccountEntry.SRC_ACCOUNT);
                if (recAcc != null) {
                    template.addDefaultValue(GLAccountEntry.SRC_ACCOUNT, recAcc);
                }
                Object costAcc = entry.getValue(GLAccountEntry.DST_ACCOUNT);
                if (costAcc != null) {
                    template.addDefaultValue(GLAccountEntry.DST_ACCOUNT,
                            costAcc);
                }

                this.addTemplate(template);
            } else if (entry instanceof CustomerEntry) {
                template = new EntryTemplate(_coreDriver,
                        EntryTemplate.CUSTOMER_ENTRY_TYPE, id, name);

                template.addDefaultValue(CustomerEntry.AMOUNT, amount);
                template.addDefaultValue(CustomerEntry.TEXT, text);

                Object customer = entry.getValue(CustomerEntry.CUSTOMER);
                if (customer != null) {
                    template.addDefaultValue(CustomerEntry.CUSTOMER, customer);
                }
                Object recAcc = entry.getValue(CustomerEntry.REC_ACC);
                if (recAcc != null) {
                    template.addDefaultValue(CustomerEntry.REC_ACC, recAcc);
                }
                Object costAcc = entry.getValue(CustomerEntry.GL_ACCOUNT);
                if (costAcc != null) {
                    template.addDefaultValue(CustomerEntry.GL_ACCOUNT, costAcc);
                }

                this.addTemplate(template);
            } else {
                return null;
            }
        } catch (NoFieldNameException e) {
            _coreDriver.logDebugInfo(this.getClass(), 72, e.toString(),
                    MessageType.ERROR);
            throw new SystemException(e);
        } catch (EntryTypeNotExistException e) {
            _coreDriver.logDebugInfo(this.getClass(), 72, e.toString(),
                    MessageType.ERROR);
            throw new SystemException(e);
        } catch (DuplicatedTemplateException e) {
            _coreDriver.logDebugInfo(this.getClass(), 72, e.toString(),
                    MessageType.ERROR);
            throw new SystemException(e);
        } catch (NotInValueRangeException e) {
            _coreDriver.logDebugInfo(this.getClass(), 72, e.toString(),
                    MessageType.ERROR);
            throw new SystemException(e);
        }

        // save
        _coreDriver.getMasterDataManagement().store();
        String filePath = String.format("%s/%s", _coreDriver.getRootPath(),
                FILE_NAME);
        File file = new File(filePath);
        FileWriter writer;
        try {
            String xdoc = this.toXMLDoc();
            writer = new FileWriter(file);
            writer.write(xdoc, 0, xdoc.length());
            writer.close();
        } catch (IOException e) {
            _coreDriver.logDebugInfo(this.getClass(), 335, e.toString(),
                    MessageType.ERROR);
            throw new SystemException(e);
        }

        return template;
    }

    /**
     * add template
     * 
     * @param entry
     * @throws DuplicatedTemplateException
     */
    private void addTemplate(EntryTemplate entry)
            throws DuplicatedTemplateException {

        if (_list.containsKey(entry.getIdentity())) {
            throw new DuplicatedTemplateException(entry.getIdentity());
        }
        _coreDriver.logDebugInfo(this.getClass(), 72,
                String.format("Template: %s add", entry.getName()),
                MessageType.INFO);
        _list.put(entry.getIdentity(), entry);
    }

    /**
     * get templates
     * 
     * @return
     */
    public ArrayList<EntryTemplate> getEntryTemplates() {
        ArrayList<EntryTemplate> ret = new ArrayList<EntryTemplate>(
                _list.values());
        Collections.sort(ret);
        return ret;
    }

    /**
     * get entry template
     * 
     * @return
     */
    public EntryTemplate getEntryTemplate(int tempId) {
        if (_list.containsKey(tempId)) {
            return _list.get(tempId);
        }

        return null;
    }

    /**
     * to xml document
     * 
     * @return
     */
    public String toXMLDoc() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(String.format("%s%s %s", XMLTransfer.BEGIN_TAG_LEFT,
                XML_ROOT, XMLTransfer.BEGIN_TAG_RIGHT));

        ArrayList<EntryTemplate> templates = this.getEntryTemplates();
        for (EntryTemplate template : templates) {
            strBuilder.append(template.toXML());
        }

        strBuilder.append(String.format("%s%s %s", XMLTransfer.END_TAG_LEFT,
                XML_ROOT, XMLTransfer.END_TAG_RIGHT));
        return strBuilder.toString();
    }

    @Override
    public void clear() {
        _isInitialize = false;
        _list.clear();
    }

    @Override
    public void establishFiles() {
    }
}
