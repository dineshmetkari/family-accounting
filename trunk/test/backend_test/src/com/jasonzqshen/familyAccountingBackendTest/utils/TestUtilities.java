package com.jasonzqshen.familyAccountingBackendTest.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.masterdata.*;
import com.jasonzqshen.familyaccounting.core.transaction.TransactionDataManagement;
import com.jasonzqshen.familyaccounting.core.utils.BankAccountType;
import com.jasonzqshen.familyaccounting.core.utils.CriticalLevel;

public class TestUtilities {
    private TestUtilities() {
    }

    public static final String TEST_ROOT_FOLDER = "C:/FamilyAccountingTestData/test_data";

    public static final String TEST_ROOT_MASTER_CREATION = "./master_creation";

    public static final String TEST_ROOT_TRAN_CREATION = "./tran_creation";

    public static final String TEST_ROOT_EMPTY_INIT = "./empty_init";

    public static final String TEST_ROOT_LEDGER_CLOSING = "./ledger_closing";

    public static final String TEST_BANK_KEY = "CMB";

    public static final String TEST_ACCOUNT_NUMBER = "1234123412341234";

    public static final BankAccountType TEST_BANK_ACCOUNT_TYPE = BankAccountType.SAVING_ACCOUNT;

    public static final CriticalLevel TEST_CRITICAL_LEVEL = CriticalLevel.LOW;

    public static final String TEST_BANK_KEY_ID = "CMB_6620";

    public static final String TEST_GL_ACCOUNT_GROUP = "1010";

    public static final String TEST_DOC_NUM = "1000000000";

    public static final String TEST_DOC_ID = "1000000000_2012_07";

    public static final String TEST_AMOUNT1 = "123.45";

    public static final String TEST_AMOUNT2 = "543.21";

    public static final String TEST_AMOUNT3 = "419.76";

    public static final String TEST_DESCP = "test";

    public static final String[] GL_IDS = { "1000100001", "1000100002",
            "1010100001", "1010100002", "1010100003", "1010100004",
            "1060100001", "1060100002", "1060100003", "1060100004",
            "1060100005", "1430100001", "1430100002", "1500100001",
            "1500100002", "2000100001", "2000100002", "3010100001",
            "4000100001", "4000100002", "4010100001", "4010100002",
            "4010100003", "4010100004", "5000100001", "5000100002",
            "5000100003", "5000100004", "5000100005", "5000100006",
            "5000100007" };

    public static final String[] VENDOR_IDS = { "SUBWAY", "BUS" };

    public static final String[] CUSTOMER_IDS = { "MS", "SAP" };

    public static final String[] BUSINESS_IDS = { "WORK", "ENTERTAIN",
            "FAMILY", "TEAM_MATES", "FRIENDS", "SNACKS", "HEALTH",
            "DAILY_LIFE", "LUX_LIFE" };

    public static final String[] BANK_KEY_IDS = { "CMB", "SPDB", "ICBC" };

    public static final String[] BANK_ACCOUNT_IDS = { "CMB_6620", "CMB_1002",
            "CMB_6235", "CMB_1001", "SPDB_3704", "ICBC_0001", "ICBC_1001" };

    public static final String[] DOCUMNET_NUMS = { "1000000001", "1000000002" };

    public static final String GL_ACCOUNT1 = "1000100001";

    public static final String GL_ACCOUNT2 = "1000100002";

    public static final String GL_ACCOUNT_COST = "5000100001";

    public static final String GL_ACCOUNT_ENQUITY = "3010100001";

    public static final String GL_ACCOUNT_PROFIT = "4010100001";

    public static final double AMOUNT = 100;

    public static final String VENDOR = "0000000BUS";

    public static final String CUSTOMER = "00000000MS";

    public static final String BUSINESS_AREA = "000000WORK";

