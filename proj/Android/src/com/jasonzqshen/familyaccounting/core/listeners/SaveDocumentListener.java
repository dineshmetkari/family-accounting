package com.jasonzqshen.familyaccounting.core.listeners;

import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;

public interface SaveDocumentListener {
	void onSaveDocumentListener(HeadEntity document);
}
