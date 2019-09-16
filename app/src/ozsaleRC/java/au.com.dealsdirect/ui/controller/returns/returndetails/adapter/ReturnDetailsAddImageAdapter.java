package au.com.dealsdirect.ui.controller.returns.returndetails.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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

import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.returns.returndetails.Value;
import au.com.dealsdirect.ui.controller.returns.returndetails.ReturnDetailsListener;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.ImageUploadUtil;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MTC on 2019-08-23.
 */
public class ReturnDetailsAddImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private static final int IMAGE_HEADER = 1;
    private static final int IMAGE_ITEMS = 0;
    private ReturnDetailsListener mListener;
    private List<Bitmap> mBitmapArray = new ArrayList<>();
    private List<Value.Attachments> mUrlImages = new ArrayList<>();


    public ReturnDetailsAddImageAdapter(Context context, ReturnDetailsListener listener,
                                        ArrayList<Bitmap> bitmapArrayList,
                                        List<Value.Attachments> urlImages) {
        mContext = context;
        mListener = listener;
        mBitmapArray = bitmapArrayList;
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
                String url = getImageUrl(position);

                Glide.with(mContext)
                        .asBitmap()
                        .load(url)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                ((VHItem) holder).newReturnImageView.setImageBitmap(resource);
                                mListener.addItemFromApi(url, resource);
                            }
                        });

                ((VHItem) holder).newReturnCloseButton.setOnClickListener(v -> {
                    mListener.removeImage(((BitmapDrawable)((VHItem) holder).newReturnImageView.getDrawable()).getBitmap(),
                            position, true, false);
                });
            } else if (mBitmapArray.size() != 0) {
                Bitmap dataItem = getItem(position);
                Bitmap newImage = ImageUploadUtil.resizeBitmapToFitSize(dataItem, AppConstants.MAX_SCALED_BITMAP_WIDTH, AppConstants.MAX_SCALED_BITMAP_HEIGHT);

                ((VHItem) holder).newReturnImageView.setImageBitmap(newImage);

                ((VHItem) holder).newReturnCloseButton.setOnClickListener(v -> {
                    mListener.removeImage(dataItem, position, true, true);
                });
            }

        } else if (holder instanceof VHHeader) {
            int maxImages = 3;
            int totalImages = mBitmapArray.size() + mUrlImages.size();
            if (totalImages == maxImages) {
                ((VHHeader) holder).placeholderLayout.setVisibility(View.GONE);
                ((VHHeader) holder).addImageLayout.setVisibility(View.GONE);
            } else {
                ((VHHeader) holder).placeholderLayout.setVisibility(View.VISIBLE);
                ((VHHeader) holder).addImageLayout.setVisibility(View.VISIBLE);
            }

            ((VHHeader) holder).addImageLayout.setOnClickListener(v -> {
                if (totalImages < maxImages) {
                    mListener.getImageFromDirectory(true);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mUrlImages.size() == 0 ? mBitmapArray.size() + 1 : mUrlImages.size() + 1;
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

    private Bitmap getItem(int position) {
        return mBitmapArray.get(position - (isHeaderVisible() ? 1 : 0));
    }

    private String getImageUrl(int position) {
        return mUrlImages.get(position - (isHeaderVisible() ? 1 : 0)).getUrl();
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

    public void addItem() {

        if (getItemCount() == 4) {
            notifyDataSetChanged();
        } else {
            notifyItemInserted(1);
            notifyItemRangeChanged(1, getItemCount());
        }

    }

    private boolean isHeaderVisible() {
        return mBitmapArray.size() < 4;
    }

    public void removeItem(int position) {
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());

        if (getItemCount() < 4) {
            notifyItemRangeChanged(0, getItemCount());
        }
    }

}
