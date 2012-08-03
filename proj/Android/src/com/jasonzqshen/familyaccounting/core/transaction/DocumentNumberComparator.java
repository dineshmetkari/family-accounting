package com.jasonzqshen.familyaccounting.core.transaction;

import java.util.Comparator;

public class DocumentNumberComparator implements Comparator<DocumentNumber> {

	public int compare(DocumentNumber lhs, DocumentNumber rhs) {
		String str0 = lhs.toString();
		String str1 = rhs.toString();

		return str0.compareToIgnoreCase(str1);
	}

}
