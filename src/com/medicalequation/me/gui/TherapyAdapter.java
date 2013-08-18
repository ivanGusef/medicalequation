package com.medicalequation.me.gui;

import android.R;
import android.content.Context;
import android.widget.ArrayAdapter;
import com.medicalequation.me.model.therapy.TherapyType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 8/4/13
 * Time: 9:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class TherapyAdapter extends ArrayAdapter<String> {

    public static final String HEADER_LABEL = "Все";

    public TherapyAdapter(Context mContext) {
        this(mContext, false);
    }

    public TherapyAdapter(Context mContext, boolean addHeader) {
        super(mContext, R.layout.simple_spinner_item);
        setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        List<String> elements = new ArrayList<String>();
        if (addHeader) {
            elements.add(HEADER_LABEL);
        }
        for (TherapyType therapy : TherapyType.values()) {
            elements.add(therapy.label);
        }
        addAll(elements);
    }
}
