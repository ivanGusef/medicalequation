package com.medicalequation.me.gui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.medicalequation.me.R;

public class StartActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_start);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_patient:
                startActivity(new Intent(this, PatientDetailsActivity.class));
                break;
            case R.id.patients:
                startActivity(new Intent(this, PatientListActivity.class));
                break;
            case R.id.about:
                Toast.makeText(this, "Under construction", Toast.LENGTH_SHORT).show();
                break;
            case R.id.manual:
                Toast.makeText(this, "Under construction", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
