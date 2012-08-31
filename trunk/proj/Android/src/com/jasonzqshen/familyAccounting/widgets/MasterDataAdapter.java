package com.jasonzqshen.familyAccounting.widgets;

import java.util.ArrayList;

import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataBase;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MasterDataAdapter extends BaseAdapter {

    protected final ArrayList<MasterDataBase> _masterDatas;

    protected final IMasterDataAdapterDrawable _drawable;

    protected final LayoutInflater _layoutInflater;

    protected final Activity _activity;

    /**
     * constructor
     * 
     * @param masterDatas
     */
    public MasterDataAdapter(Activity activity,
            ArrayList<MasterDataBase> masterDatas,
            IMasterDataAdapterDrawable drawable) {
        _activity = activity;
        _masterDatas = masterDatas;
        _drawable = drawable;
        _layoutInflater = LayoutInflater.from(_activity);
    }

    @Override
    public int getCount() {
        return _masterDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return _masterDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return _drawable.getView(_layoutInflater, _masterDatas, position,
                convertView, parent);
    }

}
