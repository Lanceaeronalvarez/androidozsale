package au.com.dealsdirect.ui.controller.saleitems;

import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluelinelabs.conductor.Controller;
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
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsRequest;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
import au.com.dealsdirect.data.network.model.sorting.SortingResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.categories.CategoriesController;
import au.com.dealsdirect.ui.controller.saleitemdetails.SaleItemDetailsController;
import au.com.dealsdirect.ui.controller.searchfilter.SearchFilterController;
import au.com.dealsdirect.ui.controller.searchfilter.ViewPagerBottomSheetBehavior;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchTagsAdapter;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;
import au.com.dealsdirect.ui.custom.transitions.ArcFadeMoveChangeHandler;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.CartUtil;
import au.com.dealsdirect.utils.PaginateUtils;
import au.com.dealsdirect.utils.module.ControllerFactory;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by smartwave on 02/11/2017.
 */

public class SaleItemsController extends BaseController implements SaleItemsMvpView {

    @Inject
    SaleItemsMvpPresenter<SaleItemsMvpView> mPresenter;

    @BindView(R.id.sale_items_recyclerview)
    RecyclerView mSaleItemsRecyclerView;

    @BindView(R.id.controller_categories_frame)
    ViewGroup mCategoriesContainer;
    @BindView(R.id.controller_search_filter_frame)
    ViewGroup mSearchFilterContainer;

    @BindView(R.id.sale_items_search_bar)
    AppBarLayout mSearchBar;
    @BindView(R.id.sale_items_cart_counter)
    TextView mCartCounter;
    @BindView(R.id.overlay)
    RelativeLayout mOverlay;

    ViewPagerBottomSheetBehavior mBottomSheetBehavior;
    boolean mIsFiltersShown;


    SaleItemsAdapter mAdapter;
    CustomGridLayoutManager mLayoutManager;
    List<SearchChipModel> mChipFilters = new ArrayList<>();
    private List<GetSaleItemsResponse.Facets> mFacets = new ArrayList<>();

    private String mChosenCategory = "";
    private String mDefaultCategory = "";
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
    RecyclerView mSearchTagsRecyclerView;

    public static SaleItemsController newInstance() {
        return new SaleItemsController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public SaleItemsController(Bundle args) {
        super(args);

//        if (args.containsKey(KEY_SALE_ID))
//            mSaleId = getArgs().getString(KEY_SALE_ID, "");
//        if (args.containsKey(KEY_CATEGORY_MAP))
//            mCategoryKey = getArgs().getString(KEY_CATEGORY_MAP, "");
//        if (args.containsKey(KEY_CHIPS_FILTER))
//            mChipFilters = JsonUtils.convertStringToObject(getArgs().getString(KEY_CHIPS_FILTER, ""), new TypeToken<ArrayList<SearchChipModel>>() {
//            }.getType());
//        if (args.containsKey(KEY_FROM_SHOP_SEARCH))
//            mFromShopSearch = getArgs().getBoolean(KEY_FROM_SHOP_SEARCH, true);
//        if (args.containsKey(KEY_FROM_CATEGORY_SEARCH))
//            mFromCategorySearch = getArgs().getBoolean(KEY_FROM_CATEGORY_SEARCH, true);
//        if (args.containsKey(KEY_FROM_CATEGORIES)) {
//            mIsFromCategory = getArgs().getBoolean(KEY_FROM_CATEGORIES, true);
//        }
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
        mPresenter.onAttach(this);
        mSaleItemClickCounter = 0;
        super.onAttach(view);
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        mActivity.setSaleItemsRouter(getRouter());
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        mDefaultCategory = getResources().getString(R.string.category_default);

        mCartCounter.setText(CartUtil.getCartValue() + "");

        mCategoriesRouter = getChildRouter(mCategoriesContainer);
        mSearchFilterRouter = getChildRouter(mSearchFilterContainer);

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

        if (mSaleItems.isEmpty()) {
            mPresenter.loadSaleItems(createSaleItemsRequest("", "", 0, mChipFilters));
        }


        RxView.clicks(mSearchBar)
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((action) -> {
                    showSearchFilters();
                });

        RxView.clicks(mOverlay)
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action -> {
                    hideSearchFilters();
                });

        setupBottomSheet();
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
        }

        setupSearchFilters();

