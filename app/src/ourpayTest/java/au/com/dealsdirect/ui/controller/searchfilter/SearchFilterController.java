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

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.data.network.model.publicsalescategories.GetPublicSalesCategoriesResponse;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
import au.com.dealsdirect.data.network.model.sorting.SortingResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsMvpPresenter;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsMvpView;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.FacetItemsAdapter;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SubCategoriesAdapter;
import au.com.dealsdirect.ui.custom.CustomRangeSeekbar;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.JsonUtils;
import au.com.dealsdirect.utils.StringUtils;
import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;


/**
 * Created by smartwave on 20/07/2017.
 */

public class SearchFilterController extends BaseController
        implements SearchFilterMvpView {

    public static final String TAG = SearchFilterController.class.getSimpleName();


    private String mSaleItemsTitle = "";

    @Inject
    SearchFilterMvpPresenter<SearchFilterMvpView> mPresenter;
    @Inject
    SaleItemsMvpPresenter<SaleItemsMvpView> mSaleItemsPresenter;

    SaleItemsMvpView mSaleItemsView;

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

    List<GetSaleItemsResponse.Facets> mFacets;
    List<GetCategoryTreeResponse> mCategoryTree;
    List<SortingResponse> mSortingFacets = new ArrayList<>();
    private Map<String, GetCategoryTreeResponse> mCategoryMap = new LinkedHashMap<>();


    SubCategoriesAdapter mSubCategoriesAdapter;
    FacetItemsAdapter mFacetItemsAdapter;
    List<SearchChipModel> mSearchItemsList = new ArrayList<>();

    ArrayList<String> mBrandList = new ArrayList<>();
    ArrayList<String> mSizeList = new ArrayList<>();
    ArrayList<String> mColorList = new ArrayList<>();
    ArrayList<String> mSortingList = new ArrayList<>();

    private int origMinValue = -1;
    private int origMaxValue = -1;
    private boolean isSeekbarReset = false;
    private String mChosenCategory = "";
    private Set<String> mCategoryKeys = new LinkedHashSet<>();
    private int mCurrentTabPosition = -1;
    private boolean mIsSearchFilterControllerActive;

    String mSaleId = "";

    private ArrayList<SearchChipModel> mPreviousSearchChips = new ArrayList<>();

    List<Pair<String, String>> mFacetFilters = new ArrayList(Arrays.asList
            (new Pair<String, String>(BundleKeys.SORT_FACETFILTER_NAME, "Sort"),
                    new Pair<String, String>(BundleKeys.CATEGORY_TREE_FACET, "Category")));

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
//        mCategoryKey = args.getString(BundleKeys.SALEITEMS_CATEGORY_MAP, "");
        mCategoryTree = JsonUtils.convertStringToObject(args.getString(BundleKeys.KEY_CATEGORY_STRING, ""), new TypeToken<ArrayList<GetCategoryTreeResponse>>() {
        }.getType());
        mBrandList = JsonUtils.convertStringToObject(args.getString(BundleKeys.KEY_BRAND_LIST, ""), new TypeToken<ArrayList<String>>() {
        }.getType());
        mSaleItemsTitle = args.getString(BundleKeys.KEY_SALE_ITEMS_TITLE);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_search_filter, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        Router homeRouter = mActivity.getHomeRouter();
        Controller mSaleItemsController = homeRouter.getControllerWithTag(getResources().getString(R.string.sale_items_controller_tag));
        mSaleItemsView = (SaleItemsMvpView) mSaleItemsController;
        mSaleItemsPresenter.onAttach(mSaleItemsView);
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

        if (mFacets != null) {
            parseFacets(mFacets);
        }

        if (mSortingFacets != null) {
            parseSortingFacets(mSortingFacets);
        }

        setupPriceFacet();

        mOpaqueView.setOnClickListener(v -> closeFacets());

        mSearchItemsList = new ArrayList<SearchChipModel>();

        mSubCategoriesAdapter = new SubCategoriesAdapter(mChosenCategory, mCategoryTree, mPresenter, mCategoryMap);
        mFilterCategoriesRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        mFilterCategoriesRecyclerView.setAdapter(mSubCategoriesAdapter);

        mFacetItemsAdapter = new FacetItemsAdapter(new ArrayList<>(), mPresenter, new HashSet<Integer>(), mFacetItemsRecyclerView);
        mFacetItemsAdapter.setSearchItemsList(mSearchItemsList);
        mFacetItemsRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        mFacetItemsRecyclerView.setAdapter(mFacetItemsAdapter);


        setRetainViewMode(RetainViewMode.RETAIN_DETACH);

    }

    private void parseFacets(List<GetSaleItemsResponse.Facets> facets) {
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
                        mFacetFilters.add(new Pair<String, String>(BundleKeys.BRANDS_FACETFILTER_NAME, "Brands"));
                        break;
                    case BundleKeys.SIZES_FACETFILTER_NAME:
                        for (int j = 0; j < facets.get(i).getFacetValues().size(); j++) {
                            GetSaleItemsResponse.Values facetValue = facets.get(i).getFacetValues().get(j);
                            mSizeList.add(facetValue.getValue());
                        }
                        mFacetFilters.add(new Pair<String, String>(BundleKeys.SIZES_FACETFILTER_NAME, "Sizes"));
                        break;
                    case BundleKeys.COLORS_FACETFILTER_NAME:
                        for (int j = 0; j < facets.get(i).getFacetValues().size(); j++) {
                            GetSaleItemsResponse.Values facetValue = facets.get(i).getFacetValues().get(j);
                            mColorList.add(facetValue.getValue());
                        }

                        mFacetFilters.add(new Pair<String, String>(BundleKeys.COLORS_FACETFILTER_NAME, "Colors"));
                        break;
                    case BundleKeys.PRICE_FACETFILTER_NAME:
                        mFacetFilters.add(new Pair<String, String>(BundleKeys.PRICE_FACETFILTER_NAME, "Price"));

                        break;
                    default:
                        break;
                }
            })

        if (mTabLayout.getTabCount() == 0) {
            setupTabs();
        }
    }

    private void setupTabs() {
        for (Pair<String, String> pair : mFacetFilters) {
            mTabLayout.addTab(mTabLayout.newTab().setText(pair.second), false);
        }

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
    }

    private void closeFacets() {
        mFacetsFrame.setVisibility(View.GONE);
        toggleTabSelection(false);
    }

    private void toggleTabSelection(boolean val) {
        mIsSearchFilterControllerActive = val;
        LinearLayout tabStrip = (LinearLayout) mTabLayout.getChildAt(0);
        tabStrip.getChildAt(mCurrentTabPosition).setSelected(val);
        mTabLayout.setSelectedTabIndicatorHeight(val ? (int) (5 * getResources().getDisplayMetrics().density) : 0);
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

                mSaleItemsPresenter.loadSaleItems(mSaleItemsView.createSaleItemsRequest(mCategoryKeys, mSaleId, 0, mSearchItemsList, ""));

                onResetPriceRange();
            }
        });
        mSeekbar.setMinPriceMovingLayout(mMinPriceMovingLayout);
        mSeekbar.setMaxPriceMovingLayout(mMaxPriceMovingLayout);
        mSeekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                mMinPrice.setText("$" + minValue.intValue());
                mMaxPrice.setText("$" + maxValue.intValue());
                if (maxValue.intValue() == origMaxValue) {
                    mMaxPrice.setText("$" + maxValue.intValue() + "+");
                }

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

                mSaleItemsPresenter.loadSaleItems(mSaleItemsView.createSaleItemsRequest(mCategoryKeys, mSaleId, 0, mSearchItemsList, ""));

                isSeekbarReset = false;
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

    private void removeAllChipsExceptCategoryAndSearchQuery() {
        List<SearchChipModel> listToIterate = new ArrayList<>(mPreviousSearchChips);
        for (SearchChipModel chip : listToIterate) {
            if (!(chip.getFilterType().equals(BundleKeys.CATEGORY_TREE_FACET) || chip.getFilterType().equals(BundleKeys.SEARCH_QUERY_NAME))) {
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

        } else { //price is clicked
            mFilterCategoriesRecyclerView.setVisibility(View.GONE);
            mFacetItemsRecyclerView.setVisibility(View.GONE);
            mSeekbarLayout.setVisibility(View.VISIBLE);
        }

        mFacetItemsAdapter.setFilterType(getFacetFilterType(position));
        mFacetItemsAdapter.replaceData(mapFacetItemClicked(position));

    }

    @Override
    public void updateFacetItemToFilters(List<SearchChipModel> selectedChips) {
        mSaleItemsPresenter.loadSaleItems(mSaleItemsView.createSaleItemsRequest(mCategoryKeys, mSaleId, 0, selectedChips, ""));
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
    public void replaceFacets(List<GetSaleItemsResponse.Facets> newFacets) {
        mFacets = newFacets;
        parseFacets(mFacets);
        if(mCurrentTabPosition!=-1) {
            mFacetItemsAdapter.replaceData(mapFacetItemClicked(mCurrentTabPosition));
        }
    }

    @Override
    public void replaceCategoryTree(List<GetCategoryTreeResponse> categoryTree) {
        mSubCategoriesAdapter.replaceData(categoryTree);
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

    public void removeChipOnCategories() {
        for (SearchChipModel chip : mSearchItemsList) {
            if (chip.getFilterType().equals(BundleKeys.CATEGORY_TREE_FACET)) {
                mSearchItemsList.remove(chip);
                break;
            }
        }
    }



    @Override
    public void onCategoryClicked(GetCategoryTreeResponse category) {
        //initially clear all keys
//        mCategoryKeys.clear();

        checkParentSelection(category);

        List<GetCategoryTreeResponse> children = mCategoryMap.get(category.getKey()).getChildren();

//        mCategoryKeys.clear();

        if (category.isSelected()) {
            mCategoryKeys.add(category.getKey()); //add to category keys
            if(!children.isEmpty()) { //if i have children{}
                setChildrenSelection(category.getKey(),true);
            }

        }
//        else{
//            //get parent node and children
//            String parentKey = StringUtils.getParentKey(category);
//            GetCategoryTreeResponse parentNode = mCategoryMap.get(parentKey);
//
//            if(!parentKey.equals(category.getKey())){ //we reached end node up.
//                parentNode.setSelected(true);
//                mCategoryKeys.add(parentNode.getKey());
//            }
//        }
        else {
            mCategoryKeys.remove(category.getKey()); //remove to category keys
            if(!children.isEmpty()) { //if i have children{}
                setChildrenSelection(category.getKey(),false);
            }
        }

        printCategoryKeys();
        mSaleItemsPresenter.onCategoryChanged(true);
        mSaleItemsPresenter.loadSaleItems(mSaleItemsView.createSaleItemsRequest(mCategoryKeys, mSaleId, 0, null, ""),
                mSaleItemsView.createSaleItemsRequest(mCategoryKeys, mSaleId, 0, mSearchItemsList, ""));


    }

    private void printCategoryKeys(){
        StringBuilder finalVal = new StringBuilder();
        for(String test:mCategoryKeys){
            finalVal.append(test + "\n");
        }

        Log.d("categoryKeys",finalVal.toString());

    }

    @Override
    public Set<String> getCategoryKeys(){
        return mCategoryKeys;
    }

    private void checkParentSelection(GetCategoryTreeResponse category){
        boolean childrenAreAllSelected = true;

        //get parent node and children
        String parentKey = StringUtils.getParentKey(category);
        GetCategoryTreeResponse parentNode = mCategoryMap.get(parentKey);
        List<GetCategoryTreeResponse> parentNodeChildren = parentNode.getChildren();

        if(parentKey.equals(category.getKey())){ //we reached end node up.
            return;
        }

        for(GetCategoryTreeResponse child : parentNodeChildren){
            if(!child.isSelected()){
                childrenAreAllSelected = false;
            }
        }

        if(childrenAreAllSelected){ //if all parentNode children(siblings of category) are selected, then parent must be selected.
            parentNode.setSelected(true);
            mCategoryKeys.add(parentNode.getKey());
        } else {
            parentNode.setSelected(false);
            mCategoryKeys.remove(parentNode.getKey());
        }

        //recursion
        checkParentSelection(parentNode);
    }

    private void setChildrenSelection(String key, boolean val) {


        List<GetCategoryTreeResponse> children = mCategoryMap.get(key).getChildren();

        for (GetCategoryTreeResponse category : children) {
            category.setSelected(val);
            if (val) {
                mCategoryKeys.add(category.getKey());
            } else {
                mCategoryKeys.remove(category.getKey());
            }
            setChildrenSelection(category.getKey(), val);
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

    private List<GetCategoryTreeResponse> getSubCategoryItems(String categoryKey) {
        return mCategoryMap.get(categoryKey).getChildren();
    }

    private void createCategoryMap(List<GetCategoryTreeResponse> getCategoryTreeResponses) {

        for (GetCategoryTreeResponse category : getCategoryTreeResponses) {
            mCategoryMap.put(category.getKey(), category);
            createCategoryMap(category.getChildren());
        }
    }
}
