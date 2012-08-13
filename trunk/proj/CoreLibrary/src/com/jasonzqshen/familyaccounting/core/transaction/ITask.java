package com.jasonzqshen.familyaccounting.core.transaction;

public interface ITask {
	Object[] getTaskInformation();

	/**
	 * handle task with parameters
	 * 
	 * @param params
	 */
	void handleTask(Object... params);

	/**
	 * commit task
	 * 
	 * @return if return false, commit failure.
	 */
	boolean commitTask();

	/**
	 * whether the task is still open
	 * 
	 * @return
	 */
	boolean isOpen();
}
