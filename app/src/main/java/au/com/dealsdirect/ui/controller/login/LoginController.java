package au.com.dealsdirect.ui.controller.login;
/*
 * Created by CodeineBot on 6/15/17.
 */

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.regex.Pattern;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;

public class LoginController extends BaseController implements LoginMvpView {

    public static final String TAG = "LoginController";

    @Inject
    LoginMvpPresenter<LoginMvpView> mPresenter;

    @BindView(R.id.email)
    EditText mEmailEditText;
    @BindView(R.id.password)
    EditText mPasswordEditText;
    @BindView(R.id.login_button)
    Button mLoginButton;
    @BindView(R.id.facebook_login_button)
    RelativeLayout mFacebookButton;
    @BindView(R.id.fragment_login_signup_text)
    TextView mSignupTextView;

    @BindView(R.id.partial_toolbar_title_view)
    TextView mLoginToolbarTitle;

    private boolean isLoginTapped = false;

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

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_login, container, false);
        getControllerComponent().inject(this);

        mPresenter.onAttach(this);

        return view;
    }

    @Override
    public void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        setUp(view);
    }

    @Override
    public void onDestroyView(View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    protected void setUp(View view) {

        mLoginToolbarTitle.setText("Login");
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isLoginTapped){
                    callLoginApi();
                }

                isLoginTapped = true;
            }
        });

//        initializeFacebookLogin();

//        mFacebookButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                onFacebookLogin(new FacebookFetchHandler() {
//
//                    @Override
//                    public void onSuccess(String email, String firstName,
//                                          String lastName, String facebookUserID,
//                                          String facebookCookieValue) {
//
//                        mPresenter.loginViaFacebook(email, firstName,
//                                lastName, facebookUserID, facebookCookieValue);
//                    }
//
//                    @Override
//                    public void onFailure(String errorMessage) {
//
//                    }
//                });
//            }
//        });
    }

    @Override
    public void showLoginSuccessful(String loginTicket) {
        if (!loginTicket.isEmpty()) {
//            Auth.didLogin(getBaseActivity(), loginTicket);
//            RxBus.instance().post(GVersion.EVENT_LOGIN);
//            getBaseActivity().finish();
//            authHandler.success();
        }
    }

    @Override
    public void showLoginError(String message) {
//        CustomAlertDialog.showCustomAlertDialog(
//                activity,
//                CustomAlertDialog.CustomDialogIconState.NEGATIVE,
//                message
//        );

        isLoginTapped = false;
    }

    @Override
    public void showRegistration() {
        //push registerfragmentcontroller
//        activity.switchFragment(RegisterFragment.newInstance(activity, authHandler));
    }

    @Override
    public void logoutResult() {

    }

    private void callLoginApi(){
        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        mPresenter.loginViaEmail(email, password);
    }

}
