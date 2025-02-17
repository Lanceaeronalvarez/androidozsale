package au.com.dealsdirect.ui.controller.address.addnewaddress;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.address.DecorationInfoList;
import au.com.dealsdirect.data.network.model.events.GA4EventParams;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.main.Settings;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.JsonUtils;
import au.com.dealsdirect.utils.KeyboardUtils;
import au.com.dealsdirect.utils.StringUtils;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by smartwave on 20/06/2017.
 */

public class AddNewAddressController extends BaseController implements AddNewAddressMvpView {
    private static final String DECORATION_INFO_LIST = "DECORATION_INFO_LIST";
    private static final String CALLED_FROM_CART = "CALLED_FROM_CART";

    private ArrayList<DecorationInfoList> mDecorationInfoList;
    private HashMap<DecorationInfoList, View> mViewMap = new HashMap<>();

    private boolean mIsFromCart = false;
    private GA4EventParams.GA4AddShippingInfoParams ga4AddShippingInfoParams = null;

    @Inject
    AddNewAddressMvpPresenter<AddNewAddressMvpView> mPresenter;

    @BindView(R.id.partial_toolbar_title)
    TextView mAddNewAddressToolarTitle;

    @BindView(R.id.partial_toolbar_right_view)
    ImageView mAddNewAddressRightOption;

    @BindView(R.id.controller_address_save_button)
    Button mSaveAddressButton;


    public AddNewAddressController(String decorationInfoList, boolean calledFromCart) {
        this(new BundleBuilder(new Bundle())
                .putString(DECORATION_INFO_LIST, decorationInfoList)
                .putBoolean(CALLED_FROM_CART, calledFromCart)
                .build());
    }

    public AddNewAddressController(Bundle args) {
        super(args);
        mDecorationInfoList = JsonUtils.convertStringToObject(args.getString(DECORATION_INFO_LIST), new TypeToken<ArrayList<DecorationInfoList>>() {
        }.getType());
        mIsFromCart = args.getBoolean(CALLED_FROM_CART, false);
    }


    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {

        mAddNewAddressToolarTitle.setText(getResources().getString(R.string.add_new_address));
        mAddNewAddressRightOption.setImageDrawable(getApplicationContext().getDrawable(R.drawable.ic_check_white_24dp));
        mSaveAddressButton.setOnClickListener(v -> callAddNewAddress());
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_add_new_address, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);

        if (mDecorationInfoList != null) {
            LinearLayout deliveryInfoPlaceholder = (LinearLayout) view.findViewById(R.id.row_new_address_placeholder);

            View dynamicView = null;
            for (DecorationInfoList info : mDecorationInfoList) {
                if (info.getType().equalsIgnoreCase("text") || info.getType().equalsIgnoreCase("numeric")) {
                    dynamicView = inflater.inflate(R.layout.add_new_address_edit_text, container, false);
                    setDynamicViewsProperties(dynamicView, info);
                    deliveryInfoPlaceholder.addView(dynamicView);
                    mViewMap.put(info, dynamicView.findViewById(R.id.row_add_address_value));
                } else if (info.getType().equalsIgnoreCase("select")) {
                    dynamicView = inflater.inflate(R.layout.add_new_address_spinner, container, false);
                    setDynamicViewsProperties(dynamicView, info);
                    deliveryInfoPlaceholder.addView(dynamicView);
                    mViewMap.put(info, dynamicView.findViewById(R.id.row_add_address_spinner));
                }
            }
        }

