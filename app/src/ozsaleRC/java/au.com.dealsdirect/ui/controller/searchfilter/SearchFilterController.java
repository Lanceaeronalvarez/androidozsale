package au.com.dealsdirect.ui.controller.searchfilter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluelinelabs.conductor.Router;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
import au.com.dealsdirect.data.network.model.sorting.SortingResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsController;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsMvpPresenter;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsMvpView;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.FacetItemsAdapter;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.FacetsAdapter;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SubCategoriesAdapter;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SubCategoryItemsAdapter;
import au.com.dealsdirect.ui.custom.CustomRangeSeekbar;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.JsonUtils;
import butterknife.BindView;

import static au.com.dealsdirect.utils.BundleKeys.BRANDS_FACET_FILTER_TYPE;
import static au.com.dealsdirect.utils.BundleKeys.COLOR_FACET_FILTER_TYPE;
import static au.com.dealsdirect.utils.BundleKeys.PRICE_FACET_FILTER_TYPE;
import static au.com.dealsdirect.utils.BundleKeys.SIZE_FACET_FILTER_TYPE;


/**
 * Created by smartwave on 20/07/2017.
 */

public class SearchFilterController extends BaseController
        implements SearchFilterMvpView, SubCategoryClickListener, SubCategoryItemClickListener {

    public static final String TAG = SearchFilterController.class.getSimpleName();

    @Inject
    SaleItemsMvpPresenter<SaleItemsMvpView> mSaleItemsPresenter;

    @Inject
    SearchFilterMvpPresenter<SearchFilterMvpView> mPresenter;

    @Inject
    protected MainActivity mActivity;

    @BindView(R.id.controller_search_filter_tabs)
    TabLayout mTabLayout;

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

    private String mSaleItemsTitle = "";

    List<GetSaleItemsResponse.Facets> mFacets;
    List<GetCategoryTreeResponse> mCategoryTree;
    List<SortingResponse> mSortingFacets = new ArrayList<>();
    private Map<String, GetCategoryTreeResponse> mCategoryMap = new HashMap<>();
    private boolean hasFacetChanged;

    SubCategoriesAdapter mSubCategoriesAdapter;
    FacetsAdapter mFacetsAdapter;
    FacetItemsAdapter mFacetItemsAdapter;
    List<SearchChipModel> mSearchItemsList = new ArrayList<>();
    List<GetCategoryTreeResponse> categoryListFromFacets = new ArrayList<>();
    SaleItemsController mSaleItemsController;
    GetCategoryTreeResponse mCategory;

    ArrayList<String> mBrandList = new ArrayList<>();
    ArrayList<String> mSizeList = new ArrayList<>();
    ArrayList<String> mColorList = new ArrayList<>();
    ArrayList<String> mSortingList = new ArrayList<>();

    private int origMinValue = -1;
    private int origMaxValue = -1;
    private boolean isSeekbarReset = false;
    private String mTitle = "";
    private int mCurrentTabPosition = -1;
    private boolean mIsSearchFilterControllerActive;

    String mSaleId = "";
    List<String> mCategoryKeys = new ArrayList<>();


    private Set<Integer> origSelectedSet = new HashSet<Integer>();

    private int mPreviousSelectedFacetIndex = -1;

    private HashMap<String, Set<Integer>> mPreviousSelectedFacetIndices = new HashMap<>();
    private ArrayList<SearchChipModel> mPreviousSearchChips = new ArrayList<>();

    List<Pair<String, String>> mFacetFilters = new ArrayList();


    public static SearchFilterController newInstance() {
        return new SearchFilterController(new BundleBuilder(new Bundle()).build());
    }

    public SearchFilterController(Bundle args) {
        super(args);
        mFacets = JsonUtils.convertStringToObject(args.getString(BundleKeys.KEY_FACET_STRING, ""), new TypeToken<ArrayList<GetSaleItemsResponse.Facets>>() {
        }.getType());
        mSortingFacets = JsonUtils.convertStringToObject(args.getString(BundleKeys.KEY_SORTING_STRING, ""), new TypeToken<ArrayList<SortingResponse>>() {
        }.getType());
        mSaleId = args.getString(BundleKeys.SALEITEMS_SALE_ID, "");
        mCategoryTree = JsonUtils.convertStringToObject(args.getString(BundleKeys.KEY_CATEGORY_STRING, ""), new TypeToken<ArrayList<GetCategoryTreeResponse>>() {
        }.getType());
        mBrandList = JsonUtils.convertStringToObject(args.getString(BundleKeys.KEY_BRAND_LIST, ""), new TypeToken<ArrayList<String>>() {
        }.getType());
        mSaleItemsTitle = args.getString(BundleKeys.KEY_SALE_ITEMS_TITLE);
        String previousChipsString = args.getString(BundleKeys.SALEITEMS_CHIPS_FILTER, "");
        if (!previousChipsString.isEmpty()) {
            mPreviousSearchChips = JsonUtils.convertStringToObject(previousChipsString, new TypeToken<ArrayList<SearchChipModel>>() {
            }.getType());
        } else {
            mPreviousSearchChips = new ArrayList<>();
        }
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_search_filter, container, false);
        getControllerComponent().inject(this);

        Router router = mActivity.getSelectedBottomNavTab() == 0 ? mActivity.getHomeRouter()
                : mActivity.getCategoriesRouter();

        mSaleItemsController = (SaleItemsController) router.getControllerWithTag(getResources()
                .getString(R.string.sale_items_controller_tag));

        mSaleItemsPresenter.onAttach(mSaleItemsController);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {

        createCategoryMap(mCategoryTree);
        createCategoryList(mCategoryTree);

        if (mFacets != null) {
            parseFacets(mFacets);
            hasFacetChanged = false;
        }

        if (mSortingFacets != null) {
            parseSortingFacets(mSortingFacets);
        }

        if (mTabLayout.getTabCount() == 0) {
            setupTabs();
        }

        setupPriceFacet();
        updateValidChips();

//      SETUP FACET ITEMS (sub of facets)
        mFacetItemsAdapter = new FacetItemsAdapter(new ArrayList<>(), mPresenter, new HashSet<Integer>(), mFacetItemsRecyclerView);
        mFacetItemsRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        mFacetItemsRecyclerView.setAdapter(mFacetItemsAdapter);
        mFacetItemsAdapter.setSearchItemsList(mSearchItemsList);

//      SETUP CATEGORIES
        mSubCategoriesAdapter = new SubCategoriesAdapter(mCategoryTree, this, this, mCategoryMap);
        mFilterCategoriesRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        mFilterCategoriesRecyclerView.setAdapter(mSubCategoriesAdapter);

        mOpaqueView.setOnClickListener(v -> closeFacets());
    }

    public void parseFacets(List<GetSaleItemsResponse.Facets> facets) {
        mFacets = facets;
        if (facets.size() != 0) {
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
                        mFacetFilters.add(new Pair<String, String>(BundleKeys.BRANDS_FACETFILTER_NAME, BRANDS_FACET_FILTER_TYPE));
                        break;
                    case BundleKeys.PRICE_FACETFILTER_NAME:
                        mFacetFilters.add(new Pair<String, String>(BundleKeys.PRICE_FACETFILTER_NAME, PRICE_FACET_FILTER_TYPE));
                        break;
                    case BundleKeys.SIZES_FACETFILTER_NAME:
                        for (int j = 0; j < facets.get(i).getFacetValues().size(); j++) {
                            GetSaleItemsResponse.Values facetValue = facets.get(i).getFacetValues().get(j);
                            mSizeList.add(facetValue.getValue());
                        }
                        mFacetFilters.add(new Pair<String, String>(BundleKeys.SIZES_FACETFILTER_NAME, SIZE_FACET_FILTER_TYPE));
                        break;
                    case BundleKeys.COLORS_FACETFILTER_NAME:
                        for (int j = 0; j < facets.get(i).getFacetValues().size(); j++) {
                            GetSaleItemsResponse.Values facetValue = facets.get(i).getFacetValues().get(j);
                            mColorList.add(facetValue.getValue());
                        }
                        mFacetFilters.add(new Pair<String, String>(BundleKeys.COLORS_FACETFILTER_NAME, COLOR_FACET_FILTER_TYPE));
                        break;

                    default:
                        break;
                }
            }
        }
        if (mTabLayout.getTabCount() == 0) {
            setupTabs();
        }
    }

    private void setupTabs() {
        mFacetFilters.add(0, new Pair<String, String>(BundleKeys.CATEGORY_TREE_FACET, "Categories"));
        mFacetFilters.add(mFacetFilters.size(), new Pair<String, String>(BundleKeys.SORT_FACETFILTER_NAME, "Sort"));
        for (Pair<String, String> pair : mFacetFilters) {
            mTabLayout.addTab(mTabLayout.newTab().setText(pair.second), false);
        }

        //Remove selected state by default setup
        mTabLayout.getTabAt(0).select();
        mCurrentTabPosition = 0;
        toggleTabSelection(false);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showFacetItem(tab.getPosition());
                mCurrentTabPosition = tab.getPosition();
                toggleTabSelection(true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                showFacetItem(tab.getPosition());
                mCurrentTabPosition = tab.getPosition();
                toggleTabSelection(true);
            }
        });

        //change tab mode depending on the screen width
        Runnable tabConfig = () -> {
            if (mTabLayout.getWidth() < mActivity.getResources().getDisplayMetrics().widthPixels) {
                mTabLayout.setTabMode(TabLayout.MODE_FIXED);
                ViewGroup.LayoutParams mParams = mTabLayout.getLayoutParams();
                mParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                mTabLayout.setLayoutParams(mParams);
            }
        };
        mTabLayout.post(tabConfig);
    }

    private void closeFacets() {
        mFacetsFrame.setVisibility(View.GONE);
        toggleTabSelection(false);
    }

    private void toggleTabSelection(boolean val) {
        mIsSearchFilterControllerActive = val;
        LinearLayout tabStrip = (LinearLayout) mTabLayout.getChildAt(0);
        tabStrip.getChildAt(mCurrentTabPosition).setSelected(val);
    }


    private void setupPriceFacet() {
        origMaxValue = mPresenter.getSearchMaxPrice();
        origMinValue = mSeekbar.getSelectedMinValue().intValue();

        if (origMaxValue == origMinValue) {
            origMaxValue = 200;
        }

        mSeekbar.setMaxValue(origMaxValue);

        mClearText.setOnClickListener((v) -> {
            if (!isSeekbarReset) {
                //remove previously selected price range
                for (SearchChipModel chip : mSearchItemsList) {
                    if (chip.getFilterType().equals(BundleKeys.PRICE_FACETFILTER_NAME)) {
                        mSearchItemsList.remove(chip);
                        break;
                    }
                }
                onResetPriceRange();
            }
        });

        mSeekbar.setMinPriceMovingLayout(mMinPriceMovingLayout);
        mSeekbar.setMaxPriceMovingLayout(mMaxPriceMovingLayout);
        mSeekbar.setOnRangeSeekbarChangeListener((minValue, maxValue) -> {
            mMinPrice.setText("$" + minValue.intValue());
            mMaxPrice.setText("$" + maxValue.intValue());
            if (maxValue.intValue() == origMaxValue) {
                mMaxPrice.setText("$" + maxValue.intValue() + "+");
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
                if (origMinValue != minValue.intValue() || origMaxValue != maxValue.intValue()) {
                    SearchChipModel priceChip = new SearchChipModel(BundleKeys.PRICE_FACETFILTER_NAME, minValue.intValue() + " to " + maxValue.intValue(), -1);
                    priceChip.setMaxValue(maxValue.intValue());
                    priceChip.setMinValue(minValue.intValue());
                    mSearchItemsList.add(priceChip);
                }

                isSeekbarReset = false;

                mSaleItemsController.setChipFilters(mSearchItemsList);
                mSaleItemsPresenter.loadSaleItems(mSaleItemsController.createSaleItemsRequest(mCategoryKeys, mSaleId, 0, mSearchItemsList, null));

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

            if (chip.getFilterType().equals(BundleKeys.BRANDS_FACETFILTER_NAME) && !mBrandList.contains(chip.getChipTitle())) {
                mPreviousSearchChips.remove(chip);
            }

            if (chip.getFilterType().equals(BundleKeys.SIZES_FACETFILTER_NAME) && !mSizeList.contains(chip.getChipTitle())) {
                mPreviousSearchChips.remove(chip);
            }

            if (chip.getFilterType().equals(BundleKeys.COLORS_FACETFILTER_NAME) && !mColorList.contains(chip.getChipTitle())) {
                mPreviousSearchChips.remove(chip);
            }
        }
    }

    @Override
    public void showFacetItem(int position) {
        mFacetsFrame.setVisibility(View.VISIBLE);

        if (getFacetFilterType(position) != BundleKeys.PRICE_FACETFILTER_NAME) { //only do this logic if facet clicked != price

            if (getFacetFilterType(position) == BundleKeys.CATEGORY_TREE_FACET) {

                mFilterCategoriesRecyclerView.setVisibility(View.VISIBLE);
                mFacetItemsRecyclerView.setVisibility(View.GONE);
                mSeekbarLayout.setVisibility(View.GONE);

            } else {

                mFacetItemsRecyclerView.setVisibility(View.VISIBLE);
                mSeekbarLayout.setVisibility(View.GONE);
                mFilterCategoriesRecyclerView.setVisibility(View.GONE);
            }

            if (mPreviousSelectedFacetIndex != -1) {
                mPreviousSelectedFacetIndices.put(getFacetFilterType(mPreviousSelectedFacetIndex), new HashSet<>(mFacetItemsAdapter.getSelectedFacets()));
            }

            if (position != mPreviousSelectedFacetIndex) {
                mFacetItemsAdapter.clearSelectedFacets();
                origSelectedSet.clear();
            }

            if (mPreviousSelectedFacetIndices.get(getFacetFilterType(position)) != null) {
                mFacetItemsAdapter.updateSelectedFacets(mPreviousSelectedFacetIndices.get(getFacetFilterType(position)));
                origSelectedSet = mPreviousSelectedFacetIndices.get(getFacetFilterType(position));
            }

        } else { //price is clicked
            mFilterCategoriesRecyclerView.setVisibility(View.GONE);
            mFacetItemsRecyclerView.setVisibility(View.GONE);
            mSeekbarLayout.setVisibility(View.VISIBLE);
        }

        mFacetItemsAdapter.setFilterType(getFacetFilterType(position));
        mFacetItemsAdapter.replaceData(mapFacetItemClicked(position));

        mPreviousSelectedFacetIndex = position;
    }

    @Override
    public void updateFacetItemToFilters(Set<Integer> selectPosSet) {
        Log.d("selectPosSet", selectPosSet.toString());

        Set<Integer> oldSet = origSelectedSet;
        Set<Integer> newSet = new HashSet<Integer>(selectPosSet);
        origSelectedSet = new HashSet<Integer>(selectPosSet);
        newSet.removeAll(oldSet);
        oldSet.removeAll(origSelectedSet);

        if (!newSet.isEmpty()) {
            List<Integer> temp = new ArrayList(newSet);
            SearchChipModel newChip = new SearchChipModel(mFacetItemsAdapter.getFilterType(), mFacetItemsAdapter.getData().get(temp.get(0)), temp.get(0));
            mSearchItemsList.add(newChip);
        } else if (!oldSet.isEmpty()) {
            List<Integer> temp = new ArrayList(oldSet);
            SearchChipModel chipToRemove = null;
            for (SearchChipModel chip : mSearchItemsList) {
                if (chip.getChipTitle().equals(mFacetItemsAdapter.getData().get(temp.get(0)))) {
                    chipToRemove = chip;
                }
            }

            if (chipToRemove != null) {
                mSearchItemsList.remove(chipToRemove);
            }
        }
        hasFacetChanged = mSearchItemsList.size() > 0;

        mSaleItemsController.setChipFilters(mSearchItemsList);
        mSaleItemsPresenter.loadSaleItems(mSaleItemsController.createSaleItemsRequest(mCategoryKeys, mSaleId, 0, mSearchItemsList, null));
    }

    @Override
    public Set<Integer> getOriginalSelectedSet() {
        return origSelectedSet;
    }

    @Override
    public void onResetPriceRange() {
        mSeekbar.setMinStartValue(origMinValue);
        mSeekbar.setMaxStartValue(origMaxValue);

        mSeekbar.apply();
        mMinPriceMovingLayout.setTranslationX(0);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mSeekbar.getLayoutParams();
        mMaxPriceMovingLayout.setX(mSeekbar.getWidth() - (lp.rightMargin));

        isSeekbarReset = true;
        mSeekbar.resetMovingLayoutVisibility();
    }

    @Override
    public void updateActiveFacets(List<SearchChipModel> activeChips) {
        if (mFacetsAdapter != null) {
//            mFacetsAdapter.updateSelectedSearchChips(activeChips);
            mFacetsAdapter.notifyDataSetChanged();
            mFacetItemsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onShowTransparentOverlay() {

    }

    @Override
    public void onHideTransparentOverlay() {

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

    private void createCategoryMap(List<GetCategoryTreeResponse> categories) {

        updateCategories(categories);

    }

    private void updateCategories(List<GetCategoryTreeResponse> getCategoryTreeResponses) {
        for (GetCategoryTreeResponse category : getCategoryTreeResponses) {
            mCategoryMap.put(category.getKey(), category);
            updateCategories(category.getChildren());
        }
    }

    private List<GetCategoryTreeResponse> fillCategoryContent() {

        return mCategoryMap.get("shop").getChildren();
    }

    @Override
    public void onSubCategoryClicked(List<GetCategoryTreeResponse> response, String key) {
        mCategoryTree = response;
        for (GetCategoryTreeResponse child : mCategoryTree) {
            if (child.getKey().equalsIgnoreCase(key) && child.getSelected()) {
                mCategoryKeys.add(child.getKey());
            } else {
                mCategoryKeys.remove(child.getKey());
            }
            if (child.getChildren().size() > 0) {
                mCategoryMap.put(child.getKey(), setSelectedChildren(child));
            }
        }

        mSaleItemsPresenter.loadSaleItems(mSaleItemsController.createSaleItemsRequest(mCategoryKeys, mSaleId, 0, mSearchItemsList, null));
    }

    @Override
    public void onSubCategoryItemClicked(SubCategoryItemsAdapter adapter, GetCategoryTreeResponse response) {
        response.setSelected(!response.getSelected());

        if (response.getSelected() && !mCategoryKeys.contains(response.getKey())) {
            mCategoryKeys.add(response.getKey());
        } else {
            mCategoryKeys.remove(response.getKey());
        }

        if (response.getChildren().size() > 0) {
            mCategoryMap.put(response.getKey(), setSelectedChildren(response));
        }

        String parentKey = getParentKey(response);
        GetCategoryTreeResponse parentNode = mCategoryMap.get(parentKey);
        List<GetCategoryTreeResponse> parentList = mCategoryMap.get(parentKey).getChildren();
        parentList.set(adapter.getPosition(), response);
        parentNode.setChildren(new ArrayList<>(parentList));

        mCategoryMap.put(response.getKey(), response);
        mCategoryMap.put(parentKey, parentNode);

        categoryTreeParentCheck(response);

        mSaleItemsPresenter.loadSaleItems(mSaleItemsController.createSaleItemsRequest(mCategoryKeys, mSaleId, 0, mSearchItemsList, null));
    }

    private void categoryTreeParentCheck(GetCategoryTreeResponse response) {
        String parentKey = getParentKey(response);
        GetCategoryTreeResponse parentCategory = mCategoryMap.get(parentKey);
        if (parentCategory != null && parentCategory.getChildren() != null) {
            parentCategory.setSelected(checkChildrenSelectedCount(parentCategory.getChildren()));
            mCategoryMap.put(parentKey, mCategoryMap.get(parentKey));
        }
        if (!parentCategory.getSelected() && mCategoryKeys.contains(parentKey)) {
            mCategoryKeys.remove(parentKey);
            for (GetCategoryTreeResponse category : parentCategory.getChildren()) {
                mCategoryKeys.add(category.getKey());
            }
        }
    }

    private boolean checkChildrenSelectedCount(List<GetCategoryTreeResponse> list) {
        int count = 0;
        for (GetCategoryTreeResponse child : list) {
            if (child.getSelected()) {
                count++;
            }
        }
        return count == list.size();
    }

    private GetCategoryTreeResponse setSelectedChildren(GetCategoryTreeResponse response) {

        for (GetCategoryTreeResponse object : response.getChildren()) {
            //no need to include children in categorykeys if parent is selected
            if (object.getSelected() != response.getSelected()) {
                mCategoryKeys.remove(object.getKey());
            }
            object.setSelected(response.getSelected());
            mCategoryMap.put(object.getKey(), object);

            if (object.getChildren().size() > 0) {
                mCategoryMap.put(response.getKey(), setSelectedChildren(response));
            }
        }
        return response;
    }

    public void updateSubCategoryFilters(List<GetCategoryTreeResponse> categoryTree) {
        categoryListFromFacets.clear();
        for (int i = 0; i < categoryTree.size(); i++) {
            categoryTree.get(i).setChildren(updateCategoryListFromResponse(new ArrayList<>(categoryTree.get(i).getChildren())));
            categoryTree.set(i, categoryTree.get(i));
            categoryListFromFacets.add(categoryTree.get(i));
        }
        mSubCategoriesAdapter.replaceData(categoryListFromFacets);
    }

    private ArrayList<GetCategoryTreeResponse> updateCategoryListFromResponse(ArrayList<GetCategoryTreeResponse> category) {
        for (int i = 0; i < category.size(); i++) {
            category.get(i).setSelected(mCategoryMap.get(category.get(i).getKey()).getSelected());
            if(category.get(i).getChildren().size() > 0) {
                category.get(i).setChildren(updateCategoryListFromResponse(new ArrayList<>(category.get(i).getChildren())));
            }
            category.set(i, category.get(i));
        }
        return category;
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

    private String getParentKey(GetCategoryTreeResponse category) {
        return category.getKey().replace(">>>" + category.getName(), "");
    }
}
