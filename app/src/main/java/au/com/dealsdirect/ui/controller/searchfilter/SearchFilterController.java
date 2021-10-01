package au.com.dealsdirect.ui.controller.searchfilter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.data.network.model.saleitems.SaleItemFacet;
import au.com.dealsdirect.data.network.model.sorting.SortingResponse;
import au.com.dealsdirect.service.datacollection.enums.SearchOperationType;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.main.Settings;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.FacetItemsAdapter;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SubCategoriesAdapter;
import au.com.dealsdirect.ui.custom.CustomRangeSeekbar;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.JsonUtils;
import au.com.dealsdirect.utils.StringUtils;
import butterknife.BindView;

import static au.com.dealsdirect.utils.BundleKeys.BRANDS_FACET_FILTER_TYPE;
import static au.com.dealsdirect.utils.BundleKeys.COLOR_FACET_FILTER_TYPE;
import static au.com.dealsdirect.utils.BundleKeys.DELIVERY_FACET_FILTER_TYPE;
import static au.com.dealsdirect.utils.BundleKeys.NEW_ARRIVAL_FACET_FILTER_TYPE;
import static au.com.dealsdirect.utils.BundleKeys.PRICE_FACET_FILTER_TYPE;
import static au.com.dealsdirect.utils.BundleKeys.SIZE_FACET_FILTER_TYPE;


/**
 * Created by smartwave on 20/07/2017.
 */

public class SearchFilterController extends BaseController implements SearchFilterMvpView {

    public abstract static class Parameters {
        private Parameters() {
        }

        public static final class FromItemsList extends Parameters {
            private List<SaleItemFacet> mFacets;
            private List<SortingResponse> mSortingFacets;
            private List<GetCategoryTreeResponse> mCategoryTree;
            private List<String> mBrandList;
            private String mCategoryKey;
            private Set<SearchChipModel> mChipsFilter;
            private Set<SearchChipModel> mPreselectedFilter;
            private boolean mIsFromCategory;

            public FromItemsList(List<SaleItemFacet> facets,
                                 List<SortingResponse> sortingFacets,
                                 List<GetCategoryTreeResponse> categoryTree,
                                 List<String> brandList,
                                 String categoryKey,
                                 Set<SearchChipModel> chipsFilter,
                                 boolean isFromCategory,
                                 Set<SearchChipModel> preselectedFilter) {
                mFacets = facets;
                mSortingFacets = sortingFacets;
                mCategoryTree = categoryTree;
                mBrandList = brandList;
                mCategoryKey = categoryKey;
                mChipsFilter = chipsFilter;
                mIsFromCategory = isFromCategory;
                mPreselectedFilter = preselectedFilter;
            }

            public ArrayList<SaleItemFacet> getFacets() {
                return mFacets == null ? new ArrayList<>() : new ArrayList<>(mFacets);
            }

            public ArrayList<SortingResponse> getSortingFacets() {
                return mSortingFacets == null ? new ArrayList<>() : new ArrayList<>(mSortingFacets);
            }

            public ArrayList<GetCategoryTreeResponse> getCategoryTree() {
                return mCategoryTree == null ? new ArrayList<>() : new ArrayList<>(mCategoryTree);
            }

            public ArrayList<String> getBrandList() {
                return mBrandList == null ? new ArrayList<>() : new ArrayList<>(mBrandList);
            }

            public String getCategoryKey() {
                return mCategoryKey;
            }

            public Set<SearchChipModel> getChipsFilter() {
                return mChipsFilter == null ? new HashSet<>() : new HashSet<>(mChipsFilter);
            }

            public boolean isFromCategory() {
                return mIsFromCategory;
            }

            public Set<SearchChipModel> getPreselectedFilter() {
                return mPreselectedFilter;
            }
        }
    }

    public static final String TAG = SearchFilterController.class.getSimpleName();
    private static final int DEFAULT_PRICE_THRESHOLD = 200;
    private static final String KEY_HAS_SAVED_INSTANCE = "SearchFilterController.KEY_HAS_SAVED_INSTANCE";
    private static final String KEY_FACET_STRING = "KEY_FACET_STRING";
    private static final String KEY_SORTING_STRING = "KEY_SORTING_STRING";
    private static final String KEY_CATEGORY_STRING = "KEY_CATEGORY_STRING";
    private static final String KEY_BRAND_LIST = "KEY_BRAND_LIST";
    private static final String SALEITEMS_CATEGORY_MAP = "SALEITEMS_CATEGORY_MAP";
    private static final String SALEITEMS_CHIPS_FILTER = "SALEITEMS_CHIPS_FILTER";
    private static final String KEY_FROM_CATEGORIES = "KEY_FROM_CATEGORIES";

