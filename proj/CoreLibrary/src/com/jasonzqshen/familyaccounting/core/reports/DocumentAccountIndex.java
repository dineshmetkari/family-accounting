package com.jasonzqshen.familyaccounting.core.reports;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.masterdata.GLAccountMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;
import com.jasonzqshen.familyaccounting.core.transaction.ItemEntity;
import com.jasonzqshen.familyaccounting.core.utils.CreditDebitIndicator;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;
import com.jasonzqshen.familyaccounting.core.utils.GLAccountGroup;

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

            /**
             * if the document is month close document and the gl account is
             * cost account, then skip
             */
            GLAccountMasterData glAccount = (GLAccountMasterData) _mdMgmt
                    .getMasterData(id, MasterDataType.GL_ACCOUNT);
            if (isSkip(head, glAccount)) {
                continue;
            }

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

    /**
     * if the document is month close document and the gl account is cost
     * account, then skip
     * 
     * @param head
     * @param id
     * @return
     */
    public static boolean isSkip(HeadEntity head, GLAccountMasterData glAccount) {
        if (head.isClose()) {
            for (GLAccountGroup group : GLAccountGroup.COST_GROUP) {
                if (glAccount.getGroup() == group) {
                    return true;
                }
            }
            for (GLAccountGroup group : GLAccountGroup.REVENUE_GROUP) {
                if (glAccount.getGroup() == group) {
                    return true;
                }
            }
        }

        return false;
    }
}
