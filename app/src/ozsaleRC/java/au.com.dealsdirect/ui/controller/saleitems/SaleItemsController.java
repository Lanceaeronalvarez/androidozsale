package au.com.dealsdirect.ui.controller.saleitems;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.util.Pair;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.paginate.Paginate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsRequest;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
import au.com.dealsdirect.data.network.model.sorting.SortingResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.saleitemdetails.SaleItemDetailsController;
import au.com.dealsdirect.ui.controller.searchfilter.SearchFilterMvpView;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;
import au.com.dealsdirect.ui.custom.SearchEditText;
import au.com.dealsdirect.ui.custom.transitions.SharedArcFadePopChangeHandler;
import au.com.dealsdirect.ui.custom.transitions.SharedArcFadePushChangeHandler;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.JsonUtils;
import au.com.dealsdirect.utils.KeyboardUtils;
import au.com.dealsdirect.utils.PaginateUtils;
import au.com.dealsdirect.utils.StringUtils;
import au.com.dealsdirect.utils.TabLayoutUtils;
import au.com.dealsdirect.utils.module.ControllerFactory;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTouch;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS;
import static android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL;
import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

/**
 * dp Created by Admin on 6/8/17.
 */

public class SaleItemsController extends BaseController implements SaleItemsMvpView, PtrHandler, AppBarLayout.OnOffsetChangedListener {

    public static final String TAG = SaleItemsController.class.getSimpleName();
    private static final long SEARCH_DELAY_MS = 1000; // milliseconds
    private static final long DELETE_DELAY_MS = 1250; // milliseconds
    private static final String CATEGORY_KEY_SEPARATOR = ">>>";
    private static final String CATEGORY_KEY_SEPARATOR_REPLACEMENT = " • ";

    private String mSaleId = "";
    private String mTitle = "";
    private String mCategoryKey = "";
    private String mCategoryForTitle = "";
    private String mSearchQuery = "";

    private Map<String, GetCategoryTreeResponse> mCategoryMap = new HashMap<>();
    private List<GetSaleItemsResponse.Products> mSaleItems = new LinkedList<>();
    private List<GetSaleItemsResponse.Facets> mFacets = new ArrayList<>();
    private List<GetCategoryTreeResponse> mCategoryTreeResponse = new LinkedList<>();
    private List<GetCategoryTreeResponse> mInitialCategoryTree = new LinkedList<>();
    private List<SortingResponse> mSortingResponse = new ArrayList<>();
    private String mSortingListJsonString = "";
    private boolean mIsFilterClicked = false;
    private boolean mIsSearchClicked = false;
    private int mCurrentTabPosition = -1;
    private List<Pair<String, String>> mFacetFilters = new ArrayList();
    private List<String> mTabTitles = new ArrayList();

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

    @BindView(R.id.controller_sale_items_text_placeholder)
    LinearLayout mPlaceholder;

    @BindView(R.id.partial_toolbar_field_title_left_option)
    View mSaleItemsBackIcon;

    @BindView(R.id.controller_search_filter_frame)
    ViewGroup mSearchFilterContainer;

    @BindView(R.id.controller_search_filter_tabs)
    TabLayout mTabLayout;

    @BindView(R.id.controller_sale_items_ptr)
    PtrClassicFrameLayout mPtrFrameLayout;

    @Nullable
    @BindView(R.id.controller_sale_items_appbar)
    AppBarLayout mAppBar;

