package com.jasonzqshen.familyaccounting.core;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;

import com.jasonzqshen.familyaccounting.core.exception.FiscalMonthRangeException;
import com.jasonzqshen.familyaccounting.core.exception.FiscalYearRangeException;
import com.jasonzqshen.familyaccounting.core.exception.RootFolderNotExsits;
import com.jasonzqshen.familyaccounting.core.exception.format.FormatException;
import com.jasonzqshen.familyaccounting.core.exception.format.MetaDataFormatException;
import com.jasonzqshen.familyaccounting.core.exception.runtime.NoMasterDataFactoryClass;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.listeners.LedgerCloseListener;
import com.jasonzqshen.familyaccounting.core.listeners.ListenersManagement;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.transaction.MonthIdentity;
import com.jasonzqshen.familyaccounting.core.transaction.MonthLedger;
import com.jasonzqshen.familyaccounting.core.transaction.TransactionDataManagement;
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
	public static final String META_DATA = "metadata.txt";
	public static final String START_YEAR_TAG = "start_year";
	public static final String START_MONTH_TAG = "start_month";
	public static final String CUR_YEAR_TAG = "cur_year";
	public static final String CUR_MONTH_TAG = "cur_month";

	public static final String MASTERDATA = "MD";
	public static final String TRANDATA = "TD";

	private LedgerCloseListener _closeLedgerListener = new LedgerCloseListener() {
		@Override
		public void onLedgerCloseListener(MonthLedger ledger) {
			_curMonthId = ledger.getMonthID();

			saveMetaData();

			logDebugInfo(this.getClass(), 58,
					"Save meta data file with new current month identity, "
							+ ledger.getMonthID().toString(), MessageType.INFO);
		}

	};

	private final ListenersManagement _listenerManagement;
	private String _applicationRootPath;
	private MonthIdentity _startMonthId;
	private MonthIdentity _curMonthId;
	private boolean _isInitialized;

	private final Hashtable<String, ManagementBase> _managements;
	private ArrayList<DebugInformation> _infos;

	/**
	 * singleton
	 */
	public CoreDriver() {
		_infos = new ArrayList<DebugInformation>();

		_listenerManagement = new ListenersManagement();
		_listenerManagement.addCloseLedgerListener(_closeLedgerListener);

		// managements
		_managements = new Hashtable<String, ManagementBase>();
		_managements.put(MASTERDATA, new MasterDataManagement(this));
		_managements.put(TRANDATA, new TransactionDataManagement(this,
				(MasterDataManagement) _managements.get(MASTERDATA)));

		_curMonthId = null;
		_startMonthId = null;

		_applicationRootPath = null;
		_isInitialized = false;
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
	private void init() throws RootFolderNotExsits, FormatException {
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

		ManagementBase m1;
		m1 = _managements.get(MASTERDATA);
		m1.initialize();
		m1 = _managements.get(TRANDATA);
		m1.initialize();
		// initialize management
		for (ManagementBase m : _managements.values()) {
			if (m instanceof TransactionDataManagement
					|| m instanceof MasterDataManagement) {
				continue;
			}

			m.initialize();
		}

		this.logDebugInfo(this.getClass(), 134,
				"Core driver initializing completed.", MessageType.INFO);
	}

	/**
	 * restart the core
	 */
	public void restart() {
		clear();

		// check root folder
		File file = new File(_applicationRootPath);
		if (file.exists()) {
			if (!file.isDirectory()) {
				return;
			}

			File[] files = file.listFiles();
			if (files.length != 0) {
				// load meta data
				try {
					loadMetaData();
					init();

					_isInitialized = true;
				} catch (RootFolderNotExsits e) {
					this.logDebugInfo(this.getClass(), 210, e.toString(),
							MessageType.ERROR);
					this.clear();
					throw new SystemException(e); // bug
				} catch (FormatException e) {
					this.logDebugInfo(this.getClass(), 210, e.toString(),
							MessageType.ERROR);
					// no handler
					this.clear();
				}
				return;
			}
		}

		// folder not exist or folder is empty
		if (!file.exists()) {
			boolean ret = file.mkdir();
			if (ret == false) {
				return;
			}
		}

		establishFolder();
	}

	/**
	 * set application root path, which will trigger initialize the core. the
	 * root folder is empty or it should follow the root folder format. If you
	 * would like to know whether it restart successfully, you could check with
	 * the method "isInitialized"
	 * 
	 * @param rootPath
	 *            if the parameter passed in is NULL, nothing will happen.
	 */
	public void setRootPath(String rootPath) {
		if (rootPath == null) {
			return;
		}
		if (_applicationRootPath == rootPath) {
			return;
		}

		_applicationRootPath = rootPath; // set the root path and restart
		this.restart();
	}

	/**
	 * 
	 * @throws MetaDataFormatException
	 */
	private void loadMetaData() throws MetaDataFormatException {
		String filePath = String.format("%s/%s", _applicationRootPath,
				META_DATA);

		FileInputStream fstream;
		try {
			fstream = new FileInputStream(filePath);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String line;
			int startYear = 0;
			int startMonth = 0;
			int curYear = 0;
			int curMonth = 0;
			while ((line = br.readLine()) != null) {
				String[] values = line.split("=");
				if (values.length != 2) {
					throw new MetaDataFormatException("Meta data format error.");
				}

				if (values[0].equals(CUR_MONTH_TAG)) {
					curMonth = Integer.parseInt(values[1]);
				} else if (values[0].equals(CUR_YEAR_TAG)) {
					curYear = Integer.parseInt(values[1]);
				} else if (values[0].equals(START_MONTH_TAG)) {
					startMonth = Integer.parseInt(values[1]);
				} else if (values[0].equals(START_YEAR_TAG)) {
					startYear = Integer.parseInt(values[1]);
				}
			}

			_startMonthId = new MonthIdentity(startYear, startMonth);
			_curMonthId = new MonthIdentity(curYear, curMonth);
		} catch (FileNotFoundException e) {
			throw new MetaDataFormatException(e.toString());
		} catch (IOException e) {
			throw new MetaDataFormatException(e.toString());
		} catch (NumberFormatException e) {
			throw new MetaDataFormatException(e.toString());
		} catch (FiscalYearRangeException e) {
			throw new MetaDataFormatException(e.toString());
		} catch (FiscalMonthRangeException e) {
			throw new MetaDataFormatException(e.toString());
		}

	}

	/**
	 * save meta data
	 */
	private void saveMetaData() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(String.format("%s=%d\n", CUR_MONTH_TAG,
				_curMonthId._fiscalMonth));
		strBuilder.append(String.format("%s=%d\n", CUR_YEAR_TAG,
				_curMonthId._fiscalYear));
		strBuilder.append(String.format("%s=%d\n", START_MONTH_TAG,
				_startMonthId._fiscalMonth));
		strBuilder.append(String.format("%s=%d\n", START_YEAR_TAG,
				_startMonthId._fiscalYear));
		// save meta data file
		String filePath = String.format("%s/%s", _applicationRootPath,
				META_DATA);
		File file = new File(filePath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				logDebugInfo(this.getClass(), 309, e.toString(),
						MessageType.ERROR);
				throw new SystemException(e);
			}
		}

		try {
			FileWriter writer = new FileWriter(file);
			writer.write(strBuilder.toString(), 0, strBuilder.length());
			writer.close();
		} catch (IOException e) {
			logDebugInfo(this.getClass(), 320, e.toString(), MessageType.ERROR);
			throw new SystemException(e);
		}

	}

	/**
	 * establish master data folder
	 */
	private void establishFolder() {
		// set meta data
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;

		try {
			_startMonthId = new MonthIdentity(year, month);
			_curMonthId = new MonthIdentity(year, month);
		} catch (FiscalYearRangeException e) {
			this.logDebugInfo(this.getClass(), 300, e.toString(),
					MessageType.ERROR);
			throw new SystemException(e);// bug
		} catch (FiscalMonthRangeException e) {
			this.logDebugInfo(this.getClass(), 302, e.toString(),
					MessageType.ERROR);
			throw new SystemException(e);// bug
		}

		saveMetaData();

		for (ManagementBase management : _managements.values()) {
			management.establishFiles();
		}
		_isInitialized = true;

	}

	/**
	 * get month identity
	 * 
	 * @return month identity
	 */
	public MonthIdentity getStartMonthId() {
		return _startMonthId;
	}

	/**
	 * get current month identity
	 * 
	 * @return
	 */
	public MonthIdentity getCurMonthId() {
		return _curMonthId;
	}

	/**
	 * check whether is initialized
	 */
	public boolean isInitialized() {
		return _isInitialized;
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
	 * get management
	 * 
	 * @param str
	 * @return
	 */
	public ManagementBase getManagement(String str) {
		if (!this.isInitialized()) {
			return null;
		}
		return _managements.get(str);
	}

	/**
	 * get master data management
	 * 
	 * @return
	 */
	public MasterDataManagement getMasterDataManagement() {
		return (MasterDataManagement) getManagement(MASTERDATA);
	}

	/**
	 * get transaction data management
	 * 
	 * @return
	 */
	public TransactionDataManagement getTransDataManagement() {
		return (TransactionDataManagement) getManagement(TRANDATA);
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
	private void clear() {
		for (ManagementBase m : _managements.values()) {
			m.clear();
		}

		_infos.clear();
		_isInitialized = false;
	}

}