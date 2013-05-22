package com.medicalequation.me.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created with IntelliJ IDEA.
 * User: Иван Гусев
 * Date: 07.05.13
 * Time: 0:25
 * May the Force be with you, always
 */
public class DBOpenHelper extends SQLiteOpenHelper {

    public static final String NAME = "medeq.db";
    public static final int VERSION = 1;

    private Context mContext;

    public DBOpenHelper(Context context) {
        super(context, NAME, null, VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        PatientTable.onCreate(mContext,db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion)
            PatientTable.onUpgrade(mContext,db);
    }
}
