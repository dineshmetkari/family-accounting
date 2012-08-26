package com.jasonzqshen.familyAccounting;

import java.util.ArrayList;

import com.jasonzqshen.familyAccounting.reports.DocListParam;
import com.jasonzqshen.familyAccounting.reports.DocListParamItem;
import com.jasonzqshen.familyAccounting.reports.DocumentsListActivity;
import com.jasonzqshen.familyAccounting.reports.GLAccountBalanceReportActivity;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.transaction.MonthIdentity;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivityHandler {
    private final MainActivity _activity;

    /**
     * package internal
     * 
     * @param activity
     */
    MainActivityHandler(MainActivity activity) {
        _activity = activity;
    }

    public final OnClickListener ASSET_FIELD_CLICK_LISTENER = new OnClickListener() {
        @Override
        public void onClick(View v) {
            navigate2AssetReports();
        }
    };

    /**
     * navigate to asset reports
     */
    private void navigate2AssetReports() {
        Intent assetReportIntent = new Intent(_activity,
                GLAccountBalanceReportActivity.class);
        _activity.startActivity(assetReportIntent);
    }

    /**
     * navigate to main menu button
     */
    public void navigate2MainMenu() {
        Intent assetReportIntent = new Intent(_activity, MainMenuActivity.class);
        _activity.startActivity(assetReportIntent);
    }

    /**
     * navigate to main menu button
     */
    public void navigate2BalanceReport() {
        Intent assetReportIntent = new Intent(_activity,
                GLAccountBalanceReportActivity.class);
        _activity.startActivity(assetReportIntent);
    }

    /**
     * navigate to details of cost
     * 
     * @param costItem
     */
    public void navigate2CostDetails(MonthIdentity monthId,
            MasterDataIdentity_GLAccount glAccount) {

        ArrayList<Object> list = new ArrayList<Object>();
        list.add(glAccount);

        // add parameters
        ArrayList<DocListParamItem> items = new ArrayList<DocListParamItem>();
        items.add(new DocListParamItem(list, DocListParam.ACCOUNT_CATEGORY));

        DocListParam param = new DocListParam(monthId, items);
        Intent docListIntent = new Intent(_activity,
                DocumentsListActivity.class);
        docListIntent.putExtra(DocListParam.PARAM_NAME, param);
        _activity.startActivity(docListIntent);
    }

    /**
     * navigate to details of cost
     * 
     * @param costItem
     */
    public void navigate2CostDetails(MonthIdentity monthId,
            ArrayList<MasterDataIdentity_GLAccount> glAccounts) {
        ArrayList<Object> list = new ArrayList<Object>(glAccounts);

        // add parameters
        ArrayList<DocListParamItem> items = new ArrayList<DocListParamItem>();
        items.add(new DocListParamItem(list, DocListParam.ACCOUNT_CATEGORY));

        DocListParam param = new DocListParam(monthId, items);
        Intent docListIntent = new Intent(_activity,
                DocumentsListActivity.class);
        docListIntent.putExtra(DocListParam.PARAM_NAME, param);
        _activity.startActivity(docListIntent);
    }

}
