package chocan.service;

/**
 * Checked exception for expected business-rule failures (unknown member
 * number, suspended member, bad login, ...). The menus catch it and show the
 * message to the user instead of crashing.
 *
 * @author [TEAM MEMBER NAME]
 */
public class ChocAnException extends Exception {

    private static final long serialVersionUID = 1L;

    public ChocAnException(String message) {
        super(message);
    }
}
