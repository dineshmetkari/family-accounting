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
    public static final String TEST_ROOT_FOLDER_INVESTMENT = "C:/FamilyAccountingTestData/test_data_investment";

    public static final String TEST_ROOT_MASTER_CREATION = "./master_creation";

    public static final String TEST_ROOT_TRAN_CREATION = "./tran_creation";

    public static final String TEST_ROOT_EMPTY_INIT = "./empty_init";

    public static final String TEST_ROOT_LEDGER_CLOSING = "./ledger_closing";

    public static final String TEST_ROOT_CREATE_WITH_TEMPLATE = "./create_with_template";

    public static final String TEST_ROOT_TEMPLATE_CREATION = "./template_creation";

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

    public static final String[] GL_IDS = { "1000100001", "1010100001",
            "3010100001", "4000100001", "5000100001" };

    public static final String[] VENDOR_IDS = { "SUBWAY", "BUS" };

    public static final String[] CUSTOMER_IDS = { "C1", "C2" };

    public static final String[] BUSINESS_IDS = { "WORK", "ENTERTAIN",
            "FAMILY", "TEAM_MATES", "FRIENDS", "SNACKS", "HEALTH",
            "DAILY_LIFE", "LUX_LIFE" };

    public static final String[] BANK_KEY_IDS = { "CMB", "SPDB", "ICBC" };

    public static final String[] BANK_ACCOUNT_IDS = { "CMB_6620", "CMB_6235" };

    public static final String[] DOCUMNET_NUMS = { "1000000001", "1000000002" };

    public static final String GL_ACCOUNT_CASH = "1000100001";

    public static final String GL_ACCOUNT_BANK = "1010100001";

    public static final String GL_ACCOUNT_COST = "5000100001";

    public static final String GL_ACCOUNT_ENQUITY = "3010100001";

    public static final String GL_ACCOUNT_PROFIT = "4010100001";

    public static final String VENDOR = "0000000BUS";

    public static final String CUSTOMER = "00000000C1";

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
            File metaDataFile = new File(rootPath + "/" + CoreDriver.META_DATA);
            FileWriter writer = new FileWriter(metaDataFile);
            writer.write(META_DATA_CONTENT_2012_07, 0,
                    META_DATA_CONTENT_2012_07.length());
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

            // 2012_07
            File tranFile = new File(rootPath + "/" + "transaction_data/"
                    + "2012_07.xml");
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
            File metaDataFile = new File(rootPath + "/" + CoreDriver.META_DATA);
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
