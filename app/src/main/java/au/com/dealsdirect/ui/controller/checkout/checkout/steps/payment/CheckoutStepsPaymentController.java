package au.com.dealsdirect.ui.controller.checkout.checkout.steps.payment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;

public class CheckoutStepsPaymentController extends BaseController implements CheckoutStepsPaymentMvpView {

    public static final String TAG = "CheckoutStepsPaymentController";

    @Inject
    CheckoutStepsPaymentMvpPresenter<CheckoutStepsPaymentMvpView> mPresenter;

    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_checkout_steps_payment, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);

        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        super.onDestroyView(view);
        mPresenter.onDetach();
    }

    @Override
    protected void setUp(View view) {

    }
}
