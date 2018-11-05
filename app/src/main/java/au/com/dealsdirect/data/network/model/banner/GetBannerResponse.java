
package au.com.dealsdirect.data.network.model.banner;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetBannerResponse {

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

    public class Banner {

        private Group group;
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

        public Boolean getIsClickable() { return isClickable; }

        public List<Banner> getBanners() {
            for (Banner banner : banners) {
                banner.setGroup(this);
            }
            return banners;
        }
    }
}
