package au.com.dealsdirect.data.network.model.events;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MTC on 3/4/19.
 */

public class VisitorInfo {
    @SerializedName("visitorId")
    @Expose
    private String visitorId;
    @SerializedName("userCohorts")
    @Expose
    private List<String> userCohorts = new ArrayList<>();
    @SerializedName("userGroup")
    @Expose
    private String userGroup;
    @SerializedName("company")
    @Expose
    private String company;
    @SerializedName("region")
    @Expose
    private String region;
    @SerializedName("userId")
    @Expose
    private String userId;

    public String getVisitorId() {
        return visitorId;
    }

    public void setVisitorId(String visitorId) {
        this.visitorId = visitorId;
    }

    public List<String> getUserCohorts() {
        return userCohorts;
    }

    public void setUserCohorts(List<String> userCohorts) {
        this.userCohorts = userCohorts;
    }

    public String getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
