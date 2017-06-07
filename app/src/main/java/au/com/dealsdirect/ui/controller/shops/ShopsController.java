package au.com.dealsdirect.ui.controller.shops;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.banner.BannerRequest;
import au.com.dealsdirect.data.network.model.banner.BannerResponse;
import au.com.dealsdirect.ui.base.BaseController;


/**
 * dp Created by Admin on 6/6/17.
 */

public class ShopsController extends BaseController implements ShopsMvpView {

    private static final String KEY_TEXT = "ShopController.KEY_TEXT";

    @Inject
    ShopsMvpPresenter<ShopsMvpView> mPresenter;


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

        BannerRequest bannerRequest
                = new BannerRequest("40f80218-a9e1-43c4-96ff-4c046d192a21",100,false,true,-1,
                                    "en","DA","");

        mPresenter.loadShopsBanner(bannerRequest);
    }


    @Override
    protected void onDestroyView(@NonNull View view) {
        super.onDestroyView(view);
    }

    @Override protected void setUp(View view) {

    }

    @Override public void showShopBanners(BannerResponse bannerResponse) {
        int listSize = bannerResponse.getBanner().getList().size();
        Log.d("ShopsController", bannerResponse.getBanner().getList().get(0).getSales().size()
                                 +"");
    }
}
