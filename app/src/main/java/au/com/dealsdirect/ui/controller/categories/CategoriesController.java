package au.com.dealsdirect.ui.controller.categories;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bluelinelabs.conductor.RouterTransaction;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.publicsalescategories.GetPublicSalesCategoriesRequest;
import au.com.dealsdirect.data.network.model.publicsalescategories.GetPublicSalesCategoriesResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.categories.listener.CategoryClickListener;
import au.com.dealsdirect.ui.controller.shops.ShopsController;
import au.com.dealsdirect.ui.custom.transitions.RightHorizontalTransitionChangeHandler;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;

/**
 * dp Created by Admin on 6/6/17.
 */

public class CategoriesController extends BaseController
        implements CategoriesMvpView, CategoryClickListener {

    @Inject
    CategoriesMvpPresenter<CategoriesMvpView> mPresenter;

    @BindView(R.id.categories_recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.partial_toolbar_title_view)
    TextView mTitleTextView;

    private CategoriesAdapter mAdapter;
    private CategoryClickListener mCategoryClickListener;

    public static CategoriesController newInstance() {

        return new CategoriesController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public CategoriesController(Bundle args) {
        super(args);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {

        View view = inflater.inflate(R.layout.controller_categories, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    protected void onViewBound(View view) {
        super.onViewBound(view);

        setUp(view);
    }


    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    protected void setUp(View view) {

        mTitleTextView.setText("Categories");

        mCategoryClickListener = this;
        mAdapter = new CategoriesAdapter(new ArrayList<>(), mPresenter, mCategoryClickListener);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);

        GetPublicSalesCategoriesRequest request = new GetPublicSalesCategoriesRequest("0", "en", false, "Member", "DA");
        mPresenter.loadPublicSalesCategories(request);
    }

    @Override
    public void showPublicSalesCategories(List<GetPublicSalesCategoriesResponse.SaleList> saleList) {
        mAdapter.replaceData(saleList);
    }


    @Override
    public void onCategoryClicked(String categoryID) {
        getRouter().setRoot(
                RouterTransaction.with(new ShopsController(categoryID))
                        .pushChangeHandler(new RightHorizontalTransitionChangeHandler())
                        .popChangeHandler(new RightHorizontalTransitionChangeHandler()));
    }
}
