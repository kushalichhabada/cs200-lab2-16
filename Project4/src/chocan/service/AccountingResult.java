package chocan.service;

import chocan.model.ReportPeriod;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Everything one run of the main accounting procedure produced: the text of
 * each report, the files they were written to and any warnings.
 *
 * @author [TEAM MEMBER NAME]
 */
public class AccountingResult {

    private final ReportPeriod period;
    private final List<String> memberReports = new ArrayList<>();
    private final List<String> providerReports = new ArrayList<>();
    private final List<Path> files = new ArrayList<>();
    private final List<String> warnings = new ArrayList<>();
    private String summaryReport = "";
    private String eftReport = "";

    public AccountingResult(ReportPeriod period) {
        this.period = period;
    }

    public ReportPeriod getPeriod() {
        return period;
    }

    public List<String> getMemberReports() {
        return Collections.unmodifiableList(memberReports);
    }

    public List<String> getProviderReports() {
        return Collections.unmodifiableList(providerReports);
    }

    public String getSummaryReport() {
        return summaryReport;
    }

    public String getEftReport() {
        return eftReport;
    }

    public List<Path> getFiles() {
        return Collections.unmodifiableList(files);
    }

    public List<String> getWarnings() {
        return Collections.unmodifiableList(warnings);
    }

    void addMemberReport(String report) {
        memberReports.add(report);
    }

    void addProviderReport(String report) {
        providerReports.add(report);
    }

    void addFile(Path file) {
        files.add(file);
    }

    void addWarning(String warning) {
        warnings.add(warning);
    }

    void setSummaryReport(String summaryReport) {
        this.summaryReport = summaryReport;
    }

    void setEftReport(String eftReport) {
        this.eftReport = eftReport;
    }
}
