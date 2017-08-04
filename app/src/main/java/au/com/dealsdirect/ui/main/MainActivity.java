package au.com.dealsdirect.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentTransaction;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.ui.base.BaseActivity;
import au.com.dealsdirect.ui.controller.account.AccountController;
import au.com.dealsdirect.ui.controller.categories.CategoriesController;
import au.com.dealsdirect.ui.controller.checkout.addpayment.AddPaymentController;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutController;
import au.com.dealsdirect.ui.controller.checkout.paymentsuccess.PaymentSuccessController;
import au.com.dealsdirect.ui.controller.login.LoginController;
import au.com.dealsdirect.ui.controller.main.MainController;
import au.com.dealsdirect.ui.controller.shops.ShopsController;
import au.com.dealsdirect.ui.controller.splash.SplashScreenController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.DialogUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends BaseActivity implements MainMvpView {

    private static final String TAG = "MainActivity";

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

    private int mViewPagerCurrentItem;
    private boolean mIsFromCategories = false;
    private boolean isSearchActive = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getActivityComponent().inject(this);

        setUnBinder(ButterKnife.bind(this));

        mPresenter.onAttach(this);

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

        switch (mViewPagerCurrentItem) {
            case 0:
                if (mCategoriesRouter.getBackstackSize() == 1) {
                    setRootViewpagerItem(1);
                } else {
                    mRouter.handleBack();
                }
                break;
            case 1:
                int backstackSize = mHomeRouter.getBackstackSize() - 1;
                String tag = mHomeRouter.getBackstack().get(backstackSize).tag();

                if (getMainController().getHomeController().getIsAccountControllerActive()){
                    Router accountRouter = getMainController().getHomeController().getAccountsRouter();
                    int accountBackstackSize = accountRouter.getBackstackSize()-1;
                    String accountTag = accountRouter.getBackstack().get(accountBackstackSize).tag();
                    if ((accountTag != null) && accountTag.equals(getString(R.string.account_controller_tag))){

                        goToShops();
                        break;
                    }else{
                        getMainController().getHomeController().getAccountsRouter().handleBack();

                    }
                }else{
                    if(mIsFromCategories && mHomeRouter.getBackstackSize() == 1){
                        goToCategoriesFromSales();
                        mIsFromCategories = false;
                    }else if (mHomeRouter.getBackstackSize() == 1) {

                        if((tag != null) && !tag.equals(ShopsController.TAG) && !tag.equals("Search")) {
                            goToShops();
                            break;
                        }

                    DialogUtils.showYesNoDialog(
                            this,
                            getString(R.string.dealsdirect),
                            getString(R.string.exit_app),
                            getString(R.string.exit),
                            getString(R.string.no),
                            (dialogInterface, i) -> finish(),
                            (dialogInterface, i) -> {

                            });
                } else {

                        if(tag!=null && tag.equals(getString(R.string.search_tag))){

                        mHomeRouter.handleBack();
                        mShopController.showSearchToolbar();

                            Handler handler = new Handler();
                            handler.postDelayed(() ->
                                    mShopController.hideSearchToolbar(),500);

                        } else if ((tag != null) && tag.equals(getString(R.string.sale_items_from_category))) {

                        goToCategoriesFromSaleItems();
                    } else {
                        mHomeRouter.handleBack();
                    }
                }
                break;
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
    public void callLogout() {
        mPresenter.callLogout();
    }

    @Override
    public void logoutSuccessful() {

        CustomAlertDialog.showCustomAlertDialog(this,
                CustomAlertDialog.CustomDialogIconState.POSITIVE,
                "Logout Successful");
        ((AccountController)getMainController().getHomeController().getAccountsRouter().getControllerWithTag("AccountController")).resetAccounts();
    }

    @Override
    public void createPaymentMethodSuccess(PaymentMethod lastPaymentMethod) {
        Controller currentController = getHomeRouterCurrentController();

        if ((currentController instanceof AddPaymentController) && ((AddPaymentController) currentController).isCalledFromAccounts()) {
            ((AddPaymentController) currentController).showAddPaymentResult(true, "");
        } else {
            setPaymentMethodSelected(lastPaymentMethod);
            mHomeRouter.popCurrentController();
        }
    }

    @Override
    public void createPaymentTransactionSuccess(CreatePaymentTransaction.ResponseValue responseValue) {
        fetchAuthorization(null);

        if (responseValue.isPaid()) {

//            GCartUtil.setValueToCart(0);
//            RxBus.instance().post("update_cart_items_immediate");

            mHomeRouter.pushController(RouterTransaction.with(new PaymentSuccessController(responseValue))
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler()));

        } else {

            CustomAlertDialog.showCustomAlertDialog(this, CustomAlertDialog.CustomDialogIconState.NEGATIVE, responseValue.getD().getMessage());

            if (getHomeRouterCurrentController() instanceof CheckoutController) {
                CheckoutController checkoutController = (CheckoutController) getHomeRouterCurrentController();
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

    public void setCategoriesRouter(Router router) {
        mCategoriesRouter = router;
    }

    public void setCurrentItem(int position) {
        mViewPagerCurrentItem = position;
    }

    public void goToSaleItemsFromCategory(Bundle bundle) {
        mShopController.goToItemsFromCategories(bundle);

        final Handler handler = new Handler();
        handler.postDelayed(() -> mMainController.goToShops(), 400);
    }

    public void goToSaleItemsFromSearchCategory(){
        mShopController.goToSaleItemsFromCategorySearch();
        final Handler handler = new Handler();
        handler.postDelayed(() -> mMainController.goToShops(), 400);
    }

    public void goToSalesFromCategory(GetCategoryTreeResponse getCategoryTreeResponse) {
        mIsFromCategories = true;
        mShopController.goToSalesFromCategories(getCategoryTreeResponse);
        mMainController.goToShops();
    }

    public void goToCategoriesFromSaleItems() {
        Handler handler = new Handler();
        handler.postDelayed(() -> mHomeRouter.handleBack(), 500);

        mMainController.goToCategories();
        mMainController.setViewpagerDraggable(true);
    }

    public void goToCategoriesFromSales() {
        mMainController.goToCategories();
        mShopController.loadShopBanners();

    }

    public void setShopController(ShopsController shopsController) {
        mShopController = shopsController;
    }

    public MainController getMainController() {
        return mMainController;
    }

    public void bottomNavSalesClick() {
        int backstackSize = mHomeRouter.getBackstackSize() - 1;
        if (backstackSize != -1) {
            String tag = mHomeRouter.getBackstack().get(backstackSize).tag();

            if ((tag != null) && tag.equals(getString(R.string.sale_items_from_category))) {
                AppLogger.d(TAG, "(tag!=null) && tag.equals(getString(R.string.sale_items_from_category))");

                mHomeRouter.handleBack();
            }
        }
    }

    public void setCategoriesController(CategoriesController categoriesController) {
        mCategoriesController = categoriesController;
    }

    public CategoriesController getCategoriesController() {
        return mCategoriesController;
    }

    public void isFromCategories(boolean isFromCategories) {
        mIsFromCategories = isFromCategories;
    }

    public void splashShownCallback() {
        mRouter.setRoot(RouterTransaction.with(mMainController)
                .tag("Home"));

    }

    public void goToShops() {
        getMainController().getHomeController().showShopController();
    }

//    public DataManager getDataManager(){
//        return mPresenter.getMainDataManager();
//    }
}
