package au.com.dealsdirect.ui.controller.address.addnewaddress;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.address.DecorationInfoList;
import au.com.dealsdirect.ui.base.SwipeableBaseToolBarController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.JsonUtils;
import au.com.dealsdirect.utils.StringUtils;

import static au.com.dealsdirect.utils.BundleKeys.DECORATION_INFO_LIST;
import static au.com.dealsdirect.utils.BundleKeys.IS_FROM_CART;

/**
 * Created by smartwave on 20/06/2017.
 */

public class AddNewAddressController extends SwipeableBaseToolBarController implements AddNewAddressMvpView {

    private ArrayList<DecorationInfoList> mDecorationInfoList;
    private HashMap<DecorationInfoList, View> mViewMap = new HashMap<>();

    @Inject
    AddNewAddressMvpPresenter<AddNewAddressMvpView> mPresenter;
    public boolean mCalledFromCart = false;

    public AddNewAddressController(String decorationInfoList, boolean calledFromCart){
        this(new BundleBuilder(new Bundle())
                .putString(DECORATION_INFO_LIST, decorationInfoList)
                .putBoolean(IS_FROM_CART, calledFromCart)
                .build());
    }

    public AddNewAddressController(Bundle args) {
        super(args);
        mDecorationInfoList = JsonUtils.convertStringToObject(args.getString(DECORATION_INFO_LIST), new TypeToken<ArrayList<DecorationInfoList>>(){}.getType());
        mCalledFromCart = args.getBoolean(IS_FROM_CART,getArgs().getBoolean(IS_FROM_CART));
    }


    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        mToolbarTitle.setText(R.string.new_address);
        setupSwipingBehavior();
        setupDefaultBottomButton(mActivity.getString(R.string.use_this_address),
                view1 -> mPresenter.addNewAddress(mViewMap));
        setUp(view);
    }

    @Override
    protected void setUp(View view) {

    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {

        View view = super.inflateView(inflater, container);

        fillContent(inflater.inflate(R.layout.controller_add_new_address, container, false));
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);

        if (mDecorationInfoList != null) {
            LinearLayout deliveryInfoPlaceholder = (LinearLayout) view.findViewById(R.id.deliveryInfoPlaceholder);

            View dynamicView = null;
            for (DecorationInfoList info : mDecorationInfoList) {
                if (info.Type.equalsIgnoreCase("text") || info.Type.equalsIgnoreCase("numeric")) {
                    dynamicView = inflater.inflate(R.layout.add_new_address_edit_text, container, false);
                    setDynamicViewsProperties(dynamicView, info);
                    deliveryInfoPlaceholder.addView(dynamicView);
                    mViewMap.put(info, dynamicView.findViewById(R.id.add_address_value));
                } else if (info.Type.equalsIgnoreCase("select")) {
                    dynamicView = inflater.inflate(R.layout.add_new_address_spinner, container, false);
                    setDynamicViewsProperties(dynamicView, info);
                    deliveryInfoPlaceholder.addView(dynamicView);
                    mViewMap.put(info, dynamicView.findViewById(R.id.add_address_spinner));
                }
            }
        }

        return view;
    }


    @SuppressLint("SetTextI18n")
    private void setDynamicViewsProperties(View dynamicView, DecorationInfoList infoList) {
        switch (infoList.Type.toLowerCase()) {
            case "text":
            case "numeric":
                EditText editTextValue = (EditText) dynamicView.findViewById(R.id.add_address_value);
                TextView textViewLabel = (TextView) dynamicView.findViewById(R.id.add_address_label);

                //Set input type
                if (infoList.getDataType() !=null && (infoList.getDataType().equalsIgnoreCase("phone") || infoList.getType().equalsIgnoreCase("numeric"))) {
                    editTextValue.setInputType(InputType.TYPE_CLASS_PHONE);
                } else {
                    editTextValue.setInputType(InputType.TYPE_CLASS_TEXT);
                }
                //Set label
                textViewLabel.setText(StringUtils.toTitleCase(infoList.Label));
                editTextValue.setFilters(new InputFilter[]{new InputFilter.LengthFilter(infoList.MaxLength)});

                //Add asterisk to required fields
                if (infoList.getValidate().equalsIgnoreCase("*")) {
                    textViewLabel.setText(textViewLabel.getText() + "*");
                }

                break;
            case "select":
                TextView textViewLabel2 = (TextView) dynamicView.findViewById(R.id.add_address_label);
                textViewLabel2.setText(StringUtils.toTitleCase(infoList.Label));

                ArrayAdapter<String> signatureOnDeliveryAdapter = new ArrayAdapter<>(mActivity,
                        R.layout.add_new_address_spinner_text, infoList.Options);

                Spinner signatureOnDeliverySpinner = (Spinner) dynamicView.findViewById(R.id.add_address_spinner);
                signatureOnDeliverySpinner.setAdapter(signatureOnDeliveryAdapter);

                //Add asterisk to required fields
                if (infoList.getValidate().equalsIgnoreCase("*")) {
                    textViewLabel2.setText(textViewLabel2.getText() + "*");
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

    @Override
    public void setFieldErrorState(View view) {
        view.setBackgroundResource(R.drawable.rounded_edittext_error);
    }

    @Override
    public void showErrorMessage(String message) {

        CustomAlertDialog.showCustomAlertDialog(
                mActivity,
                CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                message);

    }
}
