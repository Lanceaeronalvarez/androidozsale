package au.com.dealsdirect.ui.controller.checkout.paymentsuccess;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentTransaction;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.main.Settings;
import au.com.dealsdirect.ui.main.CardInfo;
import au.com.dealsdirect.ui.main.PaymentInfo;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.DialogUtils;
import au.com.dealsdirect.utils.IntrospectionUtils;
import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

public class PaymentSuccessController extends BaseController implements PaymentSuccessMvpView {

    @Inject
    PaymentSuccessMvpPresenter<PaymentSuccessMvpView> mPresenter;

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

    @BindView(R.id.thank_you_for_shopping_textview)
    TextView mThankyouTextview;


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

        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        mOrderNumberTextView.setText(mInvoiceString);

        mPackageName = getActivity().getResources().getString(R.string.app_package_name);

        mAppUri = getString(R.string.app_uri_header) + mPackageName;
        mAppPlayStoreUri = getString(R.string.app_playstore_uri_header) + mPackageName;

        String thankYouMessage = getResource().getString(R.string.thank_you_for_shopping) + " " + Settings.getSelectedCountry().siteName;
        mThankyouTextview.setText(thankYouMessage);

        mPresenter.incrementPayCount();

        CardInfo.clearCardInfo();
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

        if (!mPresenter.getHasUserRateApp()) {
            DialogUtils.showYesNoDialog(getActivity(),
                    getApplicationContext().getString(R.string.rate_us_dialog_title),
                    getApplicationContext().getString(R.string.rate_us_message),
                    getApplicationContext().getString(R.string.rate_us_positive_text),
                    getApplicationContext().getString(R.string.rate_us_negative_text),
                    (dialog, which) -> {
                        mPresenter.setHasUserRateApp(true);
                        IntrospectionUtils.verifyVersion(getApplicationContext());
                        rateApp();
                        dialog.dismiss();
                    },
                    (dialog, which) -> dialog.dismiss());
        }
    }

    @Override
    public boolean handleBack() {
        PaymentInfo.resetPaymentInfo();

        mActivity.getMainController().showShopController();
        mActivity.getMainController().resetCheckoutRouter();
        return true;
    }

    @OnClick(R.id.partial_continue_shopping_button)
    void onContinueShoppingClick() {
        mActivity.onBackPressed();
    }

}
