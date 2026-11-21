package chocan.ui;

/**
 * Thrown when the input stream closes (Ctrl+Z / Ctrl+D, or piped input that
 * has run out) so the program can exit cleanly instead of crashing.
 *
 * @author [TEAM MEMBER NAME]
 */
public class InputEndedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InputEndedException() {
        super("Input ended.");
    }
}
