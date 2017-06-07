package au.com.dealsdirect.ui.controller.shop;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;

import javax.inject.Inject;


/**
 * dp Created by Admin on 6/6/17.
 */

public class ShopController extends BaseController implements ShopMvpView {

    private static final String KEY_TEXT = "ShopController.KEY_TEXT";

    @Inject
    ShopMvpPresenter<ShopMvpView> mPresenter;


    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {

        View view = inflater.inflate(R.layout.controller_shop, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);

        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);

    }


    @Override
    protected void onDestroyView(@NonNull View view) {
        super.onDestroyView(view);
    }

    @Override protected void setUp(View view) {

    }
}
