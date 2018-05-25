package au.com.dealsdirect.ui.controller.saleitems;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsRequest;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
import au.com.dealsdirect.data.network.model.sorting.SortingResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.saleitemdetails.SaleItemDetailsController;
import au.com.dealsdirect.ui.controller.searchfilter.SearchFilterController;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;
import au.com.dealsdirect.ui.custom.SearchEditText;
import au.com.dealsdirect.ui.custom.transitions.SharedArcFadePopChangeHandler;
import au.com.dealsdirect.ui.custom.transitions.SharedArcFadePushChangeHandler;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.JsonUtils;
import au.com.dealsdirect.utils.PaginateUtils;
import au.com.dealsdirect.utils.StringUtils;
import au.com.dealsdirect.utils.ViewUtils;
import au.com.dealsdirect.utils.module.ControllerFactory;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

import static au.com.dealsdirect.utils.BundleKeys.SALEITEMS_CATEGORY_MAP;
import static au.com.dealsdirect.utils.BundleKeys.SALEITEMS_CHIPS_FILTER;
import static au.com.dealsdirect.utils.BundleKeys.SALEITEMS_FROM_CATEGORIES;
import static au.com.dealsdirect.utils.BundleKeys.SALEITEMS_FROM_CATEGORY_SEARCH;
import static au.com.dealsdirect.utils.BundleKeys.SALEITEMS_FROM_SHOP_SEARCH;
import static au.com.dealsdirect.utils.BundleKeys.SALEITEMS_SALE_ID;
import static au.com.dealsdirect.utils.BundleKeys.SALEITEMS_TITLE;

/**
 * dp Created by Admin on 6/8/17.
 */

public class SaleItemsController extends BaseController implements SaleItemsMvpView, PtrHandler, AppBarLayout.OnOffsetChangedListener {

    public static final String TAG = SaleItemsController.class.getSimpleName();
    private static final long DELAY = 1000; // milliseconds
    private static final String CATEGORY_KEY_SEPARATOR = ">>>";
    private static final String CATEGORY_KEY_SEPARATOR_REPLACEMENT = " • ";

    private String mSaleId = "";
    private String mTitle = "";
    private String mCategoryKey = "";
    private String mCategoryForTitle = "";
    private String mSearchQuery = "";

    private List<GetSaleItemsResponse.Products> mSaleItems = new LinkedList<>();
    private List<GetSaleItemsResponse.Facets> mFacets = new ArrayList<>();
    private List<GetCategoryTreeResponse> mCategoryTreeResponse = new LinkedList<>();
    private List<SortingResponse> mSortingResponse = new ArrayList<>();
    private String mSortingListJsonString = "";
    private boolean mIsFilterClicked = false;
    private boolean mIsSearchClicked = false;
    private int mCurrentTabPosition = -1;
    private List<Pair<String, String>> mFacetFilters = new ArrayList();

    @BindView(R.id.controller_sale_items_grid_view)
    RecyclerView mSaleItemsRecyclerView;

    @BindView(R.id.partial_toolbar_field_title_edittext)
    SearchEditText mSaleItemsToolbarField;

    @BindView(R.id.partial_toolbar_field_title_textview)
    TextView mSaleItemsToolbarTitle;

    @BindView(R.id.controller_sale_items_text_placeholder)
    TextView mPlaceholder;

    @BindView(R.id.partial_toolbar_field_title_left_option)
    ImageButton mSaleItemsBackIcon;

    @BindView(R.id.controller_search_popular_subheader)
    TextView mPopularProductsHeader;

    @BindView(R.id.controller_search_filter_frame)
    ViewGroup mSearchFilterContainer;

    @BindView(R.id.controller_search_filter_tabs)
    TabLayout mTabLayout;

    @BindView(R.id.controller_sale_items_ptr)
    PtrClassicFrameLayout mPtrFrameLayout;

    @BindView(R.id.controller_sale_items_appbar)
    AppBarLayout mAppBar;

    private SaleItemsAdapter mSaleItemsAdapter;
    private Paginate mPaginateManager;
    private Paginate.Callbacks mPaginateCallbacks;

