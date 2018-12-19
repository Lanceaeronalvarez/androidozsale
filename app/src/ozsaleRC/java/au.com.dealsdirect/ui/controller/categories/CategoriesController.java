package au.com.dealsdirect.ui.controller.categories;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.ControllerChangeHandler;
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
import au.com.dealsdirect.ui.controller.home.HomeController;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsController;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import butterknife.BindView;

import static au.com.dealsdirect.utils.BundleKeys.CATEGORY_SHOP;

/**
 * dp Created by Admin on 6/6/17.
 */

public class CategoriesController extends BaseController
        implements CategoriesMvpView, CategoryClickListener, SubCategoryItemClickListener {

    public static final String TAG = "CategoriesController";

    public Map<String, String> mCategoryKeyMap = new HashMap<>();

    @Inject
    CategoriesMvpPresenter<CategoriesMvpView> mPresenter;

    @BindView(R.id.controller_categories_content_layout)
    LinearLayout mContentLayout;

    @BindView(R.id.categories_recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.sub_categories_recyclerview)
    RecyclerView mSubCategoryRecyclerView;

    @BindView(R.id.partial_toolbar_title)
    TextView mToolbarTitle;

    @BindView(R.id.partial_toolbar_left_view)
    TextView mToolbarLeftButton;

    @BindView(R.id.partial_toolbar_right_view)
    ImageButton mToolbarRightButton;

    @BindView(R.id.no_network_layout)
    LinearLayout mNoNetworkLayout;

    private GetCategoryTreeResponse mChosenSubCategoryTreeResponse = new GetCategoryTreeResponse();
    private SubCategoriesAdapter mSubCategoryAdapter;
    private CategoriesAdapter mAdapter;
    private SubCategoryClickListener mSubCategoryClickListener;
    private SubCategoryItemClickListener mSubCategoryItemClickListener;

    private List<GetCategoryTreeResponse> mCategories;
    private Map<String, List<GetCategoryTreeResponse>> mCategoryMap = new HashMap<>();

    private int searchTapCounter = 0;
    private boolean mHasSavedInstance;
    private ControllerChangeHandler.ControllerChangeListener newControllerChangeHandler;

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
        mActivity.setDraggableViewPager(false);
        mActivity.setCategoriesRouter(getRouter());
        mActivity.setCategoriesController(this);
        hideKeyboard();
        if (mHasSavedInstance) {
            CategoriesController currentController = this;
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
                        if (from == null || mCategories == null) {
                            mPresenter.callGetCategoryTree();
                            mActivity.getMainController().getHomeController().setSavedCurrentItem();
                        }
                    }
                }
            };
            getRouter().addChangeListener(newControllerChangeHandler);
        }

        mPresenter.callGetCategoryTree();

        mNoNetworkLayout.setOnClickListener((v) -> mPresenter.callGetCategoryTree());

        mActionTracker.addToCartJourneyViewProductCategory();

        setUp(view);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        if (newControllerChangeHandler != null) {
            getRouter().removeChangeListener(newControllerChangeHandler);
            newControllerChangeHandler = null;
        }
        super.onDestroyView(view);
    }

    @Override
    protected void setUp(View view) {

        //noinspection ConstantConditions,deprecation
        mToolbarLeftButton.setVisibility(View.INVISIBLE);
        mToolbarRightButton.setVisibility(View.INVISIBLE);
        mToolbarTitle.setText(mActivity.getResources().getString(R.string.browse));
        mSubCategoryItemClickListener = this;

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

    private void setupCategories() {
        if (mCategories != null) {
            mAdapter = new CategoriesAdapter(mActivity, mCategories, mPresenter, this);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
            mRecyclerView.setMotionEventSplittingEnabled(false);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));

            mSubCategoryAdapter = new SubCategoriesAdapter(mActivity, !mCategories.isEmpty() && mCategories.get(0).getChildren() != null ?
                    mCategories.get(0).getChildren() : new ArrayList<>(), mPresenter,
                    mSubCategoryItemClickListener, mCategoryMap);

            mSubCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
            mSubCategoryRecyclerView.setAdapter(mSubCategoryAdapter);
            mSubCategoryRecyclerView.setMotionEventSplittingEnabled(false);
            mSubCategoryAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onCategoryClicked(int position, GetCategoryTreeResponse getCategoryTreeResponse) {
        mChosenSubCategoryTreeResponse = getCategoryTreeResponse;

        //noinspection ConstantConditions
        if (mCategories != null && mCategories.get(position).getChildren() != null) {
            mSubCategoryAdapter = new SubCategoriesAdapter(mActivity, (mCategories.get(position).getChildren()), mPresenter, mSubCategoryItemClickListener, mCategoryMap);
            mSubCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
            mSubCategoryRecyclerView.setAdapter(mSubCategoryAdapter);
            mSubCategoryAdapter.notifyDataSetChanged();

        } else {
            ArrayList<GetCategoryTreeResponse> emptyChildren = new ArrayList<>();
            mSubCategoryAdapter = new SubCategoriesAdapter(mActivity, emptyChildren, mPresenter, mSubCategoryItemClickListener, mCategoryMap);
            mSubCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
            mSubCategoryRecyclerView.setAdapter(mSubCategoryAdapter);
        }

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSubCategoryItemClicked(String categoryID, String categoryName, String categoryKey) {

        mActivity.getMainController().setChosenCategoryItemKey(categoryKey);

        SaleItemsController.Parameters.FromCategory parameters = new SaleItemsController.Parameters
                .FromCategory(categoryKey, categoryKey, mCategories);

        SaleItemsController controller = SaleItemsController.newInstance(parameters);

        mActivity.getCategoriesRouter().pushController(RouterTransaction.with(controller)
                .tag(getResources().getString(R.string.sale_items_controller_tag))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
        setRetainViewMode(RetainViewMode.RETAIN_DETACH);
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
    public void showNoNetworkLayout() {
        mContentLayout.setVisibility(View.GONE);
        mNoNetworkLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNoNetworklayout() {
        mContentLayout.setVisibility(View.VISIBLE);
        mNoNetworkLayout.setVisibility(View.GONE);
    }

    @Override
    public boolean handleBack() {
        if(getRouter().getBackstackSize() == 1){
            mActivity.getHomeController().goBackToHomePage();
            return true;
        }

        return super.handleBack();
    }

    @Override
    public void showCategories(List<GetCategoryTreeResponse> categories) {
        mCategories = categories;
        //remove SHOP from categories
        GetCategoryTreeResponse shopCategory = new GetCategoryTreeResponse();
        for (GetCategoryTreeResponse response : categories) {
            String key = response.getKey();
            if (key == null || key.isEmpty()) {
                shopCategory = response;
                break;
            }
        }
        if (mCategories.contains(shopCategory)) {
            mCategories.remove(shopCategory);
        }

        addToMap(categories);
        setupCategories();
    }

    private void addToMap(List<GetCategoryTreeResponse> categories) {
        List<GetCategoryTreeResponse> newList;

        for (GetCategoryTreeResponse category : categories) {
            for (GetCategoryTreeResponse subcategory : updateCategoryChildren(category)) {
                newList = updateCategoryChildren(subcategory);
                //add to map if there are children other than "All" subcategory
                if (newList.size() > 1) {
                    addToMap(newList);
                }
                mCategoryMap.put(subcategory.getKey(), newList);
                mCategoryKeyMap.put(subcategory.getId(), subcategory.getKey());
            }
        }
    }


    public String getCategoryKey(String categoryId) {
        Log.d("deeplinkers", "get category key = "+categoryId+ " , "+ mCategoryKeyMap.get(categoryId));
        Log.d("deeplinkers", "get category key = "+categoryId+ " , "+ mCategoryKeyMap.get("SG9tZT4_PkJlZCAmIEJhdGg_Pj5TaGVldHM="));

        return mCategoryKeyMap.get(categoryId);
    }

}
