package com.medicalequation.me.gui.activity;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import com.medicalequation.me.C;
import com.medicalequation.me.R;
import com.medicalequation.me.db.PatientProvider;
import com.medicalequation.me.db.PatientTable;
import com.medicalequation.me.gui.PatientViewBuilder;
import com.medicalequation.me.gui.TherapyAdapter;
import com.medicalequation.me.model.patient.Patient;
import com.medicalequation.me.model.therapy.TherapyType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 8/19/13
 * Time: 8:39 PM
 * May the force be with you always.
 */
public class PatientEditActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemSelectedListener {

    private long id;
    private Map<String, Number> mutValSaveBox = new HashMap<String, Number>();
    private Map<String, Number> immutValSaveBox = new HashMap<String, Number>();
    private Map<String, Number> resValSaveBox = new HashMap<String, Number>();

    private PatientViewBuilder viewBuilder;

    private TextView fioView;
    private Spinner therapyView;
    private TherapyAdapter adapter;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(C.Extra.ID, id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_patient_edit);
        fioView = (TextView) findViewById(R.id.patient_fio);
        therapyView = (Spinner) findViewById(R.id.patient_therapy);
        therapyView.setAdapter(adapter = new TherapyAdapter(this));
        viewBuilder = new PatientViewBuilder(this, true);
        if (savedInstanceState == null) {
            id = getIntent().getLongExtra(C.Extra.ID, 0);
        } else {
            id = savedInstanceState.getLong(C.Extra.ID);
        }
        Bundle b = new Bundle();
        b.putLong(C.Extra.ID, id);
        getLoaderManager().initLoader(1, b, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getLoaderManager().destroyLoader(1);
    }

    private void refreshUI(Patient patient) {
        fioView.setText(patient.fio);
        therapyView.setSelection(adapter.getPosition(patient.therapy.label));

        mutValSaveBox.putAll(patient.mutableValues);
        immutValSaveBox.putAll(patient.immutableValues);
        resValSaveBox.putAll(patient.resultValues);
        refreshTherapy(patient.therapy);
    }

    private void refreshTherapy(TherapyType therapyType) {
        viewBuilder.setTherapy(therapyType);
        viewBuilder.generate();

        viewBuilder.setMutableValues(mutValSaveBox);
        viewBuilder.setImmutableValues(immutValSaveBox);
        viewBuilder.setResultValues(resValSaveBox);
    }

    private void saveToBox() {
        mutValSaveBox.putAll(viewBuilder.getMutableValues());
        immutValSaveBox.putAll(viewBuilder.getImmutableValues());
        resValSaveBox.putAll(viewBuilder.getResultValues());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.m_complete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mi_done) {
            Patient patient = new Patient(id);
            patient.fio = fioView.getText().toString();
            patient.therapy = TherapyType.getByLabel(String.valueOf(therapyView.getSelectedItem()));
            patient.mutableValues = viewBuilder.getMutableValues();
            patient.immutableValues = viewBuilder.getImmutableValues();
            patient.resultValues = viewBuilder.getResultValues();
            if (!TextUtils.isGraphic(patient.fio)) {
                fioView.setError(getString(R.string.e_emptyFio));
                return false;
            }
            if (!viewBuilder.validateAll(patient.mutableValues, patient.immutableValues, patient.resultValues)) {
                return false;
            }
            Intent intent = new Intent(this, PatientViewActivity.class);
            if (patient.id > 0) {
                getContentResolver().update(PatientProvider.CONTENT_URI, patient.convert(), PatientTable.CN_ID + " = ?",
                        new String[]{String.valueOf(id)});
                intent.putExtra(C.Extra.ID, id);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            } else {
                long id = ContentUris.parseId(getContentResolver().insert(PatientProvider.CONTENT_URI, patient.convert()));
                intent.putExtra(C.Extra.ID, id);
            }
            startActivity(intent);
            finish();
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, Uri.parse(PatientProvider.CONTENT_URI + "/" + args.getLong(C.Extra.ID)), null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Patient patient = null;
        if (data != null) {
            if (data.moveToFirst()) {
                patient = new Patient();
                patient.parse(data);
            }
            data.close();
        }
        if (patient != null)
            refreshUI(patient);
        else
            refreshTherapy(TherapyType.getByName(String.valueOf(therapyView.getSelectedItem())));
        therapyView.setOnItemSelectedListener(this);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //nothing
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        saveToBox();
        refreshTherapy(TherapyType.getByLabel(adapter.getItem(position)));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
