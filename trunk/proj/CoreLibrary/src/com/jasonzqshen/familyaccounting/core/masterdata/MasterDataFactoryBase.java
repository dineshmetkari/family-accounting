package com.jasonzqshen.familyaccounting.core.masterdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

import org.w3c.dom.Element;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityExists;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityNotDefined;
import com.jasonzqshen.familyaccounting.core.exception.ParametersException;
import com.jasonzqshen.familyaccounting.core.exception.format.MasterDataFileFormatException;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.utils.XMLTransfer;

/**
 * Abstract Factory Pattern.
 * 
 * @author I072485
 * 
 */
public abstract class MasterDataFactoryBase {
    protected final CoreDriver _coreDriver;

    protected final MasterDataManagement _management;

    protected final Hashtable<MasterDataIdentity, MasterDataBase> _list;

    boolean _containDirtyData;

    /**
     * constructor
     * 
     * @param parser
     * @param coreDriver
     */
    protected MasterDataFactoryBase(CoreDriver coreDriver,
            MasterDataManagement management) {
        _coreDriver = coreDriver;
        _management = management;
        _list = new Hashtable<MasterDataIdentity, MasterDataBase>();
        _containDirtyData = false;
    }

    /**
     * create new master data with id. And add the master data into list
     * 
     * @param id
     * @return
     * @throws ParametersException
     * @throws MasterDataIdentityExists
     * @throws MasterDataIdentityNotDefined
     */
    public abstract MasterDataBase createNewMasterDataBase(
            MasterDataIdentity identity, String descp, Object... objects)
            throws ParametersException, MasterDataIdentityExists,
            MasterDataIdentityNotDefined, SystemException;

    /**
     * parse master data
     * 
     * @param coreDriver
     * @param elem
     * @return
     * @throws MasterDataFileFormatException
     */
    public abstract MasterDataBase parseMasterData(CoreDriver coreDriver,
            Element elem) throws MasterDataFileFormatException;

    /**
     * remove the master data entity from list
     * 
     * @param id
     * @return
     */
    public void removeNewMasterDataBase(MasterDataIdentity id) {
        _list.remove(id);
    }

    /**
     * parse memory to XML
     * 
     * @return
     */
    public String toXmlDocument() {

        if (_list.size() == 0) {
            return String.format("%s%s %s", XMLTransfer.SINGLE_TAG_LEFT,
                    MasterDataUtils.XML_ROOT, XMLTransfer.SINGLE_TAG_RIGHT);
        }

        StringBuilder ret = new StringBuilder();
        // begin tag
        ret.append(String.format("%s%s %s", XMLTransfer.BEGIN_TAG_LEFT,
                MasterDataUtils.XML_ROOT, XMLTransfer.BEGIN_TAG_RIGHT));

        ArrayList<MasterDataBase> list = new ArrayList<MasterDataBase>(
                _list.values());
        Collections.sort(list);
        for (MasterDataBase masterData : list) {
            ret.append(String.format("\t%s%s ", XMLTransfer.SINGLE_TAG_LEFT,
                    MasterDataUtils.XML_ENTITY));

            String str = masterData.toXML();
            ret.append(str);

            ret.append(XMLTransfer.SINGLE_TAG_RIGHT);
        }

        // end tag
        ret.append(String.format("%s%s %s", XMLTransfer.END_TAG_LEFT,
                MasterDataUtils.XML_ROOT, XMLTransfer.END_TAG_RIGHT));

        return ret.toString();
    }

    /**
     * get the count of master data
     * 
     * @return
     */
    public int getMasterDataCount() {
        return _list.size();
    }

    /**
     * get master data entities
     * 
     * @return
     */
    public MasterDataBase[] getAllEntities() {
        ArrayList<MasterDataBase> collection = new ArrayList<MasterDataBase>();

        for (MasterDataBase data : _list.values()) {
            collection.add(data);
        }
        Collections.sort(collection);

        MasterDataBase[] ret = new MasterDataBase[getMasterDataCount()];
        int index = 0;
        for (MasterDataBase data : collection) {
            ret[index++] = data;
        }

        return ret;
    }

    /**
     * get master data entity according identity
     * 
     * @param id
     *            identity
     * @return master data entity
     */
    public MasterDataBase getEntity(MasterDataIdentity id) {
        if (!contains(id)) {
            return null;
        }
        return _list.get(id);
    }

    /**
     * contains value with master data identity
     * 
     * @param id
     * @return
     */
    public boolean contains(MasterDataIdentity id) {
        return _list.containsKey(id);
    }

}
