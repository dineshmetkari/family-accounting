package com.jasonzqshen.familyaccounting.core.masterdata;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Hashtable;

import org.xmlpull.v1.XmlSerializer;
import android.util.Xml;

import com.jasonzqshen.familyaccounting.core.CoreDriver;

/**
 * Abstract Factory Pattern.
 * 
 * @author I072485
 * 
 */
public abstract class MasterDataFactoryBase {
	protected final CoreDriver _coreDriver;
	protected final Hashtable<MasterDataIdentity, MasterDataBase> _list;

	public MasterDataFactoryBase(CoreDriver coreDriver) {
		_coreDriver = coreDriver;
		_list = new Hashtable<MasterDataIdentity, MasterDataBase>();
	}

	/**
	 * create new master data with id. And add the masterdata into list
	 * 
	 * @param id
	 * @return
	 */
	public abstract MasterDataBase createNewMasterDataBase(MasterDataIdentity id);

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
	 * @throws IOException
	 * @throws IllegalStateException
	 * @throws IllegalArgumentException
	 */
	public String toXml() throws IllegalArgumentException,
			IllegalStateException, IOException {
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		serializer.setOutput(writer);
		serializer.startDocument("UTF-8", true);

		serializer.startTag("", MasterDataUtils.XML_ROOT);
		for (MasterDataBase masterdata : _list.values()) {
			serializer.startTag("", MasterDataUtils.XML_ENTITY);
			masterdata.toXML(serializer);
			serializer.endTag("", MasterDataUtils.XML_ENTITY);
		}

		serializer.endTag("", MasterDataUtils.XML_ROOT);
		serializer.endDocument();

		return writer.toString();
	}
}
