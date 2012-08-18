package com.jasonzqshen.familyAccounting.widgets;

import java.util.ArrayList;

import com.jasonzqshen.familyAccounting.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BalanceAccountAdapter extends BaseAdapter {
	private final Context _context;
	private final LayoutInflater _layoutInflater;
	private final ArrayList<BalanceAccountAdapterItem> _items;

	public BalanceAccountAdapter(Context context,
			ArrayList<BalanceAccountAdapterItem> items) {
		_context = context;
		_layoutInflater = LayoutInflater.from(_context);
		_items = items;
	}

	@Override
	public int getCount() {
		return _items.size();
	}

	@Override
	public int getViewTypeCount() {
		return BalanceAccountAdapterItem.VIEW_TYPE_COUNT;
	}

	@Override
	public int getItemViewType(int position) {
		return _items.get(position).Type;
	}

	@Override
	public Object getItem(int position) {
		return _items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		int type = getItemViewType(position);
		BalanceAccountAdapterItem item = (BalanceAccountAdapterItem) getItem(position);

		switch (type) {
		case BalanceAccountAdapterItem.VIEW_TYPE_TOP:
			if (null == view) {
				view = _layoutInflater.inflate(
						R.layout.balance_report_item_top, null);
			}
			fillTopView(view, item);
			break;

		case BalanceAccountAdapterItem.VIEW_TYPE_CENTER:
			if (null == view) {
				view = _layoutInflater.inflate(
						R.layout.balance_report_item_center, null);
			}
			fillCenterView(view, item);
			break;

		case BalanceAccountAdapterItem.VIEW_TYPE_BOTTOM:
			if (null == view) {
				view = _layoutInflater.inflate(
						R.layout.balance_report_item_bottom, null);
			}
			break;

		}

		return view;
	}

	/**
	 * fill top view
	 * 
	 * @param view
	 * @param item
	 */
	private void fillTopView(View view, BalanceAccountAdapterItem item) {
		TextView descpView = (TextView) view
				.findViewById(R.id.balance_report_descp);
		descpView.setText(item.Descp);
	}

	/**
	 * fill center view
	 * 
	 * @param view
	 * @param item
	 */
	private void fillCenterView(View view, BalanceAccountAdapterItem item) {
		TextView descpView = (TextView) view
				.findViewById(R.id.balance_report_descp);
		descpView.setText(item.Descp);

		TextView valueView = (TextView) view
				.findViewById(R.id.balance_report_value);
		valueView.setText(item.Value.toString());

		TextView idView = (TextView) view.findViewById(R.id.balance_report_id);
		idView.setText(item.MdId.toString());
	}
}
