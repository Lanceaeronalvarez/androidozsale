package au.com.dealsdirect.ui.controller.saleitems;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import au.com.dealsdirect.BuildConfig;
import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.saleitems.SaleItemProduct;
import au.com.dealsdirect.utils.CommonUtils;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.PriceUtils;
import au.com.dealsdirect.utils.ScreenUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * dp Created by Admin on 6/8/17.
 */

public class SaleItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int SCREEN_TRANSITION_DELAY = 2000;

    private List<SaleItemProduct> mData;
    private Activity mActivity;
    private SaleItemsMvpPresenter mPresenter;
    private String mSaleId;
    private int mColumnCount;
    private static final int FOOTER_VIEW = 1;
    private int mMinColumn;
    private boolean mIsFooterEnabled = true;
    private int mCurrentItemCount = -1;
    private OnClickFreeDeliveryListener mListener;

    private Pair<Integer, Integer> mComputedPair;

    private SaleItemsMvpPresenter.WishlistDelayedCallback delayedCallbackForWishlist = null;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.vh_sale_item_image)
        ImageView image;

        @BindView(R.id.vh_sale_item_sold_out)
        TextView soldout;

        @BindView(R.id.vh_sale_item_like_button)
        ImageButton likeButton;

        @BindView(R.id.vh_sale_item_name)
        public TextView name;

        @BindView(R.id.vh_sale_item_brand)
        TextView brand;

        @BindView(R.id.vh_sale_item_price)
        public TextView price;

        @BindView(R.id.vh_sale_item_old_price)
        public TextView oldPrice;

        @BindView(R.id.vh_sale_item_frame)
        RelativeLayout layout;

        @BindView(R.id.vh_sale_item_free_delivery)
        ImageView freeDelivery;

        @BindView(R.id.vh_sale_item_discount)
        TextView discountPercentTextView;

        @BindView(R.id.vh_sale_item_discounted_price)
        TextView discountedPriceTextView;

        @Nullable
        @BindView(R.id.adView_banner)
        View adView;

        ViewHolder(View view, Pair<Integer, Integer> pair) {
            super(view);
            ButterKnife.bind(this, view);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
            params.width = pair.first;
            params.height = pair.second;
            layout.setLayoutParams(params);
        }

        private boolean isLiked = true;

        public boolean isLiked() {
            return isLiked;
        }

        public void setLiked(boolean liked) {
            int drawableId = liked ? R.drawable.wishlist_product_list_active : R.drawable.wishlist_product_list_inactive;
            likeButton.setImageDrawable(likeButton.getContext().getResources().getDrawable(drawableId));
            isLiked = liked;
        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.adView_banner)
        View adView;

        FooterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public SaleItemsAdapter(
            Activity activity,
            List<SaleItemProduct> saleItems,
            SaleItemsMvpPresenter presenter,
            String saleId, int minColumn,
            SaleItemsMvpPresenter.WishlistDelayedCallback delayedCallbackForWishlist,
            OnClickFreeDeliveryListener listener) {

        this.mActivity = activity;
        this.mData = saleItems;
        this.mPresenter = presenter;
        this.mSaleId = saleId;
        this.mMinColumn = minColumn;
        this.delayedCallbackForWishlist = delayedCallbackForWishlist;
        this.mListener = listener;

        computeItemViewDimensions();
    }

    public void computeItemViewDimensions() {

        int orientation = mActivity.getResources().getConfiguration().orientation;
        boolean isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE;
        float screenDensity = ScreenUtils.getScreenDensity(mActivity);

        int portraitSize;
        int landscapeSize;
        if (mMinColumn == mActivity.getResources().getInteger(R.integer.items_min_column_portrait)) {
            portraitSize = mActivity.getResources().getInteger(R.integer.items_min_column_portrait);
            landscapeSize = mActivity.getResources().getInteger(R.integer.items_min_column_landscape);
        } else {
            portraitSize = mActivity.getResources().getInteger(R.integer.items_max_column_portrait);
            landscapeSize = mActivity.getResources().getInteger(R.integer.items_max_column_landscape);
        }

        ImageUtils.Grid gridDefinition = ImageUtils.getRangedGridDefinition((int) (getInteger(R.integer.item_image_width) * screenDensity),
                (int) (getInteger(R.integer.item_image_height) * screenDensity), (float) ScreenUtils.getScreenWidth(mActivity), isLandscape ?
                        landscapeSize : portraitSize,
                isLandscape ? landscapeSize : portraitSize);

        mColumnCount = gridDefinition.getColumn();
        mComputedPair = new Pair<>((int) gridDefinition.getItemWidth(), (int) gridDefinition.getItemHeight());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        if (viewType != FOOTER_VIEW) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.viewholder_sale_item, parent, false);
            return new ViewHolder(view, mComputedPair);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.footer_ads, parent, false);
            return new FooterViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder.getItemViewType() == 0 && mData.size() != 0 && holder instanceof SaleItemsAdapter.ViewHolder) {
            SaleItemProduct product = mData.get(position);
            if (product == null) {
                setupViewHolderSkeleton((SaleItemsAdapter.ViewHolder) holder, true);
            } else {
                setupViewHolderSkeleton((SaleItemsAdapter.ViewHolder) holder, false);
                setupViewHolder((SaleItemsAdapter.ViewHolder) holder, product);
            }
        } else if (holder instanceof FooterViewHolder) {
            final FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            if (footerViewHolder.adView != null && mPresenter.isGoogleAdsEnabled()) {
                CommonUtils.showAdmob(mActivity, footerViewHolder.adView,
                        mActivity.getResources().getString(R.string.admob_products_id));
            }
        }
    }

    @SuppressLint("CheckResult")
    private void setupViewHolder(SaleItemsAdapter.ViewHolder holder, SaleItemProduct product) {
        String url = product.getImages().isEmpty() ? "" : (product.getImages().size() < 4 ? product.getImages().get(0) : product.getImages().get(1));

        String urlHigherRes = ImageUtils.removeResolutionModifierInImageUrl(url);

        holder.name.setText(product.getName());

        String saleItemPrice = null;
        String saleItemOldPrice = null;

        if (product.getPrice() != null) {
            saleItemPrice = PriceUtils.getPriceStringValue(product.getPrice().getValue());
        }
        if (product.getOriginalPrice() != null) {
            saleItemOldPrice = PriceUtils.getRpStringValue(product.getOriginalPrice().getValue());
        }

        //Added key to check for crash report
        if (!BuildConfig.DEBUG) {
            FirebaseCrashlytics.getInstance().setCustomKey("Brand Name", product.getBrandName());
            FirebaseCrashlytics.getInstance().setCustomKey("Image Url", url);
        }

        ImageUtils.loadImageWithPlaceholder(url,
                holder.image,
                holder.itemView.getContext().getResources().getDrawable(R.drawable.bg_skeleton_stretch),
                null);

        holder.image.setTransitionName(mActivity.getString(R.string.transition_sale_image_indexed, holder.getAdapterPosition()));

        holder.soldout.setVisibility(product.isSoldOut() ? View.VISIBLE : View.GONE);

        final int discountValue = product.getSalePercentOff();
        final double salePriceValue = product.getSalePrice() != null ?
                product.getSalePrice().getValue() : 0;
        String discountedPriceText = PriceUtils.getRpStringValue(salePriceValue);
        String priceText = saleItemPrice;
        if (product.getPriceRangeText() != null && !product.getPriceRangeText().isEmpty()) {
            if (discountValue > 0) {
                discountedPriceText = product.getPriceRangeText() + " " + discountedPriceText;
            } else {
                priceText = product.getPriceRangeText() + " " + priceText;
            }
        }

        holder.brand.setText(product.getBrandName());
        holder.price.setText(priceText);
        holder.oldPrice.setText(saleItemOldPrice);
        holder.oldPrice.setPaintFlags(holder.oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.freeDelivery.setVisibility(product.getFreeDelivery() ? View.VISIBLE : View.GONE);
        holder.discountPercentTextView.setVisibility(discountValue > 0 ? View.VISIBLE : View.GONE);
        holder.discountedPriceTextView.setVisibility(discountValue > 0 ? View.VISIBLE : View.GONE);
        holder.discountPercentTextView.setText(product.getSalePercentOffText());
        holder.discountedPriceTextView.setText(discountedPriceText);

        holder.setLiked(mPresenter.isProductInWishlist(product.getId()));

        RxView.clicks(holder.itemView)
                .throttleFirst(SCREEN_TRANSITION_DELAY, TimeUnit.MILLISECONDS)
                .subscribe(action -> {
                    int[] originalPos = new int[2];
                    holder.itemView.getLocationOnScreen(originalPos);

                    mPresenter.loadProductDetails(
                            holder.getAdapterPosition(),
                            holder.image.getDrawable(),
                            urlHigherRes,
                            product,
                            originalPos[0],
                            originalPos[1],
                            holder.itemView.getWidth(),
                            holder.itemView.getHeight()
                    );
                });

        holder.likeButton.setOnClickListener(v -> {
            holder.setLiked(!holder.isLiked());
            if (holder.isLiked()) {
                mPresenter.addToWishlist(
                        product.getId(),
                        product.getSeoIdentifier(),
                        delayedCallbackForWishlist);
            } else {
                mPresenter.removeFromWishlist(
                        product.getId(),
                        delayedCallbackForWishlist);

            }
        });

        holder.freeDelivery.setOnClickListener(v -> {
            mListener.onClickFreeDelivery(String.valueOf(product.getDeliveryThreshold()),
                    product.getDeliveryType());
        });

    }

    @SuppressLint("CheckResult")
    private void setupViewHolderSkeleton(SaleItemsAdapter.ViewHolder holder, boolean showSkeleton) {
        Drawable skeletonFixedHeightWidthPadding = showSkeleton ?
                holder.itemView.getContext().getResources().getDrawable(R.drawable.bg_skeleton_fixed_height_with_right_padding) : null;
        Drawable skeletonFixedHeight = showSkeleton ? holder.itemView.getContext().getResources().getDrawable(R.drawable.bg_skeleton_fixed_height) : null;
        Drawable skeletonStretch = showSkeleton ? holder.itemView.getContext().getResources().getDrawable(R.drawable.bg_skeleton_stretch) : null;
        holder.image.setImageDrawable(skeletonStretch);

        holder.soldout.setVisibility(View.GONE);

        holder.brand.setText("       ");
        holder.brand.setBackground(skeletonFixedHeight);
        holder.name.setText("     ");
        holder.name.setBackground(skeletonFixedHeightWidthPadding);
        holder.price.setText(" ");
        holder.price.setBackground(skeletonFixedHeightWidthPadding);
        holder.oldPrice.setText(" ");
        holder.oldPrice.setBackground(skeletonFixedHeightWidthPadding);
        holder.freeDelivery.setVisibility(View.GONE);
        holder.discountPercentTextView.setVisibility(View.GONE);
        holder.discountedPriceTextView.setVisibility(View.GONE);
        holder.likeButton.setVisibility(showSkeleton ? View.GONE : View.VISIBLE);
        holder.discountPercentTextView.setText(null);
        holder.discountedPriceTextView.setText(null);

        RxView.clicks(holder.itemView)
                .throttleFirst(SCREEN_TRANSITION_DELAY, TimeUnit.MILLISECONDS)
                .subscribe(action -> {
                });
        holder.likeButton.setOnClickListener(null);
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        if (!mActivity.isDestroyed() && holder instanceof SaleItemsAdapter.ViewHolder) {
            ImageUtils.clearImage(((SaleItemsAdapter.ViewHolder) holder).image);
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

    private float getInteger(int resId) {
        return mActivity.getResources().getInteger(resId);
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
