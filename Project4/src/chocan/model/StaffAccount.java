package chocan.model;

import chocan.util.Validation;
import java.io.Serializable;

/**
 * Login account for ChocAn employees (operators and managers). Providers do
 * not need one because they log in with their provider number.
 *
 * @author [TEAM MEMBER NAME]
 */
public class StaffAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private final String name;
    private final Role role;

    public StaffAccount(String id, String name, Role role) {
        this.id = Validation.requireDigits("Staff ID", id, Validation.NUMBER_DIGITS);
        this.name = Validation.requireText("Name", name, Validation.NAME_MAX);
        if (role == null || role == Role.PROVIDER) {
            throw new IllegalArgumentException("Staff accounts must be an operator or a manager.");
        }
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }
}
