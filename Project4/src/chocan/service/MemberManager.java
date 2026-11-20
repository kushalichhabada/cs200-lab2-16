package chocan.service;

import chocan.data.ChocAnDatabase;
import chocan.model.Member;
import chocan.model.MemberStatus;
import chocan.model.PersonDetails;
import java.util.ArrayList;
import java.util.List;

/**
 * Operator functions for members: add, edit (including suspending or
 * reinstating) and delete. Every change is saved to disk straight away.
 *
 * @author [TEAM MEMBER NAME]
 */
public class MemberManager {

    private final ChocAnDatabase db;

    public MemberManager(ChocAnDatabase db) {
        this.db = db;
    }

    /** Registers a new member with a freshly assigned number, status VALID. */
    public Member addMember(PersonDetails details) {
        Member member = new Member(db.nextMemberNumber(), details, MemberStatus.VALID);
        db.putMember(member);
        db.save();
        return member;
    }

    public Member getMember(String number) throws ChocAnException {
        Member member = db.findMember(number == null ? "" : number.trim());
        if (member == null) {
            throw new ChocAnException("No member found with number " + number + ".");
        }
        return member;
    }

    /** Replaces the member's details and status; the number never changes. */
    public Member updateMember(String number, PersonDetails details, MemberStatus status)
            throws ChocAnException {
        Member existing = getMember(number);
        Member updated = new Member(existing.getNumber(), details, status);
        db.putMember(updated);
        db.save();
        return updated;
    }

    public void deleteMember(String number) throws ChocAnException {
        Member existing = getMember(number);
        db.removeMember(existing.getNumber());
        db.save();
    }

    public List<Member> listMembers() {
        return new ArrayList<>(db.getMembers());
    }
}
