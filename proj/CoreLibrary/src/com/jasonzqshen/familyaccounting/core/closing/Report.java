package com.jasonzqshen.familyaccounting.core.closing;

import java.util.Hashtable;

import org.w3c.dom.Document;

import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.transaction.MonthIdentity;
import com.jasonzqshen.familyaccounting.core.transaction.ReportsType;

public class Report {
	private final MonthIdentity _monthId;
	private final ReportsType _type;
	private final Hashtable<MasterDataIdentity_GLAccount, Integer> _amounts;

	public Report(MonthIdentity monthId, ReportsType type) {
		_monthId = monthId;
		_type = type;
		_amounts = new Hashtable<MasterDataIdentity_GLAccount, Integer>();
	}

	/**
	 * get month identity
	 * 
	 * @return
	 */
	public MonthIdentity getMonthId() {
		return _monthId;
	}

	/**
	 * get report type
	 * 
	 * @return
	 */
	public ReportsType getReportType() {
		return _type;
	}

	/**
	 * get amount based on GL account
	 * 
	 * @return
	 */
	public double getAmount(MasterDataIdentity_GLAccount glAccount) {
		if (this._amounts.contains(glAccount)) {
			int amount = _amounts.get(glAccount);
			return amount / 10.0;
		}
		return 0;
	}

	/**
	 * parse XML document to report
	 * 
	 * @param doc
	 * @return
	 */
	public static Report parseReport(Document doc) {
		return null;
	}
}
