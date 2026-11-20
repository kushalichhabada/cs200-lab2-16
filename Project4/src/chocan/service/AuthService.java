package chocan.service;

import chocan.data.ChocAnDatabase;
import chocan.model.Provider;
import chocan.model.Role;
import chocan.model.StaffAccount;

/**
 * Login/validation. Every user types a 9-digit ID: staff IDs identify
 * operators and managers, and a provider's own number logs that provider in.
 *
 * @author [TEAM MEMBER NAME]
 */
public class AuthService {

    private final ChocAnDatabase db;

    public AuthService(ChocAnDatabase db) {
        this.db = db;
    }

    /**
     * Checks the ID and works out the role.
     *
     * @throws ChocAnException if the ID is badly formatted or not recognised
     */
    public UserSession login(String id) throws ChocAnException {
        String trimmed = id == null ? "" : id.trim();
        if (!trimmed.matches("\\d{9}")) {
            throw new ChocAnException("An ID is exactly 9 digits.");
        }
        StaffAccount staff = db.findStaff(trimmed);
        if (staff != null) {
            return new UserSession(staff.getId(), staff.getName(), staff.getRole());
        }
        Provider provider = db.findProvider(trimmed);
        if (provider != null) {
            return new UserSession(provider.getNumber(), provider.getName(), Role.PROVIDER);
        }
        throw new ChocAnException("ID " + trimmed + " is not recognised.");
    }
}
