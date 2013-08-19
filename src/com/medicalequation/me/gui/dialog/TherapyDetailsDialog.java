package com.medicalequation.me.gui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.medicalequation.me.R;
import com.medicalequation.me.model.calc.CalcUnit;
import com.medicalequation.me.model.therapy.Line;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 8/20/13
 * Time: 1:29 AM
 * May the force be with you always.
 */
public class TherapyDetailsDialog extends Dialog {

    private CalcUnit calcUnit;

    public TherapyDetailsDialog(Context context, CalcUnit calcUnit) {
        super(context);
        this.calcUnit = calcUnit;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.d_therapy_details);
        ((TextView) findViewById(R.id.therapy_header)).setText(calcUnit.therapy.type.label);
        LinearLayout container = (LinearLayout) findViewById(R.id.result_container);
        View viewLine;
        for (Line line : calcUnit.therapy.resultLines) {
            viewLine = getLayoutInflater().inflate(R.layout.char_view_line, null);
            ((TextView) viewLine.findViewById(R.id.label)).setText(line.label);
            ((TextView) viewLine.findViewById(R.id.value)).setText(String.valueOf(calcUnit.results.get(line.name)));
            container.addView(viewLine);
        }
    }
}
