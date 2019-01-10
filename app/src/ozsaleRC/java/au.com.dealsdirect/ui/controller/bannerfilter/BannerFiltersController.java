package au.com.dealsdirect.ui.controller.bannerfilter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.ControllerChangeHandler;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.shops.ShopsController;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by pauldesilva on 4/13/18.
 */

public class BannerFiltersController extends BaseController implements BannerFiltersMvpView, BannerFilterClickListener {

    @Inject
    BannerFiltersMvpPresenter<BannerFiltersMvpView> mPresenter;

    @BindView(R.id.partial_toolbar_field_title_left_option)
    View mLeftImageButton;

    @BindView(R.id.recylerview_banner_filters)
    RecyclerView mBannerFiltersRecyclerView;

    @BindView(R.id.partial_toolbar_field_title_textview)
    TextView mTitleText;

    @BindView(R.id.no_network_layout)
    LinearLayout mNoNetworkLayout;

    BannerFiltersAdapter bannerFiltersAdapter;
    private boolean mHasSavedInstance = false;
    private ControllerChangeHandler.ControllerChangeListener newControllerChangeHandler;

    public static BannerFiltersController newInstance() {

        return new BannerFiltersController(new BundleBuilder(new Bundle()).build());
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
        mActivity.getMainController().setBannerFiltersController(this);
        bannerFiltersAdapter = new BannerFiltersAdapter(mActivity, this, new ArrayList<>());
        mBannerFiltersRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        mBannerFiltersRecyclerView.setAdapter(bannerFiltersAdapter);

        mLeftImageButton.setVisibility(View.GONE);
        mTitleText.setText(getResources().getString(R.string.category_title));

        if (mHasSavedInstance) {
            BannerFiltersController currentController = this;
            newControllerChangeHandler = new ControllerChangeHandler.ControllerChangeListener() {

                @Override
                public void onChangeStarted(@Nullable Controller to,
                                            @Nullable Controller from, boolean isPush,
                                            @NonNull ViewGroup container,
                                            @NonNull ControllerChangeHandler handler) {

                }

                @Override
                public void onChangeCompleted(@Nullable Controller to,
                                              @Nullable Controller from, boolean isPush,
                                              @NonNull ViewGroup container,
                                              @NonNull ControllerChangeHandler handler) {
                    if (to == currentController) {
                        mPresenter.callGetCategoryTree();
                        mActivity.getMainController().getHomeController().setSavedCurrentItem();
                    }
                }
            };
            getRouter().addChangeListener(newControllerChangeHandler);
        } else {
            mPresenter.callGetCategoryTree();
        }
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    public void onDetach(View view) {
        super.onDetach(view);
        mPresenter.onDetach();
    }

    @Override
    public void showCategories(List<GetCategoryTreeResponse> categories) {
        bannerFiltersAdapter.replaceData(categories);
    }
    @Override
    public void showNoNetworkLayout(){
        mBannerFiltersRecyclerView.setVisibility(View.GONE);
        mNoNetworkLayout.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.no_network_layout)
    public void refreshBannerFilters() {
        mPresenter.callGetCategoryTree();
    }

    @Override
    public void refreshContents() {
        super.refreshContents();
        mPresenter.callGetCategoryTree();
    }

    @Override
    public void hideNoNetworklayout(){
        mBannerFiltersRecyclerView.setVisibility(View.VISIBLE);
        mNoNetworkLayout.setVisibility(View.GONE);
    }

    @Override
    public void onBannerClicked(int position, GetCategoryTreeResponse getCategoryTreeResponse) {
        if (mActivity.getHomeRouter().getControllerWithTag(ShopsController.TAG) != null) {
            mActivity.getHomeRouter().popToTag(ShopsController.TAG);
        }

        mActivity.goToSalesFromCategory(getCategoryTreeResponse);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE, true);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mHasSavedInstance = savedInstanceState.getBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE);
    }

    @Override
    public void onDestroyView(@NonNull View view) {
        if (newControllerChangeHandler != null) {
            getRouter().removeChangeListener(newControllerChangeHandler);
            newControllerChangeHandler = null;
        }
        super.onDestroyView(view);
    }
}
