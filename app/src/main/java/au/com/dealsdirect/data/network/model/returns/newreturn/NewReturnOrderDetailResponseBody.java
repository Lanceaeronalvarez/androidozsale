
package au.com.dealsdirect.data.network.model.returns.newreturn;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NewReturnOrderDetailResponseBody {

    @SerializedName("d")
    @Expose
    private NewReturnOrderDetailResponse newReturnOrderDetailResponse;

    public NewReturnOrderDetailResponse getNewReturnOrderDetailResponse() {
        return newReturnOrderDetailResponse;
    }

    public void setNewReturnOrderDetailResponse(NewReturnOrderDetailResponse newReturnOrderDetailResponse) {
        this.newReturnOrderDetailResponse = newReturnOrderDetailResponse;
    }
}
