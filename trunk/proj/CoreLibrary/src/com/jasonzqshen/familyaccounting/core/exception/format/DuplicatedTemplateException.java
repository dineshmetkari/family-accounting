package com.jasonzqshen.familyaccounting.core.exception.format;

public class DuplicatedTemplateException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -7827938554803462945L;

    public DuplicatedTemplateException(int id) {
        super("Duplicated template identity " + id);
    }

}
