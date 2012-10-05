package com.jasonzqshen.familyaccounting.core.transaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.BalanceNotZero;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityNotDefined;
import com.jasonzqshen.familyaccounting.core.exception.NullValueNotAcceptable;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.masterdata.GLAccountMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.utils.CreditDebitIndicator;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;
import com.jasonzqshen.familyaccounting.core.utils.DocumentType;
import com.jasonzqshen.familyaccounting.core.utils.MessageType;

public class ClosingManagement {
    private final ArrayList<IClosingTaskManagement> _taskMgmts;

    private final TransactionDataManagement _transDataMgmt;

    private final CoreDriver _coreDriver;

    ClosingManagement(CoreDriver coreDriver,
            TransactionDataManagement transDataMgmt) {
        _taskMgmts = new ArrayList<IClosingTaskManagement>();
        _transDataMgmt = transDataMgmt;
        _coreDriver = coreDriver;
    }

    /**
     * register task management
     * 
     * @param taskMgmt
     */
    public void registerTaskMgmt(IClosingTaskManagement taskMgmt) {
        _taskMgmts.add(taskMgmt);
    }

    /**
     * get all tasks
     * 
     * @return
     */
    public ArrayList<ITask> getAllTasks() {
        ArrayList<ITask> tasks = new ArrayList<ITask>();
        for (IClosingTaskManagement mgmt : _taskMgmts) {
            tasks.addAll(mgmt.getTasks());
        }

        return tasks;
    }

    /**
     * close ledger
     * 
     * @param monthLedger
     * @return equity document
     */
    HeadEntity closeLedger() {
        MonthLedger openLedger = _transDataMgmt.getCurrentLedger();

        // check all tasks
        for (IClosingTaskManagement taskMgmt : _taskMgmts) {
            if (taskMgmt.checkBeforeClosing(openLedger) == false) {
                return null;
            }
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd");
        Date date;
        try {
            date = format.parse(openLedger.getMonthID().toString() + "_02");
        } catch (ParseException e) {
            _coreDriver.logDebugInfo(this.getClass(), 67, e.toString(),
                    MessageType.ERROR);
            throw new SystemException(e);
        }

        // create closing document
        HeadEntity headEntity = new HeadEntity(_coreDriver,
                _coreDriver.getMasterDataManagement());
        headEntity.setPostingDate(date);
        headEntity.setDocText(MonthLedger.CLOSING_DOC_TAG);
        headEntity.setDocumentType(DocumentType.GL);
        headEntity._isClose = true;

        // items
        // all the cost and profit
        MasterDataManagement mdMgmt = _coreDriver.getMasterDataManagement();
        GLAccountBalanceCollection col = _transDataMgmt.getAccBalCol();

        GLAccountMasterData[] costAccounts = mdMgmt.getCostAccounts();
        GLAccountMasterData[] revenueAccounts = mdMgmt.getRevenueAccounts();
        CurrencyAmount balance = new CurrencyAmount();

        for (GLAccountMasterData revenueAcc : revenueAccounts) {
            GLAccountBalanceItem balItem = col.getBalanceItem(revenueAcc
                    .getIdentity());
            createLineItem(headEntity, balance, balItem);
        }
        for (GLAccountMasterData costAcc : costAccounts) {
            GLAccountBalanceItem balItem = col.getBalanceItem(costAcc
                    .getIdentity());
            createLineItem(headEntity, balance, balItem);
        }

        String infor = String.format("Equity in month %s is %s",
                openLedger.getMonthID(), balance);
        _coreDriver.logDebugInfo(this.getClass(), 109, infor, MessageType.INFO);
        if (!balance.isZero()) {
            CreditDebitIndicator indicator = CreditDebitIndicator.DEBIT;
            if (balance.isNegative()) {
                indicator = CreditDebitIndicator.CREDIT;
                balance.negate();
            }

            ItemEntity equityItem = headEntity.createEntity();
            try {
                equityItem
                        .setGLAccount(mdMgmt.getEquityAccount().getIdentity());
            } catch (NullValueNotAcceptable e) {
                _coreDriver.logDebugInfo(this.getClass(), 111, e.toString(),
                        MessageType.ERROR);
                throw new SystemException(e);
            } catch (MasterDataIdentityNotDefined e) {
                _coreDriver.logDebugInfo(this.getClass(), 114, e.toString(),
                        MessageType.ERROR);
                throw new SystemException(e);
            }
            equityItem.setAmount(indicator, balance);
        }

        boolean ret = headEntity.save(true);
        if (ret == false) {
            _coreDriver.logDebugInfo(this.getClass(), 135,
                    "Equity document save with failure.", MessageType.ERROR);
            throw new SystemException(new BalanceNotZero());
        }

        return headEntity;
    }

    /**
     * create line item
     * 
     * @param head
     * @param item
     */
    private void createLineItem(HeadEntity head, CurrencyAmount sum,
            GLAccountBalanceItem balItem) {
        CurrencyAmount amount = balItem.getSumAmount();
        if (amount.isZero()) { // == 0
            return;
        }
        sum.addTo(amount);

        CreditDebitIndicator indicator = CreditDebitIndicator.CREDIT;
        if (amount.isNegative()) {
            indicator = CreditDebitIndicator.DEBIT;
            amount.negate();
        }

        ItemEntity item = head.createEntity();
        try {
            item.setGLAccount(balItem.getGLAccount());
        } catch (NullValueNotAcceptable e) {
            _coreDriver.logDebugInfo(this.getClass(), 99, e.toString(),
                    MessageType.ERROR);
            throw new SystemException(e);
        } catch (MasterDataIdentityNotDefined e) {
            _coreDriver.logDebugInfo(this.getClass(), 99, e.toString(),
                    MessageType.ERROR);
            throw new SystemException(e);
        }

        item.setAmount(indicator, amount);

        if (indicator == CreditDebitIndicator.CREDIT) {
            String infor = String.format(
                    "Line item in equity document with G/L account: %s, -%s",
                    item.getGLAccount(), item.getAmount());
            _coreDriver.logDebugInfo(this.getClass(), 176, infor,
                    MessageType.INFO);
        } else {
            String infor = String.format(
                    "Line item in equity document with G/L account: %s, %s",
                    item.getGLAccount(), item.getAmount());
            _coreDriver.logDebugInfo(this.getClass(), 182, infor,
                    MessageType.INFO);
        }
    }
}
