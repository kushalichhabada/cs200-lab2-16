package chocan.model;

import chocan.util.Validation;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * One entry in the Provider Directory: a billable service with a 6-digit code,
 * a name and the fee ChocAn pays the provider for it.
 *
 * @author [TEAM MEMBER NAME]
 */
public class ServiceType implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String code;
    private final String name;
    private final BigDecimal fee;

    public ServiceType(String code, String name, BigDecimal fee) {
        this.code = Validation.requireDigits("Service code", code, Validation.SERVICE_CODE_DIGITS);
        this.name = Validation.requireText("Service name", name, Validation.SERVICE_NAME_MAX);
        this.fee = Validation.requireFee(fee);
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getFee() {
        return fee;
    }
}
