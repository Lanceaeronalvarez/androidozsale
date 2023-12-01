
package au.com.dealsdirect.data.network.model.returns.newreturn;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NewReturnOrderDetailResponseBody {

    @SerializedName("d")
    @Expose
    private NewReturnItem newReturnOrderDetailResponse;

    public NewReturnItem getNewReturnOrderDetailResponse() {
        return newReturnOrderDetailResponse;
    }

    public void setNewReturnOrderDetailResponse(NewReturnItem newReturnOrderDetailResponse) {
        this.newReturnOrderDetailResponse = newReturnOrderDetailResponse;
    }
}
