package au.com.dealsdirect.ui.controller.shops;

import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.Router;
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
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.data.network.model.banner.GetBannerRequest;
import au.com.dealsdirect.data.network.model.banner.GetBannerResponse;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.service.event.ActionTracker;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.categories.CategoriesController;
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

import static au.com.dealsdirect.service.event.ActionTracker.ClickType.BANNER_CLICK;
import static au.com.dealsdirect.utils.BundleKeys.SALEITEMS_BANNER_ID;
import static au.com.dealsdirect.utils.BundleKeys.SALEITEMS_CATEGORY_MAP;
import static au.com.dealsdirect.utils.BundleKeys.SALEITEMS_FROM_BANNER_SEARCH;
import static au.com.dealsdirect.utils.BundleKeys.SALEITEMS_FROM_CATEGORY_DEEPLINK;
import static au.com.dealsdirect.utils.BundleKeys.SALEITEMS_FROM_POSITION;
import static au.com.dealsdirect.utils.BundleKeys.SALEITEMS_FROM_SHOP_SEARCH;
import static au.com.dealsdirect.utils.BundleKeys.SALEITEMS_HEADER_IMAGE;
import static au.com.dealsdirect.utils.BundleKeys.SALEITEMS_SALE_ID;
import static au.com.dealsdirect.utils.BundleKeys.SALEITEMS_TITLE;


/**
 * dp Created by Admin on 6/6/17.
 */

public class ShopsController extends BaseController implements ShopsMvpView, PtrHandler, AppBarLayout.OnOffsetChangedListener {

    public static final String TAG = "ShopsController";
    private static final String KEY_CATEGORY_ID = "ShopController.KEY_CATEGORY_ID";
    private static final String KEY_CATEGORY_NAME = "ShopController.KEY_CATEGORY_NAME";
    private static final String KEY_CATEGORY_MAP = "ShopController.KEY_CATEGORY_KEY";
    private static final String TEXT_ALL = "• All";

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
    private int newBannerCount = 10;
    private int bannerOffset = 0;
    private int bannerLimit = bannerOffset + newBannerCount;

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
    private ControllerChangeHandler.ControllerChangeListener mControllerChangeListener;
    private ControllerChangeHandler.ControllerChangeListener newControllerChangeHandler;

