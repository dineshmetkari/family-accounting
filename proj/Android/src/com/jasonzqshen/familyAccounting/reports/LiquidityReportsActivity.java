package com.jasonzqshen.familyAccounting.reports;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.jasonzqshen.familyAccounting.R;
import com.jasonzqshen.familyAccounting.widgets.AccountReportAdapterItem;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.transaction.MonthIdentity;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;
import com.jasonzqshen.familyaccounting.core.utils.GLAccountGroup;

public class LiquidityReportsActivity extends BalanceReportActivityBase {
	private TextView _amountField;
	private CheckBox _checkBox;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		_amountField = (TextView) this.findViewById(R.id.sum_value);
		ImageButton newBtn = (ImageButton) this.findViewById(R.id.new_icon);
		newBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(R.id.dialog_entries);
			}
		});

		// check box
		_checkBox = (CheckBox) this.findViewById(R.id.displayOnlyLiquidity);
		_checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton btn, boolean isChecked) {
				refersh();
			}
		});
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		CoreDriver coreDriver = _dataCore.getCoreDriver();
		if (coreDriver.isInitialized() == false) {
			return;
		}

		MonthIdentity monthId = coreDriver.getCurMonthId();

		AccountReportAdapterItem item = (AccountReportAdapterItem) _adapter
				.getItem(position);
		if (item.Type != AccountReportAdapterItem.ITEM_VIEW) {
			return;
		}

		ArrayList<Object> list = new ArrayList<Object>();
		list.add(item.Account.getIdentity());

		// add parameters
		ArrayList<DocListParamItem> items = new ArrayList<DocListParamItem>();
		items.add(new DocListParamItem(list, DocListParam.ACCOUNT_CATEGORY));

		DocListParam param = new DocListParam(monthId, items);
		Intent docListIntent = new Intent(this, DocumentsListActivity.class);
		docListIntent.putExtra(DocListParam.PARAM_NAME, param);
		this.startActivity(docListIntent);
	}

	@Override
	protected GLAccountGroup[] getAccountGroup() {
		boolean checked = _checkBox.isChecked();
		if (checked) {
			return new GLAccountGroup[] { GLAccountGroup.CASH,
					GLAccountGroup.BANK_ACCOUNT,
					GLAccountGroup.SHORT_LIABILITIES };
		}
		return new GLAccountGroup[] { GLAccountGroup.CASH,
				GLAccountGroup.BANK_ACCOUNT, GLAccountGroup.PREPAID,
				GLAccountGroup.ASSETS, GLAccountGroup.INVESTMENT,
				GLAccountGroup.SHORT_LIABILITIES };
	}

	@Override
	protected int getViewId() {
		return R.layout.liquidity_report;
	}

	/**
     * 
     */
	@Override
	protected void refersh() {
		super.refersh();

		CurrencyAmount sum = new CurrencyAmount();
		for (int i = 0; i < _adapter.getCount(); ++i) {
			AccountReportAdapterItem item = (AccountReportAdapterItem) _adapter
					.getItem(i);
			if (item.Type == AccountReportAdapterItem.HEAD_VIEW
					|| item.Type == AccountReportAdapterItem.HEAD_VIEW_RED) {
				sum.addTo(item.Amount);
			}
		}

		_amountField.setText(sum.toString());
	}
}
