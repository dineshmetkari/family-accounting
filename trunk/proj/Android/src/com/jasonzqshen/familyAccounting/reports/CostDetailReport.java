package com.jasonzqshen.familyAccounting.reports;

import com.jasonzqshen.familyAccounting.R;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.transaction.MonthIdentity;
import com.jasonzqshen.familyaccounting.core.transaction.TransactionDataManagement;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * Cost details report
 * 
 * @author jasonzqshen@gmail.com
 * 
 */
public class CostDetailReport extends AbstractCostDetailsActivity {

    /**
     * month spinner click
     */
    private final AdapterView.OnItemSelectedListener MONTH_SPINNER_CLICK = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapter, View view,
                int position, long id) {
            setData();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapter) {
        }

    };

    /**
     * month spinner
     */
    private Spinner _monthSpinner = null;

    // month selection
    private MonthIdentity[] _monthValueSet;

    private ArrayAdapter<MonthIdentity> _monthSpinnerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CoreDriver coreDriver = this._dataCore.getCoreDriver();
        TransactionDataManagement transMgmt = coreDriver
                .getTransDataManagement();

        _monthSpinner = (Spinner) this.findViewById(R.id.startMonthSpinner);
        _monthSpinner.setOnItemSelectedListener(MONTH_SPINNER_CLICK);

        _monthValueSet = transMgmt.getAllMonthIds();
        _monthSpinnerAdapter = new ArrayAdapter<MonthIdentity>(this,
                android.R.layout.simple_spinner_item, _monthValueSet);
        _monthSpinnerAdapter
                .setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        _monthSpinner.setAdapter(_monthSpinnerAdapter);
    }

    @Override
    protected int getContentView() {
        return R.layout.cost_details_report;
    }

    @Override
    protected MonthIdentity getMonthIdentity() {
        int position = _monthSpinner.getSelectedItemPosition();
        return _monthValueSet[position];
    }

}
