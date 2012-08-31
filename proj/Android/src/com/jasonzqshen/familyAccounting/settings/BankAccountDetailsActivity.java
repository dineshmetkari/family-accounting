package com.jasonzqshen.familyAccounting.settings;

import java.util.ArrayList;

import com.jasonzqshen.familyAccounting.R;
import com.jasonzqshen.familyAccounting.data.DataCore;
import com.jasonzqshen.familyAccounting.utils.RandomUtil;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.IdentityInvalidChar;
import com.jasonzqshen.familyaccounting.core.exception.IdentityNoData;
import com.jasonzqshen.familyaccounting.core.exception.IdentityTooLong;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityExists;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityNotDefined;
import com.jasonzqshen.familyaccounting.core.exception.NullValueNotAcceptable;
import com.jasonzqshen.familyaccounting.core.exception.ParametersException;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.masterdata.BankAccountMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.BankAccountNumber;
import com.jasonzqshen.familyaccounting.core.masterdata.BankKeyMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.GLAccountMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataBase;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataFactoryBase;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;
import com.jasonzqshen.familyaccounting.core.utils.BankAccountType;
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

public class BankAccountDetailsActivity extends Activity {

    private TextView _bankAccNum;

    private Spinner _bankKey;

    private TextView _descp;

    private MasterDataIdentity _glAccountId;

    private MasterDataBase[] _bankKeys;

