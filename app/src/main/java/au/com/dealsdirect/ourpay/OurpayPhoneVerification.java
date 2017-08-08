package au.com.dealsdirect.ourpay;
/*
 * Created by CodeineBot on 10/13/16.
 */

import org.json.JSONObject;

public class OurpayPhoneVerification {
    private boolean isRequired;
    private JSONObject jsonPhoneFormat;
    private JSONObject jsonCodeFormat;

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean required) {
        isRequired = required;
    }

    public JSONObject getJsonPhoneFormat() {
        return jsonPhoneFormat;
    }

    public void setJsonPhoneFormat(JSONObject jsonPhoneFormat) {
        this.jsonPhoneFormat = jsonPhoneFormat;
    }

    public JSONObject getJsonCodeFormat() {
        return jsonCodeFormat;
    }

    public void setJsonCodeFormat(JSONObject jsonCodeFormat) {
        this.jsonCodeFormat = jsonCodeFormat;
    }
}
