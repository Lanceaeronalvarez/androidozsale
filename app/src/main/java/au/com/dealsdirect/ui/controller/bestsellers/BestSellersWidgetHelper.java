package au.com.dealsdirect.ui.controller.bestsellers;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.controller.priceblock.SaleItemProductPriceBlockHelper;
import au.com.dealsdirect.ui.controller.saleitemdetails.HorizontalScrollingItemsAdapter;
import au.com.dealsdirect.ui.controller.shops.adapter.HorizontalPageIndicatorAdapter;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.ScreenUtils;
import au.com.dealsdirect.utils.ScrollingImageHorizontal.HorizontalRecyclerItemsViewHolder;

public class BestSellersWidgetHelper {

    private final HorizontalScrollingItemsAdapter adapter;
    private final Context context;
    private final boolean isTablet;

    public BestSellersWidgetHelper(HorizontalScrollingItemsAdapter adapter, Context context, boolean isTablet) {
        this.adapter = adapter;
        this.context = context;
        this.isTablet = isTablet;
    }

    public HorizontalRecyclerItemsViewHolder createViewHolder(ViewGroup parent, int orientation) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_horizontal_scrolling_banner, parent, false);
        float height = computeBestSellersGrid(orientation).getItemHeight();
        height += context.getResources().getDimension(R.dimen.horizontal_banner_header_title_height);
        height += context.getResources().getDimension(R.dimen.horizontal_banner_circle_indicator_height);
        height += context.getResources().getDimension(R.dimen.margin_extra_small) * 2;
        return new HorizontalRecyclerItemsViewHolder(
                view,
                (int) height,
                adapter,
                true,
                false,
                HorizontalPageIndicatorAdapter.Style.RECTANGLE);
    }

    public void onBindViewHolder(HorizontalRecyclerItemsViewHolder viewHolder, int orientation) {
        if (viewHolder == null) {
            return;
        }
        setupBestSellersDimensions(orientation);
        viewHolder.setAdapter(adapter);

        if (adapter != null) {
            adapter.resetReyclerViewPosition();
            final int numberOfColumns = isTablet ?
                    (orientation == Configuration.ORIENTATION_LANDSCAPE ?
                            context.getResources().getInteger(R.integer.best_sellers_column_count_for_landscape_tablet) :
                            context.getResources().getInteger(R.integer.best_sellers_column_count_for_portrait_tablet)) :
                    context.getResources().getInteger(R.integer.best_sellers_column_count_for_mobile);
            viewHolder.setScrollStepSize(numberOfColumns);
            viewHolder.setPageIndicatorCountWithPageSize(numberOfColumns);
        } else {
            viewHolder.setPageIndicatorItemCount(0);
        }

        if (viewHolder.getPageIndicatorAdapter() != null) {
            viewHolder.getPageIndicatorAdapter().setSelectedPosition(0);
        }
        viewHolder.setHeaderText(context.getString(R.string.best_sellers).toUpperCase());
    }

    private ImageUtils.Grid computeBestSellersGrid(int orientation) {
        final float numberOfColumns = isTablet ?
                (orientation == Configuration.ORIENTATION_LANDSCAPE ?
                        context.getResources().getInteger(R.integer.best_sellers_column_count_for_landscape_tablet) :
                        context.getResources().getInteger(R.integer.best_sellers_column_count_for_portrait_tablet)) :
                context.getResources().getInteger(R.integer.best_sellers_column_count_for_mobile);
        final float screenDensity = ScreenUtils.getScreenDensity(context);
        final int proposedWidth = (int) (context.getResources().getInteger(R.integer.item_image_width) * screenDensity);
        final int proposedHeight = (int) ((context.getResources().getInteger(R.integer.item_image_height) * screenDensity) +
                context.getResources().getDimension((R.dimen.product_list_item_like_button_size)) +
                context.getResources().getDimension((R.dimen.product_list_text_view_height)) +
                context.getResources().getDimension(R.dimen.price_block_top_text_height) +
                context.getResources().getDimension(R.dimen.price_block_height) +
                SaleItemProductPriceBlockHelper.getBottomTextViewHeight(context, true, false) +
                context.getResources().getDimension(R.dimen.price_block_free_shipping_text_height));
        final float ratio = (float) proposedHeight / Math.max(1, proposedWidth);
        return ImageUtils.getExactGridDefinition(
                numberOfColumns,
                ratio,
                ScreenUtils.getScreenWidth(context));
    }

    private void setupBestSellersDimensions(int orientation) {
        if (adapter == null) {
            return;
        }
        ImageUtils.Grid grid = computeBestSellersGrid(orientation);
        float height = grid.getItemHeight();
        height += context.getResources().getDimension(R.dimen.margin_extra_small) * 2;
        adapter.setupDimensions((int) grid.getItemWidth(), (int) height);
    }
}
