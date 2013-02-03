package com.jasonzqshen.familyAccounting.entries;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.jasonzqshen.familyAccounting.R;
import com.jasonzqshen.familyAccounting.data.DataCore;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.document_entries.EntryTemplate;
import com.jasonzqshen.familyaccounting.core.document_entries.EntryTemplatesManagement;
import com.jasonzqshen.familyaccounting.core.document_entries.IDocumentEntry;
import com.jasonzqshen.familyaccounting.core.exception.MandatoryFieldIsMissing;
import com.jasonzqshen.familyaccounting.core.exception.NoFieldNameException;
import com.jasonzqshen.familyaccounting.core.exception.NotInValueRangeException;
import com.jasonzqshen.familyaccounting.core.exception.SaveClosedLedgerException;
import com.jasonzqshen.familyaccounting.core.exception.format.CurrencyAmountFormatException;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataBase;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.transaction.MonthIdentity;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;
import com.jasonzqshen.familyaccounting.core.utils.StringUtility;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;

public abstract class EntryActivityBase extends Activity {
	public final static String TAG = "DOC_ENTRY";

	public final static String PARAM_TEMP_ID = "TEMP_ID";

	private final SimpleDateFormat _FORMAT = new SimpleDateFormat("yyyy/MM/dd");

	private TableRow _dateRow;

	private TextView _dateValue;

	private Calendar _calendar;

	private EditText _amountEditText;

	private EditText _descpEditText;

	private IDocumentEntry _docEntry;

	private ImageButton _saveBtn;

	private ImageButton _newTempBtn;

	private EntryField[] _entryFields;

	private EntryField _entryField;

	private EntryTemplate _template;

	/**
	 * date picker click
	 */
	private DatePickerDialog.OnDateSetListener _DATE_CLICK = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			_calendar.set(Calendar.YEAR, year);
			_calendar.set(Calendar.MONTH, monthOfYear);
			_calendar.set(Calendar.DATE, dayOfMonth);

