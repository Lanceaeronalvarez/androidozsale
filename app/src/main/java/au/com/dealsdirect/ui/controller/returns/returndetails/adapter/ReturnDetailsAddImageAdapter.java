package au.com.dealsdirect.ui.controller.returns.returndetails.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.controller.returns.returndetails.ReturnDetailsListener;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.ImageUploadUtil;
import au.com.dealsdirect.utils.ImageUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MTC on 2019-08-23.
 */
public class ReturnDetailsAddImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int IMAGE_HEADER = 1;
    private static final int IMAGE_ITEMS = 0;
    private ReturnDetailsListener mListener;
    private List<ImageUtils.ImageLink> mUrlImages;


    public ReturnDetailsAddImageAdapter(List<ImageUtils.ImageLink> urlImages,
                                        ReturnDetailsListener listener) {
        mListener = listener;
        mUrlImages = urlImages;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == IMAGE_HEADER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.add_image_placeholder, parent, false);
            return new VHHeader(view);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.viewholder_new_return_image, parent, false);
            return new VHItem(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof VHItem) {

            if (mUrlImages.size() != 0) {
                ImageUtils.ImageLink url = getImageUrl(position);

                Glide.with(holder.itemView.getContext())
                        .asBitmap()
                        .load(url.isURL() ? url.getLink() : Uri.parse(url.getLink()))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                Bitmap newImage = ImageUploadUtil.resizeBitmapToFitSize(resource, AppConstants.MAX_SCALED_BITMAP_WIDTH, AppConstants.MAX_SCALED_BITMAP_HEIGHT);

                                ((VHItem) holder).newReturnImageView.setImageBitmap(newImage);
                                mListener.addItemFromLink(url.getLink(), position, resource);
                            }
                        });

                ((VHItem) holder).newReturnCloseButton.setOnClickListener(v -> {
                    mListener.removeImage(((BitmapDrawable) ((VHItem) holder).newReturnImageView.getDrawable()).getBitmap(),
                            holder.getAdapterPosition(), true, false);
                });
            }

        } else if (holder instanceof VHHeader) {
            int totalImages = mUrlImages.size();
            if (totalImages == AppConstants.MAX_IMAGE_COUNT) {
                ((VHHeader) holder).placeholderLayout.setVisibility(View.GONE);
                ((VHHeader) holder).addImageLayout.setVisibility(View.GONE);
            } else {
                ((VHHeader) holder).placeholderLayout.setVisibility(View.VISIBLE);
                ((VHHeader) holder).addImageLayout.setVisibility(View.VISIBLE);
            }

            ((VHHeader) holder).addImageLayout.setOnClickListener(v -> {
                if (totalImages < AppConstants.MAX_IMAGE_COUNT) {
                    mListener.getImageFromDirectory(true);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mUrlImages.size() + (isHeaderVisible() ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mUrlImages.size()) {
            return IMAGE_HEADER;
        }
        return IMAGE_ITEMS;
    }

    private ImageUtils.ImageLink getImageUrl(int position) {
        return mUrlImages.get(position);
    }

    public void replaceData(List<ImageUtils.ImageLink> imageLinkList) {
        mUrlImages = new ArrayList<>();
        if (imageLinkList.size() != 0) {
            mUrlImages = imageLinkList;
        }
        notifyDataSetChanged();
    }

    private boolean isHeaderVisible() {
        return mUrlImages.size() < AppConstants.MAX_IMAGE_COUNT;
    }

    class VHItem extends RecyclerView.ViewHolder {
        @BindView(R.id.viewholder_new_return_imageview)
        ImageView newReturnImageView;
        @BindView(R.id.viewholder_close_button)
        ImageButton newReturnCloseButton;

        VHItem(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    class VHHeader extends RecyclerView.ViewHolder {
        @BindView(R.id.viewholder_add_image_layout)
        LinearLayout addImageLayout;

        @BindView(R.id.placeholder_layout)
        LinearLayout placeholderLayout;

        VHHeader(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
