package au.com.dealsdirect.ui.controller.saleitems.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.saleitems.GetPublicSaleItemsResponse;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsMvpPresenter;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.LegacyStringImageUtils;
import au.com.dealsdirect.utils.PriceUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * dp Created by Admin on 6/8/17.
 */

public class SaleItemsAdapter extends RecyclerView.Adapter<SaleItemsAdapter.ViewHolder>{

    List<GetPublicSaleItemsResponse.Item> mData;
    Context mContext;
    SaleItemsMvpPresenter mPresenter;
    String mSaleId;

    public void addData(List<GetPublicSaleItemsResponse.Item> saleItems) {
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

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public SaleItemsAdapter( List<GetPublicSaleItemsResponse.Item> saleItem, SaleItemsMvpPresenter presenter,String saleId) {
        this.mData = saleItem;
        this.mPresenter = presenter;
        this.mSaleId = saleId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_sale_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        GetPublicSaleItemsResponse.Item saleItem = mData.get(position);
        holder.mSaleItemName.setText(saleItem.getName());

        String url = LegacyStringImageUtils.itemImageURLString(saleItem);
        String saleItemBrand = mData.get(position).getBrandName();
        String saleItemPrice =  PriceUtils.getPriceStringValue(mData.get(position).getPrice());
        String saleItemOldPrice = PriceUtils.getRpStringValue(mData.get(position).getRP());

        ImageUtils.loadImageWithImageViewDimens(mContext,url, holder.mSaleItemImage);

        holder.mSaleBrand.setText(saleItemBrand);
        holder.mSalePrice.setText(saleItemPrice);
        holder.mOldPrice.setText(saleItemOldPrice);
        holder.mOldPrice.setPaintFlags(holder.mOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


        holder.itemView.setOnClickListener(v-> {
                mPresenter.loadProductDetails(mData.get(position).getID(),mSaleId);
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
