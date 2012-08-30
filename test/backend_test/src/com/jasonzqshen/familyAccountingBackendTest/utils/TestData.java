package com.jasonzqshen.familyAccountingBackendTest.utils;

import java.text.SimpleDateFormat;

import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;

public class TestData {
    // document text
    public static final String TEXT_VENDOR_DOC = "Traffic expense on work";
    public static final String TEXT_GL_DOC = "Get money from bank account";
    public static final String TEXT_CUSTOMER_DOC = "Salary";
    
    // date & month
    public static final String DATE_2012_07 = "2012.07.02";
    public static final String DATE_2012_08 = "2012.08.02";
    public static final int YEAR = 2012;
    public static final int MONTH_07 = 7;
    public static final int MONTH_08 = 8;
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd");
    
    // document number
    public static final String DOC_NUM1 = "1000000001";
    public static final String DOC_NUM2 = "1000000002";
    public static final String DOC_NUM3 = "1000000003";
    public static final String DOC_NUM4 = "1000000004";
    
    // amount
    public static final CurrencyAmount AMOUNT_VENDOR = new CurrencyAmount(123.45);
    public static final CurrencyAmount AMOUNT_CUSTOMER= new CurrencyAmount(543.21);
    public static final CurrencyAmount AMOUNT_GL = new CurrencyAmount(100.0);
    public static final CurrencyAmount AMOUNT_EQUITY = new CurrencyAmount(419.76);
    
    public static final String VENDOR_BUS = "0000000BUS";
    public static final String VENDOR_SUBWAY = "0000SUBWAY";
    public static final String CUSTOMER1 = "00000000C1";
    public static final String CUSTOMER2 = "00000000C2";
    
    public static final String VENDOR_BUS_DESCP = "Bus";
    public static final String VENDOR_SUBWAY_DESCP = "Subway";
    public static final String CUSTOMER1_DESCP = "Customer 1";
    public static final String CUSTOMER2_DESCP = "Customer 2";
    
    public static final String GL_ACCOUNT_CASH = "1000100001";
    public static final String GL_ACCOUNT_COST = "5000100001";
    public static final String GL_ACCOUNT_REV = "4000100001";
    public static final String GL_ACCOUNT_BANK = "1010100001";
    public static final String GL_ACCOUNT_EQUITY = "3010100001";
    
    public static final String GL_ACCOUNT_CASH_DESCP = "cash on hands 1";
    public static final String GL_ACCOUNT_COST_DESCP = "Traffic cost";
    public static final String GL_ACCOUNT_REV_DESCP = "salary incoming 1";
    public static final String GL_ACCOUNT_BANK_DESCP = "bank account 6235";
    public static final String GL_ACCOUNT_EQUITY_DESCP = "Equity";
    
    public static final String BUSINESS_AREA_WORK = "000000WORK";
    public static final String BUSINESS_AREA_ENTERTAIN = "0ENTERTAIN";
    public static final String BUSINESS_AREA_SNACKS = "0000SNACKS";
    
    public static final String BUSINESS_AREA_WORK_DESCP = "Work";
    public static final String BUSINESS_AREA_ENTERTAIN_DESCP = "Entertainment(or with Yanyan), like dinner, movie...";
    public static final String BUSINESS_AREA_SNACKS_DESCP = "Expense on snack";
    
    public static final String BANK_KEY = "0000000CMB";
    public static final String BANK_KEY_DESCP = "China Merchants Bank";
    
    public static final String BANK_ACCOUNT_CMB_6620 = "00CMB_6620";
    public static final String BANK_ACCOUNT_CMB_6235 = "00CMB_6235";
    public static final String BANK_ACCOUNT_CMB_6620_DESCP = "credit card";
    public static final String BANK_ACCOUNT_CMB_6235_DESCP = "bank account";
    
    public static final String BANK_ACCOUNT_CMB_6620_ACC = "0000000000006620";
    public static final String BANK_ACCOUNT_CMB_6235_ACC = "0000000000006235";
    
}
