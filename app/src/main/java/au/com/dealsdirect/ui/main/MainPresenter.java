package au.com.dealsdirect.ui.main;
/*
 * Created by CodeineBot on 5/15/17.
 */


import android.content.Context;
import android.util.Log;

import com.androidnetworking.error.ANError;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;

import com.google.gson.Gson;
import com.mysale.genie.utility.config.api.GetAppSettings;
import com.mysale.genie.utility.config.api.GetAppSettingsSection;
import com.mysale.genie.utility.config.api.GetServerSettings;
import com.mysale.genie.utility.config.model.getappsettingssection.Android;
import com.mysale.genie.utility.config.model.getappsettingssection.Payload;
import com.newrelic.agent.android.NewRelic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentMethod;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentTransaction;
import au.com.dealsdirect.data.network.model.checkout.GetPaymentToken;
import au.com.dealsdirect.data.network.model.checkout.getpaymentmethodnonce.GetPaymentMethodNonceRequest;
import au.com.dealsdirect.data.network.model.legalities.GetTemplateTextsRequest;
import au.com.dealsdirect.data.network.model.login.LoginEmail;
import au.com.dealsdirect.data.network.model.login.LoginTicket;
import au.com.dealsdirect.data.network.model.login.Logout;
import au.com.dealsdirect.service.fcm.GNotification;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.AppEventHelper;
import au.com.dealsdirect.utils.CurrencyUtil;
import au.com.dealsdirect.utils.IntrospectionUtils;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.fabric.sdk.android.Fabric;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

public class MainPresenter<V extends MainMvpView> extends BasePresenter<V> implements MainMvpPresenter<V> {

    GNotification gNotification;

    public static final String KEY_CHECKOUT_MYPAY_PAY_EXCEED_LIMIT = "_checkoutMyPayPayExceedLimit";
    public static final String KEY_CHECKOUT_MYPAY_PAY_INVALID_PAYMENT_METHOD = "_checkoutMyPayPayInvalidPaymentMethod";
    public static final String KEY_CHECKOUT_MYPAY_PAY_OUT_OF_RANGE_MOBILE_APP = "_checkoutMyPayPayOutOfRangeMobileApp";
    public static final String KEY_CHECKOUT_MYPAY_PAY_OUT_UP_TO_MOBILE_APP = "_checkoutMyPayPayOutUpToMobileApp";
    public static final String KEY_CHECKOUT_MYPAY_PAY_UNTRUSTED = "_checkoutMyPayPayUntrusted";
    public static final String KEY_MYPAY_DETAILS_MOBILE_APP = "myPayDetailsMobileApp";
    public static final String KEY_OURPAY_THANK_YOU_TEXT = "_OurPayThankYouTextMobileApp";
    public static final String KEY_OURPAY_TC_TEXT = "_OurPayTC_text"; // Using web's template text for hyper link
    public static final String KEY_OURPAY_TC_VALIDATION_FAILED = "_OurPayTCValidationFailed";
    public static final String KEY_PAYMENT_SCHEDULE = "_PaymentSchedule";

    private static String[] templateTextsKeys = {
            KEY_CHECKOUT_MYPAY_PAY_EXCEED_LIMIT, //0
            KEY_CHECKOUT_MYPAY_PAY_INVALID_PAYMENT_METHOD, //1
            KEY_CHECKOUT_MYPAY_PAY_OUT_OF_RANGE_MOBILE_APP, //2
            KEY_CHECKOUT_MYPAY_PAY_OUT_UP_TO_MOBILE_APP, //3
            KEY_CHECKOUT_MYPAY_PAY_UNTRUSTED, //4
            KEY_MYPAY_DETAILS_MOBILE_APP, //5
            KEY_OURPAY_THANK_YOU_TEXT, //6
            KEY_OURPAY_TC_TEXT, //7
            KEY_OURPAY_TC_VALIDATION_FAILED, //8
            KEY_PAYMENT_SCHEDULE //9
    };

    @Inject
    public MainPresenter(DataManager dataManager,
                         SchedulerProvider schedulerProvider,
                         CompositeDisposable compositeDisposable) {

        super(dataManager, schedulerProvider, compositeDisposable);
        gNotification = new GNotification(getDataManager(), getSchedulerProvider(), getCompositeDisposable());
    }

