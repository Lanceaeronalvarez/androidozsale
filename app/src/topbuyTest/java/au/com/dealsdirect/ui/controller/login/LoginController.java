package au.com.dealsdirect.ui.controller.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;
import com.facebook.CallbackManager;
import com.facebook.internal.CallbackManagerImpl;

import java.util.regex.Pattern;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.SwipeableBaseToolBarController;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by smartwave on 23/10/2017.
 */

public class LoginController extends SwipeableBaseToolBarController implements LoginMvpView {

    public static final String TAG = "LoginController";
    public static final String AUTH_HANDLER = "AUTH_HANDLER";

    @Inject
    LoginMvpPresenter<LoginMvpView> mPresenter;

    @BindView(R.id.email)
    EditText mEmailEditText;
    @BindView(R.id.password)
    EditText mPasswordEditText;
    @BindView(R.id.login_button)
    Button mLoginButton;

    private boolean isLoginTapped = false;


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
    protected void setUp(View view) {
        mToolbarTitle.setText("login");

        mActivity.setDraggableViewPager(false);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isLoginTapped) {
                    callLoginApi();
                }

                isLoginTapped = true;
            }
        });
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = super.inflateView(inflater, container);

        fillContent(inflater.inflate(R.layout.controller_login, container, false));

        getControllerComponent().inject(this);

        // Init facebook callback
        registerForActivityResult(CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode());
        mCallbackManager = CallbackManager.Factory.create();
        mPresenter.onAttach(this);
        return view;
    }


    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
        setupSwipingBehavior();
    }


    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    public void showLoginSuccessful(String loginTicket) {
        mActivity.loginSuccessHandler(getRouter(), AppConstants.POP_FLAG.BACK, AppConstants.AUTH_FLAG.LOGIN);
    }

    @Override
    public boolean handleBack() {
        hideKeyboard();
        mActivity.setDraggableViewPager(true);
        return super.handleBack();
    }

    @Override
    public void showLoginError(String message) {

        mActivity.loginErrorHandler(message);

        isLoginTapped = false;
    }

    @Override
    public void showRegistration() {
        GateKeeper.push(getRouter(), GateKeeper.Destination.REGISTER,new VerticalChangeHandler(false),new VerticalChangeHandler());
    }

    @Override
    public void showForgotPassword() {
        GateKeeper.push(getRouter(), GateKeeper.Destination.FORGOT_PASSWORD);
    }

    private void callLoginApi() {
        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        mPresenter.loginViaEmail(email, password);
    }

    @OnClick(R.id.fragment_login_signup_text)
    void onSignUpClick() {
        showRegistration();
    }

    @OnClick(R.id.controller_login_fb_layout)
    void onFacebookButtonClick() {
        mPresenter.onFacebookLogin(mActivity, mCallbackManager);
    }

    @OnClick(R.id.controller_login_forgot_password_text)
    void onForgotPasswordClick() {
        showForgotPassword();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
