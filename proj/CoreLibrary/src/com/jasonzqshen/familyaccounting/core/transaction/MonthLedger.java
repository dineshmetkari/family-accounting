package com.jasonzqshen.familyaccounting.core.transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

import com.jasonzqshen.familyaccounting.core.utils.XMLTransfer;

/**
 * Month ledger
 * 
 * @author I072485
 * 
 */
public class MonthLedger {
	private final Hashtable<DocumentIdentity, HeadEntity> _list;
	private final MonthIdentity _monthId;


	public MonthLedger(MonthIdentity monthId) {
		_monthId = monthId;
		_list = new Hashtable<DocumentIdentity, HeadEntity>();
		// _isClosed = false;
	}

	/**
	 * get month identity
	 * 
	 * @return
	 */
	public MonthIdentity getMonthID() {
		return _monthId;
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
	 * get count
	 * 
	 * @return
	 */
	public int getCount() {
		return _list.size();
	}

	/**
	 * to array
	 */
	public HeadEntity[] getEntities() {
		HeadEntity[] heads = new HeadEntity[_list.size()];
		ArrayList<HeadEntity> headArray = getEntitiesArrayList();
		int index = 0;
		for (HeadEntity h : headArray) {
			heads[index++] = h;
		}
		return heads;
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<HeadEntity> getEntitiesArrayList() {
		ArrayList<HeadEntity> headArray = new ArrayList<HeadEntity>(
				_list.values());
		Collections.sort(headArray);
		return headArray;
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

	/**
	 * parse to XML
	 * 
	 * @return
	 */
	public String toXML() {
		HeadEntity[] entities = this.getEntities();
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(String.format("%s%s %s", XMLTransfer.BEGIN_TAG_LEFT,
				TransDataUtils.XML_ROOT, XMLTransfer.BEGIN_TAG_RIGHT));

		for (HeadEntity head : entities) {
			strBuilder.append(head.toXml());
		}

		strBuilder.append(String.format("%s%s %s", XMLTransfer.END_TAG_LEFT,
				TransDataUtils.XML_ROOT, XMLTransfer.END_TAG_RIGHT));
		return strBuilder.toString();
	}
}
