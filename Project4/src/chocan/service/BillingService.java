package chocan.service;

import chocan.data.ChocAnDatabase;
import chocan.model.Member;
import chocan.model.Provider;
import chocan.model.ServiceRecord;
import chocan.model.ServiceType;
import chocan.util.Validation;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * The provider-side workflow: validate a member, look up a service code and
 * "Bill ChocAn" by creating a {@link ServiceRecord}. Only members whose status
 * is valid can be billed.
 *
 * @author [TEAM MEMBER NAME]
 */
public class BillingService {

    private final ChocAnDatabase db;

    public BillingService(ChocAnDatabase db) {
        this.db = db;
    }

    /** Checks a member number and reports whether the member may receive service. */
    public MemberCheck validateMember(String memberNumber) {
        Member member = db.findMember(memberNumber == null ? "" : memberNumber.trim());
        if (member == null) {
            return MemberCheck.INVALID_NUMBER;
        }
        return member.isValid() ? MemberCheck.VALID : MemberCheck.SUSPENDED;
    }

    /** Looks a 6-digit service code up in the provider directory. */
    public ServiceType findService(String code) throws ChocAnException {
        ServiceType service = db.findService(code == null ? "" : code.trim());
        if (service == null) {
            throw new ChocAnException("Service code " + code + " is not in the provider directory.");
        }
        return service;
    }

    /**
     * Records one consultation and saves it.
     *
     * @param serviceDate the day the service was provided (cannot be in the future)
     * @return the stored record (its fee is what ChocAn will pay the provider)
     * @throws ChocAnException if the provider, member (or its status) or service code is not acceptable
     */
    public ServiceRecord recordService(String providerNumber, String memberNumber,
                                       LocalDate serviceDate, String serviceCode, String comments)
            throws ChocAnException {
        Provider provider = db.findProvider(providerNumber);
        if (provider == null) {
            throw new ChocAnException("Provider " + providerNumber + " is not registered.");
        }
        MemberCheck check = validateMember(memberNumber);
        if (check != MemberCheck.VALID) {
            throw new ChocAnException("Cannot bill ChocAn: " + check.getMessage() + ".");
        }
        ServiceType service = findService(serviceCode);
        if (serviceDate == null || serviceDate.isAfter(LocalDate.now())) {
            throw new ChocAnException("The date of service cannot be in the future.");
        }
        String cleanComments = Validation.optionalText("Comments", comments, Validation.COMMENT_MAX);

        Member member = db.findMember(memberNumber.trim());
        ServiceRecord record = new ServiceRecord(
                LocalDateTime.now(), serviceDate, provider, member, service, cleanComments);
        db.addServiceRecord(record);
        db.save();
        return record;
    }
}
