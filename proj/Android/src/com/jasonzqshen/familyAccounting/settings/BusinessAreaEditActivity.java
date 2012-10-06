package com.jasonzqshen.familyAccounting.settings;

import android.os.Bundle;
import android.widget.Spinner;

import com.jasonzqshen.familyAccounting.R;
import com.jasonzqshen.familyaccounting.core.exception.NullValueNotAcceptable;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.masterdata.BusinessAreaMasterData;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;
import com.jasonzqshen.familyaccounting.core.utils.CriticalLevel;

public class BusinessAreaEditActivity extends AbstractEditActivity {
    private Spinner _criticalLevel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _criticalLevel = (Spinner) this.findViewById(R.id.priority);

        BusinessAreaMasterData area = (BusinessAreaMasterData) _masterData;
        CriticalLevel level = area.getCriticalLevel();
        if (level == CriticalLevel.HIGH) {
            _criticalLevel.setSelection(2);
        } else if (level == CriticalLevel.MEDIUM) {
            _criticalLevel.setSelection(1);
        } else if (level == CriticalLevel.LOW) {
            _criticalLevel.setSelection(0);
        }
    }

    @Override
    protected boolean setBeforeSaving() {
        boolean ret = super.setBeforeSaving();
        if (ret == false) {
            return false;
        }
        BusinessAreaMasterData area = (BusinessAreaMasterData) _masterData;

        int position = _criticalLevel.getSelectedItemPosition();
        try {
            switch (position) {
            case 0:
                area.setCriticalLevel(CriticalLevel.LOW);
                break;
            case 1:
                area.setCriticalLevel(CriticalLevel.MEDIUM);
                break;
            case 2:
                area.setCriticalLevel(CriticalLevel.HIGH);
                break;
            }
        } catch (NullValueNotAcceptable e) {
            throw new SystemException(e);
        }

        return true;
    }

    @Override
    protected String getStrTitle() {
        String string = this.getString(R.string.masterdata_edit_area_title);
        return String.format("%s%s", string, _masterDataId.toString());
    }

    @Override
    protected MasterDataType getMasterDataType() {
        return MasterDataType.BUSINESS_AREA;
    }

    @Override
    protected int getContentId() {
        return R.layout.masterdata_edit_area;
    }

}
