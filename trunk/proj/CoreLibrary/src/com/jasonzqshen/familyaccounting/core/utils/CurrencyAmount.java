package com.jasonzqshen.familyaccounting.core.utils;

import java.io.Serializable;

/**
 * currency amount, ~.2
 * 
 * @author I072485
 * 
 */
public class CurrencyAmount implements Comparable<CurrencyAmount>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4225306075522416102L;
	private int _value;

	public CurrencyAmount() {
		_value = 0;
	}

	public CurrencyAmount(Double value) {
		_value = (int) (value * 100);
	}

	@Override
	public int compareTo(CurrencyAmount arg0) {
		return this._value - arg0._value;
	}

}