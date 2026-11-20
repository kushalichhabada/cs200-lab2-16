package chocan.gui;

import chocan.model.Person;
import chocan.service.ChocAnException;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 * A table of people with Add / Edit / Delete buttons. The member and provider
 * tabs share this layout; subclasses supply the data and the three actions.
 * Subclasses must call {@link #refresh()} at the end of their constructor.
 *
 * @author [TEAM MEMBER NAME]
 */
abstract class PersonTablePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final String noun;
    private final DefaultTableModel model;
    private final JTable table;

    /** @param columns column titles; the first column must be the person's number and the second their name */
    PersonTablePanel(String noun, String[] columns) {
        super(new BorderLayout());
        this.noun = noun;
        this.model = new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);

        JButton add = new JButton("Add " + noun);
        JButton edit = new JButton("Edit selected");
        JButton delete = new JButton("Delete selected");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(add);
        buttons.add(edit);
        buttons.add(delete);

        add(buttons, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        add.addActionListener(e -> GuiUtil.run(this, () -> {
            addNew();
            refresh();
        }));
        edit.addActionListener(e -> GuiUtil.run(this, () -> {
            String number = selectedNumber();
            if (number != null) {
                edit(number);
                refresh();
            }
        }));
        delete.addActionListener(e -> GuiUtil.run(this, () -> {
            String number = selectedNumber();
            if (number != null && GuiUtil.confirm(this, "Permanently delete " + noun + " " + number
                    + " (" + selectedName() + ")?")) {
                remove(number);
                refresh();
            }
        }));
    }

    protected abstract List<? extends Person> loadAll();

    protected abstract Object[] toRow(Person person);

    protected abstract void addNew() throws ChocAnException;

    protected abstract void edit(String number) throws ChocAnException;

    protected abstract void remove(String number) throws ChocAnException;

    /** Reloads the table from the data store. */
    final void refresh() {
        model.setRowCount(0);
        for (Person person : loadAll()) {
            model.addRow(toRow(person));
        }
    }

    private String selectedNumber() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            GuiUtil.info(this, "Select a " + noun + " in the table first.");
            return null;
        }
        return (String) model.getValueAt(table.convertRowIndexToModel(viewRow), 0);
    }

    private String selectedName() {
        int viewRow = table.getSelectedRow();
        return (String) model.getValueAt(table.convertRowIndexToModel(viewRow), 1);
    }
}
