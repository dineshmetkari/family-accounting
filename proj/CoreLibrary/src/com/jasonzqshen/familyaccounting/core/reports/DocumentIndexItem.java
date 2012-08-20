package com.jasonzqshen.familyaccounting.core.reports;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;
import com.jasonzqshen.familyaccounting.core.transaction.MonthIdentity;

public class DocumentIndexItem {
	protected final ArrayList<HeadEntity> _list;
	protected final MasterDataIdentity _id;
	protected final CoreDriver _coreDriver;

	DocumentIndexItem(CoreDriver coreDriver, MasterDataIdentity id) {
		_coreDriver = coreDriver;
		_id = id;
		_list = new ArrayList<HeadEntity>();
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
			endId = _coreDriver.getCurMonthId();
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
			endId = _coreDriver.getCurMonthId();
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
	 * add document
	 * 
	 * @param head
	 */
	void addDoc(HeadEntity head) {
		_list.add(head);
		Collections.sort(_list, DocumentIndex.COMPARATOR_DATE);
	}
}
