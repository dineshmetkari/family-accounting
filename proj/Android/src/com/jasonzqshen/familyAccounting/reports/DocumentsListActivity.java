package com.jasonzqshen.familyAccounting.reports;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import com.jasonzqshen.familyAccounting.EntriesDialogBuilder;
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
import com.jasonzqshen.familyaccounting.core.reports.DocumentBusinessIndex;
import com.jasonzqshen.familyaccounting.core.reports.DocumentIndex;
import com.jasonzqshen.familyaccounting.core.reports.DocumentIndexItem;
import com.jasonzqshen.familyaccounting.core.reports.DocumentIndexItemWithBalance;
import com.jasonzqshen.familyaccounting.core.reports.ReportsManagement;
import com.jasonzqshen.familyaccounting.core.transaction.GLAccountBalanceCollection;
import com.jasonzqshen.familyaccounting.core.transaction.GLAccountBalanceItem;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntityComparors;
import com.jasonzqshen.familyaccounting.core.transaction.ItemEntity;
import com.jasonzqshen.familyaccounting.core.transaction.MonthIdentity;
import com.jasonzqshen.familyaccounting.core.transaction.MonthLedger;
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
import android.widget.ImageButton;
import android.widget.Spinner;

public class DocumentsListActivity extends ListActivity {
    public final static String TAG = "DocumentsListActivity";

    private DataCore _dataCore;

    private Spinner _monthFilter;

    private Spinner _groupSpinner;

    private Button _valueFilter;

    private DocumentsListAdapter _listAdapter;

    private DocumentsListAdapterItem _selectedDocItem;

