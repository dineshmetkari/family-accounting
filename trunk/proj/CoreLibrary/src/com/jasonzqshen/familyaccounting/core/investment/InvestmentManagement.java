package com.jasonzqshen.familyaccounting.core.investment;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.ManagementBase;
import com.jasonzqshen.familyaccounting.core.exception.format.FormatException;
import com.jasonzqshen.familyaccounting.core.exception.format.InvestmentFileFormatException;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.utils.MessageType;

public class InvestmentManagement extends ManagementBase {
    public static final String METADATA_FILE = "investment_metadata.txt";

    public static final String INVEST_FOLDER = "investment";

    private final Hashtable<MasterDataIdentity_GLAccount, InvestmentAccount> _list;

    private boolean _isInitialized;

    public InvestmentManagement(CoreDriver coreDriver) {
        super(coreDriver);
        _list = new Hashtable<MasterDataIdentity_GLAccount, InvestmentAccount>();
    }

    @Override
    public void initialize() throws FormatException {
        if (_isInitialized) {
            return;
        }

        // initialized
        if (_coreDriver.isInitialized() == false) {
            return;
        }

        // load the meta-data file
        String metaFolderPath = String.format("%s/%s",
                _coreDriver.getRootPath(), INVEST_FOLDER);
        String metaFilePath = String.format("%s/%s", metaFolderPath,
                METADATA_FILE);
        File metafile = new File(metaFilePath);
        if (metafile.exists() == false) {
            _isInitialized = true;
            _coreDriver.logDebugInfo(this.getClass(), 73,
                    "Initialized complete. No investment information before.",
                    MessageType.INFO);
            return;
        }

        try {
            FileInputStream fstream = new FileInputStream(metaFilePath);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = br.readLine()) != null) {
                String filePath = String
                        .format("%s/%s.xml", metaFolderPath, line);
                File file = new File(filePath);
                if (file.exists() == false) {
                    String msg = String.format(
                            "File for account %s does not exist.", line);
                    _coreDriver.logDebugInfo(this.getClass(), 73, msg,
                            MessageType.ERROR);
                    throw new InvestmentFileFormatException(msg);
                }

                DocumentBuilderFactory docFactory = DocumentBuilderFactory
                        .newInstance();
                DocumentBuilder builder = docFactory.newDocumentBuilder();
                Document doc = builder.parse(file);

                InvestmentAccount account = InvestmentAccount.parse(
                        _coreDriver, doc);
                addInvestmentAccount(account);
            }
        } catch (FileNotFoundException e) {
            throw new SystemException(e);
        } catch (IOException e) {
            throw new SystemException(e);
        } catch (ParserConfigurationException e) {
            throw new SystemException(e);
        } catch (SAXException e) {
            _coreDriver.logDebugInfo(this.getClass(), 73, e.toString(),
                    MessageType.ERROR);
            throw new InvestmentFileFormatException(e.toString());
        }

    }

    /**
     * check whether the management is initialized.
     * 
     * @return
     */
    public boolean isInitialized() {
        return _isInitialized;
    }

    @Override
    public void clear() {
        _list.clear();
        _isInitialized = true;
    }

    @Override
    public void establishFiles() {
    }

    /**
     * get accounts
     * 
     * @return
     */
    public ArrayList<MasterDataIdentity_GLAccount> getGLAccounts() {
        ArrayList<MasterDataIdentity_GLAccount> ret = new ArrayList<MasterDataIdentity_GLAccount>(
                _list.keySet());
        Collections.sort(ret);
        return ret;
    }

    /**
     * get count of investment account
     * 
     * @return
     */
    public int getAccountsCount() {
        return _list.size();
    }

    /**
     * get investment accounts
     * 
     * @return
     */
    public ArrayList<InvestmentAccount> getInvestmentAccounts() {
        ArrayList<InvestmentAccount> ret = new ArrayList<InvestmentAccount>(
                _list.values());
        Collections.sort(ret);
        return ret;
    }

    /**
     * add investment account
     * 
     * @param investAcc
     */
    private void addInvestmentAccount(InvestmentAccount investAcc) {
        if (_list.containsKey(investAcc.getAccount())) {
            return;
        }
        _list.put(investAcc.getAccount(), investAcc);
    }
}