			setDate();
		}
	};

	/**
	 * on click date row
	 */
	private View.OnClickListener _DATE_ROW_CLICK = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			int year = _calendar.get(Calendar.YEAR);
			int month = _calendar.get(Calendar.MONTH);
			int date = _calendar.get(Calendar.DATE);

			new DatePickerDialog(EntryActivityBase.this, _DATE_CLICK, year,
					month, date).show();
		}
	};

	/**
	 * on click data row
	 */
	private View.OnClickListener _DATA_ROW_CLICK = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			int id = v.getId();
			for (EntryField e : _entryFields) {
				if (e.ROW_ID == id) {
					showDialog(e.DIALOG_ID);
					_entryField = e;
					return;
				}
			}
		}
	};

	/**
	 * on data selection click
	 */
	private DialogInterface.OnClickListener _DATA_SELECTION_CLICK = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			setDataRow(which);
			dialog.dismiss();
		}
	};

	/**
	 * on alert dialog OK click
	 */
	private DialogInterface.OnClickListener _ALERT_DIALOG_OK_CLICK = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
		}
	};

	/**
	 * on alert dialog OK click
	 */
	private DialogInterface.OnClickListener _SAVED_ALERT_DIALOG_OK_CLICK = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
			finish();
		}
	};

	/**
	 * new template click
	 */
	private View.OnClickListener _NEW_TEMP_CLICK = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			DataCore dataCore = DataCore.getInstance();
			boolean ret = setEntryValue(true);
			if (ret == false) {
				return;
			}

			EntryTemplatesManagement tempMgmt = dataCore
					.getTemplateManagement();
			String text;
			try {
				text = (String) _docEntry.getValue(IDocumentEntry.TEXT);
			} catch (NoFieldNameException e) {
				throw new SystemException(e);
			}

			if (StringUtility.isNullOrEmpty(text)) {
				showDialog(R.id.dialog_text_empty_warning);
				return;
			}
			_template = tempMgmt.saveAsTemplate(_docEntry, text);
			if (_template == null) {
				showDialog(R.id.dialog_template_save_with_failure);
			} else {
				showDialog(R.id.dialog_template_saved);
			}
		}
	};

	/**
	 * on save button click
	 */
	private View.OnClickListener _SAVE_CLICK = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			boolean flag = false;
			MonthIdentity[] monthIds = DataCore.getInstance().getCoreDriver()
					.getAllMonthIds();
			for (MonthIdentity monthId : monthIds) {
				int month = _calendar.get(Calendar.MONTH) + 1;
				int year = _calendar.get(Calendar.YEAR);
				if (monthId._fiscalMonth == month
						&& monthId._fiscalYear == year) {
					flag = true;
					break;
				}
			}
			if (flag == false) {
				showDialog(R.id.dialog_date_error);
				return;
			}

			boolean ret = setEntryValue(false);
			if (ret == false) {
				return;
			}
			try {
				_docEntry.save(true);
			} catch (MandatoryFieldIsMissing e) {
				Log.e(TAG, e.toString());
				throw new SystemException(e);
			} catch (SaveClosedLedgerException e) {
				throw new SystemException(e);
			}

			showDialog(R.id.dialog_document_saved);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getContentViewId());

		// get data core
		CoreDriver coreDriver = DataCore.getInstance().getCoreDriver();
		if (coreDriver.isInitialized() == false) {
			return;
		}
		// get pass in parameter
		Integer templateId = (Integer) this.getIntent().getSerializableExtra(
				PARAM_TEMP_ID);
		if (templateId != null) {
			EntryTemplatesManagement tempMgmt = DataCore.getInstance()
					.getTemplateManagement();
			EntryTemplate template = tempMgmt.getEntryTemplate(templateId);
			_docEntry = template.generateEntry();
		} else {
			_docEntry = constructDocEntry(coreDriver);
		}
		_entryFields = getEntryFields();

		// set data row
		for (EntryField entryField : _entryFields) {
			TableRow dataRow = (TableRow) this.findViewById(entryField.ROW_ID);
			dataRow.setOnClickListener(_DATA_ROW_CLICK);
		}

		// posting date
		_dateRow = (TableRow) this.findViewById(R.id.dateRow);
		_dateRow.setOnClickListener(_DATE_ROW_CLICK);
		_dateValue = (TextView) this.findViewById(R.id.dateValue);

		_calendar = Calendar.getInstance();

		// amount
		_amountEditText = (EditText) this.findViewById(R.id.amountValue);

		// save button
		_saveBtn = (ImageButton) this.findViewById(R.id.save_icon);
		_saveBtn.setOnClickListener(_SAVE_CLICK);

		// new template button
		_newTempBtn = (ImageButton) this.findViewById(R.id.new_temp_icon);
		_newTempBtn.setOnClickListener(_NEW_TEMP_CLICK);
		// description
		_descpEditText = (EditText) this.findViewById(R.id.text);

		// set default value
		setDefaultValue();
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		for (EntryField e : _entryFields) {
			if (e.DIALOG_ID == id) {
				_entryField = e;
				try {
					Object[] objs = _docEntry
							.getValueSet(_entryField.FIELD_NAME);
					CharSequence[] items = new CharSequence[objs.length];
					for (int i = 0; i < items.length; ++i) {
						items[i] = objs[i].toString();
					}
					return new AlertDialog.Builder(this)
							.setTitle(R.string.entry_vendor_selection)
							.setSingleChoiceItems(items, e.getSelected(),
									_DATA_SELECTION_CLICK).create();
				} catch (NoFieldNameException exp) {
					exp.printStackTrace();
					Log.e(TAG, exp.toString());
					throw new SystemException(exp);
				}
			}
		}

		switch (id) {
		case R.id.dialog_amount_format_error:
			return new AlertDialog.Builder(this).setTitle(R.string.error)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setMessage(R.string.message_currency_format_error)
					.setPositiveButton(R.string.ok, _ALERT_DIALOG_OK_CLICK)
					.create();
		case R.id.dialog_amount_range_value:
			return new AlertDialog.Builder(this).setTitle(R.string.error)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setMessage(R.string.message_currency_error)
					.setPositiveButton(R.string.ok, _ALERT_DIALOG_OK_CLICK)
					.create();
		case R.id.dialog_document_saved:
			String str = this.getString(R.string.message_document_saved);
			String msg = String.format(str, _docEntry.getDocument()
					.getDocIdentity().toString());
			return new AlertDialog.Builder(this)
					.setTitle(R.string.entry_saved)
					.setIcon(android.R.drawable.ic_dialog_info)
					.setMessage(msg)
					.setPositiveButton(R.string.ok,
							_SAVED_ALERT_DIALOG_OK_CLICK).create();
		case R.id.dialog_text_empty_warning:
			return new AlertDialog.Builder(this).setTitle(R.string.error)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setMessage(R.string.message_text_required)
					.setPositiveButton(R.string.ok, _ALERT_DIALOG_OK_CLICK)
					.create();
		case R.id.dialog_template_save_with_failure:
			return new AlertDialog.Builder(this).setTitle(R.string.error)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setMessage(R.string.message_template_save_with_failure)
					.setPositiveButton(R.string.ok, _ALERT_DIALOG_OK_CLICK)
					.create();
		case R.id.dialog_template_saved:
			String templateMsg = this
					.getString(R.string.message_template_saved);
			templateMsg = String.format(templateMsg, _template.getName());
			templateMsg = templateMsg + _template.getIdentity();
			return new AlertDialog.Builder(this)
					.setTitle(R.string.entry_template_saved)
					.setIcon(android.R.drawable.ic_dialog_info)
					.setMessage(templateMsg)
					.setPositiveButton(R.string.ok,
							_SAVED_ALERT_DIALOG_OK_CLICK).create();
		case R.id.dialog_date_error:
			String dateMsg = 
					this.getString(R.string.message_date_error);

			return new AlertDialog.Builder(this).setTitle(R.string.error)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setMessage(dateMsg).setPositiveButton(R.string.ok, null)
					.create();
		}

		return null;
	}

	/**
	 * set posting date
	 */
	private void setDate() {
		Date date = _calendar.getTime();
		try {
			_docEntry.setValue(IDocumentEntry.POSTING_DATE, date);
		} catch (NoFieldNameException e) {
			Log.e(TAG, e.toString());
			throw new SystemException(e);
		} catch (NotInValueRangeException e) {
			Log.e(TAG, e.toString());
			throw new SystemException(e);
		}

		String dateStr = _FORMAT.format(_calendar.getTime());
		_dateValue.setText(dateStr);
	}

	/**
	 * set data row
	 * 
	 * @param dialog
	 * @param which
	 */
	protected void setDataRow(int which) {
		try {
			_entryField.setSelected(which);

			MasterDataBase selectedValue = _docEntry
					.getValueSet(_entryField.FIELD_NAME)[which];
			_docEntry.setValue(_entryField.FIELD_NAME,
					selectedValue.getIdentity());

			TextView valueField = (TextView) this
					.findViewById(_entryField.VALUE_ID);
			valueField.setText(selectedValue.toString());
		} catch (NoFieldNameException e) {
			e.printStackTrace();
			Log.e(TAG, e.toString());
			throw new SystemException(e);// bug
		} catch (NotInValueRangeException e) {
			e.printStackTrace();
			Log.e(TAG, e.toString());
			throw new SystemException(e);// bug
		}
	}

	/**
	 * set text and amount value into document entry
	 */
	private boolean setEntryValue(boolean isTemplate) {
		// set the amount and text
		String str = _amountEditText.getText().toString();
		CurrencyAmount amount;
		try {
			amount = CurrencyAmount.parse(str);
			_docEntry.setValue(IDocumentEntry.AMOUNT, amount);
		} catch (CurrencyAmountFormatException e) {
			// show dialog
			if (isTemplate == false) {
				showDialog(R.id.dialog_amount_format_error);
				return false;
			}
		} catch (NoFieldNameException e) {
			Log.e(TAG, e.toString());
			throw new SystemException(e);
		} catch (NotInValueRangeException e) {
			// show dialog
			showDialog(R.id.dialog_amount_range_value);
			return false;
		}

		str = _descpEditText.getText().toString();
		try {
			_docEntry.setValue(IDocumentEntry.TEXT, str);
		} catch (NoFieldNameException e) {
			Log.e(TAG, e.toString());
			throw new SystemException(e);
		} catch (NotInValueRangeException e) {
			Log.e(TAG, e.toString());
			throw new SystemException(e);
		}

		return true;
	}

	/**
	 * set default value
	 */
	private void setDefaultValue() {
		this.setDate();
		try {
			// set amount
			CurrencyAmount amount = (CurrencyAmount) _docEntry
					.getValue(IDocumentEntry.AMOUNT);
			if (amount != null) {
				_amountEditText.setText(amount.toString());
			}

			// set text
			String text = (String) _docEntry.getValue(IDocumentEntry.TEXT);
			if (text != null) {
				_descpEditText.setText(text);
			}

			for (EntryField e : _entryFields) {
				_entryField = e;
				MasterDataIdentity id = (MasterDataIdentity) _docEntry
						.getValue(_entryField.FIELD_NAME);
				if (id == null) {
					setDataRow(0);
					continue;
				}

				MasterDataBase[] defaultValueSet = _docEntry
						.getValueSet(_entryField.FIELD_NAME);
				int i = 0;
				for (i = 0; i < defaultValueSet.length; ++i) {
					if (id.equals(defaultValueSet[i].getIdentity())) {
						break;
					}
				}
				setDataRow(i);

			}
		} catch (NoFieldNameException e1) {
			throw new SystemException(e1);
		}
	}

	/**
	 * get layout id for current activity
	 * 
	 * @return layout id
	 */
	protected abstract int getContentViewId();

	/**
	 * get the entries
	 * 
	 * @return
	 */
	protected abstract EntryField[] getEntryFields();

	/**
	 * construct document entry
	 * 
	 * @param coreDriver
	 * @return
	 */
	protected abstract IDocumentEntry constructDocEntry(CoreDriver coreDriver);

}
