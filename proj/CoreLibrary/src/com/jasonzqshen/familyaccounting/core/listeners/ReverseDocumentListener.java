package com.jasonzqshen.familyaccounting.core.listeners;

import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;

public interface ReverseDocumentListener {
	void onReverseDocument(HeadEntity doc);
}
