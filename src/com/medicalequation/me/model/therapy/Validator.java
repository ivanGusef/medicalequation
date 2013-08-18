package com.medicalequation.me.model.therapy;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 8/4/13
 * Time: 11:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class Validator {

    public Number min;
    public Number max;

    public Validator(Number min, Number max) {
        this.min = min;
        this.max = max;
    }
}
