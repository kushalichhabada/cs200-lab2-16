package chocan.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * The record a provider creates when billing ChocAn for one consultation.
 * Names and the fee are copied into the record when it is created so that
 * later edits or deletions of a member, provider or service do not change what
 * was actually billed.
 *
 * @author [TEAM MEMBER NAME]
 */
public class ServiceRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private final LocalDateTime receivedAt;
    private final LocalDate serviceDate;
    private final String providerNumber;
    private final String providerName;
    private final String memberNumber;
    private final String memberName;
    private final String serviceCode;
    private final String serviceName;
    private final BigDecimal fee;
    private final String comments;

    public ServiceRecord(LocalDateTime receivedAt, LocalDate serviceDate, Provider provider,
                         Member member, ServiceType service, String comments) {
        this.receivedAt = receivedAt;
        this.serviceDate = serviceDate;
        this.providerNumber = provider.getNumber();
        this.providerName = provider.getName();
        this.memberNumber = member.getNumber();
        this.memberName = member.getName();
        this.serviceCode = service.getCode();
        this.serviceName = service.getName();
        this.fee = service.getFee();
        this.comments = comments == null ? "" : comments;
    }

    /** When the record was entered into the computer (date and time). */
    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    /** The day the service was actually provided. */
    public LocalDate getServiceDate() {
        return serviceDate;
    }

    public String getProviderNumber() {
        return providerNumber;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getMemberNumber() {
        return memberNumber;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public String getServiceName() {
        return serviceName;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public String getComments() {
        return comments;
    }
}
