package au.com.dealsdirect.ui.controller.returns.returndetails.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.returns.returndetails.Value;
import au.com.dealsdirect.ui.controller.returns.returndetails.ReturnDetailsListener;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.AppLogger;
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
    private Context mContext;
    private ReturnDetailsListener mListener;
    private List<ImageUtils.ImageLink> mUrlImages = new ArrayList<>();


    public ReturnDetailsAddImageAdapter(Context context, ReturnDetailsListener listener,
                                        List<ImageUtils.ImageLink> urlImages) {
        mContext = context;
        mListener = listener;
        mUrlImages = urlImages;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
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

                Glide.with(mContext)
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
                    int selectedPosition = itemPositionToDataPosition(position);
                    mListener.removeImage(((BitmapDrawable) ((VHItem) holder).newReturnImageView.getDrawable()).getBitmap(),
                            selectedPosition, true, false);
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
        if (isPositionHeader(position)) {
            return IMAGE_HEADER;
        }
        return IMAGE_ITEMS;
    }

    private boolean isPositionHeader(int position) {
        return position == 0 && isHeaderVisible();
    }

    private ImageUtils.ImageLink getImageUrl(int position) {
        return mUrlImages.get(position - (isHeaderVisible() ? 1 : 0));
    }

    public void replaceData(List<ImageUtils.ImageLink> imageLinkList) {
        mUrlImages = new ArrayList<>();
        if (imageLinkList.size() != 0) {
            mUrlImages = imageLinkList;
        }
        notifyDataSetChanged();
    }

    public void addItem() {

        if (mUrlImages.size() < AppConstants.MAX_IMAGE_COUNT) {
            notifyItemInserted(1);
            notifyItemRangeChanged(1, getItemCount());
        } else {
            notifyItemChanged(0);
        }
    }

    private boolean isHeaderVisible() {
        return mUrlImages.size() < AppConstants.MAX_IMAGE_COUNT;
    }

    public void removeItem(int position) {

        if (mUrlImages.size() == AppConstants.MAX_IMAGE_COUNT - 1) {
            if (position != 0) {
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
            } else {
                notifyItemChanged(0);
                notifyItemRangeChanged(0, getItemCount());
            }
        } else {
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());
        }

    }

    public int dataPositionToItemPosition(int dataPosition) {
        return dataPosition + (isHeaderVisible() ? 1 : 0);
    }

    public int itemPositionToDataPosition(int itemPosition) {
        return itemPosition - (isHeaderVisible() ? 1 : 0);
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
