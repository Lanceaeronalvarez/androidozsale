
package au.com.dealsdirect.data.network.model.saleitemdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import au.com.dealsdirect.data.network.model.saleitems.SaleItemProduct;

public class GetSaleItemDetailsResponse extends SaleItemProduct {
    @SerializedName("personalizationSchema")
    @Expose
    private String personalisation;
    @SerializedName("skuVariants")
    @Expose
    private List<GetSaleItemDetailsResponse> skuVariants = null;
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

    public String getPersonalisation() {
        return personalisation;
    }

    public List<GetSaleItemDetailsResponse> getSkuVariants() {
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
}
