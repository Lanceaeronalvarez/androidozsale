package au.com.dealsdirect.ui.controller.shops;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import au.com.dealsdirect.data.network.model.banner.GetBannerResponse;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.ui.base.BaseController;

/**
 * Created by smartwave on 02/11/2017.
 */

public class ShopsController extends BaseController implements ShopsMvpView {
    @Override
    public void showShopBanners(List<GetBannerResponse> getBannerResponses) {

    }

    @Override
    public void storeCategories(List<GetCategoryTreeResponse> categories) {

    }

    @Override
    public void refresh() {

    }

    @Override
    public void unBindPaginate() {

    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return null;
    }

    @Override
    protected void setUp(View view) {

    }
}
