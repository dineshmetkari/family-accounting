package com.jasonzqshen.familyaccounting.core.transaction;

import java.util.Calendar;
import java.util.Comparator;

public class HeadEntityComparors {
    /**
     * head entity comparator
     */
    public final static Comparator<HeadEntity> HeadEntityComparatorByDate = new Comparator<HeadEntity>() {
        @Override
        public int compare(HeadEntity doc0, HeadEntity doc1) {
            Calendar calendar0 = Calendar.getInstance();
            calendar0.setTime(doc0.getPostingDate());
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(doc1.getPostingDate());
            return calendar0.compareTo(calendar1);
        }

    };
}
