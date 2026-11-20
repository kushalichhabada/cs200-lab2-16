package chocan.gui;

import chocan.service.AuthService;
import chocan.service.ChocAnException;
import chocan.service.UserSession;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * The GUI login screen: type a 9-digit ID and press Enter or the Login button.
 * A valid ID is passed to {@code onSuccess}; an invalid one shows a message.
 *
 * @author [TEAM MEMBER NAME]
 */
class LoginPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final transient AuthService auth;
    private final transient Consumer<UserSession> onSuccess;
    private final JTextField idField = new JTextField(14);
    private final JLabel message = new JLabel(" ", SwingConstants.CENTER);

    LoginPanel(AuthService auth, Consumer<UserSession> onSuccess) {
        super(new GridBagLayout());
        this.auth = auth;
        this.onSuccess = onSuccess;

        JLabel title = new JLabel("Chocoholics Anonymous (ChocAn)", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        JLabel prompt = new JLabel("Enter your 9-digit ID to log in", SwingConstants.CENTER);
        JButton login = new JButton("Log in");
        message.setForeground(new Color(0xB00020));
        JLabel hint = new JLabel("<html><center>Sample IDs: operator 300000001, manager 400000001,"
                + "<br>providers 200000001 - 200000003</center></html>", SwingConstants.CENTER);
        hint.setForeground(Color.GRAY);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        add(title, c);
        add(prompt, c);
        add(idField, c);
        add(login, c);
        add(message, c);
        add(hint, c);

        login.addActionListener(e -> attemptLogin());
        idField.addActionListener(e -> attemptLogin());
    }

    private void attemptLogin() {
        try {
            UserSession session = auth.login(idField.getText());
            message.setText(" ");
            onSuccess.accept(session);
        } catch (ChocAnException e) {
            message.setText(e.getMessage());
        }
    }

    /** Clears the screen after a logout. */
    void reset() {
        idField.setText("");
        message.setText(" ");
        idField.requestFocusInWindow();
    }
}
