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
import com.jasonzqshen.familyaccounting.core.exception.MasterDataFileFormatException;
import com.jasonzqshen.familyaccounting.core.exception.runtime.NoMasterDataFactoryClass;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.utils.CoreMessage;
import com.jasonzqshen.familyaccounting.core.utils.MessageType;

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
	 * @throws MasterDataFileFormatException
	 */
	private MasterDataFactoryBase factoryParser(Constructor<?> constructor,
			MasterDataType type, Document doc, ArrayList<CoreMessage> messages)
			throws MasterDataFileFormatException {
		_coreDriver.logDebugInfo(this.getClass(), 88,
				String.format("Parsing XML %s...", type), MessageType.INFO);

		// get root element
		NodeList nodeList = doc.getChildNodes();
		Element rootElem = null;

		// get root element
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
			_coreDriver.logDebugInfo(this.getClass(), 112,
					String.format("No root element", type), MessageType.ERROR);
			throw new MasterDataFileFormatException(type);
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
		} catch (InstantiationException e) {
			_coreDriver.logDebugInfo(this.getClass(), 133, e.toString(),
					MessageType.ERROR);
			throw new SystemException(e);
		} catch (IllegalArgumentException e) {
			_coreDriver.logDebugInfo(this.getClass(), 137, e.toString(),
					MessageType.ERROR);
			throw new SystemException(e);
		} catch (IllegalAccessException e) {
			_coreDriver.logDebugInfo(this.getClass(), 140, e.toString(),
					MessageType.ERROR);
			throw new SystemException(e);
		} catch (InvocationTargetException e) {
			_coreDriver.logDebugInfo(this.getClass(), 145, e.toString(),
					MessageType.ERROR);
			throw new SystemException(e);
		} catch (MasterDataFileFormatException e) {
			_coreDriver.logDebugInfo(this.getClass(), 112,
					String.format("Mandatory fields contains no data", type),
					MessageType.ERROR);
			throw new MasterDataFileFormatException(type);
		}

		_coreDriver.logDebugInfo(this.getClass(), 88,
				String.format("Parsing XML %s complete.", type),
				MessageType.INFO);

		return newFactory;
	}

	/**
	 * load the data from file system
	 * 
	 * @throws NoMasterDataFactoryClass
	 *             if the exception raised from the function, it is the bug.
	 * @throws SystemException
	 *             if the exception raised from the function, it is the bug.
	 * 
	 */
	public void load(ArrayList<CoreMessage> messages) {
		_coreDriver.logDebugInfo(this.getClass(), 141,
				"Master data loading...", MessageType.INFO);

		for (MasterDataType type : MasterDataType.values()) {
			_coreDriver.logDebugInfo(this.getClass(), 144,
					String.format("Loading %s...", type), MessageType.INFO);

			String filePath = generateMasterFilePath(type);
			_coreDriver.logDebugInfo(this.getClass(), 148, String.format(
					"Master data file %s for master data %s.", filePath, type),
					MessageType.INFO);

			MasterDataFactoryBase newFactory = null;

			// get factory class
			Class<?> factoryClass = _registerFactorys.get(type);
			if (factoryClass == null) {
				_coreDriver
						.logDebugInfo(
								this.getClass(),
								178,
								String.format(
										"Master data class is not registered for master data %s",
										type), MessageType.WARNING);
				throw new NoMasterDataFactoryClass(type);
			}
			// get constructor
			Constructor<?> constructor = null;
			try {
				constructor = factoryClass.getConstructor(CoreDriver.class);
			} catch (NoSuchMethodException e1) {
				_coreDriver.logDebugInfo(this.getClass(), 191, e1.toString(),
						MessageType.ERROR);
				throw new SystemException(e1);
			} catch (SecurityException e1) {
				_coreDriver.logDebugInfo(this.getClass(), 193, e1.toString(),
						MessageType.ERROR);
				throw new SystemException(e1);
			}

			// open file
			File file = new File(filePath);
			if (!file.exists()) {
				_coreDriver
						.logDebugInfo(
								this.getClass(),
								165,
								String.format(
										"Master data file %s for master data %s does not exist.",
										filePath, type), MessageType.ERROR);
				newFactory = createFactory(constructor);
				_factoryList.put(type, newFactory);
				// next master data
				continue;
			}

			try {
				DocumentBuilderFactory docFactory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder builder = docFactory.newDocumentBuilder();
				Document doc = builder.parse(file);

				// parse
				newFactory = factoryParser(constructor, type, doc, messages);

				_coreDriver.logDebugInfo(this.getClass(), 208,
						String.format("Loading %s completed.", type),
						MessageType.INFO);
			} catch (ParserConfigurationException e) {
				_coreDriver.logDebugInfo(this.getClass(), 207, e.toString(),
						MessageType.ERROR);
				throw new SystemException(e);
			} catch (SAXException e) {
				messages.add(new CoreMessage(String.format(
						CoreMessage.ERR_FILE_FORMAT_ERROR, filePath),
						MessageType.ERROR, e));
				_coreDriver.logDebugInfo(this.getClass(), 216, String.format(
						"Master data file %s contains format error", filePath),
						MessageType.ERROR);
			} catch (IOException e) {
				_coreDriver.logDebugInfo(this.getClass(), 220,
						String.format(e.toString(), filePath),
						MessageType.ERROR);
				throw new SystemException(e);
			} catch (MasterDataFileFormatException e) {
				messages.add(new CoreMessage(String.format(
						CoreMessage.ERR_FILE_FORMAT_ERROR, filePath),
						MessageType.ERROR, e));
				_coreDriver.logDebugInfo(this.getClass(), 262, String.format(
						"Master data file %s contains format error", filePath),
						MessageType.ERROR);
			} finally {
				if (newFactory == null) {
					_coreDriver
							.logDebugInfo(
									this.getClass(),
									230,
									String.format(
											"The loading of Master data file %s contains format error. Create an empty master data factory",
											filePath), MessageType.WARNING);
					// try to create empty factory
					newFactory = createFactory(constructor);

				}
				_factoryList.put(type, newFactory);
			}

		}

		_coreDriver.logDebugInfo(this.getClass(), 282,
				"Master data loading completed.", MessageType.INFO);
	}

	/**
	 * create instance via the contructor
	 * 
	 * @param constructor
	 * @return
	 * @throws SystemException
	 *             system bug
	 */
	private MasterDataFactoryBase createFactory(Constructor<?> constructor) {
		try {
			MasterDataFactoryBase newFactory = (MasterDataFactoryBase) constructor
					.newInstance(_coreDriver);

			return newFactory;
		} catch (IllegalArgumentException e) {
			_coreDriver.logDebugInfo(this.getClass(), 240, e.toString(),
					MessageType.ERROR);
			throw new SystemException(e);
		} catch (InstantiationException e) {
			_coreDriver.logDebugInfo(this.getClass(), 244, e.toString(),
					MessageType.ERROR);
			throw new SystemException(e);
		} catch (IllegalAccessException e) {
			_coreDriver.logDebugInfo(this.getClass(), 248, e.toString(),
					MessageType.ERROR);
			throw new SystemException(e);
		} catch (InvocationTargetException e) {
			_coreDriver.logDebugInfo(this.getClass(), 251, e.toString(),
					MessageType.ERROR);
			throw new SystemException(e);
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
	public void storeSingle(MasterDataType type) {
		_coreDriver.logDebugInfo(this.getClass(), 335,
				String.format("Starting to store master data %s...", type),
				MessageType.INFO);

		MasterDataFactoryBase factory = _factoryList.get(type);

		if (factory._containDirtyData == false) {
			_coreDriver
					.logDebugInfo(
							this.getClass(),
							335,
							"No dirty data in the memory. No need to save the data to disk.",
							MessageType.INFO);
			return;
		}
		// get XML document
		String xdoc = factory.toXmlDocument();

		File file = new File(generateMasterFilePath(type));
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				_coreDriver.logDebugInfo(this.getClass(), 335, e.toString(),
						MessageType.ERROR);
				throw new SystemException(e);
			}
		}

		FileWriter writer;
		try {
			writer = new FileWriter(file);
			writer.write(xdoc, 0, xdoc.length());
			writer.close();
		} catch (IOException e) {
			_coreDriver.logDebugInfo(this.getClass(), 335, e.toString(),
					MessageType.ERROR);
			throw new SystemException(e);
		}

		factory._containDirtyData = false;
		_coreDriver.logDebugInfo(this.getClass(), 335,
				String.format("Store master data %s complete.", type),
				MessageType.INFO);
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
	 * get master data based on identity and data type
	 * 
	 * @param id
	 *            identity
	 * @param type
	 *            master data type
	 * @return
	 * 
	 */
	public MasterDataBase getMasterData(MasterDataIdentity id,
			MasterDataType type) {
		MasterDataFactoryBase factory = this.getMasterDataFactory(type);
		return factory.getEntity(id);
	}

	/**
	 * contains master data
	 * 
	 * @param id
	 * @param type
	 * @return
	 */
	public boolean containsMasterData(MasterDataIdentity id, MasterDataType type) {
		MasterDataFactoryBase factory = this.getMasterDataFactory(type);
		return factory.contains(id);
	}

	/**
	 * get G/L accounts based on G/L account group
	 * 
	 * @param group
	 *            G/L account group
	 * @return
	 */
	public MasterDataIdentity_GLAccount[] getGLAccountsBasedGroup(
			MasterDataIdentity group) {
		MasterDataFactoryBase factory = getMasterDataFactory(MasterDataType.GL_ACCOUNT);
		MasterDataBase[] datas = factory.getAllEntities();
		ArrayList<MasterDataIdentity_GLAccount> array = new ArrayList<MasterDataIdentity_GLAccount>();

		for (MasterDataBase data : datas) {
			GLAccountMasterData glAccount = (GLAccountMasterData) data;
			if (glAccount.getAccountGroup().equals(group)) {
				array.add((MasterDataIdentity_GLAccount) glAccount
						.getIdentity());
			}
		}

		MasterDataIdentity_GLAccount[] ret = new MasterDataIdentity_GLAccount[array
				.size()];
		for (int i = 0; i < ret.length; ++i) {
			ret[i] = array.get(i);
		}
		return ret;
	}

	/**
	 * clear
	 */
	public void clear() {
		_factoryList.clear();
	}
}
