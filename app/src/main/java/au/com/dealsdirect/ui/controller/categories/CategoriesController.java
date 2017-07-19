package au.com.dealsdirect.ui.controller.categories;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
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
import au.com.dealsdirect.ui.controller.search.SearchController;
import au.com.dealsdirect.ui.controller.shops.ShopsController;
import au.com.dealsdirect.ui.custom.transitions.RightHorizontalTransitionChangeHandler;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * dp Created by Admin on 6/6/17.
 */

public class CategoriesController extends BaseController
        implements CategoriesMvpView, CategoryClickListener, SubCategoryClickListener, SubCategoryItemClickListener {

    private static final String KEY_CATEGORY_ID = "CategoriesController.CATEGORY_KEY";
    private static final String KEY_QUERY_ID = "CategoriesController.CATEGORY_QUERY";
    private static final String KEY_REQUEST_FROM = "CategoriesController.REQUEST_FROM";

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

    private static List<GetCategoryTreeResponse> mCategories;
    private static Map<String, List<GetCategoryTreeResponse>> mCategoryMap = new HashMap<>();
    private String mChosenCategory = "shop";
    private String mChosenCategoryKey;

    private int searchTapCounter = 0;
    private String lastCategoryKey = "";

    private List<GetCategoryTreeResponse> mResultSubCategories;

    public static CategoriesController newInstance(
            Map<String, List<GetCategoryTreeResponse>> categoryMap,
            List<GetCategoryTreeResponse> categoryTree) {

        mCategories = categoryTree;
        mCategoryMap = categoryMap;

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
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        assert (getActivity()) != null;
        ((MainActivity)getActivity()).setCategoriesRouter(getRouter());
        setUp(view);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);

        assert (getActivity()) != null;
        ((BaseActivity) getActivity()).showBottomNavigationView();
    }

    @Override
    protected void setUp(View view) {

        mSearchField.setOnTouchListener((view1, motionEvent) -> {
            searchTapCounter += 1;
            if (searchTapCounter == 1)
                onSearchFieldClick();
            return false;
        });

        //noinspection ConstantConditions
        mToolbarRightOption.setImageDrawable(getResources().getDrawable(R.drawable.ic_tab_shop_white));
        mCategoryClickListener = this;
        mSubCategoryClickListener = this;
        mSubCategoryItemClickListener = this;

        mAdapter = new CategoriesAdapter(mCategories, mPresenter, mCategoryClickListener);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);

        if (mCategories.get(0).getChildren() != null){
            mSubCategoryAdapter = new SubCategoriesAdapter(
                    new ArrayList<>(), mPresenter, mSubCategoryClickListener, mSubCategoryItemClickListener, mCategoryMap);
        }else{
            mSubCategoryAdapter = new SubCategoriesAdapter(
                    mCategories.get(0).getChildren(), mPresenter, mSubCategoryClickListener, mSubCategoryItemClickListener, mCategoryMap);
        }

        mSubCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mSubCategoryRecyclerView.setAdapter(mSubCategoryAdapter);

