package au.com.dealsdirect.ui.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;

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
import com.mysale.genie.profiler.Profiler;
import com.mysale.genie.profiler.ProfilerInterface;
import com.mysale.genie.utility.RxBus;
import com.visa.checkout.VisaPaymentSummary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentTransaction;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.data.network.model.legalities.GetTemplateTextsResponse;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.GetCurrentOrderOurpay;
import au.com.dealsdirect.service.event.ActionTracker;
import au.com.dealsdirect.service.event.ActionTrackerInterface;
import au.com.dealsdirect.service.ourpay.Ourpay;
import au.com.dealsdirect.service.ourpay.OurpayState;
import au.com.dealsdirect.ui.base.BaseActivity;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.account.AccountController;
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
import au.com.dealsdirect.ui.controller.saleitemdetails.SaleItemDetailsController;
import au.com.dealsdirect.ui.controller.searchfilter.SearchFilterController;
import au.com.dealsdirect.ui.controller.shops.ShopsController;
import au.com.dealsdirect.ui.controller.shops.ShopsMvpView;
import au.com.dealsdirect.ui.controller.splash.SplashScreenController;
import au.com.dealsdirect.ui.controller.visacheckout.VisaCheckoutController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.BraintreeUtils;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.DialogUtils;
import au.com.dealsdirect.utils.IntrospectionUtils;
import au.com.dealsdirect.utils.NetworkUtils;
import au.com.dealsdirect.utils.ScreenUtils;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import butterknife.ButterKnife;

import static au.com.dealsdirect.ui.controller.main.MainController.BANNER_FILTER_INDEX;
import static au.com.dealsdirect.ui.controller.main.MainController.SHOP_INDEX;

public class MainActivity extends BaseActivity implements MainMvpView {

    private static final String TAG = "MainActivity";

    protected ActionTrackerInterface mActionTracker;

    @Inject
    ProfilerInterface mProfiler;

    @Inject
    MainMvpPresenter<MainMvpView> mPresenter;

    @BindView(R.id.activity_main_frame)
    ViewGroup mContainer;


    private BraintreeFragment mBraintreeFragment;
    private FetchTokenHandler mFetchTokenHandler;

    private MainController mMainController;
    private ShopsController mShopController;
    private CategoriesController mCategoriesController;
    private CheckoutController mCheckoutController;
    private ViewContactsController mContactsController;
    private AccountController mAccountController;
    private SearchFilterController mSearchFilterController;

    private Router mHomeRouter;
    private Router mCategoriesRouter;
    private Router mContactsRouter;
    private Router mAccountsRouter;
    private Router mCheckoutRouter;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAppHasSavedInstance = savedInstanceState != null;

        mIsViewAttached = true;
        getActivityComponent().inject(this);
        mProfiler.setStartLogTime(ActionTracker.CustomEventType.CV_APPLAUNCH.getValue());

        setUnBinder(ButterKnife.bind(this));

        mPresenter.onAttach(this);
//        mPresenter.callGetTemplateTexts();

        if (mAppHasSavedInstance && Settings.getIsMultiCountry() && !mPresenter.defaultCountryId().isEmpty()) {
            Settings.Country country = Settings.getCountryWithId(mPresenter.defaultCountryId());
            setAppCountries(country);
        }

        // Init All analytics sdk
        mPresenter.initializeAnalytics(this, this.getApplication());
        mActionTracker = getActivityComponent().getActionTracker();
        mRouter = Conductor.attachRouter(this, mContainer, savedInstanceState);

