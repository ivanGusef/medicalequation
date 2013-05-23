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
    public String treatment = "";
    public String healed = "";

    public void setFio(String query) {
        if (query.isEmpty()) {
            fio = "";
        } else {
            fio = PatientTable.CN_FIO + " LIKE '%" + query + "%'";
        }
    }

    public void setTreatment(long id) {
        if (id == 0) {
            treatment = "";
        } else {
            treatment = PatientTable.СТ_TREATMENT_ID + " = " + id;
        }
    }

    public void setHealed(int index) {
        if (index == 0) {
            healed = "";
        } else if (index == 1) {
            healed = PatientTable.CN_HEALED + " = 0";
        } else if (index == 2) {
            healed = PatientTable.CN_HEALED + " = 1";
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!fio.isEmpty())
            sb.append(fio);
        if (!treatment.isEmpty())
            sb.append(sb.length() > 0 ? " AND " + treatment : treatment);
        if (!healed.isEmpty())
            sb.append(sb.length() > 0 ? " AND " + healed : healed);
        return sb.toString();
    }
}
