package com.jasonzqshen.familyAccounting.reports;

import java.io.Serializable;
import java.util.ArrayList;

import com.jasonzqshen.familyaccounting.core.transaction.MonthIdentity;

public class DocListParam implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6346189251553653419L;

	public static final int ACCOUNT_CATEGORY = 0;
	public static final int DATE_CATEGORY = 2;
	public static final int BUSINESS_CATEGORY = 1;

	public static final String PARAM_NAME = "DocumentListParameter";

	public final MonthIdentity MonthId;
	public final ArrayList<DocListParamItem> List;

	public DocListParam(MonthIdentity monthId, ArrayList<DocListParamItem> list) {
		MonthId = monthId;
		List = list;
	}

}
