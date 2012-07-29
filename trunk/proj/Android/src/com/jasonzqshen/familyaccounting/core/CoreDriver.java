package com.jasonzqshen.familyaccounting.core;

import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;

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

	/**
	 * singleton
	 */
	private CoreDriver() {
		_masterDataManagement = new MasterDataManagement(this);
	}

	/**
	 * get root path
	 * 
	 * @return root path
	 */
	public String appRootPath() {
		return null;
	}

	/**
	 * get master data management
	 * 
	 * @return
	 */
	public MasterDataManagement getMasterDataManagement() {
		return _masterDataManagement;
	}
}
