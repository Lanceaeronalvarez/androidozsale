package au.com.dealsdirect.ui.controller.searchfilter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
import au.com.dealsdirect.data.network.model.sorting.SortingResponse;
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
import static au.com.dealsdirect.utils.BundleKeys.PRICE_FACET_FILTER_TYPE;
import static au.com.dealsdirect.utils.BundleKeys.SIZE_FACET_FILTER_TYPE;


/**
 * Created by smartwave on 20/07/2017.
 */

public class SearchFilterController extends BaseController implements SearchFilterMvpView {

    public abstract static class Parameters {
        private Parameters() {}

        public static final class FromItemsList extends Parameters {
            private List<GetSaleItemsResponse.Facets> mFacets;
            private List<SortingResponse> mSortingFacets;
            private List<GetCategoryTreeResponse> mCategoryTree;
            private List<String> mBrandList;
            private String mCategoryKey;
            private List<SearchChipModel> mChipsFilter;

            public FromItemsList(List<GetSaleItemsResponse.Facets> facets,
                    List<SortingResponse> sortingFacets,
                    List<GetCategoryTreeResponse> categoryTree,
                    List<String> brandList,
                    String categoryKey,
                    List<SearchChipModel> chipsFilter) {
                mFacets = facets;
                mSortingFacets = sortingFacets;
                mCategoryTree = categoryTree;
                mBrandList = brandList;
                mCategoryKey = categoryKey;
                mChipsFilter = chipsFilter;
            }

            public ArrayList<GetSaleItemsResponse.Facets> getFacets() {
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

            public String getCategoryKey() { return mCategoryKey; }

            public ArrayList<SearchChipModel> getChipsFilter() {
                return mChipsFilter == null ? new ArrayList<>() : new ArrayList<>(mChipsFilter);
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

    List<GetSaleItemsResponse.Facets> mFacets;
    List<GetCategoryTreeResponse> mCategoryTree;
    List<SortingResponse> mSortingFacets = new ArrayList<>();
    private Map<String, GetCategoryTreeResponse> mCategoryMap = new HashMap<>();

    SubCategoriesAdapter mSubCategoriesAdapter;
    FacetItemsAdapter mFacetItemsAdapter;
    List<SearchChipModel> mSearchItemsList = new ArrayList<>();

    ArrayList<String> mBrandList = new ArrayList<>();
    ArrayList<String> mSizeList = new ArrayList<>();
    ArrayList<String> mColorList = new ArrayList<>();
    ArrayList<String> mSortingList = new ArrayList<>();
    private List<Pair<String, String>> mFacetFilters = new ArrayList();

    private int mOrigMinValue = -1;
    private int mOrigMaxValue = -1;
    private boolean mHasSeekbarReset = false;
    private boolean mIsSearchFilterControllerActive = false;
    private String mCategoryKey;
    private boolean mHasDefaultCategoryKey = false;
    private boolean mHasSavedInstance = false;

    Set<String> mCategoryKeys = new LinkedHashSet<>();

    private ArrayList<SearchChipModel> mPreviousSearchChips = new ArrayList<>();
    private ControllerChangeHandler.ControllerChangeListener newControllerChangeHandler;

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
        }

        return controller;
    }

