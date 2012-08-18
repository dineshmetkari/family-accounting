package com.jasonzqshen.familyAccounting.widgets;

import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;

public class BalanceAccountAdapterItem {
	public static final int VIEW_TYPE_COUNT = 3;
	public static final int VIEW_TYPE_TOP = 1;
	public static final int VIEW_TYPE_CENTER = 2;
	public static final int VIEW_TYPE_BOTTOM = 0;

	public final CurrencyAmount Value;
	public final int Type;
	public final String Descp;
	public final MasterDataIdentity MdId;

	public BalanceAccountAdapterItem(CurrencyAmount value, int type,
			String descp, MasterDataIdentity id) {
		Value = value;
		Type = type;
		Descp = descp;
		MdId = id;
	}
}
