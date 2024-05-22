package au.com.dealsdirect.ui.controller.shops;

import static au.com.dealsdirect.service.datacollection.core.DataCollector.EventParameters.ClickType.BANNER_CLICK;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.google.android.material.appbar.AppBarLayout;
import com.mysale.genie.profiler.Profiler;
import com.paginate.Paginate;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.data.network.model.banner.GetBannerRequest;
import au.com.dealsdirect.data.network.model.banner.GetBannerResponse;
import au.com.dealsdirect.data.network.model.banner.GetBannerResponse.LinkOptions;
import au.com.dealsdirect.data.network.model.banner.GetTopBrandsResponse;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.data.network.model.events.BannerClickEventRequest;
import au.com.dealsdirect.data.network.model.events.FeatureUsageEventRequest;
import au.com.dealsdirect.service.datacollection.core.DataCollector;
import au.com.dealsdirect.service.datacollection.enums.EventParameters;
import au.com.dealsdirect.service.datacollection.enums.EventTypeId;
import au.com.dealsdirect.service.datacollection.enums.Events;
import au.com.dealsdirect.service.datacollection.enums.FeatureUsageEventType;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.categories.CategoriesMvpView;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsController;
import au.com.dealsdirect.ui.controller.shops.adapter.BannersAdapter;
import au.com.dealsdirect.ui.controller.shops.adapter.BannersAdapterHelper;
import au.com.dealsdirect.ui.controller.shops.adapter.BrandsBannersAdapter;
import au.com.dealsdirect.ui.controller.shops.adapter.BrandsBannersAdapterHelper;
import au.com.dealsdirect.ui.controller.shops.adapter.HorizontalScrollingBannerAdapter;
import au.com.dealsdirect.ui.controller.shops.adapter.ResettableDimensions;
import au.com.dealsdirect.ui.custom.SearchEditText;
import au.com.dealsdirect.ui.custom.transitions.SimpleChangeHandler;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.DialogUtils;
import au.com.dealsdirect.utils.PaginateUtils;
import au.com.dealsdirect.utils.ScreenUtils;
import au.com.dealsdirect.utils.module.ControllerFactory;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import butterknife.OnClick;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import io.reactivex.functions.Consumer;

public class ShopsController extends BaseController implements ShopsMvpView, PtrHandler, AppBarLayout.OnOffsetChangedListener {

    private enum BannerDimensionsOverride {
        DONT_OVERRIDE, USE_OLD, USE_NEW
    }

    private static final boolean CIRCULAR_CATEGORY_BANNERS = false;

    private static final boolean BANNER_DIMENSIONS_TOGGLE_BUTTON_ENABLED = false;
    private static final BannerDimensionsOverride OVERRIDE_BANNER_DIMENSIONS_FOR_MOBILE = BannerDimensionsOverride.USE_NEW;
    private static final BannerDimensionsOverride OVERRIDE_BANNER_DIMENSIONS_FOR_TABLET = BannerDimensionsOverride.USE_NEW;
    private static final BannerDimensionsOverride OVERRIDE_BRAND_BANNER_DIMENSIONS_FOR_MOBILE = BannerDimensionsOverride.USE_NEW;
    private static final BannerDimensionsOverride OVERRIDE_BRAND_BANNER_DIMENSIONS_FOR_TABLET = BannerDimensionsOverride.USE_OLD;

    public static final String TAG = "ShopsController";
    public static final String KEY_CATEGORY_ID = "ShopController.KEY_CATEGORY_ID";
    public static final String KEY_CATEGORY_NAME = "ShopController.KEY_CATEGORY_NAME";
    public static final String KEY_CATEGORY_MAP = "ShopController.KEY_CATEGORY_KEY";
    public static final String KEY_BRANDS_ONLY = "ShopController.BRANDS_ONLY";
    private static final String TEXT_ALL = "• All";
    private static final int INITIAL_BANNER_COUNT = 25;

    private static boolean SLIDING_BANNERS_ENABLED = true;
    private static boolean CATEGORY_BANNERS_ENABLED = true;
    private static boolean SPONSORED_BANNERS_ENABLED = false;

    @Inject
    ShopsMvpPresenter<ShopsMvpView> mPresenter;

    @BindView(R.id.controller_shop_banner_recycler)
    RecyclerView shopsControllerBannerRecyclerView;

    @BindView(R.id.partial_toolbar_search_button)
    ImageButton shopsControllerSearchButton;

    @BindView(R.id.partial_toolbar_cart)
    ImageButton mShopsControllerCartButton;

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

    @BindView(R.id.partial_toolbar_badge)
    RelativeLayout mBadge;

    @BindView(R.id.partial_toolbar_badge_text)
    TextView mBadgeText;

    private BannersAdapter mBannersAdapter = null;
    private ResettableDimensions mResettableDimensionsAdapter = null;
    private Paginate.Callbacks mPaginateCallbacks;
    private Paginate mPaginateManager = null;

    private int page = 0;
    private boolean loadingInProgress = false;
    private boolean hasLoadedAllItems = false;

    private String bannerGroupType = "";
    private int bannerOffset = 0;
    private int bannerLimit = 0;

    private String mCategoryID;
    private String mCategoryName;
    private String mCategoryKey;

    private boolean mIsBrandsOnly = false;

    private boolean mIsDeeplink = false;
    private boolean mHasSavedInstance = false;

    private GridLayoutManager mLayoutManager;

    private List<GetCategoryTreeResponse> mPreLoadedCategories = new LinkedList<>();
    private List<GetBannerResponse.Group> mSales = new LinkedList<>();
    private List<GetBannerResponse.Group> mSalesFromCache = null;
    private Map<String, List<GetCategoryTreeResponse>> mCategoryMap = new HashMap<>();

    private boolean isRefreshShop = false;

    private int mVerticalOffset;
    private boolean mIsRecyclerViewScrollIdle;

    private boolean mIsChangeInProgress = false;

    private boolean shouldShowCartButton = false;

