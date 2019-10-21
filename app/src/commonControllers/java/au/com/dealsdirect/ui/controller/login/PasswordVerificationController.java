package au.com.dealsdirect.ui.controller.login;

import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.login.LoginVisa;
import au.com.dealsdirect.ui.base.VisaCheckoutMvpPresenter;
import au.com.dealsdirect.ui.base.VisaCheckoutMvpView;
import au.com.dealsdirect.ui.controller.main.Settings;
import au.com.dealsdirect.ui.controller.visacheckout.VisaCheckoutController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Created by smartwave on 12/02/2018.
 */

public class PasswordVerificationController extends VisaCheckoutController implements PasswordVerificationMvpView {

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

    @BindView(R.id.partial_toolbar_right_view)
    ImageButton mFilterButton;
    @BindView(R.id.partial_toolbar_title)
    TextView mTitleTextView;
    @BindView(R.id.controller_password_verification_edittext)
    EditText mPassword;
    @BindView(R.id.controller_password_verification_submit_button)
    Button mSubmitButton;
    @BindView(R.id.controller_account_email_guide)
    TextView mAccountEmailGuide;

    Disposable mSubmitButtonClickListener;

    private boolean mAccountExists;
    private String mAccountEmail;
    private LoginVisa.RequestValue.Data mRequestData;

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_password_verification, container, false);

        getControllerComponent().inject(this);
        mVcoPresenter.onAttach(this);
        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        mTitleTextView.setText("Password Verification");
        mFilterButton.setVisibility(View.INVISIBLE);

        if(!mAccountExists) {
            mAccountEmailGuide.setText(String.format(getResources().getString(R.string.new_vco_user_guide), Settings.getSelectedCountry().siteName) + " " + mAccountEmail);
        } else {
            mAccountEmailGuide.setText(getResources().getString(R.string.password_verification_guide) + " " + mAccountEmail);
        }
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

    @OnClick(R.id.partial_toolbar_left_view)
    void backPressed(){
        mActivity.onBackPressed();
    }
}

