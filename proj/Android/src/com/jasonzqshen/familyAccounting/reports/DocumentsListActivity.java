package com.jasonzqshen.familyAccounting.reports;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.jasonzqshen.familyAccounting.R;
import com.jasonzqshen.familyAccounting.data.DataCore;
import com.jasonzqshen.familyAccounting.widgets.DocumentsListAdapter;
import com.jasonzqshen.familyAccounting.widgets.DocumentsListAdapterItem;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.document_entries.CustomerEntry;
import com.jasonzqshen.familyaccounting.core.document_entries.GLAccountEntry;
import com.jasonzqshen.familyaccounting.core.document_entries.IDocumentEntry;
import com.jasonzqshen.familyaccounting.core.document_entries.VendorEntry;
import com.jasonzqshen.familyaccounting.core.masterdata.BusinessAreaMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.GLAccountMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataBase;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataFactoryBase;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class DocumentsListActivity extends ListActivity {
	public final static String TAG = "DocumentsListActivity";

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
		public void onItemSelected(AdapterView<?> arg0, View view,
				int position, long id) {
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
	 * Multiple choice click listener
	 */
	private final DialogInterface.OnMultiChoiceClickListener _VALUE_MULTIPLE_CHOICE_LISTENER = new DialogInterface.OnMultiChoiceClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int clicked,
				boolean selected) {
			int position = _groupSpinner.getSelectedItemPosition();
			switch (position) {
			case DocListParam.ACCOUNT_CATEGORY:
				_glAccountSelection[clicked] = selected;
			case DocListParam.BUSINESS_CATEGORY:
				_businessAreaSelection[clicked] = selected;
			case DocListParam.DATE_CATEGORY:
				_dateSelection[clicked] = selected;
				break;
			}
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

	// month selection
	private MonthIdentity[] _monthValueSet;
	private ArrayAdapter<MonthIdentity> _monthSpinnerAdapter;

	// GL account identities value set
	private ArrayList<GLAccountMasterData> _glAccountValueSet;
	private boolean[] _glAccountSelection;

	// date value set
	private ArrayList<Date> _dateValueSet;
	private boolean[] _dateSelection;

	// business areas value set
	private ArrayList<BusinessAreaMasterData> _businessAreaValueSet;
	private boolean[] _businessAreaSelection;

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

		// set the month selection
		_monthFilter = (Spinner) this.findViewById(R.id.startMonthSpinner);
		_monthFilter.setOnItemSelectedListener(_MONTH_SELECTION_LISTENER);

		TransactionDataManagement transMgmt = coreDriver
				.getTransDataManagement();
		_monthValueSet = transMgmt.getAllMonthIds();
		_monthSpinnerAdapter = new ArrayAdapter<MonthIdentity>(this,
				android.R.layout.simple_spinner_item, _monthValueSet);
		_monthFilter.setAdapter(_monthSpinnerAdapter);

		// group by selection
		_groupSpinner = (Spinner) this.findViewById(R.id.groupSpinner);

		// set value filter
		_valueFilter = (Button) this.findViewById(R.id.valueFilter);
		_valueFilter.setOnClickListener(_VALUE_SELECTION_LISTENER);

		// set default value of month identity
		_monthFilter.setSelection(_monthValueSet.length - 1);

		DocListParam param = (DocListParam) this.getIntent()
				.getSerializableExtra(DocListParam.PARAM_NAME);
		;
		generateValueSet(param);
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
			CharSequence[] valueOptions = null;
			boolean[] selection = null;
			int position = _groupSpinner.getSelectedItemPosition();

			Log.i(TAG, "open value filter " + position);
			switch (position) {
			case DocListParam.ACCOUNT_CATEGORY:
				valueOptions = new CharSequence[_glAccountValueSet.size()];
				for (int i = 0; i < valueOptions.length; ++i) {
					valueOptions[i] = _glAccountValueSet.get(i).getDescp();
				}
				selection = _glAccountSelection;
				break;
			case DocListParam.BUSINESS_CATEGORY:
				valueOptions = new CharSequence[_businessAreaValueSet.size()];
				for (int i = 0; i < valueOptions.length; ++i) {
					valueOptions[i] = _businessAreaValueSet.get(i).getDescp();
				}
				selection = _businessAreaSelection;
				break;
			case DocListParam.DATE_CATEGORY:
				SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
				valueOptions = new CharSequence[_dateValueSet.size()];
				for (int i = 0; i < valueOptions.length; ++i) {
					valueOptions[i] = format.format(_dateValueSet.get(i));
				}
				selection = _dateSelection;
				break;
			}

			return new AlertDialog.Builder(this)
					.setTitle(R.string.documents_select_value)
					.setMultiChoiceItems(valueOptions, selection,
							_VALUE_MULTIPLE_CHOICE_LISTENER)
					.setPositiveButton(R.string.ok,
							_VALUE_SELECTION_OK_LISTENER).create();

		}

		return null;
	}

	/**
	 * get document list
	 */
	private void setDocListData() {
		int position = this._groupSpinner.getSelectedItemPosition();
		switch (position) {
		case DocListParam.ACCOUNT_CATEGORY:
			this.setDocList_GLAccount();
			break;
		}
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

	/**
	 * generate value set, the value set of G/L accounts, business areas, dates
	 */
	private void generateValueSet(DocListParam param) {
		CoreDriver coreDriver = _dataCore.getCoreDriver();
		if (coreDriver.isInitialized() == false) {
			return;
		}

		MasterDataManagement mdMgmt = coreDriver.getMasterDataManagement();

		int length = 0;
		if (param != null) {
			for (DocListParamItem item : param.List) {
				switch (item.Category) {
				case DocListParam.ACCOUNT_CATEGORY:
					length = item.SelectedValue.size();
					_glAccountValueSet = new ArrayList<GLAccountMasterData>();
					_glAccountSelection = new boolean[length];
					for (int i = 0; i < length; ++i) {
						MasterDataIdentity_GLAccount accountId = (MasterDataIdentity_GLAccount) item.SelectedValue
								.get(i);
						_glAccountValueSet.add((GLAccountMasterData) mdMgmt
								.getMasterData(accountId,
										MasterDataType.GL_ACCOUNT));
						_glAccountSelection[i] = true;
					}

					break;
				case DocListParam.BUSINESS_CATEGORY:
					length = item.SelectedValue.size();
					_businessAreaValueSet = new ArrayList<BusinessAreaMasterData>();
					_businessAreaSelection = new boolean[length];
					for (int i = 0; i < length; ++i) {
						MasterDataIdentity id = (MasterDataIdentity) item.SelectedValue
								.get(i);
						_businessAreaValueSet
								.add((BusinessAreaMasterData) mdMgmt
										.getMasterData(id,
												MasterDataType.BUSINESS_AREA));
						_businessAreaSelection[i] = true;
					}

					break;
				case DocListParam.DATE_CATEGORY:
					length = item.SelectedValue.size();
					_dateValueSet = new ArrayList<Date>();
					_dateSelection = new boolean[length];
					for (int i = 0; i < length; ++i) {
						_dateValueSet.add((Date) item.SelectedValue.get(i));
						_dateSelection[i] = true;
					}

					break;
				}
			}
		}

		if (_glAccountValueSet == null) {
			MasterDataFactoryBase factory = mdMgmt
					.getMasterDataFactory(MasterDataType.GL_ACCOUNT);
			MasterDataBase[] entities = factory.getAllEntities();
			_glAccountValueSet = new ArrayList<GLAccountMasterData>();
			_glAccountSelection = new boolean[entities.length];
			for (int i = 0; i < _glAccountSelection.length; ++i) {
				_glAccountValueSet.add((GLAccountMasterData) entities[i]);
				_glAccountSelection[i] = true;
			}
		}

		if (_businessAreaValueSet == null) {
			MasterDataFactoryBase factory = mdMgmt
					.getMasterDataFactory(MasterDataType.BUSINESS_AREA);
			MasterDataBase[] entities = factory.getAllEntities();
			_businessAreaValueSet = new ArrayList<BusinessAreaMasterData>();
			;
			_businessAreaSelection = new boolean[entities.length];
			for (int i = 0; i < _businessAreaSelection.length; ++i) {
				_businessAreaValueSet.add((BusinessAreaMasterData) entities[i]);
				_businessAreaSelection[i] = true;
			}
		}

		if (_dateValueSet == null) {
			int monthSelected = _monthFilter.getSelectedItemPosition();
			MonthIdentity selectedMonth = _monthValueSet[monthSelected];
			MonthIdentity nextMonth = selectedMonth.addMonth();

			Calendar startCalendar = Calendar.getInstance();
			startCalendar.set(Calendar.YEAR, selectedMonth._fiscalYear);
			startCalendar.set(Calendar.MONTH, selectedMonth._fiscalMonth - 1);
			startCalendar.set(Calendar.DATE, 1);

			Calendar endCalendar = Calendar.getInstance();
			endCalendar.set(Calendar.YEAR, nextMonth._fiscalYear);
			endCalendar.set(Calendar.MONTH, nextMonth._fiscalMonth - 1);
			endCalendar.set(Calendar.DATE, 1);

			_dateValueSet = new ArrayList<Date>();

			for (; startCalendar.compareTo(endCalendar) < 0; startCalendar.add(
					Calendar.DATE, 1)) {
				_dateValueSet.add(startCalendar.getTime());
			}

			_dateSelection = new boolean[_dateValueSet.size()];
			for (int i = 0; i < _dateValueSet.size(); ++i) {
				_dateSelection[i] = true;
			}
		}
	}

	/**
	 * set document list when group-by is G/L Account
	 */
	private void setDocList_GLAccount() {
		boolean businessIsAll = true;
		for (int j = 0; j < _businessAreaSelection.length; ++j) {
			if (_businessAreaSelection[j] == false) {
				businessIsAll = false;
				break;
			}
		}

		// get month identities
		MonthIdentity monthId = (MonthIdentity) _monthFilter.getSelectedItem();
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

		for (int i = 0; i < _glAccountSelection.length; ++i) {
			if (_glAccountSelection[i] == false) {
				continue;
			}
			GLAccountMasterData account = (GLAccountMasterData) _glAccountValueSet
					.get(i);
			DocumentIndex index = rMgmt
					.getDocumentIndex(DocumentIndex.ACCOUNT_INDEX);
			DocumentIndexItem indexItem = index.getIndexItem(account
					.getGLIdentity());
			if (indexItem == null) {
				continue;
			}

			MasterDataBase data = mdMgmt.getMasterData(account.getGLIdentity(),
					MasterDataType.GL_ACCOUNT);

			GLAccountBalanceItem balItem = balCol.getBalanceItem(account
					.getGLIdentity());
			// add group head
			items.add(new DocumentsListAdapterItem(
					DocumentsListAdapterItem.HEAD_VIEW, null, data.getDescp(),
					balItem.getAmount(monthId), null));

			// get selected value

			ArrayList<HeadEntity> docs = indexItem
					.getEntities(monthId, monthId);
			for (HeadEntity headEntity : docs) {
				// check the document is in the date value set and business
				// value set
				int dateIndex = _dateValueSet.indexOf(headEntity
						.getPostingDate());
				if (_dateSelection[dateIndex] == true) {
					if (businessIsAll == false) {
						ItemEntity[] docItems = headEntity.getItems();
						boolean flag = false;
						for (ItemEntity docItem : docItems) {
							MasterDataIdentity id = docItem.getBusinessArea();
							if (id == null) {
								continue;
							}
							MasterDataBase businessArea = mdMgmt.getMasterData(
									id, MasterDataType.BUSINESS_AREA);
							int bIndex = _businessAreaValueSet
									.indexOf(businessArea);
							if (_businessAreaSelection[bIndex] == true) {
								flag = true;
								break;
							}
						}

						if (flag) {
							addDocItem(items, headEntity,
									account.getGLIdentity());
						}
					} else {
						addDocItem(items, headEntity, account.getGLIdentity());
					}

				}

			}
		}

		// construct adapter
		_listAdapter = new DocumentsListAdapter(this, items);
		this.setListAdapter(_listAdapter);
	}

}
