package chocan.service;

/**
 * Result of a provider swiping/typing a member number to validate it.
 * {@link #getMessage()} is the text displayed on the provider terminal.
 *
 * @author [TEAM MEMBER NAME]
 */
public enum MemberCheck {
    VALID("Validated"),
    SUSPENDED("Member suspended"),
    INVALID_NUMBER("Invalid number");

    private final String message;

    MemberCheck(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
