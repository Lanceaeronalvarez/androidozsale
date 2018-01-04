package au.com.dealsdirect.ui.controller.account;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import au.com.dealsdirect.R;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * dp Created by Admin on 6/20/17.
 */

public class AccountItemAdapter extends RecyclerView.Adapter<AccountItemViewHolder> {

    private List<Integer> mAccountItems = Collections.emptyList();
    private List<Integer> mAccountImages = Collections.emptyList();
    private AccountMvpPresenter mPresenter;
    private Context mContext;


    public AccountItemAdapter(
            Context context,
            List<Integer> mAccountItems,
            List<Integer> accountImages,
            AccountMvpPresenter presenter) {
        this.mContext = context;
        this.mAccountItems = mAccountItems;
        this.mAccountImages = accountImages;
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

        holder.mAccountItemImage.setImageResource(mAccountImages.get(position));

        holder.mAccountItemName
                .setText(mContext.getResources().getString(mAccountItems.get(position)));
        holder.itemView.setOnClickListener(view -> {
                mPresenter.onAccountItemClick(mAccountItems.get(position));
        });
    }

    @Override
    public int getItemCount() {
        if (mAccountItems == null) {
            return 0;
        }
        return mAccountItems.size();
    }

    public List<Integer> getData() {
        return mAccountItems;
    }
}
