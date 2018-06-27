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
import com.braintreepayments.api.exceptions.ConfigurationException;
import com.braintreepayments.api.exceptions.DownForMaintenanceException;
import com.braintreepayments.api.exceptions.ErrorWithResponse;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.exceptions.ServerException;
import com.braintreepayments.api.exceptions.UnexpectedException;
import com.braintreepayments.api.exceptions.UpgradeRequiredException;
import com.braintreepayments.api.interfaces.BraintreeResponseListener;
import com.braintreepayments.api.models.CardBuilder;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.braintreepayments.api.models.VisaCheckoutNonce;
import com.braintreepayments.cardform.view.CardForm;
import com.mysale.genie.utility.RxBus;
import com.visa.checkout.VisaPaymentSummary;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentTransaction;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.data.network.model.legalities.GetTemplateTextsResponse;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.GetCurrentOrderOurpay;
import au.com.dealsdirect.service.ourpay.Ourpay;
import au.com.dealsdirect.service.ourpay.OurpayState;
import au.com.dealsdirect.ui.base.BaseActivity;
import au.com.dealsdirect.ui.controller.account.AccountController;
import au.com.dealsdirect.ui.controller.categories.CategoriesController;
import au.com.dealsdirect.ui.controller.checkout.addpayment.AddPaymentController;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutController;
import au.com.dealsdirect.ui.controller.checkout.paymentsuccess.PaymentSuccessController;
import au.com.dealsdirect.ui.controller.checkout.paymentsuccess.PaymentSuccessMvpView;
import au.com.dealsdirect.ui.controller.home.HomeController;
import au.com.dealsdirect.ui.controller.login.LoginHostController;
import au.com.dealsdirect.ui.controller.main.MainController;
import au.com.dealsdirect.ui.controller.saleitemdetails.SaleItemDetailsController;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsController;
import au.com.dealsdirect.ui.controller.shops.ShopsController;
import au.com.dealsdirect.ui.controller.shops.ShopsMvpView;
import au.com.dealsdirect.ui.controller.splash.SplashScreenController;
import au.com.dealsdirect.ui.controller.visacheckout.VisaCheckoutController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.BraintreeUtils;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.DialogUtils;
import au.com.dealsdirect.utils.IntrospectionUtils;
import au.com.dealsdirect.utils.NetworkUtils;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import butterknife.ButterKnife;

import static au.com.dealsdirect.ui.controller.main.MainController.BANNER_FILTER_INDEX;
import static au.com.dealsdirect.ui.controller.main.MainController.SHOP_INDEX;
import static au.com.dealsdirect.utils.BundleKeys.SALEITEMDETAILS_KEY_IS_DEEP_LINKED_WITH_SALE;
import static au.com.dealsdirect.utils.BundleKeys.SALEITEMDETAILS_KEY_SEO_IDENTIFIER_ID;
import static au.com.dealsdirect.utils.BundleKeys.SALEITEMDETAILS_KEY_SKU_ID;
import static au.com.dealsdirect.utils.BundleKeys.SALEITEMS_BANNER_ID;
import static au.com.dealsdirect.utils.BundleKeys.SALEITEMS_SALE_ID;
import static au.com.dealsdirect.utils.BundleKeys.SALEITEMS_TITLE;

public class MainActivity extends BaseActivity implements MainMvpView {

    private static final String TAG = "MainActivity";

    @Inject
    MainMvpPresenter<MainMvpView> mPresenter;

    @BindView(R.id.activity_main_frame)
    ViewGroup mContainer;


    private BraintreeFragment mBraintreeFragment;
    private FetchTokenHandler mFetchTokenHandler;

    private MainController mMainController;
    private ShopsController mShopController;
    private CategoriesController mCategoriesController;

    private Router mHomeRouter;
    private Router mCategoriesRouter;
    private Router mContactsRouter;
    private Router mAccountsRouter;
    private Router mCheckoutRouter;

