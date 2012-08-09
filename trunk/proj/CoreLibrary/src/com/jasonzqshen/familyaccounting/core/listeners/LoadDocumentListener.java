package com.jasonzqshen.familyaccounting.core.listeners;

import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;

public interface LoadDocumentListener{
	void onLoadDocumentListener(Object source, HeadEntity document);
}
