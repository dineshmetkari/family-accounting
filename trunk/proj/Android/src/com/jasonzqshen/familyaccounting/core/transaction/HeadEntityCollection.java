package com.jasonzqshen.familyaccounting.core.transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

public class HeadEntityCollection {

	private final Hashtable<DocumentIdentity, HeadEntity> _list;
	public final MonthIdentity _monthId;

	public HeadEntityCollection(MonthIdentity monthId) {
		_monthId = monthId;
		_list = new Hashtable<DocumentIdentity, HeadEntity>();
	}

	/**
	 * add head entity. Only for inner invoke
	 * 
	 * @param head
	 */
	void add(HeadEntity head) {
		DocumentIdentity id = head.getDocIdentity();
		_list.put(id, head);
	}

	/**
	 * to array
	 */
	public HeadEntity[] getEntities() {
		HeadEntity[] heads = new HeadEntity[_list.size()];
		ArrayList<HeadEntity> headArray = new ArrayList<HeadEntity>(
				_list.values());
		HeadEntityComparator comparator = new HeadEntityComparator();
		Collections.sort(headArray, comparator);
		int index = 0;
		for (HeadEntity h : headArray) {
			heads[index++] = h;
		}
		return heads;
	}

	/**
	 * get head entity based on document identity
	 * 
	 * @param docId
	 * @return
	 */
	public HeadEntity getEntity(DocumentIdentity docId) {
		HeadEntity head = _list.get(docId);
		return head;
	}

}
