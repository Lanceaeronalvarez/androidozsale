package au.com.dealsdirect.data.network.model.zippay;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ZipPayConfirmNzOrderRequest {
    @SerializedName("countryID")
    @Expose
    private String countryId;

    @SerializedName("languageID")
    @Expose
    private String languageId;

    @SerializedName("data")
    @Expose
    private Data data;

    public ZipPayConfirmNzOrderRequest(String countryId,
                                       String languageId,
                                       String paymentStatus,
                                       String zipOrderId,
                                       String token) {
        this.countryId = countryId;
        this.languageId = languageId;
        this.data = new Data(paymentStatus, zipOrderId, token);
    }

    public static class Data {
        @SerializedName("paymentStatus")
        @Expose
        private String paymentStatus;
        @SerializedName("zipOrderId")
        @Expose
        private String zipOrderId;
        @SerializedName("token")
        @Expose
        private String token;

        public Data(String paymentStatus, String zipOrderId, String token) {
            this.paymentStatus = paymentStatus;
            this.zipOrderId = zipOrderId;
            this.token = token;
        }
    }
}
