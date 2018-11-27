package me.leon.samples.utils;

import org.apache.commons.lang.time.FastDateFormat;

public class DateUtils {

    private static FastDateFormat dateFormat = FastDateFormat.getInstance("yyyyMMdd");

    public static String formatToYmd(long timemillis) {
        return dateFormat.format(timemillis);
    }
}
