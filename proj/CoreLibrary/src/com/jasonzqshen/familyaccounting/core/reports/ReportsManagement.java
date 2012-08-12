package com.jasonzqshen.familyaccounting.core.reports;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.ManagementBase;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityNotDefined;
import com.jasonzqshen.familyaccounting.core.listeners.CreateMasterDataListener;
import com.jasonzqshen.familyaccounting.core.listeners.LoadDocumentListener;
import com.jasonzqshen.familyaccounting.core.listeners.LoadMasterDataListener;
import com.jasonzqshen.familyaccounting.core.listeners.SaveDocumentListener;
import com.jasonzqshen.familyaccounting.core.masterdata.*;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;
import com.jasonzqshen.familyaccounting.core.transaction.ItemEntity;
import com.jasonzqshen.familyaccounting.core.utils.CreditDebitIndicator;

public class ReportsManagement extends ManagementBase {

	private final Hashtable<MasterDataIdentity_GLAccount, Integer> _glAccountBalance;
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
	 * report management
	 * 
	 * @param coreDriver
	 */
	public ReportsManagement(CoreDriver coreDriver) {
		super(coreDriver);
		_glAccountBalance = new Hashtable<MasterDataIdentity_GLAccount, Integer>();

		coreDriver.getListenersManagement().addLoadDocListener(
				_loadDocumentListener);
		coreDriver.getListenersManagement().addLoadMasterListener(
				_loadMasterDataListener);
		coreDriver.getListenersManagement().addSaveDocListener(
				_saveDocumentListener);
		coreDriver.getListenersManagement().addCreateMasterListener(
				_createMasterListener);
	}

	public void initialize() {

	}

	/**
	 * set report when new document
	 * 
	 * @param head
	 */
	private void newDoc(HeadEntity head) {

		for (ItemEntity item : head.getItems()) {
			int amount = (int) (item.getAmount() * 100);
			int sum = _glAccountBalance.get(item.getGLAccount());
			if (item.getCDIndicator() == CreditDebitIndicator.CREDIT) {
				sum -= amount;
			} else {
				sum += amount;
			}

			_glAccountBalance.put(item.getGLAccount(), sum);
		}
	}

	/**
	 * set report when new master data
	 * 
	 * @param dataS
	 */
	private void newMasterdata(MasterDataBase data) {
		if (data instanceof GLAccountMasterData) {
			_glAccountBalance.put(
					(MasterDataIdentity_GLAccount) data.getIdentity(), 0);
		}
	}

	/**
	 * get account balance
	 * 
	 * @param account
	 * @return
	 * @throws MasterDataIdentityNotDefined
	 */
	public double getGLAccountBalance(MasterDataIdentity_GLAccount account)
			throws MasterDataIdentityNotDefined {
		if (_glAccountBalance.contains(account)) {
			throw new MasterDataIdentityNotDefined(account,
					MasterDataType.GL_ACCOUNT);
		}

		return _glAccountBalance.get(account) / 100.0;
	}

	/**
	 * get gl account
	 * 
	 * @return
	 */
	public ArrayList<MasterDataIdentity_GLAccount> getGLAccounts() {
		ArrayList<MasterDataIdentity_GLAccount> ret = new ArrayList<MasterDataIdentity_GLAccount>(
				_glAccountBalance.keySet());
		Collections.sort(ret);
		return ret;
	}

	public void clear() {
		_glAccountBalance.clear();
	}

	@Override
	public void establishFiles() {
		// TODO Auto-generated method stub
		
	}

}
