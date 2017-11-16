package au.com.dealsdirect.ui.controller.checkout.ourpay;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.braintreepayments.api.ThreeDSecure;

import java.util.regex.Pattern;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.ourpayverificationcodeconfirm.VerificationCodeConfirmResponseBody;
import au.com.dealsdirect.data.network.model.ourpayverificationnormalizephone.VerificationNormalizePhoneResponseBody;
import au.com.dealsdirect.service.ourpay.Ourpay;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.ui.main.PaymentInfo;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * dp Created by Admin on 8/9/17.
 */

public class OurpaySMSVerificationController extends BaseController implements OurpaySMSVerificationMvpView {

    private static final String PHONE_KEY = "OurpaySMSVerificationController.PHONE_KEY_TEXT";
    private static final String PAYMENT_TYPE_MYPAY = "mypay";

    private String mPhoneFromCart;
    private Ourpay mOurpay;

    private boolean mIsPhoneValid = false;
    private boolean mIsCodeValid = false;

    @BindView(R.id.partial_toolbar_arrow_title)
    TextView mOurpaySMSVerificationTitle;

    @BindView(R.id.partial_toolbar_arrow_view)
    ImageButton mOurpaySMSVerificationLeftOption;

    @BindView(R.id.partial_toolbar_filter_view)
    ImageButton mOurpaySMSVerificationRightOption;

    @BindView(R.id.ourpay_edittext_extension)
    EditText mSMSVerificationPhoneExtension;

    @BindView(R.id.ourpay_edittext_phone)
    EditText mSMSVerificationPhone;

    @BindView(R.id.ourpay_edittext_code)
    EditText mSMSVerificationCode;

    @BindView(R.id.ourpay_text_resend_code)
    TextView mSMSVerificationResendCode;

    @BindView(R.id.ourpay_text_phone_error)
    TextView mSMSVerificationPhoneError;

    @BindView(R.id.ourpay_text_code_error)
    TextView mSMSVerificationCodeError;

    @BindView(R.id.ourpay_button_confirm)
    Button mSMSVerificationConfirmButton;

    @BindView(R.id.ourpay_progress_extension)
    ProgressBar mSMSVerificationProgressBar;

    private boolean isToVerifyCode = false;

    @Inject
    OurpaySMSVerificationMvpPresenter<OurpaySMSVerificationMvpView> mPresenter;

    public static OurpaySMSVerificationController newInstance(String phone) {

        return new OurpaySMSVerificationController(new BundleBuilder(new Bundle())
                .putString(PHONE_KEY, phone)
                .build());
    }

    public OurpaySMSVerificationController(Bundle args) {
        super(args);
        mPhoneFromCart = getArgs().getString(PHONE_KEY);
    }


    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {

        View view = inflater.inflate(R.layout.controller_ourpay_sms_verification, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);

        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        mOurpay = PaymentInfo.getOurpay();
        mPresenter.callNormalizePhone(mPhoneFromCart);
        mOurpaySMSVerificationRightOption.setVisibility(View.INVISIBLE);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        mOurpaySMSVerificationTitle.setText(R.string.sms_verification_title);

        mSMSVerificationPhone.setText(mPhoneFromCart);
        mSMSVerificationPhone.requestFocus();
        setPhoneValidations();
        setCodeValidations();
    }

    @Override
    public void onError(String message) {
        super.onError(message);
    }

    @OnClick(R.id.partial_toolbar_arrow_view)
    void onBackClick() {
        mActivity.onBackPressed();
    }

    @Override
    public void loadExtension() {
        mSMSVerificationProgressBar.setVisibility(View.VISIBLE);
        mSMSVerificationPhoneExtension.setVisibility(View.INVISIBLE);

    }

