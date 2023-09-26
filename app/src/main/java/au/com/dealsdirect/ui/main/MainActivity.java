package au.com.dealsdirect.ui.main;

import static au.com.dealsdirect.service.datacollection.core.DataCollector.EventParameters;
import static au.com.dealsdirect.service.datacollection.core.DataCollector.logEvent;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import com.bluelinelabs.conductor.Conductor;
import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;
import com.facebook.FacebookSdk;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import com.klarna.mobile.sdk.api.KlarnaLoggingLevel;
import com.klarna.mobile.sdk.api.KlarnaMobileSDKCommon;
import com.mysale.genie.profiler.Profiler;
import com.mysale.genie.profiler.ProfilerInterface;
import com.mysale.genie.utility.RxBus;
import com.mysale.genie.utility.config.model.getappsettingssection.Android;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.GooglePayConfig;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.CardParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import au.com.dealsdirect.BuildConfig;
import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentTransaction;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.GetCurrentOrderOurpay;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.data.network.model.legalities.GetTemplateTextsResponse;
import au.com.dealsdirect.data.network.model.orders.GetOrdersResponse;
import au.com.dealsdirect.service.braintree.BraintreeClientHelper;
import au.com.dealsdirect.service.braintree.FetchBraintreeClientTokenHandler;
import au.com.dealsdirect.service.datacollection.enums.Events;
import au.com.dealsdirect.service.event.FirebaseEventServiceInterface;
import au.com.dealsdirect.service.event.GenieEventServiceInterface;
import au.com.dealsdirect.service.ourpay.Ourpay;
import au.com.dealsdirect.service.ourpay.OurpayState;
import au.com.dealsdirect.ui.base.BaseActivity;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.base.BaseController.CommonControllerChangeListener;
import au.com.dealsdirect.ui.controller.account.AccountController;
import au.com.dealsdirect.ui.controller.account.AccountDeletionConfirmationDialog;
import au.com.dealsdirect.ui.controller.afterpay.AfterpayViewController;
import au.com.dealsdirect.ui.controller.bannerfilter.BannerFiltersController;
import au.com.dealsdirect.ui.controller.categories.CategoriesMvpView;
import au.com.dealsdirect.ui.controller.checkout.addpayment.AddPaymentController;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutController;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutMvpView;
import au.com.dealsdirect.ui.controller.checkout.checkouthost.CheckoutHostController;
import au.com.dealsdirect.ui.controller.checkout.ourpay.BottomSheetOurpayOffloadDialog;
import au.com.dealsdirect.ui.controller.checkout.paymentsuccess.PaymentSuccessController;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.ViewContactsController;
import au.com.dealsdirect.ui.controller.country.CountryController;
import au.com.dealsdirect.ui.controller.gdpr.StrictConsentController;
import au.com.dealsdirect.ui.controller.login.PopUpHostController;
import au.com.dealsdirect.ui.controller.main.MainController;
import au.com.dealsdirect.ui.controller.main.Settings;
import au.com.dealsdirect.ui.controller.orders.BottomSheetOrderSatisfactionDialog;
import au.com.dealsdirect.ui.controller.orders.BottomSheetOrderTrackerDialog;
import au.com.dealsdirect.ui.controller.saleitemdetails.BottomSheetSizesDialog;
import au.com.dealsdirect.ui.controller.searchfilter.SearchFilterController;
import au.com.dealsdirect.ui.controller.shops.BottomSheetInfoDialog;
import au.com.dealsdirect.ui.controller.shops.ShopsController;
import au.com.dealsdirect.ui.controller.splash.SplashScreenController;
import au.com.dealsdirect.ui.custom.BottomSheetInfoWebViewDialog;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.ui.custom.SupplierOriginalPriceInfoHelper;
import au.com.dealsdirect.utils.ActionConstants;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.CartUtil;
import au.com.dealsdirect.utils.DelayedMethodExecutionManager;
import au.com.dealsdirect.utils.DialogUtils;
import au.com.dealsdirect.utils.IntrospectionUtils;
import au.com.dealsdirect.utils.NetworkUtils;
import au.com.dealsdirect.utils.ScreenUtils;
import au.com.dealsdirect.utils.legacycookie.LegacyCookie;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements MainMvpView {

    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 53;

    private static final String TAG = "MainActivity";
    private Router mRouter;

    protected GenieEventServiceInterface mGenieEventService;

    protected FirebaseEventServiceInterface mFirebaseEventService;

    @Inject
    ProfilerInterface mProfiler;

    @Inject
    MainMvpPresenter<MainMvpView> mPresenter;

    @BindView(R.id.activity_main_frame)
    ViewGroup mContainer;

    private BraintreeClientHelper mBraintreeClientHelper;
    private FetchBraintreeClientTokenHandler mFetchBraintreeClientTokenHandler;

    private MainController mMainController;
    private CategoriesMvpView mCategoriesView;
    private CheckoutController mCheckoutController;
    private ViewContactsController mContactsController;
    private AccountController mAccountController;
    private SearchFilterController mSearchFilterController;
    private SearchFilterController mShopSearchFilterController;

    private final Set<AuthHandler> loginAuthHandlers = new HashSet<>();
    private final Set<AuthHandler> logoutAuthHandlers = new HashSet<>();

    private boolean mIsShowingStrictConsentUI = false;
    private boolean isTemplateTextsStored = false;
    private boolean mIsViewAttached = false;
    private int mVisaCheckoutActionType = -1;

    /* bug/gen-8065-reskin_bugfixing */
    private int mDeepLinkLoadDelay = 1000;

    public boolean mAppHasSavedInstance = false;
    public boolean hasShownSplash = false;
    private Stripe mStripe;
    private boolean hasCalledStripeIntent = false;
    private String clientSecret;

    private PaymentsClient paymentsClient;

    private SupplierOriginalPriceInfoHelper supplierOriginalPriceInfoHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAppHasSavedInstance = savedInstanceState != null;

        mIsViewAttached = true;
        getActivityComponent().inject(this);
        registerInternetCheckReceiver();
        mProfiler.setStartLogTime(EventParameters.CustomEventType.CV_APPLAUNCH.getValue());

        setUnBinder(ButterKnife.bind(this));

        mPresenter.onAttach(this);
//        mPresenter.callGetTemplateTexts();
        MobileAds.initialize(this);

        if (mAppHasSavedInstance && Settings.getIsMultiCountry() && !mPresenter.defaultCountryId().isEmpty()) {
            Settings.Country country = Settings.getCountryWithId(mPresenter.defaultCountryId());
            setAppCountries(country);
        }

        // Init All analytics sdk
        mPresenter.initializeAnalytics(this, this.getApplication());
        mGenieEventService = getActivityComponent().getGenieEventService();
        mFirebaseEventService = getActivityComponent().getFirebaseEventService();
        mRouter = Conductor.attachRouter(this, mContainer, savedInstanceState);
        CommonControllerChangeListener.addToRouter(mRouter);

        if (mMainController == null) {
            mMainController = MainController.newInstance();
        }

        if (!mAppHasSavedInstance) {
            mProfiler.setEndLogTime(EventParameters.CustomEventType.CV_APPLAUNCH.getValue());

            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put(EventParameters.MILLISECONDS,
                    Profiler.getTotalTime(EventParameters.CustomEventType.CV_APPLAUNCH.getValue()));
            parameters.put(EventParameters.APP_CONTEXT, this);

            logEvent(Events.CVAppLaunch, parameters);
        }

        splashShownCallback();
        onNewIntent(getIntent());

        //Initialize version introspection
        if (!IntrospectionUtils.verifyIsAppUpdated(getApplicationContext())) {
            mPresenter.setUserRateCurrentVersion(false);
            IntrospectionUtils.verifyVersion(getApplicationContext());
        }

        paymentsClient = Wallet.getPaymentsClient(this,
                new Wallet.WalletOptions.Builder()
                        .setEnvironment(BuildConfig.IS_TEST ? WalletConstants.ENVIRONMENT_TEST : WalletConstants.ENVIRONMENT_PRODUCTION)
                        .build());

        if (BuildConfig.IS_TEST) {
            KlarnaMobileSDKCommon.setLoggingLevel(KlarnaLoggingLevel.Verbose);
        } else {
            KlarnaMobileSDKCommon.setLoggingLevel(KlarnaLoggingLevel.Off);
        }

        setUp();
    }

    /**
     * bug/gen-7818-landscape - detect screen orientaiton change
     *
     * @param newConfig - new screen adjustment
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Router router = getCurrentRouter();
        if (router != null) {
            Controller controller = getCurrentController(router);
            if (controller instanceof BaseController) {
                ((BaseController) controller).onOrientationChanged(newConfig);
            }
        }
    }

    @Override
    protected void setUp() {

        // Initialize GCM
//        mPresenter.initializeNotifications(getApplicationContext());
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDetach();
        mIsViewAttached = false;
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onAttach(this);
        mPresenter.pruneCachedResponses();
        refreshWishlist();

        if (!FacebookSdk.isInitialized()) {
            FacebookSdk.sdkInitialize(this);
        }
        if (mBraintreeClientHelper != null) {
            mBraintreeClientHelper.activityOnResume();
        }
    }

    @Override
    protected void onPause() {
        mPresenter.storeCachedResponses();
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);
        String url = "";
        if (intent.getData() != null) url = intent.getData().toString();

        //  mDeepLinkProgressDialog = new ProgressUtil().showLoadingDialog(this);
        /* call for getDeepLink data */

        if (intent.getData() != null && !url.isEmpty())
            mPresenter.getDeepLinkData(url);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Call router for callbacks after going out the app and back inside
        super.onActivityResult(requestCode, resultCode, data);

        if (mBraintreeClientHelper != null) {
            mBraintreeClientHelper.activityOnActivityResult(requestCode, resultCode, data);
        }

        if (requestCode == LOAD_PAYMENT_DATA_REQUEST_CODE) {
            switch (resultCode) {
                case RESULT_OK: {
                    onGooglePayResult(data);
                    break;
                }
                case RESULT_CANCELED: {
                    break;
                }
                case AutoResolveHelper.RESULT_ERROR: {
                    // Log the status for debugging
                    // Generally there is no need to show an error to
                    // the user as the Google Payment API will do that
                    final Status status =
                            AutoResolveHelper.getStatusFromIntent(data);
                    break;
                }
                default: {
                    // Do nothing.
                }
            }
        } else if (hasCalledStripeIntent) {
            hasCalledStripeIntent = false;
            getStripeObject().onPaymentResult(requestCode, data,
                    new ApiResultCallback<PaymentIntentResult>() {
                        @Override
                        public void onSuccess(@NonNull PaymentIntentResult result) {
                            // If authentication succeeded, the PaymentIntent will
                            // have user actions resolved; otherwise, handle the
                            // PaymentIntent status as appropriate (e.g. the
                            // customer may need to choose a new payment method)

                            final PaymentIntent paymentIntent = result.getIntent();
                            final PaymentIntent.Status status =
                                    paymentIntent.getStatus();

                            if (status == PaymentIntent.Status.Succeeded ||
                                    PaymentIntent.Status.RequiresConfirmation == status) {
                                // show success UI
                                mPresenter.createPaymentTransactionStripePaymentIntent(AppConstants.STRIPE,
                                        paymentIntent.getId());
                            } else if (PaymentIntent.Status.RequiresPaymentMethod
                                    == status) {
                                // attempt authentication again or
                                // ask for a new Payment Method
                                CustomAlertDialog.showCustomAlertDialog(MainActivity.this, CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                                        "We are unable to authenticate your payment method. Please choose a different payment method and try again.");
                            }
                        }

                        @Override
                        public void onError(@NonNull Exception e) {
                            // handle error
                            CustomAlertDialog.showCustomAlertDialog(MainActivity.this, CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                                    e.getLocalizedMessage());
                        }
                    });
        } else {
            mRouter.onActivityResult(requestCode, resultCode, data);
        }


    }

    private boolean isActivityStateValid() {
        return isViewAttached() &&
                getMainController() != null &&
                getMainController().getHomeViewPager() != null;
    }

    @Override
    public void onBackPressed() {
        if (!isActivityStateValid()) {
            return;
        }

        /* gen-8065_ozsale-reskin_bugfixing - dismiss keyboard when changing screen fix  */
        hideKeyboard();

        if (getCurrentController(getCurrentRouter()) instanceof AfterpayViewController &&
                ((AfterpayViewController) getCurrentController(getCurrentRouter())).isBusy()) {
            return;
        }

        if (mIsShowingStrictConsentUI && mRouter.getControllerWithTag(StrictConsentController.TAG)
                instanceof StrictConsentController) {
            mRouter.getControllerWithTag(StrictConsentController.TAG).handleBack();
            return;
        }

        if (getMainController() == null || getMainController().getBottomNav() == null) {
            return;
        }

        if (getMainController().isPopUpControllerVisible()) {
            getMainController().getPopUpHostRouter().handleBack();
        } else {
            Router currentRouter = getCurrentRouter();
            Controller currentController = getMainController().getCurrentViewPagerController();
            if (currentController instanceof BannerFiltersController) {
                getMainController().resetShopRouter();
            } else if (currentController instanceof ShopsController &&
                    !((ShopsController) currentController).isFromCategories()) {
                //exit when shops screen is visible, if not go to shops screen
                DialogUtils.showYesNoDialog(
                        this,
                        getString(R.string.app_name),
                        getString(R.string.exit_app),
                        getString(R.string.exit),
                        getString(R.string.no),
                        (dialogInterface, i) -> finish(),
                        (dialogInterface, i) -> {
                        });
            } else {
                customRouterBackPress(currentRouter, currentController);
            }
        }
    }

    private void customRouterBackPress(Router currentRouter, Controller currentController) {
        if (currentRouter.getBackstackSize() == 1) {
            if (!isMasterDetail(currentRouter)) {
                getMainController().showBottomNav();
                getMainController().showShopController();
            } else {
                currentRouter.handleBack();
            }
        } else {
            currentRouter.handleBack();
        }
    }

    private boolean isMasterDetail(Router router) {
        return mPresenter.isTablet() && getResources().getBoolean(R.bool.master_detail_enabled) &&
                (router == getMainController().getCheckoutRouter()
                        || router == getMainController().getAccountRouter());
    }

    public void addAuthHandler(AuthHandler handler, boolean isLogin) {
        final Set<AuthHandler> authHandlers = isLogin ? loginAuthHandlers : logoutAuthHandlers;
        authHandlers.add(handler);
    }

    public void cancelAuthHandlers() {
        loginAuthHandlers.clear();
        logoutAuthHandlers.clear();
    }

    private void resolveAuthHandlers(boolean isSuccess, boolean isLogin) {
        final Set<AuthHandler> authHandlers = isLogin ? loginAuthHandlers : logoutAuthHandlers;
        for (AuthHandler authHandler : authHandlers) {
            if (isSuccess) {
                authHandler.success();
            } else {
                authHandler.error();
            }
        }
        authHandlers.clear();
    }

    @Override
    public void showLoginController(Router router, AuthHandler handler) {
        if (handler != null) {
            loginAuthHandlers.add(handler);
        }

        //any router can show login controller
        final Controller currentController = getCurrentController(router);

        if (!mPresenter.isTablet()) {
            GateKeeper.push(router, GateKeeper.Destination.LOGIN, new VerticalChangeHandler(), new VerticalChangeHandler());
        } else if (!getMainController().isPopUpControllerVisible()) {
            Bundle bundle = new BundleBuilder(new Bundle())
                    .putSerializable(BundleKeys.KEY_POP_UP_HOST_DESTINATION, GateKeeper.Destination.LOGIN)
                    .build();
            GateKeeper.setRoot(getMainController().getPopUpHostRouter(), GateKeeper.Destination.POP_UP_HOST, RouterTransaction.with(new PopUpHostController(bundle)).
                    pushChangeHandler(new FadeChangeHandler()).popChangeHandler(new FadeChangeHandler()));
        }
    }

    public void showLoginController(AuthHandler handler) {
        if (handler != null) {
            loginAuthHandlers.add(handler);
        }
    }

    //Call only here api for purchase
    @Override
    public void callCreatePaymentTransaction(String type, String nonce, String token) {

        if (PaymentInfo.getPaymentType().equals(PaymentInfo.TYPE_BRAINTREE) && !mBraintreeClientHelper.isInitialized()) {
            mBraintreeClientHelper.setCurrencyCode(Settings.getSelectedCountry().currencyCode);
            final String authorization = PaymentInfo.getAuthorization();
            if (authorization == null || authorization.isEmpty()) {
                mPresenter.callGetPaymentMethodNonce(token);
                return;
            } else {
                mBraintreeClientHelper.initialize(this, PaymentInfo.getAuthorization());
            }
        }

        //3DS Check
        if (PaymentInfo.isThreeDSecureRequired() && !PaymentInfo.isThreeDSecureCalled()) {
            mPresenter.callGetPaymentMethodNonce(token);
            return;
        }

        BraintreeClientHelper.CollectDeviceDataHandler handler = deviceData -> mPresenter.createPaymentTransaction(
                deviceData, type, nonce, token, PaymentInfo.getProvider());

        //Kount Check
        mBraintreeClientHelper.collectDeviceData(handler);
    }

    /*@Override
    public void callCreatePaymentTransactionVco(VisaPaymentSummary visaPaymentSummary) {

        BraintreeClientHelper.CollectDeviceDataHandler handler = deviceData -> mPresenter.createPaymentTransactionVco(visaPaymentSummary);

        //Kount Check
        mBraintreeClientHelper.collectDeviceData(mPresenter.getKountMerchantId(), handler);

    }*/

    @Override
    public void callCreatePaymentTransactionStripe(String paymentType, String paymentMethodId) {
        mPresenter.createPaymentTransactionStripe(paymentType, paymentMethodId, AppConstants.STRIPE);
    }

    @Override
    public void showCreatePaymentTransactionSuccess(String paymentType, CreatePaymentTransaction.ResponseValue responseValue) {

        if (responseValue.isPaid()) {

            if (paymentType.equals(PaymentInfo.TYPE_MYPAY)) {
                setPaymentSuccessOurpay(responseValue);

            } else if (PaymentInfo.getOurpay() != null) {
                PaymentInfo.getOurpay().setCanUse(false);
            }

            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put(EventParameters.PAYMENT_METHOD_TYPE, paymentType);
            parameters.put(EventParameters.IS_NEW_USER, mPresenter.getIsNewUser());
            parameters.put(EventParameters.RESULT, true);
            parameters.put(EventParameters.APP_CONTEXT, this);
            parameters.put(EventParameters.NUMBER_OF_ITEMS,
                    responseValue.getD().getValue().getOrderInfoResult().getItems().size());
            parameters.put(EventParameters.PRICE,
                    responseValue.getD().getValue().getOrderInfoResult().getTotal());
            parameters.put(EventParameters.COUNTRY_ID, Settings.getSelectedCountry().countryId);
            parameters.put(EventParameters.SCREEN_NAME, MainActivity.class.getSimpleName());
            parameters.put(EventParameters.PURCHASE_CURRENCY, Settings.getSelectedCountry().currencyCode);
            parameters.put(EventParameters.PURCHASE_TRANSACTION_ID, responseValue.getD().getValue().getPaymentID());
            logEvent(Events.PurchaseEvent, parameters);

            if (mPresenter.doesCheckoutHaveWishlistItem()) {
                HashMap<String, Object> wishlistParameters = new HashMap<>();
                wishlistParameters.put(EventParameters.APP_CONTEXT, this);
                wishlistParameters.put(EventParameters.SCREEN_NAME, MainActivity.class.getSimpleName());
                logEvent(Events.WishlistPaymentSuccessEvent, wishlistParameters);
            }


//            fabric app event sign up reset new user.
            mPresenter.setIsNewUser(false);

            if (!mPresenter.isTablet()) {
                getMainController().getCheckoutRouter().pushController(RouterTransaction.with(new PaymentSuccessController(responseValue))
                        .pushChangeHandler(new HorizontalChangeHandler())
                        .popChangeHandler(new HorizontalChangeHandler()));
                getMainController().showCheckoutController();

            } else {
                Bundle bundle = new BundleBuilder(new Bundle())
                        .putSerializable(BundleKeys.KEY_POP_UP_HOST_DESTINATION, GateKeeper.Destination.PAYMENT_SUCCESS)
                        .putString(BundleKeys.KEY_ADDRESS, responseValue.getD().getValue().getAddressString())
                        .putDouble(BundleKeys.KEY_PRICE, responseValue.getD().getValue().getOrderInfoResult().getTotal())
                        .putDouble(BundleKeys.KEY_SHIPPING_FEE, responseValue.getD().getValue().getOrderInfoResult().getShipping())
                        .putString(BundleKeys.KEY_INVOICE, responseValue.getD().getValue().getInvoiceNo() == null ? String.valueOf(responseValue.getD().getValue().getTransactionInvoiceNo()) : responseValue.getD().getValue().getInvoiceNo())
                        .putString(BundleKeys.KEY_ESTIMATED_DELIVERY, responseValue.getD().getValue().getOrderInfoResult().getEstimatedDeliveryText())
                        .build();

                GateKeeper.setRoot(getMainController().getPopUpHostRouter(), GateKeeper.Destination.POP_UP_HOST, RouterTransaction.with(new PopUpHostController(bundle)).
                        pushChangeHandler(new FadeChangeHandler()).popChangeHandler(new FadeChangeHandler()));
            }

        } else {
            Controller currentController = getMainController().getCurrentViewPagerController();

            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put(EventParameters.PAYMENT_METHOD_TYPE,
                    PaymentInfo.getFabricPaymentType());
            parameters.put(EventParameters.IS_NEW_USER,
                    mPresenter.getIsNewUser());
            parameters.put(EventParameters.RESULT, false);
            parameters.put(EventParameters.APP_CONTEXT, this);
            parameters.put(EventParameters.NUMBER_OF_ITEMS, responseValue.getD().getValue().getOrderInfoResult().getItems().size());
            parameters.put(EventParameters.PRICE, responseValue.getD().getValue().getOrderInfoResult().getTotal());
            parameters.put(EventParameters.COUNTRY_ID, Settings.getSelectedCountry().countryId);
            parameters.put(EventParameters.SCREEN_NAME, MainActivity.class.getSimpleName());
            logEvent(Events.PurchaseEvent, parameters);

            CustomAlertDialog.showCustomAlertDialog(this, CustomAlertDialog.CustomDialogIconState.NEGATIVE, responseValue.getD().getMessage());

            if (currentController instanceof CheckoutMvpView) {
                ((CheckoutMvpView) currentController).loadCart();
            }
        }


    }

    @Override
    public void showCreatePaymentTransactionFailure(String errorMessage) {

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(EventParameters.PAYMENT_METHOD_TYPE,
                PaymentInfo.getFabricPaymentType());
        parameters.put(EventParameters.IS_NEW_USER,
                mPresenter.getIsNewUser());
        parameters.put(EventParameters.RESULT, false);
        parameters.put(EventParameters.APP_CONTEXT, this);
        parameters.put(EventParameters.SCREEN_NAME,
                MainActivity.class.getSimpleName());
        parameters.put(EventParameters.FAILED_TRANSACTION_MESSAGE,
                errorMessage);
        logEvent(Events.FailedTransaction, parameters);

        if (errorMessage != null) {

            CustomAlertDialog.showCustomAlertDialog(this, CustomAlertDialog.CustomDialogIconState.NEGATIVE, errorMessage);
            getMainController().getCheckoutRouter().popToRoot();

            Controller controller = getMainController().getCurrentControllerOnRouter(getMainController().getCheckoutRouter());
            if (controller instanceof CheckoutController) {
                ((CheckoutController) controller).loadCart();
            }
        }
    }

    @Override
    public void refreshWishlist() {
        if (Settings.getSelectedCountry() != null) {
            mPresenter.callGetWishlistIdsOnly();
        }
    }

    @Override
    public void updateWishlistCounter(int count) {
        getMainController().showWishlistItemCount(count);
    }

    @Override
    public void showCreatePaymentMethodSuccess(PaymentMethod lastPaymentMethod) {

        // Pop current fragment and return to cart controller
        Controller currentController = getMainController().getCurrentViewPagerController();

        if ((currentController instanceof AddPaymentController) && ((AddPaymentController) currentController).isCalledFromAccounts()) {
            ((AddPaymentController) currentController).showAddPaymentResult(true, "");
        } else {
            setPaymentMethodSelected(lastPaymentMethod);

            if (currentController instanceof CheckoutHostController || currentController instanceof AddPaymentController) {
                Router router = currentController instanceof CheckoutHostController ? ((CheckoutHostController) currentController).getDisplayRouter() : getCurrentRouter();
                switch (router.getBackstackSize()) {
                    case 1:
                        ((BaseController) currentController).refreshContents();
                        break;
                    case 2:
                        router.handleBack();
                        break;
                    default:
                        router.popToRoot();
                        break;
                }

            } else {
                currentController.getRouter().handleBack();
            }
        }
    }

    @Override
    public void showGetPaymentMethodNonceSuccess(String nonce) {
        showLoadingDialog(getResources().getString(R.string.loading), false);
        PaymentInfo.setThreeDSecureCalled(true);
        mPresenter.setLastCartRedirection(EventParameters.LastRedirection.THREEDSECURE_OTP);
        mBraintreeClientHelper.threeDSecurePerformVerification(
                nonce,
                Double.toString(PaymentInfo.getCartCost()),
                new BraintreeClientHelper.ThreeDSecureVerificationHandler() {
            @Override
            public void onGettingNonce(String nonce) {
                callCreatePaymentTransaction(PaymentInfo.getPaymentType(), nonce, PaymentInfo.getPaymentMethod().getToken());
            }

            @Override
            public void onError(Exception error) {

            }
        });
    }

    @Override
    public void performResetWithAuthFetch() {

        performBraintreeReset();
        fetchBraintreeAuthorization(null);
    }

    @Override
    public void performBraintreeReset() {

        setPaymentMethodSelected(null);
        PaymentInfo.setAuthorization(null);
        PaymentInfo.setPaymentType(null);

        if (mBraintreeClientHelper != null) {
            mBraintreeClientHelper.reset();
        }
    }

    @Override
    public void fetchBraintreeAuthorization(FetchBraintreeClientTokenHandler handler) {
        if (!PaymentInfo.isTokenFetching()) {
            mFetchBraintreeClientTokenHandler = handler;
            //Don't proceed to call if not logged in
            mPresenter.fetchBraintreeClientToken();
            PaymentInfo.setIsTokenFetching(true);
        }
    }


    @Override
    public void storeTemplateTexts(GetTemplateTextsResponse.GetTemplateTextsValue value) {
        isTemplateTextsStored = value != null;
    }

    @Override
    public void onBraintreeAuthorizationFetchSuccess(String authorizationToken, String paymentType) {
        PaymentInfo.setAuthorization(authorizationToken);
        PaymentInfo.setPaymentType(paymentType);

        if (mBraintreeClientHelper == null) {
            mBraintreeClientHelper = new BraintreeClientHelper();
        }

        mBraintreeClientHelper.setCurrencyCode(Settings.getSelectedCountry().currencyCode);
        mBraintreeClientHelper.initialize(this, authorizationToken);

        if (mFetchBraintreeClientTokenHandler != null) {
            mFetchBraintreeClientTokenHandler.onSuccess();
        }
    }

    @Override
    public void onBraintreeAuthorizationFetchFail() {
        if (mFetchBraintreeClientTokenHandler != null) {
            mFetchBraintreeClientTokenHandler.onFailure();
        }
    }

    public void startPaypalPayment() {
//        PayPal.authorizeAccount(mBraintreeFragment);
        mBraintreeClientHelper.getPaymentHandler().startPaypalPayment();
    }

    public void startPaypalCreditPayment(String totalCost) {
        mBraintreeClientHelper.getPaymentHandler().startPaypalCreditPayment(totalCost);
    }

    @Override
    public void callApiSettings() {

    }

    @Override
    public void showStrictConsentUI() {
        if (mPresenter.shouldShowStrictConsent()) {
            mIsShowingStrictConsentUI = true;

            if (!mPresenter.isTablet() || (mPresenter.isTablet() && getMainController() == null)) {
                mRouter.setRoot(RouterTransaction.with(StrictConsentController.newInstance())
                        .tag(StrictConsentController.TAG));
            } else {
                Bundle bundle = new BundleBuilder(new Bundle())
                        .putSerializable(BundleKeys.KEY_POP_UP_HOST_DESTINATION, GateKeeper.Destination.STRICT_CONSENT_UI)
                        .build();
                GateKeeper.setRoot(getMainController().getPopUpHostRouter(), GateKeeper.Destination.POP_UP_HOST, RouterTransaction.with(new PopUpHostController(bundle)).
                        pushChangeHandler(new FadeChangeHandler()).popChangeHandler(new FadeChangeHandler()));
            }

        } else {
            setUpAfterCountrySet();
            if (!mIsShowingStrictConsentUI && !mAppHasSavedInstance) {
                initializeMainController();
            }
        }
    }

    @Override
    public void onClickAgreeStrictConsentUI() {
        mIsShowingStrictConsentUI = false;

        mPresenter.callSaveConsentData();

        initializeMainController();
    }

    public PaymentMethod getPaymentMethodSelected() {
        return PaymentInfo.getPaymentMethod();
    }

    @Override
    public void setPaymentMethodSelected(PaymentMethod paymentMethodSelected) {
        PaymentInfo.setPaymentMethod(paymentMethodSelected);
    }

    public BraintreeClientHelper getBraintreeClient() {
        return mBraintreeClientHelper;
    }

    public boolean isBraintreeInitialized() {
        return mBraintreeClientHelper != null && mBraintreeClientHelper.isInitialized();
    }

