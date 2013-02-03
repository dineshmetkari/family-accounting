package com.jasonzqshen.familyAccounting.settings;

import com.jasonzqshen.familyAccounting.R;
import com.jasonzqshen.familyAccounting.data.DataCore;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.NullValueNotAcceptable;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataBase;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;
import com.jasonzqshen.familyaccounting.core.utils.StringUtility;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public abstract class AbstractEditActivity extends Activity {
    protected TextView _descp;

    protected MasterDataIdentity _masterDataId;

    protected MasterDataBase _masterData;

    /**
     * get title as string
     * 
     * @return title
     */
    protected abstract String getStrTitle();

    /**
     * get master data type
     * 
     * @return master data type
     */
    protected abstract MasterDataType getMasterDataType();

    /**
     * get content id
     * 
     * @return
     */
    protected abstract int getContentId();

    /**
     * set the attributes to master data account before saving. before setting,
     * the attributes should be checked.
     * 
     * @return
     */
    protected boolean setBeforeSaving() {
        // check core driver
        DataCore dataCore = DataCore.getInstance();
        CoreDriver coreDriver = dataCore.getCoreDriver();
        if (coreDriver.isInitialized() == false) {
            return false;
        }

        // check description
        String descp = _descp.getText().toString();
        if (StringUtility.isNullOrEmpty(descp)) {
            showDialog(R.id.dialog_descp_error);
            return false;
        }

        try {
            _masterData.setDescp(descp);

            return true;
        } catch (NullValueNotAcceptable e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentId());

        _descp = (TextView) this.findViewById(R.id.descp);

        _masterDataId = (MasterDataIdentity) this.getIntent()
                .getSerializableExtra(
                        MasterDataSettingActivity.PARAM_MASTERDATA_ID);

        Button saveBtn = (Button) this.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(R.id.dialog_save_comfirm);
            }
        });

        DataCore dataCore = DataCore.getInstance();
        CoreDriver coreDriver = dataCore.getCoreDriver();
        if (coreDriver.isInitialized() == false) {
            return;
        }
        MasterDataManagement mdMgmt = coreDriver.getMasterDataManagement();
        _masterData = mdMgmt.getMasterData(_masterDataId, getMasterDataType());

        _descp.setText(_masterData.getDescp());

        this.setTitle(getStrTitle());

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case R.id.dialog_save_comfirm:
            return new AlertDialog.Builder(this)
                    .setTitle(R.string.masterdata_save_comfirm)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(R.string.message_save_confirm)
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    boolean ret = save();
                                    if (ret) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                }
                            })
                    .setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int arg1) {
                                    dialog.dismiss();
                                }
                            }).create();
        case R.id.dialog_bank_number_error:
            return new AlertDialog.Builder(this)
                    .setTitle(R.string.error)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(R.string.message_bank_num_error)
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int arg1) {
                                    dialog.dismiss();
                                }
                            }).create();
        case R.id.dialog_descp_error:
            return new AlertDialog.Builder(this)
                    .setTitle(R.string.error)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(R.string.message_descp_error)
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int arg1) {
                                    dialog.dismiss();
                                }
                            }).create();
        }

        return null;
    }

    /**
     * save
     * 
     * @return
     */
    private boolean save() {
        boolean ret = setBeforeSaving();
        if (ret == false) {
            return false;
        }

        DataCore dataCore = DataCore.getInstance();
        CoreDriver coreDriver = dataCore.getCoreDriver();
        // store the modification
        MasterDataManagement mdMgmt = coreDriver.getMasterDataManagement();
        mdMgmt.store();

        return true;
    }

}