    private static final String SHOP_KEY_HAS_SAVED_INSTANCE = "SearchFilterController.SHOP_KEY_HAS_SAVED_INSTANCE";
    private static final String SHOP_KEY_FACET_STRING = "SHOP_KEY_FACET_STRING";
    private static final String SHOP_KEY_SORTING_STRING = "SHOP_KEY_SORTING_STRING";
    private static final String SHOP_KEY_CATEGORY_STRING = "SHOP_KEY_CATEGORY_STRING";
    private static final String SHOP_KEY_BRAND_LIST = "SHOP_KEY_BRAND_LIST";
    private static final String SHOP_SALEITEMS_CATEGORY_MAP = "SHOP_SALEITEMS_CATEGORY_MAP";
    private static final String SHOP_SALEITEMS_CHIPS_FILTER = "SHOP_SALEITEMS_CHIPS_FILTER";
    private static final String SHOP_KEY_FROM_CATEGORIES = "SHOP_KEY_FROM_CATEGORIES";

    @Inject
    SearchFilterMvpPresenter<SearchFilterMvpView> mPresenter;

    @Inject
    protected MainActivity mActivity;

    @BindView(R.id.controller_search_filter_facets_frame)
    FrameLayout mFacetsFrame;

    @BindView(R.id.controller_search_filter_facets_page)
    FrameLayout mFacetsPage;

    @BindView(R.id.controller_sale_items_opaque_view)
    View mOpaqueView;

    @BindView(R.id.filters_facet_items_recyclerview)
    RecyclerView mFacetItemsRecyclerView;

    @BindView(R.id.filters_categories_recyclerview)
    RecyclerView mFilterCategoriesRecyclerView;

    //Seekbar bindings
    @BindView(R.id.price_facet_range_seekbar)
    CustomRangeSeekbar mSeekbar;

    @BindView(R.id.seekbar_main_layout)
    RelativeLayout mSeekbarLayout;

    @BindView(R.id.clearText)
    TextView mClearText;

    @BindView(R.id.movingMaxPriceLayout)
    LinearLayout mMaxPriceMovingLayout;

    @BindView(R.id.movingMinPriceLayout)
    LinearLayout mMinPriceMovingLayout;

    @BindView(R.id.movingMaxPrice)
    TextView mMaxPrice;

    @BindView(R.id.movingMinPrice)
    TextView mMinPrice;

    List<SaleItemFacet> mFacets;
    List<GetCategoryTreeResponse> mCategoryTree;
    List<SortingResponse> mSortingFacets = new ArrayList<>();
    private Map<String, GetCategoryTreeResponse> mCategoryMap = new HashMap<>();

    SubCategoriesAdapter mSubCategoriesAdapter;
    FacetItemsAdapter mFacetItemsAdapter;
    Set<SearchChipModel> mSearchItemsList = new HashSet<>();

    ArrayList<String> mBrandList = new ArrayList<>();
    ArrayList<String> mSizeList = new ArrayList<>();
    ArrayList<String> mColorList = new ArrayList<>();
    ArrayList<SortingResponse> mSortingList = new ArrayList<>();
    ArrayList<String> mDelivery = new ArrayList<>();
    ArrayList<String> mNewArrivals = new ArrayList<>();
    private List<Pair<String, String>> mFacetFilters = new ArrayList();

    private int mOrigMinValue = -1;
    private int mOrigMaxValue = -1;
    private boolean mHasSeekbarReset = false;
    private boolean mIsSearchFilterControllerActive = false;
    private String mCategoryKey;
    private boolean mHasDefaultCategoryKey = false;
    private boolean mHasSavedInstance = false;
    private int minPrice = 0;
    private int maxPrice = 200;
    private boolean isFromCategory = false;

    Set<String> mCategoryKeys = new LinkedHashSet<>();

    private Set<SearchChipModel> mPreviousSearchChips = new HashSet<>();
    private ControllerChangeHandler.ControllerChangeListener newControllerChangeHandler;
    private Set<SearchChipModel> mPreselectedFilter = new HashSet<>();

    public static SearchFilterController newInstance() {
        return new SearchFilterController(new BundleBuilder(new Bundle()).build());
    }

