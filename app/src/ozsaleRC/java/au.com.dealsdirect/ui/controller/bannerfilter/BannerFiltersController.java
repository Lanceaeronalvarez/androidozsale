package au.com.dealsdirect.ui.controller.bannerfilter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.account.AccountItemAdapter;
import au.com.dealsdirect.ui.controller.categories.adapter.SubCategoriesAdapter;
import au.com.dealsdirect.ui.controller.main.MainController;
import au.com.dealsdirect.ui.controller.shops.ShopsController;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;

/**
 * Created by pauldesilva on 4/13/18.
 */

public class BannerFiltersController extends BaseController implements BannerFiltersMvpView, BannerFilterClickListener {

    @Inject
    BannerFiltersMvpPresenter<BannerFiltersMvpView> mPresenter;

    @BindView(R.id.partial_toolbar_field_title_left_option)
    public ImageButton mLeftImageButton;

    @BindView(R.id.partial_toolbar_field_title_textview)
    public TextView mTitleText;

    BannerFiltersAdapter bannerFiltersAdapter;

    @BindView(R.id.recylerview_banner_filters)
    RecyclerView mBannerFiltersRecyclerView;

    public static BannerFiltersController newInstance() {

        return new BannerFiltersController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public BannerFiltersController(Bundle args) {
        super(args);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_banner_filters, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    protected void setUp(View view) {
        bannerFiltersAdapter = new BannerFiltersAdapter(mActivity, this, new ArrayList<>());
        mBannerFiltersRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity,
                LinearLayoutManager.VERTICAL,false));
        mBannerFiltersRecyclerView.setAdapter(bannerFiltersAdapter);
        mLeftImageButton.setVisibility(View.GONE);
        mTitleText.setText(getResources().getString(R.string.category_title));
        mPresenter.callGetCategoryTree();
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    public void showCategories(List<GetCategoryTreeResponse> categories) {
        bannerFiltersAdapter.replaceData(categories);
    }

    @Override
    public void hideNoNetworklayout() {

    }

    @Override
    public void onDetach(View view) {
        super.onDetach(view);
        mPresenter.onDetach();
    }

    @Override
    public void onBannerClicked(int position, GetCategoryTreeResponse getCategoryTreeResponse) {
        if (mActivity.getHomeRouter().getControllerWithTag(ShopsController.TAG)!=null){
            mActivity.getHomeRouter().popToTag(ShopsController.TAG);
        }

        mActivity.goToSalesFromCategory(getCategoryTreeResponse);
    }
}