//        mPresenter.loadCategoryTree();

        mSearchField.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(mSearchField.getText().toString());
                return true;
            }
            return false;
        });

        final Handler handler = new Handler();
        handler.postDelayed(() -> {

            assert (getActivity()) != null;
            ((BaseActivity) getActivity()).hideBottomNavigationView();
        }, 100);
    }

    @Override
    public void onDetach(View view) {
        mPresenter.onDetach();
        super.onDetach(view);

    }

    @Override
    public void showPublicSalesCategories(List<GetPublicSalesCategoriesResponse.SaleList> saleList) {
//        mAdapter.replaceData(saleList);
    }

    @Override
    public void onCategoryClicked(int position, GetCategoryTreeResponse getCategoryTreeResponse) {

        String categoryName = getCategoryTreeResponse.getName();
        String categoryKey = getCategoryTreeResponse.getKey() != null ? getCategoryTreeResponse.getKey() : categoryName;

        //noinspection ConstantConditions
        if (categoryName.equals(getActivity().getResources().getString(R.string.shop_category))){

            ArrayList<GetCategoryTreeResponse> emptyChildren = new ArrayList<>();
            mSubCategoryAdapter = new SubCategoriesAdapter(emptyChildren, mPresenter, mSubCategoryClickListener, mSubCategoryItemClickListener, mCategoryMap);
            mSubCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            mSubCategoryRecyclerView.setAdapter(mSubCategoryAdapter);

            GetCategoryTreeResponse shopCategory = new GetCategoryTreeResponse();

            assert (getActivity()) != null;
            ((MainActivity)getActivity()).goToSalesFromCategory(shopCategory);

        }else{
            if (mCategories.get(position).getChildren() != null) {
                List<GetCategoryTreeResponse> newList = updateCategoryChildren(mCategories.get(position));
                mSubCategoryAdapter = new SubCategoriesAdapter(
                        newList, mPresenter, mSubCategoryClickListener, mSubCategoryItemClickListener, mCategoryMap);
                mSubCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                mSubCategoryRecyclerView.setAdapter(mSubCategoryAdapter);


            } else {
                ArrayList<GetCategoryTreeResponse> emptyChildren = new ArrayList<>();
                mSubCategoryAdapter = new SubCategoriesAdapter(emptyChildren, mPresenter, mSubCategoryClickListener, mSubCategoryItemClickListener, mCategoryMap);
                mSubCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                mSubCategoryRecyclerView.setAdapter(mSubCategoryAdapter);
            }

        }
        mChosenCategoryKey = categoryKey != null ? categoryKey : categoryName;
        lastCategoryKey = categoryKey != null ? categoryKey : categoryName;
    }

    @Override
    public void onCategoryDoubleTap(int position, GetCategoryTreeResponse getCategoryTreeResponse) {

        getRouter().setRoot(
                RouterTransaction.with(ShopsController.newInstance(getCategoryTreeResponse))
                        .pushChangeHandler(new RightHorizontalTransitionChangeHandler())
                        .popChangeHandler(new RightHorizontalTransitionChangeHandler()));
    }

    private void createCategoryMap(List<GetCategoryTreeResponse> categories) {

        for (GetCategoryTreeResponse i : categories) {

            if (i.getChildren() != null) {
                mCategoryMap.put("shop", categories);

                int childrenSize = i.getChildren().size();
                if (childrenSize != 0) {

                    addToMap(i.getChildren());
                }
                mCategoryMap.put(i.getKey(), i.getChildren());

            }
        }

        mResultSubCategories = fillCategoryContent();
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

    private List<GetCategoryTreeResponse> fillCategoryContent() {

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

    @Override
    public void onSubCategoryClicked(GetCategoryTreeResponse getCategoryTreeResponse) {

        if (getCategoryTreeResponse.getName().equals("All")){

            assert (getActivity()) != null;
            ((MainActivity)getActivity()).goToSalesFromCategory(getCategoryTreeResponse);
        }
    }

    @Override
    public void onSubCategoryItemClicked(String categoryID, String categoryName, String categoryKey) {

        Bundle saleItemBundle = new BundleBuilder(new Bundle())
                .putString("SaleItemsController.KEY_TITLE", categoryName)
                .putString("SaleItemsController.CATEGORY_KEY", categoryKey)
                .putString("SaleItemsController.KEY_SALE_ID", categoryID)
                .build();


        assert (getActivity()) != null;
        ((MainActivity)getActivity()).goToSaleItemsFromCategory(saleItemBundle);

//        getRouter().pushController(RouterTransaction.with(
//                SaleItemsController.newInstance(saleItemBundle))
//                .pushChangeHandler(new HorizontalChangeHandler())
//                .popChangeHandler(new HorizontalChangeHandler()));
    }

    @SuppressWarnings("ConstantConditions")
    @OnClick(R.id.partial_toolbar_search_right_option)
    void onSearchOptionClicked() {

        ((MainActivity) getActivity()).setRootViewpagerItem(1);

//        String searchQuery = mSearchField.getText().toString();
//
//        Bundle saleItemBundle = new BundleBuilder(new Bundle())
//                .putString("SaleItemsController.KEY_TITLE", searchQuery)
//                .putString("SaleItemsController.SEARCH_KEY", searchQuery)
//                .build();
//
//        if (!searchQuery.isEmpty())
//            getRouter().pushController(RouterTransaction.with(
//                    SaleItemsController.newInstance(saleItemBundle))
//                    .pushChangeHandler(new HorizontalChangeHandler())
//                    .popChangeHandler(new HorizontalChangeHandler()));
//        else
//            KeyboardUtils.hideSoftInput(getActivity());
//            getActivity().onBackPressed();
    }

    private void performSearch(String searchQuery) {

        Bundle saleItemBundle = new BundleBuilder(new Bundle())
                .putString("SaleItemsController.KEY_TITLE", searchQuery)
                .putString("SaleItemsController.SEARCH_KEY", searchQuery)
                .build();

        if (!searchQuery.isEmpty())
            getRouter().pushController(RouterTransaction.with(
                    SaleItemsController.newInstance(saleItemBundle))
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //noinspection ConstantConditions
            getActivity().dismissKeyboardShortcutsHelper();
        }

    }


    private List<GetCategoryTreeResponse> updateCategoryChildren(GetCategoryTreeResponse categoryTree){
        GetCategoryTreeResponse getCategoryTreeResponse = new GetCategoryTreeResponse();
        getCategoryTreeResponse.setName("All");
        getCategoryTreeResponse.setKey(categoryTree.getKey());
        getCategoryTreeResponse.setChildren(new ArrayList<>());
        getCategoryTreeResponse.setNodeType("usual");
        getCategoryTreeResponse.setId(categoryTree.getId());

        List<GetCategoryTreeResponse> newList = new ArrayList<>();

        if (categoryTree.getChildren()!=null){
            for (int i=0;i <categoryTree.getChildren().size()+1;i++){

                if (i==0) {
                    newList.add(getCategoryTreeResponse);

                } else{
                    newList.add(categoryTree.getChildren().get(i-1));
                }
            }
        }
        return newList;
    }

    private void onSearchFieldClick(){

        ((MainActivity)getActivity()).getCategoriesRouter().pushController(
                RouterTransaction.with(SearchController.newInstance())
                        .pushChangeHandler(new FadeChangeHandler())
                        .popChangeHandler(new FadeChangeHandler()));

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            searchTapCounter = 0;
        },500);
    }
}
