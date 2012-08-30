package com.jasonzqshen.familyaccounting.core.document_entries;

import java.util.Hashtable;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.EntryTypeNotExistException;
import com.jasonzqshen.familyaccounting.core.exception.IdentityInvalidChar;
import com.jasonzqshen.familyaccounting.core.exception.IdentityNoData;
import com.jasonzqshen.familyaccounting.core.exception.IdentityTooLong;
import com.jasonzqshen.familyaccounting.core.exception.NoFieldNameException;
import com.jasonzqshen.familyaccounting.core.exception.NotInValueRangeException;
import com.jasonzqshen.familyaccounting.core.exception.format.CurrencyAmountFormatException;
import com.jasonzqshen.familyaccounting.core.exception.format.TemplateFormatException;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataBase;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;
import com.jasonzqshen.familyaccounting.core.utils.MessageType;
import com.jasonzqshen.familyaccounting.core.utils.StringUtility;
import com.jasonzqshen.familyaccounting.core.utils.XMLTransfer;

/**
 * the entry template is based on the document entries
 * 
 * @author Jason
 * 
 */
public class EntryTemplate implements Comparable<EntryTemplate> {
    public final static String XML_ID = "id";

    public final static String XML_TYPE = "type";

    public final static String XML_NAME = "name";

    public final static String XML_VALUE = "value";

    public final static String XML_VENDOR = "VENDOR";

    public final static String XML_CUSTOMER = "CUSTOMER";

    public final static String XML_GL = "GL";

    public final static String XML_FIELD = "field";

    public final static int GL_ENTRY_TYPE = 1;

    public final static int VENDOR_ENTRY_TYPE = 2;

    public final static int CUSTOMER_ENTRY_TYPE = 3;

    private final CoreDriver _coreDriver;

    private final Hashtable<String, Object> _defaultValues;

    private final int _id;

    private final int _entryType;

    private final String _name;

    EntryTemplate(CoreDriver coreDriver, int entryType, int id, String name)
            throws EntryTypeNotExistException {
        _coreDriver = coreDriver;
        _defaultValues = new Hashtable<String, Object>();
        _entryType = entryType;
        _id = id;
        _name = name;

        if (entryType != GL_ENTRY_TYPE && entryType != VENDOR_ENTRY_TYPE
                && entryType != CUSTOMER_ENTRY_TYPE) {
            throw new EntryTypeNotExistException();
        }
    }

    /**
     * get document number
     * 
     * @return
     */
    public int getIdentity() {
        return _id;
    }

    /**
     * 
     * @param name
     *            name of field, cannot set default value on date
     * @param value
     */
    public void addDefaultValue(String name, Object value)
            throws NotInValueRangeException, NoFieldNameException {
        if (value == null) {
            return;
        }

        if (name.equals(IDocumentEntry.AMOUNT)) {
            if (value instanceof CurrencyAmount) {
                CurrencyAmount amount = (CurrencyAmount) value;
                if (amount.isNegative()) {
                    throw new NotInValueRangeException(name, value);
                }

                _defaultValues.put(name, value);
                return;
            }

            throw new NotInValueRangeException(name, value);
        } else if (name.equals(IDocumentEntry.TEXT)) {
            if (value instanceof String) {
                _defaultValues.put(name, value);
                return;
            }

            throw new NotInValueRangeException(name, value);
        }

        switch (_entryType) {
        case GL_ENTRY_TYPE:
            checkValueGLEntry(name, value);
            _defaultValues.put(name, value);
            break;
        case VENDOR_ENTRY_TYPE:
            checkValueVendorEntry(name, value);
            _defaultValues.put(name, value);
            break;
        case CUSTOMER_ENTRY_TYPE:
            checkValueCustomerEntry(name, value);
            _defaultValues.put(name, value);
            break;
        }
    }

