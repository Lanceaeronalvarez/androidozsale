package au.com.dealsdirect.ui.controller.saleitems;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
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

    private List<GetSaleItemsResponse.Products> mData;
    private Activity mActivity;
    private SaleItemsMvpPresenter mPresenter;
    private String mSaleId;
    private int mColumnCount;

    private Pair<Integer, Integer> mComputedPair;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.vh_sale_item_image)
        ImageView image;

        @BindView(R.id.vh_sale_item_sold_out)
        TextView soldout;

        @BindView(R.id.vh_sale_item_name)
        public TextView name;

        @BindView(R.id.vh_sale_item_brand)
        TextView brand;

        @BindView(R.id.vh_sale_item_price)
        public TextView price;

        @BindView(R.id.vh_sale_item_old_price)
        public TextView oldPrice;

        @BindView(R.id.vh_sale_item_frame)
        FrameLayout layout;

        @BindView(R.id.vh_sale_item_free_delivery)
        ImageView freeDelivery;

        @BindView(R.id.vh_sale_item_discount)
        TextView discount;

        @BindView(R.id.vh_sale_item_sale_price)
        TextView salePrice;

        ViewHolder(View view, Pair<Integer, Integer> pair) {
            super(view);
            ButterKnife.bind(this, view);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
            params.width = pair.first;
            params.height = pair.second;
            layout.setLayoutParams(params);
        }
    }

    public SaleItemsAdapter(
            Activity activity,
            List<GetSaleItemsResponse.Products> saleItems,
            SaleItemsMvpPresenter presenter,
            String saleId) {

        this.mActivity = activity;
        this.mData = saleItems;
        this.mPresenter = presenter;
        this.mSaleId = saleId;

        computeItemViewDimensions();
    }

    public void computeItemViewDimensions() {

        int orientation = mActivity.getResources().getConfiguration().orientation;
        boolean isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE;
        int screenDensity = (int) ScreenUtils.getScreenDensity(mActivity);
        ImageUtils.Grid gridDefinition = ImageUtils.getRangedGridDefinition((int) getInteger(R.integer.item_image_width) * screenDensity,
                (int) getInteger(R.integer.item_image_height) * screenDensity, (float) ScreenUtils.getScreenWidth(mActivity),3);
        if(!mPresenter.isTablet()) gridDefinition = ImageUtils.getRangedGridDefinition((int) getInteger(R.integer.item_image_width) * screenDensity,
                (int) getInteger(R.integer.item_image_height) * screenDensity, (float) ScreenUtils.getScreenWidth(mActivity), isLandscape ? 4 : 3, 4);
        mColumnCount = gridDefinition.getColumn();
        mComputedPair = new Pair<>((int) gridDefinition.getItemWidth(),(int) gridDefinition.getItemHeight());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_sale_item, parent, false);
        return new ViewHolder(view, mComputedPair);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        GetSaleItemsResponse.Products saleItem = mData.get(position);
        String url = mData.get(position).getImages().isEmpty() ? "" : mData.get(position).getImages().get(0);

        holder.name.setText(saleItem.getProductName());

        String saleItemBrand = saleItem.getProductName();

        if (mData.get(position).getSkus() != null)
            if (!mData.get(position).getSkus().isEmpty())
                if (mData.get(position).getSkus().get(0).getBrandName() != null)
                    saleItemBrand = mData.get(position).getSkus().get(0).getBrandName();

        String saleItemPrice = PriceUtils.getPriceStringValue(mData.get(position).getPrice().getValue());
        String saleItemOldPrice = PriceUtils.getRpStringValue(mData.get(position).getOriginalPrice().getValue());

        ImageUtils.loadImage(url, holder.image);

        holder.image.setTransitionName(mActivity.getString(R.string.transition_sale_image_indexed, position));

        holder.soldout.setVisibility(saleItem.isSoldOut() ? View.VISIBLE : View.GONE);

        holder.brand.setText(saleItemBrand);
        holder.price.setText(saleItemPrice);
        holder.oldPrice.setText(saleItemOldPrice);
        holder.oldPrice.setPaintFlags(holder.oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.freeDelivery.setVisibility(mData.get(position).getFreeDelivery() ? View.VISIBLE : View.GONE);
        int discountValue = mData.get(position).getSalePercentOff().intValue();
        double salePriceValue = mData.get(position).getSalePrice().getValue();
        holder.discount.setVisibility(discountValue > 0 ? View.VISIBLE : View.GONE);
        holder.salePrice.setVisibility(discountValue > 0 ? View.VISIBLE : View.GONE);
        holder.discount.setText(mData.get(position).getSalePercentOffText());
        holder.salePrice.setText(PriceUtils.getRpStringValue(salePriceValue));


        RxView.clicks(holder.itemView)
                .throttleFirst(SCREEN_TRANSITION_DELAY, TimeUnit.MILLISECONDS)
                .subscribe(action -> mPresenter.loadProductDetails(
                        holder,
                        position,
                        mData.get(position).getSeoIdentifier(),
                        url,
                        mData.get(position).getSkus() == null || mData.get(position).getSkus().isEmpty() ? "" :
                                mData.get(position).getSkus().get(0).getId(),
                        mSaleId,
                        mData.get(position).getFreeDelivery()));

    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        if (!mActivity.isDestroyed()) {
            ImageUtils.clearImage(holder.image);
        }
        super.onViewDetachedFromWindow(holder);
    }

    public void replaceData(List<GetSaleItemsResponse.Products> saleItems) {
        mData = saleItems;
        notifyDataSetChanged();
    }

    public void addData(List<GetSaleItemsResponse.Products> saleItems) {
        int previousCount = mData.size();
        mData.addAll(saleItems);
        notifyItemRangeInserted(previousCount, mData.size() - previousCount);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public List<GetSaleItemsResponse.Products> getData() {
        return mData;
    }

    public int getColumnCount() {
        return mColumnCount;
    }

    private float getInteger(int resId) {
        return mActivity.getResources().getInteger(resId);
    }
}
