package au.com.dealsdirect.ui.controller.trendingbrands;

import static au.com.dealsdirect.service.datacollection.core.DataCollector.EventParameters.ClickType.BANNER_CLICK;

import android.content.Context;
import android.net.Uri;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.banner.GetBannerResponse;
import au.com.dealsdirect.service.datacollection.core.DataCollector;
import au.com.dealsdirect.service.datacollection.enums.Events;
import au.com.dealsdirect.ui.controller.saleitemdetails.SaleItemDetailsController;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsController;
import au.com.dealsdirect.ui.controller.shops.ShopsController;
import au.com.dealsdirect.ui.controller.shops.adapter.HorizontalPageIndicatorAdapter;
import au.com.dealsdirect.ui.controller.shops.adapter.HorizontalScrollingBannerAdapter;
import au.com.dealsdirect.utils.DialogUtils;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.ScreenUtils;
import au.com.dealsdirect.utils.ScrollingImageHorizontal.HorizontalRecyclerBannerViewHolder;
import au.com.dealsdirect.utils.StringUtils;

public class TrendingBrandsWidgetHelper {

    private HorizontalScrollingBannerAdapter adapter;
    private Context context;
    private boolean isTablet;

    public TrendingBrandsWidgetHelper(HorizontalScrollingBannerAdapter adapter, Context context, boolean isTablet) {
        this.adapter = adapter;
        this.context = context;
        this.isTablet = isTablet;
    }

    public static List<GetBannerResponse.Banner> getBannersFromResponse(GetBannerResponse getBannerResponse) {
        final List<GetBannerResponse.Banner> trendingBrandsBanners = new ArrayList<>();
        if (getBannerResponse == null || getBannerResponse.getGroups() == null) {
            return trendingBrandsBanners;
        }
        final List<GetBannerResponse.Group> groups = getBannerResponse.getGroups();
        for (GetBannerResponse.Group group : groups) {
            if (group.getType().equals("brand")) {
                final List<GetBannerResponse.Banner> banners = group.getBanners();
                if (banners != null) {
                    for (GetBannerResponse.Banner banner : banners) {
                        if (banner.getBannerType().equals("brandBanner")) {
                            trendingBrandsBanners.add(banner);
                        }
                    }
                }
            }
        }
        return trendingBrandsBanners;
    }

