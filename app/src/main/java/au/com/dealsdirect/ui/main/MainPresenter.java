package au.com.dealsdirect.ui.main;

import android.content.Context;
import android.util.Log;

import com.androidnetworking.error.ANError;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.mysale.genie.utility.config.api.GetAppSettings;
import com.mysale.genie.utility.config.api.GetAppSettingsConsent;
import com.mysale.genie.utility.config.api.GetAppSettingsSection;
import com.mysale.genie.utility.config.api.GetServerSettings;
import com.mysale.genie.utility.config.model.getappsettingssection.Android;
import com.mysale.genie.utility.config.model.getappsettingssection.Payload;
import com.mysale.genie.utility.config.model.getpublicpaymenttoken.GetPublicPaymentToken;
import com.newrelic.agent.android.NewRelic;
import com.visa.checkout.VisaPaymentSummary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.BuildConfig;
import au.com.dealsdirect.R;
import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.accountdata.AccountData;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentIntentStripe;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentMethod;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentMethodStripe;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentTransaction;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentTransactionGPay;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentTransactionStripe;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentTransactionVco;
import au.com.dealsdirect.data.network.model.checkout.GetPaymentToken;
import au.com.dealsdirect.data.network.model.checkout.getpaymentmethodnonce.GetPaymentMethodNonceRequest;
import au.com.dealsdirect.data.network.model.gdpr.consentdata.GetConsentDataResponse;
import au.com.dealsdirect.data.network.model.legalities.GetTemplateTextsRequest;
import au.com.dealsdirect.data.network.model.legalities.TemplateTextResponse;
import au.com.dealsdirect.data.network.model.login.LoginEmail;
import au.com.dealsdirect.data.network.model.login.LoginTicket;
import au.com.dealsdirect.data.network.model.login.Logout;
import au.com.dealsdirect.data.network.model.productdetails.GetPostcodeDefaultResponse;
import au.com.dealsdirect.data.network.model.returns.FileSettingsResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.BuyBoxTemplateTextResponse;
import au.com.dealsdirect.data.pref.AppPreferencesHelper;
import au.com.dealsdirect.data.wishlist.WishlistObject;
import au.com.dealsdirect.service.datacollection.core.DataCollector;
import au.com.dealsdirect.service.datacollection.enums.Events;
import au.com.dealsdirect.service.datacollection.registerservices.GenieEventService;
import au.com.dealsdirect.service.fcm.GNotification;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.ui.controller.main.Settings;
import au.com.dealsdirect.ui.custom.SupplierOriginalPriceInfoHelper;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.CookieUtils;
import au.com.dealsdirect.utils.GdprUtils;
import au.com.dealsdirect.utils.LoadingDialogType;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import okhttp3.Cookie;

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
    public static final String KEY_PERSONALISATION_VALIDATION = "_PleaseFillPersonalization";

    //    DELIVERY OPTIONS/OURPAY SELECT
    public static final String KEY_DELIVERYOPTION_OPS_FREE = "_Free";
    public static final String KEY_DELIVERYOPTION_OPS_TITLE = "_DeliveryOption_OURPAYSELECT_Title";
    public static final String KEY_DELIVERYOPTION_OPS_DESCRIPTION = "_DeliveryOption_OURPAYSELECT_Description";

    public static final String KEY_DELIVERYOPTION_EXPRESS_TITLE = "_DeliveryOption_EXPRESS_Title";
    public static final String KEY_DELIVERYOPTION_EXPRESS_DESCRIPTION = "_DeliveryOption_EXPRESS_Description";
    public static final String KEY_DELIVERYOPTION_STANDARD_TITLE = "_DeliveryOption_STANDARD_Title";

    public static final String KEY_OURPAY_OPS_DESCRIPTION_REMAINING = "_Ops_description_remaining";
    public static final String KEY_OURPAY_OPS_INFO_REMAINING_BEFORE_PURCHASE = "_Ops_info_remaining_before_purchase";
    public static final String KEY_OURPAY_OPS_INFO_REMAINING_BEFORE_PURCHASE_FREE_DELIVERY = "_Ops_info_remaining_before_purchase_free_delivery";

    public static final String KEY_OURPAY_OPS_TNC_HEADER = "_OurPaySelectTermsAndConditionsHeader";
    public static final String KEY_OURPAY_OPS_TNC_BODY = "_OurPaySelectTermsAndConditionsBody";

    public static final String KEY_OURPAY_UNAVAILABLE_MESSAGE = "_OurPayUnavailableMessage";

    /* June 22, 2018 - GDPR Template Text Keys */
    public static final String KEY_CONSENT_CONTINUE_TEXT = "_consentContinueText";
    public static final String KEY_CONSENT_WITH_REGISTRATION_TERMS_TEXT = "_consentWithTCText";
    public static final String KEY_CONSENT_WITH_REGISTRATION_EMAILS_TEXT = "_consentWithEmailsText";
    public static final String KEY_CONSENT_WITH_REGISTRATION_TERMS_WARNING = "_consentWithRegistrationTermsWarning";
    public static final String KEY_CONSENT_SHORT_TEXT = "ConsentShortTextPTNameV1";
    public static final String KEY_CONSENT_FULL_TEXT = "ConsentFullTextPTNameV1";
    public static final String KEY_CONSENT_TERMS_AND_CONDITION = "TermsAndConditions_Text";

    // Voucher Keys
    public static final String KEY_VOUCHER_STATUS_NEW = "_VoucherNew";
    public static final String KEY_VOUCHER_STATUS_ALREADY_SPENT = "_AlreadySpent";
    public static final String KEY_VOUCHER_STATUS_EXPIRING_SOON = "_VoucherExpiringSoon";
    public static final String KEY_VOUCHER_STATUS_EXPIRED = "_VoucherExpired";
    public static final String KEY_VOUCHER_STATUS_PENDING = "_VoucherPending";

    public static final String KEY_SHIPPING_RULES_HOVER = "_Shipping_Rules_hover";
    public static final String KEY_SHIPPING_RULES_HOVER_TITLE = "_Shipping_Rules_hover_title";

    // Estimate Delivery price by warehouse and delivery locations
    static final String KEY_IMPOSSIBLE_TO_DELIVER_AT_LOCATION = "_ImpossibleToDeliverAtLocation";
    static final String KEY_UNAVAILABLE = "_Unavailable";
    static final String KEY_CALCULATE = "_Calculate";
    static final String KEY_IMPOSSIBLE_TO_DELIVER_AT_LOCATION_MESSAGE = "_ImpossibleToDeliverAtLocation_Message";
    static final String KEY_AGE_RESTRICTED_TEXT = "_AgeRestrictedText";
    static final String KEY_PLEASE_CONFIRM_AGE_RESTRICTED_TEXT = "_PleaseConfirmAgeRestrictedText";

    static final String KEY_KLARNA_DESCRIPTION_TEXT = "_KlarnaDescription";

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
            KEY_PAYMENT_SCHEDULE, //9
            KEY_DELIVERYOPTION_OPS_FREE,
            KEY_DELIVERYOPTION_OPS_TITLE,
            KEY_DELIVERYOPTION_OPS_DESCRIPTION,
            KEY_DELIVERYOPTION_EXPRESS_TITLE,
            KEY_DELIVERYOPTION_EXPRESS_DESCRIPTION,
            KEY_DELIVERYOPTION_STANDARD_TITLE,
            KEY_OURPAY_OPS_DESCRIPTION_REMAINING,
            KEY_OURPAY_OPS_INFO_REMAINING_BEFORE_PURCHASE,
            KEY_OURPAY_OPS_INFO_REMAINING_BEFORE_PURCHASE_FREE_DELIVERY,
            KEY_OURPAY_OPS_INFO_REMAINING_BEFORE_PURCHASE_FREE_DELIVERY,
            KEY_OURPAY_OPS_TNC_HEADER,
            KEY_OURPAY_OPS_TNC_BODY,
            KEY_OURPAY_UNAVAILABLE_MESSAGE,
            KEY_PERSONALISATION_VALIDATION, //10
            KEY_CONSENT_CONTINUE_TEXT,
            KEY_CONSENT_WITH_REGISTRATION_TERMS_TEXT,
            KEY_CONSENT_WITH_REGISTRATION_EMAILS_TEXT,
            KEY_CONSENT_WITH_REGISTRATION_TERMS_WARNING,
            KEY_CONSENT_SHORT_TEXT,
            KEY_CONSENT_FULL_TEXT,
            KEY_CONSENT_TERMS_AND_CONDITION,
            KEY_VOUCHER_STATUS_NEW,
            KEY_VOUCHER_STATUS_ALREADY_SPENT,
            KEY_VOUCHER_STATUS_EXPIRED,
            KEY_VOUCHER_STATUS_EXPIRING_SOON,
            KEY_VOUCHER_STATUS_PENDING,
            KEY_SHIPPING_RULES_HOVER,
            KEY_SHIPPING_RULES_HOVER_TITLE,
            KEY_IMPOSSIBLE_TO_DELIVER_AT_LOCATION,
            KEY_UNAVAILABLE,
            KEY_CALCULATE,
            KEY_IMPOSSIBLE_TO_DELIVER_AT_LOCATION_MESSAGE,
            KEY_AGE_RESTRICTED_TEXT,
            KEY_PLEASE_CONFIRM_AGE_RESTRICTED_TEXT,
            KEY_KLARNA_DESCRIPTION_TEXT
    };

    @Inject
    public MainPresenter(DataManager dataManager,
                         SchedulerProvider schedulerProvider,
                         CompositeDisposable compositeDisposable) {

        super(dataManager, schedulerProvider, compositeDisposable);
        gNotification = new GNotification(getDataManager(), getSchedulerProvider(), getCompositeDisposable());
        dataManager.resetAddToCartJourneyFlags();
    }

    @Override
    public void onAttach(V mvpView) {
        super.onAttach(mvpView);
        getDataManager().setWishlistChangeListener(newCount -> getMvpView().updateWishlistCounter(newCount));
    }

    @Override
    public void onDetach() {
        if (getDataManager() != null) {
            getDataManager().setWishlistChangeListener(null);
        }
        super.onDetach();
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
                            getDataManager().setCountryIso(value.getCountry().getIso());
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
    public void callGetPublicPaymentToken() {
        getCompositeDisposable().add(getDataManager()
                .callGetPublicPaymentToken(getDataManager().getCountryId(), getDataManager().getLanguageId())
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<GetPublicPaymentToken.ResponseValue>() {
                    @Override
                    public void accept(GetPublicPaymentToken.ResponseValue responseValue) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }

                        GetPublicPaymentToken.ResponseValue.Value value = responseValue.d.getValue();
                        if (value != null) {
                            getDataManager().setPublicPaymentToken(value.getToken());
                            getDataManager().setPublicPaymentType(value.getPaymentType());
                        }
                    }
                }, mAppSettingsThrowableCallback));
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
                getDataManager().setIsPaypalEnabled(value.getPayments().getBrainTree().getPayPalEnabled());
                getDataManager().setIsMasterpassEnabled(value.getPayments().getMasterPass().getEnabled());
                getDataManager().setIsAmexEnabled(value.getPayments().getAmExpress().getEnabled());
                getDataManager().setIsKountEnabled(value.getPayments().getKount().getEnabled());
                getDataManager().setKountMerchantId(value.getPayments().getKount().getMerchantID());
                getDataManager().setSearchMaxPrice(value.getSearch().getMaxPrice());
                getDataManager().setAccessAnonymousEnabled(value.getAccess().getAnonymousEnabled());
                getDataManager().setIsMyPayEnabled(value.getPayments().getMyPay().getEnabled());
                getDataManager().setIsPaypalCreditEnabled(value.getPayments().getBrainTree().isPaypalCreditEnabled());
                getDataManager().setIsOurpayDashboardEnabled(value.getMyAccount().isShowOurpaySchedulerInMyAccount());
                getDataManager().setShippingByPostcodeEnabled(value.getCheckout().getShippingByPostcodeEnabled());
                getDataManager().setMobilePhoneBannerColumns(value.getShop().getMobilePhoneBannerColumns());
                getDataManager().setMobileTabletBannerColumns(value.getShop().getMobileTabletBannerColumns());
                getDataManager().setProductPagePriceBlockMode(value.getProductPage().getPriceBlockMode() != null ?
                        value.getProductPage().getPriceBlockMode() : 0);
                getDataManager().setHoursLeftToDisplayTimer(value.getProductPage().getHoursLeftToDisplayTimer() != null ?
                        value.getProductPage().getHoursLeftToDisplayTimer() : 48);

                if (value.getPayments().getVisaCheckout() != null) {
                    getDataManager().setIsVisaCheckoutEnabled(value.getPayments().getVisaCheckout().getVisaCheckoutEnabled());
                    // force true meanwhile
//                    getDataManager().setIsVisaCheckoutEnabled(true);
                    getDataManager().setVisaCheckoutApiKey(value.getPayments().getVisaCheckout().getVisaCheckoutApiKey());
                    getDataManager().setVisaCheckoutApiUrl(value.getPayments().getVisaCheckout().getVisaCheckoutApiUrl());
                    getDataManager().setVisaCheckoutProviderType(value.getPayments().getVisaCheckout().getVisaCheckoutProviderType());
                }

                if (value.getPayments().getStripe() != null) {
                    getDataManager().setStripePublicKey(value.getPayments().getStripe().getStripePublicKey());
                    getDataManager().setStripeEnabled(value.getPayments().getStripe().isStripeEnabled());
                } else {
                    getDataManager().setStripePublicKey(null);
                    getDataManager().setStripeEnabled(false);
                }

            }

            getMvpView().onGetAppSettings();
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

    private Consumer<AccountData> mAccountDataCallback = new Consumer<AccountData>() {
        @Override
        public void accept(AccountData accountData) throws Exception {
            if (!isViewAttached()) {
                return;
            }

            if (accountData != null) {
                getDataManager().setIsSortingEnabled(accountData.getSorting().getIsEnabled());

                getDataManager().setIsOurpayEnabled(accountData.getOurPay().isEnabled());
                getDataManager().setIsAfterpayEnabled(accountData.getAfterpay().isEnabled());

                getDataManager().setSupplierOriginalPriceInfoEnabled(accountData.getSupplierOriginalPriceInfo().isEnabled());
            }
        }
    };

    @Override
    public void callGetAppSettingsSection(Context context) {
        getCompositeDisposable().add(getDataManager()
                .callGetAppSettingsSection(getDataManager().getCountryId(), null)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(mAppSettingsSectionAcceptCallback, mAppSettingsSectionThrowableCallback));
    }

    @Override
    public void callGetPublicAppSettingsSections(Context context) {
        getCompositeDisposable().add(getDataManager()
                .callGetPublicAppSettingsSections(getDataManager().getCountryId())
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(mAppSettingsSectionAcceptCallback, mAppSettingsSectionThrowableCallback));
    }

    @Override
    public void callGetPublicAppSettingsSectionsAfterpay(Context context) {
        getCompositeDisposable().add(getDataManager()
                .callGetPublicAppSettingsSections(getDataManager().getCountryId(), "Afterpay")
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(mAppSettingsSectionAcceptAfterpayCallback, mAppSettingsSectionThrowableCallback));
    }

    @Override
    public void callGetAppSettingsSectionsPayments(Context context) {
        getCompositeDisposable().add(getDataManager()
                .callGetAppSettingsSection(getDataManager().getCountryId(), "Payments")
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(mAppSettingsSectionAcceptPaymentsCallback, mAppSettingsSectionThrowableCallback));
    }

    private Consumer<GetAppSettingsSection.ResponseValue> mAppSettingsSectionAcceptPaymentsCallback = new Consumer<GetAppSettingsSection.ResponseValue>() {
        @Override
        public void accept(GetAppSettingsSection.ResponseValue responseValue) throws Exception {
            if (!isViewAttached()) {
                return;
            }
            GetAppSettingsSection.ResponseValue.Value value = responseValue.d.getValue();
            if (value == null) {
                return;
            }
            GetAppSettingsSection.ResponseValue.Payments payments = value.getPayments();
            if (payments == null) {
                return;
            }
            getDataManager().setKlarnaEnabled(payments.getKlarna().isEnabled());
        }
    };

    @Override
    public void callGetPublicAppSettingsSectionsLPay(Context context) {
        getCompositeDisposable().add(getDataManager()
                .callGetPublicAppSettingsSections(getDataManager().getCountryId(), "LPay")
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(mAppSettingsSectionAcceptLPayCallback, mAppSettingsSectionThrowableCallback));
    }

    public void callGetAppSettingsSectionsZipPay(Context context) {
        getCompositeDisposable().add(getDataManager()
                .callGetAppSettingsSection(getDataManager().getCountryId(), "ZipPay")
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(mAppSettingsSectionAcceptZipPayCallback, mAppSettingsSectionThrowableCallback));
    }

    private final Consumer<GetAppSettingsSection.ResponseValue> mAppSettingsSectionAcceptAfterpayCallback = new Consumer<GetAppSettingsSection.ResponseValue>() {
        @Override
        public void accept(@NonNull GetAppSettingsSection.ResponseValue responseValue) throws Exception {
            if (!isViewAttached()) {
                return;
            }
            getDataManager().setAfterpayScriptUri(responseValue.d.getValue().getAfterpay().getScriptUri());

            String lightboxImageUrl = responseValue.d.getValue().getAfterpay().getLightboxImgUrl();
            if (lightboxImageUrl == null || lightboxImageUrl.isEmpty()) {
                // server links not yet implemented
                lightboxImageUrl = "https://www.ozsale.com.au/Res/Default/Img/Checkout/afterpay-lightbox.png";
            }

            String termsLink = responseValue.d.getValue().getAfterpay().getTermsLink();
            if (termsLink == null || termsLink.isEmpty()) {
                // server links not yet implemented
                termsLink = "https://www.afterpay.com/terms-of-service";
            }

            getDataManager().setAfterpayLightboxImgUrl(lightboxImageUrl);
            getDataManager().setAfterpayTermsLink(termsLink);
        }
    };

    private final Consumer<GetAppSettingsSection.ResponseValue> mAppSettingsSectionAcceptLPayCallback = new Consumer<GetAppSettingsSection.ResponseValue>() {
        @Override
        public void accept(@NonNull GetAppSettingsSection.ResponseValue responseValue) throws Exception {
            if (!isViewAttached()) {
                return;
            }

            if (responseValue.d == null || responseValue.d.getValue() == null || responseValue.d.getValue().getlPay() == null) {
                return;
            }

            boolean isEnabled = responseValue.d.getValue().getlPay().isEnabled();

            getDataManager().setLPayEnabled(isEnabled);
        }
    };

    private final Consumer<GetAppSettingsSection.ResponseValue> mAppSettingsSectionAcceptZipPayCallback = new Consumer<GetAppSettingsSection.ResponseValue>() {
        @Override
        public void accept(@NonNull GetAppSettingsSection.ResponseValue responseValue) throws Exception {
            if (!isViewAttached()) {
                return;
            }

            if (responseValue.d == null || responseValue.d.getValue() == null || responseValue.d.getValue().getZipPay() == null) {
                return;
            }

            boolean isEnabled = responseValue.d.getValue().getZipPay().isEnabled();

            getDataManager().setZipPayEnabled(isEnabled);
        }
    };

    private final Consumer<GetAppSettingsSection.ResponseValue> mAppSettingsSectionAcceptCallback = new Consumer<GetAppSettingsSection.ResponseValue>() {
        @Override
        public void accept(@NonNull GetAppSettingsSection.ResponseValue responseValue) throws Exception {
            if (!isViewAttached()) {
                return;
            }

            GetAppSettingsSection.ResponseValue.Value value = responseValue.d.getValue();
            if (value != null) {
                String version = value.getMobileApp().getVersionRules();
                version = version.replace("/", "");

                getDataManager().setIsGoogleAdsEnabled(Boolean.parseBoolean(value.getMobileApp().getGoogleAdEnabled()));

                getDataManager().setIsColorFilterEnabled(value.getMobileApp().getMobileEnableColorFilter());

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
                            payload.setTitle(jsonPayload.getString("title"));
                            payload.setMessage(jsonPayload.getString("message"));

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
                        getMvpView().showIntrospectionUtils(androidArrayList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private final Consumer<Throwable> mAppSettingsSectionThrowableCallback = new Consumer<Throwable>() {
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
    public void callGetAppSettingsConsent(Context context) {
        doApiCallForResponse(getDataManager().callGetAppSettingsConsent(
                getDataManager().getCountryId()), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);

                GetAppSettingsConsent.ResponseValue mapper = (GetAppSettingsConsent.ResponseValue) response;

                getDataManager().setAppSettingsConsent(mapper);

                checkConsentCookie();
            }
        });
    }

    @Override
    public void callGetPublicAppSettingsConsent(Context context) {
        doApiCallForResponse(getDataManager().callGetPublicAppSettingsConsent(
                getDataManager().getCountryId()), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);

                GetAppSettingsConsent.ResponseValue mapper = (GetAppSettingsConsent.ResponseValue) response;

                getDataManager().setAppSettingsConsent(mapper);

                checkConsentCookie();
            }
        });
    }

    @Override
    public void checkConsentCookie() {

        int consentMode = getDataManager().getAppSettingsConsentMode();

        if (consentMode != GdprUtils.SOFT_MODE && consentMode != GdprUtils.STRICT_MODE) return;

        boolean hasConsentCookie = false;
        String csCookieValue = "";
        String k0 = "";
        String k1 = "";
        String k2 = "";

        int prevMode = -1;

        for (Cookie cookie : CookieUtils.getInstance().getCookieSet()) {

            hasConsentCookie = cookie.name().contains("cs") &&
                    cookie.value().equals(Integer.toString(consentMode));

            if (hasConsentCookie) {
                csCookieValue = cookie.value();

                String[] parts = csCookieValue.split("&");
                for (String part : parts) {
                    int chartAt = part.indexOf("=");
                    String finalValue = part.substring(chartAt);
                    if (finalValue.contains("k0")) {
                        k0 = finalValue;
                    } else if (finalValue.contains("k1")) {
                        k1 = finalValue;
                    } else if (finalValue.contains("k2")) {
                        k2 = finalValue;
                    }
                }

                break;
            }
        }

        String cookiePageTemplateName = k1;
        String settingPageTemplateName = getDataManager().getAppSettingsConsentText(
                AppPreferencesHelper.CONSENT_FULL_TEXT);

        if (!hasConsentCookie || !cookiePageTemplateName.equals(settingPageTemplateName)) {
            callGetConsentData();
        } else {
            getMvpView().onClickAgreeStrictConsentUI();
        }
    }

    @Override
    public void callGetConsentData() {
        doApiCallForResponse(getDataManager().callGetConsentData(getDataManager().getCountryId()),
                new AppApiCallback() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);

                        GetConsentDataResponse mapper = (GetConsentDataResponse) response;

                        if (mapper.getShowConsentRequired()) {
                            getDataManager().setShouldShowStrictConsent(true);
                        }

                        showStrictConsentUI();
                    }
                });
    }

    @Override
    public void callSaveConsentData() {
        doApiCallForResponse(getDataManager().callSaveConsentData(getDataManager().getCountryId()),
                new AppApiCallback() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);

                        getDataManager().setShouldShowStrictConsent(false);
                    }
                });
    }

    @Override
    public void callGetWishlistIdsOnly() {
        doApiCallForResponse(getDataManager().callGetWishlistIdsOnly(), new AppApiCallback() {
            @Override
            public void onSuccess(List<?> response) {
                super.onSuccess(response);
                getDataManager().setWishlist((List<WishlistObject>) response);
            }
        });
    }

    @Override
    public void showStrictConsentUI() {
        getMvpView().showStrictConsentUI();
    }

    @Override
    public void fetchBraintreeClientToken() {
        if (!getDataManager().isAuthorized()) return;

        final GetPaymentToken.RequestValue request = new GetPaymentToken.RequestValue(
                getDataManager().getLanguageId(),
                getDataManager().getCountryId());

        doApiCallForResponse(
                getDataManager().callGetPaymentToken(request),
                new AppApiCallback() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);

                        PaymentInfo.setIsTokenFetching(false);

                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();

                        final GetPaymentToken.ResponseValue responseValue = (GetPaymentToken.ResponseValue) response;
                        if (responseValue.isResult() && responseValue.isAuthenticated()) {

                            getDataManager().setCurrentPaymentToken(responseValue.getPaymentToken());
                            getDataManager().setCurrentPaymentType(responseValue.getPaymentType());

                            getMvpView().onBraintreeAuthorizationFetchSuccess(responseValue.getPaymentToken(), responseValue.getPaymentType());
                        } else {
                            getMvpView().onBraintreeAuthorizationFetchFail();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        super.onFailure(t);

                        PaymentInfo.setIsTokenFetching(false);

                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();

                        getMvpView().onBraintreeAuthorizationFetchFail();
                        getMvpView().onError(t.getMessage());

                        // handle load accounts error here
                        if (t instanceof ANError) {
                            ANError anError = (ANError) t;
                            handleApiError(anError);
                        }
                    }
                }
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
    public String defaultCountryId() {
        return getDataManager().getCountryId();
    }

    @Override
    public void setCountry(Settings.Country country) {
        getDataManager().setCountryId(country.countryId);
        getDataManager().setLanguageId(country.languageId);
    }

    @Override
    public String legacyCountryId() {
        return getDataManager().getLegacyCountryId();
    }

    @Override
    public void setUserRateCurrentVersion(boolean userRateCurrentVersion) {
        getDataManager().setUserHasRateApp(userRateCurrentVersion);
    }

    @Override
    public boolean isUserRateCurrentVersion() {
        return getDataManager().userHasRateApp();
    }

    @Override
    public boolean shouldShowStrictConsent() {
        return getDataManager().shouldShowStrictConsent();
    }

    @Override
    public void callFileSettings() {
        doApiCallForResponse(getDataManager().callGetFileSettings(),
                new AppApiCallback() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);

                        if (response != null) {
                            FileSettingsResponse fileSettingsResponse = (FileSettingsResponse) response;
                            getDataManager().setFileSizeLimit(fileSettingsResponse.getFileSizeLimit());
                        }

                    }
                });
    }

    @Override
    public boolean doesCheckoutHaveWishlistItem() {
        return getDataManager().doesCheckoutHaveWishlistItem();
    }

    @Override
    public String stripePublicKey() {
        return getDataManager().getStripePublicKey();
    }

    @Override
    public void setPaymentMethodId(String paymentMethodId) {
        getDataManager().setStripePaymentMethodId(paymentMethodId);
    }

    @Override
    public boolean isStripeEnabled() {
        return getDataManager().isStripeEnabled();
    }

    @Override
    public void storeCachedResponses() {
        getDataManager().storeCache();
    }

    @Override
    public void fetchCachedResponses() {
        getDataManager().fetchCache();
    }

    @Override
    public void pruneCachedResponses() {
        getDataManager().pruneCachedResponses();
    }

    @Override
    public void callGCMNotificationEvent(Context context) {
        gNotification.callNotificationEvent(context);
    }

    @Override
    public void callApiSettings(Context context) {
        callGetServerSettings();
        callGetPublicAppSettings();
        callGetPublicPaymentToken();
        callGetAppSettingsSection(context);
        callGetAppSettingsConsent(context);
        callGetAccountData();
    }

    @Override
    public void callGetAccountData() {
        GenieEventService genieEventService = new GenieEventService(getDataManager(), getSchedulerProvider(), getCompositeDisposable());
        getCompositeDisposable().add(getDataManager()
                .callGetAccountData()
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(mAccountDataCallback, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        AppLogger.d(throwable.getMessage());
                    }
                }));
    }

    @Override
    public void initFacebookAnalytics() {
        FacebookSdk.setIsDebugEnabled(getDataManager().isDebugMode());
        FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS);
    }

    @Override
    public void initializeAnalytics(Context activityContext, Context applicationContext) {

        // Only activate analytics for release versions
        if (!getDataManager().isDebugMode()) {


            // New Relic
            NewRelic.withApplicationToken(activityContext.getResources().getString(R.string.new_relic_app_token)).start(applicationContext);
        }

        boolean shouldFirebaseBeEnabled = !BuildConfig.DEBUG || BuildConfig.IS_TEST;
        FirebaseAnalytics.getInstance(applicationContext).setAnalyticsCollectionEnabled(shouldFirebaseBeEnabled);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(shouldFirebaseBeEnabled);

        // Facebook Events
        initFacebookAnalytics();
    }

    @Override
    public void createPaymentTransaction(String deviceData, String paymentType, String paymentNonce,
                                         String paymentToken, String provider) {
        getMvpView().showLoading(LoadingDialogType.DEFAULT);

        String languageId = getDataManager().getLanguageId();
        String countryId = getDataManager().getCountryId();
        CreatePaymentTransaction.RequestValue.Request requestValue =
                new CreatePaymentTransaction.RequestValue.Request();
        requestValue.setPaymentType(paymentType);
        requestValue.setPaymentNonce(paymentNonce);
        if (provider.equals(AppConstants.BRAINTREE)) {
            requestValue.setProvider("");
            requestValue.setSelectedPaymentOption(AppConstants.BRAINTREE);
            requestValue.setPaymentToken("");
        } else {
            requestValue.setProvider(provider);
            requestValue.setPaymentToken(paymentToken);
        }
        requestValue.setDeviceData(deviceData);
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

                        //        reset 3ds called flag
                        PaymentInfo.setThreeDSecureCalled(false);
                        getMvpView().hideLoading();
                        getMvpView().performResetWithAuthFetch();

                        if (responseValue.getD().getResult()) {
                            getMvpView().showCreatePaymentTransactionSuccess(paymentType, responseValue);
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
                        //        reset 3ds called flag
                        PaymentInfo.setThreeDSecureCalled(false);
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
    public void createPaymentTransactionGPay(String token) {
        getMvpView().showLoading(LoadingDialogType.GPAY);

        String languageId = getDataManager().getLanguageId();
        String countryId = getDataManager().getCountryId();
        CreatePaymentTransactionGPay.RequestValue.Request requestValue =
                new CreatePaymentTransactionGPay.RequestValue.Request("stripeGooglePay", token);
        getCompositeDisposable().add(getDataManager()
                .callCreatePaymentTransactionGPay(new CreatePaymentTransactionGPay.RequestValue(requestValue, countryId, languageId, null))
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(responseValue -> {
                    getMvpView().showLoading(LoadingDialogType.GPAY);
                    mStripePaymentCallback.accept(responseValue);
                }, throwable -> {
                    getMvpView().hideLoading();
                    mStripePaymentThrowableCallback.accept(throwable);
                })
        );
    }

    @Override
    public void createPaymentTransactionVco(VisaPaymentSummary visaPaymentSummary) {
        getMvpView().showLoading(LoadingDialogType.DEFAULT);

        String languageId = getDataManager().getLanguageId();
        String countryId = getDataManager().getCountryId();
        CreatePaymentTransactionVco.RequestValue.Request requestValue =
                new CreatePaymentTransactionVco.RequestValue.Request(PaymentInfo.VISA_CHECKOUT_CYBERSOURCE,
                        visaPaymentSummary.getCallId(),
                        visaPaymentSummary.getEncKey(),
                        visaPaymentSummary.getEncPaymentData());
        getCompositeDisposable().add(getDataManager()
                .callCreatePaymentTransactionVco(new CreatePaymentTransactionVco.RequestValue(requestValue, countryId, languageId))
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
                            getMvpView().showCreatePaymentTransactionSuccess(PaymentInfo.VISA_CHECKOUT_CYBERSOURCE, responseValue);
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
    public void createPaymentTransactionStripe(String paymentType, String paymentMethodId, String provider) {
        String languageId = getDataManager().getLanguageId();
        String countryId = getDataManager().getCountryId();
        CreatePaymentTransactionStripe.RequestValue.Request requestValue =
                new CreatePaymentTransactionStripe.RequestValue.Request(paymentType, paymentMethodId, provider);
        getCompositeDisposable().add(getDataManager()
                .callCreatePaymentTransactionStripe(new CreatePaymentTransactionStripe.RequestValue(requestValue, countryId, languageId))
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(mStripePaymentCallback, mStripePaymentThrowableCallback)
        );
    }

    @Override
    public void createPaymentTransactionStripePaymentIntent(String paymentType, String paymentIntent) {
        String languageId = getDataManager().getLanguageId();
        String countryId = getDataManager().getCountryId();
        CreatePaymentIntentStripe.RequestValue.Request requestValue =
                new CreatePaymentIntentStripe.RequestValue.Request(paymentType, paymentIntent);
        getCompositeDisposable().add(getDataManager()
                .callCreatePaymentIntentStripe(new CreatePaymentIntentStripe.RequestValue(requestValue, countryId, languageId))
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(mStripePaymentCallback, mStripePaymentThrowableCallback)
        );
    }

    private Consumer<CreatePaymentTransaction.ResponseValue> mStripePaymentCallback = responseValue -> {
        if (!isViewAttached()) {
            return;
        }

        getMvpView().hideLoading();

        if (responseValue.getD().getValue().getErrorMessage() != null) {
            getMvpView().showErrorMessage(responseValue.getD().getValue().getErrorMessage());
        } else if (responseValue.getD().getResult() && responseValue.getD().getValue().getIsPaid()) {
            getMvpView().showCreatePaymentTransactionSuccess(responseValue.getD().getValue().getPaymentType().toString(),
                    responseValue);
        } else if (!responseValue.getD().getValue().getIsPaid() && responseValue.getD().getValue().getResponse().equalsIgnoreCase(AppConstants.USE_STRIPE_SDK)) {
            getMvpView().show3DSecureStripe(responseValue.getD().getValue().getClientSecret());
        }

    };

    private Consumer<Throwable> mStripePaymentThrowableCallback = throwable -> {
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
    };


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

                            HashMap<String, Object> parameters = new HashMap<>();
                            parameters.put(DataCollector.EventParameters.PAYMENT_METHOD_TYPE,
                                    responseValue.getD().getValue().getLastPaymentMethod().getPaymentType());
                            DataCollector.logEvent(Events.AddPaymentInfo, parameters);
                        } else {
                            getMvpView().onError(responseValue.getMessage());
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
    public void createPaymentMethodStripe(String type, String token) {
        String languageId = getDataManager().getLanguageId();
        String countryId = getDataManager().getCountryId();

        CreatePaymentMethodStripe.RequestValue.Request requestValue = new CreatePaymentMethodStripe.RequestValue.Request(type, token);
        getCompositeDisposable().add(getDataManager()
                .callCreatePaymentMethodStripe(new CreatePaymentMethodStripe.RequestValue(requestValue, countryId, languageId))
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<CreatePaymentMethodStripe.ResponseValue>() {
                    @Override
                    public void accept(@NonNull CreatePaymentMethodStripe.ResponseValue responseValue) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();

                        if (responseValue != null) {

                            if ((responseValue.getResult() && responseValue.getIsAuthenticated())) {
                                getMvpView().showCreatePaymentMethodSuccess(responseValue.getD().getValue().getLastPaymentMethod());

                                HashMap<String, Object> parameters = new HashMap<>();
                                parameters.put(DataCollector.EventParameters.PAYMENT_METHOD_TYPE,
                                        responseValue.getD().getValue().getLastPaymentMethod().getPaymentType());
                                DataCollector.logEvent(Events.AddPaymentInfo, parameters);
                            } else {
                                getMvpView().onError(responseValue.getMessage());
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
                })
        );
    }

    @Override
    public void callLoginTicket(Context context, boolean isGdprCountry) {
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
                                if (isGdprCountry) {
                                    callGetAppSettingsConsent(context);
                                }

                                callGetAppSettings();

                                callGetWishlistIdsOnly();
                            } else {
                                //On login ticket fail, call logout and go back to shop
                                callLogout(null);
                                if (isGdprCountry) {
                                    callGetPublicAppSettingsConsent(context);
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
                    })
            );
        }
    }

    @Override
    public void callLogout(AuthHandler handler) {

        getMvpView().showLoading(LoadingDialogType.DEFAULT);
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
                        getDataManager().setShouldShowStrictConsent(true);
                        getMvpView().hideLoading();
                        //fabric app event sign up reset new user.
                        setIsNewUser(false);
                        //Remove login ticket
                        getDataManager().revokeAuth();
                        //Clear payment info
                        PaymentInfo.resetPaymentInfo();
                        CardInfo.clearCardInfo();

                        getDataManager().setCurrentPaymentType(null);
                        getDataManager().setCurrentPaymentToken(null);

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
                    getDataManager().setDeliveryOptionsTemplateTexts(getTemplateTextsResponse.getResponse().getValue());
                    getDataManager().setPersonalisationTemplateTexts(getTemplateTextsResponse.getResponse().getValue());
                    getDataManager().setConsentTemplateTexts(getTemplateTextsResponse.getResponse().getValue());
                    getDataManager().setVoucherStatusTemplateText(getTemplateTextsResponse.getResponse().getValue());
                    getDataManager().setShippingHover(getTemplateTextsResponse.getResponse().getValue());
                    getDataManager().setTemplateTextsSource(getTemplateTextsResponse.getResponse().getValue());
                    getMvpView().storeTemplateTexts(getTemplateTextsResponse.getResponse().getValue());

                }, throwable -> {
                    Log.d("mainpresenter", " getTemplatetexts failed = " + throwable.getMessage());
                }));

        doApiCallForResponse(getDataManager().callGetNZCommissionTemplateTexts(), new AppApiCallback() {
            @Override
            public void onSuccess(List<?> list) {
                super.onSuccess(list);

                if (list.size() > 0) {
                    getDataManager().setSupplierOriginalPriceInfoTemplateTextType1(((TemplateTextResponse) list.get(0)).getText());
                    SupplierOriginalPriceInfoHelper.setContent1(((TemplateTextResponse) list.get(0)).getText());
                }
                if (list.size() > 1) {
                    getDataManager().setSupplierOriginalPriceInfoTemplateTextType2(((TemplateTextResponse) list.get(1)).getText());
                    SupplierOriginalPriceInfoHelper.setContent2(((TemplateTextResponse) list.get(1)).getText());
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                super.onFailure(throwable);

                Log.d("mainpresenter", " getTemplatetexts failed = " + throwable.getMessage());
            }
        });

        // TODO: combine with Supplier Info
        doApiCallForResponse(getDataManager().callGetBuyboxTemplateTexts(), new AppApiCallback() {
            @Override
            public void onSuccess(List<?> list) {
                super.onSuccess(list);

                if (list.size() > 0) {
                    final BuyBoxTemplateTextResponse templateTextResponse = (BuyBoxTemplateTextResponse) list.get(0);
                    final BuyBoxTemplateTextResponse.Templates templates = templateTextResponse.getTemplates();
                    getDataManager().setBuyBoxTemplateTextTitle(templates.getTitle());
                    getDataManager().setBuyBoxTemplateTextSellerTemplate(templates.getSellerTemplate());
                    getDataManager().setBuyBoxTemplateTextButtonText(templates.getButtonText());
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                super.onFailure(throwable);

                Log.d("mainpresenter", " getTemplatetexts failed = " + throwable.getMessage());
            }
        });
    }

    @Override
    public String getStoredTemplateTexts(String detailKey) {
        return getDataManager().getMyPayTemplateTexts(detailKey);
    }

    @Override
    public String getStoredShippingTemplateText() {
        return getDataManager().getShippingHover();
    }

    @Override
    public String getShippingTitle() {
        return getDataManager().getShippingTitle();
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

    @Override
    public void callGetUserCurrent() {
        doApiCallForResponse(getDataManager().getPostcodeDefault(), new AppApiCallback() {
            @Override
            public void onSuccess(Object object) {
                super.onSuccess(object);


                if (object instanceof GetPostcodeDefaultResponse) {
                    getDataManager().setDefaultPostcode(((GetPostcodeDefaultResponse) object).getPostcode());
                } else {
                    getDataManager().setDefaultPostcode(null);
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                super.onFailure(throwable);

                getDataManager().setDefaultPostcode(null);
            }
        });
    }

    @Override
    public boolean getSupplierOriginalPriceInfoEnabled() {
        return getDataManager().isSupplierOriginalPriceInfoEnabled();
    }
}
