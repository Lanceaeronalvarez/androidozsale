package au.com.dealsdirect.data.network.model.saleitemdetails;

/**
 * Created by Ayi on 5/29/18.
 */

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Personalisation {

    @SerializedName("$schema")
    @Expose
    private String schema = "";
    @SerializedName("$id")
    @Expose
    private String id = "";
    @SerializedName("title")
    @Expose
    private String title = "";
    @SerializedName("description")
    @Expose
    private String description = "";
    @SerializedName("property_order")
    @Expose
    private List<String> propertyOrder = new ArrayList<>();
    @SerializedName("properties")
    @Expose
    private LinkedHashMap<String, Property> properties = new LinkedHashMap<>();
    @SerializedName("required")
    @Expose
    private List<String> required = new ArrayList<>();

    public String getSchema() {
        return schema;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getPropertyOrder() {
        return propertyOrder;
    }

    public LinkedHashMap<String, Property> getProperties() {
        return properties;
    }

    public List<String> getRequired() {
        return required;
    }


    public static class Property {

        private String key = "";
        @SerializedName("title")
        @Expose
        private String title = "";
        @SerializedName("description")
        @Expose
        private String description = "";
        @SerializedName("watermark")
        @Expose
        private String watermark = "";
        @SerializedName("control")
        @Expose
        private String control = "";
        @SerializedName("enum_elements")
        @Expose
        private List<EnumElement> enumElements = new ArrayList<>();
        @SerializedName("type")
        @Expose
        private String type = "";
        @SerializedName("maxLength")
        @Expose
        private Integer maxLength = -1;
        @SerializedName("pattern")
        @Expose
        private String pattern = "";
        @SerializedName("sorting")
        @Expose
        private Integer sorting = -1;
        @SerializedName("enum")
        @Expose
        private List<String> _enum = new ArrayList<>();
        @SerializedName("format")
        @Expose
        private String format = "";

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getWatermark() {
            return watermark;
        }

        public String getControl() {
            return control;
        }

        public List<EnumElement> getEnumElements() {
            return enumElements;
        }

        public String getType() {
            return type;
        }

        public Integer getMaxLength() {
            return maxLength;
        }

        public String getPattern() {
            return pattern;
        }

        public Integer getSorting() { return sorting; }

        public List<String> getEnum() {
            return _enum;
        }

        public String getFormat() {
            return format;
        }

        public int getIndexOfEnumElementValue(String value) {
            int maxIndex = maxLength > value.length() ? value.length() : maxLength;

            for (EnumElement element : enumElements) {
                String trimmedValue = value.substring(0, maxIndex);
                String trimmedElementValue = element.getValue().substring(0, maxIndex);

                if (trimmedValue.equals(trimmedElementValue)) return enumElements.indexOf(element);
            }

            return 0;
        }

    }

    public class EnumElement {

        @SerializedName("image_url")
        @Expose
        private String imageUrl = "";
        @SerializedName("value")
        @Expose
        private String value = "";
        @SerializedName("title")
        @Expose
        private String title = "";

        public String getImageUrl() {
            return imageUrl;
        }

        public String getValue() {
            return value;
        }

        public String getTitle() {
            return title;
        }

    }

    public static class CustomizableItemDetails {

        @SerializedName(value = "key", alternate = {"Key"})
        @Expose
        private String key = "";
        @SerializedName(value = "value", alternate = {"Value"})
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