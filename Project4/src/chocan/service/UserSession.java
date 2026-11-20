package chocan.service;

import chocan.model.Role;

/**
 * Who is currently logged in: the ID they typed, their name and their role.
 * Created by {@link AuthService} and passed to the menu for that role.
 *
 * @author [TEAM MEMBER NAME]
 */
public final class UserSession {

    private final String id;
    private final String name;
    private final Role role;

    public UserSession(String id, String name, Role role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }
}
