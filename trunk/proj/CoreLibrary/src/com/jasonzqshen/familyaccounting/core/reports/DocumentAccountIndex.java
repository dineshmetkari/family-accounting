package com.jasonzqshen.familyaccounting.core.reports;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;
import com.jasonzqshen.familyaccounting.core.transaction.ItemEntity;
import com.jasonzqshen.familyaccounting.core.utils.CreditDebitIndicator;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;

public class DocumentAccountIndex extends DocumentIndex {

	DocumentAccountIndex(CoreDriver coreDriver, MasterDataManagement mdMgmt) {
		super(coreDriver, mdMgmt);
	}

	@Override
	protected void newDoc(HeadEntity head) {
		ItemEntity[] items = head.getItems();

		for (int i = 0; i < items.length; ++i) {
			MasterDataIdentity id = items[i].getGLAccount();
			DocumentIndexItem item;

			if (!_list.containsKey(id)) {
				item = new DocumentIndexItem(_coreDriver, id);
				_list.put(id, item);
			} else {
				item = _list.get(id);
			}
			item.addDoc(head);
			// add amount
			CurrencyAmount amount = items[i].getAmount();
			if (items[i].getCDIndicator() == CreditDebitIndicator.CREDIT) {
				amount.negate();
			}
			item.addAmount(head.getMonthId(), amount);
		}
	}

	@Override
	protected void reverseDoc(HeadEntity head) {
		ItemEntity[] items = head.getItems();

		for (int i = 0; i < items.length; ++i) {
			MasterDataIdentity id = items[i].getGLAccount();
			if (!_list.containsKey(id)) {
				continue;
			}
			DocumentIndexItem item = _list.get(id);

			// remove document
			item.removeDoc(head);
			// remove amount
			CurrencyAmount amount = items[i].getAmount();
			if (items[i].getCDIndicator() == CreditDebitIndicator.DEBIT) {
				amount.negate();
			}
			item.addAmount(head.getMonthId(), amount);
		}
	}
}
