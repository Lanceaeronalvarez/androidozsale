package au.com.dealsdirect.ui.controller.searchfilter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.FacetItemsAdapter;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.FacetsAdapter;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchTagsAdapter;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.JsonUtils;
import butterknife.BindView;

/**
 * Created by smartwave on 20/07/2017.
 */

public class SearchFilterController extends BaseController implements SearchFilterMvpView{

    public static final String BRANDS_FACETFILTER_NAME = "skus.brandName";
    public static final String SIZES_FACETFILTER_NAME = "skus.attributes.size";
    public static final String COLORS_FACETFILTER_NAME = "color";
    public static final String PRICE_FACETFILTER_NAME = "skus.attributesForFaceting.aud";
    public static final String SEARCH_QUERY_NAME = "search_query";
    private static final String KEY_FACET_STRING = "KEY_FACET_STRING";

    @Inject
    SearchFilterMvpPresenter<SearchFilterMvpView> mPresenter;

    @BindView(R.id.filters_facets_recyclerview)
    RecyclerView mFacetsRecyclerView;
    @BindView(R.id.filters_facet_items_recyclerview)
    RecyclerView mFacetItemsRecyclerView;
    @BindView(R.id.partial_filters_search_tags_recyclerview)
    RecyclerView mSearchTagsRecyclerView;

    @BindView(R.id.partial_toolbar_search_right_option)
    ImageButton mSearchApplyButton;

    List<GetSaleItemsResponse.Facets> mFacets;

    FacetsAdapter mFacetsAdapter;
    FacetItemsAdapter mFacetItemsAdapter;
    SearchTagsAdapter mSearchTagsAdapter;
    LinearLayoutManager mSearchTagsLayoutManager;

    ArrayList<String> mBrandList = new ArrayList<>();
    ArrayList<String> mSizeList = new ArrayList<>();
    ArrayList<String> mColorList = new ArrayList<>();

    private Set<Integer> origSelectedSet = new HashSet<Integer>();
    private Set<Integer> selectedItemsIndex = new HashSet<Integer>();

    private int mPreviousSelectedFacetIndex = -1;

    private HashMap<Integer,Set<Integer>> mPreviousSelectedFacetIndices = new HashMap<>();

    List<String> mFacetFilters = Arrays.asList
            ("Sort",
            "Category",
            "Brand",
            "Size",
            "Color",
            "Price");

    public static SearchFilterController newInstance(String jsonFacetString) {
        return new SearchFilterController(new BundleBuilder(new Bundle())
                .putString(KEY_FACET_STRING,jsonFacetString)
                .build());
    }

