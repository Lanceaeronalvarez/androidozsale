package au.com.dealsdirect.ui.base;


import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;

import com.androidnetworking.error.ANError;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.ApiCallback;
import au.com.dealsdirect.data.network.model.login.LoginFacebook;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * Base class that implements the Presenter interface and provides a base implementation for
 * onAttach() and onDetach(). It also handles keeping a reference to the mvpView that
 * can be accessed from the children classes by calling getMvpView().
 */
public class AuthenticationBasePresenter<V extends AuthenticationMvpView> implements AuthenticationMvpPresenter<V> {

    private static final String TAG = "BasePresenter";

    private final DataManager mDataManager;
    private final SchedulerProvider mSchedulerProvider;
    private final CompositeDisposable mCompositeDisposable;

    private final List<String> permissions = Arrays.asList("public_profile", "email");

    private String strEmail = "";
    private String strFirstName;
    private String strLastName;
    private String strFBUserID;
    private String strFBSignedRequest;

    private V mMvpView;

    @Inject
    public AuthenticationBasePresenter(DataManager dataManager,
                                       SchedulerProvider schedulerProvider,
                                       CompositeDisposable compositeDisposable) {
        this.mDataManager = dataManager;
        this.mSchedulerProvider = schedulerProvider;
        this.mCompositeDisposable = compositeDisposable;
    }

    @Override
    public void onAttach(V mvpView) {
        mMvpView = mvpView;
    }

    @Override
    public void onDetach() {
        mCompositeDisposable.dispose();
        mMvpView = null;
    }

    public boolean isViewAttached() {
        return mMvpView != null;
    }

    public V getMvpView() {
        return mMvpView;
    }

    public void checkViewAttached() {
        if (!isViewAttached()) throw new MvpViewNotAttachedException();
    }

    public DataManager getDataManager() {
        return mDataManager;
    }

    public SchedulerProvider getSchedulerProvider() {
        return mSchedulerProvider;
    }

    public CompositeDisposable getCompositeDisposable() {
        return mCompositeDisposable;
    }

    @Override
    public void handleApiError(ANError error) {

//        if (error == null || error.getErrorBody() == null) {
//            getMvpView().onError(R.string.api_default_error);
//            return;
//        }
//
//        if (error.getErrorCode() == AppConstants.API_STATUS_CODE_LOCAL_ERROR
//                && error.getErrorDetail().equals(ANConstants.CONNECTION_ERROR)) {
//            getMvpView().onError(R.string.connection_error);
//            return;
//        }
//
//        if (error.getErrorCode() == AppConstants.API_STATUS_CODE_LOCAL_ERROR
//                && error.getErrorDetail().equals(ANConstants.REQUEST_CANCELLED_ERROR)) {
//            getMvpView().onError(R.string.api_retry_error);
//            return;
//        }
//
//        final GsonBuilder builder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation();
//        final Gson gson = builder.create();
//
//        try {
//            ApiError apiError = gson.fromJson(error.getErrorBody(), ApiError.class);
//
//            if (apiError == null || apiError.getMessage() == null) {
//                getMvpView().onError(R.string.api_default_error);
//                return;
//            }
//
//            switch (error.getErrorCode()) {
//                case HttpsURLConnection.HTTP_UNAUTHORIZED:
//                case HttpsURLConnection.HTTP_FORBIDDEN:
//                    setUserAsLoggedOut();
//                    getMvpView().openActivityOnTokenExpire();
//                case HttpsURLConnection.HTTP_INTERNAL_ERROR:
//                case HttpsURLConnection.HTTP_NOT_FOUND:
//                default:
//                    getMvpView().onError(apiError.getMessage());
//            }
//        } catch (JsonSyntaxException | NullPointerException e) {
//            Log.e(TAG, "handleApiError", e);
//            getMvpView().onError(R.string.api_default_error);
//        }
    }

    @Override
    public boolean isTablet() {
        return getDataManager().isTablet();
    }

    @Override
    public void setUserAsLoggedOut() {
        //getDataManager().setAccessToken(null);
    }

