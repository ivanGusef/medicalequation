package com.medicalequation.me.gui;

import android.R;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Иван Гусев
 * Date: 24.05.13
 * Time: 1:05
 * May the Force be with you, always
 */
public class TreatmentAdapter extends ArrayAdapter<TreatmentAdapter.KeyValueItem> {

    private List<KeyValueItem> mKeyValueItems;

    public TreatmentAdapter(Context mContext) {
        this(mContext, null);
    }

    public TreatmentAdapter(Context mContext, List<KeyValueItem> mKeyValueItems) {
        super(mContext, R.layout.simple_spinner_item);
        if (mKeyValueItems == null) {
            mKeyValueItems = new ArrayList<KeyValueItem>();
        }
        this.mKeyValueItems = mKeyValueItems;
        setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
    }

    public void addHeader(KeyValueItem keyValueItem) {
        mKeyValueItems.add(0, keyValueItem);
        notifyDataSetChanged();
    }

    public void setItems(Cursor cursor) {
        mKeyValueItems = new ArrayList<KeyValueItem>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    mKeyValueItems.add(new KeyValueItem().fromCursor(cursor));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        notifyDataSetChanged();
    }

    public int getPositionById(long id) {
        for (int i = 0; i < mKeyValueItems.size(); i++) {
            if (mKeyValueItems.get(i).key == id) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public int getCount() {
        return mKeyValueItems.size();
    }

    @Override
    public KeyValueItem getItem(int position) {
        return mKeyValueItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mKeyValueItems.get(position).key;
    }

    public static class KeyValueItem {
        public long key;
        public String value;

        public KeyValueItem() {
        }

        public KeyValueItem(long key, String value) {
            this.key = key;
            this.value = value;
        }

        public KeyValueItem fromCursor(Cursor cursor) {
            key = cursor.getLong(0);
            value = cursor.getString(1);
            return this;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
