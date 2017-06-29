package com.mysale.genie.views.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.jakewharton.rxbinding.view.RxView;
import com.mysale.genie.views.R;


/**
 * Created by smartwave on 01/12/2016.
 */
public class ProductQuantityLayout extends LinearLayout {

    private onQuantityChangeListener listener;

    private Button minus;
    private Button plus;
    private EditText quantity;
    private static int max;
    private boolean auto_update = true;


    public ProductQuantityLayout(Context context) {
        super(context);
        init(context);
    }

    public ProductQuantityLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ProductQuantityLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        ProductQuantityLayout.max = max;
    }

    private void init(Context context){
        inflate(context, R.layout.product_quantity_layout,this);

        minus = (Button) findViewById(R.id.minus);
        plus = (Button) findViewById(R.id.plus);
        quantity = (EditText) findViewById(R.id.quantity_text);

//
//        RxTextView.afterTextChangeEvents(quantity).subscribe(action->{
//            if(action.editable().toString().trim().length() > 0 && Integer.parseInt(action.editable().toString()) == 0){
//                quantity.setText("1");
//            }
//        });

        RxView.clicks(minus).subscribe(action->{

            String val = quantity.getText().toString();
            int quantityValue = Integer.parseInt(val);
            if(auto_update && quantityValue>0) {
                quantityValue--;
                quantity.setText(String.valueOf(quantityValue));
            }

            listener.onQuantityDecrease(this, quantityValue);
        });

        RxView.clicks(plus).subscribe(action->{
            String val = quantity.getText().toString();
            int quantityValue = Integer.parseInt(val);
            if(auto_update && quantityValue<max) {
                quantityValue++;
                quantity.setText(String.valueOf(quantityValue));
            }

                listener.onQuantityIncrease(this, quantityValue);
        });
    }

    public void setAutoUpdateQuantity(boolean enabled){
        this.auto_update = enabled;
    }

    public void setOnQuantityChangeListener(onQuantityChangeListener listener) {
        this.listener = listener;
    }

    public void setQuantity(int count) {
        quantity.setText(String.format("%d", count));
    };

    public String getQuantity(){
        return quantity.getText().toString();
    }

    public void setQuantity(EditText quantity) {
        this.quantity = quantity;
    }

    public void setEditTextToNonEditable(){
        this.quantity.setEnabled(false);
    }

    public interface onQuantityChangeListener<T> {
        void onQuantityIncrease(ProductQuantityLayout view, int value);
        void onQuantityDecrease(ProductQuantityLayout view, int value);
    }
}
