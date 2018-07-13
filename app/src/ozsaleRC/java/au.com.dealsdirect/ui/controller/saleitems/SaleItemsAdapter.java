package au.com.dealsdirect.ui.controller.saleitems;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

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
    private static final int SALE_ITEM_IMAGE_WIDTH = 225;
    private static final int SALE_ITEM_IMAGE_HEIGHT = 360;
    private static final int SCREEN_PADDING_MULTIPLIER = 15;

    private List<GetSaleItemsResponse.Products> mData;
    private Context mContext;
    private SaleItemsMvpPresenter mPresenter;
    private String mSaleId;

    private int mComputedHeight = 0;

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

        ViewHolder(View view, int height) {
            super(view);
            ButterKnife.bind(this, view);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
            params.height = height;
            layout.setLayoutParams(params);
        }
    }

    public SaleItemsAdapter(
            Context context,
            List<GetSaleItemsResponse.Products> saleItems,
            SaleItemsMvpPresenter presenter,
            String saleId) {

        this.mContext = context;
        this.mData = saleItems;
        this.mPresenter = presenter;
        this.mSaleId = saleId;

        // Dynamic Height Computation
        int columns = mPresenter.isTablet() ? mContext.getResources().getInteger(R.integer.sale_items_tablet_column_count)
                : mContext.getResources().getInteger(R.integer.sale_items_phone_column_count);
        int screenWidth = (int) (ScreenUtils.getScreenWidth(mContext) / columns - (SCREEN_PADDING_MULTIPLIER * ScreenUtils.getScreenDensity(mContext)));
        mComputedHeight = ImageUtils.getComputedBannerHeight(SALE_ITEM_IMAGE_WIDTH, SALE_ITEM_IMAGE_HEIGHT, screenWidth);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_sale_item, parent, false);
        return new ViewHolder(view, mComputedHeight);
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

//        ImageUtils.clearImage(mContext,holder.mSaleItemImage);

        ImageUtils.loadImage(mContext, url, holder.image);

        holder.image.setTransitionName(mContext.getString(R.string.transition_sale_image_indexed, position));

        holder.soldout.setVisibility(saleItem.isSoldOut() ? View.VISIBLE : View.GONE);

        holder.brand.setText(saleItemBrand);
        holder.price.setText(saleItemPrice);
        holder.oldPrice.setText(saleItemOldPrice);
        holder.oldPrice.setPaintFlags(holder.oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


        holder.itemView.setOnClickListener(v -> mPresenter.loadProductDetails(
                holder,
                position,
                mData.get(position).getSeoIdentifier(),
                url,
                mData.get(position).getSkus().isEmpty() ? "" : mData.get(position).getSkus().get(0).getId(),
                mSaleId));


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
}
