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
import com.jasonzqshen.familyaccounting.core.masterdata.BankAccountMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.BankKeyMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.GLAccountMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataBase;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;
import com.jasonzqshen.familyaccounting.core.utils.GLAccountGroup;

public class BankSettingActivity extends MasterDataSettingActivity {

    @Override
    protected String getMasterdataTitle() {
        return this.getString(R.string.masterdata_bank_title);
    }

    @Override
    protected Class<?> newActivity() {
        return BankAccountDetailsActivity.class;
    }

    @Override
    protected Class<?> editActivity() {
        return BankAccountDetailsActivity.class;
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
                            R.layout.masterdata_bank_item, null);
                }
                DataCore dataCore = DataCore.getInstance();
                CoreDriver coreDriver = dataCore.getCoreDriver();
                if (coreDriver.isInitialized() == false) {
                    return view;
                }

                MasterDataManagement mdMgmt = coreDriver
                        .getMasterDataManagement();

                GLAccountMasterData account = (GLAccountMasterData) _masterDatas
                        .get(position);
                BankAccountMasterData bankAcc = (BankAccountMasterData) mdMgmt
                        .getMasterData(account.getBankAccount(),
                                MasterDataType.BANK_ACCOUNT);
                BankKeyMasterData bankKey = (BankKeyMasterData) mdMgmt
                        .getMasterData(bankAcc.getBankKey(),
                                MasterDataType.BANK_KEY);

                TextView bankKeyField = (TextView) view
                        .findViewById(R.id.bankKey);
                bankKeyField.setText(bankKey.getDescp());

                TextView bankAccountField = (TextView) view
                        .findViewById(R.id.bankAccount);
                bankAccountField.setText(bankAcc.getDescp());

                TextView bankNumField = (TextView) view
                        .findViewById(R.id.bankAccountNum);
                bankNumField.setText(bankAcc.getBankAccountNumber().toString());

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
        MasterDataIdentity_GLAccount[] ids = mdMgmt
                .getGLAccountsBasedGroup(GLAccountGroup.BANK_ACCOUNT);
        ArrayList<MasterDataBase> ret = new ArrayList<MasterDataBase>();
        for (MasterDataIdentity_GLAccount id : ids) {
            ret.add(mdMgmt.getMasterData(id, MasterDataType.GL_ACCOUNT));
        }
        return ret;
    }
}
