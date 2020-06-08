package au.com.dealsdirect.data.cachedresponses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListOfCachableResponse extends CachableResponse {

    @SerializedName("responses")
    @Expose
    private List<?> responses;

    public ListOfCachableResponse(List<?> responses) {
        this.responses = responses;
    }

    public List<?> getResponses() {
        return responses;
    }

    public void setResponses(List<?> responses) {
        this.responses = responses;
    }
}
