package au.com.dealsdirect.ui.controller.login;
/*
 * Created by CodeineBot on 6/15/17.
 */

import com.androidnetworking.error.ANError;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.login.LoginEmail;
import au.com.dealsdirect.data.network.model.login.LoginTicket;
import au.com.dealsdirect.data.network.model.login.Logout;
import au.com.dealsdirect.ui.base.AuthenticationBasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

public class LoginPresenter<V extends LoginMvpView> extends AuthenticationBasePresenter<V> implements LoginMvpPresenter<V> {

    @Inject
    public LoginPresenter(
            DataManager dataManager,
            SchedulerProvider schedulerProvider,
            CompositeDisposable compositeDisposable) {

        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public boolean loginViaEmail(String username, String password) {
        getCompositeDisposable().add(getDataManager()
                .callLoginViaEmail(
                        new LoginEmail.RequestValue(
                                username,
                                password,
                                getDataManager().getCountryId(),
                                getDataManager().getLanguageId()))
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(responseValue -> onAuthSuccess(
                        responseValue.isSuccess(),
                        responseValue.getTicket(),
                        responseValue.getMessage()),
                        throwable ->
                        {
                            onAuthFailure(throwable);
                        }));

        return true;
    }

//    @Override
//    public boolean loginViaFacebook(
//            String email,
//            String firstName,
//            String lastName,
//            String facebookUserID,
//            String facebookCookieValue) {
//
//        Log.d("loginPresenter"," value = "+email+" , "+firstName+", " +lastName+" , "+facebookUserID+" , "+facebookCookieValue);
//        getCompositeDisposable().add(getDataManager().callLoginViaFacebook(
//                new LoginFacebook.RequestValue(
//                        email,
//                        firstName,
//                        lastName,
//                        getDataManager().getCountryId(),
//                        getDataManager().getLanguageId(),
//                        facebookUserID,
//                        facebookCookieValue))
//
//                .subscribeOn(getSchedulerProvider().io())
//                .observeOn(getSchedulerProvider().ui())
//                .subscribe(responseValue -> onAuthSuccess(
//                        responseValue.isSuccess(),
//                        responseValue.getTicket(),
//                        responseValue.getMessage()),
//                        throwable -> onAuthFailure(throwable)));
//
//        return true;
//    }

    private void onAuthSuccess(boolean isSuccess, String ticket, String errorMessage) {
        if (!isViewAttached()) {
            return;
        }

        if (isSuccess) {
            getDataManager().acknowledgeAuth(ticket);
            getMvpView().showLoginSuccessful(ticket);
        } else {
            getMvpView().showLoginError(errorMessage);
        }
    }

    private void onAuthFailure(Throwable throwable) {
        if (!isViewAttached()) {
            return;
        }

        getMvpView().hideLoading();
        getMvpView().showLoginError(throwable.getMessage());

        // handle load accounts error here
        if (throwable instanceof ANError) {
            ANError anError = (ANError) throwable;
            handleApiError(anError);
        }
    }

    @Override
    public boolean logout() {
//        GCartUtil.setValueToCart(0);
//        RxBus.instance().post("update_cart_items_immediate");
//        RxBus.instance().post(Auth.EVENT_PRE_LOGOUT);

        getCompositeDisposable().add(getDataManager()
                .callLogout(new Logout.RequestValue())
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(responseValue -> {
                    if (!isViewAttached()) {
                        return;
                    }

                    getDataManager().revokeAuth();
//                        RxBus.instance().post(Auth.EVENT_LOGOUT);
//                        RxBus.instance().post(GVersion.EVENT_LOGOUT);
//                    getMvpView().logoutResult();
                }, throwable -> {
                    if (!isViewAttached()) {
                        return;
                    }

                    getMvpView().hideLoading();
                    getMvpView().onError(throwable.getMessage());
                    getMvpView().showLoginError(throwable.getMessage());

                    // handle load accounts error here
                    if (throwable instanceof ANError) {
                        ANError anError = (ANError) throwable;
                        handleApiError(anError);
                    }
                }));

        return true;
    }

    @Override
    public boolean loginTicket(String ticket, String countryId) {
        getCompositeDisposable().add(getDataManager()
                .callLoginTicket(new LoginTicket.RequestValue(ticket, countryId))
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<LoginEmail.ResponseValue>() {
                    @Override
                    public void accept(@NonNull LoginEmail.ResponseValue responseValue) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }

                        if (responseValue.isSuccess()) {
                            getDataManager().acknowledgeAuth(responseValue.getTicket());
                        } else {
                            //On login ticket fail, call logout and go back to shop
//                            RxBus.instance().post("shop_now");
                            logout();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();
                        getMvpView().onError(throwable.getMessage());

                        // handle load accounts error here
                        if (throwable instanceof ANError) {
                            ANError anError = (ANError) throwable;
                            handleApiError(anError);
                        }
                    }

                }));

