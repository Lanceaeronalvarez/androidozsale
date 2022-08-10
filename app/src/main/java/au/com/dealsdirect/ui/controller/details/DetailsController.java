package au.com.dealsdirect.ui.controller.details;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.gdpr.savereceivesales.SaveReceiveSalesResponse;
import au.com.dealsdirect.data.network.model.userdetails.GetUserDetailsResponse;
import au.com.dealsdirect.data.network.model.userdetails.SetUserDetailsRequest;
import au.com.dealsdirect.data.pref.AppPreferencesHelper;
import au.com.dealsdirect.ui.base.BasePullToRefreshController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.ui.custom.toggleswitch.BaseToggleSwitch;
import au.com.dealsdirect.ui.custom.toggleswitch.CustomToggleSwitch;
import au.com.dealsdirect.ui.custom.transitions.CustomSpinnerAdapter;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.KeyboardUtils;
import butterknife.BindView;
import butterknife.OnClick;

public class DetailsController extends BasePullToRefreshController implements DetailsMvpView {

    @Inject
    DetailsMvpPresenter<DetailsMvpView> mPresenter;

    @BindView(R.id.partial_toolbar_left_view)
    View mToolbarLeftView;

    @BindView(R.id.controller_details_consent_switch_layout)
    ViewGroup mConsentSwitchesRootLayout;

    @BindView(R.id.partial_toolbar_title)
    TextView mTitleTextView;

    @BindView(R.id.partial_toolbar_right_view)
    ImageView mSaveUserDetailsButton;

    @BindView(R.id.controller_details_text_firstname)
    EditText mFirstNameText;

    @BindView(R.id.controller_details_text_lastname)
    EditText mLastNameText;

    @BindView(R.id.controller_details_text_birthday)
    EditText mDateOfBirthText;

    @BindView(R.id.controller_details_spinner_gender)
    Spinner mGenderSpinner;

    @BindView(R.id.controller_details_text_emailaddress)
    EditText mEmailAddressText;

    @BindView(R.id.controller_details_text_password)
    EditText mPasswordText;

    @BindView(R.id.controller_details_text_new_password)
    EditText mNewPasswordText;

    @BindView(R.id.controller_details_text_confirm_password)
    EditText mConfirmPasswordText;

    @Nullable
    @BindView(R.id.register_emails_toggle)
    CustomToggleSwitch mEmailsToggle;

    @Nullable
    @BindView(R.id.register_emails_text)
    TextView mPromotionEmailsText;

    private Date dateOfBirth = null;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private BaseToggleSwitch.OnToggleSwitchChangeListener mOnToggleSwitchListener;

    private final SimpleDateFormat serverDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private final SimpleDateFormat uiDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public DetailsController(Bundle args) {
        super(args);
    }

    public static DetailsController newInstance() {

        return new DetailsController(new BundleBuilder(new Bundle()).build());
    }


    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = super.inflateView(inflater, container);

        setToolBarVisible(getResource().getBoolean(R.bool.details_toolbar_visibility));
        fillContent(inflater.inflate(R.layout.controller_user_details, container, false));

        getControllerComponent().inject(this);

