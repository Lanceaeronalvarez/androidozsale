package au.com.dealsdirect.ui.controller.searchfilter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import butterknife.BindView;

/**
 * Created by smartwave on 20/07/2017.
 */

public class SearchFilterController extends BaseController implements SearchFilterMvpView{

    @Inject
    SearchFilterMvpPresenter<SearchFilterMvpView> mPresenter;


    @BindView(R.id.filters_facets_recyclerview)
    RecyclerView mFacetsRecyclerView;
    @BindView(R.id.filters_facet_items_recyclerview)
    RecyclerView mFacetItemsRecyclerView;

    @BindView(R.id.partial_toolbar_search_field)
    EditText mSearchEditText;
    @BindView(R.id.partial_toolbar_search_right_option)
    ImageButton mSearchApplyButton;




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
        mSearchEditText.setHint("search filters");
        mSearchApplyButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
    }


    @Override
    public void showFacetItem() {

    }

    @Override
    public void includeFacetItemToFilters() {

    }
}
