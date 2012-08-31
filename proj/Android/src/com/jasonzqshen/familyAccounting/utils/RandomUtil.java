package com.jasonzqshen.familyAccounting.utils;

import java.util.Calendar;
import java.util.Random;

public class RandomUtil {
    private static RandomUtil _instance;

    public static RandomUtil getInstance() {
        if (_instance == null) {
            _instance = new RandomUtil();
        }
        return _instance;
    }

    private Random _random;

    private RandomUtil() {
        Calendar calendar = Calendar.getInstance();
        _random = new Random(calendar.getTimeInMillis());
    }

    /**
     * get random
     * @param min
     * @param max
     * @return
     */
    public int getRandom(int min, int max) {
        return _random.nextInt(max) % (max - min + 1) + min;
    }
}
