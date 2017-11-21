package au.com.dealsdirect.ui.controller.searchfilter.facetfilter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.google.gson.reflect.TypeToken;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsMvpPresenter;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsMvpView;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;
import au.com.dealsdirect.ui.custom.CustomRangeSeekbar;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.JsonUtils;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import butterknife.Optional;


/**
 * Created by smartwave on 20/11/2017.
 */

public class FacetFilterController extends BaseController {

    public static final String BRANDS_FILTER_FRAGMENT = "brands";
    public static final String COLORS_FILTER_FRAGMENT = "colors";
    public static final String SIZES_FILTER_FRAGMENT = "sizes";

    @Inject
    SaleItemsMvpPresenter<SaleItemsMvpView> mSaleItemsPresenter;

    @BindView(R.id.facet_default_layout)
    RelativeLayout mDefaultLayout;
    @BindView(R.id.facet_price_layout)
    RelativeLayout mPriceLayout;
    @BindView(R.id.price_clear_text)
    TextView mPriceClearText;

    @BindView(R.id.price_facet_range_seekbar)
    CustomRangeSeekbar mSeekbar;
    @BindView(R.id.movingMaxPriceLayout)
    LinearLayout mMaxPriceMovingLayout;
    @BindView(R.id.movingMinPriceLayout)
    LinearLayout mMinPriceMovingLayout;
    @BindView(R.id.movingMaxPrice)
    TextView mMaxPrice;
    @BindView(R.id.movingMinPrice)
    TextView mMinPrice;

    @BindView(R.id.flow_layout)
    TagFlowLayout mFlowLayout;
    @BindView(R.id.facet_type)
    TextView mFacetIndicatorText;
    @BindView(R.id.clear_text)
    TextView mClearText;

    public TagFlowLayout getFlowLayout() {
        return mFlowLayout;
    }
    public TagAdapter getFlowLayoutAdapter() {
        return mFlowLayoutAdapter;
    }

    private TagAdapter mFlowLayoutAdapter;

    private ArrayList<String> mFacetList = new ArrayList<>();
    private Set<Integer> selectedItemsIndex = new HashSet<Integer>();
    private Set<Integer> origSelectedSet = new HashSet<Integer>();
    private String mFacetFilterType = "";

    private int origMinValue = -1;
    private int origMaxValue = -1;
    private boolean isSeekbarReset = false;

    public static FacetFilterController newInstance() {
        return new FacetFilterController(new BundleBuilder(new Bundle())
                .build());
    }

    public FacetFilterController(Bundle args) {
        super(args);
        mFacetList = args.getStringArrayList(BundleKeys.FACET_PAYLOAD);
        mFacetFilterType = args.getString(BundleKeys.FACET_FILTER_TYPE,"");
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_facet_filter,container,false);
        getControllerComponent().inject(this);
        mSaleItemsPresenter.onAttach((SaleItemsMvpView) GateKeeper.getCurrentControllerOnRouter(mActivity.getSaleItemsRouter()));
        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        mFacetIndicatorText.setText(mFacetFilterType);

