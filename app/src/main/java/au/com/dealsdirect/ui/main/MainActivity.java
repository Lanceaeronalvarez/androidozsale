package au.com.dealsdirect.ui.main;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.Conductor;
import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;
import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.Card;
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
import com.braintreepayments.api.models.CardBuilder;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.braintreepayments.cardform.view.CardForm;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentTransaction;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.ui.base.BaseActivity;
import au.com.dealsdirect.ui.controller.account.AccountController;
import au.com.dealsdirect.ui.controller.categories.CategoriesController;
import au.com.dealsdirect.ui.controller.checkout.addpayment.AddPaymentController;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutController;
import au.com.dealsdirect.ui.controller.checkout.paymentsuccess.PaymentSuccessController;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.ViewContactsController;
import au.com.dealsdirect.ui.controller.home.HomeController;
import au.com.dealsdirect.ui.controller.invite.InviteController;
import au.com.dealsdirect.ui.controller.login.LoginController;
import au.com.dealsdirect.ui.controller.shops.ShopsController;
import au.com.dealsdirect.ui.custom.BottomNavigationViewHelper;
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

    @BindView(R.id.controller_home_bottom_nav)
    BottomNavigationView mBottomNavigationView;

    private int mPreviousTab = R.id.action_shop;
    private int mCurrentTab = R.id.action_shop;


    private BraintreeFragment mBraintreeFragment;
    private String mPaymentType;
    private PaymentMethod mCurrentPaymentMethod;
    private String mAuthorization;

    private Router mRouter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getActivityComponent().inject(this);

        setUnBinder(ButterKnife.bind(this));

        mPresenter.onAttach(this);

        mRouter = Conductor.attachRouter(this, mContainer, savedInstanceState);
        if (!mRouter.hasRootController()) {
            mRouter.setRoot(RouterTransaction.with(HomeController.newInstance())
                    .tag("Home"));
        }

        setUp();
    }

    @Override
    protected void setUp() {


        mPresenter.initServerSettings(this,
                ((MainPresenter) mPresenter).getDataManager()
                        .getCountryId());
        mPresenter.callGetAppSettingsSection(this,
                ((MainPresenter) mPresenter).getDataManager()
                        .getCountryId());

        BottomNavigationViewHelper.disableShiftMode(mBottomNavigationView);
        mRouter.addChangeListener(new ControllerChangeHandler.ControllerChangeListener() {
            @Override
            public void onChangeStarted(@Nullable Controller to, @Nullable Controller from, boolean isPush, @NonNull ViewGroup container, @NonNull ControllerChangeHandler handler) {

            }

            @Override
            public void onChangeCompleted(@Nullable Controller to, @Nullable Controller from, boolean isPush, @NonNull ViewGroup container, @NonNull ControllerChangeHandler handler) {
                if (to instanceof HomeController || to instanceof ShopsController) {
                    mBottomNavigationView.setSelectedItemId(R.id.action_shop);
                } else if (to instanceof AccountController) {
                    mBottomNavigationView.setSelectedItemId(R.id.action_account);
                }else if (to instanceof ViewContactsController) {
                    mBottomNavigationView.setSelectedItemId(R.id.action_contact);
                }else if (to instanceof InviteController) {
                    mBottomNavigationView.setSelectedItemId(R.id.action_invite);
                }else if (to instanceof CheckoutController) {
                    mBottomNavigationView.setSelectedItemId(R.id.action_checkout);
                }
            }
        });

        mBottomNavigationView.setOnNavigationItemSelectedListener(item -> {

            if (mBottomNavigationView.getSelectedItemId() == item.getItemId()) {
                return true;
            }

            mPreviousTab = mCurrentTab;
            mCurrentTab = item.getItemId();

            switch (item.getItemId()) {

                case R.id.action_shop:
                    showShopController();
                    break;

                case R.id.action_account:
                    showAccountController();
                    break;

                case R.id.action_contact:
                    showContactController();
                    break;

                case R.id.action_invite:
                    showInviteController();
                    break;

                case R.id.action_checkout:
                    showCheckoutController();
                    break;
            }
            return true;
        });

    }

    @Override
    protected void onDestroy() {
        mPresenter.onDetach();
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        if (!mRouter.handleBack()) {

            if (mRouter.getBackstackSize()==0){
                DialogUtils.showYesNoDialog(
                        this,
                        getString(R.string.dealsdirect),
                        getString(R.string.exit_app),
                        getString(R.string.exit),
                        getString(R.string.no),
                        (dialogInterface, i) -> {
                            super.onBackPressed();

                        },
                        (dialogInterface, i) -> {

                        });
            }else{
                super.onBackPressed();

            }
        }
    }


    @Override
    public void showCategoryController() {

        mRouter.pushController(RouterTransaction.with(CategoriesController.newInstance())
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));

    }

    @Override
    public void showShopController() {
        mRouter.setRoot(RouterTransaction.with(new ShopsController())
                .pushChangeHandler(new FadeChangeHandler())
                .popChangeHandler(new FadeChangeHandler()));
    }

    @Override
    public void showAccountController() {
        mRouter.pushController(RouterTransaction.with(AccountController.newInstance())
                .pushChangeHandler(new FadeChangeHandler())
                .popChangeHandler(new FadeChangeHandler()));


    }

    @Override
    public void showContactController() {

        mRouter.setRoot(RouterTransaction.with(ViewContactsController.newInstance())
                .pushChangeHandler(new FadeChangeHandler())
                .popChangeHandler(new FadeChangeHandler()));

    }

    @Override
    public void showInviteController() {
        mRouter.setRoot(RouterTransaction.with(InviteController.newInstance())
                .pushChangeHandler(new FadeChangeHandler())
                .popChangeHandler(new FadeChangeHandler()));

    }

    @Override
    public void showCheckoutController() {
        mRouter.pushController(RouterTransaction.with(new CheckoutController())
                .pushChangeHandler(new FadeChangeHandler())
                .popChangeHandler(new FadeChangeHandler()));
    }

    @Override
    public void showLoginController(Router router, AuthHandler handler) {
        //pinapasa yung router, para kahit childe router man siya ng kung ano mang view, pwedeng siya ang tumawag.

        router.pushController(RouterTransaction.with(LoginController.newInstance(handler))
                .tag("Login")
                .pushChangeHandler(new VerticalChangeHandler())
                .popChangeHandler(new VerticalChangeHandler()));
    }

    @Override
    public void onCancel(int requestCode) {

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
        mPresenter.createPaymentMethod(mBraintreeFragment,paymentMethodNonce.getNonce(),mPaymentType);
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

    public void fetchAuthorization(FetchTokenHandler handler) {
        //Don't proceed to call if not logged in
        mPresenter.fetchBTAuthorization(handler);

    }

    public void callCreatePaymentTransaction(String paymentNonce, String paymentToken){
        mPresenter.callCreatePaymentTransaction(mBraintreeFragment,mPaymentType,paymentNonce,paymentToken);
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
    public void createPaymentMethodSuccess(PaymentMethod lastPaymentMethod) {
        Controller currentController = getMainRouterCurrentController();

        if ((currentController instanceof AddPaymentController) && ((AddPaymentController)currentController).isCalledFromAccounts()) {
            ((AddPaymentController)currentController).showAddPaymentResult(true, "");
        } else {
            setPaymentMethodSelected(lastPaymentMethod);
            mRouter.popCurrentController();
        }
    }

    @Override
    public void createPaymentTransactionSuccess(CreatePaymentTransaction.ResponseValue responseValue) {
        fetchAuthorization(null);

        if (responseValue.isPaid()) {

//            GCartUtil.setValueToCart(0);
//            RxBus.instance().post("update_cart_items_immediate");

            mRouter.pushController(RouterTransaction.with(new PaymentSuccessController(responseValue))
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler()));

        } else {
            DialogUtils.showYesDialog(this, "Error", responseValue.getD().getMessage(), "ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });


            if(getMainRouterCurrentController() instanceof CheckoutController){
                CheckoutController checkoutController = (CheckoutController)getMainRouterCurrentController();
                checkoutController.loadCart();
            }
        }
    }

    public Controller getMainRouterCurrentController(){
        int backstackSize = mRouter.getBackstack().size();
        return mRouter.getBackstack().get(backstackSize - 1).controller();

    }
}
