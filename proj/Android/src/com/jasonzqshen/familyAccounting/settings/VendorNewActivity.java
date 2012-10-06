package com.jasonzqshen.familyAccounting.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

import com.jasonzqshen.familyAccounting.R;
import com.jasonzqshen.familyAccounting.data.DataCore;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityExists;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityNotDefined;
import com.jasonzqshen.familyaccounting.core.exception.ParametersException;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataFactoryBase;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;

public class VendorNewActivity extends AbstractNewActivity {

    @Override
    protected int getStrTitle() {
        return R.string.masterdata_new_vendor_title;
    }

    @Override
    protected boolean saveMasterData() {
        DataCore dataCore = DataCore.getInstance();
        CoreDriver coreDriver = dataCore.getCoreDriver();
        MasterDataManagement mdMgmt = coreDriver.getMasterDataManagement();
        MasterDataFactoryBase factory = mdMgmt
                .getMasterDataFactory(MasterDataType.VENDOR);

        try {
            factory.createNewMasterDataBase(_mdId, _descp);
        } catch (ParametersException e) {
            throw new SystemException(e);
        } catch (MasterDataIdentityExists e) {
            showDialog(R.id.dialog_duplicated_mdid_error);
            return false;
        } catch (MasterDataIdentityNotDefined e) {
            throw new SystemException(e);
        }
        return true;
    }

    @Override
    protected int getContentId() {
        return R.layout.masterdata_new_vendor_customer;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case R.id.dialog_duplicated_mdid_error:
            return new AlertDialog.Builder(this)
                    .setTitle(R.string.error)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(R.string.message_duplicated_id_err)
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int arg1) {
                                    dialog.dismiss();
                                }
                            }).create();
        }

        return super.onCreateDialog(id);
    }
}
