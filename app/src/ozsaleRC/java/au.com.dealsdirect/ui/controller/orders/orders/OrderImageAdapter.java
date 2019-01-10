package au.com.dealsdirect.ui.controller.orders.orders;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.orders.GetPaymentsList;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.ScreenUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderImageAdapter extends RecyclerView.Adapter<OrderImageAdapter.OrderImagesViewholder> {

    private final int IMAGE_LIMIT_POSITION = 2;

    private final int IMAGE_LIMIT_SIZE = 3;

    private List<GetPaymentsList.ResponseValue.Item> mData;

    private Activity mActivity;

    private ImageUtils.Grid mGrid;

    public OrderImageAdapter(Activity activity, List<GetPaymentsList.ResponseValue.Item> items) {
        this.mActivity = activity;
        mData = items;
        mGrid = adjustFrameLayoutHeight();
    }


    @Override
    public OrderImagesViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.row_item_ordered_image, parent, false);
        return new OrderImagesViewholder(view, mGrid);
    }

    @Override
    public void onBindViewHolder(OrderImagesViewholder holder, int position) {
        if (!(position > IMAGE_LIMIT_POSITION)) {
            GetPaymentsList.ResponseValue.Item item = mData.get(position);
            ImageUtils.loadImage(String.format(
                    mActivity.getResources().getString(R.string.default_image_link),
                    item.getBrandID(),
                    item.getImageID(),
                    item.getFileName()),
                    holder.orderImageView);

            if (position == IMAGE_LIMIT_POSITION) {
                holder.orderImageOverlayImageView.setVisibility(View.VISIBLE);
                holder.orderImageText.setText("+" + String.valueOf(mData.size() - IMAGE_LIMIT_SIZE));
            }
        }
    }

    private ImageUtils.Grid adjustFrameLayoutHeight() {
        //compute Image Height
        int height = mActivity.getResources().getInteger(R.integer.order_item_height);
        int width = mActivity.getResources().getInteger(R.integer.order_item_width);
        return ImageUtils.getExactGridDefinition(3, height / width, ScreenUtils.getScreenWidth(mActivity) / 2);
    }

    @Override
    public int getItemCount() {
        return mData.size() >= 3 ? 3 : mData.size();
    }

    public void replaceData(List<GetPaymentsList.ResponseValue.Item> data) {
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

        public OrderImagesViewholder(View itemView, ImageUtils.Grid dimensions) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            ViewGroup.LayoutParams lp = frameLayout.getLayoutParams();
            lp.width = Math.round(dimensions.getItemWidth());
            lp.height = Math.round(dimensions.getItemHeight());
            frameLayout.setLayoutParams(lp);
        }
    }
}
