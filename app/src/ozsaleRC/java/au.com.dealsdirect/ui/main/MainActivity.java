package au.com.dealsdirect.ui.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.util.Pair;

import com.bluelinelabs.conductor.Conductor;
import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;
import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.Card;
import com.braintreepayments.api.DataCollector;
import com.braintreepayments.api.PayPal;
import com.braintreepayments.api.ThreeDSecure;
import com.braintreepayments.api.exceptions.AuthenticationException;
import com.braintreepayments.api.exceptions.AuthorizationException;
import com.braintreepayments.api.exceptions.BraintreeError;
import com.braintreepayments.api.exceptions.ConfigurationException;
import com.braintreepayments.api.exceptions.DownForMaintenanceException;
import com.braintreepayments.api.exceptions.ErrorWithResponse;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.exceptions.ServerException;
import com.braintreepayments.api.exceptions.UnexpectedException;
import com.braintreepayments.api.exceptions.UpgradeRequiredException;
import com.braintreepayments.api.interfaces.BraintreeResponseListener;
import com.braintreepayments.api.models.CardBuilder;
import com.braintreepayments.api.models.PayPalRequest;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.braintreepayments.api.models.VisaCheckoutNonce;
import com.braintreepayments.cardform.view.CardForm;
import com.google.android.gms.ads.MobileAds;
import com.mysale.genie.profiler.Profiler;
import com.mysale.genie.profiler.ProfilerInterface;
import com.mysale.genie.utility.RxBus;
import com.mysale.genie.utility.config.model.getappsettingssection.Android;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentAuthConfig;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.exception.APIConnectionException;
import com.stripe.android.exception.APIException;
import com.stripe.android.exception.InvalidRequestException;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.model.StripeIntent;
import com.stripe.android.model.Token;
import com.visa.checkout.VisaPaymentSummary;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentTransaction;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.GetCurrentOrderOurpay;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.data.network.model.legalities.GetTemplateTextsResponse;
import au.com.dealsdirect.service.datacollection.enums.Events;
import au.com.dealsdirect.service.event.ActionTrackerInterface;
import au.com.dealsdirect.service.event.FirebaseEventServiceInterface;
import au.com.dealsdirect.service.event.GenieEventServiceInterface;
import au.com.dealsdirect.service.ourpay.Ourpay;
import au.com.dealsdirect.service.ourpay.OurpayState;
import au.com.dealsdirect.ui.base.BaseActivity;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.base.BaseController.CommonControllerChangeListener;
import au.com.dealsdirect.ui.controller.account.AccountController;
import au.com.dealsdirect.ui.controller.afterpay.AfterpayViewController;
import au.com.dealsdirect.ui.controller.categories.CategoriesController;
import au.com.dealsdirect.ui.controller.checkout.addpayment.AddPaymentController;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutController;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutMvpView;
import au.com.dealsdirect.ui.controller.checkout.checkouthost.CheckoutHostController;
import au.com.dealsdirect.ui.controller.checkout.paymentsuccess.PaymentSuccessController;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.ViewContactsController;
import au.com.dealsdirect.ui.controller.country.CountryController;
import au.com.dealsdirect.ui.controller.gdpr.StrictConsentController;
import au.com.dealsdirect.ui.controller.home.HomeController;
import au.com.dealsdirect.ui.controller.home.HomeMvpView;
import au.com.dealsdirect.ui.controller.login.PopUpHostController;
import au.com.dealsdirect.ui.controller.main.MainController;
import au.com.dealsdirect.ui.controller.main.Settings;
import au.com.dealsdirect.ui.controller.orders.orders.BottomSheetOrderDialog;
import au.com.dealsdirect.ui.controller.saleitemdetails.BottomSheetSizesDialog;
import au.com.dealsdirect.ui.controller.saleitemdetails.SaleItemDetailsController;
import au.com.dealsdirect.ui.controller.searchfilter.SearchFilterController;
import au.com.dealsdirect.ui.controller.shops.ShopsController;
import au.com.dealsdirect.ui.controller.shops.ShopsMvpView;
import au.com.dealsdirect.ui.controller.splash.SplashScreenController;
import au.com.dealsdirect.ui.controller.visacheckout.VisaCheckoutController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.ActionConstants;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.BraintreeUtils;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.DialogUtils;
import au.com.dealsdirect.utils.IntrospectionUtils;
import au.com.dealsdirect.utils.NetworkUtils;
import au.com.dealsdirect.utils.ScreenUtils;
import au.com.dealsdirect.utils.StripeUtils;
import au.com.dealsdirect.utils.legacycookie.LegacyCookie;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import butterknife.ButterKnife;

import static au.com.dealsdirect.service.datacollection.core.DataCollector.EventParameters;
import static au.com.dealsdirect.service.datacollection.core.DataCollector.logEvent;
import static au.com.dealsdirect.ui.controller.main.MainController.BANNER_FILTER_INDEX;
import static au.com.dealsdirect.ui.controller.main.MainController.SHOP_INDEX;

public class MainActivity extends BaseActivity implements MainMvpView {

