
package au.com.dealsdirect.data.network.model.returns.returndetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetReturnDetailsResponse {

    @SerializedName("d")
    @Expose
    private GetReturnDetailsResponseBody getReturnDetailsResponseBody;

    public GetReturnDetailsResponseBody getGetReturnDetailsResponseBody() {
        return getReturnDetailsResponseBody;
    }

    public void setGetReturnDetailsResponseBody(GetReturnDetailsResponseBody getReturnDetailsResponseBody) {
        this.getReturnDetailsResponseBody = getReturnDetailsResponseBody;
    }

}
