package au.com.dealsdirect.data.network.model.contacthistory;

import com.google.gson.annotations.SerializedName;

/**
 * Created by MTC on 2020-05-05.
 */
public class TicketSatisfactionRequest {

    @SerializedName("global")
    private int global;

    public TicketSatisfactionRequest(int global) {
        this.global = global;
    }

    public int getGlobal() {
        return global;
    }

    public void setGlobal(int global) {
        this.global = global;
    }
}
