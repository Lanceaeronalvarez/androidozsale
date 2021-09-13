package au.com.dealsdirect.ui.controller.contact.viewcontacthistory;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryResponse.Message.Attachment;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.ImageUploadUtil;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageAttachmentsDisplayAdapter extends RecyclerView.Adapter<ImageAttachmentsDisplayAdapter.AttachementDisplayViewHolder> {

    private final List<Attachment> mAttachments;
    private final OnClickAttachmentListener onClickAttachmentListener;


    public ImageAttachmentsDisplayAdapter(List<Attachment> attachments, OnClickAttachmentListener listener) {
        mAttachments = attachments;
        onClickAttachmentListener = listener;
    }

    @Override
    public AttachementDisplayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_image_display, parent, false);
        return new AttachementDisplayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AttachementDisplayViewHolder holder, int position) {
        final Context context = holder.itemView.getContext();

        final Attachment attachment = mAttachments.get(position);
        final String attachmentType = attachment.getType();
        final String attachmentUrl = attachment.getUrl();

        String title = null;

        if (attachment.isImage()) {
            holder.imageView.setImageDrawable(null);
            holder.imageView.setOnClickListener(null);
            Glide.with(context)
                    .asBitmap()
                    .load(attachmentUrl)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            Bitmap newImage = ImageUploadUtil.resizeBitmapToFitSize(resource, AppConstants.MAX_SCALED_BITMAP_WIDTH, AppConstants.MAX_SCALED_BITMAP_HEIGHT);
                            holder.imageView.setImageBitmap(newImage);
                        }
                    });
        } else {
            final String filenameRegex = "(?=\\w+\\.\\w{3,4}$).+";
            Matcher m = Pattern.compile(filenameRegex).matcher(attachmentUrl);
            if (m.find()) {
                title = m.group();
            }
            holder.imageView.setImageResource(R.drawable.ic_document_icon);
            holder.imageView.setOnClickListener(v -> {
                if (onClickAttachmentListener != null) {
                    onClickAttachmentListener.onClick(attachmentType, attachmentUrl);
                }
            });
        }

        holder.title.setText(title);
        holder.title.setVisibility(title != null ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return mAttachments.size();
    }

    static class AttachementDisplayViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.viewholder_image_display_imageview)
        ImageView imageView;
        @BindView(R.id.viewholder_title)
        TextView title;

        AttachementDisplayViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
