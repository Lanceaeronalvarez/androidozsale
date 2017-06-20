package au.com.dealsdirect.ui.controller.account.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import au.com.dealsdirect.R;

/**
 * dp Created by Admin on 6/20/17.
 */

public class AccountItemViewHolder extends RecyclerView.ViewHolder{

    public TextView mAccountItemName;

    public AccountItemViewHolder(View itemView) {
        super(itemView);

        mAccountItemName = (TextView) itemView
                .findViewById(R.id.viewholder_account_item_name);
    }
}
