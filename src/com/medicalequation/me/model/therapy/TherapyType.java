package com.medicalequation.me.model.therapy;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 8/4/13
 * Time: 8:46 PM
 * To change this template use File | Settings | File Templates.
 */
public enum TherapyType {
    HIFU("HIFU", "hifu.json"),
    HIFU_TUR("HIFU + ТУР", "hifu_tur.json"),
    BRAHI("Брахитерапия", "brahi.json"),
    DLT("ДЛТ", "dlt.json"),
    RPE("РПЭ", "rpe.json");

    public String label;
    public String genFileName;

    private TherapyType(String label, String genFileName) {
        this.label = label;
        this.genFileName = genFileName;
    }

    public static TherapyType getByName(String name) {
        for (TherapyType therapy : values()) {
            if (therapy.name().equals(name)) {
                return therapy;
            }
        }
        return null;
    }

    public static TherapyType getByLabel(String label) {
        for (TherapyType therapy : values()) {
            if (therapy.label.equals(label)) {
                return therapy;
            }
        }
        return null;
    }
}
