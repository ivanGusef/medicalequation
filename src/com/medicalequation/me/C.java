package com.medicalequation.me;

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
        public static final String THERAPY = PlaceHolder.THERAPY + "_" + PlaceHolder.RESULT + "_" + PlaceHolder.INDEX;
        public static final String THERAPY_LAST_PATIENTS_NUM = PlaceHolder.THERAPY + "_last_patients_num";

        public static final String[] RESULTS = {"urine_incontinence", "acute_urinary_retention", "disease_progression", "stricture"};
    }

    public static class PlaceHolder {
        public static final String INDEX = "%index%";
        public static final String RESULT = "%result%";
        public static final String THERAPY = "%therapy%";
    }
}