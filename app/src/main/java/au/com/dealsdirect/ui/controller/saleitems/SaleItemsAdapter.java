package au.com.dealsdirect.ui.controller.saleitems;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.saleitems.SaleItemProduct;
import au.com.dealsdirect.ui.controller.priceblock.SaleItemProductPriceBlockHelper;
import au.com.dealsdirect.utils.CommonUtils;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.ScreenUtils;

public class SaleItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int SCREEN_TRANSITION_DELAY = 2000;
    private List<SaleItemProduct> mData;
    private int mColumnCount;
    private static final int FOOTER_VIEW = 1;
    private final int mMinColumn;
    private boolean mIsFooterEnabled = true;
    private int mCurrentItemCount = -1;
    private final SaleItemAdapterHelper helper;
    private final boolean isPriceInfoClickable;
    private final boolean isSupplierOriginalPriceInfoEnabled;
    private Pair<Integer, Integer> mComputedPair;

    private final SaleItemsMvpPresenter.WishlistDelayedCallback delayedCallbackForWishlist;

    private boolean useAlternatePriceBlockHelper = false;

    public SaleItemsAdapter(
            Context context,
            List<SaleItemProduct> saleItems,
            int minColumn,
            boolean isPriceInfoClilckable,
            boolean isSupplierOriginalPriceInfoEnabled,
            boolean useAlternatePriceBlockHelper,
            SaleItemsMvpPresenter.WishlistDelayedCallback delayedCallbackForWishlist,
            SaleItemAdapterHelper listener) {

        this.mData = saleItems;
        this.mMinColumn = minColumn;
        this.isSupplierOriginalPriceInfoEnabled = isSupplierOriginalPriceInfoEnabled;
        this.isPriceInfoClickable = isPriceInfoClilckable;
        this.useAlternatePriceBlockHelper = useAlternatePriceBlockHelper;
        this.delayedCallbackForWishlist = delayedCallbackForWishlist;
        this.helper = listener;

        computeItemViewDimensions(context);
    }

    public void computeItemViewDimensions(Context context) {

        final int orientation = context.getResources().getConfiguration().orientation;
        final boolean isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE;
        final float screenDensity = ScreenUtils.getScreenDensity(context);

        int portraitSize;
        int landscapeSize;
        if (mMinColumn == context.getResources().getInteger(R.integer.items_min_column_portrait)) {
            portraitSize = context.getResources().getInteger(R.integer.items_min_column_portrait);
            landscapeSize = context.getResources().getInteger(R.integer.items_min_column_landscape);
        } else {
            portraitSize = context.getResources().getInteger(R.integer.items_max_column_portrait);
            landscapeSize = context.getResources().getInteger(R.integer.items_max_column_landscape);
        }

        final int proposedWidth = (int) (context.getResources().getInteger(R.integer.item_image_width) * screenDensity);
        final int proposedHeight = (int) ((context.getResources().getInteger(R.integer.item_image_height) * screenDensity) +
                context.getResources().getDimension(R.dimen.price_block_top_text_height) +
                context.getResources().getDimension(R.dimen.price_block_height) +
                SaleItemProductPriceBlockHelper.getBottomTextViewHeight(context, true, isSupplierOriginalPriceInfoEnabled) +
                context.getResources().getDimension(R.dimen.price_block_free_shipping_text_height));

        final int numberOfColumns = isLandscape ? landscapeSize : portraitSize;

        ImageUtils.Grid gridDefinition = ImageUtils.getRangedGridDefinition(
                proposedWidth,
                proposedHeight,
                (float) ScreenUtils.getScreenWidth(context),
                numberOfColumns);

        mColumnCount = gridDefinition.getColumn();
        mComputedPair = new Pair<>((int) gridDefinition.getItemWidth(), (int) gridDefinition.getItemHeight());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == FOOTER_VIEW) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.footer_ads, parent, false);
            return new SaleItemFooterViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.viewholder_sale_item, parent, false);
            return new SaleItemViewHolder(view, mComputedPair, isSupplierOriginalPriceInfoEnabled, useAlternatePriceBlockHelper);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Context context = holder.itemView.getContext();

        if (holder.getItemViewType() == 0 && mData.size() != 0 && holder instanceof SaleItemViewHolder) {
            final SaleItemViewHolder saleItemViewHolder = (SaleItemViewHolder) holder;
            final SaleItemProduct product = mData.get(position);
            if (product == null) {
                saleItemViewHolder.setupViewHolderSkeleton(true);
            } else {
                saleItemViewHolder.setupViewHolderSkeleton(false);
                final String imageUrl = product.getImages().isEmpty() ? "" : (product.getImages().size() < 4 ? product.getImages().get(0) : product.getImages().get(1));
                saleItemViewHolder.setupViewHolder(product, imageUrl, helper.isProductInWishlist(product));
                setupViewHolderWithPriceBlockClicks(saleItemViewHolder, imageUrl, product);
            }
        } else if (holder instanceof SaleItemFooterViewHolder) {
            final SaleItemFooterViewHolder footerViewHolder = (SaleItemFooterViewHolder) holder;
            if (footerViewHolder.adView != null && helper.isGoogleAdsEnabled()) {
                CommonUtils.showAdmob(context, footerViewHolder.adView,
                        context.getResources().getString(R.string.admob_products_id));
            }
        }
    }

    @SuppressLint("CheckResult")
    public void setupViewHolderWithPriceBlockClicks(SaleItemViewHolder viewHolder, String imageUrl, SaleItemProduct product) {
        final String urlHigherRes = ImageUtils.removeResolutionModifierInImageUrl(imageUrl);

        RxView.clicks(viewHolder.itemView)
                .throttleFirst(SCREEN_TRANSITION_DELAY, TimeUnit.MILLISECONDS)
                .subscribe(action -> {
                    int[] originalPos = new int[2];
                    viewHolder.itemView.getLocationOnScreen(originalPos);

                    helper.onItemClicked(
                            viewHolder.getBindingAdapterPosition(),
                            viewHolder.image.getDrawable(),
                            urlHigherRes,
                            product,
                            originalPos[0],
                            originalPos[1],
                            viewHolder.itemView.getWidth(),
                            viewHolder.itemView.getHeight()
                    );
                });

        viewHolder.setLikeButtonOnClickListener(v -> {
            viewHolder.setLiked(!viewHolder.isLiked());
            if (viewHolder.isLiked()) {
                helper.addToWishlist(
                        product,
                        delayedCallbackForWishlist);
            } else {
                helper.removeFromWishlist(
                        product,
                        delayedCallbackForWishlist);

            }
        });

        if (isPriceInfoClickable) {
            viewHolder.setPriceInfoOnClickListener(v -> helper.onPriceInfoClicked(product));
        } else {
            viewHolder.setPriceInfoOnClickListener(null);
        }

        viewHolder.freeDelivery.setOnClickListener(v -> helper.onClickFreeDelivery(
                String.valueOf(product.getDeliveryThreshold()),
                product.getDeliveryType())
        );

    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        if (holder instanceof SaleItemViewHolder) {
            ImageUtils.clearImage(((SaleItemViewHolder) holder).image);
        }
        super.onViewDetachedFromWindow(holder);
    }

    public void replaceData(List<SaleItemProduct> saleItems) {
        int previousCount = mData.size();
        mData = saleItems;
        if (previousCount > 0 || !saleItems.isEmpty()) {
            notifyItemRangeChanged(0, Math.min(previousCount, saleItems.size()));
        }
        if (previousCount < saleItems.size()) {
            notifyItemRangeInserted(previousCount, saleItems.size() - previousCount);
        } else if (previousCount > saleItems.size()) {
            notifyItemRangeRemoved(saleItems.size(), previousCount - saleItems.size());
        }
        mCurrentItemCount = getItemCount();
    }

    public void addData(List<SaleItemProduct> saleItems) {
        addData(saleItems, true);
    }

    public void addData(List<SaleItemProduct> saleItems, boolean withAnimation) {
        int previousCount = mData.size();
        mData.addAll(saleItems);
        if (withAnimation) {
            notifyItemRangeInserted(previousCount, mData.size() - previousCount);
        } else {
            notifyDataSetChanged();
        }
        mCurrentItemCount = getItemCount();
    }

    @Override
    public int getItemCount() {
        return mData.size() + (mIsFooterEnabled && !mData.isEmpty() ? 1 : 0);
    }

    public List<SaleItemProduct> getData() {
        return mData;
    }

    public int getColumnCount() {
        return mColumnCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionFooter(position)) {
            return FOOTER_VIEW;
        }
        return super.getItemViewType(position);
    }

    private boolean isPositionFooter(int position) {
        return mIsFooterEnabled && !mData.isEmpty() && position == getItemCount() - 1;
    }

    public void reloadCell(int position) {
        notifyItemChanged(position);
    }

    public void removeData(int position) {
        removeData(position, true);
    }

    public void removeData(int position, boolean updateData) {
        if (updateData) {
            mData.remove(position);
        }
        if (getItemCount() == 0 && mCurrentItemCount > 0) {
            notifyItemRangeRemoved(0, mCurrentItemCount);
        } else {
            notifyItemRemoved(position);
        }
        mCurrentItemCount = getItemCount();
    }

    public boolean isFooterEnabled() {
        return mIsFooterEnabled;
    }

    public void setFooterEnabled(boolean footerEnabled) {
        if (mIsFooterEnabled && !footerEnabled) {
            notifyItemRemoved(mData.size() + 1);
        } else if (!mIsFooterEnabled && footerEnabled) {
            notifyItemInserted(mData.size());
        }
        mIsFooterEnabled = footerEnabled;
    }

    public int getContentHeight() {
        return (int) (Math.ceil(mData.size() / (float) mColumnCount) * mComputedPair.second);
    }

    public int getWidthOfCell() {
        return mComputedPair.first;
    }

    public int getHeightOfCell() {
        return mComputedPair.second;
    }
}
