
package au.com.dealsdirect.data.network.model.banner;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

import au.com.dealsdirect.data.cachedresponses.CachableResponse;

public class GetBannerResponse extends CachableResponse {


    @SerializedName("groups")
    @Expose
    private List<Group> groups = null;

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public class Attributes {

        @SerializedName("saleExternalId")
        @Expose
        private String saleExternalId;

        public String getSaleExternalId() {
            return saleExternalId;
        }

        public void setSaleExternalId(String saleExternalId) {
            this.saleExternalId = saleExternalId;
        }

    }

    public static class Banner {

        private transient Group group;
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("accountId")
        @Expose
        private String accountId;
        @SerializedName("isAvailable")
        @Expose
        private Boolean isAvailable;
        @SerializedName("bannerText")
        @Expose
        private String bannerText;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("startDate")
        @Expose
        private String startDate;
        @SerializedName("endDate")
        @Expose
        private String endDate;
        @SerializedName("destinationId")
        @Expose
        private String destinationId;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("hasImage")
        @Expose
        private Boolean hasImage;
        @SerializedName("attributes")
        @Expose
        private Attributes attributes;
        @SerializedName("categories")
        @Expose
        private List<String> categories = null;
        @SerializedName("bannerType")
        @Expose
        private String bannerType;
        @SerializedName("percentOff")
        @Expose
        private Integer percentOff;
        @SerializedName("isFreeDelivery")
        @Expose
        private Boolean isFreeDelivery;
        @SerializedName("percentOffText")
        @Expose
        private String percentOffText;
        @SerializedName("link")
        @Expose
        private String link;
        @SerializedName("deliveryThreshold")
        @Expose
        private int deliveryThreshold;
        @SerializedName("deliveryType")
        @Expose
        private String deliveryType;


        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public Integer getPercentOff() {
            return percentOff;
        }

        public void setPercentOff(Integer percentOff) {
            this.percentOff = percentOff;
        }

        public Boolean getFreeDelivery() {
            return isFreeDelivery;
        }

        public void setFreeDelivery(Boolean freeDelivery) {
            isFreeDelivery = freeDelivery;
        }

        public String getPercentOffText() {
            return percentOffText;
        }

        public void setPercentOffText(String percentOffText) {
            this.percentOffText = percentOffText;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAccountId() {
            return accountId;
        }

        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }

        public Boolean getIsAvailable() {
            return isAvailable;
        }

        public void setAvailable(Boolean available) {
            isAvailable = available;
        }

        public String getBannerText() {
            return bannerText;
        }

        public void setBannerText(String bannerText) {
            this.bannerText = bannerText;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public String getDestinationId() {
            return destinationId;
        }

        public void setDestinationId(String destinationId) {
            this.destinationId = destinationId;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public Boolean getHasImage() {
            return hasImage;
        }

        public void setHasImage(Boolean hasImage) {
            this.hasImage = hasImage;
        }

        public Attributes getAttributes() {
            return attributes;
        }

        public void setAttributes(Attributes attributes) {
            this.attributes = attributes;
        }

        public List<String> getCategories() {
            return categories;
        }

        public void setCategories(List<String> categories) {
            this.categories = categories;
        }

        public String getBannerType() {
            return bannerType;
        }

        public void setBannerType(String bannerType) {
            this.bannerType = bannerType;
        }

        public Group getGroup() {
            return group;
        }

        public void setGroup(Group group) {
            this.group = group;
        }

        public int getDeliveryThreshold() {
            return deliveryThreshold;
        }

        public void setDeliveryThreshold(int deliveryThreshold) {
            this.deliveryThreshold = deliveryThreshold;
        }

        public String getDeliveryType() {
            return deliveryType;
        }

        public void setDeliveryType(String deliveryType) {
            this.deliveryType = deliveryType;
        }
    }

    public class Group {

        @SerializedName("type")
        @Expose
        private String type = "";
        @SerializedName("title")
        @Expose
        private String title = "";
        @SerializedName("isClickable")
        @Expose
        private Boolean isClickable = false;
        @SerializedName("banners")
        @Expose
        private List<Banner> banners = null;

        public String getType() {
            return type;
        }

        public String getTitle() {
            return title;
        }

        public Boolean getIsClickable() {
            return isClickable;
        }

        public List<Banner> getBanners() {
            for (Banner banner : banners) {
                banner.setGroup(this);
            }
            return banners;
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, title);
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj instanceof Group) {
                final Group other = (Group) obj;
                return ((other.type != null && other.type.equals(type)) || other.type == type) &&
                        ((other.title != null && other.title.equals(title)) || other.title == title);
            }
            return false;
        }
    }
}
