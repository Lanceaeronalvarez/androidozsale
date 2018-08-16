package au.com.dealsdirect.ui.controller.account.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;

import au.com.dealsdirect.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by smartwave on 24/05/2018.
 */

public class AccountSubItemViewHolder extends AbstractExpandableItemViewHolder {

    @BindView(R.id.row_viewholder_text)
    TextView mAccountSubItemName;

    @BindView(R.id.row_viewholder_image_right)
    ImageView mAccountArrowRight;

    AccountSubItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

