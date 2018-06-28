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
import au.com.dealsdirect.ui.main.PaymentInfo;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.DialogUtils;
import au.com.dealsdirect.utils.PriceUtils;
import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

/*
 * Created by smartwave on 30/06/2017.
 */

public class PaymentSuccessController extends BaseController implements PaymentSuccessMvpView {

    private static final int KEY_PLANNED_TRANSACTION_STATE_PAID = 2;

    @Inject
    PaymentSuccessMvpPresenter<PaymentSuccessMvpView> mPresenter;

    @BindView(R.id.payment_success_estimated_delivery_text_view)
    TextView mEstimatedDeliveryTextView;
    @BindView(R.id.payment_success_order_number)
    TextView mOrderNumberTextView;

    //rate us strings
    private String mAppPlayStoreUri;
    private String mPackageName;
    private String mAppUri;

    private String mAddressString;
    private double mPrice;
    private double mShippingFee;
    private String mInvoiceString;
    private String mEstimatedDeliveryString;

    @BindView(R.id.ourpay_panel_holder)
    ViewGroup mOurpayDetailsContainer;


    public static PaymentSuccessController newInstance(String address, String price, String invoice, String delivery) {

        return new PaymentSuccessController(
                new BundleBuilder(new Bundle())
                        .putString(BundleKeys.KEY_ADDRESS, address)
                        .putString(BundleKeys.KEY_PRICE, price)
                        .putString(BundleKeys.KEY_INVOICE, invoice)
                        .putString(BundleKeys.KEY_ESTIMATED_DELIVERY, delivery)
                        .build());
    }

    public PaymentSuccessController(CreatePaymentTransaction.ResponseValue responseValue) {

        this(new BundleBuilder(new Bundle())
                .putString(BundleKeys.KEY_ADDRESS, responseValue.getD().getValue().getAddressString())
                .putDouble(BundleKeys.KEY_PRICE, responseValue.getD().getValue().getOrderInfoResult().getTotal())
                .putDouble(BundleKeys.KEY_SHIPPING_FEE, responseValue.getD().getValue().getOrderInfoResult().getShipping())
                .putString(BundleKeys.KEY_INVOICE, responseValue.getD().getValue().getInvoiceNo() == null ? String.valueOf(responseValue.getD().getValue().getTransactionInvoiceNo()) : responseValue.getD().getValue().getInvoiceNo())
                .putString(BundleKeys.KEY_ESTIMATED_DELIVERY, responseValue.getD().getValue().getOrderInfoResult().getEstimatedDeliveryText())
                .build());
    }

    public PaymentSuccessController(Bundle args) {
        super(args);
        mAddressString = args.getString(BundleKeys.KEY_ADDRESS, "");
        mPrice = args.getDouble(BundleKeys.KEY_PRICE);
        mShippingFee = args.getDouble(BundleKeys.KEY_SHIPPING_FEE);
        mInvoiceString = args.getString(BundleKeys.KEY_INVOICE, "");
        mEstimatedDeliveryString = args.getString(BundleKeys.KEY_ESTIMATED_DELIVERY, "");
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        if (PaymentInfo.getOurpay() != null && PaymentInfo.getOurpay().isCanUse()) {
            mPresenter.generateOurpay();
        }

        mEstimatedDeliveryTextView.setVisibility(View.GONE);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        mOrderNumberTextView.setText(mInvoiceString);
        mEstimatedDeliveryTextView.setText(Html.fromHtml(mEstimatedDeliveryString).toString());

        mPackageName = getActivity().getPackageName();

        //Remove test postfix
        mPackageName = mPackageName.replace(".test", "");
        mAppUri = getString(R.string.app_uri_header) + mPackageName;
        mAppPlayStoreUri = getString(R.string.app_playstore_uri_header) + mPackageName;

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

    private void rateApp() {

        try {
            Uri uri = Uri.parse(mAppUri);
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);

            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                mActivity.startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                mActivity.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(mAppPlayStoreUri)));
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
            mOurpayDetailsContainer.removeAllViews();
            mOurpayDetailsContainer.addView(ourpayPanel.generatePanel(PaymentInfo.getOurpay()));
        }
    }

    @Override
    public boolean handleBack() {
        onContinueShoppingClick();
        return true;
    }

    @OnClick(R.id.partial_continue_shopping_button)
    void onContinueShoppingClick() {
        PaymentInfo.resetPaymentInfo();

        mActivity.getCheckoutRouter().popToRoot();
        mActivity.setShopsAsVisibleContainer();
    }

//    Unused function, price table container is hidden.
//    *May be used in the future
    private void getTotalPayment(Ourpay ourpay) {
        double totalPayment = 0;
        for (int i = 0; i < ourpay.getPlannedTransactions().size(); i++) {
            if (ourpay.getPlannedTransactions().get(i).getState() == KEY_PLANNED_TRANSACTION_STATE_PAID) {
                totalPayment = ourpay.getPlannedTransactions().get(i).getAmount();
            }
        }

//        mPriceTextView.setText(Double.toString(totalPayment));
    }
}