    private static final String TAG = "MainActivity";
    private Router mRouter;

    protected ActionTrackerInterface mActionTracker;

    protected GenieEventServiceInterface mGenieEventService;

    protected FirebaseEventServiceInterface mFirebaseEventService;

    @Inject
    ProfilerInterface mProfiler;

    @Inject
    MainMvpPresenter<MainMvpView> mPresenter;

    @BindView(R.id.activity_main_frame)
    ViewGroup mContainer;

    @BindView(R.id.bottomSheetLayout)
    View bottomSheetDialog;


    private BraintreeFragment mBraintreeFragment;
    private FetchTokenHandler mFetchTokenHandler;

    private MainController mMainController;
    private ShopsController mShopController;
    private CategoriesController mCategoriesController;
    private CheckoutController mCheckoutController;
    private ViewContactsController mContactsController;
    private AccountController mAccountController;
    private SearchFilterController mSearchFilterController;
    private SearchFilterController mShopSearchFilterController;
    private MainActivity mMainActivity;

    private Router mHomeRouter;
    private Router mCategoriesRouter;
    private Router mContactsRouter;
    private Router mAccountsRouter;
    private Router mCheckoutRouter;
    private Router mWishlistRouter;

    private AuthHandler mAuthHandler;

    private boolean mIsShowingStrictConsentUI = false;
    private boolean mIsFromBannerFilter = false;
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
        MobileAds.initialize(this, getResources().getString(R.string.admob_app_id));

        if (mAppHasSavedInstance && Settings.getIsMultiCountry() && !mPresenter.defaultCountryId().isEmpty()) {
            Settings.Country country = Settings.getCountryWithId(mPresenter.defaultCountryId());
            setAppCountries(country);
        }