    /**
     * 
     * @param type
     * @param id
     * @return
     */
    public static boolean containsID(MasterDataType type, MasterDataIdentity id) {
        String[] data = null;
        if (type == MasterDataType.BANK_ACCOUNT) {
            data = BANK_ACCOUNT_IDS;
        } else if (type == MasterDataType.BANK_KEY) {
            data = BANK_KEY_IDS;
        } else if (type == MasterDataType.CUSTOMER) {
            data = CUSTOMER_IDS;
        } else if (type == MasterDataType.BUSINESS_AREA) {
            data = BUSINESS_IDS;
        } else if (type == MasterDataType.GL_ACCOUNT) {
            data = GL_IDS;
        } else if (type == MasterDataType.VENDOR) {
            data = VENDOR_IDS;
        }
        for (String str : data) {
            try {
                MasterDataIdentity newId = new MasterDataIdentity(
                        str.toCharArray());
                if (newId.equals(id)) {
                    return true;
                }
            } catch (Exception e) {
                return false;
            }

        }
        return false;
    }

    /**
     * clear test root folder
     */
    public static File clearTestingRootFolder(String rootPath) {
        // set the root folder
        File rootFolder = new File(rootPath);
        if (!rootFolder.exists()) {
            rootFolder.mkdir();
        }

        deleteFileInFolder(rootFolder);
        return rootFolder;
    }

    private static void deleteFileInFolder(File folder) {
        if (folder.isFile()) {
            return;
        }

        for (File f : folder.listFiles()) {
            if (f.isDirectory()) {
                deleteFileInFolder(f);
            }
            f.delete();
        }
    }

    public static final String META_DATA_CONTENT_2012_07 = "cur_month=7\ncur_year=2012\nstart_month=7\nstart_year=2012";

    public static final String META_DATA_CONTENT_2012_08 = "cur_month=8\ncur_year=2012\nstart_month=8\nstart_year=2012";

    public static final String TRAN_CONTENT = "<root />";

    /**
     * establish folder with 2012 07
     * 
     * @param rootPath
     */
    public static void establishFolder2012_07(String rootPath) {
        clearTestingRootFolder(rootPath);

        try {
            // meta data
            File metaDataFile = new File(TestUtilities.TEST_ROOT_LEDGER_CLOSING
                    + "/" + CoreDriver.META_DATA);
            FileWriter writer = new FileWriter(metaDataFile);
            writer.write(META_DATA_CONTENT_2012_07, 0,
                    META_DATA_CONTENT_2012_07.length());
            writer.close();

            // master data folder
            File masterDataFolder = new File(
                    TestUtilities.TEST_ROOT_LEDGER_CLOSING + "/"
                            + MasterDataManagement.MASTER_DATA_FOLDER);
            masterDataFolder.mkdir();

            // bank account
            writer = new FileWriter(TestUtilities.TEST_ROOT_LEDGER_CLOSING
                    + "/" + MasterDataManagement.MASTER_DATA_FOLDER + '/'
                    + "bank_account.xml");
            writer.write(TRAN_CONTENT, 0, TRAN_CONTENT.length());
            writer.close();

            // bank key
            writer = new FileWriter(TestUtilities.TEST_ROOT_LEDGER_CLOSING
                    + "/" + MasterDataManagement.MASTER_DATA_FOLDER + '/'
                    + "bank_key.xml");
            writer.write(TRAN_CONTENT, 0, TRAN_CONTENT.length());
            writer.close();

            // business
            writer = new FileWriter(TestUtilities.TEST_ROOT_LEDGER_CLOSING
                    + "/" + MasterDataManagement.MASTER_DATA_FOLDER + '/'
                    + "business.xml");
            writer.write(TRAN_CONTENT, 0, TRAN_CONTENT.length());
            writer.close();

            // customer
            writer = new FileWriter(TestUtilities.TEST_ROOT_LEDGER_CLOSING
                    + "/" + MasterDataManagement.MASTER_DATA_FOLDER + '/'
                    + "customer.xml");
            writer.write(TRAN_CONTENT, 0, TRAN_CONTENT.length());
            writer.close();

            // gl_account
            writer = new FileWriter(TestUtilities.TEST_ROOT_LEDGER_CLOSING
                    + "/" + MasterDataManagement.MASTER_DATA_FOLDER + '/'
                    + "gl_account.xml");
            writer.write(TRAN_CONTENT, 0, TRAN_CONTENT.length());
            writer.close();

            // vendor
            writer = new FileWriter(TestUtilities.TEST_ROOT_LEDGER_CLOSING
                    + "/" + MasterDataManagement.MASTER_DATA_FOLDER + '/'
                    + "vendor.xml");
            writer.write(TRAN_CONTENT, 0, TRAN_CONTENT.length());
            writer.close();

            // transaction data folder
            File tranFolder = new File(TestUtilities.TEST_ROOT_LEDGER_CLOSING
                    + "/" + TransactionDataManagement.TRANSACTION_DATA_FOLDER);
            tranFolder.mkdir();

            // 2012_07
            File tranFile = new File(TestUtilities.TEST_ROOT_LEDGER_CLOSING
                    + "/" + "transaction_data/" + "2012_07.xml");
            writer = new FileWriter(tranFile);
            writer.write(TRAN_CONTENT, 0, TRAN_CONTENT.length());
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new SystemException(e);
        }

    }

