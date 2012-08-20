package com.jasonzqshen.familyaccounting.core.reports;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;
import com.jasonzqshen.familyaccounting.core.transaction.ItemEntity;

public class DocumentAccountIndex extends DocumentIndex {

	DocumentAccountIndex(CoreDriver coreDriver) {
		super(coreDriver);
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
		}
	}
}
