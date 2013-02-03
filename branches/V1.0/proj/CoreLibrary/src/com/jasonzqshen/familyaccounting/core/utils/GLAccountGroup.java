package com.jasonzqshen.familyaccounting.core.utils;

import com.jasonzqshen.familyaccounting.core.exception.format.GLAccountGroupFormatException;

public enum GLAccountGroup {
	CASH("1000"), BANK_ACCOUNT("1010"), INVESTMENT("1060"), PREPAID("1430"), ASSETS(
			"1500"), SHORT_LIABILITIES("2000"), LONG_LIABILITIES("2700"), EQUITY(
			"3010"), SALARY("4000"), INVEST_REVENUE("4010"), COST_PURE("5000"), COST_ACCI(
			"5010");

	public static final GLAccountGroup[] BALANCE_GROUP = { GLAccountGroup.CASH,
			GLAccountGroup.BANK_ACCOUNT, GLAccountGroup.INVESTMENT,
			GLAccountGroup.ASSETS, GLAccountGroup.PREPAID };
	public static final GLAccountGroup[] Liquidity_GROUP = {
			GLAccountGroup.CASH, GLAccountGroup.BANK_ACCOUNT,
			GLAccountGroup.SHORT_LIABILITIES, GLAccountGroup.PREPAID };
	public static final GLAccountGroup[] LIABILITIES_GROUP = {
			GLAccountGroup.LONG_LIABILITIES, GLAccountGroup.SHORT_LIABILITIES };
	public static final GLAccountGroup[] REVENUE_GROUP = {
			GLAccountGroup.SALARY, GLAccountGroup.INVEST_REVENUE };
	public static final GLAccountGroup[] COST_GROUP = {
			GLAccountGroup.COST_ACCI, GLAccountGroup.COST_PURE };

	private final String _id;

	private GLAccountGroup(String str) {
		_id = str;
	}

	public String toString() {
		return _id;
	}

	/**
	 * Parse string to G/L account group
	 * 
	 * @param id
	 * @return
	 * @throws GLAccountGroupFormatException
	 */
	public static GLAccountGroup parse(String id)
			throws GLAccountGroupFormatException {
		if (id == null) {
			return null;
		}

		for (GLAccountGroup g : GLAccountGroup.values()) {
			if (g.toString().equals(id)) {
				return g;
			}
		}

		throw new GLAccountGroupFormatException(id);
	}

	/**
	 * get group
	 * 
	 * @param group
	 * @return
	 */
	public static String getDescp(GLAccountGroup group) {
		if (group == GLAccountGroup.CASH) {
			return "Cash on hand";
		} else if (group == GLAccountGroup.BANK_ACCOUNT) {
			return "Bank account";
		} else if (group == GLAccountGroup.INVESTMENT) {
			return "Investment";
		} else if (group == GLAccountGroup.PREPAID) {
			return "Prepaid";
		} else if (group == GLAccountGroup.ASSETS) {
			return "Fixed assets";
		} else if (group == GLAccountGroup.EQUITY) {
			return "Equity";
		} else if (group == GLAccountGroup.LONG_LIABILITIES) {
			return "Long liabilities";
		} else if (group == GLAccountGroup.SHORT_LIABILITIES) {
			return "Short liabilities";
		} else if (group == GLAccountGroup.SALARY) {
			return "Salary";
		} else if (group == GLAccountGroup.INVEST_REVENUE) {
			return "Revenue from investment";
		} else if (group == GLAccountGroup.COST_PURE) {
			return "Cost for purpose";
		} else if (group == GLAccountGroup.COST_ACCI) {
			return "Cost accidentlly";
		}

		return "";
	}
}
