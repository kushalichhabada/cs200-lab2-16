package chocan.data;

import chocan.model.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ChocAnDatabase implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient Path dataFile;

    private final Map<String, Member> members = new LinkedHashMap<>();
    private final Map<String, Provider> providers = new LinkedHashMap<>();
    private final Map<String, StaffAccount> staff = new LinkedHashMap<>();
    private final Map<String, ServiceType> services = new LinkedHashMap<>();

    private final List<ServiceRecord> serviceRecords = new ArrayList<>();

    private long nextMemberId = 100000003;
    private long nextProviderId = 200000003;

    public ChocAnDatabase(Path dataFile) {
        this.dataFile = dataFile;
    }

    // Load saved database
    public static ChocAnDatabase load(Path dataFile) throws IOException {

        if (!Files.exists(dataFile)) {
            return null;
        }

        try (ObjectInputStream input =
                     new ObjectInputStream(Files.newInputStream(dataFile))) {

            ChocAnDatabase db = (ChocAnDatabase) input.readObject();
            db.dataFile = dataFile;

            return db;

        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to load database.", e);
        }
    }

    // Save database
    public void save() {

    try {
        Path parent = dataFile.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        try (ObjectOutputStream output =
                     new ObjectOutputStream(Files.newOutputStream(dataFile))) {

            output.writeObject(this);
        }

    } catch (IOException e) {
        throw new UncheckedIOException("Failed to save database", e);
    }
}

    // Members
    public Member findMember(String number) {
        return members.get(number);
    }

    public Collection<Member> getMembers() {
        return members.values();
    }

    public void putMember(Member member) {
        members.put(member.getNumber(), member);
    }

    public void removeMember(String number) {
        members.remove(number);
    }

    public String nextMemberNumber() {
        return String.format("%09d", nextMemberId++);
    }

    // Providers
    public Provider findProvider(String number) {
        return providers.get(number);
    }

    public Collection<Provider> getProviders() {
        return providers.values();
    }

    public void putProvider(Provider provider) {
        providers.put(provider.getNumber(), provider);
    }

    public void removeProvider(String number) {
        providers.remove(number);
    }

    public String nextProviderNumber() {
        return String.format("%09d", nextProviderId++);
    }

    // Staff
    public StaffAccount findStaff(String id) {
        return staff.get(id);
    }

    public void putStaff(StaffAccount account) {
        staff.put(account.getId(), account);
    }

    // Services
    public ServiceType findService(String code) {
        return services.get(code);
    }

    public Collection<ServiceType> getServices() {
        return services.values();
    }

    public void putService(ServiceType service) {
        services.put(service.getCode(), service);
    }

    // Service records
    public List<ServiceRecord> getServiceRecords() {
        return serviceRecords;
    }

    public void addServiceRecord(ServiceRecord record) {
        serviceRecords.add(record);
    }
}