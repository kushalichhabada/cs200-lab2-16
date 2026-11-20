package chocan.service;

import chocan.data.ChocAnDatabase;
import chocan.model.PersonDetails;
import chocan.model.Provider;
import java.util.ArrayList;
import java.util.List;

/**
 * Operator functions for providers: add, edit and delete. Every change is
 * saved to disk straight away. A provider's number is also their login ID.
 *
 * @author [TEAM MEMBER NAME]
 */
public class ProviderManager {

    private final ChocAnDatabase db;

    public ProviderManager(ChocAnDatabase db) {
        this.db = db;
    }

    /** Registers a new provider with a freshly assigned number. */
    public Provider addProvider(PersonDetails details) {
        Provider provider = new Provider(db.nextProviderNumber(), details);
        db.putProvider(provider);
        db.save();
        return provider;
    }

    public Provider getProvider(String number) throws ChocAnException {
        Provider provider = db.findProvider(number == null ? "" : number.trim());
        if (provider == null) {
            throw new ChocAnException("No provider found with number " + number + ".");
        }
        return provider;
    }

    /** Replaces the provider's name/address; the number never changes. */
    public Provider updateProvider(String number, PersonDetails details) throws ChocAnException {
        Provider existing = getProvider(number);
        Provider updated = new Provider(existing.getNumber(), details);
        db.putProvider(updated);
        db.save();
        return updated;
    }

    public void deleteProvider(String number) throws ChocAnException {
        Provider existing = getProvider(number);
        db.removeProvider(existing.getNumber());
        db.save();
    }

    public List<Provider> listProviders() {
        return new ArrayList<>(db.getProviders());
    }
}
