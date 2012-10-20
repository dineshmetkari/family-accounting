package com.jasonzqshen.familyaccounting.core.reports;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;
import com.jasonzqshen.familyaccounting.core.transaction.MonthIdentity;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;

/**
 * document index item is based on the each master data
 * 
 * @author jasonzqshen@gmail.com
 * 
 */
public class DocumentIndexItem {
	protected final ArrayList<HeadEntity> _list;

	protected final Hashtable<MonthIdentity, CurrencyAmount> _amountList;

	protected final MasterDataIdentity _id;

	protected final CoreDriver _coreDriver;

	DocumentIndexItem(CoreDriver coreDriver, MasterDataIdentity id) {
		_coreDriver = coreDriver;
		_id = id;
		_list = new ArrayList<HeadEntity>();
		_amountList = new Hashtable<MonthIdentity, CurrencyAmount>();
	}

	/**
	 * get entities based on the comparator
	 * 
	 * @param comparator
	 * @return
	 */
	public ArrayList<HeadEntity> getEntities(Comparator<HeadEntity> comparator) {
		ArrayList<HeadEntity> ret = new ArrayList<HeadEntity>(_list);
		Collections.sort(ret, comparator);

		return ret;
	}

	/**
	 * get entities with default order, sorted based on Date
	 * 
	 * @return
	 */
	public ArrayList<HeadEntity> getEntities() {
		ArrayList<HeadEntity> ret = new ArrayList<HeadEntity>(_list);
		return ret;
	}

	/**
	 * get month identity
	 * 
	 * @param startId
	 * @param endId
	 * @return
	 */
	public ArrayList<HeadEntity> getEntities(MonthIdentity startId,
			MonthIdentity endId) {
		if (startId == null) {
			startId = _coreDriver.getStartMonthId();
		}
		if (endId == null) {
			endId = _coreDriver.getCurCalendarMonthId();
		}
		ArrayList<HeadEntity> ret = new ArrayList<HeadEntity>();
		for (HeadEntity head : _list) {
			if (head.getMonthId().compareTo(endId) > 0) {
				break;
			}

			if (head.getMonthId().compareTo(startId) >= 0) {
				ret.add(head);
			}

		}

		return ret;
	}

	/**
	 * get month identity
	 * 
	 * @param startId
	 * @param endId
	 * @return
	 */
	public ArrayList<HeadEntity> getEntities(Comparator<HeadEntity> comparator,
			MonthIdentity startId, MonthIdentity endId) {
		if (startId == null) {
			startId = _coreDriver.getStartMonthId();
		}
		if (endId == null) {
			endId = _coreDriver.getCurCalendarMonthId();
		}
		ArrayList<HeadEntity> ret = new ArrayList<HeadEntity>();
		for (HeadEntity head : _list) {
			if (head.getMonthId().compareTo(startId) >= 0) {
				ret.add(head);
			}
			if (head.getMonthId().compareTo(endId) > 0) {
				break;
			}
		}

		Collections.sort(ret, comparator);

		return ret;
	}

	/**
	 * get all amount
	 * 
	 * @return
	 */
	public CurrencyAmount getAmount() {
		CurrencyAmount sum = new CurrencyAmount();
		for (CurrencyAmount amount : _amountList.values()) {
			sum.addTo(amount);
		}

		return sum;
	}

	/**
	 * get amount
	 * 
	 * @param monthID
	 * @return
	 */
	public CurrencyAmount getAmount(MonthIdentity monthID) {
		CurrencyAmount sum = new CurrencyAmount();
		if (_amountList.containsKey(monthID)) {
			sum.addTo(_amountList.get(monthID));
		}

		return sum;
	}

	/**
	 * add document
	 * 
	 * @param head
	 */
	void addDoc(HeadEntity head) {
		_list.add(head);
		Collections.sort(_list, DocumentIndex.COMPARATOR_DATE);
	}

	/**
	 * add currency amount
	 * 
	 * @param monthId
	 * @param amount
	 */
	void addAmount(MonthIdentity monthId, CurrencyAmount amount) {
		if (_amountList.containsKey(monthId)) {
			CurrencyAmount v1 = _amountList.get(monthId);
			v1.addTo(amount);
			return;
		}
		// put
		_amountList.put(monthId, amount);
	}
}
