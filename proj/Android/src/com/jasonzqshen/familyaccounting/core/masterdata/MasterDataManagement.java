package com.jasonzqshen.familyaccounting.core.masterdata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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
import com.jasonzqshen.familyaccounting.core.exception.IdentityInvalidChar;
import com.jasonzqshen.familyaccounting.core.exception.IdentityNoData;
import com.jasonzqshen.familyaccounting.core.exception.IdentityTooLong;
import com.jasonzqshen.familyaccounting.core.exception.NoMasterDataFactoryClass;
import com.jasonzqshen.familyaccounting.core.exception.NoRootElem;
import com.jasonzqshen.familyaccounting.core.exception.SystemException;
import com.jasonzqshen.familyaccounting.core.utils.CoreMessage;

/**
 * Master Data Management, which contains all master data factories.
 * 
 * @author I072485
 * 
 */
public class MasterDataManagement {
	public static final String MASTER_DATA_FOLDER = "master_data";

	private final CoreDriver _coreDriver;
	private final Hashtable<MasterDataType, MasterDataFactoryBase> _factoryList;
	private final Hashtable<MasterDataType, Class<?>> _registerFactorys;

	/**
	 * 
	 * @param coreDriver
	 */
	public MasterDataManagement(CoreDriver coreDriver) {
		_coreDriver = coreDriver;
		_factoryList = new Hashtable<MasterDataType, MasterDataFactoryBase>();
		_registerFactorys = new Hashtable<MasterDataType, Class<?>>();
		// initialize the hash table
		registerFactory();

	}

	/**
	 * register master data factory
	 */
	private void registerFactory() {
		_registerFactorys.put(MasterDataType.VENDOR,
				VendorMasterDataFactory.class);
		_registerFactorys.put(MasterDataType.CUSTOMER,
				CustomerMasterDataFactory.class);
		_registerFactorys.put(MasterDataType.BANK_KEY,
				BankKeyMasterDataFactory.class);
		_registerFactorys.put(MasterDataType.BUSINESS_AREA,
				BusinessAreaMasterDataFactory.class);
		_registerFactorys.put(MasterDataType.BANK_ACCOUNT,
				BankAccountMasterDataFactory.class);
		_registerFactorys.put(MasterDataType.GL_ACCOUNT_GROUP,
				GLAccountGroupMasterDataFactory.class);
		_registerFactorys.put(MasterDataType.GL_ACCOUNT,
				GLAccountMasterDataFactory.class);
	}

