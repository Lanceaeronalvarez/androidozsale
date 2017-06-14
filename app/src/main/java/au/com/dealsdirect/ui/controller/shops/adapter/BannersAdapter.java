package au.com.dealsdirect.ui.controller.shops.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.banner.GetPublicSalesBannerResponse;
import au.com.dealsdirect.ui.controller.shops.listener.BannerClickListener;
import au.com.dealsdirect.ui.controller.shops.viewholder.BannersViewHolder;
import au.com.dealsdirect.utils.DateUtils;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.LegacyStringImageUtils;

/**
 * dp Created by Admin on 6/7/17.
 */

public class BannersAdapter extends RecyclerView.Adapter<BannersViewHolder>{

    public static DisplayMetrics DISPLAY_METRICS;

    private List<GetPublicSalesBannerResponse.Sale> mSales;
    private Context mContext;
    private BannerClickListener mBannerClickListener;

    public BannersAdapter(
            Context context,
            List<GetPublicSalesBannerResponse.Sale> sales,
            BannerClickListener bannerClickListener) {

        this.mSales = sales;
        this.mContext = context;
        this.mBannerClickListener = bannerClickListener;
    }

    @Override public BannersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.viewholder_banner, parent, false);

        return new BannersViewHolder(v);

    }

    @Override public void onBindViewHolder(BannersViewHolder holder, final int position) {

        holder.bannerTitle.setText(mSales.get(position).getName());
        Date startDate = DateUtils.gmtDateFromServerDateString(mSales.get(position).getStart());
        String startDateString = mSales.get(position).getStart();
        String endDateString = mSales.get(position).getEnd();
        Long startDateUTC = DateUtils.getUTCFromServerDateString(startDateString);
        String formatted = getFormattedText(startDate,startDateUTC,startDateString,endDateString);

        String url = LegacyStringImageUtils.saleImageURLString(mSales.get(position));
        ImageUtils.loadImage(mContext,url,holder.bannerImage);
        holder.bannerDescription.setText(formatted);
        holder.bannerImage.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                mBannerClickListener.onBannerClicked(
                        mSales.get(position).getName(),
                        mSales.get(position).getID(),
                        position,
                        url);

            }
        });
    }

    @Override public int getItemCount() {
        return mSales.size();
    }

    public String getFormattedText(
            Date startDate,
            Long startDateUTC,
            String startDateString,
            String endDateString){

        Date now = new Date();
        String formatted = "";

        if (startDate.compareTo(now) < 0) // sale has started
        {
            // If sale started earlier today, then group by start date
            if (android.text.format.DateUtils.isToday(startDateUTC)) {

                String day = DateUtils.getDayOfWeekFromDateString(startDateString);
                String time = DateUtils.getTimeFromDateString(startDateString);
                formatted = "Started today at " + time;
            } else {
                // Group by end date

                String day = DateUtils.getDayOfWeekFromDateString(endDateString);
                String time = DateUtils.getTimeFromDateString(endDateString);
                formatted = "Ends " + day + " at " + time;
            }

        } else {

            if (android.text.format.DateUtils.isToday(startDateUTC)) {
                // Sale hasn't started yet and will start later today (group by start date)

                String day = DateUtils.getDayOfWeekFromDateString(startDateString);
                String time = DateUtils.getTimeFromDateString(startDateString);
                formatted = "Starting today" + day + " at " + time;
            } else {
                // Sale hasn't started yet and won't start later today (group by start date)

                String day = DateUtils.getDayOfWeekFromDateString(startDateString);
                String time = DateUtils.getTimeFromDateString(startDateString);
                formatted = "starts " + day + " at " + time;
            }
        }
        return formatted;
    }
}

