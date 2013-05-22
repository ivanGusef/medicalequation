package com.medicalequation.me.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created with IntelliJ IDEA.
 * User: Иван Гусев
 * Date: 07.05.13
 * Time: 3:21
 * May the Force be with you, always
 */
public class IOUtils {

    public static final String SEPARATOR = "\n";

    public static String readResourceAsString(Context context, int resourceId) {
        InputStream inputStream = context.getResources().openRawResource(resourceId);
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String str;
        try {
            while ((str = br.readLine()) != null) {
                sb.append(str).append(SEPARATOR);
            }
        } catch (IOException e) {
            LogTool.e("error loading resource by id = " + resourceId, e);
            throw new RuntimeException(e);
        }
        return sb.toString();
    }
}
