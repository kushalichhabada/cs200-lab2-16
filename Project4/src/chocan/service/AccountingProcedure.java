package chocan.service;

import chocan.data.ChocAnDatabase;
import chocan.model.Member;
import chocan.model.Provider;
import chocan.model.ReportPeriod;
import chocan.util.Formats;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;

/**
 * The main accounting procedure. For the chosen period it produces a member
 * report for every member who received a service, a provider report for every
 * provider who billed, the accounts-payable summary report and the EFT data,
 * and writes each one to the reports folder.
 *
 * <p>The requirements run this every Friday at midnight; here an operator or
 * manager starts it from their menu.
 *
 * @author [TEAM MEMBER NAME]
 */
public class AccountingProcedure {

    private final ChocAnDatabase db;
    private final ReportGenerator reports;
    private final ReportWriter writer;

    public AccountingProcedure(ChocAnDatabase db, ReportGenerator reports, ReportWriter writer) {
        this.db = db;
        this.reports = reports;
        this.writer = writer;
    }

    public AccountingResult run(ReportPeriod period) {
        AccountingResult result = new AccountingResult(period);
        String stamp = Formats.date(LocalDate.now());

        for (String memberNumber : reports.memberNumbersWithServices(period)) {
            Member member = db.findMember(memberNumber);
            if (member == null) {
                result.addWarning("Member " + memberNumber + " received services but is no longer"
                        + " registered; no member report was produced.");
                continue;
            }
            String text = reports.memberReport(member, period);
            result.addMemberReport(text);
            save(result, "Member_" + member.getName() + "_" + stamp, text);
        }

        for (String providerNumber : reports.providerNumbersWithServices(period)) {
            Provider provider = db.findProvider(providerNumber);
            if (provider == null) {
                result.addWarning("Provider " + providerNumber + " billed services but is no longer"
                        + " registered; no provider report was produced (still included in the summary).");
                continue;
            }
            String text = reports.providerReport(provider, period);
            result.addProviderReport(text);
            save(result, "Provider_" + provider.getName() + "_" + stamp, text);
        }

        String summary = reports.summaryReport(period);
        result.setSummaryReport(summary);
        save(result, "Summary_Report_" + stamp, summary);

        String eft = reports.eftReport(period);
        result.setEftReport(eft);
        save(result, "EFT_Data_" + stamp, eft);

        return result;
    }

    private void save(AccountingResult result, String baseName, String text) {
        try {
            Path file = writer.write(baseName, text);
            result.addFile(file);
        } catch (IOException e) {
            result.addWarning("Could not write " + baseName + ": " + e.getMessage());
        }
    }
}