        return true;
    }

//    private final List<String> permissions = Arrays.asList("public_profile", "email");
//
//    private String strEmail = "";
//    private String strFirstName;
//    private String strLastName;
//    private String strFBUserID;
//    private String strFBSignedRequest;
//
//    @Override
//    public void onFacebookLogin(Activity activity, CallbackManager callbackManager) {
//
//
//        LoginManager loginManager = LoginManager.getInstance();
//        loginManager.logInWithReadPermissions(activity, permissions);
//        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                Log.d("FB", "onSuccess: " + loginResult.getAccessToken());
//                fetchUserInfo(loginResult.getAccessToken());
//            }
//
//            @Override
//            public void onCancel() {
//                //TODO: Handle cancel
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                Log.d("FB", "onError: " + error.getMessage());
//            }
//        });
//    }
//
//    private void fetchUserInfo(final AccessToken accessToken) {
//
//        GraphRequest request = GraphRequest.newMeRequest(
//                accessToken,
//                new GraphRequest.GraphJSONObjectCallback() {
//                    @Override
//                    public void onCompleted(JSONObject object, GraphResponse response) {
//                        // Application code
//                        try {
//                            if (object != null) {
//                                strEmail = object.getString("email");
//                                strFirstName = object.getString("first_name");
//                                strLastName = object.getString("last_name");
//                                strFBUserID = object.getString("id");
//
//                                generateFBSignedRequest(accessToken);
//
//                                validateFBLogin();
//
//                                LoginManager.getInstance().logOut();
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//        Bundle parameters = new Bundle();
//        parameters.putString("fields", "id,email,last_name,first_name");
//        request.setParameters(parameters);
//        request.executeAsync();
//    }
//
//    private void validateFBLogin() {
//
//        if (isFacebookDetailsComplete()) {
//            loginViaFacebook(strEmail, strFirstName, strLastName, strFBUserID, strFBSignedRequest);
//        } else {
//            getMvpView().showLoginError("Missing info from Facebook");
//        }
//    }
//
//    private boolean isFacebookDetailsComplete() {
//        return strEmail != null
//                && strFirstName != null
//                && strLastName != null
//                && strFBUserID != null
//                && !this.strEmail.isEmpty()
//                && !this.strFirstName.isEmpty()
//                && !this.strLastName.isEmpty()
//                && !this.strFBUserID.isEmpty();
//    }
//
//    private void generateFBSignedRequest(AccessToken accessToken) {
//
//        try {
//            String secret = getDataManager().getFbSecret();
//
//            Date date = accessToken.getExpires();
//
//            JSONObject jObj = new JSONObject();
//            jObj.put("user_id", this.strFBUserID);
//            jObj.put("oauth_token", accessToken);
//            jObj.put("expires", date.getTime() / 1000L);
//            jObj.put("algorithm", "HMAC-SHA256");
//
//            byte[] jsonData = jObj.toString(1).getBytes("UTF-8");
//
//            String payloadString = new String(jsonData, "UTF-8");
//            byte[] payloadData = payloadString.getBytes("US-ASCII");
//            String payloadBase64URLString = Base64.encodeToString(payloadData, 0);
//
//            payloadBase64URLString = payloadBase64URLString.replace("\n", "");
//            payloadBase64URLString = payloadBase64URLString.replace("=", "");
//            payloadBase64URLString = payloadBase64URLString.replace("+", "-");
//            payloadBase64URLString = payloadBase64URLString.replace("/", "_");
//
//            String key = Normalizer.normalize(secret, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
//            String data = Normalizer.normalize(payloadBase64URLString, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
//
//            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
//            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), "HmacSHA256");
//            sha256_HMAC.init(secret_key);
//            byte[] hmacData = sha256_HMAC.doFinal(data.getBytes());
//            String hash = Base64.encodeToString(hmacData, 0);
//
//            hash = hash.replace("\n", "");
//            hash = hash.replace("=", "");
//            hash = hash.replace("+", "-");
//            hash = hash.replace("/", "_");
//
//            strFBSignedRequest = hash + "." + payloadBase64URLString;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
