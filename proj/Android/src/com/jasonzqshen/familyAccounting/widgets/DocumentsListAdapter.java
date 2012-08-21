package com.jasonzqshen.familyAccounting.widgets;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.jasonzqshen.familyAccounting.R;
import com.jasonzqshen.familyAccounting.data.DataCore;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.document_entries.GLAccountEntry;
import com.jasonzqshen.familyaccounting.core.document_entries.VendorEntry;
import com.jasonzqshen.familyaccounting.core.exception.NoFieldNameException;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataBase;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DocumentsListAdapter extends BaseAdapter {
	private final Context _context;
	private final LayoutInflater _layoutInflater;
	private final ArrayList<DocumentsListAdapterItem> _items;

	public DocumentsListAdapter(Context context,
			ArrayList<DocumentsListAdapterItem> items) {
		_context = context;
		_items = items;
		_layoutInflater = LayoutInflater.from(_context);

	}

	@Override
	public int getCount() {
		return _items.size();
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
		DocumentsListAdapterItem item = _items.get(position);
		switch (item.Type) {
		case DocumentsListAdapterItem.HEAD_VIEW:
			if (null == view) {
				view = _layoutInflater.inflate(R.layout.documents_head, null);
			}
			fillHeadView(view, item.Descp, item.Amount);
			break;
		case DocumentsListAdapterItem.OUTGOING_VIEW:
			if (null == view) {
				view = _layoutInflater.inflate(
						R.layout.documents_item_outgoing, null);
			}
			fillItemOutgoingView(view, item);
			break;
		case DocumentsListAdapterItem.INCOMING_VIEW:
			if (null == view) {
				view = _layoutInflater.inflate(
						R.layout.documents_item_incoming, null);
			}
			//fillItemCustomerView(view, item);
			break;
		case DocumentsListAdapterItem.GL_VIEW:
			if (null == view) {
				view = _layoutInflater
						.inflate(R.layout.documents_item_gl, null);
			}
			fillItemGLView(view, item);

			break;
		case DocumentsListAdapterItem.OTHER_VIEW:
			if (null == view) {
				view = _layoutInflater.inflate(
						R.layout.documents_item_customized, null);
			}
			// fillItemView(view, item);
			break;
		}

		return view;
	}

	@Override
	public int getViewTypeCount() {
		return DocumentsListAdapterItem.VIEW_TYPE_COUNT;
	}

	@Override
	public int getItemViewType(int position) {
		return _items.get(position).Type;
	}

	/**
	 * fill description
	 * 
	 * @param view
	 * @param descp
	 * @param amount
	 */
	private void fillHeadView(View view, String descp, CurrencyAmount amount) {
		TextView descpView = (TextView) view.findViewById(R.id.descp);
		descpView.setText(descp);

		TextView amountView = (TextView) view.findViewById(R.id.amount);
		amountView.setText(amount.toString());
	}

	/**
	 * fill item view
	 * 
	 * @param view
	 * @param item
	 */
	private void fillItemOutgoingView(View view, DocumentsListAdapterItem item) {
		DataCore dataCore = DataCore.getInstance();
		CoreDriver coreDriver = dataCore.getCoreDriver();

		if (coreDriver.isInitialized() == false) {
			return;
		}

		MasterDataManagement mdMgmt = coreDriver.getMasterDataManagement();
		TextView outgoingAccount = (TextView) view
				.findViewById(R.id.accountValue);
		TextView vendor = (TextView) view.findViewById(R.id.vendorValue);
		TextView costAccount = (TextView) view.findViewById(R.id.costAccount);
		TextView dateField = (TextView) view.findViewById(R.id.pstingDateValue);
		TextView amountFiled = (TextView) view.findViewById(R.id.amountValue);
		TextView descp = (TextView) view.findViewById(R.id.descp);
		try {
			// rec account
			MasterDataIdentity id = (MasterDataIdentity) item.Document
					.getValue(VendorEntry.REC_ACC);
			MasterDataBase data = mdMgmt.getMasterData(id,
					MasterDataType.GL_ACCOUNT);
			outgoingAccount.setText(data.getDescp());

			// vendor
			id = (MasterDataIdentity) item.Document
					.getValue(VendorEntry.VENDOR);
			data = mdMgmt.getMasterData(id, MasterDataType.VENDOR);
			vendor.setText(data.getDescp());

			// cost account
			id = (MasterDataIdentity) item.Document
					.getValue(VendorEntry.GL_ACCOUNT);
			data = mdMgmt.getMasterData(id, MasterDataType.GL_ACCOUNT);
			costAccount.setText(data.getDescp());

			// posting date
			Date pstDate = (Date) item.Document
					.getValue(VendorEntry.POSTING_DATE);
			SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
			dateField.setText(format.format(pstDate));

			// amount
			CurrencyAmount amount = (CurrencyAmount) item.Document
					.getValue(VendorEntry.AMOUNT);
			if (item.Document.getValue(VendorEntry.REC_ACC).equals(
					item.RelatedAccount)) {
				amount.negate();
			}
			amountFiled.setText(amount.toString());

			// description
			descp.setText(item.Document.getValue(VendorEntry.TEXT).toString());
		} catch (NoFieldNameException e) {
			throw new SystemException(e);// bug
		}
	}

	/**
	 * fill item view
	 * 
	 * @param view
	 * @param item
	 */
	private void fillItemGLView(View view, DocumentsListAdapterItem item) {
		DataCore dataCore = DataCore.getInstance();
		CoreDriver coreDriver = dataCore.getCoreDriver();

		if (coreDriver.isInitialized() == false) {
			return;
		}

		MasterDataManagement mdMgmt = coreDriver.getMasterDataManagement();
		TextView srcAccount = (TextView) view.findViewById(R.id.srcAccount);
		TextView dstAccount = (TextView) view.findViewById(R.id.dstAccount);
		TextView date = (TextView) view.findViewById(R.id.pstingDateValue);
		TextView amountFiled = (TextView) view.findViewById(R.id.amountValue);
		TextView descp = (TextView) view.findViewById(R.id.descp);
		try {
			// source account
			MasterDataIdentity id = (MasterDataIdentity) item.Document
					.getValue(GLAccountEntry.SRC_ACCOUNT);
			MasterDataBase data = mdMgmt.getMasterData(id,
					MasterDataType.GL_ACCOUNT);
			srcAccount.setText(data.getDescp());

			// destination
			id = (MasterDataIdentity) item.Document
					.getValue(GLAccountEntry.DST_ACCOUNT);
			data = mdMgmt.getMasterData(id, MasterDataType.GL_ACCOUNT);
			dstAccount.setText(data.getDescp());

			// posting date
			Date pstDate = (Date) item.Document
					.getValue(GLAccountEntry.POSTING_DATE);
			SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
			date.setText(format.format(pstDate));

			// amount
			CurrencyAmount amount = (CurrencyAmount) item.Document
					.getValue(GLAccountEntry.AMOUNT);
			if (item.Document.getValue(GLAccountEntry.SRC_ACCOUNT).equals(
					item.RelatedAccount)) {
				amount.negate();
			}
			String str = amount.toString();
			amountFiled.setText(str);

			// description
			descp.setText(item.Document.getValue(GLAccountEntry.TEXT)
					.toString());
		} catch (NoFieldNameException e) {
			throw new SystemException(e);// bug
		}
	}
}