    public static SearchFilterController newInstance(Parameters parameters) {
        SearchFilterController controller = SearchFilterController.newInstance();

        if (parameters instanceof Parameters.FromItemsList) {
            controller.mFacets = ((Parameters.FromItemsList) parameters).getFacets();
            controller.mSortingFacets = ((Parameters.FromItemsList) parameters).getSortingFacets();
            controller.mCategoryTree = ((Parameters.FromItemsList) parameters).getCategoryTree();
            controller.mBrandList = ((Parameters.FromItemsList) parameters).getBrandList();
            controller.mCategoryKey = ((Parameters.FromItemsList) parameters).getCategoryKey();
            if (controller.mCategoryKey != null && !controller.mCategoryKey.isEmpty()) {
                controller.mCategoryKeys.add(controller.mCategoryKey);
            }
            controller.mPreviousSearchChips = ((Parameters.FromItemsList) parameters).getChipsFilter();
            controller.isFromCategory = ((Parameters.FromItemsList) parameters).isFromCategory();
            controller.mPreselectedFilter = ((Parameters.FromItemsList) parameters).getPreselectedFilter();
        }

        return controller;
    }

    public SearchFilterController(Bundle args) {
        super(args);
        mFacets = JsonUtils.convertStringToObject(args.getString(BundleKeys.KEY_FACET_STRING, ""), new TypeToken<ArrayList<SaleItemFacet>>() {
        }.getType());
        mSortingFacets = JsonUtils.convertStringToObject(args.getString(BundleKeys.KEY_SORTING_STRING, ""), new TypeToken<ArrayList<SortingResponse>>() {
        }.getType());
        mCategoryTree = JsonUtils.convertStringToObject(args.getString(BundleKeys.KEY_CATEGORY_STRING, ""), new TypeToken<ArrayList<GetCategoryTreeResponse>>() {
        }.getType());
        mBrandList = JsonUtils.convertStringToObject(args.getString(BundleKeys.KEY_BRAND_LIST, ""), new TypeToken<ArrayList<String>>() {
        }.getType());
        mCategoryKey = args.getString(BundleKeys.SALEITEMS_CATEGORY_MAP, "");
        if (mCategoryKey != null && !mCategoryKey.isEmpty()) {
            mCategoryKeys.add(mCategoryKey);
        }
        mHasDefaultCategoryKey = args.getBoolean(BundleKeys.KEY_HAS_DEFAULT_CATEGORY, false);

        String previousChipsString = args.getString(BundleKeys.SALEITEMS_CHIPS_FILTER, "");
        mPreviousSearchChips = previousChipsString.isEmpty() ? new HashSet<>() :
                JsonUtils.convertStringToObject(previousChipsString, new TypeToken<HashSet<SearchChipModel>>() {
                }.getType());

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (isFromCategory) {
            outState.putBoolean(KEY_HAS_SAVED_INSTANCE, true);
            outState.putString(KEY_FACET_STRING, new Gson().toJson(mFacets));
            outState.putString(KEY_SORTING_STRING, new Gson().toJson(mSortingFacets));
            outState.putString(KEY_CATEGORY_STRING, new Gson().toJson(mCategoryTree));
            outState.putString(KEY_BRAND_LIST, new Gson().toJson(mBrandList));
            outState.putString(SALEITEMS_CATEGORY_MAP, mCategoryKey);
            outState.putString(SALEITEMS_CHIPS_FILTER, new Gson().toJson(mPreviousSearchChips));
            outState.putBoolean(KEY_FROM_CATEGORIES, isFromCategory);
        } else {
            outState.putBoolean(SHOP_KEY_HAS_SAVED_INSTANCE, true);
            outState.putString(SHOP_KEY_FACET_STRING, new Gson().toJson(mFacets));
            outState.putString(SHOP_KEY_SORTING_STRING, new Gson().toJson(mSortingFacets));
            outState.putString(SHOP_KEY_CATEGORY_STRING, new Gson().toJson(mCategoryTree));
            outState.putString(SHOP_KEY_BRAND_LIST, new Gson().toJson(mBrandList));
            outState.putString(SHOP_SALEITEMS_CATEGORY_MAP, mCategoryKey);
            outState.putString(SHOP_SALEITEMS_CHIPS_FILTER, new Gson().toJson(mPreviousSearchChips));
            outState.putBoolean(SHOP_KEY_FROM_CATEGORIES, isFromCategory);

        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.containsKey(KEY_FROM_CATEGORIES) &&
                savedInstanceState.getBoolean(KEY_FROM_CATEGORIES)) {
            mHasSavedInstance = savedInstanceState.getBoolean(KEY_HAS_SAVED_INSTANCE);
            mFacets = JsonUtils.convertStringToObject(savedInstanceState.getString(KEY_FACET_STRING, ""), new TypeToken<ArrayList<SaleItemFacet>>() {
            }.getType());
            mSortingFacets = JsonUtils.convertStringToObject(savedInstanceState.getString(KEY_SORTING_STRING, ""), new TypeToken<ArrayList<SortingResponse>>() {
            }.getType());
            mCategoryTree = JsonUtils.convertStringToObject(savedInstanceState.getString(KEY_CATEGORY_STRING, ""), new TypeToken<ArrayList<GetCategoryTreeResponse>>() {
            }.getType());
            mBrandList = JsonUtils.convertStringToObject(savedInstanceState.getString(KEY_BRAND_LIST, ""), new TypeToken<ArrayList<String>>() {
            }.getType());
            mCategoryKey = savedInstanceState.getString(SALEITEMS_CATEGORY_MAP, "");
            if (mCategoryKey != null && !mCategoryKey.isEmpty()) {
                mCategoryKeys.add(mCategoryKey);
            }

            String previousChipsString = savedInstanceState.getString(SALEITEMS_CHIPS_FILTER, "");
            mPreviousSearchChips = previousChipsString.isEmpty() ? new HashSet<>() :
                    JsonUtils.convertStringToObject(previousChipsString, new TypeToken<HashSet<SearchChipModel>>() {
                    }.getType());

            isFromCategory = savedInstanceState.getBoolean(KEY_FROM_CATEGORIES);
        } else {
            mHasSavedInstance = savedInstanceState.getBoolean(SHOP_KEY_HAS_SAVED_INSTANCE);
            mFacets = JsonUtils.convertStringToObject(savedInstanceState.getString(SHOP_KEY_FACET_STRING, ""), new TypeToken<ArrayList<SaleItemFacet>>() {
            }.getType());
            mSortingFacets = JsonUtils.convertStringToObject(savedInstanceState.getString(SHOP_KEY_SORTING_STRING, ""), new TypeToken<ArrayList<SortingResponse>>() {
            }.getType());
            mCategoryTree = JsonUtils.convertStringToObject(savedInstanceState.getString(SHOP_KEY_CATEGORY_STRING, ""), new TypeToken<ArrayList<GetCategoryTreeResponse>>() {
            }.getType());
            mBrandList = JsonUtils.convertStringToObject(savedInstanceState.getString(SHOP_KEY_BRAND_LIST, ""), new TypeToken<ArrayList<String>>() {
            }.getType());
            mCategoryKey = savedInstanceState.getString(SHOP_SALEITEMS_CATEGORY_MAP, "");
            if (mCategoryKey != null && !mCategoryKey.isEmpty()) {
                mCategoryKeys.add(mCategoryKey);
            }

            String previousChipsString = savedInstanceState.getString(SHOP_SALEITEMS_CHIPS_FILTER, "");
            mPreviousSearchChips = previousChipsString.isEmpty() ? new HashSet<>() :
                    JsonUtils.convertStringToObject(previousChipsString, new TypeToken<HashSet<SearchChipModel>>() {
                    }.getType());

            isFromCategory = savedInstanceState.getBoolean(SHOP_KEY_FROM_CATEGORIES);
        }
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_search_filter, container, false);
        getControllerComponent().inject(this);

