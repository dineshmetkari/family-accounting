package com.jasonzqshen.familyAccounting;

import java.util.ArrayList;

import com.jasonzqshen.familyAccounting.data.DataCore;
import com.jasonzqshen.familyAccounting.entries.CheckBalanceActivity;
import com.jasonzqshen.familyAccounting.entries.CustomerEntryActivity;
import com.jasonzqshen.familyAccounting.entries.EntryActivityBase;
import com.jasonzqshen.familyAccounting.entries.GLEntryActivity;
import com.jasonzqshen.familyAccounting.entries.VendorEntryActivity;
import com.jasonzqshen.familyAccounting.reports.CostDetailReport;
import com.jasonzqshen.familyAccounting.reports.DocumentsListActivity;
import com.jasonzqshen.familyAccounting.reports.LiquidityReportsActivity;
import com.jasonzqshen.familyAccounting.settings.AccountsSettingActivity;
import com.jasonzqshen.familyAccounting.settings.BusinessAreaSettingActivity;
import com.jasonzqshen.familyAccounting.settings.CostAccountsSettingActivity;
import com.jasonzqshen.familyAccounting.settings.CustomerSettingActivity;
import com.jasonzqshen.familyAccounting.settings.RevAccountsSettingActivity;
import com.jasonzqshen.familyAccounting.settings.VendorSettingActivity;
import com.jasonzqshen.familyAccounting.utils.ActivityAction;
import com.jasonzqshen.familyAccounting.utils.CostDetailsAction;
import com.jasonzqshen.familyAccounting.utils.IAction;
import com.jasonzqshen.familyAccounting.widgets.MenuAdapter;
import com.jasonzqshen.familyAccounting.widgets.MenuAdapterItem;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.document_entries.EntryTemplate;
import com.jasonzqshen.familyaccounting.core.document_entries.EntryTemplatesManagement;
import com.jasonzqshen.familyaccounting.core.exception.FiscalMonthRangeException;
import com.jasonzqshen.familyaccounting.core.exception.FiscalYearRangeException;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.transaction.MonthIdentity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListView;

public class MainMenuActivity extends ListActivity {

	public final MenuAdapterItem CUSTOMIZEING_ENTRY_HEAD = new MenuAdapterItem(
			MenuAdapter.HEAD_TYPE, 0, R.string.menu_customizing_entry, null);

	private final MenuAdapterItem[] _MENU_ITEMS = new MenuAdapterItem[] {
			new MenuAdapterItem(MenuAdapter.HEAD_TYPE, 0, R.string.menu_entry,
					null),
			new MenuAdapterItem(MenuAdapter.ITEM_TYPE, R.drawable.new_entry,
					R.string.menu_vendor_entry, new ActivityAction(
							VendorEntryActivity.class, this)),
			new MenuAdapterItem(MenuAdapter.ITEM_TYPE, R.drawable.new_entry,
					R.string.menu_customer_entry, new ActivityAction(
							CustomerEntryActivity.class, this)),
			new MenuAdapterItem(MenuAdapter.ITEM_TYPE, R.drawable.new_entry,
					R.string.menu_gl_entry, new ActivityAction(
							GLEntryActivity.class, this)),
			new MenuAdapterItem(MenuAdapter.ITEM_TYPE, R.drawable.check,
					R.string.menu_check_balance, new ActivityAction(
							CheckBalanceActivity.class, this)),
			new MenuAdapterItem(MenuAdapter.HEAD_TYPE, 0, R.string.menu_report,
					null),
			new MenuAdapterItem(MenuAdapter.ITEM_TYPE, R.drawable.chart,
					R.string.menu_liquidity_report, new ActivityAction(
							LiquidityReportsActivity.class, this)),
			// new MenuAdapterItem(MenuAdapter.ITEM_TYPE,
			// R.drawable.chart,R.string.menu_balance_report, new
			// ActivityAction( GLAccountBalanceReportActivity.class, this)),
			// new MenuAdapterItem(MenuAdapter.ITEM_TYPE, R.drawable.chart,
			// R.string.menu_profit_loss_report, null),
			new MenuAdapterItem(MenuAdapter.ITEM_TYPE, R.drawable.chart,
					R.string.menu_cost_details, new ActivityAction(
							CostDetailReport.class, this)),
			new MenuAdapterItem(MenuAdapter.ITEM_TYPE, R.drawable.chart,
					R.string.menu_month_outgoing_report, new CostDetailsAction(
							this)),
			new MenuAdapterItem(MenuAdapter.ITEM_TYPE, R.drawable.reports,
					R.string.menu_month_records_report, new ActivityAction(
							DocumentsListActivity.class, this)),
			new MenuAdapterItem(MenuAdapter.HEAD_TYPE, 0,
					R.string.menu_settings, null),
			new MenuAdapterItem(MenuAdapter.ITEM_TYPE, R.drawable.settings,
					R.string.menu_accounts_setting, new ActivityAction(
							AccountsSettingActivity.class, this)),
			new MenuAdapterItem(MenuAdapter.ITEM_TYPE, R.drawable.settings,
					R.string.menu_cost_accounts_setting, new ActivityAction(
							CostAccountsSettingActivity.class, this)),
			new MenuAdapterItem(MenuAdapter.ITEM_TYPE, R.drawable.settings,
					R.string.menu_rev_accounts_setting, new ActivityAction(
							RevAccountsSettingActivity.class, this)),
			new MenuAdapterItem(MenuAdapter.ITEM_TYPE, R.drawable.settings,
					R.string.menu_vendor_setting, new ActivityAction(
							VendorSettingActivity.class, this)),
			new MenuAdapterItem(MenuAdapter.ITEM_TYPE, R.drawable.settings,
					R.string.menu_customer_setting, new ActivityAction(
							CustomerSettingActivity.class, this)),
			new MenuAdapterItem(MenuAdapter.ITEM_TYPE, R.drawable.settings,
					R.string.menu_area_setting, new ActivityAction(
							BusinessAreaSettingActivity.class, this)),
			new MenuAdapterItem(MenuAdapter.ITEM_TYPE, R.drawable.settings,
					R.string.menu_date_setting, new IAction() {
						@Override
						public void execute() {
							// open date selection
							DataCore dataCore = DataCore.getInstance();
							CoreDriver coreDriver = dataCore.getCoreDriver();

							MonthIdentity startMonth = coreDriver
									.getStartMonthId();
							new DatePickerDialog(MainMenuActivity.this,
									_DATE_CLICK, startMonth._fiscalYear,
									startMonth._fiscalMonth - 1, 1).show();
						}
					})
	// , new MenuAdapterItem(MenuAdapter.ITEM_TYPE, R.drawable.settings,
	// R.string.menu_advanced, null)
	};

