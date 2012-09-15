package com.jasonzqshen.familyaccounting.core.investment;

import java.util.ArrayList;
import java.util.Collections;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.IdentityInvalidChar;
import com.jasonzqshen.familyaccounting.core.exception.IdentityNoData;
import com.jasonzqshen.familyaccounting.core.exception.IdentityTooLong;
import com.jasonzqshen.familyaccounting.core.exception.format.InvestmentFileFormatException;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;
import com.jasonzqshen.familyaccounting.core.utils.MessageType;
import com.jasonzqshen.familyaccounting.core.utils.StringUtility;

public class InvestmentAccount implements Comparable<InvestmentAccount> {
    public static final String XML_ACCOUNT = "account";

    public static final String XML_REV_ACCOUNT = "revenue_account";

    public static final String XML_ROOT = "root";

    public static final String XML_NAME = "name";

    public static final String XML_ITEM = "investement";

    private final CoreDriver _coreDriver;

    private final MasterDataIdentity_GLAccount _account;

    /**
     * revenue account
     */
    private final MasterDataIdentity_GLAccount _revAccount;

    private final String _name;

    private final CurrencyAmount _amount;

    private final CurrencyAmount _revAmount;

    private final ArrayList<InvestmentItem> _items;

    /**
     * investment account
     * 
     * @param account
     * @param revAccount
     * @param name
     */
    InvestmentAccount(CoreDriver coreDriver,
            MasterDataIdentity_GLAccount account,
            MasterDataIdentity_GLAccount revAccount, String name) {
        _coreDriver = coreDriver;
        _account = account;
        _revAccount = revAccount;
        _name = name;

        _amount = new CurrencyAmount();
        _revAmount = new CurrencyAmount();

        _items = new ArrayList<InvestmentItem>();
    }

    /**
     * get count of investments item
     * 
     * @return
     */
    public int getItemCount() {
        return _items.size();
    }

    /**
     * get investment items
     * 
     * @return
     */
    public ArrayList<InvestmentItem> getItems() {
        ArrayList<InvestmentItem> ret = new ArrayList<InvestmentItem>(_items);
        Collections.sort(ret);

        return ret;
    }

    /**
     * get investment account
     * 
     * @return
     */
    public MasterDataIdentity_GLAccount getAccount() {
        return _account;
    }

    /**
     * get revenue account
     * 
     * @return
     */
    public MasterDataIdentity_GLAccount getRevAccount() {
        return _revAccount;
    }

    /**
     * get the name of investment account
     * 
     * @return
     */
    public String getName() {
        return _name;
    }

    /**
     * get total amount of the account
     * 
     * @return
     */
    public CurrencyAmount getTotalAmount() {
        return _amount;
    }

    /**
     * get revenue amount
     * 
     * @return
     */
    public CurrencyAmount getRevAmount() {
        return _revAmount;
    }

    @Override
    public int compareTo(InvestmentAccount another) {
        return _account.compareTo(another._account);
    }

    /**
     * add investment item
     * 
     * @param item
     */
    void addItem(InvestmentItem item) {
        _items.add(item);
        if (item.isClosed()) {
            _revAmount.addTo(item.getRevAmount());
        } else {
            _amount.addTo(item.getAmount());
        }
    }

    /**
     * parse investment document to memory
     * 
     * @param doc
     * @return
     * @throws InvestmentFileFormatException
     */
    public static InvestmentAccount parse(CoreDriver coreDriver, Document doc)
            throws InvestmentFileFormatException {
        // get root element
        NodeList nodeList = doc.getChildNodes();
        Element rootElem = null;

        // get root element
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node child = nodeList.item(i);
            if (child instanceof Element) {
                Element elem = (Element) child;
                String nodeName = elem.getNodeName();
                if (nodeName.equals(XML_ROOT)) {
                    rootElem = elem;
                    break;
                }
            }
        }
        // no root element
        if (rootElem == null) {
            coreDriver.logDebugInfo(InvestmentAccount.class, 155,
                    "No root element", MessageType.ERROR);
            throw new InvestmentFileFormatException("No root element.");
        }

        String accStr = rootElem.getAttribute(XML_ACCOUNT);
        String revAccStr = rootElem.getAttribute(XML_REV_ACCOUNT);
        String name = rootElem.getAttribute(XML_NAME);

        try {
            MasterDataIdentity_GLAccount acc = new MasterDataIdentity_GLAccount(
                    accStr);
            MasterDataIdentity_GLAccount revAcc = new MasterDataIdentity_GLAccount(
                    revAccStr);

            if (StringUtility.isNullOrEmpty(name)) {
                coreDriver
                        .logDebugInfo(InvestmentAccount.class, 171,
                                "Name of investment cannot be empty",
                                MessageType.ERROR);
                throw new InvestmentFileFormatException(
                        "Name of investment cannot be empty");
            }

            InvestmentAccount investAccount = new InvestmentAccount(coreDriver,
                    acc, revAcc, name);

            nodeList = rootElem.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); ++i) {
                Node child = nodeList.item(i);
                if (child instanceof Element) {
                    Element elem = (Element) child;
                    if (elem.getNodeName().equals(XML_ITEM)) {
                        InvestmentItem item = InvestmentItem.parse(coreDriver,
                                investAccount, elem);
                        investAccount.addItem(item);
                    }
                }
            }

            return investAccount;
        } catch (IdentityTooLong e) {
            coreDriver.logDebugInfo(InvestmentAccount.class, 171, e.toString(),
                    MessageType.ERROR);
            throw new InvestmentFileFormatException(e.toString());
        } catch (IdentityNoData e) {
            coreDriver.logDebugInfo(InvestmentAccount.class, 175, e.toString(),
                    MessageType.ERROR);
            throw new InvestmentFileFormatException(e.toString());
        } catch (IdentityInvalidChar e) {
            coreDriver.logDebugInfo(InvestmentAccount.class, 179, e.toString(),
                    MessageType.ERROR);
            throw new InvestmentFileFormatException(e.toString());
        }

    }

}
