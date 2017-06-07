package au.com.dealsdirect.ui.controller.shop;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.utils.BundleBuilder;

import butterknife.BindView;

/**
 * dp Created by Admin on 6/6/17.
 */

public class ShopChildController extends BaseController {

    private static final String KEY_TITLE = "ChildController.title";
    private static final String KEY_BG_COLOR = "ChildController.bgColor";
    private static final String KEY_COLOR_IS_RES = "ChildController.colorIsResId";

    @BindView(R.id.controller_shop_title)
    TextView shopChildTitle;

    public ShopChildController(String title, int backgroundColor, boolean colorIsResId) {
        this(new BundleBuilder(new Bundle())
                     .putString(KEY_TITLE, title)
                     .putInt(KEY_BG_COLOR, backgroundColor)
                     .putBoolean(KEY_COLOR_IS_RES, colorIsResId)
                     .build());
    }

    public ShopChildController(Bundle args) {
        super(args);
    }

    @Override protected void setUp(View view) {

    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflater.inflate(R.layout.controller_shop_child, container, false);
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        shopChildTitle.setText(getArgs().getString(KEY_TITLE));

        int bgColor = getArgs().getInt(KEY_BG_COLOR);
        if (getArgs().getBoolean(KEY_COLOR_IS_RES)) {
            bgColor = ContextCompat.getColor(getActivity(), bgColor);
        }
        view.setBackgroundColor(bgColor);



    }
}
