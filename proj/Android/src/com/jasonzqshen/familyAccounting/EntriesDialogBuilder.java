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

public class EntriesDialogBuilder {

    public static Dialog BuildEntriesDialog(Activity activity) {
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

        EntryClickListener listener = new EntryClickListener(entryListAdapter);
        return new AlertDialog.Builder(activity)
                .setTitle(R.string.main_entries)
                .setAdapter(entryListAdapter, listener).create();
    }
}
