package chocan.model;

/**
 * The three kinds of people who can log in to the ChocAn system.
 * The role decides which menu is shown after login.
 *
 * @author [TEAM MEMBER NAME]
 */
public enum Role {
    OPERATOR("Operator"),
    PROVIDER("Provider"),
    MANAGER("Manager");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
