package com.jasonzqshen.familyAccounting.widgets;

import android.view.View.OnClickListener;

public class MenuAdapterItem {
	public static final int TYPE_COUNT = 2;
	public static final int HEAD_TYPE = 0;
	public static final int ITEM_TYPE = 1;

	public final int ItemType;
	public final int ImageID;
	public final int TextID;
	public final OnClickListener Listener;

	public MenuAdapterItem(int itemType, int image, int text,
			OnClickListener listener) {
		ItemType = itemType;
		ImageID = image;
		TextID = text;
		Listener = listener;
	}
}