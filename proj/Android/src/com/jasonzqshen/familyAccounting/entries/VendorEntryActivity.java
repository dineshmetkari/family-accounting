package com.jasonzqshen.familyAccounting.entries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.jasonzqshen.familyAccounting.R;
import com.jasonzqshen.familyAccounting.data.DataCore;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.document_entries.VendorEntry;
import com.jasonzqshen.familyaccounting.core.exception.MandatoryFieldIsMissing;
import com.jasonzqshen.familyaccounting.core.exception.NoFieldNameException;
import com.jasonzqshen.familyaccounting.core.exception.NotInValueRangeException;
import com.jasonzqshen.familyaccounting.core.exception.format.CurrencyAmountFormatException;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.masterdata.BusinessAreaMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.GLAccountMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.VendorMasterData;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;

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

public class VendorEntryActivity extends Activity {
	public final static String TAG = "VENDOR";

	private final SimpleDateFormat _FORMAT = new SimpleDateFormat("yyyy/MM/dd");

	private TableRow _dateRow;
	private TextView _dateValue;
	private Calendar _calendar;

	private EditText _amountEditText;
	private EditText _descpEditText;

	private TableRow _vendorRow;
	private TextView _vendorValue;

	private TableRow _outgoingRow;
	private TextView _outgoingValue;

	private TableRow _costRow;
	private TextView _costValue;

	private TableRow _businessAreaRow;
	private TextView _businessValue;

	private VendorEntry _vendorEntry;
	private ImageButton _saveBtn;

	private ArrayList<VendorMasterData> _VENDOR_SET;
	private int _vendorSelected = 0;
	private ArrayList<GLAccountMasterData> _OUTGOING_ACCOUNT_SET;
	private int _outgoingSelected = 0;
	private ArrayList<GLAccountMasterData> _COST_ACCOUNT_SET;
	private int _costSelected = 0;
	private ArrayList<BusinessAreaMasterData> _BUSI_VALUE_SET;
	private int _businessSelected = 0;

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