    @Override
    public void callNormalizePhoneResponse(VerificationNormalizePhoneResponseBody response) {

        if (!response.getVerificationNormalizePhoneResponse().getValue().getErrorMessage().isEmpty()) {

            setPhoneError(response.getVerificationNormalizePhoneResponse().getValue().getErrorMessage());

        } else {
            setExtension(response.getVerificationNormalizePhoneResponse().getValue().getCountryCode());
            mPresenter.callVerificationCodeSend(
                    mSMSVerificationCode.getText().toString(),
                    mSMSVerificationPhone.getText().toString(),
                    response.getVerificationNormalizePhoneResponse().getValue().getCountryCode());
            clearCode();
        }
    }

    @Override
    public void callVerificationCodeSendResponse(VerificationNormalizePhoneResponseBody response) {

        if(!response.getVerificationNormalizePhoneResponse().getResult()||!response.getVerificationNormalizePhoneResponse().getIsAuthenticated()) {
            CustomAlertDialog.showCustomAlertDialog(mActivity, CustomAlertDialog.CustomDialogIconState.NEGATIVE, response.getVerificationNormalizePhoneResponse().getValue().getErrorMessage());
        }else{
//            Log.d("smsverification", response.getVerificationNormalizePhoneResponse().getValue().getErrorMessage()+" , "+response.getVerificationNormalizePhoneResponse().getMessage());
            if (!response.getVerificationNormalizePhoneResponse().getValue().getErrorMessage().isEmpty()) {
                setPhoneError(response.getVerificationNormalizePhoneResponse().getValue().getErrorMessage());
            } else {
                setPhoneNormal();
            }
        }
    }

    @Override
    public void callVerificationCodeConfirmResponse(VerificationCodeConfirmResponseBody response) {
        hideLoading();
        boolean mIsAuthenticated = response.getVerificationCodeConfirmResponse().getIsAuthenticated();
        boolean mResult = response.getVerificationCodeConfirmResponse().getResult();
        boolean mAuthRequired;

        if (response.getVerificationCodeConfirmResponse().getIsAuthenticated() != null) {
            mIsAuthenticated = response.getVerificationCodeConfirmResponse().getIsAuthenticated();
            mAuthRequired = true;
        } else {
            mAuthRequired = false;
        }

        String mMessage = response.getVerificationCodeConfirmResponse().getMessage();

        if (!mResult || (mAuthRequired && !mIsAuthenticated)) {
//            CustomAlertDialog.showCustomAlertDialog(getActivity(), CustomAlertDialog.CustomDialogIconState.NEGATIVE, mMessage);
            Log.d(OurpaySMSVerificationController.class.getName(),mMessage);
            ourpayPaymentSubmit();
            CustomAlertDialog.showCustomAlertDialog(mActivity, CustomAlertDialog.CustomDialogIconState.POSITIVE, "Verified");
            mSMSVerificationConfirmButton.setEnabled(true);

        } else {

            String error = response.getVerificationCodeConfirmResponse().getValue().getErrorMessage();

            if (!error.isEmpty()) {
                setCodeError(error);
                mSMSVerificationConfirmButton.setEnabled(true);
            } else {
                setCodeNormal();
                CustomAlertDialog.showCustomAlertDialog(mActivity, CustomAlertDialog.CustomDialogIconState.POSITIVE, "Verified");
                ourpayPaymentSubmit();
            }
        }
    }

    @OnClick(R.id.ourpay_text_resend_code)
    void onResendCode() {
        isToVerifyCode = false;
        mPresenter.callNormalizePhone(mSMSVerificationPhone.getText().toString());
    }

    private void setCodeNormal() {
        mIsCodeValid = true;
        mSMSVerificationCodeError.setVisibility(View.GONE);
        mSMSVerificationCode.setBackgroundResource(R.drawable.ourpay_edittext_selector_edit);
    }

    private void clearCode() {
        setCodeNormal();
        mSMSVerificationCode.getText().clear();
    }

