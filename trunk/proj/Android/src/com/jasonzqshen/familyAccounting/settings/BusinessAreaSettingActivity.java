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
import com.jasonzqshen.familyaccounting.core.masterdata.BusinessAreaMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataBase;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataFactoryBase;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;
import com.jasonzqshen.familyaccounting.core.utils.CriticalLevel;

/**
 * 
 * @author jasonzqshen@gmail.com
 * 
 */
public class BusinessAreaSettingActivity extends MasterDataSettingActivity {

    @Override
    protected String getMasterdataTitle() {
        return this.getString(R.string.masterdata_area_title);
    }

    @Override
    protected Class<?> newActivity() {
        return BusinessAreaNewActivity.class;
    }

    @Override
    protected Class<?> editActivity() {
        return BusinessAreaEditActivity.class;
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
                            R.layout.masterdata_business_area_item, null);
                }

                BusinessAreaMasterData area = (BusinessAreaMasterData) _masterDatas
                        .get(position);

                TextView descp = (TextView) view.findViewById(R.id.descp);
                descp.setText(area.getDescp());

                TextView id = (TextView) view.findViewById(R.id.areaId);
                id.setText(area.getIdentity().toString());

                TextView priority = (TextView) view.findViewById(R.id.priority);
                priority.setText(getPriorityString(area.getCriticalLevel()));

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

        // add business area
        MasterDataFactoryBase factory = mdMgmt
                .getMasterDataFactory(MasterDataType.BUSINESS_AREA);
        ArrayList<MasterDataBase> ret = new ArrayList<MasterDataBase>();
        MasterDataBase[] entities = factory.getAllEntities();

        for (MasterDataBase entity : entities) {
            ret.add(entity);
        }

        return ret;
    }

    /**
     * get priority string resource identity
     * 
     * @param level
     * @return
     */
    public static int getPriorityString(CriticalLevel level) {
        if (level == CriticalLevel.HIGH) {
            return R.string.masterdata_priority_high;
        } else if (level == CriticalLevel.MEDIUM) {
            return R.string.masterdata_priority_medium;
        } else if (level == CriticalLevel.LOW) {
            return R.string.masterdata_priority_low;
        }

        return 0;
    }
}
