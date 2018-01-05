package au.com.dealsdirect.data.network.model.userdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.LegacyBaseResponseValue;

/**
 * Created by Admin on 9/29/17.
 */

public class SetUserDetailsResponse {

    @SerializedName("d")
    @Expose
    private SetUserDetailsResponseValue setUserDetailsResponseValue;

    public SetUserDetailsResponseValue getSetUserDetailsResponseValue() {
        return setUserDetailsResponseValue;
    }

    public void setSetUserDetailsResponseValue(SetUserDetailsResponseValue setUserDetailsResponseValue) {
        this.setUserDetailsResponseValue = setUserDetailsResponseValue;
    }


    public class SetUserDetailsResponseValue {

        @SerializedName("IsAuthenticated")
        @Expose
        public boolean isAuthenticated;

        @SerializedName("Value")
        @Expose
        public String value;

        @SerializedName("Result")
        @Expose
        public boolean result;

        @SerializedName("Message")
        @Expose
        public String message;

        public boolean isAuthenticated() {
            return isAuthenticated;
        }

        public void setAuthenticated(boolean authenticated) {
            isAuthenticated = authenticated;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public boolean isResult() {
            return result;
        }

        public void setResult(boolean result) {
            this.result = result;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

}
