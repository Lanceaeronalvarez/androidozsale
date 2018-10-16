
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

    public static final String PAYPAL = "paypal";
    public static final String PAYPAL_CREDIT = "paypalcredit";
    public static final String MASTERPASS = "masterpass";
    public static final String VISA_CHECKOUT = "visacheckout";
    public static final String VISA_CHECKOUT_BRAINTREE = "visacheckoutbraintree";
    public static final String PAY = "pay";

    private boolean isPinned;

    private int id;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PaymentMethod &&
                ((PaymentMethod) obj).getPaymentType().equals(paymentType) &&
                ((PaymentMethod) obj).getDescription().equals(description) &&
                ((PaymentMethod) obj).getToken().equals(token) &&
                ((PaymentMethod) obj).getImageUrl().equals(imageUrl);
    }


    public boolean canUseOurPaySelect() {
        switch (paymentType.toLowerCase()) {
            case MASTERPASS:
            case VISA_CHECKOUT:
            case VISA_CHECKOUT_BRAINTREE:
                return false;
            default:
                return true;
        }
    }
}
