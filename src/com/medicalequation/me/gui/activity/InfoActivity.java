package com.medicalequation.me.gui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import com.medicalequation.me.R;

/**
 * Created with IntelliJ IDEA.
 * User: Иван Гусев
 * Date: 28.05.13
 * Time: 0:57
 * May the Force be with you, always
 */
public class InfoActivity extends Activity {

    public static final String CONTENT = "content";

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getIntent().getIntExtra(CONTENT, R.layout.a_manual));
    }
}
