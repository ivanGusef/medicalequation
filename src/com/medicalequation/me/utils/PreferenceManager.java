package com.medicalequation.me.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created with IntelliJ IDEA.
 * User: Иван Гусев
 * Date: 08.05.13
 * Time: 2:24
 * May the Force be with you, always
 */
public class PreferenceManager {

    public static final String PLACE_HOLDER_INDEX = "%index%";
    public static final String PLACE_HOLDER_RESULT = "%result%";
    private static final String PREF_NAME = "medeq_prefs";
    private static final int EQ_DIM = 13;

    private static SharedPreferences mPreferences;

    public static SharedPreferences getPreferences(Context context) {
        if (mPreferences == null) {
            mPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
        return mPreferences;
    }

    public static SharedPreferences.Editor getEditor(Context context) {
        return getPreferences(context).edit();
    }

    public static double[] getDoubleArray(Context context, String key) {
        double[] res = null;
        if (getPreferences(context).getBoolean(key, false)) {
            res = new double[EQ_DIM];
            for (int i = 0; i < res.length; i++) {
                res[i] = getPreferences(context).getFloat(key.replace(PLACE_HOLDER_INDEX, String.valueOf(i)), 0);
            }
        }
        return res;
    }

    public static void saveDoubleArray(Context context, String key, double[] array) {
        SharedPreferences.Editor editor = getEditor(context).putBoolean(key, true);
        for (int i = 0; i < array.length; i++) {
            editor.putFloat(key.replace(PLACE_HOLDER_INDEX, String.valueOf(i)), (float) array[i]);
        }
        editor.commit();
    }

    public static int getInt(Context context, String key) {
        return getPreferences(context).getInt(key, 0);
    }

    public static void saveInt(Context context, String key, int value) {
        getEditor(context).putInt(key, value).commit();
    }
}
