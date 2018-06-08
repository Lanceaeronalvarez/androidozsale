package au.com.dealsdirect.ui.controller.contact.selectsubject.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import au.com.dealsdirect.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * dp Created by Admin on 7/5/17.
 */

public class ContactSubjectViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.my_contact_select_subject_recycler_row_layout)
    public RelativeLayout contactSubjectRowLayout;

    @BindView(R.id.contact_subject_row_item_name)
    public TextView contactSubjectTitleRowTextView;

    public ContactSubjectViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
