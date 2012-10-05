package com.jasonzqshen.familyAccounting;

import com.jasonzqshen.familyAccounting.widgets.AccountReportAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.widget.ListView;

public class CostItemsDialogBuilder {

    /**
     * build cost details dialog
     * 
     * @param activity
     * @param adapter
     * @return
     */
    public static Dialog buildCostItemsDialog(Activity activity,
            AccountReportAdapter adapter) {

        return new AlertDialog.Builder(activity)
                .setTitle(R.string.main_cost_details).setAdapter(adapter, null)
                .create();
    }

    /**
     * set cost details
     * 
     * @param dialog
     * @param activity
     */
    public static void setCostDetailsDialog(AlertDialog dialog,
            AccountReportAdapter adapter) {
        ListView listView = dialog.getListView();
        listView.setAdapter(adapter);
    }
}
