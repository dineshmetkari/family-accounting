package com.jasonzqshen.familyAccounting.widgets;

import java.util.ArrayList;

import com.jasonzqshen.familyAccounting.R;
import com.jasonzqshen.familyaccounting.core.investment.InvestmentAccount;

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
        InvestmentAccount investAcc = _list.get(position);

        TextView textView = (TextView) view.findViewById(R.id.descp);
        textView.setText(investAcc.getName());
        TextView amountView = (TextView) view.findViewById(R.id.amountValue);
        amountView.setText(investAcc.getTotalAmount().toString());
        TextView revAmountView = (TextView) view
                .findViewById(R.id.revAmountValue);
        revAmountView.setText(investAcc.getRevAmount().toString());
        TextView accountView = (TextView) view.findViewById(R.id.account);
        accountView.setText(investAcc.getAccount().toString());
        TextView revAccountView = (TextView) view.findViewById(R.id.revAccount);
        revAccountView.setText(investAcc.getRevAccount().toString());

        return view;

    }

}
