package com.medicalequation.me;

import android.app.Application;
import org.acra.ACRA;
import org.acra.ErrorReporter;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.EmailIntentSender;
import org.acra.sender.GoogleFormSender;

/**
 * Created with IntelliJ IDEA.
 * User: Иван Гусев
 * Date: 11.05.13
 * Time: 15:44
 * May the Force be with you, always
 */
@ReportsCrashes(formKey = "dDFEbmdiMG9sYVM2M01OTUhHWE1OUFE6TG", mailTo = "ivan.gusef@gmail.com", mode = ReportingInteractionMode.TOAST, resToastText = R.string.error_caught)
public class MedEqApplication extends Application {

    @Override
    public void onCreate() {
        ACRA.init(this);
        super.onCreate();
    }
}
