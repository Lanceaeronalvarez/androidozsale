package au.com.dealsdirect.ui.controller.vouchers.Add;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.vouchers.Voucher;
import au.com.dealsdirect.utils.PriceUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Paul on 6/30/17.
 */

public class AddVouchersRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private HashMap<Integer, String> voucherColorStateCollection = new HashMap<>();
    private AddVouchersMvpView mView;
    private List<Voucher> mVoucherList;
    private Context context;
    HashMap<Integer, Boolean> mVoucherOptionIndicator = new HashMap<>();

    public AddVouchersRecyclerViewAdapter(
            List<Voucher> voucherList,
            AddVouchersMvpView view,
            Context context) {

        mVoucherList = voucherList;
        this.context = context;
        this.mView = view;

        for (int i = 0; i <= mVoucherList.size(); i++) {
            mVoucherOptionIndicator.put(i, false);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_add_vouchers, parent, false);
        AddVouchersViewHolder vh = new AddVouchersViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AddVouchersViewHolder vh = (AddVouchersViewHolder) holder;
        String ticketState = "red";

        Voucher voucher = mVoucherList.get(position);
        String description = voucher.getDescription();
        Matcher m = Pattern.compile("(?!=\\d\\.\\d\\.)([\\d.]+)").matcher(description);

        //noinspection ResultOfMethodCallIgnored
        m.find();
        Double doubleValue = Double.valueOf(m.group(1));
        Float voucherAmount = Float.parseFloat(m.group(1));
        DecimalFormat df = new DecimalFormat("0.00");
        df.setMaximumFractionDigits(2);

        String formattedVoucherValue = df.format(voucherAmount);

        vh.mVoucherItemExpiresOnText.setVisibility(View.INVISIBLE);

        String finalDescription = "";
        if (!description.isEmpty()) {
            String[] splitString = description.split(" ");
            for (int i = 0; i < splitString.length; i++) {
                if (i != 0) {
                    finalDescription = finalDescription + " " + splitString[i];
                }
            }
        }

        vh.mVoucherItemDescText.setText(finalDescription);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            int voucherValue = doubleValue.intValue();

            if (0 <= voucherValue && voucherValue < 10) {
                vh.mVoucherItemLayout.setBackground(context.getResources().getDrawable(R.drawable.bg_voucher_container_red));
                ticketState = "red";
            } else if (10 <= voucherValue && voucherValue < 15) {
                vh.mVoucherItemLayout.setBackground(context.getResources().getDrawable(R.drawable.bg_voucher_container_blue));
                ticketState = "blue";
            } else if (15 <= voucherValue && voucherValue < 20) {
                vh.mVoucherItemLayout.setBackground(context.getResources().getDrawable(R.drawable.bg_voucher_container_yellow));
                ticketState = "yellow";
            } else if (20 <= voucherValue && voucherValue < 25) {
                vh.mVoucherItemLayout.setBackground(context.getResources().getDrawable(R.drawable.bg_voucher_container_green));
                ticketState = "green";
            } else if (25 <= voucherValue && voucherValue < 50) {
                vh.mVoucherItemLayout.setBackground(context.getResources().getDrawable(R.drawable.bg_voucher_container_violet));
                ticketState = "violet";
            } else if (50 <= voucherValue && voucherValue < 125) {
                vh.mVoucherItemLayout.setBackground(context.getResources().getDrawable(R.drawable.bg_voucher_container_aqua));
                ticketState = "aqua";
            } else if (125 <= voucherValue && voucherValue < 500) {
                vh.mVoucherItemLayout.setBackground(context.getResources().getDrawable(R.drawable.bg_voucher_container_orange));
                ticketState = "orange";
            } else if (500 <= voucherValue && voucherValue <= 1000) {
                vh.mVoucherItemLayout.setBackground(context.getResources().getDrawable(R.drawable.bg_voucher_container_darkblue));
                ticketState = "darkblue";
            } else {
                vh.mVoucherItemLayout.setBackground(context.getResources().getDrawable(R.drawable.bg_voucher_container_blue));
                ticketState = "blue";
            }
        }

        voucherColorStateCollection.put(position, ticketState);
        vh.mVoucherItemCostText.setText(PriceUtils.getVoucherStringValue(formattedVoucherValue));

        vh.mVoucherItemLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                boolean isClicked =  mVoucherOptionIndicator.get(position);
                if(isClicked){
                    mVoucherOptionIndicator.put(position, false);
                    vh.mVoucherItemLayout.setSelected(false);
                }else{
                    mVoucherOptionIndicator.put(position, true);
                    vh.mVoucherItemLayout.setSelected(true);
                }

                mView.onVoucherItemClicked(
                        voucher.getID(),
                        voucherColorStateCollection.get(position),
                        vh.mVoucherItemLayout,
                        position);
            }
        });
    }


    public void insert(int position, Voucher voucher) {
        mVoucherList.add(position, voucher);
        notifyItemInserted(position);
    }

    public void remove(Voucher data) {
        int position = mVoucherList.indexOf(data);
        mVoucherList.remove(position);
        notifyItemRemoved(position);
    }

    public void replace(List<Voucher> vouchers) {
        mVoucherList = vouchers;
        notifyDataSetChanged();
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

}
