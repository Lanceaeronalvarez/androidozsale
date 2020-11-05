package au.com.dealsdirect.data.network.model.contacthistory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by MTC on 2020-05-05.
 */
public class TicketSatisfactionResponse {

    @Expose
    @SerializedName("has_rating")
    private boolean hasRating;

    public boolean isHasRating() {
        return hasRating;
    }

    public void setHasRating(boolean hasRating) {
        this.hasRating = hasRating;
    }
}
