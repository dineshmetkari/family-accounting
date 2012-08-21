package com.jasonzqshen.familyAccounting.reports;

import java.util.ArrayList;

import com.jasonzqshen.familyAccounting.R;
import com.jasonzqshen.familyAccounting.data.DataCore;
import com.jasonzqshen.familyAccounting.widgets.AccountReportAdapter;
import com.jasonzqshen.familyAccounting.widgets.AccountReportAdapterItem;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.masterdata.GLAccountMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;
import com.jasonzqshen.familyaccounting.core.transaction.GLAccountBalanceCollection;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;
import com.jasonzqshen.familyaccounting.core.utils.GLAccountGroup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GLAccountBalanceReportActivity extends ListActivity {
	private DataCore _dataCore = null;
	private AccountReportAdapter _adapter = null;
	private Button _groupSelectionButton = null;

	/**
	 * value selection listener
	 */
	private final View.OnClickListener _GROUP_SELECTION_LISTENER = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			showDialog(R.id.dialog_account_group_selection);
		}
	};

	/**
	 * Multiple choice click listener
	 */
	private final DialogInterface.OnMultiChoiceClickListener _GROUP_MULTIPLE_CHOICE_LISTENER = new DialogInterface.OnMultiChoiceClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int clicked,
				boolean selected) {
			_groupSelected[clicked] = selected;
		}
	};

	/**
	 * value selection OK button listener
	 */
	private final DialogInterface.OnClickListener _GROUP_SELECTION_OK_LISTENER = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			setGroupSelectionText();
			
			refersh();
			dialog.dismiss();
		}
	};

	private GLAccountGroup[] _groupSet;
	private boolean[] _groupSelected;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_balance_report);

		_groupSelectionButton = (Button) this
				.findViewById(R.id.groupSelectionBtn);
		_groupSelectionButton.setOnClickListener(_GROUP_SELECTION_LISTENER);

		_groupSet = GLAccountGroup.values();
		_groupSelected = new boolean[_groupSet.length];
		for (int i = 0; i < _groupSet.length; ++i) {
			_groupSelected[i] = true;
		}
		
		setGroupSelectionText();

		_dataCore = DataCore.getInstance();
	}

	@Override
	public void onResume() {
		super.onResume();

		refersh();
	}

	/**
	 * refresh
	 */
	private void refersh() {
		ArrayList<AccountReportAdapterItem> arrList = new ArrayList<AccountReportAdapterItem>();
		CoreDriver coreDriver = _dataCore.getCoreDriver();
		if (!coreDriver.isInitialized()) {
			return;
		}

		MasterDataManagement masterDataMgmt = coreDriver
				.getMasterDataManagement();
		GLAccountBalanceCollection balCol = coreDriver.getTransDataManagement()
				.getAccBalCol();

		for (int i = 0; i < _groupSet.length; ++i) {
			if (_groupSelected[i] == false) {
				continue;
			}
			GLAccountGroup group = _groupSet[i];

			CurrencyAmount groupAmount = balCol.getGroupBalance(group);

			arrList.add(new AccountReportAdapterItem(GLAccountGroup
					.getDescp(group), groupAmount,
					AccountReportAdapterItem.HEAD_VIEW));
			MasterDataIdentity_GLAccount[] accountsID = masterDataMgmt
					.getGLAccountsBasedGroup(group);
			for (MasterDataIdentity_GLAccount id : accountsID) {
				GLAccountMasterData account = (GLAccountMasterData) masterDataMgmt
						.getMasterData(id, MasterDataType.GL_ACCOUNT);
				CurrencyAmount amount = balCol.getBalanceItem(id)
						.getSumAmount();
				arrList.add(new AccountReportAdapterItem(account.getDescp(),
						amount, AccountReportAdapterItem.ITEM_VIEW));
			}

		}

		_adapter = new AccountReportAdapter(this, arrList);
		setListAdapter(_adapter);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case R.id.dialog_account_group_selection:
			CharSequence[] valueOptions = new CharSequence[_groupSet.length];
			for (int i = 0; i < valueOptions.length; ++i) {
				valueOptions[i] = GLAccountGroup.getDescp(_groupSet[i]);
			}

			return new AlertDialog.Builder(this)
					.setTitle(R.string.documents_select_value)
					.setMultiChoiceItems(valueOptions, _groupSelected,
							_GROUP_MULTIPLE_CHOICE_LISTENER)
					.setPositiveButton(R.string.ok,
							_GROUP_SELECTION_OK_LISTENER).create();

		}

		return null;
	}

	/**
	 * set the text on group selection button
	 */
	private void setGroupSelectionText() {
		StringBuilder strBuilder = new StringBuilder();

		for (int i = 0; i < _groupSet.length; ++i) {
			if (_groupSelected[i] == false) {
				continue;
			}

			strBuilder.append(GLAccountGroup.getDescp(_groupSet[i]) + ", ");
		}

		_groupSelectionButton.setText(strBuilder.toString());
	}
}
