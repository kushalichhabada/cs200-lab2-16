package chocan.ui;

import chocan.util.Formats;
import java.io.InputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.function.Function;

/**
 * All keyboard input and screen output goes through this class so the menus
 * stay short and every prompt behaves the same way (trimming, re-asking after
 * invalid input, yes/no questions, date entry).
 *
 * @author [TEAM MEMBER NAME]
 */
public class ConsoleIO {

    private final Scanner in;
    private final PrintStream out;

    public ConsoleIO(InputStream input, PrintStream output) {
        this.in = new Scanner(input);
        this.out = output;
    }

    public void println(String text) {
        out.println(text);
    }

    public void blank() {
        out.println();
    }

    /** Prints a title between two lines. */
    public void header(String title) {
        out.println();
        out.println(Formats.line('=', 60));
        out.println(" " + title);
        out.println(Formats.line('=', 60));
    }

    /** Shows {@code label: } and returns the trimmed line the user types. */
    public String prompt(String label) {
        out.print(label + ": ");
        out.flush();
        if (!in.hasNextLine()) {
            throw new InputEndedException();
        }
        return in.nextLine().trim();
    }

    /**
     * Keeps asking until {@code validator} accepts the input. The validator
     * returns the cleaned value or throws {@link IllegalArgumentException}.
     */
    public String promptValid(String label, Function<String, String> validator) {
        while (true) {
            String input = prompt(label);
            try {
                return validator.apply(input);
            } catch (IllegalArgumentException e) {
                println("  ! " + e.getMessage());
            }
        }
    }

    /** Like {@link #promptValid} but pressing Enter keeps {@code current}. */
    public String promptWithDefault(String label, String current, Function<String, String> validator) {
        return promptValid(label + " [" + current + "]",
                input -> input.isEmpty() ? current : validator.apply(input));
    }

    /** Asks a yes/no question and keeps asking until the answer is y or n. */
    public boolean confirm(String question) {
        while (true) {
            String answer = prompt(question + " (y/n)").toLowerCase();
            if (answer.equals("y") || answer.equals("yes")) {
                return true;
            }
            if (answer.equals("n") || answer.equals("no")) {
                return false;
            }
            println("  ! Please answer y or n.");
        }
    }

    /**
     * Asks for a date as MM-DD-YYYY. If {@code defaultValue} is not null,
     * pressing Enter accepts it.
     */
    public LocalDate promptDate(String label, LocalDate defaultValue) {
        String shown = defaultValue == null
                ? label + " (" + Formats.DATE_HINT + ")"
                : label + " (" + Formats.DATE_HINT + ") [" + Formats.date(defaultValue) + "]";
        String result = promptValid(shown, input -> {
            if (input.isEmpty() && defaultValue != null) {
                return Formats.date(defaultValue);
            }
            return Formats.date(Formats.parseDate(input));
        });
        return Formats.parseDate(result);
    }
}
