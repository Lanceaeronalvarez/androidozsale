
package au.com.dealsdirect.data.network.model.returns.returnorders;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetReturnOrders {

    @SerializedName("d")
    @Expose
    private GetReturnOrdersBody getReturnOrdersBody;

    public GetReturnOrdersBody getGetReturnOrdersBody() {
        return getReturnOrdersBody;
    }

    public void setD(GetReturnOrdersBody getReturnOrdersBody) {
        this.getReturnOrdersBody = getReturnOrdersBody;
    }

}
