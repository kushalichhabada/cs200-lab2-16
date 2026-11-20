package chocan.model;

/**
 * A health-care provider (dietitian, counselor, ...) who treats members and
 * bills ChocAn for each consultation. The provider number is also the login ID.
 *
 * @author [TEAM MEMBER NAME]
 */
public class Provider extends Person {

    private static final long serialVersionUID = 1L;

    public Provider(String number, PersonDetails details) {
        super(number, details);
    }

    @Override
    public String getTypeLabel() {
        return "Provider";
    }
}
