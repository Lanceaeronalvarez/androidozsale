package au.com.dealsdirect.data.network.model.login;
/*
 * Created by CodeineBot on 1/5/17.
 */

import com.mysale.genie.utility.LegacyBaseResponseValue;

public class LoginFacebook {


//    @Override
//    protected void executeUseCase(RequestValue requestValues) {
//
//        Gson gson = new GsonBuilder().registerTypeAdapter(LoginFacebook.ResponseValue.class, new BaseDeserializer<LoginFacebook.ResponseValue>()).create();
//        LoginApiService service = GServiceGenerator.createService(mContext, LoginApiService.class, GServiceGenerator.API_LEGACY, gson);
//        service.loginFacebook(requestValues).enqueue(new GCallback<LoginFacebook.ResponseValue>() {
//            @Override
//            public void onResponse(Call<LoginFacebook.ResponseValue> call, Response<LoginFacebook.ResponseValue> response) {
//                super.onResponse(call, response);
//
//                if (response.isSuccessful() && response.body() != null) {
//                    getUseCaseCallback().onSuccess(response.body());
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<LoginFacebook.ResponseValue> call, Throwable t) {
//                super.onFailure(call, t);
//                getUseCaseCallback().onError();
//            }
//        });
//    }

    public static class RequestValue {

        private String email;
        private String firstName;
        private String lastName;
        private String referredBy = "android";
        private String invitedBy = "";
        private String voucherID = "00000000-0000-0000-0000-000000000000";
        private String countryID = "";
        private String languageID = "";
        private String facebookUserID;
        private String facebookCookieValue;


        public RequestValue(String email, String firstName, String lastName, String countryID, String languageID, String facebookUserID, String facebookCookieValue) {
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.facebookUserID = facebookUserID;
            this.facebookCookieValue = facebookCookieValue;
            this.countryID = countryID;
            this.languageID = languageID;
        }
    }


    public static class ResponseValue {

        public Response d;

        public static class Response extends LegacyBaseResponseValue{
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
