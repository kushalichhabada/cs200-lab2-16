package chocan.ui;

import chocan.model.Member;
import chocan.model.MemberStatus;
import chocan.model.PersonDetails;
import chocan.model.Provider;
import chocan.service.AccountingProcedure;
import chocan.service.ChocAnException;
import chocan.service.MemberManager;
import chocan.service.ProviderManager;
import java.util.List;

/**
 * Menu for ChocAn operators: manage members and providers (add, edit, delete,
 * list) and run the main accounting procedure.
 *
 * @author [TEAM MEMBER NAME]
 */
public class OperatorMenu extends Menu {

    private final MemberManager members;
    private final ProviderManager providers;
    private final AccountingProcedure accounting;

    public OperatorMenu(ConsoleIO io, MemberManager members, ProviderManager providers,
                        AccountingProcedure accounting) {
        super(io);
        this.members = members;
        this.providers = providers;
        this.accounting = accounting;
    }

    @Override
    protected String title() {
        return "OPERATOR MENU";
    }

    @Override
    protected void printOptions() {
        io.println(" 1. Add member");
        io.println(" 2. Edit member");
        io.println(" 3. Delete member");
        io.println(" 4. List members");
        io.println(" 5. Add provider");
        io.println(" 6. Edit provider");
        io.println(" 7. Delete provider");
        io.println(" 8. List providers");
        io.println(" 9. Run main accounting procedure");
        io.println(" 0. Log out");
    }

    @Override
    protected boolean handle(String choice) throws ChocAnException {
        switch (choice) {
            case "1": addMember(); break;
            case "2": editMember(); break;
            case "3": deleteMember(); break;
            case "4": listMembers(); break;
            case "5": addProvider(); break;
            case "6": editProvider(); break;
            case "7": deleteProvider(); break;
            case "8": listProviders(); break;
            case "9": runAccounting(accounting); break;
            case "0": return false;
            default: invalidChoice(choice);
        }
        return true;
    }

    // ---- members ---------------------------------------------------------

    private void addMember() {
        io.println("--- Add Member ---");
        PersonDetails details = promptPersonDetails(null);
        Member member = members.addMember(details);
        io.println("Member added. Assigned member number: " + member.getNumber());
    }

    private void editMember() throws ChocAnException {
        io.println("--- Edit Member ---");
        Member member = members.getMember(io.prompt("Member number"));
        printPerson(member);
        io.println("Press Enter to keep the current value of a field.");
        PersonDetails details = promptPersonDetails(member.getDetails());
        MemberStatus status = promptStatus(member.getStatus());
        members.updateMember(member.getNumber(), details, status);
        io.println("Member " + member.getNumber() + " updated.");
    }

    private void deleteMember() throws ChocAnException {
        io.println("--- Delete Member ---");
        Member member = members.getMember(io.prompt("Member number"));
        printPerson(member);
        if (io.confirm("Delete this member permanently?")) {
            members.deleteMember(member.getNumber());
            io.println("Member " + member.getNumber() + " deleted.");
        } else {
            io.println("Delete cancelled.");
        }
    }

    private void listMembers() {
        List<Member> all = members.listMembers();
        io.println("--- Members (" + all.size() + ") ---");
        io.println(String.format("%-10s %-25s %-10s %s", "Number", "Name", "Status", "City"));
        for (Member m : all) {
            io.println(String.format("%-10s %-25s %-10s %s", m.getNumber(), m.getName(),
                    m.getStatus().getLabel(), m.getCity()));
        }
    }

    private MemberStatus promptStatus(MemberStatus current) {
        String letter = io.promptValid("Status: V = valid, S = suspended [" + current.getLabel() + "]", s -> {
            if (s.isEmpty()) {
                return current == MemberStatus.VALID ? "V" : "S";
            }
            if (s.equalsIgnoreCase("v") || s.equalsIgnoreCase("s")) {
                return s.toUpperCase();
            }
            throw new IllegalArgumentException("Enter V or S.");
        });
        return letter.equals("V") ? MemberStatus.VALID : MemberStatus.SUSPENDED;
    }

    // ---- providers -------------------------------------------------------

    private void addProvider() {
        io.println("--- Add Provider ---");
        PersonDetails details = promptPersonDetails(null);
        Provider provider = providers.addProvider(details);
        io.println("Provider added. Assigned provider number (also their login ID): " + provider.getNumber());
    }

    private void editProvider() throws ChocAnException {
        io.println("--- Edit Provider ---");
        Provider provider = providers.getProvider(io.prompt("Provider number"));
        printPerson(provider);
        io.println("Press Enter to keep the current value of a field.");
        PersonDetails details = promptPersonDetails(provider.getDetails());
        providers.updateProvider(provider.getNumber(), details);
        io.println("Provider " + provider.getNumber() + " updated.");
    }

    private void deleteProvider() throws ChocAnException {
        io.println("--- Delete Provider ---");
        Provider provider = providers.getProvider(io.prompt("Provider number"));
        printPerson(provider);
        if (io.confirm("Delete this provider permanently?")) {
            providers.deleteProvider(provider.getNumber());
            io.println("Provider " + provider.getNumber() + " deleted.");
        } else {
            io.println("Delete cancelled.");
        }
    }

    private void listProviders() {
        List<Provider> all = providers.listProviders();
        io.println("--- Providers (" + all.size() + ") ---");
        io.println(String.format("%-10s %-25s %s", "Number", "Name", "City"));
        for (Provider p : all) {
            io.println(String.format("%-10s %-25s %s", p.getNumber(), p.getName(), p.getCity()));
        }
    }
}