    private final BannersAdapterHelper bannersAdapterHelper = new BannersAdapterHelper() {
        @Override
        public int getBannerColumnCount() {
            return mPresenter.getBannerColumnCount();
        }

        @Override
        public boolean isGoogleAdsEnabled() {
            return mPresenter.isGoogleAdsEnabled();
        }

        @Override
        public void onClickFreeDelivery(String deliveryThreshold, String deliveryType) {
            if (deliveryType.equalsIgnoreCase(AppConstants.THRESHOLD_RESTRICT) || deliveryType.equalsIgnoreCase(AppConstants.ORDER_PRICE_RESTRICT)) {

                mActivity.showFreeShippingDialog(deliveryThreshold, mActivity.getShippingTemplateText(), mActivity.getShippingTitle());
            }
        }

        @Override
        public void specialBannerEvent(BannerClickEventRequest bannerClickEventRequest, String saleName) {
            HashMap<String, Object> eventParameters = new HashMap<>();
            eventParameters.put(DataCollector.EventParameters.BANNER_TYPE, "SliderBanners");
            eventParameters.put(DataCollector.EventParameters.SALE_NAME, saleName);
            eventParameters.put(DataCollector.EventParameters.APP_CONTEXT, mActivity);
            eventParameters.put(DataCollector.EventParameters.SCREEN_NAME, "SaleBanners");
            eventParameters.put(DataCollector.EventParameters.BANNER_CLICK_REQUEST, bannerClickEventRequest);
            DataCollector.logEvent(Events.BannerClickEvent, eventParameters);
        }

        @Override
        public void onBannerTapped(GetBannerResponse.Banner banner, int position, String imgUrl, Events bannerType, String saleName) {
            if (banner.getLinkOptions() != null && banner.getLinkOptions().getLinkOptionType() == LinkOptions.LinkOptionType.CATEGORY) {
                SaleItemsController.Parameters.FromCategoryBannerClick parameters = new SaleItemsController.Parameters.FromCategoryBannerClick(banner.getLinkOptions());

                SaleItemsController controller = SaleItemsController.newInstance(parameters);

                getRouter().pushController(RouterTransaction.with(controller).tag(mActivity.getString(R.string.sale_items_controller_tag)).pushChangeHandler(new HorizontalChangeHandler()).popChangeHandler(new HorizontalChangeHandler()));
            } else if (banner.getBannerType() != null && banner.getBannerType().equals("brandBanner")) {
                // support for no linkOptions category banner

                Uri uri = Uri.parse(banner.getLink());
                String id = uri.getLastPathSegment();

                specialBannerEvent(createBannerClickEventRequest(EventParameters.SpecialBannerType.SHOP_BY_CATEGORY, banner, position), saleName);

                onBannerClicked(id);
            } else if (banner.getLink() != null && !banner.getLink().isEmpty()) {
                String link = banner.getLink();
                Pattern pattern = Pattern.compile("(?<=/s/)([^?\\n\\r])+");
                Matcher matcher = pattern.matcher(link);
                String title = " ";
                if (banner.getDescription() != null && !banner.getDescription().isEmpty()) {
                    title = banner.getDescription();
                }

                specialBannerEvent(createBannerClickEventRequest(EventParameters.SpecialBannerType.SALE_CAMPAIGN, banner, position), saleName);

                if (matcher.find()) {
                    String id = matcher.group();
                    onBannerClicked(id, title, banner.getId(), position, imgUrl, banner.getEndDate(), banner.getIsAvailable(), banner.getLinkOptions());

                } else {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(banner.getLink()));
                    mActivity.startActivity(browserIntent);
                }
            } else {

                if (bannerType.equals(Events.RegularBannerClickEvent)) {
                    HashMap<String, Object> eventParameters = new HashMap<>();
                    eventParameters.put(DataCollector.EventParameters.BANNER_TYPE, "RegularBanners");
                    eventParameters.put(DataCollector.EventParameters.SALE_NAME, saleName);
                    eventParameters.put(DataCollector.EventParameters.APP_CONTEXT, mActivity);
                    eventParameters.put(DataCollector.EventParameters.SCREEN_NAME, "SaleBanners");
                    DataCollector.logEvent(Events.BannerClickEvent, eventParameters);
                } else if (bannerType.equals(Events.SponsoredBannerClickEvent)) {
                    specialBannerEvent(createBannerClickEventRequest(EventParameters.SpecialBannerType.SPONSORED, banner, position), saleName);
                }

                onBannerClicked(banner.getDestinationId(), banner.getDescription(), banner.getId(), position, imgUrl, banner.getEndDate(), banner.getIsAvailable(), banner.getLinkOptions());

            }
        }