			new DatePickerDialog(VendorEntryActivity.this, _DATE_CLICK, year,
					month, date).show();
		}
	};

	/**
	 * on click vendor row
	 */
	private View.OnClickListener _VENDOR_ROW_CLICK = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			showDialog(R.id.dialog_vendor_selection);
		}
	};

	/**
	 * on vendor selection click
	 */
	private DialogInterface.OnClickListener _VENDOR_SELECTION_CLICK = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			_vendorSelected = which;
			setVendor();
			dialog.dismiss();
		}
	};

	/**
	 * on click outgoing account row
	 */
	private View.OnClickListener _OUTGOING_ROW_CLICK = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			showDialog(R.id.dialog_outgoing_selection);
		}
	};

	/**
	 * on outgoing account selection click
	 */
	private DialogInterface.OnClickListener _OUTGOING_SELECTION_CLICK = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			_outgoingSelected = which;
			setOutgoingAccount();
			dialog.dismiss();
		}
	};

	/**
	 * on click cost account row
	 */
	private View.OnClickListener _COST_ROW_CLICK = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			showDialog(R.id.dialog_cost_selection);
		}
	};

	/**
	 * on cost account selection click
	 */
	private DialogInterface.OnClickListener _COST_SELECTION_CLICK = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			_costSelected = which;
			setCostAccount();
			dialog.dismiss();
		}
	};

	/**
	 * on click business area row
	 */
	private View.OnClickListener _BUSINESS_ROW_CLICK = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			showDialog(R.id.dialog_business_area_selection);
		}
	};

	/**
	 * on cost business area selection click
	 */
	private DialogInterface.OnClickListener _BUSINESS_SELECTION_CLICK = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			_businessSelected = which;
			setBusiness();
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
	 * on save button click
	 */
	private View.OnClickListener _SAVE_CLICK = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// set the amount and text
			String str = _amountEditText.getText().toString();
			CurrencyAmount amount;
			try {
				amount = CurrencyAmount.parse(str);
			} catch (CurrencyAmountFormatException e) {
				// show dialog
				showDialog(R.id.dialog_amount_format_error);
				return;
			}

			try {
				_vendorEntry.setValue(VendorEntry.AMOUNT, amount);
			} catch (NoFieldNameException e) {
				Log.e(TAG, e.toString());
				throw new SystemException(e);
			} catch (NotInValueRangeException e) {
				// show dialog
				showDialog(R.id.dialog_amount_range_value);
			}

			str = _descpEditText.getText().toString();
			try {
				_vendorEntry.setValue(VendorEntry.TEXT, str);
			} catch (NoFieldNameException e) {
				Log.e(TAG, e.toString());
				throw new SystemException(e);
			} catch (NotInValueRangeException e) {
				Log.e(TAG, e.toString());
				throw new SystemException(e);
			}

			try {
				_vendorEntry.save(true);
			} catch (MandatoryFieldIsMissing e) {
				Log.e(TAG, e.toString());
				throw new SystemException(e);
			}

			showDialog(R.id.dialog_document_saved);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.outgoing_entry);

		CoreDriver coreDriver = DataCore.getInstance().getCoreDriver();
		if (coreDriver.isInitialized() == false) {
			return;
		}

		_vendorEntry = new VendorEntry(coreDriver);

		// get value set
		try {
			_VENDOR_SET = new ArrayList<VendorMasterData>();
			Object[] objs = _vendorEntry.getValueSet(VendorEntry.VENDOR);
			for (Object obj : objs) {
				_VENDOR_SET.add((VendorMasterData) obj);
			}

			_OUTGOING_ACCOUNT_SET = new ArrayList<GLAccountMasterData>();
			objs = _vendorEntry.getValueSet(VendorEntry.REC_ACC);
			for (Object obj : objs) {
				_OUTGOING_ACCOUNT_SET.add((GLAccountMasterData) obj);
			}

			_COST_ACCOUNT_SET = new ArrayList<GLAccountMasterData>();
			objs = _vendorEntry.getValueSet(VendorEntry.GL_ACCOUNT);
			for (Object obj : objs) {
				_COST_ACCOUNT_SET.add((GLAccountMasterData) obj);
			}

			_BUSI_VALUE_SET = new ArrayList<BusinessAreaMasterData>();
			objs = _vendorEntry.getValueSet(VendorEntry.BUSINESS_AREA);
			for (Object obj : objs) {
				_BUSI_VALUE_SET.add((BusinessAreaMasterData) obj);
			}

		} catch (NoFieldNameException e) {
			throw new SystemException(e);
		}

		// posting date
		_dateRow = (TableRow) this.findViewById(R.id.dateRow);
		_dateRow.setOnClickListener(_DATE_ROW_CLICK);
		_dateValue = (TextView) this.findViewById(R.id.dateValue);

		_calendar = Calendar.getInstance();

		// amount
		_amountEditText = (EditText) this.findViewById(R.id.amountValue);

		// vendor
		_vendorRow = (TableRow) this.findViewById(R.id.vendorRow);
		_vendorRow.setOnClickListener(_VENDOR_ROW_CLICK);
		_vendorValue = (TextView) this.findViewById(R.id.vendorValue);

		// outgoing account
		_outgoingRow = (TableRow) this.findViewById(R.id.outgoingRow);
		_outgoingRow.setOnClickListener(_OUTGOING_ROW_CLICK);
		_outgoingValue = (TextView) this.findViewById(R.id.outgoingValue);

		// cost account
		_costRow = (TableRow) this.findViewById(R.id.costRow);
		_costRow.setOnClickListener(_COST_ROW_CLICK);
		_costValue = (TextView) this.findViewById(R.id.costValue);

		// business
		_businessAreaRow = (TableRow) this.findViewById(R.id.businessAreaRow);
		_businessAreaRow.setOnClickListener(_BUSINESS_ROW_CLICK);
		_businessValue = (TextView) this.findViewById(R.id.businessAreaValue);

		// save button
		_saveBtn = (ImageButton) this.findViewById(R.id.save_icon);
		_saveBtn.setOnClickListener(_SAVE_CLICK);

		// description
		_descpEditText = (EditText) this.findViewById(R.id.text);

	}

	@Override
	protected void onResume() {
		super.onResume();
		
		this.setDate();
		this.setVendor();
		this.setOutgoingAccount();
		this.setCostAccount();
		this.setBusiness();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case R.id.dialog_vendor_selection:
			CharSequence[] vendorItems = new CharSequence[_VENDOR_SET.size()];
			for (int i = 0; i < vendorItems.length; ++i) {
				vendorItems[i] = _VENDOR_SET.get(i).toString();
			}
			return new AlertDialog.Builder(this)
					.setTitle(R.string.entry_vendor_selection)
					.setSingleChoiceItems(vendorItems, _vendorSelected,
							_VENDOR_SELECTION_CLICK).create();
		case R.id.dialog_outgoing_selection:
			CharSequence[] outgoingItems = new CharSequence[_OUTGOING_ACCOUNT_SET
					.size()];
			for (int i = 0; i < outgoingItems.length; ++i) {
				outgoingItems[i] = _OUTGOING_ACCOUNT_SET.get(i).toString();
			}
			return new AlertDialog.Builder(this)
					.setTitle(R.string.entry_outgoing_selection)
					.setSingleChoiceItems(outgoingItems, _outgoingSelected,
							_OUTGOING_SELECTION_CLICK).create();
		case R.id.dialog_cost_selection:
			CharSequence[] costItems = new CharSequence[_COST_ACCOUNT_SET
					.size()];
			for (int i = 0; i < costItems.length; ++i) {
				costItems[i] = _COST_ACCOUNT_SET.get(i).toString();
			}
			return new AlertDialog.Builder(this)
					.setTitle(R.string.entry_cost_selection)
					.setSingleChoiceItems(costItems, _costSelected,
							_COST_SELECTION_CLICK).create();
		case R.id.dialog_business_area_selection:
			CharSequence[] busiItems = new CharSequence[_BUSI_VALUE_SET.size()];
			for (int i = 0; i < busiItems.length; ++i) {
				busiItems[i] = _BUSI_VALUE_SET.get(i).toString();
			}
			return new AlertDialog.Builder(this)
					.setTitle(R.string.entry_business_selection)
					.setSingleChoiceItems(busiItems, this._businessSelected,
							_BUSINESS_SELECTION_CLICK).create();
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
			String msg = String.format(str, _vendorEntry.getDocument()
					.getDocIdentity().toString());
			return new AlertDialog.Builder(this)
					.setTitle(R.string.entry_saved)
					.setIcon(android.R.drawable.ic_dialog_info)
					.setMessage(msg)
					.setPositiveButton(R.string.ok,
							_SAVED_ALERT_DIALOG_OK_CLICK).create();
		}

		return null;
	}

	/**
	 * set posting date
	 */
	protected void setDate() {
		Date date = _calendar.getTime();
		try {
			_vendorEntry.setValue(VendorEntry.POSTING_DATE, date);
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
	 * set vendor value
	 */
	protected void setVendor() {
		VendorMasterData vendor = _VENDOR_SET.get(_vendorSelected);

		try {
			_vendorEntry.setValue(VendorEntry.VENDOR, vendor.getIdentity());
		} catch (NoFieldNameException e) {
			Log.e(TAG, e.toString());
			throw new SystemException(e);
		} catch (NotInValueRangeException e) {
			Log.e(TAG, e.toString());
			throw new SystemException(e);
		}

		// set vendor text view
		_vendorValue.setText(vendor.getDescp());
	}

	/**
	 * set outgoing account
	 */
	protected void setOutgoingAccount() {
		GLAccountMasterData outgoing = _OUTGOING_ACCOUNT_SET
				.get(_outgoingSelected);

		try {
			_vendorEntry.setValue(VendorEntry.REC_ACC, outgoing.getIdentity());
		} catch (NoFieldNameException e) {
			Log.e(TAG, e.toString());
			throw new SystemException(e);
		} catch (NotInValueRangeException e) {
			Log.e(TAG, e.toString());
			throw new SystemException(e);
		}

		// set outgoing text view
		_outgoingValue.setText(outgoing.getDescp());
	}

	/**
	 * set cost account
	 */
	protected void setCostAccount() {
		GLAccountMasterData cost = _COST_ACCOUNT_SET.get(_costSelected);

		try {
			_vendorEntry.setValue(VendorEntry.GL_ACCOUNT, cost.getIdentity());
		} catch (NoFieldNameException e) {
			Log.e(TAG, e.toString());
			throw new SystemException(e);
		} catch (NotInValueRangeException e) {
			Log.e(TAG, e.toString());
			throw new SystemException(e);
		}

		// set outgoing text view
		_costValue.setText(cost.getDescp());
	}

	/**
	 * set business area
	 */
	protected void setBusiness() {
		BusinessAreaMasterData business = _BUSI_VALUE_SET
				.get(_businessSelected);

		try {
			_vendorEntry.setValue(VendorEntry.BUSINESS_AREA,
					business.getIdentity());
		} catch (NoFieldNameException e) {
			Log.e(TAG, e.toString());
			throw new SystemException(e);
		} catch (NotInValueRangeException e) {
			Log.e(TAG, e.toString());
			throw new SystemException(e);
		}

		// set vendor text view
		_businessValue.setText(business.getDescp());
	}
}
