package au.com.dealsdirect.data.network.model.saleitems;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * dp  by Admin on 6/22/17.
 */

public class GetSaleItemsRequest {
    @Expose
    @SerializedName("q")
    private String query;

    @Expose
    @SerializedName("pn")
    private String pageNumber;

    @Expose
    @SerializedName("ps")
    private String pageSize;

    @Expose
    @SerializedName("c")
    private String categoryKey;

    @Expose
    @SerializedName("ff")
    private String facetFilter;

    @Expose
    @SerializedName("sa")
    private String sorting;

    private boolean hasFilters;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(String pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getCategoryKey() {
        return categoryKey;
    }

    public void setCategoryKey(String categoryKey) {
        this.categoryKey = categoryKey;
    }

    public String getFacetFilter() {
        return facetFilter;
    }

    public void setFacetFilter(String facetFilter) {
        this.facetFilter = facetFilter;
    }

    public String getSorting() {
        return sorting;
    }

    public void setSorting(String sorting) {
        this.sorting = sorting;
    }

    public boolean hasFilters() {
        return hasFilters;
    }

    public void setHasFilters(boolean val){
        this.hasFilters = val;
    }
}
