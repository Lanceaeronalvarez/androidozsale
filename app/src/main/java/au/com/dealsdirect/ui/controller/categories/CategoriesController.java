package au.com.dealsdirect.ui.controller.categories;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

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
import au.com.dealsdirect.ui.controller.categories.adapter.CategoriesAdapter;
import au.com.dealsdirect.ui.controller.categories.adapter.SubCategoriesAdapter;
import au.com.dealsdirect.ui.controller.categories.listener.CategoryClickListener;
import au.com.dealsdirect.ui.controller.categories.listener.SubCategoryClickListener;
import au.com.dealsdirect.ui.controller.categories.listener.SubCategoryItemClickListener;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsController;
import au.com.dealsdirect.ui.controller.shops.ShopsController;
import au.com.dealsdirect.ui.custom.transitions.RightHorizontalTransitionChangeHandler;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * dp Created by Admin on 6/6/17.
 */

public class CategoriesController extends BaseController
        implements CategoriesMvpView, CategoryClickListener, SubCategoryClickListener, SubCategoryItemClickListener {

    @Inject
    CategoriesMvpPresenter<CategoriesMvpView> mPresenter;

    @BindView(R.id.categories_recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.sub_categories_recyclerview)
    RecyclerView mSubCategoryRecyclerView;

    @BindView(R.id.partial_toolbar_search_field)
    EditText mSearchField;

    @BindView(R.id.partial_toolbar_search_right_option)
    ImageButton mToolbarRightOption;


    private CategoriesAdapter mAdapter;
    private CategoryClickListener mCategoryClickListener;

    private SubCategoriesAdapter mSubCategoryAdapter;
    private SubCategoryClickListener mSubCategoryClickListener;
    private SubCategoryItemClickListener mSubCategoryItemClickListener;

    private List<GetCategoryTreeResponse> mCategories;
    private static Map<String, List<GetCategoryTreeResponse>> mCategoryMap = new HashMap<>();
    private String mChosenCategory = "shop";
    private String mChosenCategoryKey;

    private int categoryTapCounter = 1;
    private String lastCategoryKey = "";

    private List<GetCategoryTreeResponse> mResultSubCategories;

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

        mCategoryClickListener = this;
        mSubCategoryClickListener = this;
        mSubCategoryItemClickListener = this;

        mAdapter = new CategoriesAdapter(new ArrayList<>(), mPresenter, mCategoryClickListener);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);

        mSubCategoryAdapter = new SubCategoriesAdapter(
                new ArrayList<>(), mPresenter, mSubCategoryClickListener, mSubCategoryItemClickListener, mCategoryMap);
        mSubCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mSubCategoryRecyclerView.setAdapter(mSubCategoryAdapter);


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
    public void onCategoryClicked(int position, String categoryID, String categoryName, String categoryKey) {


        if (categoryTapCounter == 1 && categoryKey.equals(lastCategoryKey)){
            getRouter().setRoot(
                    RouterTransaction.with(new ShopsController(categoryID, categoryName, categoryKey))
                            .pushChangeHandler(new RightHorizontalTransitionChangeHandler())
                            .popChangeHandler(new RightHorizontalTransitionChangeHandler()));
            categoryTapCounter = 1;
        }


        if (categoryKey.equals(lastCategoryKey))
            categoryTapCounter = categoryTapCounter+1;
        else
            categoryTapCounter = 1;


        mSubCategoryAdapter.replaceData(mResultSubCategories.get(position).getChildren());

        mChosenCategoryKey = categoryKey;
        lastCategoryKey = categoryKey;

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

        mResultSubCategories = fillCategoryContent();

        mAdapter.replaceData(mResultSubCategories);

        mSubCategoryAdapter.replaceData(mResultSubCategories.get(0).getChildren());

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

    public void fillSubCategories() {

    }

    @Override
    public void onSubCategoryClicked(String categoryID, String categoryName, String categoryKey) {

        getRouter().pushController(RouterTransaction.with(
                SaleItemsController.newInstance("", categoryName, "", 0, "", categoryKey))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    @Override
    public void onSubCategoryItemClicked(String categoryID, String categoryName, String categoryKey) {

        getRouter().pushController(RouterTransaction.with(
                SaleItemsController.newInstance("", categoryName, "", 0, "", categoryKey))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    @OnClick(R.id.partial_toolbar_search_right_option)
    public void onSearchOptionClicked(){
        getActivity().onBackPressed();
    }
}
