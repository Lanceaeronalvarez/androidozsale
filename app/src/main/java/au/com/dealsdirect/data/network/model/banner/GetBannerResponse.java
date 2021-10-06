
package au.com.dealsdirect.data.network.model.banner;

import android.util.Pair;

import androidx.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import au.com.dealsdirect.data.cachedresponses.CachableResponse;
import au.com.dealsdirect.utils.JsonUtils;

import static au.com.dealsdirect.utils.StringUtils.addQueryParameter;

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
        @SerializedName("linkOptions")
        @Expose
        private LinkOptions linkOptions;

        public Group getGroup() {
            return group;
        }

        private void setGroup(Group group) {
            this.group = group;
        }

        public String getId() {
            return id;
        }

        public String getAccountId() {
            return accountId;
        }

        public Boolean getIsAvailable() {
            return isAvailable;
        }

        public String getBannerText() {
            return bannerText;
        }

        public String getDescription() {
            return description;
        }

        public String getStartDate() {
            return startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public String getDestinationId() {
            return destinationId;
        }

        public String getImage() {
            return image;
        }

        public Boolean getHasImage() {
            return hasImage;
        }

        public Attributes getAttributes() {
            return attributes;
        }

        public List<String> getCategories() {
            return categories;
        }

        public String getBannerType() {
            return bannerType;
        }

        public Integer getPercentOff() {
            return percentOff;
        }

        public Boolean getFreeDelivery() {
            return isFreeDelivery;
        }

        public String getPercentOffText() {
            return percentOffText;
        }

        public String getLink() {
            return link;
        }

        public int getDeliveryThreshold() {
            return deliveryThreshold;
        }

        public String getDeliveryType() {
            return deliveryType;
        }

        public LinkOptions getLinkOptions() {
            return linkOptions;
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


    public static class LinkOptions {
        public enum LinkOptionType {
            CATEGORY,
            SALE,
            PROMO,
            UNKNOWN
        }

        @SerializedName("facets")
        @Expose
        private Facets facets;
        @SerializedName("sorting")
        @Expose
        private String sorting;
        @SerializedName("category")
        @Expose
        private Map<String, String> category;
        @SerializedName("searchQuery")
        @Expose
        private String searchQuery;

        public Facets getFacets() {
            return facets;
        }

        public void setFacets(Facets facets) {
            this.facets = facets;
        }

        public String getSorting() {
            return sorting;
        }

        public void setSorting(String sorting) {
            this.sorting = sorting;
        }

        public Map<String, String> getCategory() {
            return category;
        }

        public void setCategory(Map<String, String> category) {
            this.category = category;
        }

        public String getSearchQuery() {
            return searchQuery;
        }

        public void setSearchQuery(String searchQuery) {
            this.searchQuery = searchQuery;
        }

        public String toUrlParams(String url) {
            String output = url;
            output = addQueryParameter(output, "q", "q=" + searchQuery);
            output = addQueryParameter(output, "ff", "ff=" +
                    JsonUtils.convertToJsonObject(facets, new Pair<>(Facets.PriceLimit.class,
                            (JsonSerializer<Facets.PriceLimit>) (src, typeOfSrc, context) -> {
                                JsonArray jsonArray = new JsonArray();
                                jsonArray.add(src.from + " to " + src.max);
                                return jsonArray;
                            })).toString());
            output = addQueryParameter(output, "sa", "sa=" + sorting);
            final String categoryName = category == null || category.get("name") == null ? "" : category.get("name");
            output = addQueryParameter(output, "c", '[' + categoryName + ']');
            return output;
        }

        public LinkOptionType getLinkOptionType() {
            if (category != null) {
                return LinkOptionType.CATEGORY;
            } else if (facets != null) {
                if (facets.getSaleId() != null) {
                    return LinkOptionType.SALE;
                } else if (facets.getPromoSaleId() != null) {
                    return LinkOptionType.PROMO;
                }
            }
            return LinkOptionType.UNKNOWN;
        }

        public String getCategoryName() {
            assert category != null;
            return category.get("name");
        }

        public String getCategoryId() {
            assert category != null;
            return category.get("id");
        }

        public static class Facets {
            @SerializedName("skus.brandName")
            @Expose
            private List<String> brandNames;
            @SerializedName("skus.attributes.size")
            @Expose
            private List<String> sizes;
            @SerializedName("color")
            @Expose
            private List<String> colors;
            @SerializedName("skus.attributesForFaceting.aud")
            private PriceLimit priceLimit;
            @SerializedName("newArrivals")
            @Expose
            private List<String> newArrivals;
            @SerializedName("delivery")
            @Expose
            private List<String> delivery;
            @SerializedName("saleId")
            @Expose
            private String saleId;
            @SerializedName("promoSaleId")
            @Expose
            private String promoSaleId;

            public List<String> getBrandNames() {
                return brandNames;
            }

            public List<String> getSizes() {
                return sizes;
            }

            public List<String> getColors() {
                return colors;
            }

            public PriceLimit getPriceLimit() {
                return priceLimit;
            }

            public List<String> getNewArrivals() {
                return newArrivals;
            }

            public List<String> getDelivery() {
                return delivery;
            }

            public String getSaleId() {
                return saleId;
            }

            public String getPromoSaleId() {
                return promoSaleId;
            }

            public static class PriceLimit {
                @SerializedName("from")
                @Expose
                private int from;
                @SerializedName("to")
                @Expose
                private int to;
                @SerializedName("max")
                @Expose
                private int max;

                public int getFrom() {
                    return from;
                }

                public int getTo() {
                    return to;
                }

                public int getMax() {
                    return max;
                }
            }
        }
    }

}
