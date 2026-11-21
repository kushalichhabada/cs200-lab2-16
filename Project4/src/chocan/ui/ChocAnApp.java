package chocan.ui;

import chocan.service.AccountingProcedure;
import chocan.service.AuthService;
import chocan.service.BillingService;
import chocan.service.ChocAnException;
import chocan.service.MemberManager;
import chocan.service.ProviderDirectory;
import chocan.service.ProviderManager;
import chocan.service.ReportGenerator;
import chocan.service.UserSession;

/**
 * The top of the user interface: shows the login screen, validates the ID with
 * {@link AuthService} and opens the menu that matches the user's role. When the
 * user logs out the login screen appears again.
 *
 * @author [TEAM MEMBER NAME]
 */
public class ChocAnApp {

    private final ConsoleIO io;
    private final AuthService auth;
    private final MemberManager members;
    private final ProviderManager providers;
    private final BillingService billing;
    private final ReportGenerator reports;
    private final AccountingProcedure accounting;
    private final ProviderDirectory directory;

    public ChocAnApp(ConsoleIO io, AuthService auth, MemberManager members, ProviderManager providers,
                     BillingService billing, ReportGenerator reports, AccountingProcedure accounting,
                     ProviderDirectory directory) {
        this.io = io;
        this.auth = auth;
        this.members = members;
        this.providers = providers;
        this.billing = billing;
        this.reports = reports;
        this.accounting = accounting;
        this.directory = directory;
    }

    /** Runs until the user types "exit" at the login prompt. */
    public void run() {
        io.header("CHOCOHOLICS ANONYMOUS (ChocAn) DATA PROCESSING SYSTEM");
        while (true) {
            io.blank();
            io.println("Log in with your 9-digit ID (type 'exit' to quit).");
            String id = io.prompt("ID");
            if (id.equalsIgnoreCase("exit") || id.equalsIgnoreCase("quit")) {
                io.println("Goodbye.");
                return;
            }
            try {
                UserSession session = auth.login(id);
                io.println("Welcome, " + session.getName() + " (" + session.getRole().getLabel() + ").");
                menuFor(session).run();
                io.println("Logged out.");
            } catch (ChocAnException e) {
                io.println("  ! Login failed: " + e.getMessage());
            }
        }
    }

    /** Picks the menu class for the logged-in user's role. */
    private Menu menuFor(UserSession session) {
        switch (session.getRole()) {
            case OPERATOR:
                return new OperatorMenu(io, members, providers, accounting);
            case PROVIDER:
                return new ProviderMenu(io, session, billing, directory);
            case MANAGER:
                return new ManagerMenu(io, session, reports, members, providers, accounting);
            default:
                throw new IllegalStateException("Unknown role " + session.getRole());
        }
    }
}
