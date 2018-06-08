package au.com.dealsdirect.ui.controller.account.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;

import java.util.Collections;
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
    private Context mContext;


    public AccountItemAdapter(
            Context context,
            List<AccountItem> accountItems,
            AccountMvpPresenter presenter) {
        this.mContext = context;
        this.mAccountItems = accountItems;
        this.mPresenter = presenter;
        setHasStableIds(true);
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_account_item, parent, false);
        return new AccountItemViewHolder(v);
    }

    @Override
    public AccountSubItemViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_account_sub_item, parent, false);
        return new AccountSubItemViewHolder(v);
    }

    @Override
    public void onBindGroupViewHolder(AccountItemViewHolder holder, int groupPosition, int viewType) {
//        holder.mAccountItemImage.setImageResource(mAccountImages.get(groupPosition));
        String title = mAccountItems.get(groupPosition).getTitle();

        if (title.equals(mContext.getString(R.string.account_options))) {
            holder.mAccountArrowRight.setVisibility(View.INVISIBLE);
        } else {
            holder.mAccountArrowRight.setVisibility(View.VISIBLE);
        }

        holder.mAccountItemName.setText(title);
        holder.itemView.setOnClickListener(view -> mPresenter.onAccountItemClick(mContext, title));

    }

    @Override
    public void onBindChildViewHolder(AccountSubItemViewHolder holder, int groupPosition, int childPosition, int viewType) {

//        holder.mAccountSubItemImage.setImageResource(mAccountImages.get(position));
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
}
