package au.com.dealsdirect.data.network.model.saleitems;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * dp Created by smartwave on 6/22/17.
 */

public class GetSaleItemsResponse implements Serializable {

    public int count;
    public int page;
    public int pages;
    public int total;
    public String query;
    public ArrayList<Products> products = new ArrayList<>();
    public ArrayList<Facets> facets = new ArrayList<>();

    public GetSaleItemsResponse() {

    }

    public class Products {
        String id;
        String name;
        String description;

        String labelText;
        Price price;
        Price originalPrice;
        ArrayList<String> images;
        boolean isAvailable;
        String priceDescription;
        ArrayList<String> categories;
        ArrayList<Sku> skus;
        String seoIdentifier;

        public String getSeoIdentifier() {
            return seoIdentifier;
        }

        public ArrayList<Sku> getSkus() {
            return skus;
        }

        public ArrayList<String> getCategories() {
            return categories;
        }

        public String getProductId() {
            return id;
        }

        public String getProductName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public ArrayList<String> getImages() {
            return images;
        }

        public boolean isAvailable() {
            return isAvailable;
        }

        public String getPriceDescription() {
            return priceDescription;
        }

        public Price getOriginalPrice() {
            return originalPrice;
        }

        public Price getPrice() {
            return price;
        }

        public String getLabelText() {
            return labelText;
        }
    }

    public class Facets {
        public String getFacetName() {
            return facetName;
        }

        public ArrayList<Values> getFacetValues() {
            return facetValues;
        }

        @SerializedName("name")
        @Expose
        public String facetName;
        @Expose
        @SerializedName("values")
        public ArrayList<Values> facetValues;

    }

    public class Values {
        public String getValue() {
            return value;
        }

        public int getCount() {
            return count;
        }

        public String value;
        public int count;
    }

    public class Price {
        public String currency;

        public double getValue() {
            return value;
        }

        public String getCurrency() {
            return currency;
        }

        public double value;
    }

    public class Sku {
        @SerializedName("id")
        @Expose
        protected String id;

        public String getId() {
            return id;
        }
    }

}
