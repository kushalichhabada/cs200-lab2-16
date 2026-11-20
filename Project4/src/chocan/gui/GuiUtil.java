package chocan.gui;

import chocan.model.ReportPeriod;
import chocan.service.ChocAnException;
import chocan.util.Formats;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.time.LocalDate;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Small helpers shared by the GUI panels: error handling for button actions,
 * a monospaced report area, label/field forms and report-period fields.
 *
 * @author [TEAM MEMBER NAME]
 */
final class GuiUtil {

    /** A button action that may fail with a business-rule or file error. */
    interface Action {
        void run() throws ChocAnException, IOException;
    }

    private GuiUtil() {
        // utility class
    }

    /** Runs {@code action}; any expected failure is shown in an error dialog instead of crashing. */
    static void run(Component parent, Action action) {
        try {
            action.run();
        } catch (ChocAnException | IllegalArgumentException e) {
            error(parent, e.getMessage());
        } catch (IOException e) {
            error(parent, "File problem: " + e.getMessage());
        }
    }

    static void error(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Problem", JOptionPane.ERROR_MESSAGE);
    }

    static void info(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "ChocAn", JOptionPane.INFORMATION_MESSAGE);
    }

    static boolean confirm(Component parent, String question) {
        return JOptionPane.showConfirmDialog(parent, question, "Please confirm",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    /** A read-only monospaced text area, the right look for the text reports. */
    static JTextArea reportArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        return area;
    }

    /** Lays out "label: field" rows in two aligned columns. */
    static JPanel form(String[] labels, JComponent[] fields) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.anchor = GridBagConstraints.WEST;
        for (int i = 0; i < labels.length; i++) {
            c.gridx = 0;
            c.gridy = i;
            c.weightx = 0;
            c.fill = GridBagConstraints.NONE;
            panel.add(new JLabel(labels[i]), c);
            c.gridx = 1;
            c.weightx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            panel.add(fields[i], c);
        }
        return panel;
    }

    static JTextField dateField(LocalDate value) {
        JTextField field = new JTextField(value == null ? "" : Formats.date(value), 10);
        field.setToolTipText(Formats.DATE_HINT);
        return field;
    }

    /** Reads a start/end date pair from two text fields. */
    static ReportPeriod period(JTextField from, JTextField to) {
        return new ReportPeriod(Formats.parseDate(from.getText()), Formats.parseDate(to.getText()));
    }
}
