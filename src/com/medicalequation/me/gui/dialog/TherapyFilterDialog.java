package com.medicalequation.me.gui.dialog;

import android.app.Dialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import com.medicalequation.me.R;
import com.medicalequation.me.db.PatientProvider;
import com.medicalequation.me.gui.ListQuery;
import com.medicalequation.me.gui.TreatmentAdapter;
import com.medicalequation.me.gui.activity.PatientListActivity;

/**
 * Created with IntelliJ IDEA.
 * User: Иван Гусев
 * Date: 07.05.13
 * Time: 8:48
 * May the Force be with you, always
 */
public class TherapyFilterDialog extends Dialog implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private PatientListActivity activity;
    private ListQuery listQuery;

    private Spinner mTreatment;
    private Spinner mHealed;
    private TreatmentAdapter mAdapter;

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
        mAdapter = new TreatmentAdapter(activity);
        mTreatment = (Spinner) findViewById(R.id.treatmentChooser);
        mTreatment.setAdapter(mAdapter);
        mHealed = (Spinner) findViewById(R.id.healedChooser);
        activity.getLoaderManager().initLoader(1, null, this);

        if (!listQuery.healed.isEmpty()) {
            mHealed.setSelection(listQuery.healed.contains("1") ? 2 : 1);
        } else {
            mHealed.setSelection(0);
        }

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
            listQuery.setTreatment(mTreatment.getSelectedItemId());
            listQuery.setHealed(mHealed.getSelectedItemPosition());
            activity.reloadCursor(listQuery);
            dismiss();
        } else if (id == R.id.cancel) {
            dismiss();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(activity, PatientProvider.TREATMENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.setItems(data);
        mAdapter.addHeader(new TreatmentAdapter.KeyValueItem(0L,"Все"));
        mTreatment.setSelection(listQuery.treatment.isEmpty() ? 0
                : mAdapter.getPositionById(Long.parseLong(listQuery.treatment.split(" = ")[1])));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.setItems(null);
    }
}
