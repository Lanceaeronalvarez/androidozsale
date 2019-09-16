package au.com.dealsdirect.ui.controller.returns.returnorders.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.returns.returnorders.List;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.LegacyStringImageUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MTC on 2019-08-07.
 */
public class ReturnOrdersImageAdapter extends RecyclerView.Adapter<ReturnOrdersImageAdapter.ReturnOrdersImageViewHolder> {

    private java.util.List<List.ItemImages> mItemImageList = new ArrayList<>();

    public ReturnOrdersImageAdapter(java.util.List<List.ItemImages> itemImagesList) {
        mItemImageList = itemImagesList;
    }

    @NonNull
    @Override
    public ReturnOrdersImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.return_order_image_layout, viewGroup, false);
        return new ReturnOrdersImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReturnOrdersImageViewHolder holder, int position) {

        String imageUrl = LegacyStringImageUtils.generateImageUrl(mItemImageList.get(position).getBrandId(),
                mItemImageList.get(position).getImageId(), mItemImageList.get(position).getFileName());

        ImageUtils.loadImage(imageUrl, holder.currentReturnImage);

    }

    @Override
    public int getItemCount() {
        return mItemImageList.size();
    }

    static class ReturnOrdersImageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.return_order_imageview)
        ImageView currentReturnImage;

        public ReturnOrdersImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}