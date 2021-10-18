package au.com.dealsdirect.ui.controller.saleitems;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mysale.genie.profiler.Profiler;
import com.mysale.genie.views.custom.recyclerview.CustomGridLayoutManager;
import com.paginate.Paginate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.banner.GetBannerResponse.LinkOptions;
import au.com.dealsdirect.data.network.model.banner.GetSaleBannerDetailsResponse;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.data.network.model.events.CategoryRequest;
import au.com.dealsdirect.data.network.model.events.FeatureUsageEventRequest;
import au.com.dealsdirect.data.network.model.events.SaleEventRequest;
import au.com.dealsdirect.data.network.model.events.SearchEventRequest;
import au.com.dealsdirect.data.network.model.events.WishlistEventRequest;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsRequest;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
import au.com.dealsdirect.data.network.model.saleitems.SaleItemFacet;
import au.com.dealsdirect.data.network.model.saleitems.SaleItemProduct;
import au.com.dealsdirect.data.network.model.sorting.SortingResponse;
import au.com.dealsdirect.service.datacollection.core.DataCollector;
import au.com.dealsdirect.service.datacollection.enums.EventTypeId;
import au.com.dealsdirect.service.datacollection.enums.Events;
import au.com.dealsdirect.service.datacollection.enums.FeatureUsageEventType;
import au.com.dealsdirect.service.datacollection.enums.SearchOperationType;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.saleitemdetails.SaleItemDetailsController;
import au.com.dealsdirect.ui.controller.searchfilter.SearchFilterController;
import au.com.dealsdirect.ui.controller.searchfilter.SearchFilterMvpRepository;
import au.com.dealsdirect.ui.controller.searchfilter.SearchFilterMvpView;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;
import au.com.dealsdirect.ui.custom.AdaptiveTabLayout;
import au.com.dealsdirect.ui.custom.SearchEditText;
import au.com.dealsdirect.ui.custom.transitions.ArcZoomChangeHandler;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.CommonUtils;
import au.com.dealsdirect.utils.DateUtils;
import au.com.dealsdirect.utils.JsonUtils;
import au.com.dealsdirect.utils.KeyboardUtils;
import au.com.dealsdirect.utils.PaginateUtils;
import au.com.dealsdirect.utils.PriceUtils;
import au.com.dealsdirect.utils.StringUtils;
import au.com.dealsdirect.utils.TabLayoutUtils;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTouch;
import butterknife.Optional;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
import static au.com.dealsdirect.data.network.model.events.WishlistEventRequest.WishListInfo.ReferrerValue.HEADER;
import static au.com.dealsdirect.data.network.model.events.WishlistEventRequest.WishListInfo.ReferrerValue.PRODUCT_LIST;
import static au.com.dealsdirect.data.network.model.events.WishlistEventRequest.WishListInfo.ReferrerValue.WISHLIST;
import static au.com.dealsdirect.service.datacollection.core.DataCollector.EventParameters.ClickType.PRODUCT_CLICK;
import static com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS;
import static com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL;

/**
 * dp Created by Admin on 6/8/17.
 */

public class SaleItemsController extends BaseController implements SaleItemsMvpView, AppBarLayout.OnOffsetChangedListener, SearchFilterMvpRepository, OnClickFreeDeliveryListener {

    public enum SourceMode {
        NORMAL,
        WISHLIST
    }

    public enum GridViewMode {
        LARGER_IMAGES,
        MORE_IMAGES
    }

    public SourceMode getSourceMode() {
        return mSourceMode;
    }

    public void setSourceMode(SourceMode sourceMode) {
        mSourceMode = sourceMode;
        resetViewBasedOnSourceMode();
    }

    private SourceMode mSourceMode = SourceMode.NORMAL;

    private void toggleGridViewMode() {
        int featureUsageEventType = -1;

        switch (mGridViewMode) {
            case MORE_IMAGES:
                setGridViewMode(GridViewMode.LARGER_IMAGES);
                featureUsageEventType = FeatureUsageEventType.Search.TOGGLE_GRID_SIZE_TO_LARGE;
                break;
            case LARGER_IMAGES:
                setGridViewMode(GridViewMode.MORE_IMAGES);
                featureUsageEventType = FeatureUsageEventType.Search.TOGGLE_GRID_SIZE_TO_SMALL;
                break;
        }

        if (featureUsageEventType >= 0) {
            FeatureUsageEventRequest featureUsageEventRequest = new FeatureUsageEventRequest();
            featureUsageEventRequest.setEventType(EventTypeId.EVENT_FEATURE_USAGE);
            featureUsageEventRequest.setFeatureInfo(new FeatureUsageEventRequest.FeatureInfo(featureUsageEventType));

            HashMap<String, Object> eventParameters = new HashMap<>();
            eventParameters.put(DataCollector.EventParameters.APP_CONTEXT, mActivity);
            eventParameters.put(DataCollector.EventParameters.SCREEN_NAME, SaleItemsController.class.getSimpleName());
            eventParameters.put(DataCollector.EventParameters.FEATURE_EVENT_REQUEST, featureUsageEventRequest);

            DataCollector.logEvent(Events.FeatureUsageEvent, eventParameters);
        }
    }

    private void setGridViewMode(GridViewMode gridViewMode) {
        if (mGridViewMode == gridViewMode) {
            return;
        }

        mGridViewMode = gridViewMode;

        if (!isViewBound()) {
            return;
        }
        switch (gridViewMode) {
            case MORE_IMAGES:
                mColumnView.setImageDrawable(getResources().getDrawable(R.drawable.ic_3_column_view));

                mColumnCount = mActivity.getResources().getInteger(R.integer.items_max_column_portrait);
                mPresenter.setColumnCount(mColumnCount);

                setAdapterPerColumnChange(mActivity.getResources().getInteger(R.integer.items_max_column_portrait),
                        mActivity.getResources().getInteger(R.integer.items_max_column_landscape));
                break;
            case LARGER_IMAGES:
                mColumnView.setImageDrawable(getResources().getDrawable(R.drawable.ic_2_column_view));

                mColumnCount = mActivity.getResources().getInteger(R.integer.items_min_column_portrait);
                mPresenter.setColumnCount(mColumnCount);

                setAdapterPerColumnChange(mActivity.getResources().getInteger(R.integer.items_min_column_portrait),
                        mActivity.getResources().getInteger(R.integer.items_min_column_landscape));
                break;
        }

        determineWhereToShowAds();
    }

    private GridViewMode mGridViewMode;

    private GridViewModePreferenceHelper mGridViewModePreferenceHelper = new GridViewModePreferenceHelper();

    public abstract static class Parameters {
        private Parameters() {
        }

        public static final class FromBannerClick extends Parameters {
            private String mTitle;
            private String mSaleId;
            private String mBannerId;
            private String mEndDate;
            private String mImageURL;
            private LinkOptions mLinkOptions;
            private Integer mPosition;

            public FromBannerClick(String title,
                                   String saleId,
                                   String bannerId,
                                   String imageURL,
                                   String endDate,
                                   LinkOptions linkOptions,
                                   Integer position) {
                mTitle = title;
                mSaleId = saleId;
                mBannerId = bannerId;
                mImageURL = imageURL;
                mEndDate = endDate;
                mLinkOptions = linkOptions;
                mPosition = position;
            }

            public String getTitle() {
                return mTitle;
            }

            public String getSaleId() {
                return mSaleId;
            }

            public String getBannerId() {
                return mBannerId;
            }

            public String getImageURL() {
                return mImageURL;
            }

            public String getEndDate() {
                return mEndDate;
            }

            public LinkOptions getLinkOptions() {
                return mLinkOptions;
            }

            public Integer getPosition() {
                return mPosition;
            }
        }

        public static final class FromCategoryBannerClick extends Parameters {
            private LinkOptions mLinkOptions;

            public FromCategoryBannerClick(
                    LinkOptions linkOptions) {
                mLinkOptions = linkOptions;
            }

            public LinkOptions getLinkOptions() {
                return mLinkOptions;
            }
        }

        public static final class FromCategory extends Parameters {
            private String mTitle;
            private String mCategoryMap;
            private List<GetCategoryTreeResponse> mCategories;
            private Set<SearchChipModel> mPreSelectedFilter;

            public FromCategory(String title,
                                String categoryMap,
                                List<GetCategoryTreeResponse> categories,
                                Set<SearchChipModel> preSelectedFilter) {
                mTitle = title;
                mCategoryMap = categoryMap;
                mCategories = categories;
                mPreSelectedFilter = preSelectedFilter;
            }

            public String getTitle() {
                return mTitle;
            }

            public String getCategoryMap() {
                return mCategoryMap;
            }

            public List<GetCategoryTreeResponse> getCategories() {
                return mCategories;
            }

            public Set<SearchChipModel> getPreSelectedFilter() {
                return mPreSelectedFilter;
            }
        }


        public static final class FromShopSearch extends Parameters {
            private String mTitle;
            private String mSearchKey;

            public FromShopSearch(String title,
                                  String searchKey) {
                mTitle = title;
                mSearchKey = searchKey;
            }

            public String getTitle() {
                return mTitle;
            }

            public String getSearchKey() {
                return mSearchKey;
            }
        }

        public static final class FromTopBrands extends Parameters {
            private String mBrandName;

            public FromTopBrands(String brandName) {
                mBrandName = brandName;
            }

            public String getBrandName() {
                return mBrandName;
            }
        }

        public static final class FromSaleItemDeepLink extends Parameters {
            private String mBannerTitle;
            private String mSaleId;
            private String mBannerId;

            public FromSaleItemDeepLink(String bannerTitle,
                                        String saleId,
                                        String bannerId) {
                mBannerTitle = bannerTitle;
                mSaleId = saleId;
                mBannerId = bannerId;
            }

            public String getBannerTitle() {
                return mBannerTitle;
            }

            public String getSaleId() {
                return mSaleId;
            }

            public String getBannerId() {
                return mBannerId;
            }
        }

        public static final class FromCategoryDeepLink extends Parameters {
            private String mTitle;
            private String mCategoryMapKey;

            public FromCategoryDeepLink(String title,
                                        String categoryMapKey) {
                mTitle = title;
                mCategoryMapKey = categoryMapKey;
            }

            public String getTitle() {
                return mTitle;
            }

            public String getCategoryMapKey() {
                return mCategoryMapKey;
            }
        }

        public static final class FromLocationFilterHash extends Parameters {
            private String mLocationFilterHash;

            public FromLocationFilterHash(String locationFilterHash) {
                this.mLocationFilterHash = locationFilterHash;
            }

            public String getLocationFilterHash() {
                return mLocationFilterHash;
            }
        }
    }

    public static final String TAG = SaleItemsController.class.getSimpleName();
    private static final long SEARCH_DELAY_MS = 1000; // milliseconds
    private static final long DELETE_DELAY_MS = 1250; // milliseconds
    private static final String CATEGORY_KEY_SEPARATOR = ">>>";
    private static final String CATEGORY_KEY_SEPARATOR_REPLACEMENT = " • ";
    private static final String CATEGORY_FILTER_TYPE = "Category";
    private static final String KEY_SEARCH_TEXT = "KEY_SEARCH_TEXT";
    private static final String SHOP_KEY_SEARCH_TEXT = "SHOP_KEY_SEARCH_TEXT";

    private String mSaleId = "";
    private String mTitle = "";
    private String mSaleName = "";
    private String mCategoryKey = "";
    private String mCategoryForTitle = "";
    private String mSearchQuery = "";
    private String mShopSearchQuery = "";
    private String mEndDate = "";

