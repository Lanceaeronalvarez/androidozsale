package au.com.dealsdirect.ui.controller.contact.viewcontacthistory;

import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryResponse.Message.Attachment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LinkAttachmentsDisplayAdapter extends RecyclerView.Adapter<LinkAttachmentsDisplayAdapter.AttachementDisplayViewHolder> {

    private final static String ATTACHMENT_TYPE_PDF = "application/pdf";

    private final List<Attachment> mAttachments;
    private final OnClickAttachmentListener onClickAttachmentListener;


    public LinkAttachmentsDisplayAdapter(List<Attachment> attachments, OnClickAttachmentListener listener) {
        mAttachments = attachments;
        onClickAttachmentListener = listener;
    }

    @Override
    public AttachementDisplayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_link_display, parent, false);
        return new AttachementDisplayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AttachementDisplayViewHolder holder, int position) {
        final Attachment attachment = mAttachments.get(position);
        final String attachmentType = attachment.getType();
        final String attachmentUrl = attachment.getUrl();

        SpannableString title = null;

        final String filenameRegex = "(?=\\w+\\.\\w{3,4}$).+";
        Matcher m = Pattern.compile(filenameRegex).matcher(attachmentUrl);
        if (m.find()) {
            title = new SpannableString(m.group());
            title.setSpan(new UnderlineSpan(), 0, title.length(), 0);
        }
        holder.itemView.setOnClickListener(v -> {
            if (onClickAttachmentListener != null) {
                onClickAttachmentListener.onClick(attachmentType, attachmentUrl);
            }
        });

        holder.title.setText(title);
    }

    @Override
    public int getItemCount() {
        return mAttachments.size();
    }

    static class AttachementDisplayViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.viewholder_title)
        TextView title;

        AttachementDisplayViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
