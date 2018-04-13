package au.com.dealsdirect.ui.controller.categories;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.categories.adapter.CategoriesAdapter;
import au.com.dealsdirect.ui.controller.categories.adapter.SubCategoriesAdapter;
import au.com.dealsdirect.ui.controller.categories.listener.CategoryClickListener;
import au.com.dealsdirect.ui.controller.categories.listener.SubCategoryClickListener;
import au.com.dealsdirect.ui.controller.categories.listener.SubCategoryItemClickListener;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsController;
import au.com.dealsdirect.ui.controller.shops.ShopsController;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * dp Created by Admin on 6/6/17.
 */

public class CategoriesController extends BaseController
        implements CategoriesMvpView, CategoryClickListener, SubCategoryItemClickListener {

    @Inject
    CategoriesMvpPresenter<CategoriesMvpView> mPresenter;

    @BindView(R.id.controller_categories_content_layout)
    LinearLayout mContentLayout;

    @BindView(R.id.categories_recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.sub_categories_recyclerview)
    RecyclerView mSubCategoryRecyclerView;

    @BindView(R.id.partial_toolbar_arrow_title)
    TextView mToolbarTitle;

    @BindView(R.id.partial_toolbar_left_view)
    ImageButton mToolbarLeftButton;

    @BindView(R.id.no_network_layout)
    LinearLayout mNoNetworkLayout;

    public static final String TAG = "CategoriesController";
    public boolean mIsResetSubCategories = false;


    private GetCategoryTreeResponse mChosenSubCategoryTreeResponse = new GetCategoryTreeResponse();
    private SubCategoriesAdapter mSubCategoryAdapter;
    private CategoriesAdapter mAdapter;
    private SubCategoryClickListener mSubCategoryClickListener;
    private SubCategoryItemClickListener mSubCategoryItemClickListener;

    private List<GetCategoryTreeResponse> mCategories;
    private Map<String, List<GetCategoryTreeResponse>> mCategoryMap = new HashMap<>();

    private int searchTapCounter = 0;

    public static CategoriesController newInstance() {
        return new CategoriesController(
                new BundleBuilder(new Bundle())
                        .build());
    }


    public CategoriesController(Bundle args) {
        super(args);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        mPresenter.onAttach(this);

    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {

        View view = inflater.inflate(R.layout.controller_categories, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        assert (mActivity) != null;
        mActivity.setDraggableViewPager(true);
        mActivity.setCategoriesRouter(getRouter());
        mActivity.setCategoriesController(this);
        hideKeyboard();
        mPresenter.callGetCategoryTree();
        mNoNetworkLayout.setOnClickListener((v)->mPresenter.callGetCategoryTree());
        setUp(view);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);

    }

    @Override
    protected void setUp(View view) {

        //noinspection ConstantConditions,deprecation
        mToolbarLeftButton.setVisibility(View.GONE);
        mToolbarTitle.setText(mActivity.getResources().getString(R.string.browse));
        mSubCategoryItemClickListener = this;

    }

    private void setupCategories(){
        if (mCategories != null) {
            mAdapter = new CategoriesAdapter(mCategories, mPresenter, this);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
            mRecyclerView.setMotionEventSplittingEnabled(false);
            mRecyclerView.setAdapter(mAdapter);

            if (mCategories.get(0).getChildren() != null) {
                mSubCategoryAdapter = new SubCategoriesAdapter(
                        new ArrayList<>(), mPresenter, mSubCategoryItemClickListener, mCategoryMap);
            } else {
                mSubCategoryAdapter = new SubCategoriesAdapter(
                        mCategories.get(0).getChildren(), mPresenter, mSubCategoryItemClickListener, mCategoryMap);
            }
            mSubCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
            mSubCategoryRecyclerView.setAdapter(mSubCategoryAdapter);
            mSubCategoryRecyclerView.setMotionEventSplittingEnabled(false);

        }
    }


    @Override
    public void onCategoryClicked(int position, GetCategoryTreeResponse getCategoryTreeResponse) {
        mChosenSubCategoryTreeResponse = getCategoryTreeResponse;

        String categoryName = getCategoryTreeResponse.getName();

        //noinspection ConstantConditions
        if (categoryName.equals(mActivity.getResources().getString(R.string.shop_category))) {

            ArrayList<GetCategoryTreeResponse> emptyChildren = new ArrayList<>();
            mSubCategoryAdapter = new SubCategoriesAdapter(emptyChildren, mPresenter, mSubCategoryItemClickListener, mCategoryMap);
            mSubCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
            mSubCategoryRecyclerView.setAdapter(mSubCategoryAdapter);

            mIsResetSubCategories = true;
            GetCategoryTreeResponse shopCategory = new GetCategoryTreeResponse();
            mSubCategoryAdapter.isResetSubCategories(mIsResetSubCategories);
            mSubCategoryAdapter.notifyDataSetChanged();
            mAdapter.notifyDataSetChanged();

            assert (mActivity) != null;

        } else {
            mSubCategoryAdapter.isResetSubCategories(mIsResetSubCategories);
            if (mCategories != null && mCategories.get(position).getChildren() != null) {
                mSubCategoryAdapter = new SubCategoriesAdapter(
                        (mCategories.get(position).getChildren()), mPresenter, mSubCategoryItemClickListener, mCategoryMap);
                mSubCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
                mSubCategoryRecyclerView.setAdapter(mSubCategoryAdapter);
                mSubCategoryAdapter.notifyDataSetChanged();

            } else {
                ArrayList<GetCategoryTreeResponse> emptyChildren = new ArrayList<>();
                mSubCategoryAdapter = new SubCategoriesAdapter(emptyChildren, mPresenter, mSubCategoryItemClickListener, mCategoryMap);
                mSubCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
                mSubCategoryRecyclerView.setAdapter(mSubCategoryAdapter);

            }
            mIsResetSubCategories = true;

        }
    }

    @Override
    public void onSubCategoryItemClicked(String categoryID, String categoryName, String categoryKey) {


        mActivity.getMainController().setChosenCategoryItemKey(categoryKey);

        Bundle saleItemBundle = new BundleBuilder(new Bundle())
                .putString(BundleKeys.SALEITEMS_TITLE, categoryKey)
                .putString(BundleKeys.SALEITEMS_CATEGORY_MAP, categoryKey)
                .putBoolean(BundleKeys.SALEITEMS_FROM_CATEGORY_SEARCH, false)
                .putBoolean(BundleKeys.SALEITEMS_FROM_CATEGORIES,true)
                .build();

        mActivity.getCategoriesRouter().pushController(RouterTransaction.with(
                new SaleItemsController(saleItemBundle))
                .tag(getResources().getString(R.string.sale_items_controller_tag))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));


        mSubCategoryAdapter.animateInsertItems(false);
        mIsResetSubCategories = false;

        mActivity.setIsFromCategories(true);

    }


    private List<GetCategoryTreeResponse> updateCategoryChildren(GetCategoryTreeResponse categoryTree) {
        GetCategoryTreeResponse getCategoryTreeResponse = new GetCategoryTreeResponse();
        getCategoryTreeResponse.setName("All");
        getCategoryTreeResponse.setKey(categoryTree.getKey());
        getCategoryTreeResponse.setChildren(new ArrayList<>());
        getCategoryTreeResponse.setNodeType("usual");
        getCategoryTreeResponse.setId(categoryTree.getId());

        List<GetCategoryTreeResponse> newList = new ArrayList<>();

        if (categoryTree.getChildren() != null) {
            for (int i = 0; i < categoryTree.getChildren().size() + 1; i++) {

                if (i == 0) {
                    newList.add(getCategoryTreeResponse);

                } else {
                    newList.add(categoryTree.getChildren().get(i - 1));
                }
            }
        }
        return newList;
    }

    private void onSearchFieldClick() {
        Handler handler = new Handler();
        handler.postDelayed(() -> searchTapCounter = 0, 500);
    }

    public void updateSubCategoryItemState() {
        mSubCategoryAdapter.notifyDataSetChanged();
    }

    @Override
    public void showNoNetworkLayout(){
        mContentLayout.setVisibility(View.GONE);
        mNoNetworkLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNoNetworklayout(){
        mContentLayout.setVisibility(View.VISIBLE);
        mNoNetworkLayout.setVisibility(View.GONE);
    }


    @Override
    public void showCategories(List<GetCategoryTreeResponse> categories) {
        mCategories = categories;
        createCategoryMap(mCategories);
    }

    private void createCategoryMap(List<GetCategoryTreeResponse> categories) {

        List<GetCategoryTreeResponse> newList;
        mCategoryMap.put("shop", categories);

        for (GetCategoryTreeResponse i : categories) {

            newList = updateCategoryChildren(i);

            if (newList != null) {

                int childrenSize = newList.size();
                if (childrenSize != 1) {

                    addToMap(newList);
                }

                mCategoryMap.put(i.getKey(), newList);
            }
        }

        mCategories = fillCategoryContent();
        setupCategories();
    }

    private void addToMap(List<GetCategoryTreeResponse> list) {
        List<GetCategoryTreeResponse> newList2;

        for (GetCategoryTreeResponse i : list) {
            newList2 = updateCategoryChildren(i);

            int childrenSize = newList2.size();
            if (childrenSize != 1) {
                addToMap(newList2);
            }

            mCategoryMap.put(i.getKey(), newList2);

        }
    }

    private List<GetCategoryTreeResponse> fillCategoryContent() {

        return mCategoryMap.get("shop");
    }
}
