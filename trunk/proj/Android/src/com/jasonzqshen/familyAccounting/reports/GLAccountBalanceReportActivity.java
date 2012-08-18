package com.jasonzqshen.familyAccounting.reports;

import java.util.ArrayList;

import com.jasonzqshen.familyAccounting.R;
import com.jasonzqshen.familyAccounting.data.DataCore;
import com.jasonzqshen.familyAccounting.widgets.BalanceAccountAdapter;
import com.jasonzqshen.familyAccounting.widgets.BalanceAccountAdapterItem;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.masterdata.GLAccountMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;
import com.jasonzqshen.familyaccounting.core.transaction.GLAccountBalanceCollection;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;
import com.jasonzqshen.familyaccounting.core.utils.GLAccountGroup;

import android.app.ListActivity;
import android.os.Bundle;

public class GLAccountBalanceReportActivity extends ListActivity {
	private DataCore _dataCore = null;
	private BalanceAccountAdapter _adapter = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_balance_report);

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
		ArrayList<BalanceAccountAdapterItem> arrList = new ArrayList<BalanceAccountAdapterItem>();
		CoreDriver coreDriver = _dataCore.getCoreDriver();
		if (!coreDriver.isInitialized()) {
			return;
		}

		MasterDataManagement masterDataMgmt = coreDriver
				.getMasterDataManagement();
		GLAccountBalanceCollection balCol = coreDriver.getTransDataManagement()
				.getAccBalCol();

		for (GLAccountGroup group : GLAccountGroup.values()) {
			arrList.add(new BalanceAccountAdapterItem(null,
					BalanceAccountAdapterItem.VIEW_TYPE_TOP, GLAccountGroup
							.getDescp(group), null));
			MasterDataIdentity_GLAccount[] accountsID = masterDataMgmt
					.getGLAccountsBasedGroup(group);
			for (MasterDataIdentity_GLAccount id : accountsID) {
				GLAccountMasterData account = (GLAccountMasterData) masterDataMgmt
						.getMasterData(id, MasterDataType.GL_ACCOUNT);
				CurrencyAmount amount = balCol.getBalanceItem(id)
						.getSumAmount();
				arrList.add(new BalanceAccountAdapterItem(amount,
						BalanceAccountAdapterItem.VIEW_TYPE_CENTER, account
								.getDescp(), id));
			}

			arrList.add(new BalanceAccountAdapterItem(null,
					BalanceAccountAdapterItem.VIEW_TYPE_BOTTOM, null, null));
		}

		_adapter = new BalanceAccountAdapter(this, arrList);
		setListAdapter(_adapter);
	}
}
