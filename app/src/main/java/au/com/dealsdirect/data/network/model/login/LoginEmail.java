package au.com.dealsdirect.data.network.model.login;

import com.mysale.genie.utility.LegacyBaseResponseValue;


/**
 * dp Created by Admin on 10/28/16.
 */


public class LoginEmail {

    public static class RequestValue {

        private String countryID;
        private String userName;
        private String password;
        private String languageID;
        private String captchaResponse;

        // 2 for apps https://apacsale.atlassian.net/wiki/spaces/CX/pages/708641008/V3.26
        private int clientID = 2;

        public RequestValue(String userName, String password, String countryID, String languageID, String captchaResponse) {
            this.userName = userName;
            this.password = password;
            this.countryID = countryID;
            this.languageID = languageID;
            this.captchaResponse = captchaResponse;
        }
    }


    public static class ResponseValue {
        public Response d;

        public static class Response extends LegacyBaseResponseValue {
            public Value Value;
        }

        public static class Value {
            public String Ticket;
            public boolean ReadTerms;
        }

        public boolean isSuccess() {
            return d.isAuthenticated() && d.getResult();
        }

        public String getTicket() {
            return d.Value.Ticket;
        }

        public String getMessage() {
            return d.getMessage();
        }
    }
}
