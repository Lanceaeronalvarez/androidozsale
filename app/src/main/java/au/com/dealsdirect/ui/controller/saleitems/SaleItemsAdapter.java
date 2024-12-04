package au.com.dealsdirect.ui.controller.saleitems;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.collect.ImmutableList;

import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.saleitems.SaleItemProduct;
import au.com.dealsdirect.ui.controller.priceblock.SaleItemProductPriceBlockHelper;
import au.com.dealsdirect.utils.CommonUtils;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.ScreenUtils;

public class SaleItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private int mColumnCount;
    private static final int FOOTER_VIEW = 1;
    private final int mMinColumn;
    private final SaleItemAdapterHelper helper;
    private final boolean isPriceInfoClickable;
    private final boolean isSupplierOriginalPriceInfoEnabled;
    private Pair<Integer, Integer> mComputedPair;

    private final SaleItemsMvpPresenter.WishlistDelayedCallback delayedCallbackForWishlist;

    private boolean useAlternatePriceBlockHelper = false;

    private final DiffUtil.ItemCallback<SaleItemProduct> diffUtilItemCallback = new DiffUtil.ItemCallback<SaleItemProduct>() {
        @Override
        public boolean areItemsTheSame(@NonNull SaleItemProduct oldItem, @NonNull SaleItemProduct newItem) {
            if (oldItem instanceof SaleItemFooterPlaceholder) {
                if (newItem instanceof SaleItemFooterPlaceholder) {
                    return ((SaleItemFooterPlaceholder) oldItem).getViewType() == ((SaleItemFooterPlaceholder) newItem).getViewType();
                } else {
                    return false;
                }
            } else if (newItem instanceof SaleItemFooterPlaceholder) {
                return false;
            }
            if (oldItem.getSeoIdentifier() == null) {
                if (newItem.getSeoIdentifier() == null) {
                    if (oldItem.getId() == null) {
                        return newItem.getId() == null;
                    }
                    return oldItem.getId().equals(newItem.getId());
                } else {
                    return false;
                }
            }
            return oldItem.getSeoIdentifier().equals(newItem.getSeoIdentifier());
        }

        @Override
        public boolean areContentsTheSame(@NonNull SaleItemProduct oldItem, @NonNull SaleItemProduct newItem) {
            if (oldItem instanceof SaleItemFooterPlaceholder) {
                if (newItem instanceof SaleItemFooterPlaceholder) {
                    return ((SaleItemFooterPlaceholder) oldItem).getViewType() == ((SaleItemFooterPlaceholder) newItem).getViewType();
                } else {
                    return false;
                }
            } else if (newItem instanceof SaleItemFooterPlaceholder) {
                return false;
            }
            return helper.isProductInWishlist(oldItem) == helper.isProductInWishlist(newItem);
        }
    };

    private final AsyncListDiffer<SaleItemProduct> asyncListDiffer = new AsyncListDiffer<>(this, diffUtilItemCallback);

    public SaleItemsAdapter(
            Context context,
            ImmutableList<SaleItemProduct> saleItems,
            int minColumn,
            boolean isPriceInfoClilckable,
            boolean isSupplierOriginalPriceInfoEnabled,
            boolean useAlternatePriceBlockHelper,
            SaleItemsMvpPresenter.WishlistDelayedCallback delayedCallbackForWishlist,
            SaleItemAdapterHelper listener) {

        updateData(saleItems);
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
                context.getResources().getDimension((R.dimen.product_list_item_like_button_size)) +
                context.getResources().getDimension((R.dimen.product_list_text_view_height)) +
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
                    .inflate(R.layout.footer_container, parent, false);
            return new SaleItemFooterViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.viewholder_sale_item, parent, false);
            return new SaleItemViewHolder(view, mComputedPair, isSupplierOriginalPriceInfoEnabled, useAlternatePriceBlockHelper, false);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Context context = holder.itemView.getContext();

        final List<SaleItemProduct> data = asyncListDiffer.getCurrentList();
        final SaleItemProduct product = data.get(position);

        if (holder.getItemViewType() == 0 && !data.isEmpty() && holder instanceof SaleItemViewHolder) {
            final SaleItemViewHolder saleItemViewHolder = (SaleItemViewHolder) holder;
            if (product == null) {
                saleItemViewHolder.setupViewHolderSkeleton(true);
            } else {
                saleItemViewHolder.setupViewHolderSkeleton(false);
                final List<String> images = product.getImages();
                final String imageUrl = images == null || images.isEmpty() ? "" : (product.getImages().size() < 4 ? product.getImages().get(0) : product.getImages().get(1));
                saleItemViewHolder.setupViewHolder(product, imageUrl, helper.isProductInWishlist(product));
                setupViewHolderWithPriceBlockClicks(saleItemViewHolder, imageUrl, product, position);
            }
        } else if (holder instanceof SaleItemFooterViewHolder) {
            final SaleItemFooterViewHolder footerViewHolder = (SaleItemFooterViewHolder) holder;
            final SaleItemFooterPlaceholder itemFooterPlaceholder = (SaleItemFooterPlaceholder) product;
            switch (itemFooterPlaceholder.getViewType()) {
                case SaleItemFooterPlaceholder.VIEW_TYPE_ADMOB:
                    if (footerViewHolder.contentView != null && helper.isGoogleAdsEnabled()) {
                        CommonUtils.showAdmob(context, footerViewHolder.contentView,
                                context.getResources().getString(R.string.admob_products_id));
                    }
                    break;
                default:
                    if (footerViewHolder.contentView != null) {
                        footerViewHolder.contentView.removeAllViews();
                    }
                    break;
            }
        }
    }

    @SuppressLint("CheckResult")
    public void setupViewHolderWithPriceBlockClicks(SaleItemViewHolder viewHolder, String imageUrl, SaleItemProduct product, final int index) {
        final String urlHigherRes = ImageUtils.removeResolutionModifierInImageUrl(imageUrl);

        viewHolder.itemView.setOnClickListener(v -> {
            int[] originalPos = new int[2];
            viewHolder.itemView.getLocationOnScreen(originalPos);

            int pos = viewHolder.getBindingAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) {
                pos = index;
            }

            helper.onItemClicked(
                    pos,
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

    public void updateData(ImmutableList<SaleItemProduct> saleItems) {
        updateData(saleItems, false);
    }

    public void updateData(ImmutableList<SaleItemProduct> saleItems, boolean willEmptyFirst) {
        if (willEmptyFirst) {
            asyncListDiffer.submitList(null);
        }
        asyncListDiffer.submitList(saleItems);
    }

    @Override
    public int getItemCount() {
        return asyncListDiffer.getCurrentList().size();
    }

    public List<SaleItemProduct> getData() {
        return asyncListDiffer.getCurrentList();
    }

    public int getColumnCount() {
        return mColumnCount;
    }

    @Override
    public int getItemViewType(int position) {
        final List<SaleItemProduct> data = asyncListDiffer.getCurrentList();
        if (data.get(position) instanceof SaleItemFooterPlaceholder) {
            return FOOTER_VIEW;
        }
        return super.getItemViewType(position);
    }

    public int getContentHeight() {
        List<SaleItemProduct> data = asyncListDiffer.getCurrentList();
        return (int) (Math.ceil(data.size() / (float) mColumnCount) * mComputedPair.second);
    }

    public int getWidthOfCell() {
        return mComputedPair.first;
    }

    public int getHeightOfCell() {
        return mComputedPair.second;
    }
}
