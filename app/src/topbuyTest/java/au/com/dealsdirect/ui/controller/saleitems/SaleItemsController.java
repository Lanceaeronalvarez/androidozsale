package au.com.dealsdirect.ui.controller.saleitems;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsRequest;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
import au.com.dealsdirect.data.network.model.sorting.SortingResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.saleitems.adapter.SaleItemsAdapter;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.JsonUtils;
import butterknife.BindView;

import static au.com.dealsdirect.ui.controller.searchfilter.SearchFilterController.PRICE_FACETFILTER_NAME;

/**
 * Created by smartwave on 02/11/2017.
 */

public class SaleItemsController extends BaseController implements SaleItemsMvpView {

    @Inject
    SaleItemsMvpPresenter<SaleItemsMvpView> mPresenter;

    @BindView(R.id.sale_items_recyclerview)
    RecyclerView mSaleItemsRecyclerView;

    private int page = 0;

    public static SaleItemsController newInstance(){
        return new SaleItemsController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public SaleItemsController(Bundle args) {
        super(args);

//        if (args.containsKey(KEY_SALE_ID))
//            mSaleId = getArgs().getString(KEY_SALE_ID, "");
//        if (args.containsKey(KEY_CATEGORY_MAP))
//            mCategoryKey = getArgs().getString(KEY_CATEGORY_MAP, "");
//        if (args.containsKey(KEY_CHIPS_FILTER))
//            mChipFilters = JsonUtils.convertStringToObject(getArgs().getString(KEY_CHIPS_FILTER, ""), new TypeToken<ArrayList<SearchChipModel>>() {
//            }.getType());
//        if (args.containsKey(KEY_FROM_SHOP_SEARCH))
//            mFromShopSearch = getArgs().getBoolean(KEY_FROM_SHOP_SEARCH, true);
//        if (args.containsKey(KEY_FROM_CATEGORY_SEARCH))
//            mFromCategorySearch = getArgs().getBoolean(KEY_FROM_CATEGORY_SEARCH, true);
//        if (args.containsKey(KEY_FROM_CATEGORIES)) {
//            mIsFromCategory = getArgs().getBoolean(KEY_FROM_CATEGORIES, true);
//        }
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_sale_items, container, false);

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        mActivity.setSaleItemsRouter(getRouter());
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        mPresenter.loadSaleItems(createSaleItemsRequest("","",0,new ArrayList<>()));
    }

    @Override
    public void onLoadSortingFacetsFinished(List<SortingResponse> responseList) {

    }

    @Override
    public void showSaleItems(GetSaleItemsResponse getSaleItemsResponse, boolean forFacetCorrection) {

    }

    @Override
    public void refresh() {

    }

    @Override
    public void showProductDetails(RecyclerView.ViewHolder viewHolder, int position, String seoIdentifierId, String imageUrl, String skuId, String saleId) {

    }

    @Override
    public void unbindPaginate() {

    }

    @Override
    public void onPassFiltersData(Bundle bundle) {

    }

    private GetSaleItemsRequest createSaleItemsRequest(String categoryKey, String saleId, int pageNumber, List<SearchChipModel> chipsList) {
        List<String> saleIds = new LinkedList<>();
        HashMap<String, List<String>> facetFilters = new HashMap<>();

        GetSaleItemsRequest getSaleItemsRequest = new GetSaleItemsRequest();

        if (!categoryKey.isEmpty())
            getSaleItemsRequest.setCategoryKey("[\"" + categoryKey + "\"]");
        else
            getSaleItemsRequest.setCategoryKey("[]");


        getSaleItemsRequest.setSorting("");
        getSaleItemsRequest.setPageNumber(String.valueOf(pageNumber));
        getSaleItemsRequest.setQuery("");

        getSaleItemsRequest.setPageSize("50");

        if (saleId != null) {

            if (!saleId.isEmpty())
                saleIds.add(saleId);
        }


        if (!saleIds.isEmpty()) {
            facetFilters.put("saleId", saleIds);
        }

        if (chipsList == null) {
            getSaleItemsRequest.setHasFilters(false);
        } else {
            if (chipsList.size() != 0) {
                ArrayList<String> searchQueryFilters = new ArrayList<>();
                ArrayList<String> brandNameFacetFilters = new ArrayList<>();
                ArrayList<String> colorFacetFilters = new ArrayList<>();
                ArrayList<String> sizesFacetFilters = new ArrayList<>();
                ArrayList<String> priceFacetFilters = new ArrayList<>();

                for (SearchChipModel chip : chipsList) {
                    String facetName = chip.getFilterType();
                    if (facetName.equals(BundleKeys.BRANDS_FACETFILTER_NAME)) {
                        brandNameFacetFilters.add(chip.getChipTitle());
                    } else if (facetName.equals(BundleKeys.COLORS_FACETFILTER_NAME)) {
                        colorFacetFilters.add(chip.getChipTitle());
                    } else if (facetName.equals(BundleKeys.SIZES_FACETFILTER_NAME)) {
                        sizesFacetFilters.add(chip.getChipTitle());
                    } else if (facetName.equals(BundleKeys.PRICE_FACETFILTER_NAME)) {
                        priceFacetFilters.add(chip.getChipTitle());
                    } else if (facetName.equals(BundleKeys.SEARCH_QUERY_NAME)) {
                        searchQueryFilters.add(chip.getChipTitle());
                    } else if (facetName.equals(BundleKeys.SORT_FACETFILTER_NAME)) {
//                        getSaleItemsRequest.setSorting(mapSortingTitleToKey(chip.getChipTitle()));
                    }
                }

                facetFilters.put(BundleKeys.BRANDS_FACETFILTER_NAME, brandNameFacetFilters);
                facetFilters.put(BundleKeys.COLORS_FACETFILTER_NAME, colorFacetFilters);
                facetFilters.put(BundleKeys.SIZES_FACETFILTER_NAME, sizesFacetFilters);
                facetFilters.put(BundleKeys.PRICE_FACETFILTER_NAME, priceFacetFilters);


                if (searchQueryFilters.size() != 0) {
                    StringBuilder result = new StringBuilder();
                    for (int i = 0; i < searchQueryFilters.size(); i++) {
                        if (i > 0) {
                            result.append(" ");
                        }
                        result.append(searchQueryFilters.get(i));
                    }

                    getSaleItemsRequest.setQuery(result.toString());
                }
            }

            getSaleItemsRequest.setHasFilters(true);
        }

        String facetFiltersString = new Gson().toJson(facetFilters);

        getSaleItemsRequest.setFacetFilter(facetFiltersString);

        page = pageNumber;

        return getSaleItemsRequest;
    }
}
