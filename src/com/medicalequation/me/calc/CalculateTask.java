package com.medicalequation.me.calc;

import Jama.Matrix;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.medicalequation.me.C;
import com.medicalequation.me.db.PatientProvider;
import com.medicalequation.me.db.PatientTable;
import com.medicalequation.me.model.therapy.TherapyType;
import com.medicalequation.me.utils.PreferenceManager;

import java.lang.reflect.Type;
import java.util.*;

import static com.medicalequation.me.C.PreferenceKey.*;

/**
 * Created with IntelliJ IDEA.
 * User: Иван Гусев
 * Date: 08.05.13
 * Time: 2:22
 * May the Force be with you, always
 */
public class CalculateTask extends AsyncTask<List<CalcUnit>, Void, List<CalcUnit>> {

    private Context mContext;
    private Handler mHandler;
    private ProgressDialog mDialog;

    public CalculateTask(Context mContext, Handler mHandler) {
        this.mContext = mContext;
        this.mHandler = mHandler;
        mDialog = new ProgressDialog(mContext);
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                CalculateTask.this.cancel(true);
            }
        });
    }

    @Override
    protected void onPreExecute() {
        mDialog.setCancelable(true);
        mDialog.show();
    }

    @Override
    protected void onPostExecute(List<CalcUnit> result) {
        mDialog.dismiss();
    }

    @Override
    protected List<CalcUnit> doInBackground(List<CalcUnit>... params) {
        List<CalcUnit> result = new ArrayList<CalcUnit>();
        List<CalcUnit> input = params[0];
        double[][] bettas;
        Cursor patients;
        for (CalcUnit calcUnit : input) {
            CalcUnit resUnit = new CalcUnit();
            resUnit.therapyType = calcUnit.therapyType;
            patients = getPatients(calcUnit.therapyType);
            if (patients != null && patients.moveToFirst()) {
                if (!regressionActual(patients, calcUnit.therapyType)) {
                    bettas = calcRegression(patients);
                    saveCoefficients(THERAPY.replace(C.PlaceHolder.THERAPY, calcUnit.therapyType.name()), bettas,
                            patients.getCount());
                } else {
                    bettas = new double[RESULTS.length][];
                    for (int i = 0; i < RESULTS.length; i++) {
                        bettas[i] = PreferenceManager.getDoubleArray(mContext, THERAPY.replace(C.PlaceHolder.THERAPY,
                                calcUnit.therapyType.name()).replace(C.PlaceHolder.RESULT, RESULTS[i]));
                    }
                }
                int j;
                float resSum;
                resUnit.results = new HashMap<String, Number>();
                for (int i = 0; i < RESULTS.length; i++) {
                    j = 0;
                    resSum = 0;
                    for (String paramKey : calcUnit.results.keySet()) {
                        resSum += bettas[i][j] * calcUnit.results.get(paramKey).floatValue();
                        j++;
                    }
                    resUnit.results.put(RESULTS[i], resSum);
                }
            }
            result.add(resUnit);
        }
        Collections.sort(result);
        return result;
    }

    private Cursor getPatients(TherapyType therapyType) {
        return mContext.getContentResolver().query(PatientProvider.CONTENT_URI,
                null,
                PatientTable.CN_THERAPY + " = ?",
                new String[]{therapyType.name()},
                null);
    }

    private boolean regressionActual(Cursor patients, TherapyType therapyType) {
        return PreferenceManager.getInt(mContext, THERAPY_LAST_PATIENTS_NUM.replace(C.PlaceHolder.THERAPY, therapyType.name())) == patients.getCount();
    }

    private double[][] calcRegression(Cursor patients) {
        double[][] bettas = new double[RESULTS.length][];
        Matrix x = convertJSONArrayToMatrix(patients, PatientTable.CN_MUTABLE_VALUES);
        Matrix y = convertJSONArrayToMatrix(patients, PatientTable.CN_RESULT_VALUES);
        for (int i = 0; i < bettas.length; i++) {
            bettas[i] = convertMatrixToVector(calcRegressionCoefficients(x, y.getMatrix(0, y.getRowDimension(), i, i)));
        }
        return bettas;
    }

    private void saveCoefficients(String therapyName, double[][] bettas, int patientsCount) {
        for (int i = 0; i < RESULTS.length; i++) {
            PreferenceManager.saveDoubleArray(mContext, therapyName.replace(C.PlaceHolder.RESULT, RESULTS[i]), bettas[i]);
        }
        PreferenceManager.saveInt(mContext, THERAPY_LAST_PATIENTS_NUM.replace(C.PlaceHolder.THERAPY, therapyName), patientsCount);
    }

    private double[] convertMatrixToVector(Matrix matrix) {
        double[] res = new double[matrix.getRowDimension()];
        for (int i = 0; i < matrix.getRowDimension(); i++) {
            res[i] = matrix.get(i, 0);
        }
        return res;
    }

    private Matrix convertJSONArrayToMatrix(Cursor patients, String jsonArrayColumnName) {
        Matrix matrix;
        String charJson;
        Type type = new TypeToken<Map<String, Number>>() {
        }.getType();
        Map<String, Number> map;
        if (!patients.moveToFirst()) {
            return null;
        }
        int i = 0;
        do {
            charJson = patients.getString(patients.getColumnIndex(jsonArrayColumnName));
            map = new Gson().fromJson(charJson, type);
            matrix = new Matrix(patients.getCount(), map.size() + 1);
            matrix.set(i, 0, 1);
            int j = 0;
            for (String key : map.keySet()) {
                matrix.set(i, j, map.get(key).floatValue());
                j++;
            }
        } while (patients.moveToNext());
        return matrix;
    }

    private Matrix calcRegressionCoefficients(Matrix x, Matrix y) {
        Matrix A = x.transpose().times(x);
        Matrix invA = A.inverse();
        Matrix d = x.transpose().times(y);
        return invA.times(d);
    }

    private boolean test(double[] y) {
        return y[0] <= 3 && y[1] <= 3 && y[2] <= 3 && y[3] <= 3;
    }

    static class Result {
        public double[] y;
        public int therapyIndex;
    }
}