package au.com.dealsdirect.ui.controller.checkout.paymentsuccess;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentTransaction;
import au.com.dealsdirect.ourpay.Ourpay;
import au.com.dealsdirect.ourpay.OurpayPanel;
import au.com.dealsdirect.ourpay.OurpayState;
import au.com.dealsdirect.ui.base.BaseActivity;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.DialogUtils;
import au.com.dealsdirect.utils.PriceUtils;
import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by smartwave on 30/06/2017.
 */

public class PaymentSuccessController extends BaseController implements PaymentSuccessMvpView {

    private static final String KEY_ADDRESS = "Address";
    private static final String KEY_PRICE = "Price";
    private static final String KEY_INVOICE = "Invoice";
    private static final String KEY_ESTIMATED_DELIVERY = "EstimatedDelivery";


    @Inject
    PaymentSuccessMvpPresenter<PaymentSuccessMvpView> mPresenter;


    @BindView(R.id.fragment_payment_success_address)
    TextView mAddress;
    @BindView(R.id.fragment_payment_success_price)
    TextView mPrice;
    @BindView(R.id.fragment_payment_success_address_orderno)
    TextView mOrderNumber;
    @BindView(R.id.fragment_payment_success_estimated_delivery)
    TextView mEstimatedDelivery;

    MainActivity mActivity;

    //rate us strings
    private String appPlayStoreUri;
    private String packageName;
    private String appUri;

    private Ourpay mOurpay;
    //
    private String mAddressString;
    private String mPriceString;
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
                .putString(KEY_PRICE, PriceUtils.getPriceStringValue(responseValue.getD().getValue().getOrderInfoResult().getTotal()))
                .putString(KEY_INVOICE, responseValue.getD().getValue().getInvoiceNo())
                .putString(KEY_ESTIMATED_DELIVERY, responseValue.getD().getValue().getOrderInfoResult().getEstimatedDeliveryText())
                .build());
    }

    public PaymentSuccessController(Bundle args) {
        super(args);
        mAddressString = args.getString(KEY_ADDRESS, "");
        mPriceString = args.getString(KEY_PRICE, "");
        mInvoiceString = args.getString(KEY_INVOICE, "");
        mEstimatedDeliveryString = args.getString(KEY_ESTIMATED_DELIVERY, "");
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        mActivity = (MainActivity) getActivity();
        mOurpay = ((MainActivity)getActivity()).getOurpay();
        if (mOurpay.isCanUse()){
            mPresenter.generateOurpay();
        }

        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        mAddress.setText(mAddressString);
        mPrice.setText(mPriceString);
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

    @Override
    public boolean handleBack() {
        return getRouter().popToRoot();
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

        DialogUtils.showYesNoDialog(getActivity()
                , getApplicationContext().getString(R.string.rate_us_dialog_title)
                , getApplicationContext().getString(R.string.rate_us_message)
                , getApplicationContext().getString(R.string.rate_us_positive_text)
                , getApplicationContext().getString(R.string.rate_us_negative_text)
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        rateApp();
                        dialog.dismiss();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

    }

    @Override
    public void showOurpay() {
        mOurpay.setState(OurpayState.POSTCART);
        if (mOurpay != null){
            Log.d("postcart", "entered");
            OurpayPanel ourpayPanel = new OurpayPanel((BaseActivity)getActivity());
            mLLOurpay.removeAllViews();
            mLLOurpay.addView(ourpayPanel.generatePanel(mOurpay));
        }else{
            Log.d("postcart", "not entered");

        }
    }


    @OnClick(R.id.partial_continue_shopping_button)
    void onContinueShoppingClick() {
        getRouter().popToTag("CheckoutController");
        ((MainActivity) getActivity()).goToShops();
    }

}
