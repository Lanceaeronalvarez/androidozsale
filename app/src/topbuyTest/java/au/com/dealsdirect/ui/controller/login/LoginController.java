package au.com.dealsdirect.ui.controller.login;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.CallbackManager;
import com.facebook.internal.CallbackManagerImpl;

import java.util.regex.Pattern;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.utils.BundleBuilder;

/**
 * Created by smartwave on 23/10/2017.
 */

public class LoginController extends BaseController implements LoginMvpView {

    @Inject
    LoginMvpPresenter<LoginMvpView> mPresenter;

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

    }

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
    public void showLoginSuccessful(String loginTicket) {

    }

    @Override
    public void showLoginError(String message) {

    }

    @Override
    public void showRegistration() {

    }

    @Override
    public void showForgotPassword() {

    }
}
