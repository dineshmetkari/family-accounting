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
import com.jasonzqshen.familyaccounting.core.masterdata.GLAccountMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataBase;
import com.jasonzqshen.familyaccounting.core.utils.GLAccountGroup;

public abstract class AbstractAccountsSettingActivity extends
        MasterDataSettingActivity {

    @Override
    protected abstract String getMasterdataTitle();

    @Override
    protected abstract Class<?> newActivity();

    @Override
    protected abstract Class<?> editActivity();

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
                            R.layout.masterdata_account_item, null);
                }
                DataCore dataCore = DataCore.getInstance();
                CoreDriver coreDriver = dataCore.getCoreDriver();
                if (coreDriver.isInitialized() == false) {
                    return view;
                }
                GLAccountMasterData account = (GLAccountMasterData) _masterDatas
                        .get(position);

                TextView accountName = (TextView) view
                        .findViewById(R.id.accountName);
                accountName.setText(account.getDescp());

                TextView accountType = (TextView) view
                        .findViewById(R.id.accountTypeName);
                GLAccountGroup group = account.getGroup();
                accountType.setText(getAccountGroupName(group));

                return view;
            }
        };
    }

    @Override
    protected abstract ArrayList<MasterDataBase> getDataSet();

    /**
     * get account group name
     * 
     * @param group
     * @return
     */
    public static int getAccountGroupName(GLAccountGroup group) {
        if (group == GLAccountGroup.CASH) {
            return R.string.masterdata_cash;
        } else if (group == GLAccountGroup.BANK_ACCOUNT) {
            return R.string.masterdata_bank_account;
        } else if (group == GLAccountGroup.INVESTMENT) {
            return R.string.masterdata_investment;
        } else if (group == GLAccountGroup.PREPAID) {
            return R.string.masterdata_prepaid;
        } else if (group == GLAccountGroup.ASSETS) {
            return R.string.masterdata_assets;
        } else if (group == GLAccountGroup.SHORT_LIABILITIES) {
            return R.string.masterdata_short_liabilities;
        } else if (group == GLAccountGroup.LONG_LIABILITIES) {
            return R.string.masterdata_long_liabilities;
        } else if (group == GLAccountGroup.COST_ACCI) {
            return R.string.masterdata_cost_accidently;
        } else if (group == GLAccountGroup.COST_PURE) {
            return R.string.masterdata_cost_purpose;
        } else if (group == GLAccountGroup.SALARY) {
            return R.string.masterdata_salary;
        } else if (group == GLAccountGroup.INVEST_REVENUE) {
            return R.string.masterdata_invest_revenue;
        }

        return 0;
    }

}
