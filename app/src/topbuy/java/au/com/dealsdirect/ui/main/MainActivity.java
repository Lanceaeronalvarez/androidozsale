package au.com.dealsdirect.ui.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.bluelinelabs.conductor.Conductor;
import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
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
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.mysale.genie.utility.RxBus;
import com.newrelic.agent.android.NewRelic;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentTransaction;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.MyPayDetails;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.data.network.model.legalities.GetTemplateTextsResponse;
import au.com.dealsdirect.service.fcm.GNotification;
import au.com.dealsdirect.service.ourpay.Ourpay;
import au.com.dealsdirect.service.ourpay.OurpayState;
import au.com.dealsdirect.ui.base.BaseActivity;
import au.com.dealsdirect.ui.controller.categories.CategoriesController;
import au.com.dealsdirect.ui.controller.checkout.addpayment.AddPaymentController;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutController;
import au.com.dealsdirect.ui.controller.main.MainController;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsController;
import au.com.dealsdirect.ui.controller.splash.SplashScreenController;
import au.com.dealsdirect.ui.controller.tutorial.TutorialController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.DialogUtils;
import au.com.dealsdirect.utils.IntrospectionUtils;
import au.com.dealsdirect.utils.module.ControllerFactory;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

import static au.com.dealsdirect.utils.BundleKeys.KEY_ADDRESS;
import static au.com.dealsdirect.utils.BundleKeys.KEY_ESTIMATED_DELIVERY;
import static au.com.dealsdirect.utils.BundleKeys.KEY_INVOICE;
import static au.com.dealsdirect.utils.BundleKeys.KEY_PRICE;
import static au.com.dealsdirect.utils.BundleKeys.KEY_SHIPPING_FEE;

/**
 * Created by smartwave on 30/10/2017.
 */

public class MainActivity extends BaseActivity implements MainMvpView {

    @Inject
    MainMvpPresenter<MainMvpView> mPresenter;

    @BindView(R.id.activity_main_frame)
    FrameLayout mContainer;

    boolean mIsViewPagerSet;

    Controller mMainController;
    private Router mCategoriesRouter;
    private Router mAccountsRouter;
    private Router mMainRouter;
    private Router mCheckoutRouter;
    private Router mSearchFilterRouter;
    private BraintreeFragment mBraintreeFragment;

    private boolean isTemplateTextsStored = false;
    private boolean mIsAddPaymentControllerFromCart = false; //hence its from myAccounts
    private FetchTokenHandler mFetchTokenHandler;

