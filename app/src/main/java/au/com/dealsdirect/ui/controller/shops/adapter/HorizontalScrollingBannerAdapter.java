package au.com.dealsdirect.ui.controller.shops.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.banner.GetBannerResponse;
import au.com.dealsdirect.data.network.model.productdetails.GetYouMayAlsoLikeResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.RecommendedItemsResponse;
import au.com.dealsdirect.utils.ImageUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class HorizontalScrollingBannerAdapter extends RecyclerView.Adapter<HorizontalScrollingBannerAdapter.ViewHolder> {

    private boolean hasInitializedDimensions = false;

    private int cellWidth;
    private int cellHeight;

    private List<GetBannerResponse.Banner> dataSource;

    private RecyclerView recyclerView = null;

    public OnBannerTappedListener onBannerTappedListener = null;
    public OnItemTappedListener onItemTappedListener = null;
    public OnItemRecommendedListener onItemRecommendedListener = null;

    private static final int THROTTLE_FIRST_WINDOW_DURATION = 1000;

    List<GetYouMayAlsoLikeResponse> mYouMayAlsoLikeList;
    List<RecommendedItemsResponse> mRecommendedList;

    private String saleId = "";
    private String seoIdentifierId = "";
    public BannerViewType bannerViewType = BannerViewType.ShopBanner;

    public enum BannerViewType {
        ShopBanner,
        YouMayAlsoLike,
        RecommendedItems
    }

    private Activity mActivity;

    public HorizontalScrollingBannerAdapter(Activity activity) {
        mActivity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;

        switch (getBannerViewType()) {
            case YouMayAlsoLike:
            case RecommendedItems:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.viewholder_banner_product, parent, false);
                return new ViewHolder(view, cellWidth);
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.viewholder_banner_for_horizontal, parent, false);
                return new ViewHolder(view, cellWidth);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int width;
        int height;

        String imgUrl = "";
        GetBannerResponse.Banner item;
        GetYouMayAlsoLikeResponse youMayLikeItem;
        RecommendedItemsResponse recommendedItemsResponse;
        int virtualPosition;

        switch (getBannerViewType()) {
            case YouMayAlsoLike:
                width = holder.itemView.getContext().getResources().getInteger(R.integer.you_may_also_like_width);
                height = holder.itemView.getContext().getResources().getInteger(R.integer.you_may_also_like_height);

                virtualPosition = position % mYouMayAlsoLikeList.size();

                youMayLikeItem = mYouMayAlsoLikeList.get(virtualPosition);

                imgUrl = youMayLikeItem.getImageList().get(0);

                holder.itemName.setText(youMayLikeItem.getName());

                if (holder.subscription != null) {
                    holder.subscription.dispose();
                }

                holder.subscription = RxView.clicks(holder.layout)
                        .throttleFirst(
                                THROTTLE_FIRST_WINDOW_DURATION,
                                TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(action -> {
                            if (onItemTappedListener != null) {
                                onItemTappedListener.onItemTapped(youMayLikeItem);
                            }
                        });
                break;
            case RecommendedItems:
                virtualPosition = position % mRecommendedList.size();

                recommendedItemsResponse = mRecommendedList.get(virtualPosition);

                imgUrl = recommendedItemsResponse.getImages().get(0);

                holder.itemName.setText(recommendedItemsResponse.getName());

                if (holder.subscription != null) {
                    holder.subscription.dispose();
                }

                holder.subscription = RxView.clicks(holder.layout)
                        .throttleFirst(
                                THROTTLE_FIRST_WINDOW_DURATION,
                                TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(action -> {
                            if (onItemRecommendedListener != null) {
                                onItemRecommendedListener.onItemRecommendedTapped(recommendedItemsResponse);
                            }
                        });
                break;
            default:
                if (cellWidth > cellHeight) {
                    width = holder.itemView.getContext().getResources().getInteger(R.integer.sliding_banner_width);
                    height = holder.itemView.getContext().getResources().getInteger(R.integer.sliding_banner_height);
                } else {
                    width = holder.itemView.getContext().getResources().getInteger(R.integer.sponsored_banner_width);
                    height = holder.itemView.getContext().getResources().getInteger(R.integer.sponsored_banner_height);
                }

                virtualPosition = position % dataSource.size();

                item = dataSource.get(virtualPosition);

                imgUrl = ImageUtils.appendBannerSizeUrl(item.getImage(), width, height);

                if (holder.subscription != null) {
                    holder.subscription.dispose();
                }

                if (item.getGroup() != null && item.getGroup().getIsClickable() != null) {
                    holder.subscription = RxView.clicks(holder.layout)
                            .throttleFirst(
                                    THROTTLE_FIRST_WINDOW_DURATION,
                                    TimeUnit.MILLISECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(action -> {
                                if (onBannerTappedListener != null) {
                                    onBannerTappedListener.onBannerTapped(item);
                                }
                            });
                }
        }

        if (mActivity != null && !mActivity.isDestroyed()) {
            ImageUtils.loadImage(imgUrl, holder.image);
        }

    }

    @Override
    public int getItemCount() {
        switch (getBannerViewType()) {
            case YouMayAlsoLike:
                return (mYouMayAlsoLikeList != null && mYouMayAlsoLikeList.size() != 0) ?
                        mYouMayAlsoLikeList.size() + getEdgeBufferSize() * 2 : 0;
            case RecommendedItems:
                return (mRecommendedList != null && mRecommendedList.size() != 0) ?
                        mRecommendedList.size() + getEdgeBufferSize() * 2 : 0;
            default:
                return (dataSource != null && dataSource.size() != 0) ?
                        dataSource.size() + getEdgeBufferSize() * 2 : 0;
        }
    }

    public List<GetBannerResponse.Banner> getDataSource() {
        return dataSource;
    }

    public List<GetYouMayAlsoLikeResponse> getYouMayAlsoLikeList() {
        return mYouMayAlsoLikeList;
    }

    public void setDataSource(List<GetBannerResponse.Banner> dataSource) {
        this.dataSource = dataSource;
        if (recyclerView != null && !recyclerView.isComputingLayout()) {
            notifyDataSetChanged();
        }
    }

    public void setYouMayAlsoLikeList(List<GetYouMayAlsoLikeResponse> youMayAlsoLikeList) {
        this.mYouMayAlsoLikeList = youMayAlsoLikeList;
        if (recyclerView != null && !recyclerView.isComputingLayout()) {
            notifyDataSetChanged();
        }
    }

    public List<RecommendedItemsResponse> getRecommendedList() {
        return mRecommendedList;
    }

    public void setRecommendedList(List<RecommendedItemsResponse> mRecommendedList) {
        this.mRecommendedList = mRecommendedList;
        if (recyclerView != null && !recyclerView.isComputingLayout()) {
            notifyDataSetChanged();
        }
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
        if (recyclerView != null && recyclerView.getWidth() > 0 && cellWidth > 0) {
            return (int) (2 * Math.ceil(recyclerView.getWidth() / (float) cellWidth));
        } else {

            switch (getBannerViewType()) {
                case YouMayAlsoLike:
                    return Math.max(3, mYouMayAlsoLikeList.size());
                case RecommendedItems:
                    return Math.max(3, mRecommendedList.size());
                default:
                    return Math.max(3, dataSource.size());
            }

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

    public OnItemTappedListener getOnItemTappedListener() {
        return onItemTappedListener;
    }

    public void setOnItemTappedListener(OnItemTappedListener onItemTappedListener) {
        this.onItemTappedListener = onItemTappedListener;
    }

    public OnItemRecommendedListener getOnItemRecommendedListener() {
        return onItemRecommendedListener;
    }

    public void setOnItemRecommendedListener(OnItemRecommendedListener onItemRecommendedListener) {
        this.onItemRecommendedListener = onItemRecommendedListener;
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
        int x = recyclerView.computeHorizontalScrollOffset();

        int itemSize = 0;
        switch (getBannerViewType()) {
            case YouMayAlsoLike:
                itemSize = getYouMayAlsoLikeList().size();
                break;
            case RecommendedItems:
                itemSize = getRecommendedList().size();
                break;
            default:
                itemSize = getDataSource().size();
        }

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
        int dataSize;
        switch (getBannerViewType()) {
            case YouMayAlsoLike:
                dataSize = getYouMayAlsoLikeList().size();
                break;
            case RecommendedItems:
                dataSize = getRecommendedList().size();
                break;
            default:
                dataSize = getDataSource().size();
        }
        int index = Math.round(x / getCellWidth() - getEdgeBufferSize()) % dataSize;
        return index < 0 ? index + dataSize : index;
    }

    private int getScrollRange() {
        switch (getBannerViewType()) {
            case YouMayAlsoLike:
                return getCellWidth() * getYouMayAlsoLikeList().size();
            case RecommendedItems:
                return getCellWidth() * getRecommendedList().size();
            default:
                return getCellWidth() * getDataSource().size();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.viewholder_banner_layout)
        ViewGroup layout;

        @BindView(R.id.viewholder_banner_image)
        public
        ImageView image;

        @Nullable
        @BindView(R.id.viewholder_sale_details_text)
        TextView itemName;

        ViewHolder(View view, int width) {
            super(view);
            ButterKnife.bind(this, view);

            if (width > 0) {
                ViewGroup.LayoutParams params = layout.getLayoutParams();
                params.width = width;
                layout.setLayoutParams(params);
            }
        }

        Disposable subscription;
    }

    public interface OnBannerTappedListener {
        void onBannerTapped(GetBannerResponse.Banner banner);
    }

    public interface OnItemTappedListener {
        void onItemTapped(GetYouMayAlsoLikeResponse response);
    }

    public interface OnItemRecommendedListener {
        void onItemRecommendedTapped(RecommendedItemsResponse response);
    }
}