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

public class RevAccountsSettingActivity extends AbstractAccountsSettingActivity {

    @Override
    protected String getMasterdataTitle() {
        return this.getString(R.string.masterdata_rev_accounts_title);
    }

    @Override
    protected Class<?> newActivity() {
        return RevAccountNewActivity.class;
    }

    @Override
    protected Class<?> editActivity() {
        return RevAccountEditActivity.class;
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
                .getGLAccountsBasedGroup(GLAccountGroup.SALARY);
        for (MasterDataIdentity_GLAccount id : ids) {
            ret.add(mdMgmt.getMasterData(id, MasterDataType.GL_ACCOUNT));
        }

        // add cost accident
        ids = mdMgmt.getGLAccountsBasedGroup(GLAccountGroup.INVEST_REVENUE);
        for (MasterDataIdentity_GLAccount id : ids) {
            ret.add(mdMgmt.getMasterData(id, MasterDataType.GL_ACCOUNT));
        }

        return ret;
    }
}
