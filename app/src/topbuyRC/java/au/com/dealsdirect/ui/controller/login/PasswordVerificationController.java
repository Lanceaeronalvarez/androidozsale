package au.com.dealsdirect.ui.controller.login;

import android.os.Bundle;
import androidx.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jakewharton.rxbinding2.view.RxView;
import com.visa.checkout.Profile;
import com.visa.checkout.PurchaseInfo;
import com.visa.checkout.VisaPaymentSummary;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.forgotpassword.ForgotPasswordResponseBody;
import au.com.dealsdirect.data.network.model.login.LoginVisa;
import au.com.dealsdirect.ui.base.SwipeableBaseToolBarController;
import au.com.dealsdirect.ui.base.SwipeableVisaCheckoutController;
import au.com.dealsdirect.ui.base.VisaCheckoutMvpPresenter;
import au.com.dealsdirect.ui.base.VisaCheckoutMvpView;
import au.com.dealsdirect.ui.controller.forgotpassword.ForgotPasswordMvpPresenter;
import au.com.dealsdirect.ui.controller.forgotpassword.ForgotPasswordMvpView;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Created by smartwave on 12/02/2018.
 */

public class PasswordVerificationController extends SwipeableVisaCheckoutController implements ForgotPasswordMvpView{

    public static PasswordVerificationController newInstance() {
        return new PasswordVerificationController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public PasswordVerificationController(Bundle args) {
        super(args);
        mAccountEmail = args.getString(BundleKeys.KEY_ACCOUNT_EMAIL, "");
        mAccountExists = args.getBoolean(BundleKeys.KEY_ACCOUNT_EXISTS, false);
        mRequestData = new Gson().fromJson(args.getString(BundleKeys.KEY_LOGIN_VISA_REQUEST_DATA), LoginVisa.RequestValue.Data.class);
    }

    @Inject
    VisaCheckoutMvpPresenter<VisaCheckoutMvpView> mVcoPresenter;
    @Inject
    ForgotPasswordMvpPresenter<ForgotPasswordMvpView> mPresenter;


    @BindView(R.id.controller_password_verification_edittext)
    EditText mPassword;
    @BindView(R.id.controller_password_verification_submit_button)
    Button mSubmitButton;
    @BindView(R.id.controller_account_email_guide)
    TextView mAccountEmailGuide;
    @BindView(R.id.controller_footer_textview)
    TextView mFooterTextView;

    Disposable mSubmitButtonClickListener;

    private boolean mAccountExists;
    private String mAccountEmail;
    private LoginVisa.RequestValue.Data mRequestData;

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = super.inflateView(inflater, container);

        fillContent(inflater.inflate(R.layout.controller_password_verification, container, false));
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        mVcoPresenter.onAttach(this);
        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
        setupSwipingBehavior();
    }

    @Override
    protected void setUp(View view) {
        mToolbarTitle.setText("password verification");

        if(!mAccountExists) {
            mAccountEmailGuide.setText(getResources().getString(R.string.new_vco_user_guide) + " " + mAccountEmail);
        } else {
            mAccountEmailGuide.setText(getResources().getString(R.string.password_verification_guide) + " " + mAccountEmail);
        }
        
        Spannable text = new SpannableString("If you don't remember your password we can ");

        mFooterTextView.setText(text);

        Spannable text2 = new SpannableString("send you a link");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                mPresenter.forgotPassword(mAccountEmail);
            }
        };
        text2.setSpan(clickableSpan, 0, text2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        mFooterTextView.append(text2);

        Spannable text3 = new SpannableString(" to recover your password");
        mFooterTextView.append(text3);
        mFooterTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        mSubmitButtonClickListener = RxView.clicks(mSubmitButton)
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(v -> {
                    if (mPassword.getText().toString().isEmpty()) {
                        CustomAlertDialog.showCustomAlertDialog(mActivity, CustomAlertDialog.CustomDialogIconState.NEGATIVE, "Please enter password for verification");
                        return;
                    }

                    hideKeyboard();
                    performPasswordVerification();

                });
    }

    @Override
    public void onDetach(View view) {
        super.onDetach(view);
        mSubmitButtonClickListener.dispose();
    }

    private void performPasswordVerification() {
        AppLogger.d("VC_performPasswordVerification", "verifying password");
        mVcoPresenter.executeLoginVisa(mRequestData, mPassword.getText().toString());
    }

    @Override
    public void showLoginVisaSuccess(String loginTicket) {
        mActivity.loginSuccessHandler(getRouter(), AppConstants.POP_FLAG.ROOT, AppConstants.AUTH_FLAG.LOGIN);
    }

    @Override
    public void showForgotPasswordResponse(ForgotPasswordResponseBody forgotPasswordResponseBody) {
        if (forgotPasswordResponseBody.getForgotPasswordResponse().getResult()) {
            hideKeyboard();
            mActivity.onBackPressed();

            CustomAlertDialog.showCustomAlertDialog(
                    mActivity,
                    CustomAlertDialog.CustomDialogIconState.POSITIVE,
                    mActivity.getString(R.string.request_sent)
            );

        } else {

            CustomAlertDialog.showCustomAlertDialog(
                    mActivity,
                    CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                    forgotPasswordResponseBody.getForgotPasswordResponse().getMessage()
            );

        }
    }

    @Override
    public void showForgotPasswordError() {
        CustomAlertDialog.showCustomAlertDialog(
                mActivity,
                CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                "please try again");
    }
}
