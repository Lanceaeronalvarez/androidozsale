package au.com.dealsdirect.ui.controller.vouchers.Add;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.vouchers.Voucher;
import au.com.dealsdirect.utils.PriceUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AddVouchersRecyclerViewAdapter extends RecyclerView.Adapter<AddVouchersRecyclerViewAdapter.AddVouchersViewHolder> {

    private final List<Voucher> mVoucherList;
    private final Set<String> appliedVouchers;

    private final VoucherClickListener voucherClickListener;

    public AddVouchersRecyclerViewAdapter(
            List<Voucher> voucherList,
            Set<String> appliedVouchers,
            VoucherClickListener voucherClickListener) {

        mVoucherList = new ArrayList<>(voucherList);
        this.appliedVouchers = new HashSet<>(appliedVouchers);
        this.voucherClickListener = voucherClickListener;
    }

    @Override
    public AddVouchersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_add_vouchers, parent, false);
        return new AddVouchersViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AddVouchersViewHolder vh, int position) {
        final Voucher voucher = mVoucherList.get(position);
        final String description = voucher.getDescription();
        final Float voucherAmount = findVoucherAmountFromDescription(voucher.getDescription());

        vh.mVoucherItemLayout.setSelected(appliedVouchers.contains(voucher.getId()));

        vh.mVoucherItemExpiresOnText.setVisibility(View.INVISIBLE);
        vh.mVoucherItemDescText.setText(getDescriptionExcludingFirstWord(description));
        vh.mVoucherItemCostText.setText(PriceUtils.getPriceStringValue(voucherAmount, true));
        vh.mVoucherItemLayout.setOnClickListener(view -> {
            final int index = vh.getBindingAdapterPosition();
            if (index == RecyclerView.NO_POSITION) {
                return;
            }
            final String voucherId = mVoucherList.get(index).getId();
            boolean isClicked = appliedVouchers.contains(voucherId);
            if (isClicked) {
                appliedVouchers.remove(voucherId);
                vh.mVoucherItemLayout.setSelected(false);
            } else {
                appliedVouchers.add(voucherId);
                vh.mVoucherItemLayout.setSelected(true);
            }

            if (voucherClickListener != null) {
                voucherClickListener.onClick(index);
            }
        });
    }

    private Float findVoucherAmountFromDescription(String description) {
        final Matcher m = Pattern.compile("(?!=\\d\\.\\d\\.)([\\d.]+)").matcher(description);
        if (m.find()) {
            final String found = m.group(1);
            return found != null ? Float.parseFloat(found) : 0f;
        } else {
            return 0f;
        }
    }

    private String getDescriptionExcludingFirstWord(String sourceString) {
        StringBuilder description = new StringBuilder();
        if (!sourceString.isEmpty()) {
            String[] splitString = sourceString.split(" ");
            // i = 1 to exclude the first word
            for (int i = 1; i < splitString.length; i++) {
                final String s = splitString[i];
                description.append(" ").append(s);
            }
        }
        return description.toString();
    }

    @Override
    public int getItemCount() {
        return mVoucherList.size();
    }

    public static class AddVouchersViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.row_layout_add_vouchers)
        public LinearLayout mVoucherItemLayout;

        @BindView(R.id.row_add_vouchers_text_item_cost_value)
        public TextView mVoucherItemCostText;
        @BindView(R.id.row_add_vouchers_text_item_expires_on_value)
        public TextView mVoucherItemExpiresOnText;
        @BindView(R.id.row_add_vouchers_text_item_desc)
        public TextView mVoucherItemDescText;

        public AddVouchersViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface VoucherClickListener {
        void onClick(int position);
    }
}
