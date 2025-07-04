package au.com.dealsdirect.ui.controller.saleitemdetails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;


import com.otaliastudios.zoom.ZoomLayout;

import au.com.dealsdirect.R;

import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.ImageUtils;
import butterknife.BindView;

public class SaleItemDetailsImageZoomController extends BaseController {

    @BindView(R.id.product_details_shared_image)
    ImageView mProductSharedImage;

    @BindView(R.id.product_details_zoom_image)
    ZoomLayout mProductZoomImage;

    @BindView(R.id.close_button)
    ImageView mCloseButton;

    private String imgUrl;
    private boolean zoom = false;

    public SaleItemDetailsImageZoomController(Bundle args) {
        super(args);
    }

    public static SaleItemDetailsImageZoomController newInstance() {
        return new SaleItemDetailsImageZoomController(new BundleBuilder(new Bundle()).build());
    }

    public static SaleItemDetailsImageZoomController newInstance(String imageUrl) {
        SaleItemDetailsImageZoomController controller = SaleItemDetailsImageZoomController.newInstance();

        controller.imgUrl = imageUrl;

        return controller;
    }

    @Override
    protected void setUp(View view) {
        ImageUtils.loadImageImmediate(imgUrl, mProductSharedImage, null);

        mCloseButton.setOnClickListener(v -> {
            getRouter().popCurrentController();
        });

        mProductSharedImage.setOnClickListener(v -> {
            if (zoom) {
                mProductZoomImage.zoomBy(0.3f, true);
                zoom = false;
            } else {
                mProductZoomImage.zoomBy(3, true);
                zoom = true;
            }

        });
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_saleitem_image_zoom, container, false);
        getControllerComponent().inject(this);

        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        setUp(view);
    }
}
