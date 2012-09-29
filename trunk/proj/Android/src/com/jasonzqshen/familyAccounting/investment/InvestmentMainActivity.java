package com.jasonzqshen.familyAccounting.investment;

import com.jasonzqshen.familyAccounting.R;
import com.jasonzqshen.familyAccounting.data.DataCore;
import com.jasonzqshen.familyAccounting.widgets.InvestAccAdapter;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.investment.InvestmentManagement;

import android.app.ListActivity;
import android.os.Bundle;

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
}
