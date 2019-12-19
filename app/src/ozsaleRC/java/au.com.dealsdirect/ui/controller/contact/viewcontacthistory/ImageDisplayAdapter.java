package au.com.dealsdirect.ui.controller.contact.viewcontacthistory;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.ImageUploadUtil;
import au.com.dealsdirect.utils.ImageUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MTC on 2019-11-11.
 */
public class ImageDisplayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<au.com.dealsdirect.data.network.model.contacthistory.List.Attachments> mUrlImages = new ArrayList<>();


    public ImageDisplayAdapter(Context context,
                               List<au.com.dealsdirect.data.network.model.contacthistory.List.Attachments> urlImages) {
        mContext = context;
        mUrlImages = urlImages;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_image_display, parent, false);
        return new VHItem(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        au.com.dealsdirect.data.network.model.contacthistory.List.Attachments attachments = mUrlImages.get(position);

        Glide.with(mContext)
                .asBitmap()
                .load(attachments.getUrl())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        Bitmap newImage = ImageUploadUtil.resizeBitmapToFitSize(resource, AppConstants.MAX_SCALED_BITMAP_WIDTH, AppConstants.MAX_SCALED_BITMAP_HEIGHT);
                        ((VHItem) holder).imageView.setImageBitmap(newImage);
                    }
                });

    }

    @Override
    public int getItemCount() {
        return mUrlImages.size();
    }

    class VHItem extends RecyclerView.ViewHolder {
        @BindView(R.id.viewholder_image_display_imageview)
        ImageView imageView;

        VHItem(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
