package au.com.dealsdirect.data.network.model.vouchers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.LegacyBaseResponseValue;

import java.util.ArrayList;

/**
 * dp Created by Admin on 1/5/17.
 */
public class GetUserVoucherResponse {

    public Response d;

    public static class Response extends LegacyBaseResponseValue{

        @SerializedName("List")
        @Expose
        public ArrayList<Voucher> list;
        public ArrayList<Voucher> getList() {
            return list;
        }

        public void setList(ArrayList<Voucher> list) {
            this.list = list;
        }

    }

    public Response getValue() {
        return d;
    }

    public static class Voucher {

        @SerializedName("Fullname")
        @Expose
        String fullname;

        @SerializedName("Activated")
        @Expose
        Boolean activated;

        @SerializedName("FirstPurchase")
        @Expose
        Boolean firstPurchase;

        @SerializedName("DiscountLeft")
        @Expose
        String discountLeft;

        @SerializedName("Expired")
        @Expose
        String expired;

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

    }
}
