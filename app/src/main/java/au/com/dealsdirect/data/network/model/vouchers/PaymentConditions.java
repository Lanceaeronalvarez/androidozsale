
package au.com.dealsdirect.data.network.model.vouchers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentConditions {

    @SerializedName("MaxAmountThreshold")
    @Expose
    private Double maxAmountThreshold;
    @SerializedName("MinAmountThreshold")
    @Expose
    private Double minAmountThreshold;

    public Double getMaxAmountThreshold() {
        return maxAmountThreshold;
    }

    public Double getMinAmountThreshold() {
        return minAmountThreshold;
    }
}
