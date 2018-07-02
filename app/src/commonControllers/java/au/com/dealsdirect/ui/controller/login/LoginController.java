package au.com.dealsdirect.ui.controller.login;
/*
 * Created by CodeineBot on 6/15/17.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.facebook.CallbackManager;
import com.facebook.internal.CallbackManagerImpl;

import java.util.regex.Pattern;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.forgotpassword.ForgotPasswordController;
import au.com.dealsdirect.ui.controller.register.RegisterController;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import butterknife.OnClick;

public class LoginController extends BaseController implements LoginMvpView {

    public static final String TAG = "LoginController";
    public static final String AUTH_HANDLER = "AUTH_HANDLER";

    @Inject
    LoginMvpPresenter<LoginMvpView> mPresenter;

    @BindView(R.id.toolbar_title_login)
    Toolbar mToolbar;
    @BindView(R.id.partial_toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.controller_login_email_edittext)
    EditText mEmailEditText;
    @BindView(R.id.controller_login_password_edittext)
    EditText mPasswordEditText;
    @BindView(R.id.controller_login_button)
    Button mLoginButton;
    @BindView(R.id.controller_login_signup_text)
    TextView mSignUpTextView;
    @BindView(R.id.controller_login_forgot_password_text)
    TextView mForgotPasswordTextView;
    @BindView(R.id.partial_toolbar_left_view)
    TextView mLeftButton;

    @Nullable
    @BindView(R.id.controller_login_legalities_container)
    ViewGroup mLegalitiesContainer;
    @Nullable
    @BindView(R.id.controller_login_about_us_textview)
    TextView mAboutUsTextView;
    @Nullable
    @BindView(R.id.controller_login_tnc_textview)
    TextView mTncTextView;
    @Nullable
    @BindView(R.id.controller_login_privacy_textview)
    TextView mPrivacyTextView;

    private boolean isLoginTapped = false;
    private boolean mWillShowRegistration = false;

    private CallbackManager mCallbackManager = CallbackManager.Factory.create();

    public static LoginController newInstance() {
        return new LoginController(
                new BundleBuilder(new Bundle())
                        .build());
    }


    private static final String EMAIL_PATTERN =
            "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";

    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);


    public LoginController(Bundle args) {
        super(args);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        if (!mPresenter.isTablet()) {
            mActivity.getMainController().hideBottomNav();
        }

        super.onAttach(view);
    }

    @Override
    public void onDetach(View view) {
        if (!mPresenter.isTablet() && !mWillShowRegistration) {
            mActivity.getMainController().showBottomNav();
        }

        super.onDetach(view);
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_login, container, false);
        getControllerComponent().inject(this);

        // Init facebook callback
        registerForActivityResult(CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode());
        mCallbackManager = CallbackManager.Factory.create();
        mPresenter.onAttach(this);

        return view;
    }

    @Override
    public void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }


    @Override
    protected void setUp(View view) {
        mActivity.setDraggableViewPager(false);

        boolean shouldToolbarBeVisible = mActivity.getResources().getBoolean(R.bool.login_toolbar_visibility);

        mToolbar.setVisibility(shouldToolbarBeVisible ? View.VISIBLE : View.GONE);
        mToolbarTitle.setText(mActivity.getResources().getString(R.string.login_title));

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isLoginTapped) {
                    callLoginApi();
                }

                isLoginTapped = true;
            }
        });

        if (mLegalitiesContainer != null) {
            mAboutUsTextView.setOnClickListener(v -> onLegalitiesClicked(BundleKeys.TEMPLATE_KEY_ABOUT_US, getString(R.string.account_about_us)));
            mTncTextView.setOnClickListener(v -> onLegalitiesClicked(BundleKeys.TEMPLATE_KEY_TNC, getString(R.string.account_tnc)));
            mPrivacyTextView.setOnClickListener(v -> onLegalitiesClicked(BundleKeys.TEMPLATE_KEY_PRIVACY, getString(R.string.account_privacy)));
        }
    }


    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @OnClick(R.id.partial_toolbar_right_view)
    void onCloseIconClick() {
        mActivity.onBackPressed();
    }

    @Override
    public void showLoginSuccessful(String loginTicket) {
        mActivity.loginSuccessHandler(getRouter(), AppConstants.POP_FLAG.BACK, AppConstants.AUTH_FLAG.LOGIN);
    }

    @Override
    public boolean handleBack() {
        if (mPresenter.isTablet()) {
            if (!mActivity.isAuthorized()) {
                mActivity.getMainController().getHomeController().resetVisibleContainer();
            }
            mActivity.getHomeController().resetAccountRouter();

            return true;
        }

        hideKeyboard();

        return super.handleBack();

    }

    @Override
    public void showLoginError(String message) {

        mActivity.loginErrorHandler(message);

        isLoginTapped = false;
    }

    @Override
    public void showRegistration() {
        mWillShowRegistration = true;
        getRouter().pushController(RouterTransaction.with(RegisterController.newInstance())
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    @Override
    public void showForgotPassword() {
        getRouter().pushController(RouterTransaction.with(ForgotPasswordController.newInstance())
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    private void callLoginApi() {
        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        mPresenter.loginViaEmail(email, password);
    }

    @OnClick(R.id.controller_login_signup_text)
    void onSignUpClick() {
        showRegistration();
    }

    @OnClick(R.id.controller_login_fb_layout)
    void onFacebookButtonClick() {
        mPresenter.onFacebookLogin(mActivity, mCallbackManager, 0);

    }

    @OnClick(R.id.controller_login_forgot_password_text)
    void onForgotPasswordClick() {
        showForgotPassword();
    }

    @OnClick(R.id.partial_toolbar_left_view)
    public void onBackPress() {
        mActivity.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void onLegalitiesClicked(String key, String title) {
        Bundle bundle = new BundleBuilder(new Bundle())
                .putString(BundleKeys.TEMPLATE_KEY, key)
                .putString(BundleKeys.LEGALITIES_TITLE, title)
                .build();

        GateKeeper.push(getRouter(),
                GateKeeper.Destination.LEGALITIES,
                bundle,
                new HorizontalChangeHandler(false),
                new HorizontalChangeHandler());
    }
}
