package au.com.dealsdirect.ui.controller.contact.addcontact.selectsubject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Collections;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.controller.contact.addcontact.ContactPreferenceHelper;
import au.com.dealsdirect.ui.controller.contact.addcontact.selectsubject.listener.ContactSubjectClickListener;
import au.com.dealsdirect.ui.controller.contact.addcontact.selectsubject.viewholder.ContactSubjectViewHolder;

/**
 * dp Created by Admin on 7/5/17.
 */

public class ContactSubjectAdapter extends RecyclerView.Adapter<ContactSubjectViewHolder> {

    List<String> mCurrentContactSubjectList = Collections.emptyList();
    Context mContext;
    ContactSubjectClickListener myContactSubjectItemClickListener;

    private ImageView lastChecked;

    public ContactSubjectAdapter(
            List<String> contactSubjects,
            Context context,
            ContactSubjectClickListener contactSubjectItemClickListener) {

        this.mCurrentContactSubjectList = contactSubjects;
        this.mContext = context;
        this.myContactSubjectItemClickListener = contactSubjectItemClickListener;
    }

    @Override
    public ContactSubjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_contact_subject,
                        parent,
                        false);

        return new ContactSubjectViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final ContactSubjectViewHolder holder, final int position) {


        holder.contactSubjectRowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myContactSubjectItemClickListener.onContactSubjectItemClicked
                        (mCurrentContactSubjectList.get(position));

            }
        });

        holder.contactSubjectTitleRowTextView
                .setText(mCurrentContactSubjectList.get(position));

        holder.contactSubjectTitleRowTextView.setSelected(ContactPreferenceHelper.getChosenSubject(mContext).equalsIgnoreCase(mCurrentContactSubjectList.get(position)));
    }


    @Override
    public int getItemCount() {
        if (mCurrentContactSubjectList == null) {
            return 0;
        }
        return mCurrentContactSubjectList.size();
    }
}
