package com.medicalequation.me.model.therapy;

import com.medicalequation.me.R;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 8/4/13
 * Time: 11:09 PM
 * To change this template use File | Settings | File Templates.
 */
public enum LineType {
    INT(R.layout.char_view_line, R.layout.char_edit_line),
    REAL(R.layout.char_view_line, R.layout.char_edit_line),
    SELECT(R.layout.char_view_line, R.layout.char_edit_selectable_line);

    public int viewRes;
    public int editRes;

    private LineType(int viewRes, int editRes) {
        this.viewRes = viewRes;
        this.editRes = editRes;
    }
}
