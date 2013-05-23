package com.medicalequation.me.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created with IntelliJ IDEA.
 * User: Иван Гусев
 * Date: 23.05.13
 * Time: 23:27
 * May the Force be with you, always
 */
public class TreatmentTable {
    public static final String TN_TREATMENT = "treatment";

    public static final String CN_ID = "_id";
    public static final String CN_NAME = "name";
    public static final String CREATE_SCRIPT;

    static {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ").append(TN_TREATMENT).append("(");
        sb.append(CN_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(CN_NAME).append(" TEXT);");
        CREATE_SCRIPT = sb.toString();
    }

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SCRIPT);
    }

    public static void onUpgrade(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TN_TREATMENT);
        onCreate(db);
    }
}
