package au.com.dealsdirect.ui.controller.saleitems;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluelinelabs.conductor.ChangeHandlerFrameLayout;
import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.google.gson.Gson;
import com.jakewharton.rxbinding2.view.RxView;
import com.mysale.genie.views.custom.CoordinatorLayoutAsBottomSheetBehavior;
import com.paginate.Paginate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsRequest;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
import au.com.dealsdirect.data.network.model.sorting.SortingResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.categories.CategoriesController;
import au.com.dealsdirect.ui.controller.saleitemdetails.SaleItemDetailsController;
import au.com.dealsdirect.ui.controller.searchfilter.SearchFilterController;
import au.com.dealsdirect.ui.controller.searchfilter.SearchFilterMvpPresenter;
import au.com.dealsdirect.ui.controller.searchfilter.SearchFilterMvpView;
import au.com.dealsdirect.ui.controller.searchfilter.ViewPagerBottomSheetBehavior;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchTagsAdapter;
import au.com.dealsdirect.ui.custom.transitions.ArcFadeMoveChangeHandler;
import au.com.dealsdirect.ui.custom.transitions.SharedArcFadePopChangeHandler;
import au.com.dealsdirect.ui.custom.transitions.SharedArcFadePushChangeHandler;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.CartUtil;
import au.com.dealsdirect.utils.KeyboardUtils;
import au.com.dealsdirect.utils.PaginateUtils;
import au.com.dealsdirect.utils.module.ControllerFactory;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

import static au.com.dealsdirect.utils.ViewUtils.dpToPx;

/**
 * Created by smartwave on 02/11/2017.
 */

public class SaleItemsController extends BaseController implements SaleItemsMvpView, KeyboardHeightObserver {

    @Inject
    SaleItemsMvpPresenter<SaleItemsMvpView> mPresenter;
    @Inject
    SearchFilterMvpPresenter<SearchFilterMvpView> mSearchFilterPresenter;

    @BindView(R.id.sale_items_recyclerview)
    RecyclerView mSaleItemsRecyclerView;
    @BindView(R.id.search_tag_recycler_view)
    RecyclerView mSearchTagsRecyclerView;

    @BindView(R.id.controller_categories_frame)
    ViewGroup mCategoriesContainer;
    @BindView(R.id.controller_search_filter_frame)
    ViewGroup mSearchFilterContainer;

    @BindView(R.id.sale_items_search_bar)
    AppBarLayout mSearchBar;
    @BindView(R.id.sale_items_cart)
    RelativeLayout mSaleItemsCart;
    @BindView(R.id.sale_items_cart_counter)
    TextView mCartCounter;
    @BindView(R.id.overlay)
    RelativeLayout mOverlay;

    boolean mIsCallGetCategoryTreeFinished = false;

    ViewPagerBottomSheetBehavior mBottomSheetBehavior;

    public boolean isSearchFiltersShown() {
        return mIsSearchFiltersShown;
    }

    boolean mIsSearchFiltersShown;

    SaleItemsAdapter mAdapter;
    CustomGridLayoutManager mLayoutManager;
    List<SearchChipModel> mChipFilters = new ArrayList<>();
    private List<GetSaleItemsResponse.Facets> mFacets = new ArrayList<>();

    private String mChosenCategoryKey = "";
    private String mChosenCategory = "";
    private List<GetCategoryTreeResponse> mCategories = new ArrayList<>();
    private boolean isCategoryChanged = false;
    private boolean initialLoad = false;
    private List<GetSaleItemsResponse.Products> mSaleItems = new ArrayList<>();


    private Paginate mPaginateManager;

    private Paginate.Callbacks mPaginateCallbacks;
    private int page = 0;
    private int mSaleItemClickCounter = 0;
    private boolean loadingInProgress = false;
    private boolean hasLoadedAllItems = false;
    private Router mCategoriesRouter;

    //search filter variables
    private SearchFilterController mSearchFilterController;
    private Router mSearchFilterRouter;
    SearchTagsAdapter mSearchTagsAdapter;
    LinearLayoutManager mSearchTagsLayoutManager;

