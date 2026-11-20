package chocan.gui;

import chocan.AppContext;
import chocan.service.UserSession;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Extra credit: the graphical user interface. A single window switches
 * between the login screen and the screen for the logged-in role (operator,
 * provider or manager). It uses the same service classes as the console UI,
 * so both interfaces behave identically and share the saved data.
 *
 * @author [TEAM MEMBER NAME]
 */
public class ChocAnGui extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final String LOGIN = "login";
    private static final String SESSION = "session";

    private final transient AppContext context;
    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);
    private final LoginPanel loginPanel;
    private JPanel sessionPanel;

    /** Opens the GUI window (on the Swing event thread). */
    public static void launch(AppContext context) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // keep the default look and feel
            }
            new ChocAnGui(context).setVisible(true);
        });
    }

    private ChocAnGui(AppContext context) {
        super("ChocAn - Chocoholics Anonymous");
        this.context = context;
        loginPanel = new LoginPanel(context.getAuth(), this::onLogin);
        root.add(loginPanel, LOGIN);
        add(root);
        setDefaultCloseOperation(EXIT_ON_CLOSE); // data is already saved after every change
        setMinimumSize(new Dimension(900, 600));
        setSize(1000, 650);
        setLocationRelativeTo(null);
    }

    private void onLogin(UserSession session) {
        if (sessionPanel != null) {
            root.remove(sessionPanel);
        }
        sessionPanel = new JPanel(new BorderLayout());
        sessionPanel.add(buildHeader(session), BorderLayout.NORTH);
        sessionPanel.add(buildContent(session), BorderLayout.CENTER);
        root.add(sessionPanel, SESSION);
        cards.show(root, SESSION);
        root.revalidate();
        root.repaint();
    }

    private JPanel buildHeader(UserSession session) {
        JLabel welcome = new JLabel("Welcome, " + session.getName() + " (" + session.getRole().getLabel()
                + ", ID " + session.getId() + ")");
        welcome.setFont(welcome.getFont().deriveFont(Font.BOLD, 15f));
        JButton logout = new JButton("Log out");
        logout.addActionListener(e -> logout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        header.add(welcome, BorderLayout.WEST);
        header.add(logout, BorderLayout.EAST);
        return header;
    }

    private JPanel buildContent(UserSession session) {
        switch (session.getRole()) {
            case OPERATOR:
                return new OperatorPanel(context);
            case PROVIDER:
                return new ProviderPanel(context, session);
            case MANAGER:
                return new ManagerPanel(context);
            default:
                throw new IllegalStateException("Unknown role " + session.getRole());
        }
    }

    private void logout() {
        cards.show(root, LOGIN);
        loginPanel.reset();
    }
}
