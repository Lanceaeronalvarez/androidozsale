package au.com.dealsdirect.ui.controller.searchfilter.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsMvpPresenter;
import au.com.dealsdirect.ui.custom.ChipsEditText;
import au.com.dealsdirect.utils.BundleKeys;


/**
 * Created by smartwave on 21/07/2017.
 */

public class SearchTagsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<SearchChipModel> mData = new ArrayList<>();
    private Context mContext;
    private DisplayMetrics mDisplayMetrics;
    private LinearLayoutManager mLayoutManager;
    private SaleItemsMvpPresenter mSaleItemPresenter;
    private HashMap<String, Set<Integer>> mPreviousSelectedFacetIndices;

    public EditTextViewHolder getEditTextViewHolder() {
        return editTextViewHolder;
    }

    public EditTextViewHolder editTextViewHolder;

    public SearchTagsAdapter(Context ctx, LinearLayoutManager llm, ArrayList<SearchChipModel> items, SaleItemsMvpPresenter presenter) {
        mContext = ctx;
        mDisplayMetrics = ctx.getResources().getDisplayMetrics();
        mLayoutManager = llm;
        mData = items;
        mSaleItemPresenter = presenter;
    }


    public void add(SearchChipModel chip) {
        if (mData != null) {
            mData.add(chip);
            int addedItemIndex = mData.indexOf(chip);
            notifyItemInserted(mData.indexOf(chip));

            int offsetAmount = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 135, mDisplayMetrics);
            int currentLastItem = getItemCount()-1;

            mLayoutManager.scrollToPosition(addedItemIndex);
            mLayoutManager.scrollToPositionWithOffset(currentLastItem, offsetAmount);

        }
    }

    public void remove(SearchChipModel chip) {
        if (mData != null) {
            int itemIndex = mData.indexOf(chip);
            mData.remove(chip);
            notifyItemRemoved(itemIndex);

            if(mLayoutManager.getItemCount() > 0){
                int offsetAmount = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 135, mDisplayMetrics);
                mLayoutManager.scrollToPositionWithOffset(mLayoutManager.getItemCount()-1, offsetAmount);
            } else {
                mLayoutManager.scrollToPosition(0);
            }


        }
    }

    public void replaceData(ArrayList<SearchChipModel> chips) {
        mData = chips;
        notifyDataSetChanged();

        int offsetAmount = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 135, mDisplayMetrics);
        int currentLastItem = getItemCount()-1;
        mLayoutManager.scrollToPositionWithOffset(currentLastItem, offsetAmount);
    }

    public ArrayList<SearchChipModel> getData() {
        return mData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = null;
        View v = null;
        if (viewType == 0) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_filter_chip_item, parent, false);
            vh = new SearchTagsViewHolder(v);
        } else if (viewType == 1) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_filter_edit_text, parent, false);
            vh = editTextViewHolder = new EditTextViewHolder(v);
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SearchTagsViewHolder) {
            SearchChipModel chip = mData.get(position);
            SearchTagsViewHolder vh = (SearchTagsViewHolder) holder;
            vh.tv.setText(chip.getChipTitle());
        }

        if (holder instanceof EditTextViewHolder) {
            EditTextViewHolder vh = (EditTextViewHolder) holder;

            vh.et.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vh.et.requestFocus();
                }
            });

            vh.et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){
                        mSaleItemPresenter.showSearchFilters();
                    } else {
                        mSaleItemPresenter.hideSearchFilters();
                    }
                }
            });

            vh.et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent
                        keyEvent) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        add(new SearchChipModel(BundleKeys.SEARCH_QUERY_NAME, vh.et.getText().toString(), -1));
                        vh.et.setText("");
                        mSaleItemPresenter.hideSearchFilters();
                    }

                    return false;
                }
            });

            vh.et.setBackPressedListener(et -> mSaleItemPresenter.hideSearchFilters());

            vh.et.setDeleteListener(() -> {
                //remove search tags
                int dataSize = getData().size();

                if (dataSize > 0) {
                    SearchChipModel chipToBeRemoved = getData().get(dataSize - 1);


                    getData().remove(chipToBeRemoved);
                    notifyItemRemoved(dataSize - 1);

//                    mPresenter.getOriginalSelectedSet().remove(chipToBeRemoved.getIndex());
//                    mLayoutManager.scrollToPosition(dataSize - 1);
//                    mShopPresenter.updateShopFilters();

                    if (chipToBeRemoved.getFilterType().equals(BundleKeys.PRICE_FACETFILTER_NAME)) {
//                        mPresenter.resetPriceRange();
                    }
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
        if (getItemCount() - 1 == position) { //if last, that should be search edit text
            return 1;
        }
        //else, its search chips
        return 0;
    }

    public static class SearchTagsViewHolder extends RecyclerView.ViewHolder {
        TextView tv;

        public SearchTagsViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.search_tag_text_item);
        }
    }

    public static class EditTextViewHolder extends RecyclerView.ViewHolder {
        private ChipsEditText et;

        public ChipsEditText getEditText() {
            return et;
        }

        public EditTextViewHolder(View itemView) {
            super(itemView);
            et = (ChipsEditText) itemView.findViewById(R.id.search_edit_text);
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
