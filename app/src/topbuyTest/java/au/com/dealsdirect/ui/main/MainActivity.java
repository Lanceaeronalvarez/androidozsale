package au.com.dealsdirect.ui.main;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.bluelinelabs.conductor.Conductor;
import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;
import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.braintreepayments.cardform.view.CardForm;
import com.mysale.genie.utility.RxBus;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentTransaction;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.data.network.model.legalities.GetTemplateTextsResponse;
import au.com.dealsdirect.ui.base.BaseActivity;
import au.com.dealsdirect.ui.controller.categories.CategoriesController;
import au.com.dealsdirect.ui.controller.main.MainController;
import au.com.dealsdirect.ui.controller.main.MainCustomViewPager;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsController;
import au.com.dealsdirect.ui.controller.splash.SplashScreenController;
import au.com.dealsdirect.ui.main.MainMvpPresenter;
import au.com.dealsdirect.ui.main.MainMvpView;
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
                    getMainController().getHomeViewPager().setCurrentItem(1);
                } else {
                    getAccountsRouter().handleBack();
                    GateKeeper.updateCurrentLocation(getAccountsRouter());
                }
                break;
            case 1: //sale items

                SaleItemsController saleItemsController = (SaleItemsController) GateKeeper.getCurrentControllerOnRouter(getSaleItemsRouter());
                if (!saleItemsController.isSearchFiltersShown() && getSaleItemsRouter().getBackstackSize() == 1) {
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

    }

    @Override
    public void loginSuccessMethods() {

    }

    @Override
    public void callLoginTicket() {

    }

    @Override
    public void callLogout(AuthHandler handler) {
        mPresenter.callLogout(handler);
    }

    @Override
    public void onAuthorizationFetched(String paymentToken, String paymentMethod) {

    }

    @Override
    public void performBraintreeReset() {

    }

    @Override
    public void performResetWithAuthFetch() {

    }

    @Override
    public void fetchAuthorization(FetchTokenHandler fetchTokenHandler) {

    }

    @Override
    public void setPaymentMethodSelected(PaymentMethod paymentMethodSelected) {

    }

    @Override
    public BraintreeFragment getBraintreeFragment() {
        return null;
    }

    @Override
    public boolean isBraintreeInitialized() {
        return false;
    }

    @Override
    public void showGetPaymentMethodNonceSuccess(String nonce) {

    }

    @Override
    public void callCreatePaymentMethod(String type, String nonce) {

    }

    @Override
    public void showCreatePaymentMethodSuccess(PaymentMethod lastPaymentMethod) {

    }

    @Override
    public void callCreatePaymentTransaction(String type, String nonce, String token) {

    }

    @Override
    public void showCreatePaymentTransactionSuccess(String paymentType, CreatePaymentTransaction.ResponseValue responseValue) {

    }

    @Override
    public void showCreatePaymentTransactionFailure(String errorMessage) {

    }

    @Override
    public PaymentMethod getPaymentMethodSelected() {
        return null;
    }

    @Override
    public boolean getIsMyPayEnabled() {
        return false;
    }

    @Override
    public void onPurchase(CardForm cardForm) {

    }

    @Override
    public void startPaypalPayment() {

    }

    @Override
    public void onCancel(int requestCode) {

    }

    @Override
    public void onError(Exception error) {

    }

    @Override
    public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {

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
}