    /**
     * get default value
     * 
     * @param name
     */
    public Object getDefaultValue(String name) {
        if (_defaultValues.containsKey(name)) {
            return _defaultValues.get(name);
        }

        return null;
    }

    /**
     * get entry type
     * 
     * @return
     */
    public int getEntryType() {
        return _entryType;
    }

    /**
     * generate document entry with this template
     * 
     * @return
     */
    public IDocumentEntry generateEntry() {
        IDocumentEntry entry;
        switch (_entryType) {
        case GL_ENTRY_TYPE:
            entry = new GLAccountEntry(_coreDriver);
            break;
        case VENDOR_ENTRY_TYPE:
            entry = new VendorEntry(_coreDriver);
            break;
        case CUSTOMER_ENTRY_TYPE:
            entry = new CustomerEntry(_coreDriver);
            break;
        default:
            return null;
        }
        for (String key : _defaultValues.keySet()) {
            try {
                entry.setValue(key, _defaultValues.get(key));
            } catch (NoFieldNameException e) {
                _coreDriver.logDebugInfo(this.getClass(), 72, e.toString(),
                        MessageType.ERROR);
                throw new SystemException(e);
            } catch (NotInValueRangeException e) {
                _coreDriver.logDebugInfo(this.getClass(), 72, e.toString(),
                        MessageType.ERROR);
                throw new SystemException(e);
            }
        }
        return entry;
    }

    /**
     * check value
     * 
     * @param value
     * @param obj
     * @throws NoFieldNameException
     * @throws NotInValueRangeException
     */
    private void checkValueVendorEntry(String name, Object obj)
            throws NoFieldNameException, NotInValueRangeException {
        VendorEntry entry = new VendorEntry(_coreDriver);
        MasterDataBase[] datas = entry.getValueSet(name);
        for (MasterDataBase data : datas) {
            if (data.getIdentity().equals(obj)) {
                return;
            }
        }

        throw new NotInValueRangeException(name, obj);
    }

    /**
     * check value
     * 
     * @param value
     * @param obj
     * @throws NoFieldNameException
     * @throws NotInValueRangeException
     */
    private void checkValueCustomerEntry(String name, Object obj)
            throws NoFieldNameException, NotInValueRangeException {
        CustomerEntry entry = new CustomerEntry(_coreDriver);
        MasterDataBase[] datas = entry.getValueSet(name);
        for (MasterDataBase data : datas) {
            if (data.getIdentity().equals(obj)) {
                return;
            }
        }

        throw new NotInValueRangeException(name, obj);
    }

    /**
     * check value
     * 
     * @param value
     * @param obj
     * @throws NoFieldNameException
     * @throws NotInValueRangeException
     */
    private void checkValueGLEntry(String name, Object obj)
            throws NoFieldNameException, NotInValueRangeException {
        GLAccountEntry entry = new GLAccountEntry(_coreDriver);
        MasterDataBase[] datas = entry.getValueSet(name);
        for (MasterDataBase data : datas) {
            if (data.getIdentity().equals(obj)) {
                return;
            }
        }

        throw new NotInValueRangeException(name, obj);
    }

    @Override
    public int compareTo(EntryTemplate template) {
        return _id - template._id;
    }

    /**
     * get name of template
     * 
     * @return
     */
    public String getName() {
        return _name;
    }

