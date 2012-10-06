package com.jasonzqshen.familyAccounting.settings;

import com.jasonzqshen.familyAccounting.R;
import com.jasonzqshen.familyAccounting.data.DataCore;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.IdentityInvalidChar;
import com.jasonzqshen.familyaccounting.core.exception.IdentityNoData;
import com.jasonzqshen.familyaccounting.core.exception.IdentityTooLong;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.utils.StringUtility;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public abstract class AbstractNewActivity extends Activity {

    protected TextView _descpField;

    protected TextView _masterIdField;

    protected String _descp;

    protected MasterDataIdentity _mdId;

    /**
     * get title
     * 
     * @return
     */
    protected abstract int getStrTitle();

    /**
     * content identity
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

        // check identity
        String idStr = _masterIdField.getText().toString();
        try {
            _mdId = new MasterDataIdentity(idStr);
        } catch (IdentityTooLong e) {
            showDialog(R.id.dialog_mdid_error);
            return false;
        } catch (IdentityNoData e) {
            showDialog(R.id.dialog_mdid_error);
            return false;
        } catch (IdentityInvalidChar e) {
            showDialog(R.id.dialog_mdid_error);
            return false;
        }

        // check description
        _descp = _descpField.getText().toString();
        if (StringUtility.isNullOrEmpty(_descp)) {
            showDialog(R.id.dialog_descp_error);
            return false;
        }

        return true;
    }

    /**
     * save master data
     * 
     * @return
     */
    protected abstract boolean saveMasterData();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentId());
        this.setTitle(getStrTitle());

        _descpField = (TextView) this.findViewById(R.id.descp);
        _masterIdField = (TextView) this.findViewById(R.id.masterId);

        Button saveBtn = (Button) this.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(R.id.dialog_save_comfirm);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        case R.id.dialog_mdid_error:
            return new AlertDialog.Builder(this)
                    .setTitle(R.string.error)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(R.string.message_id_err)
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

        ret = saveMasterData();
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
