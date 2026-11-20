package chocan.gui;

import chocan.model.Member;
import chocan.model.Person;
import chocan.service.ChocAnException;
import chocan.service.MemberManager;
import java.util.List;

/**
 * The operator's member tab: lists members and lets the operator add, edit
 * (including suspend / reinstate) and delete them via {@link MemberManager}.
 *
 * @author [TEAM MEMBER NAME]
 */
class MemberTablePanel extends PersonTablePanel {

    private static final long serialVersionUID = 1L;

    private final transient MemberManager members;

    MemberTablePanel(MemberManager members) {
        super("member", new String[]{"Number", "Name", "Address", "City, State Zip", "Status"});
        this.members = members;
        refresh();
    }

    @Override
    protected List<? extends Person> loadAll() {
        return members.listMembers();
    }

    @Override
    protected Object[] toRow(Person person) {
        Member m = (Member) person;
        return new Object[]{m.getNumber(), m.getName(), m.getStreetAddress(), m.getCityStateZip(),
                m.getStatus().getLabel()};
    }

    @Override
    protected void addNew() {
        PersonDialog result = PersonDialog.show(this, "Add member", null, null);
        if (result != null) {
            Member added = members.addMember(result.getDetails());
            GuiUtil.info(this, "Member added. Assigned member number: " + added.getNumber());
        }
    }

    @Override
    protected void edit(String number) throws ChocAnException {
        Member existing = members.getMember(number);
        PersonDialog result = PersonDialog.show(this, "Edit member " + number,
                existing.getDetails(), existing.getStatus());
        if (result != null) {
            members.updateMember(number, result.getDetails(), result.getStatus());
        }
    }

    @Override
    protected void remove(String number) throws ChocAnException {
        members.deleteMember(number);
    }
}