    @Override
    protected void onAttach(@NonNull View view) {
        mPresenter.onAttach(this);
        assert (mActivity) != null;

        /* bug/gen-8065_ozsale-reskin_bugfixing - allow draggable viewpager */
        mActivity.setDraggableViewPager(true);

        mActivity.setShopController(this);
        mShopPtrLayout.setPtrHandler(this);
        mShopAppBarLayout.addOnOffsetChangedListener(this);
        resetBannerLayout();
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
        getRouter().pushController(RouterTransaction.with(new SaleItemsController(args))
                .tag(getResources().getString(R.string.sale_items_controller_tag))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        mActivity.getProfiler().setStartLogTime(ActionTracker.CustomEventType.CV_SALEBANNERS.getValue());
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
        getRouter().removeChangeListener(newControllerChangeHandler);
        newControllerChangeHandler = null;
        super.onDestroyView(view);
    }

    @Override
    protected void setUp(View view) {

        assert (mActivity) != null;

        mActivity.setShopController(this);

        mActivity.setDraggableViewPager(true);
        hideKeyboard();

        displayBanners();

        if (mHasSavedInstance) {
            ShopsController currentController = this;
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
                        mPresenter.loadShopsBanner(createBannerRequest(mCategoryID, bannerOffset, bannerLimit));
                        mActivity.getMainController().getHomeController().setSavedCurrentItem();
                    }
                }
            };
            getRouter().addChangeListener(newControllerChangeHandler);
        }

        mSearchBarEditText.setFocusable(false);
        mSearchBarEditText.setOnClickListener(view1 -> {
            showProductList();
        });
        mSearchBarEditText.setHint(getResource().getString(R.string.search_tag));

        mControllerChangeListener = new ControllerChangeHandler.ControllerChangeListener() {
            @Override
            public void onChangeStarted(@Nullable Controller to, @Nullable Controller from, boolean isPush, @NonNull ViewGroup container, @NonNull ControllerChangeHandler handler) {
                mIsChangeInProgress = true;
            }

            @Override
            public void onChangeCompleted(@Nullable Controller to, @Nullable Controller from, boolean isPush, @NonNull ViewGroup container, @NonNull ControllerChangeHandler handler) {
                mIsChangeInProgress = false;
            }
        };

        getRouter().addChangeListener(mControllerChangeListener);

        mPaginateCallbacks = new Paginate.Callbacks() {
            @Override
            public void onLoadMore() {
                // Load next page of data (e.g. network or database)
                page++;
                bannerOffset += newBannerCount; //load 10 banners every page
                bannerLimit = newBannerCount;
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
            }
        });

        if (sales.isEmpty() && !mHasSavedInstance) {
            shopsControllerBannerRecyclerView.setVisibility(View.GONE);
            mPresenter.loadShopsBanner(createBannerRequest(mCategoryID, bannerOffset, bannerLimit));
        } else {
            shopsControllerBannerRecyclerView.setAdapter(mBannersAdapter);
            mBannersAdapter.replace(sales);
            mPaginateManager = PaginateUtils.init(shopsControllerBannerRecyclerView, mPaginateCallbacks);
        }

        if (mPreLoadedCategories.size() == 0) {
            mPresenter.loadCategoryTree();
        }

        setupPtrHeader();
        setRetainViewMode(RetainViewMode.RETAIN_DETACH);
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
    public void onBannerClicked(
            String saleId,
            String bannerTitle,
            String bannerId,
            int position,
            String imageUrl,
            boolean isAvailable) {

        Bundle args = new BundleBuilder(new Bundle())
                .putString(SALEITEMS_TITLE, bannerTitle)
                .putString(SALEITEMS_SALE_ID, saleId)
                .putString(SALEITEMS_BANNER_ID, bannerId)
                .putString(SALEITEMS_HEADER_IMAGE, imageUrl)
                .putInt(SALEITEMS_FROM_POSITION, position)
                .putString(SALEITEMS_CATEGORY_MAP, null)
                .putBoolean(SALEITEMS_FROM_BANNER_SEARCH, true)
                .build();

        mActionTracker.clicksEvent(BANNER_CLICK, position);

        List<String> names = new ArrayList<>();
        names.add(bannerId + position);
        if (!mPresenter.isAccessAnonymousEnabled() && !mPresenter.isAuthorized()) {
            mActivity.showLoginController(getRouter(), new AuthHandler() {
                @Override
                public void success() {
                    mActivity.callGCMRegisterSubscriber();

                    mActivity.getHomeRouter().pushController(RouterTransaction.with(
                            new SaleItemsController(args))
                            .tag(mActivity.getString(R.string.sale_items_controller_tag))
                            .pushChangeHandler(new HorizontalChangeHandler())
                            .popChangeHandler(new HorizontalChangeHandler()));
                }

                @Override
                public void error() {

                }
            });
        } else {

            // Check if sale is available
            //TODO: Need computation for date and time when sale response is cached
            if (isAvailable) {
                getRouter().pushController(RouterTransaction.with(
                        new SaleItemsController(args))
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
        assert (mActivity) != null;
        mActivity.setRootViewpagerItem(0);
    }

    @OnClick(R.id.partial_toolbar_logo)
    void onClickLogo() {
        shopsControllerBannerRecyclerView.smoothScrollToPosition(0);
        shopsControllerBannerRecyclerView.postDelayed(() -> shopsControllerBannerRecyclerView.scrollToPosition(0), 500);
    }

    @SuppressWarnings({"ConstantConditions", "deprecation"})
    @OnClick(R.id.partial_toolbar_search_icon)
    void onSearchClick() {

        Bundle saleItemBundle = new BundleBuilder(new Bundle())
                .putString("SaleItemsController.KEY_TITLE", "")
                .putString("SaleItemsController.SEARCH_KEY", "")
                .putBoolean("SaleItemsController.FROM_SHOP_SEARCH", true)
                .build();
        Router router = getRouter();

        router.pushController(RouterTransaction.with(
                new SaleItemsController(saleItemBundle))
                .tag(mActivity.getString(R.string.sale_items_controller_tag))
                .pushChangeHandler(new SimpleChangeHandler())
                .popChangeHandler(new FadeChangeHandler()));
    }

    @Override
    public void showShopBanners(GetBannerResponse getBannerResponses) {
        mActivity.getProfiler().setEndLogTime(ActionTracker.CustomEventType.CV_SALEBANNERS.getValue());
        mActionTracker.CVSaleBanners(Profiler.getTotalTime(ActionTracker.CustomEventType.CV_SALEBANNERS.getValue()));

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

        shopsControllerBannerRecyclerView.stopScroll();
        onRefreshEnd();
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

        bannerOffset -= newBannerCount;
        bannerLimit = newBannerCount;
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
        resetShopsBanners(getCategoryTreeResponse);

        if (getCategoryTreeResponse.getKey() != null) {
            mPresenter.loadShopsBanner(createBannerRequest(getCategoryTreeResponse.getId(), bannerOffset, bannerLimit));
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
            mPresenter.loadShopsBanner(createBannerRequest(getCategoryTreeResponse.getId(), bannerOffset, bannerLimit));
        }
    }

    private void resetShopsBanners(GetCategoryTreeResponse getCategoryTreeResponse) {
        mPresenter.onAttach(this);
        if (shopsControllerBannerRecyclerView != null) {
            shopsControllerBannerRecyclerView.setVisibility(View.GONE);
        }

        //reset for values for Get_sales API call
        page = 0;
        bannerOffset = 0;
        bannerLimit = bannerOffset + newBannerCount;
        mCategoryID = getCategoryTreeResponse.getId();

        //reset adapter
        sales = new ArrayList<>();
        mBannersAdapter.getData().clear();
        mLayoutManager.scrollToPosition(0);

        hasLoadedAllItems = false;
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

        Bundle saleItemBundle = new BundleBuilder(new Bundle())
                .putString("SaleItemsController.KEY_TITLE", "")
                .putString("SaleItemsController.SEARCH_KEY", "")
                .putBoolean("SaleItemsController.FROM_CATEGORY_SEARCH", true)
                .build();

        getRouter().pushController(RouterTransaction.with(
                new SaleItemsController(saleItemBundle))
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
            mPresenter.loadShopsBanner(createDeepLinkBannerRequest(mCategoryID, bannerOffset, newBannerCount), true);
        } else {
            mPresenter.loadShopsBanner(createBannerRequest(mCategoryID, bannerOffset, newBannerCount), true);
        }
    }

    private GetBannerRequest createBannerRequest(String categoryId, int bannerOffset, int bannerLimit) {

        int lastVisiblePos = mLayoutManager.findLastVisibleItemPosition();
        GetBannerResponse.Banner lastVisibleBanner = mBannersAdapter.getItem(lastVisiblePos);
        String newGroupType = lastVisibleBanner != null ? lastVisibleBanner.getGroup().getType() : "";
        if (!newGroupType.equals("") && !newGroupType.equals(bannerGroupType)) bannerOffset = 10;
        bannerGroupType = newGroupType;

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

            Bundle saleItemBundle = new BundleBuilder(new Bundle())
                    .putString(SALEITEMS_TITLE, categoryKey)
                    .putString(SALEITEMS_CATEGORY_MAP, categoryMapKey)
                    .putBoolean(SALEITEMS_FROM_CATEGORY_DEEPLINK, true)
                    .build();

            getRouter().pushController(RouterTransaction.with(
                    new SaleItemsController(saleItemBundle))
                    .tag(getResources().getString(R.string.sale_items_controller_tag))
                    .pushChangeHandler(new SimpleChangeHandler())
                    .popChangeHandler(new FadeChangeHandler()));

        } else {

            assert (mActivity) != null;
            loadShopBanners();
        }
    }

}
