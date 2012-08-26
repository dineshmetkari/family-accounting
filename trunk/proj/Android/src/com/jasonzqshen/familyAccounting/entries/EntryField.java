package com.jasonzqshen.familyAccounting.entries;

public class EntryField {
    public final int ROW_ID;

    public final int VALUE_ID;

    public final String FIELD_NAME;

    public final int DIALOG_ID;

    private int _selected;

    /**
     * 
     * @param rowId
     * @param valueId
     * @param dialogId
     * @param fieldName
     */
    public EntryField(int rowId, int valueId, int dialogId, String fieldName) {
        ROW_ID = rowId;
        VALUE_ID = valueId;
        FIELD_NAME = fieldName;
        DIALOG_ID = dialogId;
    }

    /**
     * set selected
     * 
     * @param selected
     */
    public void setSelected(int selected) {
        _selected = selected;
    }

    /**
     * get selected
     * 
     * @return
     */
    public int getSelected() {
        return _selected;
    }
}
