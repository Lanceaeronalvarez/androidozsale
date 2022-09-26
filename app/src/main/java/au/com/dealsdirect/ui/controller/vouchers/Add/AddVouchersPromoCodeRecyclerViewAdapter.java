package au.com.dealsdirect.ui.controller.vouchers.Add;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Value;
import au.com.dealsdirect.utils.PriceUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AddVouchersPromoCodeRecyclerViewAdapter extends RecyclerView.Adapter<AddVouchersPromoCodeRecyclerViewAdapter.PromoCodeViewHolder> {

    private final List<Value.PromoCode> appliedPromoCodes;
    private final PromoCodeDeleteButtonListener listener;

    public AddVouchersPromoCodeRecyclerViewAdapter(List<Value.PromoCode> appliedPromoCodes, PromoCodeDeleteButtonListener listener) {
        this.appliedPromoCodes = appliedPromoCodes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PromoCodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PromoCodeViewHolder holder = new PromoCodeViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_promo_code_view, parent, false));
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(holder.getBindingAdapterPosition());
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PromoCodeViewHolder holder, int position) {
        Value.PromoCode promoCode = appliedPromoCodes.get(position);
        String text = promoCode.getCode() + " - " + PriceUtils.getPriceStringValue(promoCode.getAmount());
        holder.textView.setText(text);
    }

    @Override
    public int getItemCount() {
        return appliedPromoCodes.size();
    }

    public static class PromoCodeViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.promo_code_text)
        public TextView textView;
        @BindView(R.id.promo_code_close_button)
        public ImageButton closeButton;

        public PromoCodeViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setCloseButtonEnabled(boolean enabled) {
            closeButton.setEnabled(enabled);
        }
    }

    public interface PromoCodeDeleteButtonListener {
        void onClick(int position);
    }
}
