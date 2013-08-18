package com.medicalequation.me.gui.activity;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import com.medicalequation.me.C;
import com.medicalequation.me.R;
import com.medicalequation.me.db.PatientProvider;
import com.medicalequation.me.gui.PatientViewBuilder;
import com.medicalequation.me.model.patient.Patient;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 8/19/13
 * Time: 1:00 AM
 * May the force be with you always.
 */
public class PatientViewActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    private long id;

    private PatientViewBuilder viewBuilder;

    private TextView fioView;
    private TextView therapyView;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(C.Extra.ID, id);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        id = savedInstanceState.getLong(C.Extra.ID);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_patient_view);
        fioView = (TextView) findViewById(R.id.patient_fio);
        therapyView = (TextView) findViewById(R.id.patient_therapy);
        viewBuilder = new PatientViewBuilder(this, false);
        if (savedInstanceState == null) {
            id = getIntent().getLongExtra(C.Extra.ID, 0);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Bundle b = new Bundle();
        b.putLong(C.Extra.ID, id);
        getLoaderManager().initLoader(1, b, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getLoaderManager().destroyLoader(1);
    }

    private void refreshUI(Patient patient) {
        fioView.setText(patient.fio);
        therapyView.setText(patient.therapy.label);

        viewBuilder.generate(patient.therapy);

        viewBuilder.setMutableValues(patient.mutableValues);
        viewBuilder.setImmutableValues(patient.immutableValues);
        viewBuilder.setResultValues(patient.resultValues);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, Uri.parse(PatientProvider.CONTENT_URI + "/" + args.getLong(C.Extra.ID)), null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            if (data.moveToFirst()) {
                Patient patient = new Patient();
                patient.parse(data);
                refreshUI(patient);
            }
            data.close();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //nothing
    }
}