    @Override
    public void callGetServerSettings() {
        getCompositeDisposable().add(getDataManager()
                .callGetServerSettings(getDataManager().getCountryId())
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<GetServerSettings.ResponseValue>() {
                    @Override
                    public void accept(@NonNull GetServerSettings.ResponseValue responseValue) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }

                        com.mysale.genie.utility.config.model.getserversettings.Value value = responseValue.d.getValue();
                        if (value != null) {
//                            getDataManager().setCountryId(responseValue.getCountryId());
                            getDataManager().setLanguageId(responseValue.getLanguages().get(0).getID());
                            Gson gson = new Gson();
                            getDataManager().setLanguages(gson.toJson(responseValue.getLanguages()));
                            getDataManager().setSiteName(responseValue.getSiteFullname());
                            getDataManager().setCurrency(responseValue.getCurrency());
                            getDataManager().setCurrencySign(responseValue.getCurrencySign());
                            getDataManager().setFollowUsFbLink(responseValue.getFollowUsFacebookLink());
                            getDataManager().setFollowUsTwitterLink(responseValue.getFollowUsTwitterLink());
                            getDataManager().setImageServerUrl(responseValue.getImageServerUrl());
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
    }

    @Override
    public void callGetAppSettings() {
        getCompositeDisposable().add(getDataManager()
                .callGetAppSettings(getDataManager().getCountryId())
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(mAppSettingsAcceptCallback, mAppSettingsThrowableCallback));
    }

    @Override
    public void callGetPublicAppSettings() {
        getCompositeDisposable().add(getDataManager()
                .callGetPublicAppSettings(getDataManager().getCountryId())
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(mAppSettingsAcceptCallback, mAppSettingsThrowableCallback));
    }

    private Consumer<GetAppSettings.ResponseValue> mAppSettingsAcceptCallback = new Consumer<GetAppSettings.ResponseValue>() {
        @Override
        public void accept(@NonNull GetAppSettings.ResponseValue responseValue) throws Exception {
            if (!isViewAttached()) {
                return;
            }

            com.mysale.genie.utility.config.model.getappsettings.Value value = responseValue.d.getValue();
            if (value != null) {
                getDataManager().setIsPaypalEnabled(value.getPayments().getPayPal().getEnabled());
                getDataManager().setIsMasterpassEnabled(value.getPayments().getMasterPass().getEnabled());
                getDataManager().setIsAmexEnabled(value.getPayments().getAmExpress().getEnabled());
                getDataManager().setIsKountEnabled(value.getPayments().getKount().getEnabled());
                getDataManager().setKountMerchantId(value.getPayments().getKount().getMerchantID());
                getDataManager().setSearchMaxPrice(value.getSearch().getMaxPrice());
                getDataManager().setAccessAnonymousEnabled(value.getAccess().getAnonymousEnabled());
                getDataManager().setIsMyPayEnabled(value.getPayments().getMyPay().getEnabled());
            }

        }
    };

    private Consumer<Throwable> mAppSettingsThrowableCallback = new Consumer<Throwable>() {
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
    };


    @Override
    public void callGetAppSettingsSection(Context context) {
        getCompositeDisposable().add(getDataManager()
                .callGetAppSettingsSection(getDataManager().getCountryId())
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<GetAppSettingsSection.ResponseValue>() {
                               @Override
                               public void accept(@NonNull GetAppSettingsSection.ResponseValue responseValue) throws Exception {
                                   if (!isViewAttached()) {
                                       return;
                                   }

                                   GetAppSettingsSection.ResponseValue.Value value = responseValue.d.getValue();
                                   if (value != null) {
                                       String version = value.getMobileApp().getVersionRules();
                                       version = version.replace("/", "");

                                       try {
                                           JSONObject jsonVersion = new JSONObject(version);

                                           ArrayList<Android> androidArrayList = new ArrayList<>();

                                           for (int i = 0; i < jsonVersion.getJSONObject("VersionRules").getJSONArray("Android").length(); i++) {
                                               JSONObject jsonAndroid = jsonVersion.getJSONObject("VersionRules").getJSONArray("Android").getJSONObject(i);
                                               Android android = new Android();
                                               android.setAction(jsonAndroid.getString("action"));
                                               android.setEvent(jsonAndroid.getString("event"));
                                               android.setVersionNo(jsonAndroid.getString("versionNo"));

                                               Payload payload = new Payload();

                                               try {
                                                   JSONObject jsonPayload = jsonAndroid.getJSONObject("payload");
                                                   payload.setMessage(jsonPayload.getString("title"));
                                                   payload.setTitle(jsonPayload.getString("message"));

                                                   try {
                                                       payload.setUrl(jsonPayload.getString("url"));
                                                   } catch (Exception e) {
                                                       e.printStackTrace();
                                                       payload.setUrl(null);
                                                   }

                                               } catch (Exception e) {
                                                   e.printStackTrace();
                                                   payload = null;
                                               }

                                               android.setPayload(payload);

                                               androidArrayList.add(android);
                                           }

                                           if (!androidArrayList.isEmpty()) {
                                               IntrospectionUtils.checkVersion(context, androidArrayList);
                                           }
                                       } catch (JSONException e) {
                                           e.printStackTrace();
                                       }
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
                           }

                ));
    }

    @Override
    public void fetchBTAuthorization() {
        if (!getDataManager().isAuthorized()) return;

        getCompositeDisposable().add(getDataManager()
                .callGetPaymentToken(new GetPaymentToken.RequestValue(getDataManager().getLanguageId(), getDataManager().getCountryId()))
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<GetPaymentToken.ResponseValue>() {
                    @Override
                    public void accept(@NonNull GetPaymentToken.ResponseValue responseValue) throws Exception {

                        PaymentInfo.setIsTokenFetching(false);

                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();

                        if (responseValue.isResult() && responseValue.isAuthenticated()) {
                            getMvpView().onAuthorizationFetched(responseValue.getPaymentToken(), responseValue.getPaymentType());

                            if (getMvpView().getFetchTokenHandler() != null)
                                getMvpView().getFetchTokenHandler().onSuccess();
                        } else {
                            if (getMvpView().getFetchTokenHandler() != null)
                                getMvpView().getFetchTokenHandler().onFailure();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        PaymentInfo.setIsTokenFetching(false);

                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();

                        if (getMvpView().getFetchTokenHandler() != null)
                            getMvpView().getFetchTokenHandler().onFailure();

                        getMvpView().onError(throwable.getMessage());

                        // handle load accounts error here
                        if (throwable instanceof ANError) {
                            ANError anError = (ANError) throwable;
                            handleApiError(anError);
                        }
                    }
                })
        );

    }

    @Override
    public void callGetPaymentMethodNonce(String token) {
        doApiCallForResponse(getDataManager().callGetPaymentMethodNonce(new GetPaymentMethodNonceRequest(token)),
                new AppApiCallback() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);

                        if (response instanceof JSONObject) {
                            try {
                                JSONObject jsonResponse = ((JSONObject) response).getJSONObject("d");

                                if (jsonResponse.getBoolean("IsAuthenticated") && jsonResponse.getBoolean("Result")) {
                                    getMvpView().showGetPaymentMethodNonceSuccess(jsonResponse.getJSONObject("Value").getString("Nonce"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    @Override
    public boolean isDebug() {
        return getDataManager().isDebugMode();
    }

    @Override
    public void callGCMNotificationEvent(Context context) {
        gNotification.callNotificationEvent(context);
    }

    @Override
    public void callApiSettings(Context context) {
        callGetServerSettings();
        callGetPublicAppSettings();
        callGetAppSettingsSection(context);
    }

    @Override
    public void initFacebookAnalytics() {

        // Allow debugging logs if debug mode
        if (getDataManager().isDebugMode()) {
            FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS);
        }
    }

    @Override
    public void initializeAnalytics(Context activityContext, Context applicationContext) {

        // Only activate analytics for release versions
        if (getDataManager().isDebugMode()) {

            // Fabric
            Fabric.with(activityContext, new Crashlytics());
            Fabric.with(activityContext, new Answers());

            // New Relic
            NewRelic.withApplicationToken(activityContext.getResources().getString(R.string.new_relic_app_token)).start(applicationContext);

            // Facebook Events
            initFacebookAnalytics();
        }
    }

    @Override
    public void createPaymentTransaction(String deviceData, String paymentType, String paymentNonce, String paymentToken) {
        getMvpView().showLoading();

        String languageId = getDataManager().getLanguageId();
        String countryId = getDataManager().getCountryId();
        CreatePaymentTransaction.RequestValue.Request requestValue =
                new CreatePaymentTransaction.RequestValue.Request(paymentType, paymentNonce, paymentToken, deviceData);
        getCompositeDisposable().add(getDataManager()
                .callCreatePaymentTransaction(new CreatePaymentTransaction.RequestValue(requestValue, countryId, languageId))
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<CreatePaymentTransaction.ResponseValue>() {
                    @Override
                    public void accept(@NonNull CreatePaymentTransaction.ResponseValue responseValue) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();
                        getMvpView().performResetWithAuthFetch();

                        if (responseValue.getD().getResult()) {
                            getMvpView().showCreatePaymentTransactionSuccess(paymentType, responseValue);
                            AppEventHelper.completedPurchase(paymentType,
                                    responseValue.getD().getValue().getOrderInfoResult().getItems().size(),
                                    responseValue.getD().getValue().getOrderInfoResult().getTotal(),
                                    getDataManager().getCountryId());
                        } else {
                            getMvpView().showCreatePaymentTransactionFailure(responseValue.getD().getMessage());
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
                })
        );


    }

    @Override
    public void createPaymentMethod(String deviceData, String paymentNonce, String paymentType) {

        String languageId = getDataManager().getLanguageId();
        String countryId = getDataManager().getCountryId();

        CreatePaymentMethod.RequestValue.Request requestValue = new CreatePaymentMethod.RequestValue.Request(paymentType, paymentNonce, deviceData);
        getCompositeDisposable().add(getDataManager()
                        .callCreatePaymentMethod(new CreatePaymentMethod.RequestValue(requestValue, countryId, languageId))
                        .subscribeOn(getSchedulerProvider().io())
                        .observeOn(getSchedulerProvider().ui())
                        .subscribe(new Consumer<CreatePaymentMethod.ResponseValue>() {
                            @Override
                            public void accept(@NonNull CreatePaymentMethod.ResponseValue responseValue) throws Exception {
                                if (!isViewAttached()) {
                                    return;
                                }

                                getMvpView().hideLoading();
                                getMvpView().performResetWithAuthFetch();

                                if ((responseValue.getResult() && responseValue.getIsAuthenticated())) {
                                    getMvpView().showCreatePaymentMethodSuccess(responseValue.getD().getValue().getLastPaymentMethod());
                                    AppEventHelper.addedPaymentInfo(responseValue.getD().getValue().getLastPaymentMethod().getPaymentType());
                                } else {
                                    getMvpView().onError(responseValue.getMessage());
                                }
//
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
                        })
        );
    }

    @Override
    public void callLoginTicket() {
        String loginTicket = getDataManager().getLoginTicket();
        if (!loginTicket.isEmpty()) {
            getCompositeDisposable().add(getDataManager()
                    .callLoginTicket(new LoginTicket.RequestValue(loginTicket, getDataManager().getCountryId()))
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
                                // Call required post login api methods
                                getMvpView().loginSuccessMethods();
                            } else {
                                //On login ticket fail, call logout and go back to shop
                                callLogout(null);
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
                    })
            );
        }
    }

    @Override
    public void callLogout(AuthHandler handler) {

        getMvpView().showLoading();
        getCompositeDisposable().add(getDataManager()
                .callLogout(new Logout.RequestValue())
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<Logout.ResponseValue>() {
                    @Override
                    public void accept(@NonNull Logout.ResponseValue responseValue) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();

                        //Remove login ticket
                        getDataManager().revokeAuth();
                        //Clear payment info
                        PaymentInfo.resetPaymentInfo();
                        //Clear braintree
                        getMvpView().performBraintreeReset();
                        //Call Public App Settings
                        callGetPublicAppSettings();

                        if (handler != null) {
                            handler.success();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }

                        handler.error();
                        getMvpView().hideLoading();
                        getMvpView().onError(throwable.getMessage());

                        // handle load accounts error here
                        if (throwable instanceof ANError) {
                            ANError anError = (ANError) throwable;
                            handleApiError(anError);
                        }
                    }
                })
        );
    }


    @Override
    public void callGetTemplateTexts() {
        GetTemplateTextsRequest getTemplateTextRequest = new GetTemplateTextsRequest();
        getTemplateTextRequest.templateKeys = templateTextsKeys;

        getTemplateTextRequest.countryId = getDataManager().getCountryId();
        getTemplateTextRequest.languageId = getDataManager().getLanguageId();
        getCompositeDisposable().add(getDataManager().callGetTemplateTexts(getTemplateTextRequest)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(getTemplateTextsResponse -> {
                    getDataManager().setMyPayTemplateTexts(getTemplateTextsResponse.getResponse().getValue());
                    getMvpView().storeTemplateTexts(getTemplateTextsResponse.getResponse().getValue());

                }, throwable -> {
                    Log.d("mainpresenter", " getTemplatetexts failed = " + throwable.getMessage());
                }));
    }

    @Override
    public String getStoredTemplateTexts(String detailKey) {
        return getDataManager().getMyPayTemplateTexts(detailKey);
    }

    @Override
    public void initializeNotifications(Context context) {
        if (gNotification != null) {
            gNotification.registerDeviceForNotification(context);
        }
    }

    @Override
    public String getKountMerchantId() {
        return getDataManager().isKountEnabled() ? getDataManager().getKountMerchantId() : "";
    }

    @Override
    public boolean isAuthorized() {
        return getDataManager().isAuthorized();
    }


    private static JSONArray constructArrayToJsonArray(String[] templateTextsKeys) {

        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < templateTextsKeys.length; i++) {
            try {
                jsonArray.put(i, templateTextsKeys[i]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }

    public boolean getIsMyPayEnabled() {
        return getDataManager().getIsMyPayEnabled();
    }
}