    public SearchFilterController(Bundle args) {
        super(args);
        mFacets = JsonUtils.convertStringToObject(args.getString(KEY_FACET_STRING,""), new TypeToken<ArrayList<GetSaleItemsResponse.Facets>>(){}.getType());
    }


    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_search_filter,container,false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        mSearchApplyButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));

        mFacetsAdapter = new FacetsAdapter(getActivity(),mFacetFilters,mPresenter);
        mFacetsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        mFacetsRecyclerView.setAdapter(mFacetsAdapter);

        mFacetItemsAdapter = new FacetItemsAdapter(new ArrayList<>(),mPresenter,new HashSet<Integer>());
        mFacetItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        mFacetItemsRecyclerView.setAdapter(mFacetItemsAdapter);
        mFacetItemsAdapter.setOnSelectListener(new FacetItemsAdapter.OnSelectListener() {
            @Override
            public void onSelected(Set<Integer> selectPosSet) {
                mPresenter.onFacetItemClicked(selectPosSet);
            }
        });


        if(mFacets != null) {
            parseFacets(mFacets);
        }


        mSearchTagsLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mSearchTagsRecyclerView.setLayoutManager(mSearchTagsLayoutManager);
        mSearchTagsAdapter = new SearchTagsAdapter(getActivity(), mSearchTagsLayoutManager, new ArrayList<Pair<String, String>>(),mPresenter);

        mSearchTagsRecyclerView.setAdapter(mSearchTagsAdapter);
        mSearchTagsRecyclerView.setVisibility(View.VISIBLE);

    }

    @Override
    public void onDetach(View view) {
        mPresenter.onDetach();
        super.onDetach(view);
    }

    @Override
    public void showFacetItem(int position) {

        if(mPreviousSelectedFacetIndex != -1){
            mPreviousSelectedFacetIndices.put(mPreviousSelectedFacetIndex,new HashSet<>(mFacetItemsAdapter.getSelectedFacets()));
        }

        if(position != mPreviousSelectedFacetIndex){
            mFacetItemsAdapter.clearSelectedFacets();
        }

        if(mPreviousSelectedFacetIndices.get(position) != null){
            mFacetItemsAdapter.updateSelectedFacets(mPreviousSelectedFacetIndices.get(position));
        }

        mFacetItemsAdapter.setFilterType(mFacetFilters.get(position));
        mFacetItemsAdapter.replaceData(mapFacetItemClicked(position));

        mPreviousSelectedFacetIndex = position;
    }

    @Override
    public void updateFacetItemToFilters(Set<Integer> selectPosSet) {
        Log.d("test", selectPosSet.toString());

        Set<Integer> oldSet = origSelectedSet;
        Set<Integer> newSet = new HashSet<Integer>(selectPosSet);
        origSelectedSet =  new HashSet<Integer>(selectPosSet);
        newSet.removeAll(oldSet);
        oldSet.removeAll(origSelectedSet);

        if(!newSet.isEmpty()){
            List<Integer> temp = new ArrayList(newSet);
            Pair<String,String> newPair = new Pair(mFacetItemsAdapter.getFilterType(), mFacetItemsAdapter.getData().get(temp.get(0)));
            mSearchTagsAdapter.add(newPair);
            selectedItemsIndex.add(temp.get(0));
        }else if(!oldSet.isEmpty()){
            List<Integer> temp = new ArrayList(oldSet);
            Pair<String,String> newPair = new Pair(mFacetItemsAdapter.getFilterType(), mFacetItemsAdapter.getData().get(temp.get(0)));
            mSearchTagsAdapter.remove(newPair);
            selectedItemsIndex.remove(temp.get(0));
        }

        //                if(selectPosSet.size()!=0) {
//                    mFilterIndicatorTextView.setText(mFilterFragmentType + "(" + selectPosSet.size() + ")");
//                    mPresenter.setActiveTabIndicatorIcons(mFilterFragmentType);
//                }else{
//                    mFilterIndicatorTextView.setText(mFilterFragmentType);
//                    mPresenter.setActiveDefaultTabIcons(mFilterFragmentType);
//                }
    }

    private List<String> mapFacetItemClicked(int position){
        switch (position){
            case 0:
                return new ArrayList<>();
            case 1:
                return new ArrayList<>();
            case 2:
                return mBrandList;
            case 3:
                return mSizeList;
            case 4:
                return mColorList;
            case 5:
                return new ArrayList<>();
            default:
                return new ArrayList<>();
        }
    }

    private void parseFacets(List<GetSaleItemsResponse.Facets> facets){
        if (facets.size() != 0) {
            mBrandList = new ArrayList<>();
            mSizeList = new ArrayList<>();
            mColorList = new ArrayList<>();
            for (int i = 0; i < facets.size(); i++) {
                switch (facets.get(i).getFacetName()) {
                    case BRANDS_FACETFILTER_NAME:
                        for (int j = 0; j < facets.get(i).getFacetValues().size(); j++) {
                            GetSaleItemsResponse.Values facetValue = facets.get(i).getFacetValues().get(j);
                            mBrandList.add(facetValue.getValue());
                        }
                        break;
                    case SIZES_FACETFILTER_NAME:
                        for (int j = 0; j < facets.get(i).getFacetValues().size(); j++) {
                            GetSaleItemsResponse.Values facetValue = facets.get(i).getFacetValues().get(j);
                            mSizeList.add(facetValue.getValue());
                        }
                        break;
                    case COLORS_FACETFILTER_NAME:
                        for (int j = 0; j < facets.get(i).getFacetValues().size(); j++) {
                            GetSaleItemsResponse.Values facetValue = facets.get(i).getFacetValues().get(j);
                            mColorList.add(facetValue.getValue());
                        }
                        break;
                    case PRICE_FACETFILTER_NAME:
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
