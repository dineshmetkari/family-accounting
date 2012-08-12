package com.jasonzqshen.familyaccounting.core;

import java.io.File;
import java.util.ArrayList;

import com.jasonzqshen.familyaccounting.core.exception.NoMasterDataFileException;
import com.jasonzqshen.familyaccounting.core.exception.RootFolderNotExsits;
import com.jasonzqshen.familyaccounting.core.exception.format.MasterDataFileFormatException;
import com.jasonzqshen.familyaccounting.core.exception.runtime.NoMasterDataFactoryClass;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.listeners.ListenersManagement;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.reports.ReportsManagement;
import com.jasonzqshen.familyaccounting.core.transaction.MonthIdentity;
import com.jasonzqshen.familyaccounting.core.transaction.TransactionDataManagement;
import com.jasonzqshen.familyaccounting.core.utils.CoreMessage;
import com.jasonzqshen.familyaccounting.core.utils.DebugInformation;
import com.jasonzqshen.familyaccounting.core.utils.MessageType;

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

	private final ListenersManagement _listenerManagement;
	private String _applicationRootPath;
	private MonthIdentity _monthId;

	private final ArrayList<ManagementBase> _managements;

	private ArrayList<DebugInformation> _infos;

	/**
	 * singleton
	 */
	private CoreDriver() {
		_infos = new ArrayList<DebugInformation>();

		_listenerManagement = new ListenersManagement();

		// managements
		_managements = new ArrayList<ManagementBase>();
		_managements.add(new MasterDataManagement(this)); // 0
		_managements.add(new TransactionDataManagement(this)); // 1
		_managements.add(new ReportsManagement(this));// 2
	}

	/**
	 * log debug information
	 * 
	 * @param cl
	 * @param lineNum
	 * @param msg
	 */
	public void logDebugInfo(Class<?> cl, int lineNum, String msg,
			MessageType type) {
		_infos.add(new DebugInformation(cl, lineNum, msg, type));
	}

	/**
	 * get debug information
	 * 
	 * @return
	 */
	public DebugInformation[] getDebugInfos() {
		DebugInformation[] infos = new DebugInformation[_infos.size()];
		for (int i = 0; i < infos.length; ++i) {
			infos[i] = _infos.get(i);
		}
		return infos;
	}

	/**
	 * Initialize. Before the core driver really works, it should initialize. In
	 * initializing process, system will set up the folder, master data folder
	 * and transaction folder. And then, transaction and master data management
	 * will initialize.
	 * 
	 * @throws SystemException
	 *             system bug
	 * @throws NoMasterDataFactoryClass
	 *             system bug
	 * @throws RootFolderNotExsits
	 */
	public void init(ArrayList<CoreMessage> messages)
			throws RootFolderNotExsits, NoMasterDataFileException,
			MasterDataFileFormatException {
		this.logDebugInfo(this.getClass(), 96, "Core driver initializing...",
				MessageType.INFO);

		// check root folder
		File file = new File(_applicationRootPath);
		if (!file.exists()) {
			this.logDebugInfo(this.getClass(), 102, String.format(
					"Application root %s path does not exist. ",
					_applicationRootPath), MessageType.ERROR);
			throw new RootFolderNotExsits(_applicationRootPath);
		}
		String masterFolderPath = String.format("%s/%s", _applicationRootPath,
				MasterDataManagement.MASTER_DATA_FOLDER);

		// set up master data folder
		File masterFolder = new File(masterFolderPath);
		if (!masterFolder.exists()) {
			this.logDebugInfo(this.getClass(), 113,
					"Master data root folder does not exist. Make directory.",
					MessageType.INFO);
			masterFolder.mkdir();
		}
		// set up transaction data folder
		String transFolderPath = String.format("%s/%s", _applicationRootPath,
				TransactionDataManagement.TRANSACTION_DATA_FOLDER);
		File transFolder = new File(transFolderPath);
		if (!transFolder.exists()) {
			this.logDebugInfo(
					this.getClass(),
					125,
					"Transaction data root folder does not exist. Make directory.",
					MessageType.INFO);
			transFolder.mkdir();
		}

		// initialize management
		for (ManagementBase m : _managements) {
			m.initialize(messages);
		}

		this.logDebugInfo(this.getClass(), 134,
				"Core driver initializing completed.", MessageType.INFO);
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
		return (MasterDataManagement) _managements.get(0);
	}

	/**
	 * get transaction data management
	 * 
	 * @return
	 */
	public TransactionDataManagement getTransDataManagement() {
		return (TransactionDataManagement) _managements.get(1);
	}

	/**
	 * get reports management
	 * 
	 * @return
	 */
	public ReportsManagement getReportsManagement() {
		return (ReportsManagement) _managements.get(2);
	}

	/**
	 * get listener management
	 * 
	 * @return
	 */
	public ListenersManagement getListenersManagement() {
		return _listenerManagement;
	}

	/**
	 * clear
	 */
	public void clear() {
		for (ManagementBase m : _managements) {
			m.clear();
		}

		_infos.clear();
	}

}