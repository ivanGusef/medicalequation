package com.medicalequation.me.gui;

import android.app.Activity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.medicalequation.me.R;
import com.medicalequation.me.TherapyManager;
import com.medicalequation.me.model.therapy.Line;
import com.medicalequation.me.model.therapy.LineType;
import com.medicalequation.me.model.therapy.Therapy;
import com.medicalequation.me.model.therapy.TherapyType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 8/18/13
 * Time: 3:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class PatientViewBuilder {

    private Activity activity;

    private Therapy currentTherapy;
    private boolean editMode;

    private LinearLayout mutableContainer;
    private LinearLayout immutableContainer;
    private LinearLayout resultContainer;

    private TextView mutableHeader;
    private TextView immutableHeader;
    private TextView resultHeader;

    private Map<String, TextView> mutableHolder = new HashMap<String, TextView>();
    private Map<String, TextView> immutableHolder = new HashMap<String, TextView>();
    private Map<String, TextView> resultHolder = new HashMap<String, TextView>();

    public PatientViewBuilder(Activity activity, boolean editMode) {
        this(activity, null, editMode);
    }

    public PatientViewBuilder(Activity activity, TherapyType therapyType, boolean editMode) {
        this.activity = activity;
        this.editMode = editMode;
        setTherapy(therapyType);

        mutableContainer = (LinearLayout) activity.findViewById(R.id.mutable_char_container);
        immutableContainer = (LinearLayout) activity.findViewById(R.id.immutable_char_container);
        resultContainer = (LinearLayout) activity.findViewById(R.id.result_container);

        mutableHeader = (TextView) activity.findViewById(R.id.mutable_char_container_header);
        immutableHeader = (TextView) activity.findViewById(R.id.immutable_char_container_header);
        resultHeader = (TextView) activity.findViewById(R.id.result_container_header);
    }

    public void setTherapy(TherapyType therapyType) {
        if (therapyType == null)
            return;
        currentTherapy = TherapyManager.getInstance(activity).loadTherapy(therapyType);
    }

    public void generate() {
        if (currentTherapy == null) {
            throw new NullPointerException("currentTherapy must be set before call generate()");
        }
        clear();

        for (Line mutableLine : currentTherapy.mutableLines) {
            View mutableLineView = activity.getLayoutInflater().inflate(editMode ? R.layout.char_edit_line : R.layout.char_view_line, null);
            ((TextView) mutableLineView.findViewById(R.id.label)).setText(mutableLine.label);
            mutableHolder.put(mutableLine.name, configureView(mutableLineView, mutableLine));
            mutableContainer.addView(mutableLineView);
        }

        for (Line immutableLine : currentTherapy.immutableLines) {
            View immutableLineView = activity.getLayoutInflater().inflate(editMode ? R.layout.char_edit_line : R.layout.char_view_line, null);
            ((TextView) immutableLineView.findViewById(R.id.label)).setText(immutableLine.label);
            immutableHolder.put(immutableLine.name, configureView(immutableLineView, immutableLine));
            immutableContainer.addView(immutableLineView);
        }

        for (Line resultLine : currentTherapy.resultLines) {
            View resultLineView = activity.getLayoutInflater().inflate(editMode ? R.layout.char_edit_line : R.layout.char_view_line, null);
            ((TextView) resultLineView.findViewById(R.id.label)).setText(resultLine.label);
            resultHolder.put(resultLine.name, configureView(resultLineView, resultLine));
            resultContainer.addView(resultLineView);
        }

        hideEmptyHeaders();
    }

    public void setMutableValues(Map<String, Number> mutableValues) {
        Number value;
        for (String fieldName : mutableHolder.keySet()) {
            value = mutableValues.get(fieldName);
            mutableHolder.get(fieldName).setText(value != null ? String.valueOf(value) : null);
        }
    }

    public void setImmutableValues(Map<String, Number> immutableValues) {
        Number value;
        for (String fieldName : immutableHolder.keySet()) {
            value = immutableValues.get(fieldName);
            immutableHolder.get(fieldName).setText(value != null ? String.valueOf(value) : null);
        }
    }

    public void setResultValues(Map<String, Number> resultValues) {
        Number value;
        for (String fieldName : resultHolder.keySet()) {
            value = resultValues.get(fieldName);
            resultHolder.get(fieldName).setText(value != null ? String.valueOf(value) : null);
        }
    }

    public Map<String, Number> getMutableValues() {
        Map<String, Number> mutableValues = new HashMap<String, Number>();
        TextView editor;
        Number value;
        String strValue;
        for (Line mutableLine : currentTherapy.mutableLines) {
            editor = mutableHolder.get(mutableLine.name);
            strValue = editor.getText().toString();
            if (!TextUtils.isGraphic(strValue)) {
                value = null;
            } else {
                if (mutableLine.type.equals(LineType.INT))
                    value = Integer.valueOf(strValue);
                else
                    value = Double.valueOf(strValue);
            }
            mutableValues.put(mutableLine.name, value);
        }
        return mutableValues;
    }

    public Map<String, Number> getImmutableValues() {
        Map<String, Number> immutableValues = new HashMap<String, Number>();
        TextView editor;
        Number value;
        String strValue;
        for (Line immutableLine : currentTherapy.immutableLines) {
            editor = immutableHolder.get(immutableLine.name);
            strValue = editor.getText().toString();
            if (!TextUtils.isGraphic(strValue)) {
                value = null;
            } else {
                if (immutableLine.type.equals(LineType.INT))
                    value = Integer.valueOf(strValue);
                else
                    value = Double.valueOf(strValue);
            }
            immutableValues.put(immutableLine.name, value);
        }
        return immutableValues;
    }

    public Map<String, Number> getResultValues() {
        Map<String, Number> resultValues = new HashMap<String, Number>();
        TextView editor;
        Number value;
        String strValue;
        for (Line resultLine : currentTherapy.resultLines) {
            editor = resultHolder.get(resultLine.name);
            strValue = editor.getText().toString();
            if (!TextUtils.isGraphic(strValue)) {
                value = null;
            } else {
                if (resultLine.type.equals(LineType.INT))
                    value = Integer.valueOf(strValue);
                else
                    value = Double.valueOf(strValue);
            }
            resultValues.put(resultLine.name, value);
        }
        return resultValues;
    }

    public boolean validateAll(Map<String, Number> mutableValues, Map<String, Number> immutableValues, Map<String, Number> resultValues) {
        boolean valid = true;
        TextView editor;
        Number value;
        String validateErrorMessage;
        for (Line mutableLine : currentTherapy.mutableLines) {
            editor = mutableHolder.get(mutableLine.name);
            value = mutableValues.get(mutableLine.name);
            if ((validateErrorMessage = mutableLine.validate(activity, value)) != null) {
                editor.setError(validateErrorMessage);
                valid = false;
            }
        }

        for (Line immutableLine : currentTherapy.immutableLines) {
            editor = immutableHolder.get(immutableLine.name);
            value = immutableValues.get(immutableLine.name);
            if ((validateErrorMessage = immutableLine.validate(activity, value)) != null) {
                editor.setError(validateErrorMessage);
                valid = false;
            }
        }

        for (Line resultLine : currentTherapy.resultLines) {
            editor = resultHolder.get(resultLine.name);
            value = resultValues.get(resultLine.name);
            if ((validateErrorMessage = resultLine.validate(activity, value)) != null) {
                editor.setError(validateErrorMessage);
                valid = false;
            }
        }
        return valid;
    }

    private TextView configureView(View parent, Line line) {
        TextView lineView = (TextView) parent.findViewById(R.id.value);
        if (editMode) {
            lineView.setInputType(line.type.equals(LineType.INT) ? InputType.TYPE_CLASS_NUMBER
                    : InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }
        return lineView;
    }

    private void clear() {
        mutableContainer.removeAllViewsInLayout();
        immutableContainer.removeAllViewsInLayout();
        resultContainer.removeAllViewsInLayout();
    }

    private void hideEmptyHeaders() {
        mutableHeader.setVisibility(mutableContainer.getChildCount() <= 0 ? View.GONE : View.VISIBLE);
        immutableHeader.setVisibility(immutableContainer.getChildCount() <= 0 ? View.GONE : View.VISIBLE);
        resultHeader.setVisibility(resultContainer.getChildCount() <= 0 ? View.GONE : View.VISIBLE);
    }

    public Map<String, TextView> getMutableHolder() {
        return mutableHolder;
    }

    public Map<String, TextView> getImmutableHolder() {
        return immutableHolder;
    }

    public Map<String, TextView> getResultHolder() {
        return resultHolder;
    }
}
