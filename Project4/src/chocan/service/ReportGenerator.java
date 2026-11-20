package chocan.service;

import chocan.data.ChocAnDatabase;
import chocan.model.Member;
import chocan.model.Provider;
import chocan.model.ReportPeriod;
import chocan.model.ServiceRecord;
import chocan.util.Formats;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Builds the text of every ChocAn report: member report, provider report,
 * accounts-payable summary report and the EFT data. The same generator is used
 * by the managers' on-demand reports and by the main accounting procedure.
 *
 * @author [TEAM MEMBER NAME]
 */
public class ReportGenerator {

    private static final int WIDTH = 78;
    private static final String NL = System.lineSeparator();

    private final ChocAnDatabase db;

    public ReportGenerator(ChocAnDatabase db) {
        this.db = db;
    }

    // ---- selecting records ----------------------------------------------

    /** All service records received during the period, oldest service first. */
    public List<ServiceRecord> recordsIn(ReportPeriod period) {
        List<ServiceRecord> result = new ArrayList<>();
        for (ServiceRecord record : db.getServiceRecords()) {
            if (period.contains(record.getReceivedAt().toLocalDate())) {
                result.add(record);
            }
        }
        result.sort(Comparator.comparing(ServiceRecord::getServiceDate)
                .thenComparing(ServiceRecord::getReceivedAt));
        return result;
    }

    /** Numbers of every member who received at least one service in the period. */
    public List<String> memberNumbersWithServices(ReportPeriod period) {
        List<String> numbers = new ArrayList<>();
        for (ServiceRecord record : recordsIn(period)) {
            if (!numbers.contains(record.getMemberNumber())) {
                numbers.add(record.getMemberNumber());
            }
        }
        numbers.sort(Comparator.naturalOrder());
        return numbers;
    }

    /** Numbers of every provider who billed at least one service in the period. */
    public List<String> providerNumbersWithServices(ReportPeriod period) {
        return new ArrayList<>(totalsByProvider(period).keySet());
    }

    // ---- member report ---------------------------------------------------

    public String memberReport(Member member, ReportPeriod period) {
        StringBuilder sb = new StringBuilder();
        banner(sb, "MEMBER REPORT", period);
        sb.append(String.format("Name:     %s%n", member.getName()));
        sb.append(String.format("Number:   %s%n", member.getNumber()));
        sb.append(String.format("Address:  %s%n", member.getStreetAddress()));
        sb.append(String.format("          %s%n", member.getCityStateZip()));
        sb.append(String.format("Status:   %s%n", member.getStatus().getLabel()));
        sb.append(Formats.line('-', WIDTH)).append(NL);
        sb.append("Services received").append(NL);
        sb.append(String.format("  %-12s %-25s %-20s%n", "Service Date", "Provider", "Service"));

        int count = 0;
        for (ServiceRecord record : recordsIn(period)) {
            if (record.getMemberNumber().equals(member.getNumber())) {
                sb.append(String.format("  %-12s %-25s %-20s%n", Formats.date(record.getServiceDate()),
                        record.getProviderName(), record.getServiceName()));
                count++;
            }
        }
        if (count == 0) {
            sb.append("  (no services received in this period)").append(NL);
        }
        sb.append(Formats.line('=', WIDTH)).append(NL);
        return sb.toString();
    }

    // ---- provider report -------------------------------------------------

    public String providerReport(Provider provider, ReportPeriod period) {
        StringBuilder sb = new StringBuilder();
        banner(sb, "PROVIDER REPORT", period);
        sb.append(String.format("Name:     %s%n", provider.getName()));
        sb.append(String.format("Number:   %s%n", provider.getNumber()));
        sb.append(String.format("Address:  %s%n", provider.getStreetAddress()));
        sb.append(String.format("          %s%n", provider.getCityStateZip()));
        sb.append(Formats.line('-', WIDTH)).append(NL);
        sb.append("Services provided").append(NL);
        sb.append(String.format("  %-10s %-19s %-20s %-9s %-6s %9s%n",
                "Service", "Received", "Member", "Member #", "Code", "Fee"));

        int consultations = 0;
        BigDecimal total = BigDecimal.ZERO;
        for (ServiceRecord record : recordsIn(period)) {
            if (record.getProviderNumber().equals(provider.getNumber())) {
                sb.append(String.format("  %-10s %-19s %-20s %-9s %-6s %9s%n",
                        Formats.date(record.getServiceDate()),
                        Formats.dateTime(record.getReceivedAt()),
                        shorten(record.getMemberName(), 20),
                        record.getMemberNumber(),
                        record.getServiceCode(),
                        Formats.money(record.getFee())));
                consultations++;
                total = total.add(record.getFee());
            }
        }
        if (consultations == 0) {
            sb.append("  (no services billed in this period)").append(NL);
        }
        sb.append(Formats.line('-', WIDTH)).append(NL);
        sb.append(String.format("Total consultations: %d%n", consultations));
        sb.append(String.format("Total fee for the period: %s%n", Formats.money(total)));
        sb.append(Formats.line('=', WIDTH)).append(NL);
        return sb.toString();
    }

