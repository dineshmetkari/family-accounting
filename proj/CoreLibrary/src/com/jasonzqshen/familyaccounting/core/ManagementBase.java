package com.jasonzqshen.familyaccounting.core;

import java.util.ArrayList;

import com.jasonzqshen.familyaccounting.core.utils.CoreMessage;

public abstract class ManagementBase {
	protected final CoreDriver _coreDriver;

	public ManagementBase(CoreDriver coreDriver) {
		_coreDriver = coreDriver;
	}

	/**
	 * initialize
	 */
	public abstract void initialize(ArrayList<CoreMessage> messages);

	/**
	 * clear
	 */
	public abstract void clear();
}