        mSaleItems = mAdapter.getData();

    }

    @Override
    public void refresh() {
        if (mSaleItems.size() < PaginateUtils.LOADING_TRIGGER_THRESHOLD) {
            hasLoadedAllItems = true;
            page = 0;
        } else {
            loadingInProgress = true;
            mPresenter.loadSaleItems(createSaleItemsRequest("", "", page, mChipFilters));
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
                        .pushChangeHandler(new ArcFadeMoveChangeHandler(getResources().getString(R.string.transition_sale_image_indexed, position)))
                        .popChangeHandler(new ArcFadeMoveChangeHandler(getResources().getString(R.string.transition_sale_image_indexed, position))));
//                        .popChangeHandler(new SharedArcFadePopChangeHandler())
//                        .pushChangeHandler(new SharedArcFadePushChangeHandler()));
            }

        }
    }

    @Override
    public boolean handleBack() {
        return super.handleBack();
    }

    @Override
    public void unbindPaginate() {
        if (mPaginateManager != null) {
            mPaginateManager.unbind();
        }
    }

    @Override
    public void onPassFiltersData(Bundle bundle) {

    }

    @Override
    public void showCategoriesController() {
        mSearchBar.setVisibility(View.GONE);

        mCategoriesContainer.setVisibility(View.VISIBLE);

        if (!mCategoriesRouter.hasRootController()) {
            Controller controller = ControllerFactory.getInstance(GateKeeper.Destination.CATEGORIES);
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
    public void onShowSelectedCategoryText() {
        SaleItemsAdapter.HeaderViewHolder vh = mAdapter.getHeaderViewHolderInstance();
        if (vh != null) {
            vh.showShopCategoryText();
        }
        mSearchBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onExecuteCategoryChangeApiCall(String chosenCategoryKey) {
        mPresenter.loadSaleItems(createSaleItemsRequest(chosenCategoryKey, "", 0, mChipFilters));
    }

    @Override
    public void onShowTransparentOverlay() {
        mOverlay.setVisibility(View.VISIBLE);
        mOverlay.bringToFront();
    }

    @Override
    public void onHideTransparentOverlay() {
//        mSearchTagsRecyclerView.clearFocus();
        mOverlay.setVisibility(View.GONE);
        hideKeyboard();
    }

    @Override
    public void showSearchFilters() {
        mPresenter.showTransparentOverlay();
        mSearchFilterContainer.bringToFront();
        mIsFiltersShown = true;
        mBottomSheetBehavior.setState(ViewPagerBottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void hideSearchFilters() {
        mPresenter.hideTransparentOverlay();
        mIsFiltersShown = false;
        mBottomSheetBehavior.setState(ViewPagerBottomSheetBehavior.STATE_COLLAPSED);
    }

    private void setupSearchFilters() {
        mSearchFilterContainer.setVisibility(View.VISIBLE);

        if (!mSearchFilterRouter.hasRootController()) {
            Bundle bundle = new Bundle();
            bundle.putString(BundleKeys.KEY_FACET_STRING, new Gson().toJson(mFacets));
            mSearchFilterController = (SearchFilterController) ControllerFactory.getInstance(GateKeeper.Destination.SEARCH_FILTER, bundle);
            GateKeeper.setRoot(mSearchFilterRouter, GateKeeper.Destination.SEARCH_FILTER, RouterTransaction.with(mSearchFilterController));
        }
    }

    private GetSaleItemsRequest createSaleItemsRequest(String categoryKey, String saleId, int pageNumber, List<SearchChipModel> chipsList) {
        List<String> saleIds = new LinkedList<>();
        HashMap<String, List<String>> facetFilters = new HashMap<>();

        GetSaleItemsRequest getSaleItemsRequest = new GetSaleItemsRequest();

        if (!categoryKey.isEmpty())
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

                        //TODO add this logic in sale items
//                        overlay.setVisibility(View.GONE);
//                        mRecyclerView.addOnItemTouchListener(mShopOnItemTouchlistener);
//                        hideKeyboard();
//
//                        String searchTextQuery = mSearchTagAdapter.getEditTextViewHolder().getEditText().getText().toString();
//
//                        if (!searchTextQuery.isEmpty()) {
//                            //add ellipses
//                            String trimmedText = searchTextQuery.length() > 15 ? searchTextQuery.substring(0, 14) + ".." : searchTextQuery;
//
//                            mSearchQueryPair = new Pair<>(SEARCH_QUERY_TAG, trimmedText);
//                            mSearchTagAdapter.add(mSearchQueryPair);
//
//                        }
                        break;
                    case ViewPagerBottomSheetBehavior.STATE_EXPANDED:
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

    private SearchChipModel findPriceChip() {
        for (SearchChipModel chip : mSearchTagsAdapter.getData()) {
            if (chip.getFilterType().equals(BundleKeys.PRICE_FACETFILTER_NAME)) {
                return chip;
            }
        }

        return null;
    }

//    private String getFacetFilterType(int position) {
//        return mFacetsAdapter.getData().get(position).first;
//    }
//
//    private List<String> mapFacetItemClicked(int position) {
//        String facetFilterType = getFacetFilterType(position);
//        switch (facetFilterType) {
//            case BundleKeys.SORT_FACETFILTER_NAME:
//                return mSortingList;
//            case BundleKeys.CATEGORY_TREE_FACET:
//                return new ArrayList<>();
//            case BundleKeys.BRANDS_FACETFILTER_NAME:
//                return mBrandList;
//            case BundleKeys.SIZES_FACETFILTER_NAME:
//                return mSizeList;
//            case BundleKeys.COLORS_FACETFILTER_NAME:
//                return mColorList;
//            case BundleKeys.PRICE_FACETFILTER_NAME:
//                return new ArrayList<>();
//            default:
//                return new ArrayList<>();
//        }
//    }



}
