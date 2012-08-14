package com.jasonzqshen.familyAccountingBackendTest.basic;

import static org.junit.Assert.*;

import com.jasonzqshen.familyAccountingBackendTest.utils.TesterBase;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;

public class CurrencyAmountTester extends TesterBase {

	@Override
	protected void doTest(CoreDriver coreDriver) throws Exception {
		// test to string
		CurrencyAmount amount = new CurrencyAmount(123.45);
		assertEquals("123.45", amount.toString());

		// test parse
		amount = CurrencyAmount.parse("123.45");
		assertEquals("123.45", amount.toString());

		// test add
		CurrencyAmount amount2 = new CurrencyAmount(543.21);
		amount = new CurrencyAmount(123.45);
		assertEquals("666.66", CurrencyAmount.add(amount, amount2).toString());

		amount.addTo(amount2);
		assertEquals("666.66", amount.toString());
		
		// test minus
		amount2 = new CurrencyAmount(543.21);
		amount = new CurrencyAmount(123.45);
		assertEquals("-419.76", CurrencyAmount.minus(amount, amount2).toString());
		assertEquals("419.76", CurrencyAmount.minus(amount2, amount).toString());
		assertEquals("0.00", CurrencyAmount.minus(amount2, amount2).toString());
		
		// is zero
		assertEquals(true, CurrencyAmount.minus(amount2, amount2).isZero());
	}

	@Override
	protected void check(CoreDriver coreDriver) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
