package com.medicalequation.me.gui;

import com.medicalequation.me.db.PatientTable;
import com.medicalequation.me.model.therapy.TherapyType;

/**
 * Created with IntelliJ IDEA.
 * User: Иван Гусев
 * Date: 07.05.13
 * Time: 9:38
 * May the Force be with you, always
 */
public class ListQuery {
    public String fio = "";
    public TherapyType therapy;

    public String fioQuery = "";
    public String therapyQuery = "";

    public void setFio(String fio) {
        this.fio = fio;
        if (fio.isEmpty()) {
            fioQuery = "";
        } else {
            fioQuery = PatientTable.CN_FIO + " LIKE '%" + fio + "%'";
        }
    }

    public void setTherapy(TherapyType therapy) {
        this.therapy = therapy;
        if (therapy == null) {
            therapyQuery = "";
        } else {
            therapyQuery = PatientTable.CN_THERAPY + " = '" + therapy + "'";
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!fioQuery.isEmpty())
            sb.append(fioQuery);
        if (!therapyQuery.isEmpty())
            sb.append(sb.length() > 0 ? " AND " + therapyQuery : therapyQuery);
        return sb.toString();
    }
}
