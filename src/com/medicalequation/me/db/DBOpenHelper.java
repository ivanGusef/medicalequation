package com.medicalequation.me.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.medicalequation.me.R;
import com.medicalequation.me.utils.IOUtils;

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
        try {
            db.beginTransaction();
            PatientTable.onCreate(db);
            TreatmentTable.onCreate(db);
            init(mContext, db);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            try {
                db.beginTransaction();
                PatientTable.onUpgrade(db);
                TreatmentTable.onUpgrade(db);
                init(mContext, db);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    }

    private static void init(Context context, SQLiteDatabase db) {
        String sql = IOUtils.readResourceAsString(context, R.raw.init_db);
        String[] strings = sql.split(";");
        executeStatements(strings, db);
    }

    private static void executeStatements(String[] strings, SQLiteDatabase db) {
        for (String string : strings) {
            String str = string.replace("\n", "").trim().replace("\t", "");
            if (str.length() > 0) {
                db.execSQL(string);
            }
        }
    }
}
