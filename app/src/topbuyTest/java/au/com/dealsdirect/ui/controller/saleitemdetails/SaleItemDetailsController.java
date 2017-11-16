package au.com.dealsdirect.ui.controller.saleitemdetails;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import au.com.dealsdirect.data.network.model.saleitemdetails.GetSaleItemDetailsResponse;
import au.com.dealsdirect.service.ourpay.Ourpay;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsMvpView;

/**
 * Created by smartwave on 30/10/2017.
 */

public class SaleItemDetailsController extends BaseController implements SaleItemDetailsMvpView {
    @Override
    protected void setUp(View view) {

    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return null;
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
