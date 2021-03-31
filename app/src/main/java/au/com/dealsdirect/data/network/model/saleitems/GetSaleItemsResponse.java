package au.com.dealsdirect.data.network.model.saleitems;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.data.cachedresponses.CachableResponse;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;

/**
 * dp Created by smartwave on 6/22/17.
 */

public class GetSaleItemsResponse extends CachableResponse implements Serializable {

    public List<GetCategoryTreeResponse> categories;
    public int count;
    public int page;
    public int pages;
    public int total;
    public String query;
    public ArrayList<SaleItemProduct> products = new ArrayList<>();
    public ArrayList<SaleItemFacet> facets = new ArrayList<>();
    public List<GetCategoryTreeResponse> children;

    public List<GetCategoryTreeResponse> getCategories() {
        return categories;
    }

    public GetSaleItemsResponse() {

    }

    public ArrayList<SaleItemFacet> getFacets() {
        return facets;
    }

    public void setFacets(ArrayList<SaleItemFacet> facets) {
        this.facets = facets;
    }

}