        mPresenter.onAttach(this);

        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {

        mConsentSwitchesRootLayout.setVisibility(mPresenter.isGdprDisabled() ? View.GONE : View.VISIBLE);

        if (mPromotionEmailsText != null) {
            mPromotionEmailsText.setText(Html.fromHtml(mPresenter.getGdprTemplateTexts(
                    AppPreferencesHelper.CONSENT_WITH_REGISTRATION_EMAILS_TEXT)));
        }

        if (mEmailsToggle != null && !mPresenter.isGdprDisabled()) {
            mOnToggleSwitchListener = new BaseToggleSwitch.OnToggleSwitchChangeListener() {
                @Override
                public void onToggleSwitchChangeListener(int position, boolean isChecked) {
                    mPresenter.saveReceiveSales(position == 0);
                }
            };
        }

        mSaveUserDetailsButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));
        mSaveUserDetailsButton.setVisibility(getBoolean(R.bool.is_ozsale_app) ? View.INVISIBLE : View.VISIBLE);
        mTitleTextView.setText(getString(R.string.account_details));
        mToolbarLeftView.setVisibility(mPresenter.isTablet() && getBoolean(R.bool.master_detail_enabled) ? View.INVISIBLE : View.VISIBLE);

        if (getBoolean(R.bool.is_gender_enabled)) {
            List<String> list = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.genders)));
            CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(mActivity,
                    R.layout.row_custom_spinner_drop_down,
                    list);
            mGenderSpinner.setAdapter(customSpinnerAdapter);
        }

        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                final Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                dateOfBirth = calendar.getTime();
                updateDateOfBirthField();
            }
        };

        mDateOfBirthText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calender = Calendar.getInstance();
                if (dateOfBirth != null) {
                    calender.setTime(dateOfBirth);
                }
                new DatePickerDialog(mActivity, R.style.DatePickerTheme, onDateSetListener,
                        calender.get(Calendar.YEAR), calender.get(Calendar.MONTH),
                        calender.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        SetUserDetailsRequest setUserDetailsRequest = new SetUserDetailsRequest();
        showLoading();
        mPresenter.loadUser(setUserDetailsRequest);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    public void loadDetails(GetUserDetailsResponse userDetailsResponse) {
//        LinkedTreeMap details = (LinkedTreeMap) userDetailsResponse.getResponse().getScheduledPlan();
        GetUserDetailsResponse value = userDetailsResponse;
        mFirstNameText.setText(value.getForename());
        mLastNameText.setText(value.getSurname());
        mEmailAddressText.setText(value.getEmail());

        if (mEmailsToggle != null && !mPresenter.isGdprDisabled()) {
//            POSITION 0 == YES
            mEmailsToggle.removeOnToggleSwitchListener();
            mEmailsToggle.setCheckedTogglePosition(value.getReceiveInvitations() ? 0 : 1);
            mEmailsToggle.setOnToggleSwitchChangeListener(mOnToggleSwitchListener);
        }


        try {
            dateOfBirth = serverDateFormat.parse(value.getDateOfBirth());
        } catch (Exception e) {
            dateOfBirth = null;
        }
        updateDateOfBirthField();

        int genderItem = 0;
        if (!value.getGender()) {
            genderItem = 1;
        }

        if (getBoolean(R.bool.is_gender_enabled)) {
            mGenderSpinner.setSelection(genderItem);
        }
    }

    @Override
    public void saveUserDetailsSuccess() {
        CustomAlertDialog.showCustomAlertDialog(mActivity,
                CustomAlertDialog.CustomDialogIconState.POSITIVE,
                "User details is saved");
    }

    @Override
    public void saveUserDetailsFailed(String message) {
        CustomAlertDialog.showCustomAlertDialog(mActivity,
                CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                message);
    }

    @Override
    public void onSaveReceiveSales(SaveReceiveSalesResponse saveReceiveSalesResponse) {
        //TODO for future implementation
    }

    @OnClick(R.id.partial_toolbar_right_view)
    public void saveUserDetails() {
        hideKeyboard();

        if (mPresenter.isTablet() && !mActivity.isAuthorized()
                || getBoolean(R.bool.master_detail_enabled) && !mActivity.isAuthorized()) {
            CustomAlertDialog.showCustomAlertDialog(mActivity,
                    CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                    getString(R.string.controller_user_details_login_prompt));
            return;
        }

        if (mPasswordText.getText().toString().isEmpty() || mPasswordText.getText().toString() == "") {
            CustomAlertDialog.showCustomAlertDialog(mActivity,
                    CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                    getString(R.string.controller_user_details_enter_password));
            return;
        }

        String firstname = mFirstNameText.getText().toString();
        String lastname = mLastNameText.getText().toString();
        boolean gender = getBoolean(R.bool.is_gender_enabled) && mGenderSpinner.getSelectedItem().toString().equals("Male");
        String dateofbirth = dateOfBirth == null ? null : serverDateFormat.format(dateOfBirth);
        String email = mEmailAddressText.getText().toString();
        String password = mPasswordText.getText().toString();
        String newpassword = mNewPasswordText.getText().toString();
        String confirmpassword = mConfirmPasswordText.getText().toString();

        if (newpassword.equals(confirmpassword)) {
            mPresenter.sendUserDetails(createUserDetailRequest(email, firstname, lastname, dateofbirth,
                    gender, email, password, newpassword, confirmpassword));
        } else {
            CustomAlertDialog.showCustomAlertDialog(mActivity, CustomAlertDialog.CustomDialogIconState.POSITIVE, mActivity.getString(R.string.password_does_not_match));
        }
    }

    @OnClick(R.id.partial_toolbar_left_view)
    public void onBackClick() {
        hideKeyboard();
        mActivity.onBackPressed();
    }

    @OnClick(R.id.controller_details_button)
    public void saveChanges() {
        saveUserDetails();
    }

    @Override
    public void onRefreshStart() {
        super.onRefreshStart();

        SetUserDetailsRequest setUserDetailsRequest = new SetUserDetailsRequest();
        mPresenter.loadUser(setUserDetailsRequest);
    }

    private void updateDateOfBirthField() {
        mDateOfBirthText.setText(dateOfBirth == null ? null : uiDateFormat.format(dateOfBirth));
    }

    public SetUserDetailsRequest createUserDetailRequest(String userName, String firstName, String lastName,
                                                         String dateOfBirth, boolean gender, String email,
                                                         String password, String newPassword, String confirmPassword) {
        SetUserDetailsRequest userDetailsRequest = new SetUserDetailsRequest();
        userDetailsRequest.setUserName(userName);
        userDetailsRequest.setFirstname(firstName);
        userDetailsRequest.setSurname(lastName);
        userDetailsRequest.setDateBirth(dateOfBirth);
        userDetailsRequest.setGender(gender);
        userDetailsRequest.setEmail(email);
        userDetailsRequest.setPassword(password);
        userDetailsRequest.setNewPassword(newPassword);
        userDetailsRequest.setConfirmPassword(confirmPassword);

        return userDetailsRequest;
    }

    @OnClick(R.id.controller_details_text_firstname_container)
    public void onFirstNameContainerClick() {
        KeyboardUtils.showSoftInput(mFirstNameText, mActivity);
    }

    @OnClick(R.id.controller_details_text_lastname_container)
    public void onLastNameContainerClick() {
        KeyboardUtils.showSoftInput(mLastNameText, mActivity);
    }

    @OnClick(R.id.controller_details_text_birthday_container)
    public void onBirthdayContainerClick() {
        KeyboardUtils.showSoftInput(mDateOfBirthText, mActivity);
    }

    @OnClick(R.id.controller_details_text_emailaddress_container)
    public void onEmailContainerClick() {
        KeyboardUtils.showSoftInput(mEmailAddressText, mActivity);
    }

    @OnClick(R.id.controller_details_text_password_container)
    public void onPasswordContainerClick() {
        KeyboardUtils.showSoftInput(mPasswordText, mActivity);
    }

    @OnClick(R.id.controller_details_text_new_password_container)
    public void onNewPasswordContainerClick() {
        KeyboardUtils.showSoftInput(mNewPasswordText, mActivity);
    }

    @OnClick(R.id.controller_details_text_confirm_password_cotainer)
    public void onConfirmPasswordContainerClick() {
        KeyboardUtils.showSoftInput(mConfirmPasswordText, mActivity);
    }
}
