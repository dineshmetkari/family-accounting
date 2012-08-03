package com.jasonzqshen.familyaccounting.core.transaction;

import com.jasonzqshen.familyaccounting.core.exception.DocumentIdentityFormatException;
import com.jasonzqshen.familyaccounting.core.exception.FiscalMonthRangeException;
import com.jasonzqshen.familyaccounting.core.exception.FiscalYearRangeException;
import com.jasonzqshen.familyaccounting.core.exception.IdentityInvalidChar;
import com.jasonzqshen.familyaccounting.core.exception.IdentityNoData;
import com.jasonzqshen.familyaccounting.core.exception.IdentityTooLong;
import com.jasonzqshen.familyaccounting.core.exception.MonthIdentityFormatException;

public class DocumentIdentity {
	public final DocumentNumber _docNumber;
	public final MonthIdentity _monthIdentity;

	public DocumentIdentity(DocumentNumber docNum, int fiscalYear,
			int fiscalMonth) throws FiscalYearRangeException,
			FiscalMonthRangeException {
		_docNumber = docNum;
		_monthIdentity = new MonthIdentity(fiscalYear, fiscalMonth);
	}

	public DocumentIdentity(DocumentNumber docNum, MonthIdentity monthIdentity) {
		_docNumber = docNum;
		_monthIdentity = monthIdentity;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DocumentIdentity)) {
			return false;
		}

		DocumentIdentity id = (DocumentIdentity) obj;
		boolean ret = id._docNumber.equals(_docNumber)
				&& id._monthIdentity.equals(_monthIdentity);
		return ret;
	}

	@Override
	public int hashCode() {
		String str = this.toString();
		int count = 0;
		for (int i = 0; i < str.length(); ++i) {
			count += str.charAt(i);
		}

		return count;
	}

	@Override
	public String toString() {
		return String.format("%s_%s", _docNumber, _monthIdentity);
	}

	/**
	 * parse the string to the document identity
	 * 
	 * @param string
	 * @return
	 * @throws DocumentIdentityFormatException
	 */
	public static DocumentIdentity parse(String string)
			throws DocumentIdentityFormatException {
		String docNumStr = string.substring(0, 10);
		String monthIdStr = string.substring(11, 18);

		try {
			DocumentNumber id = new DocumentNumber(docNumStr.toCharArray());
			MonthIdentity monthId = MonthIdentity.parse(monthIdStr);

			return new DocumentIdentity(id, monthId);
		} catch (IdentityTooLong e) {
			throw new DocumentIdentityFormatException();
		} catch (IdentityNoData e) {
			throw new DocumentIdentityFormatException();
		} catch (IdentityInvalidChar e) {
			throw new DocumentIdentityFormatException();
		} catch (NumberFormatException e) {
			throw new DocumentIdentityFormatException();
		} catch (MonthIdentityFormatException e) {
			throw new DocumentIdentityFormatException();
		}
	}
}