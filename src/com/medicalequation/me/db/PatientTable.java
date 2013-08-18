package com.medicalequation.me.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created with IntelliJ IDEA.
 * User: Иван Гусев
 * Date: 07.05.13
 * Time: 0:39
 * May the Force be with you, always
 */
public class PatientTable {
    public static final String TN_PATIENT = "patient";

    public static final String CN_ID = "_id";
    public static final String CN_FIO = "fio";
    public static final String CN_THERAPY = "therapy";
    public static final String CN_MUTABLE_VALUES = "mutable_values";
    public static final String CN_IMMUTABLE_VALUES = "immutable_values";
    public static final String CN_RESULT_VALUES = "result_values";
    public static final String CREATE_SCRIPT;

    static {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ").append(TN_PATIENT).append("(");
        sb.append(CN_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(CN_FIO).append(" TEXT, ");
        sb.append(CN_THERAPY).append(" TEXT, ");
        sb.append(CN_MUTABLE_VALUES).append(" TEXT, ");
        sb.append(CN_IMMUTABLE_VALUES).append(" TEXT, ");
        sb.append(CN_RESULT_VALUES).append(" TEXT);");
        CREATE_SCRIPT = sb.toString();
    }

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SCRIPT);
    }

    public static void onUpgrade(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TN_PATIENT);
        onCreate(db);
    }
}
