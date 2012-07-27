package com.jasonzqshen.familyaccounting.core;

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

	/**
	 * singleton
	 */
	private CoreDriver() {

	}
}
