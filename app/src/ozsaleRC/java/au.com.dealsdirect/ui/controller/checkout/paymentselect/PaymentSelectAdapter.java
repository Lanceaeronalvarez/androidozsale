package au.com.dealsdirect.ui.controller.checkout.paymentselect;
/*
 * Created by CodeineBot on 1/11/17.
 */

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionDefault;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;
import com.stripe.android.model.Card;

import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.ImageUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentSelectAdapter extends RecyclerView.Adapter<PaymentSelectAdapter.PaymentSelectViewHolder>
        implements SwipeableItemAdapter<PaymentSelectAdapter.PaymentSelectViewHolder> {

    private MainActivity mActivity;
    private ArrayList<PaymentMethod> mData;
    private PaymentSelectMvpPresenter<PaymentSelectMvpView> mPresenter;
    private boolean isFromCart = false;
    private boolean isItemViewSelected;

    public PaymentSelectAdapter(MainActivity activity,
                                ArrayList<PaymentMethod> data,
                                PaymentSelectMvpPresenter<PaymentSelectMvpView> presenter,
                                boolean fromCart) {

        this.mActivity = activity;
        this.mData = data;
        this.mPresenter = presenter;
        this.isFromCart = fromCart;
        setHasStableIds(true);
    }

    @Override
    public PaymentSelectAdapter.PaymentSelectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.partial_payment_select_item, parent, false);
        return new PaymentSelectViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PaymentSelectAdapter.PaymentSelectViewHolder holder, int position) {

        PaymentMethod item = mData.get(position);

        if (item.getImageUrl() != null) {
            ImageUtils.loadImage(item.getImageUrl(), holder.cardImageView);
        }

        if (item.getPaymentType().equalsIgnoreCase(AppConstants.AMEX) ||
                item.getPaymentType().equalsIgnoreCase(AppConstants.AMERICAN_EXPRESS)) {
            holder.cardImageView.setImageResource(Card.getBrandIcon(Card.CardBrand.AMERICAN_EXPRESS));
        } else {
            holder.cardImageView.setImageResource(Card.getBrandIcon(Card.asCardBrand(item.getPaymentType())));
        }

        holder.nameTextView.setText(item.getPaymentType());
        holder.detailsText.setText(item.getDescription());

        holder.itemView.setBackground(mActivity.getResources().getDrawable(R.drawable.bg_swipe_item_neutral));
        holder.container.setBackgroundColor(mActivity.getResources().getColor(R.color.transparent));

        isItemViewSelected = mActivity.getPaymentMethodSelected() != null && mActivity.getPaymentMethodSelected().equals(item);
        holder.itemView.setSelected(isFromCart && isItemViewSelected);
        holder.nameTextView.setSelected(isFromCart && isItemViewSelected);

        holder.mDeleteText.setOnClickListener(view -> {
            notifyItemRemoved(position);
            mPresenter.removeUserPaymentMethod(mData.get(position));
            mData.remove(mData.get(position));
            notifyItemChanged(position);
        });

        holder.setMaxLeftSwipeAmount(-0.2f);
        holder.setMaxRightSwipeAmount(0);
        holder.setSwipeItemHorizontalSlideAmount(mData.get(position).isPinned() ? -0.2f : 0);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void replaceData(ArrayList<PaymentMethod> items) {
        this.mData.clear();
        this.mData.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return mData.get(position).hashCode();
    }

    @Override
    public int onGetSwipeReactionType(PaymentSelectViewHolder holder, int position, int x, int y) {
        return isFromCart? SwipeableItemConstants.REACTION_CAN_NOT_SWIPE_ANY : SwipeableItemConstants.REACTION_CAN_SWIPE_LEFT;
    }

    @Override
    public void onSwipeItemStarted(PaymentSelectViewHolder holder, int position) {
    }

    @Override
    public void onSetSwipeBackground(PaymentSelectViewHolder holder, int position, int type) {
        if (type == SwipeableItemConstants.DRAWABLE_SWIPE_LEFT_BACKGROUND) {
            holder.mDeleteText.setVisibility(View.VISIBLE);
            holder.container.setBackgroundColor(mActivity.getResources().getColor(isFromCart && holder.itemView.isSelected() ? R.color.item_view_selected_color : R.color.white));
            holder.parent.setBackground(mActivity.getResources().getDrawable(R.drawable.bg_swipe_item_right, null));
        } else {
            holder.mDeleteText.setVisibility(View.GONE);
            holder.container.setBackgroundColor(mActivity.getResources().getColor(R.color.transparent));
            holder.parent.setBackground(mActivity.getResources().getDrawable(isItemViewSelected ? R.drawable.bg_checkout_options : R.drawable.bg_swipe_item_neutral, null));
        }
    }


    @Override
    public SwipeResultAction onSwipeItem(PaymentSelectViewHolder holder, int position, int result) {
        if (result == SwipeableItemConstants.RESULT_SWIPED_LEFT) {
            return new SwipeLeftResultAction(this, position);
        } else {
            return position != RecyclerView.NO_POSITION ? new UnpinResultAction(this, position) : null;
        }
    }

    public static class PaymentSelectViewHolder extends AbstractSwipeableItemViewHolder {
        @BindView(R.id.partial_checkout_payment_name)
        TextView nameTextView;

        @BindView(R.id.partial_checkout_payment_details)
        TextView detailsText;

        @BindView(R.id.partial_checkout_payment_image)
        ImageView cardImageView;

        @BindView(R.id.controller_payment_select_container)
        FrameLayout container;

        @BindView(R.id.controller_payment_select_parent)
        RelativeLayout parent;

        @BindView(R.id.controller_address_delete_text)
        TextView mDeleteText;


        public PaymentSelectViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public View getSwipeableContainerView() {
            return container;
        }
    }

    private static class UnpinResultAction extends SwipeResultActionDefault {
        PaymentSelectAdapter mAdapter;
        int mPosition;

        UnpinResultAction(PaymentSelectAdapter adapter, int position) {
            mAdapter = adapter;
            mPosition = position;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();
            PaymentMethod item = mAdapter.mData.get(mPosition);
            if (item.isPinned()) {
                item.setPinned(false);
//                onswipefinished2 by wrapper adapter automatically calls notifydatasetchanged
//                mAdapter.notifyItemChanged(mPosition);
            }
        }
    }

    private static class SwipeLeftResultAction extends SwipeResultActionDefault {
        PaymentSelectAdapter mAdapter;
        int mPosition;

        SwipeLeftResultAction(PaymentSelectAdapter adapter, int position) {
            mAdapter = adapter;
            mPosition = position;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();
            PaymentMethod item = mAdapter.mData.get(mPosition);
            if (!item.isPinned()) {
                item.setPinned(true);
//                onswipefinished2 by wrapper adapter automatically calls notifydatasetchanged
//                mAdapter.notifyItemChanged(mPosition);
            }
        }
    }


    private static class PaymentSelectDiffUtils extends DiffUtil.Callback {
        List<PaymentMethod> oldList;
        List<PaymentMethod> newList;

        PaymentSelectDiffUtils(List<PaymentMethod> oldList, List<PaymentMethod> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }

        @Nullable
        @Override
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            return super.getChangePayload(oldItemPosition, newItemPosition);
        }
    }

}
