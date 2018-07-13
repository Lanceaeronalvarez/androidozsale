package au.com.dealsdirect.data.network.model.gdpr.consentdata;
/*
 * Created by CodeineBot on 1/6/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.LegacyBaseResponseValue;

public class GetConsentDataResponse {

    private Response d;

    public Boolean getShowConsentRequired() {
        return d != null && d.getValue() != null && d.getValue().getShowConsentRequired();
    }

    public class Response extends LegacyBaseResponseValue {

        public Value getValue() {
            return value;
        }

        @Expose
        @SerializedName("Value")
        public Value value;
    }

    public class Value {

        @SerializedName("ShowConsentRequired")
        @Expose
        private Boolean showConsentRequired;

        public Boolean getShowConsentRequired() {
            return showConsentRequired;
        }

        public void setShowConsentRequired(Boolean showConsentRequired) {
            this.showConsentRequired = showConsentRequired;
        }
    }
}