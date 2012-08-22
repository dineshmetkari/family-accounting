package com.jasonzqshen.familyAccounting.widgets;

import com.jasonzqshen.familyaccounting.core.masterdata.GLAccountMasterData;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;

public class AccountReportAdapterItem implements
		Comparable<AccountReportAdapterItem> {
	public final static int HEAD_VIEW = 0;
	public final static int ITEM_VIEW = 1;
	public final static int HEAD_VIEW_RED = 2;
	public final static int VIEW_TYPE_COUNT = 3;

	public final String Descp;
	public final CurrencyAmount Amount;
	public final int Type;
	public final GLAccountMasterData Account;

	public AccountReportAdapterItem(String descp, CurrencyAmount amount,
			int type, GLAccountMasterData account) {
		Descp = descp;
		Amount = amount;
		Type = type;
		Account = account;
	}

	@Override
	public int compareTo(AccountReportAdapterItem item) {
		return item.Amount.compareTo(Amount);
	}
}
