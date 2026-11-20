package chocan.gui;

import chocan.model.Person;
import chocan.model.Provider;
import chocan.service.ChocAnException;
import chocan.service.ProviderManager;
import java.util.List;

/**
 * The operator's provider tab: lists providers and lets the operator add,
 * edit and delete them via {@link ProviderManager}.
 *
 * @author [TEAM MEMBER NAME]
 */
class ProviderTablePanel extends PersonTablePanel {

    private static final long serialVersionUID = 1L;

    private final transient ProviderManager providers;

    ProviderTablePanel(ProviderManager providers) {
        super("provider", new String[]{"Number", "Name", "Address", "City, State Zip"});
        this.providers = providers;
        refresh();
    }

    @Override
    protected List<? extends Person> loadAll() {
        return providers.listProviders();
    }

    @Override
    protected Object[] toRow(Person person) {
        return new Object[]{person.getNumber(), person.getName(), person.getStreetAddress(),
                person.getCityStateZip()};
    }

    @Override
    protected void addNew() {
        PersonDialog result = PersonDialog.show(this, "Add provider", null, null);
        if (result != null) {
            Provider added = providers.addProvider(result.getDetails());
            GuiUtil.info(this, "Provider added. Assigned provider number (also their login ID): "
                    + added.getNumber());
        }
    }

    @Override
    protected void edit(String number) throws ChocAnException {
        Provider existing = providers.getProvider(number);
        PersonDialog result = PersonDialog.show(this, "Edit provider " + number,
                existing.getDetails(), null);
        if (result != null) {
            providers.updateProvider(number, result.getDetails());
        }
    }

    @Override
    protected void remove(String number) throws ChocAnException {
        providers.deleteProvider(number);
    }
}
