package com.medicalequation.me.db;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created with IntelliJ IDEA.
 * User: Иван Гусев
 * Date: 07.05.13
 * Time: 0:05
 * May the Force be with you, always
 */
public class PatientProvider extends ContentProvider {

    private static final int PATIENTS = 1;
    private static final int PATIENT_ID = 2;
    private static final int TREATMENT_FILTERED_PATIENTS = 3;
    private static final int NAME_FILTERED_PATIENTS = 4;
    private static final int TREATMENTS = 5;
    private static final int TREATMENT_ID = 6;

    private static final String AUTHORITY = "com.medicalequation.me.provider";

    private static final String PATIENTS_PATH = "patients";
    private static final String TREATMENT_PATH = "treatment";
    private static final String TREATMENT_FILTER = "treatment_filter";
    private static final String NAME_FILTER = "name_filter";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PATIENTS_PATH);
    public static final Uri TREATMENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TREATMENT_PATH);
    public static final Uri NAME_FILTER_URI = Uri.parse("content://" + AUTHORITY + "/" + PATIENTS_PATH + "/" + NAME_FILTER);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/patients";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/patient";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, PATIENTS_PATH, PATIENTS);
        sURIMatcher.addURI(AUTHORITY, PATIENTS_PATH + "/#", PATIENT_ID);
        sURIMatcher.addURI(AUTHORITY, PATIENTS_PATH + "/" + TREATMENT_FILTER + "/*", TREATMENT_FILTERED_PATIENTS);
        sURIMatcher.addURI(AUTHORITY, PATIENTS_PATH + "/" + NAME_FILTER + "/*", NAME_FILTERED_PATIENTS);
        sURIMatcher.addURI(AUTHORITY, TREATMENT_PATH, TREATMENTS);
        sURIMatcher.addURI(AUTHORITY, TREATMENT_PATH + "/#", TREATMENT_ID);
    }

    private DBOpenHelper mDatabaseHelper;

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new DBOpenHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        checkColumns(projection);

        queryBuilder.setTables(PatientTable.TN_PATIENT);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case PATIENTS:
                break;
            case PATIENT_ID:
                queryBuilder.appendWhere(PatientTable.CN_ID + "=" + uri.getLastPathSegment());
                break;
            case TREATMENT_FILTERED_PATIENTS:
                String[] args = uri.getLastPathSegment().split("&");
                StringBuilder sb = new StringBuilder();
                String[] keyValue;
                for (String arg : args) {
                    keyValue = arg.split("=");
                    sb.append(keyValue[0]).append(" = ").append(keyValue[1]).append(" OR ");
                }
                sb.delete(sb.length() - 5, sb.length());
                queryBuilder.appendWhere(sb.toString());
                break;
            case NAME_FILTERED_PATIENTS:
                queryBuilder.appendWhere(PatientTable.CN_FIO + " LIKE '%" + uri.getLastPathSegment() + "%'");
                break;
            case TREATMENT_ID:
                queryBuilder.appendWhere(TreatmentTable.CN_ID + "=" + uri.getLastPathSegment());
            case TREATMENTS:
                queryBuilder.setTables(TreatmentTable.TN_TREATMENT);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = mDatabaseHelper.getWritableDatabase();
        long id;
        switch (uriType) {
            case PATIENTS:
                id = sqlDB.insert(PatientTable.TN_PATIENT, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(PATIENTS_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = mDatabaseHelper.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case PATIENTS:
                rowsDeleted = sqlDB.delete(PatientTable.TN_PATIENT, selection, selectionArgs);
                break;
            case PATIENT_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(PatientTable.TN_PATIENT, PatientTable.CN_ID + "=" + id, null);
                } else {
                    rowsDeleted = sqlDB.delete(PatientTable.TN_PATIENT, PatientTable.CN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = mDatabaseHelper.getWritableDatabase();
        int rowsUpdated;
        switch (uriType) {
            case PATIENTS:
                rowsUpdated = sqlDB.update(PatientTable.TN_PATIENT, values, selection, selectionArgs);
                break;
            case PATIENT_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(PatientTable.TN_PATIENT, values, PatientTable.CN_ID + "=" + id, null);
                } else {
                    rowsUpdated = sqlDB.update(PatientTable.TN_PATIENT, values, PatientTable.CN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private void checkColumns(String[] projection) {
        String[] available = {PatientTable.CN_ID, PatientTable.CN_FIO, PatientTable.CN_AGE, PatientTable.CN_STAGE_TNM,
                PatientTable.CN_PSA, PatientTable.CN_GLISSON, PatientTable.CN_PROSTATE_VOLUME,
                PatientTable.CN_PROSTATE_VOLUME_HIFU, PatientTable.CN_PROSTATE_LENGTH_HIFU,
                PatientTable.CN_PROSTATE_WIDTH_HIFU, PatientTable.CN_PROSTATE_HEIGHT_HIFU,
                PatientTable.CN_MAX_URINE_VELOCITY, PatientTable.CN_AV_URINE_VELOCITY, PatientTable.CN_RESIDUAL_URINE,
                PatientTable.CN_ACUTE_URINARY_RETENTION, PatientTable.CN_DISEASE_PROGRESSION, PatientTable.CN_STRICTURE,
                PatientTable.CN_HEALED, PatientTable.СТ_TREATMENT_ID};
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }
}
