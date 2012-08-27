package com.jasonzqshen.familyAccounting.utils;

import com.jasonzqshen.familyAccounting.reports.DocumentsListNavigation;

import android.app.Activity;

public class CostDetailsAction implements IAction {
    private final Activity _source;

    public CostDetailsAction(Activity source) {
        _source = source;
    }

    @Override
    public void execute() {
        DocumentsListNavigation.navigate2CostDetails(_source);
    }
}
