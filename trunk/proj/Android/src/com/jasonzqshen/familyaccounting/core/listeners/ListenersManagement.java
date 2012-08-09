package com.jasonzqshen.familyaccounting.core.listeners;

import java.util.ArrayList;

import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataBase;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataFactoryBase;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;

public class ListenersManagement {
	private final ArrayList<LoadDocumentListener> _loadDocumentListeners;
	private final ArrayList<LoadMasterDataListener> _loadMasterDataListeners;
	private final ArrayList<CreateMasterDataListener> _createMasterDataListeners;
	private final ArrayList<SaveDocumentListener> _saveDocListeners;

	public ListenersManagement() {
		_loadDocumentListeners = new ArrayList<LoadDocumentListener>();
		_loadMasterDataListeners = new ArrayList<LoadMasterDataListener>();
		_createMasterDataListeners = new ArrayList<CreateMasterDataListener>();
		_saveDocListeners = new ArrayList<SaveDocumentListener>();
	}

	/**
	 * add load document listener
	 * 
	 * @param listener
	 *            load document listener
	 */
	public void addLoadDocListener(LoadDocumentListener listener) {
		_loadDocumentListeners.add(listener);
	}

	/**
	 * add load document listener
	 * 
	 * @param listener
	 *            load document listener
	 */
	public void addLoadMasterListener(LoadMasterDataListener listener) {
		_loadMasterDataListeners.add(listener);
	}

	/**
	 * add load document listener
	 * 
	 * @param listener
	 *            load document listener
	 */
	public void addSaveDocListener(SaveDocumentListener listener) {
		_saveDocListeners.add(listener);
	}

	/**
	 * add load document listener
	 * 
	 * @param listener
	 *            load document listener
	 */
	public void addCreateMasterListener(CreateMasterDataListener listener) {
		_createMasterDataListeners.add(listener);
	}

	/**
	 * raise save documents successfully
	 * 
	 * @param doc
	 */
	public void saveDoc(HeadEntity doc) {
		for (SaveDocumentListener l : _saveDocListeners) {
			l.onSaveDocumentListener(doc);
		}
	}

	/**
	 * raise load document from disk successfully
	 * 
	 * @param source
	 *            the object to invoke the loading
	 * @param doc
	 */
	public void loadDoc(Object source, HeadEntity doc) {
		for (LoadDocumentListener l : _loadDocumentListeners) {
			l.onLoadDocumentListener(source, doc);
		}
	}

	/**
	 * raise created master data successfully
	 * 
	 * @param masterData
	 */
	public void createMasterData(MasterDataFactoryBase factory,
			MasterDataBase masterData) {
		for (CreateMasterDataListener l : _createMasterDataListeners) {
			l.onCreateMasterDataListener(factory, masterData);
		}
	}

	/**
	 * raise save master data successfully
	 * 
	 * @param masterData
	 */
	public void loadMasterData(Object source, MasterDataBase masterData) {
		for (LoadMasterDataListener l : _loadMasterDataListeners) {
			l.onLoadMasterDataListener(source, masterData);
		}
	}
}
