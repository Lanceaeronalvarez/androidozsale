package au.com.dealsdirect.ui.controller.shops;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.banner.GetPublicSalesBannerRequest;
import au.com.dealsdirect.data.network.model.banner.GetPublicSalesBannerResponse;
import au.com.dealsdirect.ui.base.BaseActivity;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.categories.CategoriesController;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsController;
import au.com.dealsdirect.ui.controller.shops.adapter.BannersAdapter;
import au.com.dealsdirect.ui.controller.shops.changehandler.HorizontalNavTransitionChangeHandler;
import au.com.dealsdirect.ui.controller.shops.listener.BannerClickListener;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;


/**
 * dp Created by Admin on 6/6/17.
 */

public class ShopsController extends BaseController implements ShopsMvpView, BannerClickListener {

    private static final String KEY_TEXT = "ShopController.KEY_TEXT";
    private static final String KEY_CATEGORY_ID = "ShopController.KEY_CATEGORY_ID";

    @Inject
    ShopsMvpPresenter<ShopsMvpView> mPresenter;

    @BindView(R.id.controller_shop_banner_recycler)
    RecyclerView shopsControllerBannerRecyclerView;

    private BannersAdapter mBannersAdapter;
    private BannerClickListener mBannerClickListener;
    private String mCategoryID;

    private List<GetPublicSalesBannerResponse.Sale> sales = new LinkedList<>();

    public ShopsController(String categoryID) {

        this(new BundleBuilder(new Bundle()).putString(KEY_CATEGORY_ID, categoryID)
                                            .build());
    }

    public ShopsController() {

    }

    public ShopsController(Bundle args) {
        super(args);
        mCategoryID = getArgs().getString(KEY_CATEGORY_ID);

    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {

        View view = inflater.inflate(R.layout.controller_shop, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);

        return view;
    }

    @Override protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        GetPublicSalesBannerRequest getPublicSalesBannerRequest;

        if (mCategoryID == null) {

            getPublicSalesBannerRequest = new GetPublicSalesBannerRequest(
                    "40f80218-a9e1-43c4-96ff-4c046d192a21", 100, false, true, -1, "EN", "DA", "");
        } else {
            getPublicSalesBannerRequest = new GetPublicSalesBannerRequest(
                    mCategoryID, 100, false, true, -1, "EN", "DA", "");
        }


        if (sales.size() == 0) {
            mPresenter.loadShopsBanner(getPublicSalesBannerRequest);
        } else {
            mBannersAdapter = new BannersAdapter(getActivity(), sales, mBannerClickListener);

            shopsControllerBannerRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            shopsControllerBannerRecyclerView.setAdapter(mBannersAdapter);
        }

        assert (getActivity()) != null;
        ((BaseActivity) getActivity()).showToolbarLeftOption(
                getActivity().getDrawable(R.drawable.ic_action_menu),
                view1 -> {
                    getRouter().pushController(
                            RouterTransaction.with(new CategoriesController())
                                             .pushChangeHandler(
                                                     new HorizontalNavTransitionChangeHandler())
                                             .popChangeHandler(
                                                     new HorizontalNavTransitionChangeHandler()));
                });

        ((BaseActivity) getActivity()).showHeaderLogo();
        ((BaseActivity) getActivity()).hideToolbarRightOption();

    }


    @Override protected void onDestroyView(@NonNull View view) {
        super.onDestroyView(view);
    }

    @Override protected void setUp(View view) {

    }

    @Override
    public void showShopBanners(GetPublicSalesBannerResponse getPublicSalesBannerResponse) {

        sales = getPublicSalesBannerResponse.getBanner()
                                            .getList()
                                            .get(0)
                                            .getSales();

        mBannerClickListener = this;

        mBannersAdapter = new BannersAdapter(getActivity(), sales, mBannerClickListener);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setAutoMeasureEnabled(false);
        shopsControllerBannerRecyclerView.setLayoutManager(linearLayoutManager);
        shopsControllerBannerRecyclerView.setAdapter(mBannersAdapter);
        mBannersAdapter.notifyDataSetChanged();

    }


    @Override public void onBannerClicked(String bannerTitle, String bannerId, int position,
            String imageUrl) {

        List<String> names = new ArrayList<>();
        names.add(bannerId + position);

        getRouter().pushController(RouterTransaction.with(new SaleItemsController(bannerTitle,
                                                                                  bannerId,
                                                                                  position,
                                                                                  imageUrl))
                                                    .pushChangeHandler(new HorizontalChangeHandler())
                                                    .popChangeHandler(new HorizontalChangeHandler()));
    }
}
