package chocan.util;

import java.math.BigDecimal;

/**
 * Central place for the field-length and format rules taken from the ChocAn
 * requirements (name 25 chars, address 25, city 14, state 2, zip 5, ...).
 * Every method either returns the cleaned-up value or throws an
 * {@link IllegalArgumentException} whose message is safe to show to the user.
 *
 * @author [TEAM MEMBER NAME]
 */
public final class Validation {

    public static final int NAME_MAX = 25;
    public static final int ADDRESS_MAX = 25;
    public static final int CITY_MAX = 14;
    public static final int SERVICE_NAME_MAX = 20;
    public static final int COMMENT_MAX = 100;
    public static final int NUMBER_DIGITS = 9;
    public static final int SERVICE_CODE_DIGITS = 6;
    public static final BigDecimal MAX_FEE = new BigDecimal("999.99");

    private Validation() {
        // utility class
    }

    /** A required, non-blank text value no longer than {@code maxLength}. */
    public static String requireText(String field, String value, int maxLength) {
        String text = value == null ? "" : value.trim();
        if (text.isEmpty()) {
            throw new IllegalArgumentException(field + " is required.");
        }
        if (text.length() > maxLength) {
            throw new IllegalArgumentException(field + " must be at most " + maxLength + " characters.");
        }
        return text;
    }

    /** Like {@link #requireText} but a blank value is allowed (returned as ""). */
    public static String optionalText(String field, String value, int maxLength) {
        String text = value == null ? "" : value.trim();
        if (text.length() > maxLength) {
            throw new IllegalArgumentException(field + " must be at most " + maxLength + " characters.");
        }
        return text;
    }

    /** A value made of exactly {@code length} digits (member, provider, service numbers, zip). */
    public static String requireDigits(String field, String value, int length) {
        String text = value == null ? "" : value.trim();
        if (!text.matches("\\d{" + length + "}")) {
            throw new IllegalArgumentException(field + " must be exactly " + length + " digits.");
        }
        return text;
    }

    /** A two-letter state abbreviation, returned in upper case. */
    public static String requireState(String value) {
        String text = value == null ? "" : value.trim();
        if (!text.matches("[A-Za-z]{2}")) {
            throw new IllegalArgumentException("State must be a 2-letter abbreviation (e.g. AL).");
        }
        return text.toUpperCase();
    }

    /** A fee between $0.00 and $999.99 with at most two decimal places. */
    public static BigDecimal requireFee(BigDecimal fee) {
        if (fee == null || fee.signum() < 0 || fee.compareTo(MAX_FEE) > 0 || fee.scale() > 2) {
            throw new IllegalArgumentException("Fee must be between $0.00 and $999.99.");
        }
        return fee.setScale(2);
    }
}
