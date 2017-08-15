package au.com.dealsdirect.ui.controller.vouchers.View;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.vouchers.GetUserVoucherResponse;

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


            String voucherCostWithCurrency = '$'+String.valueOf(doubleValue);
            holder.mVouchersItemCostText.setText(voucherCostWithCurrency);
            holder.mVouchersItemDescText.setText(mUseBefore);
            holder.mVouchersItemValue.setVisibility(View.VISIBLE);
            holder.mVouchersLayout.setBackground(holder.mVouchersLayout.getContext().getDrawable(R.drawable.bg_voucher_item));

        } else {

            holder.mVouchersItemExpiresOnText.setText(voucher.getExpired());
            holder.mVouchersItemDescText.setText(voucher.getFullname());
            holder.mVouchersItemValue.setVisibility(View.GONE);
            holder.mVouchersItemCostText.setText(voucher.getDiscountLeft());
            holder.mVouchersLayout.setBackground(holder.mVouchersLayout.getContext().getDrawable(R.drawable.bg_voucher_item));
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
