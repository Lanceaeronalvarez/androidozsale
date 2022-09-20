
package au.com.dealsdirect.data.network.model.vouchers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Voucher {

    @SerializedName("ID")
    @Expose
    private String id;
    @SerializedName("Description")
    @Expose
    private String description;
    @SerializedName("DiscountLeft")
    @Expose
    private Double discountLeft;
    @SerializedName("DiscountLeftString")
    @Expose
    private String discountLeftString;
    @SerializedName("Expired")
    @Expose
    private String expired;
    @SerializedName("IsApplied")
    @Expose
    private boolean isApplied;

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public Double getDiscountLeft() {
        return discountLeft;
    }

    public String getDiscountLeftString() {
        return discountLeftString;
    }

    public String getExpired() {
        return expired;
    }

    public boolean isApplied() {
        return isApplied;
    }
}
