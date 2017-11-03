package au.com.dealsdirect.ui.controller.saleitems;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
import au.com.dealsdirect.data.network.model.sorting.SortingResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.JsonUtils;

/**
 * Created by smartwave on 02/11/2017.
 */

public class SaleItemsController extends BaseController implements SaleItemsMvpView {

    @Inject
    SaleItemsMvpPresenter<SaleItemsMvpView> mPresenter;

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

}
