package com.jasonzqshen.familyAccounting.reports;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;

import com.jasonzqshen.familyAccounting.data.DataCore;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.masterdata.GLAccountMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.transaction.MonthIdentity;

public class DocumentsListNavigation {
    /**
     * navigate to documents list
     * 
     * @param costItem
     */
    public static void navigate2DocList(Context context, MonthIdentity monthId,
            MasterDataIdentity_GLAccount glAccount) {

        ArrayList<Object> list = new ArrayList<Object>();
        list.add(glAccount);

        // add parameters
        ArrayList<DocListParamItem> items = new ArrayList<DocListParamItem>();
        items.add(new DocListParamItem(list, DocListParam.ACCOUNT_CATEGORY));

        DocListParam param = new DocListParam(monthId, items);
        Intent docListIntent = new Intent(context, DocumentsListActivity.class);
        docListIntent.putExtra(DocListParam.PARAM_NAME, param);
        context.startActivity(docListIntent);
    }

    /**
     * navigate to documents list
     * 
     * @param costItem
     */
    public static void navigate2DocList(Context context, MonthIdentity monthId,
            ArrayList<MasterDataIdentity_GLAccount> glAccounts) {
        ArrayList<Object> list = new ArrayList<Object>(glAccounts);

        // add parameters
        ArrayList<DocListParamItem> items = new ArrayList<DocListParamItem>();
        items.add(new DocListParamItem(list, DocListParam.ACCOUNT_CATEGORY));

        DocListParam param = new DocListParam(monthId, items);
        Intent docListIntent = new Intent(context, DocumentsListActivity.class);
        docListIntent.putExtra(DocListParam.PARAM_NAME, param);
        context.startActivity(docListIntent);
    }

    /**
     * navigate to the details of cost
     * 
     * @param context
     */
    public static void navigate2CostDetails(Context context) {
        // click on cost account list
        // negative to show the details of cost
        CoreDriver coreDriver = DataCore.getInstance().getCoreDriver();
        if (coreDriver.isInitialized() == false) {
            return;
        }

        MasterDataManagement mdMgmt = coreDriver.getMasterDataManagement();
        GLAccountMasterData[] costAccounts = mdMgmt.getCostAccounts();
        ArrayList<MasterDataIdentity_GLAccount> list = new ArrayList<MasterDataIdentity_GLAccount>();
        for (GLAccountMasterData glAccount : costAccounts) {
            list.add(glAccount.getIdentity());
        }

        MonthIdentity monthId = coreDriver.getCurCalendarMonthId();
        navigate2DocList(context, monthId, list);
    }
}
