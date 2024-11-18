package au.com.dealsdirect.ui.controller.salefilter;

import static au.com.dealsdirect.service.datacollection.registerservices.GenieEventService.getDataManager;
import static au.com.dealsdirect.utils.BundleKeys.BRANDS_FACET_FILTER_TYPE;
import static au.com.dealsdirect.utils.BundleKeys.COLOR_FACET_FILTER_TYPE;
import static au.com.dealsdirect.utils.BundleKeys.DELIVERY_FACET_FILTER_TYPE;
import static au.com.dealsdirect.utils.BundleKeys.NEW_ARRIVAL_FACET_FILTER_TYPE;
import static au.com.dealsdirect.utils.BundleKeys.PRICE_FACET_FILTER_TYPE;
import static au.com.dealsdirect.utils.BundleKeys.SIZE_FACET_FILTER_TYPE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.banner.GetBannerResponse;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsRequest;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
import au.com.dealsdirect.data.network.model.saleitems.SaleItemFacet;
import au.com.dealsdirect.data.network.model.sorting.SortingResponse;
import au.com.dealsdirect.service.datacollection.enums.SearchOperationType;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.main.Settings;
import au.com.dealsdirect.ui.controller.searchfilter.SearchFilterMvpPresenter;
import au.com.dealsdirect.ui.controller.searchfilter.SearchFilterMvpRepository;
import au.com.dealsdirect.ui.controller.searchfilter.SearchFilterMvpView;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;
import au.com.dealsdirect.ui.custom.CustomRangeSeekbar;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.StringUtils;
import butterknife.BindView;


public class SaleFilterController extends BaseController implements SaleFilterClickListener, SearchFilterMvpView {

    @BindView(R.id.controller_salecategory_recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.partial_toolbar_title)
    TextView mToolbarTitle;

    @BindView(R.id.partial_toolbar_left_view)
    View mToolbarLeftButton;

    @BindView(R.id.partial_toolbar_right_view)
    TextView mToolbarRightButton;

    @BindView(R.id.sale_item_results)
    TextView mSaleItemResults;

    @BindView(R.id.sale_filter_tile_sold_out)
    LinearLayout mSoldOutLayout;

    @BindView(R.id.chip_group)
    ChipGroup mChipGroup;

    @BindView(R.id.sale_filter_see_all_button)
    RelativeLayout mSeeAll;

    @BindView(R.id.filtered_by_text)
    TextView mFilterByText;

    @BindView(R.id.price_facet_range_seekbar)
    CustomRangeSeekbar mSeekbar;

    @BindView(R.id.seekbar_main_layout)
    RelativeLayout mSeekbarLayout;

    @BindView(R.id.movingMaxPriceLayout)
    LinearLayout mMaxPriceMovingLayout;

    @BindView(R.id.movingMinPriceLayout)
    LinearLayout mMinPriceMovingLayout;

    @BindView(R.id.movingMaxPrice)
    TextView mMaxPrice;

    @BindView(R.id.movingMinPrice)
    TextView mMinPrice;

    @BindView(R.id.sale_filter_padding)
    RelativeLayout mPadding;

    @BindView(R.id.price_text_view)
    TextView mPriceText;

    @BindView(R.id.sale_filter_horizontal_view)
    HorizontalScrollView mHorizontalView;

    @Inject
    SearchFilterMvpPresenter<SearchFilterMvpView> mPresenter;

    private SaleFilterAdapter mFilterAdapter;
    private List<Pair<String, String>> mFacetFilters = new ArrayList();
    private Set<SearchChipModel> mSearchItemsList = new HashSet<>();
    private List<SaleItemFacet> mFacets;
    private ArrayList<String> mBrandList = new ArrayList<>();
    private ArrayList<String> mBrandCountList = new ArrayList<>();
    private ArrayList<String> mSizeList = new ArrayList<>();
    private ArrayList<String> mSizeCountList = new ArrayList<>();
    private ArrayList<String> mColorList = new ArrayList<>();
    private ArrayList<String> mColorCountList = new ArrayList<>();
    private ArrayList<SortingResponse> mSortingList = new ArrayList<>();
    private ArrayList<String> mDelivery = new ArrayList<>();
    private ArrayList<String> mDeliveryCountList = new ArrayList<>();
    private ArrayList<String> mNewArrivals = new ArrayList<>();
    private ArrayList<String> mNewArrivalsCountList = new ArrayList<>();
    private ArrayList<String> mCategories = new ArrayList<>();