    public SearchFilterController(Bundle args) {
        super(args);
        mFacets = JsonUtils.convertStringToObject(args.getString(BundleKeys.KEY_FACET_STRING, ""), new TypeToken<ArrayList<GetSaleItemsResponse.Facets>>() {}.getType());
        mSortingFacets = JsonUtils.convertStringToObject(args.getString(BundleKeys.KEY_SORTING_STRING, ""), new TypeToken<ArrayList<SortingResponse>>() {}.getType());
        mCategoryTree = JsonUtils.convertStringToObject(args.getString(BundleKeys.KEY_CATEGORY_STRING, ""), new TypeToken<ArrayList<GetCategoryTreeResponse>>() {}.getType());
        mBrandList = JsonUtils.convertStringToObject(args.getString(BundleKeys.KEY_BRAND_LIST, ""), new TypeToken<ArrayList<String>>() {}.getType());
        mCategoryKey = args.getString(BundleKeys.SALEITEMS_CATEGORY_MAP, "");
        if(mCategoryKey != null && !mCategoryKey.isEmpty()) {
            mCategoryKeys.add(mCategoryKey);
        }
        mHasDefaultCategoryKey = args.getBoolean(BundleKeys.KEY_HAS_DEFAULT_CATEGORY, false);

        String previousChipsString = args.getString(BundleKeys.SALEITEMS_CHIPS_FILTER, "");
        mPreviousSearchChips =  previousChipsString.isEmpty() ? new ArrayList<>() :
                JsonUtils.convertStringToObject(previousChipsString, new TypeToken<ArrayList<SearchChipModel>>() {}.getType());

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_HAS_SAVED_INSTANCE, true);
        outState.putString(KEY_FACET_STRING, new Gson().toJson(mFacets));
        outState.putString(KEY_SORTING_STRING, new Gson().toJson(mSortingFacets));
        outState.putString(KEY_CATEGORY_STRING, new Gson().toJson(mCategoryTree));
        outState.putString(KEY_BRAND_LIST, new Gson().toJson(mBrandList));
        outState.putString(SALEITEMS_CATEGORY_MAP, mCategoryKey);
        outState.putString(SALEITEMS_CHIPS_FILTER, new Gson().toJson(mPreviousSearchChips));
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mHasSavedInstance = savedInstanceState.getBoolean(KEY_HAS_SAVED_INSTANCE);
        mFacets = JsonUtils.convertStringToObject(savedInstanceState.getString(KEY_FACET_STRING,""), new TypeToken<ArrayList<GetSaleItemsResponse.Facets>>() {}.getType());
        mSortingFacets = JsonUtils.convertStringToObject(savedInstanceState.getString(KEY_SORTING_STRING,""), new TypeToken<ArrayList<SortingResponse>>() {}.getType());
        mCategoryTree = JsonUtils.convertStringToObject(savedInstanceState.getString(KEY_CATEGORY_STRING,""), new TypeToken<ArrayList<GetCategoryTreeResponse>>() {}.getType());
        mBrandList = JsonUtils.convertStringToObject(savedInstanceState.getString(KEY_BRAND_LIST,""), new TypeToken<ArrayList<String>>() {}.getType());
        mCategoryKey = savedInstanceState.getString(SALEITEMS_CATEGORY_MAP,"");
        if(mCategoryKey != null && !mCategoryKey.isEmpty()) {
            mCategoryKeys.add(mCategoryKey);
        }

        String previousChipsString = savedInstanceState.getString(SALEITEMS_CHIPS_FILTER,"");
        mPreviousSearchChips =  previousChipsString.isEmpty() ? new ArrayList<>() :
                JsonUtils.convertStringToObject(previousChipsString, new TypeToken<ArrayList<SearchChipModel>>() {}.getType());

    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_search_filter, container, false);
        getControllerComponent().inject(this);

        mPresenter.onAttach(this);
        return view;
    }

