package au.com.dealsdirect.ui.controller.checkout.addpayment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.braintreepayments.cardform.OnCardFormScanListener;
import com.braintreepayments.cardform.OnCardFormSubmitListener;
import com.braintreepayments.cardform.utils.CardType;
import com.braintreepayments.cardform.view.CardEditText;
import com.braintreepayments.cardform.view.CardForm;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.mysale.genie.utility.RxBus;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.masterpass.MasterpassController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.ui.main.DefaultCallback;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.IntrospectionUtils;
import butterknife.BindView;
import butterknife.OnClick;

/*
 * Created by smartwave on 29/06/2017.
 */

public class AddPaymentController extends BaseController implements AddPaymentMvpView, OnCardFormSubmitListener, CardEditText.OnCardTypeChangedListener, OnCardFormScanListener {
    private final static String IS_FROM_CART = "is_from_cart";
    private final static String CART_TOTAL_COST = "cart_total_cost";
    @Inject
    AddPaymentMvpPresenter<AddPaymentMvpView> mPresenter;


    @BindView(R.id.card_form)
    CardForm mCardForm;
    @BindView(R.id.partial_checkout_button_holder)
    View mButtonHolder;
    @BindView(R.id.partial_checkout_button_pay)
    Button mButtonPay;
    @BindView(R.id.partial_checkout_button_paypal_text)
    TextView mTextPaypal;
    @BindView(R.id.partial_checkout_button_paypal)
    RelativeLayout mButtonPaypal;
    @BindView(R.id.partial_checkout_button_masterpass)
    RelativeLayout mMasterpassButton;


    @BindView(R.id.partial_toolbar_arrow_title)
    TextView mViewAddressToolarTitle;
    @BindView(R.id.partial_toolbar_filter_view)
    ImageView mViewAddressRightOption;


    private MainActivity mActivity;
    private boolean isFromCart = false;
    private boolean isPayPalSubmitClicked = false;
    private String mCartTotalCost;

    public AddPaymentController(boolean isFromCart, String cartCost) {
        this(new BundleBuilder(new Bundle())
                .putString(CART_TOTAL_COST, cartCost)
                .putBoolean(IS_FROM_CART, isFromCart).build());
    }

    public AddPaymentController(Bundle args) {
        super(args);
        isFromCart = args.getBoolean(IS_FROM_CART, false);
        mCartTotalCost = args.getString(CART_TOTAL_COST, "");
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
        mViewAddressRightOption.setVisibility(View.INVISIBLE);

        if ((getActivity()) != null) {
            ((MainActivity) getActivity()).getMainController().getHomeController().setIsResetCheckout(true);
        }

        mCardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .actionLabel("Purchase")
                .setup(getActivity());
        mCardForm.setOnCardFormSubmitListener(this);
        mCardForm.setOnCardTypeChangedListener(this);
        mCardForm.setOnCardFormScanListener(this);
        mCardForm.setCameraIcon(getResources().getDrawable(R.drawable.bg_credit_card));
        mCardForm.setToolbarColor(getResources().getColor(R.color.toolbar_active_skin));
        mCardForm.setCameraBackground(getResources().getDrawable(R.drawable.bg_camera_rounded));

        mButtonPay.setOnClickListener(action -> {
            onCardFormSubmit();
        });

        mButtonPaypal.setOnClickListener(action -> {
            onPaypalSubmit();
        });

        mMasterpassButton.setOnClickListener(action -> onMasterpassButtonClick());

        if (((MainActivity) getActivity()).isBraintreeInitialized()) {

            showPaymentButtons();
        } else {
            ((MainActivity) getActivity()).fetchAuthorization(new DefaultCallback() {
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

    @Override
    public void onDetach(View view) {
        hideLoading();
        super.onDetach(view);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        mPresenter.onAttach(this);
        super.onAttach(view);
    }

    @Override
    protected void onActivityResumed(@NonNull Activity activity) {
        super.onActivityResumed(activity);
        if (isPayPalSubmitClicked) {
            showLoading();
        }
        isPayPalSubmitClicked = false;
    }

    private void hidePaymentButtons() {
        mButtonHolder.setVisibility(View.GONE);
    }

    private void showPaymentButtons() {
        mButtonHolder.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCardFormSubmit() {
        hideKeyboard();

        if (mCardForm.isValid() && mActivity.getBraintreeFragment() != null) {
            showLoading();
            mActivity.onPurchase(mCardForm);

        } else if (mCardForm.isValid() && mActivity.getBraintreeFragment() == null) {
            CustomAlertDialog.showCustomAlertDialog(
                    mActivity, CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                    "Please wait for payments to finish initializing");

        } else {
            mCardForm.validate();
        }
    }

    @Override
    public void onCardTypeChanged(CardType cardType) {

    }

    @Override
    public void onPaypalSubmit() {
        isPayPalSubmitClicked = true;
        showLoading();
        mActivity.startPaypalPayment();
    }


    @Override
    public void showAddPaymentResult(boolean result, String message) {
        hideLoading();
        if (result) {
            CustomAlertDialog.showCustomAlertDialog(
                    mActivity, CustomAlertDialog.CustomDialogIconState.POSITIVE,
                    "Payment method added!");

        } else {
            CustomAlertDialog.showCustomAlertDialog(
                    mActivity, CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                    "Can't add payment method");
        }
        getRouter().handleBack();
    }

    @Override
    public void clearFields() {
        mCardForm.getCardEditText().getText().clear();
        mCardForm.getCvvEditText().getText().clear();
        mCardForm.getExpirationDateEditText().getText().clear();
    }

    @Override
    public void onMasterpassButtonClick() {

        RxBus.instance().post(IntrospectionUtils.EVENT_PAY);

        getRouter().pushController(RouterTransaction.with(MasterpassController.newInstance())
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    public boolean isCalledFromAccounts() {
        return !isFromCart;
    }

    @OnClick(R.id.partial_toolbar_arrow_view)
    void onBackPressed() {
        hideKeyboard();
        ((MainActivity) getActivity()).getMainController().getHomeController().setIsResetCheckout(false);
        getActivity().onBackPressed();
    }

    @OnClick(R.id.bt_camera)
    void launchCamera() {

        Answers.getInstance().logCustom(new CustomEvent("Credit Cart Scanning")
                .putCustomAttribute("Type", "Start"));

        mCardForm.scanCard(getActivity());
    }

    @Override
    public void onCardFormScan() {
        //This callback is called when successful CC scanning

        mCardForm.getCardEditText().setEnabled(false);
        Answers.getInstance().logCustom(new CustomEvent("Credit Cart Scanning")
                .putCustomAttribute("Type", "Success"));
    }
}
