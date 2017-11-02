package au.com.dealsdirect.ui.controller.account;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import au.com.dealsdirect.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by smartwave on 02/11/2017.
 */

public class AccountItemViewHolder extends RecyclerView.ViewHolder{

    @BindView(R.id.row_account_text)
    public TextView mAccountItemName;

    @BindView(R.id.row_account_image)
    public ImageButton mAccountItemImage;

    public AccountItemViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }
}
