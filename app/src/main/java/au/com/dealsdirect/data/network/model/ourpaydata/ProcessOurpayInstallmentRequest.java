package au.com.dealsdirect.data.network.model.ourpaydata;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by MTC on 2019-06-28.
 */
public class ProcessOurpayInstallmentRequest {

    @SerializedName("billingAgreementId")
    @Expose
    private String billingAgreementId;

    @SerializedName("installmentId")
    @Expose
    private String installmentId;

    public String getBillingAgreementId() {
        return billingAgreementId;
    }

    public void setBillingAgreementId(String billingAgreementId) {
        this.billingAgreementId = billingAgreementId;
    }

    public String getInstallmentId() {
        return installmentId;
    }

    public void setInstallmentId(String installmentId) {
        this.installmentId = installmentId;
    }
}
