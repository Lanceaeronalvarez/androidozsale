package au.com.dealsdirect.ui.controller.saleitems;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Paint;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse.Products;
import au.com.dealsdirect.utils.CommonUtils;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.PriceUtils;
import au.com.dealsdirect.utils.ScreenUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * dp Created by Admin on 6/8/17.
 */

public class SaleItemsAdapter extends RecyclerView.Adapter<SaleItemsAdapter.ViewHolder> {
    private static final int SCREEN_TRANSITION_DELAY = 2000;

    private List<Products> mData;
    private Activity mActivity;
    private SaleItemsMvpPresenter mPresenter;
    private String mSaleId;
    private int mColumnCount;
    private static final int FOOTER_VIEW = 1;
    private int mMinColumn;
    private boolean mIsFooterEnabled = true;
    private int mCurrentItemCount = -1;

    private Pair<Integer, Integer> mComputedPair;

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
        TextView discount;

        @BindView(R.id.vh_sale_item_sale_price)
        TextView salePrice;

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

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
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

    public SaleItemsAdapter(
            Activity activity,
            List<Products> saleItems,
            SaleItemsMvpPresenter presenter,
            String saleId, int minColumn) {

        this.mActivity = activity;
        this.mData = saleItems;
        this.mPresenter = presenter;
        this.mSaleId = saleId;
        this.mMinColumn = minColumn;

        computeItemViewDimensions();
    }

    public void computeItemViewDimensions() {

        int orientation = mActivity.getResources().getConfiguration().orientation;
        boolean isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE;
        int screenDensity = (int) ScreenUtils.getScreenDensity(mActivity);

        int portraitSize;
        int landscapeSize;
        if (mMinColumn == mActivity.getResources().getInteger(R.integer.items_min_column_portrait)) {
            portraitSize = mActivity.getResources().getInteger(R.integer.items_min_column_portrait);
            landscapeSize = mActivity.getResources().getInteger(R.integer.items_min_column_landscape);
        } else {
            portraitSize = mActivity.getResources().getInteger(R.integer.items_max_column_portrait);
            landscapeSize = mActivity.getResources().getInteger(R.integer.items_max_column_landscape);
        }

        ImageUtils.Grid gridDefinition = ImageUtils.getRangedGridDefinition((int) getInteger(R.integer.item_image_width) * screenDensity,
                (int) getInteger(R.integer.item_image_height) * screenDensity, (float) ScreenUtils.getScreenWidth(mActivity), isLandscape ?
                        landscapeSize : portraitSize,
                isLandscape ? landscapeSize : portraitSize);

        mColumnCount = gridDefinition.getColumn();
        mComputedPair = new Pair<>((int) gridDefinition.getItemWidth(), (int) gridDefinition.getItemHeight());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        if (viewType != FOOTER_VIEW) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.viewholder_sale_item, parent, false);
            return new ViewHolder(view, mComputedPair);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.footer_ads, parent, false);
            return new ViewHolder(view);
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        if (holder.getItemViewType() == 0 && mData.size() != 0) {
            Products product = mData.get(position);
            String url = product.getImages().isEmpty() ? "" : (product.getImages().size() < 4 ? product.getImages().get(0) : product.getImages().get(1));

            String urlHigherRes = ImageUtils.removeResolutionModifierInImageUrl(url);

            holder.name.setText(product.getProductName());

            String saleItemPrice = null;
            String saleItemOldPrice = null;

            if (product.getPrice() != null) {
                saleItemPrice = PriceUtils.getPriceStringValue(product.getPrice().getValue());
            }
            if (product.getOriginalPrice() != null) {
                saleItemOldPrice = PriceUtils.getRpStringValue(product.getOriginalPrice().getValue());
            }

            ImageUtils.loadImage(url, holder.image);

            holder.image.setTransitionName(mActivity.getString(R.string.transition_sale_image_indexed, position));

            holder.soldout.setVisibility(product.isSoldOut() ? View.VISIBLE : View.GONE);

            holder.brand.setText(product.getBrandName());
            holder.price.setText(saleItemPrice);
            holder.oldPrice.setText(saleItemOldPrice);
            holder.oldPrice.setPaintFlags(holder.oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.freeDelivery.setVisibility(product.getFreeDelivery() ? View.VISIBLE : View.GONE);
            int discountValue = product.getSalePercentOff();
            double salePriceValue = product.getSalePrice() != null ?
                    product.getSalePrice().getValue() : 0;
            holder.discount.setVisibility(discountValue > 0 ? View.VISIBLE : View.GONE);
            holder.salePrice.setVisibility(discountValue > 0 ? View.VISIBLE : View.GONE);
            holder.discount.setText(product.getSalePercentOffText());
            holder.salePrice.setText(PriceUtils.getRpStringValue(salePriceValue));

            holder.setLiked(mPresenter.isProductInWishlist(product.getProductId()));

            RxView.clicks(holder.itemView)
                    .throttleFirst(SCREEN_TRANSITION_DELAY, TimeUnit.MILLISECONDS)
                    .subscribe(action -> mPresenter.loadProductDetails(
                            holder,
                            holder.getAdapterPosition(),
                            product.getSeoIdentifier(),
                            holder.image.getDrawable(),
                            urlHigherRes,
                            product.getSkus() == null || product.getSkus().isEmpty() ? "" :
                                    product.getSkus().get(0).getId(),
                            mSaleId,
                            product.getFreeDelivery()));

            holder.likeButton.setOnClickListener(v -> {
                holder.setLiked(!holder.isLiked());
                if (holder.isLiked()) {
                    mPresenter.addToWishlist(
                            product.getProductId(),
                            product.getSeoIdentifier());
                } else {
                    mPresenter.removeFromWishlist(product.getProductId());

                }
            });

        } else {
            if (holder.adView != null && mPresenter.isGoogleAdsEnabled()) {
                CommonUtils.showAdmob(mActivity, holder.adView,
                        mActivity.getResources().getString(R.string.admob_products_id));
            }
        }
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        if (!mActivity.isDestroyed()) {
            ImageUtils.clearImage(holder.image);
        }
        super.onViewDetachedFromWindow(holder);
    }

    public void replaceData(List<Products> saleItems) {
        mData = saleItems;
        notifyDataSetChanged();
        mCurrentItemCount = getItemCount();
    }

    public void addData(List<Products> saleItems) {
        int previousCount = mData.size();
        mData.addAll(saleItems);
        notifyItemRangeInserted(previousCount, mData.size() - previousCount);
        mCurrentItemCount = getItemCount();
    }

    @Override
    public int getItemCount() {
        return mData.size() + (mIsFooterEnabled && !mData.isEmpty() ? 1 : 0);
    }

    public List<Products> getData() {
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
