
package au.com.dealsdirect.data.network.model.saleitemdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import au.com.dealsdirect.data.network.model.saleitems.SaleItemProduct;

public class SaleItemDetails extends SaleItemProduct {
    @SerializedName("personalizationSchema")
    @Expose
    private String personalisation;
    @SerializedName("skuVariants")
    @Expose
    private List<SaleItemDetails> skuVariants = null;
    @SerializedName("skuId")
    @Expose
    private String skuId;
    @SerializedName("attributes")
    @Expose
    private Attributes attributes;
    @SerializedName("quantity")
    @Expose
    private Integer quantity;
    @SerializedName("countryOfOrigin")
    @Expose
    private String countryOfOrigin;
    @SerializedName("deliveryInformation")
    @Expose
    private String deliveryInformation;
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
    @SerializedName("productId")
    @Expose
    private String productId;
    @SerializedName("rrpText")
    @Expose
    private String rrpText;
    @SerializedName("masterSkuId")
    @Expose
    private String masterSkuId;
    @SerializedName("supplier")
    @Expose
    private String supplier;
    @SerializedName("sellerName")
    @Expose
    private String sellerName;
    @SerializedName("seoStoreId")
    @Expose
    private String seoStoreId;
    @SerializedName("buyBoxGroup")
    @Expose
    private List<BuyBoxItem> buyBoxItem;
    @SerializedName("taxonomy")
    @Expose
    private List<Category> taxonomy;

    public String getPersonalisation() {
        return personalisation;
    }

    public List<SaleItemDetails> getSkuVariants() {
        return skuVariants;
    }

    public String getSkuId() {
        return skuId;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }

    public String getDeliveryInformation() {
        return deliveryInformation;
    }

    public String getShippingInformation() {
        return shippingInformation;
    }

    public String getReturnPolicy() {
        return returnPolicy;
    }

    public String getPricing() {
        return pricing;
    }

    public String getSeoUrl() {
        return seoUrl;
    }

    public String getProductId() {
        return productId;
    }

    public String getRrpText() {
        return rrpText;
    }

    public String getMasterSkuId() {
        return masterSkuId;
    }

    public String getSupplier() {
        return supplier;
    }

    public String getSellerName() {
        return sellerName;
    }

    public String getSeoStoreId() {
        return seoStoreId;
    }

    public List<BuyBoxItem> getBuyBoxGroup() {
        return buyBoxItem;
    }

    public List<Category> getTaxonomy() {
        return taxonomy;
    }

    public static class BuyBoxItem {
        @SerializedName("deliveryPrice")
        @Expose
        private double deliveryPrice;

        @SerializedName("deliveryThreshold")
        @Expose
        private int deliveryThreshold;

        @SerializedName("deliveryType")
        @Expose
        private String deliveryType;

        @SerializedName("isFreeDelivery")
        @Expose
        private boolean isFreeDelivery;

        @SerializedName("masterProductId")
        @Expose
        private String masterProductId;

        @SerializedName("masterSkuId")
        @Expose
        private String masterSkuId;

        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("price")
        @Expose
        private Price price;

        @SerializedName("salePrice")
        @Expose
        private Price salePrice;

        // TODO: unknown type and function
//        @SerializedName("rank")
//        @Expose
//        private Integer rank;

        @SerializedName("sellerName")
        @Expose
        private String sellerName;

        @SerializedName("seoIdentifier")
        @Expose
        private String seoIdentifier;

        @SerializedName("seoStoreId")
        @Expose
        private String seoStoreId;

        @SerializedName("seoUrl")
        @Expose
        private String seoUrl;

        @SerializedName("shippingText")
        @Expose
        private String shippingText;

        public double getDeliveryPrice() {
            return deliveryPrice;
        }

        public int getDeliveryThreshold() {
            return deliveryThreshold;
        }

        public String getDeliveryType() {
            return deliveryType;
        }

        public boolean isFreeDelivery() {
            return isFreeDelivery;
        }

        public String getMasterProductId() {
            return masterProductId;
        }

        public String getMasterSkuId() {
            return masterSkuId;
        }

        public String getName() {
            return name;
        }

        public Price getPrice() {
            return price;
        }

        public Price getSalePrice() {
            return salePrice;
        }

        public String getSellerName() {
            return sellerName;
        }

        public String getSeoIdentifier() {
            return seoIdentifier;
        }

        public String getSeoStoreId() {
            return seoStoreId;
        }

        public String getSeoUrl() {
            return seoUrl;
        }

        public String getShippingText() {
            return shippingText;
        }
    }

    public static class Category {
        @SerializedName("id")
        @Expose
        private String id;

        @SerializedName("category")
        @Expose
        private String category;

        public String getId() {
            return id;
        }

        public String getCategory() {
            return category;
        }
    }
}
