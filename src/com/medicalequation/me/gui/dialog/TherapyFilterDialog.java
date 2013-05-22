package com.medicalequation.me.gui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Spinner;
import com.medicalequation.me.R;
import com.medicalequation.me.gui.ListQuery;
import com.medicalequation.me.gui.activity.PatientListActivity;

/**
 * Created with IntelliJ IDEA.
 * User: Иван Гусев
 * Date: 07.05.13
 * Time: 8:48
 * May the Force be with you, always
 */
public class TherapyFilterDialog extends Dialog implements View.OnClickListener {

    private PatientListActivity activity;
    private ListQuery listQuery;

    private Spinner mTherapy;
    private Spinner mTreatment;

    public TherapyFilterDialog(PatientListActivity activity, ListQuery listQuery) {
        super(activity);
        this.activity = activity;
        this.listQuery = listQuery;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.d_patient_filter);
        setTitle(R.string.therapy_filter_title);
        mTherapy = (Spinner) findViewById(R.id.therapyChooser);
        mTreatment = (Spinner) findViewById(R.id.treatmentChooser);

        mTherapy.setSelection(listQuery.therapy.isEmpty() ? 0 : Integer.parseInt(listQuery.therapy.split(" = ")[1]) + 1);

        if (!listQuery.treatment.isEmpty()) {
            mTreatment.setSelection(listQuery.treatment.contains("1") ? 2 : 1);
        } else {
            mTreatment.setSelection(0);
        }

        findViewById(R.id.accept).setOnClickListener(this);
        findViewById(R.id.cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.accept) {
            listQuery.setTherapy(mTherapy.getSelectedItemPosition());
            listQuery.setTreatment(mTreatment.getSelectedItemPosition());
            activity.reloadCursor(listQuery);
            dismiss();
        } else if (id == R.id.cancel) {
            dismiss();
        }
    }
}
