package au.com.dealsdirect.ui.controller.brands.adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxbinding2.view.RxView;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.banner.GetTopBrandsResponse;
import au.com.dealsdirect.ui.controller.shops.adapter.HorizontalPageIndicatorAdapter;
import au.com.dealsdirect.ui.controller.shops.adapter.HorizontalScrollingBannerAdapter;
import au.com.dealsdirect.ui.controller.shops.adapter.ResettableDimensions;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.ScreenUtils;
import au.com.dealsdirect.utils.ScrollingImageHorizontal.HorizontalRecyclerBannerViewHolder;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class TopBrandsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ResettableDimensions {

    private static final boolean WILL_DISPLAY_BANNER = false;

    private static final int VIEW_HOLDER_TYPE_BRANDS_LIST_ITEM = 0;
    private static final int VIEW_HOLDER_TYPE_TRENDING_BRANDS_WIDGET = 1;

    private final List<GetTopBrandsResponse> mTopBrands;

    private HorizontalScrollingBannerAdapter mTrendingBrandsAdapter = null;

    private int mOrientation;

    private int mComputedHeight = -1;
    private final Context context;
    private int mWidth;
    private int mHeight;
    private int mNumberOfColumns;
    private final boolean isTablet;

    private final TopBrandsAdapterHelper mTopBrandsAdapterHelper;
    private static final int THROTTLE_FIRST_WINDOW_DURATION = 1000;

    public TopBrandsAdapter(
            Context context,
            List<GetTopBrandsResponse> topBrands,
            int orientation,
            boolean isTablet,
            TopBrandsAdapterHelper topBrandsAdapterHelper) {

        mTopBrands = new ArrayList<>(topBrands);
        Collections.sort(mTopBrands);

        this.context = context;
        this.isTablet = isTablet;

        mOrientation = orientation;

        mTopBrandsAdapterHelper = topBrandsAdapterHelper;

        resetDimensions();
    }

    static class TopBrandsBannerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.viewholder_banner_image)
        ImageView image;

        @BindView(R.id.viewholder_brand_banner_info_container)
        ViewGroup info;

        @BindView(R.id.viewholder_top_brands_item_name)
        TextView name;

        Disposable subscription;

        TopBrandsBannerViewHolder(@NonNull View itemView, int height) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            if (height > 0) {
                ViewGroup.LayoutParams params = itemView.getLayoutParams();
                params.height = height;
                itemView.setLayoutParams(params);
            }
        }
    }

    static class TopBrandsItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.viewholder_top_brands_item_name)
        TextView name;

        Disposable subscription;

        TopBrandsItemViewHolder(@NonNull View itemView, int height) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            if (height > 0) {
                ViewGroup.LayoutParams params = itemView.getLayoutParams();
                params.height = height;
                itemView.setLayoutParams(params);
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_HOLDER_TYPE_TRENDING_BRANDS_WIDGET: {
                final boolean isCircular = mTrendingBrandsAdapter.getBannerStyle() == HorizontalScrollingBannerAdapter.BannerStyle.CIRCULAR;
                final int numberOfColumns = isTablet ? context.getResources().getInteger(isCircular ? R.integer.trending_brands_circular_column_count_for_tablet : R.integer.trending_brands_column_count_for_tablet) : context.getResources().getInteger(R.integer.trending_brands_column_count);
                final int numberOfItems = mTrendingBrandsAdapter.getDataSource().size();
                final float extraPercentage = numberOfItems > numberOfColumns && isCircular ?
                        context.getResources().getInteger(isTablet ? R.integer.trending_brands_partial_column_percentage_for_tablet : R.integer.trending_brands_partial_column_percentage) / 100f : 0;
                int height = (int) (computeTrendingBrandsGrid(
                        isCircular ? Math.min(numberOfColumns, numberOfItems) : numberOfColumns,
                        extraPercentage).getItemHeight());
                if (mTrendingBrandsAdapter.isShowHeader()) {
                    height += context.getResources().getDimension(R.dimen.horizontal_banner_header_title_height);
                }
                height += context.getResources().getDimension(R.dimen.horizontal_banner_circle_indicator_height);
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_horizontal_scrolling_banner, parent, false);
                return new HorizontalRecyclerBannerViewHolder(
                        view,
                        height,
                        mTrendingBrandsAdapter,
                        false,
                        true,
                        HorizontalPageIndicatorAdapter.Style.RECTANGLE);
            }
            default:
                if (WILL_DISPLAY_BANNER) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.viewholder_banner_for_top_brands, parent, false);

                    return new TopBrandsBannerViewHolder(view, mComputedHeight);
                } else {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.viewholder_top_brands_item, parent, false);

                    return new TopBrandsItemViewHolder(view, -1);
                }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_HOLDER_TYPE_TRENDING_BRANDS_WIDGET: {
                if (holder instanceof HorizontalRecyclerBannerViewHolder) {
                    final HorizontalRecyclerBannerViewHolder horizontalRecyclerViewHolder = (HorizontalRecyclerBannerViewHolder) holder;
                    ((HorizontalRecyclerBannerViewHolder) holder).onViewBound();
                    setupTrendingBrandsBannersDimensions();
                    horizontalRecyclerViewHolder.setAdapter(mTrendingBrandsAdapter);
                    horizontalRecyclerViewHolder.setPageIndicatorVisibility(View.VISIBLE);
                    if (mTrendingBrandsAdapter != null) {
                        mTrendingBrandsAdapter.resetReyclerViewPosition();
                        final boolean isCircular = mTrendingBrandsAdapter != null && mTrendingBrandsAdapter.getBannerStyle() == HorizontalScrollingBannerAdapter.BannerStyle.CIRCULAR;
                        final int numberOfColumns = isTablet ? context.getResources().getInteger(isCircular ? R.integer.trending_brands_circular_column_count_for_tablet : R.integer.trending_brands_column_count_for_tablet) : context.getResources().getInteger(R.integer.trending_brands_column_count);
                        horizontalRecyclerViewHolder.setScrollStepSize(numberOfColumns);
                        horizontalRecyclerViewHolder.setPageIndicatorCountWithPageSize(numberOfColumns);
                        final boolean willScrollWrapAround = horizontalRecyclerViewHolder.getPageIndicatorAdapter().getItemCount() > 1;
                        mTrendingBrandsAdapter.setWillScrollWrapAround(willScrollWrapAround);
                    } else {
                        horizontalRecyclerViewHolder.setScrollStepSize(1);
                        horizontalRecyclerViewHolder.setPageIndicatorItemCount(0);
                    }
                    if (horizontalRecyclerViewHolder.getPageIndicatorAdapter() != null) {
                        horizontalRecyclerViewHolder.getPageIndicatorAdapter().setSelectedPosition(0);
                    }
                    horizontalRecyclerViewHolder.setHeaderText(mTrendingBrandsAdapter.isShowHeader() ? mTrendingBrandsAdapter.getTitle() : null);
                }
                break;
            }
            case VIEW_HOLDER_TYPE_BRANDS_LIST_ITEM: {
                final GetTopBrandsResponse item = mTopBrands.get(position - getStartingPositionOfBrandsList());
                if (holder instanceof TopBrandsBannerViewHolder) {
                    setupBannerViewHolder(item, (TopBrandsBannerViewHolder) holder);
                } else if (holder instanceof TopBrandsItemViewHolder) {
                    setupItemViewHolder(item, (TopBrandsItemViewHolder) holder);
                }
                break;
            }
            default:
                holder.itemView.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof HorizontalRecyclerBannerViewHolder) {
            HorizontalRecyclerBannerViewHolder viewHolder = (HorizontalRecyclerBannerViewHolder) holder;
            viewHolder.onViewRecycled();
            viewHolder.onViewRemoved();
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder instanceof HorizontalRecyclerBannerViewHolder) {
            HorizontalRecyclerBannerViewHolder viewHolder = (HorizontalRecyclerBannerViewHolder) holder;
            viewHolder.onViewRecycled();
            viewHolder.onViewRemoved();
        }
    }

    private void setupBannerViewHolder(GetTopBrandsResponse item, TopBrandsBannerViewHolder holder) {
        int width = mWidth;
        int height = mHeight;
        holder.name.setVisibility(View.GONE);

        if (item.getDescription() != null && !item.getDescription().isEmpty()) {
            holder.info.setVisibility(View.VISIBLE);
            holder.info.setOnClickListener(v -> {
                if (mTopBrandsAdapterHelper != null) {
                    mTopBrandsAdapterHelper.onInfoClick(item.getName(), item.getDescription());
                }
            });
        } else {
            holder.info.setVisibility(View.GONE);
        }

        String imgUrl = ImageUtils.appendBannerSizeUrl(item.getImage(), width, height);

        imgUrl += "?profile=sb&b&width=" + width;

        ImageUtils.loadImage(imgUrl, holder.image);

        if (holder.subscription != null) {
            holder.subscription.dispose();
        }

        holder.subscription = RxView.clicks(holder.itemView)
                .throttleFirst(
                        THROTTLE_FIRST_WINDOW_DURATION,
                        TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action -> {
                    if (mTopBrandsAdapterHelper != null) {
                        mTopBrandsAdapterHelper.onBrandClick(item);
                    }
                });
    }

    private void setupItemViewHolder(GetTopBrandsResponse item, TopBrandsItemViewHolder holder) {
        holder.name.setText(item.getName());

        if (holder.subscription != null) {
            holder.subscription.dispose();
        }


        holder.subscription = RxView.clicks(holder.itemView)
                .throttleFirst(
                        THROTTLE_FIRST_WINDOW_DURATION,
                        TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action -> {
                    if (mTopBrandsAdapterHelper != null) {
                        mTopBrandsAdapterHelper.onBrandClick(item);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return mTopBrands.size() + getStartingPositionOfBrandsList();
    }

    @Override
    public void resetDimensions() {
        mWidth = context.getResources().getInteger(isTablet ? R.integer.sale_banner_tablet_width : R.integer.sale_banner_mobile_width);
        mHeight = context.getResources().getInteger(isTablet ? R.integer.sale_banner_tablet_height : R.integer.sale_banner_mobile_height);
        setupDimensions(mOrientation);
    }

    @Override
    public void setupDimensions(int orientation) {
        int minColumns = mTopBrandsAdapterHelper.getColumnCount();
        if (minColumns < 1) {
            int resId;
            switch (ScreenUtils.getOrientation(context)) {
                case Configuration.ORIENTATION_LANDSCAPE:
                    resId = isTablet ? R.integer.brand_banner_tablet_landscape_column_count : R.integer.old_banner_mobile_landscape_column_count;
                    break;
                default:
                    resId = isTablet ? R.integer.brand_banner_tablet_portrait_column_count : R.integer.old_banner_mobile_portrait_column_count;
                    break;
            }
            minColumns = context.getResources().getInteger(resId);
        }
        final int maxColumns = minColumns;

        mOrientation = orientation;

        // Dynamic Height Computation
        ImageUtils.Grid grid = ImageUtils.getRangedGridDefinition(
                mWidth, mHeight,
                ScreenUtils.getScreenWidth(context),
                minColumns, maxColumns);
        mNumberOfColumns = grid.getColumn();
        mComputedHeight = (int) grid.getItemHeight();

        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        int offset = 0;
        if (mTrendingBrandsAdapter != null) {
            if (position == offset) {
                return VIEW_HOLDER_TYPE_TRENDING_BRANDS_WIDGET;
            }
            offset++;
        }
        if (position >= offset) {
            return VIEW_HOLDER_TYPE_BRANDS_LIST_ITEM;
        }
        return -1;
    }

    @Override
    public int getNumberOfColumns() {
        return mNumberOfColumns;
    }

    @Override
    public boolean isUseOldBannerDimensions() {
        return false;
    }

    @Override
    public void setUseOldBannerDimensions(boolean useOldBannerDimensions) {
    }

    private String titleForHeader(int position) {
        final int adjustedPosition = position - getStartingPositionOfBrandsList();
        if (adjustedPosition < 0) {
            return null;
        }

        final char c = mTopBrands.get(adjustedPosition).getName().charAt(0);
        if (Character.isDigit(c)) {
            return "0-9";
        } else if (Character.isLetter(c)){
            return String.valueOf(c).toUpperCase();
        } else {
            return "Other";
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.viewholder_top_brands_header_text)
        TextView headerText;

        HeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public StickyRecyclerHeadersAdapter<HeaderViewHolder> getStickyRecyclerHeadersAdapter() {
        return new StickyRecyclerHeadersAdapter<HeaderViewHolder>() {
            @Override
            public long getHeaderId(int position) {
                String title = titleForHeader(position);
                if (title != null) {
                    return title.hashCode();
                }
                return -1;
            }

            @Override
            public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_top_brands_header, parent, false);
                return new HeaderViewHolder(view);
            }

            @Override
            public void onBindHeaderViewHolder(HeaderViewHolder viewHolder, int position) {
                String title = titleForHeader(position);
                viewHolder.headerText.setText(title == null ? "" : title);
            }

            @Override
            public int getItemCount() {
                return TopBrandsAdapter.this.getItemCount();
            }
        };
    }

    private void setupTrendingBrandsBannersDimensions() {
        if (mTrendingBrandsAdapter != null) {
            final boolean isCircular = mTrendingBrandsAdapter.getBannerStyle() == HorizontalScrollingBannerAdapter.BannerStyle.CIRCULAR;
            final int numberOfColumns = isTablet ? context.getResources().getInteger(isCircular ? R.integer.trending_brands_circular_column_count_for_tablet : R.integer.trending_brands_column_count_for_tablet) : context.getResources().getInteger(R.integer.trending_brands_column_count);
            final int numberOfItems = mTrendingBrandsAdapter.getDataSource().size();
            final float extraPercentage = numberOfItems > numberOfColumns && isCircular ?
                    context.getResources().getInteger(isTablet ? R.integer.trending_brands_partial_column_percentage_for_tablet : R.integer.trending_brands_partial_column_percentage) / 100f : 0;
            ImageUtils.Grid trendingBrandsGrid = computeTrendingBrandsGrid(
                    isCircular ? Math.min(numberOfColumns, numberOfItems) : numberOfColumns,
                    extraPercentage);
            mTrendingBrandsAdapter.setImageWidth(trendingBrandsImageSize().first);
            mTrendingBrandsAdapter.setImageHeight(trendingBrandsImageSize().second);
            mTrendingBrandsAdapter.setupDimensions((int) trendingBrandsGrid.getItemWidth(), (int) trendingBrandsGrid.getItemHeight());
        }
    }

    private ImageUtils.Grid computeTrendingBrandsGrid(int numberOfColumns, float extraPercentage) {
        final boolean isCircular = mTrendingBrandsAdapter != null && mTrendingBrandsAdapter.getBannerStyle() == HorizontalScrollingBannerAdapter.BannerStyle.CIRCULAR;

        final int width = trendingBrandsImageSize().first;
        final int height = trendingBrandsImageSize().second;

        int heightPadding = getBottomPaddingForHorizontalBanners();
        if (mTrendingBrandsAdapter.isShouldShowTitle()) {
            heightPadding += context.getResources().getDimension(R.dimen.horizontal_banner_title_upper_spacing);
            heightPadding += context.getResources().getDimension(R.dimen.horizontal_banner_title_height);
        }
        if (mTrendingBrandsAdapter.isShouldShowSubtitle()) {
            heightPadding += context.getResources().getDimension(R.dimen.horizontal_banner_title_height);
        }

        if (!isCircular) {
            ImageUtils.Grid grid = ImageUtils.getRangedGridDefinition(
                    width, height,
                    ScreenUtils.getScreenWidth(context),
                    numberOfColumns, numberOfColumns);
            return new ImageUtils.Grid(
                    1,
                    grid.getItemWidth() + getHorizontalPaddingForHorizontalBanners(),
                    grid.getItemHeight() + heightPadding);
        } else {
            ImageUtils.Grid grid = ImageUtils.getExactGridDefinition(
                    numberOfColumns + extraPercentage,
                    height / (float) width,
                    ScreenUtils.getScreenWidth(context));
            return new ImageUtils.Grid(
                    1,
                    grid.getItemWidth(),
                    Math.min(width, grid.getItemWidth()) + heightPadding);
        }
    }

    private int getHorizontalPaddingForHorizontalBanners() {
        float dimen = context.getResources().getDimension(R.dimen.horizontal_banner_spacing);
        return (int) Math.ceil(dimen) * 2;
    }

    private int getBottomPaddingForHorizontalBanners() {
        final boolean isCircular = mTrendingBrandsAdapter != null && mTrendingBrandsAdapter.getBannerStyle() == HorizontalScrollingBannerAdapter.BannerStyle.CIRCULAR;
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

    private int getStartingPositionOfBrandsList() {
        int offset = 0;
        if (mTrendingBrandsAdapter != null) {
            offset++;
        }
        return offset;
    }

    public void setTrendingBrandsAdapter(HorizontalScrollingBannerAdapter trandingBrandsAdapter) {
        boolean willInsert = mTrendingBrandsAdapter == null && trandingBrandsAdapter != null;
        boolean willDelete = mTrendingBrandsAdapter != null && trandingBrandsAdapter == null;
        if (willDelete) {
            int index = 0;
            mTrendingBrandsAdapter.setOnBannerTappedListener(null);
            mTrendingBrandsAdapter = null;
            notifyItemRemoved(index);
        } else if (willInsert) {
            mTrendingBrandsAdapter = trandingBrandsAdapter;
            mTrendingBrandsAdapter.preloadBannerImages(context);
            notifyItemInserted(0);
        }
    }
}