    /**
     * parse to XML
     * 
     * @return
     */
    public String toXML() {
        StringBuilder strBuilder = new StringBuilder();

        switch (_entryType) {
        case GL_ENTRY_TYPE:
            strBuilder.append(String.format(
                    "%s%s %s=\"%s\" %s=\"%s\" %s=\"%s\" %s",
                    XMLTransfer.BEGIN_TAG_LEFT,
                    EntryTemplatesManagement.XML_TEMPLATE, XML_ID, _id,
                    XML_TYPE, XML_GL, XML_NAME, _name,
                    XMLTransfer.BEGIN_TAG_RIGHT));
            break;
        case VENDOR_ENTRY_TYPE:
            strBuilder.append(String.format(
                    "%s%s %s=\"%s\" %s=\"%s\" %s=\"%s\" %s",
                    XMLTransfer.BEGIN_TAG_LEFT,
                    EntryTemplatesManagement.XML_TEMPLATE, XML_ID, _id,
                    XML_TYPE, XML_VENDOR, XML_NAME, _name,
                    XMLTransfer.BEGIN_TAG_RIGHT));
            break;
        case CUSTOMER_ENTRY_TYPE:
            strBuilder.append(String.format(
                    "%s%s %s=\"%s\" %s=\"%s\" %s=\"%s\" %s",
                    XMLTransfer.BEGIN_TAG_LEFT,
                    EntryTemplatesManagement.XML_TEMPLATE, XML_ID, _id,
                    XML_TYPE, XML_CUSTOMER, XML_NAME, _name,
                    XMLTransfer.BEGIN_TAG_RIGHT));
            break;

        }

        // fields
        for (String key : _defaultValues.keySet()) {
            Object obj = _defaultValues.get(key);

            strBuilder.append(String.format("%s%s %s=\"%s\" %s=\"%s\" %s",
                    XMLTransfer.SINGLE_TAG_LEFT, XML_FIELD, XML_NAME, key,
                    XML_VALUE, obj, XMLTransfer.SINGLE_TAG_RIGHT));
        }

        strBuilder.append(String.format("%s%s %s", XMLTransfer.END_TAG_LEFT,
                EntryTemplatesManagement.XML_TEMPLATE,
                XMLTransfer.END_TAG_RIGHT));
        return strBuilder.toString();
    }

    /**
     * parse XML to entry template
     * 
     * @param coreDriver
     * @param elem
     * @return
     * @throws TemplateFormatException
     */
    public static EntryTemplate parse(CoreDriver coreDriver, Element elem)
            throws TemplateFormatException {
        String idStr = elem.getAttribute(XML_ID);
        String name = elem.getAttribute(XML_NAME);
        String typeStr = elem.getAttribute(XML_TYPE);

        if (StringUtility.isNullOrEmpty(name)) {
            coreDriver.logDebugInfo(EntryTemplate.class, 228,
                    "No value in template name", MessageType.ERROR);
            throw new TemplateFormatException();
        }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            coreDriver.logDebugInfo(EntryTemplate.class, 239,
                    "template id is not correct " + idStr, MessageType.ERROR);
            throw new TemplateFormatException();
        }

        int type;
        if (typeStr.equals(XML_VENDOR)) {
            type = VENDOR_ENTRY_TYPE;
        } else if (typeStr.equals(XML_CUSTOMER)) {
            type = CUSTOMER_ENTRY_TYPE;
        } else if (typeStr.equals(XML_GL)) {
            type = GL_ENTRY_TYPE;
        } else {
            coreDriver.logDebugInfo(EntryTemplate.class, 252,
                    "template type is not correct: " + typeStr,
                    MessageType.ERROR);
            throw new TemplateFormatException();
        }
        EntryTemplate template;
        try {
            template = new EntryTemplate(coreDriver, type, id, name);
        } catch (EntryTypeNotExistException e) {
            coreDriver.logDebugInfo(EntryTemplate.class, 261, e.toString(),
                    MessageType.ERROR);
            throw new SystemException(e);
        }

