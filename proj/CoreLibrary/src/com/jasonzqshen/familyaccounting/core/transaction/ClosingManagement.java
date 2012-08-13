package com.jasonzqshen.familyaccounting.core.transaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.masterdata.GLAccountMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.utils.DocumentType;
import com.jasonzqshen.familyaccounting.core.utils.MessageType;

public class ClosingManagement {
	private final ArrayList<IClosingTaskManagement> _taskMgmts;
	private final TransactionDataManagement _transDataMgmt;
	private final CoreDriver _coreDriver;

	ClosingManagement(CoreDriver coreDriver,
			TransactionDataManagement transDataMgmt) {
		_taskMgmts = new ArrayList<IClosingTaskManagement>();
		_transDataMgmt = transDataMgmt;
		_coreDriver = coreDriver;
	}

	/**
	 * register task management
	 * 
	 * @param taskMgmt
	 */
	public void registerTaskMgmt(IClosingTaskManagement taskMgmt) {
		_taskMgmts.add(taskMgmt);
	}

	/**
	 * get all tasks
	 * 
	 * @return
	 */
	public ArrayList<ITask> getAllTasks() {
		ArrayList<ITask> tasks = new ArrayList<ITask>();
		for (IClosingTaskManagement mgmt : _taskMgmts) {
			tasks.addAll(mgmt.getTasks());
		}

		return tasks;
	}

	/**
	 * close ledger
	 * 
	 * @param monthLedger
	 * @return
	 */
	public boolean closeLedger() {
		MonthLedger openLedger = _transDataMgmt.getCurrentLedger();

		// check all tasks
		for (IClosingTaskManagement taskMgmt : _taskMgmts) {
			if (taskMgmt.checkBeforeClosing(openLedger) == false) {
				return false;
			}
		}

		SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd");
		Date date;
		try {
			date = format.parse(openLedger.getMonthID().toString() + "_02");
		} catch (ParseException e) {
			_coreDriver.logDebugInfo(this.getClass(), 67, e.toString(),
					MessageType.ERROR);
			throw new SystemException(e);
		}

		// create closing document
		HeadEntity headEntity = new HeadEntity(_coreDriver,
				_coreDriver.getMasterDataManagement());
		headEntity.setPostingDate(date);
		headEntity
				.setDocText(TransactionDataManagement.TRANSACTION_DATA_FOLDER);
		headEntity.setDocumentType(DocumentType.GL);

		// items
		// all the cost and profit
		MasterDataManagement mdMgmt = _coreDriver.getMasterDataManagement();
		GLAccountMasterData[] costAccounts = mdMgmt.getCostAccounts();
		GLAccountMasterData[] profitAccounts = mdMgmt.getCostAccounts();
		for (GLAccountMasterData costAcc : costAccounts) {
			
		}

		return true;
	}
}
