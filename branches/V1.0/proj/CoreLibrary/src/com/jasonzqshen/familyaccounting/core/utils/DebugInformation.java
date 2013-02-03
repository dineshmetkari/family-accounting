package com.jasonzqshen.familyaccounting.core.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DebugInformation {

    public final Class<?> CLASS;

    public final int LINE_NUMBER;

    public final String INFO;

    public final MessageType TYPE;

    public DebugInformation(Class<?> cl, int lineNum, String info,
            MessageType type) {
        CLASS = cl;
        LINE_NUMBER = lineNum;
        INFO = info;
        TYPE = type;
    }

    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();

        return String.format("%s: %s, %s\t----%s(%d)",
                sdf.format(calendar.getTime()), TYPE, INFO,
                CLASS.getSimpleName(), LINE_NUMBER);
    }
}
