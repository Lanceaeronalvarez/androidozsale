package au.com.dealsdirect.ui.controller.saleitems.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.saleitems.GetPublicSaleItemsResponse;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.LegacyStringImageUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * dp Created by Admin on 6/8/17.
 */

public class SaleItemsAdapter extends RecyclerView.Adapter<SaleItemsAdapter.ViewHolder>{

    List<GetPublicSaleItemsResponse.Item> mData;
    Context mContext;

    public void addData(List<GetPublicSaleItemsResponse.Item> saleItems) {
        mData.addAll(saleItems);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cell_product_image)
        ImageView image;

        @BindView(R.id.cell_product_name)
        TextView name;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public SaleItemsAdapter( List<GetPublicSaleItemsResponse.Item> saleItem) {
        this.mData = saleItem;
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
        holder.name.setText(saleItem.getName());

        String url = LegacyStringImageUtils.itemImageURLString(saleItem);

        ImageUtils.loadImageWithImageViewDimens(mContext,url, holder.image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mFragment.showProductDetailsFragment(mData.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
