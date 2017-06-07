package au.com.dealsdirect.ui.controller.shops.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import au.com.dealsdirect.R;

/**
 * dp Created by Admin on 6/7/17.
 */

public class BannersViewHolder extends RecyclerView.ViewHolder{

    public TextView bannerTitle;
    public TextView bannerDescription;
    public ImageView bannerImage;

    public BannersViewHolder(View itemView) {
        super(itemView);

        bannerTitle = (TextView) itemView.findViewById(R.id.viewholder_banner_title);
        bannerDescription = (TextView) itemView.findViewById(R.id.viewholder_banner_description);
        bannerImage = (ImageView) itemView.findViewById(R.id.viewholder_banner_image);

    }
}
