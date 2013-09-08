package com.medicalequation.me.gui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 9/8/13
 * Time: 10:07 PM
 * May the force be with you always.
 */
public class SelectRunListener implements View.OnClickListener {

    private final Context context;
    private final String[] items;

    public SelectRunListener(Context context, String[] items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public void onClick(final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ((TextView) view).setText(items[i]);
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }
}
