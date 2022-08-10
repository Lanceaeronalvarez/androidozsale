package au.com.dealsdirect.ui.controller.saleitems;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.saleitems.SaleItemProduct;
import au.com.dealsdirect.ui.controller.priceblock.SaleItemProductAlternatePriceBlockHelper;
import au.com.dealsdirect.ui.controller.priceblock.SaleItemProductPriceBlockHelper;
import au.com.dealsdirect.utils.ImageUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SaleItemViewHolder extends RecyclerView.ViewHolder {
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

    @BindView(R.id.vh_sale_item_image_container)
    RelativeLayout imageContainer;

    @BindView(R.id.price_block_container)
    ViewGroup priceBlockContainer;

    @BindView(R.id.skeleton_price_block_container)
    ViewGroup skeletonPriceBlockContainer;

    @BindView(R.id.vh_sale_item_free_delivery)
    ImageView freeDelivery;

    @BindView(R.id.vh_sale_item_container)
    ViewGroup layout;

    private final boolean isSupplierOriginalPriceInfoEnabled;

    private View.OnClickListener priceInfoOnClickListener = null;

    private SaleItemProductPriceBlockHelper priceBlockHelper = null;

    boolean useAlternatePriceBlockHelper = false;

    public SaleItemViewHolder(View view, Pair<Integer, Integer> pair, boolean isSupplierOriginalPriceInfoEnabled, boolean useAlternatePriceBlockHelper) {
        super(view);
        ButterKnife.bind(this, view);

        ViewGroup.LayoutParams params = layout.getLayoutParams();
        params.width = pair.first;
        params.height = pair.second;
        layout.setLayoutParams(params);

        this.isSupplierOriginalPriceInfoEnabled = isSupplierOriginalPriceInfoEnabled;
        this.useAlternatePriceBlockHelper = useAlternatePriceBlockHelper;
    }

    private boolean isLiked = true;

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        int drawableId = liked ? R.drawable.wishlist_product_details_active : R.drawable.wishlist_product_details_inactive;
        likeButton.setImageDrawable(ContextCompat.getDrawable(likeButton.getContext(), drawableId));
        isLiked = liked;
    }

    @SuppressLint("CheckResult")
    public void setupViewHolderSkeleton(boolean showSkeleton) {
        final Context context = itemView.getContext();
        final Drawable skeletonFixedHeightWidthPadding = showSkeleton ?
                ContextCompat.getDrawable(context, R.drawable.bg_skeleton_fixed_height_with_right_padding) : null;
        final Drawable skeletonFixedHeight = showSkeleton ? ContextCompat.getDrawable(context, R.drawable.bg_skeleton_fixed_height) : null;
        final Drawable skeletonStretch = showSkeleton ? ContextCompat.getDrawable(context, R.drawable.bg_skeleton_stretch) : null;
        image.setImageDrawable(skeletonStretch);

        soldout.setVisibility(View.GONE);

        brand.setText("       ");
        brand.setBackground(skeletonFixedHeight);
        name.setText("     ");
        name.setBackground(skeletonFixedHeightWidthPadding);
        priceBlockContainer.setVisibility(showSkeleton ? View.GONE : View.VISIBLE);
        SaleItemProductPriceBlockHelper.setupSkeleton(skeletonPriceBlockContainer, true, isSupplierOriginalPriceInfoEnabled);
        skeletonPriceBlockContainer.setVisibility(showSkeleton ? View.VISIBLE : View.GONE);
        freeDelivery.setVisibility(View.GONE);
        likeButton.setVisibility(showSkeleton ? View.GONE : View.VISIBLE);

        RxView.clicks(itemView)
                .throttleFirst(2000, TimeUnit.MILLISECONDS)
                .subscribe(action -> {
                });
        likeButton.setOnClickListener(null);
    }

    @SuppressLint("CheckResult")
    public void setupViewHolder(SaleItemProduct product, String imageUrl, boolean isProductInWishlist) {
        name.setText(product.getName());

        if (imageUrl == null) {
            image.setImageDrawable(null);
        } else {
            ImageUtils.loadImageWithPlaceholder(imageUrl,
                    image,
                    ContextCompat.getDrawable(itemView.getContext(), R.drawable.bg_skeleton_stretch),
                    null);
        }

        image.setTransitionName(itemView.getContext().getResources().getString(R.string.transition_sale_image_indexed, getAdapterPosition()));

        soldout.setVisibility(product.isSoldOut() ? View.VISIBLE : View.GONE);

        brand.setText(product.getBrandName());
        freeDelivery.setVisibility(product.getFreeDelivery() ? View.VISIBLE : View.GONE);

        setLiked(isProductInWishlist);

        if (priceBlockHelper == null ||
                (useAlternatePriceBlockHelper && (priceBlockHelper instanceof SaleItemProductAlternatePriceBlockHelper)) ||
                (!useAlternatePriceBlockHelper && !(priceBlockHelper instanceof SaleItemProductAlternatePriceBlockHelper))) {
            priceBlockHelper = useAlternatePriceBlockHelper ?
                    new SaleItemProductAlternatePriceBlockHelper(priceBlockContainer) :
                    new SaleItemProductPriceBlockHelper(priceBlockContainer);
        }
        priceBlockHelper.setup(product, isSupplierOriginalPriceInfoEnabled);
        priceBlockHelper.setFreeDeliveryTextViewText(null);
        priceBlockHelper.setPriceInfoOnClickListener(priceInfoOnClickListener);
    }

    public boolean isUseAlternatePriceBlockHelper() {
        return useAlternatePriceBlockHelper;
    }

    public void setUseAlternatePriceBlockHelper(boolean useAlternatePriceBlockHelper) {
        this.useAlternatePriceBlockHelper = useAlternatePriceBlockHelper;
        if (priceBlockHelper != null &&
                ((useAlternatePriceBlockHelper && (priceBlockHelper instanceof SaleItemProductAlternatePriceBlockHelper)) ||
                        (!useAlternatePriceBlockHelper && !(priceBlockHelper instanceof SaleItemProductAlternatePriceBlockHelper)))) {
            priceBlockHelper = useAlternatePriceBlockHelper ?
                    new SaleItemProductAlternatePriceBlockHelper(priceBlockContainer) :
                    new SaleItemProductPriceBlockHelper(priceBlockContainer);
        }
    }

    public void setLikeButtonOnClickListener(View.OnClickListener onClickListener) {
        likeButton.setOnClickListener(onClickListener);
    }

    public void setPriceInfoOnClickListener(View.OnClickListener onClickListener) {
        priceInfoOnClickListener = onClickListener;
        if (priceBlockHelper != null) {
            priceBlockHelper.setPriceInfoOnClickListener(onClickListener);
        }
    }
}
