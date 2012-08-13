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
import com.jasonzqshen.familyaccounting.core.ManagementBase;
import com.jasonzqshen.familyaccounting.core.exception.IdentityInvalidChar;
import com.jasonzqshen.familyaccounting.core.exception.IdentityNoData;
import com.jasonzqshen.familyaccounting.core.exception.IdentityTooLong;
import com.jasonzqshen.familyaccounting.core.exception.format.MasterDataFileFormatException;
import com.jasonzqshen.familyaccounting.core.exception.runtime.NoMasterDataFactoryClass;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.utils.GLAccountGroup;
import com.jasonzqshen.familyaccounting.core.utils.MessageType;

/**
 * Master Data Management, which contains all master data factories.
 * 
 * @author I072485
 * 
 */
public class MasterDataManagement extends ManagementBase {
	public static final String MASTER_DATA_FOLDER = "master_data";

	private final Hashtable<MasterDataType, MasterDataFactoryBase> _factoryList;
	private final Hashtable<MasterDataType, Class<?>> _registerFactorys;

	/**
	 * 
	 * @param coreDriver
	 */
	public MasterDataManagement(CoreDriver coreDriver) {
		super(coreDriver);
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
			MasterDataType type, Document doc)
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
			newFactory = (MasterDataFactoryBase) constructor.newInstance(
					_coreDriver, this);
			nodeList = rootElem.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); ++i) {
				Node child = nodeList.item(i);
				if (child instanceof Element) {
					Element elem = (Element) child;
					if (elem.getNodeName().equals(MasterDataUtils.XML_ENTITY)) {
						// parse master data entity
						MasterDataBase masterData = newFactory.parseMasterData(
								_coreDriver, elem);

						// raise load master data
						_coreDriver.getListenersManagement().loadMasterData(
								this, masterData);
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
	public void initialize() throws MasterDataFileFormatException {
		_coreDriver.logDebugInfo(this.getClass(), 141,
				"Master data loading...", MessageType.INFO);

		for (MasterDataType type : MasterDataType.values()) {
			_coreDriver.logDebugInfo(this.getClass(), 144,
					String.format("Loading %s...", type), MessageType.INFO);

			String filePath = generateMasterFilePath(type);
			_coreDriver.logDebugInfo(this.getClass(), 148, String.format(
					"Master data file %s for master data %s.", filePath, type),
					MessageType.INFO);

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
				constructor = factoryClass.getConstructor(CoreDriver.class,
						MasterDataManagement.class);
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
				throw new MasterDataFileFormatException(type);
			}

			try {
				DocumentBuilderFactory docFactory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder builder = docFactory.newDocumentBuilder();
				Document doc = builder.parse(file);

				// parse
				MasterDataFactoryBase newFactory = factoryParser(constructor,
						type, doc);

				_coreDriver.logDebugInfo(this.getClass(), 208,
						String.format("Loading %s completed.", type),
						MessageType.INFO);

				this._factoryList.put(type, newFactory);
			} catch (ParserConfigurationException e) {
				_coreDriver.logDebugInfo(this.getClass(), 207, e.toString(),
						MessageType.ERROR);
				throw new SystemException(e);
			} catch (SAXException e) {
				_coreDriver.logDebugInfo(this.getClass(), 216, String.format(
						"Master data file %s contains format error", filePath),
						MessageType.ERROR);
				throw new MasterDataFileFormatException(type);
			} catch (IOException e) {
				_coreDriver.logDebugInfo(this.getClass(), 220,
						String.format(e.toString(), filePath),
						MessageType.ERROR);
				throw new SystemException(e);
			}

		}

		_coreDriver.logDebugInfo(
				this.getClass(),
				282,
				"Master data loading completed. Items: "
						+ _registerFactorys.size(), MessageType.INFO);
	}

	/**
	 * create instance via the constructor
	 * 
	 * @param constructor
	 * @return
	 * @throws SystemException
	 *             system bug
	 */
	private MasterDataFactoryBase createFactory(Constructor<?> constructor) {
		try {
			MasterDataFactoryBase newFactory = (MasterDataFactoryBase) constructor
					.newInstance(_coreDriver, this);

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
			GLAccountGroup group) {
		MasterDataFactoryBase factory = getMasterDataFactory(MasterDataType.GL_ACCOUNT);
		MasterDataBase[] datas = factory.getAllEntities();
		ArrayList<MasterDataIdentity_GLAccount> array = new ArrayList<MasterDataIdentity_GLAccount>();

		for (MasterDataBase data : datas) {
			GLAccountMasterData glAccount = (GLAccountMasterData) data;
			if (glAccount.getGroup() == group) {
				array.add(glAccount.getGLIdentity());
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
	 * get G/L accounts based on G/L account group
	 * 
	 * @param group
	 *            G/L account group
	 * @return
	 */
	public GLAccountMasterData[] getBalanceAccounts() {
		GLAccountMasterDataFactory factory = (GLAccountMasterDataFactory) getMasterDataFactory(MasterDataType.GL_ACCOUNT);
		return factory.getBalanceAccounts();
	}

	/**
	 * get liquidity
	 * 
	 * @param group
	 * @return
	 */
	public GLAccountMasterData[] getLiquidityAccounts() {
		GLAccountMasterDataFactory factory = (GLAccountMasterDataFactory) getMasterDataFactory(MasterDataType.GL_ACCOUNT);
		return factory.getLiquidityAccounts();
	}

	/**
	 * get liability
	 * 
	 * @param group
	 * @return
	 */
	public GLAccountMasterData[] getLiabilityAccounts() {
		GLAccountMasterDataFactory factory = (GLAccountMasterDataFactory) getMasterDataFactory(MasterDataType.GL_ACCOUNT);
		return factory.getLiabilityAccounts();
	}

	/**
	 * get cost accounts
	 * 
	 * @param group
	 * @return
	 */
	public GLAccountMasterData[] getCostAccounts() {
		GLAccountMasterDataFactory factory = (GLAccountMasterDataFactory) getMasterDataFactory(MasterDataType.GL_ACCOUNT);
		return factory.getCostAccounts();
	}

	/**
	 * get revenue accounts
	 * 
	 * @param group
	 * @return
	 */
	public GLAccountMasterData[] getRevenueAccounts() {
		GLAccountMasterDataFactory factory = (GLAccountMasterDataFactory) getMasterDataFactory(MasterDataType.GL_ACCOUNT);
		return factory.getRevenueAccounts();
	}

	/**
	 * clear
	 */
	public void clear() {
		_factoryList.clear();
	}

	@Override
	public void establishFiles() {
		String masterFolderPath = String.format("%s/%s",
				_coreDriver.getRootPath(), MASTER_DATA_FOLDER);

		// set up master data folder
		File masterFolder = new File(masterFolderPath);
		if (!masterFolder.exists()) {
			_coreDriver.logDebugInfo(this.getClass(), 113,
					"Master data root folder does not exist. Make directory.",
					MessageType.INFO);
			masterFolder.mkdir();
		}

		// establish master data files
		for (MasterDataType type : MasterDataType.values()) {
			// get factory class
			Class<?> factoryClass = _registerFactorys.get(type);
			if (factoryClass == null) {
				_coreDriver
						.logDebugInfo(
								this.getClass(),
								178,
								String.format(
										"Master data class is not registered for master data %s",
										type), MessageType.ERROR);
				throw new NoMasterDataFactoryClass(type);
			}

			try {
				Constructor<?> constructor = factoryClass.getConstructor(
						CoreDriver.class, MasterDataManagement.class);
				// create new factory
				MasterDataFactoryBase factory = createFactory(constructor);
				_factoryList.put(type, factory);

				String xdoc = factory.toXmlDocument();

				String filePath = generateMasterFilePath(type);

				File file = new File(filePath);
				if (!file.exists()) {
					file.createNewFile();
				}

				FileWriter writer = new FileWriter(file);
				writer.write(xdoc, 0, xdoc.length());
				writer.close();
			} catch (NoSuchMethodException e1) {
				_coreDriver.logDebugInfo(this.getClass(), 191, e1.toString(),
						MessageType.ERROR);
				throw new SystemException(e1);
			} catch (SecurityException e1) {
				_coreDriver.logDebugInfo(this.getClass(), 193, e1.toString(),
						MessageType.ERROR);
				throw new SystemException(e1);
			} catch (IOException e) {
				_coreDriver.logDebugInfo(this.getClass(), 193, e.toString(),
						MessageType.ERROR);
				throw new SystemException(e);
			}
		}

	}
}
