package au.com.dealsdirect.data.network.model.orders;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderReceivedSatisfactionResponse {
    @SerializedName("has_rating")
    @Expose
    private Boolean hasRating;

    public Boolean getHasRating() {
        return hasRating;
    }
}
