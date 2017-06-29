package au.com.dealsdirect.ui.controller.checkout.addpayment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.braintreepayments.cardform.OnCardFormSubmitListener;
import com.braintreepayments.cardform.utils.CardType;
import com.braintreepayments.cardform.view.CardEditText;
import com.braintreepayments.cardform.view.CardForm;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.address.DecorationInfoList;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.main.FetchTokenHandler;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.ui.main.MainMvpView;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.DialogUtils;
import au.com.dealsdirect.utils.JsonUtils;
import butterknife.BindView;

/**
 * Created by smartwave on 29/06/2017.
 */

public class AddPaymentController extends BaseController implements AddPaymentMvpView,OnCardFormSubmitListener, CardEditText.OnCardTypeChangedListener {
    private final static String  IS_FROM_CART = "IsFromCart";

    @Inject
    AddPaymentMvpPresenter<AddPaymentMvpView> mPresenter;


    @BindView(R.id.card_form)
    CardForm mCardForm;
    @BindView(R.id.partial_checkout_bt_loading)
    View mBraintreeLoading;
    @BindView(R.id.partial_checkout_button_holder)
    View mButtonHolder;
    @BindView(R.id.partial_checkout_button_pay)
    Button mButtonPay;
    @BindView(R.id.partial_checkout_button_paypal_text)
    TextView mTextPaypal;
    @BindView(R.id.partial_checkout_button_paypal)
    RelativeLayout mButtonPaypal;


    @BindView(R.id.partial_toolbar_arrow_title)
    TextView mViewAddressToolarTitle;
    @BindView(R.id.partial_toolbar_filter_view)
    ImageView mViewAddressRightOption;


    MainActivity mActivity;
    boolean isFromCart = false;

    public AddPaymentController(boolean isFromCart) {
        this(new BundleBuilder(new Bundle())
                .putBoolean(IS_FROM_CART,isFromCart).build());
    }

    public AddPaymentController(Bundle args) {
        super(args);
        isFromCart = args.getBoolean(IS_FROM_CART,false);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_add_payment, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        mActivity = ((MainActivity) getActivity());
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        mViewAddressToolarTitle.setText("Add New Payment");
        mViewAddressRightOption.setImageDrawable(null);

        mCardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .actionLabel("Purchase")
                .setup(getActivity());
        mCardForm.setOnCardFormSubmitListener(this);
        mCardForm.setOnCardTypeChangedListener(this);

        mButtonPay.setOnClickListener(action -> {
            onCardFormSubmit();
        });

        mButtonPaypal.setOnClickListener(action -> {
            onPaypalSubmit();
        });

        if(!isFromCart) {
            mButtonPay.setText("add");
            mTextPaypal.setText("add");
        }

        if (((MainActivity)getActivity()).isBraintreeInitialized()) {
            showPaymentButtons();
        } else {
            ((MainActivity)getActivity()).fetchAuthorization(new FetchTokenHandler() {
                @Override
                public void onSuccess() {
                    showPaymentButtons();
                }

                @Override
                public void onFailure() {
                    hidePaymentButtons();
                }
            });
        }

    }

    private void hidePaymentButtons() {
        mBraintreeLoading.setVisibility(View.VISIBLE);
        mButtonHolder.setVisibility(View.GONE);
    }

    private void showPaymentButtons() {
        mBraintreeLoading.setVisibility(View.GONE);
        mButtonHolder.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCardFormSubmit() {
        hideKeyboard();

        if (mCardForm.isValid() && mActivity.getBraintreeFragment() != null) {
//            mActivity.showProgressDialog("Verifying payment method");
            showLoading();
            mActivity.onPurchase(mCardForm);
        } else if (mCardForm.isValid() && mActivity.getBraintreeFragment() == null) {
//            CustomAlertDialog.showCustomAlertDialog(
//                    activity, CustomAlertDialog.CustomDialogIconState.NEGATIVE,
//                    "Please wait for payments to finish initializing");
            DialogUtils.showYesDialog(mActivity, "Notification"
                    , "Please wait for payments to finish initializing"
                    , "ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        } else {
            mCardForm.validate();
        }
    }

    @Override
    public void onCardTypeChanged(CardType cardType) {

    }

    @Override
    public void onPaypalSubmit() {
        mActivity.startPaypalPayment();
    }

    @Override
    public void showAddPaymentResult(boolean result, String message) {
        if (result) {
            DialogUtils.showYesDialog(mActivity, "Success", "Payment method added!", "ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            getRouter().popController(AddPaymentController.this);
                        }
                    });
            clearFields();
        } else {
            DialogUtils.showYesDialog(mActivity, "Failed", message, "ok", null);
        }
    }

    @Override
    public void clearFields() {
        mCardForm.getCardEditText().getText().clear();
        mCardForm.getCvvEditText().getText().clear();
        mCardForm.getExpirationDateEditText().getText().clear();
    }

    public boolean isCalledFromAccounts() {
        return !isFromCart;
    }
}