    private Router mSearchFilterRouter;
    private SearchFilterController mSearchFilterController;

    private int mVerticalOffset;
    private int mSaleItemsPageNumber = 0;
    private int mSaleItemClickCounter = 0;

    private boolean mIsFiltered = false;
    private boolean mIsSearch = false;
    private boolean mFromShopSearch = false;
    private boolean mFromCategorySearch = false;
    private boolean mIsCategoryChanged;
    private boolean hasSearchFilters;
    private boolean mIsSearchFilterControllerActive = false;
    private boolean mIsRecyclerViewScrollIdle;
    private boolean mIsLoadingProgress = false;
    private boolean mHasLoadedAllItems = false;

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
                            if (before != 0 || count != 0) {
                                mActivity.runOnUiThread(() -> showLoading());
                                mChipFilters = removeSearchQueryChips(mChipFilters);
                                buildSearchQueryChips(mChipFilters);
                                mPresenter.loadSaleItems(createSaleItemsRequest(mCategoryKey, mSaleId, 0, mChipFilters, ""));
                            }
                        }
                    }, DELAY);

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

        if (args.containsKey(SALEITEMS_TITLE)) {
            mTitle = getArgs().getString(SALEITEMS_TITLE, "");
            mTitle = mTitle.replaceAll(CATEGORY_KEY_SEPARATOR, CATEGORY_KEY_SEPARATOR_REPLACEMENT);
        }
        if (args.containsKey(SALEITEMS_SALE_ID))
            mSaleId = getArgs().getString(SALEITEMS_SALE_ID, "");
        if (args.containsKey(SALEITEMS_CATEGORY_MAP))
            mCategoryKey = getArgs().getString(SALEITEMS_CATEGORY_MAP, "");
        if (args.containsKey(SALEITEMS_CHIPS_FILTER))
            mChipFilters = JsonUtils.convertStringToObject(getArgs().getString(SALEITEMS_CHIPS_FILTER, ""), new TypeToken<ArrayList<SearchChipModel>>() {
            }.getType());
        if (args.containsKey(SALEITEMS_FROM_SHOP_SEARCH))
            mFromShopSearch = getArgs().getBoolean(SALEITEMS_FROM_SHOP_SEARCH, true);
        if (args.containsKey(SALEITEMS_FROM_CATEGORY_SEARCH))
            mFromCategorySearch = getArgs().getBoolean(SALEITEMS_FROM_CATEGORY_SEARCH, true);

    }

    public void onPassFiltersData(Bundle args) {

        if (args.containsKey(SALEITEMS_SALE_ID))
            mSaleId = args.getString(SALEITEMS_SALE_ID, "");
        if (args.containsKey(SALEITEMS_CATEGORY_MAP))
            mCategoryKey = args.getString(SALEITEMS_CATEGORY_MAP, "");
        if (!mCategoryKey.isEmpty()) {
            mCategoryForTitle = mCategoryKey.replaceAll(CATEGORY_KEY_SEPARATOR,
                    CATEGORY_KEY_SEPARATOR_REPLACEMENT);
        } else {
            mCategoryForTitle = "";
        }
        if (args.containsKey(SALEITEMS_CHIPS_FILTER)) {
            mChipFilters = JsonUtils.convertStringToObject(args.getString(SALEITEMS_CHIPS_FILTER, ""), new TypeToken<ArrayList<SearchChipModel>>() {
            }.getType());
        }

        if (args.containsKey(BundleKeys.KEY_SELECTED_FACETS)) {
            mPreviousSelectedFacetIndicesJsonString = args.getString(BundleKeys.KEY_SELECTED_FACETS, "");
        }

        mIsFiltered = true;
        mSaleItemsPageNumber = 0;
    }

    @Override
    protected void onAttach(@NonNull View view) {
        mPresenter.onAttach(this);
        mSaleItemClickCounter = 0;
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
    public void onRefreshStart() {
        super.onRefreshStart();
        mSaleItemsPageNumber = 0;
        mPresenter.loadSaleItems(createSaleItemsRequest(mCategoryKey, mSaleId, mSaleItemsPageNumber, mChipFilters, ""));
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

        //always set edittext string to empty initially
        mSaleItemsToolbarField.setText("");

        String lookingForText = getString(R.string.i_am_looking_for);
        String categoryToolbarString = StringUtils.buildCategoryToolbarTitle(mCategoryKey);

        //determining hint logic
        //category precedes above all
        if (!mCategoryKey.isEmpty()) {
            mSaleItemsToolbarField.setHint(categoryToolbarString);
        } else if (!mTitle.isEmpty()) {
            mSaleItemsToolbarField.setHint(mTitle);
        } else {
            mSaleItemsToolbarField.setHint(lookingForText);
        }


        String editTextString = mSaleItemsToolbarField.getText().toString();
        //determining toolbartitle logic
        //category precedes above all
        if (!mSearchQuery.isEmpty()) {
            mSaleItemsToolbarTitle.setText(mSearchQuery);
            mSaleItemsToolbarField.setText(mSearchQuery);
        } else if (!editTextString.isEmpty()) {
            mSaleItemsToolbarTitle.setText(editTextString);
        } else if (!mCategoryForTitle.isEmpty()) {
            mSaleItemsToolbarTitle.setText(mCategoryForTitle);
        } else if (!mTitle.isEmpty()) {
            mSaleItemsToolbarTitle.setText(mTitle);
        } else {
            mSaleItemsToolbarTitle.setText(getString(R.string.i_am_looking_for));
        }

        //show popular products if saleitemstoolbar title is "looking for"

        if (mSaleItemsToolbarTitle.getText().equals(lookingForText) && mChipFilters.isEmpty()) {
            mPopularProductsHeader.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onDetach(View view) {
        mPtrFrameLayout.setPtrHandler(null);
        mAppBar.removeOnOffsetChangedListener(this);
        super.onDetach(view);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {

        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    protected void setUp(View view) {
        hideKeyboard();

        mPresenter.loadSortingFacets();

        mActivity.getMainController().setViewpagerDraggable(false);

        setupPtrHeader();

        mPaginateCallbacks = new Paginate.Callbacks() {
            @Override
            public void onLoadMore() {
                // Load next mSaleItemsPageNumber of data (e.g. network or database)
                mSaleItemsPageNumber++;
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
        if (mPresenter.isTablet()) {
            mSaleItemsRecyclerView.setLayoutManager(new GridLayoutManager(mActivity,
                    getInteger(R.integer.sale_items_tablet_column_count)));
        } else {
            mSaleItemsRecyclerView.setLayoutManager(new GridLayoutManager(mActivity,
                    getInteger(R.integer.sale_items_phone_column_count)));
        }


        mSaleItemsRecyclerView.setAdapter(mSaleItemsAdapter);
        mSaleItemsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mIsRecyclerViewScrollIdle = newState == 0;
            }
        });

        if (mIsFiltered || mSaleItems.isEmpty()) {
            showLoading();
            hasSearchFilters = false;
            mPresenter.loadSaleItems(createSaleItemsRequest(mCategoryKey, mSaleId, mSaleItemsPageNumber, mChipFilters, ""));

            /* show popular products after filter with empty chips */
            if (mCategoryKey.isEmpty() && (mSaleId == null || mSaleId.isEmpty()) && mChipFilters.isEmpty()) {
                mPopularProductsHeader.setVisibility(View.VISIBLE);
            }

            /* set if still in search */
            if (mCategoryKey.isEmpty() && (mSaleId == null || mSaleId.isEmpty())) {
                mIsSearch = true;
                mFromShopSearch = true;
            }

            mChipFilters = removeSearchQueryChips(mChipFilters);
            mSearchQuery = buildSearchQueryText(mRemovedChipTitles);

            //this api call serves to get the correct facets for SearchFiltersController to display
            // we need to remove any chip filters to return the base facets
            // giving any filters(ff=) will change the facet return;

            mPresenter.loadSaleItems(createSaleItemsRequest(mCategoryKey, mSaleId, mSaleItemsPageNumber, null, ""));
        } else if (!mSaleItems.isEmpty()) {
            if (mSaleItems.size() >= getInteger(R.integer.sale_items_threshold)) {
                mPaginateManager = PaginateUtils.init(mActivity, mSaleItemsRecyclerView, mPaginateCallbacks);
            }
        }

        setRetainViewMode(RetainViewMode.RETAIN_DETACH);
        activateSearch();
    }

    private void setupPtrHeader() {
        mPtrFrameLayout.getHeader().setProgressIcon(getResources().getDrawable(R.drawable.ic_loader_logo));

        mPtrFrameLayout.getHeader().setPullProgressbar(getResources().getDrawable(R.drawable.bg_progress_bar));

        mPtrFrameLayout.getHeader().setProgressBar(ColorStateList.valueOf(getResources().getColor(R.color.progress_loader_stroke)));
    }

    @Override
    public void onLoadSortingFacetsFinished(List<SortingResponse> responseList) {
        mSortingResponse = responseList;
        mSortingListJsonString = new Gson().toJson(responseList);
    }

    @Override
    public void showSaleItems(GetSaleItemsResponse getSaleItemsResponse, boolean forFacetCorrection) {

        List<GetSaleItemsResponse.Products> items = getSaleItemsResponse.products;

        mFacets = getSaleItemsResponse.getFacets();
        mCategoryTreeResponse = getSaleItemsResponse.getCategories();

        if (mIsCategoryChanged || !hasSearchFilters) {
            setupSearchFilters();
            hasSearchFilters = true;
        }

        if ((mSearchQuery.isEmpty() && mFromShopSearch && mChipFilters.isEmpty()) || mFromCategorySearch) {
            mPopularProductsHeader.setVisibility(mSaleItemsToolbarField.getText().toString().isEmpty() ? View.VISIBLE : View.GONE);
        } else {
            if (mChipFilters.isEmpty() && mSaleItemsToolbarTitle.getText().toString().isEmpty() &&
                    mSaleItemsToolbarField.getHint().toString().equals(getString(R.string.i_am_looking_For))) {
                mPopularProductsHeader.setVisibility(View.VISIBLE);
            } else {
                mPopularProductsHeader.setVisibility(View.GONE);
            }
        }

        mIsLoadingProgress = false;

        if (items.size() == 0 && mSaleItemsPageNumber != 0) {
            mHasLoadedAllItems = true;
            mPaginateManager.setHasMoreDataToLoad(false);
            mSaleItemsPageNumber = 0;
        } else {

            if (!mChipFilters.isEmpty() || mFromCategorySearch || mFromShopSearch || !mSearchQuery.isEmpty()) {
                mIsFiltered = false;
            }


            if (mSaleItemsPageNumber == 0 || mIsSearch) {
                if (mPaginateManager != null) {
                    mPaginateManager.unbind();
                }
                mSaleItemsAdapter.replaceData(items);

                mPaginateManager = PaginateUtils.init(mActivity, mSaleItemsRecyclerView, mPaginateCallbacks);
                mSaleItemsRecyclerView.scrollToPosition(0);
                if (items.size() <= getInteger(R.integer.sale_items_threshold)) {
                    mHasLoadedAllItems = false;
                    mPaginateManager.setHasMoreDataToLoad(false);
                }
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

        mIsCategoryChanged = false;

        mFacetFilters = mSearchFilterController.parseFacets(getSaleItemsResponse.getFacets());
        mSearchFilterController.updateSubCategoryFilters(mCategoryTreeResponse);

        onRefreshEnd();

        if (mTabLayout.getTabCount() == 0) {
            setupTabs();
        }
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

    @Override
    public void onExecuteCategoryChangeApiCall(String chosenCategoryKey, String chosenCategoryName) {
        mPresenter.loadSaleItems(createSaleItemsRequest(chosenCategoryKey, "", 0, mChipFilters, ""));
        mIsCategoryChanged = true;
    }

    @Override
    public void refresh() {
        AppLogger.d(TAG, "refreshcalled");
        mIsLoadingProgress = true;
        mChipFilters = removeSearchQueryChips(mChipFilters);
        buildSearchQueryChips(mChipFilters);
        mPresenter.loadSaleItems(createSaleItemsRequest(mCategoryKey, mSaleId, mSaleItemsPageNumber, mChipFilters, ""));
    }

    @Override
    public void showProductDetails(RecyclerView.ViewHolder viewHolder, int position, String seoIdentifierId, String imageUrl, String skuId, String saleId) {

        if (mSaleItemClickCounter != 1) {
            mSaleItemClickCounter = +1;

            Handler clickHandler = new Handler();
            clickHandler.postDelayed(() -> {
                mSaleItemClickCounter = 0;
            }, 2000);

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

    }

    @Override
    public void unbindPaginate() {
        if (mPaginateManager != null) {
            mPaginateManager.unbind();
        }
    }

    private void setupSearchFilters() {
        mSearchFilterContainer.setVisibility(View.VISIBLE);

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

            mSearchFilterController = (SearchFilterController) ControllerFactory.getInstance(GateKeeper.Destination.SEARCH_FILTER, bundle);
            GateKeeper.setRoot(mSearchFilterRouter, GateKeeper.Destination.SEARCH_FILTER, RouterTransaction.with(mSearchFilterController));
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

    public void activateSearch() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (mSaleItemsToolbarField != null && mSaleItemsToolbarField.requestFocus()) {
                InputMethodManager inputMethodManager =
                        (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

                inputMethodManager.toggleSoftInputFromWindow(
                        mSaleItemsToolbarField.getApplicationWindowToken(),
                        InputMethodManager.SHOW_FORCED, 0);
            }
        }, 200);


        mSaleItemsToolbarField.setVisibility(View.VISIBLE);

        mSaleItemsToolbarField.setActivated(true);
        mSaleItemsToolbarField.setSelection(mSaleItemsToolbarField.getText().length());

        mSaleItemsToolbarField.addTextChangedListener(mTextWatcher);
        mSaleItemsToolbarField.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard();
                mSearchQuery = textView.getText().toString();
                mIsSearch = true;
                mChipFilters = removeSearchQueryChips(mChipFilters);
                buildSearchQueryChips(mChipFilters);
                showLoading();

                mPresenter.loadSaleItems(createSaleItemsRequest(mCategoryKey, mSaleId, 0, mChipFilters, ""));
            }

            return false;
        });

    }

    private List<SearchChipModel> removeSearchQueryChips(List<SearchChipModel> chipFilters) {

        List<SearchChipModel> chipList = new ArrayList<>(chipFilters);
        mRemovedChipTitles = new ArrayList<>();
        for (SearchChipModel chip : chipFilters) {
            if (chip.getFilterType().equals(BundleKeys.SEARCH_QUERY_NAME)) {
                chipList.remove(chip);
                mRemovedChipTitles.add(chip.getChipTitle());
            }
        }
        return chipList;
    }

    private String buildSearchQueryText(List<String> chipTitles) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : chipTitles) {
            stringBuilder.append(string);
            stringBuilder.append(" ");
        }

        return stringBuilder.toString().trim();
    }

    private void buildSearchQueryChips(List<SearchChipModel> chipFilters) {
        if (!mSearchQuery.isEmpty()) {
            String[] splitted = mSearchQuery.split("\\s+");
            for (String str : splitted) {
                chipFilters.add(new SearchChipModel(BundleKeys.SEARCH_QUERY_NAME, str, -1));
            }
        }

    }

    public GetSaleItemsRequest createSaleItemsRequest(List<String> categoryKeys, String saleId, int pageNumber, List<SearchChipModel> chipsList, String query) {
        mCategoryKey = StringUtils.generateConcatenatedCategories(categoryKeys);
        return createSaleItemsRequest(mCategoryKey, saleId, pageNumber, chipsList, query);
    }

    public GetSaleItemsRequest createSaleItemsRequest(String categoryKey, String saleId, int pageNumber, List<SearchChipModel> chipsList, String query) {
        List<String> saleIds = new LinkedList<>();
        HashMap<String, List<String>> facetFilters = new HashMap<>();

        GetSaleItemsRequest getSaleItemsRequest = new GetSaleItemsRequest();

        getSaleItemsRequest = StringUtils.updateSaleItemRequest(categoryKey, getSaleItemsRequest);

        getSaleItemsRequest.setSorting("");
        getSaleItemsRequest.setPageNumber(String.valueOf(pageNumber));
        getSaleItemsRequest.setQuery(query);

        getSaleItemsRequest.setPageSize("50");

        if (saleId != null) {

            if (!saleId.isEmpty())
                saleIds.add(saleId);
        }


        if (!saleIds.isEmpty()) {
            facetFilters.put("saleId", saleIds);
        }

        if (chipsList == null) {
            getSaleItemsRequest.setHasFilters(false);
        } else {
            if (chipsList.size() != 0) {
                ArrayList<String> searchQueryFilters = new ArrayList<>();
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
                    } else if (facetName.equals(BundleKeys.SEARCH_QUERY_NAME)) {
                        searchQueryFilters.add(chip.getChipTitle());
                    } else if (facetName.equals(BundleKeys.SORT_FACETFILTER_NAME)) {
                        getSaleItemsRequest.setSorting(mapSortingTitleToKey(chip.getChipTitle()));
                    }
                }

                facetFilters.put(BundleKeys.BRANDS_FACETFILTER_NAME, brandNameFacetFilters);
                facetFilters.put(BundleKeys.COLORS_FACETFILTER_NAME, colorFacetFilters);
                facetFilters.put(BundleKeys.SIZES_FACETFILTER_NAME, sizesFacetFilters);
                facetFilters.put(BundleKeys.PRICE_FACETFILTER_NAME, priceFacetFilters);


                if (searchQueryFilters.size() != 0) {
                    StringBuilder result = new StringBuilder();
                    for (int i = 0; i < searchQueryFilters.size(); i++) {
                        if (i > 0) {
                            result.append(" ");
                        }
                        result.append(searchQueryFilters.get(i));
                    }

                    getSaleItemsRequest.setQuery(result.toString());
                }
            }

            getSaleItemsRequest.setHasFilters(true);
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
        mTabLayout.removeAllTabs();
        mFacetFilters.add(0, new Pair<String, String>(BundleKeys.CATEGORY_TREE_FACET, "Categories"));
        mFacetFilters.add(mFacetFilters.size(), new Pair<String, String>(BundleKeys.SORT_FACETFILTER_NAME, "Sort"));
        for (Pair<String, String> pair : mFacetFilters) {
            mTabLayout.addTab(mTabLayout.newTab().setText(pair.second), false);
        }
        mSearchFilterController.setFacetFilterItems(mFacetFilters);

        //Remove selected state by default setup
        mTabLayout.getTabAt(0).select();
        mCurrentTabPosition = 0;
        toggleTabSelection(false);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mSearchFilterController.showFacetItem(tab.getPosition());
                mCurrentTabPosition = tab.getPosition();
                toggleTabSelection(true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                mSearchFilterController.showFacetItem(tab.getPosition());
                mCurrentTabPosition = tab.getPosition();
                toggleTabSelection(true);
            }
        });

        //change tab mode depending on the screen width
        Runnable tabConfig = () -> {
            ViewUtils.setDynamicTabLayout(mTabLayout, mActivity);
        };
        mTabLayout.post(tabConfig);
    }

    private void toggleTabSelection(boolean isTabActive) {
        mIsSearchFilterControllerActive = isTabActive;
        mSearchFilterController.setSearchFilterControllerActive(mIsSearchFilterControllerActive);
        LinearLayout tabStrip = (LinearLayout) mTabLayout.getChildAt(0);
        tabStrip.getChildAt(mCurrentTabPosition).setSelected(isTabActive);
    }

    public void setChipFilters(List<SearchChipModel> chipFilters) {
        mChipFilters = chipFilters;
    }


    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return mIsRecyclerViewScrollIdle && mVerticalOffset == 0 && PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        mPresenter.loadSaleItems(createSaleItemsRequest(mCategoryKey, mSaleId, 0, mChipFilters, ""));
    }

    @Override
    public void onRefreshEnd() {
        super.onRefreshEnd();
        if (mPtrFrameLayout != null) {
            mPtrFrameLayout.refreshComplete();
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        this.mVerticalOffset = verticalOffset;
    }
}
