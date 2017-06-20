package au.com.dealsdirect.ui.controller.contact.viewcontacts.viewcontactitem;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.viewcontactitem.GetContactsResponse;
import au.com.dealsdirect.utils.DateUtils;

/**
 * dp Created by Admin on 6/20/17.
 */

public class ViewContactItemAdapter
        extends RecyclerView.Adapter<ViewContactItemAdapter.ViewContactsItemViewHolder>  {

    List<GetContactsResponse.ContactList> mCurrentContactsList = Collections.emptyList();
    Context mContext;


    public ViewContactItemAdapter(
            List<GetContactsResponse.ContactList> contactitemsList,
            Context context) {

        this.mCurrentContactsList = contactitemsList;
        this.mContext = context;
    }

    @Override
    public ViewContactsItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_contact_item,
                        parent,
                        false);

        return new ViewContactsItemViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewContactsItemViewHolder holder, int position) {

        Object itemSaleName = mCurrentContactsList.get(position).getSaleName();
        Object itemLastAnswer = mCurrentContactsList.get(position).getLastAnswer();
        Object itemLastComment = mCurrentContactsList.get(position).getLastComment();
        String dateOfContactItem = mCurrentContactsList.get(position).getLastComment();
        String dateHeaderFormatOfItem = DateUtils.getDayOfWeekFromDateString(itemLastAnswer.toString());

        if (itemSaleName != null){
            holder.contactUsTitleTextView
                    .setText(itemSaleName.toString());
        }else{
            holder.contactUsTitleTextView
                    .setText("");
        }

        if (itemLastComment != null){

            holder.contactUsDescriptionTextView
                    .setText(itemLastComment.toString());
        }else{

            holder.contactUsDescriptionTextView.setText("");
        }

        if (itemLastAnswer != null){
            String itemLastAnswerTimeFormat
                    = DateUtils.getTimeFromDateString(itemLastAnswer.toString());

            holder.contactUsTimeStampTextView
                    .setText(itemLastAnswerTimeFormat);

        }else{
            holder.contactUsTimeStampTextView.setText("");
        }

        if(position == mCurrentContactsList.size()-1){
            holder.contactUsDivider.setVisibility(View.GONE);
        }

//        holder.contactItem.setOnClickListener(new View.OnClickListener() {
//            @Override public void onClick(View view) {
//                mMyContactsContactUsItemClickListener.onContactUsItemClicked(mCurrentContactsList
//                                                                                     .get(position));
//            }
//        });
    }

    @Override
    public int getItemCount() {
        if (mCurrentContactsList == null){
            return 0;
        }
        return mCurrentContactsList.size();
    }

    static class ViewContactsItemViewHolder extends RecyclerView.ViewHolder {

        LinearLayout contactItem;
        TextView contactUsTitleTextView;
        TextView contactUsTimeStampTextView;
        TextView contactUsDescriptionTextView;
        View contactUsDivider;

        public ViewContactsItemViewHolder(View itemView) {
            super(itemView);

            contactItem = (LinearLayout) itemView
                    .findViewById(R.id.my_contact_us_recycler_row_item_layout);

            contactUsTitleTextView = (TextView) itemView
                    .findViewById(R.id.my_contact_us_row_title_text_view);

            contactUsTimeStampTextView = (TextView) itemView
                    .findViewById(R.id.my_contact_us_row_item_time_stamp_text_view);

            contactUsDescriptionTextView = (TextView) itemView
                    .findViewById(R.id.my_contact_us_row_description_text_view);

            contactUsDivider = itemView
                    .findViewById(R.id.my_contact_us_fragment_row_item_divider);
        }
    }

}
