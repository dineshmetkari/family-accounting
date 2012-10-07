package com.jasonzqshen.familyAccounting.widgets;

import java.util.ArrayList;

import com.jasonzqshen.familyAccounting.R;
import com.jasonzqshen.familyAccounting.data.DataCore;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.investment.InvestmentAccount;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataBase;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class InvestAccAdapter extends BaseAdapter {

    private final Activity _activity;

    private final LayoutInflater _layoutInflater;

    private final ArrayList<InvestmentAccount> _list;

    /**
     * construct
     * 
     * @param activity
     */
    public InvestAccAdapter(Activity activity, ArrayList<InvestmentAccount> list) {
        _activity = activity;
        _list = list;
        _layoutInflater = LayoutInflater.from(_activity);
    }

    @Override
    public int getCount() {
        return _list.size();
    }

    @Override
    public Object getItem(int position) {
        return _list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        if (view == null) {
            view = _layoutInflater.inflate(R.layout.investmentaccount_item,
                    null);
        }
        DataCore dataCore = DataCore.getInstance();
        CoreDriver coreDriver = dataCore.getCoreDriver();
        MasterDataManagement mdMgmt = coreDriver.getMasterDataManagement();

        InvestmentAccount investAcc = _list.get(position);

        TextView amountView = (TextView) view.findViewById(R.id.amountValue);
        amountView.setText(investAcc.getTotalAmount().toString());

        TextView revAmountView = (TextView) view
                .findViewById(R.id.revAmountValue);
        revAmountView.setText(investAcc.getRevAmount().toString());

        // investment account
        MasterDataBase investAccount = mdMgmt.getMasterData(
                investAcc.getAccount(), MasterDataType.GL_ACCOUNT);
        TextView accountView = (TextView) view.findViewById(R.id.account);
        accountView.setText(investAccount.getDescp());

        // revenue account
        MasterDataBase revAccount = mdMgmt.getMasterData(
                investAcc.getRevAccount(), MasterDataType.GL_ACCOUNT);
        TextView revAccountView = (TextView) view.findViewById(R.id.revAccount);
        revAccountView.setText(revAccount.getDescp());

        return view;

    }

}
