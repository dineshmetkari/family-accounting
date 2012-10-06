package com.jasonzqshen.familyAccounting.settings;

import android.app.Activity;

import com.jasonzqshen.familyaccounting.core.utils.GLAccountGroup;

public class GLAccountGroupItem {
    private final GLAccountGroup _group;

    private final Activity _activity;

    public GLAccountGroupItem(Activity activity, GLAccountGroup group) {
        _activity = activity;
        _group = group;
    }

    /**
     * get account group
     * 
     * @return
     */
    public GLAccountGroup getGroup() {
        return _group;
    }

    @Override
    public String toString() {
        int strId = AccountsSettingActivity.getAccountGroupName(_group);
        return _activity.getString(strId);
    }
}
