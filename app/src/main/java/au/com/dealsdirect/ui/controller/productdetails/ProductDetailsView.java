package au.com.dealsdirect.ui.controller.productdetails;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;
import com.zhy.view.flowlayout.TagFlowLayout;

import au.com.dealsdirect.R;
import au.com.dealsdirect.widget.ElasticDragDismissFrameLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * dp Created by Admin on 6/16/17.
 */

public class ProductDetailsView extends ElasticDragDismissFrameLayout {

    @BindView(R.id.discountLabel)
    public TextView mDiscountLabel;
    @BindView(R.id.productImageRecyclerView)
    public RecyclerViewPager mProductImagesRv;
    @BindView(R.id.otherImagesRecyclerView)
    public RecyclerView mOtherImagesRv;
    @BindView(R.id.productName)
    public TextView mProductName;
    @BindView(R.id.productPrice)
    public TextView mProductPrice;
    @BindView(R.id.productPreviousPrice)
    public TextView mProductPreviousPrice;
    @BindView(R.id.sizes)
    public RelativeLayout mSizesContainer;
    @BindView(R.id.sizeList)
    public TagFlowLayout mSizesFlowLayout;
    @BindView(R.id.shipping_desc_container)
    public LinearLayout mShippingContainer;
    @BindView(R.id.shipping_desc_text)
    public WebView mShippingDescText;
    @BindView(R.id.product_description_text)
    public WebView mProductDescriptionText;
    @BindView(R.id.product_about_pricing)
    public WebView mProductAboutPricing;
    @BindView(R.id.product_details_shared_image)
    public ImageView mProductSharedImage;


    public ProductDetailsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);

    }

}
