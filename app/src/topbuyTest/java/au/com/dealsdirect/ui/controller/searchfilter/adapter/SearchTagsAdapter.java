package au.com.dealsdirect.ui.controller.searchfilter.adapter;

import android.content.Context;
import android.os.Bundle;
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

import com.google.gson.Gson;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.mysale.genie.utility.RxBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsMvpPresenter;
import au.com.dealsdirect.ui.custom.ChipsEditText;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;


/**
 * Created by smartwave on 21/07/2017.
 */

public class SearchTagsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SearchChipModel> mData = new ArrayList<>();
    private DisplayMetrics mDisplayMetrics;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private SaleItemsMvpPresenter mSaleItemPresenter;
    private boolean mBrandsActive = false;
    private boolean mColorsActive = false;
    private boolean mSizesActive = false;
    private boolean mPriceActive = false;

    public boolean isBrandsActiveFilter() {
        return mBrandsActive;
    }

    public boolean isColorsActiveFilter() {
        return mColorsActive;
    }

    public boolean isSizesActiveFilter() {
        return mSizesActive;
    }

    public boolean isPriceActiveFilter() {
        return mPriceActive;
    }

    public EditTextViewHolder getEditTextViewHolder() {
        return editTextViewHolder;
    }

    public EditTextViewHolder editTextViewHolder;

    public SearchTagsAdapter(Context ctx, RecyclerView rv, List<SearchChipModel> items, SaleItemsMvpPresenter presenter) {
        mDisplayMetrics = ctx.getResources().getDisplayMetrics();
        mRecyclerView = rv;
        mLayoutManager = (LinearLayoutManager)rv.getLayoutManager();
        mData = items;
        mSaleItemPresenter = presenter;
    }

    public void add(SearchChipModel chip) {
        if (mData != null) {
            mData.add(chip);
            int addedItemIndex = mData.indexOf(chip);
            notifyItemInserted(mData.indexOf(chip));

            int offsetAmount = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 135, mDisplayMetrics);
            int currentLastItem = getItemCount() - 1;

            mLayoutManager.scrollToPosition(addedItemIndex);
            mLayoutManager.scrollToPositionWithOffset(currentLastItem, offsetAmount);

        }
    }

    public void remove(SearchChipModel chip) {
        if (mData != null) {
            int itemIndex = mData.indexOf(chip);
            mData.remove(chip);
            notifyItemRemoved(itemIndex);

            if (mLayoutManager.getItemCount() > 0) {
                int offsetAmount = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 135, mDisplayMetrics);
                mLayoutManager.scrollToPositionWithOffset(mLayoutManager.getItemCount() - 1, offsetAmount);
            } else {
                mLayoutManager.scrollToPosition(0);
            }

        }
    }

    public void determineActiveFilters(){
        mBrandsActive = false;
        mColorsActive = false;
        mSizesActive = false;
        mPriceActive = false;

        for (SearchChipModel chip : getData()) {
            if(chip.getFilterType() == BundleKeys.BRANDS_FACETFILTER_NAME){
                mBrandsActive = true;
            }

            if(chip.getFilterType() == BundleKeys.COLORS_FACETFILTER_NAME){
                mColorsActive = true;
            }

            if(chip.getFilterType() == BundleKeys.SIZES_FACETFILTER_NAME){
                mSizesActive = true;
            }

            if(chip.getFilterType() == BundleKeys.PRICE_FACETFILTER_NAME){
                mPriceActive = true;
            }
        }
    }

    public void replaceData(ArrayList<SearchChipModel> chips) {
        mData = chips;
        notifyDataSetChanged();

        int offsetAmount = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 135, mDisplayMetrics);
        int currentLastItem = getItemCount() - 1;
        mLayoutManager.scrollToPositionWithOffset(currentLastItem, offsetAmount);
    }

    public List<SearchChipModel> getData() {
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
            vh = editTextViewHolder = new EditTextViewHolder(v, mSaleItemPresenter);
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SearchTagsViewHolder) {
            SearchChipModel chip = mData.get(position);
            SearchTagsViewHolder vh = (SearchTagsViewHolder) holder;
            vh.tv.setText(chip.getChipTitle());
            vh.itemView.setOnClickListener((v) -> {
                mSaleItemPresenter.showSearchFilters(chip.getFilterType());
            });
        }

        if (holder instanceof EditTextViewHolder) {
            EditTextViewHolder vh = (EditTextViewHolder) holder;


            vh.et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        mSaleItemPresenter.showSearchFilters("");
                        vh.subscribeTextChange();
                    } else {
                        mSaleItemPresenter.hideSearchFilters();
                        vh.textChangeDisposable.dispose();
                    }
                }
            });

            vh.et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent
                        keyEvent) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
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

                    remove(chipToBeRemoved);
//                    getData().remove(chipToBeRemoved);
//                    notifyItemRemoved(dataSize - 1);

//                    mPresenter.getOriginalSelectedSet().remove(chipToBeRemoved.getIndex());
//                    mLayoutManager.scrollToPosition(dataSize - 1);
                    mSaleItemPresenter.updateShopFilters();

                    RxBus.instance().post(new BundleBuilder(new Bundle())
                            .putString(BundleKeys.KEY_CHIP_TO_REMOVE, new Gson().toJson(chipToBeRemoved)).build());
//
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

        SaleItemsMvpPresenter mSaleItemPresenter;

        public Disposable getTextChangeDisposable() {
            return textChangeDisposable;
        }

        Disposable textChangeDisposable;

        public ChipsEditText getEditText() {
            return et;
        }

        public EditTextViewHolder(View itemView, SaleItemsMvpPresenter saleItemsMvpPresenter) {
            super(itemView);
            et = (ChipsEditText) itemView.findViewById(R.id.search_edit_text);
            mSaleItemPresenter = saleItemsMvpPresenter;
        }

        public void subscribeTextChange() {
            textChangeDisposable = RxTextView.textChanges(et)
                    .skipInitialValue()
                    .debounce(1000, TimeUnit.MILLISECONDS)
                    .map(charSequence -> charSequence.toString())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((searchQuery) -> {
                        mSaleItemPresenter.loadSaleItemsWhileTyping(searchQuery);
                    });
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