	/**
	 * parse XML document to master data factory
	 * 
	 * @param constructor
	 * @param type
	 * @param doc
	 * @param messages
	 * @return
	 */
	private MasterDataFactoryBase factoryParser(Constructor<?> constructor,
			MasterDataType type, Document doc, ArrayList<CoreMessage> messages) {
		// get root element
		NodeList nodeList = doc.getChildNodes();
		Element rootElem = null;

		// get root elem
		for (int i = 0; i < nodeList.getLength(); ++i) {
			Node child = nodeList.item(i);
			if (child instanceof Element) {
				Element elem = (Element) child;
				String nodeName = elem.getNodeName();
				if (nodeName.equals(MasterDataUtils.XML_ROOT)) {
					rootElem = elem;
					break;
				}
			}
		}
		// no root element
		if (rootElem == null) {
			messages.add(new CoreMessage(CoreMessage.ERR_FILE_NOT_ROOT_ELEM,
					CoreMessage.MessageType.ERROR, new NoRootElem()));
		}

		MasterDataFactoryBase newFactory = null;
		try {
			newFactory = (MasterDataFactoryBase) constructor
					.newInstance(_coreDriver);
			nodeList = rootElem.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); ++i) {
				Node child = nodeList.item(i);
				if (child instanceof Element) {
					Element elem = (Element) child;
					if (elem.getNodeName().equals(MasterDataUtils.XML_ENTITY)) {
						// parse master data entity
						newFactory.parseMasterData(_coreDriver, elem);
					}
				}
			}
		} catch (Exception e) {
			messages.add(new CoreMessage(e.getMessage(),
					CoreMessage.MessageType.ERROR, e));
			return null;
		}

		return newFactory;
	}

	/**
	 * load the data from file system
	 * 
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws IllegalArgumentException
	 * 
	 * @throws NoMasterDataFactoryClass
	 * @throws SystemException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public void load(ArrayList<CoreMessage> messages)
			throws NoMasterDataFactoryClass, SystemException {
		for (MasterDataType type : MasterDataType.values()) {
			String filePath = generateMasterFilePath(type);

			MasterDataFactoryBase newFactory = null;
			// open file
			File file = new File(filePath);

			// get factory class
			Class<?> factoryClass = _registerFactorys.get(type);
			if (factoryClass == null) {
				throw new NoMasterDataFactoryClass(type);
			}
			// get constructor
			Constructor<?> constructor;
			try {
				constructor = factoryClass.getConstructor(CoreDriver.class);
			} catch (Exception e1) {
				throw new SystemException(e1);
			}

			try {
				DocumentBuilderFactory docFactory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder builder = docFactory.newDocumentBuilder();
				Document doc = builder.parse(file);

				// parse
				newFactory = factoryParser(constructor, type, doc, messages);
			} catch (ParserConfigurationException e) {
				messages.add(new CoreMessage(String.format(
						CoreMessage.ERR_FILE_FORMAT_ERROR, filePath),
						CoreMessage.MessageType.ERROR, e));
			} catch (SAXException e) {
				messages.add(new CoreMessage(String.format(
						CoreMessage.ERR_FILE_FORMAT_ERROR, filePath),
						CoreMessage.MessageType.ERROR, e));
			} catch (IOException e) {
				messages.add(new CoreMessage(String.format(
						CoreMessage.ERR_FILE_NOT_EXISTS, filePath),
						CoreMessage.MessageType.ERROR, e));
			} finally {
				if (newFactory == null) {
					// try to create empty factory
					try {
						newFactory = (MasterDataFactoryBase) constructor
								.newInstance(_coreDriver);
					} catch (Exception e) {
						throw new SystemException(e);
					}
				}
				_factoryList.put(type, newFactory);
			}

		}
	}

	/**
	 * store master data
	 * 
	 * @throws SystemException
	 */
	public void store() throws SystemException {
		for (MasterDataType type : MasterDataType.values()) {
			storeSingle(type);
		}
	}

	/**
	 * store one type master data
	 * 
	 * @param type
	 * @throws SystemException
	 */
	public void storeSingle(MasterDataType type) throws SystemException {
		MasterDataFactoryBase factory = _factoryList.get(type);
		String xdoc = null;
		try {
			xdoc = factory.toXmlDocument();
		} catch (Exception e) {
			throw new SystemException(e);
		}

		File file = new File(generateMasterFilePath(type));
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				throw new SystemException(e);
			}
		}

		FileWriter writer;
		try {
			writer = new FileWriter(file);
			writer.write(xdoc, 0, xdoc.length());
			writer.close();
		} catch (IOException e) {
			throw new SystemException(e);
		}

	}

	/**
	 * get path
	 * 
	 * @param path
	 * @return
	 */
	private String generateMasterFilePath(MasterDataType type) {
		String path = null;
		if (type == MasterDataType.VENDOR) {
			path = VendorMasterData.FILE_NAME;
		} else if (type == MasterDataType.CUSTOMER) {
			path = CustomerMasterData.FILE_NAME;
		} else if (type == MasterDataType.BUSINESS_AREA) {
			path = BusinessAreaMasterData.FILE_NAME;
		} else if (type == MasterDataType.BANK_KEY) {
			path = BankKeyMasterData.FILE_NAME;
		} else if (type == MasterDataType.BANK_ACCOUNT) {
			path = BankAccountMasterData.FILE_NAME;
		} else if (type == MasterDataType.GL_ACCOUNT_GROUP) {
			path = GLAccountGroupMasterData.FILE_NAME;
		} else if (type == MasterDataType.GL_ACCOUNT) {
			path = GLAccountMasterData.FILE_NAME;
		}

		return String.format("%s/%s/%s", _coreDriver.getRootPath(),
				MASTER_DATA_FOLDER, path);
	}

	/**
	 * get master data factory
	 * 
	 * @param type
	 *            master data type
	 * @return master data factory
	 */
	public MasterDataFactoryBase getMasterDataFactory(MasterDataType type) {
		return this._factoryList.get(type);
	}

	/**
	 * get the entity of master data
	 * 
	 * @param idStr
	 *            identity of master data
	 * @param type
	 *            type of master data
	 * @return master data entity
	 * @throws IdentityInvalidChar
	 * @throws IdentityNoData
	 * @throws IdentityTooLong
	 */
	public MasterDataBase getMasterData(char[] idStr, MasterDataType type)
			throws IdentityTooLong, IdentityNoData, IdentityInvalidChar {
		MasterDataFactoryBase factory = this.getMasterDataFactory(type);
		MasterDataIdentity id = new MasterDataIdentity(idStr);

		return factory.getEntity(id);
	}

	/**
	 * 
	 * @param id
	 * @param type
	 * @return
	 * 
	 */
	public MasterDataBase getMasterData(MasterDataIdentity id,
			MasterDataType type) {
		MasterDataFactoryBase factory = this.getMasterDataFactory(type);
		return factory.getEntity(id);
	}
}