        // Init All analytics sdk
        mPresenter.initializeAnalytics(this, this.getApplication());
        mActionTracker = getActivityComponent().getActionTracker();
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
        refreshWishlist();
    }

    @Override
    protected void onPause() {
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

        if (hasCalledStripeIntent) {
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
                getHomeController() != null &&
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

        if (getHomeController() == null || getHomeController().getBottomNavigationView() == null) {
            return;
        }

        if (getHomeController().isPopUpControllerVisible()) {
            getHomeController().getPopUpHostRouter().handleBack();
        } else {
            Router currentRouter = getCurrentRouter();
            Controller currentController = getCurrentController(currentRouter);
            switch (getMainController().getHomeViewPager().getCurrentItem()) {
                case BANNER_FILTER_INDEX:
                    setRootViewpagerItem(SHOP_INDEX);
                    getHomeController().setViewpagerScreen(SHOP_INDEX);
                    resetShopController(currentRouter);
                    break;
                case SHOP_INDEX:
                    if (mIsFromBannerFilter) {
                        shopsRouterFromCategoryBackPress(currentRouter, currentController);
                    } else {
                        customRouterBackPress(currentRouter, currentController);
                    }
                    break;
            }
        }
    }

    private void resetShopController(Router currentRouter) {
        if (mIsFromBannerFilter) {
            ShopsController shopsController = ShopsController.newInstance();
            setShopController(shopsController);
            currentRouter.setRoot(RouterTransaction.with(shopsController).tag(ShopsController.TAG));
            mIsFromBannerFilter = false;
        }
    }

    private void customRouterBackPress(Router currentRouter, Controller currentController) {
        if (currentRouter.getBackstackSize() == 1) {
            //exit when shops screen is visible, if not go to shops screen
            if (currentController instanceof ShopsMvpView) {
                DialogUtils.showYesNoDialog(
                        this,
                        getString(R.string.app_name),
                        getString(R.string.exit_app),
                        getString(R.string.exit),
                        getString(R.string.no),
                        (dialogInterface, i) -> finish(),
                        (dialogInterface, i) -> {
                        });
            } else if (!isMasterDetail(currentRouter)) {
                getMainController().showBottomNav();
                setShopsAsVisibleContainer();
            } else {
                currentRouter.handleBack();
            }
        } else {
            currentRouter.handleBack();
        }
    }

    private boolean isMasterDetail(Router router) {
        return mPresenter.isTablet() && getResources().getBoolean(R.bool.master_detail_enabled) &&
                (router == mContactsRouter || router == mCheckoutRouter || router == mAccountsRouter);
    }

    private void shopsRouterFromCategoryBackPress(Router currentRouter, Controller currentController) {
        if (currentController instanceof ShopsMvpView) {
            setRootViewpagerItem(BANNER_FILTER_INDEX);
            setDraggableViewPager(true);
        } else {
            currentRouter.handleBack();
        }
    }

    @Override
    public void showLoginController(Router router, AuthHandler handler) {
        mAuthHandler = handler;
        //any router can show login controller
        Controller currentController = getCurrentController(router);

        if (!mPresenter.isTablet()) {
            if (currentController instanceof SaleItemDetailsController ||
                    currentController instanceof AccountController) {
                GateKeeper.push(router, GateKeeper.Destination.LOGIN, new VerticalChangeHandler(), new VerticalChangeHandler());
            } else {
                GateKeeper.push(router, GateKeeper.Destination.LOGIN);
            }
        } else {
            Bundle bundle = new BundleBuilder(new Bundle())
                    .putSerializable(BundleKeys.KEY_POP_UP_HOST_DESTINATION, GateKeeper.Destination.LOGIN)
                    .build();
            GateKeeper.setRoot(getHomeController().getPopUpHostRouter(), GateKeeper.Destination.POP_UP_HOST, RouterTransaction.with(new PopUpHostController(bundle)).
                    pushChangeHandler(new FadeChangeHandler()).popChangeHandler(new FadeChangeHandler()));
        }
    }

    public void showLoginController(AuthHandler handler) {
        mAuthHandler = handler;
    }

    @Override
    public void onCancel(int requestCode) {

        PaymentInfo.setThreeDSecureCalled(false);
        hideLoading();
    }

    @Override
    public void onError(Exception error) {

        if (!(error instanceof ErrorWithResponse)) {

            if (mBraintreeFragment != null) {
                if (error instanceof AuthenticationException || error instanceof AuthorizationException ||
                        error instanceof UpgradeRequiredException) {
                    mBraintreeFragment.sendAnalyticsEvent(BraintreeUtils.SDK_DEV_ERROR);
                } else if (error instanceof ConfigurationException) {
                    mBraintreeFragment.sendAnalyticsEvent(BraintreeUtils.SDK_CONFIG_ERROR);
                } else if (error instanceof ServerException || error instanceof UnexpectedException) {
                    mBraintreeFragment.sendAnalyticsEvent(BraintreeUtils.SDK_SERVER_ERROR);
                } else if (error instanceof DownForMaintenanceException) {
                    mBraintreeFragment.sendAnalyticsEvent(BraintreeUtils.SDK_SERVER_UNAVAILABLE);
                } else {
                    mBraintreeFragment.sendAnalyticsEvent(BraintreeUtils.SDK_ERROR);
                }

                //Call braintree client reset on error
                performResetWithAuthFetch();
            }
        } else {
            hideLoading();
            CustomAlertDialog.showCustomAlertDialog(this, CustomAlertDialog.CustomDialogIconState.NEGATIVE, getBrainTreeErrorMessage(((ErrorWithResponse) error)));
        }
    }

    private String getBrainTreeErrorMessage(ErrorWithResponse error) {
        String errMessage = getString(R.string.an_error_has_occurred);

        if (!(error == null)) {
            if (error.getFieldErrors() != null && !error.getFieldErrors().isEmpty()) {
                BraintreeError err = error.getFieldErrors().get(0);
                List<BraintreeError> fieldErrors = err.getFieldErrors();
                while (fieldErrors != null && !fieldErrors.isEmpty()) {
                    if (fieldErrors.get(0) != null) {
                        err = fieldErrors.get(0);
                        fieldErrors = err.getFieldErrors();
                    } else {
                        break;
                    }
                }
                errMessage = err.getMessage();
            } else if (!error.getMessage().isEmpty()) {
                errMessage = error.getMessage();
            }
        }

        return errMessage;
    }

    @Override
    public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
        HomeController homeController = getMainController().getHomeController();
        Router currentRouter = homeController.getCurrentRouter();
        Controller currentController = homeController.getCurrentControllerOnRouter(currentRouter);

        if (currentController instanceof VisaCheckoutController && paymentMethodNonce instanceof VisaCheckoutNonce) {
            switch (getVisaCheckoutActionType()) {
                case VisaCheckoutController.VISA_CHECKOUT_LOGIN:
                    ((VisaCheckoutController) currentController).doAuthenticateLoginWithVisaCheckoutBraintree((VisaCheckoutNonce) paymentMethodNonce);
                    break;
                case VisaCheckoutController.VISA_CHECKOUT_PAY:
                    callCreatePaymentTransaction(PaymentInfo.VISA_CHECKOUT_BRAINTREE, paymentMethodNonce.getNonce(), "");
                    break;
            }
        } else if (currentController instanceof CheckoutController || PaymentInfo.isThreeDSecureCalled()) {
            callCreatePaymentTransaction(PaymentInfo.getPaymentType(), paymentMethodNonce.getNonce(), "");
        } else {
            callCreatePaymentMethod(PaymentInfo.getPaymentType(), paymentMethodNonce.getNonce());
        }

    }

    //Call only here api for purchase
    @Override
    public void callCreatePaymentTransaction(String type, String nonce, String token) {

        //3DS Check
        if (PaymentInfo.isThreeDSecureRequired() && !PaymentInfo.isThreeDSecureCalled()) {
            mPresenter.callGetPaymentMethodNonce(token);
            return;
        }

        BraintreeResponseListener<String> handler = deviceData -> mPresenter.createPaymentTransaction(
                deviceData, type, nonce, token);

        //Kount Check
        if (!mPresenter.getKountMerchantId().isEmpty()) {
            DataCollector.collectDeviceData(mBraintreeFragment, mPresenter.getKountMerchantId(), handler);
        } else {
            DataCollector.collectDeviceData(mBraintreeFragment, handler);
        }
    }

    @Override
    public void callCreatePaymentTransactionVco(VisaPaymentSummary visaPaymentSummary) {

        BraintreeResponseListener<String> handler = deviceData -> mPresenter.createPaymentTransactionVco(visaPaymentSummary);

        //Kount Check
        if (!mPresenter.getKountMerchantId().isEmpty()) {
            DataCollector.collectDeviceData(mBraintreeFragment, mPresenter.getKountMerchantId(), handler);
        } else {
            DataCollector.collectDeviceData(mBraintreeFragment, handler);
        }

    }

    @Override
    public void callCreatePaymentTransactionStripe(String paymentType, String paymentMethodId) {
        mPresenter.createPaymentTransactionStripe(AppConstants.STRIPE, paymentMethodId);
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
                mCheckoutRouter.pushController(RouterTransaction.with(new PaymentSuccessController(responseValue))
                        .pushChangeHandler(new HorizontalChangeHandler())
                        .popChangeHandler(new HorizontalChangeHandler()));
                getMainController().getHomeController().showCheckoutControllerController();

            } else {
                Bundle bundle = new BundleBuilder(new Bundle())
                        .putSerializable(BundleKeys.KEY_POP_UP_HOST_DESTINATION, GateKeeper.Destination.PAYMENT_SUCCESS)
                        .putString(BundleKeys.KEY_ADDRESS, responseValue.getD().getValue().getAddressString())
                        .putDouble(BundleKeys.KEY_PRICE, responseValue.getD().getValue().getOrderInfoResult().getTotal())
                        .putDouble(BundleKeys.KEY_SHIPPING_FEE, responseValue.getD().getValue().getOrderInfoResult().getShipping())
                        .putString(BundleKeys.KEY_INVOICE, responseValue.getD().getValue().getInvoiceNo() == null ? String.valueOf(responseValue.getD().getValue().getTransactionInvoiceNo()) : responseValue.getD().getValue().getInvoiceNo())
                        .putString(BundleKeys.KEY_ESTIMATED_DELIVERY, responseValue.getD().getValue().getOrderInfoResult().getEstimatedDeliveryText())
                        .build();

                GateKeeper.setRoot(getHomeController().getPopUpHostRouter(), GateKeeper.Destination.POP_UP_HOST, RouterTransaction.with(new PopUpHostController(bundle)).
                        pushChangeHandler(new FadeChangeHandler()).popChangeHandler(new FadeChangeHandler()));
            }

        } else {
            Router currentRouter = getMainController().getHomeController().getCurrentRouter();
            Controller currentController = getMainController().getHomeController().getCurrentControllerOnRouter(currentRouter);

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
            mCheckoutRouter.popToRoot();

            Controller controller = getMainController().getHomeController().getCurrentControllerOnRouter(mCheckoutRouter);
            if (controller != null && controller instanceof CheckoutController) {
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
        getHomeController().updateWishlistItemCount(count);
    }

    //Call only here api for adding payment method
    @Override
    public void callCreatePaymentMethod(String type, String nonce) {
        BraintreeResponseListener<String> handler = deviceData -> mPresenter.createPaymentMethod(
                deviceData, nonce, type);

        //Kount Check
        if (!mPresenter.getKountMerchantId().isEmpty()) {
            DataCollector.collectDeviceData(mBraintreeFragment, mPresenter.getKountMerchantId(), handler);
        } else {
            DataCollector.collectDeviceData(mBraintreeFragment, handler);
        }
    }

    @Override
    public void showCreatePaymentMethodSuccess(PaymentMethod lastPaymentMethod) {

        // Pop current fragment and return to cart controller
        HomeController homeController = getMainController().getHomeController();
        Controller currentController = homeController.getCurrentControllerOnRouter(homeController.getCurrentRouter());

        if ((currentController instanceof AddPaymentController) && ((AddPaymentController) currentController).isCalledFromAccounts()) {
            ((AddPaymentController) currentController).showAddPaymentResult(true, "");
        } else {
            setPaymentMethodSelected(lastPaymentMethod);

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
    }

    @Override
    public void showGetPaymentMethodNonceSuccess(String nonce) {
        showLoadingDialog(getResources().getString(R.string.loading), false);
        PaymentInfo.setThreeDSecureCalled(true);
        mPresenter.setLastCartRedirection(EventParameters.LastRedirection.THREEDSECURE_OTP);
        ThreeDSecure.performVerification(getBraintreeFragment(), nonce, Double.toString(PaymentInfo.getCartCost()));
    }

    @Override
    public void performResetWithAuthFetch() {

        performBraintreeReset();
        fetchAuthorization(null);
    }

    @Override
    public void performBraintreeReset() {

        setPaymentMethodSelected(null);
        PaymentInfo.setAuthorization(null);
        PaymentInfo.setPaymentType(null);

        if (mBraintreeFragment != null && getBraintreeFragment().isResumed() && getFragmentManager().findFragmentByTag(BraintreeFragment.TAG) != null) {
            getFragmentManager().beginTransaction().remove(mBraintreeFragment).commit();
            mBraintreeFragment = null;
        }
    }

    @Override
    public FetchTokenHandler getFetchTokenHandler() {
        return mFetchTokenHandler;
    }

    @Override
    public void fetchAuthorization(FetchTokenHandler handler) {
        if (!PaymentInfo.isTokenFetching()) {
            mFetchTokenHandler = handler;
            //Don't proceed to call if not logged in
            mPresenter.fetchBTAuthorization();
            PaymentInfo.setIsTokenFetching(true);
        }
    }


    @Override
    public void storeTemplateTexts(GetTemplateTextsResponse.GetTemplateTextsValue value) {
        isTemplateTextsStored = value != null;
    }

    @Override
    public void onAuthorizationFetched(String authorizationToken, String paymentType) {
        PaymentInfo.setAuthorization(authorizationToken);
        PaymentInfo.setPaymentType(paymentType);

        try {
            mBraintreeFragment = BraintreeFragment.newInstance(this, PaymentInfo.getAuthorization());

        } catch (InvalidArgumentException e) {
            onError(e);
        }
    }

    public void startPaypalPayment() {
//        PayPal.authorizeAccount(mBraintreeFragment);
        PayPalRequest request = new PayPalRequest();
        PayPal.requestBillingAgreement(getBraintreeFragment(), request);
    }

    public void startPaypalCreditPayment(String totalCost) {
        PayPalRequest request = new PayPalRequest(totalCost)
                .offerCredit(true); // Offer PayPal Credit
        PayPal.requestOneTimePayment(getBraintreeFragment(), request);
    }

    @Override
    public void callApiSettings() {

    }

    @Override
    public void showStrictConsentUI() {
        if (mPresenter.shouldShowStrictConsent()) {
            mIsShowingStrictConsentUI = true;

            if (!mPresenter.isTablet() || (mPresenter.isTablet() && getHomeRouter() == null)) {
                mRouter.setRoot(RouterTransaction.with(StrictConsentController.newInstance())
                        .tag(StrictConsentController.TAG));
            } else {
                Bundle bundle = new BundleBuilder(new Bundle())
                        .putSerializable(BundleKeys.KEY_POP_UP_HOST_DESTINATION, GateKeeper.Destination.STRICT_CONSENT_UI)
                        .build();
                GateKeeper.setRoot(getHomeController().getPopUpHostRouter(), GateKeeper.Destination.POP_UP_HOST, RouterTransaction.with(new PopUpHostController(bundle)).
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

    public void onPurchase(CardForm cardForm) {
        CardBuilder cardBuilder = new CardBuilder()
                .cardNumber(cardForm.getCardNumber())
                .expirationMonth(cardForm.getExpirationMonth())
                .expirationYear(cardForm.getExpirationYear())
                .cvv(cardForm.getCvv())
                .postalCode(cardForm.getPostalCode());

        Card.tokenize(mBraintreeFragment, cardBuilder);
    }

    public PaymentMethod getPaymentMethodSelected() {
        return PaymentInfo.getPaymentMethod();
    }

    @Override
    public void setPaymentMethodSelected(PaymentMethod paymentMethodSelected) {
        PaymentInfo.setPaymentMethod(paymentMethodSelected);
    }

    public BraintreeFragment getBraintreeFragment() {
        return mBraintreeFragment;
    }

    public boolean isBraintreeInitialized() {
        return mBraintreeFragment != null;
    }

    @Override
    public void setVisaCheckoutActionType(int visaCheckoutActionType) {
        mVisaCheckoutActionType = visaCheckoutActionType;
    }

    @Override
    public int getVisaCheckoutActionType() {
        return mVisaCheckoutActionType;
    }

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
        mPresenter.callLogout(handler);
    }

    public void setRootViewpagerItem(int item) {
        mMainController.goToPage(item);
    }

    public void setDraggableViewPager(boolean isDraggable) {
        mMainController.setViewpagerDraggable(isDraggable);
    }

    public void setHomeRouter(Router router) {
        mHomeRouter = router;
    }

    public boolean isAuthorized() {
        return mPresenter.isAuthorized();
    }

    public Router getHomeRouter() {
        return mHomeRouter;
    }

    public Router getCategoriesRouter() {
        return mCategoriesRouter;
    }

    public void setCheckoutRouter(Router router) {
        mCheckoutRouter = router;
    }

    public Router getCheckoutRouter() {
        return mCheckoutRouter;
    }

    public void setCategoriesRouter(Router router) {
        mCategoriesRouter = router;
    }

    public void setContactRouter(Router router) {
        mContactsRouter = router;
    }

    public Router getContactRouter() {
        return mContactsRouter;
    }

    public void setAccountsRouter(Router router) {
        mAccountsRouter = router;
    }

    public Router getAccountsRouter() {
        return mAccountsRouter;
    }


    public Router getWishlistRouter() {
        return mWishlistRouter;
    }

    public void setWishlistRouter(Router wishlistRouter) {
        mWishlistRouter = wishlistRouter;
    }


    public void setShopController(ShopsController shopsController) {
        if (getMainController() != null && getMainController().getHomeController() != null) {
            getMainController().getHomeController().setShopRouterViewPagerDraggable();
        }
        mShopController = shopsController;
    }

    public MainController getMainController() {
        return mMainController;
    }

    public ShopsController getShopController() {
        return mShopController;
    }

    public void setIsFromCategories(boolean isFromBannerFilter) {
        mIsFromBannerFilter = isFromBannerFilter;
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
        }
        mPresenter.callGetPublicAppSettingsSectionsAfterpay(this);
        mPresenter.callGetAccountData();
        refreshWishlist();

        logEvent(Events.EventUser, new HashMap<>());

        callGCMRegisterSubscriber();
        mPresenter.callFileSettings();
    }

    public void initializeMainController() {
        setUpAfterCountrySet();
        mMainController = MainController.newInstance();
        mRouter.setRoot(RouterTransaction.with(mMainController).tag("Home"));
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

    public void setShopsAsVisibleContainer() {
        getHomeController().goBackToHomePage();
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

    public void setCategoriesController(CategoriesController categoriesController) {
        mCategoriesController = categoriesController;
    }

    public CategoriesController getCategoriesController() {
        return mCategoriesController;
    }

    public boolean getIsMyPayEnabled() {
        return mPresenter.getIsMyPayEnabled();
    }

    @Override
    public Router getCurrentRouter() {
        if (getMainController() == null || getMainController().getHomeController() == null) {
            return getHomeRouter();
        } else {
            return getMainController().getHomeController().getCurrentRouter();
        }
    }

    @Override
    public Controller getCurrentController(Router router) {
        try {
            Controller controller = getMainController().getCurrentViewPagerController();
            if (controller instanceof HomeMvpView) {
                return ((HomeController) controller).getCurrentControllerOnRouter(router);
            } else {
                return controller;
            }
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
                    getHomeController().getPopUpHostRouter().handleBack();
                }
                break;
            default:
                break;
        }

        if (mAuthHandler != null) {

            // Required api calls on successful auth
            loginSuccessMethods();

            mAuthHandler.success();
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
        mMainController.getHomeController().getPresenter().callGetBasketItemsQuantity();
        mMainController.showBottomNav();
    }

    @Override
    public void loginSuccessMethods() {
        //On success, must call AppSettings
        mPresenter.callGetAppSettings();
        //On success, must get new braintree token
        mPresenter.fetchBTAuthorization();
        refreshWishlist();
    }

    @Override
    public void loginErrorHandler(String message) {

        if (mAuthHandler != null) {
            mAuthHandler.error();
        }

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

    public int getSelectedBottomNavTab() {
        return getMainController().getHomeController().getSelectedBottomNavTab();
    }

    public void goToSalesFromCategory(GetCategoryTreeResponse getCategoryTreeResponse) {
        mShopController.goToSalesFromCategories(getCategoryTreeResponse.getId(), getCategoryTreeResponse.getKey());
        setRootViewpagerItem(SHOP_INDEX);
    }

    public HomeController getHomeController() {
        return getMainController().getHomeController();
    }


    @Override
    public void deepLinkSaleItems(String bannerTitle, String saleId, String bannerId) {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            mMainController.getHomeController().deepLinkSaleItems(bannerTitle, saleId, bannerId);
        }, mDeepLinkLoadDelay);

    }

    @Override
    public void deepLinkSales(String categoryName, String categoryId) {

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (mHomeRouter != null) {
                mShopController.goToSales(categoryName, categoryId);

            }
        }, mDeepLinkLoadDelay);

        deepLinkSuceeded();
    }

    @Override
    public void deepLinkSaleItemDetailsWithoutSale(String seoIdentifierId, String skuId) {

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            setDraggableViewPager(false);
            mMainController.getHomeController().deepLinkSaleItemDetails(seoIdentifierId, skuId, false);
            deepLinkSuceeded();
        }, mDeepLinkLoadDelay);

    }

    @Override
    public void deepLinkSaleItemDetailsWithSale(String saleName, String encodedSaleId, String seoIdentifier, String skuId) {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            mMainController.getHomeController().deepLinkSaleItems(saleName, encodedSaleId, "");
            deepLinkSuceeded();
            mMainController.getHomeController().deepLinkSaleItemDetails(seoIdentifier, skuId, true);
        }, mDeepLinkLoadDelay);

    }

    @Override
    public void deepLinkCategoryLink(String categoryName, String categoryIdentifier) {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (mShopController != null) {
                mShopController.goToCategoryLink(categoryName, categoryIdentifier);
            }
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
        getAccountController().reloadAccountItems();
        initializeStripeObject();
    }
    
    private void initializeStripeObject() {
        if (mStripe == null) {
            String key = mPresenter.stripePublicKey();
            if (key != null && !key.isEmpty()) {
                mStripe = new Stripe(this, key);
            }
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

        getStripeObject().authenticatePayment(this, clientSecret);
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
        if (mAppHasSavedInstance && !hasShownSplash) {
            getMainController().getHomeController().hideBottomNav();
            getHomeRouter().pushController(RouterTransaction.with(SplashScreenController.newInstance())
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

    public ActionTrackerInterface getActionTracker() {
        return mActionTracker;
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
        if (mShopController != null) {
            mShopController.refreshFromLogout();
        }
    }

    public void showOrderBottomDialog(ArrayList<String> actionArrays, HashMap<String, String> hashMap) {
        String orderID = hashMap.get(ActionConstants.ORDER_ORDER_ID);
        String invoiceNumber = hashMap.get(ActionConstants.ORDER_INVOICE_NUMBER);
        String itemDescription = hashMap.get(ActionConstants.ORDER_ITEM_DESCRIPTION);
        String itemReturnID = hashMap.get(ActionConstants.ORDER_ITEM_RETURN_ID);
        String productID = hashMap.get(ActionConstants.ORDER_PRODUCT_ID);
        String itemId = hashMap.get(ActionConstants.ORDER_ITEM_ID);
        String imageUrl = hashMap.get(ActionConstants.ORDER_ITEM_IMAGE_URL);
        String reason = hashMap.get(ActionConstants.ORDER_REASON);
        String quantity = hashMap.get(ActionConstants.ORDER_QUANTITY);
        String totalItems = hashMap.get(ActionConstants.ORDER_SUBTOTAL_ITEM);
        boolean isItemCancel = actionArrays.contains(ActionConstants.ORDER_ITEM_ACTION_REFUND);

        BottomSheetOrderDialog bottomSheetFragment = new BottomSheetOrderDialog(
                new BottomSheetOrderDialog.BottomSheetButtonListener() {
                    @Override
                    public void onContactUsPressed() {
                        if (invoiceNumber != null) {
                            showContactUs(true, Integer.parseInt(invoiceNumber), itemDescription);
                        }
                    }

                    @Override
                    public void onOrderPressed() {
                        showWhereIsOrder();
                    }

                    @Override
                    public void onChangeAddressPressed() {
                        showChangeAddress(orderID);
                    }

                    @Override
                    public void onReturnItemPressed() {
                        if (invoiceNumber != null) {
                            showReturnItems(Integer.parseInt(invoiceNumber), true, productID);
                        }
                    }

                    @Override
                    public void onCancelOrderPressed() {
                        if (!isItemCancel) {
                            showCancelDialog(invoiceNumber, reason);
                        } else if (quantity != null && totalItems != null) {
                            showCancelItemDialog(
                                    imageUrl,
                                    itemDescription,
                                    productID,
                                    invoiceNumber,
                                    reason,
                                    Integer.parseInt(quantity), Integer.parseInt(totalItems));
                        }
                    }

                    @Override
                    public void onViewReturnItemPressed() {
                        showViewReturnDetails(itemReturnID, itemDescription, true);
                    }
                });
        Bundle bundle = new Bundle();

        bundle.putStringArrayList(ActionConstants.ORDER_ARRAYS, actionArrays);

        bottomSheetFragment.setArguments(bundle);
        bottomSheetFragment.show(getSupportFragmentManager(), ActionConstants.ORDER_BOTTOM_DIALOG_TAG);
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

    public void showContactUs(boolean isCalledFromOrders, int invoiceNumber, String description) {
        getMainController().getHomeController().showSendContactMessage(isCalledFromOrders, invoiceNumber, description);
    }

    public void showWhereIsOrder() {

    }

    public void showChangeAddress(String orderID) {
        getMainController().getHomeController().showMyAddress(orderID);
    }

    public void showReturnItems(int invoiceNumber, boolean calledFromOrder, String productId) {
        getMainController().getHomeController().showMyReturns(invoiceNumber, calledFromOrder, productId);
    }

    public void showViewReturnDetails(String returnID, String productName, boolean isFromOrders) {
        getMainController().getHomeController().showViewReturnsDetails(returnID, productName, isFromOrders);
    }

    public void showCancelDialog(String orderNumber, String reason) {
        getMainController().getHomeController().showCancelOrderDialog(orderNumber, reason);
    }

    public void showCancelItemDialog(String imageUrl, String itemName, String itemId, String invoiceNumber,
                                     String reason, int quantity, int totalItems) {
        getMainController().getHomeController().showCancelItemDialog(imageUrl, itemName, itemId, invoiceNumber,
                reason, quantity, totalItems);
    }

    public void callRefundOrder(String invoiceNumber, String reason, HashMap<String, Integer> items) {
        getMainController().getHomeController().callCreateOrderRefund(invoiceNumber, reason, items);
    }

    public void showPopupMenu(View v, ArrayList<String> actionArrays, HashMap<String, String> hashMap) {

        boolean showChangeAddress = actionArrays.contains(ActionConstants.ORDER_ACTION_CHANGE_ADDRESS);
        boolean showRequestReturn = actionArrays.contains(ActionConstants.ORDER_ITEM_RETURN);
        boolean showContactUs = actionArrays.contains(ActionConstants.ORDER_ACTION_CHECK_STATUS);
        boolean showViewReturns = actionArrays.contains(ActionConstants.ORDER_ITEM_VIEW_RETURN);
        boolean showCancel = (actionArrays.contains(ActionConstants.ORDER_ITEM_ACTION_REFUND) ||
                actionArrays.contains(ActionConstants.ORDER_ACTION_REFUND));
        boolean isItemCancel = actionArrays.contains(ActionConstants.ORDER_ITEM_ACTION_REFUND);

        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenuInflater().inflate(R.menu.order_actions_pop_up, popup.getMenu());

        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Menu menu = popup.getMenu();
        MenuItem changeAddress = menu.findItem(R.id.change_address);
        changeAddress.setIcon(getResources().getDrawable(R.drawable.ic_change_address));
        changeAddress.setVisible(showChangeAddress);

        MenuItem requestReturn = menu.findItem(R.id.return_item);
        requestReturn.setIcon(getResources().getDrawable(R.drawable.ic_return_item));
        requestReturn.setVisible(showRequestReturn);

        MenuItem contactUs = menu.findItem(R.id.contact_us);
        contactUs.setIcon(getResources().getDrawable(R.drawable.ic_contact_us));
        contactUs.setVisible(showContactUs);

        MenuItem viewReturns = menu.findItem(R.id.view_return_details);
        viewReturns.setIcon(getResources().getDrawable(R.drawable.ic_return_item));
        viewReturns.setVisible(showViewReturns);

        MenuItem cancel = menu.findItem(R.id.cancel_order);
        cancel.setIcon(getResources().getDrawable(R.drawable.ic_cancel_order));
        cancel.setVisible(showCancel);

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.contact_us:
                    showContactUs(true, Integer.parseInt(hashMap.get(ActionConstants.ORDER_INVOICE_NUMBER)),
                            hashMap.get(ActionConstants.ORDER_ITEM_DESCRIPTION));
                    return true;
                case R.id.change_address:
                    showChangeAddress(hashMap.get(ActionConstants.ORDER_ORDER_ID));
                    return true;
                case R.id.return_item:
                    showReturnItems(Integer.parseInt(hashMap.get(ActionConstants.ORDER_INVOICE_NUMBER)),
                            true, hashMap.get(ActionConstants.ORDER_PRODUCT_ID));
                    return true;
                case R.id.view_return_details:
                    showViewReturnDetails(hashMap.get(ActionConstants.ORDER_ITEM_RETURN_ID),
                            hashMap.get(ActionConstants.ORDER_ITEM_DESCRIPTION), true);
                    return true;
                case R.id.cancel_order:
                    if (!isItemCancel) {
                        showCancelDialog(hashMap.get(ActionConstants.ORDER_INVOICE_NUMBER),
                                hashMap.get(ActionConstants.ORDER_REASON));
                    } else {
                        showCancelItemDialog(
                                hashMap.get(ActionConstants.ORDER_ITEM_IMAGE_URL),
                                hashMap.get(ActionConstants.ORDER_ITEM_DESCRIPTION),
                                hashMap.get(ActionConstants.ORDER_PRODUCT_ID),
                                hashMap.get(ActionConstants.ORDER_INVOICE_NUMBER),
                                hashMap.get(ActionConstants.ORDER_REASON),
                                Integer.parseInt(hashMap.get(ActionConstants.ORDER_QUANTITY)),
                                Integer.parseInt(hashMap.get(ActionConstants.ORDER_SUBTOTAL_ITEM)));
                    }
                    return true;
                default:
                    return false;
            }
        });

        popup.show();
    }


    public void createStripePaymentMethod() {

        PaymentInfo.setPaymentType(AppConstants.STRIPE);

        com.stripe.android.model.Card card = com.stripe.android.model.Card.create(
                CardInfo.getCardNumber(), CardInfo.getCardMonth(), CardInfo.getCardYear(), CardInfo.getCardCVV()
        );

        final PaymentMethodCreateParams.Card paymentMethodParamsCard =
                card.toPaymentMethodParamsCard();
        final PaymentMethodCreateParams paymentMethodCreateParams =
                PaymentMethodCreateParams.create(paymentMethodParamsCard,
                        null);

        getStripeObject().createPaymentMethod(paymentMethodCreateParams, new ApiResultCallback<com.stripe.android.model.PaymentMethod>() {
            @Override
            public void onSuccess(@NonNull com.stripe.android.model.PaymentMethod result) {

                mPresenter.setPaymentMethodId(result.id);

                callCreatePaymentTransactionStripe(AppConstants.STRIPE, result.id);

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

        com.stripe.android.model.Card card = com.stripe.android.model.Card.create(
                cardNumber, month, year, cvv);

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setPaymentType(card.getBrand());
        paymentMethod.setDescription("******"+card.getLast4());
        paymentMethod.setProviderType(AppConstants.STRIPE);
        setPaymentMethodSelected(paymentMethod);

        HomeController homeController = getMainController().getHomeController();
        Controller currentController = homeController.getCurrentControllerOnRouter(homeController.getCurrentRouter());

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
}
