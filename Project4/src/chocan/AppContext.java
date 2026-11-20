package chocan;

import chocan.data.ChocAnDatabase;
import chocan.data.SeedData;
import chocan.service.AccountingProcedure;
import chocan.service.AuthService;
import chocan.service.BillingService;
import chocan.service.MemberManager;
import chocan.service.ProviderDirectory;
import chocan.service.ProviderManager;
import chocan.service.ReportGenerator;
import chocan.service.ReportWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Builds the database and every service object once and hands them out, so the
 * console interface and the GUI share exactly the same setup code.
 *
 * @author [TEAM MEMBER NAME]
 */
public class AppContext {

    private final ChocAnDatabase db;
    private final boolean createdSampleData;
    private final AuthService auth;
    private final MemberManager members;
    private final ProviderManager providers;
    private final BillingService billing;
    private final ReportGenerator reports;
    private final AccountingProcedure accounting;
    private final ProviderDirectory directory;

    private AppContext(ChocAnDatabase db, boolean createdSampleData, Path reportDir) {
        this.db = db;
        this.createdSampleData = createdSampleData;
        ReportWriter writer = new ReportWriter(reportDir);
        this.auth = new AuthService(db);
        this.members = new MemberManager(db);
        this.providers = new ProviderManager(db);
        this.billing = new BillingService(db);
        this.reports = new ReportGenerator(db);
        this.accounting = new AccountingProcedure(db, reports, writer);
        this.directory = new ProviderDirectory(db, writer);
    }

    /**
     * Loads the saved data file, or creates it with sample data if it does not exist.
     *
     * @param reset if true, any saved data is deleted first
     */
    public static AppContext open(Path dataFile, Path reportDir, boolean reset) throws IOException {
        if (reset) {
            Files.deleteIfExists(dataFile);
        }
        ChocAnDatabase db = ChocAnDatabase.load(dataFile);
        boolean created = false;
        if (db == null) {
            db = new ChocAnDatabase(dataFile);
            SeedData.populate(db);
            db.save();
            created = true;
        }
        return new AppContext(db, created, reportDir);
    }

    /** True if no saved data existed and the sample data was just created. */
    public boolean isSampleDataCreated() {
        return createdSampleData;
    }

    public ChocAnDatabase getDatabase() {
        return db;
    }

    public AuthService getAuth() {
        return auth;
    }

    public MemberManager getMembers() {
        return members;
    }

    public ProviderManager getProviders() {
        return providers;
    }

    public BillingService getBilling() {
        return billing;
    }

    public ReportGenerator getReports() {
        return reports;
    }

    public AccountingProcedure getAccounting() {
        return accounting;
    }

    public ProviderDirectory getDirectory() {
        return directory;
    }
}
