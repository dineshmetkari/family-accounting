package com.jasonzqshen.familyaccounting.core.masterdata;

import com.jasonzqshen.familyaccounting.core.exception.IdentityInvalidChar;
import com.jasonzqshen.familyaccounting.core.exception.IdentityNoData;
import com.jasonzqshen.familyaccounting.core.exception.IdentityTooLong;

/**
 * the length of the identity is 16 with numbers
 * 
 * @author I072485
 * 
 */
public class BankAccountNumber {
    public final static int LENGTH = 16;

    private final char[] _identity = new char[LENGTH];

    public BankAccountNumber(String id) throws IdentityTooLong, IdentityNoData,
            IdentityInvalidChar {
        this(id.toCharArray());
    }

    /**
     * Construct identity
     * 
     * @param id
     * @throws IdentityTooLong
     * @throws IdentityNoData
     * @throws IdentityInvalidChar
     */
    public BankAccountNumber(char[] id) throws IdentityTooLong, IdentityNoData,
            IdentityInvalidChar {
        if (id.length == 0) {
            throw new IdentityNoData();
        }
        if (id.length > LENGTH) {
            throw new IdentityTooLong(id.length, LENGTH);
        }

        int l = id.length;
        for (int i = LENGTH - 1; i >= 0; i--) {
            if (l + i - LENGTH >= 0) {
                // check character valid
                boolean flag = isValidChar(id[l + i - LENGTH]);
                if (flag == false) {
                    throw new IdentityInvalidChar(id[l + i - LENGTH]);
                }

                _identity[i] = id[l + i - LENGTH];
            } else {
                // leading zero
                _identity[i] = '0';
            }
        }

    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BankAccountNumber)) {
            return false;
        }
        BankAccountNumber id = (BankAccountNumber) obj;
        for (int i = 0; i < LENGTH; ++i) {
            if (id._identity[i] != _identity[i]) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int sum = 0;
        for (int i = 0; i < LENGTH; ++i) {
            sum += _identity[i];
        }
        return sum;
    }

    /**
     * to String
     */
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        for (char ch : _identity) {
            strBuilder.append(ch);
        }
        return strBuilder.toString();
    }

    /**
     * only number, char(a..z) and '_' is valid
     * 
     * @param ch
     * @return
     */
    protected boolean isValidChar(char ch) {
        if ('0' <= ch && ch <= '9') {
            return true;
        }
        return false;
    }

}
