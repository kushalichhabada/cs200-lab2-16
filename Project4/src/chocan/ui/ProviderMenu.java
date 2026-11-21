package chocan.ui;

import chocan.model.ServiceRecord;
import chocan.model.ServiceType;
import chocan.service.BillingService;
import chocan.service.ChocAnException;
import chocan.service.MemberCheck;
import chocan.service.ProviderDirectory;
import chocan.service.UserSession;
import chocan.util.Formats;
import chocan.util.Validation;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;

/**
 * Menu for a logged-in provider: validate a member, bill ChocAn for a service
 * and request the provider directory.
 *
 * @author [TEAM MEMBER NAME]
 */
public class ProviderMenu extends Menu {

    private final UserSession session;
    private final BillingService billing;
    private final ProviderDirectory directory;

    public ProviderMenu(ConsoleIO io, UserSession session, BillingService billing,
                        ProviderDirectory directory) {
        super(io);
        this.session = session;
        this.billing = billing;
        this.directory = directory;
    }

    @Override
    protected String title() {
        return "PROVIDER MENU - " + session.getName() + " (" + session.getId() + ")";
    }

    @Override
    protected void printOptions() {
        io.println(" 1. Validate member");
        io.println(" 2. Bill ChocAn (record a service)");
        io.println(" 3. Request provider directory");
        io.println(" 0. Log out");
    }

    @Override
    protected boolean handle(String choice) throws ChocAnException, IOException {
        switch (choice) {
            case "1": validateMember(); break;
            case "2": billChocAn(); break;
            case "3": requestDirectory(); break;
            case "0": return false;
            default: invalidChoice(choice);
        }
        return true;
    }

    private void validateMember() {
        io.println("--- Validate Member ---");
        MemberCheck check = billing.validateMember(io.prompt("Member number"));
        io.println("Result: " + check.getMessage());
    }

    /** The "Bill ChocAn" use case: validate, enter date, verify service code, add comments, record. */
    private void billChocAn() throws ChocAnException {
        io.println("--- Bill ChocAn ---");
        String memberNumber = io.prompt("Member number");
        MemberCheck check = billing.validateMember(memberNumber);
        io.println("Member check: " + check.getMessage());
        if (check != MemberCheck.VALID) {
            io.println("The service cannot be billed.");
            return;
        }

        LocalDate serviceDate = promptServiceDate();
        ServiceType service = promptServiceCode();
        if (service == null) {
            io.println("Billing cancelled.");
            return;
        }
        String comments = io.promptValid("Comments (optional, max 100 chars)",
                s -> Validation.optionalText("Comments", s, Validation.COMMENT_MAX));

        ServiceRecord record = billing.recordService(
                session.getId(), memberNumber, serviceDate, service.getCode(), comments);
        io.println("Service recorded for " + record.getMemberName() + ".");
        io.println("Fee to be paid to you: " + Formats.money(record.getFee()));
    }

    private LocalDate promptServiceDate() {
        while (true) {
            LocalDate date = io.promptDate("Date service was provided", null);
            if (date.isAfter(LocalDate.now())) {
                io.println("  ! The date of service cannot be in the future.");
            } else {
                return date;
            }
        }
    }

    /** Asks for a service code until the provider confirms the displayed name; blank cancels. */
    private ServiceType promptServiceCode() {
        while (true) {
            String code = io.prompt("Service code (6 digits, blank to cancel)");
            if (code.isEmpty()) {
                return null;
            }
            try {
                ServiceType service = billing.findService(code);
                io.println("Service: " + service.getName() + " (" + Formats.money(service.getFee()) + ")");
                if (io.confirm("Is this the correct service?")) {
                    return service;
                }
            } catch (ChocAnException e) {
                io.println("  ! " + e.getMessage());
            }
        }
    }

    private void requestDirectory() throws IOException {
        io.println("--- Provider Directory ---");
        io.println(directory.generate());
        Path copy = directory.sendTo(session.getName());
        io.println("A copy was saved to " + copy);
    }
}
