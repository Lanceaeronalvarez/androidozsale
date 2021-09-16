package au.com.dealsdirect.ui.controller.contact.selectsubject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.contactsubject.ContactSubjectResponse;
import au.com.dealsdirect.ui.controller.contact.selectsubject.viewholder.ContactSubjectViewHolder;

/**
 * dp Created by Admin on 7/5/17.
 */

public class ContactSubjectAdapter extends RecyclerView.Adapter<ContactSubjectViewHolder> {

    private List<ContactSubjectResponse> mCurrentContactSubjectList;
    private ContactSubjectItemSelectedListener mListener;

    public ContactSubjectAdapter(
            List<ContactSubjectResponse> contactSubjects,
            ContactSubjectItemSelectedListener listener) {

        mCurrentContactSubjectList = contactSubjects;
        mListener = listener;
    }

    @Override
    public ContactSubjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_contact_subject, parent, false);
        return new ContactSubjectViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ContactSubjectViewHolder holder, int position) {

        holder.contactSubjectRowLayout.setOnClickListener(v -> mListener.itemSelected(mCurrentContactSubjectList.get(position)));
        holder.contactSubjectTitleRowTextView.setText(mCurrentContactSubjectList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mCurrentContactSubjectList == null ? 0 : mCurrentContactSubjectList.size();
    }

    public void replaceData(List<ContactSubjectResponse> subjects) {
        mCurrentContactSubjectList = subjects;
        notifyDataSetChanged();
    }

    public interface ContactSubjectItemSelectedListener {
        void itemSelected(ContactSubjectResponse response);
    }
}