    private MasterDataBase _selectedBankKey;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.masterdata_bank_details);

        _bankAccNum = (TextView) this.findViewById(R.id.number);
        _bankKey = (Spinner) this.findViewById(R.id.bankKeySelection);
        _descp = (TextView) this.findViewById(R.id.descp);

        _glAccountId = (MasterDataIdentity) this.getIntent()
                .getSerializableExtra(
                        MasterDataSettingActivity.PARAM_MASTERDATA_ID);
        this.setTitle(R.string.masterdata_new_bank_account);

        Button saveBtn = (Button) this.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(R.id.dialog_save_comfirm);
            }
        });

        if (_glAccountId != null) {
            this.setTitle(R.string.masterdata_edit_bank_account);

            DataCore dataCore = DataCore.getInstance();
            CoreDriver coreDriver = dataCore.getCoreDriver();
            if (coreDriver.isInitialized() == false) {
                return;
            }
            MasterDataManagement mdMgmt = coreDriver.getMasterDataManagement();
            GLAccountMasterData glAccount = (GLAccountMasterData) mdMgmt
                    .getMasterData(_glAccountId, MasterDataType.GL_ACCOUNT);
            BankAccountMasterData bankAccount = (BankAccountMasterData) mdMgmt
                    .getMasterData(glAccount.getBankAccount(),
                            MasterDataType.BANK_ACCOUNT);
            _bankAccNum.setText(bankAccount.getBankAccountNumber().toString());
            _descp.setText(bankAccount.getDescp());

            BankKeyMasterData bankKey = (BankKeyMasterData) mdMgmt
                    .getMasterData(bankAccount.getBankKey(),
                            MasterDataType.BANK_KEY);
            _selectedBankKey = bankKey;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        DataCore dataCore = DataCore.getInstance();
        CoreDriver coreDriver = dataCore.getCoreDriver();
        if (coreDriver.isInitialized() == false) {
            return;
        }

        MasterDataManagement mdMgmt = coreDriver.getMasterDataManagement();

        // set selection
        MasterDataFactoryBase factory = mdMgmt
                .getMasterDataFactory(MasterDataType.BANK_KEY);
        _bankKeys = factory.getAllEntities();
        ArrayList<String> array = new ArrayList<String>();
        for (MasterDataBase data : _bankKeys) {
            array.add(data.getDescp());
        }
        ArrayAdapter<String> bankAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, array);
        bankAdapter
                .setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        _bankKey.setAdapter(bankAdapter);

        if (_selectedBankKey == null) {
            _selectedBankKey = _bankKeys[0];
            _bankKey.setSelection(0);
        } else {
            for (int i = 0; i < _bankKeys.length; ++i) {
                if (_bankKey.equals(_selectedBankKey)) {
                    _bankKey.setSelection(i);
                }
            }
        }
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
        String bankNumStr = _bankAccNum.getText().toString();
        BankAccountNumber number;
        try {
            number = new BankAccountNumber(bankNumStr);
        } catch (IdentityTooLong e) {
            showDialog(R.id.dialog_bank_number_error);
            return false;
        } catch (IdentityNoData e) {
            showDialog(R.id.dialog_bank_number_error);
            return false;
        } catch (IdentityInvalidChar e) {
            showDialog(R.id.dialog_bank_number_error);
            return false;
        }

        String descp = _descp.getText().toString();
        if (StringUtility.isNullOrEmpty(descp)) {
            showDialog(R.id.dialog_descp_error);
            return false;
        }
        DataCore dataCore = DataCore.getInstance();
        CoreDriver coreDriver = dataCore.getCoreDriver();
        if (coreDriver.isInitialized() == false) {
            return false;
        }
        try {
            MasterDataManagement mdMgmt = coreDriver.getMasterDataManagement();
            if (_glAccountId != null) {
                // update
                GLAccountMasterData glAccount = (GLAccountMasterData) mdMgmt
                        .getMasterData(_glAccountId, MasterDataType.GL_ACCOUNT);
                BankAccountMasterData bankAccount = (BankAccountMasterData) mdMgmt
                        .getMasterData(glAccount.getBankAccount(),
                                MasterDataType.BANK_ACCOUNT);

                bankAccount.setBankAccountNumber(number);
                bankAccount.setBankKey(_selectedBankKey.getIdentity());
                bankAccount.setDescp(descp);
                glAccount.setDescp(descp);

                mdMgmt.store();
            } else {
                // create new
                MasterDataFactoryBase bankFactory = mdMgmt
                        .getMasterDataFactory(MasterDataType.BANK_ACCOUNT);
                MasterDataIdentity bankKeyID = generateBankAccountId(mdMgmt,
                        _selectedBankKey.getIdentity(), number);
                MasterDataBase bankAccount = bankFactory
                        .createNewMasterDataBase(bankKeyID, descp, number,
                                _selectedBankKey.getIdentity(),
                                BankAccountType.SAVING_ACCOUNT);

                MasterDataIdentity_GLAccount glId = generateGLAccountId(mdMgmt);
                MasterDataFactoryBase glAccFactory = mdMgmt
                        .getMasterDataFactory(MasterDataType.GL_ACCOUNT);
                glAccFactory.createNewMasterDataBase(glId, descp, bankAccount.getIdentity());
            }
            return true;
        } catch (NullValueNotAcceptable e) {
            throw new SystemException(e);
        } catch (MasterDataIdentityNotDefined e) {
            throw new SystemException(e);
        } catch (ParametersException e) {
            throw new SystemException(e);
        } catch (MasterDataIdentityExists e) {
            throw new SystemException(e);
        }
    }

    /**
     * generate bank account id
     * 
     * @param mdMgmt
     * @param bankKeyId
     * @param number
     * @return
     */
    private MasterDataIdentity generateBankAccountId(
            MasterDataManagement mdMgmt, MasterDataIdentity bankKeyId,
            BankAccountNumber number) {
        String bankKeyStr = bankKeyId.toString().substring(
                MasterDataIdentity.LENGTH - 3);
        String numStr = number.toString().substring(
                BankAccountNumber.LENGTH - 4);

        try {
            MasterDataIdentity id = new MasterDataIdentity(String.format(
                    "%s_%s", bankKeyStr, numStr));

            if (mdMgmt.getMasterData(id, MasterDataType.BANK_ACCOUNT) == null) {
                return id;
            }

            int random = RandomUtil.getInstance().getRandom(0, 1000);
            id = new MasterDataIdentity(String.valueOf(random));

            return id;
        } catch (IdentityTooLong e) {
            throw new SystemException(e);
        } catch (IdentityNoData e) {
            throw new SystemException(e);
        } catch (IdentityInvalidChar e) {
            throw new SystemException(e);
        }
    }

    /**
     * generate master data management
     * 
     * @param mdMgmt
     * @return
     */
    private MasterDataIdentity_GLAccount generateGLAccountId(
            MasterDataManagement mdMgmt) {
        int random = RandomUtil.getInstance().getRandom(0, 1000000);
        try {
            MasterDataIdentity num = new MasterDataIdentity(
                    String.valueOf(random));
            String str = num.toString()
                    .substring(MasterDataIdentity.LENGTH - 6);
            String glAccStr = String.format("%s%s",
                    GLAccountGroup.BANK_ACCOUNT.toString(), str);
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
