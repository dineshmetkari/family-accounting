package com.jasonzqshen.familyaccounting.core.closing;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Hashtable;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.ManagementBase;
import com.jasonzqshen.familyaccounting.core.exception.FiscalMonthRangeException;
import com.jasonzqshen.familyaccounting.core.exception.FiscalYearRangeException;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.transaction.MonthIdentity;
import com.jasonzqshen.familyaccounting.core.utils.MessageType;

public class ClosingManagement extends ManagementBase {
	public static final String BALANCE_REPORTS_FOLDER = "balance_sheet_reports";
	public static final String PL_REPORTS_FOLDER = "profit_loss_reports";

	private final Hashtable<MonthIdentity, Report> _balanceReports;
	private final Hashtable<MonthIdentity, Report> _plReports;

	public ClosingManagement(CoreDriver coreDriver) {
		super(coreDriver);

		_balanceReports = new Hashtable<MonthIdentity, Report>();
		_plReports = new Hashtable<MonthIdentity, Report>();
	}

	@Override
	public void initialize() {
		ArrayList<MonthIdentity> requiredMonths = this.getRequiredMonth();
		for (MonthIdentity monthId : requiredMonths) {
			load(monthId);
		}
	}

	/**
	 * load balance report and PL report for a month
	 * 
	 * @param monthIdentity
	 */
	public void load(MonthIdentity monthId) {
		// nothing for current implement
	}

	/**
	 * get the month identities which is required closing.
	 * 
	 * @return
	 */
	public ArrayList<MonthIdentity> getRequiredMonth() {
		// get the collection for each month
		final Calendar calendar = Calendar.getInstance();
		int curYear = calendar.get(Calendar.YEAR);
		int curMonth = calendar.get(Calendar.MONTH) + 1;

		MonthIdentity monthId = null;
		try {
			monthId = new MonthIdentity(curYear, curMonth);
		} catch (FiscalYearRangeException e) {
			throw new SystemException(e);
		} catch (FiscalMonthRangeException e) {
			throw new SystemException(e);
		}
		_coreDriver.logDebugInfo(
				this.getClass(),
				75,
				String.format("Current month identity is %s",
						monthId.toString()), MessageType.INFO);

		_coreDriver.logDebugInfo(this.getClass(), 75, String.format(
				"Starting month identity is %s", _coreDriver.getStartMonthId()
						.toString()), MessageType.INFO);

		ArrayList<MonthIdentity> ret = new ArrayList<MonthIdentity>();
		for (MonthIdentity startId = _coreDriver.getStartMonthId(); startId
				.compareTo(monthId) < 0; startId = startId.addMonth()) {
			if (this.getBalanceReport(startId) == null
					|| this.getPLReport(startId) == null) {
				continue;
			}
			ret.add(startId);
		}

		Collections.sort(ret);
		return ret;
	}

	@Override
	public void clear() {

	}

	/**
	 * get balance report
	 * 
	 * @param monthId
	 * @return
	 */
	@Deprecated
	public Report getBalanceReport(MonthIdentity monthId) {
		if (_balanceReports.contains(monthId)) {
			return _balanceReports.get(monthId);
		}
		return null;
	}

	/**
	 * get profit loss report based on month identity
	 * 
	 * @param monthId
	 * @return
	 */
	@Deprecated
	public Report getPLReport(MonthIdentity monthId) {
		if (_plReports.contains(monthId)) {
			return _plReports.get(monthId);
		}
		return null;
	}

	/**
	 * check whether closed
	 * 
	 * @param monthId
	 * @return
	 */
	public boolean isClose(MonthIdentity monthId) {
		return getRequiredMonth().contains(monthId);
	}

	@Override
	public void establishFiles() {
		// TODO Auto-generated method stub
		
	}

}
