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

    public static class SearchInfo {

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
        @SerializedName("sort")
        @Expose
        private String sort;

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

        public String getSort() {
            return sort;
        }

        public void setSort(String sort) {
            this.sort = sort;
        }
    }

}
