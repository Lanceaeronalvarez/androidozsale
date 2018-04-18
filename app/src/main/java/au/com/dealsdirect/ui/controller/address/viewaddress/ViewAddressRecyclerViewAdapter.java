package au.com.dealsdirect.ui.controller.address.viewaddress;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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


public class ViewAddressRecyclerViewAdapter extends RecyclerView.Adapter<ViewAddressRecyclerViewAdapter.MyAddressModuleViewHolder> {

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

        holder.addressNumber.setText(addressName);
        holder.addressText.setText(String.valueOf(newAddress));

        if (isCalledFromCart) {
            holder.removeAddress.setVisibility(View.GONE);

            holder.itemView.setSelected(mDeliveryAddress != null && mDeliveryAddress.equalsAddressItem(addressList.get(position)));
            holder.addressNumber.setSelected(mDeliveryAddress != null && mDeliveryAddress.equalsAddressItem(addressList.get(position)));
        } else {

            holder.removeAddress.setOnClickListener(view -> {

                Timber.d("remove address", "remove address clicked");
                DeleteUserAddress.RequestValues deleteAddressRequest = new DeleteUserAddress
                        .RequestValues(addressId);

                mView.onDeleteItemClicked(deleteAddressRequest, position);
            });
        }


    }

    @Override
    public int getItemCount() {
        if (addressList == null) {
            return 0;
        }
        return addressList.size();
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

    static class MyAddressModuleViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.view_my_address_row_item_name)
        TextView addressNumber;
        @BindView(R.id.view_my_address_row_item_text)
        TextView addressText;
        @BindView(R.id.remove_address)
        View removeAddress;

        public MyAddressModuleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void removeItemAtPosition(int position) {
        addressList.remove(position);
    }
}
