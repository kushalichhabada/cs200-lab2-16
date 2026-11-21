package chocan.ui;

import chocan.model.Member;
import chocan.model.Provider;
import chocan.model.ReportPeriod;
import chocan.service.AccountingProcedure;
import chocan.service.ChocAnException;
import chocan.service.MemberManager;
import chocan.service.ProviderManager;
import chocan.service.ReportGenerator;
import chocan.service.UserSession;

/**
 * Menu for managers: request the summary, member and provider reports on
 * demand and run the main accounting procedure.
 *
 * @author [TEAM MEMBER NAME]
 */
public class ManagerMenu extends Menu {

    private final UserSession session;
    private final ReportGenerator reports;
    private final MemberManager members;
    private final ProviderManager providers;
    private final AccountingProcedure accounting;

    public ManagerMenu(ConsoleIO io, UserSession session, ReportGenerator reports,
                       MemberManager members, ProviderManager providers,
                       AccountingProcedure accounting) {
        super(io);
        this.session = session;
        this.reports = reports;
        this.members = members;
        this.providers = providers;
        this.accounting = accounting;
    }

    @Override
    protected String title() {
        return "MANAGER MENU - " + session.getName();
    }

    @Override
    protected void printOptions() {
        io.println(" 1. Summary report");
        io.println(" 2. Member report");
        io.println(" 3. Provider report");
        io.println(" 4. Run main accounting procedure");
        io.println(" 0. Log out");
    }

    @Override
    protected boolean handle(String choice) throws ChocAnException {
        switch (choice) {
            case "1": summaryReport(); break;
            case "2": memberReport(); break;
            case "3": providerReport(); break;
            case "4": runAccounting(accounting); break;
            case "0": return false;
            default: invalidChoice(choice);
        }
        return true;
    }

    private void summaryReport() {
        io.println("--- Summary Report ---");
        ReportPeriod period = promptPeriod();
        io.println(reports.summaryReport(period));
    }

    private void memberReport() throws ChocAnException {
        io.println("--- Member Report ---");
        Member member = members.getMember(io.prompt("Member number"));
        ReportPeriod period = promptPeriod();
        io.println(reports.memberReport(member, period));
    }

    private void providerReport() throws ChocAnException {
        io.println("--- Provider Report ---");
        Provider provider = providers.getProvider(io.prompt("Provider number"));
        ReportPeriod period = promptPeriod();
        io.println(reports.providerReport(provider, period));
    }
}
