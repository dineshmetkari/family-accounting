package com.jasonzqshen.familyAccounting.data;

import java.io.File;
import java.util.Locale;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import com.jasonzqshen.familyAccounting.exceptions.CoreDriverInitException;
import com.jasonzqshen.familyAccounting.exceptions.ExternalStorageException;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.document_entries.EntryTemplatesManagement;
import com.jasonzqshen.familyaccounting.core.exception.format.FormatException;
import com.jasonzqshen.familyaccounting.core.exception.format.TemplateFormatException;
import com.jasonzqshen.familyaccounting.core.investment.InvestmentManagement;
import com.jasonzqshen.familyaccounting.core.reports.ReportsManagement;
import com.jasonzqshen.familyaccounting.core.utils.Language;

/**
 * singleton
 * 
 * @author I072485
 * 
 */
public class DataCore {
	public static final String TAG = "DataCore";

	public static final String _ANDROID_ROOT = "Android";
	public static final String _DATA_ROOT = "data";
	public static final String _FILES_ROOT = "files";
	public static final String _PKG = "package";
	public static final String[] _PATH = new String[] { _ANDROID_ROOT,
			_DATA_ROOT, _PKG };

	private static DataCore _instance;

	public static DataCore getInstance() {
		if (_instance == null) {
			_instance = new DataCore();
		}
		return _instance;
	}

	private CoreDriver _coreDriver;

	private EntryTemplatesManagement _tmpMgmt;

	private InvestmentManagement _investMgmt;

	private String _rootFolder;

	/**
	 * constructor
	 */
	private DataCore() {
		_coreDriver = new CoreDriver();
		_tmpMgmt = new EntryTemplatesManagement(_coreDriver);
		_investMgmt = new InvestmentManagement(_coreDriver);

		// set Chinese
		if (Locale.SIMPLIFIED_CHINESE.equals(Locale.getDefault())) {
			_coreDriver.setLanguage(Language.SimpleChinese);
		}
	}

	/**
	 * initialize
	 * 
	 * @return
	 * @throws ExternalStorageException
	 * @throws CoreDriverInitException
	 */
	public void initialize(Activity activity) throws ExternalStorageException,
			CoreDriverInitException {
		if (_coreDriver.isInitialized()) {
			return;
		}

		// check external storage status
		String state = Environment.getExternalStorageState();
		if (!Environment.MEDIA_MOUNTED.equals(state)) {
			throw new ExternalStorageException(
					"Cannot read or writer to external storage.");
		}

		// construct root folder for the application
		File rootDir = Environment.getExternalStorageDirectory();
		_rootFolder = establishRootFolder(activity, rootDir.getAbsolutePath());
		if (_rootFolder == null) {
			throw new ExternalStorageException(
					"Cannot create folder for application.");
		}

		// check root folder exists
		_rootFolder = String.format("%s/%s", _rootFolder, _FILES_ROOT);
		boolean initMasterData = false;
		File rootFolder = new File(_rootFolder);
		if (rootFolder.exists() == false) {
			initMasterData = true;
		}

		// initialize the core driver
		Log.i(TAG, "Root folder: " + _rootFolder);
		_coreDriver.setRootPath(_rootFolder);
		if (_coreDriver.isInitialized() == false) {
			throw new CoreDriverInitException(
					"CoreDriver initialize with failure");
		}

		// initialize the template management
		try {
			_tmpMgmt.initialize();
		} catch (TemplateFormatException e) {
			throw new CoreDriverInitException(
					"CoreDriver initialize with failure");
		}

		// initialize the investment management
		try {
			_investMgmt.initialize();
		} catch (FormatException e) {
			throw new CoreDriverInitException(
					"CoreDriver initialize with failure");
		}

		if (initMasterData) {
			DefaultMasterDataCreator.createDefaultMD(activity, _coreDriver);
		}
	}

	/**
	 * get core driver
	 * 
	 * @return
	 */
	public CoreDriver getCoreDriver() {
		return _coreDriver;
	}

	/**
	 * get reports management
	 * 
	 * @return
	 */
	public ReportsManagement getReportsManagement() {
		return _coreDriver.getReportsManagement();
	}

	/**
	 * get template management
	 * 
	 * @return
	 */
	public EntryTemplatesManagement getTemplateManagement() {
		return _tmpMgmt;
	}

	/**
	 * get investment template
	 * 
	 * @return
	 */
	public InvestmentManagement getInvestMgmt() {
		return _investMgmt;
	}

	/**
	 * establish root folder
	 * 
	 * @param path
	 *            of the SD root
	 * 
	 * @return folder path
	 */
	private String establishRootFolder(Activity activity, String sdRoot) {
		String folderPath = sdRoot;

		for (String str : _PATH) {
			String id = str;
			if (str.equals(_PKG)) {
				id = activity.getPackageName();
			}

			folderPath = String.format("%s/%s", folderPath, id);
			File folder = new File(folderPath);
			if (folder.exists() == false) {
				if (folder.mkdir() == false) {
					return null;
				}
			}
		}

		return folderPath;
	}

}