    private AuthHandler mAuthHandler;

    private boolean mIsFromBannerFilter = false;
    private boolean isTemplateTextsStored = false;
    private boolean mIsViewAttached = false;
    private boolean mIsTablet = false;
    private int mVisaCheckoutActionType = -1;

    /* bug/gen-8065-reskin_bugfixing */
    private int mDeepLinkLoadDelay = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mIsViewAttached = true;
        getActivityComponent().inject(this);

        if (getResources().getBoolean(R.bool.is_tablet)) {
            mIsTablet = true;
        }

        setUnBinder(ButterKnife.bind(this));

        mPresenter.onAttach(this);
        mPresenter.callGetTemplateTexts();

        // Init All analytics sdk
        mPresenter.initializeAnalytics(this, this.getApplication());

        mMainController = MainController.newInstance();
        mRouter = Conductor.attachRouter(this, mContainer, savedInstanceState);
        showSplashScreen();

        onNewIntent(getIntent());
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

        if (mShopController != null)
            mShopController.onOrientationChange();
    }

    @Override
    protected void setUp() {

        // Initialize GCM
//        mPresenter.initializeNotifications(getApplicationContext());

        // Call API settings
        mPresenter.callGetServerSettings();
        mPresenter.callGetAppSettingsSection(this);
        mPresenter.callGetPublicPaymentToken();
        if (isAuthorized()) {
            // If login ticket exist, call login ticket api to renew cookies and ticket
            // GetAppSettings and GetPaymentToken will be called on success of this call
            mPresenter.callLoginTicket();
        } else {
            //If not logged in, call GetPublicAppSettings
            mPresenter.callGetPublicAppSettings();
        }

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
        if (getHomeController() == null || getHomeController().getBottomNavigationView() == null) {
            return;
        }

        if (mPresenter.isTablet() && getHomeController().isPopUpControllerVisible()) {
            getHomeController().getPopUpHostRouter().handleBack();
        } else {
            Router currentRouter = getCurrentRouter();
            Controller currentController = getCurrentController(getCurrentRouter());

            switch (getMainController().getHomeViewPager().getCurrentItem()) {
                case BANNER_FILTER_INDEX:
                    setRootViewpagerItem(SHOP_INDEX);
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
            } else if (!isMasterDetailRouter(currentRouter)) {
                getMainController().showBottomNav();
                setShopsAsVisibleContainer();
            } else if (currentController instanceof PaymentSuccessMvpView) {
                //backpress for payment success
                getMainController().getHomeController().getCheckoutRouter().popToRoot();
                Controller controller = getMainController().getHomeController().getCurrentControllerOnRouter(mCheckoutRouter);
                ((CheckoutController) controller).loadCart();
            } else {
                currentRouter.handleBack();
            }
        } else {
            currentRouter.handleBack();
        }
    }

    private boolean isMasterDetailRouter(Router router) {
        return mPresenter.isTablet() && (router == mAccountsRouter || router == mCheckoutRouter || router == mContactsRouter);
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
            GateKeeper.setRoot(getHomeController().getPopUpHostRouter(), GateKeeper.Destination.LOGIN_HOST, RouterTransaction.with(LoginHostController.newInstance()).
                    pushChangeHandler(new FadeChangeHandler()).popChangeHandler(new FadeChangeHandler()));
        }
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
        }
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

            mCheckoutRouter.pushController(RouterTransaction.with(new PaymentSuccessController(responseValue))
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler()));

            if (getMainController().getHomeController() != null) {
                getMainController().getHomeController().showFifthTabController();
            }

        } else {

            Router currentRouter = getMainController().getHomeController().getCurrentRouter();
            Controller currentController = getMainController().getHomeController().getCurrentControllerOnRouter(currentRouter);

            CustomAlertDialog.showCustomAlertDialog(this, CustomAlertDialog.CustomDialogIconState.NEGATIVE, responseValue.getD().getMessage());

            if (currentController instanceof CheckoutController) {
                CheckoutController checkoutController = (CheckoutController) currentController;
                checkoutController.loadCart();
            }
        }
    }

    @Override
    public void showCreatePaymentTransactionFailure(String errorMessage) {
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
        Router currentRouter = homeController.getCurrentRouter();
        Controller currentController = homeController.getCurrentControllerOnRouter(currentRouter);

        if ((currentController instanceof AddPaymentController) && ((AddPaymentController) currentController).isCalledFromAccounts()) {
            ((AddPaymentController) currentController).showAddPaymentResult(true, "");
        } else {
            setPaymentMethodSelected(lastPaymentMethod);
            onBackPressed();
        }
    }

    @Override
    public void showGetPaymentMethodNonceSuccess(String nonce) {
        showLoadingDialog(getResources().getString(R.string.loading), false);
        PaymentInfo.setThreeDSecureCalled(true);
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
        PayPal.authorizeAccount(mBraintreeFragment);
    }

    @Override
    public void callApiSettings() {

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
    public void callLoginTicket() {
        mPresenter.callLoginTicket();
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

    public void setAccountsRouter(Router router) {
        mAccountsRouter = router;
    }

    public Router getAccountsRouter() {
        return mAccountsRouter;
    }

    public void setShopController(ShopsController shopsController) {
        mShopController = shopsController;
    }

    public MainController getMainController() {
        return mMainController;
    }


    public void setIsFromCategories(boolean isFromBannerFilter) {
        mIsFromBannerFilter = isFromBannerFilter;
    }

    public void splashShownCallback() {
        mMainController = MainController.newInstance();
        mRouter.setRoot(RouterTransaction.with(mMainController)
                .tag("Home"));

    }

    public void setShopsAsVisibleContainer() {
        getMainController().getHomeController().setVisibleContainer(0);
        getMainController().getHomeController().setShopRouterViewPagerDraggable();

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
            return getMainController().getHomeController().getCurrentControllerOnRouter(router);
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

        Bundle args = new BundleBuilder(new Bundle())
                .putString(SALEITEMS_TITLE, bannerTitle)
                .putString(SALEITEMS_SALE_ID, saleId)
                .putString(SALEITEMS_BANNER_ID, bannerId)
                .build();

        Handler handler = new Handler();
        handler.postDelayed(() -> {

            if (!mPresenter.isAuthorized()) {

                // Invoke login if no auth or not an open app
                if (mHomeRouter != null)
                    mHomeRouter.pushController(RouterTransaction.with(
                            new SaleItemsController(args))
                            .tag(this.getString(R.string.sale_items_controller_tag))
                            .pushChangeHandler(new HorizontalChangeHandler())
                            .popChangeHandler(new HorizontalChangeHandler()));
            } else {

                // Check if sale is available
                mHomeRouter.pushController(RouterTransaction.with(
                        new SaleItemsController(args))
                        .tag(this.getString(R.string.sale_items_controller_tag))
                        .pushChangeHandler(new HorizontalChangeHandler())
                        .popChangeHandler(new HorizontalChangeHandler()));
            }
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
            Bundle bundle = new Bundle();
            bundle.putString(SALEITEMDETAILS_KEY_SEO_IDENTIFIER_ID, seoIdentifierId);
            bundle.putString(SALEITEMDETAILS_KEY_SKU_ID, skuId);
            bundle.putBoolean(SALEITEMDETAILS_KEY_IS_DEEP_LINKED_WITH_SALE, false);

            mMainController.setViewpagerDraggable(false);
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

    private void showSplashScreen() {
        String url = "";
        if (getIntent().getData() != null)
            url = getIntent().getData().toString();

        if (getIntent().getData() != null && !url.isEmpty()) {
            splashShownCallback();
        } else {
            mRouter.setRoot(RouterTransaction.with(SplashScreenController.newInstance())
                    .popChangeHandler(new VerticalChangeHandler()));
        }
    }
}
