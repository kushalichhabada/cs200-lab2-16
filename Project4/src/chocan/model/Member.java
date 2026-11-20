package chocan.model;

/**
 * A ChocAn member (a chocolate addict receiving treatment). A member can only
 * be billed for services while the status is {@link MemberStatus#VALID}.
 *
 * @author [TEAM MEMBER NAME]
 */
public class Member extends Person {

    private static final long serialVersionUID = 1L;

    private final MemberStatus status;

    public Member(String number, PersonDetails details, MemberStatus status) {
        super(number, details);
        if (status == null) {
            throw new IllegalArgumentException("Member status is required.");
        }
        this.status = status;
    }

    public MemberStatus getStatus() {
        return status;
    }

    public boolean isValid() {
        return status == MemberStatus.VALID;
    }

    @Override
    public String getTypeLabel() {
        return "Member";
    }
}
