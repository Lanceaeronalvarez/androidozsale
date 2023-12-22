package au.com.dealsdirect.ui.controller.klarna;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentCategory;
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentView;
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentViewCallback;
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentsSDKError;

import org.json.JSONObject;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.checkout.klarna.KlarnaCreateSessionResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.checkout.paymentsuccess.PaymentSuccessController;
import au.com.dealsdirect.ui.controller.login.PopUpHostController;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;

public class KlarnaViewController extends BaseController implements KlarnaMvpView {

    @Inject
    KlarnaMvpPresenter<KlarnaMvpView> mPresenter;

    @BindView(R.id.controller_klarna_payment_view)
    KlarnaPaymentView klarnaPaymentView;

    private String sessionToken = null;
    private JSONObject klarnaSessionModel = null;

    private Button continueButton = null;

    private final KlarnaPaymentViewCallback klarnaPaymentCallback = new KlarnaPaymentViewCallback() {
        @Override
        public void onInitialized(@NonNull KlarnaPaymentView klarnaPaymentView) {
            Log.d(KlarnaViewController.class.getSimpleName(), "KlarnaPaymentViewCallback.onInitialized");
            klarnaPaymentView.load(null);
            continueButton.setOnClickListener(null);
            continueButton.setEnabled(false);
        }

        @Override
        public void onLoaded(@NonNull KlarnaPaymentView klarnaPaymentView) {
            Log.d(KlarnaViewController.class.getSimpleName(), "KlarnaPaymentViewCallback.onLoaded");
            continueButton.setEnabled(true);
            continueButton.setOnClickListener(v -> {
                klarnaPaymentView.authorize(false, klarnaSessionModel.toString());
                continueButton.setOnClickListener(null);
                continueButton.setEnabled(false);
            });
        }

        @Override
        public void onLoadPaymentReview(@NonNull KlarnaPaymentView klarnaPaymentView, boolean b) {
            Log.d(KlarnaViewController.class.getSimpleName(), "KlarnaPaymentViewCallback.onLoadPaymentReview");
        }

        @Override
        public void onAuthorized(@NonNull KlarnaPaymentView klarnaPaymentView, boolean approved, @Nullable String authToken, @Nullable Boolean finalizedRequired) {
            Log.d(KlarnaViewController.class.getSimpleName(), "KlarnaPaymentViewCallback.onAuthorized");
            if (approved) {

            } else {

            }

            if (finalizedRequired != null && finalizedRequired) {
                klarnaPaymentView.finalize(null);
            }

            if (authToken != null && !mPresenter.isBusy()) {
                mPresenter.createKlarnaOrder(authToken);
            }
        }

        @Override
        public void onReauthorized(@NonNull KlarnaPaymentView klarnaPaymentView, boolean approved, @Nullable String authToken) {
            Log.d(KlarnaViewController.class.getSimpleName(), "KlarnaPaymentViewCallback.onReauthorized");
            if (approved) {

            } else {

            }

            if (authToken != null && !mPresenter.isBusy()) {
                mPresenter.createKlarnaOrder(authToken);
            }
        }

        @Override
        public void onFinalized(@NonNull KlarnaPaymentView klarnaPaymentView, boolean approved, @Nullable String authToken) {
            Log.d(KlarnaViewController.class.getSimpleName(), "KlarnaPaymentViewCallback.onFinalized");
            if (authToken != null && !mPresenter.isBusy()) {
                mPresenter.createKlarnaOrder(authToken);
            }
        }

        @Override
        public void onErrorOccurred(@NonNull KlarnaPaymentView klarnaPaymentView, @NonNull KlarnaPaymentsSDKError klarnaPaymentsSDKError) {
            Log.d(KlarnaViewController.class.getSimpleName(), "KlarnaPaymentViewCallback.onErrorOccurred");
            showError(klarnaPaymentsSDKError.getMessage());
        }
    };

    public KlarnaViewController(Bundle args) {
        super(args);
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        mPresenter.onAttach(this);
        mActivity.getMainController().hideBottomNav();
        klarnaPaymentView.registerPaymentViewCallback(klarnaPaymentCallback);

        super.onAttach(view);
    }

    @Override
    public void onDetach(View view) {
        mActivity.getMainController().showBottomNav();
        klarnaPaymentView.unregisterPaymentViewCallback(klarnaPaymentCallback);

        mPresenter.onDetach();
        super.onDetach(view);
    }

