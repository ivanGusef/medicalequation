package com.medicalequation.me.gui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import com.medicalequation.me.R;
import com.medicalequation.me.gui.KeyValueEntry;
import com.medicalequation.me.gui.ListQuery;
import com.medicalequation.me.gui.TherapyAdapter;
import com.medicalequation.me.gui.activity.PatientListActivity;
import com.medicalequation.me.model.therapy.TherapyType;

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
    private TherapyAdapter mAdapter;

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
        mAdapter = new TherapyAdapter(activity, true);
        mTherapy = (Spinner) findViewById(R.id.treatmentChooser);
        mTherapy.setAdapter(mAdapter);
        mTherapy.setSelection(mAdapter.getPosition(listQuery.therapy == null ? TherapyAdapter.HEADER_LABEL : listQuery.therapy.label));
        findViewById(R.id.accept).setOnClickListener(this);
        findViewById(R.id.cancel).setOnClickListener(this);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        activity.getLoaderManager().destroyLoader(1);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.accept) {
            listQuery.setTherapy(TherapyType.getByLabel(mAdapter.getItem(mTherapy.getSelectedItemPosition())));
            activity.reloadCursor();
        }
        dismiss();
    }
}
