package au.com.dealsdirect.data.network.model.login;


public class LoginTicket {

    public static class RequestValue {
        private String countryID;
        private String ticket;

        public RequestValue(String ticket, String countryID) {
            this.ticket = ticket;
            this.countryID = countryID;
        }
    }

    public static class ResponseValue {

    }
}
