package au.com.dealsdirect.ui.controller.details;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
import au.com.dealsdirect.utils.DateUtils;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Paul on 6/20/17.
 */

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

    private Calendar mCalendar;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private BaseToggleSwitch.OnToggleSwitchChangeListener mOnToggleSwitchListener;
//    private ElasticHorizontalDragDismissFrameLayout.ElasticHorizontalDragDismissCallback mDragDismissCallback
//            = new ElasticHorizontalDragDismissFrameLayout.ElasticHorizontalDragDismissCallback() {
//        @Override
//        public void onDragDismissed() {
//            super.onDragDismissed();
//            getRouter().popController(DetailsController.this);
//        }
//    };


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

        if (mEmailsToggle != null && mPresenter.getGdprIsChecked(AppPreferencesHelper.CONSENT_EMAILS_CHECKED)) {
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
        mToolbarLeftView.setVisibility(mPresenter.isTablet() ? View.INVISIBLE : View.VISIBLE);

        if (getBoolean(R.bool.is_gender_enabled)) {
            List<String> list = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.genders)));
            CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(mActivity,
                    R.layout.row_custom_spinner_drop_down,
                    list);
            mGenderSpinner.setAdapter(customSpinnerAdapter);
        }

        mCalendar = Calendar.getInstance();

        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, monthOfYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                mDateOfBirthText.setText(DateUtils.getDateStringFromCalendar(mCalendar));
            }
        };

        mDateOfBirthText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(mActivity, R.style.DatePickerTheme, onDateSetListener,
                        mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        SetUserDetailsRequest setUserDetailsRequest = new SetUserDetailsRequest();
        showLoading();
        mPresenter.loadUser(setUserDetailsRequest);

        mActivity.getMainController().setViewpagerDraggable(false);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    public void loadDetails(GetUserDetailsResponse userDetailsResponse) {
//        LinkedTreeMap details = (LinkedTreeMap) userDetailsResponse.getResponse().getScheduledPlan();
        GetUserDetailsResponse.Value value = userDetailsResponse.getResponse().getValue();
        mFirstNameText.setText(value.getForename());
        mLastNameText.setText(value.getSurname());
        mEmailAddressText.setText(value.getEmail());

        if (mEmailsToggle != null && mPresenter.getGdprIsChecked(AppPreferencesHelper.CONSENT_EMAILS_CHECKED)) {
//            POSITION 0 == YES
            mEmailsToggle.removeOnToggleSwitchListener();
            mEmailsToggle.setCheckedTogglePosition(value.getReceiveInvitations() ? 0 : 1);
            mEmailsToggle.setOnToggleSwitchChangeListener(mOnToggleSwitchListener);
        }


        if (value.getDateOfBirth() != null) {
            String day = value.getDateOfBirth().getDay().toString();
            String year = value.getDateOfBirth().getYear().toString();
            String month = DateUtils.months[value.getDateOfBirth().getMonth() - 1];
            mDateOfBirthText.setText(month + " " + day + ", " + year);
        }

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

        if (mPasswordText.getText().toString().isEmpty() || mPasswordText.getText().toString() == "") {
            CustomAlertDialog.showCustomAlertDialog(mActivity,
                    CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                    "Please enter your password");
            return;
        }

        String firstname = mFirstNameText.getText().toString();
        String lastname = mLastNameText.getText().toString();
        boolean gender = getBoolean(R.bool.is_gender_enabled) && mGenderSpinner.getSelectedItem().toString().equals("Male");
        String dateofbirth = mDateOfBirthText.getText().toString();
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
}
