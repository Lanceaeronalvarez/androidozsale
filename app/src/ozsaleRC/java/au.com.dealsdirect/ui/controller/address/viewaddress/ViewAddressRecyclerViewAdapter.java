package au.com.dealsdirect.ui.controller.address.viewaddress;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionDoNothing;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionMoveToSwipedDirection;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;

import java.util.Collections;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.address.Address;
import au.com.dealsdirect.data.network.model.address.AddressesItem;
import au.com.dealsdirect.data.network.model.address.DeleteUserAddress;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryAddress;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by smartwave on 21/06/2017.
 */


public class ViewAddressRecyclerViewAdapter extends RecyclerView.Adapter<ViewAddressRecyclerViewAdapter.MyAddressModuleViewHolder>
        implements SwipeableItemAdapter<ViewAddressRecyclerViewAdapter.MyAddressModuleViewHolder> {

    private List<AddressesItem> addressList = Collections.emptyList();
    private Context mContext;
    private Boolean isCalledFromCart;
    private DeliveryAddress mDeliveryAddress;
    private ViewAddressMvpPresenter mPresenter;
    private int position;

    public ViewAddressRecyclerViewAdapter(
            Boolean calledFromCart,
            List<AddressesItem> addressList,
            Context context,
            DeliveryAddress deliveryAddress,
            ViewAddressMvpPresenter presenter) {

        this.isCalledFromCart = calledFromCart;
        this.addressList = addressList;
        this.mContext = context;
        this.mDeliveryAddress = deliveryAddress;
        this.mPresenter = presenter;
        setHasStableIds(true);
    }

    public void updateDeliveryAddress(AddressesItem addressesItem) {
        mDeliveryAddress.resetDataFromAddressItem(addressesItem);
        notifyDataSetChanged();
    }

    @Override
    public MyAddressModuleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_row_layout, parent, false);
        MyAddressModuleViewHolder holder = new MyAddressModuleViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyAddressModuleViewHolder holder, int position) {
        this.position = position;

        boolean isAddressSelected = mDeliveryAddress != null && mDeliveryAddress.equalsAddressItem(addressList.get(position));

        holder.addressNumberTextView.setText(addressList.get(position).getAddressName());
        holder.addressTextView.setText(String.valueOf(addressList.get(position).getFullAddress()));

        holder.itemView.setSelected(isCalledFromCart && isAddressSelected);
        holder.addressNumberTextView.setSelected(isCalledFromCart && isAddressSelected);
    }

    @Override
    public int getItemCount() {
        return addressList == null ? 0 : addressList.size();

    }

    public void replaceData(List<AddressesItem> items) {
        addressList = items;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return addressList.get(position).getAddressNumericId();
    }

    @Override
    public int onGetSwipeReactionType(ViewAddressRecyclerViewAdapter.MyAddressModuleViewHolder holder, int position, int x, int y) {
        return SwipeableItemConstants.REACTION_CAN_SWIPE_LEFT;
    }

    @Override
    public void onSwipeItemStarted(ViewAddressRecyclerViewAdapter.MyAddressModuleViewHolder holder, int position) {
        notifyDataSetChanged();
    }

    @Override
    public void onSetSwipeBackground(ViewAddressRecyclerViewAdapter.MyAddressModuleViewHolder holder, int position, int type) {
        if (type == SwipeableItemConstants.DRAWABLE_SWIPE_LEFT_BACKGROUND) {
            holder.mDeleteText.setVisibility(View.VISIBLE);
            holder.mContainerView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            holder.itemView.setBackground(mContext.getResources().getDrawable(R.drawable.bg_swipe_item_right, null));
        } else {
            holder.mDeleteText.setVisibility(View.GONE);
            holder.mContainerView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
            holder.itemView.setBackground(mContext.getResources().getDrawable(R.drawable.bg_swipe_item_neutral, null));
        }
    }

    @Override
    public SwipeResultAction onSwipeItem(ViewAddressRecyclerViewAdapter.MyAddressModuleViewHolder holder, int position, int result) {
        if (result == SwipeableItemConstants.RESULT_SWIPED_LEFT) {
            return new SwipeResultActionMoveToSwipedDirection() {
                @Override
                protected void onSlideAnimationEnd() {
                    super.onSlideAnimationEnd();
                    notifyItemRemoved(position);
                    addressList.remove(position);
                    notifyItemChanged(position);
                }

                @Override
                protected void onPerformAction() {
                    super.onPerformAction();
                    mPresenter.deleteUserDeliveryAddress(addressList.get(position));
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

    public class MyAddressModuleViewHolder extends AbstractSwipeableItemViewHolder {

        @BindView(R.id.view_my_address_row_item_name)
        TextView addressNumberTextView;
        @BindView(R.id.view_my_address_row_item_text)
        TextView addressTextView;
        @BindView(R.id.controller_address_container)
        FrameLayout mContainerView;
        @BindView(R.id.controller_address_delete_text)
        TextView mDeleteText;

        public MyAddressModuleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public View getSwipeableContainerView() {
            return mContainerView;
        }
    }
}