        NodeList nodeList = elem.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node child = nodeList.item(i);
            if (child instanceof Element) {
                Element fieldElem = (Element) child;
                if (fieldElem.getNodeName().equals(XML_FIELD)) {
                    String fieldName = fieldElem.getAttribute(XML_NAME);
                    String fieldValue = fieldElem.getAttribute(XML_VALUE);
                    if (fieldName.equals(IDocumentEntry.AMOUNT)) {
                        try {
                            CurrencyAmount amount = CurrencyAmount
                                    .parse(fieldValue);
                            template.addDefaultValue(fieldName, amount);
                        } catch (CurrencyAmountFormatException e) {
                            coreDriver.logDebugInfo(EntryTemplate.class, 287,
                                    e.toString(), MessageType.ERROR);
                            throw new TemplateFormatException();
                        } catch (NotInValueRangeException e) {
                            coreDriver.logDebugInfo(EntryTemplate.class, 291,
                                    e.toString(), MessageType.ERROR);
                            throw new TemplateFormatException();
                        } catch (NoFieldNameException e) {
                            coreDriver.logDebugInfo(EntryTemplate.class, 295,
                                    e.toString(), MessageType.ERROR);
                            throw new TemplateFormatException();
                        }
                    } else if (fieldName.equals(IDocumentEntry.TEXT)) {
                        try {
                            template.addDefaultValue(fieldName, fieldValue);
                        } catch (NotInValueRangeException e) {
                            coreDriver.logDebugInfo(EntryTemplate.class, 304,
                                    e.toString(), MessageType.ERROR);
                            throw new TemplateFormatException();
                        } catch (NoFieldNameException e) {
                            coreDriver.logDebugInfo(EntryTemplate.class, 309,
                                    e.toString(), MessageType.ERROR);
                            throw new TemplateFormatException();
                        }
                    } else {
                        try {
                            switch (type) {
                            case VENDOR_ENTRY_TYPE:
                                if (fieldName.equals(VendorEntry.REC_ACC)
                                        || fieldName
                                                .equals(VendorEntry.GL_ACCOUNT)) {
                                    MasterDataIdentity_GLAccount accountId = new MasterDataIdentity_GLAccount(
                                            fieldValue);
                                    template.addDefaultValue(fieldName,
                                            accountId);
                                } else {
                                    MasterDataIdentity dataId = new MasterDataIdentity(
                                            fieldValue);
                                    template.addDefaultValue(fieldName, dataId);
                                }
                                break;
                            case CUSTOMER_ENTRY_TYPE:
                                if (fieldName.equals(CustomerEntry.REC_ACC)
                                        || fieldName
                                                .equals(CustomerEntry.GL_ACCOUNT)) {
                                    MasterDataIdentity_GLAccount accountId = new MasterDataIdentity_GLAccount(
                                            fieldValue);
                                    template.addDefaultValue(fieldName,
                                            accountId);
                                } else {
                                    MasterDataIdentity dataId = new MasterDataIdentity(
                                            fieldValue);
                                    template.addDefaultValue(fieldName, dataId);
                                }
                                break;
                            case GL_ENTRY_TYPE:
                                MasterDataIdentity_GLAccount accountId = new MasterDataIdentity_GLAccount(
                                        fieldValue);
                                template.addDefaultValue(fieldName, accountId);
                                break;
                            }
                        } catch (IdentityTooLong e) {
                            coreDriver.logDebugInfo(EntryTemplate.class, 309,
                                    e.toString(), MessageType.ERROR);
                            throw new TemplateFormatException();
                        } catch (IdentityNoData e) {
                            coreDriver.logDebugInfo(EntryTemplate.class, 309,
                                    e.toString(), MessageType.ERROR);
                            throw new TemplateFormatException();
                        } catch (IdentityInvalidChar e) {
                            coreDriver.logDebugInfo(EntryTemplate.class, 309,
                                    e.toString(), MessageType.ERROR);
                            throw new TemplateFormatException();
                        } catch (NotInValueRangeException e) {
                            coreDriver.logDebugInfo(EntryTemplate.class, 309,
                                    e.toString(), MessageType.ERROR);
                            throw new TemplateFormatException();
                        } catch (NoFieldNameException e) {
                            coreDriver.logDebugInfo(EntryTemplate.class, 309,
                                    e.toString(), MessageType.ERROR);
                            throw new TemplateFormatException();
                        }

                    }
                }
            }
        }

        return template;
    }
}
