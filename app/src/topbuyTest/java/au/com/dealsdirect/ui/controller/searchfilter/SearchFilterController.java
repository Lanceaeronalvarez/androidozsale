package au.com.dealsdirect.ui.controller.searchfilter;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bluelinelabs.conductor.Controller;
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
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.JsonUtils;
import au.com.dealsdirect.utils.module.ControllerFactory;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;


/**
 * Created by smartwave on 20/07/2017.
 */

public class SearchFilterController extends BaseController
        implements SearchFilterMvpView {

    public static final String TAG = SearchFilterController.class.getSimpleName();


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

    TabLayout.OnTabSelectedListener mTabOnSelectedListener;

    List<GetSaleItemsResponse.Facets> mFacets;
    Controller mBlankController;
    Controller mBrandsController;
    Controller mColorsController;
    Controller mSizesController;
    Controller mPriceController;

    SearchTagsAdapter mSearchTagsAdapter;

    ArrayList<String> mBrandList = new ArrayList<>();
    ArrayList<String> mSizeList = new ArrayList<>();
    ArrayList<String> mColorList = new ArrayList<>();
    ArrayList<String> mSortingList = new ArrayList<>();

    private Set<Integer> origSelectedSet = new HashSet<Integer>();

    public static SearchFilterController newInstance() {
        return new SearchFilterController(new BundleBuilder(new Bundle())
                .build());
    }

    public SearchFilterController(Bundle args) {
        super(args);
        mFacets = JsonUtils.convertStringToObject(args.getString(BundleKeys.KEY_FACET_STRING, ""), new TypeToken<ArrayList<GetSaleItemsResponse.Facets>>() {
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

        setupTabs();

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

    @Override
    public void showFilters() {

    }

    @Override
    public void hideFilters() {

    }

    private void setDefaultTabIcons() {
        mTabLayout.getTabAt(0).setIcon(SearchTabIcons[0]).setCustomView(R.layout.tab_icon_layout);
        mTabLayout.getTabAt(1).setIcon(SearchTabIcons[1]).setCustomView(R.layout.tab_icon_layout);
        mTabLayout.getTabAt(2).setIcon(SearchTabIcons[2]).setCustomView(R.layout.tab_icon_layout);
        mTabLayout.getTabAt(3).setIcon(SearchTabIcons[3]).setCustomView(R.layout.tab_icon_layout);
        mTabLayout.getTabAt(4).setIcon(SearchTabIcons[4]).setCustomView(R.layout.tab_icon_layout);
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
                    case BundleKeys.PRICE_FACETFILTER_NAME:

                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void setupTabs() {
        BottomSheetUtils.setupViewPager(mFiltersViewPager);
        setupViewPager();
        // Give the TabLayout the ViewPager
        mTabOnSelectedListener = new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

//                if (mBottomSheetBehavior.getState() == ViewPagerBottomSheetBehavior.STATE_EXPANDED) {
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
//                }
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
        setDefaultTabIcons();

    }

    private void setupViewPager() {
        mBlankController = ControllerFactory.getInstance(GateKeeper.Destination.FACET_FILTER);

        mBrandsController = ControllerFactory.getInstance(GateKeeper.Destination.FACET_FILTER, new BundleBuilder(new Bundle())
                .putStringArrayList(BundleKeys.FACET_PAYLOAD, mBrandList)
                .putString(BundleKeys.FACET_FILTER_TYPE, BundleKeys.BRANDS_FACET_FILTER_TYPE)
                .build());

        mColorsController = ControllerFactory.getInstance(GateKeeper.Destination.FACET_FILTER, new BundleBuilder(new Bundle())
                .putStringArrayList(BundleKeys.FACET_PAYLOAD, mColorList)
                .putString(BundleKeys.FACET_FILTER_TYPE, BundleKeys.COLOR_FACET_FILTER_TYPE)
                .build());
        mSizesController = ControllerFactory.getInstance(GateKeeper.Destination.FACET_FILTER, new BundleBuilder(new Bundle())
                .putStringArrayList(BundleKeys.FACET_PAYLOAD, mSizeList)
                .putString(BundleKeys.FACET_FILTER_TYPE, BundleKeys.SIZE_FACET_FILTER_TYPE)
                .build());
        mPriceController = ControllerFactory.getInstance(GateKeeper.Destination.FACET_FILTER, new BundleBuilder(new Bundle())
                .putString(BundleKeys.FACET_FILTER_TYPE, BundleKeys.PRICE_FACET_FILTER_TYPE)
                .build());

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

    public void onSetInactiveTabIndicatorIcons(String filterFragmentType) {
        switch (mapFilterTypeToFacetName(filterFragmentType)) {
            case BundleKeys.BRANDS_FACETFILTER_NAME:
                mTabLayout.getTabAt(1).setIcon(SearchTabIndicatorIcons[1]);
                break;
            case BundleKeys.COLORS_FACETFILTER_NAME:
                mTabLayout.getTabAt(2).setIcon(SearchTabIndicatorIcons[2]);
                break;
            case BundleKeys.SIZES_FACETFILTER_NAME:
                mTabLayout.getTabAt(3).setIcon(SearchTabIndicatorIcons[3]);
                break;
            case BundleKeys.PRICE_FACETFILTER_NAME:
                mTabLayout.getTabAt(4).setIcon(SearchTabIndicatorIcons[4]);
                break;
            default:
                break;
        }
    }

    public void onSetActiveTabIndicatorIcons(String filterFragmentType) {
        switch (mapFilterTypeToFacetName(filterFragmentType)) {
            case BundleKeys.BRANDS_FACETFILTER_NAME:
                if (mTabLayout.getTabAt(1) != null) {
                    mTabLayout.getTabAt(1).setIcon(SearchTabIndicatorIcons[1]);
                    mTabLayout.getTabAt(1).getIcon().setColorFilter(ContextCompat.getColor(getActivity(), R.color.bluegreen), PorterDuff.Mode.SRC_IN);
                }
                break;
            case BundleKeys.COLORS_FACETFILTER_NAME:
                if (mTabLayout.getTabAt(2) != null) {
                    mTabLayout.getTabAt(2).setIcon(SearchTabIndicatorIcons[2]);
                    mTabLayout.getTabAt(2).getIcon().setColorFilter(ContextCompat.getColor(getActivity(), R.color.bluegreen), PorterDuff.Mode.SRC_IN);
                }
                break;
            case BundleKeys.SIZES_FACETFILTER_NAME:
                if (mTabLayout.getTabAt(3) != null) {
                    mTabLayout.getTabAt(3).setIcon(SearchTabIndicatorIcons[3]);
                    mTabLayout.getTabAt(3).getIcon().setColorFilter(ContextCompat.getColor(getActivity(), R.color.bluegreen), PorterDuff.Mode.SRC_IN);
                }
                break;
            case BundleKeys.PRICE_FACETFILTER_NAME:
                if (mTabLayout.getTabAt(4) != null) {
                    mTabLayout.getTabAt(4).setIcon(SearchTabIndicatorIcons[4]);
                    mTabLayout.getTabAt(4).getIcon().setColorFilter(ContextCompat.getColor(getActivity(), R.color.bluegreen), PorterDuff.Mode.SRC_IN);
                }
                break;
            default:
                break;
        }

    }

    public void onSetActiveDefaultTabIcons(String filterFragmentType) {
        switch (mapFilterTypeToFacetName(filterFragmentType)) {
            case BundleKeys.BRANDS_FACETFILTER_NAME:
                if (mTabLayout.getTabAt(1) != null) {
                    mTabLayout.getTabAt(1).setIcon(SearchTabIcons[1]);
                    mTabLayout.getTabAt(1).getIcon().setColorFilter(ContextCompat.getColor(getActivity(), R.color.bluegreen), PorterDuff.Mode.SRC_IN);
                }
                break;
            case BundleKeys.COLORS_FACETFILTER_NAME:
                if (mTabLayout.getTabAt(2) != null) {
                    mTabLayout.getTabAt(2).setIcon(SearchTabIcons[2]);
                    mTabLayout.getTabAt(2).getIcon().setColorFilter(ContextCompat.getColor(getActivity(), R.color.bluegreen), PorterDuff.Mode.SRC_IN);
                }
                break;
            case BundleKeys.SIZES_FACETFILTER_NAME:
                if (mTabLayout.getTabAt(3) != null) {
                    mTabLayout.getTabAt(3).setIcon(SearchTabIcons[3]);
                    mTabLayout.getTabAt(3).getIcon().setColorFilter(ContextCompat.getColor(getActivity(), R.color.bluegreen), PorterDuff.Mode.SRC_IN);
                }
                break;
            case BundleKeys.PRICE_FACETFILTER_NAME:
                if (mTabLayout.getTabAt(4) != null) {
                    mTabLayout.getTabAt(4).setIcon(SearchTabIcons[4]);
                    mTabLayout.getTabAt(4).getIcon().setColorFilter(ContextCompat.getColor(getActivity(), R.color.bluegreen), PorterDuff.Mode.SRC_IN);
                }
                break;
            default:
                break;
        }
    }

    public void onShowTabOfSelectedChip(String filterFragmentType) {
        switch (mapFilterTypeToFacetName(filterFragmentType)) {
            case BundleKeys.BRANDS_FACETFILTER_NAME:
                if (mTabLayout.getTabAt(1) != null) mTabLayout.getTabAt(1).select();
                break;
            case BundleKeys.COLORS_FACETFILTER_NAME:
                if (mTabLayout.getTabAt(2) != null) mTabLayout.getTabAt(2).select();
                break;
            case BundleKeys.SIZES_FACETFILTER_NAME:
                if (mTabLayout.getTabAt(3) != null) mTabLayout.getTabAt(3).select();
                break;
            case BundleKeys.PRICE_FACETFILTER_NAME:
                if (mTabLayout.getTabAt(4) != null) mTabLayout.getTabAt(4).select();
                break;
            default:
                break;
        }
    }

    private String mapFilterTypeToFacetName(String fragmentType) {
        String value = "";
        switch (fragmentType) {
            case BundleKeys.BRANDS_FACET_FILTER_TYPE:
                value = BundleKeys.BRANDS_FACETFILTER_NAME;
                break;
            case BundleKeys.SIZE_FACET_FILTER_TYPE:
                value = BundleKeys.SIZES_FACETFILTER_NAME;
                break;
            case BundleKeys.COLOR_FACET_FILTER_TYPE:
                value = BundleKeys.COLORS_FACETFILTER_NAME;
                break;
            case BundleKeys.PRICE_FACET_FILTER_TYPE:
                value = BundleKeys.PRICE_FACETFILTER_NAME;
                break;
            default:
                break;
        }

        return value;
    }
}
