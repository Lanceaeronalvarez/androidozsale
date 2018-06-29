package au.com.dealsdirect.ui.controller.checkout.paymentselect;
/*
 * Created by CodeineBot on 1/11/17.
 */

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
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
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionDoNothing;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionMoveToSwipedDirection;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.annotation.SwipeableItemResults;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;

import java.util.ArrayList;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.data.network.model.ourpaydashboard.Payment;
import au.com.dealsdirect.ui.main.MainActivity;
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

    public PaymentSelectAdapter(MainActivity activity, ArrayList<PaymentMethod> data,
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

        ImageUtils.loadImage(mActivity, item.getImageUrl(), holder.cardImageView);

        holder.nameTextView.setText(item.getPaymentType());
        holder.detailsText.setText(item.getDescription());

        holder.itemView.setBackground(mActivity.getResources().getDrawable(R.drawable.bg_swipe_item_neutral));
        holder.container.setBackgroundColor(mActivity.getResources().getColor(R.color.transparent));

        isItemViewSelected = mActivity.getPaymentMethodSelected() != null && mActivity.getPaymentMethodSelected().equals(item);
        holder.itemView.setSelected(isFromCart && isItemViewSelected);
        holder.nameTextView.setSelected(isFromCart && isItemViewSelected);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void replaceData(ArrayList<PaymentMethod> items) {
        mData = items;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return Integer.valueOf(mData.get(position).getId());
    }

    @Override
    public int onGetSwipeReactionType(PaymentSelectViewHolder holder, int position, int x, int y) {
        return SwipeableItemConstants.REACTION_CAN_SWIPE_LEFT;
    }

    @Override
    public void onSwipeItemStarted(PaymentSelectViewHolder holder, int position) {
        notifyDataSetChanged();
    }

    @Override
    public void onSetSwipeBackground(PaymentSelectViewHolder holder, int position, int type) {
        if (type == SwipeableItemConstants.DRAWABLE_SWIPE_LEFT_BACKGROUND) {
            holder.mDeleteText.setVisibility(View.VISIBLE);
            holder.container.setBackgroundColor(mActivity.getResources().getColor(isFromCart && holder.itemView.isSelected() ? R.color.item_view_selected_color :R.color.white));
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
            return new SwipeResultActionMoveToSwipedDirection() {
                @Override
                protected void onSlideAnimationEnd() {
                    super.onSlideAnimationEnd();
                    mData.remove(mData.get(position));
                    notifyItemRemoved(position);
                    notifyItemChanged(position);
                }

                @Override
                protected void onPerformAction() {
                    super.onPerformAction();
                    mPresenter.removeUserPaymentMethod(mData.get(position));
                }

                // Optionally, you can override these three methods
                // - void onPerformAction()
                // - void onSlideAnimationEnd()
                // - void onCleanUp()
            };

        } else {
            return new SwipeResultActionDoNothing();
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

}