    private void setPhoneValidations() {
        String phoneRegex;
        try {
            phoneRegex = mOurpay.getOurpayPhoneVerification().getFields().phoneNumberFormat.regexp;
        } catch (Exception e) {
            phoneRegex = "";
        }

        int max;
        try {
            max = mOurpay.getOurpayPhoneVerification().getFields().phoneNumberFormat.maxLength;

            InputFilter[] filters = new InputFilter[1];
            filters[0] = new InputFilter.LengthFilter(max);
            mSMSVerificationPhone.setFilters(filters);

        } catch (Exception e) {
            e.printStackTrace();
        }

        final String finalPhoneRegex = phoneRegex;
        mSMSVerificationPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.length() == 0) {
                    setPhoneError("Please enter phone number");
                } else {

                    boolean b = Pattern.matches(finalPhoneRegex, editable);
                    if (!b) {
                        setPhoneError("Invalid phone format");
                    } else {
                        setPhoneNormal();
                    }
                }
            }
        });

        mSMSVerificationPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (mIsPhoneValid) {
                    mSMSVerificationPhone.setBackgroundResource(R.drawable.ourpay_edittext_selector);
                }
            }
        });
    }

    private void setPhoneError(String error) {
        mIsPhoneValid = false;

        if (!error.isEmpty()) {
            mSMSVerificationPhoneError.setText(error);
            mSMSVerificationPhoneError.setVisibility(View.VISIBLE);
        }
        mSMSVerificationPhone.setBackgroundResource(R.drawable.ourpay_edittext_selector_error);
    }

    private void setPhoneNormal() {

        mIsPhoneValid = true;

        mSMSVerificationPhoneError.setVisibility(View.GONE);
        mSMSVerificationPhone.setBackgroundResource(R.drawable.ourpay_edittext_selector_edit);
    }

    private void setCodeError(String error) {

        mIsCodeValid = false;

        mSMSVerificationCodeError.setText(error);
        mSMSVerificationCodeError.setVisibility(View.VISIBLE);

        mSMSVerificationCode.setBackgroundResource(R.drawable.ourpay_edittext_selector_error);
    }

    @OnClick(R.id.ourpay_button_confirm)
    void onConfirmButtonClick() {
        hideKeyboard();
        showLoading();
        mSMSVerificationConfirmButton.setEnabled(false);
//        mPresenter.callNormalizePhone(mSMSVerificationPhone.getText().toString());
        mPresenter.callVerificationCodeConfirm(mSMSVerificationPhone.getText().toString(), mSMSVerificationPhoneExtension.getText().toString(), mSMSVerificationCode.getText().toString());
        isToVerifyCode = true;
    }

    private void setCodeValidations() {
        String codeRegex;
        try {
            codeRegex = mOurpay.getOurpayPhoneVerification().getFields().verificationCodeFormat.regexp;
        } catch (Exception e) {
            codeRegex = "";
        }

        int max;
        try {
            max = mOurpay.getOurpayPhoneVerification().getFields().verificationCodeFormat.maxLength;

            InputFilter[] filters = new InputFilter[1];
            filters[0] = new InputFilter.LengthFilter(max);
            mSMSVerificationCode.setFilters(filters);

            String hint = "";
            for (int i = 0; i < max; i++) {
                hint = hint + "X";
            }
            mSMSVerificationCode.setHint(hint);

        } catch (Exception e) {
            e.printStackTrace();
        }

        final String finalCodeRegex = codeRegex;
        mSMSVerificationCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                boolean b = Pattern.matches(finalCodeRegex, editable);
                if (!b) {
                    setCodeError("Invalid code format");
                } else {
                    setCodeNormal();
                }
            }
        });

        mSMSVerificationCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (mIsCodeValid) {
                    mSMSVerificationCode.setBackgroundResource(R.drawable.ourpay_edittext_selector);
                }
            }
        });
    }

    private void setExtension(String extension) {
        mSMSVerificationProgressBar.setVisibility(View.INVISIBLE);
        mSMSVerificationPhoneExtension.setVisibility(View.VISIBLE);
        mSMSVerificationPhoneExtension.setText("+" + extension);
    }

    private void ourpayPaymentSubmit() {

        PaymentInfo.setPaymentType(PaymentInfo.TYPE_MYPAY);
        mActivity.callCreatePaymentTransaction(PaymentInfo.getPaymentType(), "", PaymentInfo.getPaymentMethod().getToken());
    }
}
