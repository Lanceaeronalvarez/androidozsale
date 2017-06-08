package au.com.dealsdirect.ui.controller.productdetails;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.productdetails.GetPublicItemDetailsResponse;
import au.com.dealsdirect.ui.base.BaseController;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by smartwave on 08/06/2017.
 */

public class ProductDetailsController extends BaseController implements ProductDetailsMvpView {

    @Inject
    ProductDetailsMvpPresenter<ProductDetailsMvpView> mPresenter;

    private Unbinder mUnBinder;

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_product_details, container, false);
        getControllerComponent().inject(this);
        mUnBinder = ButterKnife.bind(this,view);
        return null;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
    }

    @Override
    protected void setUp(View view) {

    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        super.onDestroyView(view);
        mUnBinder.unbind();
    }

    @Override
    public void showProductDetails(GetPublicItemDetailsResponse.Value productDetail) {
        //bind UI values here
    }
}
