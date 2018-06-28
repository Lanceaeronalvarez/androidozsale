package au.com.dealsdirect.ui.controller.account.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;

import javax.annotation.Nullable;

import au.com.dealsdirect.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by smartwave on 02/11/2017.
 */

public class AccountItemViewHolder extends AbstractExpandableItemViewHolder {

    @BindView(R.id.row_account_text)
    TextView mAccountItemName;

    @Nullable @BindView(R.id.row_account_image)
    ImageView mAccountItemImage;

    @BindView(R.id.row_account_arrow_right)
    ImageView mAccountArrowRight;

    AccountItemViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }
}
