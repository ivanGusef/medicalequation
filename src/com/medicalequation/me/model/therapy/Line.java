package com.medicalequation.me.model.therapy;

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

    public boolean validate(Number number) {
        if (validator == null)
            return true;
        if (number.doubleValue() < validator.min.doubleValue() || number.doubleValue() > validator.max.doubleValue()) {
            return false;
        }
        return true;
    }
}