    @Override
    protected void setUp(View view) {
        Log.d(KlarnaViewController.class.getSimpleName(), "setup");
        hideKlarnaPaymentView();
        hideProgressIndicator();
        klarnaPaymentView.setCategory(KlarnaPaymentCategory.PAY_LATER);
        Log.d(KlarnaViewController.class.getSimpleName(), "createKlarnaSession");

        View closeButton = view.findViewById(R.id.controller_klarna_payment_close_button);
        continueButton = view.findViewById(R.id.controller_klarna_payment_continue_button);

        closeButton.setOnClickListener(v -> dismissSelf());
        continueButton.setOnClickListener(null);
        continueButton.setEnabled(false);

        mPresenter.createKlarnaSession();
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_klarna, container, false);

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void showKlarnaPaymentView(KlarnaCreateSessionResponse response) {
        Log.d(KlarnaViewController.class.getSimpleName(), "showKlarnaPaymentView");
        klarnaPaymentView.setVisibility(View.VISIBLE);
        final KlarnaCreateSessionResponse.Value value = response.getD().getValue();
        final String sessionToken = value.getToken();

        if (sessionToken == null || sessionToken.isEmpty()) {
            showError();
            return;
        }

        this.sessionToken = sessionToken;
        klarnaSessionModel = value.getKlarnaSessionModel();
        final String scheme = mActivity.getResources().getString(R.string.app_uri_scheme);
        final String hostname = mActivity.getResources().getString(R.string.app_uri_klarna);
        final String url;
        if (!hostname.contains(scheme)) {
            url = scheme + "://" + hostname;
        } else {
            url = hostname;
        }
        new Handler(Looper.getMainLooper()).post(() -> {
            Log.d(KlarnaViewController.class.getSimpleName(), "KlarnaPaymentView.initialize");
            klarnaPaymentView.initialize(sessionToken, url);
        });
    }

    @Override
    public void hideKlarnaPaymentView() {
        klarnaPaymentView.setVisibility(View.GONE);
    }

    @Override
    public void showProgressIndicator() {
        showAfterpayLoading();
    }

    @Override
    public void hideProgressIndicator() {
        hideAfterpayLoading();
    }

    @Override
    public void showPaymentSuccess(String address, Double price, Double shipping, String invoice, String delivery) {
        Log.d(KlarnaViewController.class.getSimpleName(), "KlarnaPaymentView.showPaymentSuccess");
        BundleBuilder bundleBuilder = new BundleBuilder(new Bundle())
                .putString(BundleKeys.KEY_ADDRESS, address)
                .putDouble(BundleKeys.KEY_PRICE, price)
                .putDouble(BundleKeys.KEY_SHIPPING_FEE, shipping)
                .putString(BundleKeys.KEY_INVOICE, invoice)
                .putString(BundleKeys.KEY_ESTIMATED_DELIVERY, delivery);

        if (mPresenter.isTablet()) {
            Router router = mActivity.getMainController().getPopUpHostRouter();
            Bundle bundle = bundleBuilder
                    .putSerializable(BundleKeys.KEY_POP_UP_HOST_DESTINATION, GateKeeper.Destination.PAYMENT_SUCCESS)
                    .build();

            RouterTransaction routerTransaction = RouterTransaction
                    .with(new PopUpHostController(bundle))
                    .pushChangeHandler(new FadeChangeHandler())
                    .popChangeHandler(new FadeChangeHandler());

            if (getRouter() == router) {
                router.replaceTopController(routerTransaction);
            } else {
                dismissSelf();
                router.pushController(routerTransaction);
            }
        } else {
            dismissSelf();
            mActivity.getCurrentRouter()
                    .pushController(RouterTransaction
                            .with(new PaymentSuccessController(bundleBuilder.build()))
                            .pushChangeHandler(new HorizontalChangeHandler())
                            .popChangeHandler(new HorizontalChangeHandler()));
        }
    }

    @Override
    public void showError(String message) {
        hideKlarnaPaymentView();
        hideProgressIndicator();

        if (message == null || message.isEmpty()) {
            showAlertDialog(mActivity.getResources()
                    .getString(R.string.klarna_failed_transaction));
        } else {
            showAlertDialog(message);
        }
    }

    private void showError() {
        showError(null);
    }

    private void showAlertDialog(String message) {
        new AlertDialog.Builder(mActivity)
                .setTitle(mActivity.getResources().getString(R.string.klarna))
                .setMessage(message)
                .setPositiveButton("OK", null)
                .setOnDismissListener(dialog -> dismissSelf())
                .show();
    }

    private void dismissSelf() {
        mActivity.onBackPressed();
    }

    public boolean isBusy() {
        return mPresenter.isBusy();
    }
}
