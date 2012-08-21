package com.jasonzqshen.familyAccounting.widgets;

import com.jasonzqshen.familyaccounting.core.document_entries.IDocumentEntry;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;

public class DocumentsListAdapterItem {
	public static final int HEAD_VIEW = 0;
	public static final int OUTGOING_VIEW = 1;
	public static final int INCOMING_VIEW = 2;
	public static final int GL_VIEW = 3;
	public static final int OTHER_VIEW = 4;

	public static final int VIEW_TYPE_COUNT = 5;

	public final int Type;
	public final IDocumentEntry Document;
	public final String Descp;
	public final CurrencyAmount Amount;
	public final MasterDataIdentity_GLAccount RelatedAccount;

	public DocumentsListAdapterItem(int type, IDocumentEntry doc, String descp,
			CurrencyAmount amount, MasterDataIdentity_GLAccount relatedAccount) {
		Type = type;
		Document = doc;
		Descp = descp;
		Amount = amount;
		RelatedAccount = relatedAccount;
	}
}
