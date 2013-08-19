package com.medicalequation.me.model.calc;

import com.medicalequation.me.model.therapy.Therapy;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: macbookpro
 * Date: 07.08.13
 * Time: 14:37
 * To change this template use File | Settings | File Templates.
 */
public class CalcUnit implements Comparable<CalcUnit> {

    public Therapy therapy;
    public Map<String, Number> results = new HashMap<String, Number>();

    @Override
    public int compareTo(CalcUnit another) {
        float sumThis = 0, sumAnother = 0;
        for (String param : results.keySet()) {
            sumThis += Math.abs(results.get(param).floatValue()-1);
        }
        for (String param : another.results.keySet()) {
            sumAnother += Math.abs(another.results.get(param).floatValue()-1);
        }
        return sumThis == sumAnother ? 0 : sumThis > sumAnother ? 1 : -1;
    }
}