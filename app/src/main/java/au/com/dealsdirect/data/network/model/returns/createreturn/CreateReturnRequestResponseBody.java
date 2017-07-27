package au.com.dealsdirect.data.network.model.returns.createreturn;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * dp Created by Admin on 7/25/17.
 */
public class CreateReturnRequestResponseBody {

    @SerializedName("d")
    @Expose
    private CreateReturnRequestResponse createReturnRequestResponse;

    public CreateReturnRequestResponse getCreateReturnRequestResponse() {
        return createReturnRequestResponse;
    }

    public void setCreateReturnRequestResponse(CreateReturnRequestResponse createReturnRequestResponse) {
        this.createReturnRequestResponse = createReturnRequestResponse;
    }

}
