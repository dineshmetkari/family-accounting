package com.jasonzqshen.familyaccounting.core.exception;

public class EntryTypeNotExistException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public EntryTypeNotExistException() {
        super("Entry type not exists.");
    }
}
