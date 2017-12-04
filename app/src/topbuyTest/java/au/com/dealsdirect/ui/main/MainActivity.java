package au.com.dealsdirect.ui.main;

import android.os.Bundle;
import android.util.Log;
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
import com.mysale.genie.utility.RxBus;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentTransaction;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.MyPayDetails;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.data.network.model.legalities.GetTemplateTextsResponse;
import au.com.dealsdirect.service.ourpay.Ourpay;
import au.com.dealsdirect.service.ourpay.OurpayState;
import au.com.dealsdirect.ui.base.BaseActivity;
import au.com.dealsdirect.ui.controller.categories.CategoriesController;
import au.com.dealsdirect.ui.controller.main.MainController;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsController;
import au.com.dealsdirect.ui.controller.splash.SplashScreenController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.DialogUtils;
import au.com.dealsdirect.utils.IntrospectionUtils;
import au.com.dealsdirect.utils.module.ControllerFactory;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import butterknife.ButterKnife;

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
    private FetchTokenHandler mFetchTokenHandler;

    AuthHandler mAuthHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getActivityComponent().inject(this);

        setUnBinder(ButterKnife.bind(this));

        mPresenter.onAttach(this);
        mPresenter.callGetTemplateTexts();

//        Init All analytics sdk
//        initializeAnalytics();

        mRouter = Conductor.attachRouter(this, mContainer, savedInstanceState);
        mRouter.setRoot(RouterTransaction.with(SplashScreenController.newInstance())
                .popChangeHandler(new VerticalChangeHandler()));

        setUp();
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
                    Log.d("mainactivity","if statement");
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
                    if (!((SaleItemsController) controller).isSearchFiltersShown() && getSaleItemsRouter().getBackstackSize() == 1) {
                        //exit app
                        DialogUtils.showYesNoDialog(
                                this,
                                getString(R.string.exit_app_name),
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
                    GateKeeper.updateCurrentLocation(getCheckoutRouter());
                }
                break;
            default:
                break;
        }

    }

    @Override
    public FetchTokenHandler getFetchTokenHandler() {
        return null;
    }

    @Override
    public void callGCMRegisterSubscriber() {

    }

    @Override
    public void storeTemplateTexts(GetTemplateTextsResponse.GetTemplateTextsValue templateKeysValue) {
        if (templateKeysValue != null) {

            isTemplateTextsStored = true;
        }
    }

    @Override
    public Router getCurrentRouter() {
        return null;
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

        CustomAlertDialog.showCustomAlertDialog(this, CustomAlertDialog.CustomDialogIconState.NEGATIVE, message);
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
//
//        // Pop current fragment and return to cart controller
//        HomeController homeController = getMainController().getHomeController();
//        Router currentRouter = homeController.getCurrentRouter();
//        Controller currentController = homeController.getCurrentControllerOnRouter(currentRouter);
//
//        if ((currentController instanceof AddPaymentController) && ((AddPaymentController) currentController).isCalledFromAccounts()) {
//            ((AddPaymentController) currentController).showAddPaymentResult(true, "");
//        } else {
//            setPaymentMethodSelected(lastPaymentMethod);
//            currentRouter.handleBack();
//        }
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
//        if (responseValue.isPaid()) {
//
//            if (paymentType.equals(PaymentInfo.TYPE_MYPAY)) {
//                setPaymentSuccessOurpay(responseValue);
//
//            } else if (PaymentInfo.getOurpay() != null) {
//                PaymentInfo.getOurpay().setCanUse(false);
//            }
//
//            mCheckoutRouter.pushController(RouterTransaction.with(new PaymentSuccessController(responseValue))
//                    .pushChangeHandler(new HorizontalChangeHandler())
//                    .popChangeHandler(new HorizontalChangeHandler()));
//
//            if (getMainController().getHomeController()!=null)
//                getMainController().getHomeController().showCheckoutController();
//
//        } else {
//
//            Router currentRouter = getMainController().getHomeController().getCurrentRouter();
//            Controller currentController = getMainController().getHomeController().getCurrentControllerOnRouter(currentRouter);
//
//            CustomAlertDialog.showCustomAlertDialog(this, CustomAlertDialog.CustomDialogIconState.NEGATIVE, responseValue.getD().getMessage());
//
//            if (currentController instanceof CheckoutController) {
//                CheckoutController checkoutController = (CheckoutController) currentController;
//                checkoutController.loadCart();
//            }
//        }
    }

    @Override
    public void showCreatePaymentTransactionFailure(String errorMessage) {
//        if (errorMessage != null) {
//
//            CustomAlertDialog.showCustomAlertDialog(this, CustomAlertDialog.CustomDialogIconState.NEGATIVE, errorMessage);
//            mCheckoutRouter.popToRoot();
//
//            Controller controller = getMainController().getHomeController().getCurrentControllerOnRouter(mCheckoutRouter);
//            if (controller != null && controller instanceof CheckoutController) {
//                ((CheckoutController) controller).loadCart();
//            }
//        }
    }

    @Override
    public PaymentMethod getPaymentMethodSelected() {
        return null;
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

//        HomeController homeController = getMainController().getHomeController();
//        Router currentRouter = homeController.getCurrentRouter();
//        Controller currentController = homeController.getCurrentControllerOnRouter(currentRouter);
//
//        if (currentController instanceof CheckoutController || PaymentInfo.isThreeDSecureCalled()) {
//            callCreatePaymentTransaction(PaymentInfo.getPaymentType(), paymentMethodNonce.getNonce(), "");
//        } else {
//            callCreatePaymentMethod(PaymentInfo.getPaymentType(), paymentMethodNonce.getNonce());
//        }

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

}