    @Override
    protected void onAttach(@NonNull View view){
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

        mActivity.setSearchFilterController(this);

        mPresenter.requestCategoryMap();
        if(mCategoryMap.isEmpty()) {
            createCategoryMap(mCategoryTree);
        }

        if (mFacets != null) {
            parseFacets(mFacets);
        }

        if (mSortingFacets != null) {
            parseSortingFacets(mSortingFacets);
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
        mFacetItemsAdapter = new FacetItemsAdapter(new ArrayList<>(), mPresenter, new HashSet<Integer>(), mFacetItemsRecyclerView);
        mFacetItemsRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        mFacetItemsRecyclerView.setAdapter(mFacetItemsAdapter);
        mFacetItemsRecyclerView.setHasFixedSize(true);
        mFacetItemsAdapter.setSearchItemsList(mSearchItemsList);

        mOpaqueView.setOnClickListener(v -> closeFacets());
        setRetainViewMode(RetainViewMode.RETAIN_DETACH);
    }

    @Override
    public List<Pair<String, String>> parseFacets(List<GetSaleItemsResponse.Facets> facets) {
        mFacets = facets;
        mBrandList = new ArrayList<>();
        mSizeList = new ArrayList<>();
        mColorList = new ArrayList<>();
        mFacetFilters = new ArrayList<>();
        Pair<String, String> pricePair = new Pair<>(BundleKeys.PRICE_FACETFILTER_NAME, PRICE_FACET_FILTER_TYPE);
        for (int i = 0; i < facets.size(); i++) {
            switch (facets.get(i).getFacetName()) {
                case BundleKeys.BRANDS_FACETFILTER_NAME:
                    for (int j = 0; j < facets.get(i).getFacetValues().size(); j++) {
                        GetSaleItemsResponse.Values facetValue = facets.get(i).getFacetValues().get(j);
                        mBrandList.add(facetValue.getValue());
                    }
                    mFacetFilters.add(new Pair<String, String>(BundleKeys.BRANDS_FACETFILTER_NAME, BRANDS_FACET_FILTER_TYPE));
                    break;
                case BundleKeys.SIZES_FACETFILTER_NAME:
                    for (int j = 0; j < facets.get(i).getFacetValues().size(); j++) {
                        GetSaleItemsResponse.Values facetValue = facets.get(i).getFacetValues().get(j);
                        mSizeList.add(facetValue.getValue());
                    }
                    mFacetFilters.add(new Pair<String, String>(BundleKeys.SIZES_FACETFILTER_NAME, SIZE_FACET_FILTER_TYPE));
                    break;
                case BundleKeys.PRICE_FACETFILTER_NAME:
                    mFacetFilters.add(pricePair);
                    break;
                case BundleKeys.COLORS_FACETFILTER_NAME:
                    for (int j = 0; j < facets.get(i).getFacetValues().size(); j++) {
                        GetSaleItemsResponse.Values facetValue = facets.get(i).getFacetValues().get(j);
                        mColorList.add(facetValue.getValue());
                    }
                    if(!mFacetFilters.contains(pricePair)) {
                        mFacetFilters.add(pricePair);
                    }
                    mFacetFilters.add(new Pair<String, String>(BundleKeys.COLORS_FACETFILTER_NAME, COLOR_FACET_FILTER_TYPE));
                    break;
                default:
                    break;
            }
        }
        if(facets.size() == 0) mFacetFilters.add(pricePair);

        return mFacetFilters;
    }

    @Override
    public void updateFacets(List<GetSaleItemsResponse.Facets> facets) {
        mBrandList = new ArrayList<>();
        mSizeList = new ArrayList<>();
        mColorList = new ArrayList<>();
        for (int i = 0; i < facets.size(); i++) {
            switch (facets.get(i).getFacetName()) {
                case BundleKeys.BRANDS_FACETFILTER_NAME:
                    for (int j = 0; j < facets.get(i).getFacetValues().size(); j++) {
                        GetSaleItemsResponse.Values facetValue = facets.get(i).getFacetValues().get(j);
                        mBrandList.add(facetValue.getValue());
                    }
                    break;
                case BundleKeys.SIZES_FACETFILTER_NAME:
                    for (int j = 0; j < facets.get(i).getFacetValues().size(); j++) {
                        GetSaleItemsResponse.Values facetValue = facets.get(i).getFacetValues().get(j);
                        mSizeList.add(facetValue.getValue());
                    }
                    break;
                case BundleKeys.COLORS_FACETFILTER_NAME:
                    for (int j = 0; j < facets.get(i).getFacetValues().size(); j++) {
                        GetSaleItemsResponse.Values facetValue = facets.get(i).getFacetValues().get(j);
                        mColorList.add(facetValue.getValue());
                    }
                    break;

                default:
                    break;
            }
        }
    }

    public void closeFacets() {
        if(!isViewAttached()) return;

        mIsSearchFilterControllerActive = false;
        mFacetsFrame.setVisibility(View.INVISIBLE);
        mPresenter.facetsClosed();
    }

    @Override
    public void updateSelectedFacet(int position) {
        mFacetItemsAdapter.setFilterType(getFacetFilterType(position));
        mFacetItemsAdapter.replaceData(mapFacetItemClicked(position));
    }

    private void setupPriceFacet() {
        mOrigMaxValue = mPresenter.getSearchMaxPrice();
        mOrigMinValue = mSeekbar.getSelectedMinValue().intValue();

        if (mOrigMaxValue == mOrigMinValue) {
            mOrigMaxValue = DEFAULT_PRICE_THRESHOLD;
        }

        mSeekbar.setMaxValue(mOrigMaxValue);

        mClearText.setOnClickListener((v) -> {
            if (!mHasSeekbarReset) {
                //remove previously selected price range
                for (SearchChipModel chip : mSearchItemsList) {
                    if (chip.getFilterType().equals(BundleKeys.PRICE_FACETFILTER_NAME)) {
                        mSearchItemsList.remove(chip);
                        break;
                    }
                }

                mPresenter.requestUpdate(mCategoryKeys, mSearchItemsList);

                onResetPriceRange();
            }
        });

        mSeekbar.setMinPriceMovingLayout(mMinPriceMovingLayout);
        mSeekbar.setMaxPriceMovingLayout(mMaxPriceMovingLayout);
        mSeekbar.setOnRangeSeekbarChangeListener((minValue, maxValue) -> {
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
                    SearchChipModel priceChip = new SearchChipModel(BundleKeys.PRICE_FACETFILTER_NAME, minValue.intValue() + " to " + maxValue.intValue(), -1);
                    priceChip.setMaxValue(maxValue.intValue());
                    priceChip.setMinValue(minValue.intValue());
                    mSearchItemsList.add(priceChip);
                }

                mHasSeekbarReset = false;

                mPresenter.requestUpdate(mCategoryKeys, mSearchItemsList);

            }
        });

