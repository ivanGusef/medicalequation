package com.medicalequation.me.model.patient;

import android.content.ContentValues;
import android.database.Cursor;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.medicalequation.me.db.PatientTable;
import com.medicalequation.me.model.BaseEntity;
import com.medicalequation.me.model.therapy.TherapyType;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 8/4/13
 * Time: 9:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class Patient implements BaseEntity {

    public long id;
    public String fio;
    public TherapyType therapy;
    public Map<String, Number> mutableValues;
    public Map<String, Number> immutableValues;
    public Map<String, Number> resultValues;

    @Override
    public ContentValues convert() {
        ContentValues cv = new ContentValues();
        cv.put(PatientTable.CN_FIO, fio);
        cv.put(PatientTable.CN_THERAPY, therapy.name());
        cv.put(PatientTable.CN_MUTABLE_VALUES, new Gson().toJson(mutableValues));
        cv.put(PatientTable.CN_IMMUTABLE_VALUES, new Gson().toJson(immutableValues));
        cv.put(PatientTable.CN_RESULT_VALUES, new Gson().toJson(resultValues));
        return cv;
    }

    @Override
    public void parse(Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex(PatientTable.CN_ID));
        fio = cursor.getString(cursor.getColumnIndex(PatientTable.CN_FIO));
        therapy = TherapyType.getByName(cursor.getString(cursor.getColumnIndex(PatientTable.CN_THERAPY)));
        mutableValues = new Gson().fromJson(cursor.getString(cursor.getColumnIndex(PatientTable.CN_MUTABLE_VALUES)),
                new TypeToken<Map<String, String>>() {
                }.getType());
        immutableValues = new Gson().fromJson(cursor.getString(cursor.getColumnIndex(PatientTable.CN_IMMUTABLE_VALUES)),
                new TypeToken<Map<String, String>>() {
                }.getType());
        resultValues = new Gson().fromJson(cursor.getString(cursor.getColumnIndex(PatientTable.CN_RESULT_VALUES)),
                new TypeToken<Map<String, String>>() {
                }.getType());
    }
}
