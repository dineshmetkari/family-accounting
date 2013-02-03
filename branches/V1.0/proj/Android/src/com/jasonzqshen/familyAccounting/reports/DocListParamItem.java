package com.jasonzqshen.familyAccounting.reports;

import java.io.Serializable;
import java.util.ArrayList;

public class DocListParamItem implements Serializable {

	public static final String PARAM_NAME = "DocumentListParameter";

	/**
	 * 
	 */
	private static final long serialVersionUID = -2335050611599555035L;

	public final ArrayList<Object> SelectedValue;
	public final int Category;

	public DocListParamItem(ArrayList<Object> selectedValue, int category) {
		SelectedValue = selectedValue;
		Category = category;
	}
}
