package au.com.dealsdirect.data.network.model.saleitemdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BuyBoxTemplateTextResponse {
    @SerializedName("type")
    @Expose
    String type;

    @SerializedName("templates")
    @Expose
    Templates templates;

    public String getType() {
        return type;
    }

    public Templates getTemplates() {
        return templates;
    }

    public static class Templates {
        @SerializedName("title")
        @Expose
        String title;

        @SerializedName("sellerTemplate")
        @Expose
        String sellerTemplate;

        @SerializedName("buttonText")
        @Expose
        String buttonText;

        public String getTitle() {
            return title;
        }

        public String getSellerTemplate() {
            return sellerTemplate;
        }

        public String getButtonText() {
            return buttonText;
        }
    }
}
