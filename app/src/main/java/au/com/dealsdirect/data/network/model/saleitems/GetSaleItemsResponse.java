package au.com.dealsdirect.data.network.model.saleitems;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.data.cachedresponses.CachableResponse;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.Attributes;
import au.com.dealsdirect.data.network.model.saleitemdetails.OriginalPrice;

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
    public ArrayList<Products> products = new ArrayList<>();
    public ArrayList<Facets> facets = new ArrayList<>();
    public List<GetCategoryTreeResponse> children;

    public List<GetCategoryTreeResponse> getCategories() {
        return categories;
    }

    public GetSaleItemsResponse() {

    }

    public class Products {
        String id;
        String name;
        String brandName;
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
        boolean isSoldOut;

        @SerializedName("salePercentOff")
        @Expose
        private int salePercentOff;
        @SerializedName("isFreeDelivery")
        @Expose
        private boolean isFreeDelivery;
        @SerializedName("salePrice")
        @Expose
        private SalePrice salePrice;
        @SerializedName("salePercentOffText")
        @Expose
        private String salePercentOffText;

        public String getSalePercentOffText() {
            return salePercentOffText;
        }

        public SalePrice getSalePrice() {
            return salePrice;
        }

        public int getSalePercentOff() {
            return salePercentOff;
        }

        public boolean getFreeDelivery() {
            return isFreeDelivery;
        }

        public boolean isSoldOut() {
            return isSoldOut;
        }

        public void setSoldOut(boolean soldOut) {
            isSoldOut = soldOut;
        }

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

        public String getBrandName() {
            return brandName;
        }

        public void setBrandName(String brandName) {
            this.brandName = brandName;
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
        private String currency;
        private double value;

        public double getValue() {
            return value;
        }

        public String getCurrency() {
            return currency;
        }
    }

    public class SalePrice extends Price { }

    public class Sku {
        @SerializedName("id")
        @Expose
        protected String id;

        @SerializedName("attributes")
        @Expose
        private Attributes attributes;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("labelText")
        @Expose
        private String labelText;
        @SerializedName("price")
        @Expose
        private au.com.dealsdirect.data.network.model.saleitemdetails.Price price;
        @SerializedName("originalPrice")
        @Expose
        private OriginalPrice originalPrice;
        @SerializedName("quantity")
        @Expose
        private int quantity;
        @SerializedName("images")
        @Expose
        private List<String> images = null;
        @SerializedName("brandName")
        @Expose
        private String brandName;
        @SerializedName("countryOfOrigin")
        @Expose
        private String countryOfOrigin;
        @SerializedName("shippingInformation")
        @Expose
        private String shippingInformation;
        @SerializedName("returnPolicy")
        @Expose
        private String returnPolicy;
        @SerializedName("pricing")
        @Expose
        private String pricing;
        @SerializedName("seoUrl")
        @Expose
        private String seoUrl;
        @SerializedName("seoIdentifier")
        @Expose
        private String seoIdentifier;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Attributes getAttributes() {
            return attributes;
        }

        public void setAttributes(Attributes attributes) {
            this.attributes = attributes;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLabelText() {
            return labelText;
        }

        public void setLabelText(String labelText) {
            this.labelText = labelText;
        }

        public au.com.dealsdirect.data.network.model.saleitemdetails.Price getPrice() {
            return price;
        }

        public void setPrice(au.com.dealsdirect.data.network.model.saleitemdetails.Price price) {
            this.price = price;
        }

        public OriginalPrice getOriginalPrice() {
            return originalPrice;
        }

        public void setOriginalPrice(OriginalPrice originalPrice) {
            this.originalPrice = originalPrice;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }

        public String getBrandName() {
            return brandName;
        }

        public void setBrandName(String brandName) {
            this.brandName = brandName;
        }

        public String getCountryOfOrigin() {
            return countryOfOrigin;
        }

        public void setCountryOfOrigin(String countryOfOrigin) {
            this.countryOfOrigin = countryOfOrigin;
        }

        public String getShippingInformation() {
            return shippingInformation;
        }

        public void setShippingInformation(String shippingInformation) {
            this.shippingInformation = shippingInformation;
        }

        public String getReturnPolicy() {
            return returnPolicy;
        }

        public void setReturnPolicy(String returnPolicy) {
            this.returnPolicy = returnPolicy;
        }

        public String getPricing() {
            return pricing;
        }

        public void setPricing(String pricing) {
            this.pricing = pricing;
        }

        public String getSeoUrl() {
            return seoUrl;
        }

        public void setSeoUrl(String seoUrl) {
            this.seoUrl = seoUrl;
        }

        public String getSeoIdentifier() {
            return seoIdentifier;
        }

        public void setSeoIdentifier(String seoIdentifier) {
            this.seoIdentifier = seoIdentifier;
        }
    }

    public ArrayList<Facets> getFacets() {
        return facets;
    }

    public void setFacets(ArrayList<Facets> facets) {
        this.facets = facets;
    }

}
