package au.com.dealsdirect.ui.controller.returns.currentreturns;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;
import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.returns.currentreturn.CurrentReturnResponseBody;
import au.com.dealsdirect.data.network.model.returns.currentreturn.CurrentReturns;
import au.com.dealsdirect.data.network.model.returns.returndetails.GetReturnDetailsResponseBody;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.returns.currentreturns.adapter.CurrentReturnAdapter;
import au.com.dealsdirect.ui.controller.returns.currentreturns.listener.CurrentReturnClickListener;
import au.com.dealsdirect.ui.controller.returns.currentreturns.viewholder.CurrentReturnViewHolder;
import au.com.dealsdirect.ui.controller.returns.returndetails.ReturnDetailsController;
import au.com.dealsdirect.ui.controller.returns.returnorders.ReturnOrdersController;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;
import butterknife.OnClick;

/*
 * Created by dp on 05/06/2017.
 */

public class CurrentReturnsController extends BaseController
        implements CurrentReturnsMvpView, CurrentReturnClickListener{

    public static final String TAG = "CurrentReturnsController";
    private static final String KEY_TEXT = "CurrentReturnsController.KEY_TEXT";

    private CurrentReturnClickListener mCurrentReturnsListener;
    private HashMap<Integer,GetReturnDetailsResponseBody> returnItemsMap = new HashMap<>();
    private List<GetReturnDetailsResponseBody> returnDetailsResponseBodyList = new ArrayList<>();
    private List<CurrentReturns> mCurrentReturns;
    private int itemIterator = 0;
    private CurrentReturnAdapter mCurrentReturnsAdapter;

    @BindView(R.id.partial_toolbar_arrow_title)
    TextView mCurrentReturnsToolarTitle;

    @BindView(R.id.partial_toolbar_filter_view)
    ImageView mCurrentReturnsRightOption;

    @BindView(R.id.controller_current_returns_recycler_viewpager)
    RecyclerViewPager mCurrentReturnsRecyclerView;

    @BindView(R.id.no_returns_placeholder)
    LinearLayout mPlaceholderLayout;


    @Inject
    CurrentReturnsMvpPresenter<CurrentReturnsMvpView> mPresenter;

    public static CurrentReturnsController newInstance() {

        return new CurrentReturnsController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public CurrentReturnsController(Bundle args) {
        super(args);
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_current_returns, container, false);

        getControllerComponent().inject(this);

        mPresenter.onAttach(this);

        return view;
    }

    @Override
    public void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
        mCurrentReturnsListener = this;
        mCurrentReturnsToolarTitle.setText("My Returns");
        if(mPresenter.isTablet()){
            mCurrentReturnsRightOption.setPadding(5, 5, 5, 5);
        } else {
            mCurrentReturnsRightOption.setPadding(20, 20, 20, 20);
        }
        mCurrentReturnsRightOption.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));

        if (mCurrentReturns == null || mCurrentReturns.size() == 0){
            mPresenter.loadCurrentReturns();
            mPlaceholderLayout.setVisibility(View.VISIBLE);
            mCurrentReturnsRecyclerView.setVisibility(View.GONE);

        }  else {

            mPlaceholderLayout.setVisibility(View.GONE);
            mCurrentReturnsRecyclerView.setVisibility(View.VISIBLE);
            mCurrentReturnsAdapter = new CurrentReturnAdapter(
                    mCurrentReturns,
                    returnDetailsResponseBodyList,
                    getActivity(),
                    mCurrentReturnsListener);

            mCurrentReturnsRecyclerView.setAdapter(mCurrentReturnsAdapter);
//            getCurrentReturnItems(mCurrentReturns);


        }
    }

    @Override
    protected void setUp(View view) {
        // Setup views here

        mCurrentReturnsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager
                .HORIZONTAL,false));

    }

    @Override
    public void onDestroyView(View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }


    @Override
    public void showCurrentReturns(CurrentReturnResponseBody currentReturnResponseBody) {
        List<CurrentReturns> currentReturns =
                currentReturnResponseBody.getCurrentReturnResponse().getCurrentReturns();

        if(currentReturns!=null && currentReturns.size() != 0) {
            mPlaceholderLayout.setVisibility(View.GONE);
            mCurrentReturnsRecyclerView.setVisibility(View.VISIBLE);

            mCurrentReturns = currentReturns;

            mCurrentReturnsAdapter = new CurrentReturnAdapter(
                    currentReturns,
                    returnDetailsResponseBodyList,
                    getActivity(),
                    mCurrentReturnsListener);

            mCurrentReturnsRecyclerView.setAdapter(mCurrentReturnsAdapter);
            getCurrentReturnItems(mCurrentReturns);


        }else{

            mPlaceholderLayout.setVisibility(View.VISIBLE);
            mCurrentReturnsRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void showCurrentReturnDetails(GetReturnDetailsResponseBody getReturnDetailsResponseBody) {

        returnItemsMap.put(itemIterator,getReturnDetailsResponseBody);
            if (returnItemsMap.size() == mCurrentReturns.size()){
                returnDetailsResponseBodyList.clear();

                for (int i = 0; i < returnItemsMap.size(); i++){
                    returnDetailsResponseBodyList.add(returnItemsMap.get(i));
                }
                mCurrentReturnsAdapter.updateReturnDetailsResponseBody(returnDetailsResponseBodyList);

            }else{
                returnDetailsResponseBodyList.add(returnItemsMap.get(itemIterator));

                itemIterator = itemIterator + 1;
                mPresenter.loadReturnDetails(mCurrentReturns.get(itemIterator).getID(), itemIterator);
            }
    }

    public void getCurrentReturnItems(List<CurrentReturns> currentReturns){
        mPresenter.loadReturnDetails(currentReturns.get(itemIterator).getID(), itemIterator);

//        for (int i = 0; i < currentReturns.size(); i++){
//            mPresenter.loadReturnDetails(currentReturns.get(itemIterator).getID(), itemIterator);
//        }
    }

    @Override
    public void onCurrentReturnClickListener(
            CurrentReturnViewHolder holder,
            int position,
            String productRequestStatus,
            String productRAN,
            String returnRequestDateFormat,
            String isRequestApproved,
            String returnId) {

        getRouter().pushController(RouterTransaction.with(
                ReturnDetailsController.newInstance(
                        returnId,
                        returnRequestDateFormat,
                        isRequestApproved,
                        productRequestStatus,
                        productRAN))
                .pushChangeHandler(new VerticalChangeHandler())
                .popChangeHandler(new VerticalChangeHandler()));

    }

    @OnClick(R.id.partial_toolbar_arrow_view)
    public void onBackClick(){
        getActivity().onBackPressed();
    }

    @OnClick(R.id.partial_toolbar_filter_view)

    public void onAddReturnClick(){
        getRouter().pushController(RouterTransaction.with(
                ReturnOrdersController.newInstance())
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }
}