    private ArrayList<String> mCurrentCountList = new ArrayList<>();
    private List<String> mFiltersToDisplay = new ArrayList();
    private List<SearchChipModel> mSelectedFilters = new ArrayList();
    private Set<SearchChipModel> mPreselectedFilter = new HashSet<>();
    private int mfilterLevel = 1; //1 - top level ; 2 - second level
    private int mOrigMinValue = -1;
    private int mOrigMaxValue = -1;
    private boolean mIsSeeAllClicked = false;
    private int minPrice = 0;
    private int maxPrice = 200;
    private int mGeniemaxPrice = 200;
    private Set<String> mCategoryKeys = new LinkedHashSet<>();
    private Set<String> mPreSelectedCategoryKeys = new LinkedHashSet<>();
    private List<String> mCategoryTitles = new ArrayList<>();
    private List<GetCategoryTreeResponse> mCategoryTree;
    private List<SortingResponse> mSortingFacets = new ArrayList<>();
    private Map<String, GetCategoryTreeResponse> mCategoryMap = new HashMap<>();
    boolean mShowSubCategories = false;
    boolean mShowSort = false;
    boolean mShowCategory = true;
    private GetBannerResponse.LinkOptions linkOptions = null;
    private String mSaleId = "";
    String mFilterType = "";
    int mSaleItemCount = 0;
    private String mCategoryKey;
    private String mSourceType;
    private boolean mRemoveCategoryKeyFromCategory = false;
    SearchFilterMvpRepository mRepository;
    private static final int DEFAULT_PRICE_THRESHOLD = 200;

    boolean mShowColor = false;

    public SaleFilterController(Bundle build) {
    }

    public static SaleFilterController newInstance() {
        return new SaleFilterController(new BundleBuilder(new Bundle()).build());
    }

    public static SaleFilterController newInstance(
            List<String> mCategories,
            List<SaleItemFacet> mFacets,
            SearchFilterMvpRepository repository,
            List<SearchChipModel> selectedFilters,
            List<String> categoryKeys,
            List<SortingResponse> mSortingResponse,
            List<GetCategoryTreeResponse> mCategoryTreeResponse,
            boolean showSort,
            int saleItemCount,
            String saleId,
            GetBannerResponse.LinkOptions linkOptions,
            int genieMaxPrice,
            String categoryKey, String sourceType) {
        SaleFilterController controller = SaleFilterController.newInstance();

        controller.mCategories.addAll(mCategories);
        controller.parseFacets(mFacets);
        controller.mRepository = repository;
        controller.mSelectedFilters.addAll(selectedFilters);
        controller.mPreselectedFilter.addAll(selectedFilters);
        controller.mCategoryTree = mCategoryTreeResponse;
        controller.mSortingFacets.addAll(mSortingResponse);
        controller.mCategoryKeys.addAll(categoryKeys);
        controller.mPreSelectedCategoryKeys.addAll(categoryKeys);
        controller.mShowSort = showSort;
        controller.mSaleItemCount = saleItemCount;
        controller.mSaleId = saleId;
        controller.linkOptions = linkOptions;
        controller.mGeniemaxPrice = genieMaxPrice;
        controller.mCategoryKey = categoryKey;
        controller.mSourceType = sourceType;

        return controller;
    }

    @Override
    protected void setUp(View view) {
        mActivity.getMainController().hideBottomNav();
        mToolbarTitle.setText("Filter");
        setupFilters();
        setupPriceFacet();
        showClearButton();
        mSaleItemResults.setText("See all " + mSaleItemCount + " products");
    }

    private void createCategoryMap(List<GetCategoryTreeResponse> getCategoryTreeResponses) {
        if (getCategoryTreeResponses != null && !getCategoryTreeResponses.isEmpty()) {
            List<GetCategoryTreeResponse> mCategoryTreeFromCategories = new ArrayList<>();
            for (GetCategoryTreeResponse category : getCategoryTreeResponses) {
                mCategoryMap.put(category.getKey(), category);
                createCategoryMap(category.getChildren());
                if (mPreSelectedCategoryKeys.contains(category.getKey())) {
                    mCategoryTitles.add(category.getName());
                    addChipGroupSubCategoryChip(category.getName(), category.getKey());
                    if (category.getKey().equals(mCategoryKey) && category.getChildren().isEmpty()) {
                        mShowCategory = false;
                    }
                }
            }
        }
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_salefilter, container, false);
        getControllerComponent().inject(this);

        mPresenter.onAttach(this);
        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        assert (mActivity) != null;
        mToolbarLeftButton.setOnClickListener(it -> {
            mActivity.onBackPressed();
        });

        mSeeAll.setOnClickListener(it -> {
            seeAllProducts();
        });

        mToolbarRightButton.setOnClickListener(it -> {
            clearFilters();
        });

        mShowColor = getDataManager().isColorFilterEnabled();

