package au.com.dealsdirect.ui.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.Conductor;
import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
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
import com.braintreepayments.cardform.view.CardForm;
import com.mysale.genie.utility.RxBus;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentTransaction;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.MyPayDetails;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.data.network.model.legalities.GetTemplateTextsResponse;
import au.com.dealsdirect.service.ourpay.Ourpay;
import au.com.dealsdirect.service.ourpay.OurpayState;
import au.com.dealsdirect.ui.base.BaseActivity;
import au.com.dealsdirect.ui.controller.account.AccountController;
import au.com.dealsdirect.ui.controller.categories.CategoriesController;
import au.com.dealsdirect.ui.controller.checkout.addpayment.AddPaymentController;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutController;
import au.com.dealsdirect.ui.controller.checkout.paymentsuccess.PaymentSuccessController;
import au.com.dealsdirect.ui.controller.home.HomeController;
import au.com.dealsdirect.ui.controller.main.MainController;
import au.com.dealsdirect.ui.controller.saleitemdetails.SaleItemDetailsController;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsController;
import au.com.dealsdirect.ui.controller.shops.ShopsController;
import au.com.dealsdirect.ui.controller.splash.SplashScreenController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.DialogUtils;
import au.com.dealsdirect.utils.IntrospectionUtils;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import butterknife.ButterKnife;

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
    private Router mAccountsRouter;
    private Router mCheckoutRouter;

    private AuthHandler mAuthHandler;

    private boolean mIsFromCategories = false;
    private boolean mIsViewPagerSet = false;
    private boolean isTemplateTextsStored = false;
    private boolean mIsViewAttached = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.App_Theme_Translucent);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mIsViewAttached = true;
        getActivityComponent().inject(this);

        setUnBinder(ButterKnife.bind(this));

        mPresenter.onAttach(this);
        mPresenter.callGetTemplateTexts();

        // Init All analytics sdk
        mPresenter.initializeAnalytics(this, this.getApplication());

        mMainController = MainController.newInstance();
        mRouter = Conductor.attachRouter(this, mContainer, savedInstanceState);
        mRouter.setRoot(RouterTransaction.with(SplashScreenController.newInstance())
                .popChangeHandler(new VerticalChangeHandler()));

        setUp();
    }

    @Override
    protected void setUp() {

        // Initialize GCM
//        mPresenter.initializeNotifications(getApplicationContext());

        // Call API settings
        mPresenter.callGetServerSettings();
        mPresenter.callGetAppSettingsSection(this);
        if (isAuthorized()) {
            // If login ticket exist, call login ticket api to renew cookies and ticket
            // GetAppSettings and GetPaymentToken will be called on success of this call
            mPresenter.callLoginTicket();
        } else {
            //If not logged in, call GetPublicAppSettings
            mPresenter.callGetPublicAppSettings();
        }

//        //create route origins for main
//        mMainRouteOrigins = new GateKeeper.RouteBuilder()
//                .addRouteFrom(GateKeeper.Destination.LOGIN)
//                .build();
//
//        //create route origins for login
//        mLoginRouteOrigins = new GateKeeper.RouteBuilder()
//                .addRouteFrom(GateKeeper.Destination.DETAILS)
//                .build();
//
//        //register them
//        GateKeeper.registerRoute(GateKeeper.Destination.LOGIN, mLoginRouteOrigins);
//        GateKeeper.registerRoute(GateKeeper.Destination.MAIN, mMainRouteOrigins);
//
//        //call to update CURRENT_LOCATION too
//        //pag di mo pinadaan sa gatekeeper baka makalimutan ng developer to set CURRENT_LOCATION.
//        GateKeeper.setRoot(mRouter,GateKeeper.Destination.DETAILS,
//                RouterTransaction.with(ControllerFactory.getInstance(GateKeeper.Destination.DETAILS)));


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
        mIsViewAttached = true;
        registerInternetCheckReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Call router for callbacks after going out the app and back inside
        mRouter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {

        if (mIsViewPagerSet && !isHomeViewPagerNull()) {
            Router currentRouter = getCurrentRouter();
            Controller currentController = getCurrentController(getCurrentRouter());

            if (currentController instanceof ShopsController) {

                if (((ShopsController) currentController).isIsDrawerOpened()) {
                    ((ShopsController) currentController).closeDrawer();
                } else {
                    //exit app
                    if (currentRouter.getBackstack().size() == 1) {
                        DialogUtils.showYesNoDialog(
                                this,
                                getString(R.string.app_name),
                                getString(R.string.exit_app),
                                getString(R.string.exit),
                                getString(R.string.no),
                                (dialogInterface, i) -> finish(),
                                (dialogInterface, i) -> {
                                });
                    }
                }

            } else if (currentController instanceof PaymentSuccessController) {
                try {

                    getMainController().getHomeController().getCheckoutRouter().popToRoot();
                    Controller controller = getMainController().getHomeController().getCurrentControllerOnRouter(mCheckoutRouter);
                    if (controller instanceof CheckoutController) {
                        ((CheckoutController) controller).loadCart();
                    }
                } catch (Exception e) {
                    Log.d(MainActivity.TAG, e.getMessage());
                }
            } else if (currentRouter.getBackstackSize() == 1) { //From Bottom Nav
                getMainController().showBottomNav();
                setShopsAsVisibleContainer();
            } else {
                currentRouter.handleBack();
            }


        } else {
            finish();
        }
    }

    @Override
    public void showLoginController(Router router, AuthHandler handler) {
        mAuthHandler = handler;
        //pinapasa yung router, para kahit child router man siya ng kung ano mang view, pwedeng siya ang tumawag.
        Controller currentController = getCurrentController(router);
        if (currentController instanceof SaleItemDetailsController ||
                currentController instanceof AccountController) {
            GateKeeper.push(router, GateKeeper.Destination.LOGIN, new VerticalChangeHandler(), new VerticalChangeHandler());
        } else {
            GateKeeper.push(router, GateKeeper.Destination.LOGIN);
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
                    mBraintreeFragment.sendAnalyticsEvent("sdk.exit.developer-error");
                } else if (error instanceof ConfigurationException) {
                    mBraintreeFragment.sendAnalyticsEvent("sdk.exit.configuration-exception");
                } else if (error instanceof ServerException || error instanceof UnexpectedException) {
                    mBraintreeFragment.sendAnalyticsEvent("sdk.exit.server-error");
                } else if (error instanceof DownForMaintenanceException) {
                    mBraintreeFragment.sendAnalyticsEvent("sdk.exit.server-unavailable");
                } else {
                    mBraintreeFragment.sendAnalyticsEvent("sdk.exit.sdk-error");
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

        if (currentController instanceof CheckoutController || PaymentInfo.isThreeDSecureCalled()) {
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

            if (getMainController().getHomeController() != null)
                getMainController().getHomeController().showFifthTabController();

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
            currentRouter.handleBack();
        }
    }

    @Override
    public void showGetPaymentMethodNonceSuccess(String nonce) {
        showLoadingDialog("Loading", false);
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
        if (value != null) {

            isTemplateTextsStored = true;
        }
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

    public void setAccountsRouter(Router router) {
        mAccountsRouter = router;
    }


    public void setShopController(ShopsController shopsController) {
        mShopController = shopsController;
    }

    public MainController getMainController() {
        return mMainController;
    }


    public void setIsFromCategories(boolean isFromCategories) {
        mIsFromCategories = isFromCategories;
    }

    public void splashShownCallback() {
        mMainController = MainController.newInstance();
        mRouter.setRoot(RouterTransaction.with(mMainController)
                .tag("Home"));

    }

    public void isViewPagerSet(boolean val) {
        mIsViewPagerSet = val;
    }

    public void setShopsAsVisibleContainer() {
        getMainController().getHomeController().setVisibleContainer(0);
        getMainController().getHomeController().setShopRouterViewPagerDraggable();

    }

    private void setPaymentSuccessOurpay(CreatePaymentTransaction.ResponseValue responseValue) {
        Ourpay paymentSuccessOurpay = new Ourpay();

        try {
            List<MyPayDetails.PlannedTransaction> transactions = responseValue.getD().getValue().getPlannedTransactions();

            paymentSuccessOurpay.setCanUse(true);
            paymentSuccessOurpay.setPlannedTransactions(transactions);

            double remainingAmount = 0;
            for (int i = 0; i < transactions.size(); i++) {
                if (transactions.get(i).getState() == 0) {
                    remainingAmount = remainingAmount + transactions.get(i).getAmount();
                }
            }

            paymentSuccessOurpay.setAmount(remainingAmount);
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

        if (mAuthHandler != null)
            mAuthHandler.error();

        CustomAlertDialog.showCustomAlertDialog(this, CustomAlertDialog.CustomDialogIconState.NEGATIVE, message);
    }

    /**
     * Method to register runtime broadcast receiver to show snackbar alert for internet connection..
     */
    private void registerInternetCheckReceiver() {
        IntentFilter internetFilter = new IntentFilter();
        internetFilter.addAction("android.net.wifi.STATE_CHANGE");
        internetFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
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
                .tag("Home"));
    }

    public boolean isHomeViewPagerNull() {
        return getMainController().getHomeViewPager() == null;
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
}
