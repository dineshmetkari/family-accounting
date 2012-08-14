package com.jasonzqshen.familyaccounting.core.transaction;

import java.util.Hashtable;

import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;

public class GLAccountBalanceItem {
	private final Hashtable<MonthIdentity, CurrencyAmount> _list;
	private final MasterDataIdentity_GLAccount _glAccount;
	private CurrencyAmount _sum = new CurrencyAmount();

	GLAccountBalanceItem(MasterDataIdentity_GLAccount glAccount) {
		_glAccount = glAccount;
		_list = new Hashtable<MonthIdentity, CurrencyAmount>();

	}

	/**
	 * get G/L account
	 * 
	 * @return
	 */
	public MasterDataIdentity_GLAccount getGLAccount() {
		return _glAccount;
	}

	/**
	 * add value
	 * 
	 * @param monthId
	 *            month identity
	 * @param value
	 */
	void addAmount(MonthIdentity monthId, CurrencyAmount amount) {
		_sum.addTo(amount);

		if (_list.contains(monthId)) {
			CurrencyAmount sum = _list.get(monthId);
			sum.addTo(amount);
		} else {
			_list.put(monthId, new CurrencyAmount(amount));
		}
	}

	/**
	 * get G/L account balance in
	 * 
	 * @param startId
	 * @return
	 */
	public CurrencyAmount getSumAmount(MonthIdentity startId,
			MonthIdentity endId) {
		CurrencyAmount sum = new CurrencyAmount();
		for (MonthIdentity id : _list.keySet()) {
			if (id.compareTo(startId) >= 0 && id.compareTo(endId) <= 0) {
				sum.addTo(_list.get(id));
			}
		}

		return sum;
	}

	/**
	 * get G/L account sum
	 * 
	 * @return
	 */
	public CurrencyAmount getSumAmount() {
		return new CurrencyAmount(_sum);
	}

	/**
	 * get amount for current month
	 * 
	 * @param id
	 * @return
	 */
	public CurrencyAmount getAmount(MonthIdentity id) {
		if (_list.contains(id)) {
			CurrencyAmount amount = _list.get(id);
			return new CurrencyAmount(amount);
		}
		return new CurrencyAmount();
	}
}
