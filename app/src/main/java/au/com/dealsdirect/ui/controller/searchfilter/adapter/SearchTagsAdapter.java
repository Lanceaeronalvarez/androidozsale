package au.com.dealsdirect.ui.controller.searchfilter.adapter;

import android.content.Context;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.controller.searchfilter.SearchFilterController;
import au.com.dealsdirect.ui.controller.searchfilter.SearchFilterMvpPresenter;
import au.com.dealsdirect.ui.custom.ChipsEditText;

/**
 * Created by smartwave on 21/07/2017.
 */

public class SearchTagsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Pair<String,String>> mData;
    private Context mContext;
    private DisplayMetrics mDisplayMetrics;
    private LinearLayoutManager mLayoutManager;
    private SearchFilterMvpPresenter mPresenter;


    public EditTextViewHolder getEditTextViewHolder() {
        return editTextViewHolder;
    }

    public EditTextViewHolder editTextViewHolder;

    public SearchTagsAdapter(Context ctx, LinearLayoutManager llm, ArrayList<Pair<String,String>> items, SearchFilterMvpPresenter presenter){
        mContext=ctx;
        mDisplayMetrics = ctx.getResources().getDisplayMetrics();
        mLayoutManager = llm;
        mData = items;
        mPresenter = presenter;
    }


    public void add(Pair<String,String> item){
        if(mData!=null){
            mData.add(item);
            int addedItemIndex = mData.indexOf(item);
            notifyItemInserted(mData.indexOf(item));

            int offsetAmount = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 135, mDisplayMetrics);
            int currentLastItem = getItemCount()-1;

            mLayoutManager.scrollToPosition(addedItemIndex);
            mLayoutManager.scrollToPositionWithOffset(currentLastItem, offsetAmount);
        }
    }

    public void remove(Pair<String,String> item){
        if(mData!=null){
            int itemIndex = mData.indexOf(item);
            mData.remove(item);
            notifyItemRemoved(itemIndex);
            if (itemIndex - 1 > 0) {
                mLayoutManager.scrollToPosition(itemIndex - 1);
            } else {
                mLayoutManager.scrollToPosition(0);
            }

        }
    }

    public ArrayList<Pair<String,String>> getData() {
        return mData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = null;
        View v= null;
        if(viewType == 0 ) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_filter_chip_item, parent, false);
            vh = new SearchTagsViewHolder(v);
        } else if (viewType == 1){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_filter_edit_text, parent, false);
            vh = editTextViewHolder = new EditTextViewHolder(v);
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof SearchTagsViewHolder) {
            Pair<String,String> pair = mData.get(position);
            SearchTagsViewHolder vh = (SearchTagsViewHolder) holder;
            vh.tv.setText(pair.second);
        }

        if(holder instanceof EditTextViewHolder) {
            EditTextViewHolder vh = (EditTextViewHolder) holder;
            if (mData.size() == 0) {
//                vh.placeholder.setVisibility(View.VISIBLE);
//                vh.et.setVisibility(View.GONE);
                vh.toggleEditTextVisibility(true);
            } else {
                vh.toggleEditTextVisibility(false);
//                vh.placeholder.setVisibility(View.GONE);
//                vh.et.setVisibility(View.VISIBLE);
            }

            vh.et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent
                        keyEvent) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        add(new Pair<String,String>(SearchFilterController.SEARCH_QUERY_NAME,vh.et.getText().toString()));
                        vh.et.setText("");
                    }

                    return false;
                }
            });

            vh.et.setDeleteListener(() -> {
                //remove search tags
                int dataSize = getData().size();
                if (dataSize != 0) {
                    //event bus post to notify all filter fragment types of deletion of a chip.
                    getData().remove(dataSize - 1);
                    notifyItemRemoved(dataSize - 1);
                    mLayoutManager.scrollToPosition(dataSize - 1);
//                    mShopPresenter.updateShopFilters();
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return mData.size() + 1; //add edit text
    }

    @Override
    public int getItemViewType(int position) {
        if(getItemCount()-1 == position){ //if last, that should be search edit text
            return 1;
        }
        //else, its search chips
        return 0;
    }

    public static class SearchTagsViewHolder extends RecyclerView.ViewHolder{
        TextView tv;

        public SearchTagsViewHolder(View itemView) {
            super(itemView);
            tv=(TextView) itemView.findViewById(R.id.search_tag_text_item);
        }
    }

    public static class EditTextViewHolder extends RecyclerView.ViewHolder {
        public ChipsEditText et;
        public TextView placeholder;
        public boolean isFinishedTyping = false;

        public EditText getEditText() {
            return et;
        }

        public EditTextViewHolder(View itemView) {
            super(itemView);
            et = (ChipsEditText) itemView.findViewById(R.id.search_edit_text);
            placeholder = (TextView) itemView.findViewById(R.id.search_text_view_placeholder);
        }

        //switching between an edittext or the textview to be visible.
        public void toggleEditTextVisibility(boolean val) {
            et.setVisibility(val ? View.VISIBLE : View.GONE);
            placeholder.setVisibility(val ? View.GONE : View.VISIBLE);
        }

        public void setVisibility(boolean isVisible) {
            if (isVisible) {
                itemView.setVisibility(View.VISIBLE);
            } else {
                itemView.setVisibility(View.GONE);

            }

        }
    }
}