	/**
	 * date picker click
	 */
	private DatePickerDialog.OnDateSetListener _DATE_CLICK = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			MonthIdentity month;
			try {
				month = new MonthIdentity(year, monthOfYear + 1);
			} catch (FiscalYearRangeException e) {
				throw new SystemException(e);// bug
			} catch (FiscalMonthRangeException e) {
				throw new SystemException(e);// bug
			}
			DataCore dataCore = DataCore.getInstance();
			CoreDriver coreDriver = dataCore.getCoreDriver();
			coreDriver.setStartMonthId(month);
		}
	};

	private MenuAdapter _adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);

	}

	protected void onResume() {
		super.onResume();
		refersh();
	}

	/**
	 * add template entry
	 */
	public static void addTemplateEntry(ArrayList<MenuAdapterItem> items,
			Activity activity) {
		DataCore dataCore = DataCore.getInstance();
		if (dataCore.getCoreDriver().isInitialized() == false) {
			return;
		}

		EntryTemplatesManagement tempMgmt = dataCore.getTemplateManagement();
		ArrayList<EntryTemplate> templates = tempMgmt.getEntryTemplates();
		for (EntryTemplate t : templates) {
			ActivityAction action;
			int imageId;
			switch (t.getEntryType()) {
			case EntryTemplate.CUSTOMER_ENTRY_TYPE:
				action = new ActivityAction(CustomerEntryActivity.class,
						activity);
				imageId = R.drawable.incoming;
				break;
			case EntryTemplate.VENDOR_ENTRY_TYPE:
				action = new ActivityAction(VendorEntryActivity.class, activity);
				imageId = R.drawable.outgoing;
				break;
			case EntryTemplate.GL_ENTRY_TYPE:
				action = new ActivityAction(GLEntryActivity.class, activity);
				imageId = R.drawable.internal;
				break;
			default:
				continue;
			}

			// add parameters
			action.ParamName = EntryActivityBase.PARAM_TEMP_ID;
			action.ParamValue = t.getIdentity();

			items.add(new MenuAdapterItem(MenuAdapter.ITEM_TYPE, imageId, t
					.getName(), action));
		}

	}

	/**
	 * refresh
	 */
	private void refersh() {
		ArrayList<MenuAdapterItem> items = new ArrayList<MenuAdapterItem>();
		// add customizing entry
		// items.add(CUSTOMIZEING_ENTRY_HEAD);
		// addTemplateEntry(items, this);

		for (MenuAdapterItem t : _MENU_ITEMS) {
			items.add(t);
		}
		_adapter = new MenuAdapter(this, items);
		setListAdapter(_adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		MenuAdapterItem item = (MenuAdapterItem) _adapter.getItem(position);
		if (item.Action == null) {
			showDialog(R.id.dialog_not_implement_alert);
			return;
		}
		item.Action.execute();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case R.id.dialog_not_implement_alert:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(this.getString(R.string.message_not_implement))
					.setCancelable(false)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.dismiss();
								}
							});
			dialog = builder.create();
			break;
		default:
			dialog = null;
		}
		return dialog;
	}
}
