package chocan.gui;

import chocan.model.MemberStatus;
import chocan.model.PersonDetails;
import java.awt.Component;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * The add/edit form for a member or provider. It re-opens with the user's
 * text still filled in until the details pass validation or the user cancels.
 *
 * @author [TEAM MEMBER NAME]
 */
final class PersonDialog {

    private final PersonDetails details;
    private final MemberStatus status;

    private PersonDialog(PersonDetails details, MemberStatus status) {
        this.details = details;
        this.status = status;
    }

    PersonDetails getDetails() {
        return details;
    }

    /** The chosen status, or null if the form had no status field (providers, new members). */
    MemberStatus getStatus() {
        return status;
    }

    /**
     * Shows the form.
     *
     * @param current the values to pre-fill (null when adding)
     * @param currentStatus the member's status to edit, or null to hide the status choice
     * @return the entered values, or null if the user cancelled
     */
    static PersonDialog show(Component parent, String title, PersonDetails current,
                             MemberStatus currentStatus) {
        JTextField name = new JTextField(current == null ? "" : current.getName(), 22);
        JTextField street = new JTextField(current == null ? "" : current.getStreetAddress(), 22);
        JTextField city = new JTextField(current == null ? "" : current.getCity(), 22);
        JTextField state = new JTextField(current == null ? "" : current.getState(), 22);
        JTextField zip = new JTextField(current == null ? "" : current.getZip(), 22);
        JComboBox<MemberStatus> statusBox = new JComboBox<>(MemberStatus.values());
        if (currentStatus != null) {
            statusBox.setSelectedItem(currentStatus);
        }

        String[] labels = {"Name (max 25)", "Street address (max 25)", "City (max 14)",
                "State (2 letters)", "Zip code (5 digits)"};
        JComponent[] fields = {name, street, city, state, zip};
        if (currentStatus != null) {
            labels = new String[]{labels[0], labels[1], labels[2], labels[3], labels[4], "Status"};
            fields = new JComponent[]{name, street, city, state, zip, statusBox};
        }
        JPanel form = GuiUtil.form(labels, fields);

        while (true) {
            int option = JOptionPane.showConfirmDialog(parent, form, title,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option != JOptionPane.OK_OPTION) {
                return null;
            }
            try {
                PersonDetails entered = new PersonDetails(name.getText(), street.getText(),
                        city.getText(), state.getText(), zip.getText());
                MemberStatus chosen = currentStatus == null ? null : (MemberStatus) statusBox.getSelectedItem();
                return new PersonDialog(entered, chosen);
            } catch (IllegalArgumentException e) {
                GuiUtil.error(parent, e.getMessage());
            }
        }
    }
}
