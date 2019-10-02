package au.com.dealsdirect.data.network.model.returns.newreturn;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by MTC on 2019-08-14.
 */
public class SetAttachmentRequest {
    @SerializedName("type")
    private String type;
    @SerializedName("id")
    private String id;
    @SerializedName("items")
    private List<Items> listItems;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Items> getListItems() {
        return listItems;
    }

    public void setListItems(List<Items> listItems) {
        this.listItems = listItems;
    }

    public static class Items {
        @SerializedName("type")
        private String type;
        @SerializedName("url")
        private String url;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
