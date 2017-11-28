
package au.com.dealsdirect.data.network.model.ourpaydashboard.deliveryservice;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetDeliveryServiceResponse {

    @SerializedName("d")
    @Expose
    private D d;

    public D getD() {
        return d;
    }

    public void setD(D d) {
        this.d = d;
    }

}
