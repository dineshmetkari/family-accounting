package com.jasonzqshen.familyAccounting.settings;

import android.os.Bundle;
import android.widget.Spinner;

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
import com.jasonzqshen.familyaccounting.core.utils.CriticalLevel;

public class BusinessAreaNewActivity extends AbstractNewActivity {
    private Spinner _criticalLevel;

    private CriticalLevel _level;

    @Override
    protected boolean setBeforeSaving() {
        boolean ret = super.setBeforeSaving();
        if (ret == false) {
            return false;
        }

        int position = _criticalLevel.getSelectedItemPosition();
        switch (position) {
        case 0:
            _level = CriticalLevel.LOW;
            break;
        case 1:
            _level = CriticalLevel.MEDIUM;
            break;
        case 2:
            _level = CriticalLevel.HIGH;
            break;
        }

        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _criticalLevel = (Spinner) this.findViewById(R.id.priority);
    }

    @Override
    protected int getStrTitle() {
        return R.string.masterdata_new_area_title;
    }

    @Override
    protected boolean saveMasterData() {
        DataCore dataCore = DataCore.getInstance();
        CoreDriver coreDriver = dataCore.getCoreDriver();
        MasterDataManagement mdMgmt = coreDriver.getMasterDataManagement();
        MasterDataFactoryBase factory = mdMgmt
                .getMasterDataFactory(MasterDataType.BUSINESS_AREA);

        try {
            factory.createNewMasterDataBase(_mdId, _descp, _level);
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
        return R.layout.masterdata_new_area;
    }

}
