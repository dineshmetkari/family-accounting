package com.jasonzqshen.familyAccounting.investment;

import com.jasonzqshen.familyAccounting.R;
import com.jasonzqshen.familyAccounting.data.DataCore;
import com.jasonzqshen.familyAccounting.settings.MasterDataSettingActivity;
import com.jasonzqshen.familyAccounting.utils.ActivityAction;
import com.jasonzqshen.familyAccounting.widgets.InvestAccAdapter;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.investment.InvestmentAccount;
import com.jasonzqshen.familyaccounting.core.investment.InvestmentManagement;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class InvestmentMainActivity extends ListActivity {
    public static final String TAG = "InvestmentMainActivity";

    InvestAccAdapter _adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.investment_main);

    }

    @Override
    protected void onResume() {
        super.onResume();

        DataCore dataCore = DataCore.getInstance();
        CoreDriver coreDriver = dataCore.getCoreDriver();
        if (coreDriver.isInitialized() == false) {
            return;
        }

        InvestmentManagement investMgmt = dataCore.getInvestMgmt();
        _adapter = new InvestAccAdapter(this,
                investMgmt.getInvestmentAccounts());

        this.setListAdapter(_adapter);
    }

    @Override
    protected void onListItemClick(ListView list, View view, int position,
            long id) {
        InvestmentAccount data = (InvestmentAccount) _adapter.getItem(position);
        ActivityAction action = new ActivityAction(
                InvestmentAccountActivity.class, this);
        action.ParamName = MasterDataSettingActivity.PARAM_MASTERDATA_ID;
        action.ParamValue = data.getAccount();

        action.execute();
    }
}
