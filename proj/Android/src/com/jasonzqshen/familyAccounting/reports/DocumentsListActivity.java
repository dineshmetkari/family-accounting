package com.jasonzqshen.familyAccounting.reports;

import java.util.ArrayList;

import com.jasonzqshen.familyAccounting.R;
import com.jasonzqshen.familyAccounting.data.DataCore;
import com.jasonzqshen.familyAccounting.widgets.DocumentsListAdapter;
import com.jasonzqshen.familyAccounting.widgets.DocumentsListAdapterItem;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.document_entries.CustomerEntry;
import com.jasonzqshen.familyaccounting.core.document_entries.GLAccountEntry;
import com.jasonzqshen.familyaccounting.core.document_entries.IDocumentEntry;
import com.jasonzqshen.familyaccounting.core.document_entries.VendorEntry;
import com.jasonzqshen.familyaccounting.core.masterdata.GLAccountMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataBase;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataFactoryBase;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;
import com.jasonzqshen.familyaccounting.core.reports.DocumentIndex;
import com.jasonzqshen.familyaccounting.core.reports.DocumentIndexItem;
import com.jasonzqshen.familyaccounting.core.reports.ReportsManagement;
import com.jasonzqshen.familyaccounting.core.transaction.GLAccountBalanceCollection;
import com.jasonzqshen.familyaccounting.core.transaction.GLAccountBalanceItem;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;
import com.jasonzqshen.familyaccounting.core.transaction.ItemEntity;
import com.jasonzqshen.familyaccounting.core.transaction.MonthIdentity;
import com.jasonzqshen.familyaccounting.core.transaction.TransactionDataManagement;
import com.jasonzqshen.familyaccounting.core.utils.CreditDebitIndicator;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;
import com.jasonzqshen.familyaccounting.core.utils.DocumentType;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class DocumentsListActivity extends ListActivity {
	private DataCore _dataCore;
	private Spinner _monthFilter;
	private Spinner _groupSpinner;
	private Button _valueFilter;
	private DocumentsListAdapter _listAdapter;

	/**
	 * month selection listener
	 */
	private final OnItemSelectedListener _MONTH_SELECTION_LISTENER = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1,
				int position, long arg3) {

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}
	};

	/**
	 * value selection listener
	 */
	private final View.OnClickListener _VALUE_SELECTION_LISTENER = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			showDialog(R.id.dialog_value_selection);
		}
	};

	/**
	 * Group by selection listener
	 */
	private final OnItemSelectedListener _GROUP_BY_LISTENER = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> arg0, View view,
				int position, long arg3) {
			setValueSet();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}

	};

	/**
	 * Multiple choice click listener
	 */
	private final DialogInterface.OnMultiChoiceClickListener _VALUE_MULTIPLE_CHOICE_LISTENER = new DialogInterface.OnMultiChoiceClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int clicked,
				boolean selected) {
			_selectedValue[clicked] = selected;
		}
	};

	/**
	 * value selection OK button listener
	 */
	private final DialogInterface.OnClickListener _VALUE_SELECTION_OK_LISTENER = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			setDocListData();
			dialog.dismiss();
		}
	};

	private MonthIdentity[] _monthValueSet;
	private ArrayAdapter<MonthIdentity> _monthSpinnerAdapter;
	private boolean[] _selectedValue;
	private Object[] _valueSet;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.documents_list);

		_dataCore = DataCore.getInstance();
		CoreDriver coreDriver = _dataCore.getCoreDriver();
		if (coreDriver.isInitialized() == false) {
			return;
		}

		_monthFilter = (Spinner) this.findViewById(R.id.startMonthSpinner);
		_monthFilter.setOnItemSelectedListener(_MONTH_SELECTION_LISTENER);
		TransactionDataManagement transMgmt = coreDriver
				.getTransDataManagement();
		_monthValueSet = transMgmt.getAllMonthIds();
		_monthSpinnerAdapter = new ArrayAdapter<MonthIdentity>(this,
				android.R.layout.simple_spinner_item, _monthValueSet);
		_monthFilter.setAdapter(_monthSpinnerAdapter);

		_groupSpinner = (Spinner) this.findViewById(R.id.groupSpinner);
		_groupSpinner.setOnItemSelectedListener(_GROUP_BY_LISTENER);

		_valueFilter = (Button) this.findViewById(R.id.valueFilter);
		_valueFilter.setOnClickListener(_VALUE_SELECTION_LISTENER);

		/**
		 * set the value set
		 */
		setValueSet();

		// set default value of month identity
		_monthFilter.setSelection(_monthValueSet.length - 1);
	}

	@Override
	protected void onResume() {
		super.onResume();

		setDocListData();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case R.id.dialog_value_selection:
			CharSequence[] valueOptions = new CharSequence[_valueSet.length];
			for (int i = 0; i < valueOptions.length; ++i) {
				valueOptions[i] = _valueSet[i].toString();
			}

			return new AlertDialog.Builder(this)
					.setTitle(R.string.documents_select_value)
					.setMultiChoiceItems(valueOptions, _selectedValue,
							_VALUE_MULTIPLE_CHOICE_LISTENER)
					.setPositiveButton(R.string.ok,
							_VALUE_SELECTION_OK_LISTENER).create();

		}

		return null;
	}

	/**
	 * set value set
	 */
	private void setValueSet() {
		// check group-by
		int position = _groupSpinner.getSelectedItemPosition();
		switch (position) {
		case 0:
			// account
			CoreDriver coreDriver = _dataCore.getCoreDriver();
			if (coreDriver.isInitialized()) {
				MasterDataManagement mdMgmt = coreDriver
						.getMasterDataManagement();
				MasterDataFactoryBase factory = mdMgmt
						.getMasterDataFactory(MasterDataType.GL_ACCOUNT);
				_valueSet = factory.getAllEntities();
				_selectedValue = new boolean[_valueSet.length];
				for (int i = 0; i < _selectedValue.length; ++i) {
					_selectedValue[i] = true;
				}
			}
			break;
		case 1:
			break;
		case 2:
			break;
		}
	}

	/**
	 * get document list
	 */
	private void setDocListData() {
		// get month identities
		MonthIdentity monthId = (MonthIdentity) _monthFilter.getSelectedItem();
		int position = _groupSpinner.getSelectedItemPosition();
		ArrayList<DocumentsListAdapterItem> items = new ArrayList<DocumentsListAdapterItem>();
		CoreDriver coreDriver = _dataCore.getCoreDriver();

		if (coreDriver.isInitialized() == false) {
			return;
		}

		TransactionDataManagement transMgmt = coreDriver
				.getTransDataManagement();
		GLAccountBalanceCollection balCol = transMgmt.getAccBalCol();
		MasterDataManagement mdMgmt = coreDriver.getMasterDataManagement();
		ReportsManagement rMgmt = _dataCore.getReportsManagement();

		switch (position) {
		case 0:
			// account

			for (int i = 0; i < _selectedValue.length; ++i) {
				if (_selectedValue[i] == false) {
					continue;
				}
				GLAccountMasterData account = (GLAccountMasterData) _valueSet[i];
				DocumentIndex index = rMgmt
						.getDocumentIndex(DocumentIndex.ACCOUNT_INDEX);
				DocumentIndexItem indexItem = index.getIndexItem(account
						.getGLIdentity());
				if (indexItem == null) {
					continue;
				}

				MasterDataBase data = mdMgmt.getMasterData(
						account.getGLIdentity(), MasterDataType.GL_ACCOUNT);

				GLAccountBalanceItem balItem = balCol.getBalanceItem(account
						.getGLIdentity());
				// add group head
				items.add(new DocumentsListAdapterItem(
						DocumentsListAdapterItem.HEAD_VIEW, null, data
								.getDescp(), balItem.getAmount(monthId), null));

				// get selected value

				ArrayList<HeadEntity> docs = indexItem.getEntities(monthId,
						monthId);
				for (HeadEntity headEntity : docs) {
					addDocItem(items, headEntity, account.getGLIdentity());
				}
			}
			break;
		}

		// construct adapter
		_listAdapter = new DocumentsListAdapter(this, items);
		this.setListAdapter(_listAdapter);
	}

	/**
	 * add document list item to the item list
	 * 
	 * @param items
	 */
	private void addDocItem(ArrayList<DocumentsListAdapterItem> items,
			HeadEntity headEntity, MasterDataIdentity_GLAccount glAccount) {
		DocumentType type = headEntity.getDocumentType();
		IDocumentEntry entry = null;
		int viewType = DocumentsListAdapterItem.OTHER_VIEW;
		if (type == DocumentType.GL) {
			entry = GLAccountEntry.parse(headEntity);
			viewType = DocumentsListAdapterItem.GL_VIEW;
		} else if (type == DocumentType.CUSTOMER_INVOICE) {
			entry = CustomerEntry.parse(headEntity);
			viewType = DocumentsListAdapterItem.INCOMING_VIEW;
		} else if (type == DocumentType.VENDOR_INVOICE) {
			entry = VendorEntry.parse(headEntity);
			viewType = DocumentsListAdapterItem.OUTGOING_VIEW;
		}
		if (entry != null) {
			items.add(new DocumentsListAdapterItem(viewType, entry, null, null,
					glAccount));
		} else {
			CurrencyAmount amount = new CurrencyAmount();
			for (ItemEntity item : headEntity.getItems()) {
				if (item.getGLAccount().equals(glAccount)) {
					if (item.getCDIndicator() == CreditDebitIndicator.DEBIT) {
						amount.addTo(item.getAmount());
					} else {
						amount.minusTo(item.getAmount());
					}
				}
			}
			items.add(new DocumentsListAdapterItem(
					DocumentsListAdapterItem.OTHER_VIEW, null, headEntity
							.getDocText(), amount, null));
		}
	}
}