    private KeyboardHeightProvider mKeyboardHeightProvider;
    private boolean isBottomSheetAdjustedHeight = false;
    private boolean isKeyboardOpen = false;
    private ControllerChangeHandler.ControllerChangeListener mControllerChangeListener;

    private boolean defaultBool = true;
    private boolean isChangeStarted = false;
    private boolean isSetupFinished = false;

    private CompositeDisposable mRxViewDisposables;

    public static SaleItemsController newInstance() {
        return new SaleItemsController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public SaleItemsController(Bundle args) {
        super(args);
        mRxViewDisposables = new CompositeDisposable();
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_sale_items, container, false);

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        mPresenter.onAttach(this);
        mSaleItemClickCounter = 0;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        mActivity.setSaleItemsRouter(getRouter());
        setUp(view);
    }

    @Override
    protected void setUp(View view) {

        if(!isCallGetCategoryTreeFinished()) {
            mPresenter.callGetCategoryTree();
        }

        if (mPresenter.isAuthorized()) {
            mPresenter.callGetBasketItemsQuantity();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSearchBar.setOutlineProvider(null);
            mSearchBar.setElevation(0);
        }


        mKeyboardHeightProvider = new KeyboardHeightProvider(mActivity);
        mKeyboardHeightProvider.setKeyboardHeightObserver(this);
        mKeyboardHeightProvider.start();

        mCartCounter.setText(CartUtil.getCartValue() + "");

        mPaginateCallbacks = new Paginate.Callbacks() {
            @Override
            public void onLoadMore() {
                // Load next page of data (e.g. network or database)
                page++;
                Log.d("SaleItemsController", "calling onLoadMore");
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

        mAdapter = new SaleItemsAdapter(mSaleItems, mActivity, mPresenter, this);
        if (mPresenter.isTablet()) {
            mSaleItemsRecyclerView.setLayoutManager(mLayoutManager = new CustomGridLayoutManager(mActivity, 4));
        } else {
            mSaleItemsRecyclerView.setLayoutManager(mLayoutManager = new CustomGridLayoutManager(mActivity, 2));
        }

        HeaderSpanSizeLookup headerSpanSizeLookup = new HeaderSpanSizeLookup(mAdapter, mLayoutManager);
        mLayoutManager.setSpanSizeLookup(headerSpanSizeLookup);

        mSaleItemsRecyclerView.setAdapter(mAdapter);

        mPaginateManager = PaginateUtils.init(mSaleItemsRecyclerView, mPaginateCallbacks);

        mSearchTagsLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
        mSearchTagsRecyclerView.setLayoutManager(mSearchTagsLayoutManager);
        mSearchTagsAdapter = new SearchTagsAdapter(mActivity, mSearchTagsRecyclerView, mChipFilters, mPresenter);
        mSearchTagsRecyclerView.setAdapter(mSearchTagsAdapter);
        mSearchTagsRecyclerView.setVisibility(View.VISIBLE);
        mSearchTagsAdapter.determineActiveFilters();

        mCategoriesRouter = getChildRouter(mCategoriesContainer);
        mSearchFilterRouter = getChildRouter(mSearchFilterContainer);

        mRxViewDisposables.add(RxView.clicks(mOverlay)
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action -> {
                    onHideSearchFilters();
                }));

        mRxViewDisposables.add(RxView.clicks(mSaleItemsCart)
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action -> {
                    mActivity.getMainController().getHomeViewPager().setCurrentItem(2);
                }));

        setupBottomSheet();
        setupSearchFilters();

        if (mSaleItems.isEmpty()) {
            initialLoad = true;
            mPresenter.loadSaleItems(createSaleItemsRequest(mChosenCategoryKey, "", 0, mChipFilters, ""));
        }

        mControllerChangeListener = new ControllerChangeHandler.ControllerChangeListener() {
            @Override
            public void onChangeStarted(@Nullable Controller to, @Nullable Controller from, boolean isPush, @NonNull ViewGroup container, @NonNull ControllerChangeHandler handler) {
                    isChangeStarted = true;
            }

            @Override
            public void onChangeCompleted(@Nullable Controller to, @Nullable Controller from, boolean isPush, @NonNull ViewGroup container, @NonNull ControllerChangeHandler handler) {
                if(from instanceof SaleItemDetailsController){
                    isChangeStarted = false;
                    defaultBool = false;
                }
            }
        };
        getRouter().addChangeListener(mControllerChangeListener);

        isSetupFinished = true;

    }

