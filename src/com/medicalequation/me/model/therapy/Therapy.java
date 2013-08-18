package com.medicalequation.me.model.therapy;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 8/4/13
 * Time: 11:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class Therapy {

    public TherapyType type;
    public List<Line> mutableLines;
    public List<Line> immutableLines;
    public List<Line> resultLines;
}
