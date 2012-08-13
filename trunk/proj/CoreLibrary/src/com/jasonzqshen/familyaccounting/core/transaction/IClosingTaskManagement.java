package com.jasonzqshen.familyaccounting.core.transaction;

import java.util.ArrayList;

public interface IClosingTaskManagement {
	boolean checkBeforeClosing(MonthLedger curLedger);

	ArrayList<ITask> getTasks();
}
