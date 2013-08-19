package com.medicalequation.me.gui.activity;

import Jama.Matrix;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.medicalequation.me.C;
import com.medicalequation.me.R;
import com.medicalequation.me.TherapyManager;
import com.medicalequation.me.db.PatientProvider;
import com.medicalequation.me.db.PatientTable;
import com.medicalequation.me.exception.ValidateException;
import com.medicalequation.me.gui.dialog.TherapyDetailsDialog;
import com.medicalequation.me.model.calc.CalcUnit;
import com.medicalequation.me.model.therapy.Line;
import com.medicalequation.me.model.therapy.LineType;
import com.medicalequation.me.model.therapy.Therapy;
import com.medicalequation.me.model.therapy.TherapyType;
import com.medicalequation.me.utils.PreferenceManager;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

import static com.medicalequation.me.C.PreferenceKey.*;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 8/19/13
 * Time: 11:02 PM
 * May the force be with you always.
 */
public class CalculateActivity extends Activity implements Handler.Callback {

    public static final int VALIDATE_ERROR = 1;
    public static final String ERROR_MSG_KEY = "errorMsg";
    public static final String LINE_NAME_KEY = "lineName";

    private Map<String, TextView> charHolder = new HashMap<String, TextView>();
    private OnTherapyClickListener listener = new OnTherapyClickListener();
    private TherapyManager therapyManager;
    private Handler errorHandler = new Handler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_calculate);
        therapyManager = TherapyManager.getInstance(this);
        new CreateGuiTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.m_calculate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mi_calc) {
            new CalculateTask().execute();
        }
        return true;
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == VALIDATE_ERROR) {
            Bundle data = msg.getData();
            charHolder.get(data.getString(LINE_NAME_KEY)).setError(data.getString(ERROR_MSG_KEY));
            return true;
        }
        return false;
    }

    private class CreateGuiTask extends AsyncTask<Void, Void, List<Line>> {

        private static final String FILE_NAME = "full_mutable.json";

        @Override
        protected List<Line> doInBackground(Void... params) {
            try {
                String linesJson = IOUtils.toString(getAssets().open(FILE_NAME));
                return new Gson().fromJson(linesJson, new TypeToken<List<Line>>() {
                }.getType());
            } catch (IOException e) {
                Log.e("CalculateActivity -> CreateGuiTask -> doInBackground", e.getMessage(), e);
                finish();
            }
            return new ArrayList<Line>();
        }

        @Override
        protected void onPostExecute(List<Line> lines) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout container = new LinearLayout(CalculateActivity.this);
            container.setLayoutParams(layoutParams);
            container.setOrientation(LinearLayout.VERTICAL);
            fillContainer(container, lines);
            ((LinearLayout) findViewById(R.id.char_container)).addView(container);
        }

        private void fillContainer(LinearLayout container, List<Line> lines) {
            View lineView;
            TextView valueView;
            for (Line line : lines) {
                lineView = getLayoutInflater().inflate(R.layout.char_edit_line, null);
                ((TextView) lineView.findViewById(R.id.label)).setText(line.label);
                valueView = (TextView) lineView.findViewById(R.id.value);
                valueView.setInputType(line.type.equals(LineType.INT) ? InputType.TYPE_CLASS_NUMBER
                        : InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                charHolder.put(line.name, valueView);
                container.addView(lineView);
            }
        }
    }

    private class CalculateTask extends AsyncTask<Void, Void, ResultCalc> {

        private ProgressDialog mDialog;

        public CalculateTask() {
            mDialog = new ProgressDialog(CalculateActivity.this);
            mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    CalculateTask.this.cancel(true);
                }
            });
            mDialog.setMessage(getString(R.string.calculating));
        }

        @Override
        protected void onPreExecute() {
            mDialog.setCancelable(true);
            mDialog.show();
        }

        @Override
        protected void onPostExecute(ResultCalc result) {
            mDialog.dismiss();
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.result_container);
            linearLayout.removeAllViewsInLayout();
            if (result == null) {
                return;
            }
            if (result.errorMessage != null) {
                Toast.makeText(CalculateActivity.this, result.errorMessage, Toast.LENGTH_SHORT).show();
            } else {
                Button therapyBtn;
                int i = 1;
                for (CalcUnit resultUnit : result.resultUnits) {
                    therapyBtn = (Button) getLayoutInflater().inflate(R.layout.therapy_button, null);
                    therapyBtn.setText(i + ". " + resultUnit.therapy.type.label);
                    therapyBtn.setTag(resultUnit);
                    therapyBtn.setOnClickListener(listener);
                    linearLayout.addView(therapyBtn);
                    i++;
                }
                findViewById(R.id.result_header).setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected ResultCalc doInBackground(Void... params) {
            /**
             * Result values
             */
            ResultCalc result = new ResultCalc();
            List<CalcUnit> resultUnits = new ArrayList<CalcUnit>();
            /**
             * Input values
             */
            List<CalcUnit> input = new ArrayList<CalcUnit>();
            Therapy therapy;
            for (TherapyType therapyType : TherapyType.values()) {
                therapy = therapyManager.loadTherapy(therapyType);
                try {
                    input.add(getCharacteristics(therapy));
                } catch (ValidateException e) {
                    return null;
                }
            }
            filterCalcUnits(input);
            if (input.isEmpty()) {
                result.errorMessage = getString(R.string.e_not_enough_data);
                return result;
            }
            /**
             * Calculation
             */
            double[][] bettas;
            Cursor patients;
            for (CalcUnit calcUnit : input) {
                CalcUnit resUnit = new CalcUnit();
                resUnit.therapy = calcUnit.therapy;
                patients = getPatients(calcUnit.therapy);
                if (patients != null && patients.moveToFirst()) {
                    if (!regressionActual(patients, calcUnit.therapy)) {
                        bettas = calcRegression(calcUnit.therapy, patients);
                        saveCoefficients(THERAPY.replace(C.PlaceHolder.THERAPY, calcUnit.therapy.type.name()), bettas,
                                patients.getCount());
                    } else {
                        bettas = new double[RESULTS.length][];
                        for (int i = 0; i < RESULTS.length; i++) {
                            bettas[i] = PreferenceManager.getDoubleArray(CalculateActivity.this, THERAPY.replace(C.PlaceHolder.THERAPY,
                                    calcUnit.therapy.type.name()).replace(C.PlaceHolder.RESULT, RESULTS[i]));
                        }
                    }
                    int i = 0, j;
                    double resSum;
                    resUnit.results = new HashMap<String, Number>();
                    for (Line resultLine : calcUnit.therapy.resultLines) {
                        j = 1;
                        resSum = bettas[i][0];
                        for (Line mutableLine : calcUnit.therapy.mutableLines) {
                            resSum += bettas[i][j] * calcUnit.results.get(mutableLine.name).doubleValue();
                            j++;
                        }
                        i++;
                        resUnit.results.put(resultLine.name, resSum);
                    }
                }
                resultUnits.add(resUnit);
            }
            Collections.sort(resultUnits);
            result.resultUnits = resultUnits;
            return result;
        }

        private void filterCalcUnits(List<CalcUnit> calcUnits) {
            ListIterator<CalcUnit> iterator = calcUnits.listIterator();
            while (iterator.hasNext()) {
                CalcUnit calcUnit;
                for (String field : (calcUnit = iterator.next()).results.keySet()) {
                    if (calcUnit.results.get(field) == null) {
                        iterator.remove();
                        break;
                    }
                }
            }
        }

        private CalcUnit getCharacteristics(Therapy therapy) {
            CalcUnit calcUnit = new CalcUnit();
            calcUnit.therapy = therapy;
            Number value;
            String strValue;
            TextView editor;
            for (Line line : therapy.mutableLines) {
                editor = charHolder.get(line.name);
                strValue = editor.getText().toString();
                if (!TextUtils.isGraphic(strValue)) {
                    value = null;
                } else {
                    if (line.type.equals(LineType.INT))
                        value = Integer.valueOf(strValue);
                    else
                        value = Double.valueOf(strValue);
                }
                String errorMessage = line.validate(CalculateActivity.this, value, true);
                if (errorMessage != null) {
                    Message message = new Message();
                    message.what = VALIDATE_ERROR;
                    Bundle data = new Bundle();
                    data.putString(ERROR_MSG_KEY, errorMessage);
                    data.putString(LINE_NAME_KEY, line.name);
                    message.setData(data);
                    errorHandler.sendMessage(message);
                    throw new ValidateException(errorMessage);
                }
                calcUnit.results.put(line.name, value);
            }
            return calcUnit;
        }

        private Cursor getPatients(Therapy therapy) {
            return CalculateActivity.this.getContentResolver().query(PatientProvider.CONTENT_URI,
                    null,
                    PatientTable.CN_THERAPY + " = ?",
                    new String[]{therapy.type.name()},
                    null);
        }

        private boolean regressionActual(Cursor patients, Therapy therapy) {
            return PreferenceManager.getInt(CalculateActivity.this, THERAPY_LAST_PATIENTS_NUM.replace(C.PlaceHolder.THERAPY,
                    therapy.type.name())) == patients.getCount();
        }

        private double[][] calcRegression(Therapy therapy, Cursor patients) {
            double[][] bettas = new double[RESULTS.length][];
            Matrix x = convertJSONArrayToMatrix(therapy.mutableLines, patients, PatientTable.CN_MUTABLE_VALUES, true);
            Matrix y = convertJSONArrayToMatrix(therapy.resultLines, patients, PatientTable.CN_RESULT_VALUES, false);
            for (int i = 0; i < bettas.length; i++) {
                bettas[i] = convertMatrixToVector(calcRegressionCoefficients(x, y.getMatrix(0, y.getRowDimension() - 1, i, i)));
            }
            return bettas;
        }

        private void saveCoefficients(String therapyName, double[][] bettas, int patientsCount) {
            for (int i = 0; i < RESULTS.length; i++) {
                PreferenceManager.saveDoubleArray(CalculateActivity.this, therapyName.replace(C.PlaceHolder.RESULT, RESULTS[i]), bettas[i]);
            }
            PreferenceManager.saveInt(CalculateActivity.this, THERAPY_LAST_PATIENTS_NUM.replace(C.PlaceHolder.THERAPY, therapyName), patientsCount);
        }

        private double[] convertMatrixToVector(Matrix matrix) {
            double[] res = new double[matrix.getRowDimension()];
            for (int i = 0; i < matrix.getRowDimension(); i++) {
                res[i] = matrix.get(i, 0);
            }
            return res;
        }

        private Matrix convertJSONArrayToMatrix(List<Line> lines, Cursor patients, String jsonArrayColumnName, boolean addOnes) {
            Matrix matrix;
            String charJson;
            Type type = new TypeToken<Map<String, Number>>() {
            }.getType();
            Map<String, Number> map;
            if (!patients.moveToFirst()) {
                return null;
            }
            int i = 0;
            charJson = patients.getString(patients.getColumnIndex(jsonArrayColumnName));
            Gson gson = new Gson();
            map = gson.fromJson(charJson, type);
            matrix = new Matrix(patients.getCount(), addOnes ? map.size() + 1 : map.size());
            do {
                charJson = patients.getString(patients.getColumnIndex(jsonArrayColumnName));
                map = gson.fromJson(charJson, type);
                if (addOnes) {
                    matrix.set(i, 0, 1);
                }
                int j = addOnes ? 1 : 0;
                for (Line line : lines) {
                    matrix.set(i, j, map.get(line.name).doubleValue());
                    j++;
                }
                i++;
            } while (patients.moveToNext());
            return matrix;
        }

        private Matrix calcRegressionCoefficients(Matrix x, Matrix y) {
            Matrix A = x.transpose().times(x);
            Matrix invA = A.inverse();
            Matrix d = x.transpose().times(y);
            return invA.times(d);
        }
    }

    private class ResultCalc {
        List<CalcUnit> resultUnits;
        String errorMessage;
    }

    private class OnTherapyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            new TherapyDetailsDialog(CalculateActivity.this, (CalcUnit) v.getTag()).show();
        }
    }
}
