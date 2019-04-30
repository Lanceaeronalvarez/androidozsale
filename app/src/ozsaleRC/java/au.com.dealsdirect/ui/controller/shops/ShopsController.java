package au.com.dealsdirect.ui.controller.shops;

import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.mysale.genie.profiler.Profiler;
import com.paginate.Paginate;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.banner.GetBannerRequest;
import au.com.dealsdirect.data.network.model.banner.GetBannerResponse;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.service.datacollection.core.DataCollector;
import au.com.dealsdirect.service.datacollection.enums.Events;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.categories.CategoriesController;
import au.com.dealsdirect.ui.controller.main.MainController;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsController;
import au.com.dealsdirect.ui.controller.shops.adapter.BannersAdapter;
import au.com.dealsdirect.ui.custom.SearchEditText;
import au.com.dealsdirect.ui.custom.transitions.SimpleChangeHandler;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.DialogUtils;
import au.com.dealsdirect.utils.PaginateUtils;
import butterknife.BindView;
import butterknife.OnClick;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

import static au.com.dealsdirect.service.datacollection.core.DataCollector.EventParameters.ClickType.BANNER_CLICK;
import static au.com.dealsdirect.utils.BundleKeys.SALEITEMS_FROM_SHOP_SEARCH;


/**
 * dp Created by Admin on 6/6/17.
 */

public class ShopsController extends BaseController implements ShopsMvpView, PtrHandler, AppBarLayout.OnOffsetChangedListener {

    public static final String TAG = "ShopsController";
    private static final String KEY_CATEGORY_ID = "ShopController.KEY_CATEGORY_ID";
    private static final String KEY_CATEGORY_NAME = "ShopController.KEY_CATEGORY_NAME";
    private static final String KEY_CATEGORY_MAP = "ShopController.KEY_CATEGORY_KEY";
    private static final String KEY_BANNERS_LAYOUT_MANAGER_STATE = "KEY_BANNERS_LAYOUT_MANAGER_STATE";
    private static final String KEY_BANNERS_OFFSET = "KEY_BANNERS_OFFSET";
    private static final String TEXT_ALL = "• All";
    private static final int INITIAL_BANNER_COUNT = 200;


    @Inject
    ShopsMvpPresenter<ShopsMvpView> mPresenter;

    @BindView(R.id.controller_shop_banner_recycler)
    RecyclerView shopsControllerBannerRecyclerView;

    @BindView(R.id.partial_toolbar_search_icon)
    ImageButton shopsControllerSearchView;

    @BindView(R.id.partial_toolbar_hamburger)
    ImageButton mShopsControllerHamburgerView;

    @BindView(R.id.partial_toolbar_logo)
    ImageView mShopsControllerToolbarLogo;

    @BindView(R.id.partial_toolbar_logo_title_view)
    TextView mShopsControllerToolbarTextView;

    @BindView(R.id.partial_toolbar_field_title_edittext)
    SearchEditText mSearchBarEditText;

    @BindView(R.id.controller_sale_items_ptr)
    PtrClassicFrameLayout mShopPtrLayout;

    @BindView(R.id.controller_sale_items_appbar)
    AppBarLayout mShopAppBarLayout;

    private BannersAdapter mBannersAdapter;
    private Paginate.Callbacks mPaginateCallbacks;
    private Paginate mPaginateManager;

    private int page = 0;
    private boolean loadingInProgress = false;
    private boolean hasLoadedAllItems = false;

    private String bannerGroupType = "";
    private int bannerOffset = 0;
    private int bannerLimit = 0;

    private String mCategoryID;
    private String mCategoryName;
    private String mCategoryKey;

    private boolean mIsDeeplink = false;
    private boolean mHasSavedInstance = false;

    private GridLayoutManager mLayoutManager;

    private List<GetCategoryTreeResponse> mPreLoadedCategories = new LinkedList<>();
    private List<GetBannerResponse.Group> sales = new LinkedList<>();
    private Map<String, List<GetCategoryTreeResponse>> mCategoryMap = new HashMap<>();

    private boolean isRefreshShop = false;

    private int mVerticalOffset;
    private boolean mIsRecyclerViewScrollIdle;

    private static final int MAX_BANNERS = 5;
    private static final int MOBILE_BANNER = 1;


    private boolean mIsChangeInProgress = false;

