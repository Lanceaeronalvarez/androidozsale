package au.com.dealsdirect.ui.controller.saleitemdetails;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.productdetails.GetYouMayAlsoLikeResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.RecentlyViewedItemResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.RecommendedItemsResponse;
import au.com.dealsdirect.data.network.model.saleitems.SaleItemProduct;
import au.com.dealsdirect.ui.controller.saleitemdetails.listener.ImageTappedListener;
import au.com.dealsdirect.utils.CommonUtils;
import au.com.dealsdirect.utils.ImageUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class HorizontalScrollingItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private boolean hasInitializedDimensions = false;

    private int cellWidth;
    private int cellHeight;

    private int imageWidth;
    private int imageHeight;

    private RecyclerView recyclerView = null;

    public OnItemTappedListener onItemTappedListener = null;

    private static final int THROTTLE_FIRST_WINDOW_DURATION = 1000;

    List<SaleItemProduct> mDatasource;

    private String saleId = "";
    private String seoIdentifierId = "";

    private boolean shouldRepeatCellsToFillWidth = true;

    private Integer imageResolutionOverride = null;
    private boolean useCircularImage = false;


    private String title;

    public HorizontalScrollingItemsAdapter() {
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_product_details_cell, parent, false);
        return new ViewHolder(view, cellWidth);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String imgUrl = "";
        final int virtualPosition = position % mDatasource.size();
        final SaleItemProduct saleItemProduct = mDatasource.get(virtualPosition);

        if (saleItemProduct instanceof GetYouMayAlsoLikeResponse) {
            final GetYouMayAlsoLikeResponse youMayLikeItem = (GetYouMayAlsoLikeResponse) saleItemProduct;

            if (youMayLikeItem.getImages() != null && !youMayLikeItem.getImages().isEmpty()) {
                imgUrl = youMayLikeItem.getImages().get(0);
            }

            ((ViewHolder) holder).title.setText(youMayLikeItem.getName());

            if (((ViewHolder) holder).subscription != null) {
                ((ViewHolder) holder).subscription.dispose();
            }

            ((ViewHolder) holder).subscription = RxView.clicks(((ViewHolder) holder).layout)
                    .throttleFirst(
                            THROTTLE_FIRST_WINDOW_DURATION,
                            TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(action -> {
                        if (onItemTappedListener != null) {
                            onItemTappedListener.onItemTapped(youMayLikeItem);
                        }
                    });
        }
        if (saleItemProduct instanceof RecommendedItemsResponse) {
            final RecommendedItemsResponse recommendedItemsResponse = (RecommendedItemsResponse) saleItemProduct;

            if (recommendedItemsResponse.getImages() != null && !recommendedItemsResponse.getImages().isEmpty()) {
                imgUrl = recommendedItemsResponse.getImages().get(0);
            }

            ((ViewHolder) holder).title.setText(recommendedItemsResponse.getName());

            if (((ViewHolder) holder).subscription != null) {
                ((ViewHolder) holder).subscription.dispose();
            }

            ((ViewHolder) holder).subscription = RxView.clicks(((ViewHolder) holder).layout)
                    .throttleFirst(
                            THROTTLE_FIRST_WINDOW_DURATION,
                            TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(action -> {
                        if (onItemTappedListener != null) {
                            onItemTappedListener.onItemTapped(recommendedItemsResponse);
                        }
                    });
        }
        if (saleItemProduct instanceof RecentlyViewedItemResponse) {
            final RecentlyViewedItemResponse recentlyItemResponse = (RecentlyViewedItemResponse) saleItemProduct;

            if (recentlyItemResponse.getImages() != null && !recentlyItemResponse.getImages().isEmpty()) {
                imgUrl = recentlyItemResponse.getImages().get(0);
            }
            ((ViewHolder) holder).title.setText(recentlyItemResponse.getName());
            ((ViewHolder) holder).subscription = RxView.clicks(((ViewHolder) holder).layout)
                    .throttleFirst(
                            THROTTLE_FIRST_WINDOW_DURATION,
                            TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(action -> {
                        if (onItemTappedListener != null) {
                            onItemTappedListener.onItemTapped(recentlyItemResponse);
                        }
                    });
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
        return (mDatasource != null && !mDatasource.isEmpty()) ?
                mDatasource.size() + getEdgeBufferSize() * 2 : 0;
    }

    public boolean isShouldRepeatCellsToFillWidth() {
        return shouldRepeatCellsToFillWidth;
    }

    public void setShouldRepeatCellsToFillWidth(boolean shouldRepeatCellsToFillWidth) {
        this.shouldRepeatCellsToFillWidth = shouldRepeatCellsToFillWidth;
    }

    public List<SaleItemProduct> getDatasource() {
        return mDatasource;
    }

    public void setRecentlyViewedList(List<RecentlyViewedItemResponse> mRecentlyViewedList) {
        this.mDatasource = new ArrayList<>(mRecentlyViewedList);
        if (recyclerView != null && !recyclerView.isComputingLayout()) {
            notifyDataSetChanged();
        }
    }

    public void setYouMayAlsoLikeList(List<GetYouMayAlsoLikeResponse> youMayAlsoLikeList) {
        this.mDatasource = new ArrayList<>(youMayAlsoLikeList);
        if (recyclerView != null && !recyclerView.isComputingLayout()) {
            notifyDataSetChanged();
        }
    }

    public void setRecommendedList(List<RecommendedItemsResponse> mRecommendedList) {
        this.mDatasource = new ArrayList<>(mRecommendedList);
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
        final int datasourceSize = mDatasource.size();

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

    public OnItemTappedListener getOnItemTappedListener() {
        return onItemTappedListener;
    }

    public void setOnItemTappedListener(OnItemTappedListener onItemTappedListener) {
        this.onItemTappedListener = onItemTappedListener;
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

        final int itemSize = getDatasource().size();

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
        final int dataSize = getDatasource().size();
        final int index = Math.round(x / getCellWidth() - getEdgeBufferSize()) % dataSize;
        return index < 0 ? index + dataSize : index;
    }

    private int getScrollRange() {
        return getCellWidth() * getDatasource().size();
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

    public interface OnItemTappedListener {
        void onItemTapped(SaleItemProduct item);
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
}