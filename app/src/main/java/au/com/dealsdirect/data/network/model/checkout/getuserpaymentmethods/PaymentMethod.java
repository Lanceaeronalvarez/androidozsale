
package au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PaymentMethod implements Serializable {

    @SerializedName("PaymentType")
    @Expose
    private String paymentType;
    @SerializedName("Description")
    @Expose
    private String description;
    @SerializedName("Token")
    @Expose
    private String token;
    @SerializedName("ImageUrl")
    @Expose
    private String imageUrl;

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
