package au.com.dealsdirect.ui.controller.account.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import au.com.dealsdirect.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * dp Created by Admin on 6/20/17.
 */

public class AccountItemViewHolder extends RecyclerView.ViewHolder{

    @BindView(R.id.row_account_text)
    public TextView mAccountItemName;

    @BindView(R.id.row_account_image)
    public ImageView mAccountItemImage;

    public AccountItemViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }
}
