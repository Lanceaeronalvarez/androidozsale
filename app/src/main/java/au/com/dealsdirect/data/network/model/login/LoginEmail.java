package au.com.dealsdirect.data.network.model.login;

import com.mysale.genie.utility.LegacyBaseResponseValue;


/**
 * dp Created by Admin on 10/28/16.
 */


public class LoginEmail {

//    @Override
//    protected void executeUseCase(RequestValue requestValues) {
//        Gson gson = new GsonBuilder().registerTypeAdapter(ResponseValue.class, new BaseDeserializer<ResponseValue>())
//                .create();
//        LoginApiService service = GServiceGenerator.createService(mContext, LoginApiService.class, GServiceGenerator.API_LEGACY, gson);
//        service.login(requestValues).enqueue(new GCallback<ResponseValue>() {
//            @Override
//            public void onResponse(Call<ResponseValue> call, Response<ResponseValue> response) {
//                super.onResponse(call, response);
//
//                if (response.isSuccessful() && response.body() != null) {
//                    getUseCaseCallback().onSuccess(response.body());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseValue> call, Throwable t) {
//                super.onFailure(call, t);
//                getUseCaseCallback().onError();
//            }
//        });
//    }

    public static class RequestValue {

        private String countryID;
        private String userName;
        private String password;
        private String languageID;

        public RequestValue(String userName, String password, String countryID, String languageID) {
            this.userName = userName;
            this.password = password;
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
