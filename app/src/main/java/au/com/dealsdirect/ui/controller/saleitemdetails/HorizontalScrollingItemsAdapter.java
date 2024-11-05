package au.com.dealsdirect.ui.controller.saleitemdetails;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.saleitems.SaleItemProduct;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemViewHolder;

public class HorizontalScrollingItemsAdapter extends RecyclerView.Adapter<SaleItemViewHolder> {

    private boolean hasInitializedDimensions = false;

    private int cellWidth = 1;
    private int cellHeight = 1;

    private RecyclerView recyclerView = null;

    private OnItemTappedListener onItemTappedListener = null;

    private OnPriceInfoTappedListener onPriceInfoTappedListener = null;
    private WishlistListener wishlistListener = null;

    private final List<SaleItemProduct> mDataSource;

    private boolean shouldRepeatCellsToFillWidth = true;

    private final boolean isSupplierOriginalPriceInfoEnabled;

    private boolean isWithBorder;

    private boolean isScaledDown;

    public HorizontalScrollingItemsAdapter(List<SaleItemProduct> dataSource, boolean isWithBorder, boolean isSupplierOriginalPriceInfoEnabled, boolean isScaledDown) {
        mDataSource = new ArrayList<>(dataSource);
        this.isWithBorder = isWithBorder;
        this.isSupplierOriginalPriceInfoEnabled = isSupplierOriginalPriceInfoEnabled;
        this.isScaledDown = isScaledDown;
    }

    @NonNull
    @Override
    public SaleItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(isWithBorder ? R.layout.viewholder_sale_item_with_border : R.layout.viewholder_sale_item,
                        parent,
                        false);
        return new SaleItemViewHolder(view, new Pair<>(cellWidth, cellHeight), isSupplierOriginalPriceInfoEnabled, false, isScaledDown);
    }

    @Override
    public void onBindViewHolder(@NonNull SaleItemViewHolder holder, int position) {
        final int virtualPosition = position % mDataSource.size();
        final SaleItemProduct saleItemProduct = mDataSource.get(virtualPosition);

        final List<String> imgUrls = saleItemProduct.getImages();
        final String imgUrl = imgUrls == null || imgUrls.isEmpty() ? null : imgUrls.get(0);

        final boolean isProductInWishlist;
        if (wishlistListener != null) {
            isProductInWishlist = wishlistListener.isProductInWishlist(saleItemProduct);
        } else {
            isProductInWishlist = false;
        }

        holder.setupViewHolderSkeleton(false);
        holder.setupViewHolder(saleItemProduct, imgUrl, isProductInWishlist);
        if (onItemTappedListener == null) {
            holder.itemView.setOnClickListener(null);
        } else {
            holder.itemView.setOnClickListener(v -> onItemTappedListener.onItemTapped(saleItemProduct, virtualPosition, mDataSource.size()));
        }
        if (onPriceInfoTappedListener == null) {
            holder.setPriceInfoOnClickListener(null);
        } else {
            holder.setPriceInfoOnClickListener(v -> onPriceInfoTappedListener.onPriceInfoTapped(saleItemProduct));
        }
        holder.setLikeButtonOnClickListener(v -> {
            if (wishlistListener != null) {
                holder.setLiked(!holder.isLiked());
                if (holder.isLiked()) {
                    wishlistListener.addToWishlist(saleItemProduct);
                } else {
                    wishlistListener.removeFromWishlist(saleItemProduct);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return !mDataSource.isEmpty() ?
                mDataSource.size() + getEdgeBufferSize() * 2 : 0;
    }

    public boolean isShouldRepeatCellsToFillWidth() {
        return shouldRepeatCellsToFillWidth;
    }

    public void setShouldRepeatCellsToFillWidth(boolean shouldRepeatCellsToFillWidth) {
        this.shouldRepeatCellsToFillWidth = shouldRepeatCellsToFillWidth;
    }

    public List<SaleItemProduct> getDataSource() {
        return mDataSource;
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

    private int getEdgeBufferSize() {
        final int datasourceSize = mDataSource.size();

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


    public OnPriceInfoTappedListener getOnPriceInfoTappedListener() {
        return onPriceInfoTappedListener;
    }

    public void setOnPriceInfoTappedListener(OnPriceInfoTappedListener onPriceInfoTappedListener) {
        this.onPriceInfoTappedListener = onPriceInfoTappedListener;
    }

    public WishlistListener getWishlistListener() {
        return wishlistListener;
    }

    public void setWishlistListener(WishlistListener wishlistListener) {
        this.wishlistListener = wishlistListener;
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

    public interface OnItemTappedListener {
        void onItemTapped(SaleItemProduct item, int position, int size);
    }

    public interface OnPriceInfoTappedListener {
        void onPriceInfoTapped(SaleItemProduct item);
    }

    public interface WishlistListener {
        void addToWishlist(SaleItemProduct item);

        void removeFromWishlist(SaleItemProduct item);

        boolean isProductInWishlist(SaleItemProduct item);
    }
}