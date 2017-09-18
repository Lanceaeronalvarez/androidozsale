package au.com.dealsdirect.ui.controller.checkout.paymentsuccess;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentTransaction;
import au.com.dealsdirect.service.ourpay.Ourpay;
import au.com.dealsdirect.service.ourpay.OurpayPanel;
import au.com.dealsdirect.service.ourpay.OurpayState;
import au.com.dealsdirect.ui.base.BaseActivity;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.ui.main.PaymentInfo;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.DialogUtils;
import au.com.dealsdirect.utils.PriceUtils;
import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

/*
 * Created by smartwave on 30/06/2017.
 */

public class PaymentSuccessController extends BaseController implements PaymentSuccessMvpView {

    private static final String KEY_ADDRESS = "Address";
    private static final String KEY_PRICE = "Price";
    private static final String KEY_SHIPPING_FEE = "Shipping";
    private static final String KEY_INVOICE = "Invoice";
    private static final String KEY_ESTIMATED_DELIVERY = "EstimatedDelivery";
    private static final int KEY_PLANNED_TRANSACTION_STATE_PAID = 2;

    @Inject
    PaymentSuccessMvpPresenter<PaymentSuccessMvpView> mPresenter;


    @BindView(R.id.fragment_payment_success_address)
    TextView mAddress;
    @BindView(R.id.fragment_payment_success_price)
    TextView mPriceTextView;
    @BindView(R.id.fragment_payment_success_address_orderno)
    TextView mOrderNumber;
    @BindView(R.id.fragment_payment_success_estimated_delivery)
    TextView mEstimatedDelivery;
    @BindView(R.id.payment_ourpay_success_detail_container)
    LinearLayout mPaymentOurpaySuccessDetailContainer;
    @BindView(R.id.payment_success_table_container)
    LinearLayout mPaymentSuccessTableContainer;
    @BindView(R.id.payment_success_order_number)
    TextView mPaymentSuccessOrderNumber;

    MainActivity mActivity;

    //rate us strings
    private String appPlayStoreUri;
    private String packageName;
    private String appUri;

    private String mAddressString;
    private double mPrice;
    private double mShippingFee;
    private String mTotalPriceString;
    private String mInvoiceString;
    private String mEstimatedDeliveryString;

    @BindView(R.id.ourpay_panel_holder)
    LinearLayout mLLOurpay;


    public static PaymentSuccessController newInstance(String address, String price, String invoice, String delivery) {

        return new PaymentSuccessController(
                new BundleBuilder(new Bundle())
                        .putString(KEY_ADDRESS, address)
                        .putString(KEY_PRICE, price)
                        .putString(KEY_INVOICE, invoice)
                        .putString(KEY_ESTIMATED_DELIVERY, delivery)
                        .build());
    }

    public PaymentSuccessController(CreatePaymentTransaction.ResponseValue responseValue) {
        this(new BundleBuilder(new Bundle())
                .putString(KEY_ADDRESS, responseValue.getD().getValue().getAddressString())
                .putDouble(KEY_PRICE,  responseValue.getD().getValue().getOrderInfoResult().getTotal())
                .putDouble(KEY_SHIPPING_FEE, responseValue.getD().getValue().getOrderInfoResult().getShipping())
                .putString(KEY_INVOICE, responseValue.getD().getValue().getInvoiceNo())
                .putString(KEY_ESTIMATED_DELIVERY, responseValue.getD().getValue().getOrderInfoResult().getEstimatedDeliveryText())
                .build());
    }

    public PaymentSuccessController(Bundle args) {
        super(args);
        mAddressString = args.getString(KEY_ADDRESS, "");
        mPrice = args.getDouble(KEY_PRICE);
        mShippingFee = args.getDouble(KEY_SHIPPING_FEE);
        mInvoiceString = args.getString(KEY_INVOICE, "");
        mEstimatedDeliveryString = args.getString(KEY_ESTIMATED_DELIVERY, "");
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        mActivity = (MainActivity) getActivity();
        if (PaymentInfo.getOurpay() != null && PaymentInfo.getOurpay().isCanUse()) {
            mPaymentOurpaySuccessDetailContainer.setVisibility(View.VISIBLE);
            mPaymentSuccessOrderNumber.setText(mInvoiceString);
            getTotalPayment(PaymentInfo.getOurpay());
            mPresenter.generateOurpay();
        }else{
            double totalPayment = mShippingFee+mPrice;
            mPaymentSuccessTableContainer.setVisibility(View.VISIBLE);
            mPriceTextView.setText(PriceUtils.getPriceStringValue(totalPayment));
        }

        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        mAddress.setText(mAddressString);
        mOrderNumber.setText(mInvoiceString);
        mEstimatedDelivery.setText(Html.fromHtml(mEstimatedDeliveryString).toString());

        packageName = getActivity().getPackageName();

        //Remove test postfix
        packageName = packageName.replace(".test", "");
        appUri = "market://details?id=" + packageName;
        appPlayStoreUri = "http://play.google.com/store/apps/details?id=" + packageName;

//        MyVouchersFragment.clearVouchers();

        mPresenter.incrementPayCount();
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_payment_success, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    public void onDetach(View view) {
        mPresenter.onDetach();
        super.onDetach(view);
    }

    public void rateApp() {

        try {
            Uri uri = Uri.parse(appUri);
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);

            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                mActivity.startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                mActivity.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(appPlayStoreUri)));
            }

        } catch (Exception exception) {
            Timber.d("ratepopup", "error call rateApp()");
        }
    }

    @Override
    public void showRatePopUp() {
        DialogUtils.showYesNoDialog(getActivity(),
                getApplicationContext().getString(R.string.rate_us_dialog_title),
                getApplicationContext().getString(R.string.rate_us_message),
                getApplicationContext().getString(R.string.rate_us_positive_text),
                getApplicationContext().getString(R.string.rate_us_negative_text),
                (dialog, which) -> {
                    rateApp();
                    dialog.dismiss();
                },
                (dialog, which) -> dialog.dismiss());
    }

    @Override
    public void showOurpay() {
        PaymentInfo.getOurpay().setState(OurpayState.POSTCART);
        if (PaymentInfo.getOurpay() != null) {
            OurpayPanel ourpayPanel = new OurpayPanel((BaseActivity) getActivity());
            mLLOurpay.removeAllViews();
            mLLOurpay.addView(ourpayPanel.generatePanel(PaymentInfo.getOurpay()));
        }
    }


    @OnClick(R.id.partial_continue_shopping_button)
    void onContinueShoppingClick() {
        PaymentInfo.resetPaymentInfo();

        getRouter().popToTag("CheckoutController");
        ((MainActivity) getActivity()).setShopsAsVisibleContainer();
    }

    public void getTotalPayment(Ourpay ourpay){
        double totalPayment = 0;
        for (int i = 0; i < ourpay.getPlannedTransactions().size(); i++){
            if(ourpay.getPlannedTransactions().get(i).getState()==KEY_PLANNED_TRANSACTION_STATE_PAID){
                totalPayment =+ ourpay.getPlannedTransactions().get(i).getAmount();
            }
        }

        mPriceTextView.setText(Double.toString(totalPayment));
    }
}
