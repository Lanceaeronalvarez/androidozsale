package au.com.dealsdirect.ui.controller.saleitems;

import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;
import java.util.Set;

import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsRequest;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
import au.com.dealsdirect.data.network.model.sorting.SortingResponse;
import au.com.dealsdirect.ui.base.MvpView;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;

/**
 * dp Created by Admin on 6/8/17.
 */

public interface SaleItemsMvpView extends MvpView {

    void onLoadSortingFacetsFinished(List<SortingResponse> responseList);

    void showSaleItems(GetSaleItemsResponse getSaleItemsResponse, boolean forFacetCorrection);

    void showWishlist(List<GetSaleItemsResponse.Products> wishlist);

    void updateWishlistWithAddition(String productId);

    void updateWishlistWithRemoval(String productId);

    void refresh();

    void showProductDetails(RecyclerView.ViewHolder viewHolder,
                            int position,
                            String seoIdentifierId,
                            Drawable imagePlaceholderDrawable,
                            String imageUrl,
                            String skuId,
                            String saleId,
                            boolean isFreeDelivery);

    GetSaleItemsRequest createSaleItemsRequest(String categoryKey, int pageNumber, List<SearchChipModel> chipsList);

    GetSaleItemsRequest createSaleItemsRequest(Set<String> categoryKeys, int pageNumber, List<SearchChipModel> chipsList);

    void enableSaleItemsScroll(boolean val);

    Map<String, GetCategoryTreeResponse> getCategoryMap();

    boolean isFromCategories();

    void toggleTabSelection(int tabPos, boolean isTabActive);

    void toggleTabSelection();

}
