package au.com.dealsdirect.ui.controller.account.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.controller.account.AccountMvpPresenter;
import au.com.dealsdirect.ui.controller.account.viewholder.AccountItemViewHolder;

/**
 * dp Created by Admin on 6/20/17.
 */

public class AccountItemAdapter extends RecyclerView.Adapter<AccountItemViewHolder>{

    private List<String> mAccountItems = Collections.emptyList();
    private int[] mAccountImages;
    private Context mContext;
    private AccountMvpPresenter mPresenter;


    public AccountItemAdapter(
            List<String> mAccountItems,
            int[] accountImages,
            Context mContext,
            AccountMvpPresenter presenter) {

        this.mAccountItems = mAccountItems;
        this.mAccountImages = accountImages;
        this.mContext = mContext;
        this.mPresenter = presenter;
    }

    @Override
    public AccountItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         View v = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.viewholder_account_item,
                                         parent,
                                         false);

         return new AccountItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AccountItemViewHolder holder, int position) {

        holder.mAccountItemImage.setImageResource(mAccountImages[position]);

        holder.mAccountItemName
                .setText(mAccountItems.get(position));
        holder.itemView.setOnClickListener(o -> mPresenter.onAccountItemClick(mAccountItems.get(position)));
    }

    @Override
    public int getItemCount() {
        if (mAccountItems == null) {
            return 0;
        }
        return mAccountItems.size();
    }

    public void removeItemAtPosition(int position) {
        mAccountItems.remove(position);
    }

    public String getItemAtPosition(int position){ return mAccountItems.get(position); }

    public List<String> getList(){ return mAccountItems; }
}