        if(!mFacetFilterType.equals(BundleKeys.PRICE_FACET_FILTER_TYPE)){
            setupDefaultLayout();
        }else{
            setupPriceLayout();
        }
    }

    public void replaceData(ArrayList<String> items){
        mFacetList = new ArrayList<>(items);
        if(mFlowLayoutAdapter!=null) {
            mFlowLayoutAdapter.notifyDataChanged();
            Set<Integer> nullSet = null;
            mFlowLayoutAdapter.setSelectedList(nullSet);
        }
    }

    private void setupDefaultLayout(){
        mClearText.setOnClickListener(action->{
//            mFilterIndicatorTextView.setText(mFacetFilterType);
//            mPresenter.setActiveDefaultTabIcons(mFacetFilterType);
            Set<Integer> set = null;
            mFlowLayoutAdapter.setSelectedList(set);
            List<Integer> tempSet = new ArrayList<Integer>(origSelectedSet);
            for (Integer i:tempSet) {
                Pair<String,String> newPair = new Pair(mFacetFilterType,mFacetList.get(i));
//                mSearchTagsAdapter.remove(newPair);
            }
            origSelectedSet.clear();

//            if(mSearchTagsAdapter.getData().size() != 0) {
//                mPresenter.updateShopFilters();
//            }
        });

        mFlowLayoutAdapter = new TagAdapter<String>(mFacetList)
        {
            @Override
            public View getView(FlowLayout parent, int position, String s)
            {
                TextView tv = (TextView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.facet_filter_chips,
                        mFlowLayout, false);
                tv.setText(s);
                return tv;
            }

        };

        mFlowLayout.setAdapter(mFlowLayoutAdapter);

        mFlowLayout.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
            @Override
            public void onSelected(Set<Integer> selectPosSet) {
//                getActivity().setTitle("choose:" + selectPosSet.toString());

                Set<Integer> oldSet = origSelectedSet;
                Set<Integer> newSet = new HashSet<Integer>(selectPosSet);
                origSelectedSet =  new HashSet<Integer>(selectPosSet);
                newSet.removeAll(oldSet);
                oldSet.removeAll(origSelectedSet);

                if(!newSet.isEmpty()){
                    List<Integer> temp = new ArrayList(newSet);
                    Pair<String,String> newPair = new Pair(mFacetFilterType, mFacetList.get(temp.get(0)));
//                    mSearchTagsAdapter.add(newPair);
                    selectedItemsIndex.add(temp.get(0));
                }else if(!oldSet.isEmpty()){
                    List<Integer> temp = new ArrayList(oldSet);
                    Pair<String,String> newPair = new Pair(mFacetFilterType, mFacetList.get(temp.get(0)));
//                    mSearchTagsAdapter.remove(newPair);
                    selectedItemsIndex.remove(temp.get(0));
                }

                if(selectPosSet.size()!=0) {
                    mFacetIndicatorText.setText(mFacetFilterType + "(" + selectPosSet.size() + ")");
//                    mPresenter.setActiveTabIndicatorIcons(mFacetFilterType);
                }else{
                    mFacetIndicatorText.setText(mFacetFilterType);
//                    mPresenter.setActiveDefaultTabIcons(mFacetFilterType);
                }

//                mPresenter.updateShopFilters();
            }
        });
    }

    private void setupPriceLayout(){
//        origMaxValue = mPresenter.getSearchMaxPrice();
        origMinValue = mSeekbar.getSelectedMinValue().intValue();

        if(origMaxValue == origMinValue){
            origMaxValue = 200;
        }

        mSeekbar.setMaxValue(origMaxValue);

        mPriceClearText.setOnClickListener((v) -> {
            if (!isSeekbarReset) {
                //remove previously selected price range
//                for (SearchChipModel chip : mSearchTagsAdapter.getData()) {
//                    if (chip.getFilterType().equals(PRICE_FACETFILTER_NAME)) {
//                        mSearchTagsAdapter.remove(chip);
//                        break;
//                    }
//                }

//                onResetPriceRange();
            }
        });
        mSeekbar.setMinPriceMovingLayout(mMinPriceMovingLayout);
        mSeekbar.setMaxPriceMovingLayout(mMaxPriceMovingLayout);
        mSeekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                mMinPrice.setText("$" + minValue.intValue());
                mMaxPrice.setText("$" + maxValue.intValue());
                if (maxValue.intValue() == origMaxValue) {
                    mMaxPrice.setText("$" + maxValue.intValue() + "+");
                }

            }
        });
        mSeekbar.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue) {

                //remove previously selected price range
//                for (SearchChipModel chip : mSearchTagsAdapter.getData()) {
//                    if (chip.getFilterType().equals(BundleKeys.PRICE_FACETFILTER_NAME)) {
//                        mSearchTagsAdapter.remove(chip);
//                        break;
//                    }
//                }
//
//                //add newly selected price range
//                if (origMinValue != minValue.intValue() || origMaxValue != maxValue.intValue()) {
//                    SearchChipModel priceChip = new SearchChipModel(BundleKeys.PRICE_FACETFILTER_NAME, minValue.intValue() + " to " + maxValue.intValue(), -1);
//                    priceChip.setMaxValue(maxValue.intValue());
//                    priceChip.setMinValue(minValue.intValue());
//                    mSearchTagsAdapter.add(priceChip);
//                }

                isSeekbarReset = false;
            }
        });

//        SearchChipModel priceChip = findPriceChip();
//
//        if (priceChip != null) {
//            mSeekbar.setMinStartValue(priceChip.getMinValue()).apply();
//            mSeekbar.setMaxStartValue(priceChip.getMaxValue()).apply();
//        }
    }

    public void onResetPriceRange() {
        mSeekbar.setMinStartValue(origMinValue);
        mSeekbar.setMaxStartValue(origMaxValue);

        mSeekbar.apply();
        mMinPriceMovingLayout.setTranslationX(0);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mSeekbar.getLayoutParams();
        mMaxPriceMovingLayout.setX(mSeekbar.getWidth() - (lp.rightMargin));

        isSeekbarReset = true;
        mSeekbar.resetMovingLayoutVisibility();
    }
}
