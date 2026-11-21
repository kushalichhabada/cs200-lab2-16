package chocan.ui;

import chocan.model.Member;
import chocan.model.Person;
import chocan.model.PersonDetails;
import chocan.model.ReportPeriod;
import chocan.service.AccountingProcedure;
import chocan.service.AccountingResult;
import chocan.service.ChocAnException;
import chocan.util.Validation;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.function.Function;

/**
 * Template for the three role menus. {@link #run()} shows the menu, reads a
 * choice and hands it to the subclass until the user logs out; error messages
 * from the service layer are caught here so a mistake never ends the program.
 * It also holds the helpers that more than one menu needs (entering a
 * person's details, choosing a report period, running accounting).
 *
 * @author [TEAM MEMBER NAME]
 */
public abstract class Menu {

    protected final ConsoleIO io;

    protected Menu(ConsoleIO io) {
        this.io = io;
    }

    /** Title shown above the option list. */
    protected abstract String title();

    /** Prints the numbered options (option 0 is always logout). */
    protected abstract void printOptions();

    /**
     * Carries out one menu choice.
     *
     * @return false when the user chose to log out, true to keep showing the menu
     */
    protected abstract boolean handle(String choice) throws ChocAnException, IOException;

    /** Shows the menu repeatedly until the user logs out. */
    public final void run() {
        boolean running = true;
        while (running) {
            io.header(title());
            printOptions();
            String choice = io.prompt("Select an option");
            try {
                running = handle(choice);
            } catch (ChocAnException | IllegalArgumentException e) {
                io.println("  ! " + e.getMessage());
            } catch (IOException e) {
                io.println("  ! File problem: " + e.getMessage());
            }
        }
    }

    protected void invalidChoice(String choice) {
        io.println("  ! \"" + choice + "\" is not a menu option.");
    }

    // ---- helpers shared by several menus --------------------------------

    /**
     * Asks for name and address. When {@code current} is given (editing),
     * pressing Enter keeps the existing value of each field.
     */
    protected PersonDetails promptPersonDetails(PersonDetails current) {
        String name = field("Name (max 25 chars)", current == null ? null : current.getName(),
                s -> Validation.requireText("Name", s, Validation.NAME_MAX));
        String street = field("Street address (max 25 chars)", current == null ? null : current.getStreetAddress(),
                s -> Validation.requireText("Street address", s, Validation.ADDRESS_MAX));
        String city = field("City (max 14 chars)", current == null ? null : current.getCity(),
                s -> Validation.requireText("City", s, Validation.CITY_MAX));
        String state = field("State (2 letters)", current == null ? null : current.getState(),
                Validation::requireState);
        String zip = field("Zip code (5 digits)", current == null ? null : current.getZip(),
                s -> Validation.requireDigits("Zip code", s, 5));
        return new PersonDetails(name, street, city, state, zip);
    }

    private String field(String label, String current, Function<String, String> validator) {
        return current == null ? io.promptValid(label, validator) : io.promptWithDefault(label, current, validator);
    }

    /** Prints a member or provider's number, name and address. */
    protected void printPerson(Person person) {
        io.println("  Found " + person.getTypeLabel().toLowerCase() + ":");
        io.println("    Number:  " + person.getNumber());
        io.println("    Name:    " + person.getName());
        io.println("    Address: " + person.getStreetAddress() + ", " + person.getCityStateZip());
        if (person instanceof Member) {
            io.println("    Status:  " + ((Member) person).getStatus().getLabel());
        }
    }

    /** Asks for the start and end of a report (default: the last seven days). */
    protected ReportPeriod promptPeriod() {
        ReportPeriod defaults = ReportPeriod.lastSevenDays(LocalDate.now());
        while (true) {
            LocalDate from = io.promptDate("Start date", defaults.getFrom());
            LocalDate to = io.promptDate("End date", defaults.getTo());
            try {
                return new ReportPeriod(from, to);
            } catch (IllegalArgumentException e) {
                io.println("  ! " + e.getMessage());
            }
        }
    }

    /** Runs the main accounting procedure and prints everything it produced. */
    protected void runAccounting(AccountingProcedure accounting) {
        io.println("--- Main Accounting Procedure ---");
        ReportPeriod period = promptPeriod();
        io.println("Running accounting for " + period + " ...");
        AccountingResult result = accounting.run(period);

        io.blank();
        io.println(">>> MEMBER REPORTS (" + result.getMemberReports().size() + ")");
        for (String report : result.getMemberReports()) {
            io.println(report);
        }
        io.println(">>> PROVIDER REPORTS (" + result.getProviderReports().size() + ")");
        for (String report : result.getProviderReports()) {
            io.println(report);
        }
        io.println(">>> SUMMARY REPORT");
        io.println(result.getSummaryReport());
        io.println(">>> EFT DATA");
        io.println(result.getEftReport());

        for (String warning : result.getWarnings()) {
            io.println("  ! Warning: " + warning);
        }
        io.println("Accounting complete. " + result.getFiles().size() + " report file(s) saved:");
        for (Path file : result.getFiles()) {
            io.println("  " + file);
        }
    }
}
