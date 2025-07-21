package au.com.dealsdirect.ui.controller.checkout.checkout.steps.contact;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;

public class CheckoutStepsContactController extends BaseController implements CheckoutStepsContactMvpView {

    public static final String TAG = "CheckoutStepsContactController";

    @Inject
    CheckoutStepsContactMvpPresenter<CheckoutStepsContactMvpView> mPresenter;

    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_checkout_steps_contact, container, false);
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
    protected void setUp(View view) {

    }
}