    @Nullable
    @BindView(R.id.controller_sale_collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;

    private SaleItemsAdapter mSaleItemsAdapter;
    private Paginate mPaginateManager;
    private Paginate.Callbacks mPaginateCallbacks;

    private Router mSearchFilterRouter;
    private SearchFilterMvpView mSearchFilterMvpView;

    private int mVerticalOffset;
    private int mSaleItemsPageNumber = 0;

    private boolean mIsFiltered = false;
    private boolean mIsSearch = false;
    private boolean mFromShopSearch = false;
    private boolean mFromCategorySearch = false;
    private boolean hasSearchFilters;
    private boolean mIsSearchFilterControllerActive = false;
    private boolean mIsRecyclerViewScrollIdle;
    private boolean mIsLoadingProgress = false;
    private boolean mHasLoadedAllItems = false;
    private boolean mIsCategoryChanged = false;
    private boolean mFromCategoryDeeplink = false;
    private boolean mInitialLoad = false;
    private boolean mIsFromCategories = false;

    private List<SearchChipModel> mChipFilters = new ArrayList<>();

    //store state of selection from filters
    private String mPreviousSelectedFacetIndicesJsonString = "";

    //store removed query chips
    private List<String> mRemovedChipTitles;


    private TextWatcher mTextWatcher = new TextWatcher() {
        private Timer timer = new Timer();

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            AppLogger.d(TAG, "on text changed");

            mSearchQuery = s.toString();
            mIsSearch = true;

            timer.cancel();
            timer = new Timer();
            timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        mActivity.runOnUiThread(() -> showLoading());
                        mPresenter.loadSaleItems(createSaleItemsRequest(mSearchFilterMvpView.getCategoryKeys(), 0, mChipFilters));
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


    public SaleItemsController(Bundle args) {
        super(args);

        if (args.containsKey(BundleKeys.SALEITEMS_TITLE)) {
            mTitle = getArgs().getString(BundleKeys.SALEITEMS_TITLE, "");
            mTitle = mTitle.replaceAll(CATEGORY_KEY_SEPARATOR, CATEGORY_KEY_SEPARATOR_REPLACEMENT);
        }
        if (args.containsKey(BundleKeys.SALEITEMS_SALE_ID)) {
            mSaleId = getArgs().getString(BundleKeys.SALEITEMS_SALE_ID, "");
        }
        if (args.containsKey(BundleKeys.SALEITEMS_CATEGORY_MAP)) {
            mCategoryKey = getArgs().getString(BundleKeys.SALEITEMS_CATEGORY_MAP, "");
        }
        if (args.containsKey(BundleKeys.SALEITEMS_CHIPS_FILTER)) {
            mChipFilters = JsonUtils.convertStringToObject(getArgs().getString(BundleKeys.SALEITEMS_CHIPS_FILTER, ""), new TypeToken<ArrayList<SearchChipModel>>() {
            }.getType());
        }
        if (args.containsKey(BundleKeys.SALEITEMS_FROM_SHOP_SEARCH)) {
            mFromShopSearch = getArgs().getBoolean(BundleKeys.SALEITEMS_FROM_SHOP_SEARCH, true);
        }
        if (args.containsKey(BundleKeys.SALEITEMS_FROM_CATEGORY_SEARCH)) {
            mFromCategorySearch = getArgs().getBoolean(BundleKeys.SALEITEMS_FROM_CATEGORY_SEARCH, true);
        }
        if (args.containsKey(BundleKeys.SALEITEMS_FROM_CATEGORY_DEEPLINK)) {
            mFromCategoryDeeplink = getArgs().getBoolean(BundleKeys.SALEITEMS_FROM_CATEGORY_DEEPLINK, false);
        }
        //initial category tree from categoriescontroller
        if (args.containsKey(BundleKeys.SALEITEMS_KEY_CATEGORIES)) {
            mInitialCategoryTree = JsonUtils.convertStringToObject(args.getString(BundleKeys.SALEITEMS_KEY_CATEGORIES, ""), new TypeToken<ArrayList<GetCategoryTreeResponse>>() {
            }.getType());
        }
        if (args.containsKey(BundleKeys.SALEITEMS_FROM_CATEGORIES)) {
            mIsFromCategories = getArgs().getBoolean(BundleKeys.SALEITEMS_FROM_CATEGORIES, false);
        }

    }

    @Override
    protected void onAttach(@NonNull View view) {
        mActivity.setDraggableViewPager(false);
        mPresenter.onAttach(this);
        mPtrFrameLayout.setPtrHandler(this);
        mAppBar.addOnOffsetChangedListener(this);

        determineToolbarTitle();

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

        mActivity.setDraggableViewPager(false);
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        mSaleItemsBackIcon.setOnClickListener(view12 -> mActivity.onBackPressed());
        setUp(view);

        setRetainViewMode(RetainViewMode.RETAIN_DETACH);
    }

    private void determineToolbarTitle() {

        String lookingForText = getString(R.string.search_tag);
        String categoryToolbarString = StringUtils.buildCategoryToolbarTitle(mCategoryKey);

        //determining hint logic
        //category precedes above all

        // bug/gen-8065_ozsale-reskin_bugfixing - always set searchbar hint to 'search'
        mSaleItemsToolbarField.setHint(lookingForText);

        String editTextString = mSaleItemsToolbarField.getText().toString();
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
        mSaleItemsToolbarTitle.setVisibility(!isFromCategories() ? View.VISIBLE : View.GONE);

        if (isFromCategories()) {
            mSaleItemsCategoryToolbarTitle.setText(title);
            mSaleItemsToolbarSubTitleText.setText(subTitle);
        } else if (!mSearchQuery.isEmpty()) {
            mSaleItemsToolbarTitle.setText(mSearchQuery);
        } else if (!editTextString.isEmpty()) {
            mSaleItemsToolbarTitle.setText(editTextString);
        } else if (!mCategoryForTitle.isEmpty()) {
            mSaleItemsToolbarTitle.setText(mCategoryForTitle);
        } else if (!mTitle.isEmpty()) {
            mSaleItemsToolbarTitle.setText(mTitle);
        } else {
            mSaleItemsToolbarTitle.setText(getString(R.string.i_am_looking_for));
        }

    }

    @Override
    public void onDetach(View view) {
        mPtrFrameLayout.setPtrHandler(null);
        mAppBar.removeOnOffsetChangedListener(this);
        hideKeyboard();
        super.onDetach(view);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {

        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    protected void setUp(View view) {


        mActivity.setSaleItemsController(this);
        mActivity.setDraggableViewPager(false);
        setupPtrHeader();

        //use initialcategory tree map if it came from categories.
        if (!mInitialCategoryTree.isEmpty()) {
            createCategoryMap(mInitialCategoryTree);
            if (mCategoryMap.get(mCategoryKey) != null) {
                mCategoryMap.get(mCategoryKey).setSelected(true);
            }
        }

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

        mSaleItemsAdapter = new SaleItemsAdapter(mActivity, mSaleItems, mPresenter, mSaleId);
        boolean isLandscape = mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        if (mPresenter.isTablet()) {
            mSaleItemsRecyclerView.setLayoutManager(new GridLayoutManager(mActivity,
                    getInteger(isLandscape ? R.integer.sale_items_tablet_column_count_landscape
                            : R.integer.sale_items_tablet_column_count_portrait)));
        } else {
            mSaleItemsRecyclerView.setLayoutManager(new GridLayoutManager(mActivity,
                    getInteger(isLandscape ? R.integer.sale_items_phone_column_count_landscape
                        : R.integer.sale_items_phone_column_count_portrait)));
        }

        mSaleItemsRecyclerView.setAdapter(mSaleItemsAdapter);
        mSaleItemsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mIsRecyclerViewScrollIdle = newState == 0;
                if (newState != SCROLL_STATE_IDLE) {
                    hideKeyboard();
                }
            }
        });

        mInitialLoad = true;

        /* bug/gen-8065_ozsale-reskin_bugfixing - dont load empty category on category link */
        if (!mFromCategoryDeeplink) {
            mPresenter.loadSaleItems(createSaleItemsRequest(mCategoryKey, mSaleItemsPageNumber, mChipFilters));
        }

        setRetainViewMode(RetainViewMode.RETAIN_DETACH);
        hideKeyboard();
    }

    private void showKeyboard() {
        if (mFromShopSearch) {
            activateSearch();
            KeyboardUtils.showSoftInput(mSaleItemsToolbarField, mActivity);
            InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.showSoftInput(mSaleItemsToolbarField, InputMethodManager.SHOW_FORCED);
            }
        }
    }

