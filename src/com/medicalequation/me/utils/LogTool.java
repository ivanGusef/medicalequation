package com.medicalequation.me.utils;

import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: Иван Гусев
 * Date: 05.02.13
 * Time: 18:26
 * To change this template use File | Settings | File Templates.
 */
public class LogTool {
    private static final String sTag = "developer";

    public static void v(String msg) {
        Log.v(sTag, msg);
    }

    public static void d(String msg) {
        Log.d(sTag, msg);
    }

    public static void i(String msg) {
        Log.i(sTag, msg);
    }

    public static void w(String msg) {
        Log.w(sTag, msg);
    }

    public static void e(String msg) {
        Log.e(sTag, msg);
    }

    public static void e(String msg, Throwable e) {
        Log.e(sTag, msg, e);
    }

    public static void wtf(String msg) {
        Log.wtf(sTag, msg);
    }
}
