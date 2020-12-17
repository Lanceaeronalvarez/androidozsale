package au.com.dealsdirect.data.network.model.events;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by MTC on 2019-10-03.
 */
public class CommonCheckoutRequest {
    @SerializedName("operation")
    @Expose
    private Integer operation;
    @SerializedName("eventType")
    @Expose
    private Integer eventType;
    @SerializedName("frontEndInfo")
    @Expose
    private FrontEndInfo frontEndInfo;
    @SerializedName("visitorInfo")
    @Expose
    private VisitorInfo visitorInfo;
    @SerializedName("result")
    @Expose
    private Integer result;
    @SerializedName("errorDescription")
    @Expose
    private String errorDescription;
    @SerializedName("isGuestCheckout")
    @Expose
    private boolean isGuestCheckout;

    public Integer getOperation() {
        return operation;
    }

    public void setOperation(Integer operation) {
        this.operation = operation;
    }

    public Integer getEventType() {
        return eventType;
    }

    public void setEventType(Integer eventType) {
        this.eventType = eventType;
    }

    public FrontEndInfo getFrontEndInfo() {
        return frontEndInfo;
    }

    public void setFrontEndInfo(FrontEndInfo frontEndInfo) {
        this.frontEndInfo = frontEndInfo;
    }

    public VisitorInfo getVisitorInfo() {
        return visitorInfo;
    }

    public void setVisitorInfo(VisitorInfo visitorInfo) {
        this.visitorInfo = visitorInfo;
    }

    public Integer isResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public String isErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public boolean isGuestCheckout() {
        return isGuestCheckout;
    }

    public void setGuestCheckout(boolean guestCheckout) {
        isGuestCheckout = guestCheckout;
    }
}
