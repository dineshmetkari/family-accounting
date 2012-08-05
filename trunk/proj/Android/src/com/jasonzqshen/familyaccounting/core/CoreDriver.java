package com.jasonzqshen.familyaccounting.core;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import com.jasonzqshen.familyaccounting.core.exception.NoMasterDataFactoryClass;
import com.jasonzqshen.familyaccounting.core.exception.RootFolderNotExsits;
import com.jasonzqshen.familyaccounting.core.exception.SystemException;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.transaction.MonthIdentity;
import com.jasonzqshen.familyaccounting.core.transaction.TransactionDataManagement;
import com.jasonzqshen.familyaccounting.core.utils.CoreMessage;

/**
 * Back end of the family finance application. The CoreDriver.java is the driver
 * of the back end. Back end can run without front end. Back end could be
 * started with function start.
 * 
 * CoreDriver follows Singleton pattern.
 * 
 * @author I072485
 * 
 */
public class CoreDriver {
	private static CoreDriver _instance;

	/**
	 * singleton
	 * 
	 * @return
	 */
	public static CoreDriver getInstance() {
		if (_instance == null) {
			_instance = new CoreDriver();
		}
		return _instance;
	}

	private final MasterDataManagement _masterDataManagement;
	private final TransactionDataManagement _transDataManagement;
	private String _applicationRootPath;
	private MonthIdentity _monthId;

	/**
	 * singleton
	 */
	private CoreDriver() {
		_masterDataManagement = new MasterDataManagement(this);
		_transDataManagement = new TransactionDataManagement(this);
	}

	/**
	 * initialize
	 * 
	 * @throws SystemException
	 * 
	 * @throws NoSuchMethodException
	 * @throws NoMasterDataFactoryClass
	 * @throws RootFolderNotExsits
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 */
	public void init(ArrayList<CoreMessage> messages)
			throws NoMasterDataFactoryClass, SystemException,
			RootFolderNotExsits {
		// establish folder
		File file = new File(_applicationRootPath);
		if (!file.exists()) {
			throw new RootFolderNotExsits(_applicationRootPath);
		}
		String masterFolderPath = String.format("%s/%s", _applicationRootPath,
				MasterDataManagement.MASTER_DATA_FOLDER);
		File masterFolder = new File(masterFolderPath);
		if (!masterFolder.exists()) {
			masterFolder.mkdir();
		}

		String transFolderPath = String.format("%s/%s", _applicationRootPath,
				TransactionDataManagement.TRANSACTION_DATA_FOLDER);
		File transFolder = new File(transFolderPath);
		if (!transFolder.exists()) {
			transFolder.mkdir();
		}
		
		_masterDataManagement.load(messages);
		_transDataManagement.load(messages);
	}

	/**
	 * set application root path
	 * 
	 * @param rootPath
	 */
	public void setRootPath(String rootPath) {
		_applicationRootPath = rootPath;
	}

	/**
	 * set month identity
	 * 
	 * @param monthId
	 */
	public void setStartMonthID(MonthIdentity monthId) {
		_monthId = monthId;
	}

	/**
	 * get month identity
	 * 
	 * @return month identity
	 */
	public MonthIdentity getStartMonthId() {
		return _monthId;
	}

	/**
	 * get root path
	 * 
	 * @return root path
	 */
	public String getRootPath() {
		return _applicationRootPath;
	}

	/**
	 * get master data management
	 * 
	 * @return
	 */
	public MasterDataManagement getMasterDataManagement() {
		return _masterDataManagement;
	}

	/**
	 * get transaction data management
	 * 
	 * @return
	 */
	public TransactionDataManagement getTransDataManagement() {
		return _transDataManagement;
	}

}