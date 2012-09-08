package com.jasonzqshen.familyAccounting;

import java.util.ArrayList;

import com.jasonzqshen.familyAccounting.entries.CustomerEntryActivity;
import com.jasonzqshen.familyAccounting.entries.GLEntryActivity;
import com.jasonzqshen.familyAccounting.entries.VendorEntryActivity;
import com.jasonzqshen.familyAccounting.utils.ActivityAction;
import com.jasonzqshen.familyAccounting.widgets.MenuAdapter;
import com.jasonzqshen.familyAccounting.widgets.MenuAdapterItem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.widget.ListView;

public class EntriesDialogBuilder {

    /**
     * create new entry dialog
     * 
     * @param activity
     * @return
     */
    public static Dialog buildEntriesDialog(Activity activity) {
        // set entries list
        ArrayList<MenuAdapterItem> list = new ArrayList<MenuAdapterItem>();

        list.add(new MenuAdapterItem(MenuAdapter.HEAD_TYPE, 0,
                R.string.menu_customizing_entry, null));
        MainMenuActivity.addTemplateEntry(list, activity);

        list.add(new MenuAdapterItem(MenuAdapter.HEAD_TYPE, 0,
                R.string.menu_entry, null));
        list.add(new MenuAdapterItem(MenuAdapter.ITEM_TYPE,
                R.drawable.new_entry, R.string.menu_vendor_entry,
                new ActivityAction(VendorEntryActivity.class, activity)));
        list.add(new MenuAdapterItem(MenuAdapter.ITEM_TYPE,
                R.drawable.new_entry, R.string.menu_customer_entry,
                new ActivityAction(CustomerEntryActivity.class, activity)));
        list.add(new MenuAdapterItem(MenuAdapter.ITEM_TYPE,
                R.drawable.new_entry, R.string.menu_gl_entry,
                new ActivityAction(GLEntryActivity.class, activity)));

        MenuAdapter entryListAdapter = new MenuAdapter(activity, list);

        return new AlertDialog.Builder(activity)
                .setTitle(R.string.main_entries)
                .setAdapter(entryListAdapter, null).create();
    }

    /**
     * set data on entry dialog
     * 
     * @param dialog
     */
    public static void setDataEntriesDialog(AlertDialog dialog,
            Activity activity) {
        ListView listView = dialog.getListView();
        ArrayList<MenuAdapterItem> list = new ArrayList<MenuAdapterItem>();

        list.add(new MenuAdapterItem(MenuAdapter.HEAD_TYPE, 0,
                R.string.menu_customizing_entry, null));
        MainMenuActivity.addTemplateEntry(list, activity);

        list.add(new MenuAdapterItem(MenuAdapter.HEAD_TYPE, 0,
                R.string.menu_entry, null));
        list.add(new MenuAdapterItem(MenuAdapter.ITEM_TYPE,
                R.drawable.outgoing, R.string.menu_vendor_entry,
                new ActivityAction(VendorEntryActivity.class, activity)));
        list.add(new MenuAdapterItem(MenuAdapter.ITEM_TYPE,
                R.drawable.incoming, R.string.menu_customer_entry,
                new ActivityAction(CustomerEntryActivity.class, activity)));
        list.add(new MenuAdapterItem(MenuAdapter.ITEM_TYPE,
                R.drawable.internal, R.string.menu_gl_entry,
                new ActivityAction(GLEntryActivity.class, activity)));

        MenuAdapter entryListAdapter = new MenuAdapter(activity, list);
        EntryClickListener listener = new EntryClickListener(entryListAdapter);

        listView.setAdapter(entryListAdapter);
        listView.setOnItemClickListener(listener);
    }
}
