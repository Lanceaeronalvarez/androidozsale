package au.com.dealsdirect.ui.controller.saleitemdetails;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.saleitemdetails.GetSaleItemDetailsResponse;
import au.com.dealsdirect.service.ourpay.Ourpay;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsMvpView;
import au.com.dealsdirect.utils.module.GateKeeper;

/**
 * Created by smartwave on 30/10/2017.
 */

public class SaleItemDetailsController extends BaseController implements SaleItemDetailsMvpView {
    @Inject
    SaleItemDetailsMvpPresenter<SaleItemDetailsMvpView> mPresenter;

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_sale_item_details, container, false);
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
    @Override
    public void showSaleDetails(GetSaleItemDetailsResponse saleDetail) {

    }

    @Override
    public void showAddToCartResponse(boolean val) {

    }

    @Override
    public void showMyPayDetails(GetSaleItemDetailsResponse value, Ourpay ourpay) {

    }
}
