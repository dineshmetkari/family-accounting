package com.jasonzqshen.familyaccounting.core.transaction;

import java.io.Serializable;

import com.jasonzqshen.familyaccounting.core.exception.FiscalMonthRangeException;
import com.jasonzqshen.familyaccounting.core.exception.FiscalYearRangeException;
import com.jasonzqshen.familyaccounting.core.exception.MonthIdentityFormatException;

public class MonthIdentity implements Serializable, Comparable<MonthIdentity> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1145199479960583527L;
	public final int _fiscalYear;
	public final int _fiscalMonth;

	public MonthIdentity(int fiscalYear, int fiscalMonth)
			throws FiscalYearRangeException, FiscalMonthRangeException {
		if (fiscalYear < 1000 || fiscalYear > 9999) {
			throw new FiscalYearRangeException(fiscalYear);
		}
		if (fiscalMonth <= 0 || fiscalMonth > 13) {
			throw new FiscalMonthRangeException(fiscalMonth);
		}
		_fiscalYear = fiscalYear;
		_fiscalMonth = fiscalMonth;
	}

	@Override
	public String toString() {
		if (_fiscalMonth < 10) {
			return String.format("%d_0%d", _fiscalYear, _fiscalMonth);
		}
		return String.format("%d_%d", _fiscalYear, _fiscalMonth);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MonthIdentity)) {
			return false;
		}

		MonthIdentity monthId = (MonthIdentity) obj;
		return monthId._fiscalMonth == this._fiscalMonth
				&& monthId._fiscalYear == this._fiscalYear;
	}

	@Override
	public int hashCode() {
		return _fiscalYear * 100 + _fiscalMonth;
	}

	/**
	 * 
	 * @param str
	 * @return
	 * @throws MonthIdentityFormatException
	 */
	public static MonthIdentity parse(String str)
			throws MonthIdentityFormatException {
		try {
			String yearStr = str.substring(0, 4);
			String monthStr = str.substring(5, 7);
			int year = Integer.parseInt(yearStr);
			int month = Integer.parseInt(monthStr);

			MonthIdentity id = new MonthIdentity(year, month);

			return id;
		} catch (FiscalYearRangeException e) {
			throw new MonthIdentityFormatException();
		} catch (FiscalMonthRangeException e) {
			throw new MonthIdentityFormatException();
		} catch (IndexOutOfBoundsException e) {
			throw new MonthIdentityFormatException();
		} catch (NumberFormatException e) {
			throw new MonthIdentityFormatException();
		}
	}

	/**
	 * 
	 * @param monthId
	 * @return the month count between the month identity. 1 when this bigger
	 *         than monthId, 0 when equal, -1 when smaller
	 */
	public int compareTo(MonthIdentity monthId) {
		int ret = (this._fiscalYear - monthId._fiscalYear) * 12
				+ this._fiscalMonth - monthId._fiscalMonth;
		return ret;
	}

	/**
	 * add month
	 */
	public MonthIdentity addMonth() {
		int month = _fiscalMonth + 1;
		int year = _fiscalYear;
		if (month > 12) {
			month = 1;
			year++;
		}

		MonthIdentity newId = null;
		try {
			newId = new MonthIdentity(year, month);
		} catch (FiscalYearRangeException e) {
		} catch (FiscalMonthRangeException e) {
		}

		return newId;
	}
}
