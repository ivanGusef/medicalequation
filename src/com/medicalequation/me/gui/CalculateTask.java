package com.medicalequation.me.gui;

import Jama.Matrix;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.medicalequation.me.C;
import com.medicalequation.me.db.PatientProvider;
import com.medicalequation.me.db.PatientTable;
import com.medicalequation.me.utils.PreferenceManager;

import static com.medicalequation.me.C.PreferenceKey.*;

/**
 * Created with IntelliJ IDEA.
 * User: Иван Гусев
 * Date: 08.05.13
 * Time: 2:22
 * May the Force be with you, always
 */
public class CalculateTask extends AsyncTask<Double, Void, CalculateTask.Result> {

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
    protected void onPostExecute(Result result) {
        mDialog.dismiss();
        if (result != null) {
            Bundle data = new Bundle();
            data.putDoubleArray(C.Extra.RESULTS, result.y);
            data.putInt(C.Extra.TREATMENT_INDEX, result.therapyIndex);
            Message msg = new Message();
            msg.setData(data);
            msg.what = C.What.RESULT;
            mHandler.sendMessage(msg);
        }
    }

    @Override
    protected Result doInBackground(Double... params) {
        Result result = new Result();
        result.y = new double[4];
        Cursor patients = getPatients(false);
        double[][] bettas;
        if (patients != null && patients.moveToFirst()) {
            if (!regressionActual(patients, false)) {
                bettas = calcRegression(patients);
                saveCoefficients(HIFU, bettas, HIFU_LAST_PATIENTS_NUM, patients.getCount());
            }
            bettas = new double[4][];
            for (int i = 0; i < RESULTS.length; i++) {
                bettas[i] = PreferenceManager.getDoubleArray(mContext, HIFU.replace(PreferenceManager.PLACE_HOLDER_RESULT, RESULTS[i]));
                for (int j = 0; j < params.length; j++) {
                    result.y[i] += bettas[i][j] * params[j];
                }
            }
            if (test(result.y)) {
                return result;
            }
        }

        patients = getPatients(true);
        if (patients != null && patients.moveToFirst()) {
            if (!regressionActual(patients, true)) {
                bettas = calcRegression(patients);
                saveCoefficients(HIFU_TUR, bettas, HIFU_TUR_LAST_PATIENTS_NUM, patients.getCount());
            }
            bettas = new double[4][];
            for (int i = 0; i < RESULTS.length; i++) {
                bettas[i] = PreferenceManager.getDoubleArray(mContext, HIFU_TUR.replace(PreferenceManager.PLACE_HOLDER_RESULT, RESULTS[i]));
                for (int j = 0; j < params.length; j++) {
                    result.y[i] += bettas[i][j] * params[j];
                }
            }
            if (test(result.y)) {
                result.therapyIndex = 1;
            } else {
                result.therapyIndex = 2;
            }
            return result;
        }
        return null;
    }

    private Cursor getPatients(boolean turExistence) {
        return mContext.getContentResolver().query(PatientProvider.CONTENT_URI,
                null,
                PatientTable.CN_HEALED + " = ? AND " + PatientTable.СТ_TREATMENT_ID + " = ?",
                new String[]{"1", turExistence ? "1" : "0"},
                null);
    }

    private boolean regressionActual(Cursor patients, boolean turExistence) {
        return PreferenceManager.getInt(mContext, turExistence ? HIFU_TUR_LAST_PATIENTS_NUM : HIFU_LAST_PATIENTS_NUM) == patients.getCount();
    }

    private double[][] calcRegression(Cursor patients) {
        double[][] bettas = new double[4][];
        Matrix[] xy = retrieveXY(patients);
        bettas[0] = convertMatrixToVector(calcRegressionCoefficients(xy[0], xy[1]));
        bettas[1] = convertMatrixToVector(calcRegressionCoefficients(xy[0], xy[2]));
        bettas[2] = convertMatrixToVector(calcRegressionCoefficients(xy[0], xy[3]));
        bettas[3] = convertMatrixToVector(calcRegressionCoefficients(xy[0], xy[4]));
        return bettas;
    }

    private void saveCoefficients(String key, double[][] bettas, String patientsCountKey, int patientsCount) {
        for (int i = 0; i < RESULTS.length; i++) {
            PreferenceManager.saveDoubleArray(mContext, key.replace(PreferenceManager.PLACE_HOLDER_RESULT, RESULTS[i]), bettas[i]);
        }
        PreferenceManager.saveInt(mContext, patientsCountKey, patientsCount);
    }

    private double[] convertMatrixToVector(Matrix matrix) {
        double[] res = new double[matrix.getRowDimension()];
        for (int i = 0; i < matrix.getRowDimension(); i++) {
            res[i] = matrix.get(i, 0);
        }
        return res;
    }

    private Matrix[] retrieveXY(Cursor patients) {
        Matrix[] res = new Matrix[5];
        Matrix x;
        Matrix y1;
        Matrix y2;
        Matrix y3;
        Matrix y4;
        x = new Matrix(patients.getCount(), 13);
        y1 = new Matrix(patients.getCount(), 1);
        y2 = new Matrix(patients.getCount(), 1);
        y3 = new Matrix(patients.getCount(), 1);
        y4 = new Matrix(patients.getCount(), 1);
        int i = 0;
        do {
            x.set(i, 0, 1);
            x.set(i, 1, patients.getDouble(patients.getColumnIndex(PatientTable.CN_AGE)));
            x.set(i, 2, patients.getDouble(patients.getColumnIndex(PatientTable.CN_STAGE_TNM)));
            x.set(i, 3, patients.getDouble(patients.getColumnIndex(PatientTable.CN_PSA)));
            x.set(i, 4, patients.getDouble(patients.getColumnIndex(PatientTable.CN_GLISSON)));
            x.set(i, 5, patients.getDouble(patients.getColumnIndex(PatientTable.CN_PROSTATE_VOLUME)));
            x.set(i, 6, patients.getDouble(patients.getColumnIndex(PatientTable.CN_PROSTATE_VOLUME_HIFU)));
            x.set(i, 7, patients.getDouble(patients.getColumnIndex(PatientTable.CN_PROSTATE_LENGTH_HIFU)));
            x.set(i, 8, patients.getDouble(patients.getColumnIndex(PatientTable.CN_PROSTATE_WIDTH_HIFU)));
            x.set(i, 9, patients.getDouble(patients.getColumnIndex(PatientTable.CN_PROSTATE_HEIGHT_HIFU)));
            x.set(i, 10, patients.getDouble(patients.getColumnIndex(PatientTable.CN_MAX_URINE_VELOCITY)));
            x.set(i, 11, patients.getDouble(patients.getColumnIndex(PatientTable.CN_AV_URINE_VELOCITY)));
            x.set(i, 12, patients.getDouble(patients.getColumnIndex(PatientTable.CN_RESIDUAL_URINE)));

            y1.set(i, 0, patients.getDouble(patients.getColumnIndex(PatientTable.CN_URINARY_INCONTINENCE)));
            y2.set(i, 0, patients.getDouble(patients.getColumnIndex(PatientTable.CN_ACUTE_URINARY_RETENTION)));
            y3.set(i, 0, patients.getDouble(patients.getColumnIndex(PatientTable.CN_DISEASE_PROGRESSION)));
            y4.set(i, 0, patients.getDouble(patients.getColumnIndex(PatientTable.CN_STRICTURE)));
            i++;
        } while (patients.moveToNext());
        patients.moveToFirst();
        res[0] = x;
        res[1] = y1;
        res[2] = y2;
        res[3] = y3;
        res[4] = y4;
        return res;
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
