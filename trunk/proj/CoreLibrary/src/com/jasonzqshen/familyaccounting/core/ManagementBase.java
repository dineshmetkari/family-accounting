package com.jasonzqshen.familyaccounting.core;

import com.jasonzqshen.familyaccounting.core.exception.format.FormatException;

public abstract class ManagementBase {
	protected final CoreDriver _coreDriver;

	public ManagementBase(CoreDriver coreDriver) {
		_coreDriver = coreDriver;
	}

	/**
	 * initialize
	 */
	public abstract void initialize() throws FormatException;

	/**
	 * clear
	 */
	public abstract void clear();

	public abstract void establishFiles();
}
