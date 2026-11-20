package chocan.service;

import chocan.data.ChocAnDatabase;
import chocan.model.ServiceType;
import chocan.util.Formats;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The Provider Directory: an alphabetical list of every service with its code
 * and fee. A provider can request it from the menu; a copy is also written to
 * the reports folder (standing in for "sending" it to the provider).
 *
 * @author [TEAM MEMBER NAME]
 */
public class ProviderDirectory {

    private static final int WIDTH = 50;

    private final ChocAnDatabase db;
    private final ReportWriter writer;

    public ProviderDirectory(ChocAnDatabase db, ReportWriter writer) {
        this.db = db;
        this.writer = writer;
    }

    /** The services sorted alphabetically by name. */
    public List<ServiceType> alphabeticalServices() {
        List<ServiceType> list = new ArrayList<>(db.getServices());
        list.sort(Comparator.comparing(ServiceType::getName, String.CASE_INSENSITIVE_ORDER));
        return list;
    }

    /** The formatted directory text. */
    public String generate() {
        String nl = System.lineSeparator();
        StringBuilder sb = new StringBuilder();
        sb.append(Formats.line('=', WIDTH)).append(nl);
        sb.append("PROVIDER DIRECTORY (alphabetical by service)").append(nl);
        sb.append(Formats.line('-', WIDTH)).append(nl);
        sb.append(String.format("%-22s %-10s %12s%n", "Service", "Code", "Fee"));
        sb.append(Formats.line('-', WIDTH)).append(nl);
        for (ServiceType service : alphabeticalServices()) {
            sb.append(String.format("%-22s %-10s %12s%n", service.getName(), service.getCode(),
                    Formats.money(service.getFee())));
        }
        sb.append(Formats.line('=', WIDTH)).append(nl);
        return sb.toString();
    }

    /** Writes the directory to the provider's own file and returns its path. */
    public Path sendTo(String providerName) throws IOException {
        return writer.write("Provider_Directory_" + providerName, generate());
    }
}