        setUp(view);
    }

    void setupFilters() {
        if (!mSelectedFilters.isEmpty() || !mCategoryKeys.isEmpty()) {
            if (mSelectedFilters.size() == 1 && mCategoryKeys.isEmpty()) {
                if (!mSelectedFilters.get(0).getFilterType().equals(BundleKeys.SORT_FACETFILTER_NAME)) {
                    mFilterByText.setVisibility(View.VISIBLE);
                }
            } else {
                mFilterByText.setVisibility(View.VISIBLE);
            }
        }

        mPresenter.requestCategoryMap();
        if (mCategoryMap.isEmpty()) {
            createCategoryMap(mCategoryTree);
        }

        for (SearchChipModel filter : mPreselectedFilter) {
            if (!filter.getFilterType().equals(BundleKeys.SORT_FACETFILTER_NAME)) {
                addChipGroupFilterChip(filter.getChipTitle());
            }
        }

        if (mSortingFacets != null) {
            mSortingList = new ArrayList<>(mSortingFacets);
        }

        Collections.sort(mCategories, String.CASE_INSENSITIVE_ORDER);
        List<String> sortedCategories = new ArrayList<>(mCategories);
        for (String filterTitles : sortedCategories) {
            if (filterTitles.equals("Category")) {
                mCategories.remove(filterTitles);
                mCategories.add(0, filterTitles);
            } else if (filterTitles.equals("Color")) {
                mCategories.remove(filterTitles);
                mCategories.add(4, filterTitles);
            }
        }
        mFiltersToDisplay.addAll(mCategories);

        setRepository(mRepository);

        if (mShowSort) {
            onCategoryClicked("Sort", 7);
        } else {
            setRecyclerAdapter("", new ArrayList<>());
        }

    }

    @Override
    public void onCategoryClicked(String category, int type) {
        mfilterLevel = 2;
        showClearButton();

        if (category.equals("Category")) {
            mToolbarTitle.setText(category);
            ArrayList list = new ArrayList();
            mShowSubCategories = true;
            setSecondLevelFilters(list, "", list);
        }
        if (category.equals("Size")) {
            mToolbarTitle.setText(category);
            setSecondLevelFilters(mSizeList, BundleKeys.SIZES_FACETFILTER_NAME, mSizeCountList);
        }
        if (category.equals("Price")) {
            mToolbarTitle.setText(category);
            mChipGroup.setVisibility(View.GONE);
            mFilterByText.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.GONE);
            mSoldOutLayout.setVisibility(View.GONE);
            mSeekbarLayout.setVisibility(View.VISIBLE);
            mPriceText.setVisibility(View.VISIBLE);
            mFilterType = BundleKeys.PRICE_FACETFILTER_NAME;
        }
        if (category.equals("Brands")) {
            mToolbarTitle.setText(category);
            setSecondLevelFilters(mBrandList, BundleKeys.BRANDS_FACETFILTER_NAME, mBrandCountList);
        }
        if (category.equals("Color")) {
            mToolbarTitle.setText(category);
            setSecondLevelFilters(mColorList, BundleKeys.COLORS_FACETFILTER_NAME, mColorCountList);
        }
        if (category.equals("Delivery")) {
            mToolbarTitle.setText(category);
            setSecondLevelFilters(mDelivery, BundleKeys.DELIVERY_FACETFILTER_NAME, mDeliveryCountList);
        }
        if (category.equals("New Arrivals")) {
            mToolbarTitle.setText(category);
            setSecondLevelFilters(mNewArrivals, BundleKeys.NEW_ARRIVAL_FACETFILTER_NAME, mNewArrivalsCountList);
        }
        if (category.equals("Sort")) {
            mToolbarTitle.setText(category);
            mShowSort = true;
            setSecondLevelFilters(new ArrayList(), BundleKeys.SORT_FACETFILTER_NAME, new ArrayList<>());
        }

    }

    @Override
    public void onAddFilter(String filterType, String title, SearchChipModel chip) {
        SearchChipModel newChip = new SearchChipModel(filterType, title, "");
        if (!filterType.equals(BundleKeys.SORT_FACETFILTER_NAME)) {
            addChipGroupFilterChip(title);
        }
        mSelectedFilters.add(newChip);
        mSearchItemsList.add(chip);
        mPresenter.onFacetItemClicked(mSearchItemsList, chip, true);
        showClearButton();
    }

    @Override
    public void onRemoveFilter(String filterType, String title, SearchChipModel chip) {
        for (int i = 0; i < mSelectedFilters.size(); i++) {
            if (mSelectedFilters.get(i).getFilterType() == filterType && mSelectedFilters.get(i).getChipTitle().equals(title)) {
                mSelectedFilters.remove(i);
            }
        }
        mSearchItemsList.remove(chip);
        mPresenter.onFacetItemClicked(mSearchItemsList, chip, false);
        removeChipGroupFilterChip(title);
    }

    @Override
    public void onAddCategoryFilter(String filterType, String title) {
        if (!mCategories.contains(filterType)) {
            mCategoryKeys.add(filterType);
            mCategoryTitles.add(title);
            addChipGroupSubCategoryChip(title, filterType);
            showClearButton();
        }
    }

    @Override
    public void onRemoveCategoryFilter(String filterType, String title) {
        if (filterType == mCategoryKey) {
            mRemoveCategoryKeyFromCategory = true;
        }
        mCategoryKeys.remove(filterType);
        mCategoryTitles.remove(title);
        removeChipGroupFilterChip(title);
    }


    @Override
    public void removeAllSort() {
        clearFilters();
    }

    @Override
    public void updateSeeAllProducts() {
        requestLoadItems();
    }

    void addChipGroupFilterChip(String title) {
        Chip chip = (Chip) mActivity.getLayoutInflater().inflate(R.layout.single_chip_layout, mChipGroup, false);
        chip.setText(title);
        chip.setCloseIconVisible(true);
        chip.setCheckable(false);
        chip.setOnCloseIconClickListener(it -> {
            for (int i = 0; i < mSelectedFilters.size(); i++) {
                if (mSelectedFilters.get(i).getChipTitle().equals(title) || mSelectedFilters.get(i).getFilterType().equals(BundleKeys.SORT_FACETFILTER_NAME)) {
                    if (mSelectedFilters.get(i).getFilterType() == BundleKeys.PRICE_FACETFILTER_NAME) {
                        onResetPriceRange();
                    }
                    SearchChipModel chipToRemove = mSelectedFilters.get(i);
                    Set<SearchChipModel> updateSelectedFilter = new HashSet<>();
                    updateSelectedFilter.addAll(mSelectedFilters);
                    mPresenter.onFacetItemClicked(updateSelectedFilter, chipToRemove, false);
                    mSelectedFilters.remove(i);
                }
            }
            mChipGroup.removeView(it);
            if (mChipGroup.getChildCount() == 0) {
                mFilterByText.setVisibility(View.GONE);
                mToolbarRightButton.setVisibility(View.GONE);
            }
            requestLoadItems();
        });
        mChipGroup.addView(chip);
    }

    void addChipGroupSubCategoryChip(String title, String filterType) {
        Chip chip = (Chip) mActivity.getLayoutInflater().inflate(R.layout.single_chip_layout, mChipGroup, false);
        chip.setText(title);
        chip.setCloseIconVisible(true);
        chip.setCheckable(false);
        chip.setOnCloseIconClickListener(it -> {
            mCategoryKeys.remove(filterType);
            mCategoryTitles.remove(title);
            mChipGroup.removeView(it);
            if (filterType.equals(mCategoryKey)) {
                mRemoveCategoryKeyFromCategory = true;
            }
            if (mChipGroup.getChildCount() == 0) {
                mFilterByText.setVisibility(View.GONE);
                mToolbarRightButton.setVisibility(View.GONE);
            }
            requestLoadItems();
        });
        mChipGroup.addView(chip);
    }

    void removeChipGroupFilterChip(String title) {
        for (int i = 0; i < mChipGroup.getChildCount(); i++) {
            Chip chipToRemove = (Chip) mChipGroup.getChildAt(i);
            if (chipToRemove.getText().equals(title)) {
                mChipGroup.removeView(chipToRemove);
            }
        }
        if (mChipGroup.getChildCount() == 0) {
            mFilterByText.setVisibility(View.GONE);
            mToolbarRightButton.setVisibility(View.GONE);
        }
    }

    public void setSecondLevelFilters(ArrayList filers, String type, ArrayList<String> filterCountList) {
        mChipGroup.setVisibility(View.GONE);
        mFilterByText.setVisibility(View.GONE);
        mFiltersToDisplay.clear();
        mSoldOutLayout.setVisibility(View.GONE);
        mfilterLevel = 2;
        mFiltersToDisplay.addAll(filers);
        mFilterType = type;
        setRecyclerAdapter(type, filterCountList);
    }

    public void setRecyclerAdapter(String type, ArrayList<String> filterCountList) {
        mCurrentCountList = filterCountList;
        mFilterAdapter = new SaleFilterAdapter(
                mActivity,
                this,
                mfilterLevel,
                new ArrayList<>(),
                mFiltersToDisplay,
                type,
                mSelectedFilters,
                mCategoryTree,
                mShowSubCategories,
                mCategoryKeys,
                mSortingList,
                mShowSort,
                filterCountList,
                mShowColor,
                mCategoryKey,
                mSourceType,
                mShowCategory);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
        mRecyclerView.setAdapter(mFilterAdapter);
        mRecyclerView.setMotionEventSplittingEnabled(false);
        mSearchItemsList.addAll(mSelectedFilters);
        mFilterAdapter.setSearchItemsList(mSearchItemsList);
        mFilterAdapter.notifyDataSetChanged();
    }

    @Override
    public void setRepository(SearchFilterMvpRepository repository) {
        if (repository != null && mPresenter != null) {
            mPresenter.setRepository(repository);
        }
    }

    @Override
    public void showFacetItem(int position) {

    }

    @Override
    public void updateFacetItemToFilters(Set<SearchChipModel> selectedChips, SearchChipModel chipChanged, boolean isAdded) {
        if (!isViewAttached() || !isAttached()) {
            return;
        }
        mPresenter.requestUpdate(
                mCategoryKeys,
                selectedChips,
                chipChanged.getFilterType(),
                chipChanged.getFilterType().equals(BundleKeys.SORT_FACETFILTER_NAME) ? chipChanged.getKey() : chipChanged.getChipTitle(),
                null,
                mBrandList.size(),
                minPrice,
                maxPrice,
                mSizeList,
                isAdded ? SearchOperationType.CHECKBOX : SearchOperationType.UNCHECKBOX);
    }

    @Override
    public void onResetPriceRange() {
        if (!isViewAttached() || !isAttached()) {
            return;
        }
        if (mSeekbar == null) {
            return;
        }
        mSeekbar.setMinStartValue(mOrigMinValue);
        mSeekbar.setMaxStartValue(mOrigMaxValue);

        mSeekbar.setMinValue(mOrigMinValue)
                .setMaxValue(mOrigMaxValue)
                .setMinStartValue(mOrigMinValue)
                .setMaxStartValue(mOrigMaxValue)
                .apply();

        mSeekbar.resetMovingLayoutVisibility();
    }

    @Override
    public void replaceCategoryTree(List<GetCategoryTreeResponse> categoryTree) {

    }

    @Override
    public void replaceSearchChipModels(Set<SearchChipModel> chipModels) {
        if (!isViewAttached() || !isAttached()) {
            return;
        }
    }

    @Override
    public void onCategoryClicked(GetCategoryTreeResponse category) {

    }

    @Override
    public Set<String> getCategoryKeys() {
        return null;
    }

    @Override
    public void clearCategoryKeys() {

    }

    @Override
    public void setSearchFilterControllerActive(boolean isTabActive) {

    }

    @Override
    public void setFacetFilterItems(List<Pair<String, String>> mFacetFilters) {

    }

    @Override
    public List<Pair<String, String>> parseFacets(List<SaleItemFacet> facets) {
        mFacets = facets;
        mBrandList = new ArrayList<>();
        mSizeList = new ArrayList<>();
        mColorList = new ArrayList<>();
        mDelivery = new ArrayList<>();
        mNewArrivals = new ArrayList<>();
        mFacetFilters = new ArrayList<>();
        mBrandCountList = new ArrayList<>();
        mSizeCountList = new ArrayList<>();
        mColorCountList = new ArrayList<>();
        mDeliveryCountList = new ArrayList<>();
        mNewArrivalsCountList = new ArrayList<>();
        Pair<String, String> pricePair = new Pair<>(BundleKeys.PRICE_FACETFILTER_NAME, PRICE_FACET_FILTER_TYPE);
        for (int i = 0; i < facets.size(); i++) {
            switch (facets.get(i).getFacetName()) {
                case BundleKeys.BRANDS_FACETFILTER_NAME:
                    for (int j = 0; j < facets.get(i).getFacetValues().size(); j++) {
                        SaleItemFacet.Value facetValue = facets.get(i).getFacetValues().get(j);
                        mBrandList.add(facetValue.getValue());
                        mBrandCountList.add(String.valueOf(facetValue.count));
                    }
                    mFacetFilters.add(new Pair<String, String>(BundleKeys.BRANDS_FACETFILTER_NAME, BRANDS_FACET_FILTER_TYPE));
                    break;
                case BundleKeys.SIZES_FACETFILTER_NAME:
                    for (int j = 0; j < facets.get(i).getFacetValues().size(); j++) {
                        SaleItemFacet.Value facetValue = facets.get(i).getFacetValues().get(j);
                        mSizeList.add(facetValue.getValue());
                        mSizeCountList.add(String.valueOf(facetValue.count));
                    }
                    mFacetFilters.add(new Pair<String, String>(BundleKeys.SIZES_FACETFILTER_NAME, SIZE_FACET_FILTER_TYPE));
                    break;
                case BundleKeys.PRICE_FACETFILTER_NAME:
                    mFacetFilters.add(pricePair);
                    break;
                case BundleKeys.COLORS_FACETFILTER_NAME:
                    for (int j = 0; j < facets.get(i).getFacetValues().size(); j++) {
                        SaleItemFacet.Value facetValue = facets.get(i).getFacetValues().get(j);
                        mColorList.add(facetValue.getValue());
                        mColorCountList.add(String.valueOf(facetValue.count));
                    }
                    mFacetFilters.add(new Pair<String, String>(BundleKeys.COLORS_FACETFILTER_NAME, COLOR_FACET_FILTER_TYPE));
                    break;
                case BundleKeys.DELIVERY_FACETFILTER_NAME:
                    for (int j = 0; j < facets.get(i).getFacetValues().size(); j++) {
                        SaleItemFacet.Value facetValue = facets.get(i).getFacetValues().get(j);
                        mDelivery.add(facetValue.getValue());
                        mDeliveryCountList.add(String.valueOf(facetValue.count));
                    }
                    mFacetFilters.add(new Pair<String, String>(BundleKeys.DELIVERY_FACETFILTER_NAME, DELIVERY_FACET_FILTER_TYPE));
                    break;
                case BundleKeys.NEW_ARRIVAL_FACETFILTER_NAME:
                    for (int j = 0; j < facets.get(i).getFacetValues().size(); j++) {
                        SaleItemFacet.Value facetValue = facets.get(i).getFacetValues().get(j);
                        mNewArrivals.add(facetValue.getValue());
                        mNewArrivalsCountList.add(String.valueOf(facetValue.count));
                    }
                    mFacetFilters.add(new Pair<String, String>(BundleKeys.NEW_ARRIVAL_FACETFILTER_NAME, NEW_ARRIVAL_FACET_FILTER_TYPE));
                    break;
                default:
                    break;
            }
        }

        return mFacetFilters;
    }

    @Override
    public void updateFacets(List<SaleItemFacet> facets) {
        mBrandList = new ArrayList<>();
        mSizeList = new ArrayList<>();
        mColorList = new ArrayList<>();
        mDelivery = new ArrayList<>();
        mNewArrivals = new ArrayList<>();
        mFacetFilters = new ArrayList<>();
        mBrandCountList = new ArrayList<>();
        mSizeCountList = new ArrayList<>();
        mColorCountList = new ArrayList<>();
        mDeliveryCountList = new ArrayList<>();
        mNewArrivalsCountList = new ArrayList<>();
        Pair<String, String> pricePair = new Pair<>(BundleKeys.PRICE_FACETFILTER_NAME, PRICE_FACET_FILTER_TYPE);
        for (int i = 0; i < facets.size(); i++) {
            switch (facets.get(i).getFacetName()) {
                case BundleKeys.BRANDS_FACETFILTER_NAME:
                    for (int j = 0; j < facets.get(i).getFacetValues().size(); j++) {
                        SaleItemFacet.Value facetValue = facets.get(i).getFacetValues().get(j);
                        mBrandList.add(facetValue.getValue());
                        mBrandCountList.add(String.valueOf(facetValue.count));
                    }
                    mFacetFilters.add(new Pair<String, String>(BundleKeys.BRANDS_FACETFILTER_NAME, BRANDS_FACET_FILTER_TYPE));
                    break;
                case BundleKeys.SIZES_FACETFILTER_NAME:
                    for (int j = 0; j < facets.get(i).getFacetValues().size(); j++) {
                        SaleItemFacet.Value facetValue = facets.get(i).getFacetValues().get(j);
                        mSizeList.add(facetValue.getValue());
                        mSizeCountList.add(String.valueOf(facetValue.count));
                    }
                    mFacetFilters.add(new Pair<String, String>(BundleKeys.SIZES_FACETFILTER_NAME, SIZE_FACET_FILTER_TYPE));
                    break;
                case BundleKeys.PRICE_FACETFILTER_NAME:
                    mFacetFilters.add(pricePair);
                    break;
                case BundleKeys.COLORS_FACETFILTER_NAME:
                    for (int j = 0; j < facets.get(i).getFacetValues().size(); j++) {
                        SaleItemFacet.Value facetValue = facets.get(i).getFacetValues().get(j);
                        mColorList.add(facetValue.getValue());
                        mColorCountList.add(String.valueOf(facetValue.count));
                    }
                    mFacetFilters.add(new Pair<String, String>(BundleKeys.COLORS_FACETFILTER_NAME, COLOR_FACET_FILTER_TYPE));
                    break;
                case BundleKeys.DELIVERY_FACETFILTER_NAME:
                    for (int j = 0; j < facets.get(i).getFacetValues().size(); j++) {
                        SaleItemFacet.Value facetValue = facets.get(i).getFacetValues().get(j);
                        mDelivery.add(facetValue.getValue());
                        mDeliveryCountList.add(String.valueOf(facetValue.count));
                    }
                    mFacetFilters.add(new Pair<String, String>(BundleKeys.DELIVERY_FACETFILTER_NAME, DELIVERY_FACET_FILTER_TYPE));
                    break;
                case BundleKeys.NEW_ARRIVAL_FACETFILTER_NAME:
                    for (int j = 0; j < facets.get(i).getFacetValues().size(); j++) {
                        SaleItemFacet.Value facetValue = facets.get(i).getFacetValues().get(j);
                        mNewArrivals.add(facetValue.getValue());
                        mNewArrivalsCountList.add(String.valueOf(facetValue.count));
                    }
                    mFacetFilters.add(new Pair<String, String>(BundleKeys.NEW_ARRIVAL_FACETFILTER_NAME, NEW_ARRIVAL_FACET_FILTER_TYPE));
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void closeFacets() {

    }

    @Override
    public void updateSelectedFacet(int position) {

    }

    @Override
    public boolean getIsFacetsVisible() {
        return false;
    }

    @Override
    public void onReceiveCategoryMap(Map<String, GetCategoryTreeResponse> categoryMap) {

    }

    @Override
    public void updateSortingFacet(List<SortingResponse> sortingList) {
        mSortingFacets = sortingList;
        if (mSortingFacets != null) {
            mSortingList.clear();
            mSortingList = new ArrayList<>(mSortingFacets);
        }
    }

    @Override
    public void showSaleItems(GetSaleItemsResponse getSaleItemsResponse, int pageNumber, boolean forFacetCorrection, boolean isFromCache) {
        if (mSourceType.equals("CategorySearch") && mRemoveCategoryKeyFromCategory) {
            mRemoveCategoryKeyFromCategory = false;
            mShowCategory = true;
            mCategoryKey = "";
            mCategoryTree = getSaleItemsResponse.getCategories();
            setRecyclerAdapter("", new ArrayList<>());
        }
        mSaleItemResults.setText("See all " + getSaleItemsResponse.total + " products");
    }

    private void setupPriceFacet() {
        mPriceText.setText("Price (" + Settings.getSelectedCountry().currencySign + ")");
        mOrigMaxValue = mGeniemaxPrice; //mPresenter.getSearchMaxPrice();
        mOrigMinValue = mSeekbar != null ? mSeekbar.getSelectedMinValue().intValue() : 0;

        if (mOrigMaxValue == mOrigMinValue) {
            mOrigMaxValue = DEFAULT_PRICE_THRESHOLD;
        }

        if (mSeekbar != null) {
            mSeekbar.setMaxValue(mOrigMaxValue);
            mSeekbar.setMinPriceMovingLayout(mMinPriceMovingLayout);
            mSeekbar.setMaxPriceMovingLayout(mMaxPriceMovingLayout);
            mSeekbar.setOnRangeSeekbarChangeListener((minValue, maxValue) -> {
                minPrice = minValue.intValue();
                maxPrice = maxValue.intValue();
                mMinPrice.setText(Settings.getSelectedCountry().currencySign + minValue.intValue());
                mMaxPrice.setText(Settings.getSelectedCountry().currencySign + maxValue.intValue());
                if (maxValue.intValue() == mOrigMaxValue) {
                    mMaxPrice.setText(Settings.getSelectedCountry().currencySign + maxValue.intValue() + "+");
                }
            });

            mSeekbar.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
                @Override
                public void finalValue(Number minValue, Number maxValue) {

                    //remove previously selected price range
                    for (SearchChipModel chip : mSelectedFilters) {
                        if (chip.getFilterType().equals(BundleKeys.PRICE_FACETFILTER_NAME)) {
                            mSelectedFilters.remove(chip);
                            removeChipGroupFilterChip(chip.getChipTitle());
                            break;
                        }
                    }

                    //add newly selected price range
                    if (mOrigMinValue != minValue.intValue() || mOrigMaxValue != maxValue.intValue()) {
                        SearchChipModel priceChip = new SearchChipModel(BundleKeys.PRICE_FACETFILTER_NAME, Settings.getSelectedCountry().currencySign + minValue.intValue() + " to " + Settings.getSelectedCountry().currencySign + maxValue.intValue(), null);
                        priceChip.setMaxValue(maxValue.intValue());
                        priceChip.setMinValue(minValue.intValue());
                        mSelectedFilters.add(priceChip);
                        addChipGroupFilterChip(priceChip.getChipTitle());
                    }

                    getUpdatedTotalProductCount();
                    showClearButton();
                }
            });

            SearchChipModel priceChip = findPriceChip();

            if (priceChip != null) {
                mSeekbar.setMinStartValue(priceChip.getMinValue()).apply();
                mSeekbar.setMaxStartValue(priceChip.getMaxValue()).apply();
            }
        }
    }

    private SearchChipModel findPriceChip() {
        for (SearchChipModel chip : mSelectedFilters) {
            if (chip.getFilterType().equals(BundleKeys.PRICE_FACETFILTER_NAME)) {
                return chip;
            }
        }
        return null;
    }

    @Override
    public boolean handleBack() {
        if (mfilterLevel == 1 || mShowSort) {
            mShowSubCategories = false;
            mShowSort = false;
            if (!mIsSeeAllClicked) {
                displayPreSelectedFilters();
            }
            mActivity.getMainController().showBottomNav();
            return super.handleBack();
        } else {
            mShowSubCategories = false;
            mShowSort = false;
            mToolbarTitle.setText("Filter");
            mfilterLevel = 1;
            mFiltersToDisplay.clear();
            mFiltersToDisplay.addAll(mCategories);
            setRecyclerAdapter("", new ArrayList<>());
            mChipGroup.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mSeekbarLayout.setVisibility(View.GONE);
            mPriceText.setVisibility(View.GONE);
            if (!mSelectedFilters.isEmpty() || !mCategoryKeys.isEmpty()) {
                mFilterByText.setVisibility(View.VISIBLE);
            } else {
                mFilterByText.setVisibility(View.GONE);
            }
            showClearButton();
            return true;
        }
    }

    private void displayPreSelectedFilters() {
        if (mSourceType.equals("CategorySearch") &&
                mCategoryKeys.isEmpty() &&
                (mCategoryKey != null && !mCategoryKey.isEmpty())) {
            mCategoryKeys.add(mCategoryKey);
        }
        SearchChipModel chipChanged = new SearchChipModel("", "", "");
        mPresenter.requestUpdate(
                mPreSelectedCategoryKeys,
                mPreselectedFilter,
                chipChanged.getFilterType(),
                chipChanged.getFilterType().equals(BundleKeys.SORT_FACETFILTER_NAME) ? chipChanged.getKey() : chipChanged.getChipTitle(),
                null,
                mBrandList.size(),
                minPrice,
                maxPrice,
                mSizeList,
                SearchOperationType.UNCHECKBOX);
    }

    private void requestLoadItems() {
        if (mSourceType.equals("CategorySearch") && mCategoryKeys.size() > 1 || mRemoveCategoryKeyFromCategory) {
            mCategoryKeys.remove(mCategoryKey);
        }
        if (mSourceType.equals("CategorySearch") && mCategoryKeys.isEmpty() && !mCategoryKey.equals("")) {
            mCategoryKeys.add(mCategoryKey);
        }
        SearchChipModel chipChanged = new SearchChipModel("", "", "");
        Set<SearchChipModel> selectedFilters = new HashSet<>();
        selectedFilters.addAll(mSelectedFilters);
        mPresenter.requestUpdate(
                mCategoryKeys,
                selectedFilters,
                chipChanged.getFilterType(),
                chipChanged.getFilterType().equals(BundleKeys.SORT_FACETFILTER_NAME) ? chipChanged.getKey() : chipChanged.getChipTitle(),
                null,
                mBrandList.size(),
                minPrice,
                maxPrice,
                mSizeList,
                SearchOperationType.UNCHECKBOX);

        getUpdatedTotalProductCount();
    }

    private void seeAllProducts() {
        requestLoadItems();
        mIsSeeAllClicked = true;
        mfilterLevel = 1;

        mActivity.onBackPressed();
    }

    void clearAllFilters() {
        mCategoryKeys.clear();
        mChipGroup.removeAllViews();
        mFilterByText.setVisibility(View.GONE);
        mSelectedFilters.clear();
        onResetPriceRange();
        getUpdatedTotalProductCount();
    }

    void clearFilters() {
        if (mfilterLevel == 1) {
            clearAllFilters();
        } else {
            if (mShowSubCategories) {
                clearAllFilters();
                setRecyclerAdapter("", mCurrentCountList);
            } else {
                List<SearchChipModel> mNewSelectedFilters = new ArrayList();
                for (SearchChipModel filters : mSelectedFilters) {
                    if (filters.getFilterType() == mFilterType) {
                        removeChipGroupFilterChip(filters.getChipTitle());
                    } else {
                        mNewSelectedFilters.add(filters);
                    }
                }
                if (mFilterType.equals(BundleKeys.PRICE_FACETFILTER_NAME)) {
                    onResetPriceRange();
                }
                mSelectedFilters.clear();
                mSelectedFilters.addAll(mNewSelectedFilters);
                setRecyclerAdapter(mFilterType, mCurrentCountList);
            }
        }
        getUpdatedTotalProductCount();
    }

    void showClearButton() {
        if (mfilterLevel == 1) {
            mToolbarRightButton.setText(mActivity.getResources().getString(R.string.clear_all_underline));
            mPadding.getLayoutParams().height = 42;
            if (mSelectedFilters.isEmpty() && mCategoryKeys.isEmpty()) {
                mToolbarRightButton.setVisibility(View.GONE);
            } else {
                mToolbarRightButton.setVisibility(View.VISIBLE);
            }
        } else {
            mToolbarRightButton.setText(mActivity.getResources().getString(R.string.clear_underline));
            mPadding.getLayoutParams().height = 10;
            if (mShowSort) {
                mToolbarRightButton.setVisibility(View.GONE);
            } else {
                mToolbarRightButton.setVisibility(View.VISIBLE);
            }
        }
    }

    private void getUpdatedTotalProductCount() {
        Set<SearchChipModel> mUpdatedSelectedFilterList = new HashSet<>();
        for (SearchChipModel chip : mSelectedFilters) {
            String chipTitle = chip.getChipTitle();
            SearchChipModel newChip = new SearchChipModel(chip.getFilterType(), chipTitle.replaceAll("[" + Settings.getSelectedCountry().currencySign + "]", ""));
            mUpdatedSelectedFilterList.add(newChip);
        }
        mPresenter.loadSaleItems(createSaleItemsRequest(mCategoryKeys, 0, mUpdatedSelectedFilterList));
    }

    private GetSaleItemsRequest createSaleItemsRequest(Set<String> categoryKeys, int pageNumber, Set<SearchChipModel> chipsList) {
        String mCategoryKey;
        mCategoryKey = StringUtils.generateConcatenatedCategories(reduceCategoryKeysForRequest(categoryKeys));
        Set<SearchChipModel> mChipFilters = chipsList != null ? chipsList : new HashSet<>();

        return createSaleItemsRequest(mCategoryKey, pageNumber, mChipFilters);
    }

    private Set<String> reduceCategoryKeysForRequest(Set<String> categoryKeys) {
        if (mCategoryTree.isEmpty()) {
            return categoryKeys;
        }

        LinkedHashSet<String> keys = new LinkedHashSet<>();
        for (GetCategoryTreeResponse node : mCategoryTree) {
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

    public GetSaleItemsRequest createSaleItemsRequest(String categoryKey, int pageNumber, Set<SearchChipModel> chipsList) {
        Map<String, List<String>> facetFilters = new HashMap<>();
        String locationFilterHash = null;
        List<String> mSelectedTitle = new ArrayList<>();
        final String CATEGORY_FILTER_TYPE = "Category";

        GetSaleItemsRequest getSaleItemsRequest = new GetSaleItemsRequest();

        getSaleItemsRequest = StringUtils.updateSaleItemRequest(categoryKey, getSaleItemsRequest);

        getSaleItemsRequest.setSorting("");
        getSaleItemsRequest.setPageNumber(pageNumber);
        getSaleItemsRequest.setQuery("");
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
            if (!chipsList.isEmpty()) {
                ArrayList<String> brandNameFacetFilters = new ArrayList<>();
                ArrayList<String> colorFacetFilters = new ArrayList<>();
                ArrayList<String> sizesFacetFilters = new ArrayList<>();
                ArrayList<String> priceFacetFilters = new ArrayList<>();
                ArrayList<String> deliveryFacetFilters = new ArrayList<>();
                ArrayList<String> newArrivalFacetFilters = new ArrayList<>();
                ArrayList<String> sortFacetFilters = new ArrayList<>();
                ArrayList<String> storeIdFilters = new ArrayList<>();

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
                    } else if (facetName.equals(BundleKeys.STORE_ID_FACETFILTER_NAME)) {
                        storeIdFilters.add(chip.getChipTitle());
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
                facetFilters.put(BundleKeys.STORE_ID_FACETFILTER_NAME, storeIdFilters);

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

        return getSaleItemsRequest;
    }

    private String mapSortingTitleToKey(String title) {
        for (SortingResponse response : mSortingFacets) {
            if (response.getTitle().equalsIgnoreCase(title)) {
                return response.getKey();
            }
        }

        return "";
    }
}
