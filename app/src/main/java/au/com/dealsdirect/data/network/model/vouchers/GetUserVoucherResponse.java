package au.com.dealsdirect.data.network.model.vouchers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.LegacyBaseResponseValue;

import java.util.ArrayList;

/**
 * dp Created by Admin on 1/5/17.
 */
public class GetUserVoucherResponse {

    //Expiring Soon, Already Spent, Expired

    public enum Status {
        NORMAL,
        EXPIRING_SOON,
        ALREADY_SPENT,
        EXPIRED,
        NEW,
        PENDING
    }

    public static final Status[] AllStatus = new Status[]{
            Status.NORMAL,
            Status.EXPIRING_SOON,
            Status.ALREADY_SPENT,
            Status.EXPIRED,
            Status.NEW,
            Status.PENDING
    };

    public Response d;

    public static class Response {

        @SerializedName(value = "Fullname", alternate = "fullname")
        @Expose
        private String fullname;

        @SerializedName(value = "Activated", alternate = "activated")
        @Expose
        private Boolean activated;

        @SerializedName(value = "FirstPurchase", alternate = "first_purchase")
        @Expose
        private Boolean firstPurchase;

        @SerializedName(value = "DiscountLeft", alternate = "discount_left")
        @Expose
        private String discountLeft;

        @SerializedName(value = "Expired", alternate = "expired")
        @Expose
        private String expired;

        @SerializedName("ExpiringSoon")
        @Expose
        private String expiringSoon;

        @SerializedName("Empty")
        @Expose
        private String empty;

        @SerializedName(value = "DiscountGiven", alternate = "discount_given")
        @Expose
        private String discountGiven;

        @SerializedName(value = "Status", alternate = "status")
        @Expose
        private String status;

        public String getFullname() {
            return fullname;
        }


        public Boolean getActivated() {
            return activated;
        }


        public Boolean getFirstPurchase() {
            return firstPurchase;
        }

        public String getDiscountLeft() {
            return discountLeft;
        }

        public String getExpired() {
            return expired;
        }

        public String getExpiringSoon() {
            return expiringSoon;
        }

        public String getEmpty() {
            return empty;
        }

        public String getDiscountGiven() {
            return discountGiven;
        }

        public String getStatus() {
            return status;
        }
    }

    public Response getValue() {
        return d;
    }

}
