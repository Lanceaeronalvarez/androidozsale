package au.com.dealsdirect.ui.controller.saleitemdetails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.otaliastudios.zoom.ZoomLayout;

import java.util.LinkedList;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.saleitemdetails.SaleItemDetails;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.PriceUtils;
import butterknife.BindView;

public class SaleItemDetailsCartController extends BaseController {

    @BindView(R.id.item_image_view)
    ImageView itemImageView;
    @BindView(R.id.brand_name)
    TextView mProductBrand;
    @BindView(R.id.item_price)
    TextView mProductPrice;
    @BindView(R.id.item_name)
    TextView mProductName;
    @BindView(R.id.item_size)
    TextView mProductSize;
    @BindView(R.id.item_plus)
    ImageView mPlusButton;
    @BindView(R.id.item_quantity)
    TextView mProductQuantity;
    @BindView(R.id.item_minus)
    ImageView mMinusButton;
    @BindView(R.id.item_remove)
    TextView mRemoveButton;
    @BindView(R.id.item_total)
    TextView mCartTotal;

    SaleItemDetails item;
    String size;
    int quantity = 1;
    Double total;

    public SaleItemDetailsCartController(Bundle args) {
        super(args);
    }

    public static SaleItemDetailsCartController newInstance() {
        return new SaleItemDetailsCartController(new BundleBuilder(new Bundle()).build());
    }

    public static SaleItemDetailsCartController newInstance(SaleItemDetails item, String size) {
        SaleItemDetailsCartController controller = SaleItemDetailsCartController.newInstance();

        controller.item = item;

        return controller;
    }

    @Override
    protected void setUp(View view) {

        String imageUrl = "";
        List<String> qualitySaleImages = getQualityImages(item.getImages());
        if (!qualitySaleImages.isEmpty()) {
            imageUrl = qualitySaleImages.get(0);
        } else if (!item.getImages().isEmpty()) {
            imageUrl = item.getImages().get(0);
        }
        ImageUtils.loadImageImmediate(imageUrl, itemImageView, null);

        mProductBrand.setText(item.getBrandName());
        mProductName.setText(item.getName());
        mProductPrice.setText(PriceUtils.getPriceStringFromPriceObject(item.getPrice()));
        mProductSize.setText(size);
        total = item.getPrice().getValue();

        mPlusButton.setOnClickListener(v -> {
            quantity += 1;
            total += total;
            mProductQuantity.setText(String.valueOf(quantity));
            mCartTotal.setText("$" + total);
        });

        mMinusButton.setOnClickListener(v -> {
            if(quantity >= 2){
                quantity -=1;
                total -= total;
                mProductQuantity.setText(String.valueOf(quantity));
                mCartTotal.setText("$" + total);
            }
        });
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.shopping_cart_dialog, container, false);
        getControllerComponent().inject(this);

        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        setUp(view);
    }

    private List<String> getQualityImages(List<String> images) {
        List<String> qualityImages = new LinkedList<>();
        for (int i = 3; i < images.size(); i += 4) {
            qualityImages.add(images.get(i));
        }
        return qualityImages;
    }
}
