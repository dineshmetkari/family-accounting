package com.jasonzqshen.familyAccounting;

import android.content.DialogInterface;

import com.jasonzqshen.familyAccounting.widgets.MenuAdapter;
import com.jasonzqshen.familyAccounting.widgets.MenuAdapterItem;

public class EntryClickListener implements DialogInterface.OnClickListener {
    private final MenuAdapter _entryListAdapter;

    public EntryClickListener(MenuAdapter entryListAdapter) {
        _entryListAdapter = entryListAdapter;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        MenuAdapterItem menuItem = (MenuAdapterItem) _entryListAdapter
                .getItem(which);

        if (menuItem.ItemType == MenuAdapterItem.HEAD_TYPE) {
            // showDialog(R.id.dialog_entries);
        } else {
            if (menuItem.Action != null) {
                menuItem.Action.execute();
            }
        }
    }

}