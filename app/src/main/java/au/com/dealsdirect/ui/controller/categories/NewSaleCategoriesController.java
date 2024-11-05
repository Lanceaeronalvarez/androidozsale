package au.com.dealsdirect.ui.controller.categories;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.service.datacollection.core.DataCollector;
import au.com.dealsdirect.service.datacollection.enums.Events;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.categories.adapter.NewSaleCategoryAdapter;
import au.com.dealsdirect.ui.controller.categories.listener.NewSaleCategoryClickListener;
import au.com.dealsdirect.ui.controller.categories.listener.SubCategoryItemClickListener;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsController;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;
import au.com.dealsdirect.ui.controller.shops.ShopsController;
import au.com.dealsdirect.ui.custom.transitions.SimpleChangeHandler;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.CommonUtils;
import butterknife.BindView;

/**
 * Created by MTC on 2019-12-05.
 */
public class NewSaleCategoriesController extends BaseController
        implements CategoriesMvpView, NewSaleCategoryClickListener {

    @Inject
    CategoriesMvpPresenter<CategoriesMvpView> mPresenter;

    @BindView(R.id.controller_salecategory)
    ViewGroup mLayout;

    @BindView(R.id.controller_salecategory_recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.partial_toolbar_field_logo_left_option)
    ImageButton mToolbarLeftOptionButton;

    @BindView(R.id.partial_toolbar_logo)
    ImageView mToolbarLogo;

    @BindView(R.id.partial_toolbar_logo_title_bold_view)
    TextView mToolbarTextView;

    @BindView(R.id.no_network_layout)
    LinearLayout mNoNetworkLayout;

    @BindView(R.id.controller_salecategory_seeall)
    RelativeLayout mSeeAllButton;

    @BindView(R.id.controller_salecategory_seeall_text)
    TextView mSeeAllButtonText;

    @BindView(R.id.partial_toolbar_search_button)
    ImageButton mToolbarSearchButton;

    private SubCategoryItemClickListener mSubCategoryItemClickListener;
    private NewSaleCategoryAdapter mCategoryAdapter;
    private List<GetCategoryTreeResponse> mCategories;
    private Map<String, List<GetCategoryTreeResponse>> mCategoryMap = new HashMap<>();
    public Map<String, String> mCategoryKeyMap = new HashMap<>();
    private List<GetCategoryTreeResponse> mSubCategories;
    private List<GetCategoryTreeResponse> mSubCategoriesChildren;
    private GetCategoryTreeResponse mAllSubCategory;
    private int mLevel = 0;
    private String mCategoryName = "";
    private String mSubCategoryName = "";
    private int mBasketQuantity = 0;
    private String allText = "All ";

    private String categoryResponseId = "";
    private String categoryResponseKey = "";
    private QueuedShowSaleItems queuedShowSaleItems = null;

    public static NewSaleCategoriesController newInstance() {
        return new NewSaleCategoriesController(
                new BundleBuilder(new Bundle())
                        .build());
    }


    public NewSaleCategoriesController(Bundle args) {
        super(args);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        mPresenter.onAttach(this);
    }

    @Override
    protected void setUp(View view) {
        mToolbarLogo.setVisibility(View.VISIBLE);
        mToolbarSearchButton.setVisibility(View.VISIBLE);
        mToolbarSearchButton.setImageTintList(ColorStateList.valueOf(mActivity.getResources().getColor(R.color.black)));
        mToolbarLeftOptionButton.setVisibility(
                CommonUtils.positionOfControllerInRouterBackstack(getRouter(), this) > 0 ?
                        View.VISIBLE : View.GONE);
        mToolbarTextView.setVisibility(View.GONE);

        mToolbarLeftOptionButton.setOnClickListener(v -> onLeftButtonClicked());
        mToolbarSearchButton.setOnClickListener(v -> onRightButtonClick());
        mSeeAllButton.setOnClickListener(v -> {
            System.out.println("mLevel " + mLevel);
            if (mLevel == 1) {
                ShopsController shopsController = ShopsController.instanceWithCategoryFilter(
                        categoryResponseId, categoryResponseKey
                );
                getRouter().pushController(
                        RouterTransaction.with(shopsController)
                                .popChangeHandler(new HorizontalChangeHandler())
                                .pushChangeHandler(new HorizontalChangeHandler()));
            } else {
                showSaleItems(mAllSubCategory.getKey(), new HashSet<>());
            }
        });
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_new_sale_categories, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        assert (mActivity) != null;
        hideKeyboard();

        mPresenter.callGetCategoryTree();

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(DataCollector.EventParameters.APP_CONTEXT, mActivity);
        parameters.put(DataCollector.EventParameters.SCREEN_NAME, OldCategoriesController.class.getSimpleName());
        DataCollector.logEvent(Events.addToCartJourneyViewProductCategory, parameters);

        setUp(view);
    }

    @Override
    public void onViewDidAppear(Controller previousController) {
        super.onViewDidAppear(previousController);
        if (previousController == null) {
            mPresenter.callGetCategoryTree();
            mActivity.getMainController().setSavedCurrentItem();
        }
        updateRecyclerView();
    }

    @Override
    public void showCategories(List<GetCategoryTreeResponse> categories) {
        mCategories = categories;
        GetCategoryTreeResponse brands = new GetCategoryTreeResponse();
        brands.setName("Brands");
        brands.setNodeType("custom");
        mCategories.add(brands);

        List<GetCategoryTreeResponse> urlNodeTypes = new ArrayList<>();
        List<GetCategoryTreeResponse> usualUndefinedNodeTypes = new ArrayList<>();
        List<GetCategoryTreeResponse> fixedNodeTypes = new ArrayList<>();
        List<GetCategoryTreeResponse> customNodeTypes = new ArrayList<>();

        for (GetCategoryTreeResponse category : mCategories) {
            if (category.getNodeType().equals("url")) {
                urlNodeTypes.add(category);
            } else if (category.getNodeType().equals("usual") || category.getNodeType().equals("undefined")) {
                usualUndefinedNodeTypes.add(category);
            } else if (category.getNodeType().equals("fixed")) {
                fixedNodeTypes.add(category);
            } else {
                customNodeTypes.add(category);
            }
        }

        mCategories.clear();
        mCategories.addAll(urlNodeTypes);
        mCategories.addAll(usualUndefinedNodeTypes);
        mCategories.addAll(customNodeTypes);
        mCategories.addAll(fixedNodeTypes);

        if (getBoolean(R.bool.is_gift_card_category_visible)) {
            GetCategoryTreeResponse giftCards = new GetCategoryTreeResponse();
            giftCards.setName("Gift Cards");
            giftCards.setNodeType("custom");
            mCategories.add(giftCards);
        }


        setupCategories();
    }

    private GetCategoryTreeResponse createAllFromCategory(GetCategoryTreeResponse categoryTree) {
        GetCategoryTreeResponse getCategoryTreeResponse = new GetCategoryTreeResponse();
        getCategoryTreeResponse.setName(mActivity.getString(R.string.category_all));
        getCategoryTreeResponse.setKey(categoryTree.getKey());
        getCategoryTreeResponse.setChildren(new ArrayList<>());
        getCategoryTreeResponse.setNodeType("usual");
        getCategoryTreeResponse.setId(categoryTree.getId());
        return getCategoryTreeResponse;
    }

    private List<GetCategoryTreeResponse> includeAllInChildren(GetCategoryTreeResponse parent) {
        if (parent == null || parent.getChildren() == null) {
            return new LinkedList<>();
        }

        final LinkedList<GetCategoryTreeResponse> children = new LinkedList<>(parent.getChildren());
        final String nameAll = mActivity.getString(R.string.category_all);
        boolean shouldAdd = true;
        for (GetCategoryTreeResponse child : children) {
            if (child.getName().equalsIgnoreCase(nameAll)) {
                shouldAdd = false;
                break;
            }
        }
        if (shouldAdd) {
            children.add(0, createAllFromCategory(parent));
        }
        return children;
    }

    @Override
    public void hideNoNetworklayout() {

    }

    public void resetLevels() {
        mLevel = 0;
        updateRecyclerView();
    }

    private void setupCategories() {
        if (mCategories != null) {

            mCategoryAdapter = new NewSaleCategoryAdapter(mActivity, mCategories, this, mCategoryMap);

            mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
            mRecyclerView.setAdapter(mCategoryAdapter);
            mRecyclerView.setMotionEventSplittingEnabled(false);
            mCategoryAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCategoryClicked(int position, GetCategoryTreeResponse getCategoryTreeResponse, String categoryName) {
        if (!isViewAttached() || mRecyclerView == null) {
            return;
        }

        mLevel += 1;

        if (mLevel == 1) {
            mCategoryName = categoryName;
            mSubCategories = includeAllInChildren(getCategoryTreeResponse);
            categoryResponseId = getCategoryTreeResponse.getId();
            categoryResponseKey = getCategoryTreeResponse.getKey();
        } else if (mLevel == 2) {
            mSubCategoryName = categoryName;
            mSubCategoriesChildren = includeAllInChildren(getCategoryTreeResponse);
        } else if (mLevel > 2) {
            mLevel = 2;
            showSaleItems(getCategoryTreeResponse.getKey(), new HashSet<>());
        }

        if (mCategoryName.contains("Today's Sales")) {
            mLevel = 0;
            mActivity.getMainController().showHomePage();
            mActivity.getMainController().getShopRouter().popToRoot();
            mActivity.getMainController().setWillGoToShopInsteadOfCategories(true);
        } else if (mCategoryName.contains("Brands") && getCategoryTreeResponse.getKey() == null && getCategoryTreeResponse.getChildren() == null) {
            mLevel = 0;
            mActivity.getMainController().showBrands();
        } else if (mCategoryName.contains("Gift Cards") && getCategoryTreeResponse.getKey() == null && getCategoryTreeResponse.getChildren() == null) {
            mLevel = 0;
            final Router router = mActivity.getCategoriesRouter();
            String storeId = getString(R.string.gift_card_id);

            SaleItemsController.Parameters.FromSeller parameters = new SaleItemsController.Parameters
                    .FromSeller("All Products", storeId);

            SaleItemsController controller = SaleItemsController.newInstance(parameters);

            RouterTransaction routerTransaction = RouterTransaction.with(controller)
                    .tag(getResources().getString(R.string.sale_items_controller_tag))
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler());

            router.pushController(routerTransaction);

        } else if (getCategoryTreeResponse.getChildren() == null) {
            mLevel = 0;
            ShopsController shopsController = ShopsController.instanceWithCategoryFilter(
                    getCategoryTreeResponse.getId(),
                    getCategoryTreeResponse.getKey()
            );
            getRouter().pushController(
                    RouterTransaction.with(shopsController)
                            .popChangeHandler(new HorizontalChangeHandler())
                            .pushChangeHandler(new HorizontalChangeHandler()));
        } else {
            updateRecyclerView(true);
        }
    }

    @Override
    public void onSubCategoryClicked(int position, GetCategoryTreeResponse getCategoryTreeResponse) {
        if (!isViewAttached() || mRecyclerView == null || mCategoryAdapter == null) {
            return;
        }

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

    public void showSaleItems(String categoryId) {
        if (mCategoryKeyMap.isEmpty() && mCategories == null) {
            mActivity.getCategoriesRouter().popToRoot();
            queuedShowSaleItems = () -> showSaleItems(categoryId);
            mPresenter.callGetCategoryTree();
        } else {
            showSaleItems(mCategoryKeyMap.get(categoryId), new HashSet<>());
        }
    }

    private void showSaleItems(String categoryKey, Set<SearchChipModel> chipFilters) {
        final Router router = mActivity.getCategoriesRouter();

        if (categoryKey == null) {
            router.popToRoot();
            return;
        }

        mActivity.getMainController().setChosenCategoryItemKey(categoryKey);

        SaleItemsController.Parameters.FromCategory parameters = new SaleItemsController.Parameters
                .FromCategory(categoryKey, categoryKey, findMainCategoryWithKey(categoryKey), chipFilters);

        SaleItemsController controller = SaleItemsController.newInstance(parameters);

        RouterTransaction routerTransaction = RouterTransaction.with(controller)
                .tag(getResources().getString(R.string.sale_items_controller_tag))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler());

        List<RouterTransaction> backstack = router.getBackstack();
        if (backstack.size() == 1) {
            router.pushController(routerTransaction);
        } else {
            List<RouterTransaction> newBackstack = new LinkedList<>();
            newBackstack.add(backstack.get(0));
            newBackstack.add(routerTransaction);
            router.setBackstack(newBackstack, new SimpleChangeHandler());
        }
        setRetainViewMode(RetainViewMode.RETAIN_DETACH);
    }

    private List<GetCategoryTreeResponse> findMainCategoryWithKey(String key) {
        if (key != null) {
            String[] split = key.split(">>>");
            if (split.length > 0) {
                String mainKey = split[0];
                for (GetCategoryTreeResponse category : mCategories) {
                    if (category.getKey().equals(mainKey)) {
                        return Lists.newArrayList(category);
                    }
                }
            }
        }
        return new ArrayList<>();
    }

    @Override
    public String getCategoryKeyFromId(String id) {
        return mCategoryKeyMap.get(id);
    }

    private interface QueuedShowSaleItems {
        void show();
    }

    @Override
    public boolean handleBack() {
        if (mLevel == 0) {
            if (getRouter().getBackstackSize() > 1) {
                return super.handleBack();
            } else {
                return true;
            }
        } else if (mLevel == 1) {
            onLeftButtonClicked();
            return true;
        } else if (mLevel == 2) {
            onLeftButtonClicked();
            return true;
        }
        return true;
    }

    private void onLeftButtonClicked() {
        if (mLevel == 0) {
            getRouter().handleBack();
        } else {
            mLevel -= 1;
            updateRecyclerView(true);
        }
    }

    private void onRightButtonClick() {
        SaleItemsController.Parameters.FromShopSearch parameters = new SaleItemsController.Parameters
                .FromShopSearch(null, null);

        SaleItemsController controller = SaleItemsController.newInstance(parameters);

        getRouter().pushController(RouterTransaction.with(controller)
                .tag(getResources().getString(R.string.sale_items_controller_tag))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    private void updateRecyclerView(boolean isAnimated) {
        if (!(isViewAttached() && isViewBound())) {
            return;
        }
        if (!isAnimated) {
            updateRecyclerView();
            return;
        }
        CommonUtils.fadeOutView(mLayout, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                updateRecyclerView();
                CommonUtils.fadeInView(mLayout, null);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                updateRecyclerView();
                CommonUtils.fadeInView(mLayout, null);
            }
        });
    }

    private void updateRecyclerView() {
        if (!(isViewAttached() && isViewBound())) {
            return;
        }
        List<GetCategoryTreeResponse> mSubCategoriesUpdated = new ArrayList<>();

        if (mLevel != 0) {
            mToolbarLeftOptionButton.setVisibility(View.VISIBLE);
            mToolbarLeftOptionButton.setImageTintList(ColorStateList.valueOf(mActivity.getResources().getColor(R.color.black)));
            mToolbarTextView.setVisibility(View.VISIBLE);
            mSeeAllButton.setVisibility(View.VISIBLE);
            mToolbarLogo.setVisibility(View.GONE);
            mToolbarSearchButton.setVisibility(View.GONE);
        } else {
            mToolbarSearchButton.setVisibility(View.VISIBLE);
            mToolbarSearchButton.setImageTintList(ColorStateList.valueOf(mActivity.getResources().getColor(R.color.black)));
            mToolbarLogo.setVisibility(View.VISIBLE);
            mToolbarTextView.setVisibility(View.GONE);
            mSeeAllButton.setVisibility(View.GONE);
        }

        if (mLevel == 0) {
            mToolbarLeftOptionButton.setVisibility(
                    CommonUtils.positionOfControllerInRouterBackstack(getRouter(), this) > 0 ?
                            View.VISIBLE : View.GONE);
            mCategoryAdapter = new NewSaleCategoryAdapter(mActivity, mCategories, this, mCategoryMap);
        } else if (mLevel == 1) {
            mToolbarTextView.setText(mCategoryName);
            mSeeAllButtonText.setText(allText + mCategoryName);
            if (!mSubCategories.isEmpty()) {
                mAllSubCategory = mSubCategories.get(0);
                mSubCategoriesUpdated.addAll(mSubCategories);
                mSubCategoriesUpdated.remove(0);
            } else {
                mSeeAllButton.setVisibility(View.GONE);
            }
            mCategoryAdapter = new NewSaleCategoryAdapter(mActivity, mSubCategories, this, mCategoryMap);
        } else if (mLevel == 2) {
            mToolbarTextView.setText(mSubCategoryName);
            mSeeAllButtonText.setText(allText + mSubCategoryName);
            if (!mSubCategoriesChildren.isEmpty()) {
                mAllSubCategory = mSubCategoriesChildren.get(0);
                mSubCategoriesUpdated.addAll(mSubCategoriesChildren);
                mSubCategoriesUpdated.remove(0);
            } else {
                mSeeAllButton.setVisibility(View.GONE);
            }
            mCategoryAdapter = new NewSaleCategoryAdapter(mActivity, mSubCategoriesChildren, this, mCategoryMap);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
        mRecyclerView.setAdapter(mCategoryAdapter);
        mRecyclerView.setMotionEventSplittingEnabled(false);
        mCategoryAdapter.notifyDataSetChanged();
    }
}
