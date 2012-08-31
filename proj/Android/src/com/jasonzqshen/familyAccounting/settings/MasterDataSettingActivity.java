package com.jasonzqshen.familyAccounting.settings;

import java.util.ArrayList;

import com.jasonzqshen.familyAccounting.R;
import com.jasonzqshen.familyAccounting.utils.ActivityAction;
import com.jasonzqshen.familyAccounting.widgets.IMasterDataAdapterDrawable;
import com.jasonzqshen.familyAccounting.widgets.MasterDataAdapter;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataBase;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public abstract class MasterDataSettingActivity extends ListActivity {
    public final static String PARAM_MASTERDATA_ID = "MASTERDATA_ID";

    protected MasterDataAdapter _adapter;

    protected IMasterDataAdapterDrawable _drawable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.masterdata_setting);

        // set title
        TextView titleView = (TextView) this.findViewById(R.id.title);
        titleView.setText(getMasterdataTitle());

        // save button
        ImageButton newBtn = (ImageButton) this.findViewById(R.id.new_icon);
        newBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityAction action = new ActivityAction(newActivity(),
                        MasterDataSettingActivity.this);
                action.execute();
            }
        });

        _drawable = getDrawable();
    }

    @Override
    protected void onResume() {
        super.onResume();

        _adapter = new MasterDataAdapter(this, getDataSet(), _drawable);
        this.setListAdapter(_adapter);
    }

    @Override
    protected void onListItemClick(ListView list, View view, int position,
            long id) {
        MasterDataBase data = (MasterDataBase) _adapter.getItem(position);
        ActivityAction action = new ActivityAction(editActivity(), this);
        action.ParamName = PARAM_MASTERDATA_ID;
        action.ParamValue = data.getIdentity();

        action.execute();
    }

    protected abstract String getMasterdataTitle();

    protected abstract Class<?> newActivity();

    protected abstract Class<?> editActivity();

    protected abstract IMasterDataAdapterDrawable getDrawable();

    protected abstract ArrayList<MasterDataBase> getDataSet();
}