    @Override
    public void doApiCallForObjectResponse(Observable observable, final ApiCallback callback) {
        getMvpView().showLoading();

        getCompositeDisposable().add(observable
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object response) throws Exception {

                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();

                        callback.onSuccess(response);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();
                        getMvpView().onError(throwable.getMessage());

                        callback.onFailure(throwable);

                        // handle load accounts error here
                        if (throwable instanceof ANError) {
                            ANError anError = (ANError) throwable;
                            handleApiError(anError);
                        }
                    }
                }));
    }

    @Override
    public void doApiCallForListResponse(Observable observable, final ApiCallback callback) {
        getMvpView().showLoading();

        getCompositeDisposable().add(observable
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<List<Object>>() {
                    @Override
                    public void accept(List<Object> response) throws Exception {

                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();

                        callback.onSuccess(response);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();
                        getMvpView().onError(throwable.getMessage());

                        callback.onFailure(throwable);

                        // handle load accounts error here
                        if (throwable instanceof ANError) {
                            ANError anError = (ANError) throwable;
                            handleApiError(anError);
                        }
                    }
                }));
    }

    public static class MvpViewNotAttachedException extends RuntimeException {
        public MvpViewNotAttachedException() {
            super("Please call Presenter.onAttach(MvpView) before" +
                    " requesting data to the Presenter");
        }
    }


    @Override
    public boolean loginViaFacebook(
            String email,
            String firstName,
            String lastName,
            String facebookUserID,
            String facebookCookieValue) {

        getCompositeDisposable().add(getDataManager().callLoginViaFacebook(
                new LoginFacebook.RequestValue(
                        email,
                        firstName,
                        lastName,
                        getDataManager().getCountryId(),
                        getDataManager().getLanguageId(),
                        facebookUserID,
                        facebookCookieValue))

                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(
                        //Elv - Changed to JSON to determine changing json structure
                        jsonObject -> {
                            JSONObject resObj = jsonObject.getJSONObject("d");
                            boolean isSuccess = (resObj.getBoolean("IsAuthenticated") && resObj.getBoolean("Result"));
                            onAuthSuccess(isSuccess, isSuccess? resObj.getJSONObject("Value").getString("Ticket"): "", resObj.getString("Message"));
                        },

                        throwable -> {
                            onAuthFailure(throwable);
                        }
                ));

        return true;
    }


    @Override
    public void onFacebookLogin(Activity activity, CallbackManager callbackManager) {
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.logInWithReadPermissions(activity, permissions);
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                fetchUserInfo(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

                //TODO: Handle cancel
            }

            @Override
            public void onError(FacebookException error) {
            }
        });
    }

    private void fetchUserInfo(final AccessToken accessToken) {

        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // Application code
                        try {
                            if (object != null) {
                                strEmail = object.getString("email");
                                strFirstName = object.getString("first_name");
                                strLastName = object.getString("last_name");
                                strFBUserID = object.getString("id");

                                generateFBSignedRequest(accessToken);

                                validateFBLogin();

                                LoginManager.getInstance().logOut();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,last_name,first_name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void validateFBLogin() {

        if (isFacebookDetailsComplete()) {
            loginViaFacebook(strEmail, strFirstName, strLastName, strFBUserID, strFBSignedRequest);
        } else {
            getMvpView().showLoginError("Missing info from Facebook");
        }
    }

    private boolean isFacebookDetailsComplete() {
        return strEmail != null
                && strFirstName != null
                && strLastName != null
                && strFBUserID != null
                && !this.strEmail.isEmpty()
                && !this.strFirstName.isEmpty()
                && !this.strLastName.isEmpty()
                && !this.strFBUserID.isEmpty();
    }

    private void generateFBSignedRequest(AccessToken accessToken) {

        try {
            String secret = getDataManager().getFbSecret();

            Date date = accessToken.getExpires();

            JSONObject jObj = new JSONObject();
            jObj.put("user_id", this.strFBUserID);
            jObj.put("oauth_token", accessToken);
            jObj.put("expires", date.getTime() / 1000L);
            jObj.put("algorithm", "HMAC-SHA256");

            byte[] jsonData = jObj.toString(1).getBytes("UTF-8");

            String payloadString = new String(jsonData, "UTF-8");
            byte[] payloadData = payloadString.getBytes("US-ASCII");
            String payloadBase64URLString = Base64.encodeToString(payloadData, 0);

            payloadBase64URLString = payloadBase64URLString.replace("\n", "");
            payloadBase64URLString = payloadBase64URLString.replace("=", "");
            payloadBase64URLString = payloadBase64URLString.replace("+", "-");
            payloadBase64URLString = payloadBase64URLString.replace("/", "_");

            String key = Normalizer.normalize(secret, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
            String data = Normalizer.normalize(payloadBase64URLString, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");

            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] hmacData = sha256_HMAC.doFinal(data.getBytes());
            String hash = Base64.encodeToString(hmacData, 0);

            hash = hash.replace("\n", "");
            hash = hash.replace("=", "");
            hash = hash.replace("+", "-");
            hash = hash.replace("/", "_");

            strFBSignedRequest = hash + "." + payloadBase64URLString;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onAuthSuccess(boolean isSuccess, String ticket, String errorMessage) {
        if (!isViewAttached()) {
            return;
        }

        if (isSuccess) {
            getDataManager().acknowledgeAuth(ticket);
            getMvpView().showLoginSuccessful(ticket);
        } else {
            getDataManager().revokeAuth();
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


}
