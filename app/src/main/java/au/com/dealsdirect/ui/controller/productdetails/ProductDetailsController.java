package au.com.dealsdirect.ui.controller.productdetails;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.productdetails.GetPublicItemDetailsRequest;
import au.com.dealsdirect.data.network.model.productdetails.GetPublicItemDetailsResponse;
import au.com.dealsdirect.data.network.model.productdetails.GetPublicSaleDetailsRequest;
import au.com.dealsdirect.data.network.model.productdetails.GetPublicSaleDetailsResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.PriceUtils;
import butterknife.BindView;

/**
 * Created by smartwave on 08/06/2017.
 */

public class ProductDetailsController extends BaseController implements ProductDetailsMvpView {

    private final String KEY_ITEM_ID = "KEY_ITEM_ID";
    private final String KEY_SALE_ID = "KEY_SALE_ID";
    private final String KEY_COUNTRY_ID = "KEY_COUNTRY_ID";
    private final String KEY_LANGUAGE_ID = "KEY_LANGUAGE_ID";
    private final String KEY_USER_GROUP = "KEY_USER_GROUP";
    private final String KEY_GET_BIG_IMAGES = "KEY_GET_BIG_IMAGES";
    private final String KEY_INCLUDE_PRICES = "KEY_INCLUDE_PRICES";

    RecyclerViewPager mRecyclerView;

    @Inject
    ProductDetailsMvpPresenter<ProductDetailsMvpView> mPresenter;

    private String mSaleId;
    private String mItemId;
    private String mCountryId = "DA";
    private String mLanguageId = "EN";
    private String mUserGroup = "";
    private boolean mGetBigImages = true;
    private boolean mIncludePrices = true;


    //product details views

    @BindView(R.id.discountLabel)
    TextView mDiscountLabel;
    @BindView(R.id.productImageRecyclerView)
    RecyclerViewPager mProductImagesRv;
    @BindView(R.id.productName)
    TextView mProductName;
    @BindView(R.id.productCategory)
    TextView mProductCategory;
    @BindView(R.id.productPrice)
    TextView mProductPrice;
    @BindView(R.id.productPreviousPrice)
    TextView mProductPreviousPrice;
    @BindView(R.id.sizes)
    RelativeLayout mSizesContainer;
    @BindView(R.id.sizeList)
    TagFlowLayout mSizesFlowLayout;
    @BindView(R.id.shipping_desc_container)
    LinearLayout mShippingContainer;
    @BindView(R.id.shipping_desc_text)
    WebView mShippingDescText;
    @BindView(R.id.product_description_text)
    WebView mProductDescriptionText;
    @BindView(R.id.product_about_pricing)
    WebView mProductAboutPricing;

    String mHtmlHeader = "";
    String mHtmlFooter = "";

    LinearLayoutManager mProductImagesRvLayoutManager;
    ProductDetailsImageAdapter mProductImagesAdapter;
    TagAdapter<Pair<String,String>> mSizesAdapter;
    ArrayList<Pair<String,String>> mProductSizes = new ArrayList<>();

    boolean hasSizes = false;
    boolean didSelectSize = false;
    String selectedSkuId = "";


    public ProductDetailsController(String itemId, String saleId){
        this(new BundleBuilder(new Bundle())
                .putString("KEY_ITEM_ID",itemId)
                .putString("KEY_SALE_ID",saleId).build()
        );
    }

    public ProductDetailsController(Bundle args) {
        super(args);
        mSaleId = args.getString(KEY_SALE_ID);
        mItemId = args.getString(KEY_ITEM_ID);
    }


    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_product_details, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        //init api call
        GetPublicItemDetailsRequest itemDetailsRequest = new GetPublicItemDetailsRequest(
                mItemId,
                mSaleId,
                mGetBigImages,
                mIncludePrices,
                mLanguageId,
                mCountryId,
                mUserGroup
        );

        GetPublicSaleDetailsRequest saleDetailsRequest = new GetPublicSaleDetailsRequest(
                mSaleId,
                mCountryId,
                mUserGroup,
                mLanguageId
        );

        mPresenter.loadProductDetails(itemDetailsRequest,saleDetailsRequest);

        mProductImagesRvLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mProductImagesRv.setLayoutManager(mProductImagesRvLayoutManager);
        mProductImagesAdapter = new ProductDetailsImageAdapter(null,mSaleId);
        mProductImagesRv.setAdapter(mProductImagesAdapter);

        mHtmlHeader = getActivity().getResources().getString(R.string.base_html_template_header);
        mHtmlFooter = getActivity().getResources().getString(R.string.base_html_template_footer);

    }

    @Override
    protected void setUp(View view) {

    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        super.onDestroyView(view);
    }

    @Override
    public void showProductDetails(GetPublicItemDetailsResponse.Value product) {

        mProductImagesAdapter.replaceData(product);

        //bind UI values here

        //product info
        mProductName.setText(product.getName());
//        mProductCategory.setText(productDetail.getBrandName());
        mProductPrice.setText(PriceUtils.getPriceStringValue(product.getUserPrice()));
        mProductPreviousPrice.setText(PriceUtils.getPriceStringValue(product.getRegularPrice()));

        if (product.getRegularPrice().equals(0d)) {
            mProductPreviousPrice.setVisibility(View.GONE);
        } else {
            mProductPreviousPrice.setText(PriceUtils.getRpStringValue(product.getRegularPrice()));
            mProductPreviousPrice.setPaintFlags(mProductPreviousPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        mProductDescriptionText.loadData(mHtmlHeader + product.getDescription() + mHtmlFooter, "text/html; charset=UTF-8", null);

        mProductDescriptionText.getSettings().setJavaScriptEnabled(true);
        mProductDescriptionText.getSettings().setDomStorageEnabled(true);

        mProductDescriptionText.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
                return true;
            }

        });

        //sizes
        if (product.getSizes().size() != 0){
            mSizesContainer.setVisibility(View.VISIBLE);
            hasSizes = true;
        }


        for (GetPublicItemDetailsResponse.Size size : product.getSizes()) {
            mProductSizes.add(new Pair<>(size.getName(),size.getID()));
        }

        if (!(mProductSizes.get(0) == null)) {
            mSizesAdapter = new TagAdapter<Pair<String,String>>(mProductSizes) {
                @Override
                public View getView(FlowLayout parent, int position, Pair<String,String> data) {
                    TextView tv = (TextView) getActivity().getLayoutInflater().inflate(R.layout.sizes_chips_layout, parent, false);
                    tv.setText(data.first);
                    return tv;
                }
            };
            mSizesFlowLayout.setAdapter(mSizesAdapter);
        }

        mSizesFlowLayout.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
            @Override
            public void onSelected(Set<Integer> selectPosSet) {
                if (selectPosSet.size() != 0) {
                    selectedSkuId = mProductSizes.get(selectPosSet.iterator().next()).second;
                    didSelectSize = true;
                } else {
                    didSelectSize = false;
                }
            }
        });




    }

    @Override
    public void showSaleDetails(GetPublicSaleDetailsResponse.Value saleDetail) {

        String shippingInformation =  saleDetail.getShipping();

        mProductAboutPricing.loadData(mHtmlHeader + saleDetail.getPricing() + mHtmlFooter, "text/html; charset=UTF-8", null);
        mProductAboutPricing.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        if (shippingInformation!=null){
            mShippingDescText.loadData(mHtmlHeader + shippingInformation + mHtmlFooter, "text/html; charset=UTF-8", null);

        }else{
            mShippingContainer.setVisibility(View.GONE);
        }

    }
}