    private Map<String, GetCategoryTreeResponse> mCategoryMap = new HashMap<>();
    private List<SaleItemProduct> mSaleItems = new LinkedList<>();
    private List<SaleItemProduct> mSaleItemsFromCache = null;
    private List<SaleItemFacet> mFacets = new ArrayList<>();
    private List<GetCategoryTreeResponse> mCategoryTreeResponse = new LinkedList<>();
    private List<GetCategoryTreeResponse> mInitialCategoryTree = new LinkedList<>();
    private List<SortingResponse> mSortingResponse = new ArrayList<>();
    private String mSortingListJsonString = "";
    private boolean mIsFilterClicked = false;
    private boolean mHasCategoryTreeResponse = false;
    private boolean mShouldRefreshFacets = true;
    private String mCurrentTabName = "";
    private String mPreviousTabName = "";
    private List<Pair<String, String>> mFacetFilters = new ArrayList();
    private List<String> mTabTitles = new ArrayList();
    private List<String> mSelectedTitle = new ArrayList<>();
    private int mColumnCount;
    private int mCurrentProductDetailPosition = -1;

    private BrandNames mBrandNames = null;
    private BrandBubblesAdapter mBrandBubblesAdapter = null;
    private Handler brandNameHandler = null;

    private boolean isSkeletonAnimating = false;

    @BindView(R.id.controller_sale_items_main_container)
    ViewGroup mMainContainer;

    @BindView(R.id.controller_sale_items_grid_view)
    RecyclerView mSaleItemsRecyclerView;

    @BindView(R.id.partial_toolbar_field_title_edittext)
    SearchEditText mSaleItemsToolbarField;

    @BindView(R.id.partial_toolbar_details_upper_title_textview)
    TextView mSaleItemsCategoryToolbarTitle;

    @BindView(R.id.partial_toolbar_details_center_title_textview)
    TextView mSaleItemsToolbarTitle;

    @BindView(R.id.partial_toolbar_details_subtitle_textview)
    TextView mSaleItemsToolbarSubTitleText;

    @BindView(R.id.partial_toolbar_title_details)
    Toolbar mToolbar;

    @BindView(R.id.partial_wishlist_empty)
    RelativeLayout mWishlistPlaceholder;

    @BindView(R.id.adView_banner)
    View mFooterAds;

    @BindView(R.id.controller_sale_items_text_placeholder)
    LinearLayout mPlaceholder;

    @BindView(R.id.partial_toolbar_field_title_left_option)
    View mSaleItemsBackIcon;

    @BindView(R.id.controller_search_filter_frame)
    ViewGroup mSearchFilterContainer;

    @BindView(R.id.controller_search_filter_tabs)
    AdaptiveTabLayout mTabLayout;

    @BindView(R.id.controller_sale_items_search_skeleton)
    ViewGroup mSearchFilterSkeleton;

    @Nullable
    @BindView(R.id.controller_sale_items_appbar)
    AppBarLayout mAppBar;

