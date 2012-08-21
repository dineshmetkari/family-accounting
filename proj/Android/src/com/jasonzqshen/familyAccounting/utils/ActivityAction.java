package com.jasonzqshen.familyAccounting.utils;

import android.app.Activity;
import android.content.Intent;

public class ActivityAction implements IAction {
	private final Class<?> _dstActivity;
	private final Activity _source;

	public ActivityAction(Class<?> dstActivity, Activity source) {
		_dstActivity = dstActivity;
		_source = source;
	}

	@Override
	public void execute() {
		// success, start main activity
		Intent intent = new Intent(_source, _dstActivity);
		_source.startActivity(intent);
	}

}
