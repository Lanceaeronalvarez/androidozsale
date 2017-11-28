
package au.com.dealsdirect.data.network.model.ourpaydashboard.deliveryservice;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Value {

    @SerializedName("InitialCount")
    @Expose
    private Integer initialCount;
    @SerializedName("RemainingCount")
    @Expose
    private Integer remainingCount;
    @SerializedName("ExpiryDate")
    @Expose
    private String expiryDate;

    public Integer getInitialCount() {
        return initialCount;
    }

    public void setInitialCount(Integer initialCount) {
        this.initialCount = initialCount;
    }

    public Integer getRemainingCount() {
        return remainingCount;
    }

    public void setRemainingCount(Integer remainingCount) {
        this.remainingCount = remainingCount;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

}