        private BannerClickEventRequest createBannerClickEventRequest(EventParameters.SpecialBannerType type, GetBannerResponse.Banner banner, int position) {
            BannerClickEventRequest bannerClickEventRequest = new BannerClickEventRequest();
            bannerClickEventRequest.setEventType(EventTypeId.EVENT_SLIDER_BANNER);

            BannerClickEventRequest.BannerInfo bannerInfo = new BannerClickEventRequest.BannerInfo();
            bannerInfo.setBannerType(type.getValue());
            bannerInfo.setSaleId(banner.getId());
            bannerInfo.setLink(banner.getLink());
            bannerInfo.setPos(position);
            bannerInfo.setCategory("");

            bannerClickEventRequest.setBannerInfo(bannerInfo);

            return bannerClickEventRequest;
        }
    };

    @Override
    protected void onAttach(@NonNull View view) {
        mPresenter.onAttach(this);
        assert (mActivity) != null;

        mShopPtrLayout.setPtrHandler(this);
        mShopAppBarLayout.addOnOffsetChangedListener(this);

        super.onAttach(view);
    }

    public static ShopsController newInstance() {
        return new ShopsController(new BundleBuilder(new Bundle()).build());
    }

    public static ShopsController instanceWithCategoryFilter(String id, String key) {
        Bundle bundle = new BundleBuilder(new Bundle())
                .putString(ShopsController.KEY_CATEGORY_ID, id)
                .putString(ShopsController.KEY_CATEGORY_MAP, key)
                .putString(ShopsController.KEY_CATEGORY_NAME, key)
                .build();
        return new ShopsController(bundle);
    }

    public static ShopsController instanceWithBrandsOnlyFilter() {
        Bundle bundle = new BundleBuilder(new Bundle())
                .putBoolean(ShopsController.KEY_BRANDS_ONLY, true).build();
        return new ShopsController(bundle);
    }

    public ShopsController(Bundle args) {
        super(args);
        mCategoryID = getArgs().getString(KEY_CATEGORY_ID);
        mCategoryName = getArgs().getString(KEY_CATEGORY_NAME);
        mCategoryKey = getArgs().getString(KEY_CATEGORY_MAP);
        mIsBrandsOnly = getArgs().getBoolean(KEY_BRANDS_ONLY, false);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_shop, container, false);

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);

        return view;
    }

    private void showProductList() {
        SaleItemsController.Parameters.FromShopSearch parameters = new SaleItemsController.Parameters.FromShopSearch(null, null);

        SaleItemsController controller = SaleItemsController.newInstance(parameters);

        getRouter().pushController(RouterTransaction.with(controller).tag(getResources().getString(R.string.sale_items_controller_tag)).pushChangeHandler(new HorizontalChangeHandler()).popChangeHandler(new HorizontalChangeHandler()));
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        mActivity.getProfiler().setStartLogTime(DataCollector.EventParameters.CustomEventType.CV_SALEBANNERS.getValue());
        setUp(view);
        mShopPtrLayout.setEnabled(mActivity.getResources().getBoolean(R.bool.is_pull_to_refresh_enabled));
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

        if (mBannersAdapter != null) {
            mBannersAdapter.restartHorizontalViewHolders();
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

        if (mBannersAdapter != null) {
            mBannersAdapter.stopHorizontalViewHolders();
        }
    }

    @Override
    public void onViewDidAppear(Controller previousController) {
        super.onViewDidAppear(previousController);
        if (!mIsBrandsOnly && !(previousController instanceof SaleItemsController) && !(mActivity.getMainController().getCurrentViewPagerController() instanceof ShopsController)) {
            mPresenter.loadShopsBanner(createBannerRequest(mCategoryID, bannerOffset, bannerLimit));
            loadSlidingBanners();
            loadSponsoredBanners();
            loadCategoryBanners();
            mActivity.getMainController().setSavedCurrentItem();
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

        resetDimensionToggleButton(getPrefersOldShopBannerDimensions());
        shopsControllerBannerRecyclerView.setBackgroundColor(mActivity.getResources().getColor(getPrefersOldShopBannerDimensions() ? R.color.background_default : R.color.white));

        hideKeyboard();

        setupBannersView();

        mShopAppBarLayout.setExpanded(true, false);

        mSearchBarEditText.setFocusable(false);
        mSearchBarEditText.setOnClickListener(view1 -> {
            showProductList();
        });
        mSearchBarEditText.setHint(getResource().getString(R.string.search_tag));

        if (!mIsBrandsOnly) {
            mPaginateCallbacks = new Paginate.Callbacks() {
                @Override
                public void onLoadMore() {
                    // Load next page of data (e.g. network or database)
                    page++;
                    bannerOffset += INITIAL_BANNER_COUNT;
                    loadingInProgress = true;
                    GetBannerRequest request;
                    if (mIsDeeplink) {
                        request = createDeepLinkBannerRequest(mCategoryID, bannerOffset, bannerLimit);
                    } else {
                        request = createBannerRequest(mCategoryID, bannerOffset, bannerLimit);
                    }
                    mPresenter.loadShopsBanner(request);
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

            if (mBannersAdapter != null) {
                shopsControllerBannerRecyclerView.addItemDecoration(new StickyRecyclerHeadersDecoration(mBannersAdapter.getStickyRecyclerHeadersAdapter()));
            }
        } else {
            mShopsControllerToolbarTextView.setVisibility(View.VISIBLE);
            mShopsControllerToolbarTextView.setText("Brands");
            mShopsControllerToolbarLogo.setVisibility(View.GONE);
            mShopsControllerCartButton.setImageDrawable(mActivity.getDrawable(R.drawable.ic_new_checkout));
            mShopsControllerCartButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
            shopsControllerSearchButton.setImageDrawable(mActivity.getDrawable(R.drawable.ic_search));
            shopsControllerSearchButton.setVisibility(View.VISIBLE);
            shopsControllerSearchButton.setImageTintList(ColorStateList.valueOf(mActivity.getResources().getColor(R.color.black)));
            mSearchBarEditText.setVisibility(View.GONE);
            shouldShowCartButton = true;
        }

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

        if (mSales.isEmpty() && (mSalesFromCache == null || mSalesFromCache.isEmpty()) && !mHasSavedInstance) {
            shopsControllerBannerRecyclerView.setVisibility(View.GONE);
        } else if (!mIsBrandsOnly) {
            shopsControllerBannerRecyclerView.setAdapter(mBannersAdapter);
            mPresenter.loadShopsBanner(createBannerRequest(mCategoryID, bannerOffset, bannerLimit));
            loadSlidingBanners();
            loadSponsoredBanners();
            loadCategoryBanners();
        }

        if (mPreLoadedCategories.size() == 0) {
            mPresenter.loadCategoryTree();
        }

        setupPtrHeader();
        setRetainViewMode(RetainViewMode.RETAIN_DETACH);

        if (mHasSavedInstance) {
            mActivity.getMainController().setSavedCurrentItem();
            mHasSavedInstance = false;
        }

        if (mIsBrandsOnly) {
            mPresenter.loadTopBrands();
        } else {
            goToSalesFromCategories(mCategoryID, mCategoryKey);
        }

        mPresenter.loadLeaderboardBanner(mCategoryID);
    }

    private void setupBannersView() {
        int orientation = ScreenUtils.getOrientation(mActivity);
        if (mResettableDimensionsAdapter != null && ((!(mResettableDimensionsAdapter instanceof BrandsBannersAdapter) && mIsBrandsOnly) || (mResettableDimensionsAdapter instanceof BrandsBannersAdapter && !mIsBrandsOnly))) {
            mResettableDimensionsAdapter = null;
        }
        if (mResettableDimensionsAdapter == null) {
            if (mIsBrandsOnly) {
                mBannersAdapter = null;
                BrandsBannersAdapter brandsBannersAdapter = new BrandsBannersAdapter(mActivity, new ArrayList<>(), orientation, getPrefersOldShopBannerDimensions(), mPresenter.isTablet(), new BrandsBannersAdapterHelper() {
                    @Override
                    public int getBannerColumnCount() {
                        return mPresenter.getBannerColumnCount();
                    }

                    @Override
                    public void onBannerClick(GetTopBrandsResponse brand) {

                    }

                    @Override
                    public void onInfoClick(String title, String description) {

                    }
                });
                shopsControllerBannerRecyclerView.setAdapter(brandsBannersAdapter);
                mResettableDimensionsAdapter = brandsBannersAdapter;
            } else {
                mBannersAdapter = new BannersAdapter(mActivity, new ArrayList<>(), orientation, getPrefersOldShopBannerDimensions(), mPresenter.isTablet(), bannersAdapterHelper);
                shopsControllerBannerRecyclerView.setAdapter(mBannersAdapter);
                mResettableDimensionsAdapter = mBannersAdapter;
            }
        } else {
            mResettableDimensionsAdapter.setupDimensions(orientation);
        }

        resetLayoutManager();
    }

    private void resetLayoutManager() {
        mLayoutManager = new GridLayoutManager(mActivity, mResettableDimensionsAdapter.getNumberOfColumns(), RecyclerView.VERTICAL, false) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };

        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mResettableDimensionsAdapter instanceof BannersAdapter) {
                    switch (BannersAdapter.removeViewHolderTypeModifiers(((BannersAdapter) mResettableDimensionsAdapter).getItemViewType(position))) {
                        case BannersAdapter.VIEW_HOLDER_TYPE_SPACER:
                        case BannersAdapter.VIEW_HOLDER_TYPE_LEADER_BANNER:
                        case BannersAdapter.VIEW_HOLDER_TYPE_PROMO_BANNER:
                        case BannersAdapter.VIEW_HOLDER_TYPE_SLIDING_BANNER:
                        case BannersAdapter.VIEW_HOLDER_TYPE_CATEGORY_BANNER:
                        case BannersAdapter.VIEW_HOLDER_TYPE_SPONSORED_BANNER:
                        case BannersAdapter.VIEW_HOLDER_TYPE_FOOTER:
                            return mResettableDimensionsAdapter.getNumberOfColumns();
                        default:
                            return 1;
                    }
                } else {
                    return 1;
                }
            }
        });

        shopsControllerBannerRecyclerView.setLayoutManager(mLayoutManager);
    }

    private void setupPtrHeader() {
        mShopPtrLayout.getHeader().setProgressIcon(getResources().getDrawable(R.drawable.ic_loader_logo));

        mShopPtrLayout.getHeader().setPullProgressbar(getResources().getDrawable(R.drawable.bg_progress_bar));

        mShopPtrLayout.getHeader().setProgressBar(ColorStateList.valueOf(getResources().getColor(R.color.progress_loader_stroke)));

    }

    @Override
    public void unBindPaginate() {
        if (mPaginateManager != null) {
            mPaginateManager.unbind();
        }
    }

    private void onBannerClicked(String saleId, String bannerTitle, String bannerId, int position, String imageUrl, String endDate, boolean isAvailable, LinkOptions linkOptions) {

        SaleItemsController.Parameters.FromBannerClick parameters = new SaleItemsController.Parameters.FromBannerClick(bannerTitle, saleId, bannerId, imageUrl, endDate, linkOptions, position);

        SaleItemsController controller = SaleItemsController.newInstance(parameters);

        HashMap<String, Object> eventParameters = new HashMap<>();
        eventParameters.put(DataCollector.EventParameters.TYPE, BANNER_CLICK);
        eventParameters.put(DataCollector.EventParameters.ITEM_ARRAY_POSITION, position);
        eventParameters.put(DataCollector.EventParameters.APP_CONTEXT, mActivity);
        eventParameters.put(DataCollector.EventParameters.SCREEN_NAME, ShopsController.class.getSimpleName());
        DataCollector.logEvent(Events.clicksEvent, eventParameters);

        List<String> names = new ArrayList<>();
        names.add(bannerId + position);

        // Check if sale is available
        //TODO: Need computation for date and time when sale response is cached
        if (isAvailable) {
            getRouter().pushController(RouterTransaction.with(controller).tag(mActivity.getString(R.string.sale_items_controller_tag)).pushChangeHandler(new HorizontalChangeHandler()).popChangeHandler(new HorizontalChangeHandler()));
        } else {
            DialogUtils.showYesDialog(mActivity, "", "Sale is currently closed", "OK", (dialogInterface, i) -> dialogInterface.dismiss());
        }
    }

    private void onBannerClicked(String categoryId) {
        final CategoriesMvpView categoriesMvpView = mActivity.getCategoriesController();
        if (categoriesMvpView == null) {
            return;
        }
        categoriesMvpView.showSaleItems(categoryId);
        mActivity.getMainController().showCategoryController();
    }

    @Override
    public boolean isChangeInProgress() {
        return mIsChangeInProgress;
    }

    @OnClick(R.id.partial_toolbar_cart)
    void onClickCart() {
        if (!shouldShowCartButton) {
            getRouter().handleBack();
            return;
        }
        Controller controller = mPresenter.isTablet() ? ControllerFactory.getInstance(GateKeeper.Destination.CHECKOUT_HOST) : ControllerFactory.getInstance(GateKeeper.Destination.CHECKOUT);

        if (!mActivity.isAuthorized()) {
            mActivity.showLoginController(getRouter(), new AuthHandler() {
                @Override
                public void success() {
                    getRouter().popCurrentController();
                    getRouter().pushController(RouterTransaction.with(controller).tag(controller.getClass().getName()).pushChangeHandler(new HorizontalChangeHandler()).popChangeHandler(new HorizontalChangeHandler()));
                }

                @Override
                public void error() {

                }
            });
        } else if (mActivity.isAuthorized()) {
            getRouter().pushController(RouterTransaction.with(controller).tag(controller.getClass().getName()).pushChangeHandler(new HorizontalChangeHandler()).popChangeHandler(new HorizontalChangeHandler()));
        }
    }

    @Override
    public boolean handleBack() {
        mPresenter.cancelRequest();
        return false;
    }

    private final Runnable onClickLogoRunnable = () -> {
        shopsControllerBannerRecyclerView.stopScroll();
        shopsControllerBannerRecyclerView.scrollToPosition(0);
    };

    @OnClick(R.id.partial_toolbar_logo)
    void onClickLogo() {
        shopsControllerBannerRecyclerView.smoothScrollToPosition(0);
        if (shopsControllerBannerRecyclerView.getHandler() != null) {
            shopsControllerBannerRecyclerView.getHandler().removeCallbacks(onClickLogoRunnable);
            shopsControllerBannerRecyclerView.getHandler().postDelayed(onClickLogoRunnable, 500);
        }
    }

    @SuppressWarnings({"ConstantConditions", "deprecation"})
    @OnClick(R.id.partial_toolbar_search_button)
    void onSearchClick() {
        showProductList();
    }

    private void resetDimensionToggleButton(boolean useOldDimensions) {
        shopsControllerSearchButton.setVisibility(!BANNER_DIMENSIONS_TOGGLE_BUTTON_ENABLED || mPresenter.isTablet() || mIsBrandsOnly ? View.GONE : View.VISIBLE);
        shopsControllerSearchButton.setImageResource(useOldDimensions ? R.drawable.shop_banner_toggle_list : R.drawable.shop_banner_toggle_grid);
    }

    private void logDimensionsToggleFeatureUsageEvent() {
        FeatureUsageEventRequest featureUsageEventRequest = new FeatureUsageEventRequest();
        featureUsageEventRequest.setEventType(EventTypeId.EVENT_FEATURE_USAGE);
        featureUsageEventRequest.setFeatureInfo(new FeatureUsageEventRequest.FeatureInfo(FeatureUsageEventType.ShopPage.TOGGLE_SALE_BANNER_SIZE));

        HashMap<String, Object> eventParameters = new HashMap<>();
        eventParameters.put(DataCollector.EventParameters.APP_CONTEXT, mActivity);
        eventParameters.put(DataCollector.EventParameters.SCREEN_NAME, SaleItemsController.class.getSimpleName());
        eventParameters.put(DataCollector.EventParameters.FEATURE_EVENT_REQUEST, featureUsageEventRequest);

        DataCollector.logEvent(Events.FeatureUsageEvent, eventParameters);
    }

    @Override
    public void showShopBanners(GetBannerResponse getBannerResponses, String categoryID, boolean isFromCache) {

        if (mCategoryID != null && categoryID != null && !mCategoryID.equals(categoryID)) {
            return;
        }

        hasLoadedAllItems = false;
        mActivity.getProfiler().setEndLogTime(DataCollector.EventParameters.CustomEventType.CV_SALEBANNERS.getValue());
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(DataCollector.EventParameters.MILLISECONDS, Profiler.getTotalTime(DataCollector.EventParameters.CustomEventType.CV_SALEBANNERS.getValue()));
        parameters.put(DataCollector.EventParameters.APP_CONTEXT, mActivity);
        parameters.put(DataCollector.EventParameters.SCREEN_NAME, ShopsController.class.getSimpleName());
        DataCollector.logEvent(Events.CVSaleBanners, parameters);

        shopsControllerBannerRecyclerView.setVisibility(View.VISIBLE);

        loadingInProgress = false;

        List<GetBannerResponse.Group> moreGroups = getBannerResponses.getGroups();
        boolean shouldRestartPaginateManager = false;

        if (isFromCache && mSalesFromCache == null) {
            if (mBannersAdapter != null) {
                mSales = new ArrayList<>(mBannersAdapter.getData());
            } else {
                mSales = new ArrayList<>();
            }
        }

        if (page == 0 || isRefreshShop) {
            if (mBannersAdapter != null) {
                mBannersAdapter.replace(moreGroups);
            }
            if (mPaginateManager != null) {
                mPaginateManager.unbind();
            }
            if (!isFromCache) {
                shouldRestartPaginateManager = true;
            }
            if (mPaginateManager != null) {
                mPaginateManager.setHasMoreDataToLoad(false);
            }
            isRefreshShop = false;
        } else {
            if (mSalesFromCache != null && !isFromCache) {
                mSales.addAll(moreGroups);
                if (mBannersAdapter != null) {
                    mBannersAdapter.replace(mSales);
                }
            } else {
                if (mBannersAdapter != null) {
                    mBannersAdapter.addAll(moreGroups);
                }
            }

            if (moreGroups.isEmpty()) {
                hasLoadedAllItems = true;
                if (mPaginateManager != null) {
                    mPaginateManager.setHasMoreDataToLoad(false);
                }
            }
        }

        if (isFromCache && mBannersAdapter != null) {
            mSalesFromCache = mBannersAdapter.getData();
        } else {
            mSalesFromCache = null;
            if (mBannersAdapter != null) {
                mSales = mBannersAdapter.getData();
            } else {
                mSales = new ArrayList<>();
            }
        }

        if (shouldRestartPaginateManager) {
            // has to moved here because loadMore() gets called too early otherwise
            mPaginateManager = PaginateUtils.init(shopsControllerBannerRecyclerView, mPaginateCallbacks);
        }

        onRefreshEnd();
    }

    @Override
    public void showSlidingBanners(GetBannerResponse getBannerResponses) {
        if (mBannersAdapter == null) {
            setupBannersView();
        }
        if (mBannersAdapter == null) {
            return;
        }

        HorizontalScrollingBannerAdapter adapter = null;
        List<GetBannerResponse.Banner> slidingBanners = new ArrayList<>();

        if (getBannerResponses != null) {
            List<GetBannerResponse.Group> groups = getBannerResponses.getGroups();
            if (groups != null) {
                for (GetBannerResponse.Group group : groups) {
                    if (group.getType().equalsIgnoreCase("campaign")) {
                        slidingBanners.addAll(group.getBanners());
                    }
                }
            }
            if (!slidingBanners.isEmpty()) {
                adapter = new HorizontalScrollingBannerAdapter();
                adapter.setDataSource(slidingBanners);
                adapter.setBannerViewType(HorizontalScrollingBannerAdapter.BannerViewType.PromoBanner);
                adapter.setImageResolutionOverride(mActivity.getResources().getInteger(R.integer.banner_resolution_override));
            }
        }
        mBannersAdapter.setSlidingBannersAdapter(adapter);
    }

    @Override
    public void showSponsoredBanners(GetBannerResponse getBannerResponses) {
        if (mBannersAdapter == null) {
            setupBannersView();
        }
        if (mBannersAdapter == null) {
            return;
        }

        HorizontalScrollingBannerAdapter adapter = null;
        if (getBannerResponses != null) {
            List<GetBannerResponse.Banner> sponsoredBanners = new ArrayList<>();
            List<GetBannerResponse.Group> groups = getBannerResponses.getGroups();
            if (groups != null) {
                for (GetBannerResponse.Group group : groups) {
                    List<GetBannerResponse.Banner> banners = group.getBanners();
                    if (banners != null) {
                        for (GetBannerResponse.Banner banner : banners) {
                            List<String> categories = banner.getCategories();
                            for (String category : categories) {
                                if (category.equalsIgnoreCase("sponsored")) {
                                    sponsoredBanners.add(banner);
                                }
                            }
                        }
                    }
                }
            }
            if (!sponsoredBanners.isEmpty()) {
                adapter = new HorizontalScrollingBannerAdapter();
                adapter.setDataSource(sponsoredBanners);
                adapter.setTitle(null);
                adapter.setShouldRepeatCellsToFillWidth(false);
            }
        }
        mBannersAdapter.setSponsoredBannersAdapter(adapter);
    }

    @Override
    public void showCategoryBanners(GetBannerResponse getBannerResponses) {
        if (mBannersAdapter == null) {
            setupBannersView();
        }
        if (mBannersAdapter == null) {
            return;
        }

        HorizontalScrollingBannerAdapter adapter = null;
        String title = null;
        if (getBannerResponses != null) {
            List<GetBannerResponse.Banner> categoryBanners = new ArrayList<>();
            List<GetBannerResponse.Group> groups = getBannerResponses.getGroups();
            if (groups != null) {
                for (GetBannerResponse.Group group : groups) {
                    if (group.getType().equals("brand")) {
                        List<GetBannerResponse.Banner> banners = group.getBanners();
                        if (banners != null) {
                            for (GetBannerResponse.Banner banner : banners) {
                                categoryBanners.add(banner);
                                if (banner.getBannerType().equals("brandBanner")) {
                                    categoryBanners.add(banner);
                                }
                            }
                            title = group.getTitle();
                        }
                    }
                }
            }
            if (!categoryBanners.isEmpty()) {
                adapter = new HorizontalScrollingBannerAdapter();
                adapter.setDataSource(categoryBanners);
                adapter.setTitle(title.toUpperCase());
                adapter.setShouldShowTitle(CIRCULAR_CATEGORY_BANNERS);
                adapter.setUseCircularImage(CIRCULAR_CATEGORY_BANNERS);
                adapter.setImageResolutionOverride(mActivity.getResources().getInteger(R.integer.category_banner_resolution_override));
                if (CIRCULAR_CATEGORY_BANNERS) {
                    adapter.setBackgroundColorOverride(mActivity.getResources().getColor(R.color.background_default));
                }
            }
        }
        mBannersAdapter.setCategoryBannersAdapter(adapter);
    }

    @Override
    public void refreshContents() {
        super.refreshContents();
        if (isViewAttached() && mSales.isEmpty() && (mSalesFromCache == null || mSalesFromCache.isEmpty()) && !mHasSavedInstance && shopsControllerBannerRecyclerView != null) {
            shopsControllerBannerRecyclerView.setVisibility(View.GONE);
            if (mIsBrandsOnly) {
                mPresenter.loadTopBrands();
            } else {
                mPresenter.loadShopsBanner(createBannerRequest(mCategoryID, bannerOffset, INITIAL_BANNER_COUNT));
                loadSlidingBanners();
                loadSponsoredBanners();
                loadCategoryBanners();
            }
        }
        resetBannerLayout();
        if (mShopAppBarLayout != null) {
            mShopAppBarLayout.setExpanded(true, true);
        }
    }

    @Override
    public void storeCategories(List<GetCategoryTreeResponse> categories) {
        mPreLoadedCategories = categories;
        createCategoryMap(mPreLoadedCategories);
    }

    @Override
    public void showTopBrands(List<GetTopBrandsResponse> topBrands) {
        if (!mIsBrandsOnly) {
            return;
        }

        // TopBrands has no pagination yet??
        unBindPaginate();
        mPaginateManager = null;
        mPaginateCallbacks = null;
        loadingInProgress = false;
        hasLoadedAllItems = true;

        BrandsBannersAdapter adapter = new BrandsBannersAdapter(mActivity, topBrands, ScreenUtils.getOrientation(mActivity), getPrefersOldShopBannerDimensions(), mPresenter.isTablet(), new BrandsBannersAdapterHelper() {
            @Override
            public int getBannerColumnCount() {
                return mPresenter.getBannerColumnCount();
            }

            @Override
            public void onBannerClick(GetTopBrandsResponse brand) {
                SaleItemsController.Parameters.FromTopBrands parameters = new SaleItemsController.Parameters.FromTopBrands(brand.getName());

                SaleItemsController controller = SaleItemsController.newInstance(parameters);

                getRouter().pushController(RouterTransaction.with(controller).tag(getResources().getString(R.string.sale_items_controller_tag)).pushChangeHandler(new HorizontalChangeHandler()).popChangeHandler(new HorizontalChangeHandler()));
            }

            @Override
            public void onInfoClick(String title, String description) {
                mActivity.showInfoDialog(title, description);
            }
        });
        mResettableDimensionsAdapter = adapter;
        shopsControllerBannerRecyclerView.setAdapter(adapter);
        shopsControllerBannerRecyclerView.setVisibility(View.VISIBLE);
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

    @Override
    public void onError(String message) {
        super.onError(message);

        bannerOffset = Math.max(0, bannerOffset - INITIAL_BANNER_COUNT);
        page = Math.max(0, page - 1);

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

        if (list != null) {
            for (GetCategoryTreeResponse i : list) {

                int childrenSize = i.getChildren().size();
                if (childrenSize != 0) {
                    addToMap(i.getChildren());
                }

                mCategoryMap.put(i.getKey(), i.getChildren());
            }
        }
    }

    private List<GetCategoryTreeResponse> fillCategoryContent() {
        return mCategoryMap.get("shop");
    }

    public void goToItemsFromCategories(Bundle bundle) {
        //noinspection ConstantConditions
        getRouter().pushController(RouterTransaction.with(new SaleItemsController(bundle)).tag(getResources().getString(R.string.sale_items_controller_tag)).pushChangeHandler(new HorizontalChangeHandler()).popChangeHandler(new HorizontalChangeHandler()));
    }

    public void goToSalesFromCategories(String id, String key) {
        if (id == null) {
            key = null;
        }
        resetShopsBanners(id);
        mCategoryKey = key;
        mCategoryName = key;

        if (key != null) {
            mPresenter.loadShopsBanner(createBannerRequest(id, bannerOffset, bannerLimit), false);
            if (mShopsControllerToolbarLogo != null) {
                mShopsControllerToolbarLogo.setVisibility(View.GONE);
            }
            mShopsControllerToolbarTextView.setVisibility(View.VISIBLE);
            mShopsControllerCartButton.setImageDrawable(mActivity.getDrawable(R.drawable.ic_pink_chevron));
            mShopsControllerCartButton.setScaleType(ImageView.ScaleType.CENTER);
            mShopsControllerToolbarTextView.setText(getCategoryParentKey(key));
        } else {
            assert (mActivity) != null;
            showLogoHeader();
            mPresenter.loadShopsBanner(createBannerRequest(id, bannerOffset, bannerLimit), false);
        }
        loadSlidingBanners();
        loadSponsoredBanners();
        loadCategoryBanners();
    }

    private void resetShopsBanners(String categoryID) {
        mPresenter.onAttach(this);
        hasLoadedAllItems = true;

        //reset adapter
        if (mBannersAdapter != null) {
            mBannersAdapter.clear();
        }

        if (shopsControllerBannerRecyclerView != null) {
            shopsControllerBannerRecyclerView.setVisibility(View.GONE);
            shopsControllerBannerRecyclerView.getRecycledViewPool().clear();
            if (!shopsControllerBannerRecyclerView.isComputingLayout() && mBannersAdapter != null) {
                mBannersAdapter.notifyDataSetChanged();
            }
        }

        mSales.clear();
        mSalesFromCache = null;

        //reset for values for Get_sales API call
        page = 0;
        bannerOffset = 0;
        mCategoryID = categoryID;

        int orientation = ScreenUtils.getOrientation(mActivity);
        mResettableDimensionsAdapter.setupDimensions(orientation);
    }

    public String getCategoryParentKey(String saleCategoryKey) {
        return getBoolean(R.bool.is_category_all_enabled) ? saleCategoryKey + TEXT_ALL : saleCategoryKey;
    }

    public void loadShopBanners() {
        if (isAttached()) {
            showLogoHeader();
        }
        if (mIsBrandsOnly) {
            mPresenter.loadTopBrands();
        } else {
            mPresenter.loadShopsBanner(createBannerRequest("", 0, 0));
            loadSlidingBanners();
            loadSponsoredBanners();
            loadCategoryBanners();
        }
    }

    public void clearHorizontalBanners() {
        if (mBannersAdapter != null) {
            mBannersAdapter.setSlidingBannersAdapter(null);
            mBannersAdapter.setCategoryBannersAdapter(null);
            mBannersAdapter.setSponsoredBannersAdapter(null);
        }
    }

    public void loadSlidingBanners() {
        if (!SLIDING_BANNERS_ENABLED) {
            return;
        }

        GetBannerRequest request = new GetBannerRequest();
        if (mCategoryID != null && !mCategoryID.isEmpty()) {
            request.setCategory(mCategoryID);
        }
        request.setOffset(null);
        request.setLimit("10");
        request.setIncludeCampaignBanners(true);
        request.setIncludePromoSales(true);

        mPresenter.loadSlidingBanners(request);
    }

    public void loadSponsoredBanners() {
        if (!SPONSORED_BANNERS_ENABLED) {
            return;
        }

        GetBannerRequest request = new GetBannerRequest();
        request.setCategory("U3BvbnNvcmVk");
        request.setOffset(null);
        request.setLimit("999");
        request.setIncludeCampaignBanners(true);
        request.setCategory(mCategoryID);

        mPresenter.loadSponsoredBanners(request);
    }

    public void loadCategoryBanners() {
        if (!CATEGORY_BANNERS_ENABLED) {
            return;
        }

        GetBannerRequest request = new GetBannerRequest();
        request.setOffset(null);
        request.setLimit("50");
        request.setBannergroups("7,8,9");
        request.setCategory(mCategoryID);

        mPresenter.loadCategoryBanners(request);
    }

    private void showLogoHeader() {
        mShopsControllerToolbarLogo.setVisibility(View.VISIBLE);
        mShopsControllerToolbarTextView.setVisibility(View.GONE);
        mShopsControllerCartButton.setImageDrawable(mActivity.getDrawable(R.drawable.ic_new_checkout));
        mShopsControllerCartButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
        shopsControllerSearchButton.setImageDrawable(mActivity.getDrawable(R.drawable.ic_search));
        shopsControllerSearchButton.setVisibility(View.VISIBLE);
        shopsControllerSearchButton.setImageTintList(ColorStateList.valueOf(mActivity.getResources().getColor(R.color.black)));
        mSearchBarEditText.setVisibility(View.GONE);
        shouldShowCartButton = true;
    }

    public void goToSaleItemsFromCategorySearch() {

        SaleItemsController.Parameters.FromCategory parameters = new SaleItemsController.Parameters.FromCategory(null, null, null, new HashSet<>());

        SaleItemsController controller = SaleItemsController.newInstance(parameters);

        getRouter().pushController(RouterTransaction.with(controller).tag(getResources().getString(R.string.sale_items_controller_tag)).pushChangeHandler(new SimpleChangeHandler()).popChangeHandler(new FadeChangeHandler()));

    }

    @Override
    public void onRefreshStart() {
        super.onRefreshStart();
        shopsControllerBannerRecyclerView.setVisibility(View.GONE);
        isRefreshShop = true;
        bannerOffset = 0;

        if (mIsBrandsOnly) {
            mPresenter.loadTopBrands();
        } else {
            if (mIsDeeplink) {
                mPresenter.loadShopsBanner(createDeepLinkBannerRequest(mCategoryID, bannerOffset, bannerLimit), true);
            } else {
                mPresenter.loadShopsBanner(createBannerRequest(mCategoryID, bannerOffset, bannerLimit), true);
            }
            loadSlidingBanners();
            loadSponsoredBanners();
            loadCategoryBanners();
        }
    }

    private GetBannerRequest createBannerRequest(String categoryId, int bannerOffset, int bannerLimit) {
        final String lastBannerType = mBannersAdapter != null && mBannersAdapter.getItemCount() > 0 ? mBannersAdapter.getLastGroupType() : "";

        //start from 0 offset when bannerGroupType changes
        if (mBannersAdapter != null && lastBannerType != null && bannerGroupType != null && !lastBannerType.equals("") && !lastBannerType.equals(bannerGroupType) && !bannerGroupType.equals("")) {
            bannerOffset = mBannersAdapter.getOffset();
        }
        bannerGroupType = lastBannerType;

        GetBannerRequest getBannerRequest = new GetBannerRequest();
        if (bannerGroupType != null && !bannerGroupType.equals("")) {
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

        final int lastVisiblePos = mLayoutManager == null ? -1 : mLayoutManager.findLastVisibleItemPosition();
        final GetBannerResponse.Banner lastVisibleBanner = mBannersAdapter != null && lastVisiblePos >= 0 ? mBannersAdapter.getItem(lastVisiblePos) : null;
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

        getBannerRequest.setIncludeCampaignBanners(true);

        return getBannerRequest;
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return mIsRecyclerViewScrollIdle && mVerticalOffset == 0 && PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        resetShopsBanners(mCategoryID);

        if (mIsBrandsOnly) {
            mPresenter.loadTopBrands();
        } else {
            if (mIsDeeplink) {
                mPresenter.loadShopsBanner(createDeepLinkBannerRequest(mCategoryID, 0, 0));
            } else {
                mPresenter.loadShopsBanner(createBannerRequest(mCategoryID, bannerOffset, bannerLimit));
            }
            loadSlidingBanners();
            loadSponsoredBanners();
            loadCategoryBanners();
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

    @Override
    public void onTabSwitch(boolean intoThisView) {
        super.onTabSwitch(intoThisView);
        if (intoThisView) {
            if (mBannersAdapter != null) {
                mBannersAdapter.restartHorizontalViewHolders();
            }
        } else {
            if (mBannersAdapter != null) {
                mBannersAdapter.stopHorizontalViewHolders();
            }
        }
    }

    /*
     * bug/gen-7818-landscape - update layoutmanager on orientation change
     *
     */
    @Override
    public void onOrientationChanged(Configuration newConfiguration) {
        super.onOrientationChanged(newConfiguration);
        if (mBannersAdapter != null) {
            mBannersAdapter.stopHorizontalViewHolders();
        }
        resetBannerLayout();
        if (mBannersAdapter != null) {
            mBannersAdapter.restartHorizontalViewHolders();
        }
    }

    private void resetBannerLayout() {
        if (mBannersAdapter != null && shopsControllerBannerRecyclerView != null && mLayoutManager != null) {
            int currentScrollPosition = Math.max(0, mLayoutManager.findFirstVisibleItemPosition());
            setupBannersView();
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
                loadSlidingBanners();
                loadSponsoredBanners();
                loadCategoryBanners();
            }

            mShopsControllerToolbarTextView.setVisibility(View.VISIBLE);
            mShopsControllerToolbarTextView.setText(getCategoryParentKey(categoryKey));
            mShopsControllerToolbarLogo.setVisibility(View.GONE);
            mShopsControllerCartButton.setImageDrawable(mActivity.getDrawable(R.drawable.ic_pink_chevron));
            mShopsControllerCartButton.setScaleType(ImageView.ScaleType.CENTER);
            shouldShowCartButton = false;

        } else {

            assert (mActivity) != null;
            loadShopBanners();
        }
    }


    public void goToCategoryLink(String categoryKey, String categoryId) {

        if (categoryKey != null && mActivity.getCategoriesController() != null) {

            final CategoriesMvpView categoriesView = mActivity.getCategoriesController();
            final String categoryMapKey = categoriesView.getCategoryKeyFromId(categoryId);

            mCategoryKey = categoryMapKey;
            mCategoryID = categoryMapKey;

            SaleItemsController.Parameters.FromCategoryDeepLink parameters = new SaleItemsController.Parameters.FromCategoryDeepLink(categoryKey, categoryMapKey);

            SaleItemsController controller = SaleItemsController.newInstance(parameters);

            getRouter().pushController(RouterTransaction.with(controller).tag(getResources().getString(R.string.sale_items_controller_tag)).pushChangeHandler(new SimpleChangeHandler()).popChangeHandler(new FadeChangeHandler()));

        } else {

            assert (mActivity) != null;
            loadShopBanners();
        }
    }

    public void refreshFromLogout() {
        mIsBrandsOnly = false;
        bannerGroupType = "";
        resetShopsBanners("");
        mPresenter.loadShopsBanner(createBannerRequest("", 0, bannerLimit), true);
        loadSlidingBanners();
        loadSponsoredBanners();
        loadCategoryBanners();
    }

    public boolean isFromCategories() {
        return mCategoryID != null || mCategoryKey != null;
    }

    private boolean getPrefersOldShopBannerDimensions() {
        BannerDimensionsOverride override;
        if (mIsBrandsOnly) {
            override = mPresenter.isTablet() ? OVERRIDE_BRAND_BANNER_DIMENSIONS_FOR_TABLET : OVERRIDE_BRAND_BANNER_DIMENSIONS_FOR_MOBILE;
        } else {
            override = mPresenter.isTablet() ? OVERRIDE_BANNER_DIMENSIONS_FOR_TABLET : OVERRIDE_BANNER_DIMENSIONS_FOR_MOBILE;
        }
        switch (override) {
            case USE_OLD:
                return true;
            case USE_NEW:
                return false;
            default:
                return mPresenter.getPrefersOldShopBannerDimensions();
        }
    }

    public void updateBasketItemsQuantity(int quantity) {
        if (quantity == 0) {
            mBadge.setVisibility(View.GONE);
        } else {
            if (shouldShowCartButton) {
                mBadge.setVisibility(View.VISIBLE);
                mBadgeText.setText(Integer.toString(quantity));
            }
        }
    }

    @Override
    public void showLeaderboardBanner(GetBannerResponse response) {
        if (!isViewAttached() || !isViewBound()) {
            return;
        }
        if (mBannersAdapter == null) {
            setupBannersView();
        }
        if (mBannersAdapter == null) {
            return;
        }
        if (response == null) {
            mBannersAdapter.setLeaderboardBanner(null, null);
            return;
        }
        GetBannerResponse.Banner leaderboardBanner = null;
        List<GetBannerResponse.Group> groups = response.getGroups();
        if (groups != null) {
            for (GetBannerResponse.Group group : groups) {
                List<GetBannerResponse.Banner> banners = group.getBanners();
                if (banners != null) {
                    for (GetBannerResponse.Banner banner : banners) {
                        if (banner.getBannerType().equals("leaderboardBanner")) {
                            leaderboardBanner = banner;
                            break;
                        }
                    }
                }
            }
        }

        Consumer<Object> onClick = null;
        if (leaderboardBanner != null) {
            final Uri uri = Uri.parse(leaderboardBanner.getLink());
            if (uri != null) {
                onClick = o -> mActivity.getMainController().processLinkUri(uri);
            }
        }

        mBannersAdapter.setLeaderboardBanner(leaderboardBanner, onClick);
    }

}
