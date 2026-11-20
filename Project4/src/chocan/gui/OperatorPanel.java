package chocan.gui;

import chocan.AppContext;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * GUI screen for operators: member management, provider management and the
 * main accounting procedure, one tab each.
 *
 * @author [TEAM MEMBER NAME]
 */
class OperatorPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    OperatorPanel(AppContext context) {
        super(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Members", new MemberTablePanel(context.getMembers()));
        tabs.addTab("Providers", new ProviderTablePanel(context.getProviders()));
        tabs.addTab("Accounting", new AccountingPanel(context.getAccounting()));
        add(tabs, BorderLayout.CENTER);
    }
}
