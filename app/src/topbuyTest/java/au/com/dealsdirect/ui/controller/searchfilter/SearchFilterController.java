package au.com.dealsdirect.ui.controller.searchfilter;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.support.RouterPagerAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.main.MainCustomViewPager;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsController;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsMvpPresenter;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsMvpView;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchTagsAdapter;
import au.com.dealsdirect.ui.controller.searchfilter.facetfilter.FacetFilterController;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
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
    SaleItemsMvpPresenter<SaleItemsMvpView> mSaleItemsPresenter;

    @BindView(R.id.bottom_sheet)
    RelativeLayout mBottomSheetLayout;
    @BindView(R.id.viewpager)
    MainCustomViewPager mFiltersViewPager;
    SaleItemsController mSaleItemsMvpView;

    boolean initialLoad = false;

    public TabLayout getTabLayout() {
        return mTabLayout;
    }

    @BindView(R.id.sliding_tabs)
    TabLayout mTabLayout;

    TabLayout.OnTabSelectedListener mTabOnSelectedListener;

    List<GetSaleItemsResponse.Facets> mFacets;
    FacetFilterController mBlankController;
    FacetFilterController mBrandsController;
    FacetFilterController mColorsController;
    FacetFilterController mSizesController;
    FacetFilterController mPriceController;

    ArrayList<String> mBrandList = new ArrayList<>();
    ArrayList<String> mSizeList = new ArrayList<>();
    ArrayList<String> mColorList = new ArrayList<>();

    SearchTagsAdapter mSearchTagsAdapter;

    public static SearchFilterController newInstance() {
        return new SearchFilterController(new BundleBuilder(new Bundle())
                .build());
    }

    public SearchFilterController(Bundle args) {
        super(args);
    }

    public void replaceFacets(List<GetSaleItemsResponse.Facets> facets) {
        mFacets = facets;
        parseFacets(mFacets);
        mBrandsController.replaceFacetList(mBrandList);
        mColorsController.replaceFacetList(mColorList);
        mSizesController.replaceFacetList(mSizeList);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_search_filter, container, false);
        getControllerComponent().inject(this);
        mSaleItemsMvpView = (SaleItemsController) GateKeeper.getCurrentControllerOnRouter(mActivity.getSaleItemsRouter());
        mSaleItemsPresenter.onAttach(mSaleItemsMvpView);
        mSearchTagsAdapter = mSaleItemsPresenter.getSearchTagsAdapter();
        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        mActivity.setSearchFilterRouter(getRouter());
        setUp(view);
    }

    @Override
    protected void setUp(View view) {

        if (mFacets != null) {
            parseFacets(mFacets);
        }

        setupTabs();
        setRetainViewMode(RetainViewMode.RETAIN_DETACH);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mSaleItemsPresenter.onDetach();
        super.onDestroyView(view);
    }


    private void setDefaultTabIcons() {

        mTabLayout.getTabAt(0).setIcon(SearchTabIcons[0]).setCustomView(R.layout.tab_icon_layout);

        if (mSearchTagsAdapter.isBrandsActiveFilter()) {
            mTabLayout.getTabAt(1).setIcon(SearchTabIndicatorIcons[1]).setCustomView(R.layout.tab_icon_layout);
        } else {
            mTabLayout.getTabAt(1).setIcon(SearchTabIcons[1]).setCustomView(R.layout.tab_icon_layout);
        }

        if (mSearchTagsAdapter.isColorsActiveFilter()) {
            mTabLayout.getTabAt(2).setIcon(SearchTabIndicatorIcons[2]).setCustomView(R.layout.tab_icon_layout);
        } else {
            mTabLayout.getTabAt(2).setIcon(SearchTabIcons[2]).setCustomView(R.layout.tab_icon_layout);

        }

        if (mSearchTagsAdapter.isSizesActiveFilter()) {
            mTabLayout.getTabAt(3).setIcon(SearchTabIndicatorIcons[3]).setCustomView(R.layout.tab_icon_layout);
        } else {
            mTabLayout.getTabAt(3).setIcon(SearchTabIcons[3]).setCustomView(R.layout.tab_icon_layout);
        }

        if (mSearchTagsAdapter.isPriceActiveFilter()) {
            mTabLayout.getTabAt(4).setIcon(SearchTabIndicatorIcons[4]).setCustomView(R.layout.tab_icon_layout);
        } else {
            mTabLayout.getTabAt(4).setIcon(SearchTabIcons[4]).setCustomView(R.layout.tab_icon_layout);
        }
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
                Log.d("tabtab", "selected is " + tab.getPosition());
                if (tab.getPosition() != 0) {
                    hideKeyboard();
                } else {
                    if (mSaleItemsMvpView.isAttached()) {
                        mSaleItemsPresenter.showKeyboard();
                    }
                }

                if (tab.getIcon() != null) {
                    tab.getIcon().setColorFilter(ContextCompat.getColor(getActivity(), R.color.bluegreen), PorterDuff.Mode.SRC_IN);
                }
//                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                Log.d("tabtab", "unselected is " + tab.getPosition());
                if (tab.getIcon() != null) {
                    tab.getIcon().setColorFilter(ContextCompat.getColor(getActivity(), R.color.filter_icon_default), PorterDuff.Mode.SRC_IN);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

                Log.d("tabtab", "reselected is " + tab.getPosition());
//                if (isKeyboardOpen && tab.getPosition() != 0) {
//                    imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
//                }

            }
        };
        mTabLayout.addOnTabSelectedListener(mTabOnSelectedListener);
        mTabLayout.setupWithViewPager(mFiltersViewPager);


        setDefaultTabIcons();


        initialLoad = true;

    }

    private void setupViewPager() {
        mBlankController = (FacetFilterController) ControllerFactory.getInstance(GateKeeper.Destination.FACET_FILTER);

        mBrandsController = (FacetFilterController) ControllerFactory.getInstance(GateKeeper.Destination.FACET_FILTER, new BundleBuilder(new Bundle())
//                .putStringArrayList(BundleKeys.FACET_PAYLOAD, mBrandList)
                .putString(BundleKeys.FACET_FILTER_TYPE, BundleKeys.BRANDS_FACET_FILTER_TYPE)
                .build());

        mColorsController = (FacetFilterController) ControllerFactory.getInstance(GateKeeper.Destination.FACET_FILTER, new BundleBuilder(new Bundle())
//                .putStringArrayList(BundleKeys.FACET_PAYLOAD, mColorList)
                .putString(BundleKeys.FACET_FILTER_TYPE, BundleKeys.COLOR_FACET_FILTER_TYPE)
                .build());
        mSizesController = (FacetFilterController) ControllerFactory.getInstance(GateKeeper.Destination.FACET_FILTER, new BundleBuilder(new Bundle())
//                .putStringArrayList(BundleKeys.FACET_PAYLOAD, mSizeList)
                .putString(BundleKeys.FACET_FILTER_TYPE, BundleKeys.SIZE_FACET_FILTER_TYPE)
                .build());
        mPriceController = (FacetFilterController) ControllerFactory.getInstance(GateKeeper.Destination.FACET_FILTER, new BundleBuilder(new Bundle())
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

        mFiltersViewPager.setOffscreenPageLimit(4);
        mFiltersViewPager.setAdapter(mViewPagerAdapter);
        mFiltersViewPager.setCurrentItem(1);
        mFiltersViewPager.setMyScroller();

    }

//    @Override
//    public void onDetach(View view) {
//        mTabLayout.addOnTabSelectedListener(null);
//        super.onDetach(view);
//    }

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

    @Override
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

    @Override
    public void onSetActiveDefaultTabIcons(String filterFragmentType) {

        TabLayout.Tab tab = onSetInactiveDefaultTabIcons(filterFragmentType);
        if (tab != null) {
            tab.getIcon().setColorFilter(ContextCompat.getColor(getActivity(), R.color.bluegreen), PorterDuff.Mode.SRC_IN);
        }
    }

    @Override
    public TabLayout.Tab onSetInactiveDefaultTabIcons(String filterFragmentType) {
        TabLayout.Tab selectedTab = null;

        switch (mapFilterTypeToFacetName(filterFragmentType)) {

            case BundleKeys.BRANDS_FACETFILTER_NAME:
                selectedTab = mTabLayout.getTabAt(1);
                if (selectedTab != null) {
                    selectedTab.setIcon(SearchTabIcons[1]);
                }
                break;
            case BundleKeys.COLORS_FACETFILTER_NAME:
                selectedTab = mTabLayout.getTabAt(2);
                if (selectedTab != null) {
                    selectedTab.setIcon(SearchTabIcons[2]);
                }
                break;
            case BundleKeys.SIZES_FACETFILTER_NAME:
                selectedTab = mTabLayout.getTabAt(3);
                if (selectedTab != null) {
                    selectedTab.setIcon(SearchTabIcons[3]);
                }
                break;
            case BundleKeys.PRICE_FACETFILTER_NAME:
                selectedTab = mTabLayout.getTabAt(4);
                if (mTabLayout.getTabAt(4) != null) {
                    selectedTab.setIcon(SearchTabIcons[4]);
                }
                break;
            default:
                break;
        }

        return selectedTab;
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

    @Override
    public void onSelectTabOfFilterType(String facetFilterName) {
        switch (facetFilterName) {
            case BundleKeys.BRANDS_FACETFILTER_NAME:
                mTabLayout.getTabAt(1).select();
                break;
            case BundleKeys.COLORS_FACETFILTER_NAME:
                mTabLayout.getTabAt(2).select();
                break;
            case BundleKeys.SIZES_FACETFILTER_NAME:
                mTabLayout.getTabAt(3).select();
                break;
            case BundleKeys.PRICE_FACETFILTER_NAME:
                mTabLayout.getTabAt(4).select();
                break;
            default:
                mTabLayout.getTabAt(0).select();
                break;
        }
    }

}
