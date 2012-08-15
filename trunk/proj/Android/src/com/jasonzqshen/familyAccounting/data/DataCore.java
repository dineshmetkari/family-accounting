package com.jasonzqshen.familyAccounting.data;

import java.io.File;

import android.os.Environment;
import android.util.Log;

import com.jasonzqshen.familyAccounting.exceptions.CoreDriverInitException;
import com.jasonzqshen.familyAccounting.exceptions.ExternalStorageException;
import com.jasonzqshen.familyaccounting.core.CoreDriver;

/**
 * singleton
 * 
 * @author I072485
 * 
 */
public class DataCore {
	public static final String TAG = "DataCore";
	public static final String _PACKAGE = "FamilyLedger";
	private static DataCore _instance;

	public static DataCore getInstance() {
		if (_instance == null) {
			_instance = new DataCore();
		}
		return _instance;
	}

	private CoreDriver _coreDriver;
	private String _rootFolder;

	/**
	 * constructor
	 */
	private DataCore() {
		_coreDriver = new CoreDriver();
	}

	/**
	 * initialize
	 * 
	 * @return
	 * @throws ExternalStorageException
	 * @throws CoreDriverInitException
	 */
	public void initialize() throws ExternalStorageException,
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

		File rootDir = Environment.getExternalStorageDirectory();
		_rootFolder = String.format("%s/%s", rootDir.getAbsolutePath(),
				_PACKAGE);

		Log.i(TAG, "Root folder: " + _rootFolder);
		_coreDriver.setRootPath(_rootFolder);

		if (_coreDriver.isInitialized() == false) {
			throw new CoreDriverInitException(
					"CoreDriver initialize with failure");
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

}
