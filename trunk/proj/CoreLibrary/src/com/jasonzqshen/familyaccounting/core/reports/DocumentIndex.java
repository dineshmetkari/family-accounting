package com.jasonzqshen.familyaccounting.core.reports;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.listeners.LoadDocumentListener;
import com.jasonzqshen.familyaccounting.core.listeners.SaveDocumentListener;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;

public abstract class DocumentIndex {
    /**
     * comparator based on posting date
     */
    public final static Comparator<HeadEntity> COMPARATOR_DATE = new Comparator<HeadEntity>() {
        @Override
        public int compare(HeadEntity head1, HeadEntity head2) {
            return head1.getPostingDate().compareTo(head2.getPostingDate());
        }
    };

    public final static int ACCOUNT_INDEX = 0;

    public final static int BUSINESS_INDEX = 1;

    public final static int INDEX_COUNT = 2;

    protected final Hashtable<MasterDataIdentity, DocumentIndexItem> _list;

    protected final CoreDriver _coreDriver;

    protected final MasterDataManagement _mdMgmt;

    // load document listener
    private final LoadDocumentListener _loadDocumentListener = new LoadDocumentListener() {
        public void onLoadDocumentListener(Object source, HeadEntity document) {
            newDoc(document);
        }
    };

    // save document listener
    private final SaveDocumentListener _saveDocumentListener = new SaveDocumentListener() {
        public void onSaveDocumentListener(HeadEntity document) {
            newDoc(document);
        }
    };

    /**
     * document index
     * 
     * @param coreDriver
     */
    protected DocumentIndex(CoreDriver coreDriver, MasterDataManagement mdMgmt) {
        _coreDriver = coreDriver;
        _mdMgmt = mdMgmt;

        _list = new Hashtable<MasterDataIdentity, DocumentIndexItem>();

        _coreDriver.getListenersManagement().addSaveDocListener(
                _saveDocumentListener);
        _coreDriver.getListenersManagement().addLoadDocListener(
                _loadDocumentListener);
    }

    /**
     * 
     * @return
     */
    public ArrayList<MasterDataIdentity> getKeys() {
        ArrayList<MasterDataIdentity> ret = new ArrayList<MasterDataIdentity>(
                _list.keySet());
        Collections.sort(ret);

        return ret;
    }

    /**
     * get index item
     * 
     * @param key
     * @return
     */
    public DocumentIndexItem getIndexItem(MasterDataIdentity key) {
        return _list.get(key);
    }

    /**
     * clear
     */
    public void clear() {
        _list.clear();
    }

    /**
     * set report when new document
     * 
     * @param head
     */
    protected abstract void newDoc(HeadEntity head);
}