        SearchChipModel priceChip = findPriceChip();

        if (priceChip != null) {
            mSeekbar.setMinStartValue(priceChip.getMinValue()).apply();
            mSeekbar.setMaxStartValue(priceChip.getMaxValue()).apply();


        }
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    private void parseSortingFacets(List<SortingResponse> sortingList) {
        for (SortingResponse response : sortingList) {
            mSortingList.add(response.getTitle());
        }
    }

    private void updateValidChips() {
        List<SearchChipModel> listToIterate = new ArrayList<>(mPreviousSearchChips);
        for (SearchChipModel chip : listToIterate) {
            boolean hasBrand = chip.getFilterType().equals(BundleKeys.BRANDS_FACETFILTER_NAME) &&
                    !mBrandList.contains(chip.getChipTitle());
            boolean hasSizes = chip.getFilterType().equals(BundleKeys.SIZES_FACETFILTER_NAME) &&
                    !mSizeList.contains(chip.getChipTitle());
            boolean hasColor = chip.getFilterType().equals(BundleKeys.COLORS_FACETFILTER_NAME) &&
                    !mColorList.contains(chip.getChipTitle());

            if (hasBrand || hasSizes || hasColor) {
                mPreviousSearchChips.remove(chip);
            }
        }
    }

    @Override
    public void showFacetItem(int position) {
        hideKeyboard();
        mFacetsFrame.setVisibility(View.VISIBLE);

        if (getFacetFilterType(position) != BundleKeys.PRICE_FACETFILTER_NAME) { //only do this logic if facet clicked != price

            if (getFacetFilterType(position) == BundleKeys.CATEGORY_TREE_FACET) {

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
    public void updateFacetItemToFilters(List<SearchChipModel> selectedChips) {
        mPresenter.requestUpdate(mCategoryKeys, selectedChips);
    }

    @Override
    public void onResetPriceRange() {
        mSeekbar.setMinStartValue(mOrigMinValue);
        mSeekbar.setMaxStartValue(mOrigMaxValue);

        mSeekbar.apply();
        mSeekbar.setMinThumbPosition(0);
        mSeekbar.setMaxThumbPosition(1);

        mHasSeekbarReset = true;
        mSeekbar.resetMovingLayoutVisibility();
    }

    @Override
    public void replaceCategoryTree(List<GetCategoryTreeResponse> categoryTree) {
        mSubCategoriesAdapter.replaceData(categoryTree);
        mSubCategoriesAdapter.setSelectedCategories(mCategoryKeys);
    }

    @Override
    public void replaceSearchChipModels(List<SearchChipModel> chipModels) {
        mFacetItemsAdapter.setSearchItemsList(chipModels);
        mSearchItemsList = chipModels;
        boolean doesSliderExist = false;
        for (SearchChipModel chipModel: chipModels) {
            if (chipModel.getFilterType().equals(BundleKeys.PRICE_FACETFILTER_NAME)) {
                mSeekbar.setMinThumbPosition(chipModel.getMinValue() / mOrigMaxValue);
                mSeekbar.setMaxThumbPosition(chipModel.getMaxValue() / mOrigMaxValue);
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

        if(!children.isEmpty()) { //if i have children{}
            setChildrenSelection(category.getKey(),false);
        }

        if (mHasDefaultCategoryKey && mCategoryKeys.size() == 0) {
            mCategoryKeys.add(mCategoryKey);
        }
        mPresenter.requestUpdate(mCategoryKeys, mSearchItemsList);
    }

    @Override
    public Set<String> getCategoryKeys() {
        return mCategoryKeys;
    }

    private void checkParentSelection(GetCategoryTreeResponse category){
        boolean childrenAreAllSelected = true;

        //get parent node and children
        String parentKey = StringUtils.getParentKey(category);
        GetCategoryTreeResponse parentNode = mCategoryMap.get(parentKey);
        List<GetCategoryTreeResponse> parentNodeChildren = parentNode.getChildren();

        if(parentKey.equals(category.getKey())){ //we reached end node up. terminate recursion
            return;
        }

        for(GetCategoryTreeResponse child : parentNodeChildren){
            if(!child.isSelected()){
                childrenAreAllSelected = false;
            }
        }

        if(childrenAreAllSelected){
            for(GetCategoryTreeResponse child : parentNodeChildren){
                setChildrenSelection(child.getKey(), false);
            }
        } else {
            parentNode.setSelected(false);
            mCategoryKeys.remove(parentNode.getKey());
        }


        //recursion
        checkParentSelection(parentNode);
        if (childrenAreAllSelected) {
            for(GetCategoryTreeResponse child : parentNodeChildren){
                setChildrenSelection(child.getKey(), false);
            }
        }
    }

    private void setChildrenSelection(String key, boolean isSelected) {
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

    private void createCategoryMap(List<GetCategoryTreeResponse> getCategoryTreeResponses) {
        if (getCategoryTreeResponses != null || !getCategoryTreeResponses.isEmpty()) {
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

    private List<String> mapFacetItemClicked(int position) {
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
    public void setSearchFilterControllerActive(boolean isTabActive){
        mIsSearchFilterControllerActive = isTabActive;
    }

    @Override
    public void setFacetFilterItems(List<Pair<String,String>> mFacetFilters) {
        this.mFacetFilters = mFacetFilters;
    }

    @Override
    public boolean getIsFacetsVisible() {
        return mFacetsFrame.getVisibility() == View.VISIBLE;
    }

    @Override
    public void onReceiveCategoryMap(Map<String, GetCategoryTreeResponse> categoryMap) {
        mCategoryMap = categoryMap;
    }

    @Override
    public void setRepository(SearchFilterMvpRepository repository) {
        mPresenter.setRepository(repository);
    }
}
