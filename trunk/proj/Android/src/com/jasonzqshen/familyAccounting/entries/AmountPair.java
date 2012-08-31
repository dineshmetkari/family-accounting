package com.jasonzqshen.familyAccounting.entries;

import com.jasonzqshen.familyaccounting.core.masterdata.GLAccountMasterData;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;

public class AmountPair {
    public final GLAccountMasterData GLAccount;

    public final CurrencyAmount OrgAmount;

    public final CurrencyAmount CurAmount;

    /**
     * 
     * @param glAccount
     * @param orgAmount
     * @param curAmount
     */
    public AmountPair(GLAccountMasterData glAccount, CurrencyAmount orgAmount,
            CurrencyAmount curAmount) {
        GLAccount = glAccount;
        OrgAmount = orgAmount;
        CurAmount = curAmount;
    }
}
