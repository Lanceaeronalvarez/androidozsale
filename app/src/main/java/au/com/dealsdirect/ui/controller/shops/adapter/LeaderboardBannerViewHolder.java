package au.com.dealsdirect.ui.controller.shops.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import au.com.dealsdirect.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;

class LeaderboardBannerViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.viewholder_banner_layout)
    ViewGroup layout;

    @BindView(R.id.viewholder_banner_image)
    ImageView image;

    LeaderboardBannerViewHolder(View view, int height) {
        super(view);
        ButterKnife.bind(this, view);

        if (height > 0) {
            ViewGroup.LayoutParams params = layout.getLayoutParams();
            params.height = height;
            layout.setLayoutParams(params);
        }
    }

    LeaderboardBannerViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    Disposable subscription;
}