    @Nullable
    @BindView(R.id.controller_sale_collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;

    @BindView(R.id.partial_toolbar_details_remaining_time_layout)
    LinearLayout mSaleItemsRemainingTimeLayout;

    @BindView(R.id.partial_toolbar_details_remaining_time_value)
    TextView mSaleItemsRemainingTimeText;

    @BindView(R.id.partial_toolbar_details_end_time_text)
    TextView mSaleEndsInText;

    @BindView(R.id.partial_toolbar_field_title_right_option)
    ImageButton mColumnView;

    @BindView(R.id.controller_toolbar_brand_bubbles)
    RecyclerView mBrandBubblesRecyclerView;

    private CustomGridLayoutManager mGridLayoutManager;

    private SaleItemsAdapter mSaleItemsAdapter;
    private Paginate mPaginateManager;
    private Paginate.Callbacks mPaginateCallbacks;

    private Router mSearchFilterRouter;
    private SearchFilterMvpView mSearchFilterMvpView;

    private int mVerticalOffset;
    private int mSaleItemsPageNumber = 0;

    private boolean mIsSearch = false;
    private boolean mFromBannerSearch = false;
    private boolean mFromShopSearch = false;
    private boolean mFromCategorySearch = false;

    private boolean mIsRecyclerViewScrollIdle;
    private boolean mIsLoadingProgress = false;
    private boolean mHasLoadedAllItems = false;
    private boolean mFromCategoryDeeplink = false;
    private boolean mInitialLoad = false;
    private boolean mHasSavedInstance = false;

    private boolean willOpenSaleDetails = false;

    private String locationFilterHash = null;

    private String mSalesOrigin = DataCollector.EventParameters.ViewSource.SALE;

    private Set<SearchChipModel> mChipFilters = new HashSet<>();
    private Set<SearchChipModel> mPreSelectedFilter = new HashSet<>();

    //store state of selection from filters
    private String mPreviousSelectedFacetIndicesJsonString = "";

    //store removed query chips
    private List<String> mRemovedChipTitles;

    private CountDownTimer mCountDownTimer;

    //genie event search info
    private SearchOperationType searchOperationType = null;
    private String mGenieCategory = null;
    private int mGenieBrandCount = 0;
    private int mGenieMinPrice = 0;
    private int mGenieMaxPrice = 200;
    private int mGenieSizesCount = 0;
    private int mGenieTotal = 0;
    private String mGenieQuery = null;
    private String mGenieSort = null;
    private String mGenieFilters = null;

    private LinkOptions linkOptions = null;

    private Parameters mSavedParameters = null;

    private TextWatcher mTextWatcher = new TextWatcher() {
        private Timer mTextWatcherTimer = null;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            AppLogger.d(TAG, "on text changed");

            mSearchQuery = s.toString();
            refreshBrandBubbles(mSearchQuery);
            showBrandBubbles();
            mIsSearch = true;

            if (mTextWatcherTimer != null) {
                mTextWatcherTimer.cancel();
            }

            mTextWatcherTimer = new Timer();
            mTextWatcherTimer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            if (!isViewAttached()) {
                                return;
                            }
                            mActivity.runOnUiThread(SaleItemsController.this::showLoading);
                            if (mSearchFilterMvpView != null) {
                                mActivity.runOnUiThread(() -> {
                                            mSaleItemsPageNumber = 0;
                                            searchOperationType = SearchOperationType.ENTERTERM;
                                            mGenieCategory = null;
                                            mPresenter.loadSaleItems(
                                                    createSaleItemsRequest(
                                                            mSearchFilterMvpView.getCategoryKeys(),
                                                            0,
                                                            mChipFilters));
                                        }
                                );
                            }
                        }
                    }, count >= before ? SEARCH_DELAY_MS : DELETE_DELAY_MS);

        }

        @Override
        public void afterTextChanged(Editable editable) {
            AppLogger.d("saleitemscontroller", "after text changed");
        }
    };

    @Inject
    SaleItemsMvpPresenter<SaleItemsMvpView> mPresenter;

    public static SaleItemsController newInstance() {
        return new SaleItemsController(new BundleBuilder(new Bundle()).build());
    }

    public static SaleItemsController newInstance(Parameters parameters) {
        SaleItemsController controller = SaleItemsController.newInstance();

        setupControllerWithParameters(controller, parameters);
        controller.mSavedParameters = parameters;

        if (controller.mFromShopSearch) {
            controller.mSalesOrigin = DataCollector.EventParameters.ViewSource.SEARCH;
        }
        if (controller.mFromCategorySearch) {
            controller.mSalesOrigin = DataCollector.EventParameters.ViewSource.CATEGORY;
        }
        return controller;
    }

    private static void setupControllerWithParameters(SaleItemsController controller, Parameters parameters) {
        String title = null;

        if (parameters instanceof Parameters.FromBannerClick) {
            title = ((Parameters.FromBannerClick) parameters).getTitle();
            controller.mSaleName = title;
            controller.mSaleId = ((Parameters.FromBannerClick) parameters).getSaleId();
            controller.mEndDate = ((Parameters.FromBannerClick) parameters).getEndDate();
            controller.linkOptions = ((Parameters.FromBannerClick) parameters).getLinkOptions();
            controller.mFromBannerSearch = true;
        } else if (parameters instanceof Parameters.FromCategoryBannerClick) {
            final LinkOptions linkOptions = ((Parameters.FromCategoryBannerClick) parameters).getLinkOptions();
            title = linkOptions.getCategoryName();
            controller.linkOptions = linkOptions;
            controller.mCategoryKey = linkOptions.getCategoryName();
            controller.mChipFilters = SearchChipModel.chipListFromLinkOptions(linkOptions);
            controller.mFromCategorySearch = true;
        } else if (parameters instanceof Parameters.FromShopSearch) {
            title = ((Parameters.FromShopSearch) parameters).getTitle();
            controller.mFromShopSearch = true;
            controller.mSearchQuery = ((Parameters.FromShopSearch) parameters).getSearchKey();
        } else if (parameters instanceof Parameters.FromTopBrands) {
            title = ((Parameters.FromTopBrands) parameters).getBrandName();
            controller.mFromShopSearch = true;
            controller.mChipFilters.add(new SearchChipModel(BundleKeys.BRANDS_FACETFILTER_NAME, title, title));
        } else if (parameters instanceof Parameters.FromCategory) {
            title = ((Parameters.FromCategory) parameters).getTitle();
            controller.mCategoryKey = ((Parameters.FromCategory) parameters).getCategoryMap();
            controller.mInitialCategoryTree = ((Parameters.FromCategory) parameters).getCategories();
            controller.mFromCategorySearch = true;
            controller.mChipFilters = ((Parameters.FromCategory) parameters).getPreSelectedFilter();
            if (controller.mChipFilters == null) {
                controller.mChipFilters = new HashSet<>();
            }
            controller.mPreSelectedFilter = ((Parameters.FromCategory) parameters).getPreSelectedFilter();
        } else if (parameters instanceof Parameters.FromSaleItemDeepLink) {
            title = ((Parameters.FromSaleItemDeepLink) parameters).getBannerTitle();
            controller.mSaleId = ((Parameters.FromSaleItemDeepLink) parameters).getSaleId();
        } else if (parameters instanceof Parameters.FromCategoryDeepLink) {
            title = ((Parameters.FromCategoryDeepLink) parameters).getTitle();
            controller.mCategoryKey = ((Parameters.FromCategoryDeepLink) parameters).getCategoryMapKey();
            controller.mFromCategoryDeeplink = true;
        } else if (parameters instanceof Parameters.FromLocationFilterHash) {
            controller.locationFilterHash = ((Parameters.FromLocationFilterHash) parameters).getLocationFilterHash();
        }

        title = title != null ? title.replaceAll(CATEGORY_KEY_SEPARATOR, CATEGORY_KEY_SEPARATOR_REPLACEMENT) : "";
        controller.mTitle = title;
    }

    public SaleItemsController(Bundle args) {
        super(args);

        if (args.containsKey(BundleKeys.SALEITEMS_TITLE)) {
            mTitle = getArgs().getString(BundleKeys.SALEITEMS_TITLE, "");
            mTitle = mTitle.replaceAll(CATEGORY_KEY_SEPARATOR, CATEGORY_KEY_SEPARATOR_REPLACEMENT);
        }

        mSaleId = getArgs().getString(BundleKeys.SALEITEMS_SALE_ID, "");

        mCategoryKey = getArgs().getString(BundleKeys.SALEITEMS_CATEGORY_MAP, "");

        if (args.containsKey(BundleKeys.SALEITEMS_CHIPS_FILTER)) {
            mChipFilters = JsonUtils.convertStringToObject(getArgs().getString(BundleKeys.SALEITEMS_CHIPS_FILTER, ""), new TypeToken<HashSet<SearchChipModel>>() {
            }.getType());
            if (mChipFilters == null) {
                mChipFilters = new HashSet<>();
            }
        }

        mFromBannerSearch = getArgs().getBoolean(BundleKeys.SALEITEMS_FROM_BANNER_SEARCH, false);
        mFromShopSearch = getArgs().getBoolean(BundleKeys.SALEITEMS_FROM_SHOP_SEARCH, false);
        mFromCategorySearch = getArgs().getBoolean(BundleKeys.SALEITEMS_FROM_CATEGORY_SEARCH, false);

        if (mFromShopSearch) mSalesOrigin = DataCollector.EventParameters.ViewSource.SEARCH;
        if (mFromCategorySearch) mSalesOrigin = DataCollector.EventParameters.ViewSource.CATEGORY;

        mFromCategoryDeeplink = getArgs().getBoolean(BundleKeys.SALEITEMS_FROM_CATEGORY_DEEPLINK, false);

        //initial category tree from categoriescontroller
        if (args.containsKey(BundleKeys.SALEITEMS_KEY_CATEGORIES)) {
            mInitialCategoryTree = JsonUtils.convertStringToObject(args.getString(BundleKeys.SALEITEMS_KEY_CATEGORIES, ""), new TypeToken<ArrayList<GetCategoryTreeResponse>>() {
            }.getType());
        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        switch (mSourceMode) {
            case NORMAL:
                outState.putBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE, true);
                outState.putString(BundleKeys.SALEITEMS_PARAMETERS, JsonUtils.convertToJsonObject(mSavedParameters).toString());
                outState.putString(BundleKeys.SALEITEMS_PARAMETERS_TYPE, mSavedParameters.getClass().getName());
                break;
            case WISHLIST:
                outState.putBoolean(BundleKeys.SALEITEMS_IS_WISHLIST, true);
                break;
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.getBoolean(BundleKeys.SALEITEMS_IS_WISHLIST, false)) {
            setSourceMode(SourceMode.WISHLIST);
        } else {
            mHasSavedInstance = savedInstanceState.getBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE);
            final String parameterTypeName = savedInstanceState.getString(BundleKeys.SALEITEMS_PARAMETERS_TYPE);
            final String parameterJson = savedInstanceState.getString(BundleKeys.SALEITEMS_PARAMETERS);
            if (parameterTypeName != null) {
                try {
                    final Object object = JsonUtils.convertStringToObject(parameterJson, Class.forName(parameterTypeName));
                    if (object instanceof Parameters) {
                        final Parameters parametersObject = (Parameters) object;
                        setupControllerWithParameters(this, parametersObject);
                        mSavedParameters = parametersObject;
                    }
                } catch (ClassNotFoundException ignore) {
                }
            }
        }
    }


    @Override
    protected void onAttach(@NonNull View view) {
        mPresenter.onAttach(this);
        if (mAppBar != null) {
            mAppBar.addOnOffsetChangedListener(this);
        }

        determineToolbarTitle();
        willOpenSaleDetails = false;

        super.onAttach(view);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_sale_items, container, false);

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    protected void onActivityResumed(@NonNull Activity activity) {
        super.onActivityResumed(activity);

        if (isViewAttached() && isViewBound()) {
            determineToolbarTitle();
            refreshContents();
        }

        mGridViewModePreferenceHelper.resetTimeElapsed();
        mGridViewModePreferenceHelper.resetTimestamp();
    }


    @Override
    public void onViewWillDisappear(Controller nextController) {
        super.onViewWillDisappear(nextController);
    }

    @Override
    public void onViewDidDisappear(Controller nextController) {
        super.onViewDidDisappear(nextController);

        if (mAppBar != null) {
            mAppBar.setExpanded(true, false);
        }
    }

    @Override
    public void onViewDidAppear(Controller previousController) {
        super.onViewDidAppear(previousController);

        if (!mFromCategoryDeeplink && (!(previousController instanceof SaleItemDetailsController) || mSaleItems == null || mSaleItems.size() == 0)) {
            resetViewBasedOnSourceMode();
            switch (mSourceMode) {
                case NORMAL:
                    if (mSaleId != null && !mSaleId.isEmpty()) {
                        if (mSaleName == null || mSaleName.isEmpty()) {
                            mPresenter.loadSaleBannerDetails(mSaleId);
                        }
                    } else {
                        showBrandBubbles();
                    }
                    mSaleItemsPageNumber = 0;
                    searchOperationType = null;
                    mGenieCategory = null;
                    mPresenter.loadSaleItems(createSaleItemsRequest(mCategoryKey, 0, mChipFilters));
                    if (mHasSavedInstance) {
                        mActivity.getMainController().setSavedCurrentItem();
                    } else if (mChipFilters.isEmpty()) {
                        showKeyboard();
                    }
                    if (mInitialLoad) {
                        animateSkeletonUI();
                    }
                    break;
                case WISHLIST:
                    mSaleItemsPageNumber = 0;
                    mPresenter.loadWishlistPaginated(18, 0);
                    mSaleItemsRecyclerView.setLayoutAnimation(null);
                    break;
            }
        }

        mGridViewModePreferenceHelper.resetTimeElapsed();
        mGridViewModePreferenceHelper.resetTimestamp();

        willOpenSaleDetails = false;
    }

    @Override
    public void onViewWillAppear(Controller previousController) {
        super.onViewWillAppear(previousController);

        if (previousController instanceof SaleItemDetailsController &&
                mSaleItemsAdapter != null &&
                mCurrentProductDetailPosition >= 0 &&
                mCurrentProductDetailPosition < mSaleItems.size()) {
            if (mSourceMode == SourceMode.WISHLIST &&
                    !mPresenter.isProductInWishlist(mSaleItems.get(mCurrentProductDetailPosition).getId())) {
                mSaleItems.remove(mCurrentProductDetailPosition);
                mSaleItemsAdapter.removeData(mCurrentProductDetailPosition);
            } else {
                mSaleItemsAdapter.reloadCell(mCurrentProductDetailPosition);
            }
            mCurrentProductDetailPosition = -1;
        }
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        mActivity.getProfiler().setStartLogTime(DataCollector.EventParameters.CustomEventType.CV_ITEMLIST.getValue());
        mSaleItemsBackIcon.setOnClickListener(view12 -> mActivity.onBackPressed());
        setUp(view);

        setRetainViewMode(RetainViewMode.RETAIN_DETACH);
    }

    private void determineToolbarTitle() {
        if (!isViewAttached() || !isViewBound()) return;

        switch (mSourceMode) {
            case NORMAL:
                String lookingForText = getString(R.string.search_tag);

                // bug/gen-8065_ozsale-reskin_bugfixing - always set searchbar hint to 'search'
                if (mHasSavedInstance && isFromCategories()) {
                    if (mSearchQuery.length() > 0) {
                        mSaleItemsToolbarField.setText(mSearchQuery);
                        refreshBrandBubbles(mSearchQuery);
                    } else {
                        mSaleItemsToolbarField.setText("");
                        refreshBrandBubbles("");
                    }
                } else if (mHasSavedInstance && !isFromCategories()) {
                    if (mShopSearchQuery.length() > 0) {
                        mSearchQuery = mShopSearchQuery;
                        mSaleItemsToolbarField.setText(mSearchQuery);
                        refreshBrandBubbles(mSearchQuery);
                    } else {
                        mSaleItemsToolbarField.setText("");
                        refreshBrandBubbles("");
                    }
                } else if (mSearchQuery != null && mSearchQuery.length() > 0) {
                    mSaleItemsToolbarField.setText(mSearchQuery);
                    refreshBrandBubbles(mSearchQuery);
                } else {
                    mSaleItemsToolbarField.setHint(lookingForText);
                }

                //determining toolbartitle logic
                //category precedes above all
                String[] titles = mTitle.split(CATEGORY_KEY_SEPARATOR_REPLACEMENT, 0);
                String title = "";
                String subTitle = "";
                switch (titles.length) {
                    case 1:
                        title = titles[0];
                        break;
                    case 2:
                        title = titles[1];
                        subTitle = titles[0];
                        break;
                    default:
                        title = titles[2];
                        subTitle = titles[0] + CATEGORY_KEY_SEPARATOR_REPLACEMENT + titles[1];
                        break;
                }
                mSaleItemsCategoryToolbarTitle.setVisibility(isFromCategories() ? View.VISIBLE : View.GONE);
                mSaleItemsToolbarSubTitleText.setVisibility(isFromCategories() ? View.VISIBLE : View.GONE);
                if (mSaleItemsRemainingTimeLayout != null) {
                    mSaleItemsRemainingTimeLayout.setVisibility(isFromCategories() || mFromShopSearch ||
                            !mActivity.getResources()
                                    .getBoolean(R.bool.is_sale_countdown_timer_enabled) ? View.GONE : View.VISIBLE);
                }
                mSaleItemsToolbarTitle.setVisibility(!isFromCategories() ? View.VISIBLE : View.GONE);

                if (isFromCategories()) {
                    mSaleItemsCategoryToolbarTitle.setText(title);
                    mSaleItemsToolbarSubTitleText.setText(subTitle);
                } else if (mCategoryForTitle != null && !mCategoryForTitle.isEmpty()) {
                    mSaleItemsToolbarTitle.setText(mCategoryForTitle);
                } else if (mTitle != null && !mTitle.isEmpty()) {
                    mSaleItemsToolbarTitle.setText(mTitle);
                } else {
                    mSaleItemsToolbarTitle.setText(getString(R.string.i_am_looking_for));
                }

                if (!isFromCategories()) {
                    String finalText = mSaleItemsToolbarTitle.getText().toString();
                    if (finalText != null && !finalText.isEmpty() && finalText.trim().isEmpty()) {
                        mSaleItemsToolbarTitle.setBackground(
                                mActivity.getResources().getDrawable(R.drawable.bg_skeleton_fixed_size));
                    } else {
                        mSaleItemsToolbarTitle.setBackground(null);
                    }
                }
                break;
            case WISHLIST:
                mSaleItemsToolbarTitle.setVisibility(View.VISIBLE);
                mSaleItemsCategoryToolbarTitle.setVisibility(View.GONE);
                mSaleItemsToolbarSubTitleText.setVisibility(View.GONE);
                mSaleItemsToolbarTitle.setText(getString(R.string.wishlist));
                break;
        }
    }

    @Override
    public void onDetach(View view) {
        if (mAppBar != null) {
            mAppBar.removeOnOffsetChangedListener(this);
        }
        mSaleItemsToolbarField.setOnEditorActionListener(null);
        mSaleItemsToolbarField.removeTextChangedListener(mTextWatcher);
        hideKeyboard();
        hideBrandBubbles();
        super.onDetach(view);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        logGridViewPreferenceEvent();

        mPresenter.onDetach();
        mSaleItemsRecyclerView.setAdapter(null);
        if (mCountDownTimer != null) mCountDownTimer.cancel();
        super.onDestroyView(view);
    }

    @Override
    protected void setUp(View view) {

        if (mEndDate == null || mEndDate.isEmpty() || !DateUtils.isWithin48Hours(DateUtils.getRemainingTimeInMillis(mEndDate))) {
            mSaleItemsRemainingTimeText.setVisibility(View.GONE);
            mSaleEndsInText.setVisibility(View.GONE);
        } else {
            mSaleItemsRemainingTimeText.setVisibility(View.VISIBLE);
            mSaleEndsInText.setVisibility(View.VISIBLE);
            setupSaleRemainingTime(mEndDate);
        }

        mColumnView.setVisibility(View.VISIBLE);
        mColumnCount = mPresenter.getColumnCount();
        setGridViewMode(mColumnCount == mActivity.getResources().getInteger(R.integer.items_max_column_portrait) ?
                GridViewMode.MORE_IMAGES : GridViewMode.LARGER_IMAGES);
        mGridViewModePreferenceHelper.resetTimeElapsed();

        mGridViewModePreferenceHelper.resetTimestamp();

        //use initialcategory tree map if it came from categories.
        if (!mInitialCategoryTree.isEmpty()) {
            createCategoryMap(mInitialCategoryTree);
            if (mCategoryMap.get(mCategoryKey) != null) {
                mCategoryMap.get(mCategoryKey).setSelected(true);
            }
        }

        mSaleItemsAdapter = new SaleItemsAdapter(mActivity,
                mSaleItems,
                mPresenter,
                mSaleId,
                mColumnCount,
                this::logWishlistEvent, this);
        mPaginateCallbacks = new Paginate.Callbacks() {
            @Override
            public void onLoadMore() {
                // Load next mSaleItemsPageNumber of data (e.g. network or database)
                refresh();

            }

            @Override
            public boolean isLoading() {
                // Indicate whether new mSaleItemsPageNumber loading is in progress or not
                return mIsLoadingProgress;
            }

            @Override
            public boolean hasLoadedAllItems() {
                // Indicate whether all data (pages) are loaded or not
                return mHasLoadedAllItems;
            }
        };

        mSearchFilterRouter = getChildRouter(mSearchFilterContainer);

        CustomGridLayoutManager gridLayoutManager = new CustomGridLayoutManager(mActivity, mSaleItemsAdapter.getColumnCount());
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mSaleItemsAdapter.getItemViewType(position)) {
                    case 1: // set default column count if not footer
                        return mSaleItemsAdapter.getColumnCount();
                    case 0: // set column count to 1 if it's a footer
                        return 1;
                    default:
                        return -1;
                }
            }
        });

        mGridLayoutManager = gridLayoutManager;
        mSaleItemsRecyclerView.setLayoutManager(gridLayoutManager);
        mSaleItemsRecyclerView.setAdapter(mSaleItemsAdapter);
        mSaleItemsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mIsRecyclerViewScrollIdle = newState == 0;
                if (newState != SCROLL_STATE_IDLE) {
                    hideKeyboard();
                    hideBrandBubbles();
                } else {
                    // Snaps search bar to expanded or hidden depending on whether
                    // t is halfway to 0 or 1
                    float t = -mVerticalOffset / (float) (mAppBar != null ? mAppBar.getHeight() : 0);
                    if (mAppBar != null) {
                        mAppBar.setExpanded(t < 0.5, true);
                    }
                }
            }
        });

        mInitialLoad = true;

        setRetainViewMode(RetainViewMode.RETAIN_DETACH);
        hideKeyboard();
        hideBrandBubbles();

        // Log data event for genie sale event
        if (mFromBannerSearch) {
            SaleEventRequest saleEventRequest = new SaleEventRequest();
            saleEventRequest.setEventType(EventTypeId.EVENT_ENTER_SALE);
            saleEventRequest.setId(mSaleId);
            saleEventRequest.setName(mTitle);

            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put(DataCollector.EventParameters.SALE_EVENT_REQUEST, saleEventRequest);

            DataCollector.logEvent(Events.SaleEvent, parameters);
        }

        LinearLayoutManager brandBubblesLayoutManager = new LinearLayoutManager(mActivity);
        brandBubblesLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        mBrandBubblesRecyclerView.setLayoutManager(brandBubblesLayoutManager);

        mFooterAds.setVisibility(View.GONE);
        setColumnViewEnabled(false);

        if (linkOptions != null) {
            mSaleItemsToolbarField.removeTextChangedListener(mTextWatcher);
            mSaleItemsToolbarField.setText(linkOptions.getSearchQuery());
            mSearchQuery = linkOptions.getSearchQuery();
        }
    }

    @OnClick(R.id.partial_toolbar_field_title_right_option)
    void onColumnClick() {
        mGridViewModePreferenceHelper.incrementTimeElapsedForGridViewMode(mGridViewMode);
        toggleGridViewMode();
    }

    @Optional
    @OnClick(R.id.partial_checkout_empty_button)
    void shopNow() {

        mActivity.getMainController().showShopController();
    }


    private void setAdapterPerColumnChange(int portraitColumn, int landscapeColumn) {

        String timestamp = mPresenter.getTimeStamp();
        int getSavedDay = 0;
        try {
            getSavedDay = timestamp == null || timestamp.isEmpty() ? 0 :
                    Integer.parseInt(timestamp);
        } catch (NumberFormatException ignored) {
        }

        if (DateUtils.hasDayPassed(getSavedDay)) {

            Calendar calander = Calendar.getInstance();
            int calendarDay = calander.get(Calendar.DAY_OF_YEAR);
            mPresenter.setTimeStamp(String.valueOf(calendarDay));

            HashMap<String, Object> eventParameters = new HashMap<>();
            eventParameters.put(DataCollector.EventParameters.APP_CONTEXT, mActivity);
            eventParameters.put(DataCollector.EventParameters.SCREEN_NAME, SaleItemsController.class.getSimpleName());
            eventParameters.put(DataCollector.EventParameters.TOGGLE_LIST_PORTRAIT, portraitColumn);
            eventParameters.put(DataCollector.EventParameters.TOGGLE_LIST_LANDSCAPE, landscapeColumn);
            DataCollector.logEvent(Events.ToggleColumn, eventParameters);
        }

        mSaleItemsAdapter = new SaleItemsAdapter(
                mActivity,
                mSaleItems,
                mPresenter,
                mSaleId,
                mColumnCount,
                this::logWishlistEvent, this);
        CustomGridLayoutManager gridLayoutManager = new CustomGridLayoutManager(mActivity, mSaleItemsAdapter.getColumnCount());
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mSaleItemsAdapter.getItemViewType(position)) {
                    case 1: // set default column count if not footer
                        return mSaleItemsAdapter.getColumnCount();
                    case 0: // set column count to 1 if it's a footer
                        return 1;
                    default:
                        return -1;
                }
            }
        });

        mGridLayoutManager = gridLayoutManager;
        mSaleItemsRecyclerView.setLayoutManager(gridLayoutManager);

        mSaleItemsRecyclerView.setAdapter(mSaleItemsAdapter);
    }

    private void setupSaleRemainingTime(String endDate) {
        mCountDownTimer = new CountDownTimer(DateUtils.getRemainingTimeInMillis(endDate), DateUtils.DATE_UTIL_MILLIS_TO_SEC) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (mSaleItemsRemainingTimeText != null) {
                    mSaleItemsRemainingTimeText.setText(DateUtils.getRemainingTimeInWeeks(millisUntilFinished));
                }
            }

            @Override
            public void onFinish() {
                if (mSaleItemsRemainingTimeText != null) {
                    mSaleItemsRemainingTimeText.setVisibility(View.GONE);
                }
                if (mSaleEndsInText != null) {
                    mSaleEndsInText.setVisibility(View.GONE);
                }
            }
        };
        mCountDownTimer.start();
    }

    private void showKeyboard() {
        if (mFromShopSearch && (mSearchQuery == null || mSearchQuery.isEmpty())) {
            activateSearch();
            KeyboardUtils.showSoftInput(mSaleItemsToolbarField, mActivity);
            InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.showSoftInput(mSaleItemsToolbarField, InputMethodManager.SHOW_FORCED);
            }
        }
    }

    @Override
    public void onLoadSortingFacetsFinished(List<SortingResponse> responseList) {
        mSortingResponse = responseList;
        mSortingListJsonString = new Gson().toJson(responseList);

        if (mSearchFilterMvpView != null) {
            mSearchFilterMvpView.updateSortingFacet(mSortingResponse);
        }
    }

    private void initializeCategoryTreeResponse(List<GetCategoryTreeResponse> source) {
        if (mInitialCategoryTree.isEmpty() || source.isEmpty()) {
            mCategoryTreeResponse = source;
        } else {
            mCategoryTreeResponse = new ArrayList<>(mInitialCategoryTree);
        }
    }

    @Override
    public void showSaleItems(GetSaleItemsResponse getSaleItemsResponse, boolean forFacetCorrection, boolean isFromCache) {
        if (isSkeletonAnimating) {
            mSaleItemsRecyclerView.setLayoutAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Handler mainHandler = new Handler(mActivity.getMainLooper());
                    Runnable myRunnable = () -> {
                        if (!isAttached()) {
                            return;
                        }
                        isSkeletonAnimating = false;
                        showSaleItems(getSaleItemsResponse, forFacetCorrection, isFromCache);
                        if (mSaleItemsRecyclerView != null) {
                            mSaleItemsRecyclerView.setLayoutAnimationListener(null);
                        }
                        if (mGridLayoutManager != null) {
                            mGridLayoutManager.setScrollEnabled(true);
                        }
                    };
                    mainHandler.post(myRunnable);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            return;
        }
        if (!mColumnView.isEnabled()) {
            setColumnViewEnabled(true);
        }

        mActivity.getProfiler().setEndLogTime(DataCollector.EventParameters.CustomEventType.CV_ITEMLIST.getValue());

        mGenieTotal = getSaleItemsResponse.total;
        mGenieQuery = getSaleItemsResponse.query;

        for (SaleItemFacet facet : getSaleItemsResponse.getFacets()) {
            if (facet.getFacetName().equals(BundleKeys.PRICE_FACETFILTER_NAME)) {
                int maxPrice = 0;
                for (SaleItemFacet.Value value : facet.getFacetValues()) {
                    int price = (int) Math.ceil(Float.parseFloat(value.getValue()));
                    if (maxPrice < price) {
                        maxPrice = price;
                    }
                }
                mGenieMaxPrice = maxPrice;
            }
        }

        if (mFromCategorySearch) {
            String categories = mTitle.replaceAll(CATEGORY_KEY_SEPARATOR_REPLACEMENT, "/");
            CategoryRequest categoryRequest = new CategoryRequest();
            categoryRequest.setEventType(EventTypeId.EVENT_ENTER_CATEGORY);
            categoryRequest.setCategories(categories);

            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put(DataCollector.EventParameters.MILLISECONDS,
                    Profiler.getTotalTime(DataCollector.EventParameters.CustomEventType.CV_ITEMLIST.getValue()));
            parameters.put(DataCollector.EventParameters.CATEGORY_REQUEST, categoryRequest);
            parameters.put(DataCollector.EventParameters.APP_CONTEXT, mActivity);
            parameters.put(DataCollector.EventParameters.ITEM_LIST, categories);
            parameters.put(DataCollector.EventParameters.SCREEN_NAME, TAG);
            DataCollector.logEvent(Events.CVItemList, parameters);
        }

        if (!mHasCategoryTreeResponse) {
            initializeCategoryTreeResponse(getSaleItemsResponse.getCategories());
            mHasCategoryTreeResponse = true;
        }
        if (mShouldRefreshFacets) {
            mFacets = getSaleItemsResponse.getFacets();
        }
        mShouldRefreshFacets = true;

        List<SaleItemProduct> items = getSaleItemsResponse.products;

        mIsLoadingProgress = false;

        if (isFromCache && mSaleItemsFromCache == null) {
            mSaleItems = new ArrayList<>(mSaleItemsAdapter.getData());
        }

        if (items.size() == 0 && mSaleItemsPageNumber != 0) {
            mHasLoadedAllItems = true;
            mPaginateManager.setHasMoreDataToLoad(false);
        } else {

            if (mSaleItemsPageNumber == 0 || mIsSearch) {
                if (mPaginateManager != null) {
                    mPaginateManager.unbind();
                }

                if (items.size() <= getResources().getInteger(R.integer.sale_items_threshold)) {
                    if (mPaginateManager == null || items.size() == 0) {
                        showPlaceholder(true);
                    } else {
                        mHasLoadedAllItems = true;
                        mPaginateManager.setHasMoreDataToLoad(false);
                    }
                } else {
                    mPaginateManager = PaginateUtils.init(mSaleItemsRecyclerView, mPaginateCallbacks);
                    mSaleItemsRecyclerView.scrollToPosition(0);
                }
                mSaleItemsAdapter.replaceData(items);

                mIsSearch = false;
            } else {
                if (mSaleItemsFromCache != null && !isFromCache) {
                    mSaleItems.addAll(items);
                    mSaleItemsAdapter.replaceData(mSaleItems);
                } else {
                    mSaleItemsAdapter.addData(items);
                }
            }
        }

        if (isFromCache) {
            mSaleItemsFromCache = mSaleItemsAdapter.getData();
        } else {
            mSaleItemsFromCache = null;
            mSaleItems = mSaleItemsAdapter.getData();
        }

        showPlaceholder((mSaleItems == null || mSaleItems.isEmpty()) && (mSaleItemsFromCache == null || mSaleItemsFromCache.isEmpty()));

        setupSearchFilters();
        setupTabs();
        if (mInitialLoad) mInitialLoad = false;

        //replace category tree all the time.
        mSearchFilterMvpView.updateFacets(mFacets);
        mSearchFilterMvpView.replaceCategoryTree(mCategoryTreeResponse);

        if (mCategoryMap.isEmpty()) {
            createCategoryMap(mCategoryTreeResponse);
        }

        onRefreshEnd();

        reselectTabIfFacetsAlreadyVisible();

        logSearchEvent();
        searchOperationType = null;

        determineWhereToShowAds();
    }

    @Override
    public void showWishlist(List<SaleItemProduct> wishlist) {
        setColumnViewEnabled(true);

        if (mSaleItemsPageNumber == 0) {
            mHasLoadedAllItems = false;
            if (mPaginateManager != null) {
                mPaginateManager.setHasMoreDataToLoad(true);
            }
            mSaleItemsAdapter.replaceData(wishlist);
        } else if (!wishlist.isEmpty()) {
            mSaleItemsAdapter.addData(wishlist);
        } else {
            mHasLoadedAllItems = true;
        }
        mSaleItems = mSaleItemsAdapter.getData();

        if (mPaginateManager == null) {
            mPaginateManager = PaginateUtils.init(mSaleItemsRecyclerView, mPaginateCallbacks);
        }

        showPlaceholder(mSaleItems == null || mSaleItems.isEmpty());
        determineWhereToShowAds();

        mIsLoadingProgress = false;

        onRefreshEnd();
    }

    @Override
    public void updateWishlistWithAddition(String productId) {
        determineWhereToShowAds();
    }

    @Override
    public void updateWishlistWithRemoval(String productId) {
        if (mSourceMode == SourceMode.NORMAL) {
            return;
        }
        for (int i = 0; i < mSaleItems.size(); i++) {
            if (mSaleItems.get(i).getId().equals(productId)) {
                mSaleItems.remove(i);
                mSaleItemsAdapter.removeData(i);
                break;
            }
        }
        showPlaceholderWithAnimation(mSaleItems.isEmpty());
        determineWhereToShowAds();
    }

    private void logWishlistEvent(String productId, boolean liked) {
        WishlistEventRequest request = new WishlistEventRequest();
        request.setEventType(EventTypeId.EVENT_WISHLIST);

        WishlistEventRequest.WishListInfo wishlistInfo = new WishlistEventRequest.WishListInfo();
        request.setWishlistInfo(wishlistInfo);

        wishlistInfo.setOperation(liked ? 1 : 0);
        wishlistInfo.setProductId(productId);
        wishlistInfo.setReferrer(mSourceMode == SourceMode.NORMAL ? PRODUCT_LIST : WISHLIST);
        wishlistInfo.setProductsQuantity(mPresenter.wishlistCount());

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(DataCollector.EventParameters.WISHLIST_EVENT_REQUEST, request);
        parameters.put(DataCollector.EventParameters.SCREEN_NAME, TAG);
        parameters.put(DataCollector.EventParameters.APP_CONTEXT, mActivity);

        DataCollector.logEvent(Events.WishlistEvent, parameters);
    }

    private void logWishlistHeaderEvent() {
        WishlistEventRequest request = new WishlistEventRequest();
        request.setEventType(EventTypeId.EVENT_WISHLIST);

        WishlistEventRequest.WishListInfo wishlistInfo = new WishlistEventRequest.WishListInfo();
        request.setWishlistInfo(wishlistInfo);

        wishlistInfo.setOperation(null);
        wishlistInfo.setProductId(null);
        wishlistInfo.setReferrer(HEADER);
        wishlistInfo.setProductsQuantity(mPresenter.wishlistCount());

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(DataCollector.EventParameters.WISHLIST_EVENT_REQUEST, request);
        parameters.put(DataCollector.EventParameters.SCREEN_NAME, TAG);
        parameters.put(DataCollector.EventParameters.APP_CONTEXT, mActivity);

        DataCollector.logEvent(Events.WishlistEvent, parameters);
    }

    @Override
    public void hideKeyboard() {
        if (mActivity != null) {
            mActivity.hideKeyboard();
        }
    }

    private void logSearchEvent() {
        if (searchOperationType == null) {
            return;
        }

        SearchEventRequest searchEventRequest = new SearchEventRequest();
        searchEventRequest.setEventType(EventTypeId.EVENT_SEARCH);

        SearchEventRequest.SearchInfo searchInfo = new SearchEventRequest.SearchInfo();
        searchInfo.setOperation(searchOperationType.getValue());
        searchInfo.setResultsCount(mGenieTotal);
        searchInfo.setSelectedFacet(mGenieCategory);
        String categories = mGenieCategory != null ? mGenieCategory : (mSaleName != null ? mSaleName : "");
        searchInfo.setCategories(categories);
        searchInfo.setSearchTerm(mGenieQuery == null ? "" : mGenieQuery);
        searchInfo.setBrandsCount(mGenieBrandCount);
        searchInfo.setMinPrice(mGenieMinPrice);
        searchInfo.setMaxPrice(mGenieMaxPrice);
        searchInfo.setSizesCount(mGenieSizesCount);
        searchInfo.setCategoriesCount(mCategoryMap.size());
        searchInfo.setSort(mGenieSort);
        searchInfo.setFilters(mGenieFilters == null ? "" : mGenieFilters);
        searchEventRequest.setSearchInfo(searchInfo);

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(DataCollector.EventParameters.SEARCH_EVENT_REQUEST, searchEventRequest);
        parameters.put(DataCollector.EventParameters.SEARCH_TERM, mGenieQuery == null ? "" : mGenieQuery);
        parameters.put(DataCollector.EventParameters.SCREEN_NAME, TAG);
        parameters.put(DataCollector.EventParameters.APP_CONTEXT, mActivity);

        DataCollector.logEvent(Events.SearchEvent, parameters);
    }

    private void logGridViewPreferenceEvent() {
        mGridViewModePreferenceHelper.incrementTimeElapsedForGridViewMode(mGridViewMode);
        String output = mGridViewModePreferenceHelper.computeGridViewModePreference().toString();

        HashMap<String, Object> eventParameters = new HashMap<>();
        eventParameters.put(DataCollector.EventParameters.APP_CONTEXT, mActivity);
        eventParameters.put(DataCollector.EventParameters.SCREEN_NAME, SaleItemsController.class.getSimpleName());
        eventParameters.put(DataCollector.EventParameters.TOGGLE_LIST_PREFERENCE, output);
        DataCollector.logEvent(Events.ProductListGridViewPreference, eventParameters);
    }

    @Override
    public void onTabSwitch(boolean intoThisView) {
        super.onTabSwitch(intoThisView);

        if (intoThisView) {
            mGridViewModePreferenceHelper.resetTimeElapsed();
            mGridViewModePreferenceHelper.resetTimestamp();
            if (mSourceMode == SourceMode.WISHLIST) {
                logWishlistHeaderEvent();
            }
        } else {
            logGridViewPreferenceEvent();
        }
    }

    @Override
    public void refreshContents() {
        super.refreshContents();
        if (!isAttached() || !isViewAttached()) {
            return;
        }

        resetViewBasedOnSourceMode();
        switch (mSourceMode) {
            case NORMAL:
                setupSearchFilters();
                if (mSaleId != null && !mSaleId.isEmpty()) {
                    if (mSaleName == null || mSaleName.isEmpty()) {
                        mPresenter.loadSaleBannerDetails(mSaleId);
                    }
                }
                mSaleItemsPageNumber = 0;
                searchOperationType = null;
                mGenieCategory = null;
                mPresenter.loadSaleItems(createSaleItemsRequest(mSearchFilterMvpView.getCategoryKeys(), 0, mChipFilters));
                if (mAppBar != null) {
                    mAppBar.setExpanded(true, true);
                }
                break;
            case WISHLIST:
                mWishlistPlaceholder.setVisibility(View.GONE);
                mToolbar.setVisibility(View.GONE);
                mSaleItemsRecyclerView.setVisibility(View.GONE);
                mSaleItemsRecyclerView.scrollToPosition(0);
                mSaleItemsPageNumber = 0;
                mPresenter.loadWishlistPaginated(18, 0);
                break;
        }
        mFooterAds.setVisibility(View.GONE);
    }

    void onBackClick() {
        KeyboardUtils.hideSoftInput(mActivity);
        if (!mIsFilterClicked) {
            mIsFilterClicked = true;
            mActivity.onBackPressed();
            new Handler().postDelayed(new TimerTask() {
                @Override
                public void run() {
                    mIsFilterClicked = false;
                }
            }, 2000);
        }
    }

    public void refresh() {
        switch (mSourceMode) {
            case NORMAL:
                if (!mInitialLoad) {
                    if (mSaleItems.size() < getResources().getInteger(R.integer.sale_items_threshold)) {
                        mHasLoadedAllItems = true;
                        mSaleItemsPageNumber = 0;
                    } else {
                        mIsLoadingProgress = true;
                        mSaleItemsPageNumber++;
                        if (mSaleId != null && !mSaleId.isEmpty()) {
                            if (mSaleName == null || mSaleName.isEmpty()) {
                                mPresenter.loadSaleBannerDetails(mSaleId);
                            }
                        }
                        searchOperationType = null;
                        mGenieCategory = null;
                        mPresenter.loadSaleItems(createSaleItemsRequest(mSearchFilterMvpView.getCategoryKeys(), mSaleItemsPageNumber, mChipFilters));
                    }
                }
                break;
            case WISHLIST:
                mIsLoadingProgress = true;
                mPresenter.loadWishlistPaginated(18, mSaleItems.size());
                mSaleItemsPageNumber++;
                break;
        }
    }

    @Override
    public void showSaleBannerDetails(GetSaleBannerDetailsResponse response) {
        if (response != null &&
                response.getSaleName() != null &&
                !response.getSaleName().isEmpty()) {
            mSaleName = response.getSaleName();
            mTitle = mSaleName;
            determineToolbarTitle();
        }
    }

    @Override
    public void showProductDetails(int position,
                                   Drawable imagePlaceholderDrawable,
                                   String imageUrl,
                                   SaleItemProduct product,
                                   int viewLeft,
                                   int viewTop,
                                   int viewWidth,
                                   int viewHeight) {
        if (!isViewAttached() || willOpenSaleDetails) {
            return;
        }

        willOpenSaleDetails = true;

        if (mSearchFilterMvpView != null) {
            mSearchFilterMvpView.closeFacets();
        }
        mSaleItemsRecyclerView.smoothScrollToPosition(position);

        mCurrentProductDetailPosition = position;

        SaleItemDetailsController.Parameters.FromItemsList parameters = new SaleItemDetailsController
                .Parameters.FromItemsList(position,
                imagePlaceholderDrawable,
                imageUrl,
                product.getSeoIdentifier(),
                product.getSkus() == null || product.getSkus().isEmpty() ? "" : product.getSkus().get(0).getId(),
                mSaleId,
                product.getName(),
                product.getBrandName(),
                product.getPrice() != null ? PriceUtils.getPriceStringValue(product.getPrice().getValue()) : "",
                product.getOriginalPrice() != null ? PriceUtils.getRpStringValue(product.getOriginalPrice().getValue()) : "",
                product.getSalePercentOffText(),
                PriceUtils.getRpStringValue(product.getSalePrice() != null ? product.getSalePrice().getValue() : 0),
                product.getPriceRangeText(),
                mSalesOrigin, mEndDate, product.getFreeDelivery(), product.isSoldOut());

        RouterTransaction routerTransaction = RouterTransaction
                .with(SaleItemDetailsController.newInstance(parameters));

        HashMap<String, Object> eventParameters = new HashMap<>();
        eventParameters.put(DataCollector.EventParameters.TYPE, mSalesOrigin + PRODUCT_CLICK);
        eventParameters.put(DataCollector.EventParameters.ITEM_ARRAY_POSITION, position);
        eventParameters.put(DataCollector.EventParameters.APP_CONTEXT, mActivity);
        eventParameters.put(DataCollector.EventParameters.SCREEN_NAME, SaleItemsController.class.getSimpleName());
        DataCollector.logEvent(Events.clicksEvent, eventParameters);

        routerTransaction = routerTransaction
                .pushChangeHandler(new ArcZoomChangeHandler(viewLeft, viewTop, viewWidth, viewHeight))
                .popChangeHandler(new ArcZoomChangeHandler(viewLeft, viewTop, viewWidth, viewHeight));

        getRouter().pushController(routerTransaction);

        mFromShopSearch = false;
    }

    private void setupSearchFilters() {
        mSearchFilterRouter = getChildRouter(mSearchFilterContainer);
        mSearchFilterSkeleton.setVisibility(View.GONE);
        if (mSearchFilterMvpView == null) {

            SearchFilterController.Parameters.FromItemsList parameters = new SearchFilterController
                    .Parameters.FromItemsList(
                    mFacets,
                    mSortingResponse,
                    mCategoryTreeResponse,
                    null,
                    mCategoryKey,
                    mChipFilters,
                    mFromCategorySearch,
                    mPreSelectedFilter);

            GateKeeper.Destination destination;
            if (mFromBannerSearch || mFromShopSearch) {
                destination = GateKeeper.Destination.SEARCH_FILTER_FOR_SHOP;
            } else {
                destination = GateKeeper.Destination.SEARCH_FILTER_FOR_CATEGORY;
            }


            if (mHasSavedInstance) {
                if (isFromCategories()) {
                    mSearchFilterMvpView = mActivity.getSearchFilterController();
                } else {
                    mSearchFilterMvpView = mActivity.getShopSearchFilterController();
                }
            }

            if (mSearchFilterMvpView == null) {
                Controller searchFilterController = SearchFilterController.newInstance(parameters);
                mSearchFilterMvpView = (SearchFilterMvpView) searchFilterController;
                GateKeeper.setRoot(mSearchFilterRouter, destination, RouterTransaction.with(searchFilterController));
            }

            mSearchFilterMvpView.setRepository(SaleItemsController.this);
            mSearchFilterMvpView.replaceSearchChipModels(mChipFilters);
        }
        showCollapsingToolbar();
    }

    public void deactivateSearch() {
        mSaleItemsToolbarField.removeTextChangedListener(mTextWatcher);
        mSearchQuery = mSaleItemsToolbarField.getText().toString();

        mSaleItemsToolbarField.setActivated(false);
        mSaleItemsToolbarField.setVisibility(View.GONE);
        mSaleItemsToolbarTitle.setVisibility(View.VISIBLE);

        mSaleItemsBackIcon.setOnClickListener(view -> onBackClick());

        determineToolbarTitle();
        hideKeyboard();
        hideBrandBubbles();
    }

    private String buildSearchQueryText(List<String> chipTitles) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : chipTitles) {
            stringBuilder.append(string);
            stringBuilder.append(" ");
        }

        return stringBuilder.toString().trim();
    }

    @Override
    public GetSaleItemsRequest createSaleItemsRequest(Set<String> categoryKeys, int pageNumber, Set<SearchChipModel> chipsList) {
        String previousCategoryKey = mCategoryKey;
        mCategoryKey = StringUtils.generateConcatenatedCategories(reduceCategoryKeysForRequest(categoryKeys));
        mShouldRefreshFacets = previousCategoryKey == null ||
                !(previousCategoryKey.equals(mCategoryKey) ||
                        previousCategoryKey.equals(mCategoryKey.replaceAll("[,\"]", ""))) ||
                !mHasCategoryTreeResponse;
        if (mShouldRefreshFacets) {
            mChipFilters = new HashSet<>();
            mSearchFilterMvpView.replaceSearchChipModels(mChipFilters);
        } else {
            mChipFilters = chipsList != null ? chipsList : new HashSet<>();
        }
        return createSaleItemsRequest(mCategoryKey, pageNumber, mChipFilters);
    }

    private Set<String> reduceCategoryKeysForRequest(Set<String> categoryKeys) {
        if (mCategoryTreeResponse.isEmpty()) {
            return categoryKeys;
        }

        LinkedHashSet<String> keys = new LinkedHashSet<>();
        for (GetCategoryTreeResponse node : mCategoryTreeResponse) {
            node.traverseTree(new GetCategoryTreeResponse.TreeTraversalBlock() {
                @Override
                public boolean execute(GetCategoryTreeResponse parent, Object option) {
                    boolean isChecked = categoryKeys.contains(parent.getKey());
                    if (isChecked) {
                        keys.add(parent.getKey());
                    }
                    return !isChecked;
                }

                @Override
                public Object transformOption(GetCategoryTreeResponse parent, Object option) {
                    return null;
                }
            }, null);
        }

        return keys;
    }

    @Override
    public GetSaleItemsRequest createSaleItemsRequest(String categoryKey, int pageNumber, Set<SearchChipModel> chipsList) {
        HashMap<String, List<String>> facetFilters = new HashMap<>();

        GetSaleItemsRequest getSaleItemsRequest = new GetSaleItemsRequest();

        getSaleItemsRequest = StringUtils.updateSaleItemRequest(categoryKey, getSaleItemsRequest);

        getSaleItemsRequest.setSorting("");
        getSaleItemsRequest.setPageNumber(String.valueOf(pageNumber));
        getSaleItemsRequest.setQuery(mSearchQuery);
        getSaleItemsRequest.setPageSize("50");

        if (mSaleId != null && !mSaleId.isEmpty()) {
            List<String> saleIds = facetFilters.get("saleId");
            if (saleIds == null) {
                saleIds = new LinkedList<>();
            } else {
                saleIds = new LinkedList<>(saleIds);
            }
            if (!saleIds.contains(mSaleId)) {
                saleIds.add(mSaleId);
            }
            facetFilters.put("saleId", saleIds);
        }

        if (locationFilterHash != null && !locationFilterHash.isEmpty()) {
            List<String> supplier = facetFilters.get("supplier");
            if (supplier == null) {
                supplier = new LinkedList<>();
            } else {
                supplier = new LinkedList<>(supplier);
            }
            supplier.add(locationFilterHash);
            facetFilters.put("supplier", supplier);
        }

        //clear SelectedTitle Array and add filter category if any
        mSelectedTitle.clear();
        if (categoryKey != null && !categoryKey.isEmpty()) mSelectedTitle.add(CATEGORY_FILTER_TYPE);

        if (chipsList == null) {
            getSaleItemsRequest.setHasFilters(false);
        } else {
            if (chipsList.size() != 0) {
                ArrayList<String> brandNameFacetFilters = new ArrayList<>();
                ArrayList<String> colorFacetFilters = new ArrayList<>();
                ArrayList<String> sizesFacetFilters = new ArrayList<>();
                ArrayList<String> priceFacetFilters = new ArrayList<>();
                ArrayList<String> deliveryFacetFilters = new ArrayList<>();
                ArrayList<String> newArrivalFacetFilters = new ArrayList<>();
                ArrayList<String> sortFacetFilters = new ArrayList<>();

                for (SearchChipModel chip : chipsList) {
                    String facetName = chip.getFilterType();
                    if (facetName.equals(BundleKeys.BRANDS_FACETFILTER_NAME)) {
                        brandNameFacetFilters.add(chip.getChipTitle());
                    } else if (facetName.equals(BundleKeys.COLORS_FACETFILTER_NAME)) {
                        colorFacetFilters.add(chip.getChipTitle());
                    } else if (facetName.equals(BundleKeys.SIZES_FACETFILTER_NAME)) {
                        sizesFacetFilters.add(chip.getChipTitle());
                    } else if (facetName.equals(BundleKeys.PRICE_FACETFILTER_NAME)) {
                        priceFacetFilters.add(chip.getChipTitle());
                    } else if (facetName.equals(BundleKeys.SORT_FACETFILTER_NAME)) {
                        getSaleItemsRequest.setSorting(mapSortingTitleToKey(chip.getChipTitle()));
                        sortFacetFilters.add(chip.getChipTitle());
                    } else if (facetName.equals(BundleKeys.DELIVERY_FACETFILTER_NAME)) {
                        deliveryFacetFilters.add(chip.getChipTitle());
                    } else if (facetName.equals(BundleKeys.NEW_ARRIVAL_FACETFILTER_NAME)) {
                        newArrivalFacetFilters.add(chip.getChipTitle());
                    }
                }

                //Store Selected Filters
                if (!brandNameFacetFilters.isEmpty())
                    mSelectedTitle.add(BundleKeys.BRANDS_FACET_FILTER_TYPE);
                if (!colorFacetFilters.isEmpty())
                    mSelectedTitle.add(BundleKeys.COLOR_FACET_FILTER_TYPE);
                if (!sizesFacetFilters.isEmpty())
                    mSelectedTitle.add(BundleKeys.SIZE_FACET_FILTER_TYPE);
                if (!priceFacetFilters.isEmpty())
                    mSelectedTitle.add(BundleKeys.PRICE_FACET_FILTER_TYPE);
                if (!deliveryFacetFilters.isEmpty()) {
                    mSelectedTitle.add(BundleKeys.DELIVERY_FACET_FILTER_TYPE);
                }
                if (!newArrivalFacetFilters.isEmpty()) {
                    mSelectedTitle.add(BundleKeys.NEW_ARRIVAL_FACET_FILTER_TYPE);
                }
                if (!sortFacetFilters.isEmpty()) {
                    mSelectedTitle.add(BundleKeys.SORT_FACET_FILTER_TYPE);
                }

                facetFilters.put(BundleKeys.BRANDS_FACETFILTER_NAME, brandNameFacetFilters);
                facetFilters.put(BundleKeys.COLORS_FACETFILTER_NAME, colorFacetFilters);
                facetFilters.put(BundleKeys.SIZES_FACETFILTER_NAME, sizesFacetFilters);
                facetFilters.put(BundleKeys.PRICE_FACETFILTER_NAME, priceFacetFilters);
                facetFilters.put(BundleKeys.DELIVERY_FACETFILTER_NAME, deliveryFacetFilters);
                facetFilters.put(BundleKeys.NEW_ARRIVAL_FACETFILTER_NAME, newArrivalFacetFilters);

            }

            getSaleItemsRequest.setHasFilters(true);
        }

        if (linkOptions != null) {
            switch (linkOptions.getLinkOptionType()) {
                case SALE: {
                    // will override the mSaleId
                    List<String> value = new LinkedList<>();
                    value.add(linkOptions.getFacets().getSaleId());
                    facetFilters.remove(BundleKeys.SALEID_FACETFILTER_NAME);
                    facetFilters.put(BundleKeys.SALEID_FACETFILTER_NAME, value);
                    break;
                }
                case PROMO: {
                    List<String> value = new LinkedList<>();
                    value.add(linkOptions.getFacets().getPromoSaleId());
                    facetFilters.remove(BundleKeys.SALEID_FACETFILTER_NAME);
                    facetFilters.put(BundleKeys.PROMOSALEID_FACETFILTER_NAME, value);
                    break;
                }
                default:
                    break;
            }
        }

        String facetFiltersString = new Gson().toJson(facetFilters);

        getSaleItemsRequest.setFacetFilter(facetFiltersString);

        mSaleItemsPageNumber = pageNumber;

        return getSaleItemsRequest;
    }

    private String mapSortingTitleToKey(String title) {
        for (SortingResponse response : mSortingResponse) {
            if (response.getTitle().equalsIgnoreCase(title)) {
                return response.getKey();
            }
        }

        return "";
    }

    private void setupTabs() {
        //clear facets and tablayout
        mFacetFilters.clear();
        mTabLayout.removeAllTabs();
        mTabTitles.clear();

        addFacets();

        TabLayoutUtils.setupWithCustomTextView(mActivity, mTabLayout, mTabTitles);

        retainSelectedTabs();

        //add listener
        if (mInitialLoad) {
            //Remove selected state by default setup
            mCurrentTabName = "";
            mPreviousTabName = "";


            if (isFromCategories()) {
                toggleTabSelection(0, true);
            } else {
                toggleTabSelection(0, false);
            }

            mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    onSelectTab(tab.getPosition());
                    retainSelectedTabs();
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    toggleTabSelection(tab.getPosition(), mSelectedTitle.contains(mFacetFilters.get(tab.getPosition()).second));
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    onSelectTab(tab.getPosition());
                    retainSelectedTabs();
                }
            });
        }
    }

    private void addFacets() {
        if (mSearchFilterMvpView == null) {
            setupSearchFilters();
        }
        mFacetFilters = mSearchFilterMvpView.parseFacets(mFacets);
        mFacetFilters.add(0, new Pair<String, String>(BundleKeys.CATEGORY_TREE_FACET, CATEGORY_FILTER_TYPE));
        if (mPresenter.isSortingEnabled())
            mFacetFilters.add(mFacetFilters.size(), new Pair<String, String>(BundleKeys.SORT_FACETFILTER_NAME, "Sort"));

        mTabLayout.setTabCount(mFacetFilters.size());
        mTabLayout.setMinTabWidth();
        for (Pair<String, String> pair : mFacetFilters) {
            String newTitle = !pair.second.isEmpty() ? pair.second.substring(0, 1).toUpperCase() + pair.second.substring(1) : pair.second;
            mTabTitles.add(newTitle);

            mSearchFilterMvpView.setFacetFilterItems(mFacetFilters);
            mTabLayout.addTab(mTabLayout.newTab(), false);
        }

    }

    private void retainSelectedTabs() {
        String facetName = "";
        mSelectedTitle.add(mCurrentTabName);
        for (int i = 0; i < mFacetFilters.size(); i++) {
            facetName = mFacetFilters.get(i).second;
            toggleTabSelection(i, mSelectedTitle.contains(facetName));
            if (mCurrentTabName.equals(facetName)) mSearchFilterMvpView.updateSelectedFacet(i);
        }
        mSelectedTitle.remove(mCurrentTabName);
    }

    private void onSelectTab(int tabPos) {
        hideKeyboard();
        hideBrandBubbles();
        mSearchFilterMvpView.showFacetItem(tabPos);
        mPreviousTabName = mCurrentTabName;
        mCurrentTabName = mFacetFilters.get(tabPos).second;
        mSearchFilterMvpView.setSearchFilterControllerActive(true);
        mSearchFilterMvpView.updateSelectedFacet(tabPos);
    }

    private void showCollapsingToolbar() {
        if (!isViewAttached() || !isViewBound()) return;
        mTabLayout.setVisibility(View.VISIBLE);
        mSearchFilterContainer.setVisibility(View.VISIBLE);
    }

    private void determineWhereToShowAds() {
        if (getView() == null) {
            if (mSaleItemsAdapter != null) {
                mSaleItemsAdapter.setFooterEnabled(false);
            }
            if (mFooterAds != null) {
                mFooterAds.setVisibility(View.GONE);
            }
            return;
        }

        int contentHeight = mSaleItemsAdapter.getContentHeight();
        boolean onlyOneRowLeft = mSaleItemsAdapter.getContentHeight() == mSaleItemsAdapter.getHeightOfCell();
        int adjustedHeight = getView().getHeight() - (onlyOneRowLeft ? 0 : mSaleItemsAdapter.getHeightOfCell());
        if (contentHeight > adjustedHeight) {
            // ads as footer
            if (mSaleItemsAdapter != null) {
                mSaleItemsAdapter.setFooterEnabled(true);
            }
            mFooterAds.setVisibility(View.GONE);
        } else {
            // ads below recyclerview and placeholder
            if (mSaleItemsAdapter != null) {
                mSaleItemsAdapter.setFooterEnabled(false);
            }
            mFooterAds.setVisibility(View.VISIBLE);
            CommonUtils.showAdmob(mActivity, mFooterAds,
                    mActivity.getResources().getString(R.string.admob_products_id));
        }
    }

    @Override
    public void toggleTabSelection(int tabPos, boolean isTabActive) {
        LinearLayout tabStrip = (LinearLayout) mTabLayout.getChildAt(0);
        View tab = tabStrip.getChildAt(tabPos);
        if (tab != null) tab.setSelected(isTabActive);
    }

    @Override
    public void toggleTabSelection() {
        mPreviousTabName = mCurrentTabName;
        mCurrentTabName = "";
        retainSelectedTabs();
    }

    @Override
    public void onRefreshEnd() {
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        this.mVerticalOffset = verticalOffset;
    }

    @OnTouch(R.id.partial_toolbar_field_title_edittext)
    public boolean touchSearch() {
        activateSearch();
        return false;
    }

    @OnClick(R.id.partial_toolbar_field_title_edittext)
    public void activateSearch() {
        if (!isViewAttached() || !isViewBound()) return;

        showBrandBubbles();

        mSaleItemsToolbarField.setSelection(mSaleItemsToolbarField.getText().length());
        if (mSearchFilterMvpView != null) {
            mSearchFilterMvpView.closeFacets();
        }

        mSaleItemsToolbarField.addTextChangedListener(mTextWatcher);
        mSaleItemsToolbarField.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard();
                hideBrandBubbles();
                mSearchQuery = textView.getText().toString();
                mIsSearch = true;
                showLoading();
                if (mSearchFilterMvpView != null) {
                    mSaleItemsPageNumber = 0;
                    searchOperationType = SearchOperationType.ENTERTERM;
                    mGenieCategory = null;
                    mPresenter.loadSaleItems(createSaleItemsRequest(mSearchFilterMvpView.getCategoryKeys(), 0, mChipFilters));
                }
            }
            return false;
        });
    }

    /* bug/gen-8605_ozsale-reskin_bugfixing - four item row on mobile landscape */
    @Override
    public void onOrientationChanged(Configuration newConfig) {
        if (!isViewAttached() || !isViewBound()) return;

        GridLayoutManager gridLayoutManager = (GridLayoutManager) mSaleItemsRecyclerView.getLayoutManager();
        int currentScrollPosition = gridLayoutManager.findFirstVisibleItemPosition();
        mSaleItemsAdapter.computeItemViewDimensions();
        mSaleItemsRecyclerView.setAdapter(mSaleItemsAdapter);
        gridLayoutManager.scrollToPosition(currentScrollPosition);

        gridLayoutManager.setSpanCount(mSaleItemsAdapter.getColumnCount());

        determineWhereToShowAds();
    }

    @Override
    public void enableSaleItemsScroll(boolean val) {
        AppBarLayout.LayoutParams layoutParams = (AppBarLayout.LayoutParams) mCollapsingToolbar.getLayoutParams();
        layoutParams.setScrollFlags(val ? SCROLL_FLAG_SCROLL | SCROLL_FLAG_ENTER_ALWAYS : 0);
        mCollapsingToolbar.setLayoutParams(layoutParams);
    }

    private void createCategoryMap(List<GetCategoryTreeResponse> getCategoryTreeResponses) {
        if (getCategoryTreeResponses != null) {
            for (GetCategoryTreeResponse category : getCategoryTreeResponses) {
                mCategoryMap.put(category.getKey(), category);
                createCategoryMap(category.getChildren());
            }
        }
    }

    public Map<String, GetCategoryTreeResponse> getCategoryMap() {
        return mCategoryMap;
    }

    @Override
    public boolean isFromCategories() {
        return mFromCategorySearch;
    }

    private void reselectTabIfFacetsAlreadyVisible() {
        if ((mSearchFilterMvpView != null && mSearchFilterMvpView.isViewAttached()) && mSearchFilterMvpView.getIsFacetsVisible()) {
            int tabPosition = -1;
            int prevTabPosition = -1;
            for (int i = 0; i < mFacetFilters.size(); i++) {
                if (tabPosition == -1 && mFacetFilters.get(i).second.equals(mCurrentTabName)) {
                    tabPosition = i;
                }
                if (prevTabPosition == -1 && mFacetFilters.get(i).second.equals(mPreviousTabName)) {
                    prevTabPosition = i;
                }
            }
            tabPosition = tabPosition == -1 ? prevTabPosition : tabPosition;
            if (tabPosition >= 0) {
                onSelectTab(tabPosition);
                retainSelectedTabs();
            }
        }
    }

    @Override
    public void requestCategoryMap(RequestCategoryMapCompletion completion) {
        completion.receivedCategoryMap(mCategoryMap);
    }

    @Override
    public void requestUpdate(Set<String> categoryKeys, Set<SearchChipModel> chipsList,
                              String facetName, String facetValue, String categoryKey,
                              int brandCount, int minPrice, int maxPrice,
                              ArrayList<String> sizeList,
                              SearchOperationType searchOperationType) {
        mGenieBrandCount = brandCount;
        mGenieMinPrice = minPrice;
        mGenieMaxPrice = maxPrice;
        mGenieSizesCount = sizeList.size();
        if (mSaleId != null && !mSaleId.isEmpty()) {
            if (mSaleName == null || mSaleName.isEmpty()) {
                mPresenter.loadSaleBannerDetails(mSaleId);
            }
        }
        mSaleItemsPageNumber = 0;
        this.searchOperationType = searchOperationType;
        mGenieCategory = null;
        mGenieFilters = null;
        mGenieSort = null;
        switch (searchOperationType) {
            case CHECKBOX:
            case UNCHECKBOX:
            case ADJUSTSLIDINGBAR:
                if (facetName.equals(BundleKeys.SORT_FACETFILTER_NAME)) {
                    mGenieSort = facetValue;
                } else {
                    mGenieFilters = facetName + ":" + facetValue;
                }
                break;
            case CATEGORYCLICK:
                mGenieCategory = categoryKey;
                break;
            default:
                break;
        }
        mPresenter.loadSaleItems(createSaleItemsRequest(categoryKeys, 0, chipsList));
    }

    @Override
    public void facetsOpened() {
        enableSaleItemsScroll(false);
    }

    @Override
    public void facetsClosed() {
        enableSaleItemsScroll(true);
        toggleTabSelection();
    }

    private void resetViewBasedOnSourceMode() {
        if (!isViewBound()) {
            return;
        }
        ViewGroup.LayoutParams lp = mAppBar.getLayoutParams();

        switch (mSourceMode) {
            case NORMAL:
                lp.height = WRAP_CONTENT;
                mAppBar.setElevation(getResources().getDimension(R.dimen.margin_extra_small));
                mSaleItemsToolbarField.setVisibility(View.VISIBLE);
                mTabLayout.setVisibility(View.VISIBLE);
                mSaleItemsBackIcon.setVisibility(View.VISIBLE);
                mSaleItemsBackIcon.setEnabled(true);
                if (mBrandNames == null && canShowBrandBubbles()) {
                    mPresenter.loadBrandBubbles();
                }
                break;
            case WISHLIST:
                lp.height = 1;
                mAppBar.setElevation(0);
                mSaleItemsToolbarField.setVisibility(View.GONE);
                mTabLayout.setVisibility(View.GONE);
                mSaleItemsBackIcon.setVisibility(View.INVISIBLE);
                mSaleItemsBackIcon.setEnabled(false);
                mBrandBubblesRecyclerView.setVisibility(View.GONE);
                break;
        }

        mAppBar.setLayoutParams(lp);
    }

    private void showPlaceholder(boolean show) {
        switch (mSourceMode) {
            case NORMAL:
                mWishlistPlaceholder.setVisibility(View.GONE);
                mMainContainer.setVisibility(View.VISIBLE);
                mToolbar.setVisibility(View.VISIBLE);
                if (show) {
                    if (mPlaceholder != null) {
                        mPlaceholder.setVisibility(View.VISIBLE);
                        CommonUtils.fadeInView(mPlaceholder, null);
                    }
                    animateHideRecyclerView();
                } else {
                    if (mPlaceholder != null) {
                        CommonUtils.fadeOutView(mPlaceholder, new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationCancel(Animator animation) {
                                super.onAnimationCancel(animation);
                                if (mPlaceholder != null) {
                                    mPlaceholder.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                if (mPlaceholder != null) {
                                    mPlaceholder.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                    animateShowRecyclerView();
                }
                break;
            case WISHLIST:
                if (mPlaceholder != null) {
                    mPlaceholder.setVisibility(View.GONE);
                }
                if (show) {
                    mMainContainer.setVisibility(View.GONE);
                    mToolbar.setVisibility(View.GONE);
                    animateHideRecyclerView();
                    mWishlistPlaceholder.setVisibility(View.VISIBLE);
                    CommonUtils.fadeInView(mWishlistPlaceholder, null);
                } else {
                    mMainContainer.setVisibility(View.VISIBLE);
                    mToolbar.setVisibility(View.VISIBLE);
                    animateShowRecyclerView();
                    CommonUtils.fadeOutView(mWishlistPlaceholder, new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationCancel(Animator animation) {
                            super.onAnimationCancel(animation);
                            if (mWishlistPlaceholder != null) {
                                mWishlistPlaceholder.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (mWishlistPlaceholder != null) {
                                mWishlistPlaceholder.setVisibility(View.GONE);
                            }
                        }
                    });
                }
                break;
        }
    }

    private void animateShowRecyclerView() {
        if (mSaleItemsRecyclerView == null || mSaleItemsRecyclerView.getVisibility() == View.VISIBLE) {
            return;
        }
        mSaleItemsRecyclerView.setVisibility(View.VISIBLE);
        CommonUtils.fadeInView(mSaleItemsRecyclerView, null);
    }

    private void animateHideRecyclerView() {
        if (mSaleItemsRecyclerView == null || mSaleItemsRecyclerView.getVisibility() == View.GONE) {
            return;
        }
        CommonUtils.fadeOutView(mSaleItemsRecyclerView, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mSaleItemsRecyclerView != null) {
                    mSaleItemsRecyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                if (mSaleItemsRecyclerView != null) {
                    mSaleItemsRecyclerView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void showPlaceholderWithAnimation(boolean show) {
        switch (mSourceMode) {
            case NORMAL:
                mWishlistPlaceholder.setVisibility(View.GONE);
                mToolbar.setVisibility(View.VISIBLE);

                if (mPlaceholder != null && mPlaceholder.getVisibility() == View.VISIBLE && !show) {
                    CommonUtils.fadeOutView(mPlaceholder, new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationCancel(Animator animation) {
                            super.onAnimationCancel(animation);
                            onAnimationEnd(animation);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (mPlaceholder != null) {
                                mPlaceholder.setVisibility(View.GONE);
                                mPlaceholder.setAlpha(1f);
                            }
                            if (mSaleItemsRecyclerView != null) {
                                mSaleItemsRecyclerView.setVisibility(View.VISIBLE);
                                CommonUtils.fadeInView(mSaleItemsRecyclerView, null);
                            }
                        }
                    });
                } else if (mPlaceholder != null && mPlaceholder.getVisibility() == View.GONE && show) {
                    CommonUtils.fadeOutView(mSaleItemsRecyclerView, new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationCancel(Animator animation) {
                            super.onAnimationCancel(animation);
                            onAnimationEnd(animation);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (mSaleItemsRecyclerView != null) {
                                mSaleItemsRecyclerView.setVisibility(View.GONE);
                                mSaleItemsRecyclerView.setAlpha(1f);
                            }
                            if (mPlaceholder != null) {
                                mPlaceholder.setVisibility(View.VISIBLE);
                                CommonUtils.fadeInView(mPlaceholder, null);
                            }
                        }
                    });
                }
                break;
            case WISHLIST:
                if (mPlaceholder != null) {
                    mPlaceholder.setVisibility(View.GONE);
                }

                if (mWishlistPlaceholder.getVisibility() == View.VISIBLE && !show) {
                    CommonUtils.fadeOutView(mWishlistPlaceholder, new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationCancel(Animator animation) {
                            super.onAnimationCancel(animation);
                            onAnimationEnd(animation);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (mWishlistPlaceholder != null) {
                                mWishlistPlaceholder.setVisibility(View.GONE);
                                mWishlistPlaceholder.setAlpha(1f);
                            }
                            if (mMainContainer != null) {
                                mMainContainer.setVisibility(View.VISIBLE);
                            }
                            if (mSaleItemsRecyclerView != null) {
                                mSaleItemsRecyclerView.setVisibility(View.VISIBLE);
                                CommonUtils.fadeInView(mSaleItemsRecyclerView, null);
                            }
                            if (mToolbar != null) {
                                mToolbar.setVisibility(View.VISIBLE);
                                CommonUtils.fadeInView(mToolbar, null);
                            }
                        }
                    });
                } else if (mWishlistPlaceholder.getVisibility() == View.GONE && show) {
                    CommonUtils.fadeOutView(mToolbar, new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationCancel(Animator animation) {
                            super.onAnimationCancel(animation);
                            onAnimationEnd(animation);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (mToolbar != null) {
                                mToolbar.setVisibility(View.GONE);
                                mToolbar.setAlpha(1f);
                            }
                        }
                    });
                    CommonUtils.fadeOutView(mSaleItemsRecyclerView, new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationCancel(Animator animation) {
                            super.onAnimationCancel(animation);
                            onAnimationEnd(animation);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (mSaleItemsRecyclerView != null) {
                                mSaleItemsRecyclerView.setVisibility(View.GONE);
                                mSaleItemsRecyclerView.setAlpha(1f);
                            }
                            if (mMainContainer != null) {
                                mMainContainer.setVisibility(View.GONE);
                            }
                            if (mWishlistPlaceholder != null) {
                                mWishlistPlaceholder.setVisibility(View.VISIBLE);
                                CommonUtils.fadeInView(mWishlistPlaceholder, null);
                            }
                        }
                    });
                }
                break;
        }
    }

    public void animateSkeletonUI() {
        isSkeletonAnimating = true;
        setColumnViewEnabled(false);
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(mActivity, R.anim.layout_sale_list_skeleton);

        ArrayList<SaleItemProduct> nullList = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            nullList.add(null);
        }
        mSaleItemsRecyclerView.setLayoutAnimation(controller);
        mSaleItemsAdapter.addData(nullList, false);
        mSaleItemsRecyclerView.scheduleLayoutAnimation();
        mGridLayoutManager.setScrollEnabled(false);

        mSaleItemsRecyclerView.setLayoutAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isSkeletonAnimating = false;
                setColumnViewEnabled(true);
                if (mSaleItemsRecyclerView != null) {
                    mSaleItemsRecyclerView.setLayoutAnimationListener(null);
                }
                if (mGridLayoutManager != null) {
                    mGridLayoutManager.setScrollEnabled(true);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mSearchFilterSkeleton.setVisibility(View.VISIBLE);
        Animation animaton = AnimationUtils.loadAnimation(mActivity, R.anim.sale_list_skeleton);
        mSearchFilterSkeleton.startAnimation(animaton);
    }

    private void setColumnViewEnabled(boolean enabled) {
        mColumnView.setEnabled(enabled);
        mColumnView.setAlpha(enabled ? 1.0f : 0.5f);
    }

    @Override
    public void onClickFreeDelivery(String deliveryThreshold, String deliveryType) {
        if (deliveryType.equalsIgnoreCase(AppConstants.THRESHOLD_RESTRICT) ||
                deliveryType.equalsIgnoreCase(AppConstants.ORDER_PRICE_RESTRICT)) {

            mActivity.showFreeShippingDialog(deliveryThreshold, mActivity.getShippingTemplateText(),
                    mActivity.getShippingTitle());

        }
    }

    @Override
    public void storeBrandNames(BrandNames brandNames) {
        mBrandNames = brandNames;
        refreshBrandBubbles(mSearchQuery);
    }

    private void refreshBrandBubbles(String searchTerm) {
        if (!isViewAttached() || mBrandNames == null || !canShowBrandBubbles()) {
            return;
        }

        if (mBrandBubblesAdapter != null && mBrandBubblesAdapter.getCurrentSearchTerm().equals(searchTerm)) {
            return;
        }

        final BrandBubblesAdapter.BrandBubbleOnSelectListener onSelectListener = brandName -> {
            if (!isAttached() || mSearchFilterMvpView == null) {
                return;
            }

            mChipFilters.clear();
            mChipFilters.add(new SearchChipModel(BundleKeys.BRANDS_FACETFILTER_NAME, brandName, brandName));
            mSearchFilterMvpView.replaceSearchChipModels(mChipFilters);
            mSaleItemsToolbarField.removeTextChangedListener(mTextWatcher);
            mSaleItemsToolbarField.setText("");
            mSaleItemsToolbarField.addTextChangedListener(mTextWatcher);
            hideKeyboard();
            hideBrandBubbles();
            mPresenter.loadSaleItems(
                    createSaleItemsRequest(
                            mSearchFilterMvpView.getCategoryKeys(),
                            0,
                            mChipFilters));
        };

        if (searchTerm == null || searchTerm.isEmpty()) {
            List<String> brandNames = new ArrayList<>(mBrandNames.getBrandNames());
            brandNames = brandNames.size() > 10 ? brandNames.subList(0, 10) : brandNames;
            mBrandBubblesAdapter = new BrandBubblesAdapter(searchTerm, brandNames, onSelectListener);
            mBrandBubblesRecyclerView.setAdapter(mBrandBubblesAdapter);
            return;
        }

        final int maxResults = mActivity.getResources().getInteger(R.integer.brand_bubbles_max_results);
        if (brandNameHandler != null) {
            brandNameHandler.removeCallbacksAndMessages(null);
        }
        brandNameHandler = new Handler();
        brandNameHandler.post(() -> {
            final List<String> searchResults = mBrandNames.getBrandNamesMatchingString(searchTerm, maxResults);
            final Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(() -> {
                final List<String> brandNames = searchResults.size() > 10 ? searchResults.subList(0, 10) : searchResults;
                mBrandBubblesAdapter = new BrandBubblesAdapter(searchTerm, brandNames, onSelectListener);
                mBrandBubblesRecyclerView.setAdapter(mBrandBubblesAdapter);
            });
        });
    }

    private boolean canShowBrandBubbles() {
        return mSaleId == null || mSaleId.isEmpty();
    }

    private void showBrandBubbles() {
        if (isViewAttached() && canShowBrandBubbles()) {
            mBrandBubblesRecyclerView.setVisibility(View.VISIBLE);
            if (mBrandBubblesRecyclerView.getParent() instanceof LinearLayout) {
                ((LinearLayout) mBrandBubblesRecyclerView.getParent()).invalidate();
            }
        }
    }

    private void hideBrandBubbles() {
        mBrandBubblesRecyclerView.setVisibility(View.GONE);
        if (mBrandBubblesRecyclerView.getParent() instanceof LinearLayout) {
            ((LinearLayout) mBrandBubblesRecyclerView.getParent()).invalidate();
        }
    }
}
