package au.com.dealsdirect.data.cachedresponses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import au.com.dealsdirect.data.network.model.sorting.SortingResponse;

public class ListOfSortingResponses extends CachableResponse {

    @SerializedName("responses")
    @Expose
    private List<SortingResponse> responses;

    public ListOfSortingResponses(List<SortingResponse> responses) {
        this.responses = responses;
    }

    public List<SortingResponse> getResponses() {
        return responses;
    }

    public void setResponses(List<SortingResponse> responses) {
        this.responses = responses;
    }
}
