package com.jasonzqshen.familyaccounting.core.reports;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;
import com.jasonzqshen.familyaccounting.core.transaction.ItemEntity;

/**
 * document index based on business area
 * 
 * @author Jason
 * 
 */
public class DocumentBusinessIndex extends DocumentIndex {

    protected DocumentBusinessIndex(CoreDriver coreDriver) {
        super(coreDriver);
    }

    @Override
    protected void newDoc(HeadEntity head) {
        ItemEntity[] items = head.getItems();
        for (int i = 0; i < items.length; ++i) {
            MasterDataIdentity id = items[i].getBusinessArea();

            if (id != null) {
                DocumentIndexItem item;
                if (!_list.containsKey(id)) {
                    item = new DocumentIndexItemWithBalance(_coreDriver, id,
                            MasterDataType.BUSINESS_AREA);
                    _list.put(id, item);
                } else {
                    item = _list.get(id);
                }
                item.addDoc(head);
            }
        }
    }

    @Override
    public DocumentIndexItemWithBalance getIndexItem(MasterDataIdentity key) {
        return (DocumentIndexItemWithBalance) super.getIndexItem(key);
    }
}