    @Override
    protected void onAttach(@NonNull View view) {
        mPresenter.onAttach(this);
        assert (mActivity) != null;

        /* bug/gen-8065_ozsale-reskin_bugfixing - allow draggable viewpager */
        mActivity.setDraggableViewPager(true);

        mActivity.setShopController(this);
        mShopPtrLayout.setPtrHandler(this);
        mShopAppBarLayout.addOnOffsetChangedListener(this);

        super.onAttach(view);
    }

    public static ShopsController newInstance() {
        return new ShopsController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public ShopsController(Bundle args) {
        super(args);
        mCategoryID = getArgs().getString(KEY_CATEGORY_ID);
        mCategoryName = getArgs().getString(KEY_CATEGORY_NAME);
        mCategoryKey = getArgs().getString(KEY_CATEGORY_MAP);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_shop, container, false);

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);

        return view;
    }

    private void showProductList() {
        Bundle args = new Bundle();
        args.putBoolean(SALEITEMS_FROM_SHOP_SEARCH, true);

        SaleItemsController.Parameters.FromShopSearch parameters = new SaleItemsController.Parameters
                .FromShopSearch(null, null);

        SaleItemsController controller = SaleItemsController.newInstance(parameters);

        getRouter().pushController(RouterTransaction.with(controller)
                .tag(getResources().getString(R.string.sale_items_controller_tag))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        mActivity.getProfiler().setStartLogTime(DataCollector.EventParameters.CustomEventType.CV_SALEBANNERS.getValue());
        setUp(view);
    }

    @Override
    public void onDetach(View view) {
        mShopPtrLayout.setPtrHandler(null);
        mShopAppBarLayout.removeOnOffsetChangedListener(this);
        mIsDeeplink = false;
        hideLoading();
        super.onDetach(view);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        unBindPaginate();
        shopsControllerBannerRecyclerView.clearOnScrollListeners();
        shopsControllerBannerRecyclerView.setAdapter(null);
        super.onDestroyView(view);
    }

    @Override
    public void onViewWillAppear(Controller previousController) {
        super.onViewWillAppear(previousController);
        if (mShopAppBarLayout != null) {
            mShopAppBarLayout.setExpanded(true, true);
        }

        mIsChangeInProgress = true;
        if (mPresenter != null) {
            mPresenter.cancelRequest();
        }
    }

    @Override
    public void onViewWillDisappear(Controller nextController) {
        super.onViewWillDisappear(nextController);
        if (mShopAppBarLayout != null) {
            mShopAppBarLayout.setExpanded(true, true);
        }

        mIsChangeInProgress = true;
        mPresenter.cancelRequest();
    }

    @Override
    public void onViewDidAppear(Controller previousController) {
        super.onViewDidAppear(previousController);
        if (!(previousController instanceof SaleItemsController)
                && !(mActivity.getMainController().getCurrentViewPagerController() instanceof ShopsController)) {
            mPresenter.loadShopsBanner(createBannerRequest(mCategoryID, bannerOffset, bannerLimit));
            mActivity.getMainController().getHomeController().setSavedCurrentItem();
        }

        mIsChangeInProgress = false;
    }

    @Override
    public void onViewDidDisappear(Controller nextController) {
        super.onViewDidDisappear(nextController);

        mIsChangeInProgress = false;
    }

    @Override
    protected void setUp(View view) {

        assert (mActivity) != null;

        bannerLimit = INITIAL_BANNER_COUNT;

        mActivity.setShopController(this);

        mActivity.setDraggableViewPager(true);
        hideKeyboard();

        displayBanners();

        mSearchBarEditText.setFocusable(false);
        mSearchBarEditText.setOnClickListener(view1 -> {
            showProductList();
        });
        mSearchBarEditText.setHint(getResource().getString(R.string.search_tag));

        mPaginateCallbacks = new Paginate.Callbacks() {
            @Override
            public void onLoadMore() {
                // Load next page of data (e.g. network or database)
                page++;
                bannerOffset += INITIAL_BANNER_COUNT;
                refresh();
            }

            @Override
            public boolean isLoading() {
                // Indicate whether new page loading is in progress or not
                return loadingInProgress;
            }

            @Override
            public boolean hasLoadedAllItems() {
                // Indicate whether all data (pages) are loaded or not
                return hasLoadedAllItems;
            }
        };

        shopsControllerBannerRecyclerView.addItemDecoration(new StickyRecyclerHeadersDecoration(mBannersAdapter));
        shopsControllerBannerRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mIsRecyclerViewScrollIdle = newState == 0;
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mShopAppBarLayout != null) {
                    // Snaps search bar to expanded or hidden depending on whether
                    // t is halfway to 0 or 1
                    float t = -mVerticalOffset / (float) mShopAppBarLayout.getHeight();
                    mShopAppBarLayout.setExpanded(t < 0.5, true);
                }
            }
        });

        if (sales.isEmpty() && !mHasSavedInstance) {
            shopsControllerBannerRecyclerView.setVisibility(View.GONE);
        } else {
            shopsControllerBannerRecyclerView.setAdapter(mBannersAdapter);
            mPresenter.loadShopsBanner(createBannerRequest(mCategoryID, bannerOffset, bannerLimit));
            mPaginateManager = PaginateUtils.init(shopsControllerBannerRecyclerView, mPaginateCallbacks);
        }

        if (mPreLoadedCategories.size() == 0) {
            mPresenter.loadCategoryTree();
        }

        setupPtrHeader();
        setRetainViewMode(RetainViewMode.RETAIN_DETACH);

        if (mHasSavedInstance) {
            mPresenter.loadShopsBanner(createBannerRequest(mCategoryID, bannerOffset, bannerLimit));
            mActivity.getMainController().getHomeController().setSavedCurrentItem();
        }
    }

    private void displayBanners() {
        if (mBannersAdapter == null) {
            mBannersAdapter = new BannersAdapter(mActivity, mPresenter, sales);
        } else {
            mBannersAdapter.setupDimensions();
        }

        mLayoutManager = new GridLayoutManager(
                mActivity,
                mBannersAdapter.getNumberOfColumns(),
                GridLayoutManager.VERTICAL,
                false);

        shopsControllerBannerRecyclerView.setLayoutManager(mLayoutManager);
        shopsControllerBannerRecyclerView.setAdapter(mBannersAdapter);
    }

    private void setupPtrHeader() {
        mShopPtrLayout.getHeader().setProgressIcon(getResources().getDrawable(R.drawable.ic_loader_logo));

        mShopPtrLayout.getHeader().setPullProgressbar(getResources().getDrawable(R.drawable.bg_progress_bar));

        mShopPtrLayout.getHeader().setProgressBar(ColorStateList.valueOf(getResources().getColor(R.color.progress_loader_stroke)));

    }

    @Override
    public void refresh() {
        loadingInProgress = true;

        if (mIsDeeplink) {
            mPresenter.loadShopsBanner(createDeepLinkBannerRequest(mCategoryID, 0, 0));
        } else {
            mPresenter.loadShopsBanner(createBannerRequest(mCategoryID, bannerOffset, bannerLimit));
        }
    }

    @Override
    public void unBindPaginate() {
        if (mPaginateManager != null) {
            mPaginateManager.unbind();
        }
    }

    @Override
    public void onBannerClicked(String saleId, String bannerTitle, String bannerId, int position, String imageUrl, String endDate, boolean isAvailable) {

        SaleItemsController.Parameters.FromBannerClick parameters = new SaleItemsController.Parameters
                .FromBannerClick(bannerTitle, saleId, bannerId, imageUrl, endDate, position);

        SaleItemsController controller = SaleItemsController.newInstance(parameters);

        HashMap<String, Object> eventParameters = new HashMap<>();
        eventParameters.put(DataCollector.EventParameters.TYPE, BANNER_CLICK);
        eventParameters.put(DataCollector.EventParameters.ITEM_ARRAY_POSITION, position);
        eventParameters.put(DataCollector.EventParameters.APP_CONTEXT, mActivity);
        eventParameters.put(DataCollector.EventParameters.SCREEN_NAME, ShopsController.class.getSimpleName());
        DataCollector.logEvent(Events.clicksEvent, eventParameters);

        List<String> names = new ArrayList<>();
        names.add(bannerId + position);
        if (isAvailable) {
            getRouter().pushController(RouterTransaction.with(controller)
                    .tag(mActivity.getString(R.string.sale_items_controller_tag))
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler()));
        } else {

            // Check if sale is available
            //TODO: Need computation for date and time when sale response is cached
            if (isAvailable) {
                getRouter().pushController(RouterTransaction.with(controller)
                        .tag(mActivity.getString(R.string.sale_items_controller_tag))
                        .pushChangeHandler(new HorizontalChangeHandler())
                        .popChangeHandler(new HorizontalChangeHandler()));
            } else {
                DialogUtils.showYesDialog(mActivity, "", "Sale is currently closed", "OK", (dialogInterface, i) -> dialogInterface.dismiss());
            }
        }
    }

    @Override
    public boolean isChangeInProgress() {
        return mIsChangeInProgress;
    }

    @OnClick(R.id.partial_toolbar_hamburger)
    void onClickHamburger() {
        mPresenter.cancelRequest();
        assert (mActivity) != null;
        mActivity.setRootViewpagerItem(0);
    }

    @Override
    public boolean handleBack() {
        mPresenter.cancelRequest();
        return false;
    }

    @OnClick(R.id.partial_toolbar_logo)
    void onClickLogo() {
        shopsControllerBannerRecyclerView.smoothScrollToPosition(0);
        shopsControllerBannerRecyclerView.postDelayed(() -> shopsControllerBannerRecyclerView.scrollToPosition(0), 500);
    }

    @SuppressWarnings({"ConstantConditions", "deprecation"})
    @OnClick(R.id.partial_toolbar_search_icon)
    void onSearchClick() {

        SaleItemsController.Parameters.FromShopSearch parameters = new SaleItemsController.Parameters
                .FromShopSearch(null, null);

        SaleItemsController controller = SaleItemsController.newInstance(parameters);

        getRouter().pushController(RouterTransaction.with(controller)
                .tag(mActivity.getString(R.string.sale_items_controller_tag))
                .pushChangeHandler(new SimpleChangeHandler())
                .popChangeHandler(new FadeChangeHandler()));
    }

    @Override
    public void showShopBanners(GetBannerResponse getBannerResponses, String categoryID) {

        mActivity.getMainController().getHomeController().setViewpagerScreen(MainController.SHOP_INDEX);

        if (mCategoryID != null && categoryID != null && !mCategoryID.equals(categoryID)) {
            return;
        }

        hasLoadedAllItems = false;
        mActivity.getProfiler().setEndLogTime(DataCollector.EventParameters.CustomEventType.CV_SALEBANNERS.getValue());
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(DataCollector.EventParameters.MILLISECONDS,
                Profiler.getTotalTime(DataCollector.EventParameters.CustomEventType.CV_SALEBANNERS.getValue()));
        parameters.put(DataCollector.EventParameters.APP_CONTEXT, mActivity);
        parameters.put(DataCollector.EventParameters.SCREEN_NAME, ShopsController.class.getSimpleName());
        DataCollector.logEvent(Events.CVSaleBanners, parameters);

        shopsControllerBannerRecyclerView.setVisibility(View.VISIBLE);

        loadingInProgress = false;

        sales = getBannerResponses.getGroups();

        if (page == 0 || isRefreshShop) {
            mBannersAdapter.replace(sales);
            if (mPaginateManager != null) {
                mPaginateManager.unbind();
            }
            mPaginateManager = PaginateUtils.init(shopsControllerBannerRecyclerView, mPaginateCallbacks);
            isRefreshShop = false;
        } else {
            mBannersAdapter.addAll(sales);

            if (sales.isEmpty()) {
                hasLoadedAllItems = true;
                mPaginateManager.setHasMoreDataToLoad(false);
            }
        }

        onRefreshEnd();
    }

    @Override
    public void refreshContents() {
        super.refreshContents();
        if (isViewAttached() &&
                sales.isEmpty() &&
                !mHasSavedInstance &&
                shopsControllerBannerRecyclerView != null) {
            shopsControllerBannerRecyclerView.setVisibility(View.GONE);
            mPresenter.loadShopsBanner(createBannerRequest(mCategoryID, bannerOffset, INITIAL_BANNER_COUNT));
        }
        resetBannerLayout();
        mShopAppBarLayout.setExpanded(true, true);
    }

    @Override
    public void storeCategories(List<GetCategoryTreeResponse> categories) {
        mPreLoadedCategories = categories;
        createCategoryMap(mPreLoadedCategories);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CATEGORY_ID, mCategoryID);
        outState.putString(KEY_CATEGORY_NAME, mCategoryName);
        outState.putString(KEY_CATEGORY_MAP, mCategoryKey);
        outState.putBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE, true);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCategoryID = savedInstanceState.getString(KEY_CATEGORY_ID);
        mCategoryName = savedInstanceState.getString(KEY_CATEGORY_NAME);
        mCategoryKey = savedInstanceState.getString(KEY_CATEGORY_MAP);
        mHasSavedInstance = savedInstanceState.getBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE);
    }


    private Animation inFromRightAnimation() {

        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(200);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }


    private Animation outToRightAnimation() {
        Animation outtoRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoRight.setDuration(200);
        outtoRight.setInterpolator(new AccelerateInterpolator());
        return outtoRight;
    }


    @Override
    public void onError(String message) {
        super.onError(message);

        bannerOffset -= INITIAL_BANNER_COUNT;
        page--;

        loadingInProgress = false;
        if (mBannersAdapter != null) {
            mBannersAdapter.notifyDataSetChanged();
        }
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

        mPreLoadedCategories = fillCategoryContent();
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
        return mCategoryMap.get("shop");
    }

    public void goToItemsFromCategories(Bundle bundle) {
        //noinspection ConstantConditions
        getRouter().pushController(RouterTransaction.with(
                new SaleItemsController(bundle))
                .tag(getResources().getString(R.string.sale_items_controller_tag))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    public void goToSalesFromCategories(GetCategoryTreeResponse getCategoryTreeResponse) {
        resetShopsBanners(getCategoryTreeResponse.getId());

        if (getCategoryTreeResponse.getKey() != null) {
            mPresenter.loadShopsBanner(createBannerRequest(getCategoryTreeResponse.getId(), bannerOffset, bannerLimit), false);
            if (mShopsControllerToolbarLogo != null) {
                mShopsControllerToolbarLogo.setVisibility(View.GONE);
            }
            mShopsControllerToolbarTextView.setVisibility(View.VISIBLE);
            mShopsControllerHamburgerView.setImageDrawable(mActivity.getDrawable(R.drawable.ic_pink_chevron));
            mShopsControllerToolbarTextView.setText(getCategoryParentKey(getCategoryTreeResponse.getKey()));
            shopsControllerSearchView.setVisibility(View.INVISIBLE);
            mActivity.setIsFromCategories(true);
        } else {
            assert (mActivity) != null;
            mActivity.setIsFromCategories(false);
            showLogoHeader();
            mPresenter.loadShopsBanner(createBannerRequest(getCategoryTreeResponse.getId(), bannerOffset, bannerLimit), false);
        }
    }

    private void resetShopsBanners(String categoryID) {
        mPresenter.onAttach(this);
        hasLoadedAllItems = true;

        if (shopsControllerBannerRecyclerView != null) {
            shopsControllerBannerRecyclerView.setVisibility(View.GONE);
        }

        //reset for values for Get_sales API call
        page = 0;
        bannerOffset = 0;
        mCategoryID = categoryID;

        //reset adapter
        sales.clear();
        mBannersAdapter.clear();
        mLayoutManager.scrollToPosition(0);
    }
//
//    @SuppressWarnings({"deprecation", "ConstantConditions"})
//    public void showSearchToolbar() {
//        if (shopsControllerSearchView != null)
//            shopsControllerSearchView.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));
//
//        mShopsControllerHamburgerView.animate().rotation(-90).setDuration(200).start();
//
//        //noinspection ConstantConditions
//        item = (RelativeLayout) getToolbar();
//
//        //noinspection ConstantConditions
//        child = mActivity.getLayoutInflater().inflate(R.layout.partial_toolbar_search, null);
//        item.addView(child);
//
//        child.setBackgroundColor(getResources().getColor(R.color.toolbar_active_skin));
//        rightOption = (ImageView) child.findViewById(R.id.partial_toolbar_search_right_option);
//        rightOption.setBackgroundColor(getResources().getColor(R.color.toolbar_active_skin));
//
//        EditText searchField = (EditText) child.findViewById(R.id.partial_toolbar_search_field);
//        searchField.setActivated(true);
//        searchField.setFocusable(true);
//
////        if (searchField.requestFocus()) {
////            KeyboardUtils.showSoftInput(searchField, mActivity);
////        }
//
//        //noinspection deprecation
//        rightOption.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));
//        rightOption.animate().rotation(360).setDuration(200).start();
//
//        searchField.setOnEditorActionListener((v, actionId, event) -> {
//            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                performSearch(searchField.getText().toString());
//                return true;
//            }
//            return false;
//        });

//        mShopsControllerToolbarLogo.setVisibility(View.GONE);
//    }

//    public void hideSearchToolbar() {
//        child.startAnimation(outToRightAnimation());
//        item.removeView(child);
//
//        //noinspection deprecation,ConstantConditions
//        shopsControllerSearchView.setImageDrawable(
//                getResources().getDrawable(R.drawable.ic_search));
//        rightOption.animate().rotation(-360).setDuration(200).start();
//        mShopsControllerHamburgerView.animate().rotation(0).setDuration(200).start();
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            //noinspection ConstantConditions
//            mActivity.dismissKeyboardShortcutsHelper();
//        }
//
//        Handler handler = new Handler();
//        handler.postDelayed(() -> {
//                    mShopsControllerToolbarLogo.setVisibility(View.VISIBLE);
//                }, 300);
//    }

    public String getCategoryParentKey(String saleCategoryKey) {
        return getBoolean(R.bool.is_category_all_enabled) ? saleCategoryKey + TEXT_ALL : saleCategoryKey;
    }

    public void loadShopBanners() {
        if (isAttached()) {
            showLogoHeader();
        }
        mPresenter.loadShopsBanner(createBannerRequest("", 0, 0));
    }

    private void showLogoHeader() {
        mShopsControllerToolbarLogo.setVisibility(View.VISIBLE);
        mShopsControllerHamburgerView.setImageDrawable(mActivity.getDrawable(R.drawable.ic_action_menu));
        mShopsControllerToolbarTextView.setVisibility(View.GONE);
        shopsControllerSearchView.setVisibility(View.VISIBLE);
    }

    public void goToSaleItemsFromCategorySearch() {

        SaleItemsController.Parameters.FromCategory parameters = new SaleItemsController.Parameters
                .FromCategory(null, null, null);

        SaleItemsController controller = SaleItemsController.newInstance(parameters);

        getRouter().pushController(RouterTransaction.with(controller)
                .tag(getResources().getString(R.string.sale_items_controller_tag))
                .pushChangeHandler(new SimpleChangeHandler())
                .popChangeHandler(new FadeChangeHandler()));

    }

    @Override
    public void onRefreshStart() {
        super.onRefreshStart();
        shopsControllerBannerRecyclerView.setVisibility(View.GONE);
        isRefreshShop = true;
        bannerOffset = 0;

        if (mIsDeeplink) {
            mPresenter.loadShopsBanner(createDeepLinkBannerRequest(mCategoryID, bannerOffset, bannerLimit), true);
        } else {
            mPresenter.loadShopsBanner(createBannerRequest(mCategoryID, bannerOffset, bannerLimit), true);
        }
    }

    private GetBannerRequest createBannerRequest(String categoryId, int bannerOffset, int bannerLimit) {
        String lastBannerType = mBannersAdapter.getItemCount() > 0 ? mBannersAdapter.getLastGroupType() : "";

        //start from 0 offset when bannerGroupType changes
        if (!lastBannerType.equals("") && !lastBannerType.equals(bannerGroupType) && !bannerGroupType.equals("")) {
            bannerOffset = mBannersAdapter.getOffset();
        }
        bannerGroupType = lastBannerType;

        GetBannerRequest getBannerRequest = new GetBannerRequest();
        if (!bannerGroupType.equals("")) {
            getBannerRequest.setOffset(String.valueOf(bannerOffset));
            getBannerRequest.setLastGroupType(bannerGroupType);
        }
        getBannerRequest.setLimit(String.valueOf(bannerLimit));

        if (categoryId != null && !categoryId.isEmpty()) {
            getBannerRequest.setCategory(categoryId);
            getBannerRequest.setCategoryId(categoryId);
        }

        hasLoadedAllItems = true;

        return getBannerRequest;
    }

    private GetBannerRequest createDeepLinkBannerRequest(String saleCategoryId, int bannerOffset, int bannerLimit) {

        int lastVisiblePos = mLayoutManager.findLastVisibleItemPosition();
        GetBannerResponse.Banner lastVisibleBanner = mBannersAdapter.getItem(lastVisiblePos);
        bannerGroupType = lastVisibleBanner != null ? lastVisibleBanner.getGroup().getType() : "";

        GetBannerRequest getBannerRequest = new GetBannerRequest();
        if (!bannerGroupType.equals("")) {
            getBannerRequest.setOffset(String.valueOf(bannerOffset));
            getBannerRequest.setLastGroupType(bannerGroupType);
        }
        getBannerRequest.setLimit(String.valueOf(bannerLimit));

        if (saleCategoryId != null && !saleCategoryId.isEmpty()) {
            getBannerRequest.setCategory(saleCategoryId);
            getBannerRequest.setCategoryId(saleCategoryId);
        }

        return getBannerRequest;
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return mIsRecyclerViewScrollIdle && mVerticalOffset == 0 && PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        hasLoadedAllItems = true;
        resetShopsBanners(mCategoryID);

        if (mIsDeeplink) {
            mPresenter.loadShopsBanner(createDeepLinkBannerRequest(mCategoryID, 0, 0));
        } else {
            mPresenter.loadShopsBanner(createBannerRequest(mCategoryID, bannerOffset, bannerLimit));
        }
    }

    @Override
    public void onRefreshEnd() {
        super.onRefreshEnd();
        if (mShopPtrLayout != null) {
            mShopPtrLayout.setLastUpdateTimeRelateObject(this);
            mShopPtrLayout.refreshComplete();
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        this.mVerticalOffset = verticalOffset;
    }

    /*
     * bug/gen-7818-landscape - update layoutmanager on orientation change
     *
     */
    @Override
    public void onOrientationChanged(Configuration newConfiguration) {
        super.onOrientationChanged(newConfiguration);
        resetBannerLayout();
    }

    private void resetBannerLayout() {
        if (mBannersAdapter != null && shopsControllerBannerRecyclerView != null && mLayoutManager != null) {
            int currentScrollPosition = mLayoutManager.findFirstVisibleItemPosition();
            displayBanners();
            mLayoutManager.scrollToPosition(currentScrollPosition);
        }
    }

    /* Deep Link Sales */
    public void goToSales(String categoryKey, String categoryId) {

        mIsDeeplink = true;
        if (categoryKey != null) {
            mCategoryName = categoryKey;
            mCategoryID = categoryId;

            if (mIsDeeplink) {
                mPresenter.loadShopsBanner(createDeepLinkBannerRequest(categoryId, 0, 0));
            }

            mShopsControllerToolbarTextView.setVisibility(View.VISIBLE);
            mShopsControllerToolbarTextView.setText(getCategoryParentKey(categoryKey));
            mShopsControllerToolbarLogo.setVisibility(View.GONE);
            mShopsControllerHamburgerView.setImageDrawable(mActivity.getDrawable(R.drawable.ic_pink_chevron));
            shopsControllerSearchView.setVisibility(View.INVISIBLE);

        } else {

            assert (mActivity) != null;
            loadShopBanners();
        }
    }


    public void goToCategoryLink(String categoryKey, String categoryId) {

        if (categoryKey != null) {

            CategoriesController categoriesController = mActivity.getCategoriesController();
            String categoryMapKey = categoriesController.getCategoryKey(categoryId);

            mCategoryKey = categoryMapKey;
            mCategoryID = categoryMapKey;

            SaleItemsController.Parameters.FromCategoryDeepLink parameters = new SaleItemsController
                    .Parameters.FromCategoryDeepLink(categoryKey, categoryMapKey);

            SaleItemsController controller = SaleItemsController.newInstance(parameters);

            getRouter().pushController(RouterTransaction.with(controller)
                    .tag(getResources().getString(R.string.sale_items_controller_tag))
                    .pushChangeHandler(new SimpleChangeHandler())
                    .popChangeHandler(new FadeChangeHandler()));

        } else {

            assert (mActivity) != null;
            loadShopBanners();
        }
    }

    public void refreshFromLogout() {
        bannerGroupType = "";
        resetShopsBanners("");
        mPresenter.loadShopsBanner(createBannerRequest("", 0, bannerLimit), true);
    }

}
