package chocan.model;

import chocan.util.Validation;
import java.io.Serializable;

/**
 * Common base for {@link Member} and {@link Provider}: a 9-digit number plus a
 * name and address. Instances are immutable; an "edit" replaces the stored
 * object with a new one that has the same number.
 *
 * @author [TEAM MEMBER NAME]
 */
public abstract class Person implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String number;
    private final PersonDetails details;

    protected Person(String number, PersonDetails details) {
        this.number = Validation.requireDigits("Number", number, Validation.NUMBER_DIGITS);
        if (details == null) {
            throw new IllegalArgumentException("Name and address details are required.");
        }
        this.details = details;
    }

    /** Human-readable kind of person, e.g. "Member" or "Provider". */
    public abstract String getTypeLabel();

    public String getNumber() {
        return number;
    }

    public PersonDetails getDetails() {
        return details;
    }

    public String getName() {
        return details.getName();
    }

    public String getStreetAddress() {
        return details.getStreetAddress();
    }

    public String getCity() {
        return details.getCity();
    }

    public String getState() {
        return details.getState();
    }

    public String getZip() {
        return details.getZip();
    }

    public String getCityStateZip() {
        return details.getCityStateZip();
    }
}
