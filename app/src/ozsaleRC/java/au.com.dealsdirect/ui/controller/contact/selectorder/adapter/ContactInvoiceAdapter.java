package au.com.dealsdirect.ui.controller.contact.selectorder.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.contactorder.ContactOrderResponse;
import au.com.dealsdirect.utils.ImageUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MTC on 2020-02-27.
 */
public class ContactInvoiceAdapter extends RecyclerView.Adapter<ContactInvoiceAdapter.ContactInvoiceImageViewHolder> {

    private List<ContactOrderResponse.Item> mItemImageList = new ArrayList<>();
    private int mWidth;
    private int mHeight;
    private int mNumberColumns;

    public ContactInvoiceAdapter(int height, int width,
                                 List<ContactOrderResponse.Item> itemImagesList,
                                 int numberColumns) {
        mHeight = height;
        mWidth = width;
        mItemImageList = itemImagesList;
        mNumberColumns = numberColumns;
    }

    @NonNull
    @Override
    public ContactInvoiceImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.viewholder_contact_us_invoice, viewGroup, false);
        return new ContactInvoiceImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactInvoiceImageViewHolder holder, int position) {
        int width = holder.itemView.getContext().getResources().getInteger(R.integer.item_image_width);
        int height = holder.itemView.getContext().getResources().getInteger(R.integer.item_image_height);

        float ratio = (float) height / (float) width;

        int cellWidth = mWidth / mNumberColumns;
        int cellHeight = (int) (cellWidth * ratio);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(cellWidth, cellHeight);
        holder.layout.setLayoutParams(layoutParams);

        ImageUtils.loadImage(mItemImageList.get(position).getImageUrl(), holder.currentReturnImage);
    }

    @Override
    public int getItemCount() {
        return mItemImageList.size();
    }

    static class ContactInvoiceImageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.return_order_imageview)
        ImageView currentReturnImage;

        @BindView(R.id.contact_invoice_container)
        ViewGroup layout;

        public ContactInvoiceImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}