    /**
     * set long click listener
     */
    private final AdapterView.OnItemLongClickListener _ITEM_LONG_CLICK_LISTENER = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapter, View view,
                int position, long id) {
            _selectedDocItem = (DocumentsListAdapterItem) _listAdapter
                    .getItem(position);
            if (_selectedDocItem.Type == DocumentsListAdapterItem.HEAD_VIEW) {
                return false;
            }

            showDialog(R.id.dialog_doc_menu);
            return true;
        }
    };

    /**
     * document menu click
     */
    private final DialogInterface.OnClickListener _DOC_MENU_CLICK_LISTENER = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int position) {
            switch (position) {
            case 0:
                // reverse
                showDialog(R.id.dialog_reverse_confirm);
                break;
            }
            dialog.dismiss();
        }
    };

    /**
     * reverse ok click
     */
    private final DialogInterface.OnClickListener _REVERSE_OK_LISTENER = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            CoreDriver coreDriver = _dataCore.getCoreDriver();
            if (coreDriver.isInitialized() == false) {
                return;
            }

            TransactionDataManagement transMgmt = coreDriver
                    .getTransDataManagement();
            HeadEntity reversingDoc = transMgmt
                    .reverseDocument(_selectedDocItem.Document.getDocument()
                            .getDocIdentity());
            if (reversingDoc == null) {
                showDialog(R.id.dialog_doc_reverse_with_failure);
            } else {
                showDialog(R.id.dialog_doc_reversed);
            }

            dialog.dismiss();
        }
    };

    /**
     * reverse cancel click
     */
    private final DialogInterface.OnClickListener _DIALOG_CLOSE_LISTENER = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    };

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
     * group-by selection listener
     */
    private final OnItemSelectedListener _GROUP_SELECTION_LISTENER = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapter, View view,
                int position, long id) {
            Log.i(TAG, "Group-By select on " + position);
            setDocListData();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapter) {
        }

    };

    /**
     * value selection listener
     */
    private final View.OnClickListener _VALUE_SELECTION_LISTENER = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = _groupSpinner.getSelectedItemPosition();
            switch (position) {
            case DocListParam.ACCOUNT_CATEGORY:
                showDialog(R.id.dialog_account_selection);
                break;
            case DocListParam.BUSINESS_CATEGORY:
                showDialog(R.id.dialog_business_selection);
                break;
            case DocListParam.DATE_CATEGORY:
                showDialog(R.id.dialog_date_selection);
                break;
            }
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
                break;
            case DocListParam.BUSINESS_CATEGORY:
                _businessAreaSelection[clicked] = selected;
                break;
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
        _monthSpinnerAdapter
                .setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        _monthFilter.setAdapter(_monthSpinnerAdapter);

        // group by selection
        _groupSpinner = (Spinner) this.findViewById(R.id.groupSpinner);
        _groupSpinner.setOnItemSelectedListener(_GROUP_SELECTION_LISTENER);
        _groupSpinner.setSelection(DocListParam.DATE_CATEGORY);

        // set value filter
        _valueFilter = (Button) this.findViewById(R.id.valueFilter);
        _valueFilter.setOnClickListener(_VALUE_SELECTION_LISTENER);

        // set default value of month identity
        _monthFilter.setSelection(_monthValueSet.length - 1);

        DocListParam param = (DocListParam) this.getIntent()
                .getSerializableExtra(DocListParam.PARAM_NAME);
        generateValueSet(param);

        // set long click listener
        this.getListView()
                .setOnItemLongClickListener(_ITEM_LONG_CLICK_LISTENER);

        // set the click listener of the button
        ImageButton newDocBtn = (ImageButton) this.findViewById(R.id.new_icon);
        newDocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(R.id.dialog_entries);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        setDocListData();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        CharSequence[] valueOptions = null;
        boolean[] selection = null;
        switch (id) {
        case R.id.dialog_account_selection:

            valueOptions = new CharSequence[_glAccountValueSet.size()];
            for (int i = 0; i < valueOptions.length; ++i) {
                valueOptions[i] = _glAccountValueSet.get(i).getDescp();
            }
            selection = _glAccountSelection;

            return new AlertDialog.Builder(this)
                    .setTitle(R.string.documents_select_value)
                    .setMultiChoiceItems(valueOptions, selection,
                            _VALUE_MULTIPLE_CHOICE_LISTENER)
                    .setPositiveButton(R.string.ok,
                            _VALUE_SELECTION_OK_LISTENER).create();
        case R.id.dialog_date_selection:
            SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
            valueOptions = new CharSequence[_dateValueSet.size()];
            for (int i = 0; i < valueOptions.length; ++i) {
                valueOptions[i] = format.format(_dateValueSet.get(i));
            }
            selection = _dateSelection;
            return new AlertDialog.Builder(this)
                    .setTitle(R.string.documents_select_value)
                    .setMultiChoiceItems(valueOptions, selection,
                            _VALUE_MULTIPLE_CHOICE_LISTENER)
                    .setPositiveButton(R.string.ok,
                            _VALUE_SELECTION_OK_LISTENER).create();

        case R.id.dialog_business_selection:
            valueOptions = new CharSequence[_businessAreaValueSet.size()];
            for (int i = 0; i < valueOptions.length; ++i) {
                valueOptions[i] = _businessAreaValueSet.get(i).getDescp();
            }
            selection = _businessAreaSelection;

            return new AlertDialog.Builder(this)
                    .setTitle(R.string.documents_select_value)
                    .setMultiChoiceItems(valueOptions, selection,
                            _VALUE_MULTIPLE_CHOICE_LISTENER)
                    .setPositiveButton(R.string.ok,
                            _VALUE_SELECTION_OK_LISTENER).create();
        case R.id.dialog_doc_menu:
            return new AlertDialog.Builder(this).setItems(R.array.doc_menus,
                    _DOC_MENU_CLICK_LISTENER).create();
        case R.id.dialog_reverse_confirm:
            String text = this.getString(R.string.message_reverse_confirm);
            text = String.format(text, _selectedDocItem.Document.getDocument()
                    .getDocIdentity());
            return new AlertDialog.Builder(this)
                    .setTitle(R.string.documents_reverse_doc)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(text)
                    .setPositiveButton(R.string.ok, _REVERSE_OK_LISTENER)
                    .setNegativeButton(R.string.cancel, _DIALOG_CLOSE_LISTENER)
                    .create();
        case R.id.dialog_doc_reversed:
            return new AlertDialog.Builder(this)
                    .setTitle(R.string.documents_reverse_doc)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(R.string.message_doc_reversed)
                    .setPositiveButton(R.string.ok, _DIALOG_CLOSE_LISTENER)
                    .create();
        case R.id.dialog_doc_reverse_with_failure:
            return new AlertDialog.Builder(this).setTitle(R.string.error)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(R.string.message_doc_reverse_with_failure)
                    .setPositiveButton(R.string.ok, _DIALOG_CLOSE_LISTENER)
                    .create();
        case R.id.dialog_entries:
            return EntriesDialogBuilder.buildEntriesDialog(this);
        }

        return super.onCreateDialog(id);
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
        case R.id.dialog_entries:
            AlertDialog entryDialog = (AlertDialog) dialog;
            EntriesDialogBuilder.setDataEntriesDialog(entryDialog, this);
            return;
        }

        super.onPrepareDialog(id, dialog);
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
        case DocListParam.DATE_CATEGORY:
            this.setDocList_Date();
            break;
        case DocListParam.BUSINESS_CATEGORY:
            this.setDocList_Business();
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
                if (item.getGLAccount().equals(glAccount)
                        || (glAccount == null && this.containGLAccount(item
                                .getGLAccount()))) {
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
     * set document list when group-by is business area
     */
    private void setDocList_Business() {
        // get month identities
        MonthIdentity monthId = (MonthIdentity) _monthFilter.getSelectedItem();
        ArrayList<DocumentsListAdapterItem> items = new ArrayList<DocumentsListAdapterItem>();
        CoreDriver coreDriver = _dataCore.getCoreDriver();

        if (coreDriver.isInitialized() == false) {
            return;
        }

        ReportsManagement rMgmt = _dataCore.getReportsManagement();
        DocumentBusinessIndex index = (DocumentBusinessIndex) rMgmt
                .getDocumentIndex(DocumentIndex.BUSINESS_INDEX);

        for (int i = 0; i < _businessAreaSelection.length; ++i) {
            if (_businessAreaSelection[i] == false) {
                continue;
            }
            MasterDataBase business = _businessAreaValueSet.get(i);

            DocumentIndexItemWithBalance indexItem = index
                    .getIndexItem(business.getIdentity());
            if (indexItem == null) {
                continue;
            }
            // add group head
            items.add(new DocumentsListAdapterItem(
                    DocumentsListAdapterItem.HEAD_VIEW, null, business
                            .getDescp(), indexItem.getAmount(monthId, monthId),
                    null));

            // get selected value
            ArrayList<HeadEntity> docs = indexItem
                    .getEntities(monthId, monthId);
            Collections.reverse(docs);
            for (HeadEntity headEntity : docs) {
                if (headEntity.IsReversed()) { // skip reversed document
                    continue;
                }
                // check the document is in the date value set and G/L account
                // value set
                if (containDate(headEntity.getPostingDate()) == true) {
                    ItemEntity[] itemEntities = headEntity.getItems();
                    for (ItemEntity entity : itemEntities) {
                        if (containGLAccount(entity.getGLAccount())) {
                            addDocItem(items, headEntity, entity.getGLAccount());
                            break;
                        }
                    }
                }

            }
        }

        // construct adapter
        _listAdapter = new DocumentsListAdapter(this, items);
        this.setListAdapter(_listAdapter);
    }

    /**
     * set document list when group-by is Date
     */
    private void setDocList_Date() {
        CoreDriver coreDriver = _dataCore.getCoreDriver();

        if (coreDriver.isInitialized() == false) {
            return;
        }
        ArrayList<DocumentsListAdapterItem> items = new ArrayList<DocumentsListAdapterItem>();
        // get month identities
        MonthIdentity monthId = (MonthIdentity) _monthFilter.getSelectedItem();
        TransactionDataManagement transMgmt = coreDriver
                .getTransDataManagement();
        MonthLedger monthLedger = transMgmt.getLedger(monthId);
        ArrayList<HeadEntity> docs = monthLedger.getEntitiesArrayList();
        Collections.sort(docs, HeadEntityComparors.HeadEntityComparatorByDate);
        Collections.reverse(docs);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        int dateId = 0;
        CurrencyAmount amount = null;
        for (int i = 0; i < docs.size(); ++i) {
            HeadEntity doc = docs.get(i);
            if (doc.IsReversed()) { // skip reversed document
                continue;
            }
            Date date = docs.get(i).getPostingDate();
            int valueSetIndex = this.getDateIndex(date);
            if (_dateSelection[valueSetIndex] == false) {
                continue;
            }

            if (dateId != date.getDate()) {
                // add new date
                amount = new CurrencyAmount();
                items.add(new DocumentsListAdapterItem(
                        DocumentsListAdapterItem.HEAD_VIEW, null, format
                                .format(date), amount, null));
                dateId = date.getDate();
            }

            // check the documents filter
            ItemEntity[] docItems = doc.getItems();
            boolean accFlag = false;
            boolean businessFlag = false;
            for (ItemEntity item : docItems) {
                boolean isInArea = false;
                boolean isInAccount = false;
                if (this.containBusinessArea(item.getBusinessArea())) {
                    businessFlag = true;
                    isInArea = true;
                }
                if (this.containGLAccount(item.getGLAccount())) {
                    accFlag = true;
                    isInAccount = true;
                }

                if (isInArea && isInAccount) {
                    if (item.getCDIndicator() == CreditDebitIndicator.DEBIT) {
                        amount.addTo(item.getAmount());
                    } else {
                        amount.minusTo(item.getAmount());
                    }
                }
            }

            if (accFlag && businessFlag) {
                addDocItem(items, doc, null);
            }
        }

        // construct adapter
        _listAdapter = new DocumentsListAdapter(this, items);
        this.setListAdapter(_listAdapter);
    }

    /**
     * set document list when group-by is G/L Account
     */
    private void setDocList_GLAccount() {
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
                    .getIdentity());
            if (indexItem == null) {
                continue;
            }
            GLAccountBalanceItem balItem = balCol.getBalanceItem(account
                    .getIdentity());
            // add group head
            items.add(new DocumentsListAdapterItem(
                    DocumentsListAdapterItem.HEAD_VIEW, null, account
                            .getDescp(), balItem.getAmount(monthId), null));

            // get selected value

            ArrayList<HeadEntity> docs = indexItem
                    .getEntities(monthId, monthId);
            Collections.reverse(docs);
            for (HeadEntity headEntity : docs) {
                if (headEntity.IsReversed()) { // skip reversed document
                    continue;
                }
                // check the document is in the date value set and business
                // value set
                if (containDate(headEntity.getPostingDate()) == true) {
                    boolean flag = false;
                    ItemEntity[] itemEntities = headEntity.getItems();
                    for (ItemEntity entity : itemEntities) {
                        if (containBusinessArea(entity.getBusinessArea())) {
                            flag = true;
                            break;
                        }
                    }

                    if (flag) {
                        addDocItem(items, headEntity, account.getIdentity());
                    }

                }

            }
        }

        // construct adapter
        _listAdapter = new DocumentsListAdapter(this, items);
        this.setListAdapter(_listAdapter);
    }

    /**
     * check whether the date has been selected.
     * 
     * @param date
     * @return
     */
    private boolean containDate(Date date) {
        int index = getDateIndex(date);
        if (index == -1) {
            return false;
        }

        return _dateSelection[index];
    }

    /**
     * get the date index in list
     * 
     * @param date
     * @return if return -1, there no such date.
     */
    private int getDateIndex(Date date) {
        for (int i = 0; i < _dateValueSet.size(); ++i) {
            Date d = _dateValueSet.get(i);
            if (d.getDate() == date.getDate()) {
                return i;
            }
        }

        return -1;
    }

    /**
     * check whether the G/L account has been selected.
     * 
     * @param glAccountId
     * @return
     */
    private boolean containGLAccount(MasterDataIdentity_GLAccount glAccountId) {
        for (int i = 0; i < _glAccountValueSet.size(); ++i) {
            if (_glAccountValueSet.get(i).getIdentity().equals(glAccountId)) {
                return _glAccountSelection[i];
            }
        }

        return false;
    }

    /**
     * check whether the business area has been selected.
     * 
     * @param businessId
     * @return
     */
    private boolean containBusinessArea(MasterDataIdentity businessId) {
        boolean businessIsAll = true;
        for (int j = 0; j < _businessAreaSelection.length; ++j) {
            if (_businessAreaSelection[j] == false) {
                businessIsAll = false;
                break;
            }
        }

        if (businessIsAll) {
            return true;
        }

        for (int i = 0; i < _businessAreaValueSet.size(); ++i) {
            if (_businessAreaValueSet.get(i).getIdentity().equals(businessId)) {
                return _businessAreaSelection[i];
            }
        }

        return false;
    }
}
