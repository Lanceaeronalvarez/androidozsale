package au.com.dealsdirect.ui.controller.address.addnewaddress;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
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

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.address.DecorationInfoList;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.JsonUtils;
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
                if (info.Type.equalsIgnoreCase("text") || info.Type.equalsIgnoreCase("numeric")) {
                    dynamicView = inflater.inflate(R.layout.add_new_address_edit_text, container, false);
                    setDynamicViewsProperties(dynamicView, info);
                    deliveryInfoPlaceholder.addView(dynamicView);
                    mViewMap.put(info, dynamicView.findViewById(R.id.row_add_address_value));
                } else if (info.Type.equalsIgnoreCase("select")) {
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
        switch (infoList.Type.toLowerCase()) {
            case "text":
            case "numeric":
                EditText editTextValue = (EditText) dynamicView.findViewById(R.id.row_add_address_value);
                TextView textViewLabel = (TextView) dynamicView.findViewById(R.id.row_add_address_label);

                editTextValue.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
                textViewLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);

                //Set input type
                if (infoList.getDataType() != null && (infoList.getDataType().equalsIgnoreCase("phone") || infoList.getType().equalsIgnoreCase("numeric"))) {
                    editTextValue.setInputType(InputType.TYPE_CLASS_PHONE);
                } else {
                    editTextValue.setInputType(InputType.TYPE_CLASS_TEXT);
                }
                //Set label
                textViewLabel.setText(StringUtils.toTitleCase(infoList.Label));
                editTextValue.setFilters(new InputFilter[]{new InputFilter.LengthFilter(infoList.MaxLength)});

                //Add asterisk to required fields
                if (infoList.getValidate() != null && infoList.getValidate().equalsIgnoreCase("*")) {
                    textViewLabel.setText(textViewLabel.getText() + "*");
                }
                break;
            case "select":
                TextView spinnerLabel = (TextView) dynamicView.findViewById(R.id.row_add_address_label);
                spinnerLabel.setText(StringUtils.toTitleCase(infoList.Label));

                spinnerLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);

                ArrayAdapter<String> signatureOnDeliveryAdapter = new ArrayAdapter<>(mActivity,
                        R.layout.add_new_address_spinner_text, infoList.Options);

                Spinner signatureOnDeliverySpinner = (Spinner) dynamicView.findViewById(R.id.row_add_address_spinner);
                signatureOnDeliverySpinner.setAdapter(signatureOnDeliveryAdapter);

                //Add asterisk to required fields
                if (infoList.getValidate().equalsIgnoreCase("*")) {
                    spinnerLabel.setText(spinnerLabel.getText() + "*");
                }
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
}