        return view;
    }


    @SuppressLint("SetTextI18n")
    private void setDynamicViewsProperties(View dynamicView, DecorationInfoList infoList) {
        float size = dynamicView.getContext().getResources().getDimension(R.dimen.text_size_caption1);
        switch (infoList.getType().toLowerCase()) {
            case "text":
            case "numeric":
                EditText editTextValue = (EditText) dynamicView.findViewById(R.id.row_add_address_value);
                TextView textViewLabel = (TextView) dynamicView.findViewById(R.id.row_add_address_label);
                TextView textViewError = (TextView) dynamicView.findViewById(R.id.row_add_address_error_text);

                editTextValue.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
                textViewLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);

                //Set input type
                String name = infoList.getName() == null ? "" : infoList.getName().toLowerCase();
                switch (name) {
                    case "phone":
                        editTextValue.setInputType(InputType.TYPE_CLASS_PHONE);
                        break;
                    case "postcode":
                        editTextValue.setHint(mActivity.getResources().getString(R.string.postcode_hint_text));
                        editTextValue.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        editTextValue.setInputType(InputType.TYPE_CLASS_TEXT);
                        break;
                    default:
                        editTextValue.setInputType(InputType.TYPE_CLASS_TEXT);
                        break;
                }

                //Set label
                textViewLabel.setText(StringUtils.toTitleCase(infoList.getLabel()));
                editTextValue.setFilters(new InputFilter[]{new InputFilter.LengthFilter(infoList.getMaxLength())});

                //Add asterisk to required fields
                if (infoList.getValidate() != null && infoList.getValidate().equalsIgnoreCase("*")) {
                    textViewLabel.setText(textViewLabel.getText() + "*");
                }

                //validate using regex
                if (infoList.getName().equalsIgnoreCase("forename")) {
                    editTextValue.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            Iterator it = mViewMap.entrySet().iterator();

                            while (it.hasNext()) {
                                try {
                                    Map.Entry pair = (Map.Entry) it.next();
                                    DecorationInfoList info = (DecorationInfoList) pair.getKey();

                                    if (pair.getValue() instanceof EditText) {
                                        EditText et = (EditText) pair.getValue();
                                        String editTextLastNameValue = et.getText().toString();
                                        String label = info.getName().toLowerCase();

                                        if (!editTextValue.getText().toString().trim().matches("[a-zA-Z ]+") || editTextValue.getText().toString().length() < 2) {
                                            textViewError.setText(getString(R.string.special_character_error));
                                            textViewError.setVisibility(View.VISIBLE);
                                        } else {
                                            if (label.equalsIgnoreCase("surname")) {
                                                String str = editTextLastNameValue;
                                                if (str.equalsIgnoreCase(editTextValue.getText().toString().trim())) {
                                                    textViewError.setText(getString(R.string.duplicate_name_error));
                                                    textViewError.setVisibility(View.VISIBLE);
                                                } else {
                                                    textViewError.setVisibility(View.GONE);
                                                }
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                }

                if (infoList.getName().equalsIgnoreCase("surname")) {
                    editTextValue.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            Iterator it = mViewMap.entrySet().iterator();

                            while (it.hasNext()) {
                                try {
                                    Map.Entry pair = (Map.Entry) it.next();
                                    DecorationInfoList info = (DecorationInfoList) pair.getKey();

                                    if (pair.getValue() instanceof EditText) {
                                        EditText et = (EditText) pair.getValue();
                                        String editTextLastNameValue = et.getText().toString();
                                        String label = info.getName().toLowerCase();

                                        if (!editTextValue.getText().toString().trim().matches("[a-zA-Z ]+") || editTextValue.getText().toString().length() < 2) {
                                            textViewError.setText(getString(R.string.special_character_error));
                                            textViewError.setVisibility(View.VISIBLE);
                                        } else {
                                            if (label.equalsIgnoreCase("forename")) {
                                                String str = editTextLastNameValue;
                                                if (str.equalsIgnoreCase(editTextValue.getText().toString().trim())) {
                                                    textViewError.setText(getString(R.string.duplicate_name_error));
                                                    textViewError.setVisibility(View.VISIBLE);
                                                } else {
                                                    textViewError.setVisibility(View.GONE);
                                                }
                                            }
                                        }

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                }

                dynamicView.setOnClickListener(v -> KeyboardUtils.showSoftInput(editTextValue, mActivity));
                break;
            case "select":
                TextView spinnerLabel = dynamicView.findViewById(R.id.row_add_address_label);
                spinnerLabel.setText(StringUtils.toTitleCase(infoList.getLabel()));

                spinnerLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);

                ArrayAdapter<String> signatureOnDeliveryAdapter = new ArrayAdapter<>(mActivity,
                        R.layout.add_new_address_spinner_text, infoList.getOptions());

                Spinner signatureOnDeliverySpinner = dynamicView.findViewById(R.id.row_add_address_spinner);
                signatureOnDeliverySpinner.setAdapter(signatureOnDeliveryAdapter);

                //Add asterisk to required fields
                if (infoList.getValidate().equalsIgnoreCase("*")) {
                    spinnerLabel.setText(spinnerLabel.getText());
                }

                dynamicView.setOnClickListener(v -> {
                    mActivity.hideKeyboard();
                    signatureOnDeliverySpinner.setFocusable(true);
                    signatureOnDeliverySpinner.setFocusableInTouchMode(true);
                    signatureOnDeliverySpinner.requestFocus();
                });
                break;

            default:
                break;
        }
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    public void addNewAddressSuccessful() {

        if (mIsFromCart) {
            logAddShipmentWhileFromCart();
        }

        CustomAlertDialog.showCustomAlertDialog(
                mActivity,
                CustomAlertDialog.CustomDialogIconState.POSITIVE,
                getApplicationContext().getString(R.string.delivery_address_added));
        mActivity.onBackPressed();

    }

    @OnClick(R.id.partial_toolbar_left_view)
    public void onBackClick() {
        hideKeyboard();
        if (mActivity != null) {
            mActivity.onBackPressed();
        }
    }

    @OnClick(R.id.partial_toolbar_right_view)
    public void callAddNewAddress() {
        hideKeyboard();
        mPresenter.addNewAddress(mViewMap);
    }

    @Override
    public void setFieldErrorState(View view) {
        //view.setBackgroundResource(R.drawable.rounded_edittext_error);
    }

    @Override
    public void showErrorMessage(String message) {

        CustomAlertDialog.showCustomAlertDialog(
                mActivity,
                CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                message);

    }

    private void logAddShipmentWhileFromCart() {
        GA4EventParams.GA4AddShippingInfoParams params = new GA4EventParams.GA4AddShippingInfoParams();
        params.setCurrency(Settings.getSelectedCountry().currencyCode);
        ga4AddShippingInfoParams = params;
    }

    public GA4EventParams.GA4AddShippingInfoParams getGa4AddShippingInfoParams() {
        return ga4AddShippingInfoParams;
    }
}