    private void setupPtrHeader() {
        mPtrFrameLayout.getHeader().setProgressIcon(getResources().getDrawable(R.drawable.ic_loader_logo));

        mPtrFrameLayout.getHeader().setPullProgressbar(getResources().getDrawable(R.drawable.bg_progress_bar));

        mPtrFrameLayout.getHeader().setProgressBar(ColorStateList.valueOf(getResources().getColor(R.color.progress_loader_stroke)));

        mPtrFrameLayout.setPullToRefresh(false);

    }

    @Override
    public void onLoadSortingFacetsFinished(List<SortingResponse> responseList) {
        mSortingResponse = responseList;
        mSortingListJsonString = new Gson().toJson(responseList);
    }

    @Override
    public void showSaleItems(GetSaleItemsResponse getSaleItemsResponse, boolean forFacetCorrection) {

        mCategoryTreeResponse = getSaleItemsResponse.getCategories();

        mPtrFrameLayout.setPullToRefresh(true);

        if (forFacetCorrection) {
            mFacets = getSaleItemsResponse.getFacets();
        } else {

            mFacets = getSaleItemsResponse.getFacets();

            List<GetSaleItemsResponse.Products> items = getSaleItemsResponse.products;

            mPtrFrameLayout.setPullToRefresh(true); //default true

            mIsLoadingProgress = false;

            if (items.size() == 0 && mSaleItemsPageNumber != 0) {
                mHasLoadedAllItems = true;
                mPaginateManager.setHasMoreDataToLoad(false);
                mPtrFrameLayout.setPullToRefresh(false);
                mSaleItemsPageNumber = 0;
            } else {

                if (!mChipFilters.isEmpty() || mFromCategorySearch || mFromShopSearch || !mSearchQuery.isEmpty()) {
                    mPtrFrameLayout.setPullToRefresh(false);
                }


                if (mSaleItemsPageNumber == 0 || mIsSearch) {
                    if (mPaginateManager != null) {
                        mPaginateManager.unbind();
                    }

                    if (items.size() <= getResources().getInteger(R.integer.sale_items_threshold)) {
                        if (mPaginateManager == null || items.size() == 0){
                            mPlaceholder.setVisibility(View.VISIBLE);
                            mSaleItemsRecyclerView.setVisibility(View.GONE);
                        } else {
                            mHasLoadedAllItems = true;
                            mPaginateManager.setHasMoreDataToLoad(false);
                            mSaleItemsPageNumber = 0;
                        }
                    } else {
                        mPaginateManager = PaginateUtils.init(mActivity, mSaleItemsRecyclerView, mPaginateCallbacks);
                        mSaleItemsRecyclerView.scrollToPosition(0);
                    }
                    mSaleItemsAdapter.replaceData(items);

                    mIsSearch = false;
                } else {
                    mSaleItemsAdapter.addData(items);
                }
            }

            mSaleItems = mSaleItemsAdapter.getData();

            if (mSaleItems == null || mSaleItems.isEmpty()) {
                mPlaceholder.setVisibility(View.VISIBLE);
                mSaleItemsRecyclerView.setVisibility(View.GONE);

            } else {
                mPlaceholder.setVisibility(View.GONE);
                mSaleItemsRecyclerView.setVisibility(View.VISIBLE);
            }

            if (mInitialLoad) {
                setupSearchFilters();
                mInitialLoad = false;
            }
        }

        mSearchFilterMvpView.updateFacets(mFacets);
        //replace category tree all the time.
        mSearchFilterMvpView.replaceCategoryTree(mCategoryTreeResponse);

        if (mIsCategoryChanged) { //replace facets only when category has changed.
            setupSearchFilters();
            mIsCategoryChanged = false;
        }
        onRefreshEnd();

    }

