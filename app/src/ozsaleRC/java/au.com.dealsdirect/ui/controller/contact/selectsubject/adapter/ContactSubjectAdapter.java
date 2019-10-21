package au.com.dealsdirect.ui.controller.contact.selectsubject.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.controller.contact.selectsubject.ContactSelectSubjectMvpPresenter;
import au.com.dealsdirect.ui.controller.contact.selectsubject.ContactSelectSubjectMvpView;
import au.com.dealsdirect.ui.controller.contact.selectsubject.viewholder.ContactSubjectViewHolder;

/**
 * dp Created by Admin on 7/5/17.
 */

public class ContactSubjectAdapter extends RecyclerView.Adapter<ContactSubjectViewHolder> {

    private List<String> mCurrentContactSubjectList = Collections.emptyList();
    private ContactSelectSubjectMvpPresenter<ContactSelectSubjectMvpView> mPresenter;

    public ContactSubjectAdapter(
            List<String> contactSubjects,
            ContactSelectSubjectMvpPresenter mvpPresenter) {

        mCurrentContactSubjectList = contactSubjects;
        mPresenter = mvpPresenter;

    }

    @Override
    public ContactSubjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_contact_subject, parent, false);
        return new ContactSubjectViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ContactSubjectViewHolder holder, int position) {

        holder.contactSubjectRowLayout.setOnClickListener(v -> mPresenter.selectContactSubject(mCurrentContactSubjectList.get(position)));
        holder.contactSubjectTitleRowTextView.setText(mCurrentContactSubjectList.get(position));
    }

    @Override
    public int getItemCount() {
        return mCurrentContactSubjectList == null ? 0 : mCurrentContactSubjectList.size();
    }

    public void replaceData(List<String> subjects){
        mCurrentContactSubjectList = subjects;
        notifyDataSetChanged();
    }
}
