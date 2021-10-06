package au.com.dealsdirect.ui.controller.bannerfilter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

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
    public void onViewDidAppear(Controller previousController) {
        super.onViewDidAppear(previousController);

        if (mHasSavedInstance) {
            mActivity.getMainController().setSavedCurrentItem();
        }
    }

    @Override
    protected void setUp(View view) {
        bannerFiltersAdapter = new BannerFiltersAdapter(this, new ArrayList<>());
        bannerFiltersAdapter.setBrandsAvailable(true);
        mBannerFiltersRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        mBannerFiltersRecyclerView.setAdapter(bannerFiltersAdapter);

        mLeftImageButton.setVisibility(View.GONE);
        mTitleText.setText(getResources().getString(R.string.category_title));
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        mPresenter.onAttach(this);
        mPresenter.callGetCategoryTree();
    }

    @Override
    public void onDetach(View view) {
        super.onDetach(view);
        mPresenter.onDetach();
    }

    @Override
    public void showCategories(List<GetCategoryTreeResponse> categories) {

        List<GetCategoryTreeResponse> filteredCategories = new ArrayList<>();
        for (int i = 0; i < categories.size(); i++) {
            GetCategoryTreeResponse categoryTreeResponse = categories.get(i);
            if (categoryTreeResponse.getLinkOptions() == null) {
                filteredCategories.add(categoryTreeResponse);
            }
        }

        bannerFiltersAdapter.replaceData(filteredCategories);
    }

    @Override
    public void showNoNetworkLayout() {
        mBannerFiltersRecyclerView.setVisibility(View.GONE);
        mNoNetworkLayout.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.no_network_layout)
    public void refreshBannerFilters() {
        if (mPresenter == null) {
            return;
        }
        mPresenter.callGetCategoryTree();
    }

    @Override
    public void refreshContents() {
        super.refreshContents();
        mPresenter.callGetCategoryTree();
    }

    @Override
    public void hideNoNetworklayout() {
        mBannerFiltersRecyclerView.setVisibility(View.VISIBLE);
        mNoNetworkLayout.setVisibility(View.GONE);
    }

    @Override
    public void onCategoryClicked(int position, GetCategoryTreeResponse getCategoryTreeResponse) {
        ShopsController shopsController = ShopsController.instanceWithCategoryFilter(
                getCategoryTreeResponse.getId(),
                getCategoryTreeResponse.getKey()
        );
        getRouter().pushController(
                RouterTransaction.with(shopsController)
                        .popChangeHandler(new HorizontalChangeHandler())
                        .pushChangeHandler(new HorizontalChangeHandler()));
    }

    @Override
    public void onBrandsClicked() {
        ShopsController shopsController = ShopsController.instanceWithBrandsOnlyFilter();
        getRouter().pushController(
                RouterTransaction.with(shopsController)
                        .popChangeHandler(new HorizontalChangeHandler())
                        .pushChangeHandler(new HorizontalChangeHandler()));
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
        super.onDestroyView(view);
    }
}
