package au.com.dealsdirect.ui.controller.searchfilter;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.support.RouterPagerAdapter;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
import au.com.dealsdirect.data.network.model.sorting.SortingResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.main.MainCustomViewPager;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.FacetsAdapter;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchTagsAdapter;
import au.com.dealsdirect.ui.controller.searchfilter.facetfilter.FacetFilterController;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.JsonUtils;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;


/**
 * Created by smartwave on 20/07/2017.
 */

public class SearchFilterController extends BaseController
        implements SearchFilterMvpView {

    public static final String TAG = SearchFilterController.class.getSimpleName();

    public static final String BRANDS_FACETFILTER_NAME = "skus.brandName";
    public static final String SIZES_FACETFILTER_NAME = "skus.attributes.size";
    public static final String COLORS_FACETFILTER_NAME = "color";
    public static final String PRICE_FACETFILTER_NAME = "skus.attributesForFaceting.aud";
    public static final String SEARCH_QUERY_NAME = "search_query";
    public static final String SORT_FACETFILTER_NAME = "sort";
    public static final String CATEGORY_TREE_FACET = "KEY_CATEGORY_FACET";
    public static final String KEY_SELECTED_FACETS = "KEY_SELECTED_FACETS";
    public static final String KEY_BRAND_LIST = "KEY_BRAND_LIST";
    public static final String KEY_ORIG_SELECTED = "KEY_ORIG_SELECTED";

    private static final String KEY_FACET_STRING = "KEY_FACET_STRING";
    private static final String KEY_CATEGORY_STRING = "KEY_CATEGORY_STRING";
    private static final String KEY_SORTING_STRING = "KEY_SORTING_STRING";
    private static final String KEY_SALE_ITEMS_TITLE = "KEY_SALE_ITEMS_TITLE";

    private int[] SearchTabIcons = {
            R.drawable.filter_inactive,
            R.drawable.brand_inactive,
            R.drawable.color_inactive,
            R.drawable.size_inactive,
            R.drawable.price_inactive
    };

    private int[] SearchTabIndicatorIcons = {
            R.drawable.filter_indicator_inactive,
            R.drawable.brand_indicator_inactive,
            R.drawable.color_indicator_inactive,
            R.drawable.size_indicator_inactive,
            R.drawable.price_indicator_inactive
    };

    @Inject
    SearchFilterMvpPresenter<SearchFilterMvpView> mPresenter;

    @BindView(R.id.bottom_sheet)
    RelativeLayout mBottomSheetLayout;
    @BindView(R.id.viewpager)
    MainCustomViewPager mFiltersViewPager;
    @BindView(R.id.sliding_tabs)
    TabLayout mTabLayout;

    ViewPagerBottomSheetBehavior mBottomSheetBehavior;
    TabLayout.OnTabSelectedListener mTabOnSelectedListener;

    List<GetSaleItemsResponse.Facets> mFacets;
    FacetFilterController mBlankController;
    FacetFilterController mBrandsController;
    FacetFilterController mColorsController;
    FacetFilterController mSizesController;
    FacetFilterController mPriceController;


    FacetsAdapter mFacetsAdapter;
    SearchTagsAdapter mSearchTagsAdapter;

    ArrayList<String> mBrandList = new ArrayList<>();
    ArrayList<String> mSizeList = new ArrayList<>();
    ArrayList<String> mColorList = new ArrayList<>();
    ArrayList<String> mSortingList = new ArrayList<>();

    private Set<Integer> origSelectedSet = new HashSet<Integer>();

    public static SearchFilterController newInstance(String jsonCategoriesString, String jsonFacetString, String sortingFacetString, String saleId, String categoryKey, String previouslySelectedFacetIndices, String previousChipFilters, String saleItemsTitle) {
        return new SearchFilterController(new BundleBuilder(new Bundle())
                .putString(KEY_FACET_STRING, jsonFacetString)
                .build());
    }

    public SearchFilterController(Bundle args) {
        super(args);
        mFacets = JsonUtils.convertStringToObject(args.getString(KEY_FACET_STRING, ""), new TypeToken<ArrayList<GetSaleItemsResponse.Facets>>() {
        }.getType());
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
//        mSearchTagsLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
//        mSearchTagsRecyclerView.setLayoutManager(mSearchTagsLayoutManager);
//        mSearchTagsAdapter = new SearchTagsAdapter(mActivity, mSearchTagsRecyclerView, mSearchTagsLayoutManager, new ArrayList<SearchChipModel>(), mPresenter, mFacetItemsAdapter, mPreviousSelectedFacetIndices);
//        mSearchTagsRecyclerView.setAdapter(mSearchTagsAdapter);
//        mSearchTagsRecyclerView.setVisibility(View.VISIBLE);

        if (mFacets != null) {
            parseFacets(mFacets);
        }

    }


    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();

        super.onDestroyView(view);
    }


    @Override
    public void showFacetItem(int position) {

    }


    @Override
    public void updateFacetItemToFilters(Set<Integer> selectPosSet) {

    }

    @Override
    public Set<Integer> getOriginalSelectedSet() {
        return origSelectedSet;
    }

    @Override
    public void onResetPriceRange() {

    }

    //
    private void setupBottomSheet() {
        BottomSheetUtils.setupViewPager(mFiltersViewPager);
        mBottomSheetBehavior = ViewPagerBottomSheetBehavior.from(mBottomSheetLayout);
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
                        mBottomSheetLayout.requestDisallowInterceptTouchEvent(true);
                        break;

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        // Give the TabLayout the ViewPager
        mTabOnSelectedListener = new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (mBottomSheetBehavior.getState() == ViewPagerBottomSheetBehavior.STATE_EXPANDED) {
//                    if (tab.getPosition() == 0 && !isKeyboardOpen) {
//                        mSearchTagAdapter.getEditTextViewHolder().getEditText().requestFocus();
//                        //                        imm.showSoftInput(SearchTagsRecyclerViewAdapter.editTextViewHolder.et, InputMethodManager.SHOW_FORCED);
//                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//
//                    }
//
//                    if (tab.getPosition() != 0 && isKeyboardOpen) {
//                        //                    imm.hideSoftInputFromWindow(searchEditTextHack.getWindowToken(), 0);
//                        imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
//                    }


                    if (tab.getIcon() != null) {
                        tab.getIcon().setColorFilter(ContextCompat.getColor(getActivity(), R.color.bluegreen), PorterDuff.Mode.SRC_IN);
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getIcon() != null) {
                    tab.getIcon().setColorFilter(ContextCompat.getColor(getActivity(), R.color.filter_icon_default), PorterDuff.Mode.SRC_IN);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
//                if (isKeyboardOpen && tab.getPosition() != 0) {
//                    imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
//                }

            }
        };
        mTabLayout.addOnTabSelectedListener(mTabOnSelectedListener);
        mTabLayout.setupWithViewPager(mFiltersViewPager);
    }

    private String getFacetFilterType(int position) {
        return mFacetsAdapter.getData().get(position).first;
    }

    private List<String> mapFacetItemClicked(int position) {
        String facetFilterType = getFacetFilterType(position);
        switch (facetFilterType) {
            case SORT_FACETFILTER_NAME:
                return mSortingList;
            case CATEGORY_TREE_FACET:
                return new ArrayList<>();
            case BRANDS_FACETFILTER_NAME:
                return mBrandList;
            case SIZES_FACETFILTER_NAME:
                return mSizeList;
            case COLORS_FACETFILTER_NAME:
                return mColorList;
            case PRICE_FACETFILTER_NAME:
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
                        mFacetsAdapter.add(new Pair<String, String>(BRANDS_FACETFILTER_NAME, "Brands"));
                        break;
                    case SIZES_FACETFILTER_NAME:
                        for (int j = 0; j < facets.get(i).getFacetValues().size(); j++) {
                            GetSaleItemsResponse.Values facetValue = facets.get(i).getFacetValues().get(j);
                            mSizeList.add(facetValue.getValue());
                        }
                        mFacetsAdapter.add(new Pair<String, String>(SIZES_FACETFILTER_NAME, "Sizes"));
                        break;
                    case COLORS_FACETFILTER_NAME:
                        for (int j = 0; j < facets.get(i).getFacetValues().size(); j++) {
                            GetSaleItemsResponse.Values facetValue = facets.get(i).getFacetValues().get(j);
                            mColorList.add(facetValue.getValue());
                        }

                        mFacetsAdapter.add(new Pair<String, String>(COLORS_FACETFILTER_NAME, "Colors"));
                        break;
                    case PRICE_FACETFILTER_NAME:
                        mFacetsAdapter.add(new Pair<String, String>(PRICE_FACETFILTER_NAME, "Price"));

                        break;
                    default:
                        break;
                }
            }
        }
    }

    private SearchChipModel findPriceChip() {
        for (SearchChipModel chip : mSearchTagsAdapter.getData()) {
            if (chip.getFilterType().equals(PRICE_FACETFILTER_NAME)) {
                return chip;
            }
        }

        return null;
    }

    private void setupViewPager() {

        RouterPagerAdapter mViewPagerAdapter = new RouterPagerAdapter(this) {
            @Override
            public void configureRouter(@NonNull Router router, int position) {
                if (!router.hasRootController()) {
                    switch (position) {
                        case 0:
                            GateKeeper.setRoot(router, GateKeeper.Destination.FACET_FILTER, RouterTransaction.with(mBlankController));
                            break;
                        case 1:
                            GateKeeper.setRoot(router, GateKeeper.Destination.FACET_FILTER, RouterTransaction.with(mBrandsController));
                            break;
                        case 2:
                            GateKeeper.setRoot(router, GateKeeper.Destination.FACET_FILTER, RouterTransaction.with(mColorsController));
                            break;
                        case 3:
                            GateKeeper.setRoot(router, GateKeeper.Destination.FACET_FILTER, RouterTransaction.with(mSizesController));
                            break;
                        case 4:
                            GateKeeper.setRoot(router, GateKeeper.Destination.FACET_FILTER, RouterTransaction.with(mPriceController));
                            break;
                        default:
                            router.setRoot(RouterTransaction.with(mBlankController));
                            break;
                    }
                }
            }

            @Override
            public int getCount() {
                return 5;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return "Page " + position;
            }
        };


        mFiltersViewPager.setAdapter(mViewPagerAdapter);
        mFiltersViewPager.setCurrentItem(1);
        mFiltersViewPager.setMyScroller();
    }
}
