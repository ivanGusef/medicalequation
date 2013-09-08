package com.medicalequation.me.gui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
    public void onConfigurationChanged(Configuration newConfig) {
        Log.i("INFO/ActivityManager", "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
        getLoaderManager().destroyLoader(1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("INFO/ActivityManager", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_patient_view);
        fioView = (TextView) findViewById(R.id.patient_fio);
        therapyView = (TextView) findViewById(R.id.patient_therapy);
        viewBuilder = new PatientViewBuilder(this, false);
        if (savedInstanceState == null) {
            id = getIntent().getLongExtra(C.Extra.ID, 0);
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
        therapyView.setText(patient.therapy.label);

        viewBuilder.setTherapy(patient.therapy);
        viewBuilder.generate();

        viewBuilder.setMutableValues(patient.mutableValues);
        viewBuilder.setImmutableValues(patient.immutableValues);
        viewBuilder.setResultValues(patient.resultValues);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.m_patient_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mi_edit) {
            startActivity(new Intent(this, PatientEditActivity.class).putExtra(C.Extra.ID, id));
        } else {
            showDeleteDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.deletion));
        builder.setMessage(getString(R.string.delete_confirmation));
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    Uri uriForDel = Uri.parse(PatientProvider.CONTENT_URI + "/" + id);
                    getContentResolver().delete(uriForDel, null, null);
                    finish();
                }
                dialog.dismiss();
            }
        };
        builder.setNegativeButton(R.string.no, listener);
        builder.setPositiveButton(R.string.yes, listener);
        builder.create().show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i("INFO/ActivityManager", "onCreateLoader");
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
        Log.i("INFO/ActivityManager", "onLoadFinished");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.i("INFO/ActivityManager", "onLoaderReset");
        //nothing
    }
}
