package com.jasonzqshen.familyAccounting;


import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.jasonzqshen.familyAccounting.widgets.MenuAdapter;
import com.jasonzqshen.familyAccounting.widgets.MenuAdapterItem;

public class EntryClickListener implements OnItemClickListener {
    private final MenuAdapter _entryListAdapter;

    public EntryClickListener(MenuAdapter entryListAdapter) {
        _entryListAdapter = entryListAdapter;
    }

    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int position,
            long id) {
        MenuAdapterItem menuItem = (MenuAdapterItem) _entryListAdapter
                .getItem(position);

        if (menuItem.ItemType == MenuAdapterItem.HEAD_TYPE) {
            // showDialog(R.id.dialog_entries);
        } else {
            if (menuItem.Action != null) {
                menuItem.Action.execute();
            }
        }

    }

}