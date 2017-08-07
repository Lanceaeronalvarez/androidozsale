package au.com.dealsdirect.ui.controller.searchfilter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
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
import au.com.dealsdirect.ui.controller.categories.listener.SubCategoryClickListener;
import au.com.dealsdirect.ui.controller.categories.listener.SubCategoryItemClickListener;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsController;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.FacetItemsAdapter;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.FacetsAdapter;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchTagsAdapter;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SubCategoriesAdapter;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SubCategoryItemsAdapter;
import au.com.dealsdirect.ui.custom.CustomRangeSeekbar;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.JsonUtils;
import butterknife.BindView;
import butterknife.OnClick;

import static au.com.dealsdirect.ui.controller.saleitems.SaleItemsController.KEY_CATEGORY_MAP;
import static au.com.dealsdirect.ui.controller.saleitems.SaleItemsController.KEY_CHIPS_FILTER;
import static au.com.dealsdirect.ui.controller.saleitems.SaleItemsController.KEY_SALE_ID;

/**
 * Created by smartwave on 20/07/2017.
 */

public class SearchFilterController extends BaseController
        implements SearchFilterMvpView, SubCategoryClickListener, SubCategoryItemClickListener {

    public static final String BRANDS_FACETFILTER_NAME = "skus.brandName";
    public static final String SIZES_FACETFILTER_NAME = "skus.attributes.size";
    public static final String COLORS_FACETFILTER_NAME = "color";
    public static final String PRICE_FACETFILTER_NAME = "skus.attributesForFaceting.aud";
    public static final String SEARCH_QUERY_NAME = "search_query";
    public static final String SORT_FACETFILTER_NAME = "sort";
    public static final String CATEGORY_TREE_FACET = "KEY_CATEGORY_FACET";
    public static final String KEY_SELECTED_FACETS = "KEY_SELECTED_FACETS";
    public static final String KEY_ORIG_SELECTED = "KEY_ORIG_SELECTED";

    private static final String KEY_FACET_STRING = "KEY_FACET_STRING";
    private static final String KEY_CATEGORY_STRING = "KEY_CATEGORY_STRING";
    private static final String KEY_SORTING_STRING = "KEY_SORTING_STRING";

    @Inject
    SearchFilterMvpPresenter<SearchFilterMvpView> mPresenter;

    @BindView(R.id.filters_facets_recyclerview)
    RecyclerView mFacetsRecyclerView;
    @BindView(R.id.filters_facet_items_recyclerview)
    RecyclerView mFacetItemsRecyclerView;
    @BindView(R.id.filters_categories_recyclerview)
    RecyclerView mFilterCategoriesRecyclerView;

    @BindView(R.id.partial_filters_search_tags_recyclerview)
    RecyclerView mSearchTagsRecyclerView;

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

    @BindView(R.id.partial_toolbar_search_right_option)
    ImageButton mSearchApplyButton;

    SubCategoryClickListener mSubCategoryClickListener;
    SubCategoryItemClickListener mSubCategoryItemClickListener;

    List<GetSaleItemsResponse.Facets> mFacets;
    List<GetCategoryTreeResponse> mCategoryTree;
    List<SortingResponse> mSortingFacets = new ArrayList<>();
    private Map<String, List<GetCategoryTreeResponse>> mCategoryMap = new HashMap<>();
    private List<GetCategoryTreeResponse> mPreLoadedCategories = new LinkedList<>();

    SubCategoriesAdapter mSubCategoriesAdapter;
    SubCategoryItemsAdapter mSubCategoryItemAdapter;
    FacetsAdapter mFacetsAdapter;
    FacetItemsAdapter mFacetItemsAdapter;
    SearchTagsAdapter mSearchTagsAdapter;
    LinearLayoutManager mSearchTagsLayoutManager;

    ArrayList<String> mBrandList = new ArrayList<>();
    ArrayList<String> mSizeList = new ArrayList<>();
    ArrayList<String> mColorList = new ArrayList<>();
    ArrayList<String> mSortingList = new ArrayList<>();

    private int origMinValue = -1;
    private int origMaxValue = -1;
    private boolean isSeekbarReset = false;

    private String mChosenCategory = "";

    String mCategoryKey = "";
    String mSaleId = "";


    private Set<Integer> origSelectedSet = new HashSet<Integer>();

    private int mPreviousSelectedFacetIndex = -1;

    private HashMap<String, Set<Integer>> mPreviousSelectedFacetIndices = new HashMap<>();
    private ArrayList<SearchChipModel> mPreviousSearchChips = new ArrayList<>();

    List<String> mFacetFilters = Arrays.asList
            ("Sort",
                    "Category",
                    "Brands",
                    "Sizes",
                    "Colors",
                    "Price");

    public static SearchFilterController newInstance(String jsonCategoriesString, String jsonFacetString, String sortingFacetString, String saleId, String categoryKey, String previouslySelectedFacetIndices, String previousChipFilters) {
        return new SearchFilterController(new BundleBuilder(new Bundle())
                .putString(KEY_CATEGORY_STRING, jsonCategoriesString)
                .putString(KEY_FACET_STRING, jsonFacetString)
                .putString(KEY_SORTING_STRING, sortingFacetString)
                .putString(KEY_CATEGORY_MAP, categoryKey)
                .putString(KEY_SALE_ID,saleId)
                .putString(KEY_SELECTED_FACETS,previouslySelectedFacetIndices)
                .putString(KEY_CHIPS_FILTER,previousChipFilters)
                .build());
    }

    public SearchFilterController(Bundle args) {
        super(args);
        mFacets = JsonUtils.convertStringToObject(args.getString(KEY_FACET_STRING, ""), new TypeToken<ArrayList<GetSaleItemsResponse.Facets>>() {
        }.getType());
        mSortingFacets = JsonUtils.convertStringToObject(args.getString(KEY_SORTING_STRING, ""), new TypeToken<ArrayList<SortingResponse>>() {
        }.getType());
        mSaleId = args.getString(KEY_SALE_ID,"");
        mCategoryKey = args.getString(KEY_CATEGORY_MAP,"");
        mCategoryTree = JsonUtils.convertStringToObject(args.getString(KEY_CATEGORY_STRING, ""), new TypeToken<ArrayList<GetCategoryTreeResponse>>() {
        }.getType());

        restoreStateSelection(args);
    }


    private void restoreStateSelection(Bundle args){
        String selectedFacetItemsString = args.getString(KEY_SELECTED_FACETS,"");
        if(!selectedFacetItemsString.isEmpty()) {
            mPreviousSelectedFacetIndices = JsonUtils.convertStringToObject(selectedFacetItemsString, new TypeToken<HashMap<String,Set<Integer>>>(){}.getType());
        } else{
            mPreviousSelectedFacetIndices = new HashMap<>();
        }

        String previousChipsString = args.getString(KEY_CHIPS_FILTER,"");
        if(!previousChipsString.isEmpty()) {
            mPreviousSearchChips = JsonUtils.convertStringToObject(previousChipsString, new TypeToken<ArrayList<SearchChipModel>>(){}.getType());
        } else{
            mPreviousSearchChips = new ArrayList<>();
        }

        if (!mCategoryKey.isEmpty())
            mChosenCategory = mCategoryKey;
        else
            mChosenCategory = "";
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_search_filter, container, false);
        getControllerComponent().inject(this);
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
        origMaxValue = mPresenter.getSearchMaxPrice();
        origMinValue = mSeekbar.getSelectedMinValue().intValue();

        mSubCategoryClickListener = this;
        mSubCategoryItemClickListener = this;

        createCategoryMap(mCategoryTree);

        mSeekbar.setMaxValue(origMaxValue);
        mSeekbar.setMaxStartValue(origMaxValue);
        mClearText.setOnClickListener((v)->{
            if(!isSeekbarReset) {
                //remove previously selected price range
                for (SearchChipModel chip : mSearchTagsAdapter.getData()) {
                    if (chip.getFilterType() == PRICE_FACETFILTER_NAME) {
                        mSearchTagsAdapter.remove(chip);
                        break;
                    }
                }

                onResetPriceRange();
            }
        });
        mSeekbar.setMinPriceMovingLayout(mMinPriceMovingLayout);
        mSeekbar.setMaxPriceMovingLayout(mMaxPriceMovingLayout);
        mSeekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                mMinPrice.setText("$"+minValue.intValue());
                mMaxPrice.setText("$"+maxValue.intValue());
                if(maxValue.intValue() == origMaxValue){
                    mMaxPrice.setText("$"+maxValue.intValue()+"+");
                }

            }
        });
        mSeekbar.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue) {

                //remove previously selected price range
                for (SearchChipModel chip : mSearchTagsAdapter.getData()) {
                    if(chip.getFilterType() == PRICE_FACETFILTER_NAME){
                        mSearchTagsAdapter.remove(chip);
                        break;
                    }
                }

                //add newly selected price range
                if(origMinValue!=minValue.intValue() || origMaxValue!=maxValue.intValue()){
                    mSearchTagsAdapter.add(new SearchChipModel(PRICE_FACETFILTER_NAME, minValue.intValue() + " to " + maxValue.intValue(),-1));
                }

                isSeekbarReset=false;
            }
        });


        mSearchApplyButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));

        mFacetsAdapter = new FacetsAdapter(getActivity(), mFacetFilters, mPresenter);
        mFacetsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mFacetsRecyclerView.setAdapter(mFacetsAdapter);

        mSubCategoriesAdapter = new SubCategoriesAdapter(mChosenCategory, mPreLoadedCategories, mSubCategoryClickListener,mSubCategoryItemClickListener, mCategoryMap);
        mFilterCategoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mFilterCategoriesRecyclerView.setAdapter(mSubCategoriesAdapter);

        mFacetItemsAdapter = new FacetItemsAdapter(new ArrayList<>(), mPresenter, new HashSet<Integer>());
        mFacetItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mFacetItemsRecyclerView.setAdapter(mFacetItemsAdapter);
        mFacetItemsAdapter.setOnSelectListener(new FacetItemsAdapter.OnSelectListener() {
            @Override
            public void onSelected(Set<Integer> selectPosSet) {
                mPresenter.onFacetItemClicked(selectPosSet);
            }
        });


        mSearchTagsLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mSearchTagsRecyclerView.setLayoutManager(mSearchTagsLayoutManager);
        mSearchTagsAdapter = new SearchTagsAdapter(getActivity(), mSearchTagsRecyclerView, mSearchTagsLayoutManager, new ArrayList<SearchChipModel>(), mPresenter, mFacetItemsAdapter, mPreviousSelectedFacetIndices);
        mSearchTagsRecyclerView.setAdapter(mSearchTagsAdapter);
        mSearchTagsRecyclerView.setVisibility(View.VISIBLE);

        if(!mPreviousSearchChips.isEmpty()) {
            mSearchTagsAdapter.replaceData(mPreviousSearchChips);
        }

        if (mFacets != null) {
            parseFacets(mFacets);
        }

        if(mSortingFacets != null){
            parseSortingFacets(mSortingFacets);
        }

    }

    @Override
    public void onDetach(View view) {
        mPresenter.onDetach();
        super.onDetach(view);
    }

    private void parseSortingFacets(List<SortingResponse> sortingList) {
        for (SortingResponse response: sortingList) {
            mSortingList.add(response.getTitle());
        }
    }

    @Override
    public void showFacetItem(int position) {

        if(mapFacetFilterType(position) != PRICE_FACETFILTER_NAME) { //only do this logic if facet clicked != price

            if (mapFacetFilterType(position) == CATEGORY_TREE_FACET){

                mFilterCategoriesRecyclerView.setVisibility(View.VISIBLE);
                mFacetItemsRecyclerView.setVisibility(View.GONE);
                mSeekbarLayout.setVisibility(View.GONE);

            }else{

                mFacetItemsRecyclerView.setVisibility(View.VISIBLE);
                mSeekbarLayout.setVisibility(View.GONE);
                mFilterCategoriesRecyclerView.setVisibility(View.GONE);
            }

            if (mPreviousSelectedFacetIndex != -1) {
                mPreviousSelectedFacetIndices.put(mapFacetFilterType(mPreviousSelectedFacetIndex), new HashSet<>(mFacetItemsAdapter.getSelectedFacets()));
            }

            if (position != mPreviousSelectedFacetIndex) {
                mFacetItemsAdapter.clearSelectedFacets();
                origSelectedSet.clear();
            }

            if (mPreviousSelectedFacetIndices.get(mapFacetFilterType(position)) != null) {
                mFacetItemsAdapter.updateSelectedFacets(mPreviousSelectedFacetIndices.get(mapFacetFilterType(position)));
                origSelectedSet = mPreviousSelectedFacetIndices.get(mapFacetFilterType(position));
            }

        } else { //price is clicked
            mFacetItemsRecyclerView.setVisibility(View.GONE);
            mSeekbarLayout.setVisibility(View.VISIBLE);
        }

        mFacetItemsAdapter.setFilterType(mapFacetFilterType(position));
        mFacetItemsAdapter.replaceData(mapFacetItemClicked(position));

        mPreviousSelectedFacetIndex = position;
    }

    private void trackLastSelectedFacet(){
        mPreviousSelectedFacetIndices.put(mapFacetFilterType(mPreviousSelectedFacetIndex), new HashSet<>(mFacetItemsAdapter.getSelectedFacets()));
    }


    private String mapFacetFilterType(int position){
        switch (position) {
            case 0:
                return SORT_FACETFILTER_NAME;
            case 1:
                return CATEGORY_TREE_FACET;
            case 2:
                return BRANDS_FACETFILTER_NAME;
            case 3:
                return SIZES_FACETFILTER_NAME;
            case 4:
                return COLORS_FACETFILTER_NAME;
            case 5:
                return PRICE_FACETFILTER_NAME;
            default:
                return "";
        }
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
            mSearchTagsAdapter.add(newChip);
        } else if (!oldSet.isEmpty()) {
            List<Integer> temp = new ArrayList(oldSet);
            SearchChipModel chipToRemove = null;
            for (SearchChipModel chip : mSearchTagsAdapter.getData()) {
                if (chip.getChipTitle().equals(mFacetItemsAdapter.getData().get(temp.get(0)))) {
                    chipToRemove = chip;
                }
            }

            if (chipToRemove != null) {
                mSearchTagsAdapter.remove(chipToRemove);
            }
        }

    }

    @Override
    public Set<Integer> getOriginalSelectedSet() {
        return origSelectedSet;
    }

    @Override
    public void onResetPriceRange() {
        mSeekbar.setMinValue(origMinValue);
        mSeekbar.setMaxValue(origMaxValue);

        mSeekbar.apply();
        mMinPriceMovingLayout.setTranslationX(0);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mSeekbar.getLayoutParams();
        mMaxPriceMovingLayout.setX(mSeekbar.getWidth() - (lp.rightMargin));

        isSeekbarReset = true;
    }

    @Override
    public void categoryChipRemoved() {
        Log.d("removechip", "oncategorychipremoved");
        mChosenCategory = "";
    }

    private List<String> mapFacetItemClicked(int position) {
        switch (position) {
            case 0:
                return mSortingList;
            case 1:
                return new ArrayList<>();
            case 2:
                return mBrandList;
            case 3:
                return mSizeList;
            case 4:
                return mColorList;
            case 5:
                return new ArrayList<>();
            default:
                return new ArrayList<>();
        }
    }

    private void parseFacets(List<GetSaleItemsResponse.Facets> facets) {
        if (facets.size() != 0) {
            mBrandList = new ArrayList<>();
            mSizeList = new ArrayList<>();
            mColorList = new ArrayList<>();
            for (int i = 0; i < facets.size(); i++) {
                switch (facets.get(i).getFacetName()) {
                    case BRANDS_FACETFILTER_NAME:
                        for (int j = 0; j < facets.get(i).getFacetValues().size(); j++) {
                            GetSaleItemsResponse.Values facetValue = facets.get(i).getFacetValues().get(j);
                            mBrandList.add(facetValue.getValue());
                        }
                        break;
                    case SIZES_FACETFILTER_NAME:
                        for (int j = 0; j < facets.get(i).getFacetValues().size(); j++) {
                            GetSaleItemsResponse.Values facetValue = facets.get(i).getFacetValues().get(j);
                            mSizeList.add(facetValue.getValue());
                        }
                        break;
                    case COLORS_FACETFILTER_NAME:
                        for (int j = 0; j < facets.get(i).getFacetValues().size(); j++) {
                            GetSaleItemsResponse.Values facetValue = facets.get(i).getFacetValues().get(j);
                            mColorList.add(facetValue.getValue());
                        }
                        break;
                    case PRICE_FACETFILTER_NAME:
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @OnClick(R.id.partial_toolbar_search_right_option)
    void applyFilters(){

        trackLastSelectedFacet();

        Bundle saleItemBundle = new BundleBuilder(new Bundle())
                .putString(KEY_CATEGORY_MAP, mChosenCategory)
                .putString(KEY_SALE_ID,mSaleId)
                .putString(KEY_CHIPS_FILTER,new Gson().toJson(mSearchTagsAdapter.getData()))
                .putString(KEY_SELECTED_FACETS, new Gson().toJson(mPreviousSelectedFacetIndices))
                .build();
        
        SaleItemsController saleItemsController = (SaleItemsController) getRouter().getControllerWithTag("SaleItemsController");
        saleItemsController.onPassFiltersData(saleItemBundle);

        //noinspection ConstantConditions
        getActivity().onBackPressed();
    }


    private void createCategoryMap(List<GetCategoryTreeResponse> categories) {

        List<GetCategoryTreeResponse> newList;
        mCategoryMap.put("shop", categories);

        for (GetCategoryTreeResponse i : categories) {

            newList = updateCategoryChildren(i);

            if (newList != null) {

                int childrenSize = newList.size();
                if (childrenSize != 1) {

                    addToMap(newList);
                }

                mCategoryMap.put(i.getKey(), newList);
            }
        }

        mPreLoadedCategories = fillCategoryContent();
    }

    private List<GetCategoryTreeResponse> updateCategoryChildren(GetCategoryTreeResponse categoryTree){
        if (!categoryTree.getName().equals("All")) {

            List<GetCategoryTreeResponse> newList = new ArrayList<>();

            if (categoryTree.getChildren()!=null){
                for (int i=0;i <categoryTree.getChildren().size();i++){
                    newList.add(categoryTree.getChildren().get(i));

                }
            }
            return newList;
        }
        return categoryTree.getChildren();
    }

    private void addToMap(List<GetCategoryTreeResponse> list) {
        List<GetCategoryTreeResponse> newList2;

        for (GetCategoryTreeResponse i : list) {
            newList2 = updateCategoryChildren(i);

            int childrenSize = newList2.size();
            if (childrenSize != 1) {
                addToMap(newList2);
            }

            mCategoryMap.put(i.getKey(), newList2);

        }
    }

    private List<GetCategoryTreeResponse> fillCategoryContent() {

        return mCategoryMap.get("shop");
    }

    @Override
    public void onSubCategoryItemClicked(String categoryID, String categoryName, String categoryKey) {

        mSubCategoriesAdapter.setActiveCategoryKey(categoryKey);
        updateSubCategoryOnSearchTagAdapter(categoryName,categoryKey);
    }

    @Override
    public void onSubCategoryClicked(GetCategoryTreeResponse getCategoryTreeResponse) {
        mSubCategoriesAdapter.setActiveCategoryKey(getCategoryTreeResponse.getKey());

        updateSubCategoryOnSearchTagAdapter(
                getCategoryTreeResponse.getName(),
                getCategoryTreeResponse.getKey());
    }

    public void removeChipOnCategories(){

        for (SearchChipModel chip : mSearchTagsAdapter.getData()) {
            if (chip.getFilterType().equals(CATEGORY_TREE_FACET)) {
                mSearchTagsAdapter.remove(chip);
                break;
            }
        }
    }

    public void updateSubCategoryOnSearchTagAdapter(String categoryName, String categoryKey){
        SearchChipModel categoryChip = new SearchChipModel(CATEGORY_TREE_FACET, categoryName, 0);
        if (!mChosenCategory.isEmpty() && mChosenCategory.equals(categoryKey)){
            removeChipOnCategories();
            mChosenCategory = "";

        }else{

            removeChipOnCategories();

            SearchTagsAdapter searchTagsAdapter = (SearchTagsAdapter) mSearchTagsRecyclerView.getAdapter();
            searchTagsAdapter.add(categoryChip);
            mChosenCategory = categoryKey;
        }
    }
}
