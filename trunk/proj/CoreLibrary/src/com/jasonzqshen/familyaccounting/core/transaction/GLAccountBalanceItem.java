package com.jasonzqshen.familyaccounting.core.transaction;

import java.util.Hashtable;

import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;

public class GLAccountBalanceItem {
	private final Hashtable<MonthIdentity, Integer> _list;
	private final MasterDataIdentity_GLAccount _glAccount;
	private int _sum = 0;

	GLAccountBalanceItem(MasterDataIdentity_GLAccount glAccount) {
		_glAccount = glAccount;
		_list = new Hashtable<MonthIdentity, Integer>();
		
				
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
	void addAmount(MonthIdentity monthId, double amount) {
		int _v = (int) (amount * 100);
		_sum += _v;

		if (_list.contains(monthId)) {
			int sum = _list.get(monthId);
			sum += _v;
			_list.put(monthId, sum);
		} else {
			_list.put(monthId, _v);
		}
	}

	/**
	 * get G/L account balance in
	 * 
	 * @param startId
	 * @return
	 */
	public double getSumAmount(MonthIdentity startId, MonthIdentity endId) {
		int sum = 0;
		for (MonthIdentity id : _list.keySet()) {
			if (id.compareTo(startId) >= 0 && id.compareTo(endId) <= 0) {
				sum += _list.get(id);
			}
		}

		return sum / 100.0;
	}

	/**
	 * get G/L account sum
	 * 
	 * @return
	 */
	public double getSumAmount() {
		return _sum / 100.0;
	}

	/**
	 * get amount for current month
	 * 
	 * @param id
	 * @return
	 */
	public double getAmount(MonthIdentity id) {
		if (_list.contains(id)) {
			return _list.get(id) / 10.0;
		}
		return 0;
	}
}
