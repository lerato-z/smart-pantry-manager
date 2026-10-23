package com.smartpantry.app.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * User settings stored with SharedPreferences (small key-value settings,
 * kept separate from the SQLite data).
 */
public final class Prefs {

    private static final String FILE = "smart_pantry_settings";

    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts";
    private static final String KEY_EXPIRY_DAYS = "expiry_days";
    private static final String KEY_SHOW_ALMOST = "show_almost_there";
    private static final String KEY_METRIC_BASE = "metric_base_units";

    public static final int[] EXPIRY_DAY_OPTIONS = {1, 2, 3, 5, 7};

    private Prefs() {
    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    public static boolean expiryAlertsEnabled(Context context) {
        return prefs(context).getBoolean(KEY_EXPIRY_ALERTS, true);
    }

    public static void setExpiryAlertsEnabled(Context context, boolean enabled) {
        prefs(context).edit().putBoolean(KEY_EXPIRY_ALERTS, enabled).apply();
    }

    public static int expiryDays(Context context) {
        return prefs(context).getInt(KEY_EXPIRY_DAYS, 3);
    }

    public static void setExpiryDays(Context context, int days) {
        prefs(context).edit().putInt(KEY_EXPIRY_DAYS, days).apply();
    }

    public static boolean showAlmostThere(Context context) {
        return prefs(context).getBoolean(KEY_SHOW_ALMOST, true);
    }

    public static void setShowAlmostThere(Context context, boolean show) {
        prefs(context).edit().putBoolean(KEY_SHOW_ALMOST, show).apply();
    }

    public static boolean useMetricBaseUnits(Context context) {
        return prefs(context).getBoolean(KEY_METRIC_BASE, false);
    }

    public static void setUseMetricBaseUnits(Context context, boolean metric) {
        prefs(context).edit().putBoolean(KEY_METRIC_BASE, metric).apply();
    }
}
