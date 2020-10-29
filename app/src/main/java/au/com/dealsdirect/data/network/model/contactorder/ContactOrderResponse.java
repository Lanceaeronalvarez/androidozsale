package au.com.dealsdirect.data.network.model.contactorder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * dp Created by Admin on 7/5/17.
 */
public class ContactOrderResponse {

    @SerializedName("number")
    @Expose
    private Integer number;

    @SerializedName("order_number")
    @Expose
    private Integer orderNumber;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("actions")
    @Expose
    private List<String> actions;

    @SerializedName("items")
    @Expose
    private List<Item> items;

    public Integer getNumber() {
        return number;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getActions() {
        return actions;
    }

    public List<Item> getItems() {
        return items;
    }

    public static class Item {
        @SerializedName("id")
        @Expose
        private String id;

        @SerializedName("orderItemId")
        @Expose
        private String orderItemId;

        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("image_url")
        @Expose
        private String imageUrl;

        @SerializedName("size")
        @Expose
        private String size;

        public String getId() {
            return id;
        }

        public String getOrderItemId() {
            return orderItemId;
        }

        public String getName() {
            return name;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getSize() {
            return size;
        }
    }
}