//    @Override
//    public void setVisaCheckoutActionType(int visaCheckoutActionType) {
//        mVisaCheckoutActionType = visaCheckoutActionType;
//    }

//    @Override
//    public int getVisaCheckoutActionType() {
//        return mVisaCheckoutActionType;
//    }

    @Override
    public void logLoginTicket() {

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(EventParameters.METHOD,
                EventParameters.LoginType.TICKET);
        parameters.put(EventParameters.RESULT, true);
        parameters.put(EventParameters.APP_CONTEXT, this);
        logEvent(Events.Login, parameters);
    }

    @Override
    public void callLoginTicket(boolean isGdprCountry) {
        mPresenter.callLoginTicket(this, isGdprCountry);
    }

    @Override
    public void callGCMRegisterSubscriber() {
        mPresenter.initializeNotifications(getApplicationContext());
    }

    @Override
    public void callLogout(AuthHandler handler) {
        if (handler != null) {
            logoutAuthHandlers.add(handler);
        }
        mPresenter.callLogout(new AuthHandler() {
            @Override
            public void success() {
                CartUtil.setValueToCart(0);
                getMainController().removeBasketItemCount();
                mPresenter.setActiveCheckoutSessionFalse();

                //reset routers with unique user info
                getMainController().resetCheckoutRouter();
                getMainController().getRouter().popToRoot();
                getMainController().showShopController();
                callPublicSettings();
                refreshBannersFromLogout();
                refreshWishlist();

                String[] array = getResources().getStringArray(R.array.gdpr_countries);
                List<String> mGdprCountriesArray = new ArrayList<String>(Arrays.asList(array));
                if (mGdprCountriesArray.contains(Settings.getSelectedCountry().countryName.toLowerCase()) && mPresenter.shouldShowStrictConsent()) {
                    callAppConsent();
                }

                resolveAuthHandlers(true, false);
            }

            @Override
            public void error() {
                resolveAuthHandlers(false, false);
            }
        });
    }

    public boolean isAuthorized() {
        return mPresenter.isAuthorized();
    }

    public Router getCategoriesRouter() {
        return getMainController().getCategoriesRouter();
    }

    public Router getCheckoutRouter() {
        return getMainController().getCheckoutRouter();
    }

    public Router getAccountsRouter() {
        return getMainController().getAccountRouter();
    }

    public Router getWishlistRouter() {
        return getMainController().getWishlistRouter();
    }

    public MainController getMainController() {
        return mMainController;
    }

    public ShopsController getShopController() {
        Router router = getMainController().getShopRouter();
        for (RouterTransaction routerTransaction : router.getBackstack()) {
            Controller controller = routerTransaction.controller();
            if (controller instanceof ShopsController) {
                return (ShopsController) controller;
            }
        }
        return null;
    }

    public void splashShownCallback() {
        ScreenUtils.setStatusBarColor(this, R.color.status_bar);

        String defaultCountryId = !mPresenter.defaultCountryId().isEmpty() ? mPresenter.defaultCountryId() : mPresenter.legacyCountryId();

        if (Settings.getIsMultiCountry() && defaultCountryId.isEmpty()) {
            if (!mIsShowingStrictConsentUI && !mAppHasSavedInstance) {
                mRouter.setRoot(RouterTransaction.with(new CountryController(true)));
            }
        } else {

            Settings.Country country = Settings.getIsMultiCountry() ?
                    Settings.getCountryWithId(defaultCountryId) :
                    Settings.getDefaultCountry();

            mPresenter.setCountry(country);
            setAppCountries(country);

            String[] array = getResources().getStringArray(R.array.gdpr_countries);
            List<String> mGdprCountriesArray = new ArrayList<String>(Arrays.asList(array));
            if (mGdprCountriesArray.contains(Settings.getSelectedCountry().countryName.toLowerCase()) && mPresenter.shouldShowStrictConsent()) {

                if (LegacyCookie.hasConsentSaved()) {
                    mPresenter.callSaveConsentData();
                    initializeMainController();
                } else {
                    callAppConsent();
                }

            } else {
                if (isAuthorized()) {
                    // If login ticket exist, call login ticket api to renew cookies and ticket
                    // GetAppSettings and GetPaymentToken will be called on success of this call
                    mPresenter.callLoginTicket(this, false);
                }
                if (!mAppHasSavedInstance) {
                    initializeMainController();
                } else {
                    setUpAfterCountrySet();
                }
            }

        }
    }

    public void setUpAfterCountrySet() {

        // Call API settings
        mPresenter.callGetTemplateTexts();
        mPresenter.callGetServerSettings();
        if (!isAuthorized()) {
            callPublicSettings();
        } else {
            mPresenter.callGetAppSettingsSection(this);
            mPresenter.callGetAppSettingsSectionsPayments(this);
        }
        mPresenter.callGetPublicAppSettingsSectionsAfterpay(this);
        mPresenter.callGetPublicAppSettingsSectionsLPay(this);
        mPresenter.callGetAccountData();
        refreshWishlist();

        logEvent(Events.EventUser, new HashMap<>());

        callGCMRegisterSubscriber();
        mPresenter.callFileSettings();
    }

    public void initializeMainController() {
        setUpAfterCountrySet();
        if (mMainController == null) {
            mMainController = MainController.newInstance();
        }
        mRouter.setRoot(RouterTransaction.with(mMainController).tag("Home"));
        if (isAuthorized()) {
            mMainController.updateBasketItemsQuantity();
        }
    }

    public void callPublicSettings() {
        //If not logged in, call GetPublicAppSettings
        mPresenter.callGetPublicPaymentToken();
        mPresenter.callGetPublicAppSettings();
        mPresenter.callGetPublicAppSettingsSections(this);
    }

    public void callAppConsent() {
        mPresenter.callGetTemplateTexts();
        if (isAuthorized()) {
            mPresenter.callLoginTicket(this, true);
        } else {
            mPresenter.callGetPublicAppSettingsConsent(this);
        }
    }

    private void setPaymentSuccessOurpay(CreatePaymentTransaction.ResponseValue responseValue) {
        Ourpay paymentSuccessOurpay = new Ourpay();

        try {
            List<GetCurrentOrderOurpay.PlannedTransaction> transactions = responseValue.getD().getValue().getPlannedTransactions();

            paymentSuccessOurpay.setCanUse(true);
            paymentSuccessOurpay.setPlannedTransactions(transactions);

            double remainingAmount = 0;
            for (int i = 0; i < transactions.size(); i++) {
                if (transactions.get(i).getState() == 0) {
                    remainingAmount = remainingAmount + transactions.get(i).getAmount();
                }
            }

            paymentSuccessOurpay.setInitialAmount(remainingAmount);
            PaymentInfo.setOurpay(paymentSuccessOurpay);
        } catch (Exception e) {

            paymentSuccessOurpay.setCanUse(false);
            paymentSuccessOurpay.setPlannedTransactions(null);
            paymentSuccessOurpay.setState(paymentSuccessOurpay.getState() | OurpayState.ERROR);
        }

    }

    public String getMyTemplateTexts(String detailKey) {
        return mPresenter.getStoredTemplateTexts(detailKey);
    }

    public String getShippingTemplateText() {
        return mPresenter.getStoredShippingTemplateText();
    }

    public String getShippingTitle() {
        return mPresenter.getShippingTitle();
    }

    public CategoriesMvpView getCategoriesController() {
        if (mCategoriesView == null && getMainController() != null && getMainController().getCategoriesRouter() != null) {
            for (RouterTransaction routerTransaction : getMainController().getCategoriesRouter().getBackstack()) {
                if (routerTransaction.controller() instanceof CategoriesMvpView) {
                    mCategoriesView = (CategoriesMvpView) routerTransaction.controller();
                    break;
                }
            }
        }
        return mCategoriesView;
    }

    public boolean getIsMyPayEnabled() {
        return mPresenter.getIsMyPayEnabled();
    }

    @Override
    public Router getCurrentRouter() {
        return getMainController().getCurrentRouter();
    }

    @Override
    public Controller getCurrentController(Router router) {
        try {
            return getMainController().getCurrentViewPagerController();
        } catch (NullPointerException e) {
            return getMainController();
        }
    }

    @Override
    public void loginSuccessHandler(Router router, AppConstants.POP_FLAG flag, AppConstants.AUTH_FLAG authFlag) {

        RxBus.instance().post(IntrospectionUtils.EVENT_LOGIN);

        switch (flag) {
            case BACK:
                onBackPressed();
                break;
            case ROOT:
                router.popToRoot();
                if (mPresenter.isTablet()) {
                    getMainController().getPopUpHostRouter().handleBack();
                }
                break;
            default:
                break;
        }

        if (!loginAuthHandlers.isEmpty()) {

            // Required api calls on successful auth
            //On success, must call AppSettings
            mPresenter.callGetAppSettings();
            mPresenter.callGetUserCurrent();
            //On success, must get new braintree token
            mPresenter.fetchBraintreeClientToken();
            mPresenter.callGetAppSettingsSectionsPayments(this);
            refreshWishlist();

            resolveAuthHandlers(true, true);
        }

        String successMessage = getString(R.string.login_successfully);
        switch (authFlag) {
            case REGISTER:
                successMessage = getString(R.string.registered_successfully);
                break;
            case LOGIN:
                successMessage = getString(R.string.login_successfully);
                break;
            default:
                break;
        }
        hideKeyboard();
        CustomAlertDialog.showCustomAlertDialog(this, CustomAlertDialog.CustomDialogIconState.POSITIVE, successMessage);
        mMainController.updateBasketItemsQuantity();
        mMainController.showBottomNav();
    }

    @Override
    public void loginErrorHandler(String message) {

        resolveAuthHandlers(false, true);

        if (message != null && !message.isEmpty()) {
            CustomAlertDialog.showCustomAlertDialog(this, CustomAlertDialog.CustomDialogIconState.NEGATIVE, message);
        }
    }

    /**
     * Method to register runtime broadcast receiver to show snackbar alert for internet connection..
     */
    private void registerInternetCheckReceiver() {
        IntentFilter internetFilter = new IntentFilter();
        internetFilter.addAction(NetworkUtils.NET_WIFI_STATE_CHANGE);
        internetFilter.addAction(NetworkUtils.NET_CONNECTIVITY_CHANGE);
        registerReceiver(broadcastReceiver, internetFilter);
    }

    /**
     * Runtime Broadcast receiver inner class to capture internet connectivity events
     */
    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateSnackbar(isNetworkConnected());

            if (isNetworkConnected()) {
                refreshWishlist();
                Controller currentController = getCurrentController(getCurrentRouter());
                BaseController baseController = currentController instanceof BaseController ?
                        (BaseController) getCurrentController(getCurrentRouter()) : null;
                if (baseController != null && baseController.isViewAttached()) {
                    baseController.refreshContents();
                }
            }
        }
    };

    public void updateSnackbar(boolean isOnline) {
        if (!isOnline && !mSnackbar.isShown()) {
            showSnackBar(getString(R.string.no_internet_connection), true);
        } else if (isOnline && mSnackbar.isShown()) {
            dismissSnackBar();
        }
    }

    public void attachMainController() {
        mMainController = MainController.newInstance();
        mRouter.setRoot(RouterTransaction.with(mMainController)
                .tag(MainController.TAG));
    }

    public void setMainController(MainController mainController) {
        mMainController = mainController;
    }

    @Override
    public void hideNoNetworkLayout() {

    }

    @Override
    public void showNoNetworkLayout() {

    }

    @Override
    public boolean isViewAttached() {
        return mIsViewAttached;
    }

    @Override
    public void deepLinkSaleItems(String bannerTitle, String saleId, String bannerId) {
        //TODO: proper delay execution

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            mMainController.deepLinkSaleItems(bannerTitle, saleId, bannerId);
        }, mDeepLinkLoadDelay);

    }

    @Override
    public void deepLinkSales(String categoryName, String categoryId) {
        //TODO: proper delay execution

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (getMainController() != null) {
                if (getShopController() == null) {
                    ShopsController shopsController = ShopsController.instanceWithCategoryFilter(
                            categoryId,
                            categoryName
                    );
                    getMainController().getShopRouter().pushController(
                            RouterTransaction.with(shopsController)
                                    .popChangeHandler(new HorizontalChangeHandler())
                                    .pushChangeHandler(new HorizontalChangeHandler()));
                } else {
                    getShopController().goToSales(categoryName, categoryId);
                }
            }
        }, mDeepLinkLoadDelay);

        deepLinkSuceeded();
    }

    @Override
    public void deepLinkSaleItemDetailsWithoutSale(String seoIdentifierId, String skuId) {
        //TODO: proper delay execution

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            mMainController.deepLinkSaleItemDetails(seoIdentifierId, skuId, false);
            deepLinkSuceeded();
        }, mDeepLinkLoadDelay);

    }

    @Override
    public void deepLinkSaleItemDetailsWithSale(String saleName, String encodedSaleId, String seoIdentifier, String skuId) {
        //TODO: proper delay execution

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            mMainController.deepLinkSaleItems(saleName, encodedSaleId, "");
            deepLinkSuceeded();
            mMainController.deepLinkSaleItemDetails(seoIdentifier, skuId, true);
        }, mDeepLinkLoadDelay);

    }

    @Override
    public void deepLinkCategoryLink(String categoryName, String categoryIdentifier) {
        //TODO: proper delay execution

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (getShopController() == null) {
                getMainController().resetShopRouter();
            }
            getShopController().goToCategoryLink(categoryName, categoryIdentifier);
        }, mDeepLinkLoadDelay);

        deepLinkSuceeded();
    }

    @Override
    public void deeLinkMessageThread() {

    }

    @Override
    public void deepLinkDefault() {
        deepLinkSuceeded();
    }

    @Override
    public void showIntrospectionUtils(ArrayList<Android> androidArrayList) {
        IntrospectionUtils.checkVersion(this, androidArrayList);
    }

    @Override
    public void onGetAppSettings() {
        if (getAccountController() == null) {
            DelayedMethodExecutionManager.getInstance().queueDelayedMethodCall(
                    this.getClass().getName(),
                    "onGetAppSettings",
                    this::onGetAppSettings
            );
            return;
        }
        if (getAccountController() != null && getAccountController().isViewAttached()) {
            getAccountController().reloadAccountItems();
        }
        initializeStripeObject();
        initializeStripePaymentConfiguration();
    }

    private void initializeStripeObject() {
        if (mStripe == null) {
            String key = mPresenter.stripePublicKey();
            if (key != null && !key.isEmpty()) {
                mStripe = new Stripe(this, key);
            }
        }
    }

    private void initializeStripePaymentConfiguration() {
        String key = mPresenter.stripePublicKey();
        if (key != null && !key.isEmpty()) {
            PaymentConfiguration.init(this, key);
        }
    }


    private Stripe getStripeObject() {
        initializeStripeObject();
        //TODO: throw exception when mStripe is null
        return mStripe;
    }

    @Override
    public void show3DSecureStripe(String clientSecret) {
        hasCalledStripeIntent = true;

        this.clientSecret = clientSecret;

        getStripeObject().handleNextActionForPayment((ComponentActivity) this, clientSecret);
    }

    @Override
    public void showErrorMessage(String errorMessage) {
        CustomAlertDialog.showCustomAlertDialog(this, CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                errorMessage);
    }

    private void deepLinkSuceeded() {
        /* deep link succeeded */
    }

    public void showSplashScreen() {
        // I don't think this is needed

        if (mAppHasSavedInstance && !hasShownSplash) {
            getMainController().hideBottomNav();
            getMainController().getCurrentRouter().pushController(RouterTransaction.with(SplashScreenController.newInstance())
                    .popChangeHandler(new VerticalChangeHandler()));
        } else {
            String url = "";
            if (getIntent().getData() != null) {
                url = getIntent().getData().toString();
            }

            if (getIntent().getData() != null && !url.isEmpty()) {
                splashShownCallback();
            } else {
                mRouter.setRoot(RouterTransaction.with(SplashScreenController.newInstance())
                        .popChangeHandler(new VerticalChangeHandler()));
            }
        }
    }

    public void setAppCountries(Settings.Country selectedCountry) {
        Settings.setCountry(selectedCountry);
    }

    public void setCheckoutController(CheckoutController checkoutController) {
        mCheckoutController = checkoutController;
    }

    public CheckoutController getCheckoutController() {
        return mCheckoutController;
    }

    public ViewContactsController getContactsController() {
        return mContactsController;
    }

    public void setContactsController(ViewContactsController viewContactsController) {
        mContactsController = viewContactsController;
    }

    public void setAccountController(AccountController accountController) {
        mAccountController = accountController;
        DelayedMethodExecutionManager.getInstance()
                .executeDelayedMethodCalls(this.getClass().getName());
    }

    public void setSearchFilterController(SearchFilterController searchFilterController) {
        mSearchFilterController = searchFilterController;
    }

    public SearchFilterController getSearchFilterController() {
        return mSearchFilterController;
    }

    public SearchFilterController getShopSearchFilterController() {
        return mShopSearchFilterController;
    }

    public void setShopSearchFilterController(SearchFilterController shopSearchFilterController) {
        this.mShopSearchFilterController = shopSearchFilterController;
    }

    public AccountController getAccountController() {
        return mAccountController;
    }

    public GenieEventServiceInterface getGenieEventService() {
        return mGenieEventService;
    }

    public FirebaseEventServiceInterface getFirebaseEventService() {
        return mFirebaseEventService;
    }

    public ProfilerInterface getProfiler() {
        return mProfiler;
    }

    public void refreshBannersFromLogout() {
        if (getShopController() != null) {
            getShopController().refreshFromLogout();
        }
    }

    public void showOrderTrackingStepBottomDialog(GetOrdersResponse.Order.Invoice.Delivery.Step upperStep,
                                                  GetOrdersResponse.Order.Invoice.Delivery.Step lowerStep) {
        BottomSheetOrderTrackerDialog bottomSheetFragment = new BottomSheetOrderTrackerDialog();

        bottomSheetFragment.setUpperStep(upperStep);
        bottomSheetFragment.setLowerStep(lowerStep);

        bottomSheetFragment.setArguments(null);
        bottomSheetFragment.show(getSupportFragmentManager(), ActionConstants.ORDER_TRACKING_STEP_BOTTOM_DIALOG_TAG);
    }

    public void showOrderSatisfactionDialog(BottomSheetOrderSatisfactionDialog.OnResponseSelectedListener listener) {
        BottomSheetOrderSatisfactionDialog bottomSheetFragment = new BottomSheetOrderSatisfactionDialog();

        bottomSheetFragment.setListener(listener);

        bottomSheetFragment.setArguments(null);
        bottomSheetFragment.show(getSupportFragmentManager(), ActionConstants.ORDER_SATISFACTION_BOTTOM_DIALOG_TAG);
    }

    public void showProductDetailsSizesBottomDialog(ArrayList<Pair<String, String>> productSizes,
                                                    HashSet<Integer> indicesOfSoldOutSizes,
                                                    BottomSheetSizesDialog.OnSizeGuideTappedListener onSizeGuideTappedListener,
                                                    BottomSheetSizesDialog.OnDoneListener onDoneListener) {
        BottomSheetSizesDialog bottomSheetFragment = new BottomSheetSizesDialog();

        bottomSheetFragment.setProductSizes(productSizes);
        bottomSheetFragment.setIndicesOfSoldOutSizes(indicesOfSoldOutSizes);
        bottomSheetFragment.setOnSizeGuideTappedListener(onSizeGuideTappedListener);
        bottomSheetFragment.setOnDoneListener(onDoneListener);

        bottomSheetFragment.setArguments(null);
        bottomSheetFragment.show(getSupportFragmentManager(), ActionConstants.ORDER_BOTTOM_DIALOG_TAG);
    }

    public void showFreeShippingDialog(String deliveryThreshold, String deliveryType, String title) {
        BottomSheetInfoDialog bottomSheetFragment = new BottomSheetInfoDialog();

        final String KEY_SHIPPING_AMOUNT = "[[Amount]]";
        final String shippingAmount = Settings.getSelectedCountry().currencySign + deliveryThreshold;
        final String description = deliveryType.replace(KEY_SHIPPING_AMOUNT, shippingAmount);

        bottomSheetFragment.setTitle(title);
        bottomSheetFragment.setDescription(description);
        bottomSheetFragment.setLayoutId(R.layout.bottom_sheet_free_shipping_info);

        bottomSheetFragment.show(getSupportFragmentManager(), ActionConstants.ORDER_BOTTOM_DIALOG_TAG);
    }

    public void showInfoDialog(String title, String description) {
        BottomSheetInfoDialog bottomSheetFragment = new BottomSheetInfoDialog();

        bottomSheetFragment.setTitle(title);
        bottomSheetFragment.setDescription(description);

        bottomSheetFragment.show(getSupportFragmentManager(), ActionConstants.ORDER_BOTTOM_DIALOG_TAG);
    }

    public void showOffloadOurpayDialog(View.OnClickListener onCloseButtonClickListener,
                                        View.OnClickListener onKlarnaButtonClickListener,
                                        View.OnClickListener onOtherPaymentsButtonClickListener) {
        BottomSheetOurpayOffloadDialog bottomSheetFragment = new BottomSheetOurpayOffloadDialog();

        bottomSheetFragment.setOnCloseButtonClickListener(onCloseButtonClickListener);
        bottomSheetFragment.setOnKlarnaButtonClickListener(onKlarnaButtonClickListener);
        bottomSheetFragment.setIsKlarnaVisible(onKlarnaButtonClickListener != null);
        bottomSheetFragment.setOnOtherPaymentsButtonClickListener(onOtherPaymentsButtonClickListener);

        bottomSheetFragment.show(getSupportFragmentManager(), ActionConstants.ORDER_BOTTOM_DIALOG_TAG);
    }

    public void createStripePaymentMethod(String cardNumber, int cardMonth, int cardYear, String cardCVV) {

        PaymentInfo.setPaymentType(AppConstants.STRIPE);

        CardParams cardParams = new CardParams(cardNumber, cardMonth, cardYear, cardCVV);

        final PaymentMethodCreateParams paymentMethodCreateParams =
                PaymentMethodCreateParams.createCard(cardParams);


        getStripeObject().createPaymentMethod(paymentMethodCreateParams, new ApiResultCallback<com.stripe.android.model.PaymentMethod>() {
            @Override
            public void onSuccess(@NonNull com.stripe.android.model.PaymentMethod result) {

                mPresenter.createPaymentMethodStripe(AppConstants.STRIPE, result.id);

            }

            @Override
            public void onError(@NonNull Exception e) {

                CustomAlertDialog.showCustomAlertDialog(
                        MainActivity.this, CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                        getResources().getString(R.string.stripe_add_card_error));

            }
        });
    }

    public void setCardInfoFromAddPayment(String cardNumber, int month, int year, String cvv) {
        CardInfo.setCardNumber(cardNumber);
        CardInfo.setCardMonth(month);
        CardInfo.setCardYear(year);
        CardInfo.setCardCVV(cvv);

        CardParams cardParams = new CardParams(cardNumber, month, year, cvv);

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setPaymentType(cardParams.getBrand().getCode());
        paymentMethod.setDescription("******" + cardParams.getLast4());
        paymentMethod.setProviderType(AppConstants.STRIPE);
        setPaymentMethodSelected(paymentMethod);

        Controller currentController = getMainController().getCurrentViewPagerController();

        if (currentController instanceof CheckoutHostController || currentController instanceof AddPaymentController) {
            Router router = currentController instanceof CheckoutHostController ? ((CheckoutHostController) currentController).getDisplayRouter() : getCurrentRouter();
            if (router.getBackstackSize() > 2) {
                router.popToRoot();
            } else {
                router.handleBack();
            }
        } else {
            currentController.getRouter().handleBack();
        }
    }

    @NonNull
    private PaymentDataRequest createGPaymentDataRequest(double price, String currency) {
        try {
            final JSONObject tokenizationSpec =
                    new GooglePayConfig(this).getTokenizationSpecification();
            final JSONObject cardPaymentMethod = new JSONObject()
                    .put("type", "CARD")
                    .put(
                            "parameters",
                            new JSONObject()
                                    .put("allowedAuthMethods", new JSONArray()
                                            .put("PAN_ONLY")
                                            .put("CRYPTOGRAM_3DS"))
                                    .put("allowedCardNetworks",
                                            new JSONArray()
                                                    .put("AMEX")
                                                    .put("DISCOVER")
                                                    .put("MASTERCARD")
                                                    .put("VISA"))

                                    // require billing address
                                    .put("billingAddressRequired", true)
                                    .put(
                                            "billingAddressParameters",
                                            new JSONObject()
                                                    // require full billing address
                                                    .put("format", "MIN")

                                                    // require phone number
                                                    .put("phoneNumberRequired", true)
                                    )
                    )
                    .put("tokenizationSpecification", tokenizationSpec);

            // create PaymentDataRequest
            @SuppressLint("DefaultLocale") final String paymentDataRequest = new JSONObject()
                    .put("apiVersion", 2)
                    .put("apiVersionMinor", 0)
                    .put("allowedPaymentMethods",
                            new JSONArray().put(cardPaymentMethod))
                    .put("transactionInfo", (new JSONObject())
                            .put("totalPrice", String.format("%.2f", price))
                            .put("totalPriceStatus", "FINAL")
                            .put("currencyCode", currency)
                    )
                    .put("merchantInfo", new JSONObject()
                            .put("merchantName", getResources().getString(R.string.app_name)))

                    // require email address
                    .put("emailRequired", true)
                    .toString();

            return PaymentDataRequest.fromJson(paymentDataRequest);
        } catch (JSONException ignored) { // exception is only for checking NaN
            return PaymentDataRequest.fromJson("");
        }
    }

    public void isReadyToGPay(OnCompleteListener<Boolean> onCompleteListener) {
        final IsReadyToPayRequest request = createIsReadyToGPayRequest();
        paymentsClient.isReadyToPay(request).addOnCompleteListener(onCompleteListener);
    }

    @NonNull
    private IsReadyToPayRequest createIsReadyToGPayRequest() {
        final JSONArray allowedAuthMethods = new JSONArray();
        allowedAuthMethods.put("PAN_ONLY");
        allowedAuthMethods.put("CRYPTOGRAM_3DS");

        final JSONArray allowedCardNetworks = new JSONArray();
        allowedCardNetworks.put("AMEX");
        allowedCardNetworks.put("DISCOVER");
        allowedCardNetworks.put("MASTERCARD");
        allowedCardNetworks.put("VISA");

        final JSONObject isReadyToPayRequestJson = new JSONObject();
        try {
            isReadyToPayRequestJson.put("allowedAuthMethods", allowedAuthMethods);
            isReadyToPayRequestJson.put("allowedCardNetworks", allowedCardNetworks);
        } catch (JSONException ignored) {
        } // exception is only checking for NaN

        return IsReadyToPayRequest.fromJson(isReadyToPayRequestJson.toString());
    }

    public void payWithGoogle(double price) {
        AutoResolveHelper.resolveTask(
                paymentsClient.loadPaymentData(
                        createGPaymentDataRequest(price, Settings.getSelectedCountry().currencyCode)),
                this,
                LOAD_PAYMENT_DATA_REQUEST_CODE
        );
    }

    private void onGooglePayResult(@NonNull Intent data) {
        final PaymentData paymentData = PaymentData.getFromIntent(data);
        if (paymentData == null) {
            return;
        }

        initializeStripeObject();
        initializeStripePaymentConfiguration();

        try {
            final PaymentMethodCreateParams paymentMethodCreateParams =
                    PaymentMethodCreateParams.createFromGooglePay(
                            new JSONObject(paymentData.toJson()));

            showGPayLoading();
            mStripe.createPaymentMethod(
                    paymentMethodCreateParams,
                    new ApiResultCallback<com.stripe.android.model.PaymentMethod>() {
                        @Override
                        public void onSuccess(@NonNull com.stripe.android.model.PaymentMethod result) {
                            mPresenter.createPaymentTransactionGPay(result.id);
                        }

                        @Override
                        public void onError(@NonNull Exception e) {
                            hideGPayLoading();
                            CustomAlertDialog.showCustomAlertDialog(MainActivity.this, CustomAlertDialog.CustomDialogIconState.NEGATIVE, e.getMessage());
                        }
                    }
            );
        } catch (JSONException ignored) {
        } // exception is only checking for NaN
    }

    public void fetchCachedResponses() {
        mPresenter.fetchCachedResponses();
        mPresenter.pruneCachedResponses();
    }

    public void openAttachment(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    public void showBottomSheetInfoWebViewDialog(String content, boolean showCloseButton) {
        final BottomSheetInfoWebViewDialog bottomSheetFragment = new BottomSheetInfoWebViewDialog();

        bottomSheetFragment.setCloseButtonVisible(showCloseButton);
        bottomSheetFragment.setCloseButtonVisible(true);
        bottomSheetFragment.setWebViewContent(content);

        bottomSheetFragment.show(getSupportFragmentManager(), ActionConstants.ORDER_BOTTOM_WEBVIEW_DIALOG_TAG);
    }

    public void showAccountDeletionConfirmationDialog(AccountDeletionConfirmationDialog.DismissListener dismissListener) {
        final AccountDeletionConfirmationDialog dialogFragment = new AccountDeletionConfirmationDialog();

        dialogFragment.setDismissListener(dismissListener);

        dialogFragment.show(getSupportFragmentManager(), "AccountDeletionConfirmation");
    }

    public SupplierOriginalPriceInfoHelper getSupplierOriginalPriceInfoHelper() {
        if (supplierOriginalPriceInfoHelper == null) {
            if (mPresenter.getSupplierOriginalPriceInfoEnabled()) {
                supplierOriginalPriceInfoHelper = new SupplierOriginalPriceInfoHelper();
            }
        }
        return supplierOriginalPriceInfoHelper;
    }
}