        mPresenter.onAttach(this);
        return view;
    }

    @Override
    protected void onAttach(@NonNull View view) {
        mPresenter.onAttach(this);
        super.onAttach(view);
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {

        //create a category map from the categorytreeresponse in saleitems, else
        // use saleitemscontroller's category map if it is not empty else

        if (isFromCategory) {
            mActivity.setSearchFilterController(this);
        } else {
            mActivity.setShopSearchFilterController(this);
        }

        mPresenter.requestCategoryMap();
        if (mCategoryMap.isEmpty()) {
            createCategoryMap(mCategoryTree);
        }

        if (mFacets != null) {
            parseFacets(mFacets);
        }

        if (mSortingFacets != null) {
            mSortingList = new ArrayList<>(mSortingFacets);
        }

        setupPriceFacet();

        //      SETUP CATEGORIES
        mSubCategoriesAdapter = new SubCategoriesAdapter(mActivity, "", mCategoryTree, (int) getDimension(R.dimen.margin_small));
        mSubCategoriesAdapter.setOnClickListener(new SubCategoriesAdapter.OnClickCategoryListener() {
            @Override
            public void onClick(String categoryItemKey) {
                mPresenter.requestCategoryMap();
                GetCategoryTreeResponse categoryItem = mCategoryMap.get(categoryItemKey);
                if (categoryItem != null) {
                    boolean isSelected = !categoryItem.isSelected();
                    categoryItem.setSelected(isSelected);
                    mPresenter.selectCategory(categoryItem);
                    mSubCategoriesAdapter.setSelectedCategories(mCategoryKeys);
                }
            }
        });


        mFilterCategoriesRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        mFilterCategoriesRecyclerView.setAdapter(mSubCategoriesAdapter);
        mFilterCategoriesRecyclerView.setHasFixedSize(true);

        //      SETUP FACET ITEMS (sub of facets)
        mFacetItemsAdapter = new FacetItemsAdapter(new ArrayList<>(), mPresenter, new HashSet<Integer>(), mFacetItemsRecyclerView,
                mPreselectedFilter);
        mFacetItemsRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        mFacetItemsRecyclerView.setAdapter(mFacetItemsAdapter);
        mFacetItemsRecyclerView.setHasFixedSize(true);
        mFacetItemsAdapter.setSearchItemsList(mSearchItemsList);

        mOpaqueView.setOnClickListener(v -> closeFacets());
        setRetainViewMode(RetainViewMode.RETAIN_DETACH);

        if (mPreviousSearchChips != null && !mPreviousSearchChips.isEmpty()) {
            mOrigMinValue = 0;
            SearchChipModel priceChip = findPriceChip();
            mOrigMaxValue = Math.max(DEFAULT_PRICE_THRESHOLD, priceChip == null ? -1 : priceChip.getMaxValue());
            replaceSearchChipModels(mPreviousSearchChips);
        }
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
        Pair<String, String> pricePair = new Pair<>(BundleKeys.PRICE_FACETFILTER_NAME, PRICE_FACET_FILTER_TYPE);
        for (int i = 0; i < facets.size(); i++) {
            switch (facets.get(i).getFacetName()) {
                case BundleKeys.BRANDS_FACETFILTER_NAME:
                    for (int j = 0; j < facets.get(i).getFacetValues().size(); j++) {
                        SaleItemFacet.Value facetValue = facets.get(i).getFacetValues().get(j);
                        mBrandList.add(facetValue.getValue());
                    }
                    mFacetFilters.add(new Pair<String, String>(BundleKeys.BRANDS_FACETFILTER_NAME, BRANDS_FACET_FILTER_TYPE));
                    break;
                case BundleKeys.SIZES_FACETFILTER_NAME:
                    for (int j = 0; j < facets.get(i).getFacetValues().size(); j++) {
                        SaleItemFacet.Value facetValue = facets.get(i).getFacetValues().get(j);
                        mSizeList.add(facetValue.getValue());
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
                    }
                    mFacetFilters.add(new Pair<String, String>(BundleKeys.COLORS_FACETFILTER_NAME, COLOR_FACET_FILTER_TYPE));
                    break;
                case BundleKeys.DELIVERY_FACETFILTER_NAME:
                    for (int j = 0; j < facets.get(i).getFacetValues().size(); j++) {
                        SaleItemFacet.Value facetValue = facets.get(i).getFacetValues().get(j);
                        mDelivery.add(facetValue.getValue());
                    }
                    mFacetFilters.add(new Pair<String, String>(BundleKeys.DELIVERY_FACETFILTER_NAME, DELIVERY_FACET_FILTER_TYPE));
                    break;
                case BundleKeys.NEW_ARRIVAL_FACETFILTER_NAME:
                    for (int j = 0; j < facets.get(i).getFacetValues().size(); j++) {
                        SaleItemFacet.Value facetValue = facets.get(i).getFacetValues().get(j);
                        mNewArrivals.add(facetValue.getValue());
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
        for (int i = 0; i < facets.size(); i++) {
            switch (facets.get(i).getFacetName()) {
                case BundleKeys.BRANDS_FACETFILTER_NAME:
                    for (int j = 0; j < facets.get(i).getFacetValues().size(); j++) {
                        SaleItemFacet.Value facetValue = facets.get(i).getFacetValues().get(j);
                        mBrandList.add(facetValue.getValue());
                    }
                    break;
                case BundleKeys.SIZES_FACETFILTER_NAME:
                    for (int j = 0; j < facets.get(i).getFacetValues().size(); j++) {
                        SaleItemFacet.Value facetValue = facets.get(i).getFacetValues().get(j);
                        mSizeList.add(facetValue.getValue());
                    }
                    break;
                case BundleKeys.COLORS_FACETFILTER_NAME:
                    for (int j = 0; j < facets.get(i).getFacetValues().size(); j++) {
                        SaleItemFacet.Value facetValue = facets.get(i).getFacetValues().get(j);
                        mColorList.add(facetValue.getValue());
                    }
                    break;
                case BundleKeys.DELIVERY_FACETFILTER_NAME:
                    for (int j = 0; j < facets.get(i).getFacetValues().size(); j++) {
                        SaleItemFacet.Value facetValue = facets.get(i).getFacetValues().get(j);
                        mDelivery.add(facetValue.getValue());
                    }
                    break;
                case BundleKeys.NEW_ARRIVAL_FACETFILTER_NAME:
                    for (int j = 0; j < facets.get(i).getFacetValues().size(); j++) {
                        SaleItemFacet.Value facetValue = facets.get(i).getFacetValues().get(j);
                        mNewArrivals.add(facetValue.getValue());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void closeFacets() {
        if (!isViewAttached() || !isViewBound()) return;

        mIsSearchFilterControllerActive = false;
        if (mFacetsFrame != null) {
            mFacetsFrame.setVisibility(View.INVISIBLE);
        }
        mPresenter.facetsClosed();
    }

    @Override
    public void updateSelectedFacet(int position) {
        mFacetItemsAdapter.setFilterType(getFacetFilterType(position));
        mFacetItemsAdapter.replaceData(mapFacetItemClicked(position));
    }

    private void setupPriceFacet() {
        mOrigMaxValue = mPresenter.getSearchMaxPrice();
        mOrigMinValue = mSeekbar != null ? mSeekbar.getSelectedMinValue().intValue() : 0;

        if (mOrigMaxValue == mOrigMinValue) {
            mOrigMaxValue = DEFAULT_PRICE_THRESHOLD;
        }

        if (mClearText != null) {
            mClearText.setOnClickListener((v) -> {
                if (!mHasSeekbarReset) {
                    //remove previously selected price range
                    for (SearchChipModel chip : mSearchItemsList) {
                        if (chip.getFilterType().equals(BundleKeys.PRICE_FACETFILTER_NAME)) {
                            mSearchItemsList.remove(chip);
                            break;
                        }

                    }
                    mPresenter.requestUpdate(
                            mCategoryKeys,
                            mSearchItemsList,
                            BundleKeys.PRICE_FACETFILTER_NAME,
                            0 + " to " + DEFAULT_PRICE_THRESHOLD,
                            null,
                            mBrandList.size(),
                            minPrice,
                            maxPrice,
                            mSizeList,
                            SearchOperationType.ADJUSTSLIDINGBAR);

                    onResetPriceRange();
                }
            });
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
                    for (SearchChipModel chip : mSearchItemsList) {
                        if (chip.getFilterType().equals(BundleKeys.PRICE_FACETFILTER_NAME)) {
                            mSearchItemsList.remove(chip);
                            break;
                        }
                    }

                    //add newly selected price range
                    if (mOrigMinValue != minValue.intValue() || mOrigMaxValue != maxValue.intValue()) {
                        SearchChipModel priceChip = new SearchChipModel(BundleKeys.PRICE_FACETFILTER_NAME, minValue.intValue() + " to " + maxValue.intValue(), null);
                        priceChip.setMaxValue(maxValue.intValue());
                        priceChip.setMinValue(minValue.intValue());
                        mSearchItemsList.add(priceChip);
                    }

                    mHasSeekbarReset = false;

                    mPresenter.requestUpdate(
                            mCategoryKeys,
                            mSearchItemsList,
                            BundleKeys.PRICE_FACETFILTER_NAME,
                            minValue.intValue() + " to " + maxValue.intValue(),
                            null,
                            mBrandList.size(),
                            minPrice,
                            maxPrice,
                            mSizeList,
                            SearchOperationType.ADJUSTSLIDINGBAR);

                }
            });

            SearchChipModel priceChip = findPriceChip();

            if (priceChip != null) {
                mSeekbar.setMinStartValue(priceChip.getMinValue()).apply();
                mSeekbar.setMaxStartValue(priceChip.getMaxValue()).apply();
            }
        }
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    public void showFacetItem(int position) {
        if (!isViewAttached() || !isViewBound()) return;

        hideKeyboard();
        if (mFacetsFrame != null) {
            mFacetsFrame.setVisibility(View.VISIBLE);
        }

        if (!getFacetFilterType(position).equals(BundleKeys.PRICE_FACETFILTER_NAME)) { //only do this logic if facet clicked != price

            if (getFacetFilterType(position).equals(BundleKeys.CATEGORY_TREE_FACET)) {

                mFilterCategoriesRecyclerView.setVisibility(View.VISIBLE);
                mFacetItemsRecyclerView.setVisibility(View.GONE);
                mSeekbarLayout.setVisibility(View.GONE);

            } else {

                mFacetItemsRecyclerView.setVisibility(View.VISIBLE);
                mSeekbarLayout.setVisibility(View.GONE);
                mFilterCategoriesRecyclerView.setVisibility(View.INVISIBLE);
            }

        } else { //price is clicked
            mFilterCategoriesRecyclerView.setVisibility(View.INVISIBLE);
            mFacetItemsRecyclerView.setVisibility(View.GONE);
            mSeekbarLayout.setVisibility(View.VISIBLE);
        }
        updateSelectedFacet(position);
        mPresenter.facetsOpened();
    }

    @Override
    public void updateFacetItemToFilters(Set<SearchChipModel> selectedChips, SearchChipModel chipChanged, boolean isAdded) {
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
        mHasSeekbarReset = true;
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
        mSubCategoriesAdapter.replaceData(categoryTree);
        mSubCategoriesAdapter.setSelectedCategories(mCategoryKeys);
    }

    @Override
    public void replaceSearchChipModels(Set<SearchChipModel> chipModels) {
        mFacetItemsAdapter.setSearchItemsList(chipModels);
        mSearchItemsList = chipModels;
        boolean doesSliderExist = false;
        for (SearchChipModel chipModel : chipModels) {
            if (chipModel.getFilterType().equals(BundleKeys.PRICE_FACETFILTER_NAME)) {
                final float minValue = Math.min(0, chipModel.getMinValue());
                final float maxValue = Math.max(DEFAULT_PRICE_THRESHOLD, chipModel.getMaxValue());
                mSeekbar.setMaxValue(maxValue);
                minPrice = chipModel.getMinValue();
                maxPrice = chipModel.getMaxValue();
                mMinPrice.setText(Settings.getSelectedCountry().currencySign + chipModel.getMinValue());
                mMaxPrice.setText(Settings.getSelectedCountry().currencySign + chipModel.getMaxValue());
                mSeekbar.setMinValue(minValue)
                        .setMaxValue(maxValue)
                        .setMinStartValue(minPrice)
                        .setMaxStartValue(maxPrice)
                        .apply();
                doesSliderExist = true;
            }
        }

        if (!doesSliderExist) {
            onResetPriceRange();
        }
    }

    @Override
    public void onCategoryClicked(GetCategoryTreeResponse category) {

        checkParentSelection(category);

        List<GetCategoryTreeResponse> children = mCategoryMap.get(category.getKey()).getChildren();

        if (category.isSelected()) {
            mCategoryKeys.add(category.getKey()); //add to category keys
        } else {
            mCategoryKeys.remove(category.getKey()); //remove to category keys
        }

        if (!children.isEmpty()) { //if i have children{}
            setChildrenSelection(category.getKey(), false);
        }

        if (mHasDefaultCategoryKey && mCategoryKeys.size() == 0) {
            mCategoryKeys.add(mCategoryKey);
        }

        mPresenter.requestUpdate(
                mCategoryKeys,
                mSearchItemsList,
                null,
                null,
                category.getKey(),
                mBrandList.size(),
                minPrice,
                maxPrice,
                mSizeList,
                SearchOperationType.CATEGORYCLICK);
    }

    @Override
    public Set<String> getCategoryKeys() {
        return mCategoryKeys;
    }

    @Override
    public void clearCategoryKeys() {
        mCategoryKeys.clear();
    }

    private void checkParentSelection(GetCategoryTreeResponse category) {
        if (category == null) {
            return;
        }

        boolean childrenAreAllSelected = true;

        // get parent node and children
        String parentKey = StringUtils.getParentKey(category);

        if (parentKey.equals(category.getKey())) { // we reached end node up. terminate recursion
            return;
        }

        GetCategoryTreeResponse parentNode = mCategoryMap.get(parentKey);

        if (parentNode == null) { // parentKey might be invalid
            return;
        }

        List<GetCategoryTreeResponse> parentNodeChildren = parentNode.getChildren();

        for (GetCategoryTreeResponse child : parentNodeChildren) {
            if (!child.isSelected()) {
                childrenAreAllSelected = false;
            }
        }

        if (childrenAreAllSelected) {
            for (GetCategoryTreeResponse child : parentNodeChildren) {
                setChildrenSelection(child.getKey(), false);
            }
        } else {
            parentNode.setSelected(false);
            mCategoryKeys.remove(parentNode.getKey());
        }


        //recursion
        checkParentSelection(parentNode);
        if (childrenAreAllSelected) {
            for (GetCategoryTreeResponse child : parentNodeChildren) {
                setChildrenSelection(child.getKey(), false);
            }
        }
    }

    private void setChildrenSelection(String key, boolean isSelected) {

        if (mCategoryMap.get(key) != null && mCategoryMap.get(key).getChildren() != null) {

            List<GetCategoryTreeResponse> children = mCategoryMap.get(key).getChildren();

            for (GetCategoryTreeResponse category : children) {
                category.setSelected(isSelected);
                if (isSelected) {
                    mCategoryKeys.add(category.getKey());
                } else {
                    mCategoryKeys.remove(category.getKey());
                }
                setChildrenSelection(category.getKey(), isSelected);
            }
        }

    }

    private void createCategoryMap(List<GetCategoryTreeResponse> getCategoryTreeResponses) {
        if (getCategoryTreeResponses != null && !getCategoryTreeResponses.isEmpty()) {
            for (GetCategoryTreeResponse category : getCategoryTreeResponses) {
                mCategoryMap.put(category.getKey(), category);
                createCategoryMap(category.getChildren());
            }
        }
    }

    @Override
    public boolean handleBack() {
        if (mIsSearchFilterControllerActive) {
            closeFacets();
            return true;
        }

        return super.handleBack();
    }

    private String getFacetFilterType(int position) {
        return mFacetFilters.get(position).first;
    }

    private List<?> mapFacetItemClicked(int position) {
        String facetFilterType = getFacetFilterType(position);
        switch (facetFilterType) {
            case BundleKeys.SORT_FACETFILTER_NAME:
                return mSortingList;
            case BundleKeys.CATEGORY_TREE_FACET:
                return new ArrayList<>();
            case BundleKeys.BRANDS_FACETFILTER_NAME:
                return mBrandList;
            case BundleKeys.SIZES_FACETFILTER_NAME:
                return mSizeList;
            case BundleKeys.COLORS_FACETFILTER_NAME:
                return mColorList;
            case BundleKeys.DELIVERY_FACETFILTER_NAME:
                return mDelivery;
            case BundleKeys.NEW_ARRIVAL_FACETFILTER_NAME:
                return mNewArrivals;
            case BundleKeys.PRICE_FACETFILTER_NAME:
                return new ArrayList<>();
            default:
                return new ArrayList<>();
        }
    }

    private SearchChipModel findPriceChip() {
        for (SearchChipModel chip : mSearchItemsList) {
            if (chip.getFilterType().equals(BundleKeys.PRICE_FACETFILTER_NAME)) {
                return chip;
            }
        }
        return null;
    }

    private void createCategoryList(List<GetCategoryTreeResponse> list) {
        for (GetCategoryTreeResponse category : list) {
            mCategoryMap.put(category.getKey(), category);
            if (category.getChildren().size() > 0) {
                createCategoryList(category.getChildren());
            }
        }
    }

    @Override
    public void setSearchFilterControllerActive(boolean isTabActive) {
        mIsSearchFilterControllerActive = isTabActive;
    }

    @Override
    public void setFacetFilterItems(List<Pair<String, String>> mFacetFilters) {
        this.mFacetFilters = mFacetFilters;
    }

    @Override
    public boolean getIsFacetsVisible() {
        return mFacetsFrame != null && mFacetsFrame.getVisibility() == View.VISIBLE;
    }

    @Override
    public void onReceiveCategoryMap(Map<String, GetCategoryTreeResponse> categoryMap) {
        mCategoryMap = categoryMap;
    }

    @Override
    public void setRepository(SearchFilterMvpRepository repository) {
        if (repository != null) {
            mPresenter.setRepository(repository);
        }
    }

    @Override
    public void updateSortingFacet(List<SortingResponse> sortingList) {
        mSortingFacets = sortingList;
        if (mSortingFacets != null) {
            mSortingList.clear();
            mSortingList = new ArrayList<>(mSortingFacets);
        }
    }
}
