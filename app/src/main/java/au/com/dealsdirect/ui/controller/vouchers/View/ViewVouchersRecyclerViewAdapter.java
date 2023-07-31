package au.com.dealsdirect.ui.controller.vouchers.View;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.vouchers.GetUserVoucherResponse;

import static au.com.dealsdirect.data.network.model.vouchers.GetUserVoucherResponse.Status;

/**
 * Created by Paul on 6/27/17.
 */

public class ViewVouchersRecyclerViewAdapter extends RecyclerView.Adapter<ViewVouchersViewHolder> {

    private static final float GRAYED_OUT_ALPHA = 0.28f;

    private HashMap<Integer, String> voucherColorStateCollection = new HashMap<>();
    private List<GetUserVoucherResponse.Response> vouchersList;
    private Context mContext;
    private static final float UNUSED_VOUCHER_OVERLAY = 0.21f;
    private HashMap<String, Status> statusAssociatedString;

    public ViewVouchersRecyclerViewAdapter(List<GetUserVoucherResponse.Response> vouchersList,
                                           HashMap<String, Status> statusAssociatedString,
                                           Context context) {
        this.vouchersList = vouchersList;
        this.statusAssociatedString = statusAssociatedString;
        this.mContext = context;

    }

    @Override
    public ViewVouchersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_view_vouchers,
                parent, false);
        ViewVouchersViewHolder viewHolder = new ViewVouchersViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewVouchersViewHolder holder, int position) {
        GetUserVoucherResponse.Response voucher = vouchersList.get(position);

        holder.mVoucherName.setText(voucher.getFullname());

        holder.mVouchersItemCostText.setText(voucher.getDiscountGiven());
        holder.mVouchersItemCostText.setVisibility(View.VISIBLE);
        holder.mVouchersItemExpiresOnText.setText(voucher.getExpired());
        holder.mVouchersItemDescText.setText(voucher.getFullname());
        holder.mVouchersItemDescText.setVisibility(View.GONE);
        holder.mVouchersItemValue.setVisibility(View.VISIBLE);

        holder.itemView.setAlpha(1f);
        holder.mVouchersItemValue.setText("");
        holder.mStatusText.setText(voucher.getStatus());
        holder.mVouchersItemExpiresOnText.setVisibility(View.VISIBLE);
        Status status = statusAssociatedString.get(voucher.getStatus());
        if (status == null) {
            status = Status.NORMAL;
        }
        switch (status) {
            case PENDING:
                holder.mStatusText.setTextColor(mContext.getResources().getColor(R.color.orange));
                break;
            case NEW:
                holder.mStatusText.setTextColor(mContext.getResources().getColor(R.color.green));
                break;
            case EXPIRING_SOON:
                holder.mStatusText.setTextColor(mContext.getResources().getColor(R.color.activered));
                break;
            case ALREADY_SPENT:
                holder.itemView.setAlpha(GRAYED_OUT_ALPHA);
                holder.mVouchersItemExpiresOnText.setVisibility(View.GONE);
                holder.mStatusText.setTextColor(mContext.getResources().getColor(R.color.activered));
                break;
            case EXPIRED:
                holder.itemView.setAlpha(GRAYED_OUT_ALPHA);
                holder.mStatusText.setTextColor(mContext.getResources().getColor(R.color.text_medium));
                holder.mStatusText.setText("");
                break;
            default:
                holder.mStatusText.setTextColor(mContext.getResources().getColor(R.color.text_medium));
                if (!voucher.getDiscountLeft().isEmpty()) {
                    String text = mContext.getResources()
                            .getString(R.string.vouchers_balance_prefix) + voucher.getDiscountLeft();
                    holder.mVouchersItemValue.setText(text);
                }
                break;
        }

        holder.mVouchersLayout.setBackground(mContext.getResources().getDrawable(R.drawable.bg_voucher_item));
    }

    public void replace(List<GetUserVoucherResponse.Response> vouchersList) {
        this.vouchersList = vouchersList;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return vouchersList.size();
    }
}
