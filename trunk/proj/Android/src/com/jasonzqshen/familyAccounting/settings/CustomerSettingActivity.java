package com.jasonzqshen.familyAccounting.settings;

import java.util.ArrayList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jasonzqshen.familyAccounting.R;
import com.jasonzqshen.familyAccounting.data.DataCore;
import com.jasonzqshen.familyAccounting.widgets.IMasterDataAdapterDrawable;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.masterdata.CustomerMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataBase;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataFactoryBase;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;

/**
 * 
 * @author jasonzqshen@gmail.com
 * 
 */
public class CustomerSettingActivity extends MasterDataSettingActivity {

    @Override
    protected String getMasterdataTitle() {
        return this.getString(R.string.masterdata_customer_title);
    }

    @Override
    protected Class<?> newActivity() {
        return CustomerNewActivity.class;
    }

    @Override
    protected Class<?> editActivity() {
        return CustomerEditActivity.class;
    }

    @Override
    protected IMasterDataAdapterDrawable getDrawable() {
        return new IMasterDataAdapterDrawable() {
            @Override
            public View getView(LayoutInflater layoutInflater,
                    ArrayList<MasterDataBase> _masterDatas, int position,
                    View convertView, ViewGroup parent) {
                View view = convertView;
                if (view == null) {
                    view = layoutInflater.inflate(
                            R.layout.masterdata_vendor_customer_item, null);
                }

                CustomerMasterData area = (CustomerMasterData) _masterDatas
                        .get(position);

                TextView descp = (TextView) view.findViewById(R.id.descp);
                descp.setText(area.getDescp());

                TextView id = (TextView) view.findViewById(R.id.areaId);
                id.setText(area.getIdentity().toString());

                return view;
            }
        };
    }

    @Override
    protected ArrayList<MasterDataBase> getDataSet() {
        DataCore dataCore = DataCore.getInstance();
        CoreDriver coreDriver = dataCore.getCoreDriver();
        if (coreDriver.isInitialized() == false) {
            return new ArrayList<MasterDataBase>();
        }

        MasterDataManagement mdMgmt = coreDriver.getMasterDataManagement();

        // add customer
        MasterDataFactoryBase factory = mdMgmt
                .getMasterDataFactory(MasterDataType.CUSTOMER);
        ArrayList<MasterDataBase> ret = new ArrayList<MasterDataBase>();
        MasterDataBase[] entities = factory.getAllEntities();

        for (MasterDataBase entity : entities) {
            ret.add(entity);
        }

        return ret;
    }
}
