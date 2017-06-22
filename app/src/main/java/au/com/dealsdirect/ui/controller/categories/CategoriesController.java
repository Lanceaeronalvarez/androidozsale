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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.data.network.model.publicsalescategories.GetPublicSalesCategoriesResponse;
import au.com.dealsdirect.ui.base.BaseActivity;
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


    private List<GetCategoryTreeResponse> mCategories;
    private static Map<String, List<GetCategoryTreeResponse>> mCategoryMap = new HashMap<>();
    private String mChosenCategory = "shop";
    private String mChosenCategoryKey;

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

        assert (getActivity()) != null;
        ((BaseActivity) getActivity()).hideBottomNavigationView();

        setUp(view);
    }


    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    protected void setUp(View view) {

        mTitleTextView.setText(R.string.category_title);

        mCategoryClickListener = this;
        mAdapter = new CategoriesAdapter(new ArrayList<>(), mPresenter, mCategoryClickListener);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);

        mPresenter.loadCategoryTree();
    }

    @Override
    public void onDetach(View view) {
        super.onDetach(view);

        assert (getActivity()) != null;
        ((BaseActivity) getActivity()).showBottomNavigationView();
    }

    @Override
    public void showPublicSalesCategories(List<GetPublicSalesCategoriesResponse.SaleList> saleList) {
//        mAdapter.replaceData(saleList);
    }

    @Override
    public void showCategories(List<GetCategoryTreeResponse> categories) {
        mCategories = categories;
        createCategoryMap(mCategories);
    }


    @Override
    public void onCategoryClicked(String categoryID, String categoryName, String categoryKey) {
        getRouter().setRoot(
                RouterTransaction.with(new ShopsController(categoryID, categoryName))
                        .pushChangeHandler(new RightHorizontalTransitionChangeHandler())
                        .popChangeHandler(new RightHorizontalTransitionChangeHandler()));

        mChosenCategory = categoryName;
        mChosenCategoryKey = categoryKey;
    }

    private void createCategoryMap(List<GetCategoryTreeResponse> categories) {
        mCategoryMap.put("shop", categories);

        for (GetCategoryTreeResponse i : categories) {

            int childrenSize = i.getChildren().size();
            if (childrenSize != 0) {

                addToMap(i.getChildren());
            }

            mCategoryMap.put(i.getKey(), i.getChildren());
        }

        List<GetCategoryTreeResponse> result = fillCategoryContent();
        mAdapter.replaceData(result);

    }

    private void addToMap(List<GetCategoryTreeResponse> list) {

        for (GetCategoryTreeResponse i : list) {

            int childrenSize = i.getChildren().size();
            if (childrenSize != 0) {
                addToMap(i.getChildren());
            }

            mCategoryMap.put(i.getKey(), i.getChildren());

        }
    }

    public List<GetCategoryTreeResponse> fillCategoryContent() {

        if (mChosenCategory.equals("shop")) {

            return mCategoryMap.get(mChosenCategory);

        } else if (mCategoryMap.get(mChosenCategory) != null) {

            return mCategoryMap.get(mChosenCategoryKey);

        } else {

            List<GetCategoryTreeResponse> listApi = new LinkedList<>();
            GetCategoryTreeResponse apiCategoryModel = new GetCategoryTreeResponse();
            apiCategoryModel.setName("");
            apiCategoryModel.setCount(0);
            apiCategoryModel.setKey("");
            apiCategoryModel.setChildren(null);

            listApi.add(apiCategoryModel);

            return listApi;
        }
    }

}
