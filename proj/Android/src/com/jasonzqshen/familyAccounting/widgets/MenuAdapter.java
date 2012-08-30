package com.jasonzqshen.familyAccounting.widgets;

import java.util.ArrayList;

import com.jasonzqshen.familyAccounting.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuAdapter extends BaseAdapter {
    public static final int TYPE_COUNT = 2;

    public static final int HEAD_TYPE = 0;

    public static final int ITEM_TYPE = 1;

    private final ArrayList<MenuAdapterItem> _items;

    private final LayoutInflater _layoutInflater;

    private final Context _context;

    public MenuAdapter(Context context, ArrayList<MenuAdapterItem> items) {
        _items = items;
        _context = context;
        _layoutInflater = LayoutInflater.from(_context);
    }

    @Override
    public int getCount() {
        return _items.size();
    }

    @Override
    public Object getItem(int position) {
        return _items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return _items.get(position).ItemType;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        int type = getItemViewType(position);
        MenuAdapterItem item = _items.get(position);

        switch (type) {
        case HEAD_TYPE:
            if (null == view) {
                view = _layoutInflater.inflate(R.layout.menu_head, null);
            }
            fillHeadView(view, item);
            break;

        case ITEM_TYPE:
            if (null == view) {
                view = _layoutInflater.inflate(R.layout.menu_item, null);
            }
            fillItemView(view, item);
            break;
        }

        return view;
    }

    /**
     * fill top menu item
     * 
     * @param view
     * @param item
     */
    private void fillHeadView(View view, MenuAdapterItem item) {
        TextView textView = (TextView) view.findViewById(R.id.menu_text);
        textView.setText(_context.getString(item.TextID));
    }

    /**
     * fill item view
     * 
     * @param view
     * @param item
     */
    private void fillItemView(View view, MenuAdapterItem item) {
        TextView textView = (TextView) view.findViewById(R.id.menu_text);
        if (item.Text == null) {
            textView.setText(_context.getString(item.TextID));
        } else {
            textView.setText(item.Text);
        }

        ImageView image = (ImageView) view.findViewById(R.id.menu_image);
        image.setImageResource(item.ImageID);
    }
}
