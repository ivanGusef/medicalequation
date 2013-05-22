package com.medicalequation.me;

import com.medicalequation.me.utils.PreferenceManager;

/**
 * Created with IntelliJ IDEA.
 * User: Иван Гусев
 * Date: 07.05.13
 * Time: 0:19
 * May the Force be with you, always
 */
public class C {
    public static class App {
        public static final String NAME = "Medical Equation";
        public static final String SHORT_NAME = "MedEQ";
    }

    public static class Extra {
        public static final String ID = "extra_id";
        public static final String RESULTS = "extra_data";
        public static final String TREATMENT_INDEX = "extra_treatment_index";
    }

    public static class What {
        public static final int RESULT = 1;
    }

    public static class PreferenceKey {
        public static final String HIFU = "hifu_" + PreferenceManager.PLACE_HOLDER_RESULT + "_" + PreferenceManager.PLACE_HOLDER_INDEX;
        public static final String HIFU_TUR = "hifu_tur_" + PreferenceManager.PLACE_HOLDER_RESULT + "_" + PreferenceManager.PLACE_HOLDER_INDEX;

        public static final String[] RESULTS = {"urine_incontinence", "acute_urinary_retention", "disease_progression", "stricture"};

        public static final String HIFU_LAST_PATIENTS_NUM = "hifu_last_patients_num";
        public static final String HIFU_TUR_LAST_PATIENTS_NUM = "hifu_tur_last_patients_num";
    }
}
