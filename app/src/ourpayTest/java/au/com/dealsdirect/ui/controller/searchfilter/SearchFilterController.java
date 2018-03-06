package au.com.dealsdirect.ui.controller.searchfilter;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsController;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsMvpPresenter;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsMvpView;
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



    @Inject
    SaleItemsMvpPresenter<SaleItemsMvpView> mSaleItemsPresenter;

    SaleItemsController mSaleItemsMvpView;

    boolean initialLoad = false;


    List<GetSaleItemsResponse.Facets> mFacets;
//    FacetFilterController mBlankController;
//    FacetFilterController mBrandsController;
//    FacetFilterController mColorsController;
//    FacetFilterController mSizesController;
//    FacetFilterController mPriceController;

    ArrayList<String> mBrandList = new ArrayList<>();
    ArrayList<String> mSizeList = new ArrayList<>();
    ArrayList<String> mColorList = new ArrayList<>();

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
//        mBrandsController.replaceFacetList(mBrandList);
//        mColorsController.replaceFacetList(mColorList);
//        mSizesController.replaceFacetList(mSizeList);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_search_filter, container, false);
        getControllerComponent().inject(this);
        mSaleItemsMvpView = (SaleItemsController) GateKeeper.getCurrentControllerOnRouter(mActivity.getHomeRouter());
        mSaleItemsPresenter.onAttach(mSaleItemsMvpView);
        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {

        if (mFacets != null) {
            parseFacets(mFacets);
        }

        setRetainViewMode(RetainViewMode.RETAIN_DETACH);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mSaleItemsPresenter.onDetach();
        super.onDestroyView(view);
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


    @Override
    public void onSetActiveTabIndicatorIcons(String facetFilterType) {

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

    }

}
