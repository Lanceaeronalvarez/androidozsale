package au.com.dealsdirect.ui.controller.contact.viewcontacthistory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.controller.returns.returndetails.ReturnDetailsListener;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.ImageUploadUtil;
import au.com.dealsdirect.utils.ImageUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MTC on 2019-12-02.
 */
public class ViewContactsAddImageAdapter extends RecyclerView.Adapter<ViewContactsAddImageAdapter.ViewContactsAddImageViewHolder> {

    private Context mContext;
    private ReturnDetailsListener mListener;
    private List<ImageUtils.ImageLink> mUrlImages = new ArrayList<>();

    public ViewContactsAddImageAdapter(Context context, ReturnDetailsListener listener,
                                        List<ImageUtils.ImageLink> urlImages) {
        mContext = context;
        mListener = listener;
        mUrlImages = urlImages;
    }

    @Override
    public ViewContactsAddImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_image_display, parent, false);
        return new ViewContactsAddImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewContactsAddImageViewHolder holder, int position) {

        if (mUrlImages.size() != 0) {
            holder.viewContactsCloseButton.setVisibility(View.VISIBLE);
            ImageUtils.ImageLink url = getImageUrl(position);

            Glide.with(mContext)
                    .asBitmap()
                    .load(url.isURL() ? url.getLink() : Uri.parse(url.getLink()))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            Bitmap newImage = ImageUploadUtil.resizeBitmapToFitSize(resource, AppConstants.MAX_SCALED_BITMAP_WIDTH, AppConstants.MAX_SCALED_BITMAP_HEIGHT);
                            holder.viewContactsImageView.setImageBitmap(newImage);
                        }
                    });

            holder.viewContactsCloseButton.setOnClickListener(v -> {
                mListener.removeImage(((BitmapDrawable) holder.viewContactsImageView.getDrawable()).getBitmap(),
                        holder.getAdapterPosition(), true, false);
            });
        }

    }

    @Override
    public int getItemCount() {
        return mUrlImages.size();
    }

    private ImageUtils.ImageLink getImageUrl(int position) {
        return mUrlImages.get(position);
    }

    public void addItem() {
        notifyItemInserted(0);
        notifyItemRangeChanged(0, getItemCount());
    }

    public void removeItem(int position) {
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    public void showProgressBar(ViewContactsAddImageViewHolder holder) {
        holder.viewContactsProgressBar.setVisibility(View.VISIBLE);
    }

    public void hideVisibility(ViewContactsAddImageViewHolder holder) {
        holder.viewContactsProgressBar.setVisibility(View.GONE);
    }

    public class ViewContactsAddImageViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.viewholder_image_display_imageview)
        public ImageView viewContactsImageView;
        @BindView(R.id.viewholder_close_button)
        ImageButton viewContactsCloseButton;
        @BindView(R.id.viewholder_progress_bar)
        ProgressBar viewContactsProgressBar;

        ViewContactsAddImageViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}