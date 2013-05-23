package com.medicalequation.me.gui.activity;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.medicalequation.me.C;
import com.medicalequation.me.R;
import com.medicalequation.me.db.PatientProvider;
import com.medicalequation.me.db.PatientTable;
import com.medicalequation.me.db.TreatmentTable;
import com.medicalequation.me.gui.ListQuery;
import com.medicalequation.me.gui.dialog.TherapyFilterDialog;

/**
 * Created with IntelliJ IDEA.
 * User: Иван Гусев
 * Date: 07.05.13
 * Time: 1:20
 * May the Force be with you, always
 */
public class PatientListActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ListQuery listQuery = new ListQuery();
    private PatientsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_patient_list);
        fillData();
    }

    private void fillData() {
        getLoaderManager().initLoader(0, null, this);
        adapter = new PatientsAdapter();
        setListAdapter(adapter);
        registerForContextMenu(getListView());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.m_list_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (id == R.id.menu_view) {
            Intent intent = new Intent(this, PatientDetailsActivity.class);
            intent.putExtra(C.Extra.ID, acmi.id);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_delete) {
            getContentResolver().delete(Uri.parse(PatientProvider.CONTENT_URI + "/" + acmi.id), null, null);
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.m_patient_list, menu);
        MenuItem menuItem = menu.getItem(0);
        ((SearchView) menuItem.getActionView()).setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listQuery.setFio(newText);
                getLoaderManager().restartLoader(0, null, PatientListActivity.this);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.filter) {
            new TherapyFilterDialog(this, listQuery).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {PatientTable.CN_ID, PatientTable.CN_FIO, PatientTable.СТ_TREATMENT_ID, PatientTable.CN_HEALED};
        return new CursorLoader(this, PatientProvider.CONTENT_URI, projection, listQuery.toString(), null, PatientTable.CN_FIO + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // data is not available anymore, delete reference
        adapter.swapCursor(null);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(this, PatientDetailsActivity.class);
        intent.putExtra(C.Extra.ID, id);
        startActivity(intent);
    }

    public void reloadCursor(ListQuery listQuery) {
        this.listQuery = listQuery;
        getLoaderManager().restartLoader(0, null, PatientListActivity.this);
    }

    private class PatientsAdapter extends CursorAdapter {

        private final int colorRed;
        private final int colorGreen;
        private final String healed;
        private final String notHealed;
        private Cursor treatmentCursor;

        public PatientsAdapter() {
            super(PatientListActivity.this, null, true);
            colorRed = getResources().getColor(R.color.red_alpha_30);
            colorGreen = getResources().getColor(R.color.green);
            healed = getString(R.string.healed);
            notHealed = getString(R.string.not_healed);
            treatmentCursor = getContentResolver().query(PatientProvider.TREATMENT_URI, null, null, null, null);
        }

        @Override
        public long getItemId(int position) {
            Cursor c = getCursor();
            if (c != null) {
                c.moveToPosition(position);
                return c.getLong(0);
            }
            return 0;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();
            View view = getLayoutInflater().inflate(R.layout.i_patient, null);
            holder.fio = (TextView) view.findViewById(R.id.patient_fio);
            holder.group = (TextView) view.findViewById(R.id.patient_group);
            holder.healed = (TextView) view.findViewById(R.id.patient_healed);
            view.setTag(holder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.fio.setText(cursor.getString(1));
            SetTreatmentTask task;
            if ((task = (SetTreatmentTask) holder.group.getTag()) != null) {
                task.cancel(true);
            }
            task = new SetTreatmentTask(holder.group);
            holder.group.setTag(task);
            task.execute(cursor.getLong(2));
            holder.healed.setTextColor(cursor.getLong(3) == 1 ? colorGreen : colorRed);
            holder.healed.setText(cursor.getInt(3) == 1 ? healed : notHealed);
        }

        @Override
        protected void finalize() throws Throwable {
            if (treatmentCursor != null) treatmentCursor.close();
            super.finalize();
        }

        class ViewHolder {
            TextView fio, group, healed;
        }
    }

    private class SetTreatmentTask extends AsyncTask<Long, Void, String> {

        private TextView mTextView;

        private SetTreatmentTask(TextView mTextView) {
            this.mTextView = mTextView;
        }

        @Override
        protected void onPreExecute() {
            mTextView.setText(R.string.loading);
        }

        @Override
        protected String doInBackground(Long... params) {
            String result = "";
            Cursor c = getContentResolver().query(Uri.parse(PatientProvider.TREATMENT_URI + "/" + params[0]), null, null, null, null);
            if (c != null) {
                if (c.moveToFirst()) {
                    result = c.getString(c.getColumnIndex(TreatmentTable.CN_NAME));
                }
                c.close();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            mTextView.setText(s);
        }
    }
}
