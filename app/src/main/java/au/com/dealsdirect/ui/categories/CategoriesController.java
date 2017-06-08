package au.com.dealsdirect.ui.categories;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.publicsalescategories.GetPublicSalesCategoriesRequest;
import au.com.dealsdirect.data.network.model.publicsalescategories.GetPublicSalesCategoriesResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.utils.AppLogger;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * dp Created by Admin on 6/6/17.
 */

public class CategoriesController extends BaseController implements CategoriesMvpView {

    @Inject
    CategoriesMvpPresenter<CategoriesMvpView> mPresenter;

    @BindView(R.id.categories_recyclerview)
    RecyclerView mRecyclerView;

    CategoriesAdapter mAdapter;

    private Unbinder mUnBinder;

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {

        View view = inflater.inflate(R.layout.controller_categories, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);

        mUnBinder = ButterKnife.bind(this,view);

        setUp(view);
        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
    }


    @Override
    protected void setUp(View view) {
        mAdapter = new CategoriesAdapter(new ArrayList<>(),mPresenter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        mRecyclerView.setAdapter(mAdapter);

        GetPublicSalesCategoriesRequest request = new GetPublicSalesCategoriesRequest("0","en","false","Member","DA");
        mPresenter.loadPublicSalesCategories(request);
    }


    @Override
    protected void onDestroyView(@NonNull View view) {
        super.onDestroyView(view);
        mUnBinder.unbind();
    }


    @Override
    public void showPublicSalesCategories(List<GetPublicSalesCategoriesResponse.SaleList> saleList) {
        mAdapter.replaceData(saleList);
    }

    @Override
    public void onError(String message) {
        super.onError(message);
    }
}
