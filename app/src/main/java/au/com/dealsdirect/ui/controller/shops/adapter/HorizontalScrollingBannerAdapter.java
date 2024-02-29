package au.com.dealsdirect.ui.controller.shops.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Priority;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.banner.GetBannerResponse;
import au.com.dealsdirect.data.network.model.productdetails.GetYouMayAlsoLikeResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.RecentlyViewedItemResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.RecommendedItemsResponse;
import au.com.dealsdirect.utils.CommonUtils;
import au.com.dealsdirect.utils.ImageUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class HorizontalScrollingBannerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private boolean hasInitializedDimensions = false;

    private int cellWidth;
    private int cellHeight;

    private int imageWidth;
    private int imageHeight;

    private Integer backgroundColorOverride = null;

    private List<GetBannerResponse.Banner> dataSource = new ArrayList<>();
    private boolean shouldShowTitle = false;

    private RecyclerView recyclerView = null;

    public OnBannerTappedListener onBannerTappedListener = null;

    private static final int THROTTLE_FIRST_WINDOW_DURATION = 1000;

    private String saleId = "";
    private String seoIdentifierId = "";
    public BannerViewType bannerViewType = BannerViewType.ShopBanner;

    private boolean shouldRepeatCellsToFillWidth = true;

    private Integer imageResolutionOverride = null;
    private boolean useCircularImage = false;

    public enum BannerViewType {
        ShopBanner,
        PromoBanner,

        LeaderBanner
    }

    private String title;

    public HorizontalScrollingBannerAdapter() {
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        switch (getBannerViewType()) {
            case PromoBanner:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.viewholder_banner_for_promo, parent, false);
                return new PromoBannerViewHolder(view, cellWidth, backgroundColorOverride);
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(useCircularImage ? R.layout.viewholder_banner_for_horizontal_circular : R.layout.viewholder_banner_for_horizontal,
                                parent, false);
                return new ViewHolder(view, cellWidth, backgroundColorOverride);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String imgUrl = "";
        GetBannerResponse.Banner item;
        GetYouMayAlsoLikeResponse youMayLikeItem;
        RecentlyViewedItemResponse recentlyItemResponse;
        RecommendedItemsResponse recommendedItemsResponse;
        int virtualPosition;

        switch (getBannerViewType()) {
            case PromoBanner: {
                PromoBannerViewHolder promoBannerViewHolder = (PromoBannerViewHolder) holder;
                virtualPosition = position % dataSource.size();

                item = dataSource.get(virtualPosition);

                imgUrl = ImageUtils.appendBannerSizeUrl(item.getImage(), imageWidth, imageHeight);
                ImageUtils.loadImageWithPriority(imgUrl, promoBannerViewHolder.image, Priority.HIGH);

                if (promoBannerViewHolder.subscription != null) {
                    promoBannerViewHolder.subscription.dispose();
                }

                if (item.getGroup() != null && item.getGroup().getIsClickable() != null) {
                    promoBannerViewHolder.subscription = RxView.clicks(promoBannerViewHolder.layout)
                            .throttleFirst(
                                    THROTTLE_FIRST_WINDOW_DURATION,
                                    TimeUnit.MILLISECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(action -> {
                                if (onBannerTappedListener != null) {
                                    onBannerTappedListener.onBannerTapped(item, position);
                                }
                            });
                }


                if (item.getDescription() != null && !item.getDescription().isEmpty()) {
                    promoBannerViewHolder.name.setVisibility(View.VISIBLE);
                    promoBannerViewHolder.name.setText(item.getDescription());
                } else {
                    promoBannerViewHolder.name.setVisibility(View.GONE);
                }
                if (item.getBannerText() != null && !item.getBannerText().isEmpty()) {
                    promoBannerViewHolder.discount.setVisibility(View.VISIBLE);
                    promoBannerViewHolder.discount.setText(item.getBannerText());
                } else {
                    promoBannerViewHolder.discount.setVisibility(View.GONE);
                }
            }
            break;
            case LeaderBanner:
                virtualPosition = position % dataSource.size();
                item = dataSource.get(virtualPosition);

                if(item.getImage() != null || item.getImage() != ""){
                    ImageUtils.loadImage(item.getImage(), ((ViewHolder) holder).image);
                }

                if (item.getGroup() != null && item.getGroup().getIsClickable() != null) {
                    ((ViewHolder) holder).subscription = RxView.clicks(((ViewHolder) holder).layout)
                            .throttleFirst(
                                    THROTTLE_FIRST_WINDOW_DURATION,
                                    TimeUnit.MILLISECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(action -> {
                                if (onBannerTappedListener != null) {
                                    onBannerTappedListener.onBannerTapped(item, position);
                                }
                            });
                }

                ((ViewHolder) holder).title.setVisibility(View.GONE);
                break;
            default:
                virtualPosition = position % dataSource.size();

                item = dataSource.get(virtualPosition);

                imgUrl = ImageUtils.appendBannerSizeUrl(item.getImage(), imageWidth, imageHeight, imageResolutionOverride);

                if (((ViewHolder) holder).subscription != null) {
                    ((ViewHolder) holder).subscription.dispose();
                }

                if (item.getGroup() != null && item.getGroup().getIsClickable() != null) {
                    ((ViewHolder) holder).subscription = RxView.clicks(((ViewHolder) holder).layout)
                            .throttleFirst(
                                    THROTTLE_FIRST_WINDOW_DURATION,
                                    TimeUnit.MILLISECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(action -> {
                                if (onBannerTappedListener != null) {
                                    onBannerTappedListener.onBannerTapped(item, position);
                                }
                            });
                }

                String title = item.getBannerText();
                if (shouldShowTitle && title != null) {
                    ((ViewHolder) holder).title.setText(title);
                    ((ViewHolder) holder).title.setVisibility(View.VISIBLE);
                } else {
                    ((ViewHolder) holder).title.setVisibility(View.GONE);
                }
                break;
        }

        if (holder instanceof ViewHolder && !imgUrl.isEmpty() && !CommonUtils.isActivityOfViewDestroyed(((ViewHolder) holder).image)) {
            if (useCircularImage) {
                ImageUtils.loadImageWithCircleCrop(imgUrl, ((ViewHolder) holder).image);
            } else {
                ImageUtils.loadImage(imgUrl, ((ViewHolder) holder).image);
            }
        }

    }

    @Override
    public int getItemCount() {
        return (dataSource != null && !dataSource.isEmpty()) ?
                dataSource.size() + getEdgeBufferSize() * 2 : 0;
    }

    public boolean isShouldRepeatCellsToFillWidth() {
        return shouldRepeatCellsToFillWidth;
    }

    public void setShouldRepeatCellsToFillWidth(boolean shouldRepeatCellsToFillWidth) {
        this.shouldRepeatCellsToFillWidth = shouldRepeatCellsToFillWidth;
    }

    public List<GetBannerResponse.Banner> getDataSource() {
        return dataSource;
    }

    public void setDataSource(List<GetBannerResponse.Banner> dataSource) {
        if (dataSource != null) {
            this.dataSource = new ArrayList<>(dataSource);
        } else {
            this.dataSource = new ArrayList<>();
        }
        if (recyclerView != null && !recyclerView.isComputingLayout()) {
            notifyDataSetChanged();
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setupDimensions(int width, int height) {
        cellWidth = width;
        cellHeight = height;
        if (recyclerView != null) {
            recyclerView.getRecycledViewPool().clear();
            if (!recyclerView.isComputingLayout()) {
                notifyDataSetChanged();
            }
        }
    }

    public int getCellWidth() {
        return cellWidth;
    }

    public int getCellHeight() {
        return cellHeight;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public BannerViewType getBannerViewType() {
        return bannerViewType;
    }

    public void setBannerViewType(BannerViewType bannerViewType) {
        this.bannerViewType = bannerViewType;
    }

    public String getSaleId() {
        return saleId;
    }

    public void setSaleId(String saleId) {
        this.saleId = saleId;
    }

    public String getSeoIdentifierId() {
        return seoIdentifierId;
    }

    public void setSeoIdentifierId(String seoIdentifierId) {
        this.seoIdentifierId = seoIdentifierId;
    }

    private int getEdgeBufferSize() {
        final int datasourceSize = dataSource.size();

        if (!shouldRepeatCellsToFillWidth) {
            if (recyclerView != null && recyclerView.getWidth() > 0 && cellWidth > 0 &&
                    Math.ceil(recyclerView.getWidth() / (float) cellWidth) > datasourceSize) {
                return 0;
            }
        }

        if (datasourceSize == 0) {
            return 0;
        } else if (recyclerView != null && recyclerView.getWidth() > 0 && cellWidth > 0) {
            return (int) (2 * Math.ceil(recyclerView.getWidth() / (float) cellWidth));
        } else {
            return Math.max(3, datasourceSize);
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
        if (!hasInitializedDimensions) {
            hasInitializedDimensions = true;
            setupDimensions(cellWidth, cellHeight);
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }

    public OnBannerTappedListener getOnBannerTappedListener() {
        return onBannerTappedListener;
    }

    public void setOnBannerTappedListener(OnBannerTappedListener onBannerTappedListener) {
        this.onBannerTappedListener = onBannerTappedListener;
    }

    public void resetReyclerViewPosition() {
        if (recyclerView != null) {
            recyclerView.scrollToPosition(getEdgeBufferSize());
        }
    }

    public void wrapScrollPosition(int speed) {
        if (recyclerView == null) {
            return;
        }
        final int x = recyclerView.computeHorizontalScrollOffset();

        final int itemSize = getDataSource().size();

        if (speed > 0 && x > getCellWidth() * (itemSize + getEdgeBufferSize())) {
            recyclerView.scrollBy(-getScrollRange(), 0);
        } else if (speed < 0 && x < getCellWidth() * getEdgeBufferSize()) {
            recyclerView.scrollBy(getScrollRange(), 0);
        }
    }

    public int getRecyclerViewPosition() {
        return getRecyclerViewPosition(0);
    }

    public int getRecyclerViewPosition(int offset) {
        if (recyclerView == null) {
            return -1;
        }
        int x = recyclerView.computeHorizontalScrollOffset() + offset;
        return getAdapterPositionFromX(x);
    }

    public int getAdapterPositionFromX(int x) {
        final int dataSize = getDataSource().size();
        final int index = Math.round(x / getCellWidth() - getEdgeBufferSize()) % dataSize;
        return index < 0 ? index + dataSize : index;
    }

    private int getScrollRange() {
        return getCellWidth() * getDataSource().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.viewholder_banner_layout)
        ViewGroup layout;

        @BindView(R.id.viewholder_banner_image)
        public
        ImageView image;

        @Nullable
        @BindView(R.id.viewholder_horizontal_scrolling_cell_title)
        TextView title;

        ViewHolder(View view, int width, Integer backgroundColorOverride) {
            super(view);
            ButterKnife.bind(this, view);

            if (width > 0) {
                ViewGroup.LayoutParams params = layout.getLayoutParams();
                params.width = width;
                layout.setLayoutParams(params);
            }

            if (backgroundColorOverride != null) {
                view.setBackgroundColor(backgroundColorOverride);
            }
        }

        Disposable subscription;
    }

    class PromoBannerViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.viewholder_banner_layout)
        ViewGroup layout;

        @BindView(R.id.viewholder_banner_image)
        ImageView image;

        @BindView(R.id.viewholder_banner_name)
        TextView name;

        @BindView(R.id.viewholder_banner_discount)
        TextView discount;

        PromoBannerViewHolder(View view, int width, Integer backgroundColorOverride) {
            super(view);
            ButterKnife.bind(this, view);

            if (width > 0) {
                ViewGroup.LayoutParams params = layout.getLayoutParams();
                params.width = width;
                layout.setLayoutParams(params);
            }

            if (backgroundColorOverride != null) {
                view.setBackgroundColor(backgroundColorOverride);
            }
        }

        Disposable subscription;
    }

    public interface OnBannerTappedListener {
        void onBannerTapped(GetBannerResponse.Banner banner, int position);
    }

    public interface OnItemTappedListener {
        void onItemTapped(GetYouMayAlsoLikeResponse response);
    }

    public interface OnItemRecommendedListener {
        void onItemRecommendedTapped(RecommendedItemsResponse response);
    }

    public boolean isShouldShowTitle() {
        return shouldShowTitle;
    }

    public void setShouldShowTitle(boolean shouldShowTitle) {
        this.shouldShowTitle = shouldShowTitle;
    }

    public Integer getImageResolutionOverride() {
        return imageResolutionOverride;
    }

    public void setImageResolutionOverride(Integer imageResolutionOverride) {
        this.imageResolutionOverride = imageResolutionOverride;
    }

    public boolean isUseCircularImage() {
        return useCircularImage;
    }

    public void setUseCircularImage(boolean useCircularImage) {
        this.useCircularImage = useCircularImage;
    }

    public Integer getBackgroundColorOverride() {
        return backgroundColorOverride;
    }

    public void setBackgroundColorOverride(Integer backgroundColorOverride) {
        this.backgroundColorOverride = backgroundColorOverride;
    }

    public void preloadBannerImages(Context context) {
        for (GetBannerResponse.Banner item : dataSource) {
            final String url = ImageUtils.appendBannerSizeUrl(item.getImage(), imageWidth, imageHeight);
            ImageUtils.preLoadImage(url, context);
        }
    }
}