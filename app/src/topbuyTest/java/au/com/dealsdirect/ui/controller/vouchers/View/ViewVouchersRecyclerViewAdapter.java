package au.com.dealsdirect.ui.controller.vouchers.View;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.vouchers.GetUserVoucherResponse;
import au.com.dealsdirect.utils.PriceUtils;

/**
 * Created by Paul on 6/27/17.
 */

public class ViewVouchersRecyclerViewAdapter extends RecyclerView.Adapter<ViewVouchersViewHolder> {

    private HashMap<Integer,String> voucherColorStateCollection = new HashMap<>();
    private List<GetUserVoucherResponse.Voucher> vouchersList;
    private Context context;

    public ViewVouchersRecyclerViewAdapter(List<GetUserVoucherResponse.Voucher> vouchersList,
                                           Context context) {
        this.vouchersList = vouchersList;
        this.context = context;

    }

    @Override
    public ViewVouchersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_view_vouchers,
                parent , false);
        ViewVouchersViewHolder viewHolder = new ViewVouchersViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewVouchersViewHolder holder, int position) {
        GetUserVoucherResponse.Voucher voucher =vouchersList.get(position);
        Matcher m = Pattern.compile("(?!=\\d\\.\\d\\.)([\\d.]+)").matcher(voucher.getDiscountLeft());

        if(m.find()) {
            Double doubleValue = Double.parseDouble(m.group(1));
            String mUseBefore = voucher.getExpired();
            Float voucherAmount = Float.parseFloat(m.group(1));
            DecimalFormat df = new DecimalFormat("0.00");
            df.setMaximumFractionDigits(2);

            String formattedVoucherValue = df.format(voucherAmount);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                int voucherValue = doubleValue.intValue();

                if(0 <= voucherValue && voucherValue < 10) {

                    holder.mVouchersLayout.setBackground(
                            context.getResources()
                                    .getDrawable(R.drawable.bg_voucher_container_red));

                }

                else if(10 <= voucherValue && voucherValue < 15){

                    holder.mVouchersLayout.setBackground(
                            context.getResources()
                                    .getDrawable(R.drawable.bg_voucher_container_blue));

                }

                else if(15 <= voucherValue && voucherValue < 20){

                    holder.mVouchersLayout.setBackground(
                            context.getResources()
                                    .getDrawable(R.drawable.voucher_container_yellow));

                }

                else if(20 <= voucherValue && voucherValue < 25){

                    holder.mVouchersLayout.setBackground(
                            context.getResources()
                                    .getDrawable(R.drawable.bg_voucher_container_green));

                }

                else if(25 <= voucherValue && voucherValue < 50){

                    holder.mVouchersLayout.setBackground(
                            context.getResources()
                                    .getDrawable(R.drawable.bg_voucher_container_violet));

                }

                else if(50 <= voucherValue && voucherValue < 125){

                    holder.mVouchersLayout.setBackground(
                            context.getResources()
                                    .getDrawable(R.drawable.bg_voucher_container_aqua));

                }

                else if(125 <= voucherValue && voucherValue < 500){

                    holder.mVouchersLayout.setBackground(
                            context.getResources()
                                    .getDrawable(R.drawable.bg_voucher_container_orange));

                }

                else if(500 <= voucherValue && voucherValue <= 1000){

                    holder.mVouchersLayout.setBackground(
                            context.getResources()
                                    .getDrawable(R.drawable.bg_voucher_container_darkblue));

                }
                else{

                    holder.mVouchersLayout.setBackground(
                            context.getResources()
                                    .getDrawable(R.drawable.bg_voucher_container_blue));

                }
            }


            String voucherCostWithCurrency = PriceUtils.getVoucherStringValue(formattedVoucherValue);
            holder.mVouchersItemCostText.setText(voucherCostWithCurrency);
            holder.mVouchersItemCostText.setVisibility(View.VISIBLE);
            holder.mVouchersItemDescText.setText(mUseBefore);
            holder.mVouchersLayout.setBackground(holder.mVouchersLayout.getContext().getDrawable(R.drawable.bg_voucher_item));

        } else {

            if(voucher.getDiscountLeft().equalsIgnoreCase("already spent")) {
                holder.mVouchersItemCostText.setVisibility(View.GONE);
            } else {
                holder.mVouchersItemCostText.setText(voucher.getDiscountLeft());
            }

            holder.mVouchersItemExpiresOnText.setText(voucher.getExpired());
            holder.mVouchersItemDescText.setText(voucher.getFullname());
            holder.mVouchersLayout.setBackground(holder.mVouchersLayout.getContext().getDrawable(R.drawable.bg_voucher_item));
            holder.mVouchersLayout.setAlpha(0.21f);
        }

    }

    public void replace(List<GetUserVoucherResponse.Voucher> vouchersList) {
        this.vouchersList = vouchersList;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return vouchersList.size();
    }
}
