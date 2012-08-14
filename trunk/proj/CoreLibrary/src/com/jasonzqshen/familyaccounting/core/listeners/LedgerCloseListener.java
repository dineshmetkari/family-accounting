package com.jasonzqshen.familyaccounting.core.listeners;

import com.jasonzqshen.familyaccounting.core.transaction.MonthLedger;

public interface LedgerCloseListener {
	void onLedgerCloseListener(MonthLedger ledger);
}
