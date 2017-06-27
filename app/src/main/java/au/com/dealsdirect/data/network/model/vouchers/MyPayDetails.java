
package au.com.dealsdirect.data.network.model.vouchers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MyPayDetails {

    @SerializedName("Enabled")
    @Expose
    private Boolean enabled;
    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("ReasonCode")
    @Expose
    private String reasonCode;
    @SerializedName("TermsAndConditions")
    @Expose
    private Integer termsAndConditions;
    @SerializedName("PaymentConditions")
    @Expose
    private PaymentConditions paymentConditions;

    public Boolean getEnabled() {
        return enabled;
    }

    public String getMessage() {
        return message;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public Integer getTermsAndConditions() {
        return termsAndConditions;
    }

    public PaymentConditions getPaymentConditions() {
        return paymentConditions;
    }
}