    void onBackClick() {
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
        if (!mInitialLoad) {
            if (mSaleItems.size() < getResources().getInteger(R.integer.sale_items_threshold)) {
                mHasLoadedAllItems = true;
                mSaleItemsPageNumber = 0;
            } else {
                mIsLoadingProgress = true;
                mSaleItemsPageNumber++;
                mPresenter.loadSaleItems(createSaleItemsRequest(mSearchFilterMvpView.getCategoryKeys(), mSaleItemsPageNumber, mChipFilters));
            }
        }
    }

    @Override
    public void showProductDetails(RecyclerView.ViewHolder viewHolder, int position, String seoIdentifierId, String imageUrl, String skuId, String saleId) {

        mSearchFilterMvpView.closeFacets();
        mSaleItemsRecyclerView.smoothScrollToPosition(position);

        Bundle bundle = new Bundle();
        bundle.putInt("KEY_POSITION", position);
        bundle.putString("KEY_IMAGE_ID", imageUrl);
        bundle.putString("KEY_SEO_IDENTIFIER", seoIdentifierId);
        bundle.putString("KEY_SKU_ID", skuId);
        bundle.putString("KEY_SALE_ID", saleId);
        bundle.putString("KEY_SALE_NAME", ((SaleItemsAdapter.ViewHolder) viewHolder).name.getText().toString());
        bundle.putString("KEY_SALE_PRICE", ((SaleItemsAdapter.ViewHolder) viewHolder).price.getText().toString());
        bundle.putString("KEY_SALE_OLD_PRICE", ((SaleItemsAdapter.ViewHolder) viewHolder).oldPrice.getText().toString());

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            getRouter().pushController(RouterTransaction.with(SaleItemDetailsController.newInstance(bundle))
                    .pushChangeHandler(new FadeChangeHandler())
                    .popChangeHandler(new FadeChangeHandler()));
        } else {
            getRouter().pushController(RouterTransaction.with(SaleItemDetailsController.newInstance(bundle))
                    .pushChangeHandler(new SharedArcFadePushChangeHandler())
                    .popChangeHandler(new SharedArcFadePopChangeHandler()));
        }

