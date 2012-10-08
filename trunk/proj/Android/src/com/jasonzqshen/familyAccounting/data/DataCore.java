package com.jasonzqshen.familyAccounting.data;

import java.io.File;
import java.util.Locale;

import android.os.Environment;
import android.util.Log;

import com.jasonzqshen.familyAccounting.exceptions.CoreDriverInitException;
import com.jasonzqshen.familyAccounting.exceptions.ExternalStorageException;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.document_entries.EntryTemplatesManagement;
import com.jasonzqshen.familyaccounting.core.exception.format.FormatException;
import com.jasonzqshen.familyaccounting.core.exception.format.TemplateFormatException;
import com.jasonzqshen.familyaccounting.core.investment.InvestmentManagement;
import com.jasonzqshen.familyaccounting.core.reports.ReportsManagement;
import com.jasonzqshen.familyaccounting.core.utils.Language;

/**
 * singleton
 * 
 * @author I072485
 * 
 */
public class DataCore {
    public static final String TAG = "DataCore";

    public static final String _PACKAGE = "FamilyLedger";

    private static DataCore _instance;

    public static DataCore getInstance() {
        if (_instance == null) {
            _instance = new DataCore();
        }
        return _instance;
    }

    private CoreDriver _coreDriver;

    private EntryTemplatesManagement _tmpMgmt;

    private InvestmentManagement _investMgmt;

    private String _rootFolder;

    /**
     * constructor
     */
    private DataCore() {
        _coreDriver = new CoreDriver();
        _tmpMgmt = new EntryTemplatesManagement(_coreDriver);
        _investMgmt = new InvestmentManagement(_coreDriver);

        if (Locale.SIMPLIFIED_CHINESE.equals(Locale.getDefault())) {
            _coreDriver.setLanguage(Language.SimpleChinese);
        }
    }

    /**
     * initialize
     * 
     * @return
     * @throws ExternalStorageException
     * @throws CoreDriverInitException
     */
    public void initialize() throws ExternalStorageException,
            CoreDriverInitException {
        if (_coreDriver.isInitialized()) {
            return;
        }

        // check external storage status
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            throw new ExternalStorageException(
                    "Cannot read or writer to external storage.");
        }

        File rootDir = Environment.getExternalStorageDirectory();
        _rootFolder = String.format("%s/%s", rootDir.getAbsolutePath(),
                _PACKAGE);

        // initialize the core driver
        Log.i(TAG, "Root folder: " + _rootFolder);
        _coreDriver.setRootPath(_rootFolder);
        if (_coreDriver.isInitialized() == false) {
            throw new CoreDriverInitException(
                    "CoreDriver initialize with failure");
        }

        // initialize the template management
        try {
            _tmpMgmt.initialize();
        } catch (TemplateFormatException e) {
            throw new CoreDriverInitException(
                    "CoreDriver initialize with failure");
        }

        // initialize the investment management
        try {
            _investMgmt.initialize();
        } catch (FormatException e) {
            throw new CoreDriverInitException(
                    "CoreDriver initialize with failure");
        }
    }

    /**
     * get core driver
     * 
     * @return
     */
    public CoreDriver getCoreDriver() {
        return _coreDriver;
    }

    /**
     * get reports management
     * 
     * @return
     */
    public ReportsManagement getReportsManagement() {
        return _coreDriver.getReportsManagement();
    }

    /**
     * get template management
     * 
     * @return
     */
    public EntryTemplatesManagement getTemplateManagement() {
        return _tmpMgmt;
    }

    /**
     * get investment template
     * 
     * @return
     */
    public InvestmentManagement getInvestMgmt() {
        return _investMgmt;
    }

}
