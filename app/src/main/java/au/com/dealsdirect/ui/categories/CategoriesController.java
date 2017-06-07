package au.com.dealsdirect.ui.categories;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.publicsalescategories.GetPublicSalesCategoriesRequest;
import au.com.dealsdirect.data.network.model.publicsalescategories.GetPublicSalesCategoriesResponse;
import au.com.dealsdirect.ui.base.BaseController;

/**
 * dp Created by Admin on 6/6/17.
 */

public class CategoriesController extends BaseController implements CategoriesMvpView {

    @Inject
    CategoriesMvpPresenter<CategoriesMvpView> mPresenter;

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {

        View view = inflater.inflate(R.layout.controller_categories, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);

        setUp(view);
        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
    }


    @Override
    protected void setUp(View view) {
        GetPublicSalesCategoriesRequest request = new GetPublicSalesCategoriesRequest("0","en","false","Member","DA");
        mPresenter.loadPublicSalesCategories(request);
    }


    @Override
    protected void onDestroyView(@NonNull View view) {
        super.onDestroyView(view);
    }


    @Override
    public void showPublicSalesCategories(List<GetPublicSalesCategoriesResponse.SaleList> saleList) {
        Log.d("saleListSize",saleList.size()+"");

    }

    @Override
    public void onError(String message) {
        super.onError(message);
    }
}