    AuthHandler mAuthHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.App_Theme_Translucent);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getActivityComponent().inject(this);

        setUnBinder(ButterKnife.bind(this));

        mPresenter.onAttach(this);
        mPresenter.callGetTemplateTexts();

        // Init All analytics sdk
        mPresenter.initializeAnalytics(this, this.getApplication());

        mRouter = Conductor.attachRouter(this, mContainer, savedInstanceState);
        mRouter.setRoot(RouterTransaction.with(SplashScreenController.newInstance())
                .popChangeHandler(new VerticalChangeHandler()));

        setUp();

        Intent intent = getIntent();
        if (intent != null) {
            // If activity was launched from notification0
            if (intent.hasExtra(GNotification.FCM_INTENT_LAUNCHED)) {
                boolean isOpenedFromNotifications = intent.getExtras().getBoolean(GNotification.FCM_INTENT_LAUNCHED);
                if (isOpenedFromNotifications) {
                    mPresenter.callGCMNotificationEvent(getApplicationContext());
                }
            }
            //If activity was launched via deep link
//            else if (intent.hasExtra(GDeepLinkUtil.DEEP_LINK_INTENT_LAUNCHED)) {
//                switchFragment(ShopProductDetailsFragment.newInstance(intent.getStringExtra(GDeepLinkUtil.KEY_DEEP_LINK_SEOIDENTIFIER)));
//            }
        }


        callGCMRegisterSubscriber();
    }

    @Override
    protected void setUp() {
        // Call API settings
        mPresenter.callGetServerSettings();
        mPresenter.callGetAppSettingsSection(this);
        if (mPresenter.isAuthorized()) {
            // If login ticket exist, call login ticket api to renew cookies and ticket
            // GetAppSettings and GetPaymentToken will be called on success of this call
            mPresenter.callLoginTicket();
        } else {
            //If not logged in, call GetPublicAppSettings
            mPresenter.callGetPublicAppSettings();
        }
    }

    @Override
    public void onBackPressed() {

        if(isFinishing()){
            return;
        }

        Controller mRouterController = GateKeeper.getCurrentControllerOnRouter(mRouter);
        if(mRouterController instanceof TutorialController || mRouterController instanceof SplashScreenController){
            finish();
            return;
        }

        if (getCategoriesRouter() != null) {
            CategoriesController categoriesController = (CategoriesController) GateKeeper.getCurrentControllerOnRouter(getCategoriesRouter());

            if (categoriesController != null && categoriesController.isActive() && getCategoriesRouter().getBackstackSize() != 0) {
                getCategoriesRouter().handleBack();
            } else {
                backPressLogic();
            }

        } else {
            backPressLogic();
        }
    }

    private void backPressLogic() {
        switch (getMainController().getHomeViewPager().getCurrentItem()) {
            case 0: //accounts
                if (getAccountsRouter().getBackstack().size() == 1) {
                    getMainController().getHomeViewPager().setCurrentItem(1);
                } else {
                    setDraggableViewPager(true);
                    getAccountsRouter().handleBack();
                    GateKeeper.updateCurrentLocation(getAccountsRouter());
                }
                break;
            case 1: //sale items

                Controller controller = GateKeeper.getCurrentControllerOnRouter(getSaleItemsRouter());
                if (controller instanceof SaleItemsController) {
                    if (!((SaleItemsController) controller).isSearchFiltersActive() && getSaleItemsRouter().getBackstackSize() == 1) {
                        //exit app
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
                } else {
                    getSaleItemsRouter().handleBack();
                }

                break;


            case 2: //checkout
                if (getCheckoutRouter().getBackstack().size() == 1) {
                    getMainController().getHomeViewPager().setCurrentItem(1);
                } else {
                    getCheckoutRouter().handleBack();

                    Controller currentController = GateKeeper.getCurrentControllerOnRouter(getCheckoutRouter());
                    if (currentController instanceof CheckoutController) {
                        setDraggableViewPager(true);
                    }

                    GateKeeper.updateCurrentLocation(getCheckoutRouter());

                }
                break;
            default:
                break;
        }

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
    public FetchTokenHandler getFetchTokenHandler() {
        return mFetchTokenHandler;
    }

    @Override
    public void callGCMRegisterSubscriber() {
        mPresenter.initializeNotifications(getApplicationContext());
    }

    @Override
    public void storeTemplateTexts(GetTemplateTextsResponse.GetTemplateTextsValue templateKeysValue) {
        if (templateKeysValue != null) {

            isTemplateTextsStored = true;
        }
    }

    @Override
    public Router getCurrentRouter() {
        try{
            return mRouter;
        }catch (NullPointerException e){
            return getSaleItemsRouter();
        }
    }

    @Override
    public Controller getCurrentController(Router router) {
        return null;
    }

    @Override
    public void showLoginController(Router router, AuthHandler handler) {
        mAuthHandler = handler;
        //pinapasa yung router, para kahit child router man siya ng kung ano mang view, pwedeng siya ang tumawag.
        GateKeeper.Destination currentLocation = GateKeeper.getCurrentLocation(router);
        if (currentLocation == GateKeeper.Destination.SALEITEM_DETAILS ||
                currentLocation == GateKeeper.Destination.ACCOUNT) {
            GateKeeper.push(router, GateKeeper.Destination.LOGIN, new VerticalChangeHandler(false), new VerticalChangeHandler());
        } else {
            GateKeeper.push(router, GateKeeper.Destination.LOGIN, new VerticalChangeHandler(false), new VerticalChangeHandler());
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

    }


    @Override
    public void loginErrorHandler(String message) {

        if (mAuthHandler != null)
            mAuthHandler.error();

        if(message != null && !message.isEmpty()) {
            if (message.contains("UnknownHostException") || message.contains("SocketTimeoutException")) {
                CustomAlertDialog.showCustomAlertDialog(this, CustomAlertDialog.CustomDialogIconState.NEGATIVE, getString(R.string.no_internet_connection));
            }else{
                CustomAlertDialog.showCustomAlertDialog(this, CustomAlertDialog.CustomDialogIconState.NEGATIVE, message);
            }
        }

    }

    @Override
    public void loginSuccessMethods() {
        //On success, must call AppSettings
        mPresenter.callGetAppSettings();
        //On success, must get new braintree token
        mPresenter.fetchBTAuthorization();
    }

    @Override
    public void callLoginTicket() {
        mPresenter.callLoginTicket();
    }

    @Override
    public void callLogout(AuthHandler handler) {
        mPresenter.callLogout(handler);
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
    public void performResetWithAuthFetch() {

        performBraintreeReset();
        fetchAuthorization(null);
    }

    @Override
    public void fetchAuthorization(FetchTokenHandler fetchTokenHandler) {

        if (!PaymentInfo.isTokenFetching()) {
            mFetchTokenHandler = fetchTokenHandler;
            //Don't proceed to call if not logged in
            mPresenter.fetchBTAuthorization();
            PaymentInfo.setIsTokenFetching(true);
        }
    }

    @Override
    public void setPaymentMethodSelected(PaymentMethod paymentMethodSelected) {

        PaymentInfo.setPaymentMethod(paymentMethodSelected);
    }

    @Override
    public BraintreeFragment getBraintreeFragment() {
        return mBraintreeFragment;
    }

    @Override
    public boolean isBraintreeInitialized() {
        return mBraintreeFragment != null;
    }

    @Override
    public void showGetPaymentMethodNonceSuccess(String nonce) {
        showLoadingDialog("Loading", false);
        PaymentInfo.setThreeDSecureCalled(true);
        ThreeDSecure.performVerification(getBraintreeFragment(), nonce, Double.toString(PaymentInfo.getCartCost()));
    }

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


        if (!mIsAddPaymentControllerFromCart) {
            Controller currentAccountsController = GateKeeper.getCurrentControllerOnRouter(getAccountsRouter());
            if(currentAccountsController instanceof AddPaymentController) {
                ((AddPaymentController) currentAccountsController).showAddPaymentResult(true, "");
            } else {
                setPaymentMethodSelected(lastPaymentMethod);
                mRouter.handleBack();
            }
        } else {
            setPaymentMethodSelected(lastPaymentMethod);
            getCheckoutRouter().popToRoot();
        }
    }

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

        new BundleBuilder(new Bundle())
                .putString(KEY_ADDRESS, responseValue.getD().getValue().getAddressString())
                .putDouble(KEY_PRICE,  responseValue.getD().getValue().getOrderInfoResult().getTotal())
                .putDouble(KEY_SHIPPING_FEE, responseValue.getD().getValue().getOrderInfoResult().getShipping())
                .putString(KEY_INVOICE, responseValue.getD().getValue().getInvoiceNo() == null ? String.valueOf(responseValue.getD().getValue().getTransactionInvoiceNo()): responseValue.getD().getValue().getInvoiceNo())
                .putString(KEY_ESTIMATED_DELIVERY, responseValue.getD().getValue().getOrderInfoResult().getEstimatedDeliveryText())
                .build();

        if (responseValue.isPaid()) {

            if (paymentType.equals(PaymentInfo.TYPE_MYPAY)) {
                setPaymentSuccessOurpay(responseValue);

            } else if (PaymentInfo.getOurpay() != null) {
                PaymentInfo.getOurpay().setCanUse(false);
            }

            Bundle bundle = new Bundle();
            bundle.putString(KEY_ADDRESS, responseValue.getD().getValue().getAddressString());
            bundle.putDouble(KEY_PRICE,  responseValue.getD().getValue().getOrderInfoResult().getTotal());
            bundle.putDouble(KEY_SHIPPING_FEE, responseValue.getD().getValue().getOrderInfoResult().getShipping());
            bundle.putString(KEY_INVOICE, responseValue.getD().getValue().getInvoiceNo() == null ? String.valueOf(responseValue.getD().getValue().getTransactionInvoiceNo()): responseValue.getD().getValue().getInvoiceNo());
            bundle.putString(KEY_ESTIMATED_DELIVERY, responseValue.getD().getValue().getOrderInfoResult().getEstimatedDeliveryText());

            GateKeeper.push(mRouter, GateKeeper.Destination.PAYMENT_SUCCESS, bundle, new VerticalChangeHandler(false), new VerticalChangeHandler());

            if (getMainController().getHomeController()!=null)
                getMainController().getHomeController().showCheckoutController();

        } else {

            Controller currentController = GateKeeper.getCurrentControllerOnRouter(mCheckoutRouter);
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

            Controller controller = GateKeeper.getCurrentControllerOnRouter(mCheckoutRouter);
            if (controller != null && controller instanceof CheckoutController) {
                ((CheckoutController) controller).loadCart();
            }
        }
    }

    @Override
    public PaymentMethod getPaymentMethodSelected() {
        return PaymentInfo.getPaymentMethod();
    }

    @Override
    public boolean getIsMyPayEnabled() {
        return mPresenter.getIsMyPayEnabled();
    }

    @Override
    public void onPurchase(CardForm cardForm) {
        CardBuilder cardBuilder = new CardBuilder()
                .cardNumber(cardForm.getCardNumber())
                .expirationMonth(cardForm.getExpirationMonth())
                .expirationYear(cardForm.getExpirationYear())
                .cvv(cardForm.getCvv())
                .postalCode(cardForm.getPostalCode());

        Card.tokenize(mBraintreeFragment, cardBuilder);
    }

    @Override
    public void startPaypalPayment() {

        PayPal.authorizeAccount(mBraintreeFragment);
    }

    @Override
    public void callApiSettings() {

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

        Router currentRouter = getCurrentRouter();
        Controller currentController = GateKeeper.getCurrentControllerOnRouter(currentRouter);

        if (currentController instanceof CheckoutController || PaymentInfo.isThreeDSecureCalled()) {
            callCreatePaymentTransaction(PaymentInfo.getPaymentType(), paymentMethodNonce.getNonce(), "");
        } else {
            callCreatePaymentMethod(PaymentInfo.getPaymentType(), paymentMethodNonce.getNonce());
        }

    }

    public void isViewPagerSet(boolean val) {
        mIsViewPagerSet = val;
    }


    public MainController getMainController() {
        return (MainController) mMainController;
    }

    public void setCategoriesRouter(Router router) {
        mCategoriesRouter = router;
    }

    public Router getSearchFilterRouter() {
        return mSearchFilterRouter;
    }


    public void setSearchFilterRouter(Router router) {
        mSearchFilterRouter = router;
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

    public void setSaleItemsRouter(Router router) {
        mMainRouter = router;
    }

    public Router getSaleItemsRouter() {
        return mMainRouter;
    }

    public void setAccountsRouter(Router router) {
        mAccountsRouter = router;
    }

    public Router getAccountsRouter() {
        return mAccountsRouter;
    }


    public void setDraggableViewPager(boolean isDraggable) {
        getMainController().setViewpagerDraggable(isDraggable);
    }

    public String getMyTemplateTexts(String detailKey) {
        return mPresenter.getStoredTemplateTexts(detailKey);
    }

    public boolean isAuthorized() {
        return mPresenter.isAuthorized();
    }

    public void splashShownCallback() {
        mMainController = ControllerFactory.getInstance(GateKeeper.Destination.MAIN);
        mRouter.setRoot(RouterTransaction.with(mMainController)
                .tag("Main"));

    }

    public void setShopsAsVisibleContainer() {

    }

    private void registerInternetCheckReceiver() {
        IntentFilter internetFilter = new IntentFilter();
        internetFilter.addAction("android.net.wifi.STATE_CHANGE");
        internetFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver, internetFilter);
    }

    /**
     *  Runtime Broadcast receiver inner class to capture internet connectivity events
     */
    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateSnackbar(isNetworkConnected());
        }
    };

    public void updateSnackbar(boolean isOnline) {
        if(!isOnline && !mSnackbar.isShown()) {
            showSnackBar(getString(R.string.no_internet_connection), true);
        }else if(isOnline && mSnackbar.isShown()){
            dismissSnackBar();
        }
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

    public void setAddPaymentControllerIsFromCart(boolean val){
        mIsAddPaymentControllerFromCart = val;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void hideNoNetworkLayout() {

    }

    @Override
    public void showNoNetworkLayout() {

    }
}
