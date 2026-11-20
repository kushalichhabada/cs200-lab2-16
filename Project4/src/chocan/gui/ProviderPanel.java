package chocan.gui;

import chocan.AppContext;
import chocan.model.ServiceRecord;
import chocan.model.ServiceType;
import chocan.service.MemberCheck;
import chocan.service.UserSession;
import chocan.util.Formats;
import chocan.util.Validation;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.nio.file.Path;
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
 * GUI screen for a logged-in provider: validate a member, bill ChocAn for a
 * service and view the provider directory.
 *
 * @author [TEAM MEMBER NAME]
 */
class ProviderPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final transient AppContext context;
    private final transient UserSession session;

    ProviderPanel(AppContext context, UserSession session) {
        super(new BorderLayout());
        this.context = context;
        this.session = session;
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Validate member", buildValidateTab());
        tabs.addTab("Bill ChocAn", buildBillingTab());
        tabs.addTab("Provider directory", buildDirectoryTab());
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildValidateTab() {
        JTextField memberNumber = new JTextField(12);
        JButton check = new JButton("Validate");
        JLabel result = new JLabel(" ");
        result.setFont(result.getFont().deriveFont(Font.BOLD, 18f));

        check.addActionListener(e -> showCheck(context.getBilling().validateMember(memberNumber.getText()), result));
        memberNumber.addActionListener(e -> check.doClick());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Member number:"));
        top.add(memberNumber);
        top.add(check);
        JPanel resultRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        resultRow.add(result);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(top, BorderLayout.NORTH);
        panel.add(resultRow, BorderLayout.CENTER);
        return panel;
    }

    private static void showCheck(MemberCheck check, JLabel label) {
        label.setText(check.getMessage());
        label.setForeground(check == MemberCheck.VALID ? new Color(0x1B7F2A) : new Color(0xB00020));
    }

    private JPanel buildBillingTab() {
        JTextField memberNumber = new JTextField(12);
        JTextField serviceDate = GuiUtil.dateField(LocalDate.now());
        JComboBox<ServiceType> service = new JComboBox<>();
        for (ServiceType type : context.getDirectory().alphabeticalServices()) {
            service.addItem(type);
        }
        service.setRenderer((list, value, index, selected, focus) -> {
            JLabel label = new JLabel(value == null ? "" : value.getName() + "  -  " + value.getCode()
                    + "  -  " + Formats.money(value.getFee()));
            label.setOpaque(true);
            label.setBackground(selected ? list.getSelectionBackground() : list.getBackground());
            label.setForeground(selected ? list.getSelectionForeground() : list.getForeground());
            return label;
        });
        JTextField comments = new JTextField(30);
        JButton bill = new JButton("Bill ChocAn");

        bill.addActionListener(e -> GuiUtil.run(this, () -> {
            MemberCheck check = context.getBilling().validateMember(memberNumber.getText());
            if (check != MemberCheck.VALID) {
                GuiUtil.error(this, "Member check: " + check.getMessage() + ". The service cannot be billed.");
                return;
            }
            LocalDate date = Formats.parseDate(serviceDate.getText());
            String cleanComments = Validation.optionalText("Comments", comments.getText(), Validation.COMMENT_MAX);
            ServiceType chosen = (ServiceType) service.getSelectedItem();
            if (chosen == null) {
                GuiUtil.error(this, "Choose a service.");
                return;
            }
            if (!GuiUtil.confirm(this, "Bill " + chosen.getName() + " (" + Formats.money(chosen.getFee())
                    + ") for member " + memberNumber.getText().trim() + "?")) {
                return;
            }
            ServiceRecord record = context.getBilling().recordService(
                    session.getId(), memberNumber.getText(), date, chosen.getCode(), cleanComments);
            GuiUtil.info(this, "Service recorded for " + record.getMemberName() + ".\nFee to be paid to you: "
                    + Formats.money(record.getFee()));
            memberNumber.setText("");
            comments.setText("");
        }));

        JPanel form = GuiUtil.form(
                new String[]{"Member number", "Date of service (MM-DD-YYYY)", "Service", "Comments (optional)"},
                new javax.swing.JComponent[]{memberNumber, serviceDate, service, comments});
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonRow.add(bill);

        JPanel panel = new JPanel(new BorderLayout());
        JPanel north = new JPanel(new BorderLayout());
        north.add(form, BorderLayout.CENTER);
        north.add(buttonRow, BorderLayout.SOUTH);
        panel.add(north, BorderLayout.NORTH);
        return panel;
    }

    private JPanel buildDirectoryTab() {
        JTextArea area = GuiUtil.reportArea();
        area.setText(context.getDirectory().generate());
        JButton save = new JButton("Save a copy to reports folder");
        save.addActionListener(e -> GuiUtil.run(this, () -> {
            Path file = context.getDirectory().sendTo(session.getName());
            GuiUtil.info(this, "A copy was saved to " + file);
        }));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(save);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        return panel;
    }
}
