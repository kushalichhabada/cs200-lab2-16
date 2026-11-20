package chocan.gui;

import chocan.model.ReportPeriod;
import chocan.service.AccountingProcedure;
import chocan.service.AccountingResult;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.nio.file.Path;
import java.time.LocalDate;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * "Run main accounting procedure" tab (used by operators and managers): pick a
 * period, run the procedure and see every report it produced.
 *
 * @author [TEAM MEMBER NAME]
 */
class AccountingPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final transient AccountingProcedure accounting;
    private final JTextField from;
    private final JTextField to;
    private final JTextArea output = GuiUtil.reportArea();

    AccountingPanel(AccountingProcedure accounting) {
        super(new BorderLayout());
        this.accounting = accounting;
        ReportPeriod defaults = ReportPeriod.lastSevenDays(LocalDate.now());
        from = GuiUtil.dateField(defaults.getFrom());
        to = GuiUtil.dateField(defaults.getTo());

        JButton run = new JButton("Run main accounting procedure");
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("From (MM-DD-YYYY):"));
        top.add(from);
        top.add(new JLabel("To:"));
        top.add(to);
        top.add(run);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(output), BorderLayout.CENTER);
        run.addActionListener(e -> GuiUtil.run(this, this::runProcedure));
    }

    private void runProcedure() {
        ReportPeriod period = GuiUtil.period(from, to);
        AccountingResult result = accounting.run(period);

        StringBuilder sb = new StringBuilder();
        sb.append("MAIN ACCOUNTING PROCEDURE - ").append(period).append("\n\n");
        sb.append(">>> MEMBER REPORTS (").append(result.getMemberReports().size()).append(")\n");
        for (String report : result.getMemberReports()) {
            sb.append(report).append("\n");
        }
        sb.append(">>> PROVIDER REPORTS (").append(result.getProviderReports().size()).append(")\n");
        for (String report : result.getProviderReports()) {
            sb.append(report).append("\n");
        }
        sb.append(">>> SUMMARY REPORT\n").append(result.getSummaryReport()).append("\n");
        sb.append(">>> EFT DATA\n").append(result.getEftReport()).append("\n");
        for (String warning : result.getWarnings()) {
            sb.append("Warning: ").append(warning).append("\n");
        }
        sb.append("Accounting complete. ").append(result.getFiles().size()).append(" report file(s) saved:\n");
        for (Path file : result.getFiles()) {
            sb.append("  ").append(file).append("\n");
        }
        output.setText(sb.toString());
        output.setCaretPosition(0);
    }
}
