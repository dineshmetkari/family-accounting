package com.jasonzqshen.familyAccounting.settings;

import java.util.ArrayList;

import com.jasonzqshen.familyAccounting.R;
import com.jasonzqshen.familyAccounting.data.DataCore;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataBase;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;
import com.jasonzqshen.familyaccounting.core.utils.GLAccountGroup;

public class CostAccountsSettingActivity extends
        AbstractAccountsSettingActivity {

    @Override
    protected String getMasterdataTitle() {
        return this.getString(R.string.masterdata_cost_accounts_title);
    }

    @Override
    protected Class<?> newActivity() {
        return CostAccountNewActivity.class;
    }

    @Override
    protected Class<?> editActivity() {
        return CostAccountEditActivity.class;
    }

    @Override
    protected ArrayList<MasterDataBase> getDataSet() {
        DataCore dataCore = DataCore.getInstance();
        CoreDriver coreDriver = dataCore.getCoreDriver();
        if (coreDriver.isInitialized() == false) {
            return new ArrayList<MasterDataBase>();
        }

        MasterDataManagement mdMgmt = coreDriver.getMasterDataManagement();

        ArrayList<MasterDataBase> ret = new ArrayList<MasterDataBase>();

        // add cost for purpose
        MasterDataIdentity_GLAccount[] ids = mdMgmt
                .getGLAccountsBasedGroup(GLAccountGroup.COST_PURE);
        for (MasterDataIdentity_GLAccount id : ids) {
            ret.add(mdMgmt.getMasterData(id, MasterDataType.GL_ACCOUNT));
        }

        // add cost accident
        ids = mdMgmt.getGLAccountsBasedGroup(GLAccountGroup.COST_ACCI);
        for (MasterDataIdentity_GLAccount id : ids) {
            ret.add(mdMgmt.getMasterData(id, MasterDataType.GL_ACCOUNT));
        }

        return ret;
    }
}
