package com.jasonzqshen.familyAccounting;

import java.util.ArrayList;

import com.jasonzqshen.familyAccounting.data.DataCore;
import com.jasonzqshen.familyAccounting.entries.CustomerEntryActivity;
import com.jasonzqshen.familyAccounting.entries.EntryActivityBase;
import com.jasonzqshen.familyAccounting.entries.GLEntryActivity;
import com.jasonzqshen.familyAccounting.entries.VendorEntryActivity;
import com.jasonzqshen.familyAccounting.reports.DocumentsListActivity;
import com.jasonzqshen.familyAccounting.reports.GLAccountBalanceReportActivity;
import com.jasonzqshen.familyAccounting.utils.ActivityAction;
import com.jasonzqshen.familyAccounting.utils.CostDetailsAction;
import com.jasonzqshen.familyAccounting.widgets.MenuAdapter;
import com.jasonzqshen.familyAccounting.widgets.MenuAdapterItem;
import com.jasonzqshen.familyaccounting.core.document_entries.EntryTemplate;
import com.jasonzqshen.familyaccounting.core.document_entries.EntryTemplatesManagement;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class MainMenuActivity extends ListActivity {

    public final MenuAdapterItem CUSTOMIZEING_ENTRY_HEAD = new MenuAdapterItem(
            MenuAdapter.HEAD_TYPE, 0, R.string.menu_customizing_entry, null);

    private final MenuAdapterItem[] _MENU_ITEMS = new MenuAdapterItem[] {
            new MenuAdapterItem(MenuAdapter.HEAD_TYPE, 0, R.string.menu_entry,
                    null),
            new MenuAdapterItem(MenuAdapter.ITEM_TYPE, R.drawable.new_entry,
                    R.string.menu_vendor_entry, new ActivityAction(
                            VendorEntryActivity.class, this)),
            new MenuAdapterItem(MenuAdapter.ITEM_TYPE, R.drawable.new_entry,
                    R.string.menu_customer_entry, new ActivityAction(
                            CustomerEntryActivity.class, this)),
            new MenuAdapterItem(MenuAdapter.ITEM_TYPE, R.drawable.new_entry,
                    R.string.menu_gl_entry, new ActivityAction(
                            GLEntryActivity.class, this)),
            new MenuAdapterItem(MenuAdapter.ITEM_TYPE, R.drawable.new_entry,
                    R.string.menu_investment_entry, null),
            new MenuAdapterItem(MenuAdapter.ITEM_TYPE, R.drawable.new_entry,
                    R.string.menu_fixed_asset_entry, null),
            new MenuAdapterItem(MenuAdapter.HEAD_TYPE, 0, R.string.menu_report,
                    null),
            new MenuAdapterItem(MenuAdapter.ITEM_TYPE, R.drawable.chart,
                    R.string.menu_liquidity_report, null),
            new MenuAdapterItem(MenuAdapter.ITEM_TYPE, R.drawable.chart,
                    R.string.menu_balance_report, new ActivityAction(
                            GLAccountBalanceReportActivity.class, this)),
            new MenuAdapterItem(MenuAdapter.ITEM_TYPE, R.drawable.chart,
                    R.string.menu_profit_loss_report, null),
            new MenuAdapterItem(MenuAdapter.ITEM_TYPE, R.drawable.chart,
                    R.string.menu_month_outgoing_report, new CostDetailsAction(
                            this)),
            new MenuAdapterItem(MenuAdapter.ITEM_TYPE, R.drawable.reports,
                    R.string.menu_month_records_report, new ActivityAction(
                            DocumentsListActivity.class, this)),
            new MenuAdapterItem(MenuAdapter.HEAD_TYPE, 0,
                    R.string.menu_settings, null),
            new MenuAdapterItem(MenuAdapter.ITEM_TYPE, R.drawable.settings,
                    R.string.menu_advanced, null) };

    private MenuAdapter _adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

    }

    protected void onResume() {
        super.onResume();
        refersh();
    }

    /**
     * add template entry
     */
    public static void addTemplateEntry(ArrayList<MenuAdapterItem> items, Activity activity) {
        DataCore dataCore = DataCore.getInstance();
        if (dataCore.getCoreDriver().isInitialized() == false) {
            return;
        }

        EntryTemplatesManagement tempMgmt = dataCore.getTemplateManagement();
        ArrayList<EntryTemplate> templates = tempMgmt.getEntryTemplates();
        for (EntryTemplate t : templates) {
            ActivityAction action;
            switch (t.getEntryType()) {
            case EntryTemplate.CUSTOMER_ENTRY_TYPE:
                action = new ActivityAction(CustomerEntryActivity.class, activity);
                break;
            case EntryTemplate.VENDOR_ENTRY_TYPE:
                action = new ActivityAction(VendorEntryActivity.class, activity);
                break;
            case EntryTemplate.GL_ENTRY_TYPE:
                action = new ActivityAction(GLEntryActivity.class, activity);
                break;
            default:
                continue;
            }

            // add parameters
            action.ParamName = EntryActivityBase.PARAM_TEMP_ID;
            action.ParamValue = t.getIdentity();

            items.add(new MenuAdapterItem(MenuAdapter.ITEM_TYPE, 0,
                    t.getName(), action));
        }

    }

    /**
     * refresh
     */
    private void refersh() {
        ArrayList<MenuAdapterItem> items = new ArrayList<MenuAdapterItem>();
        // add customizing entry
        items.add(CUSTOMIZEING_ENTRY_HEAD);
        addTemplateEntry(items, this);

        for (MenuAdapterItem t : _MENU_ITEMS) {
            items.add(t);
        }
        _adapter = new MenuAdapter(this, items);
        setListAdapter(_adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        MenuAdapterItem item = (MenuAdapterItem) _adapter.getItem(position);
        if (item.Action == null) {
            showDialog(R.id.dialog_not_implement_alert);
            return;
        }
        item.Action.execute();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
        case R.id.dialog_not_implement_alert:
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(this.getString(R.string.message_not_implement))
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int id) {
                                    dialog.dismiss();
                                }
                            });
            dialog = builder.create();
            break;
        default:
            dialog = null;
        }
        return dialog;
    }
}
