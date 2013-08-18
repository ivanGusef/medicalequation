package com.medicalequation.me;

import android.content.Context;
import com.google.gson.Gson;
import com.medicalequation.me.model.therapy.Therapy;
import com.medicalequation.me.model.therapy.TherapyType;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 8/4/13
 * Time: 11:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class TherapyManager {

    private Context context;
    private Map<TherapyType, SoftReference<Therapy>> cache = new HashMap<TherapyType, SoftReference<Therapy>>();

    private static TherapyManager therapyManager;

    private TherapyManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static TherapyManager getInstance(Context context) {
        if (therapyManager == null) {
            therapyManager = new TherapyManager(context);
        }
        return therapyManager;
    }

    public Therapy loadTherapy(TherapyType therapyType) {
        Therapy therapy;
        SoftReference<Therapy> reference = cache.get(therapyType);
        if (reference == null || (therapy = reference.get()) == null) {
            try {
                therapy = new Gson().fromJson(IOUtils.toString(context.getAssets().open(therapyType.genFileName)), Therapy.class);
                cache.put(therapyType, new SoftReference<Therapy>(therapy));
            } catch (IOException e) {
                throw new IllegalStateException("No structure file for this therapy " + therapyType.label);
            }
        }
        return therapy;
    }
}
