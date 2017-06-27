package au.com.dealsdirect.data.network.model.checkout.getcurrentorder;
/*
 * Created by CodeineBot on 1/6/17.
 */

import com.google.gson.annotations.SerializedName;

public class MyPayDetails {
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

    @SerializedName("Enabled")
    public Boolean enabled;
    @SerializedName("Message")
    public String message;
    @SerializedName("ReasonCode")
    public String reasonCode;
    @SerializedName("TermsAndConditions")
    public Integer termsAndConditions;
    @SerializedName("PaymentConditions")
    public PaymentConditions paymentConditions;
}