    @Override
    public void onDetach(View view) {
        mKeyboardHeightProvider.close();
        isBottomSheetAdjustedHeight = false;
        mRxViewDisposables.dispose();
        super.onDetach(view);
    }

    @Override
    protected void onActivityResumed(@NonNull Activity activity) {
        super.onActivityResumed(activity);
        if (isSetupFinished) {
            mKeyboardHeightProvider.setKeyboardHeightObserver(this);
        }
    }

    @Override
    protected void onActivityPaused(@NonNull Activity activity) {
        super.onActivityPaused(activity);
        mKeyboardHeightProvider.setKeyboardHeightObserver(null);
    }

    @Override
    public void onLoadSortingFacetsFinished(List<SortingResponse> responseList) {

    }

    @Override
    public void showSaleItems(GetSaleItemsResponse getSaleItemsResponse, boolean forFacetCorrection) {
        List<GetSaleItemsResponse.Products> items = getSaleItemsResponse.products;

        if (items.size() == 0 && page != 0) {
            hasLoadedAllItems = true;
            mPaginateManager.setHasMoreDataToLoad(false);
            page = 0;
        } else {
            mFacets = getSaleItemsResponse.facets;

            if (page == 0) {
                mAdapter.replaceData(items);


                mSaleItemsRecyclerView.scrollToPosition(0);
                if (items.size() <= PaginateUtils.LOADING_TRIGGER_THRESHOLD) {
                    hasLoadedAllItems = false;
                    mPaginateManager.setHasMoreDataToLoad(false);
                }
            } else {
                mAdapter.addData(items);
            }
            hasLoadedAllItems = false;
            mPaginateManager.setHasMoreDataToLoad(true);
            loadingInProgress = false;

            if (isCategoryChanged || initialLoad) {
                mSearchFilterController.replaceFacets(mFacets);
                initialLoad = false;
            }

            isCategoryChanged = false;
        }


        mSaleItems = mAdapter.getData();

    }

    @Override
    public void onLoadSaleItemsWhileTyping(String searchQuery) {
        mPresenter.loadSaleItems(createSaleItemsRequest(mChosenCategoryKey, "", page, mChipFilters, searchQuery));
    }

    @Override
    public void refresh() {
        if (mSaleItems.size() < PaginateUtils.LOADING_TRIGGER_THRESHOLD) {
            hasLoadedAllItems = true;
            page = 0;
        } else {
            loadingInProgress = true;
            mPresenter.loadSaleItems(createSaleItemsRequest(mChosenCategoryKey, "", page, mChipFilters, ""));
        }
    }

