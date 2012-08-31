package com.jasonzqshen.familyAccounting.widgets;

import java.util.ArrayList;

import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataBase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface IMasterDataAdapterDrawable {
    View getView(LayoutInflater layoutInflater, ArrayList<MasterDataBase> masterDatas, int position,
            View convertView, ViewGroup parent);
}
