package com.jasonzqshen.familyAccounting.settings;

import com.jasonzqshen.familyAccounting.R;
import com.jasonzqshen.familyAccounting.data.DataCore;
import com.jasonzqshen.familyAccounting.utils.RandomUtil;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.IdentityInvalidChar;
import com.jasonzqshen.familyaccounting.core.exception.IdentityNoData;
import com.jasonzqshen.familyaccounting.core.exception.IdentityTooLong;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityExists;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityNotDefined;
import com.jasonzqshen.familyaccounting.core.exception.ParametersException;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataFactoryBase;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;
import com.jasonzqshen.familyaccounting.core.utils.GLAccountGroup;
import com.jasonzqshen.familyaccounting.core.utils.StringUtility;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public abstract class AbstractAccountNewActivity extends Activity {

    private TextView _accountName;

    private Spinner _accountType;

    private ArrayAdapter<GLAccountGroupItem> _accountTypeAdapter;

    protected abstract GLAccountGroupItem[] getGLAccountGroupSet();

    protected abstract int getStrTitle();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.masterdata_new_account);
        this.setTitle(getStrTitle());

        _accountName = (TextView) this.findViewById(R.id.accountName);
        _accountType = (Spinner) this.findViewById(R.id.accountType);
        _accountTypeAdapter = new ArrayAdapter<GLAccountGroupItem>(this,
                android.R.layout.simple_spinner_item, getGLAccountGroupSet());
        _accountTypeAdapter
                .setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        _accountType.setAdapter(_accountTypeAdapter);

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
        String accountName = _accountName.getText().toString();

        // check account name
        if (StringUtility.isNullOrEmpty(accountName)) {
            showDialog(R.id.dialog_descp_error);
            return false;
        }

        int position = _accountType.getSelectedItemPosition();
        GLAccountGroup accountType = _accountTypeAdapter.getItem(position)
                .getGroup();

        DataCore dataCore = DataCore.getInstance();
        CoreDriver coreDriver = dataCore.getCoreDriver();
        if (coreDriver.isInitialized() == false) {
            return false;
        }

        MasterDataManagement mdMgmt = coreDriver.getMasterDataManagement();
        MasterDataFactoryBase glAccFactory = mdMgmt
                .getMasterDataFactory(MasterDataType.GL_ACCOUNT);
        while (true) {
            try {
                // create new
                MasterDataIdentity_GLAccount glId = generateGLAccountId(
                        accountType, mdMgmt);
                glAccFactory.createNewMasterDataBase(glId, accountName);
                
                
                // store
                mdMgmt.store();

                return true;
            } catch (MasterDataIdentityNotDefined e) {
                throw new SystemException(e);
            } catch (ParametersException e) {
                throw new SystemException(e);
            } catch (MasterDataIdentityExists e) {

            }
        }
    }

    /**
     * generate master data management
     * 
     * @param group
     * @param mdMgmt
     * @return
     */
    private MasterDataIdentity_GLAccount generateGLAccountId(
            GLAccountGroup group, MasterDataManagement mdMgmt) {
        int random = RandomUtil.getInstance().getRandom(0, 1000000);
        try {
            MasterDataIdentity num = new MasterDataIdentity(
                    String.valueOf(random));
            String str = num.toString()
                    .substring(MasterDataIdentity.LENGTH - 6);
            String glAccStr = String.format("%s%s", group.toString(), str);
            return new MasterDataIdentity_GLAccount(glAccStr);
        } catch (IdentityTooLong e) {
            throw new SystemException(e);
        } catch (IdentityNoData e) {
            throw new SystemException(e);
        } catch (IdentityInvalidChar e) {
            throw new SystemException(e);
        }
    }
}
