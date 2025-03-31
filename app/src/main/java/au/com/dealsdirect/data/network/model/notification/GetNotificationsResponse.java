package au.com.dealsdirect.data.network.model.notification;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetNotificationsResponse {
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("link")
    @Expose
    @Nullable
    private String link;
    @SerializedName("currencySign")
    @Expose
    @Nullable
    private String currencySign;
    @SerializedName("discountLeft")
    @Expose
    @Nullable
    private Double discountLeft;
    @SerializedName("promoCode")
    @Expose
    @Nullable
    private String promoCode;
    @SerializedName("messageType")
    @Expose
    private String messageType;
    @SerializedName("notificationType")
    @Expose
    private String notificationType;

    public String getText() {
        return text;
    }

    @Nullable
    public String getLink() {
        return link;
    }

    @Nullable
    public String getCurrencySign() {
        return currencySign;
    }

    @Nullable
    public Double getDiscountLeft() {
        return discountLeft;
    }

    @Nullable
    public String getPromoCode() {
        return promoCode;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getNotificationType() {
        return notificationType;
    }
}
