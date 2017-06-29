package au.com.dealsdirect.ui.controller.checkout.checkout;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Item;
import au.com.dealsdirect.ui.custom.ProductQuantityLayout;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.LegacyStringImageUtils;
import au.com.dealsdirect.utils.PriceUtils;

/**
 * Created by smartwave on 28/06/2017.
 */

public class CheckoutOrderAdapter extends ArrayAdapter<Item> {

    private Context mContext;
    private ArrayList<Item> mData;
    private CheckoutMvpPresenter<CheckoutMvpView> mPresenter;
    private int resLayout;

    public CheckoutOrderAdapter(Context context, int resLayout, ArrayList<Item> data, CheckoutMvpPresenter<CheckoutMvpView> presenter) {
        super(context, resLayout, data);

        this.mContext = context;
        this.resLayout = resLayout;
        this.mData = data;
        this.mPresenter = presenter;
    }

    private class ViewHolder {
        ImageView image;
        TextView name;
        TextView sizeText;
        TextView sizeValue;
        TextView colorText;
        TextView colorValue;
        TextView price;
        ProductQuantityLayout quantityLayout;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder view;
        Item item = mData.get(position);

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(resLayout, null);
            view = new ViewHolder();

            view.image = (ImageView) convertView.findViewById(R.id.item_checkout_image);
            view.name = (TextView) convertView.findViewById(R.id.item_checkout_name);
            view.sizeText = (TextView) convertView.findViewById(R.id.item_checkout_size_text);
            view.sizeValue = (TextView) convertView.findViewById(R.id.item_checkout_size_value);
            view.price = (TextView) convertView.findViewById(R.id.item_checkout_price);
            view.colorText = (TextView) convertView.findViewById(R.id.item_checkout_color_text);
            view.colorValue = (TextView) convertView.findViewById(R.id.item_checkout_color);
            view.quantityLayout = (ProductQuantityLayout) convertView.findViewById(R.id.item_checkout_quantity);

            convertView.setTag(view);
        } else {
            view = (ViewHolder) convertView.getTag();
        }

        if(item == null) return convertView;

//   (1) fix when item.fileName is null
        if(item.fileName!=null){
            ImageUtils.clearImage(mContext,view.image);
            if (!item.fileName.isEmpty() && view.image.getDrawable() == null){
                ImageUtils.loadImage(mContext,LegacyStringImageUtils.productDetailsImageURLString(item.brandID, item.imageID, item.fileName),view.image);
            }
        }

//   commented since item.fileName can be null when isEmpty is called. Replaced with (1)
//        if(item.fileName!=null && !item.fileName.isEmpty()) {
//            Glide.with(mContext).load(GImageUrlUtil.generateImageUrl(item.brandID, item.imageID, item.fileName)).into(view.image);
//        }

        view.name.setText(item.item);
        if (item.size == null || item.size.length() < 0) {
            view.sizeText.setVisibility(View.INVISIBLE);
            view.sizeValue.setVisibility(View.INVISIBLE);
        } else {
            view.sizeValue.setText(item.size);
        }
        view.colorText.setVisibility(View.INVISIBLE);

        view.price.setText(PriceUtils.getPriceStringValue(item.price));
        view.quantityLayout.setQuantity(item.qty);
        view.quantityLayout.setAutoUpdateQuantity(false);
        view.quantityLayout.setEditTextToNonEditable();

        view.quantityLayout.setOnQuantityChangeListener(new ProductQuantityLayout.onQuantityChangeListener() {
            @Override
            public void onQuantityIncrease(ProductQuantityLayout view, int value) {
                mPresenter.fetchAdjustItemQuantity("IncreaseOrderItem", item.id);
            }

            @Override
            public void onQuantityDecrease(ProductQuantityLayout view, int value) {
                mPresenter.fetchAdjustItemQuantity("DecreaseOrderItem", item.id);
            }
        });

        return convertView;


    }
}
