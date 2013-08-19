package com.medicalequation.me;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.medicalequation.me.gui.PatientViewBuilder;
import com.medicalequation.me.model.therapy.TherapyType;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 8/5/13
 * Time: 12:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestActivity extends Activity {

    private PatientViewBuilder patientViewBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_patient_edit);
        patientViewBuilder = new PatientViewBuilder(this, TherapyType.HIFU, true);
        patientViewBuilder.generate();

        Spinner spinner = (Spinner) findViewById(R.id.patient_therapy);
        ArrayAdapter<String> adapter = new TestAdapter();
        spinner.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.m_complete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        patientViewBuilder.getMutableValues();
        return true;
    }

    private class TestAdapter extends ArrayAdapter<String> {


        public TestAdapter() {
            super(TestActivity.this, R.layout.i_therapy, new String[]{"Один", "Два", "Три", "Четыре"});
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }
    }
}
