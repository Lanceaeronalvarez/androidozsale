package au.com.dealsdirect.data.network.model.contactsubjecttemplates;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ContactSubjectTemplatesResponse {
    @SerializedName("id")
    @Expose
    String id;

    @SerializedName("suggestions")
    @Expose
    List<Suggestion> suggestions;

    public String getId() {
        return id;
    }

    public List<Suggestion> getSuggestions() {
        return suggestions;
    }

    public static class Suggestion {
        @SerializedName("type")
        @Expose
        String type;

        @SerializedName("value")
        @Expose
        String value;

        @SerializedName("feature_event_type")
        @Expose
        Integer featureEventType;

        @SerializedName("mobile")
        @Expose
        Mobile mobile;

        public String getType() {
            return type;
        }

        public String getValue() {
            return value;
        }

        public Integer getFeatureEventType() {
            return featureEventType;
        }

        public Mobile getMobile() {
            return mobile;
        }

        public static class Mobile {
            @SerializedName("message")
            @Expose
            String message;

            @SerializedName("type")
            @Expose
            String type;

            @SerializedName("subtype")
            @Expose
            String subtype;

            @SerializedName("link")
            @Expose
            Link link;

            public String getMessage() {
                return message;
            }

            public String getType() {
                return type;
            }

            public String getSubtype() {
                return subtype;
            }

            public Link getLink() {
                return link;
            }

            public static class Link {
                @SerializedName("title")
                @Expose
                String title;

                @SerializedName("route_key")
                @Expose
                String routeKey;

                @SerializedName("url")
                @Expose
                String url;

                public String getTitle() {
                    return title;
                }

                public String getUrl() {
                    return url;
                }

                public String getRouteKey() {
                    return routeKey;
                }
            }
        }
    }
}