package au.com.dealsdirect.ui.controller.cart;
/*
 * Created by CodeineBot on 6/22/17.
 */

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.utils.BundleBuilder;

public class CartController extends BaseController implements CartMvpView {

    public static final String TAG = "CartController";

    @Inject
    CartMvpPresenter<CartMvpView> mPresenter;

    public static CartController newInstance(String arg) {

        return new CartController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public CartController(Bundle args) {
        super(args);
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_cart, container, false);

        getControllerComponent().inject(this);

        mPresenter.onAttach(this);

        return view;
    }

    @Override
    public void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    public void onDestroyView(View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    protected void setUp(View view) {

    }
}
