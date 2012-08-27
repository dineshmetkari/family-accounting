package com.jasonzqshen.familyAccounting;

import com.jasonzqshen.familyAccounting.reports.GLAccountBalanceReportActivity;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivityHandler {
    private final MainActivity _activity;

    /**
     * package internal
     * 
     * @param activity
     */
    MainActivityHandler(MainActivity activity) {
        _activity = activity;
    }

    public final OnClickListener ASSET_FIELD_CLICK_LISTENER = new OnClickListener() {
        @Override
        public void onClick(View v) {
            navigate2AssetReports();
        }
    };

    /**
     * navigate to asset reports
     */
    private void navigate2AssetReports() {
        Intent assetReportIntent = new Intent(_activity,
                GLAccountBalanceReportActivity.class);
        _activity.startActivity(assetReportIntent);
    }

    /**
     * navigate to main menu button
     */
    public void navigate2MainMenu() {
        Intent assetReportIntent = new Intent(_activity, MainMenuActivity.class);
        _activity.startActivity(assetReportIntent);
    }

    /**
     * navigate to main menu button
     */
    public void navigate2BalanceReport() {
        Intent assetReportIntent = new Intent(_activity,
                GLAccountBalanceReportActivity.class);
        _activity.startActivity(assetReportIntent);
    }

}
