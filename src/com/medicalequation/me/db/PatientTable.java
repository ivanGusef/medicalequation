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
    public static final String CN_AGE = "age";
    public static final String CN_STAGE_TNM = "stage_tnm";
    public static final String CN_PSA = "psa";
    public static final String CN_GLISSON = "glisson";
    public static final String CN_PROSTATE_VOLUME = "prostate_volume";
    public static final String CN_PROSTATE_VOLUME_HIFU = "prostate_volume_hifu";
    public static final String CN_PROSTATE_LENGTH_HIFU = "prostate_length_hifu";
    public static final String CN_PROSTATE_WIDTH_HIFU = "prostate_width_hifu";
    public static final String CN_PROSTATE_HEIGHT_HIFU = "prostate_height_hifu";
    public static final String CN_MAX_URINE_VELOCITY = "max_urine_velocity";
    public static final String CN_AV_URINE_VELOCITY = "av_urine_velocity";
    public static final String CN_RESIDUAL_URINE = "residual_urine";
    public static final String CN_URINARY_INCONTINENCE = "urinary_incontinence";
    public static final String CN_ACUTE_URINARY_RETENTION = "acute_urinary_retention";
    public static final String CN_DISEASE_PROGRESSION = "disease_progression";
    public static final String CN_STRICTURE = "stricture";
    public static final String CN_HEALED = "healed";
    public static final String СТ_TREATMENT_ID = "treatment_id";
    public static final String CREATE_SCRIPT;

    static {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ").append(TN_PATIENT).append("(");
        sb.append(CN_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(CN_FIO).append(" TEXT, ");
        sb.append(CN_AGE).append(" INTEGER, ");
        sb.append(CN_STAGE_TNM).append(" INTEGER, ");
        sb.append(CN_PSA).append(" REAL, ");
        sb.append(CN_GLISSON).append(" INTEGER, ");
        sb.append(CN_PROSTATE_VOLUME).append(" REAL, ");
        sb.append(CN_PROSTATE_VOLUME_HIFU).append(" REAL, ");
        sb.append(CN_PROSTATE_LENGTH_HIFU).append(" REAL, ");
        sb.append(CN_PROSTATE_WIDTH_HIFU).append(" REAL, ");
        sb.append(CN_PROSTATE_HEIGHT_HIFU).append(" REAL, ");
        sb.append(CN_MAX_URINE_VELOCITY).append(" REAL, ");
        sb.append(CN_AV_URINE_VELOCITY).append(" REAL, ");
        sb.append(CN_RESIDUAL_URINE).append(" INTEGER, ");
        sb.append(CN_URINARY_INCONTINENCE).append(" INTEGER, ");
        sb.append(CN_ACUTE_URINARY_RETENTION).append(" INTEGER, ");
        sb.append(CN_DISEASE_PROGRESSION).append(" INTEGER, ");
        sb.append(CN_STRICTURE).append(" INTEGER, ");
        sb.append(CN_HEALED).append(" INTEGER, ");
        sb.append(СТ_TREATMENT_ID).append(" INTEGER);");
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
