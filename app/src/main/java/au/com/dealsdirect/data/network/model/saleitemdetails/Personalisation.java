package au.com.dealsdirect.data.network.model.saleitemdetails;

/**
 * Created by Ayi on 5/29/18.
 */

import java.util.LinkedHashMap;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Personalisation {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("properties")
    @Expose
    private LinkedHashMap<String, Property> properties;
    @SerializedName("required")
    @Expose
    private List<String> required = null;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LinkedHashMap<String, Property> getProperties() {
        return properties;
    }

    public void setProperties(LinkedHashMap<String, Property> properties) {
        this.properties = properties;
    }

    public List<String> getRequired() {
        return required;
    }

    public void setRequired(List<String> required) {
        this.required = required;
    }

    public static class Property {

        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("watermark")
        @Expose
        private String watermark;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("maxLength")
        @Expose
        private Integer maxLength;
        @SerializedName("pattern")
        @Expose
        private String pattern;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getWatermark() {
            return watermark;
        }

        public void setWatermark(String watermark) {
            this.watermark = watermark;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Integer getMaxLength() {
            return maxLength;
        }

        public void setMaxLength(Integer maxLength) {
            this.maxLength = maxLength;
        }

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }

    }

    public static class CustomizableItemDetails {

        @SerializedName("Key")
        @Expose
        private String key = "";
        @SerializedName("Value")
        @Expose
        private String value = "";

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

    }
}