package au.com.dealsdirect.data.network.model.returns.createreturn;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReturnReceivedSatisfactionResponse {
    @SerializedName("has_rating")
    @Expose
    private Boolean hasRating;

    public Boolean getHasRating() {
        return hasRating;
    }
}
