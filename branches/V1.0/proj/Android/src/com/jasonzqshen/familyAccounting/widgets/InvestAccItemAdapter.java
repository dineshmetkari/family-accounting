package com.jasonzqshen.familyAccounting.widgets;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.jasonzqshen.familyAccounting.R;
import com.jasonzqshen.familyaccounting.core.investment.InvestmentItem;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class InvestAccItemAdapter extends BaseAdapter {
    private final static int VIEW_TYPE_COUNT = 2;

    private final static int OPEN_ITEM_TYPE = 0;

    private final static int CLOSE_ITEM_TYPE = 1;

    private final Activity _activity;

    private final LayoutInflater _layoutInflater;

    private final ArrayList<InvestmentItem> _list;

    public InvestAccItemAdapter(Activity activity,
            ArrayList<InvestmentItem> list) {
        _activity = activity;
        _layoutInflater = LayoutInflater.from(_activity);
        _list = list;
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
    public View getView(int position, View convertView, ViewGroup group) {
        InvestmentItem item = _list.get(position);
        View view = convertView;
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");

        if (item.isClosed()) {
            if (view == null) {
                view = _layoutInflater.inflate(
                        R.layout.investmentaccount_closeitem_item, null);
            }

            TextView startdate = (TextView) view.findViewById(R.id.startdate);
            startdate.setText(format.format(item.getStartDate()));

            TextView enddate = (TextView) view.findViewById(R.id.enddate);
            enddate.setText(format.format(item.getEndDate()));

            TextView amount = (TextView) view.findViewById(R.id.amountValue);
            amount.setText(item.getAmount().toString());

            TextView revAmount = (TextView) view
                    .findViewById(R.id.revAmountValue);
            revAmount.setText(item.getRevAmount().toString());
        } else {
            if (view == null) {
                view = _layoutInflater.inflate(
                        R.layout.investmentaccount_openitem_item, null);
            }

            TextView startdate = (TextView) view.findViewById(R.id.startdate);
            startdate.setText(format.format(item.getStartDate()));

            TextView enddate = (TextView) view.findViewById(R.id.enddate);
            enddate.setText(format.format(item.getDueDate()));

            TextView amount = (TextView) view.findViewById(R.id.amountValue);
            amount.setText(item.getAmount().toString());
        }
        return view;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        if (_list.get(position).isClosed()) {
            return CLOSE_ITEM_TYPE;
        }
        return OPEN_ITEM_TYPE;
    }

}
