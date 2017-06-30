package au.com.dealsdirect.ui.controller.vouchers.Add;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.vouchers.Voucher;

/**
 * Created by Paul on 6/30/17.
 */

public class AddVouchersRecycleViewAdapter extends RecyclerView.Adapter<AddVouchersViewHolder> {

    private HashMap<Integer,String> voucherColorStateCollection = new HashMap<>();
//    private MyVouchersItemClickListener mListener;
    private List<Voucher> mVoucherList;
    private Context context;

    public AddVouchersRecycleViewAdapter(
            List<Voucher> voucherList,
            AddVouchersItemClickListener myVouchersItemClickListener,
            Context context ) {

        mVoucherList = voucherList;
        this.context = context;
//        this.mListener = myVouchersItemClickListener;
    }

    @Override
    public AddVouchersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_add_vouchers, parent, false);
        AddVouchersViewHolder vh = new AddVouchersViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(AddVouchersViewHolder holder, int position) {
        String ticketState = "red";

        Voucher voucher = mVoucherList.get(position);
        String description = voucher.getDescription();

        Matcher m = Pattern.compile("(?!=\\d\\.\\d\\.)([\\d.]+)").matcher(description);

        //noinspection ResultOfMethodCallIgnored
        m.find();
        Double doubleValue = Double.parseDouble(m.group(1));
        holder.mVoucherItemExpiresOnText.setVisibility(View.INVISIBLE);

        String finalDescription = "";
        if (!description.isEmpty()){
            String[] splitString = description.split(" ");
            for (int i = 0; i < splitString.length; i++){
                if (i!=0){
                    finalDescription = finalDescription +" "+ splitString[i];
                }
            }
        }

        holder.mVoucherItemDescText.setText(finalDescription);


        Log.d("voucherDescription", "description : "+description);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            int voucherValue = doubleValue.intValue();
//
//            if(0 <= voucherValue && voucherValue < 10) {
//
//                holder.mVoucherItemLayout.setBackground(
//                        context.getResources()
//                                .getDrawable(R.drawable.voucher_container_red));
//
//                ticketState = "red";
//            }
//
//            else if(10 <= voucherValue && voucherValue < 15){
//
//                holder.mVoucherItemLayout.setBackground(
//                        context.getResources()
//                                .getDrawable(R.drawable.voucher_container_blue));
//
//                ticketState = "blue";
//            }
//
//            else if(15 <= voucherValue && voucherValue < 20){
//
//                holder.mVoucherItemLayout.setBackground(
//                        context.getResources()
//                                .getDrawable(R.drawable.voucher_container_yellow));
//
//                ticketState = "yellow";
//            }
//
//            else if(20 <= voucherValue && voucherValue < 25){
//
//                holder.mVoucherItemLayout.setBackground(
//                        context.getResources()
//                                .getDrawable(R.drawable.voucher_container_green));
//
//                ticketState = "green";
//            }
//
//            else if(25 <= voucherValue && voucherValue < 50){
//
//                holder.mVoucherItemLayout.setBackground(
//                        context.getResources()
//                                .getDrawable(R.drawable.voucher_container_violet));
//
//                ticketState = "violet";
//            }
//
//            else if(50 <= voucherValue && voucherValue < 125){
//
//                holder.mVoucherItemLayout.setBackground(
//                        context.getResources()
//                                .getDrawable(R.drawable.voucher_container_aqua));
//
//                ticketState = "aqua";
//            }
//
//            else if(125 <= voucherValue && voucherValue < 500){
//
//                holder.mVoucherItemLayout.setBackground(
//                        context.getResources()
//                                .getDrawable(R.drawable.voucher_container_orange));
//
//                ticketState = "orange";
//            }
//
//            else if(500 <= voucherValue && voucherValue <= 1000){
//
//                holder.mVoucherItemLayout.setBackground(
//                        context.getResources()
//                                .getDrawable(R.drawable.voucher_container_darkblue));
//
//                ticketState = "darkblue";
//            }
//
//
//            else{
//
//                holder.mVoucherItemLayout.setBackground(
//                        context.getResources()
//                                .getDrawable(R.drawable.voucher_container_blue));
//
//                ticketState = "blue";
//            }
//        }

        voucherColorStateCollection.put(position, ticketState);
//        holder.mVoucherItemCostText.setText(
//                GPriceUtil.getPriceStringValue(doubleValue)+" value");
        holder.mVoucherItemCostText.setText(String.valueOf(doubleValue)+" value");

        final String finalTicketState = voucherColorStateCollection.get(position);

        holder.mVoucherItemLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

//                GDebug.log(this.getClass().getSimpleName(), "voucher item is clicked");
//                mListener.onVoucherItemClicked(
//                        voucher.getID(),
//                        finalTicketState,
//                        holder.voucherItemLayout,
//                        position);
            }
        });
    }


    public void insert(int position, Voucher voucher){
        mVoucherList.add(position, voucher);
        notifyItemInserted(position);
    }

    public void remove(Voucher data) {
        int position = mVoucherList.indexOf(data);
        mVoucherList.remove(position);
        notifyItemRemoved(position);
    }

    public void replace(List<Voucher> vouchers){
        mVoucherList = vouchers;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mVoucherList.size();
    }
}
