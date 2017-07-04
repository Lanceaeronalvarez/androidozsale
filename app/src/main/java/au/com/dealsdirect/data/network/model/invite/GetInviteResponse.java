package au.com.dealsdirect.data.network.model.invite;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.LegacyBaseResponseValue;

/**
 * Created by Paul on 7/2/17.
 */

public class GetInviteResponse {

    public Response d;

    public static class Response extends LegacyBaseResponseValue {

        @SerializedName("Value")
        @Expose
        public Value value;

        public Value getValue() {
            return value;
        }
    }

    public Response getResponse() {
        return d;
    }

    public static class Value {
        @SerializedName("Link")
        @Expose
        private String link;
        @SerializedName("BannerUrl")
        @Expose
        private String bannerUrl;
        @SerializedName("InviteSubject")
        @Expose
        private String inviteSubject;
        @SerializedName("InviteMessage")
        @Expose
        private String inviteMessage;

        public String getLink() {
            return link;
        }

        public String getBannerUrl() {
            return bannerUrl;
        }

        public String getInviteSubject() {
            return inviteSubject;
        }

        public String getInviteMessage() {
            return inviteMessage;
        }
    }

}
