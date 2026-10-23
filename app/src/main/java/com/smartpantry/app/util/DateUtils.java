package com.smartpantry.app.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Date helpers. Dates are stored in the database as yyyy-MM-dd strings.
 */
public final class DateUtils {

    private static final String STORAGE_PATTERN = "yyyy-MM-dd";
    private static final String DISPLAY_PATTERN = "d MMM yyyy";

    private DateUtils() {
    }

    public static String toStorage(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month, day);
        return new SimpleDateFormat(STORAGE_PATTERN, Locale.US).format(calendar.getTime());
    }

    public static String daysFromToday(int days) {
        Calendar calendar = startOfToday();
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return new SimpleDateFormat(STORAGE_PATTERN, Locale.US).format(calendar.getTime());
    }

    public static Calendar parse(String stored) {
        if (stored == null || stored.isEmpty()) {
            return null;
        }
        try {
            Date date = new SimpleDateFormat(STORAGE_PATTERN, Locale.US).parse(stored);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        } catch (ParseException e) {
            return null;
        }
    }

    public static String toDisplay(String stored) {
        Calendar calendar = parse(stored);
        if (calendar == null) {
            return "";
        }
        return new SimpleDateFormat(DISPLAY_PATTERN, Locale.getDefault()).format(calendar.getTime());
    }

    /** Whole days from today until the stored date; negative when already expired, null when no date. */
    public static Integer daysUntil(String stored) {
        Calendar target = parse(stored);
        if (target == null) {
            return null;
        }
        long diff = target.getTimeInMillis() - startOfToday().getTimeInMillis();
        return (int) Math.round(diff / (double) TimeUnit.DAYS.toMillis(1));
    }

    private static Calendar startOfToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }
}
