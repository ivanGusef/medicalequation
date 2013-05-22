package com.medicalequation.me.gui;

import com.medicalequation.me.db.PatientTable;

/**
 * Created with IntelliJ IDEA.
 * User: Иван Гусев
 * Date: 07.05.13
 * Time: 9:38
 * May the Force be with you, always
 */
public class ListQuery {
    public String fio = "";
    public String therapy = "";
    public String treatment = "";

    public void setFio(String query) {
        if (query.isEmpty()) {
            fio = "";
        } else {
            fio = PatientTable.CN_FIO + " LIKE '%" + query + "%'";
        }
    }

    public void setTherapy(int index) {
        if (index == 0) {
            therapy = "";
        } else {
            therapy = PatientTable.СТ_RECOMMENDED_THERAPY + " = " + (--index);
        }
    }

    public void setTreatment(int index) {
        if (index == 0) {
            treatment = "";
        } else if (index == 1) {
            treatment = PatientTable.CN_HEALED + " = 0";
        } else if (index == 2) {
            treatment = PatientTable.CN_HEALED + " = 1";
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!fio.isEmpty())
            sb.append(fio);
        if (!therapy.isEmpty())
            sb.append(sb.length() > 0 ? " AND " + therapy : therapy);
        if (!treatment.isEmpty())
            sb.append(sb.length() > 0 ? " AND " + treatment : treatment);
        return sb.toString();
    }
}
