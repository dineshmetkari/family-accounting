package com.jasonzqshen.familyaccounting.core.transaction;

import java.util.Comparator;

/**
 * head entity comparator
 * 
 * @author I072485
 * 
 */
public class HeadEntityComparator implements Comparator<HeadEntity> {
	public HeadEntityComparator() {

	}

	public int compare(HeadEntity h0, HeadEntity h1) {
		DocumentNumberComparator com = new DocumentNumberComparator();

		return com.compare(h0.getDocumentNumber(), h1.getDocumentNumber());
	}

}
