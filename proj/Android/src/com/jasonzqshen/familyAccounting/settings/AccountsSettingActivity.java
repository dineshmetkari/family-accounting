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

public class AccountsSettingActivity extends AbstractAccountsSettingActivity {

    @Override
    protected String getMasterdataTitle() {
        return this.getString(R.string.masterdata_accounts_title);
    }

    @Override
    protected Class<?> newActivity() {
        return AccountNewActivity.class;
    }

    @Override
    protected Class<?> editActivity() {
        return AccountEditActivity.class;
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

        // add cash
        MasterDataIdentity_GLAccount[] ids = mdMgmt
                .getGLAccountsBasedGroup(GLAccountGroup.CASH);
        for (MasterDataIdentity_GLAccount id : ids) {
            ret.add(mdMgmt.getMasterData(id, MasterDataType.GL_ACCOUNT));
        }

        // add bank account
        ids = mdMgmt.getGLAccountsBasedGroup(GLAccountGroup.BANK_ACCOUNT);
        for (MasterDataIdentity_GLAccount id : ids) {
            ret.add(mdMgmt.getMasterData(id, MasterDataType.GL_ACCOUNT));
        }

        // add investment
        ids = mdMgmt.getGLAccountsBasedGroup(GLAccountGroup.INVESTMENT);
        for (MasterDataIdentity_GLAccount id : ids) {
            ret.add(mdMgmt.getMasterData(id, MasterDataType.GL_ACCOUNT));
        }

        // add pre-paid
        ids = mdMgmt.getGLAccountsBasedGroup(GLAccountGroup.PREPAID);
        for (MasterDataIdentity_GLAccount id : ids) {
            ret.add(mdMgmt.getMasterData(id, MasterDataType.GL_ACCOUNT));
        }

        // add assets
        ids = mdMgmt.getGLAccountsBasedGroup(GLAccountGroup.ASSETS);
        for (MasterDataIdentity_GLAccount id : ids) {
            ret.add(mdMgmt.getMasterData(id, MasterDataType.GL_ACCOUNT));
        }

        // add SHORT_LIABILITIES
        ids = mdMgmt.getGLAccountsBasedGroup(GLAccountGroup.SHORT_LIABILITIES);
        for (MasterDataIdentity_GLAccount id : ids) {
            ret.add(mdMgmt.getMasterData(id, MasterDataType.GL_ACCOUNT));
        }

        // add Long_LIABILITIES
        ids = mdMgmt.getGLAccountsBasedGroup(GLAccountGroup.LONG_LIABILITIES);
        for (MasterDataIdentity_GLAccount id : ids) {
            ret.add(mdMgmt.getMasterData(id, MasterDataType.GL_ACCOUNT));
        }
        return ret;
    }
}
