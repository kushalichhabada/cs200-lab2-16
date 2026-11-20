package chocan.model;

import chocan.util.Validation;
import java.io.Serializable;

/**
 * Immutable, validated name/address block shared by members and providers.
 * Keeping it in one class means the add and edit screens for both kinds of
 * person can use the same code.
 *
 * @author [TEAM MEMBER NAME]
 */
public final class PersonDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String name;
    private final String streetAddress;
    private final String city;
    private final String state;
    private final String zip;

    /** @throws IllegalArgumentException if any field breaks the length/format rules */
    public PersonDetails(String name, String streetAddress, String city, String state, String zip) {
        this.name = Validation.requireText("Name", name, Validation.NAME_MAX);
        this.streetAddress = Validation.requireText("Street address", streetAddress, Validation.ADDRESS_MAX);
        this.city = Validation.requireText("City", city, Validation.CITY_MAX);
        this.state = Validation.requireState(state);
        this.zip = Validation.requireDigits("Zip code", zip, 5);
    }

    public String getName() {
        return name;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZip() {
        return zip;
    }

    public String getCityStateZip() {
        return city + ", " + state + " " + zip;
    }
}
