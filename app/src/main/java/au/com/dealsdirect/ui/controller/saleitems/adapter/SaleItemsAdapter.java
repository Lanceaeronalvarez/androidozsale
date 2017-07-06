package au.com.dealsdirect.ui.controller.saleitems.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsMvpPresenter;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.PriceUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * dp Created by Admin on 6/8/17.
 */

public class SaleItemsAdapter extends RecyclerView.Adapter<SaleItemsAdapter.ViewHolder> {

    private List<GetSaleItemsResponse.Products> mData;
    private Context mContext;
    private SaleItemsMvpPresenter mPresenter;
    private String mSaleId;
    private String mSaleName;

    public void addData(List<GetSaleItemsResponse.Products> saleItems) {
        mData.addAll(saleItems);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cell_product_image)
        ImageView mSaleItemImage;

        @BindView(R.id.sale_item_name)
        TextView mSaleItemName;

        @BindView(R.id.sale_item_brand)
        TextView mSaleBrand;

        @BindView(R.id.sale_item_price)
        TextView mSalePrice;

        @BindView(R.id.sale_item_old_price)
        TextView mOldPrice;

        @BindView(R.id.sale_item_container)
        RelativeLayout mCardView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public SaleItemsAdapter(
            List<GetSaleItemsResponse.Products> saleItems,
            SaleItemsMvpPresenter presenter,
            String saleId,
            String saleName) {

        this.mData = saleItems;
        this.mPresenter = presenter;
        this.mSaleId = saleId;
        this.mSaleName = saleName;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.viewholder_sale_item, parent, false);
        return new ViewHolder(view);
    }

    @Override public void onBindViewHolder(ViewHolder holder, final int position) {
        GetSaleItemsResponse.Products saleItem = mData.get(position);
        String url = mData.get(position).getImages().get(0);

        holder.mSaleItemName.setText(saleItem.getProductName());
        holder.mSaleItemImage.setTransitionName(mData.get(position).getProductId());

        String saleItemBrand = saleItem.getProductName();

        if (mData.get(position).getSkus()!=null)
            if (!mData.get(position).getSkus().isEmpty())
                if (mData.get(position).getSkus().get(0).getBrandName()!=null)
                    saleItemBrand = mData.get(position).getSkus().get(0).getBrandName();

        String saleItemPrice = PriceUtils.getPriceStringValue(mData.get(position).getPrice().getValue());
        String saleItemOldPrice = PriceUtils.getRpStringValue(mData.get(position).getOriginalPrice().getValue());

        ImageUtils.loadImageWithImageViewDimens(mContext, url, holder.mSaleItemImage);

        holder.mSaleBrand.setText(saleItemBrand);
        holder.mSalePrice.setText(saleItemPrice);
        holder.mOldPrice.setText(saleItemOldPrice);
        holder.mOldPrice.setPaintFlags(
                holder.mOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        holder.itemView.setOnClickListener(v -> mPresenter.loadProductDetails(
                mData.get(position).getSeoIdentifier(),
                url,
                mData.get(position).getProductId(),
                mSaleId));
    }

    @Override public int getItemCount() {
        return mData.size();
    }
}
