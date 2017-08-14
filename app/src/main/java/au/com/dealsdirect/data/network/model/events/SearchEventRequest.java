package au.com.dealsdirect.data.network.model.events;
/*
 * Created by CodeineBot on 8/8/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SearchEventRequest {

    @SerializedName("searchInfo")
    @Expose
    private SearchInfo searchInfo;
    @SerializedName("eventType")
    @Expose
    private Integer eventType;
    @SerializedName("frontEndInfo")
    @Expose
    private FrontEndInfo frontEndInfo;
    @SerializedName("visitorInfo")
    @Expose
    private VisitorInfo visitorInfo;

    public SearchInfo getSearchInfo() {
        return searchInfo;
    }

    public void setSearchInfo(SearchInfo searchInfo) {
        this.searchInfo = searchInfo;
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

    public static class FrontEndInfo {

        @SerializedName("frontEnd")
        @Expose
        private String frontEnd;
        @SerializedName("uiVersion")
        @Expose
        private String uiVersion;
        @SerializedName("osVersion")
        @Expose
        private String osVersion;

        public String getFrontEnd() {
            return frontEnd;
        }

        public void setFrontEnd(String frontEnd) {
            this.frontEnd = frontEnd;
        }

        public String getUiVersion() {
            return uiVersion;
        }

        public void setUiVersion(String uiVersion) {
            this.uiVersion = uiVersion;
        }

        public String getOsVersion() {
            return osVersion;
        }

        public void setOsVersion(String osVersion) {
            this.osVersion = osVersion;
        }

    }

    public class SearchInfo {

        @SerializedName("resultsCount")
        @Expose
        private Integer resultsCount;
        @SerializedName("categoriesCount")
        @Expose
        private Integer categoriesCount;
        @SerializedName("brandsCount")
        @Expose
        private Integer brandsCount;
        @SerializedName("sizesCount")
        @Expose
        private Integer sizesCount;
        @SerializedName("minPrice")
        @Expose
        private Integer minPrice;
        @SerializedName("maxPrice")
        @Expose
        private Integer maxPrice;
        @SerializedName("searchTerm")
        @Expose
        private String searchTerm;
        @SerializedName("categories")
        @Expose
        private String categories;
        @SerializedName("filters")
        @Expose
        private String filters;
        @SerializedName("operation")
        @Expose
        private Integer operation;

        public Integer getResultsCount() {
            return resultsCount;
        }

        public void setResultsCount(Integer resultsCount) {
            this.resultsCount = resultsCount;
        }

        public Integer getCategoriesCount() {
            return categoriesCount;
        }

        public void setCategoriesCount(Integer categoriesCount) {
            this.categoriesCount = categoriesCount;
        }

        public Integer getBrandsCount() {
            return brandsCount;
        }

        public void setBrandsCount(Integer brandsCount) {
            this.brandsCount = brandsCount;
        }

        public Integer getSizesCount() {
            return sizesCount;
        }

        public void setSizesCount(Integer sizesCount) {
            this.sizesCount = sizesCount;
        }

        public Integer getMinPrice() {
            return minPrice;
        }

        public void setMinPrice(Integer minPrice) {
            this.minPrice = minPrice;
        }

        public Integer getMaxPrice() {
            return maxPrice;
        }

        public void setMaxPrice(Integer maxPrice) {
            this.maxPrice = maxPrice;
        }

        public String getSearchTerm() {
            return searchTerm;
        }

        public void setSearchTerm(String searchTerm) {
            this.searchTerm = searchTerm;
        }

        public String getCategories() {
            return categories;
        }

        public void setCategories(String categories) {
            this.categories = categories;
        }

        public String getFilters() {
            return filters;
        }

        public void setFilters(String filters) {
            this.filters = filters;
        }

        public Integer getOperation() {
            return operation;
        }

        public void setOperation(Integer operation) {
            this.operation = operation;
        }

    }

    public static class VisitorInfo {

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

}
