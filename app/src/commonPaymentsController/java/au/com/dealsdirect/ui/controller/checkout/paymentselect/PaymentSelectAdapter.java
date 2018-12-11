package au.com.dealsdirect.ui.controller.checkout.paymentselect;
/*
 * Created by CodeineBot on 1/11/17.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.ImageUtils;

public class PaymentSelectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private MainActivity mActivity;
    private ArrayList<PaymentMethod> mData;
    private PaymentSelectMvpPresenter<PaymentSelectMvpView> mPresenter;
    private boolean isFromCart = false;


    public PaymentSelectAdapter(MainActivity activity, ArrayList<PaymentMethod> data,
                                PaymentSelectMvpPresenter<PaymentSelectMvpView> presenter,
                                boolean fromCart) {

        this.mActivity = activity;
        this.mData = data;
        this.mPresenter = presenter;
        this.isFromCart = fromCart;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.partial_payment_select_item, parent, false);
        return new PaymentSelectViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PaymentSelectViewHolder vh = (PaymentSelectViewHolder) holder;

        PaymentMethod item = mData.get(position);

        ImageUtils.loadImage(item.getImageUrl(), vh.cardImageView);

        vh.nameTextView.setText(item.getPaymentType());
        vh.detailsText.setText(item.getDescription());

        vh.removeView.setVisibility(isFromCart ? View.GONE : View.VISIBLE);

        if (isFromCart) {
            vh.itemView.setSelected(isFromCart && mActivity.getPaymentMethodSelected() != null && mActivity.getPaymentMethodSelected().equals(item));
            vh.nameTextView.setSelected(isFromCart && mActivity.getPaymentMethodSelected() != null && mActivity.getPaymentMethodSelected().equals(item));
        } else {
            vh.removeView.setOnClickListener(view -> {
                mActivity.showLoading();
                mPresenter.removeUserPaymentMethod(mData.get(position));
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void replaceData(ArrayList<PaymentMethod> items) {
        mData = items;
        notifyDataSetChanged();
    }

    public static class PaymentSelectViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView detailsText;
        ImageView cardImageView;
        View dividerView;
        View removeView;

        public PaymentSelectViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.partial_checkout_payment_name);
            detailsText = (TextView) itemView.findViewById(R.id.partial_checkout_payment_details);
            cardImageView = (ImageView) itemView.findViewById(R.id.partial_checkout_payment_image);
            dividerView = itemView.findViewById(R.id.item_payment_select_divider);
            removeView = itemView.findViewById(R.id.partial_checkout_payment_remove);
        }
    }

}
