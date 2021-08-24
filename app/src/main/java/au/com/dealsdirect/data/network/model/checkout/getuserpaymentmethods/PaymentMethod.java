
package au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stripe.android.model.CardBrand;

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
    @SerializedName("ProviderType")
    @Expose
    private String providerType;

    public static final String PAYPAL = "paypal";
    public static final String PAYPAL_CREDIT = "paypalcredit";
    public static final String MASTERPASS = "masterpass";
    public static final String VISA_CHECKOUT = "visacheckout";
    public static final String VISA_CHECKOUT_BRAINTREE = "visacheckoutbraintree";
    public static final String PAY = "pay";
    public static final String STRIPE = "stripe";

    private boolean isPinned;

    private int id;

    @NonNull
    public CardBrand getCardBrand() {
        return CardBrand.Companion.fromCode(paymentType);
    }
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

    public String getProviderType() {
        return providerType;
    }

    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PaymentMethod &&
                ((PaymentMethod) obj).getToken() != null &&
                ((PaymentMethod) obj).getToken().equals(token);
    }


    public boolean canUseOurPaySelect() {
        switch (paymentType.toLowerCase()) {
            case MASTERPASS:
                return false;
            default:
                return true;
        }
    }
}
