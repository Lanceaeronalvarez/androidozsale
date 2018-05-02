package au.com.dealsdirect.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import au.com.dealsdirect.R;


/**
 * Created by smartwave on 01/12/2016.
 */
public class ProductQuantityLayout extends LinearLayout {

    private onQuantityChangeListener listener;

    private Button minus;
    private Button plus;
    private ProgressBar mMinusLoader;
    private ProgressBar mPlusLoader;
    private EditText quantity;
    private static int max = 0;
    private static int min = 0;
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

    public static int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        ProductQuantityLayout.max = max;
    }

    public void setMin(int min) {
        ProductQuantityLayout.min = min;
    }

    private void init(Context context) {
        inflate(context, R.layout.quantity_widget, this);

        minus = (Button) findViewById(R.id.quantity_widget_minus);
        plus = (Button) findViewById(R.id.quantity_widget_plus);
        quantity = (EditText) findViewById(R.id.quantity_widget_text);
        mMinusLoader = (ProgressBar) findViewById(R.id.quantity_widget_minus_loader);
        mPlusLoader = (ProgressBar) findViewById(R.id.quantity_widget_plus_loader);

//
//        RxTextView.afterTextChangeEvents(quantity).subscribe(action->{
//            if(action.editable().toString().trim().length() > 0 && Integer.parseInt(action.editable().toString()) == 0){
//                quantity.setText("1");
//            }
//        });

        minus.setOnClickListener(action -> {

            String val = quantity.getText().toString();
            int quantityValue = Integer.parseInt(val);
            if (auto_update && quantityValue > min) {

                quantityValue--;
                quantity.setText(String.valueOf(quantityValue));
            }

            plus.setClickable(false);
            minus.setVisibility(View.GONE);
            mMinusLoader.setVisibility(View.VISIBLE);
            listener.onQuantityDecrease(this, quantityValue);
        });

        plus.setOnClickListener(action -> {
            String val = quantity.getText().toString();
            int quantityValue = Integer.parseInt(val);
            if (auto_update && quantityValue < max) {
                quantityValue++;
                quantity.setText(String.valueOf(quantityValue));
            }

            if (quantityValue < max) {
                minus.setClickable(false);
                plus.setVisibility(View.GONE);
                mPlusLoader.setVisibility(View.VISIBLE);
            }
            listener.onQuantityIncrease(this, quantityValue);
        });
    }

    public void resetLoaders() {
        minus.setClickable(true);
        minus.setVisibility(View.VISIBLE);
        mMinusLoader.setVisibility(View.GONE);
        plus.setClickable(true);
        plus.setVisibility(View.VISIBLE);
        mPlusLoader.setVisibility(View.GONE);
    }

    public void setAutoUpdateQuantity(boolean enabled) {
        this.auto_update = enabled;
    }

    public void setOnQuantityChangeListener(onQuantityChangeListener listener) {
        this.listener = listener;
    }

    public void setQuantity(int count) {
        resetLoaders();
        quantity.setText(String.format("%d", count));
    }

    public String getQuantity() {
        return quantity.getText().toString();
    }

    public void setQuantity(EditText quantity) {
        this.quantity = quantity;
    }

    public void setEditTextToNonEditable() {
        this.quantity.setEnabled(false);
    }

    public interface onQuantityChangeListener<T> {
        void onQuantityIncrease(ProductQuantityLayout view, int value);

        void onQuantityDecrease(ProductQuantityLayout view, int value);
    }
}