        mFromShopSearch = false;
        mFromCategorySearch = false;

    }

    private void setupSearchFilters() {
        if (!mSearchFilterRouter.hasRootController()) {
            Bundle bundle = new BundleBuilder(new Bundle())
                    .putString(BundleKeys.KEY_CATEGORY_STRING, new Gson().toJson(mCategoryTreeResponse))
                    .putString(BundleKeys.KEY_FACET_STRING, new Gson().toJson(mFacets))
                    .putString(BundleKeys.KEY_SORTING_STRING, mSortingListJsonString)
                    .putString(BundleKeys.SALEITEMS_SALE_ID, mSaleId)
                    .putString(BundleKeys.SALEITEMS_CATEGORY_MAP, mCategoryKey)
                    .putString(BundleKeys.KEY_SELECTED_FACETS, mPreviousSelectedFacetIndicesJsonString)
                    .putString(BundleKeys.SALEITEMS_CHIPS_FILTER, new Gson().toJson(mChipFilters))
                    .putString(BundleKeys.KEY_SALE_ITEMS_TITLE, mSearchQuery)
                    .build();

            Controller searchFilterController = ControllerFactory.getInstance(GateKeeper.Destination.SEARCH_FILTER, bundle);
            mSearchFilterMvpView = (SearchFilterMvpView) searchFilterController;
            GateKeeper.setRoot(mSearchFilterRouter, GateKeeper.Destination.SEARCH_FILTER, RouterTransaction.with(searchFilterController));
            mFacetFilters = mSearchFilterMvpView.parseFacets(mFacets);
            setupTabs();
            Completable.timer(DELETE_DELAY_MS, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).subscribe(this::showCollapsingToolbar);
            Completable.timer(DELETE_DELAY_MS, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).subscribe(this::showKeyboard);
        }
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
    public GetSaleItemsRequest createSaleItemsRequest(Set<String> categoryKeys, int pageNumber, List<SearchChipModel> chipsList) {
        mCategoryKey = StringUtils.generateConcatenatedCategories(categoryKeys);
        mChipFilters = chipsList;
        return createSaleItemsRequest(mCategoryKey, pageNumber, chipsList);
    }

    @Override
    public GetSaleItemsRequest createSaleItemsRequest(String categoryKey, int pageNumber, List<SearchChipModel> chipsList) {
        List<String> saleIds = new LinkedList<>();
        HashMap<String, List<String>> facetFilters = new HashMap<>();

        GetSaleItemsRequest getSaleItemsRequest = new GetSaleItemsRequest();

        getSaleItemsRequest = StringUtils.updateSaleItemRequest(categoryKey, getSaleItemsRequest);

        getSaleItemsRequest.setSorting("");
        getSaleItemsRequest.setPageNumber(String.valueOf(pageNumber));
        getSaleItemsRequest.setQuery(mSearchQuery);
        getSaleItemsRequest.setPageSize("50");

        if (!(mSaleId == "")) {
            saleIds.add(mSaleId);
            facetFilters.put("saleId", saleIds);
        }

        if (chipsList == null) {
            getSaleItemsRequest.setHasFilters(false);
        } else {
            if (chipsList.size() != 0) {
                ArrayList<String> brandNameFacetFilters = new ArrayList<>();
                ArrayList<String> colorFacetFilters = new ArrayList<>();
                ArrayList<String> sizesFacetFilters = new ArrayList<>();
                ArrayList<String> priceFacetFilters = new ArrayList<>();

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
                    }
                }

                facetFilters.put(BundleKeys.BRANDS_FACETFILTER_NAME, brandNameFacetFilters);
                facetFilters.put(BundleKeys.COLORS_FACETFILTER_NAME, colorFacetFilters);
                facetFilters.put(BundleKeys.SIZES_FACETFILTER_NAME, sizesFacetFilters);
                facetFilters.put(BundleKeys.PRICE_FACETFILTER_NAME, priceFacetFilters);

            }

            getSaleItemsRequest.setHasFilters(true);
        }

        String facetFiltersString = new Gson().toJson(facetFilters);

        getSaleItemsRequest.setFacetFilter(facetFiltersString);

        mSaleItemsPageNumber = pageNumber;

        return getSaleItemsRequest;
    }

    @Override
    public void setIsCategoryChanged(boolean val) {
        mIsCategoryChanged = val;
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
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mTabLayout.getLayoutParams();
        layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin + (int) getDimension(R.dimen.edit_text_height), layoutParams.rightMargin, layoutParams.bottomMargin);
        mTabLayout.removeAllTabs();
        mTabTitles.clear();
        mFacetFilters.add(0, new Pair<String, String>(BundleKeys.CATEGORY_TREE_FACET, "Category"));
        if(mPresenter.isSortingEnabled()) {
            mFacetFilters.add(mFacetFilters.size(), new Pair<String, String>(BundleKeys.SORT_FACETFILTER_NAME, "Sort"));
        }
        for (Pair<String, String> pair : mFacetFilters) {
            String newTitle = !pair.second.isEmpty() ? pair.second.substring(0,1).toUpperCase() + pair.second.substring(1) : pair.second;
            mTabTitles.add(newTitle);
            mTabLayout.addTab(mTabLayout.newTab(), false);
        }

        TabLayoutUtils.setupWithCustomTextView(mActivity,mTabLayout,mTabTitles,getString(R.string.font_app_light));

        //ANDR - Fit filters on the screen (TAB) https://apacsale.atlassian.net/browse/GEN-9022
        //set tabs layout weight to 1 so that when rotated to landscape even still in scrollable mode, it will fill whole width
        ViewGroup slidingTabStrip = (ViewGroup) mTabLayout.getChildAt(0);
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            View tab = slidingTabStrip.getChildAt(i);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tab.getLayoutParams();
            lp.weight = 1;
            tab.setLayoutParams(lp);
        }

        mSearchFilterMvpView.setFacetFilterItems(mFacetFilters);

        //Remove selected state by default setup
        mTabLayout.getTabAt(0).select();
        mCurrentTabPosition = 0;
        toggleTabSelection(0,false);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                onSelectTab(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                toggleTabSelection(tab.getPosition(),false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                onSelectTab(tab);
            }
        });
    }

    private void onSelectTab(TabLayout.Tab tab){
        hideKeyboard();
        mSearchFilterMvpView.showFacetItem(tab.getPosition());
        mCurrentTabPosition = tab.getPosition();
        mIsSearchFilterControllerActive = true;
        mSearchFilterMvpView.setSearchFilterControllerActive(mIsSearchFilterControllerActive);
        toggleTabSelection(mCurrentTabPosition,true);
    }

    private void showCollapsingToolbar() {
        if(isViewAttached()) {
            AppBarLayout.LayoutParams collapsingToolbarLayoutParams = (AppBarLayout.LayoutParams) mCollapsingToolbar.getLayoutParams();
            collapsingToolbarLayoutParams.height = Math.round(getDimension(R.dimen.sale_details_app_bar_height));
            mCollapsingToolbar.setLayoutParams(collapsingToolbarLayoutParams);

            mTabLayout.setVisibility(View.VISIBLE);
            mSearchFilterContainer.setVisibility(View.VISIBLE);
        }
    }

    private void toggleTabSelection(int tabPos, boolean isTabActive) {
        LinearLayout tabStrip = (LinearLayout) mTabLayout.getChildAt(0);
        tabStrip.getChildAt(tabPos).setSelected(isTabActive);
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return mIsRecyclerViewScrollIdle && mVerticalOffset == 0 && PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        mPresenter.loadSaleItems(createSaleItemsRequest(mCategoryKey, 0, mChipFilters));
    }

    @Override
    public void onRefreshEnd() {
        if (mPtrFrameLayout != null) {
            mPtrFrameLayout.setLastUpdateTimeRelateObject(this);
            mPtrFrameLayout.refreshComplete();
        }
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
        if(isViewAttached()) {
            mSaleItemsToolbarField.setSelection(mSaleItemsToolbarField.getText().length());
            if (mSearchFilterMvpView != null) {
                mSearchFilterMvpView.closeFacets();
            }

            mSaleItemsToolbarField.addTextChangedListener(mTextWatcher);
            mSaleItemsToolbarField.setOnEditorActionListener((textView, actionId, keyEvent) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard();
                    mSearchQuery = textView.getText().toString();
                    mIsSearch = true;
                    showLoading();
                    mPresenter.loadSaleItems(createSaleItemsRequest(mSearchFilterMvpView.getCategoryKeys(), 0, mChipFilters));
                }
                return false;
            });
        }
    }

    /* bug/gen-8605_ozsale-reskin_bugfixing - four item row on mobile landscape */
    @Override
    public void onOrientationChanged(Configuration newConfig) {
        boolean isLandscape = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE;
        if (mSaleItemsRecyclerView != null) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) mSaleItemsRecyclerView.getLayoutManager();
            int currentScrollPosition = gridLayoutManager.findFirstVisibleItemPosition();
            mSaleItemsRecyclerView.setAdapter(mSaleItemsAdapter);
            gridLayoutManager.scrollToPosition(currentScrollPosition);

            gridLayoutManager.setSpanCount(getInteger(isLandscape ? R.integer.sale_items_phone_column_count_landscape
                : R.integer.sale_items_phone_column_count_portrait));
            if (mPresenter != null && mPresenter.isTablet()) {
                gridLayoutManager.setSpanCount(getInteger(isLandscape ? R.integer.sale_items_tablet_column_count_landscape
                        : R.integer.sale_items_tablet_column_count_portrait));
            }
        }
//        updateTabConfiguration();
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
        return mIsFromCategories;
    }
}
