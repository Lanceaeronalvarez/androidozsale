
package au.com.dealsdirect.data.network.model.contacthistory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetContactHistoryResponse {

    @SerializedName("d")
    @Expose
    private GetContactHistoryResponseBody getContactHistoryResponseBody;

    public GetContactHistoryResponseBody getGetContactHistoryResponseBody() {
        return getContactHistoryResponseBody;
    }

    public void setGetContactHistoryResponseBody(
            GetContactHistoryResponseBody getContactHistoryResponseBody) {

        this.getContactHistoryResponseBody = getContactHistoryResponseBody;
    }

}
