package com.jasonzqshen.familyaccounting.core.transaction;

import java.util.Hashtable;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.listeners.CreateMasterDataListener;
import com.jasonzqshen.familyaccounting.core.listeners.LoadDocumentListener;
import com.jasonzqshen.familyaccounting.core.listeners.LoadMasterDataListener;
import com.jasonzqshen.familyaccounting.core.listeners.SaveDocumentListener;
import com.jasonzqshen.familyaccounting.core.masterdata.GLAccountMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataBase;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataFactoryBase;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.utils.CreditDebitIndicator;

public class GLAccountBalanceCollection {
	private final CoreDriver _coreDriver;

	private final Hashtable<MasterDataIdentity_GLAccount, GLAccountBalanceItem> _items;

	GLAccountBalanceCollection(CoreDriver coreDriver) {
		_coreDriver = coreDriver;

		_items = new Hashtable<MasterDataIdentity_GLAccount, GLAccountBalanceItem>();
		
		_coreDriver.getListenersManagement().addSaveDocListener(
				_saveDocumentListener);
		_coreDriver.getListenersManagement().addLoadDocListener(
				_loadDocumentListener);
		_coreDriver.getListenersManagement().addLoadMasterListener(
				_loadMasterDataListener);
		coreDriver.getListenersManagement().addCreateMasterListener(
				_createMasterListener);
	}

	// load document listener
	private LoadDocumentListener _loadDocumentListener = new LoadDocumentListener() {
		public void onLoadDocumentListener(Object source, HeadEntity document) {
			newDoc(document);
		}
	};

	// save document listener
	private SaveDocumentListener _saveDocumentListener = new SaveDocumentListener() {
		public void onSaveDocumentListener(HeadEntity document) {
			newDoc(document);
		}
	};

	private LoadMasterDataListener _loadMasterDataListener = new LoadMasterDataListener() {
		public void onLoadMasterDataListener(Object obj,
				MasterDataBase masterData) {
			newMasterdata(masterData);
		}
	};

	private CreateMasterDataListener _createMasterListener = new CreateMasterDataListener() {
		public void onCreateMasterDataListener(MasterDataFactoryBase factory,
				MasterDataBase master) {
			newMasterdata(master);
		}

	};

	/**
	 * set report when new document
	 * 
	 * @param head
	 */
	private void newDoc(HeadEntity head) {

		for (ItemEntity item : head.getItems()) {
			int amount = (int) (item.getAmount() * 100);
			if (item.getCDIndicator() == CreditDebitIndicator.CREDIT) {
				amount = 0 - amount;
			}
			GLAccountBalanceItem balItem = _items.get(item.getGLAccount());

			balItem.addAmount(head.getMonthId(), amount);
		}
	}

	/**
	 * set report when new master data
	 * 
	 * @param dataS
	 */
	private void newMasterdata(MasterDataBase data) {
		if (data instanceof GLAccountMasterData) {
			MasterDataIdentity_GLAccount glId = (MasterDataIdentity_GLAccount) data
					.getIdentity();
			_items.put(glId, new GLAccountBalanceItem(glId));
		}
	}
}
