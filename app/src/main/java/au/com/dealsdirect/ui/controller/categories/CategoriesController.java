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
import android.widget.ImageButton;
import android.widget.TextView;

import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.data.network.model.publicsalescategories.GetPublicSalesCategoriesResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.categories.adapter.CategoriesAdapter;
import au.com.dealsdirect.ui.controller.categories.adapter.SubCategoriesAdapter;
import au.com.dealsdirect.ui.controller.categories.listener.CategoryClickListener;
import au.com.dealsdirect.ui.controller.categories.listener.SubCategoryClickListener;
import au.com.dealsdirect.ui.controller.categories.listener.SubCategoryItemClickListener;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsController;
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

    @Inject
    CategoriesMvpPresenter<CategoriesMvpView> mPresenter;

    @BindView(R.id.categories_recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.sub_categories_recyclerview)
    RecyclerView mSubCategoryRecyclerView;

    @BindView(R.id.partial_toolbar_disabled_search_title)
    TextView mSearchField;

    @BindView(R.id.partial_toolbar_disabled_search_right_option)
    ImageButton mToolbarRightOption;

    public static final String TAG = "CategoriesController";


    private GetCategoryTreeResponse mChosenSubCategoryTreeResponse = new GetCategoryTreeResponse();
    private SubCategoriesAdapter mSubCategoryAdapter;
    private CategoriesAdapter mAdapter;
    private SubCategoryClickListener mSubCategoryClickListener;
    private SubCategoryItemClickListener mSubCategoryItemClickListener;

    private static List<GetCategoryTreeResponse> mCategories;
    private static Map<String, List<GetCategoryTreeResponse>> mCategoryMap = new HashMap<>();

    private int searchTapCounter = 0;

    private MainActivity mActivity;

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
        mActivity = (MainActivity) getActivity();
        mActivity.setDraggableViewPager(true);
        mActivity.setCategoriesRouter(getRouter());
	hideKeyboard();
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
        mToolbarRightOption.setImageDrawable(getResources().getDrawable(R.drawable.ic_double_chevron));
        CategoryClickListener mCategoryClickListener = this;
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
        mChosenSubCategoryTreeResponse = getCategoryTreeResponse;

        String categoryName = getCategoryTreeResponse.getName();

        //noinspection ConstantConditions
        if (categoryName.equals(getActivity().getResources().getString(R.string.shop_category))){

            ArrayList<GetCategoryTreeResponse> emptyChildren = new ArrayList<>();
            mSubCategoryAdapter = new SubCategoriesAdapter(emptyChildren, mPresenter, mSubCategoryClickListener, mSubCategoryItemClickListener, mCategoryMap);
            mSubCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            mSubCategoryRecyclerView.setAdapter(mSubCategoryAdapter);

            GetCategoryTreeResponse shopCategory = new GetCategoryTreeResponse();
            mSubCategoryAdapter.notifyDataSetChanged();
            mAdapter.notifyDataSetChanged();

            assert (getActivity()) != null;
            ((MainActivity)getActivity()).goToSalesFromCategory(shopCategory);

        }else{
            if (mCategories.get(position).getChildren() != null) {
                mSubCategoryAdapter = new SubCategoriesAdapter(
                        (mCategories.get(position).getChildren()), mPresenter, mSubCategoryClickListener, mSubCategoryItemClickListener, mCategoryMap);
                mSubCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                mSubCategoryRecyclerView.setAdapter(mSubCategoryAdapter);


            } else {
                ArrayList<GetCategoryTreeResponse> emptyChildren = new ArrayList<>();
                mSubCategoryAdapter = new SubCategoriesAdapter(emptyChildren, mPresenter, mSubCategoryClickListener, mSubCategoryItemClickListener, mCategoryMap);
                mSubCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                mSubCategoryRecyclerView.setAdapter(mSubCategoryAdapter);
            }
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

        assert (getActivity()) != null;
        mActivity.getMainController().setChosenCategoryItemKey(categoryKey);

        Bundle saleItemBundle = new BundleBuilder(new Bundle())
                .putString("SaleItemsController.KEY_TITLE", categoryName)
                .putString("SaleItemsController.CATEGORY_KEY", categoryKey)
                .putBoolean("SaleItemsController.IS_FROM_CATEGORY", true)
                .build();

        mActivity.getHomeRouter().pushController(RouterTransaction.with(
                SaleItemsController.newInstance(saleItemBundle))
                .tag("SaleItemsController")
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));

        new Handler().postDelayed(()->{
            ((MainActivity)getActivity()).getMainController().goToShops();
            mSubCategoryAdapter.notifyDataSetChanged();
        },400);

        mActivity.setIsFromCategories(true);

    }

    @SuppressWarnings("ConstantConditions")
    @OnClick(R.id.partial_toolbar_disabled_search_right_option)
    void onSearchOptionClicked() {
        ((MainActivity)getActivity()).goToSalesFromCategory(mChosenSubCategoryTreeResponse);

    }

    @OnClick(R.id.partial_toolbar_disabled_search_title)
    void onSearchTextViewClick(){
        searchTapCounter += 1;
        if (searchTapCounter == 1)
            onSearchFieldClick();
    }

    private void performSearch(String searchQuery) {

        Bundle saleItemBundle = new BundleBuilder(new Bundle())
                .putString("SaleItemsController.KEY_TITLE", searchQuery)
                .putString("SaleItemsController.SEARCH_KEY", searchQuery)
                .build();

        if (!searchQuery.isEmpty())
            getRouter().pushController(RouterTransaction.with(
                    SaleItemsController.newInstance(saleItemBundle))
                    .tag("SaleItemsController")
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
        if ((getActivity()) != null) {
            ((MainActivity)getActivity()).goToSaleItemsFromSearchCategory();
        }
//        assert (getActivity()) != null;
//        ((MainActivity)getActivity()).getCategoriesRouter().pushController(
//                RouterTransaction.with(SearchController.newInstance())
//                        .pushChangeHandler(new FadeChangeHandler())
//                        .popChangeHandler(new FadeChangeHandler()));
//
        Handler handler = new Handler();
        handler.postDelayed(() -> searchTapCounter = 0,500);
    }

    public void updateSubCategoryItemState(){
        mSubCategoryAdapter.notifyDataSetChanged();
    }
}
