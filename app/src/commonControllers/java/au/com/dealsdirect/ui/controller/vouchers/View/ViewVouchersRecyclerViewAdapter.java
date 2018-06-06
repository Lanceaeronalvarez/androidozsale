package au.com.dealsdirect.ui.controller.vouchers.View;

import android.content.Context;
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
    private Context mContext;
    private static final float UNUSED_VOUCHER_OVERLAY = 0.21f;

    public ViewVouchersRecyclerViewAdapter(List<GetUserVoucherResponse.Voucher> vouchersList,
                                           Context context) {
        this.vouchersList = vouchersList;
        this.mContext = context;

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

        holder.mVoucherName.setText(voucher.getFullname());

        boolean hasMatch = m.find();
        boolean hasSpent = voucher.getDiscountLeft().equalsIgnoreCase(mContext.getString(R.string.already_spent));

        if(hasMatch) {
            Double doubleValue = Double.parseDouble(m.group(1));
            String mUseBefore = voucher.getExpired();
            Float voucherAmount = Float.parseFloat(m.group(1));
            DecimalFormat df = new DecimalFormat("0.00");
            df.setMaximumFractionDigits(2);

            String formattedVoucherValue = df.format(voucherAmount);

            String voucherCostWithCurrency = PriceUtils.getVoucherStringValue(formattedVoucherValue);
            holder.mVouchersItemCostText.setText(voucherCostWithCurrency);
            holder.mVouchersItemCostText.setVisibility(View.VISIBLE);
            holder.mVouchersAlreadySpent.setVisibility(View.GONE);
            holder.mVouchersItemDescText.setText(mUseBefore);
            holder.mVouchersItemValue.setVisibility(View.VISIBLE);
            holder.mActivatedValue.setText(String.valueOf(hasMatch));
            holder.mPurchasedValue.setText(String.valueOf(hasSpent));
            holder.mVouchersLayout.setBackground(mContext.getResources().getDrawable(R.drawable.bg_voucher_item));

        } else {

            if(hasSpent) {
                holder.mVouchersItemCostText.setVisibility(View.GONE);
                holder.mVouchersAlreadySpent.setVisibility(View.VISIBLE);
                holder.mPurchasedValue.setText(String.valueOf(hasSpent));
            } else {
                holder.mVouchersItemCostText.setText(voucher.getDiscountLeft());
                holder.mPurchasedValue.setText(String.valueOf(hasSpent));
            }

            holder.mVouchersItemExpiresOnText.setText(voucher.getExpired());
            holder.mVouchersItemDescText.setText(voucher.getFullname());
            holder.mVouchersItemValue.setVisibility(View.GONE);
            holder.mVouchersLayout.setBackground(holder.mVouchersLayout.getContext().getDrawable(R.drawable.bg_voucher_item));
            holder.mVouchersLayout.setAlpha(UNUSED_VOUCHER_OVERLAY);
            holder.mActivatedValue.setText(String.valueOf(hasMatch));
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
