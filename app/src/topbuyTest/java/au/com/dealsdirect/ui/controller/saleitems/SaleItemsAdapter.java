package au.com.dealsdirect.ui.controller.saleitems;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jakewharton.rxbinding2.view.RxView;
import com.mysale.genie.animation.AnimationEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.PriceUtils;
import au.com.dealsdirect.utils.ScreenUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by smartwave on 07/11/2017.
 */

public class SaleItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public HeaderViewHolder getHeaderViewHolderInstance() {
        return headerViewHolderInstance;
    }

    private HeaderViewHolder headerViewHolderInstance;
    private List<GetSaleItemsResponse.Products> mData = new ArrayList<>();
    private MainActivity mActivity;
    private SaleItemsMvpPresenter mPresenter;
    private SaleItemsMvpView mvpView;

    private int mComputedHeight = 0;

    public SaleItemsAdapter(List<GetSaleItemsResponse.Products> mData, MainActivity activity, SaleItemsMvpPresenter mPresenter, SaleItemsMvpView mvpView) {
        this.mData = mData;
        this.mActivity = activity;
        this.mPresenter = mPresenter;
        this.mvpView = mvpView;

        // Dynamic Height Computation
        int columns = mPresenter.isTablet() ? 4 : 2;
        int screenWidth = (int) (ScreenUtils.getScreenWidth(mActivity) / columns - (15 * ScreenUtils.getScreenDensity(mActivity)));
        mComputedHeight = ImageUtils.getComputedBannerHeight(225, 360, screenWidth);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = null;
        View v = null;
        if (viewType == 0) {
            Log.d("SaleItemsAdapter", "onCreateViewHolder header");
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_sale_item_header, parent, false);
            vh = headerViewHolderInstance = new HeaderViewHolder(v, mPresenter);
        } else if (viewType == 1) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_sale_item, parent, false);
            vh = new ShopItemsViewHolder(v, mComputedHeight);
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {

        if (vh instanceof ShopItemsViewHolder) {
            ShopItemsViewHolder holder = (ShopItemsViewHolder) vh;

            GetSaleItemsResponse.Products saleItem = mData.get(position - 1);
            String url = saleItem.getImages().isEmpty() ? "" : saleItem.getImages().get(0);

            holder.productName.setText(saleItem.getProductName());

            String saleItemBrand = saleItem.getProductName();

            if (saleItem.getSkus() != null)
                if (!saleItem.getSkus().isEmpty())
                    if (saleItem.getSkus().get(0).getBrandName() != null)
                        saleItemBrand = saleItem.getSkus().get(0).getBrandName();

            if (saleItem.getLabelText() == null) {
                holder.discountLabel.setVisibility(View.GONE);
            } else {
//                AnimationEngine.Builder.animate(holder.discountLabel).fadeIn().setDuration(200).build().start();
                holder.discountLabel.setVisibility(View.VISIBLE);
                holder.discountLabel.setText(saleItem.getLabelText());
            }

            String saleItemPrice = PriceUtils.getPriceStringValue(saleItem.getPrice().getValue());
            String saleItemOldPrice = PriceUtils.getRpStringValue(saleItem.getOriginalPrice().getValue());

//        ImageUtils.clearImage(mContext,holder.mSaleItemImage);

            ImageUtils.loadImage(mActivity, url, holder.productImage);

            holder.productImage.setTransitionName(mActivity.getString(R.string.transition_sale_image_indexed, position));

//            holder.soldout.setVisibility(saleItem.isSoldOut() ? View.VISIBLE : View.GONE);

//            holder..setText(saleItemBrand);
            holder.productPrice.setText(saleItemPrice);
            holder.productPreviousPrice.setText(saleItemOldPrice);
            holder.productPreviousPrice.setPaintFlags(holder.productPreviousPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


            holder.itemView.setOnClickListener(v -> {
                if(!mvpView.isChangeStarted() || mvpView.isDefaultBool()) {
                    mPresenter.loadProductDetails(
                            holder,
                            position,
                            saleItem.getSeoIdentifier(),
                            url,
                            saleItem.getSkus().isEmpty() ? "" : saleItem.getSkus().get(0).getId(),
                            "");
                }
            });
        }

        if (vh instanceof HeaderViewHolder) {
            HeaderViewHolder holder = (HeaderViewHolder) vh;
//            ImageUtils.loadImage(mContext, url, holder.headerImage);
            Glide.with(mActivity).load(R.drawable.bg_sale_item_header)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .centerCrop().into(holder.headerImage);
//            Glide.with(mActivity).load(R.drawable.bg_sale_item_header)
//                    .asBitmap()
//                    .encoder(new BitmapEncoder(Bitmap.CompressFormat.JPEG, 50))
//                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                    .skipMemoryCache(true)
//                    .format(DecodeFormat.PREFER_RGB_565)
//                    .centerCrop().into(holder.headerImage);

            holder.shopTextView.setVisibility(View.VISIBLE);
            holder.headerUnderline.setVisibility(View.VISIBLE);

            if (mvpView.getChosenCategory().isEmpty()) {
                holder.shopTextView.setText(mActivity.getString(R.string.category_default));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData.size() + 1;
    }

    public void replaceData(List<GetSaleItemsResponse.Products> saleItems) {
        mData = saleItems;
        notifyDataSetChanged();
    }

    public void addData(List<GetSaleItemsResponse.Products> saleItems) {
        mData.addAll(saleItems);
        notifyDataSetChanged();
    }

    public List<GetSaleItemsResponse.Products> getData() {
        return mData;
    }

    @Override
    public int getItemViewType(int position) {
        //header
        if (position == 0) {
            return 0;
        }

        return 1;

    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder implements CategoryObserver {

        @BindView(R.id.welcome_header_layout)
        LinearLayout welcomeHeaderTextLayout;
        @BindView(R.id.main_page_header_text)
        TextView shopTextView;
        @BindView(R.id.header_underline)
        View headerUnderline;
        @BindView(R.id.header_image)
        ImageView headerImage;
        @BindView(R.id.no_search_items_layout)
        LinearLayout noSearchItemsLayout;
        @BindView(R.id.header_separator)
        View headerSeparator;
        @BindView(R.id.see_other_popular_products_text)
        TextView seeOtherPopularProductsText;
        @BindView(R.id.no_items_text)
        TextView noItemsText;

        SaleItemsMvpPresenter mPresenter;

//        public SimpleFilterCategoryClickObserver getFilterCategoryClickObserver() {
//            return mFilterCategoryClickObserver;
//        }
//
//        private SimpleFilterCategoryClickObserver mFilterCategoryClickObserver;

        public LinearLayout getNoSearchItemsLayout() {
            return noSearchItemsLayout;
        }

        public View getHeaderSeparator() {
            return headerSeparator;
        }

        public TextView getSeeOtherPopularProductsText() {
            return seeOtherPopularProductsText;
        }

        public TextView getNoItemsText() {
            return noItemsText;
        }

        public HeaderViewHolder(View itemView, SaleItemsMvpPresenter presenter) {
            super(itemView);
            mPresenter = presenter;
            ButterKnife.bind(this, itemView);
            headerImage.setColorFilter(new PorterDuffColorFilter(Color.parseColor("#6c000000"), PorterDuff.Mode.SRC_OVER));


            RxView.clicks(shopTextView)
                    .throttleFirst(1000, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(action -> {
                        AnimationEngine.Builder.animate(shopTextView).fadeOut().build().start();
                        AnimationEngine.Builder.animate(headerUnderline).fadeOut().build().start();
                        mPresenter.showCategoriesController();
                    });


        }

        @Override
        public void onCategoryChangeUpdateUI(String text, int color) {
            Log.d("SaleItemsAdapter", "updating " + text + " color " + color);
            shopTextView.setText(text);
            welcomeHeaderTextLayout.setBackgroundColor(color);
        }

        @Override
        public void showShopCategoryText() {
            shopTextView.setVisibility(View.VISIBLE);
            headerUnderline.setVisibility(View.VISIBLE);
            if (shopTextView.getAlpha() != 1f) {
                AnimationEngine.Builder.animate(shopTextView).fadeIn().build().start();
                AnimationEngine.Builder.animate(headerUnderline).fadeIn().build().start();
            }
        }
    }

    public static class ShopItemsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_card_view)
        public CardView cardView;
        @BindView(R.id.vh_uppercard)
        public RelativeLayout upperCard;
        @BindView(R.id.vh_sale_item_discount)
        public TextView discountLabel;
        @BindView(R.id.vh_sale_item_price)
        public TextView productPrice;
        @BindView(R.id.vh_sale_item_old_price)
        public TextView productPreviousPrice;
        @BindView(R.id.vh_sale_item_image)
        public ImageView productImage;
        @BindView(R.id.vh_sale_item_name)
        public TextView productName;

        public ShopItemsViewHolder(View v, int computedHeight) {
            super(v);

            ButterKnife.bind(this, v);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) upperCard.getLayoutParams();
            params.height = computedHeight;
            upperCard.setLayoutParams(params);
        }

    }
}