        if (!mAppHasSavedInstance) {
            mMainController = MainController.newInstance();
            mProfiler.setEndLogTime(ActionTracker.CustomEventType.CV_APPLAUNCH.getValue());
            mActionTracker.CVAppLaunch(Profiler.getTotalTime(ActionTracker.CustomEventType.CV_APPLAUNCH.getValue()));
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

        BaseController controller = (BaseController) getCurrentController(getCurrentRouter());
        controller.onOrientationChanged(newConfig);
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
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onAttach(this);
        registerInternetCheckReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onNewIntent(Intent intent) {

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
        mRouter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        /* gen-8065_ozsale-reskin_bugfixing - dismiss keyboard when changing screen fix  */
        hideKeyboard();
        if (mIsShowingStrictConsentUI && mRouter.getControllerWithTag(StrictConsentController.TAG)
                instanceof StrictConsentController) {
            mRouter.getControllerWithTag(StrictConsentController.TAG).handleBack();
            return;
        }

        if (getHomeController() == null || getHomeController().getBottomNavigationView() == null) {
            return;
        }

        if (mPresenter.isTablet() && getHomeController().isPopUpControllerVisible()) {
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
    public void showCreatePaymentTransactionSuccess(String paymentType, CreatePaymentTransaction.ResponseValue responseValue) {

        if (responseValue.isPaid()) {

            if (paymentType.equals(PaymentInfo.TYPE_MYPAY)) {
                setPaymentSuccessOurpay(responseValue);

            } else if (PaymentInfo.getOurpay() != null) {
                PaymentInfo.getOurpay().setCanUse(false);
            }
            mActionTracker.purchase(PaymentInfo.getFabricPaymentType(), mPresenter.getIsNewUser(), true);
//            fabric app event sign up reset new user.
            mPresenter.setIsNewUser(false);

            if (!mPresenter.isTablet()) {
                mCheckoutRouter.pushController(RouterTransaction.with(new PaymentSuccessController(responseValue))
                        .pushChangeHandler(new HorizontalChangeHandler())
                        .popChangeHandler(new HorizontalChangeHandler()));
                getMainController().getHomeController().showFifthTabController();

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

            mActionTracker.purchase(PaymentInfo.getFabricPaymentType(), mPresenter.getIsNewUser(), false);
            CustomAlertDialog.showCustomAlertDialog(this, CustomAlertDialog.CustomDialogIconState.NEGATIVE, responseValue.getD().getMessage());

            if (currentController instanceof CheckoutMvpView) {
                ((CheckoutMvpView) currentController).loadCart();
            }
        }
    }

    @Override
    public void showCreatePaymentTransactionFailure(String errorMessage) {
        mActionTracker.purchase(PaymentInfo.getFabricPaymentType(), mPresenter.getIsNewUser(), false);
        if (errorMessage != null) {

            CustomAlertDialog.showCustomAlertDialog(this, CustomAlertDialog.CustomDialogIconState.NEGATIVE, errorMessage);
            mCheckoutRouter.popToRoot();

            Controller controller = getMainController().getHomeController().getCurrentControllerOnRouter(mCheckoutRouter);
            if (controller != null && controller instanceof CheckoutController) {
                ((CheckoutController) controller).loadCart();
            }
        }
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
        mPresenter.setLastCartRedirection(ActionTracker.LastRedirection.THREEDSECURE_OTP);
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

        if (mBraintreeFragment != null && getFragmentManager().findFragmentByTag(BraintreeFragment.TAG) != null) {
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
        mActionTracker.login(ActionTracker.LoginType.TICKET, true);
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
            setUpAfterCountrySet();

            String[] array = getResources().getStringArray(R.array.gdpr_countries);
            List<String> mGdprCountriesArray = new ArrayList<String>(Arrays.asList(array));
            if (mGdprCountriesArray.contains(Settings.getSelectedCountry().countryName.toLowerCase()) && mPresenter.shouldShowStrictConsent()) {
                callAppConsent();
            } else {
                if (isAuthorized()) {
                    // If login ticket exist, call login ticket api to renew cookies and ticket
                    // GetAppSettings and GetPaymentToken will be called on success of this call
                    mPresenter.callLoginTicket(this, false);
                }
                if (!mAppHasSavedInstance) {
                    initializeMainController();
                }
            }

        }
    }

    public void setUpAfterCountrySet() {

        // Call API settings
        mPresenter.callGetTemplateTexts();
        mPresenter.callGetServerSettings();
        mPresenter.callGetAppSettingsSection(this);
        if (!isAuthorized()) {
            callPublicSettings();
        }
        mPresenter.callGetAccountData();

        callGCMRegisterSubscriber();
    }

    public void initializeMainController() {
        mMainController = MainController.newInstance();
        mRouter.setRoot(RouterTransaction.with(mMainController).tag("Home"));
    }

    public void callPublicSettings() {
        //If not logged in, call GetPublicAppSettings
        mPresenter.callGetPublicPaymentToken();
        mPresenter.callGetPublicAppSettings();
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
        try {
            return getMainController().getHomeController().getCurrentRouter();
        } catch (NullPointerException e) {
            return getHomeRouter();
        }
    }

    @Override
    public Controller getCurrentController(Router router) {
        try {
            Controller controller = getMainController().getCurrentViewPagerController();
            if(controller instanceof HomeMvpView) {
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

            Controller currentController = getCurrentController(getCurrentRouter());
            BaseController baseController = currentController instanceof BaseController ?
                    (BaseController) getCurrentController(getCurrentRouter()) : null;
            if(isNetworkConnected() && baseController != null) {
                baseController.refreshContents();
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
        mShopController.goToSalesFromCategories(getCategoryTreeResponse);
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

    public AccountController getAccountController() {
        return mAccountController;
    }

    public ActionTrackerInterface getActionTracker() {
        return mActionTracker;
    }

    public ProfilerInterface getProfiler() {
        return mProfiler;
    }

    public void refreshBannersFromLogout() {
        if (mShopController != null) {
            mShopController.refreshFromLogout();
        }
    }

}
