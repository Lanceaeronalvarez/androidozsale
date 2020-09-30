package au.com.dealsdirect.ui.controller.categories;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.service.datacollection.core.DataCollector;
import au.com.dealsdirect.service.datacollection.enums.Events;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.categories.adapter.CategoriesAdapter;
import au.com.dealsdirect.ui.controller.categories.adapter.SubCategoriesAdapter;
import au.com.dealsdirect.ui.controller.categories.listener.CategoryClickListener;
import au.com.dealsdirect.ui.controller.categories.listener.SubCategoryClickListener;
import au.com.dealsdirect.ui.controller.categories.listener.SubCategoryItemClickListener;
import au.com.dealsdirect.ui.controller.orders.orderdetails.OrderDetailItemDecorator;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsController;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import butterknife.BindView;
import butterknife.OnClick;

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

        mPresenter.callGetCategoryTree();

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(DataCollector.EventParameters.APP_CONTEXT, mActivity);
        parameters.put(DataCollector.EventParameters.SCREEN_NAME, CategoriesController.class.getSimpleName());
        DataCollector.logEvent(Events.addToCartJourneyViewProductCategory, parameters);

        setUp(view);
    }

    @Override
    public void onViewDidAppear(Controller previousController) {
        super.onViewDidAppear(previousController);
        if (mHasSavedInstance) {
            if (previousController == null || mCategories == null) {
                mPresenter.callGetCategoryTree();
                mActivity.getMainController().getHomeController().setSavedCurrentItem();
            }
        }
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @OnClick(R.id.no_network_layout)
    public void refreshCategories() {
        mPresenter.callGetCategoryTree();
    }

    @Override
    public void refreshContents() {
        super.refreshContents();
        if (mNoNetworkLayout.getVisibility() == View.VISIBLE) {
            refreshCategories();
        }
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
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
            mRecyclerView.setMotionEventSplittingEnabled(false);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.addItemDecoration(new OrderDetailItemDecorator());

            mSubCategoryAdapter = new SubCategoriesAdapter(mActivity, !mCategories.isEmpty() && mCategories.get(0).getChildren() != null ?
                    mCategories.get(0).getChildren() : new ArrayList<>(), mPresenter,
                    mSubCategoryItemClickListener, this, mCategoryMap);

            mSubCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
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
            mSubCategoryAdapter = new SubCategoriesAdapter(mActivity, (mCategories.get(position).getChildren()), mPresenter, mSubCategoryItemClickListener, this, mCategoryMap);
            mSubCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
            mSubCategoryRecyclerView.setAdapter(mSubCategoryAdapter);
            mSubCategoryAdapter.notifyDataSetChanged();

        } else {
            ArrayList<GetCategoryTreeResponse> emptyChildren = new ArrayList<>();
            mSubCategoryAdapter = new SubCategoriesAdapter(mActivity, emptyChildren, mPresenter, mSubCategoryItemClickListener, this, mCategoryMap);
            mSubCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
            mSubCategoryRecyclerView.setAdapter(mSubCategoryAdapter);
        }

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSubCategoryClicked(int position, GetCategoryTreeResponse getCategoryTreeResponse) {

    }

    @Override
    public void onSubCategoryItemClicked(String categoryID, String categoryName, String categoryKey,
                                         List<SearchChipModel> chipFilters) {

        mActivity.getMainController().setChosenCategoryItemKey(categoryKey);

        SaleItemsController.Parameters.FromCategory parameters = new SaleItemsController.Parameters
                .FromCategory(categoryKey, categoryKey, mCategories, new HashSet<>(chipFilters));

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
        if (getRouter().getBackstackSize() == 1) {
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
        Log.d("deeplinkers", "get category key = " + categoryId + " , " + mCategoryKeyMap.get(categoryId));
        Log.d("deeplinkers", "get category key = " + categoryId + " , " + mCategoryKeyMap.get("SG9tZT4_PkJlZCAmIEJhdGg_Pj5TaGVldHM="));

        return mCategoryKeyMap.get(categoryId);
    }

}