    @Override
    public void showProductDetails(RecyclerView.ViewHolder viewHolder, int position, String seoIdentifierId, String imageUrl, String skuId, String saleId) {
        if (mSaleItemClickCounter != 1) {
            mSaleItemClickCounter++;

            Handler clickHandler = new Handler();
            clickHandler.postDelayed(() -> {
                mSaleItemClickCounter = 0;
            }, 2000);

//            List<String> names = new ArrayList<>();
//            names.add(getResources().getString(R.string.transition_sale_image_indexed, position));
            mSaleItemsRecyclerView.smoothScrollToPosition(position);

            Bundle bundle = new Bundle();
            bundle.putInt(BundleKeys.SALEITEMDETAILS_KEY_POSITION, position);
            bundle.putString(BundleKeys.SALEITEMDETAILS_KEY_ITEM_IMAGE_ID, imageUrl);
            bundle.putString(BundleKeys.SALEITEMDETAILS_KEY_SEO_IDENTIFIER_ID, seoIdentifierId);
            bundle.putString(BundleKeys.SALEITEMDETAILS_KEY_SKU_ID, skuId);
            bundle.putString(BundleKeys.SALEITEMDETAILS_KEY_SALE_ID, saleId);
            bundle.putString(BundleKeys.SALEITEMDETAILS_KEY_ITEM_NAME, ((SaleItemsAdapter.ShopItemsViewHolder) viewHolder).productName.getText().toString());
            bundle.putString(BundleKeys.SALEITEMDETAILS_KEY_ITEM_PRICE, ((SaleItemsAdapter.ShopItemsViewHolder) viewHolder).productPrice.getText().toString());
            bundle.putString(BundleKeys.SALEITEMDETAILS_KEY_ITEM_OLD_PRICE, ((SaleItemsAdapter.ShopItemsViewHolder) viewHolder).productPreviousPrice.getText().toString());

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                getRouter().pushController(RouterTransaction.with(SaleItemDetailsController.newInstance(bundle))
                        .pushChangeHandler(new FadeChangeHandler())
                        .popChangeHandler(new FadeChangeHandler()));
            } else {
                getRouter().pushController(RouterTransaction.with(SaleItemDetailsController.newInstance(bundle))
//                        .pushChangeHandler(new ArcFadeMoveChangeHandler(getResources().getString(R.string.transition_sale_image_indexed, position)))
//                        .popChangeHandler(new ArcFadeMoveChangeHandler(getResources().getString(R.string.transition_sale_image_indexed, position))));
                        .popChangeHandler(new SharedArcFadePopChangeHandler())
                        .pushChangeHandler(new SharedArcFadePushChangeHandler()));
            }

        }
    }

    @Override
    public boolean handleBack() {
        if (mBottomSheetBehavior.getState() == ViewPagerBottomSheetBehavior.STATE_EXPANDED) {
            mPresenter.hideSearchFilters();
            return true;
        }
        return super.handleBack();
    }

    @Override
    public void unbindPaginate() {
        if (mPaginateManager != null) {
            mPaginateManager.unbind();
        }
    }

    @Override
    public void onShowCategoriesController() {
        mSearchTagsRecyclerView.setEnabled(false);
        mSearchBar.setVisibility(View.GONE);

        mCategoriesContainer.setVisibility(View.VISIBLE);

        if (!mCategoriesRouter.hasRootController()) {
            Bundle bundle = new BundleBuilder(new Bundle())
                    .putString(BundleKeys.CATEGORIES_ITEM_LIST,new Gson().toJson(mCategories))
                    .build();
            Controller controller = ControllerFactory.getInstance(GateKeeper.Destination.CATEGORIES,bundle);
            GateKeeper.setRoot(mCategoriesRouter, GateKeeper.Destination.CATEGORIES, RouterTransaction.with(controller).popChangeHandler(new FadeChangeHandler()).pushChangeHandler(new FadeChangeHandler()));
        } else {
            CategoriesController controller = (CategoriesController) GateKeeper.getCurrentControllerOnRouter(mCategoriesRouter);

            if (controller.getBottomSheetBehavior().getState() == CoordinatorLayoutAsBottomSheetBehavior.STATE_COLLAPSED) {
                controller.getBottomSheetBehavior().setState(CoordinatorLayoutAsBottomSheetBehavior.STATE_EXPANDED);
            }
        }


    }

    @Override
    public void onCategoryClicked(String chosenCategoryName, int color) {
        SaleItemsAdapter.HeaderViewHolder vh = mAdapter.getHeaderViewHolderInstance();
        mChosenCategory = chosenCategoryName;
        if (vh != null) {
            vh.onCategoryChangeUpdateUI(chosenCategoryName, color);
        }
    }

    @Override
    public String getChosenCategory() {
        return mChosenCategory;
    }

    @Override
    public SearchTagsAdapter onGetSearchTagsAdapter() {
        return mSearchTagsAdapter;
    }

    @Override
    public void onUpdateShopFilters() {
        //TODO API CALL FOR CALLING NEW SHOP FILTERS
        page = 0;
        mChipFilters = mSearchTagsAdapter.getData();
        mPresenter.loadSaleItems(createSaleItemsRequest(mChosenCategoryKey, "", page, mChipFilters, ""));
    }

    @OnClick(R.id.sale_items_logo)
    void scrollToTop() {
        mSaleItemsRecyclerView.smoothScrollToPosition(0);
    }

    @Override
    public void onDismissCategoriesController() {
        SaleItemsAdapter.HeaderViewHolder vh = mAdapter.getHeaderViewHolderInstance();
        if (vh != null) {
            vh.showShopCategoryText();
        }
        mSearchTagsRecyclerView.setEnabled(true);
        mSearchBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onExecuteCategoryChangeApiCall(String chosenCategoryKey) {
        mChosenCategoryKey = chosenCategoryKey;
        mSearchTagsAdapter.replaceData(new ArrayList<>());
        mChipFilters = mSearchTagsAdapter.getData();
        mPresenter.loadSaleItems(createSaleItemsRequest(chosenCategoryKey, "", 0, mChipFilters, ""));
        isCategoryChanged = true;
    }

    @Override
    public void onShowTransparentOverlay() {
        mOverlay.setVisibility(View.VISIBLE);
        mOverlay.bringToFront();
    }

    @Override
    public void onHideTransparentOverlay() {
        mSearchTagsRecyclerView.clearFocus();
        mOverlay.setVisibility(View.GONE);
        hideKeyboard();
    }

    @Override
    public void onShowSearchFilters(String facetFilterName) {
        onShowTransparentOverlay();
        mSearchFilterContainer.bringToFront();
        mSearchFilterPresenter.selectTabOfFilterType(facetFilterName);
        mBottomSheetBehavior.setState(ViewPagerBottomSheetBehavior.STATE_EXPANDED);
//        mSearchTagsAdapter.getEditTextViewHolder().subscribeTextChange();
    }

    @Override
    public void onHideSearchFilters() {
        onHideTransparentOverlay();
        mBottomSheetBehavior.setState(ViewPagerBottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void onShowKeyboard() {
        mSearchTagsAdapter.getEditTextViewHolder().getEditText().requestFocus();
        KeyboardUtils.showSoftInput(mSearchTagsAdapter.getEditTextViewHolder().getEditText(), mActivity);
    }

    private void setupSearchFilters() {
        mSearchFilterContainer.setVisibility(View.VISIBLE);

        if (!mSearchFilterRouter.hasRootController()) {
            mSearchFilterController = (SearchFilterController) ControllerFactory.getInstance(GateKeeper.Destination.SEARCH_FILTER);
            GateKeeper.setRoot(mSearchFilterRouter, GateKeeper.Destination.SEARCH_FILTER, RouterTransaction.with(mSearchFilterController));
            mSearchFilterRouter.addChangeListener(new ControllerChangeHandler.ControllerChangeListener() {
                @Override
                public void onChangeStarted(@Nullable Controller to, @Nullable Controller from, boolean isPush, @NonNull ViewGroup container, @NonNull ControllerChangeHandler handler) {

                }

                @Override
                public void onChangeCompleted(@Nullable Controller to, @Nullable Controller from, boolean isPush, @NonNull ViewGroup container, @NonNull ControllerChangeHandler handler) {
                    if (to instanceof SearchFilterController) {
                        mSearchFilterPresenter.onAttach((SearchFilterMvpView) GateKeeper.getCurrentControllerOnRouter(mSearchFilterRouter));
                    }
                }
            });
        }
    }

    private GetSaleItemsRequest createSaleItemsRequest(String categoryKey, String saleId, int pageNumber, List<SearchChipModel> chipsList, @NonNull String searchString) {
        List<String> saleIds = new LinkedList<>();
        HashMap<String, List<String>> facetFilters = new HashMap<>();

        GetSaleItemsRequest getSaleItemsRequest = new GetSaleItemsRequest();

        if (categoryKey != null && !categoryKey.isEmpty())
            getSaleItemsRequest.setCategoryKey("[\"" + categoryKey + "\"]");
        else
            getSaleItemsRequest.setCategoryKey("[]");


        getSaleItemsRequest.setSorting("");
        getSaleItemsRequest.setPageNumber(String.valueOf(pageNumber));
        getSaleItemsRequest.setQuery("");

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
//                        getSaleItemsRequest.setSorting(mapSortingTitleToKey(chip.getChipTitle()));
                    }
                }

                facetFilters.put(BundleKeys.BRANDS_FACETFILTER_NAME, brandNameFacetFilters);
                facetFilters.put(BundleKeys.COLORS_FACETFILTER_NAME, colorFacetFilters);
                facetFilters.put(BundleKeys.SIZES_FACETFILTER_NAME, sizesFacetFilters);
                facetFilters.put(BundleKeys.PRICE_FACETFILTER_NAME, priceFacetFilters);


                StringBuilder result = new StringBuilder();
                for (int i = 0; i < searchQueryFilters.size(); i++) {
                    if (i > 0) {
                        result.append(" ");
                    }
                    result.append(searchQueryFilters.get(i));
                }

                if (!searchString.isEmpty()) {
                    result.append(" " + searchString);
                }

                getSaleItemsRequest.setQuery(result.toString());

            } else {
                getSaleItemsRequest.setQuery(searchString);
            }

            getSaleItemsRequest.setHasFilters(true);
        }

        String facetFiltersString = new Gson().toJson(facetFilters);

        getSaleItemsRequest.setFacetFilter(facetFiltersString);

        page = pageNumber;

        return getSaleItemsRequest;
    }

    private void setupBottomSheet() {

        mBottomSheetBehavior = ViewPagerBottomSheetBehavior.from(mSearchFilterContainer);
        mBottomSheetBehavior.setPeekHeight(0);
        mBottomSheetBehavior.setBottomSheetCallback(new ViewPagerBottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case ViewPagerBottomSheetBehavior.STATE_COLLAPSED:
                        mActivity.setDraggableViewPager(true);
                        onHideTransparentOverlay();
                        mIsSearchFiltersShown = false;

                        EditText searchEditText = mSearchTagsAdapter.getEditTextViewHolder().getEditText();
                        if (searchEditText != null && !searchEditText.getText().toString().isEmpty()) {
                            //add ellipses
                            String searchTextQuery = searchEditText.getText().toString();
                            String trimmedText = searchTextQuery.length() > 15 ? searchTextQuery.substring(0, 14) + ".." : searchTextQuery;

                            mSearchTagsAdapter.add(new SearchChipModel(BundleKeys.SEARCH_QUERY_NAME, trimmedText, -1));

                            searchEditText.getText().clear();
                        }

                        break;
                    case ViewPagerBottomSheetBehavior.STATE_EXPANDED:
                        mActivity.setDraggableViewPager(false);
                        mIsSearchFiltersShown = true;
                        break;
                    case ViewPagerBottomSheetBehavior.STATE_SETTLING:
                        break;

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        mBottomSheetBehavior.setState(ViewPagerBottomSheetBehavior.STATE_COLLAPSED);

    }


    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {
        if (height == 0) {
            isKeyboardOpen = false;
        } else {
            isKeyboardOpen = true;
            if (!isBottomSheetAdjustedHeight) {
                View bottomSheetChild = mSearchFilterContainer.getChildAt(0);
                if (bottomSheetChild != null) {
                    ChangeHandlerFrameLayout.LayoutParams params = (ChangeHandlerFrameLayout.LayoutParams) bottomSheetChild.getLayoutParams();
                    params.height = height + dpToPx(50);
                    bottomSheetChild.setLayoutParams(params);
                    mSearchFilterContainer.postDelayed(() -> mSearchFilterContainer.requestLayout(), 500);
                    isBottomSheetAdjustedHeight = true;
                }
            }
        }
    }

    @Override
    public boolean isDefaultBool(){
        return defaultBool;
    }

    @Override
    public boolean isChangeStarted() {
        return isChangeStarted ;
    }

    @Override
    public void onCallGetBasketItemsQuantity() {
        mCartCounter.setText(CartUtil.getCartValue()+"");
    }

    @Override
    public void onCallGetCategoryTree(List<GetCategoryTreeResponse> response) {
        mCategories = response;
    }

    @Override
    public void setCallGetCategoryTreeFinished(boolean val) {
        mIsCallGetCategoryTreeFinished = val;
    }

    @Override
    public boolean isCallGetCategoryTreeFinished() {
        return mIsCallGetCategoryTreeFinished;
    }
}




