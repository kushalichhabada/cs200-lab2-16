package chocan.model;

/**
 * Whether a member may currently receive (and be billed for) services.
 * A member whose dues are unpaid is SUSPENDED until reinstated by an operator.
 *
 * @author [TEAM MEMBER NAME]
 */
public enum MemberStatus {
    VALID("Valid"),
    SUSPENDED("Suspended");

    private final String label;

    MemberStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
