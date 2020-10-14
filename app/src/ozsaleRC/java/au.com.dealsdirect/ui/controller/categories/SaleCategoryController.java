package au.com.dealsdirect.ui.controller.categories;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.service.datacollection.core.DataCollector;
import au.com.dealsdirect.service.datacollection.enums.Events;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.categories.adapter.SaleCategoryAdapter;
import au.com.dealsdirect.ui.controller.categories.adapter.SaleCategoryAdapter.SaleCategoryViewHolder;
import au.com.dealsdirect.ui.controller.categories.adapter.SubCategoryItemsAdapter;
import au.com.dealsdirect.ui.controller.categories.adapter.SubSaleCategoryAdapter;
import au.com.dealsdirect.ui.controller.categories.adapter.SubSaleCategoryAdapter.SubCategoriesViewHolder;
import au.com.dealsdirect.ui.controller.categories.listener.SaleCategoryClickListener;
import au.com.dealsdirect.ui.controller.categories.listener.SubCategoryItemClickListener;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsController;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;

/**
 * Created by MTC on 2019-12-05.
 */
public class SaleCategoryController extends BaseController
        implements CategoriesMvpView, SaleCategoryClickListener, SubCategoryItemClickListener {

    @Inject
    CategoriesMvpPresenter<CategoriesMvpView> mPresenter;

    @BindView(R.id.controller_salecategory_recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.partial_toolbar_title)
    TextView mToolbarTitle;

    @BindView(R.id.partial_toolbar_left_view)
    TextView mToolbarLeftButton;

    @BindView(R.id.partial_toolbar_right_view)
    ImageButton mToolbarRightButton;

    @BindView(R.id.no_network_layout)
    LinearLayout mNoNetworkLayout;

    private SubCategoryItemClickListener mSubCategoryItemClickListener;
    private SaleCategoryAdapter mCategoryAdapter;
    private List<GetCategoryTreeResponse> mCategories;
    private Map<String, List<GetCategoryTreeResponse>> mCategoryMap = new HashMap<>();
    public Map<String, String> mCategoryKeyMap = new HashMap<>();

    public static SaleCategoryController newInstance() {
        return new SaleCategoryController(
                new BundleBuilder(new Bundle())
                        .build());
    }


    public SaleCategoryController(Bundle args) {
        super(args);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        mPresenter.onAttach(this);
    }

    @Override
    protected void setUp(View view) {
        mToolbarLeftButton.setVisibility(View.INVISIBLE);
        mToolbarRightButton.setVisibility(View.INVISIBLE);
        mToolbarTitle.setText(mActivity.getResources().getString(R.string.browse));
        mSubCategoryItemClickListener = this;
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_salecategories, container, false);
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
        mActivity.setSaleCategoryController(this);
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
        if (previousController == null) {
            mPresenter.callGetCategoryTree();
            mActivity.getMainController().getHomeController().setSavedCurrentItem();
        }
    }

    @Override
    public void showCategories(List<GetCategoryTreeResponse> categories) {
        mCategories = categories;

        List<GetCategoryTreeResponse> toBeRemoved = new LinkedList<>();
        for (GetCategoryTreeResponse response : categories) {
            if (response.getChildren() == null || response.getChildren().isEmpty()) {
                toBeRemoved.add(response);
            }
        }
        mCategories.removeAll(toBeRemoved);

        addToMap(mCategories);
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

    @Override
    public void hideNoNetworklayout() {

    }

    private void setupCategories() {
        if (mCategories != null) {

            mCategoryAdapter = new SaleCategoryAdapter(mActivity, mCategories, mPresenter, this, mCategoryMap,
                    mSubCategoryItemClickListener);

            mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
            mRecyclerView.setAdapter(mCategoryAdapter);
            mRecyclerView.setMotionEventSplittingEnabled(false);
            mCategoryAdapter.notifyDataSetChanged();
        }
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

    @Override
    public void onCategoryClicked(int position, SaleCategoryViewHolder saleCategoryViewHolder, GetCategoryTreeResponse getCategoryTreeResponse) {
        if (!isViewAttached() || mRecyclerView == null) {
            return;
        }

        //noinspection ConstantConditions
        if (getCategoryTreeResponse != null && getCategoryTreeResponse.getChildren() != null) {
            SubSaleCategoryAdapter subCategoryAdapter = new SubSaleCategoryAdapter(mActivity, (getCategoryTreeResponse.getChildren()), mSubCategoryItemClickListener, this, mCategoryMap);
            subCategoryAdapter.setParentPosition(position);

            if (saleCategoryViewHolder != null) {
                saleCategoryViewHolder.subCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
                saleCategoryViewHolder.subCategoryRecyclerView.setAdapter(subCategoryAdapter);
            }

            smoothScrollIntoFullViewAfterLayoutChange(saleCategoryViewHolder.itemView);
        } else if (mActivity.getResources().getBoolean(R.bool.should_use_old_category_layout)) { //should only display blank screen on old layout when response is empty
            ArrayList<GetCategoryTreeResponse> emptyChildren = new ArrayList<>();
            SubSaleCategoryAdapter mSubCategoryAdapter = new SubSaleCategoryAdapter(mActivity, emptyChildren, mSubCategoryItemClickListener, this, mCategoryMap);
            mSubCategoryAdapter.setParentPosition(position);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
            mRecyclerView.setAdapter(mSubCategoryAdapter);
        }
        mCategoryAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSubCategoryClicked(int position, SubCategoriesViewHolder subCategoriesViewHolder, GetCategoryTreeResponse getCategoryTreeResponse) {
        if (!isViewAttached() || mRecyclerView == null || mCategoryAdapter == null) {
            return;
        }

        List<GetCategoryTreeResponse> subCategoryItems = mCategoryMap.get(getCategoryTreeResponse.getKey());
        if (subCategoryItems == null) {
            return;
        }
        SubCategoryItemsAdapter mSubCategoryItemsAdapter = new SubCategoryItemsAdapter(mActivity, subCategoryItems, mSubCategoryItemClickListener, true,
                getCategoryTreeResponse.getLinkOptions());

        subCategoriesViewHolder.subCategoryItemsRecyclerView.setLayoutManager(new LinearLayoutManager(subCategoriesViewHolder.itemView.getContext(), LinearLayoutManager.VERTICAL, false));
        subCategoriesViewHolder.subCategoryItemsRecyclerView.setAdapter(mSubCategoryItemsAdapter);

        smoothScrollIntoFullViewAfterLayoutChange(subCategoriesViewHolder.itemView);
    }

    private void smoothScrollIntoFullViewAfterLayoutChange(View view) {
        final View.OnLayoutChangeListener layoutChangeListener = new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                smoothScrollIntoFullView(v);
                v.removeOnLayoutChangeListener(this);
            }
        };
        view.addOnLayoutChangeListener(layoutChangeListener);
    }

    private void smoothScrollIntoFullView(View view) {
        if (view == null || !view.isAttachedToWindow() ||
                mRecyclerView == null || !mRecyclerView.isAttachedToWindow()) {
            return;
        }

        int[] pos1 = new int[2];
        view.getLocationOnScreen(pos1);
        int[] pos2 = new int[2];
        mRecyclerView.getLocationOnScreen(pos2);

        int top1 = pos1[1];
        int top2 = pos2[1];

        int height1 = view.getMeasuredHeight();
        int height2 = mRecyclerView.getMeasuredHeight();

        // try to scroll if bottom part will be clipped
        if (height1 + top1 > height2 + top2) {
            // check if scrolling to reveal the bottom edge will clip the top part
            if (top1 - ((height1 + top1) - (height2 + top2)) < top2) {
                // snap top edge to top of scrollview
                mRecyclerView.smoothScrollBy(0, top1 - top2);
            } else {
                // snap bottom edge to bottom of scrollview
                mRecyclerView.smoothScrollBy(0, (height1 + top1) - (height2 + top2));
            }
        }
    }
}
