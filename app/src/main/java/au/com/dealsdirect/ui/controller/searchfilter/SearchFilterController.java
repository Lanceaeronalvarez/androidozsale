package au.com.dealsdirect.ui.controller.searchfilter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.google.errorprone.annotations.Var;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
import au.com.dealsdirect.data.network.model.sorting.SortingResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsController;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.FacetItemsAdapter;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.FacetsAdapter;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchTagsAdapter;
import au.com.dealsdirect.ui.custom.CustomRangeSeekbar;
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

public class SearchFilterController extends BaseController implements SearchFilterMvpView {

    public static final String BRANDS_FACETFILTER_NAME = "skus.brandName";
    public static final String SIZES_FACETFILTER_NAME = "skus.attributes.size";
    public static final String COLORS_FACETFILTER_NAME = "color";
    public static final String PRICE_FACETFILTER_NAME = "skus.attributesForFaceting.aud";
    public static final String SEARCH_QUERY_NAME = "search_query";
    public static final String SORT_FACETFILTER_NAME = "sort";
    public static final String KEY_SELECTED_FACETS = "KEY_SELECTED_FACETS";
    public static final String KEY_ORIG_SELECTED = "KEY_ORIG_SELECTED";

    private static final String KEY_FACET_STRING = "KEY_FACET_STRING";
    private static final String KEY_SORTING_STRING = "KEY_SORTING_STRING";

    @Inject
    SearchFilterMvpPresenter<SearchFilterMvpView> mPresenter;

    @BindView(R.id.filters_facets_recyclerview)
    RecyclerView mFacetsRecyclerView;
    @BindView(R.id.filters_facet_items_recyclerview)
    RecyclerView mFacetItemsRecyclerView;
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

    List<GetSaleItemsResponse.Facets> mFacets;
    List<SortingResponse> mSortingFacets = new ArrayList<>();

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

    public static SearchFilterController newInstance(String jsonFacetString, String sortingFacetString, String saleId, String categoryKey, String previouslySelectedFacetIndices, String previousChipFilters) {
        return new SearchFilterController(new BundleBuilder(new Bundle())
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
        mSearchTagsAdapter = new SearchTagsAdapter(getActivity(), mSearchTagsLayoutManager, new ArrayList<SearchChipModel>(), mPresenter, mFacetItemsAdapter, mPreviousSelectedFacetIndices);

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

            mFacetItemsRecyclerView.setVisibility(View.VISIBLE);
            mSeekbarLayout.setVisibility(View.GONE);

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
                return "";
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
            Log.d("selectPosSet", "added");
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
                .putString(KEY_CATEGORY_MAP, mCategoryKey)
                .putString(KEY_SALE_ID,mSaleId)
                .putString(KEY_CHIPS_FILTER,new Gson().toJson(mSearchTagsAdapter.getData()))
                .putString(KEY_SELECTED_FACETS, new Gson().toJson(mPreviousSelectedFacetIndices))
                .build();

        SaleItemsController saleItemsController = (SaleItemsController) getRouter().getControllerWithTag("SaleItemsController");
        saleItemsController.onPassFiltersData(saleItemBundle);

        getActivity().onBackPressed();

    }
}