    public HorizontalRecyclerBannerViewHolder createViewHolder(ViewGroup parent) {
        final boolean isCircular = adapter.getBannerStyle() == HorizontalScrollingBannerAdapter.BannerStyle.CIRCULAR;
        final int numberOfColumns = isTablet ? context.getResources().getInteger(isCircular ? R.integer.trending_brands_circular_column_count_for_tablet : R.integer.trending_brands_column_count_for_tablet) : context.getResources().getInteger(R.integer.trending_brands_column_count);
        final int numberOfItems = adapter.getDataSource().size();
        final float extraPercentage = numberOfItems > numberOfColumns && isCircular ?
                context.getResources().getInteger(isTablet ? R.integer.trending_brands_partial_column_percentage_for_tablet : R.integer.trending_brands_partial_column_percentage) / 100f : 0;
        int height = (int) (computeTrendingBrandsGrid(
                isCircular ? Math.min(numberOfColumns, numberOfItems) : numberOfColumns,
                extraPercentage).getItemHeight());
        if (adapter.isShowHeader()) {
            height += context.getResources().getDimension(R.dimen.horizontal_banner_header_title_height);
        }
        height += context.getResources().getDimension(R.dimen.horizontal_banner_circle_indicator_height);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_horizontal_scrolling_banner, parent, false);
        return new HorizontalRecyclerBannerViewHolder(
                view,
                height,
                adapter,
                true,
                true,
                HorizontalPageIndicatorAdapter.Style.RECTANGLE);
    }

    public void onBindViewHolder(HorizontalRecyclerBannerViewHolder viewHolder) {
        if (viewHolder == null) {
            return;
        }
        setupTrendingBrandsDimensions();
        viewHolder.setAdapter(adapter);
        viewHolder.setPageIndicatorVisibility(View.VISIBLE);

        if (adapter != null) {
            adapter.resetReyclerViewPosition();
            final boolean isCircular = adapter != null && adapter.getBannerStyle() == HorizontalScrollingBannerAdapter.BannerStyle.CIRCULAR;
            final int numberOfColumns = isTablet ? context.getResources().getInteger(isCircular ? R.integer.trending_brands_circular_column_count_for_tablet : R.integer.trending_brands_column_count_for_tablet) : context.getResources().getInteger(R.integer.trending_brands_column_count);
            viewHolder.setScrollStepSize(numberOfColumns);
            viewHolder.setPageIndicatorCountWithPageSize(numberOfColumns);
            final boolean willScrollWrapAround = viewHolder.getPageIndicatorAdapter().getItemCount() > 1;
            adapter.setWillScrollWrapAround(willScrollWrapAround);
        } else {
            viewHolder.setPageIndicatorItemCount(0);
        }

        if (viewHolder.getPageIndicatorAdapter() != null) {
            viewHolder.getPageIndicatorAdapter().setSelectedPosition(0);
        }
        viewHolder.setHeaderText(adapter.isShowHeader() ? adapter.getTitle() : null);
    }

    private ImageUtils.Grid computeTrendingBrandsGrid(int numberOfColumns, float extraPercentage) {
        final boolean isCircular = adapter.getBannerStyle() == HorizontalScrollingBannerAdapter.BannerStyle.CIRCULAR;

        final int width = trendingBrandsImageSize().first;
        final int height = trendingBrandsImageSize().second;

        int verticalPadding = getVerticalPaddingForHorizontalBanners();
        if (adapter.isShouldShowTitle()) {
            verticalPadding += context.getResources().getDimension(R.dimen.horizontal_banner_title_upper_spacing);
            verticalPadding += context.getResources().getDimension(R.dimen.horizontal_banner_title_height);
        }
        if (adapter.isShouldShowSubtitle()) {
            verticalPadding += context.getResources().getDimension(R.dimen.horizontal_banner_title_height);
        }

        if (!isCircular) {
            ImageUtils.Grid grid = ImageUtils.getRangedGridDefinition(width, height, ScreenUtils.getScreenWidth(context), numberOfColumns, numberOfColumns);
            return new ImageUtils.Grid(
                    1,
                    grid.getItemWidth() + getHorizontalPaddingForHorizontalBanners(),
                    grid.getItemHeight() + verticalPadding);
        } else {
            ImageUtils.Grid grid = ImageUtils.getExactGridDefinition(numberOfColumns + extraPercentage, height / (float) width, ScreenUtils.getScreenWidth(context));
            return new ImageUtils.Grid(
                    1,
                    grid.getItemWidth(),
                    Math.min(width, grid.getItemWidth()) + verticalPadding);
        }
    }

    private int getHorizontalPaddingForHorizontalBanners() {
        float dimen = context.getResources().getDimension(R.dimen.horizontal_banner_spacing);
        return (int) Math.ceil(dimen) * 2;
    }

    private int getVerticalPaddingForHorizontalBanners() {
        final boolean isCircular = adapter != null && adapter.getBannerStyle() == HorizontalScrollingBannerAdapter.BannerStyle.CIRCULAR;
        float dimen = 0;
        if (isCircular) {
            dimen += context.getResources().getDimension(R.dimen.horizontal_circular_banner_top_padding);
            dimen += context.getResources().getDimension(R.dimen.horizontal_circular_banner_bottom_padding);
        } else {
            dimen += context.getResources().getDimension(R.dimen.horizontal_banner_bottom_padding);
        }
        return (int) Math.ceil(dimen);
    }

    private Pair<Integer, Integer> trendingBrandsImageSize() {
        int width;
        int height;
        if (isTablet) {
            width = context.getResources().getInteger(R.integer.trending_brands_width_for_tablet);
            height = context.getResources().getInteger(R.integer.trending_brands_height_for_tablet);
        } else {
            width = context.getResources().getInteger(R.integer.trending_brands_width);
            height = context.getResources().getInteger(R.integer.trending_brands_height);
        }
        return new Pair<>(width, height);
    }

    public void setupTrendingBrandsDimensions() {
        final boolean isCircular = adapter.getBannerStyle() == HorizontalScrollingBannerAdapter.BannerStyle.CIRCULAR;
        final int numberOfColumns = isTablet ? context.getResources().getInteger(isCircular ? R.integer.trending_brands_circular_column_count_for_tablet : R.integer.trending_brands_column_count_for_tablet) : context.getResources().getInteger(R.integer.trending_brands_column_count);
        final int numberOfItems = adapter.getDataSource().size();
        final float extraPercentage = numberOfItems > numberOfColumns && isCircular ?
                context.getResources().getInteger(isTablet ? R.integer.trending_brands_partial_column_percentage_for_tablet : R.integer.trending_brands_partial_column_percentage) / 100f : 0;
        ImageUtils.Grid trendingBrandsGrid = computeTrendingBrandsGrid(
                isCircular ? Math.min(numberOfColumns, numberOfItems) : numberOfColumns,
                extraPercentage);
        adapter.setImageWidth(trendingBrandsImageSize().first);
        adapter.setImageHeight(trendingBrandsImageSize().second);
        adapter.setupDimensions((int) trendingBrandsGrid.getItemWidth(), (int) trendingBrandsGrid.getItemHeight());
    }

    public void onBannerBrandClicked(GetBannerResponse.Banner banner, Router router, int position) {
        if (banner.getBannerType() == null || !banner.getBannerType().equals("brandBanner")) {
            return;
        }
        final Uri uri = Uri.parse(banner.getLink());
        final String saleId = uri.getQueryParameter("saleID");

        if (saleId == null || saleId.isEmpty()) {
            return;
        }
        String saleNameFromUri = null;
        final String path = uri.getPath();
        if (path == null || path.isEmpty()) {
            return;
        }
        final ArrayList<String> directories = new ArrayList<>(Arrays.asList(path.split("/")));
        if (directories.get(0).isEmpty()) {
            directories.remove(0);
        }
        saleNameFromUri = StringUtils.toTitleCase(
                StringUtils.fixApostropheS(
                        directories.get(2)
                                .replace('-', ' ')
                                .replace(" or ", " | ")
                                .replace(" and ", " & ")));
        onBannerClicked(router, saleId, saleNameFromUri, banner.getId(), position, banner.getEndDate(), banner.getIsAvailable(), banner.getLinkOptions());
    }

    private void onBannerClicked(Router router, String saleId, String bannerTitle, String bannerId, int position, String endDate, boolean isAvailable, GetBannerResponse.LinkOptions linkOptions) {

        SaleItemsController.Parameters.FromBannerClick parameters = new SaleItemsController.Parameters.FromBannerClick(bannerTitle, saleId, bannerId, null, endDate, linkOptions, position);

        SaleItemsController controller = SaleItemsController.newInstance(parameters);

        HashMap<String, Object> eventParameters = new HashMap<>();
        eventParameters.put(DataCollector.EventParameters.TYPE, BANNER_CLICK);
        eventParameters.put(DataCollector.EventParameters.ITEM_ARRAY_POSITION, position);
        eventParameters.put(DataCollector.EventParameters.APP_CONTEXT, context);
        eventParameters.put(DataCollector.EventParameters.SCREEN_NAME, ShopsController.class.getSimpleName());
        DataCollector.logEvent(Events.clicksEvent, eventParameters);

        if (isAvailable) {
            final List<RouterTransaction> backstack = router.getBackstack();
            final List<RouterTransaction> newBackstack = new ArrayList<>();
            for (RouterTransaction routerTransaction : backstack) {
                if (routerTransaction.controller() instanceof SaleItemsController ||
                        routerTransaction.controller() instanceof SaleItemDetailsController) {
                    break;
                } else {
                    newBackstack.add(routerTransaction);
                }
            }

            newBackstack.add(RouterTransaction.with(controller)
                    .tag(context.getString(R.string.sale_items_controller_tag))
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler()));

            router.setBackstack(newBackstack, new HorizontalChangeHandler());
        } else {
            DialogUtils.showYesDialog(context, "", "Sale is currently closed", "OK", (dialogInterface, i) -> dialogInterface.dismiss());
        }
    }
}
