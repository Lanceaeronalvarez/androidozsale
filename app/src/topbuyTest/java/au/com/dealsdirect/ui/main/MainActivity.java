package au.com.dealsdirect.ui.main;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.support.RouterPagerAdapter;
import com.braintreepayments.api.models.PaymentMethodNonce;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentTransaction;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.data.network.model.legalities.GetTemplateTextsResponse;
import au.com.dealsdirect.ui.base.BaseActivity;
import au.com.dealsdirect.ui.controller.main.MainCustomViewPager;
import au.com.dealsdirect.ui.main.MainMvpPresenter;
import au.com.dealsdirect.ui.main.MainMvpView;
import au.com.dealsdirect.utils.AppConstants;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by smartwave on 30/10/2017.
 */

public class MainActivity extends BaseActivity implements MainMvpView {

    @Inject
    MainMvpPresenter<MainMvpView> mPresenter;

    @BindView(R.id.viewpage_main)
    MainCustomViewPager mMainViewPager;

    boolean mIsViewPagerSet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getActivityComponent().inject(this);

        setUnBinder(ButterKnife.bind(this));

        mPresenter.onAttach(this);
        mPresenter.callGetTemplateTexts();

        //Init All analytics sdk
//        initializeAnalytics();


//        mRouter = Conductor.attachRouter(this, mContainer, savedInstanceState);
//        mRouter.setRoot(RouterTransaction.with(SplashScreenController.newInstance())
//                .popChangeHandler(new VerticalChangeHandler()));

        setUp();
    }

    @Override
    protected void setUp() {

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

    }

    @Override
    public void loginSuccessHandler(Router router, AppConstants.POP_FLAG flag, AppConstants.AUTH_FLAG authFlag) {

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

}
