package com.jasonzqshen.familyaccounting.core.utils;

import java.io.Serializable;

import com.jasonzqshen.familyaccounting.core.exception.format.CurrencyAmountFormatException;

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
		set(value);
	}

	public CurrencyAmount(CurrencyAmount amount) {
		if (amount == null) {
			_value = 0;
			return;
		}

		_value = amount._value;
	}

	/**
	 * +=
	 * 
	 * @param amount
	 */
	public void addTo(CurrencyAmount amount) {
		_value += amount._value;
	}

	/**
	 * -=
	 * 
	 * @param amount
	 */
	public void minusTo(CurrencyAmount amount) {
		_value -= amount._value;
	}

	/**
	 * 
	 * @param amount
	 */
	public void set(CurrencyAmount amount) {
		if (amount == null) {
			return;
		}

		_value = amount._value;
	}

	public void set(double value) {
		_value = (int) (value * 100);
	}

	@Override
	public int compareTo(CurrencyAmount arg0) {
		return this._value - arg0._value;
	}

	/**
	 * == 0
	 * 
	 * @return
	 */
	public boolean isZero() {
		return this._value == 0;
	}

	/**
	 * < 0
	 * 
	 * @return
	 */
	public boolean isNegative() {
		return this._value < 0;
	}

	/**
	 * 
	 */
	public void negate() {
		this._value = 0 - this._value;
	}

	/**
	 * oper1 + oper2
	 * 
	 * @param oper1
	 * @param oper2
	 * @return
	 */
	public static CurrencyAmount add(CurrencyAmount oper1, CurrencyAmount oper2) {
		CurrencyAmount cur = new CurrencyAmount();
		cur.addTo(oper1);
		cur.addTo(oper2);

		return cur;
	}

	/**
	 * oper1 - oper2
	 * 
	 * @param oper1
	 * @param oper2
	 * @return
	 */
	public static CurrencyAmount minus(CurrencyAmount oper1,
			CurrencyAmount oper2) {
		CurrencyAmount cur = new CurrencyAmount();
		cur.addTo(oper1);
		cur.minusTo(oper2);

		return cur;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CurrencyAmount)) {
			return false;
		}
		CurrencyAmount amount = (CurrencyAmount) obj;
		return this._value == amount._value;
	}

	@Override
	public String toString() {
		double value = this.toNumber();
		return String.format("%.2f", value);
	}

	/**
	 * to number
	 * 
	 * @return
	 */
	public double toNumber() {
		return _value / 100.0;
	}

	/**
	 * parse value
	 * 
	 * @param value
	 * @return
	 * @throws CurrencyAmountFormatException
	 */
	public static CurrencyAmount parse(String value)
			throws CurrencyAmountFormatException {
		try {
			double v = Double.parseDouble(value);

			return new CurrencyAmount(v);
		} catch (NumberFormatException e) {
			throw new CurrencyAmountFormatException(e.toString());
		}
	}

}