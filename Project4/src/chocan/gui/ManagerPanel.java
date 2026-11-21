package chocan.gui;

import chocan.AppContext;
import chocan.model.Member;
import chocan.model.Provider;
import chocan.model.ReportPeriod;
import chocan.service.ChocAnException;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * GUI screen for managers: request the summary, member and provider reports
 * on demand, or run the main accounting procedure.
 *
 * @author [TEAM MEMBER NAME]
 */
class ManagerPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final String SUMMARY = "Summary report";
    private static final String MEMBER = "Member report";
    private static final String PROVIDER = "Provider report";

    private final transient AppContext context;
    private final JComboBox<String> type = new JComboBox<>(new String[]{SUMMARY, MEMBER, PROVIDER});
    private final JTextField number = new JTextField(10);
    private final JTextField from;
    private final JTextField to;
    private final JTextArea output = GuiUtil.reportArea();

    ManagerPanel(AppContext context) {
        super(new BorderLayout());
        this.context = context;
        ReportPeriod defaults = ReportPeriod.lastSevenDays(LocalDate.now());
        from = GuiUtil.dateField(defaults.getFrom());
        to = GuiUtil.dateField(defaults.getTo());

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Reports", buildReportsTab());
        tabs.addTab("Accounting", new AccountingPanel(context.getAccounting()));
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildReportsTab() {
        JButton generate = new JButton("Generate report");
        JLabel numberLabel = new JLabel("Member / provider number:");

        JPanel top = new JPanel(new java.awt.GridLayout(2, 1));

        JPanel firstRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel secondRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        firstRow.add(new JLabel("Report:"));
        firstRow.add(type);
        firstRow.add(numberLabel);
        firstRow.add(number);

        secondRow.add(new JLabel("From:"));
        secondRow.add(from);
        secondRow.add(new JLabel("To:"));
        secondRow.add(to);
        secondRow.add(generate);

        top.add(firstRow);
        top.add(secondRow);

        // The number only matters for member and provider reports.
        type.addActionListener(e -> {
            boolean needsNumber = !SUMMARY.equals(type.getSelectedItem());
            number.setEnabled(needsNumber);
            numberLabel.setEnabled(needsNumber);
        });
        number.setEnabled(false);
        numberLabel.setEnabled(false);
        generate.addActionListener(e -> GuiUtil.run(this, this::generate));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(output), BorderLayout.CENTER);
        return panel;
    }

    private void generate() throws ChocAnException {
        ReportPeriod period = GuiUtil.period(from, to);
        String selected = (String) type.getSelectedItem();
        String text;
        if (MEMBER.equals(selected)) {
            Member member = context.getMembers().getMember(number.getText());
            text = context.getReports().memberReport(member, period);
        } else if (PROVIDER.equals(selected)) {
            Provider provider = context.getProviders().getProvider(number.getText());
            text = context.getReports().providerReport(provider, period);
        } else {
            text = context.getReports().summaryReport(period);
        }
        output.setText(text);
        output.setCaretPosition(0);
    }
}