    // ---- summary report --------------------------------------------------

    /** Accounts-payable summary: one line per provider to be paid, plus grand totals. */
    public String summaryReport(ReportPeriod period) {
        Map<String, ProviderTotal> totals = totalsByProvider(period);

        StringBuilder sb = new StringBuilder();
        banner(sb, "SUMMARY REPORT - ACCOUNTS PAYABLE", period);
        sb.append(String.format("  %-25s %-10s %14s %12s%n",
                "Provider", "Number", "Consultations", "Total Fee"));
        sb.append(Formats.line('-', WIDTH)).append(NL);

        int providerCount = 0;
        int consultations = 0;
        BigDecimal grandTotal = BigDecimal.ZERO;
        for (ProviderTotal total : totals.values()) {
            sb.append(String.format("  %-25s %-10s %14d %12s%n", shorten(total.name, 25),
                    total.number, total.consultations, Formats.money(total.fee)));
            providerCount++;
            consultations += total.consultations;
            grandTotal = grandTotal.add(total.fee);
        }
        if (providerCount == 0) {
            sb.append("  (no services were billed in this period)").append(NL);
        }
        sb.append(Formats.line('-', WIDTH)).append(NL);
        sb.append(String.format("Total providers to be paid: %d%n", providerCount));
        sb.append(String.format("Total consultations:        %d%n", consultations));
        sb.append(String.format("Overall fee total:          %s%n", Formats.money(grandTotal)));
        sb.append(Formats.line('=', WIDTH)).append(NL);
        return sb.toString();
    }

    // ---- EFT data --------------------------------------------------------

    /** What the bank needs to pay each provider: name, number and amount to transfer. */
    public String eftReport(ReportPeriod period) {
        StringBuilder sb = new StringBuilder();
        banner(sb, "EFT DATA - ELECTRONIC FUNDS TRANSFER", period);
        sb.append(String.format("  %-25s %-10s %16s%n", "Provider", "Number", "Amount to Transfer"));
        sb.append(Formats.line('-', WIDTH)).append(NL);

        boolean any = false;
        for (ProviderTotal total : totalsByProvider(period).values()) {
            sb.append(String.format("  %-25s %-10s %18s%n", shorten(total.name, 25),
                    total.number, Formats.money(total.fee)));
            any = true;
        }
        if (!any) {
            sb.append("  (no transfers needed for this period)").append(NL);
        }
        sb.append(Formats.line('=', WIDTH)).append(NL);
        return sb.toString();
    }

    // ---- helpers ---------------------------------------------------------

    private Map<String, ProviderTotal> totalsByProvider(ReportPeriod period) {
        Map<String, ProviderTotal> totals = new TreeMap<>();
        for (ServiceRecord record : recordsIn(period)) {
            ProviderTotal total = totals.get(record.getProviderNumber());
            if (total == null) {
                total = new ProviderTotal(record.getProviderNumber(), record.getProviderName());
                totals.put(record.getProviderNumber(), total);
            }
            total.consultations++;
            total.fee = total.fee.add(record.getFee());
        }
        return totals;
    }

    private void banner(StringBuilder sb, String title, ReportPeriod period) {
        sb.append(Formats.line('=', WIDTH)).append(NL);
        sb.append(title).append(NL);
        sb.append("Period: ").append(period).append(NL);
        sb.append(Formats.line('-', WIDTH)).append(NL);
    }

    private static String shorten(String text, int max) {
        return text.length() <= max ? text : text.substring(0, max);
    }

    /** Running totals for one provider while building the summary and EFT data. */
    private static final class ProviderTotal {
        private final String number;
        private final String name;
        private int consultations;
        private BigDecimal fee = BigDecimal.ZERO;

        private ProviderTotal(String number, String name) {
            this.number = number;
            this.name = name;
        }
    }
}
