package au.com.dealsdirect.ui.main;

import android.content.Intent;
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
import au.com.dealsdirect.ui.controller.categories.CategoriesController;
import au.com.dealsdirect.ui.controller.checkout.addpayment.AddPaymentController;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutController;
import au.com.dealsdirect.ui.controller.checkout.paymentsuccess.PaymentSuccessController;
import au.com.dealsdirect.ui.controller.home.HomeController;
import au.com.dealsdirect.ui.controller.login.LoginController;
import au.com.dealsdirect.ui.controller.main.MainController;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsController;
import au.com.dealsdirect.ui.controller.shops.ShopsController;
import au.com.dealsdirect.ui.controller.splash.SplashScreenController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.DialogUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends BaseActivity implements MainMvpView {

    private static final String TAG = "MainActivity";
    private static final String PAYMENT_TYPE_MYPAY = "mypay";

    @Inject
    MainMvpPresenter<MainMvpView> mPresenter;

    @BindView(R.id.activity_main_frame)
    ViewGroup mContainer;

    private BraintreeFragment mBraintreeFragment;
    private String mPaymentType;
    private PaymentMethod mCurrentPaymentMethod;
    private String mAuthorization;
    private Router mRouter;
    private FetchTokenHandler mFetchTokenHandler;

    private MainController mMainController;
    private ShopsController mShopController;
    private CategoriesController mCategoriesController;

    private Router mHomeRouter;
    private Router mCategoriesRouter;
    private Router mAccountsRouter;
    private Router mCheckoutRouter;


    private boolean mIsFromCategories = false;
    private boolean mIsViewPagerSet = false;
    private boolean isSearchActive = false;
    private boolean isTemplateTextsStored = false;

    private Ourpay mOurpay;
    private boolean mThreeDSecureRequired;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getActivityComponent().inject(this);

        setUnBinder(ButterKnife.bind(this));

        mPresenter.onAttach(this);
        mPresenter.callGetTemplateTexts();

        mMainController = MainController.newInstance();
        mRouter = Conductor.attachRouter(this, mContainer, savedInstanceState);
        if (!mRouter.hasRootController()) {
            mRouter.setRoot(RouterTransaction.with(SplashScreenController.newInstance())
                    .popChangeHandler(new VerticalChangeHandler()));
        }

        setUp();
    }

    @Override
    protected void setUp() {

        // Initialize GCM
        mPresenter.initializeNotifications(getApplicationContext());

        // Call API settings
        mPresenter.callGetServerSettings();
        mPresenter.callGetAppSettingsSection(this);

    }

    @Override
    protected void onDestroy() {
        mPresenter.onDetach();
        mMainController = null;
        mShopController = null;
        mCategoriesController = null;
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onAttach(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Call router for callbacks after going out the app and back inside
        mRouter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {

        if (mIsViewPagerSet) {
            Router currentRouter = getMainController().getHomeController().getCurrentRouter();
            Controller currentController = getMainController().getHomeController().getCurrentControllerOnRouter(currentRouter);

            switch (getMainController().getHomeViewPager().getCurrentItem()) {
                case 0:
                    if (mCategoriesRouter.getBackstackSize() == 1) { //go back to shops
                        setRootViewpagerItem(1);
                    }
                    break;
                case 1:
                    if (mIsFromCategories) {
                        if (currentController instanceof ShopsController) { //reset shops controller root.
                            ShopsController shopsController = new ShopsController();
                            setShopController(shopsController);
                            currentRouter.setRoot(RouterTransaction.with(shopsController).tag(ShopsController.TAG));
                            setRootViewpagerItem(0);
                            setDraggableViewPager(true);
                        } else if (currentController instanceof SaleItemsController) {
                            setRootViewpagerItem(0);
                            currentRouter.handleBack();
                        } else {
                            currentRouter.handleBack();
                        }
                        mIsFromCategories = false;
                    } else {
                        if (currentController instanceof ShopsController && currentRouter.getBackstack().size() == 1) {
                            //exit app
                            DialogUtils.showYesNoDialog(
                                    this,
                                    getString(R.string.dealsdirect),
                                    getString(R.string.exit_app),
                                    getString(R.string.exit),
                                    getString(R.string.no),
                                    (dialogInterface, i) -> finish(),
                                    (dialogInterface, i) -> {
                                    });
                            break;
                        } else if (currentRouter.getBackstackSize() == 1) { //From Bottom Nav
                            getMainController().showBottomNav();
                            setShopsAsVisibleContainer();
                            break;
                        } else {
                            getMainController().showBottomNav();
                            currentRouter.handleBack();
                        }
                    }
                    break;
            }
        } else {
            finish();
        }
    }

    @Override
    public void showLoginController(Router router, AuthHandler handler) {
        //pinapasa yung router, para kahit child router man siya ng kung ano mang view, pwedeng siya ang tumawag.
        router.pushController(RouterTransaction.with(LoginController.newInstance(handler))
                .tag(LoginController.TAG)
                .pushChangeHandler(new VerticalChangeHandler())
                .popChangeHandler(new VerticalChangeHandler()));
    }

    @Override
    public void onCancel(int requestCode) {
        mFetchTokenHandler = null;
    }

    @Override
    public void onError(Exception error) {
        if (error instanceof ErrorWithResponse) {
//            hideProgressDialog();
//            CustomAlertDialog.showCustomAlertDialog(this, CustomAlertDialog.CustomDialogIconState.NEGATIVE, error.getMessage());
        } else {

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

//                Crashlytics.log(error.getMessage());
                //Call braintree client reset on error
                performResetWithAuthFetch();
            }
        }
    }

    @Override
    public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {

        BraintreeResponseListener<String> handler = deviceData ->
                mPresenter.createPaymentMethod(deviceData, paymentMethodNonce.getNonce(), mPaymentType);

        if (!mPresenter.getKountMerchantId().isEmpty()) {
            DataCollector.collectDeviceData(mBraintreeFragment, mPresenter.getKountMerchantId(), handler);
        } else {
            DataCollector.collectDeviceData(mBraintreeFragment, handler);
        }

    }

    @Override
    public void performResetWithAuthFetch() {
        performReset();
        fetchAuthorization(null);
    }

    @Override
    public void performReset() {

        setPaymentMethodSelected(null);
        mAuthorization = null;
        mPaymentType = null;

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
        mFetchTokenHandler = handler;
        //Don't proceed to call if not logged in
        mPresenter.fetchBTAuthorization();

    }

    @Override
    public void callCreatePaymentTransaction(String paymentNonce) {

        BraintreeResponseListener<String> handler = deviceData ->
                mPresenter.callCreatePaymentTransaction(deviceData, mPaymentType, paymentNonce, getPaymentMethodSelected().getToken());

        if (!mPresenter.getKountMerchantId().isEmpty()) {
            DataCollector.collectDeviceData(mBraintreeFragment, mPresenter.getKountMerchantId(), handler);
        } else {
            DataCollector.collectDeviceData(mBraintreeFragment, handler);
        }

    }

    @Override
    public void callCreatePaymentTransaction(String paymentType, String paymentNonce) {
        mPaymentType = paymentType;
        BraintreeResponseListener<String> handler = deviceData ->
                    mPresenter.callCreatePaymentTransaction(deviceData, mPaymentType, paymentNonce, getPaymentMethodSelected().getToken());

        if (!mPresenter.getKountMerchantId().isEmpty()) {
            DataCollector.collectDeviceData(mBraintreeFragment, mPresenter.getKountMerchantId(), handler);
        } else {
            DataCollector.collectDeviceData(mBraintreeFragment, handler);
        }
    }

    @Override
    public void storeTemplateTexts(GetTemplateTextsResponse.GetTemplateTextsValue value) {
        if (value != null) {

            isTemplateTextsStored = true;
        }
    }

    @Override
    public void onAuthorizationFetched(String paymentToken, String paymentMethod) {
        mAuthorization = paymentToken;
        mPaymentType = paymentMethod;

        try {
            mBraintreeFragment = BraintreeFragment.newInstance(this, mAuthorization);

        } catch (InvalidArgumentException e) {
            onError(e);
        }
    }

    public void startPaypalPayment() {
        PayPal.authorizeAccount(mBraintreeFragment);
    }

    public void onPurchase(CardForm cardForm) {
        CardBuilder cardBuilder = new CardBuilder()
                .cardNumber(cardForm.getCardNumber())
                .expirationMonth(cardForm.getExpirationMonth())
                .expirationYear(cardForm.getExpirationYear())
                .cvv(cardForm.getCvv())
                .postalCode(cardForm.getPostalCode());

        Timber.d(TAG, "BT_cardNumber: " + cardForm.getCardNumber());
        Timber.d(TAG, "BT_expirationMonth: " + cardForm.getExpirationMonth());
        Timber.d(TAG, "BT_expirationYear: " + cardForm.getExpirationYear());
        Timber.d(TAG, "BT_cvv: " + cardForm.getCvv());

        Card.tokenize(mBraintreeFragment, cardBuilder);

    }

    public PaymentMethod getPaymentMethodSelected() {
        return mCurrentPaymentMethod;
    }

    @Override
    public void setPaymentMethodSelected(PaymentMethod paymentMethodSelected) {
        this.mCurrentPaymentMethod = paymentMethodSelected;
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


    @Override
    public void createPaymentMethodSuccess(PaymentMethod lastPaymentMethod) {

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
    public void createPaymentTransactionSuccess(CreatePaymentTransaction.ResponseValue responseValue) {
        fetchAuthorization(null);

        if (responseValue.isPaid()) {

            if (mPaymentType.equals(PAYMENT_TYPE_MYPAY)) {
                setPaymentSuccessOurpay(responseValue);
            } else {

                mOurpay.setCanUse(false);
            }

            mCheckoutRouter.pushController(RouterTransaction.with(new PaymentSuccessController(responseValue))
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler()));

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

    public Controller getHomeRouterCurrentController() {
        int backstackSize = mHomeRouter.getBackstack().size();
        if (backstackSize > 0) {
            return mHomeRouter.getBackstack().get(backstackSize - 1).controller();
        } else {
            return null;
        }
    }

    public void setRootViewpagerItem(int item) {

        switch (item) {
            case 0:
                mMainController.goToCategories();
                break;
            case 1:
                mMainController.goToShops();
                break;
            default:
                mMainController.goToShops();
                break;
        }
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

    public void setCheckoutRouter(Router router){
        mCheckoutRouter = router;
    }

    public Router getCheckoutRouter(){
        return mCheckoutRouter;
    }

    public void setCategoriesRouter(Router router) {
        mCategoriesRouter = router;
    }

    public void setAccountsRouter(Router router) {
        mAccountsRouter = router;
    }

    public void goToSaleItemsFromCategory(Bundle bundle) {
        mShopController.goToItemsFromCategories(bundle);

        final Handler handler = new Handler();
        handler.postDelayed(() -> mMainController.goToShops(), 400);
    }

    public void goToSaleItemsFromSearchCategory() {
        mIsFromCategories = true;
        mShopController.goToSaleItemsFromCategorySearch();
        final Handler handler = new Handler();
        handler.postDelayed(() -> setRootViewpagerItem(1), 400);
    }

    public void goToSalesFromCategory(GetCategoryTreeResponse getCategoryTreeResponse) {
        mIsFromCategories = true;
        mShopController.goToSalesFromCategories(getCategoryTreeResponse);
        setRootViewpagerItem(1);
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
        mRouter.setRoot(RouterTransaction.with(mMainController)
                .tag("Home"));

    }

    public void isViewPagerSet(boolean val) {
        mIsViewPagerSet = val;
    }

    public void setShopsAsVisibleContainer() {
        getMainController().getHomeController().setVisibleContainer(0);
    }

    public void callGetAppSettings() {
        mPresenter.callGetAppSettings();
    }


    public Ourpay getOurpay() {
        return mOurpay;
    }

    public void setOurpay(Ourpay mOurpay) {
        this.mOurpay = mOurpay;
    }

    public boolean isThreeDSecureRequired() {
        return mThreeDSecureRequired;
    }

    public void setThreeDSecureRequired(boolean mThreeDSecureRequired) {
        this.mThreeDSecureRequired = mThreeDSecureRequired;
    }

    public String getPaymentType() {
        return mPaymentType;
    }

    public void setPaymentType(String mPaymentType) {
        this.mPaymentType = mPaymentType;
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
            setOurpay(paymentSuccessOurpay);
        } catch (Exception e) {

            paymentSuccessOurpay.setCanUse(false);
            paymentSuccessOurpay.setPlannedTransactions(null);
            paymentSuccessOurpay.setState(paymentSuccessOurpay.getState() | OurpayState.ERROR);
        }

    }

    public String getMyTemplateTexts(String detailKey) {
        return mPresenter.getStoredTemplateTexts(detailKey);
    }
}
