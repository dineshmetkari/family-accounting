package com.jasonzqshen.familyAccounting.widgets;

import java.util.ArrayList;

import com.jasonzqshen.familyAccounting.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AccountReportAdapter extends BaseAdapter {
	private final Context _context;
	private final ArrayList<AccountReportAdapterItem> _list;
	private final LayoutInflater _layoutInflater;

	public AccountReportAdapter(Context context,
			ArrayList<AccountReportAdapterItem> list) {
		_context = context;
		_list = list;
		_layoutInflater = LayoutInflater.from(_context);

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
	public int getItemViewType(int position) {
		return _list.get(position).Type;
	}

	@Override
	public int getViewTypeCount() {
		return AccountReportAdapterItem.VIEW_TYPE_COUNT;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		AccountReportAdapterItem item = _list.get(position);
		switch (item.Type) {
		case AccountReportAdapterItem.HEAD_VIEW:
			if (null == view) {
				view = _layoutInflater.inflate(
						R.layout.account_report_item_top, null);
			}
			fillHeadView(view, item);
			break;
		case AccountReportAdapterItem.ITEM_VIEW:
			if (null == view) {
				view = _layoutInflater.inflate(
						R.layout.account_report_item_center, null);
			}
			fillItemView(view, item);
			break;
		}

		return view;
	}

	/**
	 * fill item view
	 * 
	 * @param view
	 */
	private void fillItemView(View view, AccountReportAdapterItem item) {

		TextView descp = (TextView) view.findViewById(R.id.descp);
		descp.setText(item.Descp);
		TextView value = (TextView) view.findViewById(R.id.amount);
		value.setText(item.Amount.toString());
	}

	/**
	 * fill item view
	 * 
	 * @param view
	 */
	private void fillHeadView(View view, AccountReportAdapterItem item) {

		TextView descp = (TextView) view.findViewById(R.id.descp);
		descp.setText(item.Descp);
		TextView value = (TextView) view.findViewById(R.id.amount);
		value.setText(item.Amount.toString());
	}

}
