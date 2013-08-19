package com.medicalequation.me.model.therapy;

import android.content.Context;
import com.medicalequation.me.R;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 8/4/13
 * Time: 11:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class Line {

    public String name;
    public String label;
    public LineType type;
    public Validator validator;

    public String validate(Context context, Number number) {
        return validate(context, number, false);
    }

    public String validate(Context context, Number number, boolean nullable) {
        if (!nullable && number == null) {
            return context.getString(R.string.e_illegalInput);
        }
        if (validator == null)
            return null;
        if (number != null && (number.doubleValue() < validator.min.doubleValue()
                || number.doubleValue() > validator.max.doubleValue())) {
            return context.getString(R.string.e_outOfBounds, label, validator.min, validator.max);
        }
        return null;
    }
}