    /**
     * establish folder with 2012 07
     * 
     * @param rootPath
     */
    public static void establishFolder2012_08(String rootPath) {
        clearTestingRootFolder(rootPath);

        try {
            // meta data
            File metaDataFile = new File(rootPath
                    + "/" + CoreDriver.META_DATA);
            FileWriter writer = new FileWriter(metaDataFile);
            writer.write(META_DATA_CONTENT_2012_08, 0,
                    META_DATA_CONTENT_2012_08.length());
            writer.close();

            // master data folder
            File masterDataFolder = new File(rootPath + "/"
                    + MasterDataManagement.MASTER_DATA_FOLDER);
            masterDataFolder.mkdir();

            // bank account
            writer = new FileWriter(rootPath + "/"
                    + MasterDataManagement.MASTER_DATA_FOLDER + '/'
                    + "bank_account.xml");
            writer.write(TRAN_CONTENT, 0, TRAN_CONTENT.length());
            writer.close();

            // bank key
            writer = new FileWriter(rootPath + "/"
                    + MasterDataManagement.MASTER_DATA_FOLDER + '/'
                    + "bank_key.xml");
            writer.write(TRAN_CONTENT, 0, TRAN_CONTENT.length());
            writer.close();

            // business
            writer = new FileWriter(rootPath + "/"
                    + MasterDataManagement.MASTER_DATA_FOLDER + '/'
                    + "business.xml");
            writer.write(TRAN_CONTENT, 0, TRAN_CONTENT.length());
            writer.close();

            // customer
            writer = new FileWriter(rootPath + "/"
                    + MasterDataManagement.MASTER_DATA_FOLDER + '/'
                    + "customer.xml");
            writer.write(TRAN_CONTENT, 0, TRAN_CONTENT.length());
            writer.close();

            // gl_account
            writer = new FileWriter(rootPath + "/"
                    + MasterDataManagement.MASTER_DATA_FOLDER + '/'
                    + "gl_account.xml");
            writer.write(TRAN_CONTENT, 0, TRAN_CONTENT.length());
            writer.close();

            // vendor
            writer = new FileWriter(rootPath + "/"
                    + MasterDataManagement.MASTER_DATA_FOLDER + '/'
                    + "vendor.xml");
            writer.write(TRAN_CONTENT, 0, TRAN_CONTENT.length());
            writer.close();

            // transaction data folder
            File tranFolder = new File(rootPath + "/"
                    + TransactionDataManagement.TRANSACTION_DATA_FOLDER);
            tranFolder.mkdir();

            // 2012_08
            File tranFile = new File(rootPath + "/" + "transaction_data/"
                    + "2012_08.xml");
            writer = new FileWriter(tranFile);
            writer.write(TRAN_CONTENT, 0, TRAN_CONTENT.length());
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new SystemException(e);
        }

    }

}
