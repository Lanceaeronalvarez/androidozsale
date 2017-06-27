
package au.com.dealsdirect.data.network.model.saleitemdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetSaleItemDetailsResponse {

    @SerializedName("skuVariants")
    @Expose
    private List<SkuVariant> skuVariants = null;
    @SerializedName("skuId")
    @Expose
    private String skuId;
    @SerializedName("attributes")
    @Expose
    private Attributes_ attributes;
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
    private Price_ price;
    @SerializedName("originalPrice")
    @Expose
    private OriginalPrice_ originalPrice;
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

    public List<SkuVariant> getSkuVariants() {
        return skuVariants;
    }

    public void setSkuVariants(List<SkuVariant> skuVariants) {
        this.skuVariants = skuVariants;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public Attributes_ getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes_ attributes) {
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

    public Price_ getPrice() {
        return price;
    }

    public void setPrice(Price_ price) {
        this.price = price;
    }

    public OriginalPrice_ getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(OriginalPrice_ originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
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
