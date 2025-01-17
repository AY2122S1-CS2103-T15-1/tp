package gomedic.model.person.doctor;

import gomedic.commons.util.AppUtil;
import gomedic.model.commonfield.Id;

/**
 * Represents a general id owned by a doctor.
 * Guarantees: immutable; is valid as declared in {@link #isValidDoctorId(Id)})}
 */
public class DoctorId extends Id {
    public static final String MESSAGE_CONSTRAINTS =
            "Id should only contain from 1 to 999, Prefix should be D";
    private static final Character DOCTOR_PREFIX = 'D';

    /**
     * {@inheritDoc}
     *
     * @param id Integer from 1 to 999
     */
    public DoctorId(Integer id) {
        super(id, DOCTOR_PREFIX);
        AppUtil.checkArgument(isValidDoctorId(this), MESSAGE_CONSTRAINTS);
    }

    /**
     * {@inheritDoc}
     *
     * @param id a string of format "PDDD", where "P" is an alphabetic character and "D" is a decimal number
     */
    public DoctorId(String id) {
        super(Integer.parseInt(id.substring(1)), DOCTOR_PREFIX);
        AppUtil.checkArgument(isValidDoctorId(this), MESSAGE_CONSTRAINTS);
    }

    /**
     * Returns true if a given stringId is a valid doctor id.
     * Valid if integer is 3 digit, from 1 to 999, prefix is D.
     *
     * @param id Id.
     * @return true if valid.
     */
    public static boolean isValidDoctorId(Id id) {
        int number = Integer.parseInt(id.toString().substring(1));
        Character prefix = id.toString().charAt(0);
        boolean isValidPrefix = prefix.equals(DOCTOR_PREFIX);

        return isValidId(number, prefix) && isValidPrefix;
    }

    /**
     * Returns true if a given stringId is a valid activity valid id.
     * Valid if integer is 3 digit, from 1 to 999, prefix is D.
     *
     * @param did String.
     * @return true if valid, else false.
     */
    public static boolean isValidDoctorId(String did) {
        if (!isValidIdFormat(did)) {
            return false;
        }

        int number = Integer.parseInt(did.substring(1));
        Character prefix = did.charAt(0);
        boolean isValidPrefix = prefix.equals(DOCTOR_PREFIX);

        return isValidId(number, prefix) && isValidPrefix;
    }
}
