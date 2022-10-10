package au.com.dealsdirect.data.network.model.openpay;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OpenpayCapturePaymentRequest {
    @SerializedName("countryID")
    @Expose
    private String countryId;

    @SerializedName("languageID")
    @Expose
    private String languageId;

    @SerializedName("data")
    @Expose
    private Data data;

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    public String getPlanId() {
        if (data != null) {
            return data.planId;
        } else {
            return null;
        }
    }

    public String getOrderId() {
        if (data != null) {
            return data.orderId;
        } else {
            return null;
        }
    }

    public String getStatus() {
        if (data != null) {
            return data.status;
        } else {
            return null;
        }
    }

    public void setData(String planId, String orderId, String status) {
        data = new Data(planId, orderId, status);
    }

    public static class Data {
        @SerializedName("planId")
        @Expose
        private String planId;
        @SerializedName("orderId")
        @Expose
        private String orderId;
        @SerializedName("status")
        @Expose
        private String status;

        public Data(String planId, String orderId, String status) {
            this.planId = planId;
            this.orderId = orderId;
            this.status = status;
        }
    }
}
