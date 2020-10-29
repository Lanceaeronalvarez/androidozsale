package au.com.dealsdirect.ui.controller.account.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;

import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.controller.account.AccountMvpPresenter;
import au.com.dealsdirect.ui.controller.account.model.AccountItem;

/**
 * dp Created by Admin on 6/20/17.
 */

public class AccountItemAdapter extends AbstractExpandableItemAdapter<AccountItemViewHolder, AccountSubItemViewHolder> {

    private List<AccountItem> mAccountItems;
    private AccountMvpPresenter mPresenter;
    private Integer mSelectedPosition = 0;
    private Context mContext;
    private boolean mSelectFirstItem = false;
    private boolean mIsTablet = false;

    private RecyclerView mRecyclerView = null;

    public AccountItemAdapter(Context context, List<AccountItem> accountItems, AccountMvpPresenter presenter) {
        mContext = context;
        mAccountItems = accountItems;
        mPresenter = presenter;
        setHasStableIds(true);
    }

    public AccountItemAdapter(Context context, List<AccountItem> accountItems, AccountMvpPresenter presenter, boolean isSelectFirstItem) {
        this(context, accountItems, presenter);
        mSelectFirstItem = isSelectFirstItem;
        mIsTablet = isSelectFirstItem;
    }

    public List<AccountItem> getData() {
        return mAccountItems;
    }

    @Override
    public int getGroupCount() {
        return mAccountItems.size();
    }

    @Override
    public int getChildCount(int groupPosition) {
        return mAccountItems.get(groupPosition).getSubItems().size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return mAccountItems.get(groupPosition).getId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return mAccountItems.get(groupPosition).getSubItems().get(childPosition).getId();
    }

    @Override
    public AccountItemViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_text_item, parent, false);
        return new AccountItemViewHolder(v);
    }

    @Override
    public AccountSubItemViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_account_sub_item, parent, false);
        return new AccountSubItemViewHolder(v);
    }

    @Override
    public void onBindGroupViewHolder(AccountItemViewHolder holder, int groupPosition, int viewType) {
        String title = mAccountItems.get(groupPosition).getTitle();

        if (title.equals(mContext.getString(R.string.account_options))) {
            holder.mAccountArrowRight.setVisibility(View.INVISIBLE);
        } else {
            holder.mAccountArrowRight.setVisibility(View.VISIBLE);
        }

        holder.itemView.setSelected(groupPosition == mSelectedPosition);

        if (mSelectFirstItem && groupPosition == 0) {
            setSelectedPosition(groupPosition);
            mPresenter.onAccountItemClick(mContext, title);
            mSelectFirstItem = false; //reset
        }

        holder.mAccountItemName.setText(title);
        holder.itemView.setOnClickListener(view -> {
            if (mIsTablet && mPresenter.willScreenChange(mContext, title)) {
                setSelectedPosition(groupPosition);
            }
            mPresenter.onAccountItemClick(mContext, title);
        });

    }

    @Override
    public void onBindChildViewHolder(AccountSubItemViewHolder holder, int groupPosition, int childPosition, int viewType) {
        String title = mAccountItems.get(groupPosition).getSubItems().get(childPosition).getTitle();

        if (title.equals(mContext.getString(R.string.account_clear_cookies_data))) {
            holder.mAccountArrowRight.setVisibility(View.INVISIBLE);
        } else {
            holder.mAccountArrowRight.setVisibility(View.VISIBLE);
        }

        holder.mAccountSubItemName.setText(title);
        holder.itemView.setOnClickListener(view -> mPresenter.onAccountItemClick(mContext, title));

    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(AccountItemViewHolder holder, int groupPosition, int x, int y, boolean expand) {
        return true;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (mRecyclerView == recyclerView) {
            mRecyclerView = null;
        }
    }

    public void setSelectedPosition(Integer position) {
        Integer previous = mSelectedPosition;
        mSelectedPosition = position;
        if (mRecyclerView != null && !mRecyclerView.isComputingLayout()) {
            if (previous != null) {
                notifyItemChanged(previous);
            }
            if (mSelectedPosition != null) {
                notifyItemChanged(mSelectedPosition);
            }
        }
    }
}
