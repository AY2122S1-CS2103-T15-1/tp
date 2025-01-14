package gomedic.logic.parser;

/**
 * Contains Command Line Interface (CLI) syntax definitions common to multiple commands
 */
public class CliSyntax {
    /* Prefixes for entry types */
    public static final String PREFIX_TYPE_AS_STRING = "t/";
    public static final String PREFIX_TYPE_DOCTOR = PREFIX_TYPE_AS_STRING + "doctor";
    public static final String PREFIX_TYPE_ACTIVITY = PREFIX_TYPE_AS_STRING + "activity";
    public static final String PREFIX_TYPE_PATIENT = PREFIX_TYPE_AS_STRING + "patient";
    public static final String PREFIX_TYPE_ALL = PREFIX_TYPE_AS_STRING + "all";

    public static final String PREFIX_TYPE_APPOINTMENT = PREFIX_TYPE_AS_STRING + "appointment";

    /* General prefixes */
    public static final Prefix PREFIX_NAME = new Prefix("n/");
    public static final Prefix PREFIX_PHONE = new Prefix("p/");
    public static final Prefix PREFIX_TAG = new Prefix("ta/");
    public static final Prefix PREFIX_ID = new Prefix("i/");

    /* Prefix definitions for user profile */
    public static final Prefix PREFIX_POSITION = new Prefix("p/");
    public static final Prefix PREFIX_ORGANIZATION = new Prefix("o/");


    /* Prefix definitions for a doctor */
    public static final Prefix PREFIX_DEPARTMENT = new Prefix("de/");

    /* Prefix definitions for a patient */
    public static final Prefix PREFIX_AGE = new Prefix("a/");
    public static final Prefix PREFIX_BLOOD_TYPE = new Prefix("b/");
    public static final Prefix PREFIX_GENDER = new Prefix("g/");
    public static final Prefix PREFIX_HEIGHT = new Prefix("h/");
    public static final Prefix PREFIX_WEIGHT = new Prefix("w/");
    public static final Prefix PREFIX_MEDICAL_CONDITIONS = new Prefix("m/");

    /* Prefix definitions for an activity */
    public static final Prefix PREFIX_TITLE = new Prefix("ti/");
    public static final Prefix PREFIX_DESCRIPTION = new Prefix("d/");
    public static final Prefix PREFIX_START_TIME = new Prefix("s/");
    public static final Prefix PREFIX_END_TIME = new Prefix("e/");

    /* Categories */
    public static final String NAME = "n";
    public static final String PHONE_NUMBER = "p";
    public static final String AGE = "a";
    public static final String GENDER = "g";
    public static final String HEIGHT = "h";
    public static final String WEIGHT = "w";
    public static final String BLOOD_TYPE = "b";
    public static final String MEDICAL_CONDITION = "m";
    public static final String DEPARTMENT = "de";
    public static final String ACTIVITY_TITLE = "ti";

    /* Prefix definitions for listing */
    public static final Prefix PREFIX_SORT_FLAG = new Prefix("s/");
    public static final Prefix PREFIX_PERIOD_FLAG = new Prefix("p/");

    /* Specific id prefixes for Referral*/
    public static final Prefix PREFIX_DOCTOR_ID = new Prefix("di/");
    public static final Prefix PREFIX_PATIENT_ID = new Prefix("pi/");
}
