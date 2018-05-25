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
import timber.log.Timber;

/**
 * Created by smartwave on 21/06/2017.
 */


public class ViewAddressRecyclerViewAdapter extends RecyclerView.Adapter<ViewAddressRecyclerViewAdapter.MyAddressModuleViewHolder>
    implements SwipeableItemAdapter<ViewAddressRecyclerViewAdapter.MyAddressModuleViewHolder>{

    public List<AddressesItem> addressList = Collections.emptyList();
    Context context;
    Boolean isCalledFromCart;
    ViewAddressMvpView mView;
    DeliveryAddress mDeliveryAddress;
    ViewAddressMvpPresenter mPresenter;

    public ViewAddressRecyclerViewAdapter(
            Boolean calledFromCart,
            ViewAddressMvpView view,
            List<AddressesItem> addressList,
            Context context,
            DeliveryAddress deliveryAddress,
            ViewAddressMvpPresenter presenter) {

        this.isCalledFromCart = calledFromCart;
        this.mView = view;
        this.addressList = addressList;
        this.context = context;
        this.mDeliveryAddress = deliveryAddress;
        this.mPresenter = presenter;
        setHasStableIds(true);
    }

    public void updateDeliveryAddress(AddressesItem addressesItem){
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

        String newAddress = addressList.get(position).getFullAddress();
        String addressName = addressList.get(position).getAddressName();
        String addressId = addressList.get(position).getAddressId();

        holder.addressNumberTextView.setText(addressName);
        holder.addressTextView.setText(String.valueOf(newAddress));

        if (isCalledFromCart) {

            holder.itemView.setSelected(mDeliveryAddress != null && mDeliveryAddress.equalsAddressItem(addressList.get(position)));
            holder.addressNumberTextView.setSelected(mDeliveryAddress != null && mDeliveryAddress.equalsAddressItem(addressList.get(position)));
        }


    }

    @Override
    public int getItemCount() {
       return addressList == null ?  0 :  addressList.size();

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void insert(int position, AddressesItem data) {
        addressList.add(position, data);
        notifyItemInserted(position);
    }

    public void remove(Address data) {
        int position = addressList.indexOf(data);
        addressList.remove(position);
        notifyItemRemoved(position);
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
            holder.mContainerView.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.itemView.setBackground(context.getResources().getDrawable(R.drawable.bg_swipe_item_right, null));
        } else {
            holder.mDeleteText.setVisibility(View.GONE);
            holder.mContainerView.setBackgroundColor(context.getResources().getColor(R.color.transparent));
            holder.itemView.setBackground(context.getResources().getDrawable(R.drawable.bg_swipe_item_neutral, null));
        }
    }

    @Override
    public SwipeResultAction onSwipeItem(ViewAddressRecyclerViewAdapter.MyAddressModuleViewHolder holder, int position, int result) {
        if (result == SwipeableItemConstants.RESULT_SWIPED_LEFT) {
            return new SwipeResultActionMoveToSwipedDirection() {
                @Override
                protected void onSlideAnimationEnd() {
                    super.onSlideAnimationEnd();
                    holder.mDeleteText.setVisibility(View.GONE);
                    holder.mContainerView.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                    holder.itemView.setBackground(context.getResources().getDrawable(R.drawable.bg_swipe_item_neutral, null));
                }

                @Override
                protected void onPerformAction() {
                    super.onPerformAction();
                    mPresenter.deleteUserDeliveryAddress(addressList.get(position).getAddressId());
                    notifyItemRemoved(position);
                    notifyDataSetChanged();
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

    public void removeItemAtPosition(int position) {
        addressList.remove(position);
    }
}
