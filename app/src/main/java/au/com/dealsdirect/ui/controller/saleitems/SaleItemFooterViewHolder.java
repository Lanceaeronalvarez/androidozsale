package au.com.dealsdirect.ui.controller.saleitems;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import au.com.dealsdirect.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SaleItemFooterViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.footer_content_container)
    ViewGroup contentView;

    SaleItemFooterViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}
