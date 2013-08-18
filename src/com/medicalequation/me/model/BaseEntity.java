package com.medicalequation.me.model;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 8/4/13
 * Time: 9:52 PM
 * To change this template use File | Settings | File Templates.
 */
public interface BaseEntity {
    ContentValues convert();
    void parse(Cursor cursor);
}
