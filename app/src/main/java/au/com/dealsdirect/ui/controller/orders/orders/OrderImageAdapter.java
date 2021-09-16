package au.com.dealsdirect.ui.controller.orders.orders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.orders.GetOrdersResponse.Order.Invoice.Product;
import au.com.dealsdirect.utils.ActionConstants;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.ScreenUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderImageAdapter extends RecyclerView.Adapter<OrderImageAdapter.OrderImagesViewholder> {

    private final int IMAGE_LIMIT_POSITION = 2;

    private final int IMAGE_LIMIT_SIZE = 3;

    private List<Product> mData;

    public OrderImageAdapter(List<Product> items) {
        mData = items;
    }


    @Override
    public OrderImagesViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item_ordered_image, parent, false);
        return new OrderImagesViewholder(view);
    }

    @Override
    public void onBindViewHolder(OrderImagesViewholder holder, int position) {
        if (!(position > IMAGE_LIMIT_POSITION)) {
            Product item = mData.get(position);
            ImageUtils.loadImage(item.getImageUrl(),
                    holder.orderImageView);

            if (position == IMAGE_LIMIT_POSITION && mData.size() > IMAGE_LIMIT_SIZE) {
                holder.orderImageOverlayImageView.setVisibility(View.VISIBLE);
                String imageText = "+" + (mData.size() - IMAGE_LIMIT_SIZE);
                holder.orderImageText.setText(imageText);
            }

            if (item.getActions().contains(ActionConstants.ORDER_ITEM_CANCELLED)) {
                holder.orderImageCancelledTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    private ImageUtils.Grid adjustFrameLayoutHeight(Context context) {
        //compute Image Height
        int height = context.getResources().getInteger(R.integer.order_item_height);
        int width = context.getResources().getInteger(R.integer.order_item_width);
        return ImageUtils.getExactGridDefinition(3, height / width, ScreenUtils.getScreenWidth(context) / 2);
    }

    @Override
    public int getItemCount() {
        return mData.size() >= 3 ? 3 : mData.size();
    }

    public void replaceData(List<Product> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public static class OrderImagesViewholder extends RecyclerView.ViewHolder {

        @BindView(R.id.order_image_framelayout)
        FrameLayout frameLayout;

        @BindView(R.id.ordered_image_view)
        ImageView orderImageView;

        @BindView(R.id.ordered_image_text)
        TextView orderImageText;

        @BindView(R.id.overlay_image_view)
        ImageView orderImageOverlayImageView;

        @BindView(R.id.controller_order_cancelled_text)
        TextView orderImageCancelledTextView;

        public OrderImagesViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
