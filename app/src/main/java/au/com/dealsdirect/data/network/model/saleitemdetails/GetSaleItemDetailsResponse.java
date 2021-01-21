
package au.com.dealsdirect.data.network.model.saleitemdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetSaleItemDetailsResponse {

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
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("labelText")
    @Expose
    private String labelText;
    @SerializedName("isSoldOut")
    @Expose
    private boolean isSoldOut;
    @SerializedName("price")
    @Expose
    private Price price;
    @SerializedName("originalPrice")
    @Expose
    private OriginalPrice originalPrice;
    @SerializedName("quantity")
    @Expose
    private Integer quantity;
    @SerializedName("images")
    @Expose
    private List<String> images = null;
    @SerializedName("brandName")
    @Expose
    private String brandName;
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
    @SerializedName("seoIdentifier")
    @Expose
    private String seoIdentifier;
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

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLabelText() {
        return labelText;
    }

    public boolean isSoldOut() {
        return isSoldOut;
    }

    public Price getPrice() {
        return price;
    }

    public OriginalPrice getOriginalPrice() {
        return originalPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public List<String> getImages() {
        return images;
    }

    public String getBrandName() {
        return brandName;
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

    public String getSeoIdentifier() {
        return seoIdentifier;
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
